package com.example.lenovo.uu.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.lenovo.uu.config.Config;

import cn.bmob.newsmssdk.BmobSMS;
import cn.bmob.newsmssdk.BmobSmsState;
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.QuerySMSStateListener;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.SMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;
import cn.bmob.v3.Bmob;

public class RegisterInPhone extends BaseActivity implements OnClickListener{
	LinearLayout register_messge, register_message_hint;
	TextView input_phone;
	EditText et_country, et_phone, et_ver_code;
	Button btn_register_next, btn_send_ver_message;

//	Context context;
	String phoneNumber = "";
	int smsId = 123;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_inphone);
//		context = getApplicationContext();
//		Bmob.initialize(this, Config.applicationId);
		BmobSMS.initialize(RegisterInPhone.this, Config.applicationId, new MySMSCodeListener());
		initTopBarForLeft("注册");
		initViews();
	}
	private void initViews() {
		register_messge = (LinearLayout)findViewById(R.id.register_messge);
		register_message_hint = (LinearLayout)findViewById(R.id.register_message_hint);
		input_phone = (TextView)findViewById(R.id.input_phone);
		et_country = (EditText) findViewById(R.id.et_country);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_ver_code = (EditText) findViewById(R.id.et_ver_code);
		btn_register_next = (Button) findViewById(R.id.btn_register_next);
		btn_send_ver_message = (Button) findViewById(R.id.btn_send_ver_message);
		btn_register_next.setOnClickListener(this);
		btn_send_ver_message.setOnClickListener(this);
		btn_send_ver_message.setEnabled(false);

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

		et_ver_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (et_phone.getText().toString().length() == 6) {
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_send_ver_message:
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
			/*case R.id.btn_register_next:
				BmobSMS.querySmsState(context, smsId, new QuerySMSStateListener() {

					@Override
					public void done(BmobSmsState state, BmobException ex) {
						if(ex == null){
							toast("getSmsState" + state.getSmsState() +"getVerifyState "+state.getVerifyState());

						}else {
							toast(ex.toString());
						}
					}
				});
				break;*/
			case R.id.btn_register_next:
				BmobSMS.verifySmsCode(this, phoneNumber, et_ver_code.getText().toString().trim(), new VerifySMSCodeListener() {

					@Override
					public void done(BmobException ex) {
						if(ex == null){
							toast("verify ok ");
							Intent intent = new Intent(RegisterInPhone.this,RegisterActivity.class);
							intent.putExtra("phonenumber", phoneNumber);
							startActivityForResult(intent, 1);
						}else {
							toast(ex.toString());
						}
					}
				});
				break;
			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch (requestCode){
			case 1:
				if(resultCode == RESULT_OK){
					String result = data.getStringExtra("result");
					if(result.equals("succeed")){
						finish();
					} else if(result.equals("failed")){

					}
				}
				break;
			default:
				break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void toast(String string){
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}

	class MySMSCodeListener implements SMSCodeListener {

		@Override
		public void onReceive(String content) {
			if(et_ver_code != null){
				et_ver_code.setText(content);
			}
		}

	}

}
