package com.only.controller.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.only.config.KeyConfiguration;
import com.only.touch.Profile;

import android.view.KeyEvent;

public class GlobalData {
	public static String[] keyAppName = new String[] {
		"DPAD_CENTER",
		"DPAD_LEFT",
		"DPAD_RIGHT",
		"DPAD_DOWN",
		"DPAD_UP",
		"BUTTON_A",
		"BUTTON_B",
		"BUTTON_C",
		"BUTTON_X",
		"BUTTON_Y",
		"BUTTON_Z",
		"BUTTON_L1",
		"BUTTON_R1",
		"BUTTON_L2",
		"BUTTON_R2",
		"BUTTON_THUMBL",
		"BUTTON_THUMBR",
		"BUTTON_START",
		"BUTTON_SELECT",
		"BUTTON_MODE",
		"BUTTON_1",
		"BUTTON_2",
		"BUTTON_3",
		"BUTTON_4",
		"BUTTON_5",
		"BUTTON_6",
		"BUTTON_7",
		"BUTTON_8",
		"BUTTON_9",
		"BUTTON_10",
		"BUTTON_11",
		"BUTTON_12",
		"BUTTON_13",
		"BUTTON_14",
		"BUTTON_15",
		"BUTTON_16",
		"A",
		"B",
		"C",
		"D",
		"E",
		"F",
		"G",
		"H",
		"I",
		"J",
		"K",
		"L",
		"M",
		"N",
		"O",
		"P",
		"Q",
		"R",
		"S",
		"T",
		"U",
		"V",
		"W",
		"X",
		"Y",
		"Z",
	};
	
	public static int[] keyAppValue = new int[] {
		KeyEvent.KEYCODE_DPAD_CENTER,
		KeyEvent.KEYCODE_DPAD_LEFT,
		KeyEvent.KEYCODE_DPAD_RIGHT,
		KeyEvent.KEYCODE_DPAD_DOWN,
		KeyEvent.KEYCODE_DPAD_UP,
		KeyEvent.KEYCODE_BUTTON_A,
		KeyEvent.KEYCODE_BUTTON_B,
		KeyEvent.KEYCODE_BUTTON_C,
		KeyEvent.KEYCODE_BUTTON_X,
		KeyEvent.KEYCODE_BUTTON_Y,
		KeyEvent.KEYCODE_BUTTON_Z,
		KeyEvent.KEYCODE_BUTTON_L1,
		KeyEvent.KEYCODE_BUTTON_R1,
		KeyEvent.KEYCODE_BUTTON_L2,
		KeyEvent.KEYCODE_BUTTON_R2,
		KeyEvent.KEYCODE_BUTTON_THUMBL,
		KeyEvent.KEYCODE_BUTTON_THUMBR,
		KeyEvent.KEYCODE_BUTTON_START,
		KeyEvent.KEYCODE_BUTTON_SELECT,
		KeyEvent.KEYCODE_BUTTON_MODE,
		KeyEvent.KEYCODE_BUTTON_1,
		KeyEvent.KEYCODE_BUTTON_2,
		KeyEvent.KEYCODE_BUTTON_3,
		KeyEvent.KEYCODE_BUTTON_4,
		KeyEvent.KEYCODE_BUTTON_5,
		KeyEvent.KEYCODE_BUTTON_6,
		KeyEvent.KEYCODE_BUTTON_7,
		KeyEvent.KEYCODE_BUTTON_8,
		KeyEvent.KEYCODE_BUTTON_9,
		KeyEvent.KEYCODE_BUTTON_10,
		KeyEvent.KEYCODE_BUTTON_11,
		KeyEvent.KEYCODE_BUTTON_12,
		KeyEvent.KEYCODE_BUTTON_13,
		KeyEvent.KEYCODE_BUTTON_14,
		KeyEvent.KEYCODE_BUTTON_15,
		KeyEvent.KEYCODE_BUTTON_16,
		KeyEvent.KEYCODE_A,
		KeyEvent.KEYCODE_B,
		KeyEvent.KEYCODE_C,
		KeyEvent.KEYCODE_D,
		KeyEvent.KEYCODE_E,
		KeyEvent.KEYCODE_F,
		KeyEvent.KEYCODE_G,
		KeyEvent.KEYCODE_H,
		KeyEvent.KEYCODE_I,
		KeyEvent.KEYCODE_J,
		KeyEvent.KEYCODE_K,
		KeyEvent.KEYCODE_L,
		KeyEvent.KEYCODE_M,
		KeyEvent.KEYCODE_N,
		KeyEvent.KEYCODE_O,
		KeyEvent.KEYCODE_P,
		KeyEvent.KEYCODE_Q,
		KeyEvent.KEYCODE_R,
		KeyEvent.KEYCODE_S,
		KeyEvent.KEYCODE_T,
		KeyEvent.KEYCODE_U,
		KeyEvent.KEYCODE_V,
		KeyEvent.KEYCODE_W,
		KeyEvent.KEYCODE_X,
		KeyEvent.KEYCODE_Y,
		KeyEvent.KEYCODE_Z,
	};
	
	public static Map<String, String> keyMapCache = new HashMap<String, String>();
	public static Map<String, Integer> intKeyMapCache = new HashMap<String, Integer>();
	public static List<Map<String, Object>> listCache = new ArrayList<Map<String, Object>> ();
	public static String currentConfigurationXML = "null";
	public static List<Profile> keyList;
	public static int joystickAreaCheckKey = KeyEvent.KEYCODE_VOLUME_UP; //Joystick����ȷ�ϰ���
	public static List<KeyConfiguration> listKeyConfiguration = new ArrayList<KeyConfiguration>();
	/**
	 * Game configuration file state
	 */
	public static String gameConfigStateXML = "game_config_state";
	
}
