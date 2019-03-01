package net.iaround.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.RankingHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.ranking.SkillRankingEntity;
import net.iaround.model.ranking.SkillRankingEntity.ListBean;
import net.iaround.model.ranking.SkillRankingEntity.ListBean.ActiveBean;
import net.iaround.model.skill.RankSkillParentEntity;
import net.iaround.model.skill.RankSkillParentEntity.GrowingBean;
import net.iaround.model.skill.RankSkillParentEntity.InteractiveBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.RankSkillItemInfoActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2017/8/15.
 */

public class SkillRankingFragment extends LazyLoadBaseFragment implements View.OnClickListener, HttpCallBack {
    private ScrollView scrollViewSkillFragment;

    private TextView btCurrentWeek;
    private TextView btLastWeek;
    private TextView btCurrentMonth;
    private TextView btLastMonth;

    /**
     * 技能id
     */
    public static final int skill_id_juhuacan = 1;
    public static final int skill_id_dameme = 2;
    public static final int skill_id_jinguzhou = 3;
    public static final int skill_id_wuyingjiao = 4;
    public static final int skill_id_baohufei = 5;

    /**
     * 技能图片
     */
    public static final int icons[] = {
            R.drawable.skill_name_juhuacan,  //技能ID 1
            R.drawable.skill_name_dameme,   //技能ID 2
            R.drawable.skill_name_jinguzhou,  //技能ID 3
            R.drawable.skill_name_wuyingjiao,  //技能ID 4
            R.drawable.skill_name_baohufei   //技能ID 5
    };
    private int starSelectedDrawable = icons[0];
    /**
     * 技能背景
     */
    public static final int iconsbg[] = {
            R.drawable.skill_background_juhuacan,
            R.drawable.skill_background_dameme,
            R.drawable.skill_background_jinguzhou,
            R.drawable.skill_background_wuyingjiao,
            R.drawable.skill_background_baohufei
    };
    private int starSelectedDrawablebg = iconsbg[0];

    /**
     * 类型  1魅力 2富豪
     */
    private int cat = 1;
    /**
     * 1本周 2上周 3本月 4上月
     */
    private int type = 1;
    /**
     * 技能榜类型 active 互动  update 成长
     */
    private String skillType = "";
    /**
     * 技能类型    互动榜1 菊花残 2，大么么3，紧箍咒4，无影脚5，保护费
     */
    private int skillIdActiveOne;
    private int skillIdActiveTwo;
    private int skillIdActiveThree;
    private int skillIdActiveFour;
    private int skillIdActiveFive;
    /**
     * 技能类型    成长榜1 菊花残 2
     */
    private int skillIdGrowOne;
    private int skillIdGrowTwo;
    private int skillIdGrowThree;
    private int skillIdGrowFour;
    private int skillIdGrowFive;
    /**
     * 技能详情标题
     */
    private String skillInfoTitle = "";
    private static String SKILL_TYPE_ACTIVE = "active";
    private static String SKILL_TYPE_GROW = "update";

    /**
     * 请求关注数据的flag
     */
    private long GET_CHARM_DATA_FLAG;
    /**
     * 页面展示数量
     */
    private int pageSize = 10;

    private int pageNo = 1;//加载页数
    //测试数据
    private RankSkillParentEntity rankSkillParentEntity;//结构体bean
    private List<InteractiveBean> interactiveBeanList;//互动榜的集合
    private List<GrowingBean> growingBeanList;//成长榜的集合
    //正式数据
    private SkillRankingEntity skillRankingEntity;//结构体bean
    private List<ActiveBean> activeBeanList;//互动榜的集合
    private List<ActiveBean> growBeanList;//成长榜的集合
    private List<ListBean> skillRankingList;//技能排行榜的集合


    private Intent intent;

    private RelativeLayout rlSkillActiveOne;
    private RelativeLayout rlSkillActiveTwo;
    private RelativeLayout rlSkillActiveThree;
    private RelativeLayout rlSkillActiveFour;
    private RelativeLayout rlSkillActiveFive;

    private RelativeLayout rlSkillActiveBgOne;
    private RelativeLayout rlSkillActiveBgTwo;
    private RelativeLayout rlSkillActiveBgThree;
    private RelativeLayout rlSkillActiveBgFour;
    private RelativeLayout rlSkillActiveBgFive;

    private RelativeLayout rlSkillGrowingOne;
    private RelativeLayout rlSkillGrowingTwo;
    private RelativeLayout rlSkillGrowingThree;
    private RelativeLayout rlSkillGrowingFour;
    private RelativeLayout rlSkillGrowingFive;

    private RelativeLayout rlSkillGrowBgOne;
    private RelativeLayout rlSkillGrowBgTwo;
    private RelativeLayout rlSkillGrowBgThree;
    private RelativeLayout rlSkillGrowBgFour;
    private RelativeLayout rlSkillGrowBgFive;

    private ImageView ivSkillActivePhotoOne;
    private ImageView ivSkillActivePhotoTwo;
    private ImageView ivSkillActivePhotoThree;
    private ImageView ivSkillActivePhotoFour;
    private ImageView ivSkillActivePhotoFive;

    private ImageView ivSkillGrowingPhotoOne;
    private ImageView ivSkillGrowingPhotoTwo;
    private ImageView ivSkillGrowingPhotoThree;
    private ImageView ivSkillGrowingPhotoFour;
    private ImageView ivSkillGrowingPhotoFive;

    private TextView tvSkillActiveNameOne;
    private TextView tvSkillActiveNameTwo;
    private TextView tvSkillActiveNameThree;
    private TextView tvSkillActiveNameFour;
    private TextView tvSkillActiveNameFive;

    private TextView tvSkillGrowingNameOne;
    private TextView tvSkillGrowingNameTwo;
    private TextView tvSkillGrowingNameThree;
    private TextView tvSkillGrowingNameFour;
    private TextView tvSkillGrowingNameFive;

    private HeadPhotoView hpSkillOneMemberFist;
    private HeadPhotoView hpSkillOneMemberSecond;
    private HeadPhotoView hpSkillOneMemberThird;

    private HeadPhotoView hpSkillTwoMemberFist;
    private HeadPhotoView hpSkillTwoMemberSecond;
    private HeadPhotoView hpSkillTwoMemberThird;

    private HeadPhotoView hpSkillThreeMemberFist;
    private HeadPhotoView hpSkillThreeMemberSecond;
    private HeadPhotoView hpSkillThreeMemberThird;

    private HeadPhotoView hpSkillFourMemberFist;
    private HeadPhotoView hpSkillFourMemberSecond;
    private HeadPhotoView hpSkillFourMemberThird;

