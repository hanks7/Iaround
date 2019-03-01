package net.iaround.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.ranking.RankingEntity.RankingBean;
import net.iaround.model.ranking.RankingEntityType;
import net.iaround.model.ranking.SkillRankingInfoEntity.ListBean;
import net.iaround.model.ranking.SkillRankingInfoEntity.SkillBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.ui.view.ranking.RankingHeadview;

import java.util.List;

import static net.iaround.ui.view.ranking.RankingHeadview.iconsBaohufeiActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsBaohufeiGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsDamemeActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsDamemeGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJinguzhouActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJinguzhouGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJuhuancanActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsJuhuancanGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsWuyingjiaoActive;
import static net.iaround.ui.view.ranking.RankingHeadview.iconsWuyingjiaoGrow;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveBaohufei;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveDameme;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveJinguzhou;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveJuhuacan;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesActiveWuyingjiao;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesCharm;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowBaohufei;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowDameme;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowJinguzhou;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowJuhuacan;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesGrowWuyingjiao;
import static net.iaround.ui.view.ranking.RankingHeadview.titlesRegal;


public class RankingCommonAdapter extends BaseAdapter {
    private final int CHAT_BAR_HOUR_HOT_LIST = 1;
    private final int CHAT_BAR_OFFICAL_RECOMMENDATION = 2;
    public final int CHAT_BAR_ADVER = 3;
    private final int NORMAL_ITEM_TYPE = 0;
    private Context mContext;
    private List<RankingEntityType> rankingBeanList;
    private final int RANKING_ORDER_THREE = 1;
    private final int RANKING_ORDER_OTHER = 2;
    private int rankType;//1 charm 2 regal 3 skill

    public RankingCommonAdapter() {
        super();
    }

    public RankingCommonAdapter(Context context, int type, List<RankingEntityType> rankingBeanList) {
        this.mContext = context;
        this.rankType = type;
        this.rankingBeanList = rankingBeanList;
    }

    public int getCount() {
        if (rankingBeanList != null) {
            return rankingBeanList.size();
        }
        return 0;
    }

