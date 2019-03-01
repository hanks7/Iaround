package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.iaround.R;

/**
 * 登录、注册入口
 * Created by gaohang on 2017/4/26.
 */

public class LoginMainActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.tv_regist).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.tv_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_regist:
                intent = new Intent(this, RegisterNewActivity.class);
                startActivity(intent);
                break;
        }
    }
}
