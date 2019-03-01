
package net.iaround.tools;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: CustomDialog
 * @Description: 自定义普通Dialog(包含一个按钮或两个按钮)
 * @date 2014-1-6 下午3:16:11
 */
public class CustomDialog extends Dialog {

    private Context mContext;

    private TextView mTitleName;
    private TextView mDialogContent;
    private TextView mBtnPositive;
    private ImageView mBtnDivider;
    private TextView mBtnNegative;
    private View dialogView;

    public CustomDialog(Context context, CharSequence title, CharSequence content) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(title, content);
    }

    public CustomDialog(Context context, CharSequence title, CharSequence content,
                        CharSequence positiveBtnText, View.OnClickListener positiveClick) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(title, content, false, positiveBtnText, "");
        setListeners(positiveClick, null);
    }

    public CustomDialog(Context context, CharSequence title, CharSequence content,
                        CharSequence positiveBtnText, View.OnClickListener positiveClick,
                        CharSequence negativeBtnText, View.OnClickListener negativeClick) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(title, content, false, positiveBtnText, negativeBtnText);
        setListeners(positiveClick, negativeClick);
    }

    public CustomDialog(Context context, CharSequence content,
                        CharSequence positiveBtnText, View.OnClickListener positiveClick,
                        CharSequence negativeBtnText) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(content, false, positiveBtnText, negativeBtnText);
        setListeners(positiveClick, null);
    }

    /**
     * 转让聊吧的dialog
     *
     * @param context
     * @param title
     * @param content
     * @param positiveBtnText
     * @param positiveClick
     * @param negativeBtnText
     * @param negativeClick
     */
    public CustomDialog(Context context, CharSequence title, CharSequence content, boolean isTranferChatbar,
                        CharSequence positiveBtnText, View.OnClickListener positiveClick,
                        CharSequence negativeBtnText, View.OnClickListener negativeClick) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(title, content, isTranferChatbar, positiveBtnText, negativeBtnText);
        setListeners(positiveClick, negativeClick);
    }

    public CustomDialog(Context context, CharSequence title, CharSequence content,
                        int layoutRes, CharSequence positiveBtnText, View.OnClickListener positiveClick,
                        CharSequence negativeBtnText, View.OnClickListener negativeClick) {
        super(context, R.style.NewDialog);
        this.mContext = context;
        initViews(title, content, layoutRes, positiveBtnText, negativeBtnText);
        setListeners(positiveClick, negativeClick);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(CharSequence title, CharSequence content) {
        dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_simple,
                null);
        mTitleName = (TextView) dialogView.findViewById(R.id.dialog_title);
        mTitleName.setText(title);
        mDialogContent = (TextView) dialogView.findViewById(R.id.dialog_content);

//		if(Common.getInstance( ).isUserLogin)
//		{
//			//登录之后才能使用表情分析
//			mDialogContent.setText( FaceManager.getInstance( mContext ).parseIconForString(
//					mDialogContent , mContext , content.toString( ), 16 ) );
//		}
//		else
//		{
        mDialogContent.setText(content.toString());
//		}
        mBtnPositive = (TextView) dialogView.findViewById(R.id.dialog_positive);
        mBtnPositive.setVisibility(View.GONE);
//		mBtnDivider = ( ImageView ) dialogView.findViewById( R.id.dialog_btn_devider );
//		mBtnDivider.setVisibility(View.GONE);//jiqiang
        mBtnNegative = (TextView) dialogView.findViewById(R.id.dialog_negative);
        mBtnNegative.setVisibility(View.GONE);

        if (CommonFunction.getLang(mContext) == 3) {
            //中文繁体不加粗

        } else {
            //其他字体加粗
//			TextPaint paint = mBtnNegative.getPaint();
//			paint.setFakeBoldText(true);
        }
        setContentView(dialogView);

        reSetDialogSize();
    }

    /**
     * 是否需要对某些特殊字体特殊处理的dialog
     *
     * @param title             标题 提示
     * @param content           展示内容
     * @param isTransferChatbar 是否需要对某些字体特殊处理
     * @param positiveBtnText   确定按钮
     * @param negativeBtnText   取消按钮
     */
    private void initViews(CharSequence title, CharSequence content, boolean isTransferChatbar,
                           CharSequence positiveBtnText, CharSequence negativeBtnText) {
        dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_simple,
                null);
        mTitleName = (TextView) dialogView.findViewById(R.id.dialog_title);
        mTitleName.setText(title);
        mDialogContent = (TextView) dialogView.findViewById(R.id.dialog_content);

        if (!TextUtils.isEmpty(content)) {
            if (isTransferChatbar) {
                String username = content.toString();
                String propExpends = mContext.getResources().getString(R.string.chatbar_transfer_hin1) + "<font color='#FF4064'>" + username + "</font>" +
                        mContext.getResources().getString(R.string.chatbar_transfer_hin2);
                mDialogContent.setText(Html.fromHtml(propExpends));
            } else
                mDialogContent.setText(content.toString());
        } else
            mDialogContent.setVisibility(View.GONE);
        mBtnPositive = (TextView) dialogView.findViewById(R.id.dialog_positive);
        mBtnPositive.setText(positiveBtnText);
        mBtnNegative = (TextView) dialogView.findViewById(R.id.dialog_negative);

        if (CommonFunction.getLang(mContext) == 3) {
            //中文繁体不加粗

        } else {
            //其他字体加粗
//			TextPaint paint = mBtnNegative.getPaint();
//			paint.setFakeBoldText(true);
        }
        if (TextUtils.isEmpty(negativeBtnText)) {
            mBtnNegative.setVisibility(View.GONE);
//			mBtnDivider.setVisibility( View.GONE );
        } else {
            mBtnNegative.setText(negativeBtnText);
        }

        if (TextUtils.isEmpty(positiveBtnText)) {
            mBtnPositive.setVisibility(View.GONE);
//			mBtnDivider.setVisibility( View.GONE );
        } else {
            mBtnPositive.setText(positiveBtnText);
        }
        setContentView(dialogView);

        reSetDialogSize();
    }

    /**
     * 是否需要对某些特殊字体特殊处理的不需要标题dialog
     *
     * @param content           展示内容
     * @param isTransferChatbar 是否需要对某些字体特殊处理
     * @param positiveBtnText   确定按钮
     * @param negativeBtnText   取消按钮
     */
    private void initViews(CharSequence content, boolean isTransferChatbar,
                           CharSequence positiveBtnText, CharSequence negativeBtnText) {
        dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_simple,
                null);
        mTitleName = (TextView) dialogView.findViewById(R.id.dialog_title);
        mTitleName.setVisibility(View.GONE);
        mDialogContent = (TextView) dialogView.findViewById(R.id.dialog_content);

        if (!TextUtils.isEmpty(content)) {
            if (isTransferChatbar) {
                String username = content.toString();
                String propExpends = mContext.getResources().getString(R.string.chatbar_transfer_hin1) + "<font color='#FF4064'>" + username + "</font>" +
                        mContext.getResources().getString(R.string.chatbar_transfer_hin2);
                mDialogContent.setText(Html.fromHtml(propExpends));
            } else
                mDialogContent.setText(content.toString());
        } else
            mDialogContent.setVisibility(View.GONE);
        mBtnPositive = (TextView) dialogView.findViewById(R.id.dialog_positive);
        mBtnPositive.setText(positiveBtnText);
        mBtnNegative = (TextView) dialogView.findViewById(R.id.dialog_negative);

        if (CommonFunction.getLang(mContext) == 3) {
            //中文繁体不加粗

        } else {
            //其他字体加粗
//			TextPaint paint = mBtnNegative.getPaint();
//			paint.setFakeBoldText(true);
        }
        if (TextUtils.isEmpty(negativeBtnText)) {
            mBtnNegative.setVisibility(View.GONE);
//			mBtnDivider.setVisibility( View.GONE );
        } else {
            mBtnNegative.setText(negativeBtnText);
        }

        if (TextUtils.isEmpty(positiveBtnText)) {
            mBtnPositive.setVisibility(View.GONE);
//			mBtnDivider.setVisibility( View.GONE );
        } else {
            mBtnPositive.setText(positiveBtnText);
        }
        setContentView(dialogView);

        reSetDialogSize();
    }

    private void initViews(CharSequence title, CharSequence content, int layoutRes,
                           CharSequence positiveBtnText, CharSequence negativeBtnText) {
        dialogView = LayoutInflater.from(mContext).inflate(layoutRes,
                null);
        mTitleName = (TextView) dialogView.findViewById(R.id.dialog_title);
        mTitleName.setText(title);
        mDialogContent = (TextView) dialogView.findViewById(R.id.dialog_content);

//		if(Common.getInstance( ).isUserLogin)
//		{
//			//登录之后才能使用表情分析
//			mDialogContent.setText( FaceManager.getInstance( mContext ).parseIconForString(
//				mDialogContent , mContext , content.toString( ), 16 ) );
//		}
//		else
//		{
        mDialogContent.setText(content.toString());
//		}
        mBtnPositive = (TextView) dialogView.findViewById(R.id.dialog_positive);
        mBtnPositive.setText(positiveBtnText);
//		mBtnDivider = ( ImageView ) dialogView.findViewById( R.id.dialog_btn_devider );
        mBtnNegative = (TextView) dialogView.findViewById(R.id.dialog_negative);

        if (CommonFunction.getLang(mContext) == 3) {
            //中文繁体不加粗

        } else {
            //其他字体加粗
//			TextPaint paint = mBtnNegative.getPaint();
//			paint.setFakeBoldText(true);
        }
        if (TextUtils.isEmpty(negativeBtnText)) {
            mBtnNegative.setVisibility(View.GONE);
//			mBtnDivider.setVisibility( View.GONE );
        } else {
            mBtnNegative.setText(negativeBtnText);
        }

        if (TextUtils.isEmpty(positiveBtnText)) {
            mBtnPositive.setVisibility(View.GONE);
//			mBtnDivider.setVisibility( View.GONE );
        } else {
            mBtnPositive.setText(positiveBtnText);
        }
        setContentView(dialogView);

        reSetDialogSize();
    }

    private void reSetDialogSize() {
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = (int) (d.getWidth() * 0.9);
        getWindow().setAttributes(p); // 设置生效
    }

    private void setListeners(View.OnClickListener positiveClick,
                              View.OnClickListener negativeClick) {
        mBtnPositive.setOnClickListener(new OnDialogBtnClickListener(positiveClick));
        mBtnNegative.setOnClickListener(new OnDialogBtnClickListener(negativeClick));
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

    public View getDialogView() {
        return dialogView;
    }

    /**
     * @param btnText
     * @param onclickListener
     * @Title: setPositiveButton
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public void setPositiveButton(CharSequence btnText, View.OnClickListener onclickListener) {
        mBtnPositive.setText(btnText);
        mBtnPositive.setOnClickListener(new OnDialogBtnClickListener(onclickListener));
    }

    public void setNegativeButton(CharSequence btnText, View.OnClickListener onclickListener) {
        mBtnNegative.setText(btnText);
        mBtnNegative.setOnClickListener(new OnDialogBtnClickListener(onclickListener));
    }
}
