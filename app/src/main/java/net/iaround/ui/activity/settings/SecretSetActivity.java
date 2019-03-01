package net.iaround.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.ui.activity.TitleActivity;

public class SecretSetActivity extends TitleActivity implements View.OnClickListener {

    private TextView tvInvisiableMyself;
    private TextView tvInvisiableOther;
    private TextView tvBlack;
    private TextView tvInvisibleSetting;//隐身设置

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
        }, getString(R.string.setting_secret_setting), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_secret_set);
        tvInvisiableMyself = findView(R.id.tv_invisiable_myself);
        tvInvisiableOther = findView(R.id.tv_invisiable_other);
        tvBlack = findView(R.id.tv_black);
        tvInvisibleSetting = findView(R.id.tv_invisible_setting);
    }

    private void initDatas() {

    }

    private void initListeners() {
        tvInvisiableMyself.setOnClickListener(this);
        tvInvisiableOther.setOnClickListener(this);
        tvBlack.setOnClickListener(this);
        tvInvisibleSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, BlackEditActivity.class);
        switch (v.getId()){
            case R.id.tv_invisible_setting://隐身设置
                // TODO: 2017/5/4 处理隐身设置
                intent.putExtra(Constants.SECRET_SET_TYPE,Constants.SECRET_SET_INVISIBLE);
                break;
            case R.id.tv_invisiable_myself:
                intent.putExtra(Constants.SECRET_SET_TYPE, Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION);
                break;
            case R.id.tv_invisiable_other:
                intent.putExtra(Constants.SECRET_SET_TYPE, Constants.SECRET_SET_INVISIABLE_OTHER_ACTION);
                break;
            case R.id.tv_black:
                intent.putExtra(Constants.SECRET_SET_TYPE, Constants.SECRET_SET_BLACK_LIST);
                break;
        }
        startActivity(intent);
    }
}
