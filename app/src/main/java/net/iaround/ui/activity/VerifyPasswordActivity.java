package net.iaround.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.VerifyPasswordProtocol;
import net.iaround.model.entity.BaseEntity;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;

public class VerifyPasswordActivity extends TitleActivity implements View.OnClickListener {

    private EditText etPassword;
    private Button btnConfirm;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intViews();
        initDatas();
        initListeners();

    }

    private void intViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.check_password), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = findView(R.id.tv_title);
        setContent(R.layout.activity_verify_password);
        etPassword = findView(R.id.et_password);
        btnConfirm = findView(R.id.btn_confirm);
    }

    private void initDatas() {

    }

    private void initListeners() {
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_confirm:
                confirmPwd();
                break;
        }
    }

    private void confirmPwd() {
        String pwd = etPassword.getText().toString();
        if(!TextUtils.isEmpty(pwd)){

            VerifyPasswordProtocol.verifyPassword(this,pwd, new HttpCallBack(){
                @Override
                public void onGeneralError(int e, long flag) {

                }

                @Override
                public void onGeneralSuccess(String result, long flag) {
                    CommonFunction.log("xiaohua", "chekout result = " + result);
                    BaseServerBean baseEntity = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
                    if(baseEntity.isSuccess()){
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        ErrorCode.showError(getActivity(), result);
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.authen_pwd_check_null), Toast.LENGTH_SHORT).show();
        }
    }

}
