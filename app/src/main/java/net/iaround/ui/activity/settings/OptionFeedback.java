package net.iaround.ui.activity.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.TitleActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OptionFeedback extends TitleActivity implements View.OnClickListener , HttpCallBack{

    private EditText etFeedback;
    private List<ImageView> feedbackTypes = new ArrayList<>();
    private int currentType = 1;// 1、咨询 2、投诉 3、建议
    private LinearLayout llConsult;
    private ImageView ivConsult;
    private LinearLayout llComplaint;
    private ImageView ivComplaint;
    private LinearLayout llSuggest;
    private ImageView ivSuggest;
    private TextView tvRight;
    private long SEND_FLAG;
    private final int SEND_SUCCESS = 1001;
    private final int SEND_ERROR = 1002;
    private Dialog dialog;

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
        }, getString(R.string.setting_feed_back), false, R.drawable.icon_publish, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/4/10 暂时未设置点击事件
                feedBack();
            }
        });
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvRight = findView(R.id.tv_right);
        tvRight.setTextColor(0xFFFF4064);
        setContent(R.layout.activity_option_feedback);
        etFeedback = findView(R.id.et_feedback);
        llConsult = findView(R.id.ll_consult);
        ivConsult = findView(R.id.iv_consult);
        feedbackTypes.add(ivConsult);
        llComplaint = findView(R.id.ll_complaint);
        ivComplaint = findView(R.id.iv_complaint);
        feedbackTypes.add(ivComplaint);
        llSuggest = findView(R.id.ll_suggest);
        ivSuggest = findView(R.id.iv_suggest);
        feedbackTypes.add(ivSuggest);
    }

    private void initDatas() {
        ivConsult.setSelected(true);
    }

    private void initListeners() {
        tvRight.setOnClickListener(this);
        llConsult.setOnClickListener(this);
        llComplaint.setOnClickListener(this);
        llSuggest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_consult:
                changeFeebackType(1);
                break;
            case R.id.ll_complaint:
                changeFeebackType(2);
                break;
            case R.id.ll_suggest:
                changeFeebackType(3);
                break;
        }
    }

    private void feedBack() {
        String feedBackMsg = etFeedback.getText().toString();

         if ( currentType != 0 ){
             if(TextUtils.isEmpty(feedBackMsg)){
                 Toast.makeText(this, getString(R.string.setting_check_feedback_text_null), Toast.LENGTH_SHORT).show();
             } else {
                 dialog = DialogUtil.showProgressDialog(this, "",
                         getResString(R.string.please_wait), null);
                 SEND_FLAG = LoginHttpProtocol.systemFeedBack(this, feedBackMsg, currentType, this);
                 if (SEND_FLAG == -1) {
                     dialog.dismiss();
                 }
             }
         } else {
             CommonFunction.showToast( this ,
                     getResString( R.string.chose_back_type ) , 0 );
         }
    }

    public void changeFeebackType(int type){
        feedbackTypes.get(currentType - 1 ).setSelected(false);
        feedbackTypes.get(type - 1).setSelected(true);
        currentType = type;
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if ( SEND_FLAG == flag )
        {
            Message msg = new Message( );
            msg.what = SEND_SUCCESS;
            msg.obj = result;
            mHandler.sendMessage( msg );
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        ErrorCode.toastError( mContext , e );
        if ( SEND_FLAG == flag )
        {
            mHandler.sendEmptyMessage( SEND_ERROR );
        }
    }

    private Handler mHandler = new Handler( )
    {
        @Override
        public void handleMessage( Message msg )
        {
            super.handleMessage( msg );
            switch ( msg.what )
            {
                case SEND_ERROR :
                    dialog.dismiss( );

                    break;
                case SEND_SUCCESS :
                    dialog.dismiss( );
                    try
                    {
                        String result = String.valueOf( msg.obj );
                        JSONObject json = new JSONObject( result );
                        if ( json.optInt( "status" ) == 200 )
                        {
                            etFeedback.setText( "" );
                            Toast.makeText( OptionFeedback.this ,
                                    getResString( R.string.feedback_success ) ,
                                    Toast.LENGTH_SHORT ).show( );
                            finish();
                        }
                        else
                        {
                            if ( json.has( "error" ) )
                            {
                                ErrorCode.showError( OptionFeedback.this , result );
                            }
                        }
                    }
                    catch ( JSONException e )
                    {
                        e.printStackTrace( );
                    }
                    break;
            }
        }
    };
}
