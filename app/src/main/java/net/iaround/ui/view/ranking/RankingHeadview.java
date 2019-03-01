package net.iaround.ui.view.ranking;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.ranking.RankingEntity.RankingBean;
import net.iaround.model.ranking.RankingEntityType;
import net.iaround.model.ranking.SkillRankingInfoEntity.ListBean;
import net.iaround.model.ranking.SkillRankingInfoEntity.SkillBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.utils.ScreenUtils;

import java.util.List;

/**
 * Created by Ray on 2017/6/17.
 */

public class RankingHeadview extends LinearLayout {
    private Context mContext;
    private RankingEntityType mRankingEntityType;
    private int mRankType;

    private ImageView ivSecond;
    private HeadPhotoView hpSecond;
    private TextView tvRankingNameSecond;
    private LinearLayout llRaningSexSecond;
    private ImageView ivSexSecond;
    private TextView tvAgeSecond;
    private TextView tvCharmTextSecond;
    private TextView tvCharmNumSecond;
//    private TextView ivCharmNumSecond;

    private ImageView ivFirst;
    private HeadPhotoView hpFirst;
    private TextView tvRankingNameFirst;
    private LinearLayout llRaningSexFirst;
    private ImageView ivSexFirst;
    private TextView tvAgeFirst;
    private TextView tvCharmTextFirst;
    private TextView tvCharmNumFirst;
//    private TextView ivCharmNumFirst;

    private ImageView ivThird;
    private HeadPhotoView hpThird;
    private TextView tvRankingNameThird;
    private LinearLayout llRaningSexThird;
    private ImageView ivSexThird;
    private TextView tvAgeThird;
    private TextView tvCharmTextThird;
    private TextView tvCharmNumThird;
//    private TextView ivCharmNumThird;

    private UserTitleView userTitleViewFirst;
    private UserTitleView userTitleViewSecond;
    private UserTitleView userTitleViewThird;

    private LinearLayout llRankingFirst;
    private LinearLayout llRankingSecond;
    private LinearLayout llRankingThird;
    private LinearLayout llRankHeight;

    /**
     * 选中的星星资源id
     */
    public static final int iconsCharm[] = {
            R.drawable.chat_bar_recuit_charm_one,
            R.drawable.chat_bar_recuit_charm_two,
            R.drawable.chat_bar_recuit_charm_three,
            R.drawable.chat_bar_recuit_charm_four,
    };

    public static final int iconsRegal[] = {
            R.drawable.chat_bar_recuit_regal_one,
            R.drawable.chat_bar_recuit_regal_two,
            R.drawable.chat_bar_recuit_regal_three,
            R.drawable.chat_bar_recuit_regal_four,
    };

    public static final int iconsDamemeActive[] = {
            R.drawable.skill_active_dameme_one,
            R.drawable.skill_active_dameme_two,
            R.drawable.skill_active_dameme_three,
            R.drawable.skill_active_dameme_four,
    };
    public static final int iconsBaohufeiActive[] = {
            R.drawable.skill_active_baohufei_one,
            R.drawable.skill_active_baohufei_two,
            R.drawable.skill_active_baohufei_three,
            R.drawable.skill_active_baohufei_four,
    };
    public static final int iconsJinguzhouActive[] = {
            R.drawable.skill_active_jinguzhou_one,
            R.drawable.skill_active_jinguzhou_two,
            R.drawable.skill_active_jinguzhou_three,
            R.drawable.skill_active_jinguzhou_four,
    };
    public static final int iconsWuyingjiaoActive[] = {
            R.drawable.skill_active_wuyingjiao_one,
            R.drawable.skill_active_wuyingjiao_two,
            R.drawable.skill_active_wuyingjiao_three,
            R.drawable.skill_active_wuyingjiao_four,
    };
    public static final int iconsJuhuancanActive[] = {
            R.drawable.skill_active_juhuacan_one,
            R.drawable.skill_active_juhuacan_two,
            R.drawable.skill_active_juhuacan_three,
            R.drawable.skill_active_juhuacan_four,
    };

