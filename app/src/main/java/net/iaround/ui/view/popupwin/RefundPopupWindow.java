package net.iaround.ui.view.popupwin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.iaround.R;

/**
 * Created by yz on 2018/8/23.
 * 退款弹窗
 */

public class RefundPopupWindow implements View.OnClickListener {
    private Context mContext;
    private View mContentView;
    private PopupWindow mWindow;
    private TextView mTvReasonOne;
    private TextView mTvReasonTwo;
    private TextView mTvReasonThree;
    private EditText mEtReason;
    private TextView mTvSubmit;
    private TextView mTvClose;
    private String mReason;
    private RefundReasonListener mRefundReasonListener;

    public RefundPopupWindow(Context context) {
        this.mContext = context;
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.refund_popup, null, false);
        initView();
        mWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setOutsideTouchable(true);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        initListener();

    }

    private void initView() {
        mTvReasonOne = (TextView) mContentView.findViewById(R.id.tv_reason_one);
        mTvReasonTwo = (TextView) mContentView.findViewById(R.id.tv_reason_two);
        mTvReasonThree = (TextView) mContentView.findViewById(R.id.tv_reason_three);
        mTvSubmit = (TextView) mContentView.findViewById(R.id.tv_submit);
        mEtReason = (EditText) mContentView.findViewById(R.id.et_reason);
        mTvClose = (TextView) mContentView.findViewById(R.id.tv_close);
    }

    private void initListener() {
        mTvReasonOne.setOnClickListener(this);
        mTvReasonTwo.setOnClickListener(this);
        mTvReasonThree.setOnClickListener(this);
        mTvClose.setOnClickListener(this);
        mTvSubmit.setOnClickListener(this);
        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reason_one:
                mTvReasonOne.setSelected(true);
                mTvReasonTwo.setSelected(false);
                mTvReasonThree.setSelected(false);
                mReason = mTvReasonOne.getText().toString();
                break;
            case R.id.tv_reason_two:
                mTvReasonOne.setSelected(false);
                mTvReasonTwo.setSelected(true);
                mTvReasonThree.setSelected(false);
                mReason = mTvReasonTwo.getText().toString();
                break;
            case R.id.tv_reason_three:
                mTvReasonOne.setSelected(false);
                mTvReasonTwo.setSelected(false);
                mTvReasonThree.setSelected(true);
                mReason = mTvReasonThree.getText().toString();
                break;
            case R.id.tv_submit:
                if (mRefundReasonListener != null) {
                    String other = mEtReason.getText().toString().trim();
                    if (TextUtils.isEmpty(mReason) && TextUtils.isEmpty(other)) {
                        return;
                    }
                    mRefundReasonListener.onRefundReason(mReason, other);
                }
                break;
            case R.id.tv_close:
                mWindow.dismiss();
                break;
        }
    }

    public void setRefundReasonListener(RefundReasonListener listener) {
        mRefundReasonListener = listener;
    }

    public interface RefundReasonListener {
        void onRefundReason(String reason, String otherReason);
    }

    public void showPopup() {
        mWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    public void dismissWindow() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        }
    }
}
