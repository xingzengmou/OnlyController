package com.only.controller;

import com.only.controller.data.GlobalData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
	private View clickView;
	private InputKeyDialog requestInputKeyDialog;
	private String packageNameXML;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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
			packageNameXML = bundle.getString("packageName");
		}
		addView();
		sp = this.getSharedPreferences(packageNameXML, Context.MODE_PRIVATE);
		editor = sp.edit();
		requestInputKeyDialog = new InputKeyDialog(this);
		requestInputKeyDialog.setMessage(getString(R.string.please_input_key));
		requestInputKeyDialog.setButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				keyNameTv = (TextView) clickView.findViewById(R.id.key_name_tv);
				keyMapValueTv = (TextView) clickView.findViewById(R.id.map_key_name_tv);
				keyMapValueTv.setText(R.string.unknown);
				GlobalData.keyMapCache.put(keyNameTv.getText().toString(), keyMapValueTv.getText().toString());
				GlobalData.intKeyMapCache.put(keyNameTv.getText().toString() + "_INT", 0);
			}
		});
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
		keyMapValueTv = (TextView) view.findViewById(R.id.map_key_name_tv);
		keyMapValueTv.setText(GlobalData.keyMapCache.get(keyNameTv.getText().toString()));
		view.setOnClickListener(this);
		lyContent.addView(view);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		configurationSaved = false;
		if (arg0.equals(btnSave)) {
			saveConfiguration();
		} else {
			clickView = arg0;
			requestInputKeyDialog.show();
		}
	}
	
	public void saveConfiguration() {
		configurationSaved = true;
		for (int i = 0; i < lyContent.getChildCount(); i ++) {
			keyNameTv = (TextView) lyContent.getChildAt(i).findViewById(R.id.key_name_tv);
			keyMapValueTv = (TextView) lyContent.getChildAt(i).findViewById(R.id.map_key_name_tv);
			editor.putString(keyNameTv.getText().toString(), keyMapValueTv.getText().toString());
			editor.putInt(keyNameTv.getText().toString() + "_INT", GlobalData.intKeyMapCache.get(keyNameTv.getText().toString() + "_INT"));
		}
		editor.commit();
		Toast.makeText(this, R.string.configuration_saved, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.e(TAG, "keyCode = " + keyCode); 
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
		return true;
	}
	
	
	class InputKeyDialog extends AlertDialog {
		
		protected InputKeyDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			keyNameTv = (TextView) clickView.findViewById(R.id.key_name_tv);
			keyMapValueTv = (TextView) clickView.findViewById(R.id.map_key_name_tv);
			String str = "";
			int keyMapCode = keyCode;
			switch (keyCode) {
			case KeyEvent.KEYCODE_0:
				str = "0";
				break;
			case KeyEvent.KEYCODE_1:
				str = "1";
				break;
			case KeyEvent.KEYCODE_2:
				str = "2";
				break;
			case KeyEvent.KEYCODE_3:
				str = "3";
				break;
			case KeyEvent.KEYCODE_4:
				str = "4";
				break;
			case KeyEvent.KEYCODE_5:
				str = "5";
				break;
			case KeyEvent.KEYCODE_6:
				str = "6";
				break;
			case KeyEvent.KEYCODE_7:
				str = "7";
				break;
			case KeyEvent.KEYCODE_8:
				str = "8";
				break;
			case KeyEvent.KEYCODE_9:
				str = "9";
				break;
			case KeyEvent.KEYCODE_A:
				str = "A";
				break;
			case KeyEvent.KEYCODE_B:
				str = "B";
				break;
			case KeyEvent.KEYCODE_C:
				str = "C";
				break;
			case KeyEvent.KEYCODE_D:
				str = "D";
				break;
			case KeyEvent.KEYCODE_E:
				str = "E";
				break;
			case KeyEvent.KEYCODE_F:
				str = "F";
				break;
			case KeyEvent.KEYCODE_G:
				str = "G";
				break;
			case KeyEvent.KEYCODE_H:
				str = "H";
				break;
			case KeyEvent.KEYCODE_I:
				str = "I";
				break;
			case KeyEvent.KEYCODE_J:
				str = "J";
				break;
			case KeyEvent.KEYCODE_K:
				str = "K";
				break;
			case KeyEvent.KEYCODE_L:
				str = "L";
				break;
			case KeyEvent.KEYCODE_N:
				str = "N";
				break;
			case KeyEvent.KEYCODE_O:
				str = "O";
				break;
			case KeyEvent.KEYCODE_P:
				str = "P";
				break;
			case KeyEvent.KEYCODE_Q:
				str = "Q";
				break;
			case KeyEvent.KEYCODE_R:
				str = "R";
				break;
			case KeyEvent.KEYCODE_S:
				str = "S";
				break;
			case KeyEvent.KEYCODE_T:
				str = "T";
				break;
			case KeyEvent.KEYCODE_U:
				str = "U";
				break;
			case KeyEvent.KEYCODE_W:
				str = "W";
				break;
			case KeyEvent.KEYCODE_Y:
				str = "Y";
				break;
			case KeyEvent.KEYCODE_X:
				str = "X";
				break;
			case KeyEvent.KEYCODE_Z:
				str = "Z";
				break;
			case KeyEvent.KEYCODE_M:
				str = "M";
				break;
			case KeyEvent.KEYCODE_V:
				str = "V";
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				str = "VOLUME_DOWN";
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				str = "VOLUME_UP";
				break;
			case KeyEvent.KEYCODE_ALT_LEFT:
				str = "ALT_LEFT";
				break;
			case KeyEvent.KEYCODE_ALT_RIGHT:
				str = "ALT_LEFT";
				break;
			case KeyEvent.KEYCODE_BACKSLASH:
				str = "BACKSLASH";
				break;
			case KeyEvent.KEYCODE_BUTTON_A:
				str = "BUTTON_A";
				break;
			case KeyEvent.KEYCODE_BUTTON_B:
				str = "BUTTON_B";
				break;
			case KeyEvent.KEYCODE_BUTTON_C:
				str = "BUTTON_C";
				break;
			case KeyEvent.KEYCODE_BUTTON_L1:
				str = "BUTTON_L1";
				break;
			case KeyEvent.KEYCODE_BUTTON_L2:
				str = "BUTTON_L2";
				break;
			case KeyEvent.KEYCODE_BUTTON_MODE:
				str = "BUTTON_MODE";
				break;
			case KeyEvent.KEYCODE_BUTTON_R1:
				str = "BUTTON_R1";
				break;
			case KeyEvent.KEYCODE_BUTTON_R2:
				str = "BUTTON_R2";
				break;
			case KeyEvent.KEYCODE_BUTTON_SELECT:
				str = "BUTTON_SELECT";
				break;
			case KeyEvent.KEYCODE_BUTTON_START:
				str = "BUTTON_START";
				break;
			case KeyEvent.KEYCODE_BUTTON_THUMBL:
				str = "BUTTON_THUMBL";
				break;
			case KeyEvent.KEYCODE_BUTTON_THUMBR:
				str = "BUTTON_THUMBR";
				break;
			case KeyEvent.KEYCODE_BUTTON_X:
				str = "BUTTON_X";
				break;
			case KeyEvent.KEYCODE_BUTTON_Y:
				str = "BUTTON_Y";
				break;
			case KeyEvent.KEYCODE_BUTTON_Z:
				str = "BUTTON_Z";
				break;
			case KeyEvent.KEYCODE_COMMA:
				str = "COMMA";
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				str = "DPAD_CENTER";
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				str = "DPAD_DOWN";
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				str = "DPAD_LEFT";
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				str = "DPAD_RIGHT";
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				str = "DPAD_UP";
				break;
			case KeyEvent.KEYCODE_ENTER:
				str = "ENTER";
				break;
			case KeyEvent.KEYCODE_EQUALS:
				str = "=";//"EQUALS";
				break;
			case KeyEvent.KEYCODE_GRAVE:
				str = "`";//"GRAVE";
				break;
			case KeyEvent.KEYCODE_LEFT_BRACKET:
				str = "[";//"LEFT_BRACKET";
				break;
			case KeyEvent.KEYCODE_RIGHT_BRACKET:
				str = "]";//"RIGHT_BRACKET";
				break;
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
				str = "MEDIA_FAST_FORWARD";
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				str = "MEDIA_NEXT";
				break;
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				str = "MEDIA_PLAY_PAUSE";
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				str = "MEDIA_PREVIOUS";
				break;
			case KeyEvent.KEYCODE_MEDIA_REWIND:
				str = "MEDIA_REWIND";
				break;
			case KeyEvent.KEYCODE_MEDIA_STOP:
				str = "MEDIA_STOP";
				break;
			case KeyEvent.KEYCODE_PAGE_DOWN:
				str = "PAGE_DOWN";
				break;
			case KeyEvent.KEYCODE_PAGE_UP:
				str = "PAGE_UP";
				break;
			case KeyEvent.KEYCODE_PERIOD:
				str = ".";//"PERIOD";
				break;
			case KeyEvent.KEYCODE_PLUS:
				str = "+";//"PLUS";
				break;
			case KeyEvent.KEYCODE_POUND:
				str = "#";//"POUND";
				break;
			case KeyEvent.KEYCODE_SEARCH:
				str = "SEARCH";
				break;
			case KeyEvent.KEYCODE_MINUS:
				str = "-";//"MINUS";
				break;
			case KeyEvent.KEYCODE_SEMICOLON:
				str = ";"; //"SEMICOLON";
				break;
			case KeyEvent.KEYCODE_SHIFT_LEFT:
				str = "SHIFT_LEFT";
				break;
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				str = "SHIFT_RIGHT";
				break;
			case KeyEvent.KEYCODE_SLASH:
				str = "SLASH";
				break;
			case KeyEvent.KEYCODE_SOFT_LEFT:
				str = "SOFT_LEFT";
				break;
			case KeyEvent.KEYCODE_SOFT_RIGHT:
				str = "SOFT_RIGHT";
				break;
			case KeyEvent.KEYCODE_SPACE:
				str = "SPACE";
				break;
			case KeyEvent.KEYCODE_STAR:
				str = "STAR";
				break;
			case KeyEvent.KEYCODE_TAB:
				str = "TAB";
				break;
			case KeyEvent.KEYCODE_UNKNOWN:
				str = "UNKNOWN";
				break;
//			case KeyEvent.KEYCODE_BACK:
//				str = "UNKNOWN";
//				break;
			default: break;
			}
			if (!str.isEmpty()) {
				keyMapValueTv.setText(str);
				GlobalData.intKeyMapCache.put(keyNameTv.getText().toString() + "_INT", keyMapCode);
				GlobalData.keyMapCache.put(keyNameTv.getText().toString(), keyMapValueTv.getText().toString());
			}
			this.cancel();
			return true;
		}
		
	}
	
}
