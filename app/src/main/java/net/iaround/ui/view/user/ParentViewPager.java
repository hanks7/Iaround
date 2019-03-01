package net.iaround.ui.view.user;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author：liush on 2017/1/20 14:19
 */
public class ParentViewPager extends ViewPager {
    public ParentViewPager(Context context) {
        super(context);
    }

    public ParentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
