package com.only.jni;

import java.util.ArrayList;
import java.util.List;

import com.only.config.KeyConfiguration;
import com.only.root.Root;

import android.util.Log;
 
public class InputAdapter {
	private static final String TAG = "InputAdapter";
	
	private static List<KeyConfiguration> listKeyConfiguration = new ArrayList<KeyConfiguration>();
	private static List<OnKeyListener> listOnKeyListener = new ArrayList<OnKeyListener>();
	private static InputAdapterKeyEvent keyEvent;
	
	public static void onInputAdapterKeyDown(int scanCode, int value, String configFileName) {
		Log.e(TAG, "onInputAdapterKeyDown scancode = " + scanCode + " value = " + value + " configFileName = " + configFileName);
		for(KeyConfiguration kc : listKeyConfiguration) {
			if (kc.getConfigFileName().equals(configFileName)) {
				Log.e(TAG, "you presss scancode = " + scanCode + " keycode = " + kc.getKeyCode(scanCode));
				keyEvent.scanCode = scanCode;
				keyEvent.keyCode = kc.getKeyCode(scanCode);
//				for (OnKeyListener listener : listOnKeyListener) {
//					listener.onInputAdapterKeyDown(keyEvent);
//				}
				break;
			}
		}
	}
	
	public static void onInputAdapterKeyUp(int scanCode, int value, String configFileName) {
		Log.e(TAG, "onInputAdapterKeyUp  scanCode = " + scanCode + " value = " + value + " configFileName = " + configFileName);
		for(KeyConfiguration kc : listKeyConfiguration) {
			if (kc.getConfigFileName().equals(configFileName)) {
				Log.e(TAG, "you presss scancode = " + scanCode + " keycode = " + kc.getKeyCode(scanCode));
				keyEvent.scanCode = scanCode;
				keyEvent.keyCode = kc.getKeyCode(scanCode);
//				for (OnKeyListener listener : listOnKeyListener) {
//					listener.onInputAdapterKeyUp(keyEvent);
//				}
				break;
			}
		}
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
	
	public class InputAdapterKeyEvent {
		public int scanCode;
		public int keyCode; 
	}
	
	public interface OnKeyListener {
		public void onInputAdapterKeyDown(InputAdapterKeyEvent keyEvent);
		public void onInputAdapterKeyUp(InputAdapterKeyEvent keyEvent);
	}
	
	public static void onSetKeyListener(OnKeyListener listener) {
		if (listener != null) {
			listOnKeyListener.add(listener);
		}
	}
}
