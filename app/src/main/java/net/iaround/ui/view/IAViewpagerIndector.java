package net.iaround.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.iaround.R;

/**
 * Creat by zhangsen on 2018/9/13 on 上午11:56
 * net.iaround.ui.view
 */
public class IAViewpagerIndector extends View{

    private static final String TAG = IAViewpagerIndector.class.getSimpleName();
    private final int DEFAULT_INDICATOR_MODE = Mode.SOLO.ordinal();
    private final int DEFAULT_INDICATOR_COUNT = 3;
    private final int DEFAULT_INDICATOR_RADIUS = 10;
    private final int DEFAULT_INDICATOR_MARGIN = 20;
    private final int DEFAULT_INDICATOR_NORMAL_COLOR = getResources().getColor(R.color.circle_normal);
    private final int DEFAULT_INDICATOR_SELECTED_COLOR = getResources().getColor(R.color.login_btn);

    private Mode mMode = Mode.values()[DEFAULT_INDICATOR_MODE];

    private int mCiCount = DEFAULT_INDICATOR_COUNT;
    private float mCiRadius = DEFAULT_INDICATOR_RADIUS;
    private float mCiMargin = DEFAULT_INDICATOR_MARGIN;
    private int mCiNormalColor = DEFAULT_INDICATOR_NORMAL_COLOR;
    private int mCiSelectedColor = DEFAULT_INDICATOR_SELECTED_COLOR;

    private int mViewWidth;
    private int mViewHeigth;

    private Paint mPaintNormal;
    private Paint mPaintSelected;

    public enum Mode {
        SOLO,
        OUTSIDE
    }

    private int mCurrentSelectPosition = 0;

    private float mIndicatorSelectX;

    public IAViewpagerIndector(Context context) {
        this(context,null);
    }

