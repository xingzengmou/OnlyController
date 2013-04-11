package com.only.net.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class netSocket {
	private static final String TAG = "netSocket";
	
	private static PrintWriter pw;
	private static DataInputStream dis;
	
	public static boolean connectService() {
		Socket socket;
		try {
			socket = new Socket("127.0.0.1", 44444);
			dis = new DataInputStream(socket.getInputStream());
			pw = new PrintWriter(socket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void send(String content) {
		pw.println(content);
		pw.flush();
		try {
			Log.e(TAG, "get from service = " + dis.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
