package com.example.studydemo;

import com.example.eiditortext.R;
import com.example.util.TypefaceUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputConnection;

public class CustomSecurityKeyboard extends View {

	public Paint mPaint;
	public Paint mBgPaint;
	public int mKeyHeight;
	public int mKeyWidth;
	public int mScreenWidth;
	public int mScreenHeight;
	public final int LINE_NUM = 4;
	public final int ROW_NUM = 3;
	private String[] mKeyContents = {"1","2","3","4","5","6","7","8","9","*","0","DEL"};
	// show and hide animation
	private Animation mShowAnim;
	private Animation mHideAnim;
	private int[] mCurrentPos = {-1, -1};
	// InputConnection
	private InputConnection mInputConnection;
	
	public CustomSecurityKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initSize(context);
		setBackgroundColor(0x88999999);
		mPaint = new Paint();
		mPaint.setTextSize(80);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setAntiAlias(true);
		mBgPaint = new Paint();
		mBgPaint.setColor(0x44777777);
		mShowAnim = AnimationUtils.loadAnimation(context, R.anim.keyboard_show_anim);
		mHideAnim = AnimationUtils.loadAnimation(context, R.anim.keyboard_hide_anim);
	}
	
	private void initSize(Context context) {
		int mDensityDpi = (int) context.getResources().getDisplayMetrics().densityDpi;
		mScreenHeight = context.getResources().getConfiguration().screenHeightDp * mDensityDpi / 160;
		mScreenWidth = context.getResources().getConfiguration().screenWidthDp * mDensityDpi / 160;
		mKeyHeight = mScreenHeight / LINE_NUM / 2;
		mKeyWidth = mScreenWidth / ROW_NUM;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		String content = "";
		for(int i = 0; i < LINE_NUM; i++) {
			int lineY = mKeyHeight * i;
			canvas.drawLine(0, lineY, mKeyWidth * ROW_NUM, lineY, mPaint);
			for(int j = 0; j < ROW_NUM; j++) {
				if(i == 0) {
					int lineX = mKeyWidth * (j + 1);
					canvas.drawLine(lineX, 0, lineX, mKeyHeight * LINE_NUM, mPaint);
				}
				// draw background
				if(mCurrentPos[0] == j && mCurrentPos[1] == i) {
					int left = j * mKeyWidth;
					int right = left + mKeyWidth;
					int top = i * mKeyHeight;
					int bottom = top + mKeyHeight;
					canvas.drawRect(left, top, right, bottom, mBgPaint);
				}
				// draw text
				content = mKeyContents[i * (LINE_NUM - 1) + j];
				int halfWidth = mKeyWidth / 2;
				int halfHeight = mKeyHeight / 2;
				int textX = mKeyWidth * j + halfWidth;
				int baseline = (int) (mKeyHeight * i + halfHeight + TypefaceUtils.getLabelHeight(content, mPaint) / 2);
				canvas.drawText(content, textX, baseline, mPaint);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mCurrentPos = getKeyIndex(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			int[] temp = getKeyIndex(x, y);
			if(mCurrentPos[0] != temp[0] || mCurrentPos[1] != temp[1]) {
				mCurrentPos = temp;
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			int index = mCurrentPos[1] * (LINE_NUM - 1) + mCurrentPos[0];
			processKey(index, mKeyContents[index]);
			mCurrentPos[0] = -1;
			mCurrentPos[1] = -1;
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			mCurrentPos[0] = -1;
			mCurrentPos[1] = -1;
			invalidate();
			break;

		default:
			break;
		}
		return true;
	}
	
	private int[] getKeyIndex(int x, int y) {
		int[] pos = {-1, -1};
		pos[0] = x / mKeyWidth;
		pos[1] = y / mKeyHeight;
		return pos;
	}
	
	private void processKey(int index, String content) {
		if(mInputConnection == null) {
			return;
		}
		if(content == "DEL" || content.equals("DEL")) {
			mInputConnection.deleteSurroundingText(1, 0);
		} else {
			mInputConnection.commitText(content, 0);
		}
	}
	
	public void showKeyboard() {
		mShowAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				setVisibility(View.VISIBLE);
			}
		});
		this.startAnimation(mShowAnim);
	}
	
	public void hideKeyboard() {
		mHideAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				setVisibility(View.INVISIBLE);
			}
		});
		this.startAnimation(mHideAnim);
	}
	
	public void setInputConnection(InputConnection ipc) {
		mInputConnection = ipc;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(mKeyHeight * LINE_NUM, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
