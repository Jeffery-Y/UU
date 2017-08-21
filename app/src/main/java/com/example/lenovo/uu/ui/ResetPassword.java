package com.example.lenovo.uu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.config.BmobConstants;
import com.example.lenovo.uu.util.CommonUtils;

public class ResetPassword extends BaseActivity implements View.OnClickListener{
    LinearLayout old_password_layout;
    Button btn_register, register_in_back;
    EditText et_username, et_password, re_et_password, et_old_password;
    TextView register_account_hint_text;
    BmobChatUser currentUser;
    User user;

    String from = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        from = getIntent().getStringExtra("from");

        user = userManager.getCurrentUser(User.class);
        register_account_hint_text = (TextView)findViewById(R.id.register_account_hint_text);
        register_account_hint_text.setText("修改密码");
        et_username = (EditText) findViewById(R.id.et_username);
        et_username.setEnabled(false);
        et_username.setText(user.getUsername());
        et_password = (EditText) findViewById(R.id.et_password);
        re_et_password = (EditText) findViewById(R.id.re_et_password);

        if(from != null && from.equals("reset_password_by_old")){
            old_password_layout = (LinearLayout)findViewById(R.id.old_password_layout);
            old_password_layout.setVisibility(View.VISIBLE);
            et_old_password = (EditText)findViewById(R.id.et_old_password);
            et_old_password.requestFocus();
        } else if(from != null && from.equals("verified_phone")){
            et_password.requestFocus();
        }

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setText("修改密码");
        btn_register.setOnClickListener(this);
        register_in_back = (Button) findViewById(R.id.register_in_back);
        register_in_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_register:
                resetPassword();
                break;
            case R.id.registr_in_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void resetPassword(){
        String password = et_password.getText().toString();
        String pwd_again = re_et_password.getText().toString();

        if(from != null && from.equals("reset_password_by_old")){
            String in_ole_password = et_old_password.getText().toString();
            String user_old_password = user.getPassword();
            if (TextUtils.isEmpty(in_ole_password)) {
                ShowToast("旧密码不能为空！");
                return;
            }
            if (TextUtils.isEmpty(user_old_password)) {
                ShowToast("获取该用户账号信息失败！");
                return;
            }
            if(!in_ole_password.equals(user_old_password)){
                ShowToast("旧密码有误！");
                return;
            }
        } else if(from != null && from.equals("verified_phone")){
            re_et_password.requestFocus();
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }

        if (!pwd_again.equals(password)) {
            ShowToast(R.string.toast_error_comfirm_password);
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            ShowToast(R.string.network_tips);
            return;
        }

        final ProgressDialog progress = new ProgressDialog(ResetPassword.this);
        progress.setMessage("正在修改...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        user.setPassword(password);
        user.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                final User u = userManager.getCurrentUser(User.class);
                ShowToast("修改密码成功");
                progress.dismiss();
                if(from != null && from.equals("verified_phone")){
                    //发广播通知登陆页面退出
                    sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
                }
                // 启动主页
                Intent intent = new Intent(ResetPassword.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ShowToast("onFailure:" + arg1);
                progress.dismiss();
            }
        });
    }


/*	@Override
	public void onBackPressed(){
		Intent intent_result = new Intent();
		intent_result.putExtra("result", "failed");
		setResult(RESULT_OK, intent_result);
		super.onBackPressed();
	}*/

}
