package com.only.jni;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.only.config.KeyConfiguration;
import com.only.controller.InputAdapterKeyEvent;
import com.only.core.EventService.EventHandler;
import com.only.root.Root;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
 
public class InputAdapter {
	private static final String TAG = "InputAdapter";
	private static List<KeyConfiguration> listKeyConfiguration = new ArrayList<KeyConfiguration>();
	private static List<OnKeyListener> listOnKeyListener = new ArrayList<OnKeyListener>();
	private static InputAdapterKeyEvent keyEvent = new InputAdapterKeyEvent();
	private static Context context;
	private static EventHandler mHandler;
	
	public static void onInputAdapterKeyDown(int scanCode, int value, String configFileName) {
		Log.e(TAG, "onInputAdapterKeyDown scancode = " + scanCode + " value = " + value + " configFileName = " + configFileName);
		for(KeyConfiguration kc : listKeyConfiguration) {
			if (kc.getConfigFileName().equals(configFileName)) {
				Log.e(TAG, "you presss scancode = " + scanCode + " keycode = " + kc.getKeyCode(scanCode));
				keyEvent.scanCode = scanCode;
				keyEvent.keyCode = kc.getKeyCode(scanCode);
				if (mHandler != null) mHandler.sendMessage(mHandler.obtainMessage(EventHandler.MSG_KEY_DOWN, keyEvent));
				Log.e(TAG, "mHandler = " + mHandler);
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
				if (mHandler != null) mHandler.sendMessage(mHandler.obtainMessage(EventHandler.MSG_KEY_UP, keyEvent));
				break;
			}
		}
	}
	
	public static void onInputAdapterJoystickChange(int joystickType, int x, int y, String configFileName) {
		Log.e(TAG, "onInputAdapterJoystickChange  joystickType = " + joystickType + " x = " + x + " y = " + y + " configFileName = " + configFileName);
	}
	
	public static void onDeviceAdded(String devName) {
		Log.e(TAG, "onDeviceAdded = " + devName);
		final String devPath = devName;
		final String cmd = "busybox chmod 777 " + devName;
//		Log.e(TAG, "run a command = " + cmd);
//		Root.execCmmd(cmd);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Root.chmod(cmd);
				try {
					Thread.sleep(1000);
					//openDeviceLocked(devPath);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}).start();
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

	public static void setHandler(EventHandler handler) {
		mHandler = handler;
	}
	
	public static native boolean init();
	public static native boolean start();
	public static native boolean stop();
	public static native int openDeviceLocked(String devPath);
	
	public interface OnKeyListener {
		public void onInputAdapterKeyDown(InputAdapterKeyEvent keyEvent);
		public void onInputAdapterKeyUp(InputAdapterKeyEvent keyEvent);
	}
	
	public static void onSetKeyListener(OnKeyListener listener) {
		if (listener != null) {
			listOnKeyListener.add(listener);
		}
	}
	
	public static void openEvent() {
		try {
			Process process = Runtime.getRuntime().exec("ls /dev/input/");
			final DataInputStream dis = new DataInputStream(process.getInputStream());
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String line;
					try {
						line = dis.readLine();
						while (line != null) {
							Log.e(TAG, "openevent line = " + line);
							if (line.contains("event")) {
								String cmd = "busybox chmod 777 /dev/input/" + line;
								Log.e(TAG, "cmd = " + cmd);
//								Root.execCmmd(cmd);
								Root.chmod(cmd);
								Thread.sleep(1000);
								if (openDeviceLocked("/dev/input/" + line) < 0) {
									Log.e(TAG, "open /dev/intpu/" + line + " error");
								}
							}
							line = dis.readLine();
						}
					} catch (IOException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
