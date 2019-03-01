package net.iaround.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.model.entity.GameChatInfo;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.InnerJump;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.ChatBarPopularActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.GameListActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.NearActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.utils.OnClickUtil;

import java.util.ArrayList;

/**
 * Created by yz on 2018/7/30.
 */

public class HomePageAdapter extends BaseAdapter {
    private static final int GAME_TYPE = 0;
    private static final int GAME_INFO = 1;
    private static final int NEAR = 2;
    private static final int CHAT_BAR = 3;

    private Context mContext;
    private GameChatInfo mGameChatInfo = new GameChatInfo();
    private int mIndex;//

    private AdapterView.OnItemClickListener mGameTypeItemClick;//游戏分类点击事件

    public HomePageAdapter(Context context) {
        mContext = context;
    }

    public void updateData(GameChatInfo gameChatInfo) {
        mGameChatInfo = gameChatInfo;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mGameChatInfo != null) {
            if (mGameChatInfo.game_info != null) {
                if (mGameChatInfo.game_type != null) {

                    return mGameChatInfo.game_info.size() + 1;
                }
                return mGameChatInfo.game_info.size();
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mGameChatInfo != null) {
            if (position == 0) {
                return GAME_TYPE;
            }
            int type = 0;
            if (mGameChatInfo.game_info != null && mGameChatInfo.game_info.size() > 0) {

                type = mGameChatInfo.game_info.get(position - 1).mtype;
            }
            if (type == 1) {
                return GAME_INFO;
            } else if (type == 2) {
                return NEAR;
            } else if (type == 3) {
                return CHAT_BAR;
            }
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        //判断样式有几种
        if (mGameChatInfo.topbanners != null) {
            return 5;
        } else {
            return 4;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == GAME_TYPE) {
            MyGridView myGridView;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_game_type, null);
                myGridView = (MyGridView) convertView.findViewById(R.id.gv_home_page_game_type);
                convertView.setTag(myGridView);
            } else {
                myGridView = (MyGridView) convertView.getTag();
            }
            GameTypeAdapter adapter = new GameTypeAdapter();
            adapter.update(mGameChatInfo.game_type);
            myGridView.setAdapter(adapter);
            myGridView.setOnItemClickListener(mGameTypeItemClick);
        } else if (type == GAME_INFO) {
            GameInfoViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_game_info, null);
                viewHolder = new GameInfoViewHolder();
                viewHolder.myGridView = (MyGridView) convertView.findViewById(R.id.gv_home_page_game_info);

