
package net.iaround.ui.comon;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;


/**
 * @author tanzy
 * @Description:上下两按钮的对话框
 * @date 2014-9-28
 */
public class UpDownTwoButtomDialog extends Dialog {
    private Context mContext;

    private TextView mTitleName;
    private TextView mDialogContent;
    private TextView mBtnUp;
    private ImageView mBtnDivider;
    private TextView mBtnDown;

    /**
     * upable 和 downable只用于显示，具体能否点击在onclick中控制
     */
    public UpDownTwoButtomDialog(Context context, String titleText, String content,
                                 String upButtonText, String downButtonText,
                                 android.view.View.OnClickListener upOnClick,
                                 android.view.View.OnClickListener downOnClick) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(titleText, content, upButtonText, downButtonText, true, true);
        setListeners(upOnClick, downOnClick);
    }

    /**
     * upable 和 downable只用于显示，具体能否点击在onclick中控制
     */
    public UpDownTwoButtomDialog(Context context, String titleText, String content,
                                 String upButtonText, String downButtonText, boolean upAble, boolean downAble,
                                 android.view.View.OnClickListener upOnClick,
                                 android.view.View.OnClickListener downOnClick) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(titleText, content, upButtonText, downButtonText, upAble, downAble);
        setListeners(upOnClick, downOnClick);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(String title, String content, String upText, String downText,
                           boolean upAble, boolean downAble) {
        View dialogView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_up_down_two_button, null);
        mTitleName = (TextView) dialogView.findViewById(R.id.dialog_title);
        mTitleName.setText(title);
        mDialogContent = (TextView) dialogView.findViewById(R.id.dialog_content);
        if (CommonFunction.isEmptyOrNullStr(content)) {
            mDialogContent.setVisibility(View.GONE);
        } else {
            mDialogContent.setText(FaceManager.getInstance(mContext).parseIconForString(
                    mDialogContent, mContext, content.toString(), 16));
        }
        mBtnUp = (TextView) dialogView.findViewById(R.id.btn_up);
        mBtnUp.setText(upText);

        mBtnDivider = (ImageView) dialogView.findViewById(R.id.dialog_btn_devider);
        mBtnDown = (TextView) dialogView.findViewById(R.id.btn_down);

        if (CommonFunction.getLang(mContext) == 3) {
            // 中文繁体不加粗

        } else {
            // 其他字体加粗
            if(mBtnDown != null){
                TextPaint paint = mBtnDown.getPaint();
                paint.setFakeBoldText(true);
            }
        }
        if (TextUtils.isEmpty(downText)) {
            mBtnDown.setVisibility(View.GONE);
            mBtnDivider.setVisibility(View.GONE);
        } else {
            mBtnDown.setText(downText);
        }

        if (TextUtils.isEmpty(upText)) {
            mBtnUp.setVisibility(View.GONE);
            mBtnDivider.setVisibility(View.GONE);
        } else {
            mBtnUp.setText(upText);
        }

        if (!upAble)
            mBtnUp.setTextAppearance(mContext, R.style.dialog_button_disabled);
        if (!downAble)
            mBtnDown.setTextAppearance(mContext, R.style.dialog_button_disabled);

        setContentView(dialogView);

        reSetDialogSize();
    }


    private void reSetDialogSize() {
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = (int) (d.getWidth() * 0.8);
        getWindow().setAttributes(p); // 设置生效
    }

    private void setListeners(View.OnClickListener upClick, View.OnClickListener downClick) {
        mBtnUp.setOnClickListener(new OnDialogBtnClickListener(upClick));
        mBtnDown.setOnClickListener(new OnDialogBtnClickListener(downClick));
    }

    class OnDialogBtnClickListener implements View.OnClickListener {

        private View.OnClickListener mOnClickListener;

        public OnDialogBtnClickListener(View.OnClickListener listener) {
            this.mOnClickListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (this.mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }
            dismiss();
        }
    }

    public void setUpButton(CharSequence btnText, View.OnClickListener onclickListener) {
        mBtnUp.setText(btnText);
        mBtnUp.setOnClickListener(new OnDialogBtnClickListener(onclickListener));
    }

    public void setDownButton(CharSequence btnText, View.OnClickListener onclickListener) {
        mBtnDown.setText(btnText);
        mBtnDown.setOnClickListener(new OnDialogBtnClickListener(onclickListener));
    }

}
