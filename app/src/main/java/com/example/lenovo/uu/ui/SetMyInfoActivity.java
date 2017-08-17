package com.example.lenovo.uu.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.example.lenovo.uu.CustomApplcation;
import com.example.lenovo.uu.R;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.config.BmobConstants;
import com.example.lenovo.uu.util.CollectionUtils;
import com.example.lenovo.uu.util.ImageLoadOptions;
import com.example.lenovo.uu.util.PhotoUtil;
import com.example.lenovo.uu.view.dialog.DialogTips;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 个人资料页面
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("SimpleDateFormat")
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

	static int RESETPASSWORD = 1;
	static int RESETAVATOR = 0;

	TextView tv_set_name, tv_set_nick, tv_set_phone, tv_set_email, tv_set_gender;
	ImageView iv_set_avator, iv_arrow, iv_nick_arrow, iv_email_arrow, iv_phone_arrow, iv_account_arrow, iv_gender_arrow;
	LinearLayout layout_all;

	Button btn_chat, btn_back, btn_delete_friend, btn_add_friend;
	RelativeLayout layout_head, layout_nick, layout_email, layout_phone, layout_account
			, layout_gender, layout_black_tips, layout_two_code;

	String from = "";
	String other_username = "";
	User other_user, myself;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*// 隐藏掉三个虚拟的导航按钮，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}*/
		setContentView(R.layout.activity_set_info);
		from = getIntent().getStringExtra("from");//me add other
		if(from != null && from.equals("other")){
			other_username = getIntent().getStringExtra("username");
		}
		initView();
