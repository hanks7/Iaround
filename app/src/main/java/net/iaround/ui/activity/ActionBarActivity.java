package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PermissionUtils;

/**
 * Class:
 * Author：gh
 * Date: 2016/12/7 14:04
 * Emial：jt_gaohang@163.com
 */
public abstract class ActionBarActivity extends BaseActivity {

    private ImageView rightGoImage, leftBackImage;
    private TextView righterTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        bindActionBarEvent();
    }

    /**
     * 设置actionbar标题
     */
    protected void setActionBarTitle(String title) {
        TextView actionBar = (TextView) findViewById(R.id.tv_title);
        if (actionBar != null) {
            actionBar.setText(title);
        }
    }

    /**
     * 设置actionbar标题
     */
    protected void setActionBarTitle(int resId) {
        TextView actionBar = (TextView) findViewById(R.id.tv_title);
        if (actionBar != null) {
            actionBar.setText(resId);
        }
    }

    /**
     * 设置actionbar返回图片的显示
     */
    protected void hideBackActionBar() {
        View backView = findViewById(R.id.iv_back);
        if (backView != null) {
            backView.setVisibility(View.GONE);
        }
    }

    protected void showBackActionBar() {
        View backView = findViewById(R.id.iv_back);
        if (backView != null) {
            backView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置actionbar返回图片的点击事件
     */
    private void bindActionBarEvent() {
        View backView = findViewById(R.id.fl_back);
        if (backView != null) {
            backView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    CommonFunction.hideInputMethod(mContext, arg0);
                    boolean isBack = actionBarBackPressed();
                    if (isBack) {
                        onBackPressed();
                    }
                }
            });
        }
        View goView = findViewById(R.id.fl_go);
        if (goView != null) {
            goView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    CommonFunction.hideInputMethod(mContext, arg0);
                    actionBarRightGoPressed();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            boolean isBack = actionBarBackPressed();
            if (isBack) {
                onBackPressed();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 设置actionbar返回图片部分的替换图片事件
     */
    protected void leftBackActionBarEvent(Drawable background) {
        leftBackImage = (ImageView) findViewById(R.id.iv_back);
        if (leftBackImage != null) {
            leftBackImage.setImageDrawable(background);
            leftBackImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    actionBarBackPressed();
                }
            });
        }
    }

    /**
     * 隐藏右边边文字的方法
     */
    protected void hideRightActionBar() {
        rightGoImage = (ImageView) findViewById(R.id.iv_back);
        if (rightGoImage != null) {
            rightGoImage.setVisibility(View.GONE);
        }
    }

    /**
     * 表情界面隐藏，显示键盘
     */
    public void hideFaceShowKeyboard() {

    }

    protected void rightActionBarEvent(int pic) {
        FrameLayout rightGoTv = (FrameLayout) findViewById(R.id.fl_go);
        TextView tvleftBack = (TextView) findViewById(R.id.tv_go);
        ImageView ivleftBack = (ImageView) findViewById(R.id.iv_go);
        if (rightGoTv != null) {
            if (pic != 0) {
                ivleftBack.setVisibility(View.VISIBLE);
                tvleftBack.setVisibility(View.GONE);
                ivleftBack.setImageResource(pic);
            }
            rightGoTv.setVisibility(View.VISIBLE);
        }
    }

    protected TextView rightActionBarEvent(String str) {
        FrameLayout rightGoTv = (FrameLayout) findViewById(R.id.fl_go);
        TextView tvRightGo = (TextView) findViewById(R.id.tv_go);
        ImageView ivRightGo = (ImageView) findViewById(R.id.iv_go);
        if (rightGoTv != null) {
            if (!TextUtils.isEmpty(str)) {
                ivRightGo.setVisibility(View.GONE);
                tvRightGo.setVisibility(View.VISIBLE);
                tvRightGo.setText(str);
            }
            rightGoTv.setVisibility(View.VISIBLE);
        }
        return tvRightGo;
    }

    protected void hideRightTitleActionBar() {
        FrameLayout rightGo = (FrameLayout) findViewById(R.id.fl_go);
        if (rightGo != null) {
            rightGo.setVisibility(View.GONE);
        }
    }

    protected void rightTitleActionBarEvent(String title) {
        righterTitleTv = (TextView) findViewById(R.id.tv_go);
        if (righterTitleTv != null) {
            righterTitleTv.setText(title);
            righterTitleTv.setVisibility(View.VISIBLE);
        }
    }

    protected void rightTitleActionBarEvent(int id) {
        righterTitleTv = (TextView) findViewById(R.id.tv_go);
        if (righterTitleTv != null) {
            righterTitleTv.setText(getString(id));
            righterTitleTv.setVisibility(View.VISIBLE);
        }
    }

    protected void rightTitleActionBarEvent(int id, String titleText) {
        ImageView righterTitleLv = (ImageView) findViewById(R.id.iv_go);
        righterTitleLv.setImageResource(id);
        righterTitleTv = (TextView) findViewById(R.id.tv_go);
        if (titleText != null) {
            righterTitleTv.setText(getString(id));
            righterTitleTv.setVisibility(View.VISIBLE);
        }
    }

    protected void leftActionBarEvent(String text) {
        FrameLayout leftGoTv = (FrameLayout) findViewById(R.id.fl_back);
        TextView tvleftBack = (TextView) findViewById(R.id.tv_back);
        ImageView ivleftBack = (ImageView) findViewById(R.id.iv_back);
        if (leftGoTv != null) {
            if (!TextUtils.isEmpty(text)) {
                ivleftBack.setVisibility(View.GONE);
                tvleftBack.setVisibility(View.VISIBLE);
                tvleftBack.setText(text);
            }
            leftGoTv.setVisibility(View.VISIBLE);
        }
    }


    protected void leftActionBarEvent(int id) {
        FrameLayout leftGoTv = (FrameLayout) findViewById(R.id.fl_back);
        TextView tvleftBack = (TextView) findViewById(R.id.tv_back);
        ImageView ivleftBack = (ImageView) findViewById(R.id.iv_back);
        if (leftGoTv != null) {
            if (id != 0) {
                ivleftBack.setVisibility(View.VISIBLE);
                tvleftBack.setVisibility(View.GONE);
                ivleftBack.setImageResource(id);
            }
            leftGoTv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 右侧按钮点击事件
     */
    protected void actionBarRightGoPressed() {

    }

    /**
     * 返回false 拦截退出
     *
     * @return
     */
    protected boolean actionBarBackPressed() {

        return true;
    }

    /**
     * 获取控件对象的泛型方法
     *
     * @param resId
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getView(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * 把软键盘隐藏
     */
    public boolean hiddenKeyBoard(Activity mActivity) {
        // 点击屏幕任何地方则把软键盘隐藏
        if (mActivity.getCurrentFocus() != null) {
            ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    /**
     * 获取摄像头权限
     */
    public void requestCamera() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
    }

    /**
     * 获取摄像头权限后执行操作
     */
    public void doCamera() {
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA://获取摄像头权限
                    doCamera();
                    break;
            }
        }
    };

}
