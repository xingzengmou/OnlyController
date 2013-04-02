package com.only.controller;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class OnlyMainActivity extends Activity {
	private static final String TAG = "OnlyMainActivity";
	
	/**
	 * Buttons define
	 */
	private Button btnGameConfigFiles;
	private Button btnKeyConfig;
	private Button btnTouchConfig;
	/**
	 * LinearLayout define
	 */
	private LinearLayout lyContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_only_main);
		getViewHandles();
		setViewListener();
	}

	private void getViewHandles() {
		btnGameConfigFiles = (Button) findViewById(R.id.game_list_btn);
		btnKeyConfig = (Button) findViewById(R.id.key_config_btn);
		btnTouchConfig = (Button) findViewById(R.id.touch_config_btn);
		lyContent = (LinearLayout) findViewById(R.id.content_ly);
	}
	
	private void setViewListener() {
		btnGameConfigFiles.setOnClickListener(btnOnClicks);
		btnKeyConfig.setOnClickListener(btnOnClicks);
		btnTouchConfig.setOnClickListener(btnOnClicks);
	}
	
	
	private View.OnClickListener btnOnClicks = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.equals(btnGameConfigFiles)) {
				switchBackground(btnGameConfigFiles, true);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnTouchConfig, false);
			} else if (v.equals(btnKeyConfig)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, true);
				switchBackground(btnTouchConfig, false);
			} else if (v.equals(btnTouchConfig)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnTouchConfig, true);
			}
		}
	};
	
	private void switchBackground(View v, boolean d) {
		v.setBackgroundResource(d ? R.drawable.btn_d : R.drawable.btn_n);
	}
}
