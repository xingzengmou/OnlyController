package com.only.core;

import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.KeyEvent;

import com.only.controller.InputAdapterKeyEvent;
import com.only.controller.R;
import com.only.controller.data.GlobalData;
import com.only.jni.InputAdapter;
import com.only.net.socket.netSocket;

public class ControllerCore {
	private static final String TAG = "ControllerCore";
	
	public static void start() {
		InputAdapter.onSetKeyListener(keyListener);
	}
	
	private static InputAdapter.OnKeyListener keyListener = new InputAdapter.OnKeyListener() {
		
		//send key format: injectKey:eventcode:scancode:state
		@Override
		public void onInputAdapterKeyUp(InputAdapterKeyEvent keyEvent) {
			// TODO Auto-generated method stub
			for (int i = 0; i < GlobalData.keyAppValue.length; i ++) {
				int mapCode = GlobalData.intKeyMapCache.get(GlobalData.keyAppName[i] + "_INT");
				Log.e(TAG, "keyCode = " + keyEvent.keyCode + " mapCode = " + mapCode);
				if (mapCode == keyEvent.keyCode) {
					int eventCode = GlobalData.keyAppValue[i];
					Log.e(TAG, "found key = " + GlobalData.keyAppName[i] + " keycode = " + GlobalData.keyAppValue[i]);
					if (eventCode != 0) { // this key has mapped
						netSocket.send("injectKey:" + eventCode + ":0:" + KeyEvent.ACTION_UP);
					}
					break;
				} 
			}
		}
		
		@Override
		public void onInputAdapterKeyDown(InputAdapterKeyEvent keyEvent) {
			// TODO Auto-generated method stub
			for (int i = 0; i < GlobalData.keyAppValue.length; i ++) {
				int mapCode = GlobalData.intKeyMapCache.get(GlobalData.keyAppName[i] + "_INT");
				Log.e(TAG, "keyCode = " + keyEvent.keyCode + " mapCode = " + mapCode);
				if (mapCode == keyEvent.keyCode) {
					int eventCode = GlobalData.keyAppValue[i];
					Log.e(TAG, "found key = " + GlobalData.keyAppName[i] + " keycode = " + GlobalData.keyAppValue[i]);
					if (eventCode != 0) { // this key has mapped
						netSocket.send("injectKey:" + eventCode + ":0:" + KeyEvent.ACTION_DOWN);
					}
					break;
				} 
			}
		}
	};
	
	public void appsMonitor(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> lrapi = am.getRunningAppProcesses();
				String packageName = lrapi.get(0).processName;
				if (!packageName.contains(GlobalData.currentConfigurationXML)) {
					for (Map<String, Object> map: GlobalData.listCache) {
						String dbPackageName = map.get("packageName").toString();
						if (packageName.contains(dbPackageName)) {
							GlobalData.currentConfigurationXML = packageName;
							loadKeyMapConfigurationToCache(context);
							break;
						}
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();
	}
	
	private void loadKeyMapConfigurationToCache(Context context) {
		Log.e(TAG, "use configuration xml = " + GlobalData.currentConfigurationXML + ".xml");
		SharedPreferences sp = context.getSharedPreferences(GlobalData.currentConfigurationXML, Context.MODE_PRIVATE);
		for (int i = 0; i < GlobalData.keyAppName.length; i ++) {
			GlobalData.keyMapCache.put(GlobalData.keyAppName[i], sp.getString(GlobalData.keyAppName[i], context.getString(R.string.unknown)));
			GlobalData.intKeyMapCache.put(GlobalData.keyAppName[i] + "_INT", sp.getInt(GlobalData.keyAppName[i] + "_INT", 0));
		}
	}
	
}