                viewHolder.tvGameInfoMore = (TextView) convertView.findViewById(R.id.tv_game_info_more);
                viewHolder.tvGameInfoName = (TextView) convertView.findViewById(R.id.tv_game_info_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GameInfoViewHolder) convertView.getTag();
            }
            if (mGameChatInfo.game_info != null && mGameChatInfo.game_info.get(position - 1) != null) {
                //TODO 此处有待优化
                viewHolder.gameInfoAdapter = new GameInfoAdapter();
                viewHolder.gameInfoAdapter.updateData(mGameChatInfo.game_info.get(position - 1).info);
                viewHolder.myGridView.setAdapter(viewHolder.gameInfoAdapter);

                viewHolder.tvGameInfoName.setText(mGameChatInfo.game_info.get(position - 1).game_name);
                viewHolder.tvGameInfoMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(OnClickUtil.isFastClick()){
                            return;
                        }
                        // 跳转陪玩分类详情
                        Intent intent = new Intent(mContext, GameListActivity.class);
                        intent.putExtra("GameId", mGameChatInfo.game_info.get(position - 1).game_id);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if (type == NEAR) {
            NearViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_game_info, null);
                viewHolder = new NearViewHolder();
                viewHolder.myGridView = (MyGridView) convertView.findViewById(R.id.gv_home_page_game_info);
                viewHolder.tvGameInfoMore = (TextView) convertView.findViewById(R.id.tv_game_info_more);
                viewHolder.tvGameInfoName = (TextView) convertView.findViewById(R.id.tv_game_info_name);
                viewHolder.myGridView.setNumColumns(3);
                viewHolder.nearAdapter = new NearAdapter();
                viewHolder.myGridView.setAdapter(viewHolder.nearAdapter);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (NearViewHolder) convertView.getTag();
            }
            if (mGameChatInfo.game_info != null && mGameChatInfo.game_info.get(position - 1) != null) {

                viewHolder.nearAdapter.update(mGameChatInfo.game_info.get(position - 1).info);
                viewHolder.tvGameInfoName.setText(R.string.near_personal);
                viewHolder.tvGameInfoMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到附近的人
                        if(OnClickUtil.isFastClick()){
                            return;
                        }
                        Intent intent = new Intent(mContext, NearActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }


        } else if (type == CHAT_BAR) {
            ChatBarViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ChatBarViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_game_info, null);
                viewHolder.myGridView = (MyGridView) convertView.findViewById(R.id.gv_home_page_game_info);
                viewHolder.tvGameInfoMore = (TextView) convertView.findViewById(R.id.tv_game_info_more);
                viewHolder.tvGameInfoName = (TextView) convertView.findViewById(R.id.tv_game_info_name);
                viewHolder.chatBarAdapter = new HomePageChatBarAdapter();
                viewHolder.myGridView.setAdapter(viewHolder.chatBarAdapter);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChatBarViewHolder) convertView.getTag();
            }
            if (mGameChatInfo.game_info != null && mGameChatInfo.game_info.get(position - 1) != null) {
                viewHolder.chatBarAdapter.updateData(mGameChatInfo.game_info.get(position - 1).info);
                viewHolder.tvGameInfoName.setText(R.string.chat_bar_text);
                viewHolder.tvGameInfoMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到聊吧
//                        ((MainFragmentActivity) mContext).mChatBarLy.performClick();
                        if(OnClickUtil.isFastClick()){
                            return;
                        }
                        Intent intent = new Intent(mContext, ChatBarPopularActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }

    class GameInfoViewHolder {
        public MyGridView myGridView;
        public TextView tvGameInfoName;
        public TextView tvGameInfoMore;
        public GameInfoAdapter gameInfoAdapter;
    }

    class NearViewHolder {
        public MyGridView myGridView;
        public TextView tvGameInfoName;
        public TextView tvGameInfoMore;
        public NearAdapter nearAdapter;
    }

    class ChatBarViewHolder {
        public MyGridView myGridView;
        public TextView tvGameInfoName;
        public TextView tvGameInfoMore;
        public HomePageChatBarAdapter chatBarAdapter;
    }

    //gameType游戏类型点击事件
    public void setGameTypeItemClick(AdapterView.OnItemClickListener itemClick) {
        mGameTypeItemClick = itemClick;
    }

    //游戏分类适配器
    class GameTypeAdapter extends BaseAdapter {
        ArrayList<GameChatInfo.GameType> gameTypes = new ArrayList<>();

        public void update(ArrayList<GameChatInfo.GameType> gameTypes) {
            if (gameTypes != null) {
                this.gameTypes = gameTypes;
//                this.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (gameTypes != null) {

                return gameTypes.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return gameTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GameTypeViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_game_type_item, null);
                viewHolder = new GameTypeViewHolder();
                viewHolder.ivGameType = (ImageView) convertView.findViewById(R.id.iv_game_type);
                viewHolder.tvGameTypeName = (TextView) convertView.findViewById(R.id.tv_game_tye_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GameTypeViewHolder) convertView.getTag();
            }
            GlideUtil.loadCircleImage(mContext, gameTypes.get(position).GameIcon, viewHolder.ivGameType);
            viewHolder.tvGameTypeName.setText(gameTypes.get(position).name);
            return convertView;
        }

        class GameTypeViewHolder {
            public ImageView ivGameType;
            public TextView tvGameTypeName;
        }
    }

    //游戏主播信息适配器
    class GameInfoAdapter extends BaseAdapter {
        ArrayList<GameChatInfo.InfoBean> gamePeople = new ArrayList<>();

        public void updateData(ArrayList<GameChatInfo.InfoBean> gamePeople) {
            if (gamePeople != null) {
                this.gamePeople.addAll(gamePeople);
//                this.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (gamePeople != null) {
                return gamePeople.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return gamePeople.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GameInfoItemViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_game_info_item, null);
                viewHolder = new GameInfoItemViewHolder();

                viewHolder.ivGamePerson = (ImageView) convertView.findViewById(R.id.iv_game_person);
                viewHolder.tvGamePersonName = (TextView) convertView.findViewById(R.id.tv_game_person_name);
                viewHolder.tvGamePersonRank = (TextView) convertView.findViewById(R.id.tv_game_person_rank);
                viewHolder.tvGamePersonOnline = (TextView) convertView.findViewById(R.id.tv_game_person_online);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GameInfoItemViewHolder) convertView.getTag();
            }
            final GameChatInfo.InfoBean person = gamePeople.get(position);
            GlideUtil.loadImage(BaseApplication.appContext, person.gamer_url, viewHolder.ivGamePerson, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
            if (person.rank != null && !TextUtils.isEmpty(person.rank.name)) {
                viewHolder.tvGamePersonRank.setVisibility(View.VISIBLE);
                GradientDrawable grad = (GradientDrawable) viewHolder.tvGamePersonRank.getBackground();
                grad.setColor(CommonFunction.getRankColor(person.rank.level));
                viewHolder.tvGamePersonRank.setText(person.rank.name + "");
            } else {
                viewHolder.tvGamePersonRank.setVisibility(View.INVISIBLE);
            }
            viewHolder.tvGamePersonOnline.setText(TimeFormat.timeFormat1(mContext, person.lastonlinetime));
            if (!TextUtils.isEmpty(person.name)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, person.name, 0, null);
                viewHolder.tvGamePersonName.setText(spName);
            }

            viewHolder.ivGamePerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(OnClickUtil.isFastClick()){
                        return;
                    }
                    InnerJump.JumpGamerDetail(mContext, person.gamer_id, person.uid);
                }
            });

            return convertView;
        }

        class GameInfoItemViewHolder {
            public ImageView ivGamePerson;
            public TextView tvGamePersonName;
            public TextView tvGamePersonRank;
            public TextView tvGamePersonOnline;
        }
    }


    //附近的人适配器
    class NearAdapter extends BaseAdapter {
        ArrayList<GameChatInfo.InfoBean> nearInfo = new ArrayList<>();

        public void update(ArrayList<GameChatInfo.InfoBean> nearInfo) {
            if (nearInfo != null) {
                this.nearInfo = nearInfo;
                this.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (nearInfo != null) {

                return nearInfo.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return nearInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NearViewItemHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_page_near, null);
                viewHolder = new NearViewItemHolder();
                viewHolder.ivNearIcon = (ImageView) convertView.findViewById(R.id.iv_near_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tvStatusDistance = (TextView) convertView.findViewById(R.id.tv_status_distance);
                viewHolder.ivSex = (ImageView) convertView.findViewById(R.id.iv_sex);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (NearViewItemHolder) convertView.getTag();
            }
            final GameChatInfo.InfoBean nearBean = nearInfo.get(position);
            GlideUtil.loadCircleImage(mContext, nearBean.icon, viewHolder.ivNearIcon);
            viewHolder.ivNearIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(OnClickUtil.isFastClick()){
                        return;
                    }
                    if (nearBean.uid == Common.getInstance().loginUser.getUid()) {
                        Intent intent = new Intent(mContext, UserInfoActivity.class);
                        intent.putExtra(Constants.UID, nearBean.uid);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, OtherInfoActivity.class);
                        intent.putExtra(Constants.UID, nearBean.uid);
                        mContext.startActivity(intent);
                    }
                }
            });
            viewHolder.tvName.setText(nearBean.name);
            if (nearBean.lastonlinetime > 0) {
                viewHolder.tvStatusDistance.setText(TimeFormat.timeFormat1(mContext, nearBean.lastonlinetime) + "  " + CommonFunction.covertSelfDistance(nearBean.distance));
            }

            if (!TextUtils.isEmpty(nearBean.nickname)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, nearBean.nickname, 0, null);
                viewHolder.tvName.setText(spName);
            }
            if ("m".equals(nearBean.gender)) {
                viewHolder.ivSex.setImageResource(R.drawable.bg_home_sex_man);
            } else {
                viewHolder.ivSex.setImageResource(R.drawable.bg_home_sex_girl);

            }
            return convertView;
        }

        class NearViewItemHolder {
            public ImageView ivNearIcon;
            public ImageView ivSex;
            public TextView tvName;
            public TextView tvStatusDistance;
        }
    }

    //聊吧适配器
    class HomePageChatBarAdapter extends BaseAdapter {
        ArrayList<GameChatInfo.InfoBean> charBar;

        public void updateData(ArrayList<GameChatInfo.InfoBean> charBar) {
            if (charBar != null) {
                this.charBar = charBar;
                this.notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (charBar != null) {
                return charBar.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return charBar.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CharBarItemViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_chat_bar_item, null);
                viewHolder = new CharBarItemViewHolder();

                viewHolder.ivChatBarIcon = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon);
                viewHolder.ivChatBarWave = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave);
                viewHolder.tvChatBarName = (TextView) convertView.findViewById(R.id.tv_chat_bar_name);
                viewHolder.tvChatBarHotFamily = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family);
                GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, viewHolder.ivChatBarWave);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CharBarItemViewHolder) convertView.getTag();
            }
            final GameChatInfo.InfoBean chatBarBean = charBar.get(position);
            GlideUtil.loadImage(BaseApplication.appContext, chatBarBean.url, viewHolder.ivChatBarIcon, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
            viewHolder.tvChatBarHotFamily.setText(chatBarBean.hot + "");
            viewHolder.ivChatBarIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(OnClickUtil.isFastClick()){
                        return;
                    }
                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
                    intent.putExtra("id", chatBarBean.groupid + "");
                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    GroupChatTopicActivity.ToGroupChatTopicActivity(mContext, intent);
                }
            });
            if (!TextUtils.isEmpty(chatBarBean.name)) {
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, chatBarBean.name, 0, null);
                viewHolder.tvChatBarName.setText(spName);
            }
            return convertView;
        }

        class CharBarItemViewHolder {
            public ImageView ivChatBarIcon;
            public ImageView ivChatBarWave;
            public TextView tvChatBarName;
            public TextView tvChatBarHotFamily;
        }
    }


}
