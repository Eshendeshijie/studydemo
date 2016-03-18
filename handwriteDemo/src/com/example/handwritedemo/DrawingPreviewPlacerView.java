/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.example.handwritedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public final class DrawingPreviewPlacerView extends RelativeLayout {
    private final int[] mKeyboardViewOrigin = CoordinateUtils.newInstance();

    private final ArrayList<AbstractDrawingPreview> mPreviews = new ArrayList<AbstractDrawingPreview>();

	private GestureStrokeDrawingPoints mGestureStrokeDrawingPoints;

	private GestureStrokeDrawingParams sGestureStrokeDrawingParams;
	
	private int mFirstTouchTime = 0;

    public DrawingPreviewPlacerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        sGestureStrokeDrawingParams = new GestureStrokeDrawingParams();
        mGestureStrokeDrawingPoints = new GestureStrokeDrawingPoints(sGestureStrokeDrawingParams);
    }

    public void setHardwareAcceleratedDrawingEnabled(final boolean enabled) {
        if (!enabled) return;
        final Paint layerPaint = new Paint();
        layerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        setLayerType(LAYER_TYPE_HARDWARE, layerPaint);
    }

    public void addPreview(final AbstractDrawingPreview preview) {
        if (mPreviews.indexOf(preview) < 0) {
            mPreviews.add(preview);
        }
    }

    public void setKeyboardViewGeometry(final int[] originCoords, final int width,
            final int height) {
        CoordinateUtils.copy(mKeyboardViewOrigin, originCoords);
        final int count = mPreviews.size();
        for (int i = 0; i < count; i++) {
            mPreviews.get(i).setKeyboardViewGeometry(originCoords, width, height);
        }
    }

    public void deallocateMemory() {
        final int count = mPreviews.size();
        for (int i = 0; i < count; i++) {
            mPreviews.get(i).onDeallocateMemory();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        deallocateMemory();
    }

    @Override
    public void onDraw(final Canvas canvas) {
    	Log.v("zsw_show", "drawing preview on draw");
        super.onDraw(canvas);
        final int originX = CoordinateUtils.x(mKeyboardViewOrigin);
        final int originY = CoordinateUtils.y(mKeyboardViewOrigin);
        canvas.translate(originX, originY);
        final int count = mPreviews.size();
        for (int i = 0; i < count; i++) {
            mPreviews.get(i).drawPreview(canvas);
        }
        canvas.translate(-originX, -originY);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	Log.v("zsw_show", "ontouch");
    	int action = event.getAction() & MotionEvent.ACTION_MASK;
    	int x = (int) event.getX();
    	int y = (int) event.getY();
    	switch (action) {
		case MotionEvent.ACTION_DOWN:
			mFirstTouchTime = (int) event.getEventTime();
			mGestureStrokeDrawingPoints.onDownEvent(x, y, 0);
			break;
        case MotionEvent.ACTION_MOVE:
        	// Add historical points to gesture path.
            final int pointerIndex = event.findPointerIndex(0);
            final int historicalSize = event.getHistorySize();
            for (int h = 0; h < historicalSize; h++) {
                final int historicalX = (int)event.getHistoricalX(pointerIndex, h);
                final int historicalY = (int)event.getHistoricalY(pointerIndex, h);
                final long historicalTime = event.getHistoricalEventTime(h);
                mGestureStrokeDrawingPoints.onMoveEvent(historicalX, historicalY, (int) historicalTime - mFirstTouchTime);
                mPreviews.get(0).setPreviewPosition();
            }
			break;
        case MotionEvent.ACTION_UP:
	        break;
		default:
			break;
		}
    	return true;
    	
    }
    
    public GestureStrokeDrawingPoints getGestureStrokeDrawingPoints() {
    	return mGestureStrokeDrawingPoints;
    }
    
    public int getDownTime() {
    	return mFirstTouchTime;
    }
}
