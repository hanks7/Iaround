package net.iaround.ui.view.pipeline;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.entity.PipelineGift;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.utils.ScreenUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：zx on 2017/7/10 17:03
 * 管道礼物view
 */
public class PipelineGiftView extends LinearLayout implements View.OnClickListener {

    private TextView sendUserName;
    private TextView receiveUserName;
    private HeadPhotoView sendUserAvatar;
    private ImageView giftImage;
    private RideTextView tv_giftNum;
    private TextView tv_gift_array;
    private FrameLayout contentView;
    private LinearLayout array_ll;

    private PipelineGift gift;//礼物信息
    private int currentNum = 0;//当前view显示的礼物数量
    private int totalNum = 0;//当前礼物数量
    private int giftNum = 0;//当前礼物数量
    private boolean isShow = false;//判断当前view是否显示
    private int defaultMarginTop = 10;//view默认距上的距离

    private ObjectAnimator scaleAnimator;
    private ObjectAnimator translationXAnimator;

    private OnHeadViewClickListener onHeadViewClickListener;
    private OnAddGiftNumListener onAddGiftNumListener;

    private Timer mGiftClearTimer;//礼物连击定时器

    private long sendGiftId;      // 送礼ID

    public PipelineGiftView(Context context) {
        this(context, null);
    }

