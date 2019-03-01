package net.lucode.hackware.magicindicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.util.Log;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

/**
 * 带颜色渐变和缩放的指示器标题
 * 博客: http://hackware.lucode.net
 */
public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {
    public Context mContext;
    private float mMinScale = 0.8f;//0.62.f

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
//        setPivotX(getTextWidth()/2 - (1.0f - mMinScale) * enterPercent*getTextWidth()/2);
//        setPivotY(getContentBottom());
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
//        setPivotX(getTextWidth()/2 + (1.0f - mMinScale) * leavePercent*getTextWidth()/2);
//        setPivotY(getContentBottom());
    }

    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }
}
