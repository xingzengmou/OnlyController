package com.only.controller;

import android.content.ComponentName;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

public class ViewSettings {
	private static final String TAG = "ViewSettings";
	
	private View view;
	private LayoutInflater inflater;
	private LinearLayout lyContent;
	
	/**
	 * View handler
	 * @param v
	 * @param inflater
	 */
	private LinearLayout btnInputIME;
	
	public ViewSettings(View v, LayoutInflater inflater) {
		lyContent = (LinearLayout) v;
		this.inflater = inflater;
		view = (View) inflater.inflate(R.layout.view_settings, null);
		btnInputIME = (LinearLayout) view.findViewById(R.id.ime_config_btn);
		btnInputIME.setOnClickListener(btnClick);
		lyContent.addView(view);
	}
	
	public void show() {
		lyContent.setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		lyContent.setVisibility(View.GONE);
	}
	
	private View.OnClickListener btnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.equals(btnInputIME)) {
//				lyContent.getContext().startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
//				Intent localIntent3 = new Intent();
//			    localIntent3.setAction("android.intent.action.MAIN");
//			    localIntent3.setComponent(new ComponentName("com.android.settings", "com.android.settings.LanguageSettings"));
//			    lyContent.getContext().startActivity(localIntent3);
//				((InputMethodManager)lyContent.getContext().getSystemService("input_method")).showInputMethodPicker();
				
			}
		}
	};
}
