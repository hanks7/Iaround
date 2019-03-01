package net.iaround.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.DailyVoiceListItemData;
import net.iaround.tools.FaceManager;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;
import net.iaround.ui.adapter.poweradapter.PowerHolder;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

public class DailyVoiceActivityAdapter extends PowerAdapter<DailyVoiceListItemData> {

    private Context mContext;

    private LayoutInflater inflater;

    public DailyVoiceActivityAdapter(Context context) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PowerHolder<DailyVoiceListItemData> onViewHolderCreate(@NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.voice_daily_list_item, parent, false);
        return new VoiceFragmentHolder(inflate);
    }

    @Override
    public void onViewHolderBind(@NonNull PowerHolder<DailyVoiceListItemData> holder, int position) {
        holder.onBind(list.get(position));
    }


    private class VoiceFragmentHolder extends PowerHolder<DailyVoiceListItemData> {

        private HeadPhotoView userIconView;
        private TextView onlineTagView;
        private TextView userNameView;
        private TextView tvChatStatus;
        private TextView tvNotice;

        public VoiceFragmentHolder(View inflate) {
            super(inflate);
            userIconView = (HeadPhotoView) inflate.findViewById(R.id.friend_icon);
            onlineTagView = (TextView) inflate.findViewById(R.id.onlineTag);
            userNameView = (TextView) inflate.findViewById(R.id.userName);
            tvChatStatus = (TextView) inflate.findViewById(R.id.chat_status);
            tvNotice = (TextView) inflate.findViewById(R.id.tv_notice);
        }

        @Override
        public void onBind(final DailyVoiceListItemData voiceListItemData) {
            super.onBind(voiceListItemData);
            User user = new User();
            user.setIcon(voiceListItemData.icon);
            user.setUid(voiceListItemData.userid);
            userIconView.execute(ChatFromType.UNKONW, user, null);

            if (!TextUtils.isEmpty(voiceListItemData.nickname)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, voiceListItemData.nickname, 0, null);
                userNameView.setText(spName);
            }
            onlineTagView.setText(voiceListItemData.cost);
            tvChatStatus.setText(voiceListItemData.seconds);
            tvNotice.setText(voiceListItemData.chattime);
        }
    }
}
