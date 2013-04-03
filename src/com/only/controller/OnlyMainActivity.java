package com.only.controller;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
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
	private LinearLayout lyGameConfigFiles;
	private LinearLayout lyKeyConfig;
	private LinearLayout lyTouchConfig;
	private LinearLayout lyTitle;
	/**
	 * View for configuration
	 */
	private ViewGameConfiguration mViewGameConfiguration = null;
	private ViewKeyConfiguration mViewKeyConfiguration = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_only_main);
		getViewHandles();
		setViewListener();
		newView();
	}

	private void getViewHandles() {
		btnGameConfigFiles = (Button) findViewById(R.id.game_list_btn);
		btnKeyConfig = (Button) findViewById(R.id.key_config_btn);
		btnTouchConfig = (Button) findViewById(R.id.touch_config_btn);
		lyGameConfigFiles = (LinearLayout) findViewById(R.id.game_config_files_ly);
		lyKeyConfig = (LinearLayout) findViewById(R.id.key_config_ly);
		lyTouchConfig = (LinearLayout) findViewById(R.id.touch_config_ly);
		lyTitle = (LinearLayout) findViewById(R.id.title_ly);
//		lyTitle.getBackground().setAlpha(10);
	}
	
	private void setViewListener() {
		btnGameConfigFiles.setOnClickListener(btnOnClicks);
		btnKeyConfig.setOnClickListener(btnOnClicks);
		btnTouchConfig.setOnClickListener(btnOnClicks);
	}
	
	private void newView() {
		mViewGameConfiguration = new ViewGameConfiguration(lyGameConfigFiles, this.getLayoutInflater());
		mViewKeyConfiguration = new ViewKeyConfiguration(lyKeyConfig, this.getLayoutInflater());
		mViewGameConfiguration.show();
		mViewKeyConfiguration.hide();
	}
	
	private View.OnClickListener btnOnClicks = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.equals(btnGameConfigFiles)) {
				switchBackground(btnGameConfigFiles, true);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnTouchConfig, false);
				if (mViewGameConfiguration != null) {
					mViewKeyConfiguration.hide();
					mViewGameConfiguration.show();
				}
			} else if (v.equals(btnKeyConfig)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, true);
				switchBackground(btnTouchConfig, false);
				if (mViewKeyConfiguration != null) {
					mViewGameConfiguration.hide();
					mViewKeyConfiguration.show();
				}
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