    public RankingEntityType getItem(int position) {
        if (rankingBeanList != null) {
            return rankingBeanList.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 3;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    public void updateData(List<RankingEntityType> rankingBeanList) {
        this.rankingBeanList = rankingBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int type = rankingBeanList.get(position).getType();
        if (type == RANKING_ORDER_THREE) {
            return RANKING_ORDER_THREE;
        } else if (type == RANKING_ORDER_OTHER) {
            return RANKING_ORDER_OTHER;
        } else {
            return RANKING_ORDER_OTHER;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (rankingBeanList != null) {

            int itemViewType = getItemViewType(position);
            final RankingEntityType item = rankingBeanList.get(position);
            switch (itemViewType) {
                case RANKING_ORDER_THREE:
//                    if (convertView == null) {
//                        convertView = new RankingHeadview(mContext, item, rankType,item.getShowType());
//                    } else {
//                        ((RankingHeadview) convertView).refreshView(item,rankType);
//                    }
                    convertView = new RankingHeadview(mContext, item, rankType,item.getShowType());
                    break;

                default:
                    RankingHolder holder = null;
                    if (convertView == null || holder == null) {
                        holder = new RankingHolder();
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_charm_fragment, null);
                        holder.headIcon = (HeadPhotoView) convertView.findViewById(R.id.user_icon);
                        holder.tvNickName = (TextView) convertView.findViewById(R.id.user_name);
                        holder.lyAge = (LinearLayout) convertView.findViewById(R.id.ly_user_age_left);
                        holder.ivAge = (ImageView) convertView.findViewById(R.id.iv_user_sex_left);
                        holder.tvAge = (TextView) convertView.findViewById(R.id.tv_user_age_left);
//                        holder.tvNotes = (TextView) convertView.findViewById(R.id.user_notes);
                        holder.tvWealth = (TextView) convertView.findViewById(R.id.tv_charm_fragment_person_wealth);
                        holder.tvRaningOrder = (TextView) convertView.findViewById(R.id.tv_item_rank_order);
                        holder.tvWealthWord = (TextView) convertView.findViewById(R.id.tv_charm_fragment_wealth_word);
                        holder.ivSkillTitle = (TextView) convertView.findViewById(R.id.tv_ranking_title);
                        holder.userTitleView = (UserTitleView) convertView.findViewById(R.id.rank_item_user_title);
                        convertView.setTag(holder);
                    } else {
                        holder = (RankingHolder) convertView.getTag();
                    }
                    if (rankType == 1) {
                        holder.tvWealthWord.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_charm));
                        holder.tvWealthWord.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
                        holder.tvWealth.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
                    } else if(rankType == 2){
                        holder.tvWealthWord.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_regal));
                        holder.tvWealthWord.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
                        holder.tvWealth.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
                    }else {
                        holder.tvWealthWord.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
                        holder.tvWealth.setVisibility(View.GONE);
                    }
                    //展示的逻辑
                    if (item != null) {
                        if( item.getObject() instanceof RankingBean){
                            final RankingBean rankingBean = (RankingBean) item.getObject();
                            SpannableString spName;
                            if(!TextUtils.isEmpty(rankingBean.getNotes())){
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, rankingBean.getNotes(),
                                        0, null);
                            }else if(!TextUtils.isEmpty(rankingBean.getNickname())){
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, rankingBean.getNickname(),
                                        0, null);
                            }else {
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, rankingBean.getUserid() + "",
                                        0, null);
                            }
                            holder.tvNickName.setText(spName + "");
                            if (rankingBean.getSvip() > 0) {//yuchao  将vip改为svip
                                holder.tvNickName.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                holder.tvNickName.setTextColor(Color.parseColor("#000000"));
                            }
                            final User user = new User();
                            user.setUid(rankingBean.getUserid());
                            user.setIcon(rankingBean.getIcon());
                            user.setSVip(rankingBean.getSvip());
                            user.setViplevel(rankingBean.getViplevel());
                            user.setNoteName(rankingBean.getNotes());
                            user.setAge(rankingBean.getAge());
                            holder.headIcon.execute(ChatFromType.UNKONW, user, null);
//                        holder.tvNotes.setText(rankingBean.getNotes());
                            holder.tvWealth.setText(rankingBean.getLove() + "");
                            if ("m".equals(rankingBean.getGender())) {
                                holder.ivAge.setImageResource(R.drawable.thread_register_man_select);
                                holder.lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                holder.ivAge.setImageResource(R.drawable.thread_register_woman_select);
                                holder.lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            try {
                                holder.tvAge.setText("" + rankingBean.getAge());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (rankingBean.getIndex() > 99) {
                                holder.tvRaningOrder.setTextSize(9);
                            }else {
                                holder.tvRaningOrder.setTextSize(12);
                            }
                            holder.tvRaningOrder.setText("" + rankingBean.getIndex());
                            if(rankingBean.getIndex() >= 4&&rankingBean.getIndex()<=10){
                                if(rankType ==1) {
                                    if(item.getShowType() == 1 || item.getShowType() == 3){
                                        holder.userTitleView.setText(titlesCharm[3]);
                                        holder.userTitleView.setTitleBackground(R.drawable.chat_bar_recuit_charm_four);
                                    }else {
                                        holder.userTitleView.setVisibility(View.GONE);
                                    }

                                }else {
                                    if(item.getShowType() == 1 || item.getShowType() == 3){
                                        holder.userTitleView.setText(titlesRegal[3]);
                                        holder.userTitleView.setTitleBackground(R.drawable.chat_bar_recuit_regal_four);
                                    }else {
                                        holder.userTitleView.setVisibility(View.GONE);
                                    }

                                }
                            }else {
                                holder.userTitleView.setVisibility(View.GONE);
                            }

//                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.ivSkillTitle.getLayoutParams();
//                            layoutParams.width = ScreenUtils.dp2px(52);
//                            layoutParams.height = ScreenUtils.dp2px(22);
//                            holder.ivSkillTitle.setLayoutParams(layoutParams);

                            convertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (rankingBean.getUserid() == Common.getInstance().loginUser.getUid()) {
                                        Intent intent = new Intent(mContext, UserInfoActivity.class);
                                        intent.putExtra(Constants.UID, Common.getInstance().loginUser.getUid());
                                        mContext.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(mContext, OtherInfoActivity.class);
                                        intent.putExtra(Constants.UID, user.getUid());
                                        intent.putExtra("user", user);
                                        mContext.startActivity(intent);
                                    }
                                }
                            });
                        }else {
                            final ListBean rankingBean = (ListBean) item.getObject();
                            SkillBean skillBean = (SkillBean) item.getSkillObject();
                            if(skillBean == null){
                                break;
                            }
                            SpannableString spName;
                            if(!TextUtils.isEmpty(rankingBean.getNotes())){
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, rankingBean.getNotes(),
                                        0, null);
                            }else if(!TextUtils.isEmpty(rankingBean.getNickName())){
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, rankingBean.getNickName(),
                                        0, null);
                            }else {
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, rankingBean.getUserID() + "",
                                        0, null);
                            }
                            holder.tvNickName.setText(spName + "");
                            if (rankingBean.getVIP() > 0) {//yuchao  将vip改为svip
                                holder.tvNickName.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                holder.tvNickName.setTextColor(Color.parseColor("#000000"));
                            }
                            final User user = new User();
                            user.setUid(rankingBean.getUserID());
                            user.setIcon(rankingBean.getICON());
                            user.setSVip(rankingBean.getVIP());
                            user.setViplevel(rankingBean.getVipLevel());
                            user.setNoteName(rankingBean.getNotes());
                            user.setAge(rankingBean.getAge());
                            holder.headIcon.execute(ChatFromType.UNKONW, user, null);
