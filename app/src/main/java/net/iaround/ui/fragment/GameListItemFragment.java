package net.iaround.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.PlayMatesListBean;
import net.iaround.model.entity.PlayMatesListFilterBean;
import net.iaround.model.entity.PlayMatesListFilterItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.menu.FilterGameListPopupWindow;
import net.iaround.utils.OnClickUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yz on 2018/8/1.
 * 陪玩分类详情列表-数据
 */

public class GameListItemFragment extends LazyLoadBaseFragment implements HttpCallBack, LocationUtil.MLocationListener {


    private PlayMatesListBean mListBean;

    private PullToRefreshListView mPtrlGameList;
    private GameListAdapter mAdapter;
    private FilterGameListPopupWindow mWindow;
    private ArrayList<PlayMatesListBean.UsersBean> mUsersBeans = new ArrayList<>();
    private PlayMatesListFilterBean mFilterBean = new PlayMatesListFilterBean();
    private ArrayList<PlayMatesListFilterItemBean> mGenders = new ArrayList<>();//筛选项性别集合
    private ArrayList<PlayMatesListFilterItemBean> mPrices = new ArrayList<>();//筛选项价格集合
    private ArrayList<PlayMatesListFilterItemBean> mLevels = new ArrayList<>();//筛选项等级集合
    private int mPageNo = 1;
    private int mPageSize = 24;

    //筛选条件
    private String mGender;//性别
    private String mPrice;//价格
    private String mLevel;//等级

    private long mGameId;

    private LocationUtil mLocationUtil;

    @Override
    protected int setContentView() {
        return R.layout.fragment_game_list;
    }

