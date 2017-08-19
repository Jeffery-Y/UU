package com.example.lenovo.uu.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

import com.example.lenovo.uu.R;
import com.example.lenovo.uu.adapter.MessageRecentAdapter;
import com.example.lenovo.uu.ui.AddFriendActivity;
import com.example.lenovo.uu.ui.ChatActivity;
import com.example.lenovo.uu.ui.FragmentBase;
import com.example.lenovo.uu.ui.SetMyInfoActivity;
import com.example.lenovo.uu.util.CharacterParser;
import com.example.lenovo.uu.view.ClearEditText;
import com.example.lenovo.uu.view.HeaderLayout;
import com.example.lenovo.uu.view.dialog.DialogTips;
import com.example.lenovo.uu.widge.CircleRefreshLayout;

import java.util.List;

import static android.R.attr.delay;

/** 最近会话 */
public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	ClearEditText mClearEditText;
	private CircleRefreshLayout mRefreshLayout;/////////////////////
	ListView listview;
	
	MessageRecentAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		characterParser = CharacterParser.getInstance();
		initView();
	}
	
	private void initView(){
		initTopBarForLeftBtn("消息", R.drawable.head,
				new HeaderLayout.onLeftImageButtonClickListener() {
					@Override
					public void onClick() {
						Intent intent =new Intent(getActivity(),SetMyInfoActivity.class);
						intent.putExtra("from", "me");
						startActivity(intent);
					}
				});
		listview = (ListView)findViewById(R.id.list);
		mRefreshLayout = (CircleRefreshLayout) findViewById(R.id.refresh_layout);///////////
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
		listview.setAdapter(adapter);

		mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
		mClearEditText.setCursorVisible(false);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//
				// adapter.getFilter().filter(s);
				String filterStr = mClearEditText.getText().toString();

				if(!TextUtils.isEmpty(filterStr)){
					adapter.clear();
					List<BmobRecent> recent_conversations = BmobDB.create(getActivity()).queryRecents();
					for(BmobRecent item : recent_conversations){
						String name = item.getUserName();
						if (name != null) {
							if (name.indexOf(characterParser.getSelling(filterStr.toString())) != -1 ||
									name.indexOf(filterStr.toString()) != -1||
									characterParser.getSelling(name).contains(characterParser.getSelling(filterStr.toString()))) {
								adapter.add(item);
							}
						}
					}
				} else {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
				}
				listview.setAdapter(adapter);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mRefreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
					@Override
					public void refreshing() {
						refresh();
						new Handler().postDelayed(new Runnable(){
							public void run() {
								mRefreshLayout.finishRefreshing();
							}
						}, 3000);
					}
					@Override
					public void completeRefresh() {
					}
				});
	}
	
	/** 删除会话 */
	private void deleteRecent(BmobRecent recent){
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getUserName(),"删除会话", "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		//重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		//组装聊天对象
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}

	public void self_refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
					listview.setAdapter(adapter);
					mRefreshLayout.finishRefreshing();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
}
