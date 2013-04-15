package com.only.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewKeyConfiguration extends Activity implements OnClickListener {
	private static final String TAG = "ViewKeyConfiguration";
	
	private View view = null;
	private LinearLayout lyContent = null;
	private LayoutInflater inflater = null;
	private TextView keyNameTv = null;
	private TextView keyMapValueTv = null;
	private TextView packageLabelTv = null;
	private Button btnSave;
	
	private Activity thiz;
	private boolean configurationSaved = true;
	private SharedPreferences sp;
	private Editor editor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.thiz = this;
		setContentView(R.layout.activity_key_view);
		lyContent = (LinearLayout) this.findViewById(R.id.key_config_ly);
		packageLabelTv = (TextView) this.findViewById(R.id.tv_package_label);
		btnSave = (Button) this.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(this);
		this.inflater = this.getLayoutInflater();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			packageLabelTv.setText(bundle.getString("package_label"));
		}
		addView();
		sp = this.getSharedPreferences("key_map", Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public void addView() {
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
		configurationSaved = false;
		if (arg0.equals(btnSave)) {
			saveConfiguration();
		}
	}
	
	public void saveConfiguration() {
		configurationSaved = true;
		for (int i = 0; i < lyContent.getChildCount(); i ++) {
			keyNameTv = (TextView) lyContent.getChildAt(i).findViewById(R.id.key_name_tv);
			keyMapValueTv = (TextView) lyContent.getChildAt(i).findViewById(R.id.map_key_name_tv);
			editor.putString(keyNameTv.getText().toString(), keyMapValueTv.getText().toString());
		}
		editor.commit();
		Toast.makeText(this, R.string.configuration_saved, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !configurationSaved) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setMessage(R.string.yet_not_save_config);
			b.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					thiz.finish();
				}
			});
			b.setNegativeButton(R.string.btn_save, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					saveConfiguration();
					thiz.finish();
				}
			});
			b.show();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}