    private HeadPhotoView hpSkillFiveMemberFist;
    private HeadPhotoView hpSkillFiveMemberSecond;
    private HeadPhotoView hpSkillFiveMemberThird;

    private HeadPhotoView hpSkillOneMemberGrowingFist;
    private HeadPhotoView hpSkillOneMemberGrowingSecond;
    private HeadPhotoView hpSkillOneMemberGrowingThird;

    private HeadPhotoView hpSkillTwoMemberGrowingFist;
    private HeadPhotoView hpSkillTwoMemberGrowingSecond;
    private HeadPhotoView hpSkillTwoMemberGrowingThird;

    private HeadPhotoView hpSkillThreeMemberGrowingFist;
    private HeadPhotoView hpSkillThreeMemberGrowingSecond;
    private HeadPhotoView hpSkillThreeMemberGrowingThird;

    private HeadPhotoView hpSkillFourMemberGrowingFist;
    private HeadPhotoView hpSkillFourMemberGrowingSecond;
    private HeadPhotoView hpSkillFourMemberGrowingThird;

    private HeadPhotoView hpSkillFiveMemberGrowingFist;
    private HeadPhotoView hpSkillFiveMemberGrowingSecond;
    private HeadPhotoView hpSkillFiveMemberGrowingThird;


    @Override
    protected int setContentView() {
        return R.layout.fragment_skill_ranking;
    }

    @Override
    protected boolean lazyLoad() {
        showWaitDialog();
        if (scrollViewSkillFragment == null) {
            initView(getContentView());
            initListener();
        }
        initData();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_skill_ranking, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView(view);
//        initListener();
//        initData();
////        refreshData(rankSkillParentEntity);
//
//    }


    private void initView(View view) {
        scrollViewSkillFragment = (ScrollView) view.findViewById(R.id.scroll_skill_fragment);
        btCurrentWeek = (TextView) view.findViewById(R.id.bt_chat_bar_current_week_charm);
        btLastWeek = (TextView) view.findViewById(R.id.bt_chat_bar_last_week_charm);
        btCurrentMonth = (TextView) view.findViewById(R.id.bt_chat_bar_current_month_charm);
        btLastMonth = (TextView) view.findViewById(R.id.bt_chat_bar_last_month_charm);

        btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);//本周设置默认选定

        rlSkillActiveOne = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_active_one);
        rlSkillActiveTwo = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_active_two);
        rlSkillActiveThree = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_active_three);
        rlSkillActiveFour = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_active_four);
        rlSkillActiveFive = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_active_five);

        rlSkillActiveBgOne = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_one);
        rlSkillActiveBgTwo = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_two);
        rlSkillActiveBgThree = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_three);
        rlSkillActiveBgFour = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_four);
        rlSkillActiveBgFive = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_five);

        rlSkillGrowingOne = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_grow_one);
        rlSkillGrowingTwo = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_grow_two);
        rlSkillGrowingThree = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_grow_three);
        rlSkillGrowingFour = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_grow_four);
        rlSkillGrowingFive = (RelativeLayout) view.findViewById(R.id.rl_skill_fragment_grow_five);

        rlSkillGrowBgOne = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_grow_one);
        rlSkillGrowBgTwo = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_grow_two);
        rlSkillGrowBgThree = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_grow_three);
        rlSkillGrowBgFour = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_grow_four);
        rlSkillGrowBgFive = (RelativeLayout) view.findViewById(R.id.rl_fragment_skill_photo_grow_five);

        ivSkillActivePhotoOne = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_one);
        ivSkillActivePhotoTwo = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_two);
        ivSkillActivePhotoThree = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_three);
        ivSkillActivePhotoFour = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_four);
        ivSkillActivePhotoFive = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_five);

        ivSkillGrowingPhotoOne = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_grow_one);
        ivSkillGrowingPhotoTwo = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_grow_two);
        ivSkillGrowingPhotoThree = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_grow_three);
        ivSkillGrowingPhotoFour = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_grow_four);
        ivSkillGrowingPhotoFive = (ImageView) view.findViewById(R.id.iv_fragment_skill_photo_grow_five);

        tvSkillActiveNameOne = (TextView) view.findViewById(R.id.tv_fragment_skill_name_one);
        tvSkillActiveNameTwo = (TextView) view.findViewById(R.id.tv_fragment_skill_name_two);
        tvSkillActiveNameThree = (TextView) view.findViewById(R.id.tv_fragment_skill_name_three);
        tvSkillActiveNameFour = (TextView) view.findViewById(R.id.tv_fragment_skill_name_four);
        tvSkillActiveNameFive = (TextView) view.findViewById(R.id.tv_fragment_skill_name_five);

        tvSkillGrowingNameOne = (TextView) view.findViewById(R.id.tv_fragment_skill_name_grow_one);
        tvSkillGrowingNameTwo = (TextView) view.findViewById(R.id.tv_fragment_skill_name_grow_two);
        tvSkillGrowingNameThree = (TextView) view.findViewById(R.id.tv_fragment_skill_name_grow_three);
        tvSkillGrowingNameFour = (TextView) view.findViewById(R.id.tv_fragment_skill_name_grow_four);
        tvSkillGrowingNameFive = (TextView) view.findViewById(R.id.tv_fragment_skill_name_grow_five);

        hpSkillOneMemberFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_one_member_fist);
        hpSkillOneMemberSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_one_member_second);
        hpSkillOneMemberThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_one_member_third);

        hpSkillTwoMemberFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_two_member_fist);
        hpSkillTwoMemberSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_two_member_second);
        hpSkillTwoMemberThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_two_member_third);

        hpSkillThreeMemberFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_three_member_fist);
        hpSkillThreeMemberSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_three_member_second);
        hpSkillThreeMemberThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_three_member_third);

        hpSkillFourMemberFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_four_member_fist);
        hpSkillFourMemberSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_four_member_second);
        hpSkillFourMemberThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_four_member_third);

        hpSkillFiveMemberFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_five_member_fist);
        hpSkillFiveMemberSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_five_member_second);
        hpSkillFiveMemberThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_five_member_third);

        hpSkillOneMemberGrowingFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_one_member_grow_fist);
        hpSkillOneMemberGrowingSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_one_member_grow_second);
        hpSkillOneMemberGrowingThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_one_member_grow_third);

        hpSkillTwoMemberGrowingFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_two_member_grow_fist);
        hpSkillTwoMemberGrowingSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_two_member_grow_second);
        hpSkillTwoMemberGrowingThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_two_member_grow_third);

        hpSkillThreeMemberGrowingFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_three_member_grow_fist);
        hpSkillThreeMemberGrowingSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_three_member_grow_second);
        hpSkillThreeMemberGrowingThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_three_member_grow_third);

        hpSkillFourMemberGrowingFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_four_member_grow_fist);
        hpSkillFourMemberGrowingSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_four_member_grow_second);
        hpSkillFourMemberGrowingThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_four_member_grow_third);

        hpSkillFiveMemberGrowingFist = (HeadPhotoView) view.findViewById(R.id.fragment_skill_five_member_grow_fist);
        hpSkillFiveMemberGrowingSecond = (HeadPhotoView) view.findViewById(R.id.fragment_skill_five_member_grow_second);
        hpSkillFiveMemberGrowingThird = (HeadPhotoView) view.findViewById(R.id.fragment_skill_five_member_grow_third);