    @Override
    protected boolean lazyLoad() {
        if (getArguments() != null) {
            mListBean = (PlayMatesListBean) getArguments().getSerializable("GameList");
            if (mListBean.users != null) {

                //统计每个分类分别查看人数
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("uid", Common.getInstance().getUid());
                map.put("GameName",mListBean.gameslist.GameName);
                MobclickAgent.onEvent(BaseApplication.appContext,"watch_every_category_person_num",map);

                updateFilter(1, "all", "all", "all", mListBean.gameslist.GameID);
                mUsersBeans.addAll(mListBean.users);
                mAdapter.updateData(mUsersBeans);
                initFilterData();
            } else {
                showFail();
            }
        }
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPtrlGameList = (PullToRefreshListView) getContentView().findViewById(R.id.ptrl_game_list);
        mAdapter = new GameListAdapter();
        mPtrlGameList.setMode(PullToRefreshBase.Mode.BOTH);
        mPtrlGameList.setPullToRefreshOverScrollEnabled(false);
        mPtrlGameList.setAdapter(mAdapter);

        mWindow = new FilterGameListPopupWindow(getContext());
        mWindow.setOnSaveClickListener(new FilterGameListPopupWindow.OnSaveClickListener() {
            @Override
            public void onSaveClick(PlayMatesListFilterBean listBean) {
                mFilterBean = listBean;

                for (int i = 0; i < mFilterBean.genders.size(); i++) {
                    if (mFilterBean.genders.get(i).isSelected) {
                        mGender = mFilterBean.genders.get(i).itemId;
                    }
                }
                for (int i = 0; i < mFilterBean.prices.size(); i++) {
                    if (mFilterBean.prices.get(i).isSelected) {
                        mPrice = mFilterBean.prices.get(i).itemId;
                    }
                }
                for (int i = 0; i < mFilterBean.levels.size(); i++) {
                    if (mFilterBean.levels.get(i).isSelected) {
                        mLevel = mFilterBean.levels.get(i).itemId;
                    }
                }
                if (mListBean != null && mListBean.gameslist != null) {
                    updateFilter(1, mGender, mPrice, mLevel, mListBean.gameslist.GameID);
                    requestData(false);
                }
            }
        });

        mPtrlGameList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPageNo = 1;
                requestData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mListBean != null) {
                    if (mUsersBeans.size() >= mListBean.amount) {
                        mPtrlGameList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CommonFunction.toastMsg(BaseApplication.appContext, R.string.no_more);
                                mPtrlGameList.onRefreshComplete();
                            }
                        }, 200);

                    } else {
                        mPageNo = mListBean.pageno + 1;
                        requestData(false);
                    }
                } else {
                    mPtrlGameList.onRefreshComplete();
                }
            }
        });

        mPtrlGameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(OnClickUtil.isFastClick()){
                    return;
                }
                PlayMatesListBean.UsersBean bean = (PlayMatesListBean.UsersBean) parent.getAdapter().getItem(position);
                if (bean != null) {
                    InnerJump.JumpGamerDetail(getContext(), mGameId, bean.userid);
                }
            }
        });

    }

    public FilterGameListPopupWindow getWindow() {
        return mWindow;
    }

    //初始化筛选选项数据
    private void initFilterData() {
        if (mListBean != null && mListBean.conditions != null && mGenders.size() <= 0) {

            if (mListBean.conditions.gender != null) {

                for (int i = 0; i < mListBean.conditions.gender.size(); i++) {
                    PlayMatesListFilterItemBean itemBean = new PlayMatesListFilterItemBean();
                    itemBean.isSelected = false;
                    itemBean.itemId = mListBean.conditions.gender.get(i);
                    if ("all".equals(itemBean.itemId)) {
                        itemBean.isSelected = true;
                        itemBean.itemName = getString(R.string.encounter_filter_all);
                    } else if ("f".equals(itemBean.itemId)) {
                        itemBean.itemName = getString(R.string.encounter_filter_woman);
                    } else if ("m".equals(itemBean.itemId)) {
                        itemBean.itemName = getString(R.string.encounter_filter_man);
                    }
                    mGenders.add(itemBean);
                }
            }
            if (mListBean.conditions.price != null) {

                for (int i = 0; i < mListBean.conditions.price.size(); i++) {
                    PlayMatesListFilterItemBean itemBean = new PlayMatesListFilterItemBean();
                    itemBean.isSelected = false;
                    itemBean.itemId = mListBean.conditions.price.get(i);
                    if ("all".equals(itemBean.itemId)) {
                        itemBean.isSelected = true;
                        itemBean.itemName = getString(R.string.encounter_filter_all);
                    } else {
                        itemBean.itemName = mListBean.conditions.price.get(i);
                    }
                    mPrices.add(itemBean);
                }
            }
            if (mListBean.conditions.level != null) {
                for (int i = 0; i < mListBean.conditions.level.size(); i++) {
                    PlayMatesListFilterItemBean itemBean = new PlayMatesListFilterItemBean();
                    itemBean.isSelected = false;
                    itemBean.itemId = mListBean.conditions.level.get(i).gamelevel;
                    if ("all".equals(itemBean.itemId)) {
                        itemBean.isSelected = true;
                        itemBean.itemName = getString(R.string.encounter_filter_all);
                    } else {
                        itemBean.itemName = mListBean.conditions.level.get(i).levelName;
                    }
                    mLevels.add(itemBean);
                }
            }

            mFilterBean.genders = mGenders;
            mFilterBean.prices = mPrices;
            mFilterBean.levels = mLevels;
        }
    }

    public void showFilterWindow(View view) {
        if (mFilterBean != null && mFilterBean.genders != null) {
            mWindow.show(view, mFilterBean);
        }

    }

    //更新筛选条件
    public void updateFilter(int pageNo, String gender, String price, String level, long gameId) {
        mPageNo = pageNo;
        mGender = gender;
        mPrice = price;
        mLevel = level;
        mGameId = gameId;
    }

    /**
     * @param isNeedLocation 是否需要重新定位
     */
    public void requestData(boolean isNeedLocation) {

        if (isNeedLocation) {
            if (mLocationUtil == null) {
                mLocationUtil = new LocationUtil(getContext());
            }
            mLocationUtil.startListener(this, 0);
            return;
        }

        GeoData geo = LocationUtil.getCurrentGeo(getContext());
        int lat = 0;
        int lng = 0;
        if (geo != null && geo.getLat() != 0 && geo.getLng() != 0) {
            lat = geo.getLat();
            lng = geo.getLng();
        }
        GameChatHttpProtocol.getPlayMatesList(getContext(), mPageNo, mPageSize, lat, lng, mGender, mPrice, mLevel, mGameId, this);

    }

    @Override
    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {
        if (type == 0) { // 失败
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.location_service_unavalible);
            return;
        }
        GameChatHttpProtocol.getPlayMatesList(getContext(), mPageNo, mPageSize, lat, lng, mGender, mPrice, mLevel, mGameId, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        mPtrlGameList.onRefreshComplete();
        mListBean = GsonUtil.getInstance().getServerBean(result, PlayMatesListBean.class);
        if (mListBean != null && mListBean.users != null) {
            if (mPageNo == 1) {
                mUsersBeans.clear();
            }
            mUsersBeans.addAll(mListBean.users);
            mAdapter.updateData(mUsersBeans);
            initFilterData();
            if (mListBean.users.size() <= 0) {
                showFail();
            }
        } else {
            showFail();
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        mPtrlGameList.onRefreshComplete();
        showFail();
        ErrorCode.toastError(getContext(), e);
    }

    private void showFail() {
        showPullToReListViewLoadFailedView(mPtrlGameList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(true);
            }
        });
    }

    class GameListAdapter extends BaseAdapter {
        private ArrayList<PlayMatesListBean.UsersBean> usersBeans;

        public void updateData(ArrayList<PlayMatesListBean.UsersBean> usersBeans) {
            this.usersBeans = usersBeans;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (usersBeans != null) {
                return usersBeans.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (usersBeans != null) {
                return usersBeans.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fragment_game_list, null);
                holder = new ViewHolder();
                holder.iv_head_icon = (ImageView) convertView.findViewById(R.id.iv_head_icon);
                holder.iv_show_discount = (ImageView) convertView.findViewById(R.id.iv_show_discount);
                holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
                holder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
                holder.tv_level = (TextView) convertView.findViewById(R.id.tv_level);
                holder.tv_time_distance = (TextView) convertView.findViewById(R.id.tv_time_distance);
                holder.tv_order_times = (TextView) convertView.findViewById(R.id.tv_order_times);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PlayMatesListBean.UsersBean usersBean = usersBeans.get(position);
            GlideUtil.loadCircleImage(getContext(), usersBean.icon, holder.iv_head_icon);
            if (!TextUtils.isEmpty(usersBean.nickname)) {
                SpannableString spName = FaceManager.getInstance(getContext()).parseIconForString(getContext(), usersBean.nickname, 0, null);
                holder.tv_user_name.setText(spName);
            }
            holder.tv_sex.setText(usersBean.age + "");
            GradientDrawable grad = (GradientDrawable) holder.tv_sex.getBackground();
            if ("m".equals(usersBean.gender)) {

                holder.tv_sex.setCompoundDrawablesWithIntrinsicBounds(BaseApplication.appContext.getResources().getDrawable(R.drawable.tag_man), null, null, null);
                grad.setColor(BaseApplication.appContext.getResources().getColor(R.color.sex_man));
            } else {
                holder.tv_sex.setCompoundDrawablesWithIntrinsicBounds(BaseApplication.appContext.getResources().getDrawable(R.drawable.tag_girl), null, null, null);
                grad.setColor(BaseApplication.appContext.getResources().getColor(R.color.sex_girl));
            }
            if (!TextUtils.isEmpty(usersBean.levelname)) {
                GradientDrawable gradRank = (GradientDrawable) holder.tv_level.getBackground();
                gradRank.setColor(CommonFunction.getRankColor(usersBean.level));
                holder.tv_level.setVisibility(View.VISIBLE);
                holder.tv_level.setText(usersBean.levelname);
            } else {
                holder.tv_level.setVisibility(View.INVISIBLE);

            }
            if (usersBean.distance < 0) { // 不可知
                holder.tv_time_distance.setText(TimeFormat.timeFormat1(getContext(), usersBean.lastonlinetime) + " · ");
            } else {
                holder.tv_time_distance.setText(TimeFormat.timeFormat1(getContext(), usersBean.lastonlinetime) + " | " + CommonFunction.covertSelfDistance(usersBean.distance));
            }
            holder.tv_order_times.setText(String.format(BaseApplication.appContext.getString(R.string.order_times), usersBean.ordertimes + ""));
            if (usersBean.discount > 0) {
                holder.iv_show_discount.setVisibility(View.VISIBLE);
            } else {
                holder.iv_show_discount.setVisibility(View.GONE);

            }
            holder.tv_price.setText(usersBean.price + usersBean.unit);
            return convertView;
        }

        class ViewHolder {
            public ImageView iv_head_icon;
            public ImageView iv_show_discount;
            public TextView tv_user_name;
            public TextView tv_sex;
            public TextView tv_level;
            public TextView tv_time_distance;
            public TextView tv_order_times;
            public TextView tv_price;
        }

    }
}