//		path = "";
	}

	private void initView() {
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
		iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
		iv_nick_arrow = (ImageView) findViewById(R.id.iv_nick_arrow);
		iv_account_arrow = (ImageView) findViewById(R.id.iv_account_arrow);
		iv_gender_arrow = (ImageView) findViewById(R.id.iv_gender_arrow);
		iv_email_arrow = (ImageView) findViewById(R.id.iv_email_arrow);
		iv_phone_arrow = (ImageView) findViewById(R.id.iv_phone_arrow);
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
		tv_set_email = (TextView) findViewById(R.id.tv_set_email);
		tv_set_phone = (TextView) findViewById(R.id.tv_set_phone);
		layout_head = (RelativeLayout) findViewById(R.id.layout_head);
		layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
		layout_email = (RelativeLayout) findViewById(R.id.layout_email);
		layout_account = (RelativeLayout) findViewById(R.id.layout_account);
		layout_phone = (RelativeLayout) findViewById(R.id.layout_phone);
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
		layout_two_code = (RelativeLayout) findViewById(R.id.layout_two_code);
		// 黑名单提示语
		layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
		btn_chat = (Button) findViewById(R.id.btn_chat);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_delete_friend = (Button)findViewById(R.id.btn_delete);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
		if (from.equals("me")) {
			initTopBarForLeft("个人资料");
			iv_nick_arrow.setVisibility(View.VISIBLE);
			iv_account_arrow.setVisibility(View.VISIBLE);
			iv_gender_arrow.setVisibility(View.VISIBLE);
			iv_arrow.setVisibility(View.VISIBLE);
			iv_email_arrow.setVisibility(View.VISIBLE);
			iv_phone_arrow.setVisibility(View.VISIBLE);
			btn_back.setVisibility(View.GONE);
			btn_delete_friend.setVisibility(View.GONE);
			btn_chat.setVisibility(View.GONE);
			btn_add_friend.setVisibility(View.GONE);
			layout_head.setOnClickListener(this);
			layout_nick.setOnClickListener(this);
			layout_email.setOnClickListener(this);
			layout_account.setOnClickListener(this);
			layout_phone.setOnClickListener(this);
			layout_gender.setOnClickListener(this);
			initMyData();
		} else {
			initTopBarForLeft("详细资料");
			iv_phone_arrow.setVisibility(View.INVISIBLE);
			iv_email_arrow.setVisibility(View.INVISIBLE);
			iv_nick_arrow.setVisibility(View.INVISIBLE);
			iv_account_arrow.setVisibility(View.INVISIBLE);
			iv_gender_arrow.setVisibility(View.INVISIBLE);
			iv_arrow.setVisibility(View.INVISIBLE);
			//不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
			btn_chat.setVisibility(View.VISIBLE);
			btn_back.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			btn_back.setOnClickListener(this);

			if (mApplication.getContactList().containsKey(other_username)) {// 是好友
//					btn_chat.setVisibility(View.VISIBLE);
//					btn_chat.setOnClickListener(this);
				btn_add_friend.setVisibility(View.GONE);
				btn_delete_friend.setVisibility(View.VISIBLE);
				btn_delete_friend.setOnClickListener(this);
			} else {
//					btn_chat.setVisibility(View.GONE);
				btn_delete_friend.setVisibility(View.GONE);
				btn_add_friend.setVisibility(View.VISIBLE);
				btn_add_friend.setOnClickListener(this);
			}
			initOtherData(other_username);
		}
	}

	private void initMyData() {
		layout_head.setEnabled(false);
		layout_nick.setEnabled(false);
		layout_email.setEnabled(false);
		layout_account.setEnabled(false);
		layout_phone.setEnabled(false);
		layout_gender.setEnabled(false);
		myself = userManager.getCurrentUser(User.class);
		updateUser(myself);
	}

	private void initOtherData(String name) {
		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		btn_back.setEnabled(false);
		btn_delete_friend.setEnabled(false);
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					other_user = arg0.get(0);
					updateUser(other_user);
				} else {
					if(from.equals("scan")){
						ShowToast("哪来的码！？瞎扫！！");
						finish();
					} else {
						ShowToast("此账号不存在！");
					}
					ShowLog("查无此人");
				}
			}
		});
	}

	private void updateUser(User user) {
		// 更改
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_phone.setText(user.getMobilePhoneNumber());
		tv_set_email.setText(user.getEmail());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");
		layout_two_code.setVisibility(View.VISIBLE);
		layout_two_code.setOnClickListener(this);
		if(from != null && from.equals("me")){
			layout_head.setEnabled(true);
			layout_nick.setEnabled(true);
			layout_email.setEnabled(true);
			layout_account.setEnabled(true);
			layout_phone.setEnabled(true);
			layout_gender.setEnabled(true);
		} else if (from.equals("other")) {
			btn_chat.setEnabled(true);
			btn_back.setEnabled(true);
			btn_delete_friend.setEnabled(true);
			btn_add_friend.setEnabled(true);
			// 检测是否为黑名单用户
			if (BmobDB.create(this).isBlackUser(user.getUsername())) {
				btn_back.setVisibility(View.GONE);
				btn_delete_friend.setVisibility(View.GONE);
				layout_black_tips.setVisibility(View.VISIBLE);
			} else {
				btn_back.setVisibility(View.VISIBLE);
//				btn_delete_friend.setVisibility(View.VISIBLE);
				layout_black_tips.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
					ImageLoadOptions.getOptions());
		} else {
			iv_set_avator.setImageResource(R.drawable.head);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(from != null){
			if (from.equals("me")) {
				initMyData();
			} else if(from.equals("other")){
				initOtherData(other_username);
			}
		} else {
			ShowToast("数据丢失！请重新加载！");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_chat:// 发起聊天
				Intent intent = new Intent(this, ChatActivity.class);
				intent.putExtra("user", other_user);
				startAnimActivity(intent);
				finish();
				break;
			case R.id.layout_head:
				showResetPop(RESETAVATOR);
				break;
			case R.id.layout_account:
				showResetPop(RESETPASSWORD);
				break;
			case R.id.layout_nick:
				Intent intent_nick = new Intent(this, UpdateInfoActivity.class);
				intent_nick.putExtra("layout", "昵称");
				startAnimActivity(intent_nick);
//				startAnimActivity(UpdateInfoActivity.class);
				break;
			case R.id.layout_email:
				Intent intent_email = new Intent(this, UpdateInfoActivity.class);
				intent_email.putExtra("layout", "邮箱");
				startAnimActivity(intent_email);
				break;
			case R.id.layout_phone:
				Intent intent_phone = new Intent(this, UpdateInPhone.class);
				startAnimActivity(intent_phone);
				break;
			case R.id.layout_gender:// 性别
				showSexChooseDialog();
				break;
			case R.id.layout_two_code:
				Intent intent2 = new Intent(this, MyTwoCodeActivity.class);
				if(from.equals("me")){
					intent2.putExtra("user_name", myself.getUsername());
				} else if(from.equals("other")){
					intent2.putExtra("user_name", other_user.getUsername());
				}
				startAnimActivity(intent2);
				break;
			case R.id.btn_back:// 黑名单
				showBlackDialog(other_user.getUsername());
				break;
			case R.id.btn_delete://删除好友
				deleteContact(other_user);
				break;
			case R.id.btn_add_friend:// 添加好友
				addFriend();
				break;
		}
	}
	
	String[] sexs = new String[]{ "男", "女" };
	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
		.setTitle("单选框")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setSingleChoiceItems(sexs, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						BmobLog.i("点击的是"+sexs[which]);
						updateInfo(which);
						dialog.dismiss();
					}
				})
		.setNegativeButton("取消", null)
		.show();
	}