//        int language = CommonFunction.getLanguageIndex(getActivity());
//        if (language == 0) {
//            btCurrentWeek.setTextSize(8);
//            btLastWeek.setTextSize(8);
//            btCurrentMonth.setTextSize(8);
//            btLastMonth.setTextSize(8);
//        } else {
//            btCurrentWeek.setTextSize(12);
//            btLastWeek.setTextSize(12);
//            btCurrentMonth.setTextSize(12);
//            btLastMonth.setTextSize(12);
//        }

    }

    public void initListener() {
        btCurrentWeek.setOnClickListener(this);
        btLastWeek.setOnClickListener(this);
        btCurrentMonth.setOnClickListener(this);
        btLastMonth.setOnClickListener(this);

        rlSkillActiveOne.setOnClickListener(this);
        rlSkillActiveTwo.setOnClickListener(this);
        rlSkillActiveThree.setOnClickListener(this);
        rlSkillActiveFour.setOnClickListener(this);
        rlSkillActiveFive.setOnClickListener(this);

        rlSkillGrowingOne.setOnClickListener(this);
        rlSkillGrowingTwo.setOnClickListener(this);
        rlSkillGrowingThree.setOnClickListener(this);
        rlSkillGrowingFour.setOnClickListener(this);
        rlSkillGrowingFive.setOnClickListener(this);
    }


    public void initData() {
        type = 1;
        requestPageData(type, false);
        activeBeanList = new ArrayList<>();
        growingBeanList = new ArrayList<>();

//        rankSkillParentEntity = new RankSkillParentEntity();
//        interactiveBeanList = new ArrayList<>();
//        InteractiveBean interactiveBean;
//        GrowingBean growingBean;
//        List<String> iconList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            interactiveBean = new InteractiveBean();
//            interactiveBean.setIcon("http://f1.iaround.com//upload//images//a04cf04197923b6548ee08a6feedfe03.png");
//            iconList.add("http://p1.iaround.com/201604/28/FACE/06947718a8eab5e406885a96dadfc66c_s.jpg");
//            iconList.add("http://p1.iaround.com/201604/28/FACE/06947718a8eab5e406885a96dadfc66c_s.jpg");
//            iconList.add("http://p1.iaround.com/201604/28/FACE/06947718a8eab5e406885a96dadfc66c_s.jpg");
//            interactiveBean.setIconList(iconList);
//            interactiveBean.setName("玫瑰花");
//            interactiveBeanList.add(interactiveBean);
//
//        }
//        rankSkillParentEntity.setInteractiveBeanList(interactiveBeanList);
//        growingBeanList = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            growingBean = new GrowingBean();
//            growingBean.setIcon("http://f1.iaround.com//upload//images//a04cf04197923b6548ee08a6feedfe03.png");
//            iconList.add("http://p1.iaround.com/201604/28/FACE/06947718a8eab5e406885a96dadfc66c_s.jpg");
//            iconList.add("http://p1.iaround.com/201604/28/FACE/06947718a8eab5e406885a96dadfc66c_s.jpg");
//            iconList.add("http://p1.iaround.com/201604/28/FACE/06947718a8eab5e406885a96dadfc66c_s.jpg");
//            growingBean.setIconList(iconList);
//            growingBean.setName("玫瑰花");
//            growingBeanList.add(growingBean);
//
//        }
//        rankSkillParentEntity.setGrowingBeanList(growingBeanList);
//        refreshData(rankSkillParentEntity);
    }


    public void refreshData(SkillRankingEntity skillRankingEntity) {
        skillRankingList = skillRankingEntity.getList();
        if (skillRankingList != null) {
            if (!skillRankingList.isEmpty()) {
                ListBean listBean;
                for (int i = 0; i < skillRankingList.size(); i++) {
                    switch (i) {
                        case 0:
                            listBean = skillRankingList.get(0);
                            hpSkillOneMemberFist.setVisibility(View.INVISIBLE);
                            hpSkillOneMemberSecond.setVisibility(View.INVISIBLE);
                            hpSkillOneMemberThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdActiveOne = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillActiveBgOne.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillActivePhotoOne);
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillActivePhotoOne);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillActiveNameOne.setText(strName);
                                if (listBean.getActiveList() == null || listBean.getActiveList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getActiveList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillOneMemberFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getActiveList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getActiveList().get(0).getICON());
                                            userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                            hpSkillOneMemberFist.execute(userFirst);
                                            hpSkillOneMemberFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillOneMemberSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getActiveList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getActiveList().get(1).getICON());
                                            userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                            hpSkillOneMemberSecond.execute(userSecond);
                                            hpSkillOneMemberSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillOneMemberThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getActiveList().get(2).getUserID());
                                            userThird.setIcon(listBean.getActiveList().get(2).getICON());
                                            userThird.setSVip(listBean.getActiveList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                            hpSkillOneMemberThird.execute(userThird);
                                            hpSkillOneMemberThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }
                                }

