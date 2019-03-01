package net.iaround.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.GuideActivity;
import net.iaround.ui.activity.SplashActivity;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.activity.WebViewAvtivity;

public class AboutIaroundActivity extends TitleActivity implements View.OnClickListener {

    private LinearLayout llCommonQuestion;
    private LinearLayout llContactOur;
    private LinearLayout llWelcome;
    private LinearLayout llAgreement;
    //开启调试模式
    private ImageView llCommonIcon;
    private long iconClickCount = 0;
    private long lastClickTime = 0;

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
        }, getString(R.string.setting_about), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_about);
        llCommonIcon = findView(R.id.ll_common_icon);
        llCommonQuestion = findView(R.id.ll_common_question);
        llContactOur = findView(R.id.ll_contact_our);
        llWelcome = findView(R.id.ll_welcome);
        llAgreement = findView(R.id.ll_agreement);
    }

    private void initDatas() {

    }

    private void initListeners() {
        llCommonQuestion.setOnClickListener(this);
        llContactOur.setOnClickListener(this);
        llWelcome.setOnClickListener(this);
        llCommonIcon.setOnClickListener(this);
        llAgreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.ll_common_question://常见问题搜索
                intent.setClass(this, WebViewAvtivity.class);
                intent.putExtra( WebViewAvtivity.WEBVIEW_TITLE ,
                        this.getString( R.string.setting_about_comment_question ) );
                intent.putExtra( WebViewAvtivity.WEBVIEW_URL , Constants.iAroundFAQUrl );
                startActivity(intent);
                break;
            case R.id.ll_contact_our://联系我们
                intent.setClass(this, ContactOurActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_welcome:
                intent.setClass(this,GuideActivity.class);
                intent.putExtra("class_Actio",true);
                startActivity(intent);
                break;
            case R.id.ll_common_icon: //连续点击6次开启BUG模式 会有日志输出
                openDebug();
                break;
            case R.id.ll_agreement:
                intent.setClass(this, WebViewAvtivity.class);
                intent.putExtra( WebViewAvtivity.WEBVIEW_TITLE ,
                        this.getString( R.string.iaround_user_agreement_title ) );
                intent.putExtra( WebViewAvtivity.WEBVIEW_URL , Config.USER_AGREEMENT_URL );
                startActivity(intent);
                break;
        }
    }

    /* 开始调试模式逻辑
    * */
    private void openDebug(){
        long current = System.currentTimeMillis();
        if(lastClickTime!=0){
            if( (current-lastClickTime)<=400 ){
                iconClickCount++;
                if(iconClickCount>=6){
                    iconClickCount = 0;
                    boolean debug = SharedPreferenceUtil.getInstance(this).getBoolean(SharedPreferenceUtil.APP_DEBUG_SWITCH);
                    if(false==debug){
                        SharedPreferenceUtil.getInstance(this).putBoolean(SharedPreferenceUtil.APP_DEBUG_SWITCH, true);
                        Config.DEBUG = true;
                        Toast.makeText(this,"您已经在调试模式下",Toast.LENGTH_LONG).show();
                    }else{
                        SharedPreferenceUtil.getInstance(this).putBoolean(SharedPreferenceUtil.APP_DEBUG_SWITCH, false);
                        Config.DEBUG = false;
                        Toast.makeText(this,"调试模式已经关闭",Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                iconClickCount = 0;
            }
            lastClickTime = current;
        }else{
            lastClickTime = current;
            iconClickCount = 0;
            iconClickCount++;
        }
    }
}
