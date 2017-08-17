package com.example.lenovo.uu.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.config.BmobConstants;
import com.example.lenovo.uu.util.CommonUtils;
import com.example.lenovo.uu.view.dialog.DialogTips;

import static java.security.AccessController.getContext;

/**
 * @ClassName: LoginActivity
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	int i = 0;
	static final int wrapper[] = {R.drawable.background1, R.drawable.background2, R.drawable.background3
			, R.drawable.background4, R.drawable.background5};
	RelativeLayout login_layout;
	ImageView change;
	EditText et_username, et_password;
	Button btn_login;
	TextView btn_register, btn_login_by_phone, btn_register_no_phone;
	BmobChatUser currentUser;

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		//注册退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
	}
	
	private void init() {
		login_layout = (RelativeLayout)findViewById(R.id.login_layout);
		change = (ImageView) findViewById(R.id.iv_icon);
		change.setOnClickListener(this);
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login_by_phone = (TextView) findViewById(R.id.btn_login_by_phone);
		btn_register = (TextView) findViewById(R.id.btn_register);
		btn_register_no_phone = (TextView)findViewById(R.id.btn_register_no_phone);
		btn_register_no_phone.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_login_by_phone.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && BmobConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
				finish();
			}
		}

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_icon:
				login_layout.setBackground(LoginActivity.this.getResources()
						.getDrawable(wrapper[i]));
				i = (i + 1) % 5;
				break;
			case R.id.btn_register:
				Intent intent = new Intent(LoginActivity.this,	RegisterInPhone.class);
				intent.putExtra("from", "register");
				startActivity(intent);
				break;
			case R.id.btn_login:
				boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
				if(!isNetConnected){
					ShowToast(R.string.network_tips);
					return;
				}
				login();
				break;
			case R.id.btn_login_by_phone:
				Intent intent2 = new Intent(LoginActivity.this, RegisterInPhone.class);
				intent2.putExtra("from", "login");
				startActivity(intent2);
				break;
			case R.id.btn_register_no_phone:
				Intent intent3 = new Intent(LoginActivity.this, RegisterActivity.class);
				intent3.putExtra("from", "no_phone");
				startActivity(intent3);
				break;
			default:
				break;
		}
	}
	
	private void login(){
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();

		if (TextUtils.isEmpty(name)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}

		final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.login(name, password, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						progress.setMessage("正在获取好友列表...");
					}
				});
				//更新用户的地理位置以及好友的资料
				updateUserInfos();
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				// TODO Auto-generated method stub
				progress.dismiss();
				BmobLog.i(arg0);
				ShowToast(arg0);
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
}