//                                if(listBean.getActiveList().size()>=3){
//                                    User userFirst = new User();
//                                    userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                    userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                    userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                    userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                    User userSecond = new User();
//                                    userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                    userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                    userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                    userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                    User userThird = new User();
//                                    userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                    userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                    userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                    userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                    hpSkillOneMemberFist.execute(userFirst);
//                                    hpSkillOneMemberSecond.execute(userSecond);
//                                    hpSkillOneMemberThird.execute(userThird);
//                                }

                            }

                            break;
                        case 1:
                            listBean = skillRankingList.get(1);
                            hpSkillTwoMemberFist.setVisibility(View.INVISIBLE);
                            hpSkillTwoMemberSecond.setVisibility(View.INVISIBLE);
                            hpSkillTwoMemberThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdActiveTwo = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillActiveBgTwo.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillActivePhotoTwo);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillActivePhotoTwo);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoTwo, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillActiveNameTwo.setText(strName);
                                if (listBean.getActiveList() == null || listBean.getActiveList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getActiveList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillTwoMemberFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getActiveList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getActiveList().get(0).getICON());
                                            userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                            hpSkillTwoMemberFist.execute(userFirst);
                                            hpSkillTwoMemberFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillTwoMemberSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getActiveList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getActiveList().get(1).getICON());
                                            userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                            hpSkillTwoMemberSecond.execute(userSecond);
                                            hpSkillTwoMemberSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillTwoMemberThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getActiveList().get(2).getUserID());
                                            userThird.setIcon(listBean.getActiveList().get(2).getICON());
                                            userThird.setSVip(listBean.getActiveList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                            hpSkillTwoMemberThird.execute(userThird);
                                            hpSkillTwoMemberThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }
                                }

//                                User userFirst = new User();
//                                if(listBean.getActiveList().size()>=3){
//                                    userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                    userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                    userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                    userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                    User userSecond = new User();
//                                    userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                    userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                    userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                    userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                    User userThird = new User();
//                                    userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                    userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                    userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                    userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                    hpSkillTwoMemberFist.execute(userFirst);
//                                    hpSkillTwoMemberSecond.execute(userSecond);
//                                    hpSkillTwoMemberThird.execute(userThird);
//                                }

                            }
                            break;
                        case 2:
                            listBean = skillRankingList.get(2);
                            hpSkillThreeMemberFist.setVisibility(View.INVISIBLE);
                            hpSkillThreeMemberSecond.setVisibility(View.INVISIBLE);
                            hpSkillThreeMemberThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdActiveThree = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillActiveBgThree.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillActivePhotoThree);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillActivePhotoThree);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoThree, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillActiveNameThree.setText(strName);
                                if (listBean.getActiveList() == null || listBean.getActiveList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getActiveList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillThreeMemberFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getActiveList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getActiveList().get(0).getICON());
                                            userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                            hpSkillThreeMemberFist.execute(userFirst);
                                            hpSkillThreeMemberFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillThreeMemberSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getActiveList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getActiveList().get(1).getICON());
                                            userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                            hpSkillThreeMemberSecond.execute(userSecond);
                                            hpSkillThreeMemberSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillThreeMemberThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getActiveList().get(2).getUserID());
                                            userThird.setIcon(listBean.getActiveList().get(2).getICON());
                                            userThird.setSVip(listBean.getActiveList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                            hpSkillThreeMemberThird.execute(userThird);
                                            hpSkillThreeMemberThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }
                                }

//                                User userFirst = new User();
//                                if(listBean.getActiveList().size()>=3){
//                                    userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                    userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                    userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                    userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                    User userSecond = new User();
//                                    userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                    userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                    userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                    userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                    User userThird = new User();
//                                    userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                    userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                    userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                    userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                    hpSkillThreeMemberFist.execute(userFirst);
//                                    hpSkillThreeMemberSecond.execute(userSecond);
//                                    hpSkillThreeMemberThird.execute(userThird);
//                                }

                            }
                            break;
                        case 3:
                            listBean = skillRankingList.get(3);
                            hpSkillFourMemberFist.setVisibility(View.INVISIBLE);
                            hpSkillFourMemberSecond.setVisibility(View.INVISIBLE);
                            hpSkillFourMemberThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdActiveFour = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillActiveBgFour.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillActivePhotoFour);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillActivePhotoFour);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoFour, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillActiveNameFour.setText(strName);
                                if (listBean.getActiveList() == null || listBean.getActiveList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getActiveList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillFourMemberFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getActiveList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getActiveList().get(0).getICON());
                                            userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                            hpSkillFourMemberFist.execute(userFirst);
                                            hpSkillFourMemberFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillFourMemberSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getActiveList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getActiveList().get(1).getICON());
                                            userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                            hpSkillFourMemberSecond.execute(userSecond);
                                            hpSkillFourMemberSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillFourMemberThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getActiveList().get(2).getUserID());
                                            userThird.setIcon(listBean.getActiveList().get(2).getICON());
                                            userThird.setSVip(listBean.getActiveList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                            hpSkillFourMemberThird.execute(userThird);
                                            hpSkillFourMemberThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }
                                }

//                                User userFirst = new User();
//                                if(listBean.getActiveList().size()>=3){
//                                    userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                    userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                    userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                    userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                    User userSecond = new User();
//                                    userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                    userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                    userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                    userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                    User userThird = new User();
//                                    userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                    userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                    userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                    userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                    hpSkillFourMemberFist.execute(userFirst);
//                                    hpSkillFourMemberSecond.execute(userSecond);
//                                    hpSkillFourMemberThird.execute(userThird);
//                                }

                            }
                            break;
                        case 4:
                            listBean = skillRankingList.get(4);
                            hpSkillFiveMemberFist.setVisibility(View.INVISIBLE);
                            hpSkillFiveMemberSecond.setVisibility(View.INVISIBLE);
                            hpSkillFiveMemberThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdActiveFive = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillActiveBgFive.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillActivePhotoFive);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillActivePhotoFive);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoFive, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillActiveNameFive.setText(strName);
                                if (listBean.getActiveList() == null || listBean.getActiveList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getActiveList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillFiveMemberFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getActiveList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getActiveList().get(0).getICON());
                                            userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                            hpSkillFiveMemberFist.execute(userFirst);
                                            hpSkillFiveMemberFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillFiveMemberSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getActiveList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getActiveList().get(1).getICON());
                                            userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                            hpSkillFiveMemberSecond.execute(userSecond);
                                            hpSkillFiveMemberSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillFiveMemberThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getActiveList().get(2).getUserID());
                                            userThird.setIcon(listBean.getActiveList().get(2).getICON());
                                            userThird.setSVip(listBean.getActiveList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                            hpSkillFiveMemberThird.execute(userThird);
                                            hpSkillFiveMemberThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }
                                }

