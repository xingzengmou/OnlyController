package com.only.controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
	private static final int MSG_CONNECT_INPUT_JAR_FAILED = 0x01;
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
	private ViewKeyConfiguration mViewKeyConfiguration = null;
	private ViewSettings mViewSettings;
	
	/**
	 * apps Dialog
	 */
	private Dialog appsDialog;
	private ListView appListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		thiz = this;
		setContentView(R.layout.activity_only_main);
		getViewHandles();
		setViewListener();
		newView();
		appsDialog = new Dialog(this);
		appsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		appsDialog.setContentView(R.layout.view_app_list);
		appListView = (ListView) appsDialog.findViewById(R.id.lv_apps);
		appListView.setOnItemClickListener(appsListViewOnItemClickListener);
		
		if (Root.root()) {
			Toast.makeText(this, R.string.root_successful, Toast.LENGTH_SHORT).show();
			if (InputJar.run(this)) {
				InputAdapter.init();
				InputAdapter.openEvent();
				InputAdapter.start();
			}
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!netSocket.connectService()) {
						mHandler.sendEmptyMessage(MSG_CONNECT_INPUT_JAR_FAILED);
					} else {
							netSocket.send("injectpointer:3:3222.44422:44232.55555:44.000:666.000:333.21233:443.000");
					}
				}
				
			}).start();
		} else {
			//Toast.makeText(this, R.string.root_failed, Toast.LENGTH_SHORT).show();
			AlertDialog.Builder b = new AlertDialog.Builder(thiz);
			b.setMessage(R.string.root_failed);
			b.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					thiz.finish();
				}
			});
			b.show();
		}
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
		mViewKeyConfiguration = new ViewKeyConfiguration(lyKeyConfig, this.getLayoutInflater());
		mViewSettings = new ViewSettings(lySettings, this.getLayoutInflater());
		mViewGameConfiguration.show();
		mViewKeyConfiguration.hide();
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
					mViewKeyConfiguration.hide();
					mViewSettings.hide();
					mViewGameConfiguration.show();
				}
			} else if (v.equals(btnKeyConfig)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, true);
				switchBackground(btnSettings, false);
				if (mViewKeyConfiguration != null) {
					mViewGameConfiguration.hide();
					mViewKeyConfiguration.show();
					mViewSettings.hide();
				}
			} else if (v.equals(btnSettings)) {
				switchBackground(btnGameConfigFiles, false);
				switchBackground(btnKeyConfig, false);
				switchBackground(btnSettings, true);
				if (mViewSettings != null) {
					mViewGameConfiguration.hide();
					mViewKeyConfiguration.hide();
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
				b.show();
				break;
			}
		}
	};
	
	private void loadAllApps() {
		PackageManager pm = (PackageManager) this.getPackageManager();
		List<PackageInfo> listPI = pm.getInstalledPackages(0);
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		for (PackageInfo pi : listPI) {
			if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) continue;
			Map<String, Object> map = new HashMap<String, Object>();
			Drawable icon = pi.applicationInfo.loadIcon(pm);
			String label = pi.applicationInfo.loadLabel(pm).toString();
			String packageName = pi.applicationInfo.packageName;
			map.put("icon", icon);
			map.put("label", label);
			map.put("packageName", packageName);
			listData.add(map);
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
			mViewGameConfiguration.addGameView(map);
		}
	};
	
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
				ViewHandle mViewHandle;
				mViewHandle = new ViewHandle();
				mViewHandle.ivIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
				mViewHandle.tvLabel = (TextView) view.findViewById(R.id.tv_app_name);
				mViewHandle.ivIcon.setBackgroundDrawable((Drawable) list.get(arg0).get("icon"));
				mViewHandle.tvLabel.setText(list.get(arg0).get("label").toString());
				view.setTag(list.get(arg0));
			}
			
			return view;
		}
		
		class ViewHandle {
			ImageView ivIcon;
			TextView tvLabel;
		}
		
	}
}
 