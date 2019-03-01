package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;

/**
 * @author：liush on 2016/12/3 16:19
 */
public abstract class TitleActivity extends BaseActivity {
    /**
     * title bar的左右控件
     */
    private FrameLayout flLeft, flBack;
    private LinearLayout flRight;
    private ImageView ivLeft, ivRight;
    private TextView tvLeft, tvRight;
    /**
     * titlebar中间的标题名
     */
    private TextView tvTitle;
    /**
     * 这个是存继承TitleActivity的activity的布局,就形成同一个title bar下的不同布局
     */
    private FrameLayout flContent;

    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        findViews();
    }

    private void findViews() {
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
//        flRight = (FrameLayout) findViewById(R.id.fl_right);
        flBack = (FrameLayout) findViewById(R.id.fl_back);
        flRight = (LinearLayout) findViewById(R.id.fl_right);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvRight = (TextView) findViewById(R.id.tv_right);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置内容
     *
     * @param layout
     */
    public void setContent(int layout) {
        flContent.removeAllViews();
        mLayoutInflater.inflate(layout, flContent);
    }


    /**
     * 只是设置标题文本
     *
     * @param title
     */
    public void setTitle_C(String title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    /**
     * 隐藏标题
     */
    public void setHideTitle() {
        tvTitle.setVisibility(View.GONE);
    }

    /**
     * 只是设置标题文本，参数必须是id，不能是数字
     *
     * @param title
     */
    public void setTitle_C(int title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    /**
     * 只设置标题左边view
     *
     * @param picId
     * @param desc
     */
    public void setTitle_L(int picId, String desc, View.OnClickListener listener) {

        if (picId != 0) {
            ivLeft.setVisibility(View.VISIBLE);
            tvLeft.setVisibility(View.GONE);
            ivLeft.setImageResource(picId);
        } else if (desc != null) {
            ivLeft.setVisibility(View.GONE);
            tvLeft.setVisibility(View.VISIBLE);
            tvLeft.setText(desc);
        } else {
            return;
        }
        if (listener != null) {
            flLeft.setOnClickListener(listener);
        }
    }

    /**
     * 只设置标题右边view
     *
     * @param picId
     * @param desc
     */
    public void setTitle_R(int picId, String desc, View.OnClickListener listener) {

        if (picId != 0) {
            ivRight.setVisibility(View.VISIBLE);
            tvRight.setVisibility(View.GONE);
            ivRight.setImageResource(picId);
        } else if (desc != null) {
            ivRight.setVisibility(View.GONE);
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(desc);
        } else {
            return;
        }
        if (listener != null) {
            flRight.setOnClickListener(listener);
        }
    }


    /**
     * 对标题栏进行初始化
     *
     * @param leftHidden    是否隐藏标题栏左边的view, true为隐藏，false为不隐藏
     * @param leftPicId     标题栏左边的view图片的资源id，0为使用默认图片
     * @param leftDesc      标题栏左边的TextViw的内容， null为使用默认的文本
     * @param leftListener  给标题左边view设置点击事件
     * @param title         中间标题显示的内容
     * @param rightHidden
     * @param rightPicId
     * @param rightDesc
     * @param rightListener
     */
    public void setTitle_LCR(boolean leftHidden, int leftPicId, String leftDesc, View.OnClickListener leftListener, String title, boolean rightHidden, int rightPicId, String rightDesc, View.OnClickListener rightListener) {
        if (leftHidden) {
            hiddenLeft();
        } else {
            setTitle_L(leftPicId, leftDesc, leftListener);
        }
        setTitle_C(title);
        if (rightHidden) {
            hiddenRight();
        } else {
            setTitle_R(rightPicId, rightDesc, rightListener);
        }
    }

    private void hiddenRight() {
        ivRight.setVisibility(View.GONE);
        tvRight.setVisibility(View.GONE);
    }

    private void hiddenLeft() {
        ivLeft.setVisibility(View.GONE);
        tvLeft.setVisibility(View.GONE);
    }

    /**
     * 表情界面隐藏，显示键盘
     */
    public void hideFaceShowKeyboard() {

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
     * 获取右边TextView控件
     *
     * @return
     */
    public TextView getTvRight() {
        return tvRight;
    }

    /**
     * 获取右边ImageView控件
     *
     * @return
     */
    public ImageView getIvRight() {
        return ivRight;
    }

    /**
     * 获取右边布局控件
     * @return
     */
    public LinearLayout getFlRight(){
        return flRight;
    }
}