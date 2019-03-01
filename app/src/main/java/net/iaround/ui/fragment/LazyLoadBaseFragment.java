package net.iaround.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import net.iaround.R;
import net.iaround.connector.ConnectorManage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;

/**
 * Fragment预加载问题的解决方案：
 * 1.可以懒加载的Fragment
 * 2.是否显示加载框loadingDialog
 * 3.切换到其他页面时停止加载数据（可选）
 */

public abstract class LazyLoadBaseFragment extends Fragment {
    private final String TAG = "LazyLoadBaseFragment--";
    private final String CLASS_NAME = getClass().getSimpleName();
    /**
     * 视图是否已经初初始化
     */
    private boolean mIsInit = false;
    private boolean mIsLoad = false;//是否正在加载数据
    private boolean mIsAuto = true;//每次进入fragment界面时，是否被动触发加载数据
//    private boolean mIsShowDefaultDialog = false;//是否显示加载框，默认不显示
    private View mContentView;
    private FrameLayout mRootView;
    private RelativeLayout mRlDefaultProgressDialog;
    private FrameLayout mFlContentLayout;
    private LinearLayout mLlLoadFailed;//对子类开放，可以对布局内容根据相应情况修改
    private View mEmptyView;//
    private Dialog mWaitDialog = null;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CommonFunction.log(TAG + CLASS_NAME, "onCreateView");
        mIsInit = true;
        mRootView = (FrameLayout) inflater.inflate(R.layout.lazyload_base_layout, container, false);
//        mRlDefaultProgressDialog = (RelativeLayout) mRootView.findViewById(R.id.rl_default_progress_dialog);
        mLlLoadFailed = (LinearLayout) mRootView.findViewById(R.id.ll_load_failed);
        mFlContentLayout = (FrameLayout) mRootView.findViewById(R.id.fl_content_layout);
        mFlContentLayout.removeAllViews();
        mContentView = inflater.inflate(setContentView(), mFlContentLayout, true);
        mEmptyView = inflater.inflate(R.layout.no_data_view,null);
        init(inflater, container, savedInstanceState);
        /**初始化的时候去加载数据**/
        isCanLoadData();
        return mRootView;
    }

    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        CommonFunction.log(TAG + CLASS_NAME, "setUserVisibleHint");
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     * 3.是否被动触发
     */
    private void isCanLoadData() {
        if (!mIsInit) {
            return;
        }

        if (getUserVisibleHint() && mIsAuto) {
            mIsAuto = lazyLoad();
            CommonFunction.log(TAG + CLASS_NAME, "lazyLoad() mIsAuto：" + mIsAuto + "  mIsLoad: " + mIsLoad);
            //根据mIsLoad判断是否需要进行网络监测，有些界面不需要网络请求，只需加载一次即可
            if (!mIsLoad && !ConnectorManage.getInstance(getContext()).CheckNetwork(getContext())) {
                mIsAuto = true;
                return;
            }
            mIsLoad = true;
        } else {
            if (mIsLoad) {
                stopLoad();
            }
        }
    }

