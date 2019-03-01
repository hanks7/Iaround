package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.FaceManager;

/**
 * Class: 收到召唤
 * Author：gh
 * Date: 2017/8/22 11:17
 * Email：jt_gaohang@163.com
 */
public class ReceivedCallDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private SureClickListener onClickListener;
   // type = 0招募广播  type = 1是组长召唤
    private String group;
    private String content;

    public ReceivedCallDialog(Context context, String groupName, String content, SureClickListener onClickListener) {
        // 更改样式,把背景设置为透明的
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        this.group = groupName;
        this.content = content;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载dialog的布局
        setContentView(R.layout.dialog_received_call);
        //拿到布局控件进行处理
        TextView titleTv = (TextView) findViewById(R.id.tv_dialog_title);
        TextView groupNameTv = (TextView) findViewById(R.id.tv_dialog_received_call_group_name);
        ImageView typeIv = (ImageView) findViewById(R.id.iv_recruit_and_call_type);
        TextView contentlTv = (TextView) findViewById(R.id.edt_dialog_received_call_content);
        TextView refuseTv = (TextView) findViewById(R.id.tv_dialog_received_call_refuse);
        TextView agreeTv = (TextView) findViewById(R.id.tv_dialog_received_call_agree);

        titleTv.setText(context.getResources().getString(R.string.dialog_received_recruit_title));
        typeIv.setImageResource(R.drawable.dialog_received_call_icon);
        groupNameTv.setText(String.format(context.getResources().getString(R.string.dialog_received_call_group_name),group));
        SpannableString contentStr = FaceManager.getInstance(getContext()).parseIconForString(getContext(), content,
                0, null);
        contentlTv.setText(contentStr);

        agreeTv.setOnClickListener(this);
        refuseTv.setOnClickListener(this);
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
            case R.id.tv_dialog_received_call_agree:
                if (onClickListener != null){
                    onClickListener.onAgree();
                }
                break;
        }
        dismiss();
    }

    public interface SureClickListener{
        void onAgree();
    }

}
