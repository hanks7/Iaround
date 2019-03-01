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
 * @ClassName MySelfAudioChatRecordView.java
 * @Description: 我的一对一语音聊天消息
 */
public class MySelfAudioChatRecordView extends MySelfBaseRecordView {
    private RichTextView mTextView;
    private ImageView mImageView;
    private LinearLayout mLlContent;

    public MySelfAudioChatRecordView(Context context) {
        super(context);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mImageView = (ImageView) findViewById(R.id.iv_icon);
        mTextView = (RichTextView) findViewById(R.id.content);
        mImageView.setImageResource(R.drawable.icon_phone);
    }

    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_record_video_chat_mine, this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);
        mLlContent.setOnLongClickListener(mRecordLongClickListener);
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        // 设置聊天内容


        mTextView.setText(record.getContent());
        checkbox.setChecked(record.isSelect());
        mLlContent.setTag(R.id.im_preview_uri, record);

        // 替换成未通过的头像
        if (record.getId() == Common.getInstance().loginUser.getUid()) {
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)) {
                record.setIcon(verifyicon);
            }
        }

        checkbox.setTag(record);
        setTag(record);

        // 设置头像
        setUserIcon(context, record, mIconView);

        // 设置消息状态
        updateStatus(context, record);

        //设置用户昵称
        setUserNameDis(context, record, record.getMgroupRole());
    }

    @Override
    public void reset() {
        mTextView.setText("");
        mStatusView.setText("");
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        mTextView.setEnabled(isEnable);
    }

}
