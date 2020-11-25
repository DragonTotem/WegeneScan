/*
 * Copyright (C) 2008 ZXing authors
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

package com.google.zxing.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.google.zxing.uitls.DensityUtil;
import com.google.zxing.client.android.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 */
public final class ViewfinderView extends BaseViewfinderView {

    private Bitmap upBitmap;
    private Bitmap downBitmap;

    protected static final long ANIMATION_DELAY = 10L;
    protected static final int OPAQUE = 0xFF;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView() {
        upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_collector_up);
        downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_collector_down);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (frame == null) {
            return;
        }
        if (scannerStart == 0 || scannerEnd == 0) {
            scannerStart = frame.top;
            scannerEnd = frame.bottom;
        }

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // Draw the exterior (i.e. outside the framing rect) darkened
        drawExterior(canvas, frame, width, height);

        // Draw a two pixel solid black border inside the framing rect
        drawFrame(canvas, frame);
        // Draw two pic
        drawBitmap(canvas, frame);
        // 绘制边角
        drawCorner(canvas, frame);
        // Draw a red "laser scanner" line through the middle to show decoding is active
        drawLaserScanner(canvas, frame);
        // Request another update at the animation interval, but only repaint the laser line,
        //指定重绘区域，该方法会在子线程中执行
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }

    protected void drawBitmap(Canvas canvas, Rect frame) {
        paint.setAlpha(OPAQUE);
        canvas.drawBitmap(upBitmap, frame.width() / 2 + frame.left - upBitmap.getWidth() / 2,
                frame.top - upBitmap.getHeight() + DensityUtil.dip2px(getContext(), 32), paint);
        canvas.drawBitmap(downBitmap, frame.width() / 2 + frame.left - downBitmap.getWidth() / 2,
                frame.bottom, paint);
    }

}
