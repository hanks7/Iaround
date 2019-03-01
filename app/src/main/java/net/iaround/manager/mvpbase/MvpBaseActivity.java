package net.iaround.manager.mvpbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import net.iaround.ui.activity.BaseActivity;

import java.util.List;

/**
 * 作者：zx on 2017/8/9 11:37
 */
public abstract class MvpBaseActivity extends BaseActivity{

    //获取activity的layoutid
    protected abstract int getLayoutId();

    //布局中添加Fragment的ID
    protected abstract int getFragmentContentId();

    //获取当前fragment
    protected abstract MvpBaseFragment getCurrentFragment();

    //初始化Presenter的实例
    protected abstract void initPresenterInstance();

    //获取Intent
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //避免重复添加Fragment
        if (null == fragments || (null != fragments && fragments.size() == 0)) {
            MvpBaseFragment currentFragment = getCurrentFragment();
            if (null != currentFragment) {
                addFragment(currentFragment);
            }
        }
        initPresenterInstance();
    }



    //添加fragment
    protected void addFragment(MvpBaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    protected void removeFragment() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
