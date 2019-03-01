package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.entity.RecordGiftBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @ClassName MySelfGiftRecordView.java
 * @Description: 我的礼物消息
 */

public class MySelfGiftRecordViewFromChatBar extends MySelfBaseRecordView {

    private ImageView mImageView;
    private TextView mNameView;//礼物的数量 1314
    private TextView mPriceView;
    private TextView mCharismaView;
    private TextView mEXPView;
    private ImageView mGiftCount;
    private RelativeLayout content;
    private TextView mTvGiftDesc;
    private LinearLayout llChatBarGiftRemind;
    private TextView tvChatPersonalGiftWord;

    public MySelfGiftRecordViewFromChatBar(Context context) {
        super(context);

        mImageView = (ImageView) findViewById(R.id.img);
        mNameView = (TextView) findViewById(R.id.name);
        mPriceView = (TextView) findViewById(R.id.price);
        mCharismaView = (TextView) findViewById(R.id.charisma);
        content = (RelativeLayout) findViewById(R.id.content);
        mEXPView = (TextView) findViewById(R.id.exp);
        mGiftCount = (ImageView) findViewById(R.id.count_img);
        mTvGiftDesc = (TextView) findViewById(R.id.tv_chat_personal_des);
        llChatBarGiftRemind = (LinearLayout) findViewById(R.id.ll_chat_bar_gift_remind);
        tvChatPersonalGiftWord = (TextView) findViewById(R.id.tv_chat_personal_gift_word);
    }

    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_record_gift_mine_new, this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);
        // 设置长按事件
        content.setOnLongClickListener(mRecordLongClickListener);
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        RecordGiftBean bean = GsonUtil.getInstance()
                .getServerBean(record.getContent(), RecordGiftBean.class);
        if (bean != null) {
            if (bean.isFromChatRoom != null) {
                if (bean.isFromChatRoom.equals("1")) {
                    llChatBarGiftRemind.setVisibility(VISIBLE);
                    tvChatPersonalGiftWord.setText(context.getString(R.string.chat_bar_gift_send_from_bar));
                } else if (bean.isFromChatRoom.equals("2")) {
                    llChatBarGiftRemind.setVisibility(VISIBLE);
                    tvChatPersonalGiftWord.setText(context.getString(R.string.chat_bar_gift_send_from_video_chat));
                } else {
                    llChatBarGiftRemind.setVisibility(GONE);
                }
            } else {
                llChatBarGiftRemind.setVisibility(GONE);
           }

            mNameView.setText(" "+bean.giftnum + " ");
            TextPaint paint=mNameView.getPaint();
            paint.setFakeBoldText(true);
            if (bean.gift_desc != null) {
                mTvGiftDesc.setText(bean.gift_desc + bean.giftname);
            } else {
                mTvGiftDesc.setText(context.getResources().getString(R.string.chat_bar_gift_send_gift_desc)+bean.giftname);
            }
//            mTvGiftDesc.setText(bean.gift_desc + bean.giftname);
            String strFromat = context.getString(R.string.chat_send_exp);
            String strExperience = String.format(strFromat, "  +" + bean.exp);
            mCharismaView.setText(strExperience);
            if (CommonFunction.isEmptyOrNullStr(bean.price)) {
                mPriceView.setVisibility(View.GONE);
            } else {
                //1为金币商品　2为钻石商品 3为爱心商品  6为星星商品
                String strPrice = null;
                if (bean.currencytype ==1) {
                    strPrice = context.getString(R.string.gift_price_1);
                }else if(bean.currencytype == 2){
                    strPrice = context.getString(R.string.gift_price_2);
                } else if (bean.currencytype == 3) {
                    strPrice = context.getString(R.string.item_pay_love_order) + ":";
                } else if(bean.currencytype == 6){
                    strPrice = context.getString(R.string.stars_m);
                }
                mPriceView.setText(strPrice + "  " + bean.price);
            }

            String thumbPicUrl = CommonFunction.thumPicture(record.getAttachment());

            GlideUtil.loadImage(BaseApplication.appContext, thumbPicUrl, mImageView, defSmall, defSmall);

            int countRes = 0;

//        if (bean.giftnum instanceof Integer) {
//            int value = 0;
//            value = ((Integer) bean.giftnum).intValue();
//            switch (value) {
//                case 10:
//                    countRes = R.drawable.chatbar_gift_num_10;
//                    break;
//                case 30:
//                    countRes = R.drawable.chatbar_gift_num_30;
//                    break;
//                case 66:
//                    countRes = R.drawable.chatbar_gift_num_66;
//                    break;
//                case 188:
//                    countRes = R.drawable.chatbar_gift_num_188;
//                    break;
//                case 520:
//                    countRes = R.drawable.chatbar_gift_num_520;
//                    break;
//                case 1314:
//                    countRes = R.drawable.chatbar_gift_num_1314;
//                    break;
//                default:
//                    countRes = R.color.transparent;
//                    break;
//
//            }
//
//        } else if (bean.giftnum instanceof String) {
//            String value = "";
//            value = ((String) bean.giftnum);
//            switch (value) {
//                case 10+"":
//                    countRes = R.drawable.chatbar_gift_num_10;
//                    break;
//                case 30+"":
//                    countRes = R.drawable.chatbar_gift_num_30;
//                    break;
//                case 66+"":
//                    countRes = R.drawable.chatbar_gift_num_66;
//                    break;
//                case 188+"":
//                    countRes = R.drawable.chatbar_gift_num_188;
//                    break;
//                case 520+"":
//                    countRes = R.drawable.chatbar_gift_num_520;
//                    break;
//                case 1314+"":
//                    countRes = R.drawable.chatbar_gift_num_1314;
//                    break;
//                default:
//                    countRes = R.color.transparent;
//                    break;
//
//            }
//
//        }
//        switch (value) {
//            case 10:
//                countRes = R.drawable.chatbar_gift_num_10;
//                break;
//            case 30:
//                countRes = R.drawable.chatbar_gift_num_30;
//                break;
//            case 66:
//                countRes = R.drawable.chatbar_gift_num_66;
//                break;
//            case 188:
//                countRes = R.drawable.chatbar_gift_num_188;
//                break;
//            case 520:
//                countRes = R.drawable.chatbar_gift_num_520;
//                break;
//            case 1314:
//                countRes = R.drawable.chatbar_gift_num_1314;
//                break;
//            default:
//                countRes = R.color.transparent;
//                break;
//
//        }
            mGiftCount.setImageResource(countRes);
        }

        // 替换成未通过的头像
        if(record.getId() == Common.getInstance().loginUser.getUid()){
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                record.setIcon(verifyicon);
            }
        }

        content.setTag(R.id.im_preview_uri, record);
        checkbox.setChecked(record.isSelect());
        checkbox.setTag(record);
        setTag(record);

        // 设置头像点击事件
        setUserIcon(context, record, mIconView);
        // 设置消息状态
        updateStatus(context, record);
    }

    @Override
    public void reset() {
        mNameView.setText("");
        mPriceView.setText("");
        mCharismaView.setText("");
        mStatusView.setText("");
        mEXPView.setText("");
        mGiftCount.setImageResource(R.color.transparent);
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        content.setEnabled(isEnable);
    }

}
