package com.only.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.only.config.GameConfigurationState;
import com.only.controller.data.AppsDatabase;
import com.only.controller.data.GlobalData;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ViewGameConfiguration implements OnClickListener {
	private static final String TAG = "ViewGameConfiguration";
	
	private LayoutInflater inflater;
	private View view = null;
	private LinearLayout lyContent = null;
	private View menuView = null;
	private TextView tvEnableKeyConfig;
	private TextView tvEnableTouchConfig;
	
	
	/**
	 * the views for menudialog
	 * @param v
	 * @param inflater
	 */
	private Dialog menuDialog =  null;
	private Button btnKeyMapConfiguration;
	private Button btnTouchMapConfiguration;
	private Button btnEnableKeyMap;
	private Button btnEnableTouchMap;
	private Button btnOpenGame;
	private Button btnRemoveGame;
	 
	private String packageLabel = "";
	private String packageNameXML = "";
	private Map<String, Object> smap;
	 
	private GameConfigurationState mGameConfigurationState;
	
	public ViewGameConfiguration (View v, LayoutInflater inflater) {
		lyContent = (LinearLayout)v;
		this.inflater = inflater;
		mGameConfigurationState = new GameConfigurationState(v.getContext());
		menuDialog = new Dialog(lyContent.getContext());
		menuDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		menuDialog.setContentView(R.layout.view_popwindow_operation);
		
		btnKeyMapConfiguration = (Button) menuDialog.findViewById(R.id.btn_edit_key_config);
		btnTouchMapConfiguration = (Button) menuDialog.findViewById(R.id.btn_touch_config);
		btnEnableKeyMap = (Button) menuDialog.findViewById(R.id.btn_enable_key_file_config);
		btnEnableTouchMap = (Button) menuDialog.findViewById(R.id.btn_enable_touch_file_config);
		btnOpenGame = (Button) menuDialog.findViewById(R.id.btn_open_game);
		btnRemoveGame = (Button)menuDialog.findViewById(R.id.btn_delete_game);
		
		btnKeyMapConfiguration.setOnClickListener(menuDialogButtonOnClick);
		btnTouchMapConfiguration.setOnClickListener(menuDialogButtonOnClick);
		btnEnableKeyMap.setOnClickListener(menuDialogButtonOnClick);
		btnEnableTouchMap.setOnClickListener(menuDialogButtonOnClick);
		btnKeyMapConfiguration.setOnClickListener(menuDialogButtonOnClick);
		btnOpenGame.setOnClickListener(menuDialogButtonOnClick);
		btnRemoveGame.setOnClickListener(menuDialogButtonOnClick);
		
	}
	
	public void loadGameViewFromDatabase(Object data, PackageManager pm) {
		List<Map<String, Object>> ldb = (List<Map<String, Object>>) data;
		List<PackageInfo> lpi = pm.getInstalledPackages(0);
		for (PackageInfo pi : lpi) {
			if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
				for (Map<String, Object> map : ldb) {
					if (map.get("packageName").toString().equals(pi.applicationInfo.packageName)) {
						map.put("icon", pi.applicationInfo.loadIcon(pm));
						map.put("label", pi.applicationInfo.loadLabel(pm));
						map.put("keyFileState", mGameConfigurationState.getKeyConfigurationState(pi.applicationInfo.packageName));
						map.put("touchFileState", mGameConfigurationState.getTouchConfigurationState(pi.applicationInfo.packageName));
						addGameView(map);
						break;
					}
				}
			}
		}
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
		tvKeyConfig.setText(map.get("packageName").toString() + ".xml");
		TextView tvTouchConfig = (TextView) view.findViewById(R.id.touch_file_name_tv);
		tvTouchConfig.setText(map.get("packageName").toString() + ".tp");
		tvEnableKeyConfig = (TextView) view.findViewById(R.id.tv_key_file_state);
		tvEnableKeyConfig.setText(map.get("keyFileState").toString().contains("true") ? R.string.tv_key_file_state_enable : R.string.tv_key_file_state_disable);
		tvEnableTouchConfig = (TextView) view.findViewById(R.id.tv_touch_file_state);
		tvEnableTouchConfig.setText(map.get("touchFileState").toString().contains("true") ? R.string.tv_key_file_state_enable : R.string.tv_key_file_state_disable);
		view.setTag(map);
		lyContent.addView(view);
	}
	
	public void hide() {
		lyContent.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		tvEnableKeyConfig = (TextView) view.findViewById(R.id.tv_key_file_state);
		tvEnableTouchConfig = (TextView) view.findViewById(R.id.tv_touch_file_state);
		smap = (Map<String, Object>) arg0.getTag();
		packageLabel = smap.get("label").toString();
		packageNameXML = smap.get("packageName").toString();
		btnEnableKeyMap.setText(smap.get("keyFileState").toString().contains("true") ? R.string.btn_disable_key_file_config : R.string.btn_enable_key_file_config);
		btnEnableTouchMap.setText(smap.get("touchFileState").toString().contains("true") ? R.string.btn_disable_touch_file_config : R.string.btn_enable_touch_file_config);
		menuDialog.show();
	}
	
	private View.OnClickListener menuDialogButtonOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.equals(btnKeyMapConfiguration)) {
				loadKeyMapConfigurationToCache();
				Intent intent = new Intent();
				intent.setClass(lyContent.getContext(), ViewKeyConfiguration.class);
				Bundle bundle = new Bundle();
				bundle.putString("package_label", packageLabel);
				bundle.putString("packageName", packageNameXML);
				intent.putExtras(bundle);
				lyContent.getContext().startActivity(intent);
			} else if (btnOpenGame.equals(v)) {
				Intent mIntent = new Intent(v.getContext().getPackageManager().getLaunchIntentForPackage(packageNameXML));
				lyContent.getContext().startActivity(mIntent);
				loadKeyMapConfigurationToCache();
			} else if (btnEnableKeyMap.equals(v)) {
				mGameConfigurationState.setKeyConfigurationState(packageNameXML, smap.get("keyFileState").toString().contains("true") ? "false" : "true");
				tvEnableKeyConfig.setText(smap.get("keyFileState").toString().contains("true") ? R.string.tv_key_file_state_enable : R.string.tv_key_file_state_disable);
				btnEnableKeyMap.setText(smap.get("keyFileState").toString().contains("true") ? R.string.btn_disable_key_file_config : R.string.btn_enable_key_file_config);
			} else if (btnEnableTouchMap.equals(v)) {
				mGameConfigurationState.setKeyConfigurationState(packageNameXML, smap.get("touchFileState").toString().contains("true") ? "false" : "true");
				tvEnableTouchConfig.setText(smap.get("touchFileState").toString().contains("true") ? R.string.tv_key_file_state_enable : R.string.tv_key_file_state_disable);
				btnEnableTouchMap.setText(smap.get("touchFileState").toString().contains("true") ? R.string.btn_disable_touch_file_config : R.string.btn_enable_touch_file_config);
			}
			menuDialog.cancel();
		}
	};
	
	private void loadKeyMapConfigurationToCache() {
		SharedPreferences sp = lyContent.getContext().getSharedPreferences(packageNameXML, Context.MODE_PRIVATE);
		for (int i = 0; i < GlobalData.keyAppName.length; i ++) {
			GlobalData.keyMapCache.put(GlobalData.keyAppName[i], sp.getString(GlobalData.keyAppName[i], lyContent.getContext().getString(R.string.unknown)));
			GlobalData.intKeyMapCache.put(GlobalData.keyAppName[i] + "_INT", sp.getInt(GlobalData.keyAppName[i] + "_INT", 0));
		}
	}
}
