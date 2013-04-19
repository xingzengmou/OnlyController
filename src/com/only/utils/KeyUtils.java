package com.only.utils;

import android.util.Log;
import android.view.KeyEvent;

import com.only.controller.InputAdapterKeyEvent;
import com.only.controller.data.GlobalData;
import com.only.net.socket.netSocket;

public class KeyUtils {
	private static final String TAG = "KeyUtils";
	
	public static boolean sendMapKeyDown(InputAdapterKeyEvent event) {
		for (int i = 0; i < GlobalData.keyAppValue.length; i ++) {
			int mapCode = GlobalData.intKeyMapCache.get(GlobalData.keyAppName[i] + "_INT");
			Log.e(TAG, "sendMapKeyDown keyCode = " + event.keyCode + " mapCode = " + mapCode);
			if (mapCode == event.keyCode) {
				int eventCode = GlobalData.keyAppValue[i];
				Log.e(TAG, "found key = " + GlobalData.keyAppName[i] + " keycode = " + GlobalData.keyAppValue[i]);
				if (eventCode != 0) { // this key has mapped
					netSocket.send("injectKey:" + eventCode + ":0:" + KeyEvent.ACTION_DOWN);
				}
				break;
			} 
		}  
		
		return true;
	}
	
	public static boolean sendMapKeyUp(InputAdapterKeyEvent event) {
		for (int i = 0; i < GlobalData.keyAppValue.length; i ++) {
			int mapCode = GlobalData.intKeyMapCache.get(GlobalData.keyAppName[i] + "_INT");
			Log.e(TAG, " sendMapKeyUp keyCode = " + event.keyCode + " mapCode = " + mapCode);
			if (mapCode == event.keyCode) {
				int eventCode = GlobalData.keyAppValue[i];
				Log.e(TAG, "found key = " + GlobalData.keyAppName[i] + " keycode = " + GlobalData.keyAppValue[i]);
				if (eventCode != 0) { // this key has mapped
					netSocket.send("injectKey:" + eventCode + ":0:" + KeyEvent.ACTION_UP);
				}
				break;
			} 
		}
		return true;
	}
}
