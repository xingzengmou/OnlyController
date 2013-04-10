package com.only.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ViewSettings {
	private static final String TAG = "ViewSettings";
	
	private View view;
	private LayoutInflater inflater;
	private LinearLayout lyContent;
	
	public ViewSettings(View v, LayoutInflater inflater) {
		lyContent = (LinearLayout) v;
		this.inflater = inflater;
		view = (View) inflater.inflate(R.layout.view_settings, null);
		lyContent.addView(view);
	}
	
	public void show() {
		lyContent.setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		lyContent.setVisibility(View.GONE);
	}
}