    public IAViewpagerIndector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IAViewpagerIndector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs == null){
            return;
        }
        mPaintNormal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSelected  = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IAViewpagerIndector,defStyleAttr,0);
        mCiCount = typedArray.getInt(R.styleable.IAViewpagerIndector_ci_count,DEFAULT_INDICATOR_COUNT);
        mCiRadius = typedArray.getDimensionPixelSize(R.styleable.IAViewpagerIndector_ci_radius,DEFAULT_INDICATOR_RADIUS);
        mCiMargin = typedArray.getDimensionPixelSize(R.styleable.IAViewpagerIndector_ci_margin,DEFAULT_INDICATOR_MARGIN);
        mCiNormalColor = typedArray.getColor(R.styleable.IAViewpagerIndector_ci_normalColor,DEFAULT_INDICATOR_NORMAL_COLOR);
        mCiSelectedColor = typedArray.getColor(R.styleable.IAViewpagerIndector_ci_selectedColor,DEFAULT_INDICATOR_SELECTED_COLOR);
        int mode = typedArray.getInt(R.styleable.IAViewpagerIndector_ci_mode,DEFAULT_INDICATOR_MODE);
        mMode = Mode.values()[mode];
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //EXACTLY 父布局指定了确切的大小，无论子view指定多大的尺寸，子view必须在父view指定的大小范围内，对应的属性是match_parent或者具体的值
        // AT_MOST   父view为子view指定了一个最大的尺寸，子view必须确保自己的孩子view可以适应在该尺寸上，对应的属性是wrap_parent
        if(widthMode == MeasureSpec.EXACTLY){
            mViewWidth = widthSize;
        }else {
            mViewWidth = (int)(mCiCount*mCiRadius*2+(mCiCount-1)*mCiMargin+4*mCiRadius);
        }

        int heigthMode = MeasureSpec.getMode(heightMeasureSpec);
        int heigthSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heigthMode == MeasureSpec.EXACTLY){
            mViewHeigth = heigthSize;
        }else {
            mViewHeigth = getHeight();
        }
        setMeasuredDimension(mViewWidth,mViewHeigth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawIndicators(canvas);
        canvas.restore();
    }

    private void drawIndicators(Canvas canvas) {
        mPaintNormal.setColor(mCiNormalColor);
        mPaintSelected.setColor(mCiSelectedColor);
        float cx = mViewWidth / 2;
        float cy = mViewHeigth / 2;
        for (int i = 0; i < mCiCount; i++) {
            if(i == mCurrentSelectPosition){
                RectF oval = new RectF(calculateIndicatorX(i)-3*mCiRadius,cy-mCiRadius,calculateIndicatorX(i)+3*mCiRadius,cy+mCiRadius);
                canvas.drawRoundRect(oval,mCiRadius,mCiRadius,mPaintSelected);
            }else {
                if(mCurrentSelectPosition > 0){
                    if(i>mCurrentSelectPosition){
                        canvas.drawCircle(calculateIndicatorX(i)+2*mCiRadius, cy, mCiRadius, mPaintNormal);
                    }else {
                        canvas.drawCircle(calculateIndicatorX(i)-2*mCiRadius, cy, mCiRadius, mPaintNormal);
                    }
                }else {
                    canvas.drawCircle(calculateIndicatorX(i)+2*mCiRadius, cy, mCiRadius, mPaintNormal);
                }
            }
        }
    }

    /**
     * X轴坐标
     */
    private float calculateIndicatorX(int position){
        float indicatorRealWidth = mCiCount*mCiRadius*2+(mCiCount-1)*mCiMargin;
        float margin = position*mCiMargin+position*mCiRadius*2;
        return mViewWidth/2-indicatorRealWidth/2+mCiRadius+margin;
    }

    /**
     * Y轴坐标
     */
    private float calculateIndicatorY(int position) {
        float indicatorRealHeight = mCiCount*mCiRadius*2+(mCiCount-1)*mCiMargin;
        float margin = position*mCiMargin+position*mCiRadius*2;
        return mViewHeigth/2-indicatorRealHeight/2+mCiRadius+margin;
    }

    /**
     * SOLO 标准模式
     */
    public void setPosition(int position){
        this.mCurrentSelectPosition = position;
        float currentIndectorX = calculateIndicatorX(position);
        if(currentIndectorX != mIndicatorSelectX){
            mIndicatorSelectX = currentIndectorX;
            invalidate();
        }
    }

    /**
     * outside 模式  正在开发
     */
    public void setPosition(int position, float positionOffset){
        if(positionOffset <= 0){
            //scroll end
            mCurrentSelectPosition = position;
        }
        if(mMode == Mode.SOLO){
            setPosition(position);
        }else {
            float destIndectorX = calculateIndicatorX(position);
            float towIndicatorDistance = mCiRadius * 2 + mCiMargin;
            mIndicatorSelectX = destIndectorX + positionOffset * towIndicatorDistance;
            invalidate();
        }
    }

    /**
     * 设置滑动模式
     */
    public void setMode(Mode mode) {
        if (mode != mMode) {
            this.mMode = mode;
            invalidate();
        }
    }

    /**
     * 设置个数
     */
    public void setCount(int count) {
        if (count<=0) {
            throw new RuntimeException("ERROR: invalid count.");
        }
        if (count != mCiCount) {
            this.mCiCount = count;
            requestLayout();
        }else {
            invalidate();
        }
    }

    /**
     *  设置小圆点半径
     */
    public void setRadius(float radius) {
        if (radius != mCiRadius) {
            this.mCiRadius = radius;
            requestLayout();
        }else {
            invalidate();
        }
    }

    /**
     * 设置间距
     */
    public void setMargin(float margin) {
        if (margin != mCiMargin) {
            this.mCiMargin = margin;
            requestLayout();
        }else {
            invalidate();
        }
    }

    /**
     * 设置默认颜色
     */
    public void setNormalColor(@ColorInt int normalColor) {
        if (normalColor != mCiNormalColor) {
            this.mCiNormalColor = normalColor;
            invalidate();
        }
    }

    /**
     * 设置选中颜色
     */
    public void setSelectedColor(@ColorInt int selectedColor) {
        if (selectedColor != mCiSelectedColor) {
            this.mCiSelectedColor = selectedColor;
            invalidate();
        }
    }

}
