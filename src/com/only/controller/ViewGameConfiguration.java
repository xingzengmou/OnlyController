package com.only.controller;

import java.util.Map;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ViewGameConfiguration implements OnClickListener {
	private static final String TAG = "ViewGameConfiguration";
	
	private LayoutInflater inflater;
	private View view = null;
	private LinearLayout lyContent = null;
	private View menuView = null;
	private Dialog menuDialog =  null;
	
	/**
	 * menuDialog subView
	 * @param v
	 * @param inflater
	 */
	
	public ViewGameConfiguration (View v, LayoutInflater inflater) {
		lyContent = (LinearLayout)v;
		this.inflater = inflater;
		menuDialog = new Dialog(lyContent.getContext());
		menuDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		menuDialog.setContentView(R.layout.view_popwindow_operation);
	}
	
	public void show() {
		lyContent.setVisibility(View.VISIBLE);
	}
	
	public void addGameView(Object data) {
		Map<String, Object> map = (Map<String, Object>) data;
		view = inflater.inflate(R.layout.view_game_config_files_phone_portrait, null);
		view.setOnClickListener(this);
		ImageView ivIcon = (ImageView) view.findViewById(R.id.icon_iv);
		ivIcon.setBackgroundDrawable((Drawable)map.get("icon"));
		TextView tvAppName = (TextView) view.findViewById(R.id.app_name_tv);
		tvAppName.setText(map.get("label").toString());
		TextView tvKeyConfig = (TextView) view.findViewById(R.id.key_file_name_tv);
		tvKeyConfig.setText(map.get("packageName").toString() + ".key");
		view.setTag(map);
		lyContent.addView(view);
	}
	
	public void hide() {
		lyContent.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		menuDialog.show();
	}
}
