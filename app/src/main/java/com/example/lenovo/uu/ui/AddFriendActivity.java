package com.example.lenovo.uu.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.adapter.AddFriendAdapter;
import com.example.lenovo.uu.bean.User;
import com.example.lenovo.uu.ui.CommonScanActivity;
import com.example.lenovo.uu.util.CharacterParser;
import com.example.lenovo.uu.util.CollectionUtils;
import com.example.lenovo.uu.view.xlist.XListView;
import com.example.lenovo.uu.view.xlist.XListView.IXListViewListener;

/** 添加好友 */
public class AddFriendActivity extends ActivityBase implements OnClickListener,IXListViewListener,OnItemClickListener{
	private CharacterParser characterParser;
	ProgressDialog progress ;
	List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	XListView mListView;
	AddFriendAdapter adapter;

	EditText et_find_name;
	Button btn_exact_search, btn_vague_search, scan_twocode;
	/*if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
                            mListView.setPullLoadEnable(false);
                            ShowToast("用户搜索完成!");
                        }else{
                            ShowLog("setPullLoadEnable true");
                            mListView.setPullLoadEnable(true);
                        }*/
//	int curPage = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		initView();
	}
	
	private void initView(){
		initTopBarForLeft("查找好友");
		et_find_name = (EditText)findViewById(R.id.et_find_name);
		btn_exact_search = (Button)findViewById(R.id.btn_exact_search);
		btn_exact_search.setOnClickListener(this);
		btn_vague_search = (Button)findViewById(R.id.btn_vague_search);
		btn_vague_search.setOnClickListener(this);
        scan_twocode = (Button)findViewById(R.id.scan_twocode);
        scan_twocode.setOnClickListener(this);
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_search);
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 不允许下拉
		mListView.setPullRefreshEnable(false);
		// 设置监听器
		mListView.setXListViewListener(this);
		//
		mListView.pullRefreshing();
		
		adapter = new AddFriendAdapter(this, users);
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.btn_exact_search:
				btn_vague_search.setEnabled(false);
//				adapter
				users.clear();
				String exact_search_name = et_find_name.getText().toString();
				if(exact_search_name!=null && !exact_search_name.equals("")){
					initExactSearchList(exact_search_name);
				}else{
					ShowToast("请输入用户名");
				}
				btn_vague_search.setEnabled(true);
				break;
			case R.id.btn_vague_search://模糊搜索
				btn_exact_search.setEnabled(false);
				users.clear();
				String vague_search_name = et_find_name.getText().toString();
				if(vague_search_name!=null && !vague_search_name.equals("")){
					initVagueSearchList(vague_search_name);
				}else{
					ShowToast("请输入用户名");
				}
				btn_exact_search.setEnabled(true);
				break;
			case R.id.scan_twocode:
				Intent intent =new Intent(this,CommonScanActivity.class);
				startAnimActivity(intent);
				break;
			default:
				break;
		}
	}

	private void initExactSearchList(String exact_search_name){
		userManager.queryUser(exact_search_name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				if (arg0 != null && arg0.size() > 0) {
					adapter.add(arg0.get(0));
				} else {
					users.clear();
					adapter = new AddFriendAdapter(AddFriendActivity.this, users);
					mListView.setAdapter(adapter);

					mListView.setOnItemClickListener(AddFriendActivity.this);
//					mListView.removeAllViews();
					ShowToast("此账号不存在！");
					ShowLog("Add ExactSearch:查无此人");
				}
			}
		});
	}

	private void initVagueSearchList(String vague_search_name){
		progress = new ProgressDialog(AddFriendActivity.this);
		progress.setMessage("正在搜索...");
		progress.setCanceledOnTouchOutside(true);
		progress.show();
		queryAllRelateUser(vague_search_name, 0);
	}

	private void queryAllRelateUser(final String vague_search_name, final int cur_page){//?????????????????????????isupdate

		userManager.queryUserByPage(false, cur_page, vague_search_name, new FindListener<BmobChatUser>() {
			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				if (CollectionUtils.isNotNull(arg0)) {
					//根据用户输入的用户名筛选
					characterParser = CharacterParser.getInstance();
					List<BmobChatUser> filterDateList = new ArrayList<BmobChatUser>();
					filterDateList.clear();
					for (BmobChatUser sortModel : arg0) {
						String name = sortModel.getUsername();
						ShowLog("Add querry by page0:" + name + "Sell:" + characterParser.getSelling(name));
						if (name != null) {
							if (name.indexOf(characterParser.getSelling(vague_search_name)) != -1 ||
									characterParser.getSelling(name).contains(characterParser.getSelling(vague_search_name))) {
								ShowLog("Add in adapter:" + name);
								filterDateList.add(sortModel);
							}
						}
					}
//					arg0 = filterDateList;//??????????????脱裤子放屁
					adapter.addAll(filterDateList);

					queryAllRelateUser(vague_search_name, cur_page + 1);
				}else{
					BmobLog.i("查询成功:无返回值");
					ShowToast("查询结束");
					ShowLog("查询结束");
					progress.dismiss();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i("查询错误:"+arg1);
				ShowToast("查询出现错误");
//				mListView.setPullLoadEnable(false);
//				refreshPull();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobChatUser user = (BmobChatUser) adapter.getItem(position-1);
		Intent intent =new Intent(this,SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);		
	}



	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore(){
		// TODO Auto-generated method stub
	}
}
