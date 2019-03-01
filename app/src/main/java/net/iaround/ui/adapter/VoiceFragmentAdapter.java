package net.iaround.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.entity.VoiceListItemData;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;
import net.iaround.ui.adapter.poweradapter.PowerHolder;

/**
 * 语音主播界面适配器
 */
public class VoiceFragmentAdapter extends PowerAdapter<VoiceListItemData> {

    private Context mContext;

    private LayoutInflater inflater;

    public VoiceFragmentAdapter(Context context) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PowerHolder<VoiceListItemData> onViewHolderCreate(@NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.home_page_voice_item, parent, false);
        return new VoiceFragmentHolder(inflate);
    }

    @Override
    public void onViewHolderBind(@NonNull PowerHolder<VoiceListItemData> holder, int position) {
        holder.onBind(list.get(position));
    }


    private class VoiceFragmentHolder extends PowerHolder<VoiceListItemData> {

        private ImageView ivGamePerson;
        private TextView tvGamePersonName;
        private ImageView ivDiscountIcon;
        private TextView tvGamePersonRank;

        public VoiceFragmentHolder(View inflate) {
            super(inflate);
            ivGamePerson = (ImageView) inflate.findViewById(R.id.iv_game_person);
            tvGamePersonName = (TextView) inflate.findViewById(R.id.tv_game_person_name);
            ivDiscountIcon = (ImageView) inflate.findViewById(R.id.iv_discount_icon);
            tvGamePersonRank = (TextView) inflate.findViewById(R.id.tv_game_person_rank);
        }

        @Override
        public void onBind(VoiceListItemData voiceListItemData) {
            super.onBind(voiceListItemData);
            GlideUtil.loadImage(BaseApplication.appContext, voiceListItemData.icon, ivGamePerson, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
            if (!TextUtils.isEmpty(voiceListItemData.nickName)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, voiceListItemData.nickName, 0, null);
                tvGamePersonName.setText(spName);
            }
            switch (voiceListItemData.status) {
                case 1:
                    ivDiscountIcon.setImageResource(R.drawable.icon_idle);
                    tvGamePersonRank.setText(mContext.getResources().getString(R.string.voice_online));
                    break;
                case 2:
                    ivDiscountIcon.setImageResource(R.drawable.icon_undisturb);
                    tvGamePersonRank.setText(mContext.getResources().getString(R.string.voice_offline));
                    break;
                case 3:
                    ivDiscountIcon.setImageResource(R.drawable.icon_calling);
                    tvGamePersonRank.setText(mContext.getResources().getString(R.string.voice_line_busy));
                    break;
            }
        }
    }
}
