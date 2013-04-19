package com.only.root;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.only.jni.Console;

import android.util.Log;
import android.widget.Toast;

public class Root {
	private static final String TAG = "Root";
	
	private static FileOutputStream fos;
	private static FileInputStream fis;
	private static DataOutputStream dos = null;
	private static DataInputStream dis = null;
	private static Process process = null;
	
	public static boolean root() {
		try {
			process = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(process.getOutputStream());
			dis = new DataInputStream(process.getInputStream());
			dos.write("id \n".getBytes());
			dos.flush();
			String retLine = dis.readLine();
			if (retLine == null) return false;
			if (retLine.contains("uid=0")) {
				initConsole();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printLog.start();
		return true;
	}
	
	private static void initConsole() {
		int[] pid = new int[1];
		String cmd = "/system/bin/sh";
		FileDescriptor fd = Console.createSubprocess(cmd, null, null, pid);
		fos = new FileOutputStream(fd);
		fis = new FileInputStream(fd);
		String initCommand = "export PATH=/data/local/bin:$PATH \r";
		byte[] buffer = new byte[255];
		try {
		fos.write(initCommand.getBytes());
		fos.flush();
		fos.write("su \r".getBytes());
		fos.flush();
		fos.write("busybox mount -o remount rw /system/ \r".getBytes());
		fos.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	public static synchronized void execCmmd(String cmd) {
		String s = cmd + " \r";
		Log.e(TAG, "execCmmd = " + s + " fos = " + fos);
		try {
			byte[] cmdByte = s.getBytes();
			fos.write(cmdByte);
			fos.flush();
//			root();
//			s = cmd + " \n";
//			cmdByte = s.getBytes();
//			dos.write(cmdByte);
//			dos.flush();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	  
	public static synchronized void chmod(String cmd) {
		try {
//			root();
			cmd += " \n";
			dos.write(cmd.getBytes());
			dos.flush();
//			dos.write("exit \n".getBytes());
//			dos.flush();
//			String s = cmd + " \r";
//			fos.write(s.getBytes());
//			fos.flush();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Thread printLog = new Thread(new Runnable() {
		public void run() {
			DataInputStream mdis = new DataInputStream(process.getErrorStream());
			String eline = null;
			String line = null;
			while (true) {
				try {
					eline = mdis.readLine();
					line = dis.readLine();
					while (eline != null || line != null) {
						Log.e(TAG, "eline = " + eline);
						Log.e(TAG, "line = " + line);
						eline = mdis.readLine();
						line = dis.readLine();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
}