//                                User userFirst = new User();
//                                if(listBean.getActiveList().size()>=3){
//                                    userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                    userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                    userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                    userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                    User userSecond = new User();
//                                    userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                    userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                    userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                    userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                    User userThird = new User();
//                                    userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                    userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                    userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                    userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                    hpSkillFiveMemberFist.execute(userFirst);
//                                    hpSkillFiveMemberSecond.execute(userSecond);
//                                    hpSkillFiveMemberThird.execute(userThird);
//                                }

                            }
                            break;
                    }
                }
            }
        }

        if (skillRankingList != null) {
            if (!skillRankingList.isEmpty()) {
                ListBean listBean;
                for (int i = 0; i < skillRankingList.size(); i++) {
                    switch (i) {
                        case 0:
                            listBean = skillRankingList.get(0);
                            hpSkillOneMemberGrowingFist.setVisibility(View.INVISIBLE);
                            hpSkillOneMemberGrowingSecond.setVisibility(View.INVISIBLE);
                            hpSkillOneMemberGrowingThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdGrowOne = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillGrowBgOne.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillGrowingPhotoOne);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillGrowingPhotoOne);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillGrowingNameOne.setText(strName);
                                if (listBean.getUpdateList() == null || listBean.getUpdateList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getUpdateList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillOneMemberGrowingFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getUpdateList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getUpdateList().get(0).getICON());
                                            userFirst.setSVip(listBean.getUpdateList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getUpdateList().get(0).getVipLevel());
//                                            hpSkillOneMemberGrowingFist.execute(userFirst);
                                            hpSkillOneMemberGrowingFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillOneMemberGrowingSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getUpdateList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getUpdateList().get(1).getICON());
                                            userSecond.setSVip(listBean.getUpdateList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getUpdateList().get(1).getVipLevel());
//                                            hpSkillOneMemberGrowingSecond.execute(userSecond);
                                            hpSkillOneMemberGrowingSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillOneMemberGrowingThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getUpdateList().get(2).getUserID());
                                            userThird.setIcon(listBean.getUpdateList().get(2).getICON());
                                            userThird.setSVip(listBean.getUpdateList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getUpdateList().get(2).getVipLevel());
//                                            hpSkillOneMemberGrowingThird.execute(userThird);
                                            hpSkillOneMemberGrowingThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }

                                }
//                                User userFirst = new User();
//                                userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                User userSecond = new User();
//                                userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                User userThird = new User();
//                                userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                hpSkillOneMemberGrowingFist.execute(userFirst);
//                                hpSkillOneMemberGrowingSecond.execute(userSecond);
//                                hpSkillOneMemberGrowingThird.execute(userThird);lOneMemberThird.execute(userThird);
                            }

                            break;
                        case 1:
                            listBean = skillRankingList.get(1);
                            hpSkillTwoMemberGrowingFist.setVisibility(View.INVISIBLE);
                            hpSkillTwoMemberGrowingSecond.setVisibility(View.INVISIBLE);
                            hpSkillTwoMemberGrowingThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdGrowTwo = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillGrowBgTwo.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillGrowingPhotoTwo);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillGrowingPhotoTwo);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillGrowingNameTwo.setText(strName);
                                if (listBean.getUpdateList() == null || listBean.getUpdateList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getUpdateList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillTwoMemberGrowingFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getUpdateList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getUpdateList().get(0).getICON());
                                            userFirst.setSVip(listBean.getUpdateList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getUpdateList().get(0).getVipLevel());
//                                            hpSkillTwoMemberGrowingFist.execute(userFirst);
                                            hpSkillTwoMemberGrowingFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillTwoMemberGrowingSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getUpdateList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getUpdateList().get(1).getICON());
                                            userSecond.setSVip(listBean.getUpdateList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getUpdateList().get(1).getVipLevel());
//                                            hpSkillTwoMemberGrowingSecond.execute(userSecond);
                                            hpSkillTwoMemberGrowingSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillTwoMemberGrowingThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getUpdateList().get(2).getUserID());
                                            userThird.setIcon(listBean.getUpdateList().get(2).getICON());
                                            userThird.setSVip(listBean.getUpdateList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getUpdateList().get(2).getVipLevel());
//                                            hpSkillTwoMemberGrowingThird.execute(userThird);
                                            hpSkillTwoMemberGrowingThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }

                                }

//                                User userFirst = new User();
//                                userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
////                                User userSecond = new User();
////                                userSecond.setUid(listBean.getActiveList().get(1).getUserID());
////                                userSecond.setIcon(listBean.getActiveList().get(1).getICON());
////                                userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
////                                userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
////                                User userThird = new User();
////                                userThird.setUid(listBean.getActiveList().get(2).getUserID());
////                                userThird.setIcon(listBean.getActiveList().get(2).getICON());
////                                userThird.setSVip(listBean.getActiveList().get(2).getVIP());
////                                userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                hpSkillTwoMemberGrowingFist.execute(userFirst);
////                                hpSkillTwoMemberGrowingSecond.execute(userSecond);
////                                hpSkillTwoMemberGrowingThird.execute(userThird);
                            }
                            break;
                        case 2:
                            listBean = skillRankingList.get(2);
                            hpSkillThreeMemberGrowingFist.setVisibility(View.INVISIBLE);
                            hpSkillThreeMemberGrowingSecond.setVisibility(View.INVISIBLE);
                            hpSkillThreeMemberGrowingThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdGrowThree = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillGrowBgThree.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillGrowingPhotoThree);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillGrowingPhotoThree);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillGrowingNameThree.setText(strName);
                                if (listBean.getUpdateList() == null || listBean.getUpdateList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getUpdateList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillThreeMemberGrowingFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getUpdateList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getUpdateList().get(0).getICON());
                                            userFirst.setSVip(listBean.getUpdateList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getUpdateList().get(0).getVipLevel());
//                                            hpSkillThreeMemberGrowingFist.execute(userFirst);
                                            hpSkillThreeMemberGrowingFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillThreeMemberGrowingSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getUpdateList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getUpdateList().get(1).getICON());
                                            userSecond.setSVip(listBean.getUpdateList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getUpdateList().get(1).getVipLevel());
//                                            hpSkillThreeMemberGrowingSecond.execute(userSecond);
                                            hpSkillThreeMemberGrowingSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillThreeMemberGrowingThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getUpdateList().get(2).getUserID());
                                            userThird.setIcon(listBean.getUpdateList().get(2).getICON());
                                            userThird.setSVip(listBean.getUpdateList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getUpdateList().get(2).getVipLevel());
//                                            hpSkillThreeMemberGrowingThird.execute(userThird);
                                            hpSkillThreeMemberGrowingThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }

                                }
