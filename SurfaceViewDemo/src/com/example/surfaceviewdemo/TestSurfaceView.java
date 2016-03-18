/**
 * 
 */
package com.example.surfaceviewdemo;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author Administrator
 *
 */
public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


	private SurfaceHolder mHolder;
	private DrawThread mDrawThread;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private boolean mRunning = false; 
	private float mTouchX = -1;
	private float mTouchY = -1;
	private float mLastTouchX = -1;
	private float mLastTouchY = -1;
	private ArrayList<Float> mTouchXList = new ArrayList<Float>();
	private ArrayList<Float> mTouchYList = new ArrayList<Float>();
	private Path mPath = new Path();
	private float mLastR;
	
	public TestSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mHolder = getHolder();
		mHolder.addCallback(this);
		mDrawThread = new DrawThread(mHolder);
		mHandlerThread = new HandlerThread("draw-thread");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mRunning = true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mRunning = false; 
	}
	
    //线程内部类
	class DrawThread extends Thread {
		private SurfaceHolder holder;
		public boolean isRun;

		public DrawThread(SurfaceHolder holder) {
			this.holder = holder;
			isRun = true;
		}

		@Override
		public void run() {
			int count = 0;
			while (isRun) {
				Canvas c = null;
				try {
					synchronized (holder) {
						c = holder.lockCanvas();// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
						c.drawColor(Color.BLACK);// 设置画布背景颜色
						Paint p = new Paint(); // 创建画笔
						p.setTextSize(40);
						p.setColor(Color.WHITE);
						Rect r = new Rect(100, 50, 300, 250);
						c.drawRect(r, p);
						c.drawText("这是第" + (count++) + "秒", 100, 310, p);
						if(mTouchX >= 0 && mTouchY >= 0) {
							p.setColor(Color.BLUE);
							c.drawCircle(mTouchX, mTouchY, 5, p);
						}
						Log.v("zsw_show", "mTouchX : "+mTouchX+"=="+count);
						Thread.sleep(100);// 睡眠时间为1秒
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					if (c != null) {
						holder.unlockCanvasAndPost(c);// 结束锁定画图，并提交改变。

					}
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		mTouchX = event.getX();
		mTouchY = event.getY();
		mTouchXList.add(mTouchX);
		mTouchYList.add(mTouchY);
		Log.v("zsw_show", "ontouch");
		mHandler.post(this);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mPath.moveTo(mTouchX, mTouchY);
			mLastTouchX = mTouchX;
			mLastTouchY = mTouchY;
			break;
		case MotionEvent.ACTION_MOVE:
			addPoint(mTouchX, mTouchY);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mTouchYList.clear();
			mTouchXList.clear();
			mLastTouchX = -1;
			mLastTouchY = -1;
			mPath.reset();
			break;

		default:
			break;
		}
		mLastTouchX = mTouchX;
		mLastTouchY = mTouchY;
		return true;
	}
	
	private void addPoint(float x, float y) {
		mPath.quadTo(mLastTouchX, mLastTouchY, (mLastTouchX + x) / 2, (mLastTouchY + y) / 2);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int count = 0;
		//while (mRunning) {
			Canvas c = null;
			try {
				synchronized (mHolder) {
					c = mHolder.lockCanvas();// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
					c.drawColor(Color.BLACK);// 设置画布背景颜色
					Paint p = new Paint(); // 创建画笔
					p.setTextSize(40);
					p.setColor(Color.WHITE);
					Rect r = new Rect(100, 50, 300, 250);
					c.drawRect(r, p);
					c.drawText("这是第" + (count++) + "秒", 100, 310, p);
					p.setColor(Color.BLUE);
					p.setStyle(Paint.Style.STROKE);
					p.setStrokeWidth(15);
					/*for(int i = 0; i < mTouchXList.size(); i++) {
						float touchX = mTouchXList.get(i);
						float touchY = mTouchYList.get(i);
						if(touchX >= 0 && touchY >= 0) {
							c.drawCircle(touchX, touchY, 15, p);
						}
					}*/
					// draw path
					c.drawPath(mPath, p);
					// draw stroke
					/*for(int i = 0; i < mTouchXList.size(); i++) {
						float touchX = mTouchXList.get(i);
						float touchY = mTouchYList.get(i);
						float lastX = i > 0 ? mTouchXList.get(i - 1) : touchX; 
						float lastY = i > 0 ? mTouchYList.get(i - 1) : touchY;
						drawPoints(c, p, touchX, touchY, lastX, lastY);
					}*/
					//draw point
					/*float lastX = mLastTouchX >= 0 ? mLastTouchX : mTouchX; 
					float lastY = mLastTouchX >= 0 ? mLastTouchY : mTouchY;
					drawPoints(c, p, mTouchX, mTouchY, lastX, lastY);*/
					Log.v("zsw_show", "mTouchX : "+mTouchX+"=="+mTouchXList.size());
					//Thread.sleep(100);// 睡眠时间为1秒
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (c != null) {
					mHolder.unlockCanvasAndPost(c);// 结束锁定画图，并提交改变。

				}
			}
		//}
	}
	
	private void drawPoints(Canvas canvas, Paint paint, float x, float y, float lastX, float lastY) {
		// connect the dots, la-la-la
        float mLastLen = dist(lastX, lastY, x, y);
        float xi, yi, ri, frac;
        float d = 0;
        float r = 15;
        ri = r;
        while (true) {
            if (d > mLastLen) {
                break;
            }
            frac = d == 0 ? 0 : (d / mLastLen);
            ri = lerp(mLastR, r, frac);
            xi = lerp(lastX, x, frac);
            yi = lerp(lastY, y, frac);
            canvas.drawCircle(xi, yi, ri, paint);

            // for very narrow lines we must step (not much more than) one radius at a time
            final float MIN = 1f;
            final float THRESH = 16f;
            final float SLOPE = 0.1f; // asymptote: the spacing will increase as SLOPE*x
            if (ri <= THRESH) {
                d += MIN;
            } else {
                d += Math.sqrt(SLOPE * Math.pow(ri - THRESH, 2) + MIN);
            }
        }
        mLastR = ri;
	}
	
	private final float dist (float x1, float y1, float x2, float y2) {
        x2-=x1;
        y2-=y1;
        return (float) Math.sqrt(x2*x2 + y2*y2);
    }
	
	public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

}
