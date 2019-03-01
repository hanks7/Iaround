package net.iaround.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.DensityUtils;

import java.lang.ref.WeakReference;

public class AudioControlView extends View {

    private static final String TAG = AudioControlView.class.getName();
    /**
     * 录制最长时间 秒
     */
    private static final int AUDIO_RECORD_DEFAULT_MAX_TIME = 20;
    /**
     * 录制最小时间 秒
     */
    private final int AUDIO_RECORD_DEFAULT_MIN_TIME = 1;
    /**
     * 录制内圆半径
     */
    private final float AUDIO_RECORD_DEFAULT_INNER_CIRCLE_RADIUS = 5f;
    /**
     * 录制内圆默认颜色
     */
    private final int AUDIO_RECORD_DEFAULT_INNER_CIRCLE_COLOR = Color.parseColor("#F5F5F5");
    /**
     * 录制进度默认颜色
     */
    private final int AUDIO_RECORD_DEFAULT_PROGRESS_COLOR = Color.parseColor("#00A653");


    /**
     * 内圆缩小倍数
     */
    private final float INNER_CIRCLE_SHRINKS = 1.1f;
    private float innerCircleShrinks;
    /**
     * 视频实际录制最大时间
     */
    private int mMaxTime;
    /**
     * 视频实际录制最小时间
     */
    private int mMinTime;

    /**
     * 内圆半径
     */
    private float mInnerCircleRadius, mInitInnerRadius;
    /**
     * 内圆颜色
     */
    private int mInnerCircleColor;
    /**
     * 进度条颜色
     */
    private int mProgressColor;
    /**
     * 内圆画笔
     */
    private Paint mInnerCirclePaint;
    /**
     * 进度条画笔
     */
    private Paint mProgressPaint;


    /**
     * 是否正在录制
     */
    private boolean isRecording = false;
    /**
     * 进度条值动画
     */
    private ValueAnimator mProgressAni;

    /**
     * 开始录制时间
     */
    private long mStartTime = 0;
    /**
     * 录制 结束时间
     */
    private long mEndTime = 0;
    /**
     * 长按最短时间  单位毫秒
     */
    public long LONG_CLICK_MIN_TIME = 300;
    private Context context;
    private int mWidth;
    private int mHeight;
    private float mCurrentProgress;
    private MHandler handler = new MHandler(this);

    public AudioControlView(Context context) {
        this(context, null);
    }


