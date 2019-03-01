package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.RankingHttpProtocol;
import net.iaround.model.ranking.RankingEntity.RankingBean;
import net.iaround.model.ranking.RankingEntityType;
import net.iaround.model.ranking.SkillRankingInfoEntity;
import net.iaround.model.ranking.SkillRankingInfoEntity.ListBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.RankingCommonAdapter;
import net.iaround.ui.view.HeadPhotoView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2017/8/16.
 */

public class RankSkillItemInfoActivity extends ActionBarActivity implements View.OnClickListener,HttpCallBack {

    private RelativeLayout viewCharmBottom;
    private View headerView;
    private TextView btCurrentWeek;
    private TextView btLastWeek;
    private TextView btCurrentMonth;
    private TextView btLastMonth;
    private Context mContext;
    private PullToRefreshListView lvCharm;
    private RelativeLayout rlCharmFragmentEmpty;
    private SkillRankingInfoEntity.SkillBean skillBean;
    private List<ListBean> rankingBeanList;
    private List<RankingBean> rankingBeanListTotal;
    private List<ListBean> rankingBeanListThree;
    private List<RankingEntityType> rankingEntityTypeList;
    private RankingCommonAdapter rankingAdapter;
    private FrameLayout flCurrentWeek;
    private LinearLayout llCurrentWeek;
    private LinearLayout llLastWeek;
    private LinearLayout llCurrentMonth;
    private LinearLayout llLastMonth;

    private LinearLayout llRankingCharm;


    private HeadPhotoView hpSecond;
    private TextView tvRankingNameSecond;
    private LinearLayout llRaningSexSecond;
    private ImageView ivSexSecond;
    private TextView tvAgeSecond;

    private HeadPhotoView hpFirst;
    private TextView tvRankingNameFirst;
    private LinearLayout llRaningSexFirst;
    private ImageView ivSexFirst;
    private TextView tvAgeFirst;

    private HeadPhotoView hpThird;
    private TextView tvRankingNameThird;
    private LinearLayout llRaningSexThird;
    private ImageView ivSexThird;
    private TextView tvAgeThird;

    private FrameLayout flBack;
    private TextView tvTitle;

    /**
     * 请求关注数据的flag
     */
    private long GET_CHARM_DATA_FLAG;
    /**
     * 页码
     */
    private int pageNum = 1;
    /**
     * 页面展示数量
     */
    private int pageSize = 10;

    /**
     * 类型  1魅力 2富豪 3 技能
     */
    private int cat = 1;
    /**
     * 1本周 2上周 3本月 4上月
     */
    private int type = 1;
    /**
     * 技能榜类型 active 互动  update成长
     */
    private String skillType = "";
    /**
     * 技能类型 1 菊花残 2
     */
    private int skillId;
    /**
     * 技能详情标题
     */
    private String skillInfoTitle = "";

    private int pageNo = 1;//加载页数


    private int mCurrentPage = 0;
    private int mTotalNum;
    private int mTotalPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_skill_iteminfo);
        initIntentData();
        initView();
        initListener();
        initData();
    }

    private void initIntentData(){
        Intent intent = getIntent();
        type = intent.getIntExtra("type",type);
        skillId = intent.getIntExtra("skillId",skillId);
        skillType = intent.getStringExtra("skillType");
        skillInfoTitle = intent.getStringExtra("skillInfoTitle");

    }


    private void initView() {
        flBack = (FrameLayout) findViewById(R.id.fl_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(skillInfoTitle);
        TextPaint tp = tvTitle.getPaint();
        tp.setFakeBoldText(true);
        tvTitle.setTextSize(15);
        viewCharmBottom = (RelativeLayout) findViewById(R.id.view_fragment_charm_bottm);
        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_ranking_charm_fragment, null);
        btCurrentWeek = (TextView) headerView.findViewById(R.id.bt_chat_bar_current_week_charm);
        btLastWeek = (TextView) headerView.findViewById(R.id.bt_chat_bar_last_week_charm);
        btCurrentMonth = (TextView) headerView.findViewById(R.id.bt_chat_bar_current_month_charm);
        btLastMonth = (TextView) headerView.findViewById(R.id.bt_chat_bar_last_month_charm);
        llCurrentWeek = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_current_week_charm);
        llLastWeek = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_last_week_charm);
        llCurrentMonth = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_current_month_charm);
        llLastMonth = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_last_month_charm);
        llRankingCharm = (LinearLayout) findViewById(R.id.ll_ranking_fragment_charm);
        Bitmap bitmap=readBitMap(getActivity(),R.drawable.ranking_skill_growing_background);
        Drawable drawable=new BitmapDrawable(bitmap);
        llRankingCharm.setBackgroundDrawable(drawable);
