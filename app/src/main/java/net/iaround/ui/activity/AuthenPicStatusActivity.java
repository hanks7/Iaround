package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.BaseEntity;
import net.iaround.tools.GsonUtil;

public class AuthenPicStatusActivity extends TitleActivity implements View.OnClickListener {

    private LinearLayout llAuthenFail;
    private LinearLayout llAuthenSuccess;
    private Button takePic;
    private Button releaseAuthen;

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
        }, getString(R.string.authen_title), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_authen_pic_status);
        llAuthenFail = findView(R.id.ll_authen_status_fail);
        llAuthenSuccess = findView(R.id.ll_authen_status_success);
        takePic = findView(R.id.btn_take_pic);
        releaseAuthen = findView(R.id.btn_release_authen);
    }

    private void initDatas() {
        Boolean authenStatus = getIntent().getBooleanExtra(Constants.AUTHEN_PIC_STATUS, false);
        if(authenStatus){
            llAuthenSuccess.setVisibility(View.VISIBLE);
            llAuthenFail.setVisibility(View.GONE);
        } else {
            llAuthenFail.setVisibility(View.VISIBLE);
            llAuthenSuccess.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        takePic.setOnClickListener(this);
        releaseAuthen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_take_pic:
                Intent startAuthen = new Intent(this, AuthenPicGuideActivity.class);
                startActivity(startAuthen);
                finish();
                break;
            case R.id.btn_release_authen:
                releaseAuthen();
                break;
        }
    }

    private void releaseAuthen() {
//        AuthenHttpProtocol.authenPic(this, 2, null, new HttpStringCallback() {
//
//            @Override
//            public void onGeneralError(String error, Exception e, int id) {
//
//            }
//
//            @Override
//            public void onGeneralSuccess(String result, int id) {
//                BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
//                if(baseEntity.isSuccess()){
//                    setResult(Activity.RESULT_OK);
//                    finish();
//                } else {
//                    Toast.makeText(getActivity(), "网路繁忙请稍后重试", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        });
    }
}