//	TextView tv_choose_first, tv_choose_second;
	LinearLayout layout_choose_first, layout_choose_second, layout_reset_avatar_exit;
	PopupWindow avatorPop;

	public String filePath = "";

	private void showResetPop(final int object) {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
		layout_choose_first = (LinearLayout) view.findViewById(R.id.layout_choose_first);
		layout_choose_second = (LinearLayout) view.findViewById(R.id.layout_choose_second);
		layout_reset_avatar_exit = (LinearLayout) view.findViewById(R.id.layout_reset_avatar_exit);
		final TextView tv_choose_first = (TextView)view.findViewById(R.id.tv_choose_first);
		final TextView tv_choose_second = (TextView)view.findViewById(R.id.tv_choose_second);

		//用户点击第一个选项
		layout_choose_first.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ShowLog("点击选项一");
				layout_choose_second.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
				layout_choose_first.setBackground(getResources().getDrawable(R.drawable.pop_bg_press));
				if(object == RESETPASSWORD){
					//更换密码
					Intent intent_reset_password = new Intent(SetMyInfoActivity.this, RegisterInPhone.class);
					intent_reset_password.putExtra("from", "reset_password");
					startAnimActivity(intent_reset_password);
				} else if(object == RESETAVATOR){
					//更换头像
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intent,BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
				}
			}
		});

		//用户点击第二个选项
		layout_choose_second.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("点击第二个选项");
				layout_choose_first.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
				layout_choose_second.setBackground(getResources().getDrawable(R.drawable.pop_bg_press));
				if(object == RESETAVATOR){
					//更换头像
					File dir = new File(BmobConstants.MyAvatarDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					// 原图
					File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
					filePath = file.getAbsolutePath();// 获取相片的保存路径
					Uri imageUri = Uri.fromFile(file);

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
				} else if(object == RESETPASSWORD){
					//更换密码
					Intent intent_reset_password_by_old = new Intent(SetMyInfoActivity.this, ResetPassword.class);
					intent_reset_password_by_old.putExtra("from", "reset_password_by_old");
					startAnimActivity(intent_reset_password_by_old);
				}
			}
		});

		//用户点击退出
		layout_reset_avatar_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ShowLog("退出更换头像");
				avatorPop.dismiss();
			}
		});
		avatorPop = new PopupWindow(view, mScreenWidth, mScreenHeight);

		//设置popupWindow消失时的监听
		avatorPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
			//在dismiss中恢复透明度
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		//产生背景变暗效果
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.4f;
		getWindow().setAttributes(lp);
		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
//		Drawable drawable = getResources().getDrawable();
//		avatorPop.setBackgroundDrawable(drawable);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从底部弹起
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);

		if(object == RESETPASSWORD){
			tv_choose_first.setText("验证已绑定手机来修改密码");
			tv_choose_second.setText("验证旧密码来修改密码");
		}
		myself = (User) userManager.getCurrentUser(User.class);
		if(object == RESETPASSWORD && TextUtils.isEmpty(myself.getMobilePhoneNumber())){
			//修改密码时若用户没有绑定手机，则不显示通过验证手机修改密码的选项
			layout_choose_first.setVisibility(View.GONE);
		}

	}

	/** 删除联系人
	 * deleteContact
	 * @return void
	 * @throws
	 */
	private void deleteContact(final User user){
		final ProgressDialog progress = new ProgressDialog(SetMyInfoActivity.this);
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("删除成功");
				//删除内存
				CustomApplcation.getInstance().getContactList().remove(user.getUsername());
				CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
//				finish();
				//更新界面
				SetMyInfoActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						progress.dismiss();
						btn_back.setVisibility(View.GONE);
						btn_delete_friend.setVisibility(View.GONE);
						btn_add_friend.setVisibility(View.VISIBLE);
						btn_add_friend.setOnClickListener(SetMyInfoActivity.this);
//						userAdapter.remove(user);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("删除失败："+arg1);
				progress.dismiss();
			}
		});
	}

	/** 修改资料
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(int which) {
		myself = userManager.getCurrentUser(User.class);
		BmobLog.i("updateInfo 性别："+ myself.getSex());
		if(which==0){
			myself.setSex(true);
		}else{
			myself.setSex(false);
		}
		myself.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("修改成功");
				final User u = userManager.getCurrentUser(User.class);
				BmobLog.i("修改成功后的sex = "+u.getSex());
				tv_set_gender.setText(u.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
	/**
	 * 添加好友请求
	 * 
	 * @Title: addFriend
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求
		BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
				other_user.getObjectId(), new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
						ShowLog("发送请求失败:" + arg1);
					}
				});
	}

	/**
	 * 显示黑名单提示框
	 * 
	 * @Title: showBlackDialog
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void showBlackDialog(final String username) {
		DialogTips dialog = new DialogTips(this, "加入黑名单",
				"加入黑名单，你将不再收到对方的消息，确定要继续吗？", "确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				// 添加到黑名单列表
				userManager.addBlack(username, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowToast("黑名单添加成功!");
						btn_back.setVisibility(View.GONE);
						btn_delete_friend.setVisibility(View.GONE);
						layout_black_tips.setVisibility(View.VISIBLE);
						// 重新设置下内存中保存的好友列表
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("黑名单添加失败:" + arg1);
					}
				});
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200, BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200, BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				 Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
//			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			break;

		}
	}

	private void uploadAvatar() {
		BmobLog.i("头像地址：" + filePath);//////////////
		final BmobFile bmobFile = new BmobFile(new File(filePath));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	private void updateUserAvatar(final String url) {
		myself = (User) userManager.getCurrentUser(User.class);
		myself.setAvatar(url);
		myself.update(this, new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("头像更新成功！");
				// 更新头像
				refreshAvatar(url);
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}

//	String path = "";

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				filePath = BmobConstants.MyAvatarDir + filename;
				BmobLog.i("裁剪图片的路径：" + filePath);////////////
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

}
