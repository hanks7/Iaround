package net.iaround.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.ui.activity.TitleActivity;

/**
 * 在这里选择好友类别，是：好友、关注还是粉丝
 * 然后跳转至BlackSelectFriendsActivity
 */
public class BlackSelectTypeActivity extends TitleActivity implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvBlackListTips;
    private LinearLayout llBlackListFriends;
    private LinearLayout llBlackListFocus;
    private LinearLayout llBlackListFans;
    private int setType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null, true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = findView(R.id.tv_title);
        setContent(R.layout.activity_add_black);
        tvBlackListTips = findView(R.id.tv_black_list_tips);
        llBlackListFriends = findView(R.id.ll_black_list_friends);
        llBlackListFocus = findView(R.id.ll_black_list_focus);
        llBlackListFans = findView(R.id.ll_black_list_fans);
    }

    private void initDatas() {
        setType = getIntent().getIntExtra(Constants.SECRET_SET_TYPE, 0);
        switch (setType){
            case Constants.SECRET_SET_INVISIBLE:
                tvTitle.setText(getString(R.string.setting_invisiable_myself));
                tvBlackListTips.setText(getString(R.string.setting_invisible));
                break;
            case Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION:
                tvTitle.setText(getString(R.string.setting_invisiable_myself_action));
                tvBlackListTips.setText(getString(R.string.setting_invisiable_myself_action_tips));
                break;
            case Constants.SECRET_SET_INVISIABLE_OTHER_ACTION:
                tvTitle.setText(getString(R.string.setting_invisiable_other_action));
                tvBlackListTips.setText(getString(R.string.setting_invisiable_other_action_tips));
                break;
            case Constants.SECRET_SET_BLACK_LIST:
                tvTitle.setText(getString(R.string.setting_black));
                tvBlackListTips.setText(getString(R.string.setting_black_tips));
                break;
        }
    }

    private void initListeners() {
        llBlackListFriends.setOnClickListener(this);
        llBlackListFocus.setOnClickListener(this);
        llBlackListFans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, BlackSelectFriendsActivity.class);
        switch (v.getId()){
            case R.id.ll_black_list_friends:
                intent.putExtra(Constants.SECRET_SET_TYPE, setType);
                intent.putExtra(Constants.SECRET_SELECT_TYPE, Constants.SECRET_SET_FRIENDS_LIST);
                break;
            case R.id.ll_black_list_focus:
                intent.putExtra(Constants.SECRET_SET_TYPE, setType);
                intent.putExtra(Constants.SECRET_SELECT_TYPE, Constants.SECRET_SET_FOCUS_LIST);
                break;
            case R.id.ll_black_list_fans:
                intent.putExtra(Constants.SECRET_SET_TYPE, setType);
                intent.putExtra(Constants.SECRET_SELECT_TYPE, Constants.SECRET_SET_FANS_LIST);
                break;
        }
        startActivity(intent);
    }
}
