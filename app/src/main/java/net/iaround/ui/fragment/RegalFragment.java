package net.iaround.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.RankingHttpProtocol;
import net.iaround.model.ranking.RankingEntity;
import net.iaround.model.ranking.RankingEntity.RankingBean;
import net.iaround.model.ranking.RankingEntityType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.RankingCommonAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：gh on 2017/6/11 03:17
 * <p>
 * 邮箱：jt_gaohang@163.com
 */
public class RegalFragment extends LazyLoadBaseFragment implements View.OnClickListener, HttpCallBack {
    private RelativeLayout viewRegalBottom;
    private View headerView;
    private TextView btCurrentWeek;
    private TextView btLastWeek;
    private TextView btCurrentMonth;
    private TextView btLastMonth;
    private Context mContext;
    private PullToRefreshListView lvCharm;
    private RelativeLayout rlCharmFragmentEmpty;
    private List<RankingBean> rankingBeanList;
    private List<RankingBean> rankingBeanListTotal;
    private List<RankingBean> rankingBeanListThree;
    private List<RankingEntityType> rankingEntityTypeList;
    private RankingCommonAdapter rankingAdapter;

    private LinearLayout llCurrentWeek;
    private LinearLayout llLastWeek;
    private LinearLayout llCurrentMonth;
    private LinearLayout llLastMonth;
    private LinearLayout llRankingRegal;


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
     * 类型  1魅力 2富豪
     */
    private int cat = 2;
    /**
     * 1本周 2上周 3本月 4上月
     */
    private int type = 1;

    private int pageNo = 1;//加载页数


    private int mCurrentPage = 0;
    private int mTotalNum;
    private int mTotalPage = 1;

    @Override
    protected int setContentView() {
        return R.layout.fragment_regal;
    }

