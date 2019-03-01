package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;

/**
 * Class: 招募和召唤
 * Author：gh
 * Date: 2017/8/22 11:17
 * Email：jt_gaohang@163.com
 */
public class RecruitAndCallDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private EditText diamondsEdt;
    private SureClickListener onClickListener;
   // type = 0招募广播  type = 1是组长召唤
    private int type;

    public RecruitAndCallDialog(Context context, int type, SureClickListener onClickListener) {
        // 更改样式,把背景设置为透明的
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        this.type = type;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载dialog的布局
        setContentView(R.layout.dialog_recruit_and_call);
        //拿到布局控件进行处理
        TextView titleTv = (TextView) findViewById(R.id.tv_dialog_title);
        ImageView typeIv = (ImageView) findViewById(R.id.iv_recruit_and_call_type);
        TextView cancelTv = (TextView) findViewById(R.id.tv_dialog_recruit_and_call_cancel);
        RelativeLayout callSureRl = (RelativeLayout) findViewById(R.id.rl_dialog_recruit_and_call_sure);
        TextView diamondsTv = (TextView) findViewById(R.id.tv_dialog_recruit_and_call_diamonds);
        diamondsEdt = (EditText) findViewById(R.id.edt_dialog_recruit_and_call_content);

        if (type == 0){
            titleTv.setText(context.getResources().getString(R.string.dialog_recruit_title));
//            diamondsEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            typeIv.setImageResource(R.drawable.dialog_recruit_icon);
            diamondsEdt.setHint(R.string.dialog_call_hint);
            diamondsTv.setVisibility(View.VISIBLE);

        }else {
//            diamondsEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            typeIv.setImageResource(R.drawable.dialog_call_icon);
            titleTv.setText(context.getResources().getString(R.string.dialog_call_title));
            diamondsEdt.setHint(R.string.dialog_recruit_hint);
            diamondsTv.setVisibility(View.GONE);
        }


        diamondsEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = diamondsEdt.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);

                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，

                //如果是汉字则为两个字符

                for (int i = 0; i < str.length(); i++) {

                    char charAt = str.charAt(i);

                    //32-122包含了空格，大小写字母，数字和一些常用的符号，

                    //如果在这个范围内则算一个字符，

                    //如果不在这个范围比如是汉字的话就是两个字符

                    if (charAt >= 32 && charAt <= 122) {

                        mTextMaxlenght++;

                    } else {

                        mTextMaxlenght += 2;

                    }

                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小
                    int maxLength = type == 0 ? 60 : 100;
                    if (mTextMaxlenght > maxLength) {

                        // 截取最大的字段

                        String newStr = str.substring(0, i);

                        diamondsEdt.setText(newStr);

                        // 得到新字段的长度值

                        editable = diamondsEdt.getText();

                        int newLen = editable.length();

                        if (selEndIndex > newLen) {

                            selEndIndex = editable.length();

                        }

                        // 设置新光标所在的位置

                        Selection.setSelection(editable, selEndIndex);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        callSureRl.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
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
            case R.id.rl_dialog_recruit_and_call_sure:
                if (onClickListener != null){
                    String content = diamondsEdt.getText().toString();
                    if (TextUtils.isEmpty(content.trim())){
                        CommonFunction.toastMsg(context,context.getResources().getString(R.string.e_4001));
                        return;
                    }
                    onClickListener.onSure(content);
                }
                if (type == 1){
                    dismiss();
                }
                break;
            case R.id.tv_dialog_recruit_and_call_cancel:
                dismiss();
                break;
        }
    }

    public interface SureClickListener{
        void onSure(String content);
    }

}
