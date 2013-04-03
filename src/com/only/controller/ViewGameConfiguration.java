package com.only.controller;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class ViewGameConfiguration implements OnClickListener {
	private static final String TAG = "ViewGameConfiguration";
	
	private View view = null;
	private LinearLayout lyContent = null;
	
	public ViewGameConfiguration (View v, LayoutInflater inflater) {
		view = inflater.inflate(R.layout.view_game_config_files_phone_portrait, null);
		view.setOnClickListener(this);
		lyContent = (LinearLayout)v;
		lyContent.addView(view);
	}
	
	public void show() {
		lyContent.setVisibility(View.VISIBLE);
	}
	
	public void hide() {
		lyContent.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
