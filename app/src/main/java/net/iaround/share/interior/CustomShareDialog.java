package net.iaround.share.interior;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.EditTextUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.StringUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.utils.EditTextViewUtil;
import net.iaround.utils.OnEditPast;


/**
 * 内部分享对话框
 */
public class CustomShareDialog extends Dialog {
    /**
     * 编辑内容最大长度限制
     */
    private final int MAX_SHARE_CONTENT_LENGTH = 140;

    private Context mContext;

    private TextView mTitleName;
    private TextView mDialogContent;
    private ImageView mDialogImage;

    private EditTextViewUtil mDialogEditView;

    private TextView mBtnPositive;
    private TextView mBtnNegative;

    public int DEFAULT_ICON = R.drawable.icon;

    public CustomShareDialog(Context context, int shareType, String imgUrl, CharSequence title, CharSequence content,
                             CharSequence positiveBtnText, View.OnClickListener positiveClick,
                             CharSequence negativeBtnText, View.OnClickListener negativeClick) {
        super(context, R.style.NewDialog);
        // TODO Auto-generated constructor stub
        CommonFunction.log("imgUrl", "imgUrl---" + imgUrl);
        this.mContext = context;
        initViews(shareType, imgUrl, title, content, positiveBtnText, negativeBtnText);
        setListeners(positiveClick, negativeClick);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(int shareType, String imgUrl, CharSequence title, CharSequence content,
                           CharSequence positiveBtnText, CharSequence negativeBtnText) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.iaround_share_custom_dialog,
                null);

        mDialogImage = (ImageView) dialogView.findViewById(R.id.dialog_img);
        if (shareType == IAroundDynamicUtil.ID) {
            DEFAULT_ICON = R.drawable.icon;
        } else if (shareType == IAroundGroupUtil.ID) {
            DEFAULT_ICON = R.drawable.chat_record_share_default_icon;
        } else if (shareType == IAroundFriendUtil.ID) {
            DEFAULT_ICON = R.drawable.icon;
        }
//        ImageViewUtil.getDefault().loadImageInConvertView(
//                imgUrl, mDialogImage, DEFAULT_ICON, DEFAULT_ICON);
        GlideUtil.loadImage(BaseApplication.appContext, imgUrl, mDialogImage, DEFAULT_ICON, DEFAULT_ICON);

        mTitleName = (TextView) dialogView.findViewById(R.id.dialog_title);
        mTitleName.setText(FaceManager.getInstance(mContext).parseIconForString(
                mTitleName, mContext, title.toString(), 16));
        mDialogContent = (TextView) dialogView.findViewById(R.id.dialog_content);
        mDialogContent.setText(FaceManager.getInstance(mContext).parseIconForString(
                mDialogContent, mContext, content.toString(), 16));

        mDialogEditView = (EditTextViewUtil) dialogView.findViewById(R.id.dialog_edit_content);
        EditTextUtil.autoLimitLength(mDialogEditView, MAX_SHARE_CONTENT_LENGTH);

        mBtnPositive = (TextView) dialogView.findViewById(R.id.dialog_right_btn);
        mBtnPositive.setText(positiveBtnText);

        mBtnNegative = (TextView) dialogView.findViewById(R.id.dialog_left_btn);
        mBtnNegative.setText(negativeBtnText);

        setConfig(dialogView);

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

    private void setConfig(final View dialogView) {
        mDialogEditView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                if (StringUtil.getLengthCN1(s.toString()) > MAX_SHARE_CONTENT_LENGTH) {
                    CommonFunction.toastMsg(mContext,
                            mContext.getString(R.string.max_share_content_length_tips));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String str = s.toString();
                if (str.contains("\n")) {
                    str = str.replaceAll("\n", "");
                    mDialogEditView.setText(str);
                }
            }
        });
        mDialogEditView.setOnEditPas(new OnEditPast() {

            @Override
            public void onEditPastToView(EditTextViewUtil editText) {
                // TODO Auto-generated method stub
                FaceManager.getInstance(mContext)
                        .parseIconForEditText(mContext, mDialogEditView);
                String mContent = mDialogEditView.getText().toString();
                mDialogEditView.setSelection(Math.max(0, mContent.length()));
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        final Handler mHandler = new Handler(Looper.getMainLooper());
        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                //对话框消失时隐藏软键盘
                CommonFunction.log("dialog", "OnDismiss hideInputMethod****");
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            CommonFunction.log("dialog", "OnDismiss hideInputMethod--------");
                            CommonFunction.hideInputMethod(mContext,
                                    dialogView);
                        }
                    }, 300);
                }
            }
        });
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
            CommonFunction.hideInputMethod(mContext, v);
            dismiss();
        }
    }

    public String getEditTextContent() {
        return mDialogEditView.getText().toString().trim();
    }
}
