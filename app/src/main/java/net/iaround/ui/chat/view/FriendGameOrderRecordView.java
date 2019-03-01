package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.chat.GameOrderMessageBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.RichTextView;
import net.iaround.utils.OnClickUtil;

/**
 * @ClassName FriendGameOrderRecordView.java
 * @Description: 朋友的预约陪玩的订单消息
 */

public class FriendGameOrderRecordView extends FriendBaseRecordView {
    private RichTextView mTextView;
    private TextView mTvOrderStatus;
    private TextView mTvAgree;
    private TextView mTvRefuse;
    private LinearLayout mLlOrderOption;

    public FriendGameOrderRecordView(Context context) {
        super(context);

        mTextView = (RichTextView) findViewById(R.id.content);
//        mTextView.setBackgroundResource(contentBackgroundRes);
        mTvOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        mTvAgree = (TextView) findViewById(R.id.tv_agree);
        mTvRefuse = (TextView) findViewById(R.id.tv_refuse);
        mLlOrderOption = (LinearLayout) findViewById(R.id.ll_order_option);

//		int paddingLeft = CommonFunction.dipToPx(context, 24);
//		int paddingRight = CommonFunction.dipToPx(context, 12);
//		int paddingVer = CommonFunction.dipToPx(context, 9);
//		mTextView.setPadding(paddingLeft, paddingVer, paddingRight, paddingVer);
    }

    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_record_order_game_other, this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);

        if (!bIsSystemUser(record.getFuid())) {
            mTextView.setOnLongClickListener(mRecordLongClickListener);
//            mTvAgree.setOnClickListener(mOrderAgreeClickListener);
//            mTvRefuse.setOnClickListener(mOrderRefuseClickListener);

            mTvAgree.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(OnClickUtil.isFastClick()){
                        return;
                    }

                    if (mOrderAgreeClickListener != null) {
                        mLlOrderOption.setVisibility(GONE);
                        mTvOrderStatus.setVisibility(VISIBLE);
                        mTvOrderStatus.setText(R.string.is_agreed);
                        mTvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_select), null, null, null);
                        mOrderAgreeClickListener.onClick(v);
                    }
                }
            });
            mTvRefuse.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(OnClickUtil.isFastClick()){
                        return;
                    }

                    if (mOrderRefuseClickListener != null) {
                        mLlOrderOption.setVisibility(GONE);
                        mTvOrderStatus.setVisibility(VISIBLE);
                        mTvOrderStatus.setText(R.string.is_refused);
                        mTvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pic_refuse), null, null, null);
                        mOrderRefuseClickListener.onClick(v);

                    }
                }
            });
        }

    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        GameOrderMessageBean bean;
        String result = record.getAttachment();
        if (!TextUtils.isEmpty(result)) {
            bean = GsonUtil.getInstance().getServerBean(result, GameOrderMessageBean.class);
            if (bean != null) {

                if (bean.orderStatus == 0) {
                    switch (bean.step) {
                        case 3:
                            mLlOrderOption.setVisibility(VISIBLE);
                            mTvOrderStatus.setVisibility(GONE);
                            mTvRefuse.setVisibility(GONE);
                            break;
                        default:
                            mLlOrderOption.setVisibility(VISIBLE);
                            mTvOrderStatus.setVisibility(GONE);
                            mTvRefuse.setVisibility(VISIBLE);
                            break;
                    }
                } else if (bean.orderStatus == 1) {
                    mLlOrderOption.setVisibility(GONE);
                    mTvOrderStatus.setVisibility(VISIBLE);
                    mTvOrderStatus.setText(R.string.is_agreed);
                    mTvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_select), null, null, null);
                } else if (bean.orderStatus == 2) {
                    mLlOrderOption.setVisibility(GONE);
                    mTvOrderStatus.setVisibility(VISIBLE);
                    mTvOrderStatus.setText(R.string.is_refused);
                    mTvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pic_refuse), null, null, null);
                }


            }
        }


        mTextView.setText(record.getContent());
        mTextView.parseIcon();

        checkbox.setChecked(record.isSelect());

        // 替换成未通过的头像
        if (record.getId() == Common.getInstance().loginUser.getUid()) {
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)) {
                record.setIcon(verifyicon);
            }
        }

        checkbox.setTag(record);
        mTextView.setTag(R.id.im_preview_uri, record);
        mTvAgree.setTag(record);
        mTvRefuse.setTag(record);
        setTag(record);
        //设置用户备注昵称
        setUserNotename(context, record);

        //设置用户昵称
        setUserNameDis(context, record, record.getGroupRole());

        // 设置头像点击事件
        setUserIcon(context, record, mIconView);
    }

    @Override
    public void reset() {
        mIconView.getImageView().setImageBitmap(null);
        mTextView.setText("");
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        mTextView.setEnabled(isEnable);
    }


}