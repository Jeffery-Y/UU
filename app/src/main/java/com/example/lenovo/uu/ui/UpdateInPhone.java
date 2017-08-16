package com.example.lenovo.uu.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
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
import cn.bmob.newsmssdk.exception.BmobException;
import cn.bmob.newsmssdk.listener.RequestSMSCodeListener;
import cn.bmob.newsmssdk.listener.SMSCodeListener;
import cn.bmob.newsmssdk.listener.VerifySMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateInPhone extends ActivityBase implements OnClickListener{

	LinearLayout register_messge, register_message_hint;
	TextView input_phone, register_hint_text;
	EditText et_country, et_phone, et_ver_code;
	Button btn_register_next, btn_send_ver_message;

//	Context context;
	String phoneNumber = "";
	int smsId = 123;

	private boolean bind_or_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_updateinfo_phone);
		final User u = userManager.getCurrentUser(User.class);
		initViews();

		if(u.getMobilePhoneNumber().isEmpty()){
			bind_or_cancel = true;
			initTopBarForLeft("绑定手机");
			register_hint_text.setText("绑定手机号码验证");
			et_phone.setHint("请输入要绑定的手机号码");
			btn_register_next.setText("绑定");
		}
		else {
			bind_or_cancel = false;
			initTopBarForLeft("解绑手机");
			register_hint_text.setText("解绑手机号码验证");
			et_phone.setHint("请输入已绑定的手机号码");
			btn_register_next.setText("解除绑定");
		}
//		context = getApplicationContext();
//		Bmob.initialize(this, Config.applicationId);
		BmobSMS.initialize(UpdateInPhone.this, Config.applicationId, new MySMSCodeListener());
	}

	private void initViews() {
		register_messge = (LinearLayout)findViewById(R.id.register_messge);
		register_message_hint = (LinearLayout)findViewById(R.id.register_message_hint);
		input_phone = (TextView)findViewById(R.id.input_phone);
		register_hint_text = (TextView)findViewById(R.id.register_hint_text);
		et_country = (EditText) findViewById(R.id.et_country);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_phone.requestFocus();
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
					btn_send_ver_message.setBackground(UpdateInPhone.this.getResources().getDrawable(R.drawable.btn_login_selector));
				} else {
					btn_send_ver_message.setEnabled(false);
					btn_send_ver_message.setBackground(UpdateInPhone.this.getResources().getDrawable(R.drawable.register_btn_shape));
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
				et_ver_code.requestFocus();
				btn_send_ver_message.setEnabled(false);
				phoneNumber = et_phone.getText().toString();
				if(phoneNumber.length() == 11){
					BmobSMS.requestSMSCode(this, phoneNumber, "UU", new RequestSMSCodeListener() {

						@Override
						public void done(Integer smsId, BmobException ex) {
							if(ex == null){
								toast("send ok " + smsId);
								UpdateInPhone.this.smsId = smsId;
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
											btn_register_next.setBackground(UpdateInPhone.this.getResources().getDrawable(R.drawable.btn_login_selector));
										} else {
											btn_register_next.setEnabled(false);
											btn_register_next.setBackground(UpdateInPhone.this.getResources().getDrawable(R.drawable.register_btn_shape));
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
							if(bind_or_cancel){
								updateInPhone();
							} else {
								User u = userManager.getCurrentUser(User.class);
								if(phoneNumber.equals(u.getMobilePhoneNumber())){
									ShowToast("原手机验证成功!\n 请绑定新手机");
									bind_or_cancel = true;
									initTopBarForLeft("绑定新手机");
									register_hint_text.setText("绑定手机号码验证");
									et_phone.setHint("请输入要绑定的手机号码");
									btn_register_next.setText("绑定");
								}else {
									ShowToast("当前验证的手机与已绑定手机不一致！");
								}
								et_phone.setText("");
								et_ver_code.setText("");
								register_messge.setVisibility(View.GONE);
								register_message_hint.setVisibility(View.GONE);
								btn_send_ver_message.setEnabled(false);
								btn_send_ver_message.setBackground(UpdateInPhone.this.getResources()
										.getDrawable(R.drawable.register_btn_shape));
								btn_register_next.setEnabled(false);
								btn_register_next.setBackground(UpdateInPhone.this.getResources()
										.getDrawable(R.drawable.register_btn_shape));
								et_phone.requestFocus();
							}
							/*Intent intent = new Intent(UpdateInPhone.this,RegisterActivity.class);
							intent.putExtra("phonenumber", phoneNumber);
							startActivity(intent);*/
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

	private void updateInPhone() {
		final User user = userManager.getCurrentUser(User.class);
		user.setMobilePhoneNumber(phoneNumber);
		user.setMobilePhoneNumberVerified(true);
		user.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				final User u = userManager.getCurrentUser(User.class);
				ShowToast("手机绑定成功:"+u.getMobilePhoneNumber());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
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
				if(et_ver_code != null)et_ver_code.setText(content);
		}

	}
}
