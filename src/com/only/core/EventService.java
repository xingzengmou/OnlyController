package com.only.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.only.config.KeyConfiguration;
import com.only.controller.InputAdapterKeyEvent;
import com.only.controller.R;
import com.only.controller.data.GlobalData;
import com.only.inputjar.InputJar;
import com.only.jni.InputAdapter;
import com.only.joystick.JoystickDataAnalysist;
import com.only.net.socket.netSocket;
import com.only.root.Root;
import com.only.touch.Position;
import com.only.touch.Profile;
import com.only.touch.ScreenView;
import com.only.utils.KeyUtils;
import com.only.utils.TouchUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class EventService extends Service {
	private static final String TAG = "EventService";
	private static final String ACTION = "com.only.core.EventService";
	
	private static EventHandler mHandler;
	private InputAdapterKeyEvent event;
	private Context context;
	private static Activity thiz;
	private String runningPackageName;
	private static boolean packageIsRunning = false; //��ǰ���е�Ӧ���ǲ�������IME�����ã��������ΪTRUE�� ��ΪFALSE
	
	/**
	 * touch configuration params
	 */
	private ScreenView screenView;
	private int backKeyCount = 0;
	private boolean touched = false;
	private int oldKey;
	private boolean noTouchData = true;
	private boolean debug = true;
	private Position bop;
	private float touchX = 0.0f;
	private float touchY = 0.0f;
	private float touchR = 0.0f;
	private boolean hasLeftJoystick = false;
	private boolean hasRightJoystick = false;
	private boolean tpconfiging = false;
	private static Handler mJoystickHandler;
	  
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;  
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "EventService oncreate");
		context = this.getApplicationContext();
		mHandler = new EventHandler();
		JoystickDataAnalysist.init();
		new Thread(new Runnable() {
			public void run() {
				envInit();
			}
		}).start();
	}
	
	public static void setActivity(Activity activity) {
		thiz = activity;
	}
	
	public static void setJoystickHandler(Handler handler) {
		mJoystickHandler = handler;
	}

	private void envInit() {
			// TODO Auto-generated method stub
			if (Root.root()) {
				mHandler.sendEmptyMessage(EventHandler.MSG_ROOTED);
				if (InputJar.run(thiz)) {
					InputAdapter.init();
					InputAdapter.openEvent();
					InputAdapter.start();
				}
				Log.e(TAG, "inputjar running will to connect netsocket");
				if (!netSocket.connectService()) {
					mHandler.sendEmptyMessage(EventHandler.MSG_CONNECT_INPUT_JAR_FAILED);
				}
				mHandler.sendEmptyMessage(EventHandler.MSG_INPUTJAR_CONNECTED);
			} else {
				mHandler.sendEmptyMessage(EventHandler.MSG_ROOT_FAILED);
			}
			Log.e(TAG, "netsocket connect finish, controllercore start");
			TouchUtils.setContext(context);
			appsMonitor(context);
			InputAdapter.setHandler(mHandler);
	}
		
	public class EventHandler extends Handler {
		public static final int MSG_KEY_DOWN = 0X01;
		public static final int MSG_KEY_UP = 0X02;
		public static final int MSG_JOYSTICK = 0X03;
		public static final int MSG_CONNECT_INPUT_JAR_FAILED = 0x04;
		public static final int MSG_INPUTJAR_CONNECTED = 0x05;
		public static final int MSG_ROOT_FAILED = 0x06;
		public static final int MSG_ROOTED = 0x07;
		
		public void handleMessage(Message msg) {
			AlertDialog d = null;
			switch (msg.what) {
			case MSG_KEY_DOWN:
				event = (InputAdapterKeyEvent) msg.obj;
				if (!getKeyCode(event)) break; 
				Log.e(TAG, "MSG_KEY_DOWN event.keyCode = " + event.keyCode + " tpconfiging = " + tpconfiging + " packageIsRunning = " + packageIsRunning);
				if (!packageIsRunning) break;
				if (event.keyCode == 25 && !tpconfiging) {
					startTouchConfigurationView();
					tpconfiging = true;
					break;
				}
				if (!tpconfiging && !TouchUtils.sendTouchMapDown(event)) {
					KeyUtils.sendMapKeyDown(event);
				}
				break;
			case MSG_KEY_UP:
				if (!packageIsRunning) break;
				event = (InputAdapterKeyEvent) msg.obj;
				if (!getKeyCode(event)) break; 
				Log.e(TAG, "MSG_KEY_UP event.keyCode = " + event.keyCode + " tpconfiging = " + tpconfiging + " packageIsRunning = " + packageIsRunning);
				if (!tpconfiging && !TouchUtils.sendTouchMapUp(event)) {
					KeyUtils.sendMapKeyUp(event);
				}
				break;
			case MSG_JOYSTICK:
				event = (InputAdapterKeyEvent) msg.obj;
				if (event.joystickType == JoystickDataAnalysist.JOYSTICK_RIGHT_DATA) {
					mJoystickHandler.sendMessage(mJoystickHandler.obtainMessage(JoystickDataAnalysist.MSG_JOYSTICK_RIGHT_DATA, msg.obj));
				} else if (event.joystickType == JoystickDataAnalysist.JOYSTICK_LEFT_DATA) {
					mJoystickHandler.sendMessage(mJoystickHandler.obtainMessage(JoystickDataAnalysist.MSG_JOYSTICK_LEFT_DATA, msg.obj));
				}
				break;
			case MSG_CONNECT_INPUT_JAR_FAILED:
				AlertDialog.Builder b = new AlertDialog.Builder(thiz);
				b.setMessage(R.string.connect_inputjar_error);
				b.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						thiz.finish();
					}
				});
				b.setCancelable(false);
				d = b.create();
				d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				d.show();
				break;
			case MSG_INPUTJAR_CONNECTED:
				break;
			case MSG_ROOT_FAILED:
				AlertDialog.Builder b1 = new AlertDialog.Builder(thiz);
				b1.setMessage(R.string.root_failed);
				b1.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						thiz.finish();
					}
				});
				b1.setCancelable(false);
				d = b1.create();
				d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				d.show();
				break;
			case MSG_ROOTED:
				Toast.makeText(context, R.string.root_successful, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	
	private boolean getKeyCode(InputAdapterKeyEvent event) {
		for(KeyConfiguration kc : GlobalData.listKeyConfiguration) {
			Log.e(TAG, "suport keyconfiguration = " + kc.getConfigFileName());
			if (kc.getConfigFileName().equals(event.configFile)) {
				Log.e(TAG, "you presss scancode = " + event.scanCode + " keycode = " + kc.getKeyCode(event.scanCode));
				event.keyCode = kc.getKeyCode(event.scanCode);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * auto load configuration file
	 * @param context
	 */
	public void appsMonitor(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningAppProcessInfo> lrapi = am.getRunningAppProcesses();
					if (waitCurrentPackageQuti(lrapi)) {
						for (int i = 0; i < lrapi.size(); i ++) {
							RunningAppProcessInfo rpi = lrapi.get(i);
//							Log.e(TAG, " package = " + rpi.processName);
							for (int j = 0; j < GlobalData.listCache.size(); j ++) {
								Map<String, Object> map = GlobalData.listCache.get(j);
								String dbPackageName = map.get("packageName").toString();
								if (rpi.processName.contains(dbPackageName) && 
										rpi.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
									GlobalData.currentConfigurationXML = dbPackageName;
									loadKeyMapConfigurationToCache(context);
									packageIsRunning = true;
//									Log.e(TAG, "appsMonitor packageIsRunning = " + packageIsRunning + " found package  = " + dbPackageName);
									break;
								}
							}
						}
					} 
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	private boolean waitCurrentPackageQuti(List<RunningAppProcessInfo> lrpi) {
		for (int i = 0; i < lrpi.size(); i ++) {
			RunningAppProcessInfo rpi = lrpi.get(i);
//			Log.e(TAG, "waitCurrentPackageQuti package = " + rpi.processName);
			if (rpi.processName.contains(GlobalData.currentConfigurationXML)
					&& (rpi.importance & RunningAppProcessInfo.IMPORTANCE_FOREGROUND) == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				packageIsRunning = true;
				return false;
			} else if (rpi.processName.contains(GlobalData.currentConfigurationXML)) {
				Log.e(TAG, "GlobalData.currentConfigurationXM packagename = " + GlobalData.currentConfigurationXML + " quit");
				break;
			}
		}  
//		Log.e(TAG, "GlobalData.currentConfigurationXM packagename = " + GlobalData.currentConfigurationXML + " quit");
		packageIsRunning = false;
		return true;
	}
	
	private void loadKeyMapConfigurationToCache(Context context) {
		Log.e(TAG, "use configuration xml = " + GlobalData.currentConfigurationXML + ".xml");
		SharedPreferences sp = context.getSharedPreferences(GlobalData.currentConfigurationXML, Context.MODE_PRIVATE);
		for (int i = 0; i < GlobalData.keyAppName.length; i ++) {
			GlobalData.keyMapCache.put(GlobalData.keyAppName[i], sp.getString(GlobalData.keyAppName[i], context.getString(R.string.unknown)));
			GlobalData.intKeyMapCache.put(GlobalData.keyAppName[i] + "_INT", sp.getInt(GlobalData.keyAppName[i] + "_INT", 0));
		}
		
		if (GlobalData.keyList != null) GlobalData.keyList.clear();
		GlobalData.keyList = null;
		GlobalData.keyList = TouchUtils.loadFile(GlobalData.currentConfigurationXML + ".tp");
	} 

	private void startTouchConfigurationView() {
//		Log.e(TAG, "startTouchConfigurationView packageIsRunning = " + packageIsRunning); 
		if (!packageIsRunning) return;
		GlobalData.keyList = null;
		GlobalData.keyList = new ArrayList<Profile>();
		Dialog tpDialogView = new Dialog(context,  R.style.selectorDialog);
		tpDialogView.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		tpDialogView.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		tpDialogView.setContentView(R.layout.view_touch_configuration);
		screenView = (ScreenView) tpDialogView.findViewById(R.id.sv_touch);
		screenView.setOnTouchListener(onTouchConfigurationViewListener);
		tpDialogView.show();
		tpDialogView.setOnKeyListener(onTpDialogViewKeyListener);
		tpDialogView.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				tpconfiging = false;
			}
		});
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = tpDialogView.getWindow().getAttributes();
		lp.width = display.getWidth();
		lp.height = display.getHeight();
		lp.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;// | LayoutParams.FLAG_NOT_FOCUSABLE;
		lp.alpha = 0.9f;
		tpDialogView.getWindow().setAttributes(lp); 
		
	} 
	
	private View.OnTouchListener onTouchConfigurationViewListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			Log.e(TAG, "even.getaction = " + event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchX = event.getRawX(); //yuan dian
				touchY = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float tx = event.getRawX(); //zhong dian
				float ty = event.getRawY();
				float tr = (float)Math.sqrt(Math.pow(Math.abs(touchX - tx) , 2) + Math.pow(Math.abs(touchY - ty) , 2));
				touchR = tr;
				bop = new Position();
				bop.x = touchX;
				bop.y = touchY;
				bop.r = touchR;
				bop.msg = "";
				backKeyCount = 0;
				touched = true;
				noTouchData = false;
				screenView.drawNow(true);
				screenView.drawCircle2(touchX, touchY, touchR);
				Log.e(TAG, "touch R = " + touchR + " touchX = " + touchX + " touchY = " + touchY);
				break;
			case MotionEvent.ACTION_UP:
				if (touchR < 50.0f) {
					bop = new Position();
					bop.x = event.getRawX();
					bop.y = event.getRawY();
					bop.r = 0;
					bop.msg = "";
					screenView.drawNow(true);
//					screenView.drawCircle(arg1.getX(), arg1.getY());
					screenView.drawCircle(event.getRawX(), event.getRawY());
					backKeyCount = 0;
					touched = true;
					noTouchData = false;
				}
				break;
		}
		return true;
		}
	};
	
	private DialogInterface.OnKeyListener onTpDialogViewKeyListener = new DialogInterface.OnKeyListener() {
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			Log.e(TAG, "dialog keyCode = " + keyCode);
			if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
				backKeyCount ++;
				if (backKeyCount == 1 && noTouchData) return false; //���screenview ��û����ݣ���ֱ���˳�touch
				if (backKeyCount < 2) {
					screenView.drawNow(false);
					touched = false;
					touchR = 0;
					return true;
				}
			}
			Log.e(TAG, "touched = " + touched + " oldkey = " +oldKey + " event.getKeyCode = " + event.getKeyCode());
			if (touched && (oldKey != event.getKeyCode())) {
				if (!drawInfo(event)) return false;
				Profile mProfile = new Profile();
				mProfile.key = event.getKeyCode();
//				mProfile.key = event.getScanCode();
				mProfile.posX = screenView.getTouchX();  
				mProfile.posY = screenView.getTouchY();
				mProfile.posR = screenView.getTouchR();
				mProfile.posType = screenView.getCircleType();
				GlobalData.keyList.add(mProfile);
//				if (event.getKeyCode() != GlobalData.joystickAreaCheckKey) { 
				oldKey = event.getKeyCode();
//				}
				if (debug) Log.e(TAG, "config a key pos scankey= " + oldKey + " posx= " + mProfile.posX + " posy= " + mProfile.posY);
				screenView.drawNow(false);
				touched = false;
				backKeyCount ++;
				return false;
			}
			
			if (!noTouchData && event.getKeyCode() != 25 && oldKey != event.getKeyCode()
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				tpconfiging = false;
				saveFile(GlobalData.currentConfigurationXML + ".tp");
				noTouchData = true;
			}
		
			oldKey = 0;
			return false;
		}
	};
	
	private boolean drawInfo(KeyEvent event) {
		if (bop.r > 50 && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) { //touchR < 50 ���Ǵ�����Ͱ����ӳ�䣬touchR > 50����ҡ������ӳ��
			bop.color = Color.GREEN;
			DisplayMetrics dm = this.getResources().getDisplayMetrics();
			if ((bop.x > (dm.widthPixels / 2)) && ((bop.x - bop.r) <=  (dm.widthPixels / 2))) { //���Բ�����ĵ�X���bop.x���������Ұ�ߣ������Բ����ҡ�����򣬷�֮������ҡ������
				Toast.makeText(this, this.getString(R.string.invalid_joystick_area), Toast.LENGTH_SHORT).show();
				return false;
			} else if ((bop.x < (dm.widthPixels /2)) && ((bop.x + bop.r) >= (dm.widthPixels /2))) {
				Toast.makeText(this, this.getString(R.string.invalid_joystick_area), Toast.LENGTH_SHORT).show();
				return false;
			} else if (bop.r < 50) {
				Log.e(TAG, "bop.r = " + bop.r + " invalid joystick_area");
				Toast.makeText(this, this.getString(R.string.invalid_joystick_area), Toast.LENGTH_SHORT).show();
				return false;
			}
			if ((bop.x - bop.r) > (dm.widthPixels/2)) { //��ҡ������
				if (hasRightJoystick) {
					Toast.makeText(this, this.getString(R.string.has_right_joystick), Toast.LENGTH_SHORT).show();
					return false;
				}
				bop.msg = this.getString(R.string.right_joystick);
				hasRightJoystick = true;
				bop.type = Position.TYPE_RIGHT_JOYSTICK;
				screenView.setCircleType(bop.type);
			} else {
				if (hasLeftJoystick) {
					Toast.makeText(this, this.getString(R.string.has_left_joystick), Toast.LENGTH_SHORT).show();
					return false;
				}
				bop.msg = this.getString(R.string.left_joystick);
				hasLeftJoystick = true;
				bop.type = Position.TYPE_LEFT_JOYSTICK;
				screenView.setCircleType(bop.type);
			}
			screenView.posList.add(bop); 
			touchR = 0.0f;
			return true;
		} 
		bop.color = Color.GREEN;
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_0:
			bop.msg = "0";
			break;
		case KeyEvent.KEYCODE_1:
			bop.msg = "1";
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			bop.msg = "VOLUME_DOWN";
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			bop.msg = "VOLUME_UP";
			break;
		case KeyEvent.KEYCODE_2:
			bop.msg = "2";
			break;
		case KeyEvent.KEYCODE_3:
			bop.msg = "3";
			break;
		case KeyEvent.KEYCODE_4:
			bop.msg = "4";
			break;
		case KeyEvent.KEYCODE_5:
			bop.msg = "5";
			break;
		case KeyEvent.KEYCODE_6:
			bop.msg = "6";
			break;
		case KeyEvent.KEYCODE_7:
			bop.msg = "7";
			break;
		case KeyEvent.KEYCODE_8:
			bop.msg = "8";
			break;
		case KeyEvent.KEYCODE_9:
			bop.msg = "9";
			break;
		case KeyEvent.KEYCODE_A:
			bop.msg = "A";
			break;
		case KeyEvent.KEYCODE_ALT_LEFT:
			bop.msg = "ALT_LEFT";
			break;
		case KeyEvent.KEYCODE_ALT_RIGHT:
			bop.msg = "ALT_RIGHT";
			break;
		case KeyEvent.KEYCODE_APOSTROPHE:
			bop.msg = "APOSTROPHE";
			break;
		case KeyEvent.KEYCODE_B:
			bop.msg = "B";
			break;
		case KeyEvent.KEYCODE_BACKSLASH:
			bop.msg = "BACKSLASH";
			break;
		case KeyEvent.KEYCODE_C:
			bop.msg = "C";
			break;
		case KeyEvent.KEYCODE_COMMA:
			bop.msg = "COMMA";
			break;
		case KeyEvent.KEYCODE_D:
			bop.msg = "D";
			break;
		case KeyEvent.KEYCODE_E:
			bop.msg = "E";
			break;
		case KeyEvent.KEYCODE_EQUALS:
			bop.msg = "EQUALS";
			break;
		case KeyEvent.KEYCODE_F:
			bop.msg = "F";
			break;
		case KeyEvent.KEYCODE_G:
			bop.msg = "G";
			break;
		case KeyEvent.KEYCODE_H:
			bop.msg = "H";
			break;
		case KeyEvent.KEYCODE_I:
			bop.msg = "I";
			break;
		case KeyEvent.KEYCODE_J:
			bop.msg = "J";
			break;
		case KeyEvent.KEYCODE_K:
			bop.msg = "K";
			break;
		case KeyEvent.KEYCODE_L:
			bop.msg = "L";
			break;
		case KeyEvent.KEYCODE_N:
			bop.msg = "N";
			break;
		case KeyEvent.KEYCODE_M:
			bop.msg = "M";
			break;
		case KeyEvent.KEYCODE_O:
			bop.msg = "O";
			break;
		case KeyEvent.KEYCODE_P:
			bop.msg = "P";
			break;
		case KeyEvent.KEYCODE_Q:
			bop.msg = "Q";
			break;
		case KeyEvent.KEYCODE_R:
			bop.msg = "R";
			break;
		case KeyEvent.KEYCODE_S:
			bop.msg = "S";
			break;
		case KeyEvent.KEYCODE_T:
			bop.msg = "T";
			break;
		case KeyEvent.KEYCODE_U:
			bop.msg = "U";
			break;
		case KeyEvent.KEYCODE_V:
			bop.msg = "V";
			break;
		case KeyEvent.KEYCODE_W:
			bop.msg = "W";
			break;
		case KeyEvent.KEYCODE_X:
			bop.msg = "X";
			break;
		case KeyEvent.KEYCODE_Y:
			bop.msg = "Y";
			break;
		case KeyEvent.KEYCODE_Z:
			bop.msg = "Z";
			break;
		case KeyEvent.KEYCODE_BUTTON_A:
			bop.msg = "BUTTON_A";
			break;
		case KeyEvent.KEYCODE_BUTTON_B:
			bop.msg = "BUTTON_B";
			break;
		case KeyEvent.KEYCODE_BUTTON_C:
			bop.msg = "BUTTON_C";
			break;
		case KeyEvent.KEYCODE_BUTTON_L1:
			bop.msg = "BUTTON_L1";
			break;
		case KeyEvent.KEYCODE_BUTTON_L2:
			bop.msg = "BUTTON_L2";
			break;
		case KeyEvent.KEYCODE_BUTTON_MODE:
			bop.msg = "BUTTON_MODE";
			break;
		case KeyEvent.KEYCODE_BUTTON_R1:
			bop.msg = "BUTTON_R1";
			break;
		case KeyEvent.KEYCODE_BUTTON_R2:
			bop.msg = "BUTTON_R2";
			break;
		case KeyEvent.KEYCODE_BUTTON_SELECT:
			bop.msg = "BUTTON_SELECT";
			break;
		case KeyEvent.KEYCODE_BUTTON_START:
			bop.msg = "BUTTON_START";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBL:
			bop.msg = "BUTTON_THUMBL";
			break;
		case KeyEvent.KEYCODE_BUTTON_THUMBR:
			bop.msg = "BUTTON_THUMBR";
			break;
		case KeyEvent.KEYCODE_BUTTON_X:
			bop.msg = "BUTTON_X";
			break;
		case KeyEvent.KEYCODE_BUTTON_Y:
			bop.msg = "BUTTON_Y";
			break;
		case KeyEvent.KEYCODE_BUTTON_Z:
			bop.msg = "BUTTON_Z";
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			bop.msg = "DPAD_CENTER";
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			bop.msg = "DPAD_DWON";
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			bop.msg = "DPAD_LEFT";
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			bop.msg = "DPAD_RIGHT";
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			bop.msg = "DPAD_UP";
			break;
		case KeyEvent.KEYCODE_LEFT_BRACKET:
			bop.msg = "LEFT_BRACKET";
			break;
		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
			bop.msg = "MEDIA_FAST_FORWARD";
			break;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			bop.msg = "MEDIA_NEXT";
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			bop.msg = "MEDIA_PLAY_PAUSE";
			break;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			bop.msg = "MEDIA_PREVIOUS";
			break;
		case KeyEvent.KEYCODE_MEDIA_REWIND:
			bop.msg = "MEDIA_REWIND";
			break;
		case KeyEvent.KEYCODE_MEDIA_STOP:
			bop.msg = "MEDIA_STOP";
			break;
		case KeyEvent.KEYCODE_MINUS:
			bop.msg = "MINUS";
			break;
		case KeyEvent.KEYCODE_NUM:
			bop.msg = "NUM";
			break;
		case KeyEvent.KEYCODE_PAGE_DOWN:
			bop.msg = "PAGE_DOWN";
			break;
		case KeyEvent.KEYCODE_PAGE_UP:
			bop.msg = "PAGE_UP";
			break;
		case KeyEvent.KEYCODE_PICTSYMBOLS:
			bop.msg = "PICTSYMBOLS";
			break;
		case KeyEvent.KEYCODE_PLUS:
			bop.msg = "PLUS";
			break;
		case KeyEvent.KEYCODE_POUND:
			bop.msg = "POUND";
			break;
		case KeyEvent.KEYCODE_RIGHT_BRACKET:
			bop.msg = "RIGHT_BRACKET";
			break;
		case KeyEvent.KEYCODE_SEARCH:
			bop.msg = "SEARCH";
			break;
		case KeyEvent.KEYCODE_SEMICOLON:
			bop.msg = "SEMICOLON";
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			bop.msg = "SHIFT_LEFT";
			break;
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			bop.msg = "SHIFT_RIGHT";
			break;
		case KeyEvent.KEYCODE_SLASH:
			bop.msg = "SLASH";
			break;
		case KeyEvent.KEYCODE_SOFT_LEFT:
			bop.msg = "SOFT_LEFT";
			break;
		case KeyEvent.KEYCODE_SOFT_RIGHT:
			bop.msg = "SOFT_RIGHT";
			break;
		case KeyEvent.KEYCODE_SPACE:
			bop.msg = "SPACE";
			break;
		case KeyEvent.KEYCODE_STAR:
			bop.msg = "STAR";
			break;
		case KeyEvent.KEYCODE_SYM:
			bop.msg = "SYM";
			break;
		case KeyEvent.KEYCODE_TAB:
			bop.msg = "TAB";
			break;
		case KeyEvent.KEYCODE_BUTTON_1:
			bop.msg = "BUTTON_1";
			break;
		case KeyEvent.KEYCODE_BUTTON_2:
			bop.msg = "BUTTON_2";
			break;
		case KeyEvent.KEYCODE_BUTTON_3:
			bop.msg = "BUTTON_3";
			break;
		case KeyEvent.KEYCODE_BUTTON_4:
			bop.msg = "BUTTON_4";
			break;
		case KeyEvent.KEYCODE_BUTTON_5:
			bop.msg = "BUTTON_5";
			break;
		case KeyEvent.KEYCODE_BUTTON_6:
			bop.msg = "BUTTON_6";
			break;
		case KeyEvent.KEYCODE_BUTTON_7:
			bop.msg = "BUTTON_7";
			break;
		case KeyEvent.KEYCODE_BUTTON_8:
			bop.msg = "BUTTON_8";
			break;
		case KeyEvent.KEYCODE_BUTTON_9:
			bop.msg = "BUTTON_9";
			break;
		case KeyEvent.KEYCODE_BUTTON_10:
			bop.msg = "BUTTON_10";
			break;
		case KeyEvent.KEYCODE_BUTTON_11:
			bop.msg = "BUTTON_11";
			break;
		case KeyEvent.KEYCODE_BUTTON_12:
			bop.msg = "BUTTON_12";
			break;
		case KeyEvent.KEYCODE_BUTTON_13:
			bop.msg = "BUTTON_13";
			break;
		case KeyEvent.KEYCODE_BUTTON_14:
			bop.msg = "BUTTON_14";
			break;
		case KeyEvent.KEYCODE_BUTTON_15:
			bop.msg = "BUTTON_15";
			break;
		case KeyEvent.KEYCODE_BUTTON_16:
			bop.msg = "BUTTON_16";
			break;
		default: break;
		}
		screenView.posList.add(bop);
		return true;
	}
	
	private void saveFile(String path) {
		try {
			FileOutputStream fos = context.openFileOutput(path, Context.MODE_PRIVATE);
			if (GlobalData.keyList != null) {
				for (int i = 0; i < GlobalData.keyList.size(); i ++) {
					fos.write(String.valueOf(GlobalData.keyList.get(i).key).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(GlobalData.keyList.get(i).posX).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(GlobalData.keyList.get(i).posY).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(GlobalData.keyList.get(i).posR).getBytes());
					fos.write("\n".getBytes());
					fos.write(String.valueOf(GlobalData.keyList.get(i).posType).getBytes());
					fos.write("\n".getBytes());
				}
			}
			fos.close();
			TouchUtils.setTouchList(GlobalData.keyList);
			Toast.makeText(context, context.getString(R.string.save_to) + GlobalData.currentConfigurationXML + ".tp", Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
