package net.iaround.ui.view.pipeline;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.utils.ScreenUtils;

/**
 * 作者：zx on 2017/7/25 18:31
 */
public class RideTextView extends TextView {

    private static final String SEAT_CHAR = "[seat_char]";//占位符
    private Drawable drawable;
    private ImageSpan span;
    private SpannableStringBuilder spannable;
    public RideTextView(Context context) {
        this(context, null);
    }

    public RideTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RideTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawable = getResources().getDrawable(R.drawable.icon_ride);
        drawable.setBounds(0, 0, ScreenUtils.dp2px(14), ScreenUtils.dp2px(14));
        //要让图片替代指定的文字就要用ImageSpan
        span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable = new SpannableStringBuilder("");
    }

    public void setRideText(String text){
        if (null != spannable && null != span){
            spannable.clear();
            spannable.append(SEAT_CHAR + text);
            spannable.setSpan(span, 0, SEAT_CHAR.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            setText(spannable);
        }else {
            init();
        }
    }
}
