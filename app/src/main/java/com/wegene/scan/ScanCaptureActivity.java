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
package com.wegene.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.zxing.BaseCaptureActivity;
import com.google.zxing.OnCaptureCallback;
import com.google.zxing.uitls.DensityUtil;

public class ScanCaptureActivity extends BaseCaptureActivity implements OnCaptureCallback {

    @Override
    public void initView() {
        viewfinderView = findViewById(R.id.viewfinderView);
        surfaceView = findViewById(R.id.preview_view);
        lightView = findViewById(R.id.iv_light);
        View scanTipTv = findViewById(R.id.tv_scan_tip);
        scanTipTv.postDelayed(() -> {
            Rect rect = viewfinderView.getFrameRect();
            if (rect != null) {
                ViewGroup.LayoutParams layoutParams = scanTipTv.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutParams;
                    params.topMargin = rect.bottom + DensityUtil.dip2px(ScanCaptureActivity.this, 30);
                    scanTipTv.setLayoutParams(params);
                }
            }
        }, 100);

        findViewById(R.id.iv_header_back).setOnClickListener(v -> finish());
        findViewById(R.id.tv_header_right).setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanCaptureActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
            } else {
                switchSelectedImage();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_capture;
    }

    @Override
    public void handleCode(String code) {
        Toast.makeText(this, "handleCodeSuccess: code:" + code, Toast.LENGTH_LONG).show();
        Log.w("MainActivity", "handleCode code: " + code);
        Intent i = new Intent();
        i.putExtra("code", code);
        this.setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void handleCodeError() {
        Toast.makeText(this, "未能识别，请手动输入", Toast.LENGTH_LONG).show();
        viewfinderView.postDelayed(() -> scanAgain(), 100);
    }
}