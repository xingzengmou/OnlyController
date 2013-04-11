package com.only.inputjar;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
			checkInputJarRunning(thiz);
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
		return true;
	}
	
	private static void runInputJar() {
		String cmd = "export LD_LIBRARY_PATH=/vender/lib; export CLASSPATH=/data/inputjar/OnlyInput.jar; exec app_process /system/bin com.blueocean.jnsinput.JNSInput";
		Root.execCmmd(cmd);
	}
	
	private static void checkInputJarRunning(Activity thiz) {
		String cmd = "ps > /data/inputjar/ps.txt";
		Root.execCmmd(cmd);
	}
}