    @Override
    protected boolean lazyLoad() {
        showWaitDialog();
        initView(getContentView());
        initData();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_regal, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView(view);
//        initData();
//    }


    public void update() {
        btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
        btLastWeek.setBackgroundResource(0);
        btCurrentMonth.setBackgroundResource(0);
        btLastMonth.setBackgroundResource(0);

        cat = 2;
        type = 1;
        requestPageData(pageNo, cat, type, false);
        lvCharm.getRefreshableView().postDelayed(new Runnable() {
            @Override
            public void run() {
                lvCharm.getRefreshableView().setSelection(0);
            }
        }, 100);
    }

    private void initView(View view) {


//        rgRanking = (RadioGroup) view.findViewById(R.id.rg_ranking_charm);
//        viewRegalBottom = (RelativeLayout) view.findViewById(R.id.view_fragment_charm_bottm);
        if (headerView == null) {

            headerView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_ranking_regal_fragment, null);
            btCurrentWeek = (TextView) headerView.findViewById(R.id.bt_chat_bar_current_week_regal);
            btLastWeek = (TextView) headerView.findViewById(R.id.bt_chat_bar_last_week_regal);
            btCurrentMonth = (TextView) headerView.findViewById(R.id.bt_chat_bar_current_month_regal);
            btLastMonth = (TextView) headerView.findViewById(R.id.bt_chat_bar_last_month_regal);

            llCurrentWeek = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_current_week_regal);
            llLastWeek = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_last_week_regal);
            llCurrentMonth = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_current_month_regal);
            llLastMonth = (LinearLayout) headerView.findViewById(R.id.ll_chat_bar_last_month_regal);

            llRankingRegal = (LinearLayout) view.findViewById(R.id.ll_ranking_fragment_regal);
            Bitmap bitmap = readBitMap(getActivity(), R.drawable.ranking_regal_background);
            Drawable drawable = new BitmapDrawable(bitmap);
            llRankingRegal.setBackgroundDrawable(drawable);

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
            btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
            btCurrentWeek.setOnClickListener(this);
            btLastWeek.setOnClickListener(this);
            btCurrentMonth.setOnClickListener(this);
            btLastMonth.setOnClickListener(this);

            llCurrentWeek.setOnClickListener(this);
            llLastWeek.setOnClickListener(this);
            llCurrentMonth.setOnClickListener(this);
            llLastMonth.setOnClickListener(this);
            lvCharm = (PullToRefreshListView) view.findViewById(R.id.lv_chat_bar_charm);
            rlCharmFragmentEmpty = (RelativeLayout) view.findViewById(R.id.ll_fragment_charm_empty);
            lvCharm.getRefreshableView().addHeaderView(headerView);
            lvCharm.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//PULL_FROM_START
            lvCharm.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
            lvCharm.getRefreshableView().setFastScrollEnabled(false);
            lvCharm.setOnRefreshListener(mOnRefreshListener);
            rankingAdapter = new RankingCommonAdapter(getActivity(), 2, rankingEntityTypeList);
            lvCharm.setAdapter(rankingAdapter);
            lvCharm.setVerticalScrollBarEnabled(false);
        }

    }

    private void initData() {
        rankingBeanList = new ArrayList<>();
        rankingBeanListTotal = new ArrayList<>();
        rankingBeanListThree = new ArrayList<>();
        rankingEntityTypeList = new ArrayList<>();
        GET_CHARM_DATA_FLAG = RankingHttpProtocol.getInstance().getChatBarAttentionData(getContext(), pageNum, pageSize, cat, type,
                this);


    }

    private void requestPageData(int page, int cat, int type, boolean isShowDialog) {
        if (isShowDialog) {
            showWaitDialog();
        }

        GET_CHARM_DATA_FLAG = RankingHttpProtocol.getInstance().getChatBarAttentionData(getContext(), page, pageSize, cat, type,
                this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {//cat 魅力，富豪 type
            case R.id.bt_chat_bar_current_week_regal:
            case R.id.ll_chat_bar_current_week_regal:
                btCurrentWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
                btLastWeek.setBackgroundResource(0);
                btCurrentMonth.setBackgroundResource(0);
                btLastMonth.setBackgroundResource(0);

                cat = 2;
                type = 1;
                requestPageData(pageNo, cat, type, false);
                break;
            case R.id.bt_chat_bar_last_week_regal:
            case R.id.ll_chat_bar_last_week_regal:
                btCurrentWeek.setBackgroundResource(0);
                btLastWeek.setBackgroundResource(R.drawable.ranking_charm_backgound);
                btCurrentMonth.setBackgroundResource(0);
                btLastMonth.setBackgroundResource(0);

//                cat = 2;
//                type = 2;
                cat = 2;
                type = 3;
                requestPageData(pageNo, cat, type, true);
                break;
            case R.id.bt_chat_bar_current_month_regal:
            case R.id.ll_chat_bar_current_month_regal:
                btCurrentWeek.setBackgroundResource(0);
                btLastWeek.setBackgroundResource(0);
                btCurrentMonth.setBackgroundResource(R.drawable.ranking_charm_backgound);
                btLastMonth.setBackgroundResource(0);
                cat = 2;
                type = 2;
//                cat = 2;
//                type = 3;
                requestPageData(pageNo, cat, type, true);
                break;
            case R.id.bt_chat_bar_last_month_regal:
            case R.id.ll_chat_bar_last_month_regal:
                btCurrentWeek.setBackgroundResource(0);
                btLastWeek.setBackgroundResource(0);
                btCurrentMonth.setBackgroundResource(0);
                btLastMonth.setBackgroundResource(R.drawable.ranking_charm_backgound);

                cat = 2;
                type = 4;
                requestPageData(pageNo, cat, type, true);
                break;

        }
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            if (GET_CHARM_DATA_FLAG == flag) {
                hideWaitDialog();
                lvCharm.onRefreshComplete();

                RankingEntity bean = GsonUtil.getInstance().getServerBean(result, RankingEntity.class);
                if (bean != null) {
                    if (bean.isSuccess()) {
                        if (bean.getList() != null) {
                            rankingBeanList = bean.getList();

//                        mCurrentPage = bean.getPage_no();
//                        mTotalNum = bean.getTotal_page();
//                        mTotalPage = mTotalNum / pageSize;
//                        if (mTotalPage % pageSize > 0) {
//                            mTotalPage++;
//                        }

                            mCurrentPage = bean.getPage_no();
                            mTotalPage = bean.getTotal_page();

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
                                if (rankingBeanList.size() > 3) {
                                    rankingBeanListThree = rankingBeanList.subList(0, 3);
                                    RankingEntityType rankingEntityTypeThree = new RankingEntityType();
                                    rankingEntityTypeThree.setType(1);
                                    rankingEntityTypeThree.setShowType(type);
                                    rankingEntityTypeThree.setSkill(false);
                                    rankingEntityTypeThree.setObject(rankingBeanListThree);
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
                                        rankingEntityType.setShowType(type);
                                        rankingEntityType.setObject(rankingBeanList.get(i));
                                        rankingEntityTypeList.add(rankingEntityType);
                                    }
                                    rankingAdapter.updateData(rankingEntityTypeList);
                                }

                            } else {
                                for (int i = 0; i <= rankingBeanList.size() - 1; i++) {
                                    RankingEntityType rankingEntityType = new RankingEntityType();
                                    rankingEntityType.setType(2);
                                    rankingEntityType.setShowType(type);
                                    rankingEntityType.setObject(rankingBeanList.get(i));
                                    rankingEntityTypeList.add(rankingEntityType);
                                }
                            }

                            if (rankingEntityTypeList == null || rankingEntityTypeList.isEmpty()) {
//                                viewRegalBottom.setVisibility(View.GONE);
                                rlCharmFragmentEmpty.setVisibility(View.VISIBLE);
                                lvCharm.setMode(PullToRefreshBase.Mode.DISABLED);
                            } else {
                                rlCharmFragmentEmpty.setVisibility(View.GONE);
//                                viewRegalBottom.setVisibility(View.VISIBLE);
                                lvCharm.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                            }
                            rankingAdapter.updateData(rankingEntityTypeList);
                        }

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
        hideWaitDialog();
    }

    // 刷新的监听器
    private PullToRefreshBase.OnRefreshListener2<ListView> mOnRefreshListener = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            mCurrentPage = 0;
            requestPageData(pageNo, cat, type, false);
        }

        @Override
        public void onPullUpToRefresh(
                final PullToRefreshBase<ListView> refreshView) {

            if (mCurrentPage < mTotalPage) {
                requestPageData(mCurrentPage + 1, cat, type, false);
                lvCharm.onRefreshComplete();
            } else {
                lvCharm.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        hideWaitDialog();
                        CommonFunction.toastMsg(getContext(), R.string.no_more_data);
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
}
