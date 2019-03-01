package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.contract.RegisterContract;
import net.iaround.tools.CommonFunction;

/**
 * Class: 注册页面
 * Author：gh
 * Date: 2016/11/30 21:02
 * Emial：jt_gaohang@163.com
 */
public class RegisterActivity extends TitleActivity implements RegisterContract.View, View.OnClickListener {


    public static final String KEY_HEAD = "head";
    public static final String KEY_NAME = "name";
    public static final String KEY_SEX = "sex";
    public static final String KEY_BIRTHDAY = "birthday";

    private TextView tvTitle;
    
    private LinearLayout lyPhoneAre;
    private TextView tvPhoneNumer;
    private EditText editPhoneNumer;
    private TextView tvPasswordHint;
    private EditText editPhonePassword;
    private TextView btnNext;
    private CheckBox rbRead;
    private TextView btnUserProtocol;

    private String are;
    private String number;
    private String password;
    private String areaCode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        setActionBarTitle(R.string.register_title);
        initView();
        setMonitor();
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.register_title);
        lyPhoneAre = (LinearLayout) findViewById(R.id.ly_register_phone_are);
        tvPhoneNumer = (TextView) findViewById(R.id.tv_register_phone_are);
        editPhoneNumer = (EditText) findViewById(R.id.edit_register_phone_number);
        editPhonePassword = (EditText) findViewById(R.id.tv_register_phone_password);
        tvPasswordHint = (TextView) findViewById(R.id.tv_register_password_hint);
        btnNext = (TextView) findViewById(R.id.btn_register_next);
        rbRead = (CheckBox) findViewById(R.id.rb_register_read);
        btnUserProtocol = (TextView) findViewById(R.id.btn_register_user_protocol);
    }

    @Override
    public void setMonitor() {
        lyPhoneAre.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnUserProtocol.setOnClickListener(this);
        
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);
        

        editPhoneNumer.addTextChangedListener(textWatcher);
        editPhonePassword.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.iv_left:
            case R.id.fl_left:
                finish();
                break;
            case R.id.ly_register_phone_are:
                intent = new Intent(RegisterActivity.this,CountrySelectActivity.class);
                startActivityForResult( intent , 401 );
                break;
            case R.id.btn_register_next:
                // 获取当前区号
                String[ ] spitString = tvPhoneNumer.getText().toString().split( "\\+" );
                areaCode = "+" + spitString[ 1 ];

                number = editPhoneNumer.getText().toString();
                if ( number == null || number.length( ) <= 0 )
                { // 手机号为空
                    Toast.makeText( this , R.string.register_please_enter_number , Toast.LENGTH_SHORT )
                            .show( );
                    return;
                }

                boolean numValid = CommonFunction.isPhoneNumberValid(number);
                if (!numValid){
                    // 手机号非法
                    Toast.makeText( this ,R.string.register_number_invalid , Toast.LENGTH_SHORT )
                            .show();
                    return;
                }

                password = editPhonePassword.getText().toString();
                if ( password == null || password.length( ) <= 0 )
                { // 密码为空
                    Toast.makeText( this , R.string.register_please_enter_password , Toast.LENGTH_SHORT )
                            .show( );
                    return;
                }

                boolean pswValid = CommonFunction.isPassword( password );
                if ( !pswValid )
                { // 密码非法
                    tvPasswordHint.setVisibility(View.VISIBLE);
                    return;
                }else{
                    tvPasswordHint.setVisibility(View.INVISIBLE);
                }



                intent = new Intent(RegisterActivity.this,RegisterVerActivity.class);
                intent.putExtra(RegisterVerActivity.COUNTRY_ARE,areaCode);
                intent.putExtra(RegisterVerActivity.NUMBER,number);
                intent.putExtra(RegisterVerActivity.PASSWORD,password);
                intent.putExtra(RegisterActivity.KEY_HEAD,getIntent().getStringExtra(RegisterActivity.KEY_HEAD));
                intent.putExtra(RegisterActivity.KEY_NAME,getIntent().getStringExtra(RegisterActivity.KEY_NAME));
                intent.putExtra(RegisterActivity.KEY_BIRTHDAY,getIntent().getStringExtra(RegisterActivity.KEY_BIRTHDAY));
                intent.putExtra(RegisterActivity.KEY_SEX,getIntent().getIntExtra(RegisterActivity.KEY_SEX,-1));
                startActivity(intent);
                break;
            case R.id.btn_register_user_protocol://点击遇见用户协议
                // TODO: 2017/5/2 跳转到遇见用户协议
                Intent i = new Intent( this , WebViewAvtivity.class );
                i.putExtra( WebViewAvtivity.WEBVIEW_TITLE , getString( R.string.iaround_user_agreement_title ) );
                i.putExtra( WebViewAvtivity.WEBVIEW_URL , Config.USER_AGREEMENT_URL );
                startActivity( i );
                break;
        }
    }

    @Override
    protected void onActivityResult( int requestCode , int resultCode , Intent data )
    {
        if ( requestCode == 401 && data != null )
        {
            are = data.getStringExtra( "area" );
            if ( !CommonFunction.isEmptyOrNullStr( are ) && are.contains( "+" ) )
            {
                // 清空原来输入的手机号
                editPhoneNumer.setText( "" );

                String[ ] spitString = are.split( "\\+" );
                areaCode = "+" + spitString[ 1 ];
                tvPhoneNumer.setText(are);
                if ( spitString[ 1 ].equals( "+86" ) )
                {
                    // 大陆手机限制11位输入
                    editPhoneNumer.setFilters( new InputFilter[ ]
                            { new InputFilter.LengthFilter( 11 ) } );
                }
                else
                {
                    editPhoneNumer.setFilters( new InputFilter[ ]
                            { new InputFilter.LengthFilter( 50 ) } );
                }
                updateContent();
            }
        }
    }

    // EditText监听
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateContent();
        }
    };

    private void updateContent(){

        String password = editPhonePassword.getText().toString();
        String phone = tvPhoneNumer.getText().toString();
        String number = editPhoneNumer.getText().toString().replace(phone,"");
        if (number.length() > 0 && password.length() > 0 && phone.length() > 0){
            btnNext.setEnabled(true);
        }else{
            btnNext.setEnabled(false);
        }
    }

}
