
package net.iaround.ui.space.more;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.comon.SuperActivity;


public class BindTelphoneForTwo extends SuperActivity implements OnClickListener {
    private int type = -1;
    private String phone = "";
    private TextView title_name;
    private ImageView ivLeft;
    private FrameLayout flLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bindtelphone_for_two);
        Common.getInstance().addBindActivity(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        phone = intent.getStringExtra("phone");
        initView();
    }

    private void initView() {
        title_name = (TextView) findViewById(R.id.tv_title);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        title_name.setText(R.string.space_bind_mobile_desc2);
        TextView tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvPhone.setText(phone);
        findViewById(R.id.replace_telphone).setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left: {
                finish();
            }
            break;
            case R.id.replace_telphone: {
                Intent intent = new Intent(mContext, EnterPwdActivity.class);
                intent.putExtra("type", type);// （已绑定）更换手机号
                mContext.startActivity(intent);
                // finish();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        CommonFunction.hideInputMethod(mContext, title_name);
        super.onResume();
    }
}
