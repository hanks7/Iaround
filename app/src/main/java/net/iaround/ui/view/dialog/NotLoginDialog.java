package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.RegisterNextActivity;

/**
 * Class: 未登录
 * Author：gh
 * Date: 2017/1/9 16:39
 * Email：jt_gaohang@163.com
 */
public class NotLoginDialog extends Dialog{

    private Context mContext;

    private TextView title;
    private TextView content;
    private TextView cancel;
    private TextView quickRegister;

//    private static NotLoginDialog instance = null;

    public static NotLoginDialog getInstance(Context context) {
        /*if (instance == null) {
            instance = new NotLoginDialog(context);
        }*/
//        instance = new NotLoginDialog(context);
        return new NotLoginDialog(context);
    }

    private NotLoginDialog(Context context) {
        super(context,R.style.transparent_dialog);
        this.mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_not_login);

        title = (TextView)findViewById(R.id.tv_dialog_title);
        content = (TextView)findViewById(R.id.tv_dialog_content);
        cancel = (TextView)findViewById(R.id.tv_not_login_cacel);
        quickRegister = (TextView)findViewById(R.id.tv_not_login_quick);

        title.setText(mContext.getString(R.string.not_login_title));
        content.setText(mContext.getString(R.string.not_login_content));
        cancel.setText(mContext.getString(R.string.not_login_cancel));
        quickRegister.setText(mContext.getString(R.string.not_login_quick));

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager m = window.getWindowManager( );
        Display d = m.getDefaultDisplay( );
        WindowManager.LayoutParams p = window.getAttributes( ); // 获取对话框当前的参数值
        p.height = ( int ) ( d.getWidth( ) * 0.5 );
        p.width = ( int ) ( d.getWidth( ) * 0.8 );
        window.setAttributes( p );
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        quickRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterNextActivity.class);
                mContext.startActivity(intent);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

    }

    /**
     * 是否登录
     * @return
     */
    public boolean isLogin(){
//        String accessToken = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.ACCESS_TOKEN);
//        if (TextUtils.isEmpty(accessToken)){
//            show();
//            return false;
//        }
        return true;
    }

}
