package net.iaround.model.chatbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.chatbar.ChatBarFamilyType;

/**
 * Created by Ray on 2017/6/21.
 */

public class ChatBarFmailyHeadView extends RelativeLayout {
    private Context mContext;
    private ChatBarFamilyType mChatBarFamilyType;
    private ImageView ivMyFamilyIcon;
    private TextView tvMyFamilyText;

    public ChatBarFmailyHeadView(Context context, ChatBarFamilyType chatBarFamilyType) {
        super(context);
        this.mContext = context;
        this.mChatBarFamilyType = chatBarFamilyType;
        initView();
        refreshView(chatBarFamilyType);
    }


    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.chat_bar_my_family_headview, this);
        ivMyFamilyIcon = (ImageView) itemView.findViewById(R.id.iv_chat_bar_family_join);
        tvMyFamilyText = (TextView) itemView.findViewById(R.id.tv_chat_bar_family_my_family);
    }

    public void refreshView(ChatBarFamilyType chatBarFamilyType) {
        String text = (String) chatBarFamilyType.getObjectLeft();
        if (text != null) {
            tvMyFamilyText.setText("" + text);
        }

    }

    public ChatBarFmailyHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBarFmailyHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
