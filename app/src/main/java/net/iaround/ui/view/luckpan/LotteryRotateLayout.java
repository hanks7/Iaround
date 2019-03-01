package net.iaround.ui.view.luckpan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import net.iaround.R;

/**
 * Created by liangyuanhuan on 23/10/2017.
 */

public class LotteryRotateLayout extends RelativeLayout {
    private Context mContext;
    private Paint textPaint = null;
    private int panNum = 0;
    private int InitAngle = 0;
    private int radius = 0;
    private int verPanRadius ;
    private int diffRadius ;
    private String[] strs ;
    private int[] counts;
    //开始和结束的角度
    private int angleStart = 0;
    private int angleEnd = 0;
    private static final int ROTATE_ROUND = 2; //旋转圈数
    private static final int ROTATE_TIME = 2000; //旋转时间 2秒
    private int mCurrentPos = 0;

    public LotteryRotateLayout(Context context) {
        this(context,null);
    }

    public LotteryRotateLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LotteryRotateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(null!=inflater){
            RelativeLayout rotateLayout = (RelativeLayout)inflater.inflate(R.layout.chatbar_lottery_rotate,this,false);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            this.addView(rotateLayout,layoutParams);
        }

        panNum = 10;
        InitAngle = (360 / panNum)*7;
        verPanRadius = 360 / panNum;
        diffRadius = verPanRadius /2;
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Util.dip2px(context,14));

        strs = context.getResources().getStringArray(R.array.luckpan_names);
        counts = context.getResources().getIntArray(R.array.luckpan_counts);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //Log.d("LotteryRotateLayout","dispatchDraw() into");
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        //Log.d("LotteryRotateLayout","dispatchDraw() into, width="+width + ", height="+height);

        int MinValue = Math.min(width, height);

        radius = MinValue / 2;

        int len = Util.dip2px(mContext,45);
        RectF rectF = new RectF(paddingLeft+len, paddingTop+len, width-len, height-len);

        //名字
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Util.dip2px(mContext,12));
        for (int i = 0; i < panNum; i++) {
            //float startAngle = (panNum % 4 == 0) ? InitAngle + diffRadius + (diffRadius * 3 / 4) : InitAngle + diffRadius;
            float startAngle = InitAngle + diffRadius/2;
            drawNameText(startAngle + 3, strs[i], 2 * radius, textPaint, canvas, rectF);
            InitAngle += verPanRadius;
        }

        InitAngle = (360/panNum)*7;

        //数量
        len = Util.dip2px(mContext,62);
        rectF = new RectF(paddingLeft+len, paddingTop+len, width-len, height-len);
        textPaint.setTextSize(Util.dip2px(mContext, 12));
        textPaint.setColor(Color.parseColor("#db6ef2"));
        for (int i = 0; i < panNum; i++) {
            //float startAngle = (panNum % 4 == 0) ? InitAngle + diffRadius + (diffRadius * 3 / 4) : InitAngle + diffRadius;
            float startAngle = InitAngle + diffRadius/2;
            drawCountText(startAngle + 3, String.valueOf(counts[i]), 2 * radius, textPaint, canvas, rectF);
            InitAngle += verPanRadius;
        }
    }


    private void drawNameText(float startAngle, String string, int mRadius, Paint mTextPaint, Canvas mCanvas, RectF mRange)
    {
        //Log.d("LotteryRotateLayout","drawNameText() into, startAngle="+startAngle+", string=" + string + ", mRadius="+mRadius  + ", mRange.top=" + mRange.top + ", mRange.left="+mRange.left + ", mRange.right="+mRange.right + ", mRange.bottom="+mRange.bottom);
        Path path = new Path();
        path.addArc(mRange, startAngle, verPanRadius);
        //float textWidth = mTextPaint.measureText(string);

        //圆弧的水平偏移
        //float hOffset  = (panNum % 4 == 0)?((float) (mRadius * Math.PI / panNum/ 3 )) :((float) (mRadius * Math.PI / panNum/ 3 - textWidth/2 ));
        //float hOffset = Util.dip2px(mContext,(float)(mRadius * Math.PI / panNum/ 2 ));
        float hOffset = 0;//Util.dip2px(mContext, mRadius / 12) ;
        //圆弧的垂直偏移
        float vOffset =  0;//((float)mRadius *2 / 15); //Util.dip2px(mContext, mRadius / 15) ;

        //Log.d("LotteryRotateLayout","drawNameText() hOffset=" + hOffset + ", vOffset="+vOffset);
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    private void drawCountText(float startAngle, String string,int mRadius,Paint mTextPaint,Canvas mCanvas,RectF mRange)
    {
        Path path = new Path();
        path.addArc(mRange, startAngle, verPanRadius);
        //float textWidth = mTextPaint.measureText(string);

        //圆弧的水平偏移
        float hOffset  = 0;//(panNum % 4 == 0)?((float) (mRadius * Math.PI / panNum/2 )) :((float) (mRadius * Math.PI / panNum/2 - textWidth/2 ));
        if(string.length()==1){
            hOffset = 20;
        }else if(string.length()==2){
            hOffset = 15;
        }else if(string.length()==3){
            hOffset = 5;
        }
        //圆弧的垂直偏移
        float vOffset = 0;//(float)mRadius * 2 / 11;
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    /*设置奖品的数量
    * */
    public void setAmount(int[] amount){
        if(amount==null || amount.length!=10){
            return;
        }

        counts = amount;
    }

    /*开始旋转到指定位置
    * pos 奖品位置 0-9
    * 0 <item>道具包</item>
      1  <item>银月亮 50</item>
      2  <item>无影脚</item>
      3  <item>紧箍咒</item>
      4  <item>大么么</item>
      5  <item>菊花残</item>
      6  <item>金币 50000</item>
      7  <item>保护费</item>
      8  <item>银月亮 5</item>
      9 <item>金币 5000</item>
    * */
    public void startRotate(int pos){
        int add = pos-mCurrentPos >= 5 ? 360 : 0;
        angleEnd = angleStart + ROTATE_ROUND*360 -  (pos-mCurrentPos)*36 + add;
        Log.d("LotteryRotateLayout","angleStart=" + angleStart + ", angleEnd=" + angleEnd);
        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "rotation", angleStart,angleEnd);
        oa.setDuration(ROTATE_TIME);
        final int fpos = pos;
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mCurrentPos = fpos;
                ViewParent parent = getParent().getParent();
                if(parent instanceof  LuckPanLayout2) {
                    ((LuckPanLayout2) (getParent().getParent())).animationEndEvent(fpos,counts[fpos]);
                    ((LuckPanLayout2) (parent)).setStartBtnEnable(true);
                }
            }
        });
        oa.start();
        angleStart = angleEnd;
    }
}
