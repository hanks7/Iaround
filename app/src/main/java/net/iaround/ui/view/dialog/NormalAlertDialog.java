package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import net.iaround.R;

/**
 * Created by Weavey on 2016/9/3.
 */
public class NormalAlertDialog {

    private TextView mTitle;
    private TextView mContent;
    private Button mLeftBtn;
    private Button mRightBtn;
    private Button mSingleBtn;
    private TextView mLine;
    private Dialog mDialog;
    private View mDialogView;
    private Builder mBuilder;

    public NormalAlertDialog(Builder builder) {

        this.mBuilder = builder;
        mDialog = new Dialog(mBuilder.getContext(), R.style.NormalAlertDialogStyle);
        mDialogView = View.inflate(mBuilder.getContext(), R.layout.dialog_normal_alert, null);
        mTitle = (TextView) mDialogView.findViewById(R.id.dialog_normal_title);
        mContent = (TextView) mDialogView.findViewById(R.id.dialog_normal_content);
        mLeftBtn = (Button) mDialogView.findViewById(R.id.dialog_normal_leftbtn);
        mRightBtn = (Button) mDialogView.findViewById(R.id.dialog_normal_rightbtn);
        mSingleBtn = (Button) mDialogView.findViewById(R.id.dialog_normal_midbtn);
        mLine = (TextView) mDialogView.findViewById(R.id.dialog_normal_line);
        mDialogView.setMinimumHeight((int) (ScreenSizeUtils.getInstance(mBuilder.getContext())
                .getScreenHeight() * builder.getHeight()));
        mDialog.setContentView(mDialogView);

        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(mBuilder.getContext()).getScreenWidth() *
                builder.getWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        initDialog(builder);
    }

