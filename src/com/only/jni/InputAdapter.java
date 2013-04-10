package com.only.jni;

import android.util.Log;

public class InputAdapter {
	private static final String TAG = "InputAdapter";
	
	public static void onInputAdapterKeyDown(int scanCode, int value) {
		Log.e(TAG, "onInputAdapterKeyDown scancode = " + scanCode + " value = " + value);
	}
	
	public static void onInputAdapterKeyUp(int scanCode, int value) {
		Log.e(TAG, "onInputAdapterKeyUp  scanCode = " + scanCode + " value = " + value);
	}
	
	public static void onInputAdapterJoystickChange(int scanCode, int value) {
		Log.e(TAG, "onInputAdapterJoystickChange  scanCode = " + scanCode + " value = " + value);
	}
	
	public static void onDeviceAdded(String devName) {
		Log.e(TAG, "onDeviceAdded = " + devName);
	}
	
	static {
		System.loadLibrary("jni_input_adapter");
	}
	
	public static native boolean init();
	public static native boolean start();
	public static native boolean stop();
}