    public static final int iconsDamemeGrow[] = {
            R.drawable.skill_grow_dameme_one,
            R.drawable.skill_grow_dameme_two,
            R.drawable.skill_grow_dameme_three,
            R.drawable.skill_grow_dameme_four,
    };
    public static final int iconsBaohufeiGrow[] = {
            R.drawable.skill_grow_baohufei_one,
            R.drawable.skill_grow_baohufei_two,
            R.drawable.skill_grow_baohufei_three,
            R.drawable.skill_grow_baohufei_four,
    };
    public static final int iconsJinguzhouGrow[] = {
            R.drawable.skill_grow_jinguzhou_one,
            R.drawable.skill_grow_jinguzhou_two,
            R.drawable.skill_grow_jinguzhou_three,
            R.drawable.skill_grow_jinguzhou_four,
    };
    public static final int iconsWuyingjiaoGrow[] = {
            R.drawable.skill_grow_wuyingjiao_one,
            R.drawable.skill_grow_wuyingjiao_two,
            R.drawable.skill_grow_wuyingjiao_three,
            R.drawable.skill_grow_wuyingjiao_four,
    };
    public static final int iconsJuhuancanGrow[] = {
            R.drawable.skill_grow_juhuacan_one,
            R.drawable.skill_grow_juhuacan_two,
            R.drawable.skill_grow_juhuacan_three,
            R.drawable.skill_grow_juhuacan_four,
    };

    public static final String titlesActiveJuhuacan[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_interactive_rank_1);
    public static final String titlesActiveDameme[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_interactive_rank_2);
    public static final String titlesActiveBaohufei[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_interactive_rank_5);
    public static final String titlesActiveWuyingjiao[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_interactive_rank_4);
    public static final String titlesActiveJinguzhou[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_interactive_rank_3);

    public static final String titlesGrowJuhuacan[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_grow_rank_1);
    public static final String titlesGrowDameme[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_grow_rank_2);
    public static final String titlesGrowBaohufei[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_grow_rank_5);
    public static final String titlesGrowWuyingjiao[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_grow_rank_4);
    public static final String titlesGrowJinguzhou[] = BaseApplication.appContext.getResources().getStringArray(R.array.skill_grow_rank_3);

    public static  final String titlesCharm[] = BaseApplication.appContext.getResources().getStringArray(R.array.charm_rank);
    public static  final String titlesRegal[] = BaseApplication.appContext.getResources().getStringArray(R.array.regal_rank);


    private int starSelectedDrawable = iconsDamemeActive[0];


    public RankingHeadview(Context context) {
        super(context);
    }

    public RankingHeadview(Context context, RankingEntityType rankingEntityType, int rankingType, int showType) {
        super(context);
        this.mContext = context;
        this.mRankingEntityType = rankingEntityType;
        this.mRankType = rankingType;

        initView(showType);

        refreshView(rankingEntityType,mRankType);
    }

