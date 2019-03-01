package net.iaround.ui.group.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.datamodel.GroupAffairModel;

/**
 * @Description: 6.0圈消息设置，用于设置圈消息接收状态
 * @author tanzy
 * @date 2015-5-4
 */
public class GroupMsgSetActivity extends BaseActivity implements
        OnClickListener , HttpCallBack {

	//标题栏
	private TextView tvTitle;
	private ImageView mIvLeft;
	private FrameLayout flLeft;


	private TextView tvgroup;
	private CheckBox[] checkBoxes = new CheckBox[3];
	private Dialog waitDialog;

	private String groupId = "";
	private String groupName = "";
	private int type = 0;
	private int newType = 0;

	private static final int MSG_HIDE_WAIT = 1000;
	private long updateStatusFlag = 0;

	public static void launch(Activity context, int requestCode,
                              String groupId, String name) {
		Intent intent = new Intent(context, GroupMsgSetActivity.class);
		intent.putExtra("group_id", groupId);
		intent.putExtra("group_name", name);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_msg_set);
		groupId = getIntent().getStringExtra("group_id");
		groupName = getIntent().getStringExtra("group_name");
		initViews();
		setListeners();
	}

	/**
	 * @Title: initViews
	 * @Description: 初始化所有控件
	 */
	private void initViews() {
		//初始化标题栏
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.group_msg_set_title);

		tvgroup = (TextView) findViewById(R.id.group_name);

		checkBoxes[0] = (CheckBox) findViewById(R.id.check_box_1);
		checkBoxes[0].setTag(GroupMsgReceiveType.RECEIVE_AND_NOTICE);
		checkBoxes[1] = (CheckBox) findViewById(R.id.check_box_2);
		checkBoxes[1].setTag(GroupMsgReceiveType.RECEIVE_NOT_NOTICE);
		checkBoxes[2] = (CheckBox) findViewById(R.id.check_box_3);
		checkBoxes[2].setTag(GroupMsgReceiveType.NOT_RECEIVE);

		String[] titles = getResources().getStringArray(
				R.array.group_msgsetting_s);
		((TextView) findViewById(R.id.text_1)).setText(titles[1]);
		((TextView) findViewById(R.id.text_2)).setText(titles[2]);
		((TextView) findViewById(R.id.text_3)).setText(titles[3]);

		String formatStr = getResources().getString(
				R.string.group_msg_set_tips_text);
		String titleName = String.format(formatStr, groupName);
		SpannableString groupName = FaceManager.getInstance(GroupMsgSetActivity.this)
				.parseIconForString(tvgroup, GroupMsgSetActivity.this, titleName, 14);
		tvgroup.setText(groupName);

		type = GroupAffairModel.getInstance().getGroupMsgStatus(
				Long.parseLong(groupId));
		if (type > 0 && type <= checkBoxes.length)
			checkBoxes[type - 1].setChecked(true);

		waitDialog = DialogUtil.getProgressDialog(GroupMsgSetActivity.this, "", GroupMsgSetActivity.this
				.getResources().getString(R.string.please_wait), null);
	}

	/**
	 * @Title: setListeners
	 * @Description: 初始化监听器
	 */
	private void setListeners() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mIvLeft.setImageResource(R.drawable.title_back);
		mIvLeft.setOnClickListener(this);
		flLeft.setOnClickListener(this);

		findViewById(R.id.rl_group_set1).setOnClickListener(this);
		findViewById(R.id.rl_group_set2).setOnClickListener(this);
		findViewById(R.id.rl_group_set3).setOnClickListener(this);

		for (int i = 0; i < checkBoxes.length; i++)
			checkBoxes[i].setOnClickListener(checkBoxClick);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fl_left:
			case R.id.iv_left:
				updateStatus();
				break;
			case R.id.rl_group_set1:

				checkBoxes[0].setChecked(true);
				checkBoxes[1].setChecked(false);
				checkBoxes[2].setChecked(false);
				break;
			case R.id.rl_group_set2:
				checkBoxes[0].setChecked(false);
				checkBoxes[1].setChecked(true);
				checkBoxes[2].setChecked(false);
				break;
			case R.id.rl_group_set3:
				checkBoxes[0].setChecked(false);
				checkBoxes[1].setChecked(false);
				checkBoxes[2].setChecked(true);
				break;

		}
	}

	/**
	 * 更新消息接收状态
	 */
	private void updateStatus() {

		for (int i = 0; i < checkBoxes.length; i++) {
			if (checkBoxes[i].isChecked())
				newType = (Integer) checkBoxes[i].getTag();
		}
		if (newType == type) {// 状态没有改变
			setResult(RESULT_OK);
			finish();
		} else {// 状态改变了
			updateStatusFlag = GroupHttpProtocol.setGroupMsgReceiveStatus(
					GroupMsgSetActivity.this, Long.parseLong(groupId), newType, this);
			waitDialog.show();
		}
	}

	@Override
	public void onGeneralSuccess(String result, long flag) {
		handler.sendEmptyMessage(MSG_HIDE_WAIT);
		if (flag == updateStatusFlag) {
			if (Constant.isSuccess(result)) {
				GroupAffairModel.getInstance().setGroupMsgStatus(
						Long.parseLong(groupId), newType);
				setResult(RESULT_OK);
				finish();
			} else {
				ErrorCode.showError(GroupMsgSetActivity.this, result);
				finish();
			}
		}
	}

	@Override
	public void onGeneralError(int e, long flag) {
		handler.sendEmptyMessage(MSG_HIDE_WAIT);
	}

	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_HIDE_WAIT: {
				waitDialog.hide();
			}
				break;
			}
		}
    };

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (waitDialog != null)
			waitDialog.dismiss();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			updateStatus();
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener checkBoxClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			for (int i = 0; i < checkBoxes.length; i++)
				checkBoxes[i].setChecked(false);

			((CheckBox) v).setChecked(true);
		}
	};

}
