package net.iaround.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.glide.GlideUtil;

public class MatchSucActivity extends BaseActivity {

    private TextView tvOtherNickname;
    private ImageView ivMyselfHead;
    private ImageView ivOtherHead;
    private Button btnContinueMatches;
    private Button btnStartChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_suc);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        tvOtherNickname = (TextView) findViewById(R.id.tv_other_nickname);
        ivMyselfHead = (ImageView) findViewById(R.id.iv_myself_head);
        ivOtherHead = (ImageView) findViewById(R.id.iv_other_head);
        btnContinueMatches = (Button) findViewById(R.id.btn_continue_match);
        btnStartChat = (Button) findViewById(R.id.btn_start_chat);
    }

    private void initDatas() {
        /*intent.putExtra(Constants.OTHER_UID, otherUid);
        intent.putExtra(Constants.OTHER_HEAD_PIC, otherHeadPic);
        intent.putExtra(Constants.OTHER_NICKNAME, nickname);*/
        long otherUid = getIntent().getLongExtra(Constants.OTHER_UID, 0);
        String otherNickname = getIntent().getStringExtra(Constants.OTHER_NICKNAME);
        tvOtherNickname.append(otherNickname);
        String otherHeadpic = getIntent().getStringExtra(Constants.OTHER_HEAD_PIC);
        GlideUtil.loadImage(BaseApplication.appContext, SharedPreferenceUtil.getInstance(this).getString(SharedPreferenceUtil.USER_AVATAR), ivMyselfHead, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);
        GlideUtil.loadImage(BaseApplication.appContext, otherHeadpic, ivOtherHead, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);
    }

    private void initListeners() {
        btnContinueMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MatchSucActivity.this, "跳转至聊天界面", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
