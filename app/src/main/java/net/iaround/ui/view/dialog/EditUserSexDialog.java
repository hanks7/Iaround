package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.DialogUtil;

/**
 * Class: 编辑用户性别
 * Author：gh
 * Date: 2017/8/22 11:17
 * Email：jt_gaohang@163.com
 */
public class EditUserSexDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private CheckBox womanCheck;
    private CheckBox manCheck;

    private String gender;

    private SureClickListener onClickListener;

    public EditUserSexDialog(Context context, SureClickListener onClickListener) {
        // 更改样式,把背景设置为透明的
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载dialog的布局
        setContentView(R.layout.dialog_edit_user_sex);
        //拿到布局控件进行处理

        womanCheck = (CheckBox) findViewById(R.id.checkbox_woman);
        manCheck = (CheckBox) findViewById(R.id.checkbox_man);

        womanCheck.setOnClickListener(this);
        manCheck.setOnClickListener(this);
        findViewById(R.id.ly_edit_user_sex_man).setOnClickListener(this);
        findViewById(R.id.ly_edit_user_sex_woman).setOnClickListener(this);
        findViewById(R.id.tv_edit_user_sex_cancel).setOnClickListener(this);

        //初始化布局的位置
        initLayoutParams();
    }

    // 初始化布局的参数
    private void initLayoutParams() {
        // 布局的参数

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkbox_man:
            case R.id.ly_edit_user_sex_man:
                gender = "m";
                manCheck.setChecked(true);
                womanCheck.setChecked(false);
                showSureDialog();
                break;
            case R.id.checkbox_woman:
            case R.id.ly_edit_user_sex_woman:
                gender = "f";
                manCheck.setChecked(false);
                womanCheck.setChecked(true);
                showSureDialog();
                break;
            case R.id.tv_edit_user_sex_cancel:
                dismiss();
                break;

        }
    }

    public interface SureClickListener{
        void onSure(String gender);
    }

    private void showSureDialog(){
        String title = BaseApplication.appContext.getString(R.string.dialog_title);
        String message = BaseApplication.appContext.getString(R.string.dialog_edit_user_sex_message);
        DialogUtil.showOKDialog(context, title, message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != gender && !TextUtils.isEmpty(gender)) {
                    if (onClickListener != null) {
                        onClickListener.onSure(gender);
                        dismiss();
                    }
                }

            }
        });


    }



}
