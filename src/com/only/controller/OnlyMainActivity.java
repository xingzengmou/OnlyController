package com.only.controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.only.config.GameConfigurationState;
import com.only.controller.data.AppsDatabase;
import com.only.controller.data.GlobalData;
import com.only.core.EventService;
import com.only.inputjar.InputJar;
import com.only.jni.InputAdapter;
import com.only.net.socket.netSocket;
import com.only.root.Root;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OnlyMainActivity extends Activity {
	private static final String TAG = "OnlyMainActivity";
	public static final int MSG_CONNECT_INPUT_JAR_FAILED = 0x01;
	public static final int MSG_ROOT_FAILED = 0x02;
	public static final int MSG_ROOTED = 0x03;
	public static final int MSG_INPUTJAR_CONNECTED = 0x04;
	public static final int MSG_SDCARD_UNMOUNTED = 0X05;
	public static final int MSG_MKDIR_INPUTJAR_ERROR = 0X06;

	/**
	 * Buttons define
	 */
	private Button btnGameConfigFiles;
	private Button btnKeyConfig;
	private Button btnSettings;
	private Button btnAddGame;
	/**
	 * LinearLayout define
	 */
	private LinearLayout lyGameConfigFiles;
	private LinearLayout lyKeyConfig;
	private LinearLayout lySettings;
	private LinearLayout lyTitle;
	
	private Activity thiz;
	/**
	 * View for configuration
	 */
	private ViewGameConfiguration mViewGameConfiguration = null;
//	private ViewKeyConfiguration mViewKeyConfiguration = null;
	private ViewSettings mViewSettings;
	
	/**
	 * apps Dialog
	 */
	private Dialog appsDialog;
	private AlertDialog waitTipDialog;
	private ListView appListView;
	
	private AppsDatabase mAppDatabase = null;
	private GameConfigurationState mGameConfigurationState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		mGameConfigurationState = new GameConfigurationState(this);
		thiz = this;
		setContentView(R.layout.activity_only_main);
		getViewHandles();
		setViewListener();
		mAppDatabase = new AppsDatabase(this, "appsdatabase", null, 1);
		loadDatabaseToListCache();
		appsDialog = new Dialog(this);
		appsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		appsDialog.setContentView(R.layout.view_app_list);
		appListView = (ListView) appsDialog.findViewById(R.id.lv_apps);
		appListView.setOnItemClickListener(appsListViewOnItemClickListener);
		newView();
		loadKeyMapConfigurationToCache();
		InputJar.setHandler(mHandler);
		EventService.setActivity(this);
		Intent intent = new Intent(this, EventService.class);
		this.startService(intent);
	}

	private void getViewHandles() {
		btnGameConfigFiles = (Button) findViewById(R.id.game_list_btn);
		btnKeyConfig = (Button) findViewById(R.id.key_config_btn);
		btnSettings = (Button) findViewById(R.id.settings_tv);
		lyGameConfigFiles = (LinearLayout) findViewById(R.id.game_config_files_ly);
		lyKeyConfig = (LinearLayout) findViewById(R.id.key_config_ly);
		lySettings = (LinearLayout) findViewById(R.id.settings_ly);
		lyTitle = (LinearLayout) findViewById(R.id.title_ly);
		btnAddGame = (Button) findViewById(R.id.btn_add_game);
	}
	
	private void setViewListener() {
		btnGameConfigFiles.setOnClickListener(btnOnClicks);
		btnKeyConfig.setOnClickListener(btnOnClicks);
		btnSettings.setOnClickListener(btnOnClicks);
		btnAddGame.setOnClickListener(btnOnClicks);
	}
	
	private void newView() {
		mViewGameConfiguration = new ViewGameConfiguration(lyGameConfigFiles, this.getLayoutInflater());
//		mViewKeyConfiguration = new ViewKeyConfiguration(lyKeyConfig, this.getLayoutInflater());
		mViewSettings = new ViewSettings(lySettings, this.getLayoutInflater());
		mViewGameConfiguration.loadGameViewFromDatabase(GlobalData.listCache, this.getPackageManager());
		mViewGameConfiguration.show();
//		mViewKeyConfiguration.hide();
		mViewSettings.hide();
	}
	
	private View.OnClickListener btnOnClicks = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			btnAddGame.setVisibility(View.GONE);
			if (v.equals(btnGameConfigFiles)) {
				switchBackground(btnGameConfigFiles, true);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnSettings, false);
				if (mViewGameConfiguration != null) {
//					mViewKeyConfiguration.hide();
					mViewSettings.hide();
					mViewGameConfiguration.show();
				}
			} else if (v.equals(btnKeyConfig)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, true);
				switchBackground(btnSettings, false);
