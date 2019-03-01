package net.iaround.model.chatbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.chatbar.ChatBarPopularType;

/**
 * Created by Ray on 2017/7/28.
 */

public class ChatBarPopularHeadView extends RelativeLayout {
    private Context mContext;
    private ChatBarPopularType mChatBarFamilyType;
    private TextView tvMyFamilyText;

    public ChatBarPopularHeadView(Context context, ChatBarPopularType chatBarFamilyType) {
        super(context);
        this.mContext = context;
        this.mChatBarFamilyType = chatBarFamilyType;
        initView();
        refreshView(chatBarFamilyType);
    }


    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.chat_bar_popular_headview, this);
        tvMyFamilyText = (TextView) itemView.findViewById(R.id.tv_chat_bar_popular);
    }

    public void refreshView(ChatBarPopularType chatBarFamilyType) {
        int type = chatBarFamilyType.getType();
        if (type == 1) {
            tvMyFamilyText.setText("" + mContext.getResources().getString(R.string.chat_bar_popular_recommend));
        }else if(type== 3){
            tvMyFamilyText.setText(""+mContext.getResources().getString(R.string.chat_bar_popular_other));

        }

    }

    public ChatBarPopularHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBarPopularHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
