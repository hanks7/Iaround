package net.iaround.ui.view.luckpan;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;


public class LuckPanLayout2 extends RelativeLayout {

    private static final String TAG = "LuckPanLayout2";

    private LotteryRotateLayout rotatePan = null;
    private ImageView startBtn = null;
    private ImageView clsoeBtn = null;
    private TextView luckPanrule = null;
    private RelativeLayout bingoRoot = null;
    private ImageView bingoImage = null;
    private TextView bingoText = null;
    private OnClickListener onStartListener = null;
    private OnClickListener onCloseListener = null;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private int[] mIcons = {R.drawable.giftbox_purple,
            R.drawable.moon_silver,
            R.drawable.skill_kick,
            R.drawable.skill_speak,
            R.drawable.skill_kiss,
            R.drawable.skill_burst,
            R.drawable.gold_coin,
            R.drawable.skill_protect,
            R.drawable.moon_silver,
            R.drawable.gold_coin};

    public LuckPanLayout2(Context context) {
        this(context,null);
    }

    public LuckPanLayout2(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LuckPanLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void initView(){
        if(null==rotatePan) {
            rotatePan = (LotteryRotateLayout) findViewById(R.id.lottery_rotate_layout);
        }
        if(null==startBtn){
            startBtn = (ImageView)findViewById(R.id.lottery_pointer);
        }

        if(bingoRoot == null){
            bingoRoot = (RelativeLayout) findViewById(R.id.lottery_bingo);
        }
        if(bingoImage == null){
            bingoImage = (ImageView) findViewById(R.id.lottery_bingo_image);
        }
        if(bingoText == null){
            bingoText = (TextView) findViewById(R.id.lottery_bingo_text);
        }
        if(null==clsoeBtn){
            clsoeBtn = (ImageView)findViewById(R.id.lottery_close);
        }
        if(null==luckPanrule){
            luckPanrule = (TextView) findViewById(R.id.btn_lottery_rule);
            luckPanrule.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }
    /**
     * 开始旋转
     * @param pos  0-9 转到指定的转盘
     */
    public void rotate(int pos){
        if(pos<0 || pos >9){
            return;
        }
        initView();
        rotatePan.startRotate(pos);
        setStartBtnEnable(false);
    }

    /**
     * 点击开始按钮事件
     */
    public void setStartListener(OnClickListener clickListener){
        initView();
        onStartListener = clickListener;
        if(null!=onStartListener) {
            startBtn.setOnClickListener(onStartListener);
        }
    }

    /**
     * 点击关闭按钮事件
     */
    public void setCloseListener(OnClickListener clickListener){
        initView();
        onCloseListener = clickListener;
        if(null!=onCloseListener) {
            clsoeBtn.setOnClickListener(onCloseListener);
        }
    }

    /**
     * 点击规则按钮事件
     */
    public void setRuleListener(View.OnClickListener clickListener){
        initView();
        if(null!=clickListener) {
            luckPanrule.setOnClickListener(clickListener);
        }
    }

    /* 设置奖励数量
    * */
    public void setAmount(int[] amount){
        initView();
        rotatePan.setAmount(amount);
    }

    protected void setStartBtnEnable(boolean enable){
        if(startBtn != null) {
            startBtn.setEnabled(enable);
        }
    }

    //通知旋转结束
    protected void animationEndEvent(int pos, int count){
        CommonFunction.log(TAG, "animationEndEvent() pos="+pos+", count=" + count);
        if(bingoRoot == null || bingoImage==null || bingoText==null){
            return;
        }
        bingoImage.setImageResource(mIcons[pos]);
        bingoText.setText("x " + String.valueOf(count));
        bingoRoot.setVisibility(View.VISIBLE);

        //scale view
        if(null==mAnimation) {
            mAnimation = new ScaleAnimation(1, 1.5f, 1, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mAnimation.setInterpolator(new LinearInterpolator());
            mAnimation.setDuration(1500);
            mAnimation.setStartOffset(0);
        }
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bingoRoot.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bingoRoot.startAnimation( mAnimation );
    }

    //旋转结束 Listener 接口
    public interface AnimationEndListener{
        void endAnimation(int position);
    }

    private AnimationEndListener l;

    public void setAnimationEndListener(AnimationEndListener l){
        this.l = l;
    }

    public AnimationEndListener getAnimationEndListener(){
        return l;
    }

}
