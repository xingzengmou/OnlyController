package com.only.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.only.controller.InputAdapterKeyEvent;
import com.only.net.socket.netSocket;
import com.only.touch.Profile;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

public class TouchUtils {
	private static final String TAG = "TouchUtils";
	private static Context context;
	private static List<Profile> touchList;
	private static List<Profile> mapListDown;
	private static List<Profile> mapListUp;
	
	public static void setContext(Context ctx) {
		context = ctx;
		mapListDown = new ArrayList<Profile> ();
		mapListUp = new ArrayList<Profile>();
		touchEventLooper.start();
	}
	
	public static void setTouchList(List<Profile> mTouchList) {
		touchList = mTouchList;
	}
	
	public static List<Profile> loadFile(String arg1) {
		FileReader fr;
		if (touchList == null) touchList = new ArrayList<Profile>();
		if (touchList.size() > 0)  touchList.clear();
		try {
			fr = new FileReader(context.getFilesDir() + "/" + arg1.toString());
			BufferedReader br = new BufferedReader(fr);
			String val = br.readLine();
			while (null != val) {
				Log.e(TAG, "read val = " + val);
				if (val.equals('\n')) val = br.readLine();
				Profile bp = new Profile();
				if (val != null && !val.equals("")) {
					bp.key = Integer.valueOf(val);	
				}
				val = br.readLine();
				if ( val != null && !val.equals("")) {
					bp.posX = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posY = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posR = Float.valueOf(val);
				}
				val = br.readLine();
				if (val != null && !val.equals("")) {
					bp.posType = Float.valueOf(val);
				}
				touchList.add(bp);
				Log.e(TAG, "load file add Profile key= " + bp.key + " posx= " + bp.posX + " posy= " + bp.posY);
				val = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return touchList;
	}
	
	public static boolean sendTouchMapDown(InputAdapterKeyEvent event) {
		if (touchList != null) {
			synchronized(mapListDown) {
				boolean profileMapped = false;
				for (Profile profile : mapListDown) {
					if (profile.key == event.keyCode) {
						profileMapped = true;
					}
				}
				if (!profileMapped) {
					for (Profile profile : touchList) {
						if (profile.key == event.keyCode) {
							Log.e(TAG, "found touch map key = " + profile.key + " x = " + profile.posX + " y = " + profile.posY);
							mapListDown.add(profile);
						}
					}
				}
				if (mapListDown.size() > 0) {
					String cmd = "injectTouch:" + mapListDown.size() + ":" + MotionEvent.ACTION_DOWN + ":";
					for (Profile profile : mapListDown) {
						cmd += profile.posX + ":" + profile.posY + ":";
					}
					cmd = cmd.substring(0, cmd.length() - 1);
					Log.e(TAG, "cmd = " + cmd + " touch down");
					netSocket.send(cmd);
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean sendTouchMapUp(InputAdapterKeyEvent event) {
		synchronized(mapListDown) {
			for (Profile profile : mapListDown) {
				if (profile.key == event.keyCode) {
					mapListUp.add(profile);
				}
			}
			
			if (mapListUp.size() > 0) {
				String cmd = "injectTouch:" + mapListUp.size() + ":" + MotionEvent.ACTION_UP + ":";
				for (Profile profile : mapListUp) {
					cmd += profile.posX + ":" + profile.posY + ":";
					synchronized (mapListDown) {
						mapListDown.remove(profile);
					}
				}
				mapListUp.clear();
				cmd = cmd.substring(0, cmd.length() - 1);
				Log.e(TAG, "cmd = " + cmd + " touch up");
				netSocket.send(cmd);
				return true;
			}
		}
		return false;
	}
	
	private static Thread touchEventLooper = new Thread(new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (mapListDown.size() > 0) {
					String cmd = "injectTouch:" + mapListDown.size() + ":" + MotionEvent.ACTION_MOVE + ":";
					for (Profile profile : mapListDown) {
						cmd += profile.posX + ":" + profile.posY + ":";
					}
					cmd = cmd.substring(0, cmd.length() - 1);
					Log.e(TAG, "cmd = " + cmd + " touch down");
					netSocket.send(cmd);
				}
				 
				if (mapListUp.size() > 0) {
					String cmd = "injectTouch:" + mapListUp.size() + ":" + MotionEvent.ACTION_UP + ":";
					for (Profile profile : mapListUp) {
						cmd += profile.posX + ":" + profile.posY + ":";
					}
					synchronized (mapListUp) {
						mapListUp.clear();
					}
					cmd = cmd.substring(0, cmd.length() - 1);
					Log.e(TAG, "cmd = " + cmd + " touch up");
					netSocket.send(cmd);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	});
}
