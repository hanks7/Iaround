package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.iaround.R;

/**
 * 爱心充值提示
 *
 * Created by Administrator on 2017/12/13.
 */

public class RechargeLoveDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private String message;
    private OnRechargeClickListener rechargeClickListener;

    public RechargeLoveDialog(Context context, String message, OnRechargeClickListener listener) {
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        this.message = message;
        this.rechargeClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_recharge_love);

        TextView messafeTv = (TextView)findViewById(R.id.tv_rechaarge_love_message);

        findViewById(R.id.tv_recharge_love_cancel).setOnClickListener(this);
        findViewById(R.id.tv_recharge_love_ok).setOnClickListener(this);

        messafeTv.setText(message == null ? "" : message);

        //初始化布局的位置
        initLayoutParams();
    }


    // 初始化布局的参数
    private void initLayoutParams() {
        // 布局的参数
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1f;
        setCanceledOnTouchOutside(false);
        getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_recharge_love_cancel:
                dismiss();
                break;
            case R.id.tv_recharge_love_ok:
                if (rechargeClickListener != null){
                    rechargeClickListener.onRecharge();
                }
                dismiss();
                break;
        }
    }


    public interface OnRechargeClickListener{
        void onRecharge();
    }
}
