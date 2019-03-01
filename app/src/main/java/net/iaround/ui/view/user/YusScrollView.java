package net.iaround.ui.view.user;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 作者：史蒂芬
 * 日期: 2016/9/1
 * 我的GitHub：https://github.com/gnehsuy
 */
public class YusScrollView extends ScrollView {
    public YusScrollView(Context context) {
        super(context);
    }

    public YusScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YusScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public interface ScrollViewListener {
        void onScrollChanged(YusScrollView scrollView, int x, int y, int oldx, int oldy);
    }
    private ScrollViewListener scrollViewListener = null;

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
