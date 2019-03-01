package net.iaround.ui.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.im.Me;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.CreateChatBarActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.group.bean.CreateGroupConditions;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ScaleTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * 聊吧入口
 */
public class ChatBarFragment extends LazyLoadBaseFragment implements View.OnClickListener, HttpCallBack, MainFragmentActivity.PagerSelectChat {

    private Context mContext;

    private ViewPager mViewPager;
    private MagicIndicator mMagicIndicator;
    private Button mBtnCreateChatBar;

    private Dialog mProgressDialog;

    private String[] titles = null;

    /**
     * 热门聊吧
     */
    private ChatBarPopularFragment chatBarPopularFragment;
    /**
     * 家族聊吧
     */
    private ChatBarFamilyFragment chatBarFamilyFragment;

    /**
     * 排行榜
     */
    private RankingFragment rankingFragment;

    /**
     * 点击创建圈子按钮请求码
     */
    private static final int REQUEST_CREATE_CHATBAR = 10001;
    private long getCreateChatbarFlag = 0;

    /**
     * 创建圈子条件
     */
    private CreateGroupConditions conditions;
    /**
     * 剩余创建圈子数
     */
    private int chatbarNum;
    /**
     * 钻石数量
     */
    private int diamondnum;
    /**
     * 金币数量
     */
    private int gold;

    /**
     * 获取当前登录用户信息
     */
    private long getUserInfoFlag;

    private Me me = Common.getInstance().loginUser;

    public ChatBarFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_chat_bar;
    }

    @Override
    protected boolean lazyLoad() {
        setIsLoadOnce(true);
        initData();
        initListener();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(getContentView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonFunction.log("ChatBarFragment", "onDestroy() into");
    }

    private void initView(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.chat_bar_viewPager);
        mBtnCreateChatBar = (Button) view.findViewById(R.id.btn_create_chat_bar);
        mMagicIndicator = (MagicIndicator) view.findViewById(R.id.chat_bar_xTablayout);
        mMagicIndicator.setBackgroundColor(Color.WHITE);
    }

    private void initListener() {
        mBtnCreateChatBar.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mBtnCreateChatBar.setVisibility(View.GONE);
                    //统计
                    Statistics.onPageClick(Statistics.PAGE_CHAT_HOT);
                } else if(position == 1){
                    mBtnCreateChatBar.setVisibility(View.VISIBLE);
                    //统计
                    Statistics.onPageClick(Statistics.PAGE_CHAT_MINE);
                } else if(position == 2){//排行榜
                    mBtnCreateChatBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        titles = new String[]{
                mContext.getResources().getString(R.string.chat_bar_hot),
                mContext.getResources().getString(R.string.chat_bar_family),
                mContext.getResources().getString(R.string.rank_tab_text)
        };

        ChatBarAdapter chatBarAdapter = new ChatBarAdapter(getChildFragmentManager());
        mViewPager.setAdapter(chatBarAdapter);
        mViewPager.setOffscreenPageLimit(3);

        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles == null ? 0 : titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(24);
                simplePagerTitleView.setNormalColor(Color.parseColor("#333333"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#FF4064"));
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });

        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_chat_bar:
                showProgressDialog();
                getCreateChatbarFlag = GroupHttpProtocol.getCreateGroupCondition_5_3(mContext, this);
                if (getCreateChatbarFlag == -1) {
                    hideProgressDialog();
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_CHATBAR && resultCode == Activity.RESULT_OK) {
            hideProgressDialog();
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideProgressDialog();
        if (result != null && flag == getCreateChatbarFlag) {
            conditions = GsonUtil.getInstance().getServerBean(result, CreateGroupConditions.class);
            if (conditions != null) {
                chatbarNum = conditions.groupnum;
                diamondnum = conditions.diamondnum;
                gold = conditions.gold;
            }
            /**
             * 判断当前剩余圈子数是否小于2 如果小于2不能创建聊吧
             * 判断当前用户等级是否大于8级
             */
            if (chatbarNum == 0) {
                Toast.makeText(mContext, getResources().getString(R.string.create_group_quantity_limit_msg), Toast.LENGTH_SHORT).show();
                return;
            } else if (conditions.level < 8) {
                String note = mContext.getResources().getString(R.string.create_chatbar_conditions2);
                net.iaround.tools.DialogUtil.showOKCancelDialog(mContext,
                        "", note, R.string.create_chatbar_looklevel,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO: 2017/6/15 等级不足，跳转到h5升级
                                String url = Config.getlevelDescUrl(CommonFunction.getLang(mContext));
                                Intent i = new Intent(mContext, WebViewAvtivity.class);
                                i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
                                startActivity(i);
                            }
                        });
                return;
            } else {
                Intent intent = new Intent(mContext, CreateChatBarActivity.class);
                intent.putExtra("diamondnum", diamondnum);
                intent.putExtra("gold", gold);
                startActivityForResult(intent, REQUEST_CREATE_CHATBAR);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(mContext, "",
                    mContext.getResources().getString(R.string.please_wait), null);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private class ChatBarAdapter extends FragmentPagerAdapter {

        public ChatBarAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (chatBarPopularFragment == null) {
                    return chatBarPopularFragment = new ChatBarPopularFragment();
                }
                return chatBarPopularFragment;
            } else if (position == 1) {
                if (chatBarFamilyFragment == null) {
                    return chatBarFamilyFragment = new ChatBarFamilyFragment();
                }
                return chatBarFamilyFragment;
            } else if(position == 2){
                if(rankingFragment == null){
                    return rankingFragment = new RankingFragment();
                }
                return rankingFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public void onChatSelected() {
        if(null== mViewPager) return;
        if (mViewPager.getCurrentItem() == 0) {
            //统计
            Statistics.onPageClick(Statistics.PAGE_CHAT_HOT);
        } else if (mViewPager.getCurrentItem() == 1){
            //统计
            Statistics.onPageClick(Statistics.PAGE_CHAT_MINE);
        }
    }
}