    private void initDialog(Builder builder) {

        mDialog.setCanceledOnTouchOutside(builder.isTouchOutside());

        if (builder.getTitleVisible()) {

            mTitle.setVisibility(View.VISIBLE);
        } else {

            mTitle.setVisibility(View.GONE);
        }

        if (builder.isSingleMode()) {

            mSingleBtn.setVisibility(View.VISIBLE);
            mLine.setVisibility(View.GONE);
            mLeftBtn.setVisibility(View.GONE);
            mRightBtn.setVisibility(View.GONE);
        }

        mTitle.setText(builder.getTitleText());
        mTitle.setTextColor(builder.getTitleTextColor());
        mTitle.setTextSize(builder.getTitleTextSize());
        mContent.setText(builder.getContentText());
        mContent.setTextColor(builder.getContentTextColor());
        mContent.setTextSize(builder.getContentTextSize());
        mLeftBtn.setText(builder.getLeftButtonText());
        mLeftBtn.setTextColor(builder.getLeftButtonTextColor());
        mLeftBtn.setTextSize(builder.getButtonTextSize());
        mRightBtn.setText(builder.getRightButtonText());
        mRightBtn.setTextColor(builder.getRightButtonTextColor());
        mRightBtn.setTextSize(builder.getButtonTextSize());
        mSingleBtn.setText(builder.getSingleButtonText());
        mSingleBtn.setTextColor(builder.getSingleButtonTextColor());
        mSingleBtn.setTextSize(builder.getButtonTextSize());

        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBuilder.getOnclickListener() != null) {

                    mBuilder.getOnclickListener().clickLeftButton(NormalAlertDialog.this, mLeftBtn);
                }
            }
        });
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBuilder.getOnclickListener() != null) {

                    mBuilder.getOnclickListener().clickRightButton(NormalAlertDialog.this,
                            mRightBtn);
                }

            }
        });
        mSingleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBuilder.getSingleListener() != null) {

                    mBuilder.getSingleListener().clickSingleButton(NormalAlertDialog.this,
                            mSingleBtn);
                }
            }
        });

    }

    public void show() {

        mDialog.show();
    }

    public void dismiss() {

        mDialog.dismiss();
    }

    public Dialog getDialog(){

        return mDialog;
    }

    public static class Builder {

        private String titleText;
        private int titleTextColor;
        private int titleTextSize;
        private String contentText;
        private int contentTextColor;
        private int contentTextSize;
        private boolean isSingleMode;
        private String singleButtonText;
        private int singleButtonTextColor;
        private String leftButtonText;
        private int leftButtonTextColor;
        private String rightButtonText;
        private int rightButtonTextColor;
        private int buttonTextSize;
        private DialogInterface.OnLeftAndRightClickListener onclickListener;
        private DialogInterface.OnSingleClickListener singleListener;
        private boolean isTitleVisible;
        private boolean isTouchOutside;
        private float height;
        private float width;
        private Context mContext;

        public Builder(Context context) {

            mContext = context;
            titleText = mContext.getString(R.string.tips);
            titleTextColor = ContextCompat.getColor(mContext, R.color.dialog_normal_color);

            contentText = "";
            contentTextColor = ContextCompat.getColor(mContext, R.color.dialog_normal_color);
            isSingleMode = false;
            singleButtonText = mContext.getString(R.string.open);
            singleButtonTextColor = ContextCompat.getColor(mContext, R.color.dialog_normal_color);
            leftButtonText = mContext.getString(R.string.close);
            leftButtonTextColor = ContextCompat.getColor(mContext, R.color.dialog_normal_color);
            rightButtonText = mContext.getString(R.string.open);
            rightButtonTextColor = ContextCompat.getColor(mContext, R.color.dialog_normal_color);
            onclickListener = null;
            singleListener = null;
            isTitleVisible = true;
            isTouchOutside = true;
            height = 0.23f; //屏幕高度*0.23
            width = 0.65f; //屏幕宽度*0.23
            titleTextSize = 16;
            contentTextSize = 14;
            buttonTextSize = 14;

        }

        public Context getContext() {

            return mContext;
        }

        public String getTitleText() {
            return titleText;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public int getTitleTextColor() {
            return titleTextColor;
        }

        public Builder setTitleTextColor(@ColorRes int titleTextColor) {
            this.titleTextColor = ContextCompat.getColor(mContext, titleTextColor);
            return this;
        }

        public String getContentText() {
            return contentText;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public int getContentTextColor() {
            return contentTextColor;
        }

        public Builder setContentTextColor(@ColorRes int contentTextColor) {
            this.contentTextColor = ContextCompat.getColor(mContext, contentTextColor);
            return this;
        }

        public boolean isSingleMode() {
            return isSingleMode;
        }

        public Builder setSingleMode(boolean singleMode) {
            isSingleMode = singleMode;
            return this;
        }

        public String getSingleButtonText() {
            return singleButtonText;
        }

        public Builder setSingleButtonText(String singleButtonText) {
            this.singleButtonText = singleButtonText;
            return this;
        }

        public int getSingleButtonTextColor() {
            return singleButtonTextColor;
        }

        public Builder setSingleButtonTextColor(@ColorRes int singleButtonTextColor) {
            this.singleButtonTextColor = ContextCompat.getColor(mContext, singleButtonTextColor);
            return this;
        }

        public String getLeftButtonText() {
            return leftButtonText;
        }

        public Builder setLeftButtonText(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }

        public int getLeftButtonTextColor() {
            return leftButtonTextColor;
        }

        public Builder setLeftButtonTextColor(@ColorRes int leftButtonTextColor) {
            this.leftButtonTextColor = ContextCompat.getColor(mContext, leftButtonTextColor);
            return this;
        }

        public String getRightButtonText() {
            return rightButtonText;
        }

        public Builder setRightButtonText(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }

        public int getRightButtonTextColor() {
            return rightButtonTextColor;
        }

        public Builder setRightButtonTextColor(@ColorRes int rightButtonTextColor) {
            this.rightButtonTextColor = ContextCompat.getColor(mContext, rightButtonTextColor);
            return this;
        }

        public DialogInterface.OnLeftAndRightClickListener getOnclickListener() {
            return onclickListener;
        }

        public Builder setOnclickListener(DialogInterface.OnLeftAndRightClickListener
                                                  onclickListener) {
            this.onclickListener = onclickListener;
            return this;
        }

        public DialogInterface.OnSingleClickListener getSingleListener() {
            return singleListener;
        }

        public Builder setSingleListener(DialogInterface.OnSingleClickListener singleListener) {
            this.singleListener = singleListener;
            return this;
        }

        public boolean getTitleVisible() {
            return isTitleVisible;
        }

        public Builder setTitleVisible(boolean isVisible) {
            isTitleVisible = isVisible;
            return this;
        }

        public boolean isTouchOutside() {
            return isTouchOutside;
        }

        public Builder setCanceledOnTouchOutside(boolean isTouchOutside) {

            this.isTouchOutside = isTouchOutside;
            return this;
        }

        public int getContentTextSize() {
            return contentTextSize;
        }

        public Builder setContentTextSize(int contentTextSize) {
            this.contentTextSize = contentTextSize;
            return this;
        }

        public int getTitleTextSize() {
            return titleTextSize;
        }

        public Builder setTitleTextSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }

        public int getButtonTextSize() {
            return buttonTextSize;
        }

        public Builder setButtonTextSize(int buttonTextSize) {
            this.buttonTextSize = buttonTextSize;
            return this;
        }

        public float getHeight() {
            return height;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public float getWidth() {
            return width;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public NormalAlertDialog build() {

            return new NormalAlertDialog(this);
        }
    }


}