    public AudioControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs);
    }

    /**
     * 获取布局属性
     *
     * @param context
     * @param attrs
     */
    private void initData(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AudioControlView);
        mMaxTime = a.getInt(R.styleable.AudioControlView_maxTime, AUDIO_RECORD_DEFAULT_MAX_TIME);
        mMinTime = a.getInt(R.styleable.AudioControlView_minTime, AUDIO_RECORD_DEFAULT_MIN_TIME);

        innerCircleShrinks = a.getFloat(R.styleable.AudioControlView_innerCircleShrinks
                , INNER_CIRCLE_SHRINKS);
        if (innerCircleShrinks < 1) {
            throw new RuntimeException("内圆缩小倍数必须大于1");
        }

        mInitInnerRadius = mInnerCircleRadius = a.getDimension(R.styleable.AudioControlView_innerCircleRadius
                , AUDIO_RECORD_DEFAULT_INNER_CIRCLE_RADIUS);

        mInnerCircleColor = a.getColor(R.styleable.AudioControlView_innerCircleColor
                , AUDIO_RECORD_DEFAULT_INNER_CIRCLE_COLOR);
        mProgressColor = a.getColor(R.styleable.AudioControlView_progressColor
                , AUDIO_RECORD_DEFAULT_PROGRESS_COLOR);
        a.recycle();

        //初始化内圆画笔
        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(mInnerCircleColor);
        setLayerType(LAYER_TYPE_SOFTWARE, mInnerCirclePaint);
        mInnerCirclePaint.setShadowLayer(10, 0, 3, getResources().getColor(R.color.common_iaround_red));

        //初始化进度条画笔
        mProgressPaint = new Paint();
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStrokeWidth(DensityUtils.dp2px(BaseApplication.appContext,7));
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        //进度条的属性动画
        mProgressAni = ValueAnimator.ofFloat(0, 360f);
        mProgressAni.setDuration(mMaxTime * 1000);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        //画内圆
        canvas.drawCircle(mWidth / 2, mHeight / 2, mInnerCircleRadius, mInnerCirclePaint);
        if (isRecording) {
            drawProgress(canvas);
        }
    }

    /**
     * 绘制圆形进度条
     * Draw a circular progress bar.
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        final RectF rectF = new RectF(
                mWidth / 2 - (mInnerCircleRadius + 15 + 5),
                mHeight / 2 - (mInnerCircleRadius + 15 + 5),
                mWidth / 2 + (mInnerCircleRadius + 15 + 5),
                mHeight / 2 + (mInnerCircleRadius + 15 + 5));
        canvas.drawArc(rectF, -90, mCurrentProgress, false, mProgressPaint);
    }

    @Override
    public boolean performClick() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                mStartTime = System.currentTimeMillis();
                handler.sendEmptyMessageDelayed(MSG_START_LONG_RECORD, LONG_CLICK_MIN_TIME);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(isRecording){
                    isRecording = false;
                    mEndTime = System.currentTimeMillis();
                    if (mEndTime - mStartTime < LONG_CLICK_MIN_TIME) {
                        //Long press the action time too short.
                        if (handler.hasMessages(MSG_START_LONG_RECORD)) {
                            handler.removeMessages(MSG_START_LONG_RECORD);
                        }
                        if (onRecordListener != null) {
                            onRecordListener.onShortClick();
                        }

                    } else {

                        if (mProgressAni != null && mProgressAni.getCurrentPlayTime() / 500 < mMinTime) {
                            //The recording time is less than the minimum recording time.
                            if (onRecordListener != null) {
                                onRecordListener.OnFinish(0);
                            }
                        } else {
                            //The end of the normal
                            if (onRecordListener != null) {
                                onRecordListener.OnFinish(1);
                            }
                        }
                    }
                    mInnerCircleRadius = mInitInnerRadius;
                }
                mProgressAni.cancel();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    /**
     * 设置外圆 内圆缩放动画
     *
     * @param smallStart
     * @param smallEnd
     */
    private void startAnimation(float smallStart, float smallEnd) {

        ValueAnimator smallObjAni = ValueAnimator.ofFloat(smallStart, smallEnd);
        smallObjAni.setDuration(150);
        smallObjAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerCircleRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

//        bigObjAni.start();
        smallObjAni.start();

        smallObjAni.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //开始绘制圆形进度
                if (isRecording) {
                    startAniProgress();
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
     * 开始圆形进度值动画
     */
    private void startAniProgress() {
        if (mProgressAni == null) {
            return;
        }
        mProgressAni.start();
        mProgressAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mProgressAni.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if(isRecording){
                    if (onRecordListener != null) {
                        onRecordListener.OnFinish(1);
                    }
                }
                isRecording = false;
                mCurrentProgress = 0;
                invalidate();
            }
        });
    }

    /**
     * 设置最大录制时间
     *
     * @param mMaxTime 最大录制时间 秒
     */
    public void setMaxTime(int mMaxTime) {
        this.mMaxTime = mMaxTime;
    }

    /**
     * 设置最小录制时间
     *
     * @param mMinTime 最小录制时间 秒
     */
    public void setMinTime(int mMinTime) {
        this.mMinTime = mMinTime;
    }


    /**
     * 设置内圆半径
     *
     * @param mInnerCircleRadius 内圆半径
     */
    public void setInnerCircleRadius(float mInnerCircleRadius) {
        this.mInnerCircleRadius = mInnerCircleRadius;
    }


    /**
     * 设置进度圆环颜色
     *
     * @param mProgressColor
     */
    public void setProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
        mProgressPaint.setColor(mProgressColor);
    }

    /**
     * 设置内圆颜色
     *
     * @param mInnerCircleColor
     */
    public void setInnerCircleColor(int mInnerCircleColor) {
        this.mInnerCircleColor = mInnerCircleColor;
        mInnerCirclePaint.setColor(mInnerCircleColor);
    }

    private OnRecordListener onRecordListener;

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    public static abstract class OnRecordListener {

        /**
         * 点击事件
         */
        public void onShortClick() {
        }

        /**
         * 开始录制
         */
        public abstract void OnRecordStartClick();

        /**
         * 录制结束
         *
         * @param resultCode 0 录制时间太短 1 正常结束
         */
        public abstract void OnFinish(int resultCode);
    }

    /**
     * 长按录制
     */
    private static final int MSG_START_LONG_RECORD = 0x1;


    static class MHandler extends android.os.Handler {

        private WeakReference<AudioControlView> weakReference = null;

        public MHandler(AudioControlView controlView) {
            weakReference = new WeakReference<AudioControlView>(controlView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference == null || weakReference.get() == null) return;
            final AudioControlView videoControlView = weakReference.get();
            switch (msg.what) {
                case AudioControlView.MSG_START_LONG_RECORD:
                    if (videoControlView.onRecordListener != null) {
                        videoControlView.onRecordListener.OnRecordStartClick();
                    }
                    //内外圆动画，内圆缩小，外圆放大
                    videoControlView.startAnimation(
                            videoControlView.mInnerCircleRadius,
                            videoControlView.mInnerCircleRadius * videoControlView.innerCircleShrinks);
                    break;
            }
        }
    }
}
