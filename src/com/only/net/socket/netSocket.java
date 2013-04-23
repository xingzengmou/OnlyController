package com.only.net.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

public class netSocket {
	private static final String TAG = "netSocket";
	
	private static PrintWriter pw;
	private static DataInputStream dis;
	
	
	//udp
	private static DatagramSocket mDatagramSocket;
	private static int port = 6000;
	private static InetAddress mInetAddress;
	
	private static boolean useUDP = true;
	
	public static boolean connectService() {
		if (useUDP) {
			connectUDPServer();
		} else {
			new Thread(new Runnable() {
			public void run() {
			Socket socket;
			try {
				socket = new Socket("127.0.0.1", port);
				dis = new DataInputStream(socket.getInputStream());
				pw = new PrintWriter(socket.getOutputStream());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	//			return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	//			return false;
			}
			}}).start();
		}
		return true;
	}
	
	public static boolean connectUDPServer() {
		new Thread(new Runnable() {
			public void run() {
				try {
					mDatagramSocket = new DatagramSocket();
					mInetAddress = InetAddress.getByName("localhost");
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		return true;
	}
	
	static byte[] gMsg = new byte[1024];
	public static synchronized void send(final String content) {
		Thread sendThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!useUDP) {
					pw.println(content);
					pw.flush();
					try {
						Log.e(TAG, "get from service = " + dis.readLine());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					byte[] tbyte = content.getBytes();
					for (int i = 0; i < content.length(); i ++) {
						gMsg[i] = tbyte[i];
					}
					DatagramPacket dp = new DatagramPacket(gMsg, gMsg.length, mInetAddress, port);
					try {
						mDatagramSocket.send(dp);
//						Thread.sleep(200);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		sendThread.start();
		try {
			sendThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e(TAG, "send finish");
	}
	
}
