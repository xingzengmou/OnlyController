package com.only.config;

import com.only.controller.data.GlobalData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GameConfigurationState {
	private static final String TAG = "GameConfigurationState";
	
	private Context context;
	private SharedPreferences sp;
	private Editor editor;
	
	public GameConfigurationState(Context context) {
		sp = context.getSharedPreferences(GlobalData.gameConfigStateXML, Context.MODE_PRIVATE);
		editor = sp.edit();
		this.context = context;
	}
	
	public String getKeyConfigurationState(String keyFileName) {
		return sp.getString(keyFileName + "_xml", "true");
	}
	
	public String getTouchConfigurationState(String touchFileName) {
		return sp.getString(touchFileName + "_tp", "true");
	}
	
	public void setKeyConfigurationState(String packageName, String value) {
		editor.putString(packageName + "_xml", value);
		editor.commit();
	}
	
	public void setTouchConfigurationState(String packageName, String value) {
		editor.putString(packageName + "_tp", value);
		editor.commit();
	}
}