    public void refreshView(RankingEntityType rankingBeanType,int mRankType) {
        llRankingFirst.setVisibility(INVISIBLE);
        llRankingSecond.setVisibility(INVISIBLE);
        llRankingThird.setVisibility(INVISIBLE);
        if (rankingBeanType != null) {
            if (rankingBeanType.isSkill()) {
                List<ListBean> rankingBeanList = (List<ListBean>) rankingBeanType.getObject();
                SkillBean skillBean = (SkillBean) rankingBeanType.getSkillObject();
                for(int i = 0;i < rankingBeanList.size();i++){
                    switch (i){
                        case 0:
                            llRankingFirst.setVisibility(VISIBLE);
                            ListBean first = rankingBeanList.get(0);
                            User userFirst = new User();
                            userFirst.setUid(first.getUserID());
                            userFirst.setIcon(first.getICON());
                            userFirst.setSVip(first.getVIP());
                            userFirst.setViplevel(first.getVipLevel());
                            userFirst.setNoteName(first.getNotes());
                            userFirst.setAge(first.getAge());
                            hpFirst.execute(ChatFromType.UNKONW, userFirst, null);
                            SpannableString spNameFirst = FaceManager.getInstance(mContext).parseIconForString(mContext, first.getNickName(),
                                    0, null);
                            tvRankingNameFirst.setText(spNameFirst + "");
                            if (first.getVIP() > 0) {//yuchao  将vip改为svip
                                tvRankingNameFirst.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                tvRankingNameFirst.setTextColor(Color.parseColor("#000000"));
                            }
                            if ("m".equals(first.getGender())) {
                                ivSexFirst.setImageResource(R.drawable.thread_register_man_select);
                                llRaningSexFirst.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                ivSexFirst.setImageResource(R.drawable.thread_register_woman_select);
                                llRaningSexFirst.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            tvAgeFirst.setText("" + first.getAge());
                            String skillNameFir = CommonFunction.getLangText(skillBean.getName());
                            if(rankingBeanType.getSkillType().equals("update")){
                                tvCharmTextFirst.setText(getContext().getString(R.string.rank_skill_info_user_skill_grow)+skillNameFir+getContext().getString(R.string.rank_skill_info_user_info_skill_name)+first.getCount()+getContext().getString(R.string.rank_skill_info_user_skill_times));
                            }else {
                                tvCharmTextFirst.setText(getContext().getString(R.string.rank_skill_info_user_skill)+skillNameFir+getContext().getString(R.string.rank_skill_info_user_info_skill_name)+first.getCount()+getContext().getString(R.string.rank_skill_info_user_skill_times));
                            }
                            showSkillRanking(rankingBeanType.getSkillType(),skillBean.getID(),1,userTitleViewFirst,userTitleViewSecond,userTitleViewThird,rankingBeanType.getShowType());

                            break;
                        case 1:
                            llRankingSecond.setVisibility(VISIBLE);
                            ListBean second = rankingBeanList.get(1);
                            User userSecond = new User();
                            userSecond.setUid(second.getUserID());
                            userSecond.setIcon(second.getICON());
                            userSecond.setSVip(second.getVIP());
                            userSecond.setViplevel(second.getVipLevel());
                            userSecond.setNoteName(second.getNotes());
                            userSecond.setAge(second.getAge());
                            hpSecond.execute(ChatFromType.UNKONW, userSecond, null);
                            SpannableString spNameSecond = FaceManager.getInstance(mContext).parseIconForString(mContext, second.getNickName(),
                                    0, null);
                            tvRankingNameSecond.setText(spNameSecond + "");
                            if (second.getVIP() > 0) {//yuchao  将vip改为svip
                                tvRankingNameSecond.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                tvRankingNameSecond.setTextColor(Color.parseColor("#000000"));
                            }
                            if ("m".equals(second.getGender())) {
                                ivSexSecond.setImageResource(R.drawable.thread_register_man_select);
                                llRaningSexSecond.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                ivSexSecond.setImageResource(R.drawable.thread_register_woman_select);
                                llRaningSexSecond.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            tvAgeSecond.setText("" + second.getAge());
                            String skillNameSec = CommonFunction.getLangText(skillBean.getName());
                            if(rankingBeanType.getSkillType().equals("update")){
                                tvCharmTextSecond.setText(getContext().getString(R.string.rank_skill_info_user_skill_grow)+skillNameSec+getContext().getString(R.string.rank_skill_info_user_info_skill_name)+second.getCount()+getContext().getString(R.string.rank_skill_info_user_skill_times));
                            }else {
                                tvCharmTextSecond.setText(getContext().getString(R.string.rank_skill_info_user_skill)+skillNameSec+getContext().getString(R.string.rank_skill_info_user_info_skill_name)+second.getCount()+getContext().getString(R.string.rank_skill_info_user_skill_times));
                            }

                            showSkillRanking(rankingBeanType.getSkillType(),skillBean.getID(),2,userTitleViewFirst,userTitleViewSecond,userTitleViewThird,rankingBeanType.getShowType());

                            break;
                        case 2:
                            llRankingThird.setVisibility(VISIBLE);
                            ListBean third = rankingBeanList.get(2);
                            User userThird = new User();
                            userThird.setUid(third.getUserID());
                            userThird.setIcon(third.getICON());
                            userThird.setSVip(third.getVIP());
                            userThird.setViplevel(third.getVipLevel());
                            userThird.setNoteName(third.getNotes());
                            userThird.setAge(third.getAge());
                            hpThird.execute(ChatFromType.UNKONW, userThird, null);
                            SpannableString spNameThird = FaceManager.getInstance(mContext).parseIconForString(mContext, third.getNickName(),
                                    0, null);
                            tvRankingNameThird.setText(spNameThird + "");
                            if (third.getVIP() > 0) {//yuchao  将vip改为svip
                                tvRankingNameThird.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                tvRankingNameThird.setTextColor(Color.parseColor("#000000"));
                            }
                            if ("m".equals(third.getGender())) {
                                ivSexThird.setImageResource(R.drawable.thread_register_man_select);
                                llRaningSexThird.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                ivSexThird.setImageResource(R.drawable.thread_register_woman_select);
                                llRaningSexThird.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            tvAgeThird.setText("" + third.getAge());
                            String skillNameThird = CommonFunction.getLangText(skillBean.getName());
//                    tvCharmNumThird.setText("" + third.getLove());
                            if(rankingBeanType.getSkillType().equals("update")){
                                tvCharmTextThird.setText(getContext().getString(R.string.rank_skill_info_user_skill_grow)+skillNameThird+getContext().getString(R.string.rank_skill_info_user_info_skill_name)+third.getCount()+getContext().getString(R.string.rank_skill_info_user_skill_times));
                            }else {
                                tvCharmTextThird.setText(getContext().getString(R.string.rank_skill_info_user_skill)+skillNameThird+getContext().getString(R.string.rank_skill_info_user_info_skill_name)+third.getCount()+getContext().getString(R.string.rank_skill_info_user_skill_times));
                            }
                            showSkillRanking(rankingBeanType.getSkillType(),skillBean.getID(),3,userTitleViewFirst,userTitleViewSecond,userTitleViewThird,rankingBeanType.getShowType());

                            break;
                    }
                }

            } else {
                List<RankingBean> rankingBeanList = (List<RankingBean>) rankingBeanType.getObject();
                for(int i = 0;i < rankingBeanList.size();i++){
                    switch (i){
                        case 0:
                            llRankingFirst.setVisibility(VISIBLE);
                            RankingBean first = rankingBeanList.get(0);
                            User userFirst = new User();
                            userFirst.setUid(first.getUserid());
                            userFirst.setIcon(first.getIcon());
                            userFirst.setSVip(first.getSvip());
                            userFirst.setViplevel(first.getViplevel());
                            userFirst.setNoteName(first.getNotes());
                            userFirst.setAge(first.getAge());
                            hpFirst.execute(ChatFromType.UNKONW, userFirst, null);
                            SpannableString spNameFirst = FaceManager.getInstance(mContext).parseIconForString(mContext, first.getNickname(),
                                    0, null);
                            tvRankingNameFirst.setText(spNameFirst + "");
                            if (first.getVip() > 0) {//yuchao  将vip改为svip
                                tvRankingNameFirst.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                tvRankingNameFirst.setTextColor(Color.parseColor("#000000"));
                            }
                            if ("m".equals(first.getGender())) {
                                ivSexFirst.setImageResource(R.drawable.thread_register_man_select);
                                llRaningSexFirst.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                ivSexFirst.setImageResource(R.drawable.thread_register_woman_select);
                                llRaningSexFirst.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            tvAgeFirst.setText("" + first.getAge());
                            tvCharmNumFirst.setText("" + first.getLove());
                            showRankingTitle(mRankType,1,userTitleViewFirst,userTitleViewSecond,userTitleViewThird,rankingBeanType.getShowType());
                            break;
                        case 1:
                            llRankingSecond.setVisibility(VISIBLE);
                            RankingBean second = rankingBeanList.get(1);
                            User userSecond = new User();
                            userSecond.setUid(second.getUserid());
                            userSecond.setIcon(second.getIcon());
                            userSecond.setSVip(second.getSvip());
                            userSecond.setViplevel(second.getViplevel());
                            userSecond.setNoteName(second.getNotes());
                            userSecond.setAge(second.getAge());
                            hpSecond.execute(ChatFromType.UNKONW, userSecond, null);
                            SpannableString spNameSecond = FaceManager.getInstance(mContext).parseIconForString(mContext, second.getNickname(),
                                    0, null);
                            tvRankingNameSecond.setText(spNameSecond + "");
                            if (second.getVip() > 0) {//yuchao  将vip改为svip
                                tvRankingNameSecond.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                tvRankingNameSecond.setTextColor(Color.parseColor("#000000"));
                            }
                            if ("m".equals(second.getGender())) {
                                ivSexSecond.setImageResource(R.drawable.thread_register_man_select);
                                llRaningSexSecond.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                ivSexSecond.setImageResource(R.drawable.thread_register_woman_select);
                                llRaningSexSecond.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            tvAgeSecond.setText("" + second.getAge());
                            tvCharmNumSecond.setText("" + second.getLove());
                            showRankingTitle(mRankType,2,userTitleViewFirst,userTitleViewSecond,userTitleViewThird,rankingBeanType.getShowType());

                            break;
                        case 2:
                            llRankingThird.setVisibility(VISIBLE);
                            RankingBean third = rankingBeanList.get(2);
                            User userThird = new User();
                            userThird.setUid(third.getUserid());
                            userThird.setIcon(third.getIcon());
                            userThird.setSVip(third.getSvip());
                            userThird.setViplevel(third.getViplevel());
                            userThird.setNoteName(third.getNotes());
                            userThird.setAge(third.getAge());
                            hpThird.execute(ChatFromType.UNKONW, userThird, null);
                            SpannableString spNameThird = FaceManager.getInstance(mContext).parseIconForString(mContext, third.getNickname(),
                                    0, null);
                            tvRankingNameThird.setText(spNameThird + "");
                            if (third.getVip() > 0) {//yuchao  将vip改为svip
                                tvRankingNameThird.setTextColor(Color.parseColor("#FF4064"));
                            } else {
                                tvRankingNameThird.setTextColor(Color.parseColor("#000000"));
                            }
                            if ("m".equals(third.getGender())) {
                                ivSexThird.setImageResource(R.drawable.thread_register_man_select);
                                llRaningSexThird.setBackgroundResource(R.drawable.encounter_man_circel_bg);

                            } else {
                                ivSexThird.setImageResource(R.drawable.thread_register_woman_select);
                                llRaningSexThird.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
                            }
                            tvAgeThird.setText("" + third.getAge());
                            tvCharmNumThird.setText("" + third.getLove());
                            showRankingTitle(mRankType,3,userTitleViewFirst,userTitleViewSecond,userTitleViewThird,rankingBeanType.getShowType());
                            break;
                    }
                }

            }
        }

    }

    private void initView(int rankType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_ranking_charm_fragment_headview, this);
        llRankHeight = (LinearLayout) itemView.findViewById(R.id.ll_rank_height);
        if(rankType == 2 || rankType == 4){
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) llRankHeight.getLayoutParams();
            if(linearParams != null){
                linearParams.height = ScreenUtils.dp2px(150);
                itemView.setLayoutParams(linearParams);
            }

        }else {
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) llRankHeight.getLayoutParams();
            if(linearParams != null){
                linearParams.height = ScreenUtils.dp2px(184);
                itemView.setLayoutParams(linearParams);
            }

        }

        ivFirst = (ImageView) itemView.findViewById(R.id.iv_headview_crown_first);
        hpFirst = (HeadPhotoView) itemView.findViewById(R.id.iv_headview_charm_first);
        tvRankingNameFirst = (TextView) itemView.findViewById(R.id.tv_headview_person_first);
        llRaningSexFirst = (LinearLayout) itemView.findViewById(R.id.ly_user_age_first);
        ivSexFirst = (ImageView) itemView.findViewById(R.id.iv_user_sex_first);
        tvAgeFirst = (TextView) itemView.findViewById(R.id.tv_user_age_first);
        tvCharmTextFirst = (TextView) itemView.findViewById(R.id.tv_charm_num_text_first);
        tvCharmNumFirst = (TextView) itemView.findViewById(R.id.tv_charm_num_first);
//        ivCharmNumFirst = (TextView) itemView.findViewById(R.id.iv_ranking_first_title);
        userTitleViewFirst = (UserTitleView) itemView.findViewById(R.id.rank_user_title_first);


        hpSecond = (HeadPhotoView) itemView.findViewById(R.id.iv_headview_charm_second);
        ivSecond = (ImageView) itemView.findViewById(R.id.iv_headview_crown_second);
        tvRankingNameSecond = (TextView) itemView.findViewById(R.id.tv_headview_person_second);
        llRaningSexSecond = (LinearLayout) itemView.findViewById(R.id.ly_user_age_second);
        ivSexSecond = (ImageView) itemView.findViewById(R.id.iv_user_sex_second);
        tvAgeSecond = (TextView) itemView.findViewById(R.id.tv_user_age_second);
        tvCharmTextSecond = (TextView) itemView.findViewById(R.id.tv_charm_num_text_second);
        tvCharmNumSecond = (TextView) itemView.findViewById(R.id.tv_charm_num_second);
//        ivCharmNumSecond = (TextView) itemView.findViewById(R.id.iv_ranking_second_title);
        userTitleViewSecond = (UserTitleView) itemView.findViewById(R.id.rank_user_title_second);

        ivThird = (ImageView) itemView.findViewById(R.id.iv_headview_crown_third);
        hpThird = (HeadPhotoView) itemView.findViewById(R.id.iv_headview_charm_third);
        tvRankingNameThird = (TextView) itemView.findViewById(R.id.tv_headview_person_third);
        llRaningSexThird = (LinearLayout) itemView.findViewById(R.id.ly_user_age_third);
        ivSexThird = (ImageView) itemView.findViewById(R.id.iv_user_sex_third);
        tvAgeThird = (TextView) itemView.findViewById(R.id.tv_user_age_third);
        tvCharmTextThird = (TextView) itemView.findViewById(R.id.tv_charm_num_text_third);
        tvCharmNumThird = (TextView) itemView.findViewById(R.id.tv_charm_num_third);
//        ivCharmNumThird = (TextView) itemView.findViewById(R.id.iv_ranking_third_title);
        userTitleViewThird = (UserTitleView) itemView.findViewById(R.id.rank_user_title_third);

        llRankingFirst = (LinearLayout) itemView.findViewById(R.id.ll_ranking_first);
        llRankingFirst = (LinearLayout) itemView.findViewById(R.id.ll_ranking_first);
        llRankingSecond = (LinearLayout) itemView.findViewById(R.id.ll_ranking_second);
        llRankingThird = (LinearLayout) itemView.findViewById(R.id.ll_ranking_third);

        if (mRankType == 1) {
            tvCharmTextFirst.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
            tvCharmNumFirst.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
            tvCharmTextFirst.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_charm));
//            ivCharmNumFirst.setBackgroundResource(R.drawable.chat_bar_recuit_charm_one);

            tvCharmTextSecond.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
            tvCharmNumSecond.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
            tvCharmTextSecond.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_charm));
//            ivCharmNumSecond.setBackgroundResource(R.drawable.chat_bar_recuit_charm_two);

