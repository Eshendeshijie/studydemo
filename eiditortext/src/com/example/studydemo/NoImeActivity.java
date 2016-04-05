package com.example.studydemo;

import com.example.eiditortext.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NoImeActivity extends CustomTitleActivity implements Runnable{
	
	private CustomSecurityKeyboard mSecurityKeyboard;
	private NoImeEditText mNoImeEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mNoImeEditText = (NoImeEditText) findViewById(R.id.no_ime_edit);
		mSecurityKeyboard = (CustomSecurityKeyboard) findViewById(R.id.custom_keyboard);
		mNoImeEditText.setSecurityKeyboard(mSecurityKeyboard);
	}
	
	@Override
	protected void addContentView() {
		// TODO Auto-generated method stub
		super.addContentView();
		setContentView(R.layout.no_ime_activity);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Handler().postDelayed(this, 200);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//mNoImeEditText.hideKeyboard();
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && mNoImeEditText.hideKeyboard()) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		mNoImeEditText.showKeyboard();
	}

}
