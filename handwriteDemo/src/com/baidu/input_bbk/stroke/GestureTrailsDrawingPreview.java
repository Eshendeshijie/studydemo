/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.baidu.input_bbk.stroke;

import com.example.handwritedemo.CoordinateUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

/**
 * Draw preview graphics of multiple gesture trails during gesture input.
 */
public final class GestureTrailsDrawingPreview extends AbstractDrawingPreview implements Runnable {
    private final SparseArray<GestureTrailDrawingPoints> mGestureTrails = new SparseArray<GestureTrailDrawingPoints>();
    private final GestureTrailDrawingParams mDrawingParams;
    private final Paint mGesturePaint;
    private int mOffscreenWidth;
    private int mOffscreenHeight;
    private int mOffscreenOffsetY;
    private Bitmap mOffscreenBuffer;
    private final Canvas mOffscreenCanvas = new Canvas();
    private final Rect mOffscreenSrcRect = new Rect();
    private final Rect mDirtyRect = new Rect();
    private final Rect mGestureTrailBoundsRect = new Rect(); // per trail

    private final Handler mDrawingHandler = new Handler();
	private int mFirstTouchTime;
    public static final float EXTRA_GESTURE_TRAIL_AREA_ABOVE_KEYBOARD_RATIO = 0.25f;
    private GestureStrokeDrawingPoints mGestureStrokeDrawingPoints;
	private GestureStrokeDrawingParams sGestureStrokeDrawingParams;

    public GestureTrailsDrawingPreview() {
        mDrawingParams = new GestureTrailDrawingParams();
        final Paint gesturePaint = new Paint();
        gesturePaint.setAntiAlias(true);
        gesturePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mGesturePaint = gesturePaint;
        sGestureStrokeDrawingParams = new GestureStrokeDrawingParams();
        mGestureStrokeDrawingPoints = new GestureStrokeDrawingPoints(sGestureStrokeDrawingParams);
    }

    @Override
    public void setKeyboardViewGeometry(final int[] originCoords, final int width,
            final int height) {
        super.setKeyboardViewGeometry(originCoords, width, height);
        mOffscreenOffsetY = (int)(height
                * EXTRA_GESTURE_TRAIL_AREA_ABOVE_KEYBOARD_RATIO);
        mOffscreenWidth = width;
        mOffscreenHeight = mOffscreenOffsetY + height;
    }

    @Override
    public void onDeallocateMemory() {
        freeOffscreenBuffer();
    }

    private void freeOffscreenBuffer() {
        mOffscreenCanvas.setBitmap(null);
        mOffscreenCanvas.setMatrix(null);
        if (mOffscreenBuffer != null) {
            mOffscreenBuffer.recycle();
            mOffscreenBuffer = null;
        }
    }

    private void mayAllocateOffscreenBuffer() {
        if (mOffscreenBuffer != null && mOffscreenBuffer.getWidth() == mOffscreenWidth
                && mOffscreenBuffer.getHeight() == mOffscreenHeight) {
            return;
        }
        freeOffscreenBuffer();
        mOffscreenWidth = mDrawingView.getWidth();
        mOffscreenHeight = mDrawingView.getHeight();
        mOffscreenBuffer = Bitmap.createBitmap(
                mOffscreenWidth, mOffscreenHeight, Bitmap.Config.ARGB_8888);
        mOffscreenCanvas.setBitmap(mOffscreenBuffer);
        mOffscreenCanvas.translate(0, mOffscreenOffsetY);
    }

    private boolean drawGestureTrails(final Canvas offscreenCanvas, final Paint paint,
            final Rect dirtyRect) {
    	Log.v("zsw_show", "draw drawGestureTrails");
        // Clear previous dirty rectangle.
        if (!dirtyRect.isEmpty()) {
            paint.setColor(Color.TRANSPARENT);
            paint.setStyle(Paint.Style.FILL);
            offscreenCanvas.drawRect(dirtyRect, paint);
        }
        dirtyRect.setEmpty();
        boolean needsUpdatingGestureTrail = false;
        // Draw gesture trails to offscreen buffer.
        synchronized (mGestureTrails) {
            // Trails count == fingers count that have ever been active.
            final int trailsCount = mGestureTrails.size();
            for (int index = 0; index < trailsCount; index++) {
                final GestureTrailDrawingPoints trail = mGestureTrails.valueAt(index);
                needsUpdatingGestureTrail |= trail.drawGestureTrail(offscreenCanvas, paint,
                        mGestureTrailBoundsRect, mDrawingParams);
                // {@link #mGestureTrailBoundsRect} has bounding box of the trail.
                dirtyRect.union(mGestureTrailBoundsRect);
            }
        }
        return needsUpdatingGestureTrail;
    }

    @Override
    public void run() {
        // Update preview.
        invalidateDrawingView();
    }

    /**
     * Draws the preview
     * @param canvas The canvas where the preview is drawn.
     */
    @Override
    public void drawPreview(final Canvas canvas) {
    	Log.v("zsw_show", "draw preview");
        if (!isPreviewEnabled()) {
            return;
        }
        mayAllocateOffscreenBuffer();
        // Draw gesture trails to offscreen buffer.
        final boolean needsUpdatingGestureTrail = drawGestureTrails(
                mOffscreenCanvas, mGesturePaint, mDirtyRect);
        if (needsUpdatingGestureTrail) {
            mDrawingHandler.removeCallbacks(this);
            mDrawingHandler.postDelayed(this, mDrawingParams.mUpdateInterval);
        }
        // Transfer offscreen buffer to screen.
        if (!mDirtyRect.isEmpty()) {
            mOffscreenSrcRect.set(mDirtyRect);
            mOffscreenSrcRect.offset(0, mOffscreenOffsetY);
            canvas.drawBitmap(mOffscreenBuffer, mOffscreenSrcRect, mDirtyRect, null);
            // Note: Defer clearing the dirty rectangle here because we will get cleared
            // rectangle on the canvas.
        }
    }
    
    /**
     * Set the position of the preview.
     * @param tracker The new location of the preview is based on the points in PointerTracker.
     */
    @Override
    public void setPreviewPosition() {
    	Log.v("zsw_show", "set preview position");
        GestureTrailDrawingPoints trail;
        synchronized (mGestureTrails) {
            trail = mGestureTrails.get(0);
            if (trail == null) {
                trail = new GestureTrailDrawingPoints();
                mGestureTrails.put(0, trail);
            }
        }
        trail.addStroke(mGestureStrokeDrawingPoints, mFirstTouchTime);

        // TODO: Should narrow the invalidate region.
        invalidateDrawingView();
    }
    
    public void clearAllStrokes() {
    	mGestureTrails.clear();
    }
    
    @Override
    public void onDraw(final Canvas canvas) {
    	Log.v("zsw_show", "drawing preview on draw");
        super.onDraw(canvas);
        drawPreview(canvas);
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
                setPreviewPosition();
            }
			break;
        case MotionEvent.ACTION_UP:
	        break;
		default:
			break;
		}
    	return true;
    	
    }

}
