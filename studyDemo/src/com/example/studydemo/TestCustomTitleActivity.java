package com.example.studydemo;

import com.example.eiditortext.R;

import android.os.Bundle;

public class TestCustomTitleActivity extends CustomTitleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected void addContentView() {
		// TODO Auto-generated method stub
		super.addContentView();
		setContentView(R.layout.test_custom_title_activity);
	}
}
