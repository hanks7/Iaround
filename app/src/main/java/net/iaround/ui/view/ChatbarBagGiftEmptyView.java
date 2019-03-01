package net.iaround.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iaround.R;

/**
 * Class:背包礼物空布局
 * Author：yuchao
 * Date: 2017/7/20 12:24
 * Email：15369302822@163.com
 */
public class ChatbarBagGiftEmptyView extends RelativeLayout {

    private Context mContext;
    private LinearLayout llEmptyLayout;
    private View view;

    public ChatbarBagGiftEmptyView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }
    private void initView()
    {
        view = LayoutInflater.from(mContext).inflate(R.layout.chatbar_gift_empty_layout,this,true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        llEmptyLayout = (LinearLayout) findViewById(R.id.ll_empty_layout);
    }

    public void setListener(OnClickListener clickListener)
    {
        llEmptyLayout.setOnClickListener(clickListener);
    }
}
