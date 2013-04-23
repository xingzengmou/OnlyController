package com.only.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.KeyEvent;

public class KeyConfiguration {
	private static final String TAG = "KeyConfiguration";
	private static final String KEYLAYOUT_DIR = "/system/usr/keylayout/";
	private static final String DEFAULT_KEYLAYOUT_PATH = "/system/usr/keylayout/Generic.kl";
	
	private File configFile = null;
	private String configPath = null;
	private String configFileName = null;
//	private List<Map<Integer, Integer>> listKeyMap = null;
	private Map<Integer, Integer> keyMap = null;
	
	public KeyConfiguration(String configFileName) {
		this.configFileName = configFileName;
		configPath = KEYLAYOUT_DIR + configFileName + ".kl";
		configFile = new File(configPath);
		String usingFile = configFileName;
		if (!configFile.exists()) {
			configFile = new File(DEFAULT_KEYLAYOUT_PATH);
			usingFile = DEFAULT_KEYLAYOUT_PATH;
			if (!configFile.exists()) {
				configFile = null;
				usingFile = null;
			}
		}
		Log.e(TAG, "configFileName = " + this.configFileName + " using = " + usingFile);
	}
	
	public String getConfigFileName() {
		return this.configFileName;
	}
	
	public boolean parse() {
		if (configFile == null) {
			Log.e(TAG, "could't find the " + configPath);
			return false;
		}
		
//		listKeyMap = new ArrayList<Map<Integer, Integer>>();
		keyMap = new HashMap<Integer, Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			String line = br.readLine();
			while (line != null) {
				if (!line.trim().isEmpty() && line.getBytes()[0] == '#') {
					Log.e(TAG, "# line = " + line);
				} else {
					String[] list = line.split(" ");
					int index = 0;
					for (int i = 0; i < list.length; i ++) {
						if (!list[i].trim().isEmpty()) {
							list[index++] = list[i].trim();
						}
					}
					keyMap(list, index);
				}
				line = br.readLine();
				if (line != null)
				Log.e(TAG, "LINE = " + line + " isEmpty = " + line.trim().isEmpty());
			}  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private void keyMap(String[] keyList, int listSize) {
		Log.e(TAG, "keyList.size = " + keyList);
		String scanCodeStr = keyList[1].trim();
		String mapKey = keyList[2].trim();
		Log.e(TAG, "scancodestr = " + scanCodeStr + " mapKey = " + mapKey);
		int scanCode = Integer.parseInt(scanCodeStr);
		int intKeyMap = 0;
		if (mapKey.contains("SOFT_LEFT")) {
			intKeyMap = KeyEvent.KEYCODE_SOFT_LEFT;
		} else if (mapKey.contains("SOFT_RIGHT")) {
			intKeyMap = KeyEvent.KEYCODE_SOFT_RIGHT;
		} else if (mapKey.contains("HOME")) {
			intKeyMap = KeyEvent.KEYCODE_HOME;
		} else if (mapKey.contains("BACK")) {
			intKeyMap = KeyEvent.KEYCODE_BACK;
		} else if (mapKey.contains("CALL")) {
			intKeyMap = KeyEvent.KEYCODE_CALL;
		} else if (mapKey.contains("ENDCALL")) {
			intKeyMap = KeyEvent.KEYCODE_ENDCALL;
		} else if (mapKey.contains("STAR")) {
			intKeyMap = KeyEvent.KEYCODE_STAR;
		} else if (mapKey.contains("POUND")) {
			intKeyMap = KeyEvent.KEYCODE_POUND;
		} else if (mapKey.contains("DPAD_UP")) {
			intKeyMap = KeyEvent.KEYCODE_DPAD_UP;
		} else if (mapKey.contains("DPAD_DOWN")) {
			intKeyMap = KeyEvent.KEYCODE_DPAD_DOWN;
		} else if (mapKey.contains("DPAD_LEFT")) {
			intKeyMap = KeyEvent.KEYCODE_DPAD_LEFT;
		} else if (mapKey.contains("DPAD_RIGHT")) {
			intKeyMap = KeyEvent.KEYCODE_DPAD_RIGHT;
		} else if (mapKey.contains("DPAD_CENTER")) {
			intKeyMap = KeyEvent.KEYCODE_DPAD_CENTER;
		} else if (mapKey.contains("VOLUME_UP")) {
			intKeyMap = KeyEvent.KEYCODE_VOLUME_UP;
		} else if (mapKey.contains("VOLUME_DOWN")) {
			intKeyMap = KeyEvent.KEYCODE_VOLUME_DOWN;
		} else if (mapKey.contains("POWER")) {
			intKeyMap = KeyEvent.KEYCODE_POWER;
		} else if (mapKey.contains("CLEAR")) {
			intKeyMap = KeyEvent.KEYCODE_CLEAR;
		} else if (mapKey.contains("COMMA")) {
			intKeyMap = KeyEvent.KEYCODE_COMMA;
		} else if (mapKey.contains("PERIOD")) {
			intKeyMap = KeyEvent.KEYCODE_PERIOD;
		} else if (mapKey.contains("ALT_LEFT")) {
			intKeyMap = KeyEvent.KEYCODE_ALT_LEFT;
		} else if (mapKey.contains("ALT_RIGHT")) {
			intKeyMap = KeyEvent.KEYCODE_ALT_RIGHT;
		} else if (mapKey.contains("SHIFT_LEFT")) {
			intKeyMap = KeyEvent.KEYCODE_SHIFT_LEFT;
		} else if (mapKey.contains("SHIFT_RIGHT")) {
			intKeyMap = KeyEvent.KEYCODE_SHIFT_RIGHT;
		} else if (mapKey.contains("TAB")) {
			intKeyMap = KeyEvent.KEYCODE_TAB;
		} else if (mapKey.contains("SPACE")) {
			intKeyMap = KeyEvent.KEYCODE_SPACE;
		} else if (mapKey.contains("SYM")) {
			intKeyMap = KeyEvent.KEYCODE_SYM;
		} else if (mapKey.contains("EXPLORER")) {
			intKeyMap = KeyEvent.KEYCODE_EXPLORER;
		} else if (mapKey.contains("ENVELOPE")) {
			intKeyMap = KeyEvent.KEYCODE_ENVELOPE;
		} else if (mapKey.contains("ENTER")) {
			intKeyMap = KeyEvent.KEYCODE_ENTER;
		} else if (mapKey.contains("DEL")) {
			intKeyMap = KeyEvent.KEYCODE_DEL;
		} else if (mapKey.contains("GRAVE")) {
			intKeyMap = KeyEvent.KEYCODE_GRAVE;
		} else if (mapKey.contains("MINUS")) {
			intKeyMap = KeyEvent.KEYCODE_MINUS;
		} else if (mapKey.contains("EQUALS")) {
			intKeyMap = KeyEvent.KEYCODE_EQUALS;
		} else if (mapKey.contains("LEFT_BRACKET")) {
			intKeyMap = KeyEvent.KEYCODE_LEFT_BRACKET;
		} else if (mapKey.contains("RIGHT_BRACKET")) {
			intKeyMap = KeyEvent.KEYCODE_RIGHT_BRACKET;
		} else if (mapKey.contains("BACKSLASH")) {
			intKeyMap = KeyEvent.KEYCODE_BACKSLASH;
		} else if (mapKey.contains("SEMICOLON")) {
			intKeyMap = KeyEvent.KEYCODE_SEMICOLON;
		} else if (mapKey.contains("APOSTROPHE")) {
			intKeyMap = KeyEvent.KEYCODE_APOSTROPHE;
		} else if (mapKey.contains("SLASH")) {
			intKeyMap = KeyEvent.KEYCODE_SLASH;
		} else if (mapKey.contains("AT")) {
			intKeyMap = KeyEvent.KEYCODE_AT;
		} else if (mapKey.contains("NUM")) {
			intKeyMap = KeyEvent.KEYCODE_NUM;
		} else if (mapKey.contains("PLUS")) {
			intKeyMap = KeyEvent.KEYCODE_PLUS;
		} else if (mapKey.contains("MENU")) {
			intKeyMap = KeyEvent.KEYCODE_MENU;
		} else if (mapKey.contains("SEARCH")) {
			intKeyMap = KeyEvent.KEYCODE_SEARCH;
		} else if (mapKey.contains("MEDIA_PLAY_PAUSE")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
		} else if (mapKey.contains("MEDIA_STOP")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_STOP;
		} else if (mapKey.contains("MEDIA_NEXT")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_NEXT;
		} else if (mapKey.contains("MEDIA_PREVIOUS")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
		} else if (mapKey.contains("MEDIA_REWIND")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_REWIND;
		} else if (mapKey.contains("MEDIA_FAST_FORWARD")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
		} else if (mapKey.contains("MUTE")) {
			intKeyMap = KeyEvent.KEYCODE_MUTE;
		} else if (mapKey.contains("PAGE_UP")) {
			intKeyMap = KeyEvent.KEYCODE_PAGE_UP;
		} else if (mapKey.contains("PAGE_DOWN")) {
			intKeyMap = KeyEvent.KEYCODE_PAGE_DOWN;
		} else if (mapKey.contains("PICTSYMBOLS")) {
			intKeyMap = KeyEvent.KEYCODE_PICTSYMBOLS;
		} else if (mapKey.contains("SWITCH_CHARSET")) {
			intKeyMap = KeyEvent.KEYCODE_SWITCH_CHARSET;
		} else if (mapKey.contains("BUTTON_A")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_A;
		} else if (mapKey.contains("BUTTON_B")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_B;
		} else if (mapKey.contains("BUTTON_C")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_C;
		} else if (mapKey.contains("BUTTON_X")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_X;
		} else if (mapKey.contains("BUTTON_Y")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_Y;
		} else if (mapKey.contains("BUTTON_Z")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_Z;
		} else if (mapKey.contains("BUTTON_L1")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_L1;
		} else if (mapKey.contains("BUTTON_R1")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_R1;
		} else if (mapKey.contains("BUTTON_L2")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_L2;
		} else if (mapKey.contains("BUTTON_R2")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_R2;
		} else if (mapKey.contains("BUTTON_THUMBL")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_THUMBL;
		} else if (mapKey.contains("BUTTON_THUMBR")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_THUMBR;
		} else if (mapKey.contains("BUTTON_START")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_START;
		} else if (mapKey.contains("BUTTON_SELECT")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_SELECT;
		} else if (mapKey.contains("BUTTON_MODE")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_MODE;
		} else if (mapKey.contains("ESCAPE")) {
			intKeyMap = KeyEvent.KEYCODE_ESCAPE;
		} else if (mapKey.contains("FORWARD_DEL")) {
			intKeyMap = KeyEvent.KEYCODE_FORWARD_DEL;
		} else if (mapKey.contains("CTRL_LEFT")) {
			intKeyMap = KeyEvent.KEYCODE_CTRL_LEFT;
		} else if (mapKey.contains("CTRL_RIGHT")) {
			intKeyMap = KeyEvent.KEYCODE_CTRL_RIGHT;
		} else if (mapKey.contains("CAPS_LOCK")) {
			intKeyMap = KeyEvent.KEYCODE_CAPS_LOCK;
		} else if (mapKey.contains("SCROLL_LOCK")) {
			intKeyMap = KeyEvent.KEYCODE_SCROLL_LOCK;
		} else if (mapKey.contains("META_LEFT")) {
			intKeyMap = KeyEvent.KEYCODE_META_LEFT;
		} else if (mapKey.contains("META_RIGHT")) {
			intKeyMap = KeyEvent.KEYCODE_META_RIGHT;
		} else if (mapKey.contains("FUNCTION")) {
			intKeyMap = KeyEvent.KEYCODE_FUNCTION;
		} else if (mapKey.contains("SYSRQ")) {
			intKeyMap = KeyEvent.KEYCODE_SYSRQ;
		} else if (mapKey.contains("BREAK")) {
			intKeyMap = KeyEvent.KEYCODE_BREAK;
		} else if (mapKey.contains("MOVE_HOME")) {
			intKeyMap = KeyEvent.KEYCODE_MOVE_HOME;
		} else if (mapKey.contains("MOVE_END")) {
			intKeyMap = KeyEvent.KEYCODE_MOVE_END;
		} else if (mapKey.contains("INSERT")) {
			intKeyMap = KeyEvent.KEYCODE_INSERT;
		} else if (mapKey.contains("FORWARD")) {
			intKeyMap = KeyEvent.KEYCODE_FORWARD;
		} else if (mapKey.contains("MEDIA_PLAY")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_PLAY;
		} else if (mapKey.contains("MEDIA_PAUSE")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_PAUSE;
		} else if (mapKey.contains("MEDIA_CLOSE")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_CLOSE;
		} else if (mapKey.contains("MEDIA_EJECT")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_EJECT;
		} else if (mapKey.contains("MEDIA_RECORD")) {
			intKeyMap = KeyEvent.KEYCODE_MEDIA_RECORD;
		} else if (mapKey.contains("F1")) {
			intKeyMap = KeyEvent.KEYCODE_F1;
		} else if (mapKey.contains("F2")) {
			intKeyMap = KeyEvent.KEYCODE_F2;
		} else if (mapKey.contains("F3")) {
			intKeyMap = KeyEvent.KEYCODE_F3;
		} else if (mapKey.contains("F4")) {
			intKeyMap = KeyEvent.KEYCODE_F4;
		} else if (mapKey.contains("F5")) {
			intKeyMap = KeyEvent.KEYCODE_F5;
		} else if (mapKey.contains("F6")) {
			intKeyMap = KeyEvent.KEYCODE_F6;
		} else if (mapKey.contains("F7")) {
			intKeyMap = KeyEvent.KEYCODE_F7;
		} else if (mapKey.contains("F8")) {
			intKeyMap = KeyEvent.KEYCODE_F8;
		} else if (mapKey.contains("F9")) {
			intKeyMap = KeyEvent.KEYCODE_F9;
		} else if (mapKey.contains("F10")) {
			intKeyMap = KeyEvent.KEYCODE_F10;
		} else if (mapKey.contains("F11")) {
			intKeyMap = KeyEvent.KEYCODE_F11;
		} else if (mapKey.contains("F12")) {
			intKeyMap = KeyEvent.KEYCODE_F12;
		} else if (mapKey.contains("NUM_LOCK")) {
			intKeyMap = KeyEvent.KEYCODE_NUM_LOCK;
		} else if (mapKey.contains("NUMPAD_0")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_0;
		} else if (mapKey.contains("NUMPAD_1")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_1;
		} else if (mapKey.contains("NUMPAD_2")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_2;
		} else if (mapKey.contains("NUMPAD_3")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_3;
		} else if (mapKey.contains("NUMPAD_4")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_4;
		} else if (mapKey.contains("NUMPAD_5")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_5;
		} else if (mapKey.contains("NUMPAD_6")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_6;
		} else if (mapKey.contains("NUMPAD_7")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_7;
		} else if (mapKey.contains("NUMPAD_8")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_8;
		} else if (mapKey.contains("NUMPAD_9")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_9;
		} else if (mapKey.contains("NUMPAD_DIVIDE")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_DIVIDE;
		} else if (mapKey.contains("NUMPAD_MULTIPLY")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_MULTIPLY;
		} else if (mapKey.contains("NUMPAD_SUBTRACT")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_SUBTRACT;
		} else if (mapKey.contains("NUMPAD_ADD")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_ADD;
		} else if (mapKey.contains("NUMPAD_DOT")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_DOT;
		} else if (mapKey.contains("NUMPAD_COMMA")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_COMMA;
		} else if (mapKey.contains("NUMPAD_ENTER")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_ENTER;
		} else if (mapKey.contains("NUMPAD_EQUALS")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_EQUALS;
		} else if (mapKey.contains("NUMPAD_LEFT_PAREN")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN;
		} else if (mapKey.contains("NUMPAD_RIGHT_PAREN")) {
			intKeyMap = KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN;
		} else if (mapKey.contains("VOLUME_MUTE")) {
			intKeyMap = KeyEvent.KEYCODE_VOLUME_MUTE;
		} else if (mapKey.contains("BUTTON_1")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_1;
		} else if (mapKey.contains("BUTTON_2")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_2;
		} else if (mapKey.contains("BUTTON_3")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_3;
		} else if (mapKey.contains("BUTTON_4")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_4;
		} else if (mapKey.contains("BUTTON_5")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_5;
		} else if (mapKey.contains("BUTTON_6")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_6;
		} else if (mapKey.contains("BUTTON_7")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_7;
		} else if (mapKey.contains("BUTTON_8")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_8;
		} else if (mapKey.contains("BUTTON_9")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_9;
		} else if (mapKey.contains("BUTTON_10")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_10;
		} else if (mapKey.contains("BUTTON_11")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_11;
		} else if (mapKey.contains("BUTTON_12")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_12;
		} else if (mapKey.contains("BUTTON_13")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_13;
		} else if (mapKey.contains("BUTTON_14")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_14;
		} else if (mapKey.contains("BUTTON_15")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_15;
		} else if (mapKey.contains("BUTTON_16")) {
			intKeyMap = KeyEvent.KEYCODE_BUTTON_16;
		}
		if (intKeyMap != 0) {
			keyMap.put(scanCode, intKeyMap);
		}
	}
	
	public void dumpKeys() {
		Log.e(TAG, "keyMap = " + keyMap);
	}
	
	public int getKeyCode(int scanCode) {
		return keyMap.get(scanCode);
	}
}