//                                User userFirst = new User();
//                                userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                User userSecond = new User();
//                                userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                User userThird = new User();
//                                userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                hpSkillThreeMemberGrowingFist.execute(userFirst);
//                                hpSkillThreeMemberGrowingSecond.execute(userSecond);
//                                hpSkillThreeMemberGrowingThird.execute(userThird);
                            }
                            break;
                        case 3:
                            listBean = skillRankingList.get(3);
                            hpSkillFourMemberGrowingFist.setVisibility(View.INVISIBLE);
                            hpSkillFourMemberGrowingSecond.setVisibility(View.INVISIBLE);
                            hpSkillFourMemberGrowingThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdGrowFour = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillGrowBgFour.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillGrowingPhotoFour);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillGrowingPhotoFour);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillGrowingNameFour.setText(strName);
                                if (listBean.getUpdateList() == null || listBean.getUpdateList().isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < listBean.getUpdateList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillFourMemberGrowingFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getUpdateList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getUpdateList().get(0).getICON());
                                            userFirst.setSVip(listBean.getUpdateList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getUpdateList().get(0).getVipLevel());
//                                            hpSkillFourMemberGrowingFist.execute(userFirst);
                                            hpSkillFourMemberGrowingFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillFourMemberGrowingSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getUpdateList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getUpdateList().get(1).getICON());
                                            userSecond.setSVip(listBean.getUpdateList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getUpdateList().get(1).getVipLevel());
//                                            hpSkillFourMemberGrowingSecond.execute(userSecond);
                                            hpSkillFourMemberGrowingSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillFourMemberGrowingThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getUpdateList().get(2).getUserID());
                                            userThird.setIcon(listBean.getUpdateList().get(2).getICON());
                                            userThird.setSVip(listBean.getUpdateList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getUpdateList().get(2).getVipLevel());
//                                            hpSkillFourMemberGrowingThird.execute(userThird);
                                            hpSkillFourMemberGrowingThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }

                                }
//                                User userFirst = new User();
//                                userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                User userSecond = new User();
//                                userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                User userThird = new User();
//                                userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                hpSkillFourMemberGrowingFist.execute(userFirst);
//                                hpSkillFourMemberGrowingSecond.execute(userSecond);
//                                hpSkillFourMemberGrowingThird.execute(userThird);
                            }
                            break;
                        case 4:
                            listBean = skillRankingList.get(4);
                            hpSkillFiveMemberGrowingFist.setVisibility(View.INVISIBLE);
                            hpSkillFiveMemberGrowingSecond.setVisibility(View.INVISIBLE);
                            hpSkillFiveMemberGrowingThird.setVisibility(View.INVISIBLE);
                            if (listBean != null) {
                                skillIdGrowFive = listBean.getID();
                                starSelectedDrawablebg = iconsbg[listBean.getID() - 1];
                                rlSkillGrowBgFive.setBackgroundResource(starSelectedDrawablebg);
                                starSelectedDrawable = icons[listBean.getID() - 1];
//                                Glide.with(getContext()).load(starSelectedDrawable).into(ivSkillGrowingPhotoFive);
                                GlideUtil.loadImageDefault(BaseApplication.appContext, starSelectedDrawable, ivSkillGrowingPhotoFive);
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, listBean.getICON(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                                String strName = CommonFunction.getLangText(listBean.getName());
                                tvSkillGrowingNameFive.setText(strName);
                                if (listBean.getUpdateList() == null || listBean.getUpdateList().isEmpty()) {
                                    continue;
                                }
                                for (int j = 0; j < listBean.getUpdateList().size(); j++) {
                                    switch (j) {
                                        case 0:
                                            hpSkillFiveMemberGrowingFist.setVisibility(View.VISIBLE);
                                            User userFirst = new User();
                                            userFirst.setUid(listBean.getUpdateList().get(0).getUserID());
                                            userFirst.setIcon(listBean.getUpdateList().get(0).getICON());
                                            userFirst.setSVip(listBean.getUpdateList().get(0).getVIP());
                                            userFirst.setViplevel(listBean.getUpdateList().get(0).getVipLevel());
//                                            hpSkillFiveMemberGrowingFist.execute(userFirst);
                                            hpSkillFiveMemberGrowingFist.execute(ChatFromType.UNKONW, userFirst, null);
                                            break;
                                        case 1:
                                            hpSkillFiveMemberGrowingSecond.setVisibility(View.VISIBLE);
                                            User userSecond = new User();
                                            userSecond.setUid(listBean.getUpdateList().get(1).getUserID());
                                            userSecond.setIcon(listBean.getUpdateList().get(1).getICON());
                                            userSecond.setSVip(listBean.getUpdateList().get(1).getVIP());
                                            userSecond.setViplevel(listBean.getUpdateList().get(1).getVipLevel());
//                                            hpSkillFiveMemberGrowingSecond.execute(userSecond);
                                            hpSkillFiveMemberGrowingSecond.execute(ChatFromType.UNKONW, userSecond, null);
                                            break;
                                        case 2:
                                            hpSkillFiveMemberGrowingThird.setVisibility(View.VISIBLE);
                                            User userThird = new User();
                                            userThird.setUid(listBean.getUpdateList().get(2).getUserID());
                                            userThird.setIcon(listBean.getUpdateList().get(2).getICON());
                                            userThird.setSVip(listBean.getUpdateList().get(2).getVIP());
                                            userThird.setViplevel(listBean.getUpdateList().get(2).getVipLevel());
//                                            hpSkillFiveMemberGrowingThird.execute(userThird);
                                            hpSkillFiveMemberGrowingThird.execute(ChatFromType.UNKONW, userThird, null);
                                            break;
                                    }

                                }

//                                User userFirst = new User();
//                                userFirst.setUid(listBean.getActiveList().get(0).getUserID());
//                                userFirst.setIcon(listBean.getActiveList().get(0).getICON());
//                                userFirst.setSVip(listBean.getActiveList().get(0).getVIP());
//                                userFirst.setViplevel(listBean.getActiveList().get(0).getVipLevel());
//                                User userSecond = new User();
//                                userSecond.setUid(listBean.getActiveList().get(1).getUserID());
//                                userSecond.setIcon(listBean.getActiveList().get(1).getICON());
//                                userSecond.setSVip(listBean.getActiveList().get(1).getVIP());
//                                userSecond.setViplevel(listBean.getActiveList().get(1).getVipLevel());
//                                User userThird = new User();
//                                userThird.setUid(listBean.getActiveList().get(2).getUserID());
//                                userThird.setIcon(listBean.getActiveList().get(2).getICON());
//                                userThird.setSVip(listBean.getActiveList().get(2).getVIP());
//                                userThird.setViplevel(listBean.getActiveList().get(2).getVipLevel());
//                                hpSkillFiveMemberGrowingFist.execute(userFirst);
//                                hpSkillFiveMemberGrowingSecond.execute(userSecond);
//                                hpSkillFiveMemberGrowingThird.execute(userThird);
                            }
                            break;
                    }
                }
            }
        }
    }


