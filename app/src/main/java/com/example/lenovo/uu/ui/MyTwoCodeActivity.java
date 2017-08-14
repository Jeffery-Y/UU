package com.example.lenovo.uu.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.scancode.zxing.encode.EncodingHandler;
import com.example.lenovo.uu.util.ImageLoadOptions;
import com.google.zxing.WriterException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;

/**
 * 二维码页面
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("SimpleDateFormat")
public class MyTwoCodeActivity extends ActivityBase implements View.OnClickListener{

	TextView my_name, add_me;
	ImageView my_two_code, my_head, create_bar_code, create_two_code;

	String username = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_twocode);
		ButterKnife.bind(this);
		username = getIntent().getStringExtra("user_name");
//		Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
		initView();
	}

	private void initView() {
		initTopBarForLeft("二维码");
		User user = userManager.getCurrentUser(User.class);
		my_head = (ImageView)findViewById(R.id.my_head);
		my_two_code = (ImageView)findViewById(R.id.my_two_code);
		my_name = (TextView)findViewById(R.id.my_name);
		add_me = (TextView)findViewById(R.id.add_me);
		create_bar_code = (ImageView)findViewById(R.id.create_bar_code);
		create_two_code = (ImageView)findViewById(R.id.create_two_code);
		my_name.setText(username);
		refreshAvatar(user.getAvatar());
		my_head.setImageResource(R.drawable.head);
		create2Code(username);
		my_head.setOnClickListener(this);
		create_bar_code.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.my_head:
				if(create_bar_code.getVisibility() == View.VISIBLE){
					Bitmap bitmap = create2Code(username);
					Bitmap headBitmap = getHeadBitmap(60);
					if(bitmap!=null&&headBitmap!=null) {
						createQRCodeBitmapWithPortrait(bitmap, headBitmap);
					}
				}
				break;
			case R.id.create_bar_code:
				createBarCode(username);
				break;
			case R.id.create_two_code:
				create2Code(username);
				create_two_code.setVisibility(View.GONE);
				create_bar_code.setVisibility(View.VISIBLE);
				create_bar_code.setOnClickListener(this);
				add_me.setText(R.string.add_me_two);
				initTopBarForLeft("二维码");
				break;
			default:
				break;
		}
	}

	private Bitmap createBarCode(String key) {
		Bitmap qrCode = null;
		try {
			qrCode = EncodingHandler.createBarCode(key, 600, 300);
			my_two_code.setImageBitmap(qrCode);
			create_bar_code.setVisibility(View.GONE);
			create_two_code.setVisibility(View.VISIBLE);
			create_two_code.setOnClickListener(this);
			add_me.setText(R.string.add_me_bar);
			initTopBarForLeft("条形码");
		} catch (Exception e) {
			Toast.makeText(this,"UU账户包含汉字！条形码不支持！",Toast.LENGTH_SHORT).show();
			create_two_code.setVisibility(View.GONE);
			create_bar_code.setVisibility(View.VISIBLE);
			add_me.setText(R.string.add_me_two);
			initTopBarForLeft("二维码");
			e.printStackTrace();
		}
		return qrCode;
	}

	/**
	 * 生成二维码
	 * @param key
	 */
	private Bitmap create2Code(String key) {
		Bitmap qrCode=null;
		try {
			qrCode= EncodingHandler.create2Code(key, 400);
			my_two_code.setImageBitmap(qrCode);
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qrCode;
	}

	/**
	 * 初始化头像图片
	 */
	private Bitmap getHeadBitmap(int size) {
		try {
			// 这里采用从asset中加载图片abc.jpg
			Bitmap portrait = ((BitmapDrawable) my_head.getDrawable()).getBitmap();
//			BitmapFactory.decodeResource(getResources(), R.drawable.head);
			// 对原有图片压缩显示大小
			Matrix mMatrix = new Matrix();
			float width = portrait.getWidth();
			float height = portrait.getHeight();
			mMatrix.setScale(size / width, size / height);
			return Bitmap.createBitmap(portrait, 0, 0, (int) width, (int) height, mMatrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在二维码上绘制头像
	 */
	private void createQRCodeBitmapWithPortrait(Bitmap qr, Bitmap portrait) {
		// 头像图片的大小
		int portrait_W = portrait.getWidth();
		int portrait_H = portrait.getHeight();

		// 设置头像要显示的位置，即居中显示
		int left = (qr.getWidth() - portrait_W) / 2;
		int top = (qr.getHeight() - portrait_H) / 2;
		int right = left + portrait_W;
		int bottom = top + portrait_H;
		Rect rect1 = new Rect(left, top, right, bottom);

		// 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
		Canvas canvas = new Canvas(qr);

		// 设置我们要绘制的范围大小，也就是头像的大小范围
		Rect rect2 = new Rect(0, 0, portrait_W, portrait_H);
		// 开始绘制
		canvas.drawBitmap(portrait, rect2, rect1, null);
	}

	/**
	 * 更新头像 refreshAvatar
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, my_head, ImageLoadOptions.getOptions());
		} else {
			my_head.setImageResource(R.drawable.head);
		}
	}
}
