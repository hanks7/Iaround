package net.iaround.ui.skill.skillpropshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.manager.mvpbase.MvpBaseFragment;
import net.iaround.model.skill.PropItemBean;
import net.iaround.model.skill.PropsShopBean;
import net.iaround.model.skill.PropsShopBuySuccess;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.MoonGridItemDecoration;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.skill.FullyGridLayoutManager;
import net.iaround.ui.skill.RecycleItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：zx on 2017/8/18 11:59
 */
public class SkillPropsShopFragment extends MvpBaseFragment<SkillPropsShopContract.Presenter> implements SkillPropsShopContract.View, RecycleItemClickListener<PropItemBean> {

    @BindView(R.id.moon_recyclerView)
    RecyclerView moon_recyclerView;
    @BindView(R.id.tv_mime_diamond)
    TextView tv_mime_diamond;
    @BindView(R.id.tv_mime_gold)
    TextView tv_mime_gold;
    @BindView(R.id.tv_mime_star)
    TextView tv_mime_star;
    @BindView(R.id.tv_title)
    TextView tv_title;

    private SkillPropsShopAdapter adapter;
    private PropItemBean selectedPropItem;
    public int currentDiamondNum = 0;
    public int currentGoldNum = 0;
    public int currentStarNum = 0;
    private List<PropItemBean> dataList;
    private PropsShopBuySuccess result;


    public static SkillPropsShopFragment getInstance() {
        return new SkillPropsShopFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_moon_charge;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        init();
        mPresenter.getPropsShopData();
    }

    @Override
    public void onResume() {
        super.onResume();
        //得到Fragment的根布局并使该布局可以获得焦点
        getView().setFocusableInTouchMode(true);
        //得到Fragment的根布局并且使其获得焦点
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    EventBus.getDefault().post(result);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 更新页面UI
     *
     * @param propsShopBean
     */
    @Override
    public void updatePropsShopView(PropsShopBean propsShopBean) {
        if (null != propsShopBean) {
            if (null != propsShopBean.user) {
                currentDiamondNum = propsShopBean.user.DiamondNum;
                tv_mime_diamond.setText(currentDiamondNum + "");
                currentGoldNum = propsShopBean.user.GoldNum;
                tv_mime_gold.setText(currentGoldNum + "");
                currentStarNum = propsShopBean.user.StarNum;
                tv_mime_star.setText(currentStarNum + "");
            }
            selectedPropItem = propsShopBean.list.get(0);
            dataList = propsShopBean.list;
            adapter.updateDatas(dataList);
        }
    }

    /**
     * 购买道具结果
     *
     * @param shopBuySuccess
     */
    @Override
    public void propsBuyResult(PropsShopBuySuccess shopBuySuccess) {
        if (null == shopBuySuccess || null == shopBuySuccess.list || shopBuySuccess.list.size() <= 0) {
            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.buy_failure));
            return;
        }
        result = shopBuySuccess;
        currentDiamondNum = shopBuySuccess.user.DiamondNum;
        tv_mime_diamond.setText(currentDiamondNum + "");

        currentGoldNum = shopBuySuccess.user.GoldNum;
        tv_mime_gold.setText(currentGoldNum + "");

        currentStarNum = shopBuySuccess.user.StarNum;
        tv_mime_star.setText(currentStarNum + "");
        CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.buy_success));
    }


    /**
     * 点击事件
     */
    @OnClick({R.id.btn_charge, R.id.fl_back})
    public void onViewClicked(View view) {
        if (isActive()) {
            switch (view.getId()) {
                case R.id.fl_back:
                    EventBus.getDefault().post(result);
                    mActivity.finish();
                    break;
                case R.id.btn_charge:
                    if (null == selectedPropItem) {
                        return;
                    }
                    if (1 == selectedPropItem.CurrencyType) { //金币
                        if (currentGoldNum < selectedPropItem.CurrencyNum) {
                            showDialog(1);
                            return;
                        }
                    } else if (2 == selectedPropItem.CurrencyType) {//钻石
                        if (currentDiamondNum < selectedPropItem.CurrencyNum) {
                            showDialog(2);
                            return;
                        }
                    } else if (6 == selectedPropItem.CurrencyType) {//星星
                        if (currentStarNum < selectedPropItem.CurrencyNum) {
                            showDialog(6);
                            return;
                        }
                    }
                    showBuyDialog();
                    break;

            }
        }

    }


    private void showBuyDialog() {
        DialogUtil.showTowButtonDialog(mActivity, mActivity.getResources().getString(R.string.prompt),
                getString(R.string.sure_pay_props),
                mActivity.getResources().getString(R.string.edit_cancle),
                getString(R.string.pay),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.propsBuy(selectedPropItem.PropsShopID);
                    }
                });
    }

    /*提示余额不足
     * */
    private void showDialog(int type) {
        if (1 == type) {
            DialogUtil.showTowButtonDialog(mActivity, mActivity.getResources().getString(R.string.prompt),
                    mActivity.getResources().getString(R.string.chatbar_gift_gold_not_enough),
                    mActivity.getResources().getString(R.string.edit_cancle),
                    mActivity.getResources().getString(R.string.chatbar_gift_get_gold),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = Config.getGoldDescUrlNew(CommonFunction.getLang(mActivity));
                            Intent i = new Intent(mActivity, WebViewAvtivity.class);
                            i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
                            mActivity.startActivity(i);
                        }
                    });
        } else if (2 == type) {
            DialogUtil.showTowButtonDialog(mActivity, mActivity.getResources().getString(R.string.prompt),
                    mActivity.getResources().getString(R.string.chatbar_gift_diamond_not_enough),
                    mActivity.getResources().getString(R.string.edit_cancle),
                    mActivity.getResources().getString(R.string.chatbar_gift_get_diamond),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, MyWalletActivity.class);
                            mActivity.startActivity(intent);
                        }
                    });
        } else if (6 == type) {
            DialogUtil.showTowButtonDialog(mActivity, mActivity.getResources().getString(R.string.prompt),
                    mActivity.getResources().getString(R.string.chatbar_gift_star_not_enough),
                    mActivity.getResources().getString(R.string.edit_cancle),
                    mActivity.getResources().getString(R.string.chatbar_gift_get_diamond),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, MyWalletActivity.class);
                            mActivity.startActivity(intent);
                        }
                    });
        }
    }

    @Override
    public void onItemClick(int position, PropItemBean propItemBean) {
        if (null != propItemBean) {
            selectedPropItem = propItemBean;
        }
    }

    private void init() {
        if (null == result) {
            result = new PropsShopBuySuccess();
        }
        tv_title.setText(R.string.get_moon);
        if (null == dataList) {
            dataList = new ArrayList<>();
        }
        adapter = new SkillPropsShopAdapter(this);
        moon_recyclerView.setHasFixedSize(true);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(BaseApplication.appContext, 2);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        manager.canScrollVertically();
        MoonGridItemDecoration itemDecoration = new MoonGridItemDecoration(BaseApplication.appContext);
        moon_recyclerView.addItemDecoration(itemDecoration);
        moon_recyclerView.setLayoutManager(manager);
        moon_recyclerView.setAdapter(adapter);
    }

    @Override
    public void setPresenter(SkillPropsShopContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        showWaitDialog();
    }

    @Override
    public void hideLoading() {
        hideWaitDialog();
    }

    @Override
    public boolean isActive() {
        return isLive();
    }
}