package com.only.jni;

import java.util.ArrayList;
import java.util.List;

import com.only.config.KeyConfiguration;
import com.only.root.Root;

import android.util.Log;

public class InputAdapter {
	private static final String TAG = "InputAdapter";
	
	private static List<KeyConfiguration> listKeyConfiguration = new ArrayList<KeyConfiguration>();
	
	public static void onInputAdapterKeyDown(int scanCode, int value, String configFileName) {
		Log.e(TAG, "onInputAdapterKeyDown scancode = " + scanCode + " value = " + value + " configFileName = " + configFileName);
	}
	
	public static void onInputAdapterKeyUp(int scanCode, int value, String configFileName) {
		Log.e(TAG, "onInputAdapterKeyUp  scanCode = " + scanCode + " value = " + value + " configFileName = " + configFileName);
	}
	
	public static void onInputAdapterJoystickChange(int scanCode, int value, String configFileName) {
		Log.e(TAG, "onInputAdapterJoystickChange  scanCode = " + scanCode + " value = " + value + " configFileName = " + configFileName);
	}
	
	public static void onDeviceAdded(String devName) {
		Log.e(TAG, "onDeviceAdded = " + devName);
		String cmd = "busybox chmod 777 /dev/input/" + devName;
		Log.e(TAG, "run a command = " + cmd);
		Root.execCmmd(cmd);
	}
	
	public static void onOpenEventConfigFile(String configFileName) {
		Log.e(TAG, "onOpenEventConfigFile configFileName = " + configFileName);
		KeyConfiguration mKeyConfiguration = new KeyConfiguration(configFileName);
		if (mKeyConfiguration.parse()) {
			listKeyConfiguration.add(mKeyConfiguration);
		}
	}
	
	static {
		System.loadLibrary("jni_input_adapter");
	}
	
	public static native boolean init();
	public static native boolean start();
	public static native boolean stop();
}
