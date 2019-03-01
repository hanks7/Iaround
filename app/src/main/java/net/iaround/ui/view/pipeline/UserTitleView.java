package net.iaround.ui.view.pipeline;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.model.entity.Item;
import net.iaround.tools.RankingTitleUtil;


/**
 * Created by liangyuanhuan on 01/09/2017.
 */

public class UserTitleView extends FrameLayout {
    private RelativeLayout mBackground = null;
    private TextView mTextView = null;

    public UserTitleView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public UserTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
    }

    public UserTitleView(@NonNull Context context, @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);

        mBackground = new RelativeLayout(context);
        FrameLayout.LayoutParams paramsRl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dip2px(context,22));

        paramsRl.gravity = Gravity.CENTER_VERTICAL;
        mBackground.setLayoutParams(paramsRl);


        mTextView = new TextView(context); //new SpacingTextView(context);
        RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dip2px(context,22) );
        mTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mTextView.setLayoutParams(paramsText);
        mTextView.setPadding(dip2px(context,16),0,0,0);
        mTextView.setTextSize(12);
        mTextView.setTextColor(0xffffffff);
        mBackground.addView(mTextView);
        this.addView(mBackground);
    }

    //设置称号文字颜色
    public void setTitleTextColor(int color){
        mTextView.setTextColor(color);
    }

    //设置称号文字大小
    public void setTitleTextSize(float size) {
        mTextView.setTextSize(size);
    }

    //设置称号文字内容
    public void setTitleText(Item item) {
        if(item != null){
            RankingTitleUtil.getInstance().handleReallyRankNew(item.key,item.value,mTextView,mBackground);
        }
//        mTextView.setText(text);
    }
    public  void setText(String text){
        mTextView.setText(text);
    }

    //设置称号背景图片
    public void setTitleBackground(int resId) {
        mBackground.setBackgroundResource(resId);
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
