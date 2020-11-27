/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.camera.CameraManager;
import com.google.zxing.decode.CodeUtils;
import com.google.zxing.view.BaseViewfinderView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.zxing.uitls.MathUitl;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class BaseCaptureActivity extends AppCompatActivity implements OnCaptureCallback {

    protected SurfaceView surfaceView;
    protected BaseViewfinderView viewfinderView;
    protected View lightView;

    protected CaptureHelper mCaptureHelper;

    public static final int REQUEST_SCAN_CODE = 100;
    public static final int REQUEST_CAMERA_PERMISSION = 101;
    public static final int REQUEST_READ_PERMISSION = 102;
    public static final int REQUEST_IMAGE_CODE = 103;

    private boolean gotoAlbum = Boolean.FALSE;

    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initCaptureHelper();
    }

    public void initCaptureHelper() {
        mCaptureHelper = new CaptureHelper(this, surfaceView, viewfinderView, lightView);
        mCaptureHelper.setOnCaptureCallback(this);
        mCaptureHelper.onCreate();
        mCaptureHelper.playBeep(true)
                .vibrate(true)
                .supportVerticalCode(true);
    }

    /**
     * 初始化
     */
    public abstract void initView();

    /**
     * 布局id
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 处理解析相册中选择图片的二维码、条形码数据
     *
     * @param code
     */
    public abstract void handleCode(String code);

    /**
     * Get {@link CaptureHelper}
     *
     * @return {@link #mCaptureHelper}
     */
    public CaptureHelper getCaptureHelper() {
        return mCaptureHelper;
    }

    /**
     * Get {@link CameraManager} use {@link #getCaptureHelper()#getCameraManager()}
     *
     * @return {@link #mCaptureHelper#getCameraManager()}
     */
    @Deprecated
    public CameraManager getCameraManager() {
        return mCaptureHelper.getCameraManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
        if (gotoAlbum) {
            gotoAlbum = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
        if (gotoAlbum) {
            mCaptureHelper.setPauseToHandle(true);
        }
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switchSelectedImage();
        } else {
            Toast.makeText(this, "权限申请失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewfinderView.setNoSuspendDrawLaser(false);
        if (resultCode == RESULT_OK && data != null && requestCode == REQUEST_IMAGE_CODE) {
            Observable
                    .create((ObservableOnSubscribe<String>) emitter -> {

                        Uri uri = data.getData();
                        String path = getPath(uri);

                        Bitmap bitmap = CodeUtils.compressBitmap(BaseCaptureActivity.this, path);
                        String result = CodeUtils.parseCode(bitmap);
                        if (!TextUtils.isEmpty(result)) {
                            emitter.onNext(result);
                        } else {
                            result = CodeUtils.ratotionAndParseBitmap(bitmap, 90);
                            if (!TextUtils.isEmpty(result)) {
                                emitter.onNext(result);
                            } else {
                                emitter.onError(null);
                            }
                        }
                        mCaptureHelper.playBeepSoundAndVibrate();
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            mDisposable = d;
                        }

                        @Override
                        public void onNext(@NonNull String result) {
                            handleCode(result);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Toast.makeText(BaseCaptureActivity.this, "扫码失败", Toast.LENGTH_LONG);
                            viewfinderView.postDelayed(() -> scanAgain(), 100);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            viewfinderView.postDelayed(() -> scanAgain(), 100);
        }
    }

    /**
     * 接收扫码结果回调
     *
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
     */
    @Override
    public boolean onResultCallback(String result) {
        handleCode(result);
        return true;
    }

    /***
     * 以下是处理相册识别相关内容
     */
    protected void switchSelectedImage() {
        gotoAlbum = Boolean.TRUE;
        gotoAlbumForImage(this, REQUEST_IMAGE_CODE);
    }

    /**
     * 跳转到相册，先判断用户是否有相册，没有的话就 Toast提示。
     * <p>
     * 另外该方法暂时不能用于Fragment中，要使用在Fragment中需要将方法中的参数Activity改为FragmentActivity
     *
     * @param activity
     * @param requestCode
     */
    public void gotoAlbumForImage(@NotNull Activity activity, int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        List<ResolveInfo> infoList = activity.getPackageManager().queryIntentActivities(i,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (infoList != null && infoList.size() > 0) {
            activity.startActivityForResult(i, requestCode);
        } else {
            Toast.makeText(BaseCaptureActivity.this, "找不到相册", Toast.LENGTH_LONG);
        }
    }

    protected String getPath(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), MathUitl.stringToLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    protected String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    protected void scanAgain() {
        if (viewfinderView != null) {
            viewfinderView.setNoSuspendDrawLaser(true);
            viewfinderView.postInvalidateDelayed();
            if (mCaptureHelper != null) {
                mCaptureHelper.setPauseToHandle(false);
                mCaptureHelper.resetScanViewState();
                mCaptureHelper.restartPreviewAndDecode();
            }
        }
    }
}