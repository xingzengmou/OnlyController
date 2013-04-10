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
	
	public static boolean root() {
		try {
			Process process = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(process.getOutputStream());
			DataInputStream dis = new DataInputStream(process.getInputStream());
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
	
	public static void execCmmd(String cmd) {
		String s = cmd + " \r";
		try {
			fos.write(s.getBytes());
			fos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