    public PipelineGiftView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PipelineGiftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setVisible(GONE);
        setOrientation(VERTICAL);
        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        View giftView = inflate(getContext(), R.layout.pipeline_gift_view, null);
        contentView = (FrameLayout) giftView.findViewById(R.id.contentView);
        array_ll = (LinearLayout) giftView.findViewById(R.id.array_ll);
        sendUserAvatar = (HeadPhotoView) giftView.findViewById(R.id.iv_send_user_avatar);
        sendUserAvatar.setOnClickListener(this);
        giftImage = (ImageView) giftView.findViewById(R.id.gift_type);
        sendUserName = (TextView) giftView.findViewById(R.id.tv_send_user_name);
        receiveUserName = (TextView) giftView.findViewById(R.id.tv_receive_user_name);
        tv_gift_array = (TextView) giftView.findViewById(R.id.gift_array);
        tv_giftNum = (RideTextView) giftView.findViewById(R.id.gift_num);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "DIN-BlackItalic.otf");
                tv_gift_array.setTypeface(typeface);
                tv_giftNum.setTypeface(typeface);
            }
        });

        addView(giftView);
    }

    /**
     * 设置当前view在屏幕上下的位置
     * @param heightPosition
     */
    public void setHeightPosition(int heightPosition){
        if (heightPosition <= 0){
            heightPosition = defaultMarginTop;
        }
        heightPosition = ScreenUtils.dp2px(heightPosition);
        MarginLayoutParams margin = new MarginLayoutParams(getLayoutParams());
        margin.setMargins(margin.leftMargin, heightPosition, margin.rightMargin, margin.bottomMargin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(margin);
        setLayoutParams(params);
    }

    /**
     * 设置礼物信息
     *
     * @param gift
     */
    public void setGift(PipelineGift gift) {
        Log.d("GiftQueueHandler","setGift = ");
        this.gift = gift;
        if (gift.getCurrentGiftPosition() > 1){
            this.giftNum = gift.getCurrentGiftPosition();
        }else{
            this.giftNum = 0;
        }
        this.totalNum = 0;
        this.isShow = false;
        refreshView();
    }

    /**
     * 更新当前礼物数据
     * @param num
     */
    public void refershViewNumber(int num){
        if (null == gift) {
            return;
        }

        CommonFunction.log("GiftQueueHandler","refershViewNumber   num  =" + num +"  giftArray" + gift.getGiftArrayNmber());
        if (gift.getGiftType() == 1){
            this.totalNum = num * gift.getGiftArrayNmber();
        }else{
            this.totalNum = num;
        }

        handler.removeCallbacks(runnable);
    }

    /**
     * 刷新view基本信息
     */
    private void refreshView() {
        if (null == gift) {
            return;
        }

        sendGiftId = gift.getUser_gift_id();
        //设置数据
        CommonFunction.log("GiftQueueHandler","getSendIcon = "+gift.getSendIcon());
        sendUserAvatar.executeChat(R.drawable.iaround_default_img,gift.getSendIcon(),gift.getSvip(),gift.getViplevel(),-1);
        GlideUtil.loadImageCache(BaseApplication.appContext,gift.giftImgUrl,giftImage);
        sendUserName.setText(FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, gift.sendUser,
                0, null));
        receiveUserName.setText(FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, gift.receiveUser,
                0, null));
        if (gift.getGiftType() == 1){
            tv_giftNum.setRideText(String.valueOf(gift.getGiftArrayNmber()));
        }else {
            tv_giftNum.setRideText("1");
        }

        if (gift.getGiftType() == 1 || gift.getGiftType() == 0) {
            setGiftArray(GONE);
        }else {
            setGiftArray(VISIBLE);
        }
    }

    /**
     * 设置连送条目与礼物数组
     *
     * @param num
     * @param giftArray 礼物数组的数量
     */
    public void setGiftNum(final int num, int giftArray) {
        CommonFunction.log("GiftQueueHandler","setGiftNum   num  =" + num +"  giftArray" + giftArray);
        if (gift.getGiftType() == 1){
            this.totalNum = num * gift.getGiftArrayNmber();
        }else{
            this.totalNum = num;
        }
        setVisible(VISIBLE);
        tv_gift_array.setText(giftArray + "");
        if (!isShow) {
            isShow = true;
            translationView(300);
        }

        // 礼物定时器
        if(mGiftClearTimer == null){
            mGiftClearTimer = new Timer();
            mGiftClearTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (giftNum < totalNum) {
                        if (gift.getGiftType() == 1) {
                            giftNum += gift.getGiftArrayNmber();
                        } else {
                            giftNum++;
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_giftNum.setRideText(giftNum + "");
                                scaleView(tv_giftNum, 300);
                            }
                        });
                    }else{
                        if (null != scaleAnimator){
                            if (!scaleAnimator.isRunning()){
                                handler.postDelayed(runnable, 6000);
                            }
                        }
                    }
                }
            }, 0, 600);
        }

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //当前view消失时恢复默认
            synchronized (onAddGiftNumListener){
                if (null != scaleAnimator){
                    if (!scaleAnimator.isRunning()) {
                        sendGiftId = 0;
                        isShow = false;
                        alphaGoneView(PipelineGiftView.this, 260);
                        releaseResource();
                        if (null != onAddGiftNumListener) {
                            onAddGiftNumListener.onAddGiftNumEnd(gift);
                        }
                    }}
            }
        }
    };

    /**
     * 控制当前view是否显示
     * @param visible
     */
    private void setVisible(final int visible) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(visible);
            }
        });
    }

    /**
     * 控制礼物数组条目是否显示
     * @param visible
     */
    private void setGiftArray(int visible) {
        array_ll.setVisibility(visible);
    }

    public long getSendGiftId() {
        return sendGiftId;
    }

    /**
     * view的位移动画，从屏幕左侧移动到当前屏幕
     *
     * @param duration
     */
    private void translationView(long duration) {
        int width = getWidth();
        if (width == 0){
            width = ScreenUtils.dp2px(210);
        }

        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", -width, 0f, 0f, 0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        if (null == translationXAnimator){
            translationXAnimator = ObjectAnimator.ofPropertyValuesHolder(this, translationX, alpha);
        }
        translationXAnimator.setDuration(duration);
        translationXAnimator.start();
    }

    private void alphaGoneView(View view, long duration){
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        alpha.setDuration(duration);
        alpha.start();
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisible(INVISIBLE);
                setGiftArray(GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setVisible(VISIBLE);
                setGiftArray(array_ll.getVisibility());
            }
        });
    }

    /**
     * 设置礼物数量缩放效果
     *
     * @param view
     * @param duration
     */
    private void scaleView(View view, long duration) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 3.0f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 3.0f, 1f);
        if (null == scaleAnimator){
            scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        }
        scaleAnimator.setDuration(duration);
        scaleAnimator.start();

        scaleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (giftNum >= totalNum) {
                    handler.postDelayed(runnable, 3000);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 是否在执行View
     * @return
     */
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    /**
     * 释放资源
     */
    public void releaseResource() {
        if (null != handler) {
            handler.removeCallbacks(runnable);
        }
        if (null != scaleAnimator) {
            scaleAnimator.cancel();
        }
        if (null != translationXAnimator) {
            translationXAnimator.cancel();
        }

        if (mGiftClearTimer != null) {
            mGiftClearTimer.cancel();
            mGiftClearTimer = null;
        }
        onHeadViewClickListener = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseResource();
    }


    public void setOnHeadViewClickListener(OnHeadViewClickListener OnHeadViewClickListener) {
        this.onHeadViewClickListener = OnHeadViewClickListener;
    }

    public void setOnAddGiftNumListener(OnAddGiftNumListener onAddGiftNumListener) {
        this.onAddGiftNumListener = onAddGiftNumListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send_user_avatar:
                if (null != onHeadViewClickListener){
                    onHeadViewClickListener.headViewClick(gift);
                }
                break;
        }
    }
}