//    public void refreshData(RankSkillParentEntity rankSkillParentEntity) {
//        interactiveBeanList = rankSkillParentEntity.getInteractiveBeanList();
//        if(interactiveBeanList != null){
//            if(!interactiveBeanList.isEmpty()){
//                InteractiveBean interactiveBean;
//                for(int i = 0 ;i < interactiveBeanList.size(); i++){
//                    switch (i){
//                        case 0:
//                           interactiveBean = interactiveBeanList.get(0);
//                            if(interactiveBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, interactiveBean.getIcon(), ivSkillActivePhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillActiveNameOne.setText(interactiveBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(interactiveBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(interactiveBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(interactiveBean.getIconList().get(2));
//                                hpSkillOneMemberFist.execute(userFirst);
//                                hpSkillOneMemberSecond.execute(userSecond);
//                                hpSkillOneMemberThird.execute(userThird);
//                            }
//
//                            break;
//                        case 1:
//                            interactiveBean = interactiveBeanList.get(1);
//                            if(interactiveBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, interactiveBean.getIcon(), ivSkillActivePhotoTwo, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillActiveNameTwo.setText(interactiveBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(interactiveBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(interactiveBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(interactiveBean.getIconList().get(2));
//                                hpSkillTwoMemberFist.execute(userFirst);
//                                hpSkillTwoMemberSecond.execute(userSecond);
//                                hpSkillTwoMemberThird.execute(userThird);
//                            }
//                            break;
//                        case 2:
//                            interactiveBean = interactiveBeanList.get(2);
//                            if(interactiveBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, interactiveBean.getIcon(), ivSkillActivePhotoThree, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillActiveNameThree.setText(interactiveBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(interactiveBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(interactiveBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(interactiveBean.getIconList().get(2));
//                                hpSkillThreeMemberFist.execute(userFirst);
//                                hpSkillThreeMemberSecond.execute(userSecond);
//                                hpSkillThreeMemberThird.execute(userThird);
//                            }
//                            break;
//                        case 3:
//                            interactiveBean = interactiveBeanList.get(3);
//                            if(interactiveBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, interactiveBean.getIcon(), ivSkillActivePhotoFour, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillActiveNameFour.setText(interactiveBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(interactiveBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(interactiveBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(interactiveBean.getIconList().get(2));
//                                hpSkillFourMemberFist.execute(userFirst);
//                                hpSkillFourMemberSecond.execute(userSecond);
//                                hpSkillFourMemberThird.execute(userThird);
//                            }
//                            break;
//                        case 4:
//                            interactiveBean = interactiveBeanList.get(4);
//                            if(interactiveBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, interactiveBean.getIcon(), ivSkillActivePhotoFive, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillActiveNameFive.setText(interactiveBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(interactiveBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(interactiveBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(interactiveBean.getIconList().get(2));
//                                hpSkillFiveMemberFist.execute(userFirst);
//                                hpSkillFiveMemberSecond.execute(userSecond);
//                                hpSkillFiveMemberThird.execute(userThird);
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//
//        growingBeanList = rankSkillParentEntity.getGrowingBeanList();
//        if(growingBeanList != null){
//            if(!growingBeanList.isEmpty()){
//                GrowingBean growingBean;
//                for(int i = 0 ;i < growingBeanList.size(); i++){
//                    switch (i){
//                        case 0:
//                            growingBean = growingBeanList.get(0);
//                            if(growingBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, growingBean.getIcon(), ivSkillGrowingPhotoOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillGrowingNameOne.setText(growingBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(growingBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(growingBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(growingBean.getIconList().get(2));
//                                hpSkillOneMemberGrowingFist.execute(userFirst);
//                                hpSkillOneMemberGrowingSecond.execute(userSecond);
//                                hpSkillOneMemberGrowingThird.execute(userThird);
//                            }
//
//                            break;
//                        case 1:
//                            growingBean = growingBeanList.get(1);
//                            if(growingBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, growingBean.getIcon(), ivSkillGrowingPhotoTwo, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillGrowingNameTwo.setText(growingBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(growingBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(growingBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(growingBean.getIconList().get(2));
//                                hpSkillTwoMemberGrowingFist.execute(userFirst);
//                                hpSkillTwoMemberGrowingSecond.execute(userSecond);
//                                hpSkillTwoMemberGrowingThird.execute(userThird);
//                            }
//                            break;
//                        case 2:
//                            growingBean = growingBeanList.get(2);
//                            if(growingBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, growingBean.getIcon(), ivSkillGrowingPhotoThree, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillGrowingNameThree.setText(growingBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(growingBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(growingBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(growingBean.getIconList().get(2));
//                                hpSkillThreeMemberGrowingFist.execute(userFirst);
//                                hpSkillThreeMemberGrowingSecond.execute(userSecond);
//                                hpSkillThreeMemberGrowingThird.execute(userThird);
//                            }
//                            break;
//                        case 3:
//                            growingBean = growingBeanList.get(3);
//                            if(growingBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, growingBean.getIcon(), ivSkillGrowingPhotoFour, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillGrowingNameFour.setText(growingBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(growingBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(growingBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(growingBean.getIconList().get(2));
//                                hpSkillFourMemberGrowingFist.execute(userFirst);
//                                hpSkillFourMemberGrowingSecond.execute(userSecond);
//                                hpSkillFourMemberGrowingThird.execute(userThird);
//                            }
//                            break;
//                        case 4:
//                            growingBean = growingBeanList.get(4);
//                            if(growingBean != null){
//                                GlideUtil.loadCircleImage(BaseApplication.appContext, growingBean.getIcon(), ivSkillGrowingPhotoFive, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//                                tvSkillGrowingNameFive.setText(growingBean.getName() + "");
//                                User  userFirst = new User();
//                                userFirst.setUid(12355);
//                                userFirst.setIcon(growingBean.getIconList().get(0));
//                                User  userSecond = new User();
//                                userSecond.setUid(12355);
//                                userSecond.setIcon(growingBean.getIconList().get(1));
//                                User  userThird = new User();
//                                userThird.setUid(12355);
//                                userThird.setIcon(growingBean.getIconList().get(2));
//                                hpSkillFiveMemberGrowingFist.execute(userFirst);
//                                hpSkillFiveMemberGrowingSecond.execute(userSecond);
//                                hpSkillFiveMemberGrowingThird.execute(userThird);
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//type 1本周 2上周 3本月 4上月
            case R.id.bt_chat_bar_current_week_charm:
            case R.id.ll_chat_bar_current_week_charm:
                btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
                btLastWeek.setBackgroundResource(0);
                btCurrentMonth.setBackgroundResource(0);
                btLastMonth.setBackgroundResource(0);
                type = 1;
                requestPageData(type, true);

