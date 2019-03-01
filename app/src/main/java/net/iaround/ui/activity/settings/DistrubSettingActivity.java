package net.iaround.ui.activity.settings;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.connector.protocol.AnchorsCertificationProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.ActionBarActivity;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.space.more.PrivateSettingType;

/**
 * 主播免打扰设置
 */
public class DistrubSettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivOpne;
    private ImageView ivClose;

    private String notDisturbKey;// 主播免打扰状态
    private boolean notDisturb;;// 主播免打扰状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrub_setting);

        initViews();
        initData();

    }

    private void initViews() {

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.setting_no_disturb));

        FrameLayout backFl = (FrameLayout) findViewById(R.id.fl_back);
        ImageView backIv = (ImageView) findViewById(R.id.iv_back);
        backFl.setOnClickListener(this);
        backIv.setOnClickListener(this);

        ivOpne = (ImageView)findViewById(R.id.iv_notification_setting_open);
        ivClose = (ImageView)findViewById(R.id.iv_notification_setting_close);

        findViewById(R.id.ly_notification_setting_open).setOnClickListener(this);
        findViewById(R.id.ly_notification_setting_close).setOnClickListener(this);

    }

    private void initData(){

        notDisturbKey = SharedPreferenceUtil.NOT_DISTURB + CommonFunction.getUSERID(this);
        notDisturb = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean( notDisturbKey , true );

        if (notDisturb){
            ivOpne.setImageResource(R.drawable.edit_selected);
            ivClose.setImageResource(R.drawable.white);
        }else {
            ivOpne.setImageResource(R.drawable.white);
            ivClose.setImageResource(R.drawable.edit_selected);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_notification_setting_open:
                ivOpne.setImageResource(R.drawable.edit_selected);
                ivClose.setImageResource(R.drawable.white);
                notDisturb = true;
                break;

            case R.id.ly_notification_setting_close:
                ivOpne.setImageResource(R.drawable.white);
                ivClose.setImageResource(R.drawable.edit_selected);
                notDisturb = false;
                break;
            case R.id.fl_back:
            case R.id.iv_back:
                saveAndUpload();
                finish();
                break;
        }
    }

    private void saveAndUpload( )
    {
        if ( notDisturb != SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean( notDisturbKey , true ) )
        {// 主播免打扰
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean( notDisturbKey , notDisturb );

            AnchorsCertificationProtocol.setAnchorDisturbStatus(mContext,notDisturb ? 1 : 0 ,null);
            CommonFunction.log( "sherlock" ,
                    "receive accost change to " + notDisturb + " , saved" );
        }
    }

    @Override
    public boolean onKeyDown( int keyCode , KeyEvent event )
    {
        if ( keyCode == KeyEvent.KEYCODE_BACK )
        {
            saveAndUpload( );
            finish( );
        }
        return false;
    }

}
