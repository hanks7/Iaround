package net.iaround.ui.activity.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.TitleActivity;

import java.util.HashMap;

public class ChangePwdActivity extends TitleActivity implements View.OnClickListener ,HttpCallBack{

    private static final int MSG_MODIFY_PASSWORD = 1;
    private EditText tvOriginPwd;
    private EditText tvNewPwd;
    private EditText tvConfirmPwd;
    private Button btnConfirmChange;
    private TextView tvCheckPwdHint;

    private Dialog pd;

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
        }, getString(R.string.setting_change_pwd), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_change_pwd);
        tvOriginPwd = findView(R.id.et_origin_pwd);
        tvNewPwd = findView(R.id.et_new_pwd);
        tvConfirmPwd = findView(R.id.et_confirm_pwd);
        btnConfirmChange = findView(R.id.btn_confirm_change);
        tvCheckPwdHint = findView(R.id.tv_register_password_hint);
    }

    private void initDatas() {

    }

    private void initListeners() {
        btnConfirmChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm_change:
                btnConfirmChange();
                break;
        }
    }
    private void btnConfirmChange() {
        if ( pd != null && pd.isShowing( ) )
        {
            pd.dismiss( );
        }
        String originPwd = tvOriginPwd.getText().toString();
        if(TextUtils.isEmpty(originPwd)){
            Toast.makeText(this, getString(R.string.setting_change_pwd_origin_tips), Toast.LENGTH_SHORT).show();
            return;
        }

        String newPwd = tvNewPwd.getText().toString();
        if(TextUtils.isEmpty(newPwd)){
            Toast.makeText(this, getString(R.string.setting_change_pwd_new_tips), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean pswValid = CommonFunction.isPassword( newPwd );

        String confirmPwd = tvConfirmPwd.getText().toString();
        if(TextUtils.isEmpty(confirmPwd)){
            Toast.makeText(this, getString(R.string.setting_change_pwd_new_tips), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!confirmPwd.equals(newPwd)){
            Toast.makeText(this, getString(R.string.setting_change_pwd_confirm_tips), Toast.LENGTH_SHORT).show();
            return;
        }
        if ( !pswValid )
        { // 密码非法
            tvCheckPwdHint.setVisibility(View.VISIBLE);
            return;
        }else{
            tvCheckPwdHint.setVisibility(View.INVISIBLE);
        }
        if (originPwd.equals(newPwd))
        {
            Toast.makeText(ChangePwdActivity.this, "新密码和原始密码一样，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        pd = DialogUtil.showProgressDialog( this , R.string.modify_psw , R.string.please_wait ,
                null );
        // 发送请求
        try
        {
            SpaceModelNew.getInstance( this ).modifyPasswordReq( this , originPwd , newPwd ,
                    this );
        }
        catch ( Throwable t )
        {
            CommonFunction.log( t );
            handler.sendEmptyMessage( 0 );
            Toast.makeText( this , R.string.network_req_failed , Toast.LENGTH_SHORT ).show( );

        }


        //yuchao 重复调用了修改密码的接口
//        pd = DialogUtil.showProgressDialog( this , R.string.modify_psw , R.string.please_wait ,
//                null );
        // 发送请求
//        try
//        {
//            UserHttpProtocol.userPasswordUpdate( this , originPwd , newPwd ,
//                    this );
//        }
//        catch ( Throwable t )
//        {
//            CommonFunction.log( t );
//            handler.sendEmptyMessage( 0 );
//            Toast.makeText( this , R.string.network_req_failed , Toast.LENGTH_SHORT ).show( );
//
//        }

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        HashMap< String , Object > resMap = null;
        try
        {
            resMap = SpaceModel.getInstance( this ).getRes( result , flag );
        }
        catch ( Throwable t )
        {
            CommonFunction.log( t );
        }

        if ( resMap != null )
        {
            Object reqType = resMap.get( "reqType" );
            if ( SpaceModelReqTypes.MODIFY_PASSWORD.equals( reqType ) )
            {
                Integer status = ( Integer ) resMap.get( "status" );
                Message msg = new Message( );
                msg.what = MSG_MODIFY_PASSWORD;
                if ( status != null && status == 200 )
                { // 成功
                    msg.arg1 = 1;
                }
                else
                {
//                    msg.obj = resMap.get( "error" );
                    msg.obj = result;//yuchao
                }
                handler.sendMessage( msg );
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ( pd != null && pd.isShowing( ) )
            {
                pd.dismiss( );
            }
            switch ( msg.what )
            {
                case MSG_MODIFY_PASSWORD :
                {
                    if ( msg.arg1 == 1 )
                    { // 成功
                        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( ChangePwdActivity.this );
                        String password = tvNewPwd.getText( ).toString( );
                        sp.putString( SharedPreferenceUtil.LOGIN_PASSWORD , CryptoUtil.md5( password ) );
                        Toast.makeText( ChangePwdActivity.this , R.string.change_psw_success , Toast.LENGTH_LONG ).show( );
                        finish( );
                    }
                    else
                    {
//                        String message = (String) msg.obj;//ClassCastException
                        String message = String.valueOf(msg.obj);
                        if ( message != null && message.trim( ).length( ) > 0 )
                        {
                            ErrorCode.showError( ChangePwdActivity.this , message );
                        }
                    }
                }
                break;
            }
        }
    };
}
