package net.iaround.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.PlayMatesListBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.adapter.FragmentStatePagerAdapter;
import net.iaround.ui.fragment.GameListItemFragment;
import net.iaround.ui.view.menu.FilterGameListPopupWindow;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.HashMap;

/**
 * 陪玩分类详情列表-Fragment容器
 */
public class GameListActivity extends BaseFragmentActivity implements HttpCallBack ,LocationUtil.MLocationListener{

    private ViewPager mVpGameList;
    private LinearLayout mLlBack;
    private LinearLayout mLlFilterMenu;//筛选弹窗
    private MagicIndicator mMagicIndicator;
    private ImageView mIvFilter;

    private Fragment[] titleFragments;
    private PlayMatesListBean mListBean;

    private GameListItemFragment fragment;
    private GameTitleAdapter mAdapter;

    private long mGameId;
    private LocationUtil mLocationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        //统计查看分类人数
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("uid", Common.getInstance().getUid());
        MobclickAgent.onEvent(BaseApplication.appContext,"watch_category_person_num",map);

        mVpGameList = (ViewPager) findViewById(R.id.vp_game_list);
        mMagicIndicator = (MagicIndicator) findViewById(R.id.tb_game_list_indicator);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mLlFilterMenu = (LinearLayout) findViewById(R.id.ll_filter_menu);
        mIvFilter = (ImageView) findViewById(R.id.iv_filter);
        mLlFilterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null && mAdapter.getCurrentFragment() != null) {
                    FilterGameListPopupWindow window = ((GameListItemFragment) mAdapter.getCurrentFragment()).getWindow();
                    if (!window.getPopupWindow().isShowing()) {
                        ((GameListItemFragment) mAdapter.getCurrentFragment()).showFilterWindow(mIvFilter);
                    }
                }
            }
        });
        mLlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGameId = getIntent().getLongExtra("GameId", 0);

        if (mLocationUtil == null) {
            mLocationUtil = new LocationUtil(this);
        }
        mLocationUtil.startListener(this, 0);

        showWaitDialog();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mAdapter != null && mAdapter.getCurrentFragment() != null
                    && ((GameListItemFragment) mAdapter.getCurrentFragment()).getWindow() != null
                    && ((GameListItemFragment) mAdapter.getCurrentFragment()).getWindow().getPopupWindow() != null
                    && ((GameListItemFragment) mAdapter.getCurrentFragment()).getWindow().getPopupWindow().isShowing()) {
                ((GameListItemFragment) mAdapter.getCurrentFragment()).getWindow().getPopupWindow().dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {
        CommonFunction.log("GameListActivity", "updateLocation() into, type=" + type + ", lat=" + lat + ", lng=" + lng + ", address=" + address);
        if (type == 0) { // 失败
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.location_service_unavalible);
            return;
        }
        GameChatHttpProtocol.getPlayMatesList(this, 1, 24, lat, lng, "all", "all", "all", mGameId, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        cancleWaitDialog();
        mListBean = GsonUtil.getInstance().getServerBean(result, PlayMatesListBean.class);
        if (mListBean != null && mListBean.gameslist != null && mListBean.gameslist.GameName != null) {
            String[] titles = new String[]{mListBean.gameslist.GameName};
            mVpGameList.setOffscreenPageLimit(titles.length);
            titleFragments = new Fragment[titles.length];
            mAdapter = new GameTitleAdapter(getSupportFragmentManager(),titles);
            mVpGameList.setAdapter(mAdapter);
            initMagicIndicator(titles);
        }
    }

    private void initMagicIndicator(final String[] versions) {
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(BaseApplication.appContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                if (versions == null) {
                    return 0;
                }
                return versions.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setPaintBold();
                clipPagerTitleView.setText(versions[index]);
                clipPagerTitleView.setTextSize(UIUtil.dip2px(context, 17));
                clipPagerTitleView.setTextColor(Color.parseColor("#333333"));
                clipPagerTitleView.setClipColor(Color.parseColor("#333333"));
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mVpGameList.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mVpGameList);
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        cancleWaitDialog();
        ErrorCode.toastError(mContext, e);
    }


    private class GameTitleAdapter extends FragmentStatePagerAdapter {

        private String[] versions;

        public GameTitleAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.versions = titles;
        }

        @Override
        public Fragment getItem(int position) {
            if (titleFragments[position] == null) {
                fragment = new GameListItemFragment();
                Bundle bundle;
                if (position == 0) {
                    bundle = new Bundle();
                    bundle.putSerializable("GameList", mListBean);
                    fragment.setArguments(bundle);
                }
                titleFragments[position] = fragment;
                CommonFunction.log("getFragmentForPage", "  first  titleFragments " + position + "  个" + titleFragments.length);
            } else {
                CommonFunction.log("getFragmentForPage", "   second titleFragments " + position + "  个" + titleFragments.length);
                return titleFragments[position];
            }

            CommonFunction.log("getFragmentForPage", "    我是第 " + position + "  个");
            return fragment;
        }

        @Override
        public int getCount() {
            if (versions == null) {
                return 0;
            }
            return versions.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return versions[position] ;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
