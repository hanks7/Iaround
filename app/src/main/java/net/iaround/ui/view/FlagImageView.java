package net.iaround.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import net.iaround.R;

/**
 * @author：liush on 2017/1/3 17:28
 */
public class FlagImageView extends ImageView {
    public FlagImageView(Context context) {
        this(context, null);
    }

    public FlagImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FlagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        setBackgroundResource(R.drawable.flag_selector);
    }

    public void setState(Boolean state){
        setSelected(state);
    }
}