            tvCharmTextThird.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
            tvCharmNumThird.setTextColor(mContext.getResources().getColor(R.color.edit_user_tips));
            tvCharmTextThird.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_charm));
//            ivCharmNumThird.setBackgroundResource(R.drawable.chat_bar_recuit_charm_three);

        } else if(mRankType == 2){

            tvCharmTextFirst.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
            tvCharmNumFirst.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
            tvCharmTextFirst.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_regal));
//            ivCharmNumFirst.setBackgroundResource(R.drawable.chat_bar_recuit_regal_one);

            tvCharmTextSecond.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
            tvCharmNumSecond.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
            tvCharmTextSecond.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_regal));
//            ivCharmNumSecond.setBackgroundResource(R.drawable.chat_bar_recuit_regal_two);

            tvCharmTextThird.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
            tvCharmNumThird.setTextColor(mContext.getResources().getColor(R.color.ranking_charm_charmnumber));
            tvCharmTextThird.setText(mContext.getResources().getString(R.string.item_lv_charm_fragment_regal));
//            ivCharmNumThird.setBackgroundResource(R.drawable.chat_bar_recuit_regal_three);
        }else {
            tvCharmTextFirst.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
            tvCharmNumFirst.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
            tvCharmNumFirst.setVisibility(GONE);
//            ivCharmNumFirst.setBackgroundResource(iconsBaohufeiActive[0]);

            tvCharmTextSecond.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
            tvCharmNumSecond.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
            tvCharmNumSecond.setVisibility(GONE);
//            ivCharmNumSecond.setBackgroundResource(R.drawable.chat_bar_recuit_regal_two);

            tvCharmTextThird.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
            tvCharmNumThird.setTextColor(mContext.getResources().getColor(R.color.skill_ranking_info_times));
            tvCharmNumThird.setVisibility(GONE);
//            ivCharmNumThird.setBackgroundResource(R.drawable.chat_bar_recuit_regal_three);
        }

        ///加入到的是 listview
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        ///
    }


    /**
     *
     * @param rankType 1 charm 2 regal
     * @param index 1 第一名 2 第二名 3 第三名
     * @param ivCharmNumFirst
     * @param ivCharmNumSecond
     * @param ivCharmNumThird
     */

    private void showRankingTitle(int rankType ,int index,UserTitleView ivCharmNumFirst,UserTitleView ivCharmNumSecond ,UserTitleView ivCharmNumThird,int showType){
        if(showType == 1 || showType == 3){
            ivCharmNumFirst.setVisibility(VISIBLE);
            ivCharmNumSecond.setVisibility(VISIBLE);
            ivCharmNumThird.setVisibility(VISIBLE);
        }else {
            ivCharmNumFirst.setVisibility(GONE);
            ivCharmNumSecond.setVisibility(GONE);
            ivCharmNumThird.setVisibility(GONE);
            return;

        }

        if(index == 1){
            if(rankType == 1){
                ivCharmNumFirst.setText(titlesCharm[0]);
                ivCharmNumFirst.setTitleBackground(iconsCharm[0]);
            }else if(rankType == 2){
                ivCharmNumFirst.setText(titlesRegal[0]);
                ivCharmNumFirst.setTitleBackground(iconsRegal[0]);
            }

        }else if(index == 2){
            if(rankType ==1){
                ivCharmNumSecond.setText(titlesCharm[1]);
                ivCharmNumSecond.setTitleBackground(iconsCharm[1]);
            }else if(rankType == 2){
                ivCharmNumSecond.setText(titlesRegal[1]);
                ivCharmNumSecond.setTitleBackground(iconsRegal[1]);
            }

        }else if(index ==3){
            if(rankType ==1){
                ivCharmNumThird.setText(titlesCharm[2]);
                ivCharmNumThird.setTitleBackground(iconsCharm[2]);
            }else if(rankType == 2){
                ivCharmNumThird.setText(titlesRegal[2]);
                ivCharmNumThird.setTitleBackground(iconsRegal[2]);
            }

        }

    }

    /**
     *
     * @param activeOrGrow   1代表互动 2代表成长
     * @param skillType   技能类型 1 菊花残2 大么么3 保护分4无影脚5紧箍咒
     * @param index
     */
    private void  showSkillRanking(String activeOrGrow,int skillType,int index,UserTitleView ivCharmNumFirst,UserTitleView ivCharmNumSecond ,UserTitleView ivCharmNumThird,int showType){
       if(showType == 1 || showType == 3){
           ivCharmNumFirst.setVisibility(VISIBLE);
           ivCharmNumSecond.setVisibility(VISIBLE);
           ivCharmNumThird.setVisibility(VISIBLE);
       }else {
           ivCharmNumFirst.setVisibility(GONE);
           ivCharmNumSecond.setVisibility(GONE);
           ivCharmNumThird.setVisibility(GONE);
           return;
       }
        if("active".equals(activeOrGrow)){
            if(skillType ==1){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesActiveJuhuacan[0]);
                    ivCharmNumFirst.setTitleBackground(iconsJuhuancanActive[0]);
                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesActiveJuhuacan[1]);
                    ivCharmNumSecond.setTitleBackground(iconsJuhuancanActive[1]);
                }else if(index == 3){
                    ivCharmNumThird.setText(titlesActiveJuhuacan[2]);
                    ivCharmNumThird.setTitleBackground(iconsJuhuancanActive[2]);
                }

            }else if(skillType == 2){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesActiveDameme[0]);
                    ivCharmNumFirst.setTitleBackground(iconsDamemeActive[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesActiveDameme[1]);
                    ivCharmNumSecond.setTitleBackground(iconsDamemeActive[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesActiveDameme[2]);
                    ivCharmNumThird.setTitleBackground(iconsDamemeActive[2]);

                }

            }
            else if(skillType == 3){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesActiveJinguzhou[0]);
                    ivCharmNumFirst.setTitleBackground(iconsJinguzhouActive[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesActiveJinguzhou[1]);
                    ivCharmNumSecond.setTitleBackground(iconsJinguzhouActive[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesActiveJinguzhou[2]);
                    ivCharmNumThird.setTitleBackground(iconsJinguzhouActive[2]);

                }

            }else if(skillType == 4){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesActiveWuyingjiao[0]);
                    ivCharmNumFirst.setTitleBackground(iconsWuyingjiaoActive[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesActiveWuyingjiao[1]);
                    ivCharmNumSecond.setTitleBackground(iconsWuyingjiaoActive[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesActiveWuyingjiao[2]);
                    ivCharmNumThird.setTitleBackground(iconsWuyingjiaoActive[2]);

                }

            }else if(skillType == 5){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesActiveBaohufei[0]);
                    ivCharmNumFirst.setTitleBackground(iconsBaohufeiActive[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesActiveBaohufei[1]);
                    ivCharmNumSecond.setTitleBackground(iconsBaohufeiActive[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesActiveBaohufei[2]);
                    ivCharmNumThird.setTitleBackground(iconsBaohufeiActive[2]);

                }

            }

        }else if("update".equals(activeOrGrow)){
            if(skillType ==1){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesGrowJuhuacan[0]);
                    ivCharmNumFirst.setTitleBackground(iconsJuhuancanGrow[0]);
                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesGrowJuhuacan[1]);
                    ivCharmNumSecond.setTitleBackground(iconsJuhuancanGrow[1]);
                }else if(index == 3){
                    ivCharmNumThird.setText(titlesGrowJuhuacan[2]);
                    ivCharmNumThird.setTitleBackground(iconsJuhuancanGrow[2]);
                }

            }else if(skillType == 2){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesGrowDameme[0]);
                    ivCharmNumFirst.setTitleBackground(iconsDamemeGrow[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesGrowDameme[1]);
                    ivCharmNumSecond.setTitleBackground(iconsDamemeGrow[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesGrowDameme[2]);
                    ivCharmNumThird.setTitleBackground(iconsDamemeGrow[2]);

                }

            }
            else if(skillType == 3){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesGrowJinguzhou[0]);
                    ivCharmNumFirst.setTitleBackground(iconsJinguzhouGrow[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesGrowJinguzhou[1]);
                    ivCharmNumSecond.setTitleBackground(iconsJinguzhouGrow[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesGrowJinguzhou[2]);
                    ivCharmNumThird.setTitleBackground(iconsJinguzhouGrow[2]);

                }

            }else if(skillType == 4){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesGrowWuyingjiao[0]);
                    ivCharmNumFirst.setTitleBackground(iconsWuyingjiaoGrow[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesGrowWuyingjiao[1]);
                    ivCharmNumSecond.setTitleBackground(iconsWuyingjiaoGrow[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesGrowWuyingjiao[2]);
                    ivCharmNumThird.setTitleBackground(iconsWuyingjiaoGrow[2]);

                }

            }else if(skillType == 5){
                if(index ==1){
                    ivCharmNumFirst.setText(titlesGrowBaohufei[0]);
                    ivCharmNumFirst.setTitleBackground(iconsBaohufeiGrow[0]);

                }else if(index == 2){
                    ivCharmNumSecond.setText(titlesGrowBaohufei[1]);
                    ivCharmNumSecond.setTitleBackground(iconsBaohufeiGrow[1]);

                }else if(index == 3){
                    ivCharmNumThird.setText(titlesGrowBaohufei[2]);
                    ivCharmNumThird.setTitleBackground(iconsBaohufeiGrow[2]);

                }

            }

        }

    }
}
