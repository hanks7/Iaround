package net.iaround.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 作者：zx on 2017/8/17 10:54
 */
public class WrapperScrollView extends ScrollView{
    public WrapperScrollView(Context context) {
        super(context);
    }

    public WrapperScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapperScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 处理scrollView中嵌套recycleView, listView加载完数据滑动到底部问题
     * @param rect
     * @return
     */
    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
