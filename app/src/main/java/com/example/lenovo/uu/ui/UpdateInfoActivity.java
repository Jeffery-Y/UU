package com.example.lenovo.uu.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.listener.UpdateListener;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.view.HeaderLayout.onRightImageButtonClickListener;

/**
 * 设置昵称
 */
public class UpdateInfoActivity extends ActivityBase {

	EditText edit_update_information;
	TextView update_information;

	private String name_update_object;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_updateinfo);
		edit_update_information = (EditText) findViewById(R.id.edit_update_information);
		update_information = (TextView)findViewById(R.id.update_information);
		name_update_object = getIntent().getStringExtra("layout");
		update_information.setText(name_update_object);
		edit_update_information.setHint("请输入" + name_update_object);
		initView();
	}

	private void initView() {
		initTopBarForBoth("修改" + name_update_object, R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String information = edit_update_information.getText().toString();
						if (information.equals("")) {
							ShowToast("请填写" + name_update_object + "!");
							return;
						}
						updateInfo(information);
					}
				});
	}

	/** 修改资料
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(String information) {
		final User user = userManager.getCurrentUser(User.class);
		if(name_update_object.equals("昵称")){
			user.setNick(information);
		} else if (name_update_object.equals("邮箱")){
			user.setEmail(information);
			user.setEmailVerified(true);
		}
		user.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				final User u = userManager.getCurrentUser(User.class);
				ShowToast("修改" + name_update_object + "成功:" +u.getEmail());
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
