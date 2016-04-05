package com.example.studydemo;

import com.example.eiditortext.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomTitleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// change title bar of activity
		setTheme(R.style.HasTitlebarTheme);
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		addContentView();
		if (isCustom) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_activity_title_bar);
		}
		initCustomTitle();
	}

	protected void addContentView() {
		// TODO Auto-generated method stub
		
	}

	protected void initCustomTitle() {
		// TODO Auto-generated method stub
		TextView title = (TextView) findViewById(R.id.custom_activity_title);
		Button backBtn = (Button) findViewById(R.id.custom_activity_back);
		if(title != null) {
		    title.setText(getTitle());
		}
		if(backBtn != null) {
			backBtn.setBackground(null);
			backBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}
	}
}
