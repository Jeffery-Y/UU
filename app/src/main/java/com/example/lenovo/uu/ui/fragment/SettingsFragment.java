package com.example.lenovo.uu.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

import com.example.lenovo.uu.CustomApplcation;
import com.example.lenovo.uu.R;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.ui.BlackListActivity;
import com.example.lenovo.uu.ui.FragmentBase;
import com.example.lenovo.uu.ui.LoginActivity;
import com.example.lenovo.uu.ui.RegisterActivity;
import com.example.lenovo.uu.ui.SetMyInfoActivity;
import com.example.lenovo.uu.util.SharePreferenceUtil;
import com.suke.widget.SwitchButton;

/**
 * 设置
 */
@SuppressLint("SimpleDateFormat")
public class SettingsFragment extends FragmentBase implements OnClickListener{

	Button btn_logout;
	TextView tv_set_name;
	RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
			rl_switch_vibrate,layout_blacklist;

	SwitchButton iv_notification, iv_voice, iv_vibrate;

	/*ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;*/
	
	View view1,view2;
	SharePreferenceUtil mSharedUtil;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedUtil = mApplication.getSpUtil();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_set, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	private void initView() {
		initTopBarForOnlyTitle("设置");
		//黑名单列表
		layout_blacklist = (RelativeLayout) findViewById(R.id.layout_blacklist);
		
		layout_info = (RelativeLayout) findViewById(R.id.layout_info);
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		iv_notification = (SwitchButton)findViewById(R.id.iv_notification);
		iv_voice = (SwitchButton)findViewById(R.id.iv_voice);
		iv_vibrate = (SwitchButton)findViewById(R.id.iv_vibrate);

		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);

		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		btn_logout = (Button) findViewById(R.id.btn_logout);

		// 初始化
		final boolean isAllowNotify = mSharedUtil.isAllowPushNotify();
		iv_notification.setChecked(isAllowNotify);

		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		iv_voice.setChecked(isAllowVoice);

		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		iv_vibrate.setChecked(isAllowVibrate);

		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
		layout_blacklist.setOnClickListener(this);

		iv_notification.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(SwitchButton view, boolean isChecked) {
				if(isChecked == false){
					mSharedUtil.setPushNotifyEnable(false);
					rl_switch_vibrate.setVisibility(View.GONE);
					rl_switch_voice.setVisibility(View.GONE);
					view1.setVisibility(View.GONE);
					view2.setVisibility(View.GONE);
				} else {
					mSharedUtil.setPushNotifyEnable(true);
					rl_switch_vibrate.setVisibility(View.VISIBLE);
					rl_switch_voice.setVisibility(View.VISIBLE);
					view1.setVisibility(View.VISIBLE);
					view2.setVisibility(View.VISIBLE);
				}
			}
		});
		iv_voice.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(SwitchButton view, boolean isChecked) {
				if (isChecked == false) {
					mSharedUtil.setAllowVoiceEnable(false);
				} else {
					mSharedUtil.setAllowVoiceEnable(true);
				}
			}
		});
		iv_vibrate.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(SwitchButton view, boolean isChecked) {
				if (isChecked == false) {
					mSharedUtil.setAllowVibrateEnable(false);
				} else {
					mSharedUtil.setAllowVibrateEnable(true);
				}
			}
		});
	}

	private void initData() {
		tv_set_name.setText(BmobUserManager.getInstance(getActivity()).getCurrentUser().getUsername());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_blacklist:// 启动到黑名单页面
			startAnimActivity(new Intent(getActivity(),BlackListActivity.class));
			break;
		case R.id.layout_info:// 启动到个人资料页面
			Intent intent =new Intent(getActivity(),SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startActivity(intent);
			break;
		case R.id.btn_logout:
			CustomApplcation.getInstance().logout();
			getActivity().finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.rl_switch_notification:
			if(iv_notification.isChecked() == true){
				iv_notification.setChecked(false);
				mSharedUtil.setPushNotifyEnable(false);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			} else {
				iv_notification.setChecked(true);
				mSharedUtil.setPushNotifyEnable(true);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.rl_switch_voice:
			if (iv_voice.isChecked() == true) {
				iv_voice.setChecked(false);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_voice.setChecked(true);
				mSharedUtil.setAllowVoiceEnable(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_vibrate.isChecked() == true) {
				iv_vibrate.setChecked(false);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				iv_vibrate.setChecked(true);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;

			default:
				break;
		}
	}

}
