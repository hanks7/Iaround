package net.iaround.manager.mvpbase;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iaround.ui.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：zx on 2017/8/9 11:37
 */
public abstract class MvpBaseFragment<P extends BasePresenter> extends BaseFragment{

    protected MvpBaseActivity mActivity;

    protected P mPresenter;

    protected boolean mActive;

    protected Unbinder bind;

    //获取fragment布局文件ID
    protected abstract int getLayoutId();

    //初始化view
    protected abstract void initView(View view, Bundle savedInstanceState);


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MvpBaseActivity){
            this.mActivity = (MvpBaseActivity) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != mPresenter){
            mPresenter.onAttach();
        }
        mActive = true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActive = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        bind = ButterKnife.bind(this, view);
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActive = false;
        destroyWaitDialog();
        if (null != mPresenter){
            mPresenter.onDettach();
            mPresenter = null;
        }
        bind.unbind();
    }

    /**
     * 判断当前view是否存活
     * @return
     */
    protected boolean isLive(){
        return mActive && isAdded();
    }
}
