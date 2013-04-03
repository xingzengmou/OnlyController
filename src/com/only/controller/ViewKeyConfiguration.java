package com.only.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewKeyConfiguration implements OnClickListener {
	private static final String TAG = "ViewKeyConfiguration";
	
	private View view = null;
	private LinearLayout lyContent = null;
	private LayoutInflater inflater = null;
	private TextView keyNameTv = null;
	
	public ViewKeyConfiguration(View v, LayoutInflater inflater) {
		lyContent = (LinearLayout) v;
		this.inflater = inflater;
		addChildView("DPAD_CENTER");
		addChildView("DPAD_LEFT");
		addChildView("DPAD_RIGHT");
		addChildView("DPAD_DOWN");
		addChildView("DPAD_UP");
		addChildView("BUTTON_A");
		addChildView("BUTTON_B");
		addChildView("BUTTON_C");
		addChildView("BUTTON_X");
		addChildView("BUTTON_Y");
		addChildView("BUTTON_Z");
		addChildView("BUTTON_L1");
		addChildView("BUTTON_R1");
		addChildView("BUTTON_L2");
		addChildView("BUTTON_R2");
		addChildView("BUTTON_THUMBL");
		addChildView("BUTTON_THUMBR");
		addChildView("BUTTON_START");
		addChildView("BUTTON_SELECT");
		addChildView("BUTTON_MODE");
		addChildView("BUTTON_1");
		addChildView("BUTTON_2");
		addChildView("BUTTON_3");
		addChildView("BUTTON_4");
		addChildView("BUTTON_5");
		addChildView("BUTTON_6");
		addChildView("BUTTON_7");
		addChildView("BUTTON_8");
		addChildView("BUTTON_9");
		addChildView("BUTTON_10");
		addChildView("BUTTON_11");
		addChildView("BUTTON_12");
		addChildView("BUTTON_13");
		addChildView("BUTTON_14");
		addChildView("BUTTON_15");
		addChildView("BUTTON_16");
		addChildView("A");
		addChildView("B");
		addChildView("C");
		addChildView("D");
		addChildView("E");
		addChildView("F");
		addChildView("G");
		addChildView("H");
		addChildView("I");
		addChildView("J");
		addChildView("K");
		addChildView("L");
		addChildView("M");
		addChildView("N");
		addChildView("O");
		addChildView("P");
		addChildView("Q");
		addChildView("R");
		addChildView("S");
		addChildView("T");
		addChildView("U");
		addChildView("V");
		addChildView("W");
		addChildView("X");
		addChildView("Y");
		addChildView("Z");
	}
	/**
	 *  show key configuration view
	 */
	public void show() {
		lyContent.setVisibility(View.VISIBLE);
	}
	/**
	 * hide key configuration view
	 */
	public void hide() {
		lyContent.setVisibility(View.GONE);
	}
	
	public void addChildView(String keyName) {
		view = inflater.inflate(R.layout.view_key_configuration, null);
		keyNameTv = (TextView) view.findViewById(R.id.key_name_tv);
		keyNameTv.setText(keyName);
		view.setOnClickListener(this);
		lyContent.addView(view);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
