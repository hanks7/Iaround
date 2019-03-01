package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.im.proto.Iavchat;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.friend.bean.VideoChatBean;

/**
 * @ClassName FriendAudioChatRecordView.java
 * @Description: 朋友的一对一语音聊天消息
 */

public class FriendAudioChatRecordView extends FriendBaseRecordView {
    private RichTextView mTextView;
    private ImageView mImageView;
    private LinearLayout mLlContent;
    private int grouprole;

    public FriendAudioChatRecordView(Context context) {
        super(context);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mTextView = (RichTextView) findViewById(R.id.content);
        mImageView = (ImageView) findViewById(R.id.iv_icon);
        mImageView.setImageResource(R.drawable.icon_phone_gray);
//		mTextView.setBackgroundResource(contentBackgroundRes);

//		int paddingLeft = CommonFunction.dipToPx(context, 24);
//		int paddingRight = CommonFunction.dipToPx(context, 12);
//		int paddingVer = CommonFunction.dipToPx(context, 9);
//		mTextView.setPadding(paddingLeft, paddingVer, paddingRight, paddingVer);
    }

    public void setGrouprole(int grouprole) {
        this.grouprole = grouprole;
    }

    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_record_video_chat_other,
                this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);

        if (!bIsSystemUser(record.getFuid())) {
            mLlContent.setOnLongClickListener(mRecordLongClickListener);
        }
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {

        mTextView.setText(record.getContent());
        checkbox.setChecked(record.isSelect());

        // 替换成未通过的头像
        if (record.getId() == Common.getInstance().loginUser.getUid()) {
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)) {
                record.setIcon(verifyicon);
            }
        }

        checkbox.setTag(record);
        mLlContent.setTag(R.id.im_preview_uri, record);
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