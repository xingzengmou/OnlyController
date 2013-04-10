package com.only.controller;

import com.only.inputjar.InputJar;
import com.only.jni.InputAdapter;
import com.only.root.Root;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OnlyMainActivity extends Activity {
	private static final String TAG = "OnlyMainActivity";
	
	/**
	 * Buttons define
	 */
	private Button btnGameConfigFiles;
	private Button btnKeyConfig;
	private Button btnSettings;
	/**
	 * LinearLayout define
	 */
	private LinearLayout lyGameConfigFiles;
	private LinearLayout lyKeyConfig;
	private LinearLayout lySettings;
	private LinearLayout lyTitle;
	/**
	 * View for configuration
	 */
	private ViewGameConfiguration mViewGameConfiguration = null;
	private ViewKeyConfiguration mViewKeyConfiguration = null;
	private ViewSettings mViewSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_only_main);
		getViewHandles();
		setViewListener();
		newView();
//		if (Root.root()) {
//			Toast.makeText(this, R.string.root_successful, Toast.LENGTH_SHORT);
//			if (InputJar.run(this)) {
//				InputAdapter.init();
//				InputAdapter.start();
//			}
//		} else {
//			Toast.makeText(this, R.string.root_failed, Toast.LENGTH_SHORT);
//		}
	}

	private void getViewHandles() {
		btnGameConfigFiles = (Button) findViewById(R.id.game_list_btn);
		btnKeyConfig = (Button) findViewById(R.id.key_config_btn);
		btnSettings = (Button) findViewById(R.id.settings_tv);
		lyGameConfigFiles = (LinearLayout) findViewById(R.id.game_config_files_ly);
		lyKeyConfig = (LinearLayout) findViewById(R.id.key_config_ly);
		lySettings = (LinearLayout) findViewById(R.id.settings_ly);
		lyTitle = (LinearLayout) findViewById(R.id.title_ly);
	}
	
	private void setViewListener() {
		btnGameConfigFiles.setOnClickListener(btnOnClicks);
		btnKeyConfig.setOnClickListener(btnOnClicks);
		btnSettings.setOnClickListener(btnOnClicks);
	}
	
	private void newView() {
		mViewGameConfiguration = new ViewGameConfiguration(lyGameConfigFiles, this.getLayoutInflater());
		mViewKeyConfiguration = new ViewKeyConfiguration(lyKeyConfig, this.getLayoutInflater());
		mViewSettings = new ViewSettings(lySettings, this.getLayoutInflater());
		mViewGameConfiguration.show();
		mViewKeyConfiguration.hide();
		mViewSettings.hide();
	}
	
	private View.OnClickListener btnOnClicks = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.equals(btnGameConfigFiles)) {
				switchBackground(btnGameConfigFiles, true);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnSettings, false);
				if (mViewGameConfiguration != null) {
					mViewKeyConfiguration.hide();
					mViewSettings.hide();
					mViewGameConfiguration.show();
				}
			} else if (v.equals(btnKeyConfig)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, true);
				switchBackground(btnSettings, false);
				if (mViewKeyConfiguration != null) {
					mViewGameConfiguration.hide();
					mViewKeyConfiguration.show();
					mViewSettings.hide();
				}
			} else if (v.equals(btnSettings)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnSettings, true);
				if (mViewSettings != null) {
					mViewGameConfiguration.hide();
					mViewKeyConfiguration.hide();
					mViewSettings.show();
				}
			}
		}
	};
	
	private void switchBackground(View v, boolean d) {
		v.setBackgroundResource(d ? R.drawable.btn_d : R.drawable.btn_n);
	}
}