//				if (mViewKeyConfiguration != null) {
//					mViewGameConfiguration.hide();
//					mViewKeyConfiguration.show();
//					mViewSettings.hide();
//				}
			} else if (v.equals(btnSettings)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnSettings, true);
				if (mViewSettings != null) {
					mViewGameConfiguration.hide();
//					mViewKeyConfiguration.hide();
					mViewSettings.show();
				}
			} else if (v.equals(btnAddGame)) {
				loadAllApps();
				btnAddGame.setVisibility(View.VISIBLE);
			}
		}
	};
	
	private void switchBackground(View v, boolean d) {
		v.setBackgroundResource(d ? R.drawable.btn_d : R.drawable.btn_n);
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (waitTipDialog != null) waitTipDialog.cancel();
			switch (msg.what) {
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
				b.show();
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
				b1.show();
				break;
			case MSG_ROOTED:
				Toast.makeText(thiz, R.string.root_successful, Toast.LENGTH_SHORT).show();
				break;
			case MSG_INPUTJAR_CONNECTED:
//				ControllerCore.start(thiz);
				Log.e(TAG, "controllercore.start");
				break;
			case MSG_SDCARD_UNMOUNTED:
				AlertDialog.Builder sdErrorDialog = new AlertDialog.Builder(thiz);
				sdErrorDialog.setMessage(R.string.external_starage_has_unmounted);
				sdErrorDialog.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						thiz.finish();
					}
				});
				sdErrorDialog.setCancelable(false);
				sdErrorDialog.show();;
				break;
			case MSG_MKDIR_INPUTJAR_ERROR:
				AlertDialog.Builder dirErrorDialog = new AlertDialog.Builder(thiz);
				dirErrorDialog.setMessage(R.string.make_sdcard_input_jar_dir_error);
				dirErrorDialog.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						thiz.finish();
					}
				});
				dirErrorDialog.setCancelable(false);
				dirErrorDialog.show();
				break;
			}
		}
	};
	
	private void loadAllApps() {
		PackageManager pm = (PackageManager) this.getPackageManager();
		List<PackageInfo> listPI = pm.getInstalledPackages(0);
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		for (PackageInfo pi : listPI) {
			if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
				Map<String, Object> map = new HashMap<String, Object>();
				Drawable icon = pi.applicationInfo.loadIcon(pm);
				String label = pi.applicationInfo.loadLabel(pm).toString();
				String packageName = pi.applicationInfo.packageName;
				map.put("icon", icon);
				map.put("label", label);
				map.put("packageName", packageName);
				map.put("keyFileState", "true");
				map.put("touchFileState", "true");
				listData.add(map);  
				Log.e(TAG, "loadallapps packagename = " + packageName);
			}
		}
		AppsAdapter adapter = new AppsAdapter(listData);
		appListView.setAdapter(adapter);
		appsDialog.show();
	}
	
	private AdapterView.OnItemClickListener appsListViewOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Map<String, Object> map = (Map<String, Object>) arg1.getTag();
			for (Map<String, Object> tmap : GlobalData.listCache) {
				if (tmap.get("packageName").toString().equals(map.get("packageName").toString())) {
					Toast.makeText(thiz, R.string.item_is_exited, Toast.LENGTH_LONG).show();
					return;
				}
			}
			mGameConfigurationState.setKeyConfigurationState(map.get("packageName").toString(), map.get("keyFileState").toString());
			mGameConfigurationState.setKeyConfigurationState(map.get("packageName").toString(), map.get("touchFileState").toString());
			mViewGameConfiguration.addGameView(map);
			GlobalData.listCache.add(map);
			insertDatabase(map);
		}
	};
	
	private void insertDatabase(Map<String, Object> map) {
		ContentValues cv = new ContentValues();
		cv.put("package_name", map.get("packageName").toString());
		mAppDatabase.getWritableDatabase().insert("app_list", null, cv);
	}
	
	private void loadDatabaseToListCache() {
		Cursor cursor = mAppDatabase.getReadableDatabase().rawQuery("select * from app_list", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String packageName = cursor.getString(0);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("packageName", packageName);
				Log.e(TAG, "database packagename = " + packageName);
				GlobalData.listCache.add(map);
			}
			cursor.close();
			cursor = null;
		}
	}
	
	class AppsAdapter extends BaseAdapter {
		private List<Map<String, Object>> list;
		
		public AppsAdapter(Object data) {
			this.list = (List<Map<String, Object>>) data;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View view = arg1;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) arg2.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (View) inflater.inflate(R.layout.view_app_list_content, null);
			}
			ViewHandle mViewHandle;
			mViewHandle = new ViewHandle();
			mViewHandle.ivIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
			mViewHandle.tvLabel = (TextView) view.findViewById(R.id.tv_app_name);
			mViewHandle.ivIcon.setBackgroundDrawable((Drawable) list.get(arg0).get("icon"));
			mViewHandle.tvLabel.setText(list.get(arg0).get("label").toString());
			view.setTag(list.get(arg0));
			
			return view;
		}
		
		class ViewHandle {
			ImageView ivIcon;
			TextView tvLabel;
		}
		
	}

	private void loadKeyMapConfigurationToCache() {
		for (int i = 0; i < GlobalData.keyAppName.length; i ++) {
			GlobalData.keyMapCache.put(GlobalData.keyAppName[i], this.getString(R.string.unknown));
			GlobalData.intKeyMapCache.put(GlobalData.keyAppName[i] + "_INT", 0);
		}
	}
}
 