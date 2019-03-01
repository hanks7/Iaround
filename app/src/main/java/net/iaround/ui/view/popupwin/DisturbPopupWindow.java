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

public class DisturbPopupWindow implements View.OnClickListener {
    private Context mContext;
    private View mContentView;
    private PopupWindow mWindow;
    private TextView mTvSubmit;
    private TextView mTvClose;
    private TextView mTvContent;
    private RefundReasonListener mRefundReasonListener;
    private boolean mVoiceVotDisturb;

    public DisturbPopupWindow(Context context, boolean voiceVotDisturb) {
        this.mContext = context;
        this.mVoiceVotDisturb = voiceVotDisturb;
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.disturb_popup, null, false);
        initView();
        mWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setOutsideTouchable(true);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        initListener();

    }

    private void initView() {
        mTvSubmit = (TextView) mContentView.findViewById(R.id.tv_submit);
        mTvClose = (TextView) mContentView.findViewById(R.id.tv_close);
        mTvContent = (TextView) mContentView.findViewById(R.id.tv_content);
        if (mVoiceVotDisturb) {
            mTvContent.setText(mContext.getResources().getString(R.string.disturb_shut_down));
            mTvSubmit.setText(mContext.getResources().getString(R.string.shut_down));
        } else {
            mTvContent.setText(mContext.getResources().getString(R.string.disturb_open));
            mTvSubmit.setText(mContext.getResources().getString(R.string.pop_open));
        }
    }

    private void initListener() {
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
            case R.id.tv_submit:
                if (mRefundReasonListener != null) {
                    mRefundReasonListener.onRefundReason();
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
        void onRefundReason();
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
