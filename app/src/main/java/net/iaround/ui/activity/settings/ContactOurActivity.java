package net.iaround.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.activity.WebViewAvtivity;

public class ContactOurActivity extends TitleActivity implements View.OnClickListener {

    private LinearLayout llVisitWebsite;
    private LinearLayout llIaroundWechat;
    private LinearLayout llServiceEmail;

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
        }, getString(R.string.setting_about_connect_our), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_contact_our);
        llVisitWebsite = findView(R.id.ll_visit_website);
        llIaroundWechat = findView(R.id.ll_iaround_wechat);
        llServiceEmail = findView(R.id.ll_service_email);

    }

    private void initDatas() {
        if (Config.isShowGoogleApp){
            llVisitWebsite.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        llVisitWebsite.setOnClickListener(this);
        llIaroundWechat.setOnClickListener(this);
        llServiceEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.ll_visit_website:
                intent.setClass(this, WebViewAvtivity.class);
                intent.putExtra( WebViewAvtivity.WEBVIEW_TITLE ,getString( R.string.iaround_official_website ) );
                intent.putExtra( WebViewAvtivity.WEBVIEW_URL , Constants.IAROUND_WEBSITE);
                startActivity(intent);
                break;
            case R.id.ll_iaround_wechat:
                try
                {
                    Intent.parseUri(
                                    "#Intent;action=gh_40aec2ade344;component=com.tencent.mm/.ui.LauncherUI;B.LauncherUI_From_Biz_Shortcut=true;end" ,
                                    0x0 );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(intent);
                    return;
                }
                catch ( Exception e )
                {
                    e.printStackTrace( );
                    Toast.makeText( this , R.string.attention_weixin_error ,
                            Toast.LENGTH_LONG ).show( );
                }
                break;
            case R.id.ll_service_email:
                CommonFunction.sendEmail(Constants.SERVICE_EMAIL ,getString(R.string.setting_contact_our_share), this);
                break;
        }
    }
}
