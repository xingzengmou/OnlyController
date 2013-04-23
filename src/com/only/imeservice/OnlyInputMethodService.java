package com.only.imeservice;

import com.only.controller.data.GlobalData;
import com.only.net.socket.netSocket;
import com.only.touch.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class OnlyInputMethodService extends InputMethodService {
	private static final String TAG = "BlueOceanInputMethodService";
	private Context mContext = null;
	private boolean debug = true;
	private long perDealZoomTime = 0;

	@Override public void onCreate() {
		Log.e(TAG, "oncreate");
		super.onCreate();
	}

	@Override public void onInitializeInterface() {
		Log.e(TAG, "onInitializeInterface");
		super.onInitializeInterface();
	}

	@Override public View onCreateInputView() {
		Log.e(TAG, "onCreateInputView");
		return super.onCreateInputView();
	}

	@Override public View onCreateCandidatesView() {
		Log.e(TAG, "onCreateCandidatesView");
		return super.onCreateCandidatesView();
	}

	@Override public void onStartInput(EditorInfo attribute, boolean restarting) {
		Log.e(TAG, "onStartInput");
		super.onStartInput(attribute, restarting);
	}

	@Override public void onFinishInput() {
		Log.e(TAG, "onFinishInput");
		super.onFinishInput();
	}

	@Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
		Log.e(TAG, "onStartInputView");
		super.onStartInputView(attribute, restarting);
	}

	@Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd,
			int candidatesStart, int candidatesEnd) {
		Log.e(TAG, "onUpdateSelection");
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
	}

	@Override public void onDisplayCompletions(CompletionInfo[] completions) {
		Log.e(TAG, "onDisplayCompletions");
		super.onDisplayCompletions(completions);
	}
	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyIsMapped(keyCode)) return true;
		if (keyIsMappedTouch(keyCode)) return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyIsMapped(keyCode)) return true;
		if (keyIsMappedTouch(keyCode)) return true;
		return super.onKeyUp(keyCode, event);
	}

	private boolean keyIsMapped(int keyCode) {
		for (int i = 0; i < GlobalData.keyAppValue.length; i ++) {
			int mapCode = GlobalData.intKeyMapCache.get(GlobalData.keyAppName[i] + "_INT");
			Log.e(TAG, "sendMapKeyDown keyCode = " + keyCode + " mapCode = " + mapCode);
			if (mapCode == keyCode) {
				int eventCode = GlobalData.keyAppValue[i];
				Log.e(TAG, "found key = " + GlobalData.keyAppName[i] + " keycode = " + GlobalData.keyAppValue[i]);
				return true;
			} 
		}
		return false;
	}
	
	private boolean keyIsMappedTouch(int keyCode) {
		if (GlobalData.keyList == null) return false;
		for (Profile profile : GlobalData.keyList) {
			if (profile.key == keyCode) {
				Log.e(TAG, "found touch map key = " + profile.key + " x = " + profile.posX + " y = " + profile.posY);
				return true;
			}
		}
		return false;
	}
}
