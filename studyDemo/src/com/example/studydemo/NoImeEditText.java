package com.example.studydemo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class NoImeEditText extends EditText {

	private CustomSecurityKeyboard mSecurityKeyboard;
	private InputConnection mInputConnection;
	
	public NoImeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		hideSoftInputMethod(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			InputMethodManager imm = (InputMethodManager)
                    this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
		} else if(event.getAction() == MotionEvent.ACTION_UP && mSecurityKeyboard != null && 
				mSecurityKeyboard.getVisibility() != View.VISIBLE) {
			showKeyboard();
		}
		return super.onTouchEvent(event);
	}

	// 隐藏系统键盘
	public void hideSoftInputMethod(EditText ed) {

		int currentVersion = android.os.Build.VERSION.SDK_INT;
		String methodName = null;
		if (currentVersion >= 16) {
			// android 4.2
			methodName = "setShowSoftInputOnFocus";
		} else if (currentVersion >= 14) {
			// android 4.0
			methodName = "setSoftInputShownOnFocus";
		}

		if (methodName == null) {
			ed.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method setShowSoftInputOnFocus;
			try {
				setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(ed, false);
			} catch (NoSuchMethodException e) {
				ed.setInputType(InputType.TYPE_NULL);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setSecurityKeyboard(CustomSecurityKeyboard keyboard) {
		mSecurityKeyboard = keyboard;
	}
	
	public boolean showKeyboard() {
		if(mSecurityKeyboard != null && mSecurityKeyboard.getVisibility() != View.VISIBLE) {
			mSecurityKeyboard.showKeyboard();
			return true;
		}
		return false;
	}
	
	public boolean hideKeyboard() {
		if(mSecurityKeyboard != null && mSecurityKeyboard.getVisibility() == View.VISIBLE) {
			mSecurityKeyboard.hideKeyboard();
			return true;
		}
		return false;
	}
	
	public int getKeyboardVisibility() {
		if(mSecurityKeyboard != null) {
			return mSecurityKeyboard.getVisibility();
		}
		return View.INVISIBLE;
	}
	
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		// TODO Auto-generated method stub
		mInputConnection = super.onCreateInputConnection(outAttrs);
		if(mSecurityKeyboard != null) {
			mSecurityKeyboard.setInputConnection(mInputConnection);
		}
		return mInputConnection;
	}
	
}
