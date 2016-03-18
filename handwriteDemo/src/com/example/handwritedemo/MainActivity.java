package com.example.handwritedemo;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	private GestureTrailsDrawingPreview mGestureTrailsDrawingPreview;
	private DrawingPreviewPlacerView mDrawingPreviewPlacerView;
	private int[] mLocation = new int[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDrawingPreviewPlacerView = (DrawingPreviewPlacerView) this.findViewById(R.id.draw_view);
		mGestureTrailsDrawingPreview = new GestureTrailsDrawingPreview();
        mGestureTrailsDrawingPreview.setDrawingView(mDrawingPreviewPlacerView);
        //mDrawingPreviewPlacerView.addPreview(mGestureTrailsDrawingPreview);
        mDrawingPreviewPlacerView.getLocationInWindow(mLocation);
        mDrawingPreviewPlacerView.setKeyboardViewGeometry(mLocation, mDrawingPreviewPlacerView.getMeasuredWidth(), 
        		mDrawingPreviewPlacerView.getMeasuredHeight());
        mDrawingPreviewPlacerView.setHardwareAcceleratedDrawingEnabled(true);
        //mGestureTrailsDrawingPreview.setPreviewPosition();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