//                cat = 1;
//                type = 1;
//                requestPageData(1, cat, type, false);//type
                break;
            case R.id.bt_chat_bar_last_week_charm:
            case R.id.ll_chat_bar_last_week_charm:
                btCurrentWeek.setBackgroundResource(0);
                btLastWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
                btCurrentMonth.setBackgroundResource(0);
                btLastMonth.setBackgroundResource(0);
                type = 3;
                requestPageData(type, true);

//                cat = 1;
//                type = 3;
//                requestPageData(1, cat, type, true);
                break;
            case R.id.bt_chat_bar_current_month_charm:
            case R.id.ll_chat_bar_current_month_charm:
                btCurrentWeek.setBackgroundResource(0);
                btLastWeek.setBackgroundResource(0);
                btCurrentMonth.setBackgroundResource(R.drawable.ranking_charm_backgound);
                btLastMonth.setBackgroundResource(0);
                type = 2;
                requestPageData(type, true);

//                cat = 1;
//                type = 2;
//                requestPageData(1, cat, type, true);
                break;
            case R.id.bt_chat_bar_last_month_charm:
            case R.id.ll_chat_bar_last_month_charm:
                btCurrentWeek.setBackgroundResource(0);
                btLastWeek.setBackgroundResource(0);
                btCurrentMonth.setBackgroundResource(0);
                btLastMonth.setBackgroundResource(R.drawable.ranking_charm_backgound);
                type = 4;
                requestPageData(type, true);

//                cat = 1;
//                type = 4;
//                requestPageData(1, cat, type, true);
                break;
            case R.id.rl_skill_fragment_active_one:
                skillType = "active";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
//                intent.putExtra("type",type);
//                intent.putExtra("skillType",skillType);
//                intent.putExtra("skillId",skillId);
//                getActivity().startActivity(intent);
                skillToInfoActivity(intent, type, skillType, skillIdActiveOne);
                break;
            case R.id.rl_skill_fragment_active_two:
                skillType = "active";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdActiveTwo);
                break;
            case R.id.rl_skill_fragment_active_three:
                skillType = "active";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdActiveThree);
                break;
            case R.id.rl_skill_fragment_active_four:
                skillType = "active";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdActiveFour);
                break;
            case R.id.rl_skill_fragment_active_five:
                skillType = "active";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdActiveFive);
                break;
            case R.id.rl_skill_fragment_grow_one:
                skillType = "update";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdGrowOne);
                break;
            case R.id.rl_skill_fragment_grow_two:
                skillType = "update";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdGrowTwo);
                break;
            case R.id.rl_skill_fragment_grow_three:
                skillType = "update";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdGrowThree);
                break;
            case R.id.rl_skill_fragment_grow_four:
                skillType = "update";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdGrowFour);
                break;
            case R.id.rl_skill_fragment_grow_five:
                skillType = "update";
                intent = new Intent(getActivity(), RankSkillItemInfoActivity.class);
                skillToInfoActivity(intent, type, skillType, skillIdGrowFive);
                break;

        }


    }

    public void skillToInfoActivity(Intent intent, int type, String skillType, int skillId) {
        handleSkillInfoTitle(skillType, skillId);
        intent.putExtra("type", type);
        intent.putExtra("skillType", skillType);
        intent.putExtra("skillId", skillId);
        intent.putExtra("skillInfoTitle", skillInfoTitle);
        getActivity().startActivity(intent);

    }

    public void handleSkillInfoTitle(String skillType, int skillId) {
        if (1 == skillId) {
            if (SKILL_TYPE_ACTIVE.equals(skillType)) {
                skillInfoTitle = getString(R.string.other_info_skill_title_juhuacan);
            } else {
                skillInfoTitle = getString(R.string.other_info_skill_title_juhuacan);
            }

        } else if (2 == skillId) {
            if (SKILL_TYPE_ACTIVE.equals(skillType)) {
                skillInfoTitle = getString(R.string.other_info_skill_title_dameme);
            } else {
                skillInfoTitle = getString(R.string.other_info_skill_title_dameme);
            }

        } else if (3 == skillId) {
            if (SKILL_TYPE_ACTIVE.equals(skillType)) {
                skillInfoTitle = getString(R.string.other_info_skill_title_jinguzhou);
            } else {
                skillInfoTitle = getString(R.string.other_info_skill_title_jinguzhou);
            }

        } else if (4 == skillId) {
            if (SKILL_TYPE_ACTIVE.equals(skillType)) {
                skillInfoTitle = getString(R.string.other_info_skill_title_wuyingjiao);
            } else {
                skillInfoTitle = getString(R.string.other_info_skill_title_wuyingjiao);
            }

        } else if (5 == skillId) {
            if (SKILL_TYPE_ACTIVE.equals(skillType)) {
                skillInfoTitle = getString(R.string.other_info_skill_title_baohufei);
            } else {
                skillInfoTitle = getString(R.string.other_info_skill_title_baohufei);
            }

        }

    }


    public void update() {

        btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
        btLastWeek.setBackgroundResource(0);
        btCurrentMonth.setBackgroundResource(0);
        btLastMonth.setBackgroundResource(0);
        type = 1;

//        cat = 2;
//        type = 1;
//        requestPageData(pageNo, cat, type,false);
        if (scrollViewSkillFragment != null) {
            scrollViewSkillFragment.post(new Runnable() {
                @Override
                public void run() {
                    scrollViewSkillFragment.fullScroll(ScrollView.FOCUS_UP);
                }
            });

        }


    }

    private void requestPageData(int type, boolean isShowDiaglog) {
        if (isShowDiaglog) {
            showWaitDialog();
        }

        GET_CHARM_DATA_FLAG = RankingHttpProtocol.getInstance().getChatBarSkillRankingData(getContext(), type,
                this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            if (GET_CHARM_DATA_FLAG == flag) {
                hideWaitDialog();
//                RankSkillParentEntity bean = GsonUtil.getInstance().getServerBean(result, RankSkillParentEntity.class);
                SkillRankingEntity bean = GsonUtil.getInstance().getServerBean(result, SkillRankingEntity.class);
                if (bean == null)
                    return;
                if (bean.isSuccess()) {
                    refreshData(bean);
                } else {
                    ErrorCode.showError(getActivity(), result);

                }

            }

        } else {
            hideWaitDialog();
            ErrorCode.showError(getActivity(), result);

        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (getActivity() == null) return;
        hideWaitDialog();
        ErrorCode.toastError(getActivity(), e);

    }

    public void initRankingView() {

    }
}
