package com.example.lenovo.uu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.config.BmobConstants;
import com.example.lenovo.uu.config.Config;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.BmobSmsState;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.QuerySMSStateListener;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.SMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;

public class RegisterInPhone extends BaseActivity implements OnClickListener{

	private RegisterInPhone.MyBroadcastReceiver receiver = new RegisterInPhone.MyBroadcastReceiver();

	LinearLayout register_messge, register_message_hint;
	TextView input_phone;
	EditText et_country, et_phone, et_ver_code;
	Button btn_register_next, btn_send_ver_message, registr_in_back;

//	Context context;
	String from = "";
	String phoneNumber = "";
	int smsId = 123;
	RegisterInPhone.MyCountTimer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_inphone);

//		context = getApplicationContext();
//		Bmob.initialize(this, Config.applicationId);
		BmobSMS.initialize(RegisterInPhone.this, Config.applicationId, new MySMSCodeListener());
//		initTopBarForLeft("注册");
		initViews();
		from = getIntent().getStringExtra("from");//login and register
		if(from.equals("login")){
			btn_register_next.setText("验证登录");
		} else if(from.equals("register")){
			btn_register_next.setText("注册账号");
		} else if(from.equals("reset_password")){
			btn_register_next.setText("修改密码");
		}
		//注册退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
	}

	private void initViews() {
		register_messge = (LinearLayout)findViewById(R.id.register_messge);
		register_message_hint = (LinearLayout)findViewById(R.id.register_message_hint);
		input_phone = (TextView)findViewById(R.id.input_phone);
		et_country = (EditText) findViewById(R.id.et_country);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phone.requestFocus();
		et_ver_code = (EditText) findViewById(R.id.et_ver_code);
		btn_register_next = (Button) findViewById(R.id.btn_register_next);
		btn_send_ver_message = (Button) findViewById(R.id.btn_send_ver_message);
		registr_in_back = (Button) findViewById(R.id.registr_in_back);
		btn_register_next.setOnClickListener(this);
		btn_send_ver_message.setOnClickListener(this);
		btn_send_ver_message.setEnabled(false);
		registr_in_back.setOnClickListener(this);

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (et_phone.getText().toString().length() == 11) {
					btn_send_ver_message.setEnabled(true);
					btn_send_ver_message.setBackground(RegisterInPhone.this.getResources().getDrawable(R.drawable.btn_login_selector));
				} else {
					btn_send_ver_message.setEnabled(false);
					btn_send_ver_message.setBackground(RegisterInPhone.this.getResources().getDrawable(R.drawable.register_btn_shape));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.registr_in_back:
				finish();
				break;
			case R.id.btn_send_ver_message:
				timer = new RegisterInPhone.MyCountTimer(60000, 1000);
				timer.start();
				et_ver_code.requestFocus();
				btn_send_ver_message.setEnabled(false);
				phoneNumber = et_phone.getText().toString();
				if(phoneNumber.length() == 11){
					BmobSMS.requestSMSCode(this, phoneNumber, "UU", new RequestSMSCodeListener() {

						@Override
						public void done(Integer smsId, BmobException ex) {
							if(ex == null){
								toast("send ok " + smsId);
								RegisterInPhone.this.smsId = smsId;
//								et_smsid.setText(smsId+"");
								Log.e("demo", ""+smsId);
								register_messge.setVisibility(View.VISIBLE);
								register_message_hint.setVisibility(View.VISIBLE);
								input_phone.setText(phoneNumber);

								et_ver_code.addTextChangedListener(new TextWatcher() {

									@Override
									public void onTextChanged(CharSequence s, int start, int before, int count) {
										// TODO Auto-generated method stub
										if (!et_phone.getText().toString().isEmpty()) {
											btn_register_next.setEnabled(true);
											btn_register_next.setBackground(RegisterInPhone.this.getResources().getDrawable(R.drawable.btn_login_selector));
										} else {
											btn_register_next.setEnabled(false);
											btn_register_next.setBackground(RegisterInPhone.this.getResources().getDrawable(R.drawable.register_btn_shape));
										}
									}

									@Override
									public void beforeTextChanged(CharSequence s, int start, int count, int after) {
										// TODO Auto-generated method stub

									}

									@Override
									public void afterTextChanged(Editable s) {
										// TODO Auto-generated method stub
									}
								});
							}else {
								toast(ex.toString());
								btn_send_ver_message.setEnabled(true);
							}
						}

					});
				} else {
					toast("手机号码错误！");
					btn_send_ver_message.setEnabled(true);
				}
				break;
			case R.id.btn_register_next:
				final ProgressDialog progress = new ProgressDialog(RegisterInPhone.this);
				progress.setMessage("正在验证短信验证码...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				if(from.equals("login")){
					BmobUser.signOrLoginByMobilePhone(RegisterInPhone.this,phoneNumber,
							et_ver_code.getText().toString().trim(), new LogInListener<User>() {

								@Override
								public void done(User user, cn.bmob.v3.exception.BmobException ex) {
									// TODO Auto-generated method stub
									if(ex==null){
										toast("登录成功");
										//发广播通知登陆页面退出
										sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
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
										Intent intent = new Intent(RegisterInPhone.this,MainActivity.class);
										startActivity(intent);
										finish();
									}else{
										toast("登录失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage());
									}
								}
							});
				} else if(from.equals("register")){
					BmobSMS.verifySmsCode(this, phoneNumber, et_ver_code.getText().toString().trim(), new VerifySMSCodeListener() {

						@Override
						public void done(BmobException ex) {
							progress.dismiss();
							if(ex == null){
								toast("手机验证通过");
								Intent intent = new Intent(RegisterInPhone.this,RegisterActivity.class);
								intent.putExtra("from", "phone");
								intent.putExtra("phonenumber", phoneNumber);
								startActivity(intent);
							}else {
								toast(ex.toString());
							}
						}
					});
				} else if(from.equals("reset_password")){
					BmobSMS.verifySmsCode(this, phoneNumber, et_ver_code.getText().toString().trim(), new VerifySMSCodeListener() {

						@Override
						public void done(BmobException ex) {
							progress.dismiss();
							if(ex == null){
								toast("手机验证通过");
								Intent intent = new Intent(RegisterInPhone.this,ResetPassword.class);
								intent.putExtra("from", "verified_phone");
								startActivity(intent);
							}else {
								toast(ex.toString());
							}
						}
					});
				}
				break;
			default:
				break;
		}
	}

	class MyCountTimer extends CountDownTimer {

		public MyCountTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			btn_send_ver_message.setEnabled(false);
		}
		@Override
		public void onTick(long millisUntilFinished) {
			btn_send_ver_message.setText((millisUntilFinished / 1000) +"秒后重发");
		}
		@Override
		public void onFinish() {
			btn_send_ver_message.setText("发送验证码");
			btn_send_ver_message.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer!=null){
			timer.cancel();
		}
	}

	public void toast(String string){
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}

	class MySMSCodeListener implements SMSCodeListener {

		@Override
		public void onReceive(String content) {
				if(et_ver_code != null)et_ver_code.setText(content);
		}

	}


	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && BmobConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
				finish();
			}
		}

	}

}
