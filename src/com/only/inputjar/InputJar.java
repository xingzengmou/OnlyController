package com.only.inputjar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.only.controller.R;
import com.only.root.Root;

public class InputJar {
	private static final String TAG = "InputJar";
	
	public static boolean run(final Activity thiz) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			AlertDialog.Builder b = new AlertDialog.Builder(thiz);
			b.setMessage(R.string.external_starage_has_unmounted);
			b.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					thiz.finish();
				}
			});
			b.show();
		} else {
			copyInputJarFile(thiz);
			runInputJar();
		}
		
		return true;
	}
	
	private static boolean copyInputJarFile(final Activity thiz) {
		File dir = new File(Environment.getExternalStorageDirectory() + "/inputjar");
		if (dir.exists()) {
			File file = new File(Environment.getExternalStorageDirectory() + "/inputjar/OnlyInput.jar");
			if (file.exists()) {
				dir = new File("/data/inputjar");
				boolean fileExit = false;
				if (dir.exists()) {
					fileExit = true;
					file = new File("/data/inputjar/OnlyInput.jar");
					if (file.exists()) {
						return true;
					} else {
						fileExit = false;
					}
				}
				if (!fileExit) {
					copyInputJarFileFromSDCARDToData();
				}
			} else {
				copyInputJarFileFromAssertToSDCARD(thiz);
			}
		} else {
			if (!dir.mkdir()) {
				AlertDialog.Builder b = new AlertDialog.Builder(thiz);
				b.setMessage(R.string.make_sdcard_input_jar_dir_error);
				b.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						thiz.finish();
					}
				});
				b.show();
			} else {
				copyInputJarFileFromAssertToSDCARD(thiz);
				copyInputJarFileFromSDCARDToData();
			}
		}
		
		return true;
	}
	
	private static boolean copyInputJarFileFromAssertToSDCARD(Activity thiz) {
		try {
			InputStream is = thiz.getAssets().open("OnlyInput.jar");
			int size = is.available();
			if (size > 0) {
				File file = new File(Environment.getExternalStorageDirectory() + "/inputjar/OnlyInput.jar");
				byte[] buffer = new byte[size];
				is.read(buffer);
				FileOutputStream os = new FileOutputStream(file);
				os.write(buffer);
				os.flush();
				os.close();
				os = null;
				file = null;
			} else {
				return false;
			} 
			is.close();
			is = null;
			
			is = thiz.getAssets().open("OnlyInput_above_4.0.jar");
			size = is.available();
			if (size > 0) {
				File file = new File(Environment.getExternalStorageDirectory() + "/inputjar/OnlyInput_above_4.0.jar");
				byte[] buffer = new byte[size];
				is.read(buffer);
				FileOutputStream os = new FileOutputStream(file);
				os.write(buffer);
				os.flush();
				os.close();
				os = null;
				file = null;
			} else {
				return false;
			} 
			is.close();
			is = null;
			 
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private static boolean copyInputJarFileFromSDCARDToData() {
		String moveCmd = "busybox cp -rf " + Environment.getExternalStorageDirectory() + "/inputjar " + " /data/";
		Root.execCmmd(moveCmd);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	  
	private static void runInputJar() {
		Log.e(TAG, "release = " + Build.VERSION.RELEASE);
		String[] versions = new String[2];
		versions[0] = Build.VERSION.RELEASE.substring(0,  Build.VERSION.RELEASE.indexOf("."));
		String temp = Build.VERSION.RELEASE.substring(Build.VERSION.RELEASE.indexOf(".") + 1);
		versions[1] = temp.substring(0, temp.indexOf("."));
		for (String v : versions) {
			Log.e(TAG, "version slipt v = " + v);
		}
		String version = versions[0].trim() + "." + versions[1].trim();
		Log.e(TAG, "version = " + version);
		float fVersion = Float.parseFloat(version);
		Log.e(TAG, "fVersion = " + fVersion);
		String cmd = "";
		if (fVersion <= 4.0) {
			Log.e(TAG, "your os version is = " + fVersion + " so inputjar is OnlyInput.jar");
			cmd = "export LD_LIBRARY_PATH=/vender/lib; export CLASSPATH=/data/inputjar/OnlyInput.jar; exec app_process /system/bin com.only.input.OnlyInput &";
		} else {
			Log.e(TAG, "your os version is = " + fVersion + " so inputjar is OnlyInput_above_4.0.jar");
			cmd = "export LD_LIBRARY_PATH=/vender/lib; export CLASSPATH=/data/inputjar/OnlyInput_above_4.0.jar; exec app_process /system/bin com.only.input.OnlyInput &";
		}
		Root.execCmmd(cmd);
	}
	
	private static void runInputJarCmd(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			DataOutputStream dos = new DataOutputStream(process.getOutputStream());
			DataInputStream dis = new DataInputStream(process.getInputStream());
			String line = dis.readLine();
			while (line != null) {
				Log.e(TAG, "runInputJarCmd = " + line);
				line = dis.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