//                        holder.tvNotes.setText(rankingBean.getNotes());
//                            holder.tvWealth.setText(rankingBean.getLove() + "");
                            if ("m".equals(rankingBean.getGender())) {
                                holder.ivAge.setImageResource(R.drawable.thread_register_man_select);
                                holder.lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                holder.ivAge.setImageResource(R.drawable.thread_register_woman_select);
                                holder.lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            try {
                                holder.tvAge.setText("" + rankingBean.getAge());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (rankingBean.getIndex() > 99) {
                                holder.tvRaningOrder.setTextSize(9);
                            }else {
                                holder.tvRaningOrder.setTextSize(12);
                            }
                            holder.tvRaningOrder.setText("" + rankingBean.getIndex());
                            String skillName = CommonFunction.getLangText(skillBean.getName());
                            if(item.getSkillType().equals("update")){
                                holder.tvWealthWord.setText(mContext.getResources().getString(R.string.rank_skill_info_user_skill_grow)+skillName+mContext.getResources().getString(R.string.rank_skill_info_user_info_skill_name)+rankingBean.getCount()+mContext.getResources().getString(R.string.rank_skill_info_user_skill_times));
                            }else {
                                holder.tvWealthWord.setText(mContext.getResources().getString(R.string.rank_skill_info_user_skill)+skillName+mContext.getResources().getString(R.string.rank_skill_info_user_info_skill_name)+rankingBean.getCount()+mContext.getResources().getString(R.string.rank_skill_info_user_skill_times));
                            }


                            if(rankingBean.getIndex() >= 4&&rankingBean.getIndex()<=10){
                                if(rankType == 3) {
                                    showSkillRanking(item.getSkillType(),skillBean.getID(),4,holder.userTitleView,item.getShowType());
                                }
                            }else {
                                holder.ivSkillTitle.setVisibility(View.GONE);
                            }

                            convertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (rankingBean.getUserID() == Common.getInstance().loginUser.getUid()) {
                                        Intent intent = new Intent(mContext, UserInfoActivity.class);
                                        intent.putExtra(Constants.UID, Common.getInstance().loginUser.getUid());
                                        mContext.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(mContext, OtherInfoActivity.class);
                                        intent.putExtra(Constants.UID, user.getUid());
                                        intent.putExtra("user", user);
                                        mContext.startActivity(intent);
                                    }
                                }
                            });
                        }

                    }
                    break;

            }
        }


        return convertView;
    }

    private class RankingHolder {
        HeadPhotoView headIcon;
        TextView tvNickName;
        LinearLayout lyAge;
        ImageView ivAge;
        TextView tvAge;
        TextView tvWealth;
        TextView tvWealthWord;
        TextView tvNotes;
        TextView tvRaningOrder;
        TextView ivSkillTitle;
        UserTitleView userTitleView;//称号
    }

    private void  showSkillRanking(String activeOrGrow,int skillType,int index,UserTitleView imageView,int showType){
        if(showType == 1 || showType == 3){
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
            return;
        }
        if("active".equals(activeOrGrow)){
            if(skillType ==1){
                if(index >=4){
                    imageView.setText(titlesActiveJuhuacan[3]);
                    imageView.setTitleBackground(iconsJuhuancanActive[3]);
                }

            }else if(skillType == 2){
                if(index >=4){
                    imageView.setText(titlesActiveDameme[3]);
                    imageView.setTitleBackground(iconsDamemeActive[3]);

                }

            }
            else if(skillType == 3){
                if(index >=4){
                    imageView.setText(titlesActiveJinguzhou[3]);
                    imageView.setTitleBackground(iconsJinguzhouActive[3]);

                }

            }else if(skillType == 4){
                if(index >=4){
                    imageView.setText(titlesActiveWuyingjiao[3]);
                    imageView.setTitleBackground(iconsWuyingjiaoActive[3]);

                }

            }else if(skillType == 5){
                if(index >=4){
                    imageView.setText(titlesActiveBaohufei[3]);
                    imageView.setTitleBackground(iconsBaohufeiActive[3]);

                }

            }

        }else if("update".equals(activeOrGrow)){
            if(skillType ==1){
                if(index >=4){
                    imageView.setText(titlesGrowJuhuacan[3]);
                    imageView.setTitleBackground(iconsJuhuancanGrow[3]);
                }

            }else if(skillType == 2){
                if(index >=4){
                    imageView.setText(titlesGrowDameme[3]);
                    imageView.setTitleBackground(iconsDamemeGrow[3]);

                }

            }
            else if(skillType == 3){
                if(index >=4){
                    imageView.setText(titlesGrowJinguzhou[3]);
                    imageView.setTitleBackground(iconsJinguzhouGrow[3]);

                }

            }else if(skillType == 4){
                if(index >=4){
                    imageView.setText(titlesGrowWuyingjiao[3]);
                    imageView.setTitleBackground(iconsWuyingjiaoGrow[3]);

                }

            }else if(skillType == 5){
                if(index >=4){
                    imageView.setText(titlesGrowBaohufei[3]);
                    imageView.setTitleBackground(iconsBaohufeiGrow[3]);

                }

            }

        }

    }


}