//        flCurrentWeek = (FrameLayout) headerView.findViewById(R.id.fl_current_week);
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

        rlCharmFragmentEmpty = (RelativeLayout) findViewById(R.id.ll_fragment_charm_empty);

        hpFirst = (HeadPhotoView) headerView.findViewById(R.id.iv_headview_charm_first);
        tvRankingNameFirst = (TextView) headerView.findViewById(R.id.tv_headview_person_first);
        llRaningSexFirst = (LinearLayout) headerView.findViewById(R.id.ly_user_age_first);
        ivSexFirst = (ImageView) headerView.findViewById(R.id.iv_user_sex_first);
        tvAgeFirst = (TextView) headerView.findViewById(R.id.tv_user_age_first);

        hpSecond = (HeadPhotoView) headerView.findViewById(R.id.iv_headview_charm_second);
        tvRankingNameSecond = (TextView) headerView.findViewById(R.id.tv_headview_person_second);
        llRaningSexSecond = (LinearLayout) headerView.findViewById(R.id.ly_user_age_second);
        ivSexSecond = (ImageView) headerView.findViewById(R.id.iv_user_sex_second);
        tvAgeSecond = (TextView) headerView.findViewById(R.id.tv_user_age_second);

        hpThird = (HeadPhotoView) headerView.findViewById(R.id.iv_headview_charm_third);
        tvRankingNameThird = (TextView) headerView.findViewById(R.id.tv_headview_person_third);
        llRaningSexThird = (LinearLayout) headerView.findViewById(R.id.ly_user_age_third);
        ivSexThird = (ImageView) headerView.findViewById(R.id.iv_user_sex_third);
        tvAgeThird = (TextView) headerView.findViewById(R.id.tv_user_age_third);


        btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
        btCurrentWeek.setOnClickListener(this);
        btLastWeek.setOnClickListener(this);
        btCurrentMonth.setOnClickListener(this);
        btLastMonth.setOnClickListener(this);

        llCurrentWeek.setOnClickListener(this);
        llLastWeek.setOnClickListener(this);
        llCurrentMonth.setOnClickListener(this);
        llLastMonth.setOnClickListener(this);
        lvCharm = (PullToRefreshListView)findViewById(R.id.lv_chat_bar_charm);
        lvCharm.getRefreshableView().addHeaderView(headerView);
        lvCharm.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//PULL_FROM_START
        lvCharm.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        lvCharm.getRefreshableView().setFastScrollEnabled(false);
        lvCharm.setOnRefreshListener(mOnRefreshListener);
        rankingAdapter = new RankingCommonAdapter(getActivity(), 3, rankingEntityTypeList);
        lvCharm.setAdapter(rankingAdapter);
        lvCharm.setVerticalScrollBarEnabled(false);
//        flCurrentWeek.setOnClickListener(this);
        setBackGround(type);



    }

    private void initListener() {
        flBack.setOnClickListener(this);

    }

    private void initData() {
        rankingBeanList = new ArrayList<>();
        rankingBeanListTotal = new ArrayList<>();
        rankingBeanListThree = new ArrayList<>();
        rankingEntityTypeList = new ArrayList<>();
//        type = 1;

//        GET_CHARM_DATA_FLAG = RankingHttpProtocol.getInstance().getChatBarAttentionData(RankSkillItemInfoActivity.this, pageNum, pageSize, cat, type,
//                this);//type
        requestPageData(1,type,skillType,skillId, false);//type

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void requestPageData(int page,  int type,String index,int skillId, boolean isShowDiaglog) {
        if (isShowDiaglog) {
            showWaitDialog();
        }

        GET_CHARM_DATA_FLAG = RankingHttpProtocol.getInstance().getChatBarSkillRankingDataInfo(BaseApplication.appContext,type,skillId,index,page,pageSize,
                this);
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            mCurrentPage = 0;
            requestPageData(1,type,"active",1, false);//type
        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {

            if (mCurrentPage < mTotalPage) {
                requestPageData(mCurrentPage + 1, type,skillType,skillId,true);
                hideWaitDialog();
                lvCharm.onRefreshComplete();
            } else {
                lvCharm.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        hideWaitDialog();
                        CommonFunction.toastMsg(RankSkillItemInfoActivity.this, R.string.no_more_data);
                        lvCharm.onRefreshComplete();
                    }
                }, 200);
            }
        }
    };

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//cat 魅力，富豪 type
            case R.id.bt_chat_bar_current_week_charm:
            case R.id.ll_chat_bar_current_week_charm:
//                btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
//                btLastWeek.setBackgroundResource(0);
//                btCurrentMonth.setBackgroundResource(0);
//                btLastMonth.setBackgroundResource(0);

                type = 1;
                setBackGround(type);
                requestPageData(1,type,skillType,skillId, true);//type
//                requestPageData(1,type,"active",1, false);//type
                break;
            case R.id.bt_chat_bar_last_week_charm:
            case R.id.ll_chat_bar_last_week_charm:

                type = 3;
                setBackGround(type);
                requestPageData(1,type,skillType,skillId, true);//type
//                requestPageData(1,type,"active",1, false);//type
                break;
            case R.id.bt_chat_bar_current_month_charm:
            case R.id.ll_chat_bar_current_month_charm:
                type = 2;
                setBackGround(type);
                requestPageData(1,type,skillType,skillId, true);//type
//                requestPageData(1,type,"active",1, false);//type
                break;
            case R.id.bt_chat_bar_last_month_charm:
            case R.id.ll_chat_bar_last_month_charm:

                type = 4;
                setBackGround(type);
                requestPageData(1,type,skillType,skillId, true);//type
//                requestPageData(1,type,"active",1, false);//type
                break;
            case R.id.fl_back:
                finish();
                break;

        }


    }

    public void setBackGround(int type){
        if(1 == type){
            btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
            btLastWeek.setBackgroundResource(0);
            btCurrentMonth.setBackgroundResource(0);
            btLastMonth.setBackgroundResource(0);

        }else if(3 == type){
            btCurrentWeek.setBackgroundResource(0);
            btLastWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
            btCurrentMonth.setBackgroundResource(0);
            btLastMonth.setBackgroundResource(0);

        }else if(2 == type){
            btCurrentWeek.setBackgroundResource(0);
            btLastWeek.setBackgroundResource(0);
            btCurrentMonth.setBackgroundResource(R.drawable.ranking_charm_backgound);
            btLastMonth.setBackgroundResource(0);

        }else if(4 == type){
            btCurrentWeek.setBackgroundResource(0);
            btLastWeek.setBackgroundResource(0);
            btCurrentMonth.setBackgroundResource(0);
            btLastMonth.setBackgroundResource(R.drawable.ranking_charm_backgound);

        }

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            if (GET_CHARM_DATA_FLAG == flag) {
                hideWaitDialog();
                lvCharm.onRefreshComplete();
                SkillRankingInfoEntity bean = GsonUtil.getInstance().getServerBean(result, SkillRankingInfoEntity.class);
                if (bean == null)
                    return;
                if (bean.isSuccess()) {
                    if (bean.getList() != null) {
                        rankingBeanList = bean.getList();
                        skillBean = bean.getSkill();

//                        mCurrentPage = bean.getPage_no();
//                        mTotalNum = bean.getTotal_page();
//                        mTotalPage = mTotalNum / pageSize;
//                        if (mTotalPage % pageSize > 0) {
//                            mTotalPage++;
//                        }

                        mCurrentPage = bean.getPageNo();
                        mTotalPage = bean.getPages();

//                        if (rankingBeanList.size() > 4) {
//                            if (mCurrentPage == 1) {
//                                rankingBeanListThree = rankingBeanList.subList(0, 4);
//                                updateRankingThree(rankingBeanListThree);
//                                rankingBeanList = rankingBeanList.subList(3, rankingBeanList.size());
//                            }
//
//                            rankingBeanListTotal.addAll(rankingBeanList);
//                            rankingAdapter.updateData(rankingBeanListTotal);
////                            lvCharm.setAdapter(rankingAdapter);
//                        }
                        if (mCurrentPage == 1) {
                            rankingEntityTypeList.clear();
                            rankingBeanListThree.clear();
                            if(rankingBeanList.size() <=3 && rankingBeanList.size() > 0){
                                for(int i = 0;i<rankingBeanList.size();i++){
                                    switch (i){
                                        case 0:
                                            ListBean listBeanFirst = rankingBeanList.get(0);
                                            rankingBeanListThree.add(listBeanFirst);
                                            break;
                                        case 1:
                                            ListBean listBeanSecond = rankingBeanList.get(1);
                                            rankingBeanListThree.add(listBeanSecond);
                                            break;
                                        case 2:
                                            ListBean listBeanThird = rankingBeanList.get(2);
                                            rankingBeanListThree.add(listBeanThird);
                                            break;

                                    }
                                }
                                RankingEntityType rankingEntityTypeThree = new RankingEntityType();
                                rankingEntityTypeThree.setType(1);
                                rankingEntityTypeThree.setSkill(true);
                                rankingEntityTypeThree.setSkillType(bean.getIndex());
                                rankingEntityTypeThree.setShowType(type);
                                rankingEntityTypeThree.setObject(rankingBeanListThree);
                                if(skillBean != null){
                                    rankingEntityTypeThree.setSkillObject(skillBean);
                                }
                                rankingEntityTypeList.add(rankingEntityTypeThree);

                            }else if (rankingBeanList.size() >3) {
                                rankingBeanListThree = rankingBeanList.subList(0, 3);
                                RankingEntityType rankingEntityTypeThree = new RankingEntityType();
                                rankingEntityTypeThree.setType(1);
                                rankingEntityTypeThree.setSkill(true);
                                rankingEntityTypeThree.setSkillType(bean.getIndex());
                                rankingEntityTypeThree.setShowType(type);
                                rankingEntityTypeThree.setObject(rankingBeanListThree);
                                if(skillBean != null){
                                    rankingEntityTypeThree.setSkillObject(skillBean);
                                }
                                rankingEntityTypeList.add(rankingEntityTypeThree);
//                                for (int i = 0; i < 3; i++) {
//                                    RankingEntityType rankingEntityType = new RankingEntityType();
//                                    rankingEntityType.setType(1);
//                                    rankingEntityType.setObject(rankingBeanListThree.get(i));
//                                    rankingEntityTypeList.add(rankingEntityType);
//                                }
                                rankingBeanList = rankingBeanList.subList(3, rankingBeanList.size());
                                for (int i = 0; i <= rankingBeanList.size() - 1; i++) {
                                    RankingEntityType rankingEntityType = new RankingEntityType();
                                    rankingEntityType.setType(2);
                                    rankingEntityType.setSkillType(bean.getIndex());
                                    rankingEntityType.setShowType(type);
                                    rankingEntityType.setObject(rankingBeanList.get(i));
                                    if(skillBean != null){
                                        rankingEntityType.setSkillObject(skillBean);
                                    }
                                    rankingEntityTypeList.add(rankingEntityType);
                                }
                            }

                        } else {
                            for (int i = 0; i <= rankingBeanList.size() - 1; i++) {
                                RankingEntityType rankingEntityType = new RankingEntityType();
                                rankingEntityType.setType(2);
                                rankingEntityType.setSkillType(bean.getIndex());
                                rankingEntityType.setShowType(type);
                                rankingEntityType.setObject(rankingBeanList.get(i));
                                if(skillBean != null){
                                    rankingEntityType.setSkillObject(skillBean);
                                }
                                rankingEntityTypeList.add(rankingEntityType);
                            }
                        }
                        if (rankingEntityTypeList == null || rankingEntityTypeList.isEmpty()) {
                            rlCharmFragmentEmpty.setVisibility(View.VISIBLE);
                            viewCharmBottom.setVisibility(View.GONE);
                            lvCharm.setMode(PullToRefreshBase.Mode.DISABLED);
                        } else {
                            rlCharmFragmentEmpty.setVisibility(View.GONE);
                            viewCharmBottom.setVisibility(View.VISIBLE);
                            lvCharm.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                        }
                        rankingAdapter.updateData(rankingEntityTypeList);


                    }

                } else {
                    ErrorCode.showError(getActivity(), result);
                }
            }
        } else {
            hideWaitDialog();
            lvCharm.onRefreshComplete();
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        lvCharm.onRefreshComplete();

    }
}
