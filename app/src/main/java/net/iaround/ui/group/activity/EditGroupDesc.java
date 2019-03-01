package net.iaround.ui.group.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.activity.TitleActivity;

public class EditGroupDesc extends TitleActivity implements View.OnClickListener {

    //标题栏
    private ImageView mIvLeft;//返回键
    private FrameLayout flLeft;//返回键所在布局
    private TextView mTvTitle;//标题

    //控件
    private EditText mEtGroupDesc;
    private ImageView mIvFaceMenu;
    private TextView mTvDescTextNum;
    private TextView mTvSave;
    /**isFrom     0 编辑聊吧介绍   1 编辑欢迎语*/
    private int isFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initActionBar();
        initDatas();
        initListener();
    }

    private void initActionBar() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mIvLeft.setVisibility(View.VISIBLE);


        mIvLeft.setImageResource(R.drawable.title_back);
    }

    private void initView() {
        setContentView(R.layout.activity_edit_group_desc);

        mEtGroupDesc = (EditText) findViewById(R.id.et_group_desc);
        mIvFaceMenu = (ImageView) findViewById(R.id.group_face_menu);
        mTvDescTextNum = (TextView) findViewById(R.id.group_desc_num);
        mTvSave = (TextView) findViewById(R.id.tv_right);
        mTvSave.setVisibility(View.VISIBLE);
        mTvSave.setText(getResources().getString(R.string.pay_alipay_Ensure));

        mEtGroupDesc.setFocusable(true);
    }

    private void initListener() {
        mIvLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        mIvFaceMenu.setOnClickListener(this);
        mEtGroupDesc.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }

    private void initDatas() {
        Intent intent = getIntent();
        String groupDesc = intent.getStringExtra(GroupEditActivity.EDIT_GROUP_DESC);
        String groupWel = intent.getStringExtra(GroupEditActivity.EDIT_GROUP_WEL);
        isFrom = intent.getIntExtra("isFrom",0);
        if (isFrom == 1)
        {
            mTvTitle.setText(getResString(R.string.chatbar_welcome));
            if (!TextUtils.isEmpty(groupWel)) {
                mEtGroupDesc.setText(groupWel);
                mEtGroupDesc.setSelection(groupWel.length());
                mTvDescTextNum.setText(groupWel.length() + "");
            } else if (TextUtils.isEmpty(groupWel) || "".equals(groupWel)) {
                mEtGroupDesc.setHint(getResString(R.string.group_edit_groupWel));
                mTvDescTextNum.setVisibility(View.INVISIBLE);
            }
        }else
        {
            mTvTitle.setText(getResources().getString(R.string.group_description));
            if (!TextUtils.isEmpty(groupDesc)) {
                mEtGroupDesc.setText(groupDesc);
                mEtGroupDesc.setSelection(groupDesc.length());
                mTvDescTextNum.setText(groupDesc.length() + "");
            } else if (TextUtils.isEmpty(groupDesc) || "".equals(groupDesc)) {
                mEtGroupDesc.setHint(getResources().getString(R.string.group_edit_groupDesc));
                mTvDescTextNum.setVisibility(View.INVISIBLE);
//                mTvDescTextNum.setText(groupDesc.length() + "");
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            finish();
            onBackFinish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void onBackFinish() {
        Intent intent = new Intent();
        if (isFrom == 0) {
            String groupDesc = mEtGroupDesc.getText().toString();
            intent.putExtra(GroupEditActivity.EDIT_GROUP_DESC, groupDesc);
        }else if (isFrom == 1)
        {
            String groupwel = mEtGroupDesc.getText().toString();
            intent.putExtra(GroupEditActivity.EDIT_GROUP_WEL, groupwel);
        }
        setResult(RESULT_OK, intent);
        hiddenKeyBoard(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                //返回键
                onBackFinish();
//                finish();
                break;
            case R.id.group_face_menu:
                // TODO: 2017/4/20 跳转到表情选择界面 目前没有此功能
                break;
            case R.id.tv_right:
                onBackFinish();
                break;
            default:
                break;
        }
    }
}
