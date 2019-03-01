package net.iaround.model.chatbar.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iaround.R;
import net.iaround.ui.seach.news.SearchChatBarActivity;


/**
 * Created by Ray on 2017/6/21.
 */

public class ChatBarFmailyHeadViewEmpty extends RelativeLayout {
    private Context mContext;

    private LinearLayout llChatBarEmypty;

    public ChatBarFmailyHeadViewEmpty(Context context) {
        super(context);
        this.mContext = context;
        initView();
        initListener();
    }


    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.chat_bar_family_top_info, this);
        llChatBarEmypty = (LinearLayout) itemView.findViewById(R.id.ll_chat_bar_family_empty_select);
    }

    private void initListener() {
        llChatBarEmypty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,SearchChatBarActivity.class);
                mContext.startActivity(intent);
            }
        });
    }


    public ChatBarFmailyHeadViewEmpty(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBarFmailyHeadViewEmpty(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