//    /**
//     * 加载完数据后一定要调用,隐藏加载框，显示内容
//     */
//    protected void hideDefaultProgressDialog() {
//        CommonFunction.log(TAG + CLASS_NAME, "hideDefaultProgressDialog");
//        if (mIsInit) {
//            mFlContentLayout.setVisibility(View.VISIBLE);
//            mRlDefaultProgressDialog.setVisibility(View.GONE);
//            mLlLoadFailed.setVisibility(View.GONE);
//        }
//    }
//
//    /**
//     * 显示加载框，隐藏内容布局
//     */
//    protected void showDefaultProgressDialog() {
//        CommonFunction.log(TAG + CLASS_NAME, "showDefaultProgressDialog");
//        if (mIsInit) {
//            mFlContentLayout.setVisibility(View.GONE);
//            mRlDefaultProgressDialog.setVisibility(View.VISIBLE);
//        }
//    }

    /**
     * 初始化PullToRefreshListView加载失败布局
     * @param ptrlv
     * @param listener
     */
    protected void showPullToReListViewLoadFailedView(PullToRefreshListView ptrlv, View.OnClickListener listener) {
        CommonFunction.log(TAG + CLASS_NAME, "showPullToReListViewLoadFailedView");
        if (mIsInit) {
            if (ptrlv != null) {
                ptrlv.setEmptyView(mEmptyView);
                if (listener != null) {
                    mEmptyView.setOnClickListener(listener);
                }
            }
        }
    }

    /**
     * 显示加载失败布局，非listView界面
     * @param listener
     */
    protected void showLoadFailedView(View.OnClickListener listener) {
        CommonFunction.log(TAG + CLASS_NAME, "showLoadingFailView");
        if (mIsInit) {
//            mFlContentLayout.setVisibility(View.GONE);
//            mRlDefaultProgressDialog.setVisibility(View.GONE);
            mLlLoadFailed.setVisibility(View.VISIBLE);
            if (listener != null) {
                mLlLoadFailed.setOnClickListener(listener);
            }
        }
    }

    /**
     * 根据mIsLoad判断是否需要进行网络监测，有些界面不需要网络请求，只需加载一次即可；在{@link #lazyLoad()}中调用，
     * 或者在调用该方法之前调用，方可生效
     * @param isLoad 默认为false; 为true时{@link #lazyLoad()}不管是否网络是否正常，只需执行一次
     */
    protected void setIsLoadOnce(boolean isLoad) {
        mIsLoad = isLoad;
    }

    /**
     * 重置是否自动加载数据状态
     * @param isAuto true为每次进入fragment自动加载数据
     */
    protected void setIsAuto(boolean isAuto) {
        mIsAuto = isAuto;
    }

    /**
     * 视图销毁的时候Fragment的状态重置
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonFunction.log(TAG + CLASS_NAME, "onDestroyView");
        mIsInit = false;
        mIsLoad = false;
        mIsAuto = true;

    }

    /**
     * 设置Fragment要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int setContentView();

    /**
     * 获取设置的布局
     *
     * @return
     */
    protected View getContentView() {
        return mContentView;
    }

    /**
     * 获取加载失败空白布局，可以对布局内容根据相应情况修改，该布局ListView数据加载失败时显示
     * @return
     */
    protected View getEmptyView() {
        return mEmptyView;
    }

    /**
     * 获取加载失败空白布局，可以对布局内容根据相应情况修改
     * @return
     */
    protected LinearLayout getLlLoadFailed() {
        return mLlLoadFailed;
    }

    /**
     * 找出对应的控件
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findView(int id) {

        return (T) getContentView().findViewById(id);
    }

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     *
     * @return 非首次进入fragment界面时，是否被动触发加载数据 true:自动加载；false:不加载
     */
    protected abstract boolean lazyLoad();

    /**
     * 初始化控件
     */
    protected abstract void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以调用此方法；
     * 因为是中途停止加载数据，所以需要考虑到是否要再方法最后把自动加载的状态重置为 mIsAuto = true，
     * 已便再次进入fragment可以重新加载数据
     */
    protected void stopLoad() {
    }

    protected void showWaitDialog() {
        if (mWaitDialog == null) {
            CommonFunction.log(TAG + CLASS_NAME, "showWaitDialog");
            mWaitDialog = DialogUtil.getProgressDialog(getActivity(), getString(R.string.dialog_title),
                    getString(R.string.please_wait), null);
        }
        mWaitDialog.show();
    }

    protected void destroyWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }

    protected void hideWaitDialog() {
        if (mWaitDialog != null) {
            if (mWaitDialog.isShowing()) {
                mWaitDialog.hide();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(CLASS_NAME); //统计页面("CLASS_NAME"为页面名称，可自定义)
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(CLASS_NAME);
    }

}
