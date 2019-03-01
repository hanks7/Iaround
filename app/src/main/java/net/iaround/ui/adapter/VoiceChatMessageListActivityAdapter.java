package net.iaround.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.VoiceChatMessageListItemData;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;
import net.iaround.ui.adapter.poweradapter.PowerHolder;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

public class VoiceChatMessageListActivityAdapter extends PowerAdapter<VoiceChatMessageListItemData> {

    private Context mContext;

    private LayoutInflater inflater;

    public VoiceChatMessageListActivityAdapter(Context context) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PowerHolder<VoiceChatMessageListItemData> onViewHolderCreate(@NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.voice_chat_msg_list_item, parent, false);
        return new VoiceFragmentHolder(inflate);
    }

    @Override
    public void onViewHolderBind(@NonNull PowerHolder<VoiceChatMessageListItemData> holder, int position) {
        holder.onBind(list.get(position));
    }


    private class VoiceFragmentHolder extends PowerHolder<VoiceChatMessageListItemData> {

        private HeadPhotoView userIconView;
        private TextView onlineTagView;
        private TextView userNameView;
        private TextView tvNotice;
        private View line;

        public VoiceFragmentHolder(View inflate) {
            super(inflate);
            userIconView = (HeadPhotoView) inflate.findViewById(R.id.friend_icon);
            onlineTagView = (TextView) inflate.findViewById(R.id.onlineTag);
            userNameView = (TextView) inflate.findViewById(R.id.userName);
            tvNotice = (TextView) inflate.findViewById(R.id.tv_notice);
            line = inflate.findViewById(R.id.divider_view);
        }

        @Override
        public void onBind(final VoiceChatMessageListItemData voiceListItemData) {
            super.onBind(voiceListItemData);
            User user = new User();
            user.setIcon(voiceListItemData.icon);
            user.setSVip(voiceListItemData.svip);//YC   将vip改为svip
            user.setViplevel(voiceListItemData.vip);//YC 添加viplevel字段
            user.setUid(voiceListItemData.userid);
            userIconView.execute(ChatFromType.UNKONW, user, null);
            if (voiceListItemData.svip > 0) {//yuchao  将vip改为svip
                userNameView.setTextColor(Color.parseColor("#FF4064"));
            } else {
                userNameView.setTextColor(Color.parseColor("#000000"));
            }
            onlineTagView.setText(TimeFormat.timeFormat4(mContext, voiceListItemData.chattime*1000));
            if (!TextUtils.isEmpty(voiceListItemData.nickname)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, voiceListItemData.nickname, 0, null);
                userNameView.setText(spName);
            }
            switch (voiceListItemData.state) {
                case 1:
                    tvNotice.setText(mContext.getResources().getString(R.string.duration) + "   " + TimeFormat.timeParse(voiceListItemData.seconds));
                    break;
                case 2:
                    tvNotice.setText(mContext.getResources().getString(R.string.not_connected));
                    break;
            }
        }
    }
}
