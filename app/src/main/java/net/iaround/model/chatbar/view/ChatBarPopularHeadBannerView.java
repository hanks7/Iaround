package net.iaround.model.chatbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import net.iaround.R;
import net.iaround.model.chatbar.ChatBarPopularType;
import net.iaround.ui.datamodel.ResourceBanner;
import net.iaround.ui.view.FlyBanner;
import java.util.List;

/**
 * Created by GH on 2017/7/28.
 */

public class ChatBarPopularHeadBannerView extends RelativeLayout {

    private Context mContext;

    private FlyBanner mVpBanner;

    private List<ResourceBanner> chatBarFamilyTypeObjectBanner;

    public ChatBarPopularHeadBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBarPopularHeadBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChatBarPopularHeadBannerView(Context context, ChatBarPopularType chatBarFamilyType) {
        super(context);
        this.mContext = context;
        initView();
        refreshView(chatBarFamilyType);
    }


    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.chat_bar_popular_head_banner_view, this);
        mVpBanner = (FlyBanner) itemView.findViewById(R.id.vp_charbarpopular);
    }

    public void refreshView(ChatBarPopularType chatBarFamilyType) {
        int type = chatBarFamilyType.getType();
        if (type == 5) {
            chatBarFamilyTypeObjectBanner = (List<ResourceBanner>) chatBarFamilyType.getObjectBanner();
            mVpBanner.setImagesUrl(chatBarFamilyTypeObjectBanner,true);
        }
    }

}
