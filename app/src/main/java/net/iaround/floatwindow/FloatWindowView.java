package net.iaround.floatwindow;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;


import net.iaround.R;

import java.lang.reflect.Field;

/**
 * Created by liangyuanhuan on 25/08/2017.
 */

public class FloatWindowView extends FrameLayout {
    //自定义布局坐标
    private int mScreenTop = -1;
    private int mScreenBottom = -1;
    private int mScreenLeft = -1;
    private int mScreenRight = -1;
    //自定义布局宽高
    private int mWindowWidth = -1;
    private int mWindowHeight;

    //移动悬浮窗
    //移动开关
    private boolean mCanMove = false;

    //用于更新悬浮窗的位置
    private WindowManager windowManager;

    //悬浮窗的参数
    private WindowManager.LayoutParams mParams = null;

    //记录小悬浮窗的宽度
    private int viewWidth;

    //记录小悬浮窗的高度
    private int viewHeight;

    //记录系统状态栏的高度
    private  int statusBarHeight;

    //记录当前手指位置在屏幕上的横坐标值
    private float xInScreen;

    //记录当前手指位置在屏幕上的纵坐标值
    private float yInScreen;

    //记录手指按下时在屏幕上的横坐标的值
    private float xDownInScreen;

    //记录手指按下时在屏幕上的纵坐标的值
    private float yDownInScreen;

    //记录手指按下时在小悬浮窗的View上的横坐标的值
    private float xInView;

    //记录手指按下时在小悬浮窗的View上的纵坐标的值
    private float yInView;

    public FloatWindowView(Context context) {
        this(context, null);
    }

    public FloatWindowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatWindowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FloatWindowView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.FloatWindowView_screenTop:
                    // 默认设置为 -1
                    mScreenTop = a.getDimensionPixelSize(attr, -1);//a.getLayoutDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.FloatWindowView_screenBottom:
                    // 默认设置为 -1
                    mScreenBottom = a.getDimensionPixelSize(attr, -1);//a.getLayoutDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.FloatWindowView_screenLeft:
                    // 默认设置为20
                    mScreenLeft = a.getDimensionPixelSize(attr, -1); //a.getLayoutDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.FloatWindowView_screenRight:
                    // 默认设置为20
                    mScreenRight = a.getDimensionPixelSize(attr, -1); //a.getLayoutDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.FloatWindowView_floatWindowWidth:
                    // 默认设置为0
                    mWindowWidth = a.getDimensionPixelSize(attr, -1); //a.getLayoutDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.FloatWindowView_floatWindowHeight:
                    // 默认设置为20
                    mWindowHeight = a.getDimensionPixelSize(attr, 20); //a.getLayoutDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                    break;

            }

        }
        a.recycle();

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        //计算 WIDTH
        if(-1 == mWindowWidth) {
            if(mScreenLeft!= -1 && mScreenRight !=-1) {
                viewWidth = screenWidth - mScreenLeft - mScreenRight;
            }
        }else if(-1!=mWindowWidth){
            viewWidth = mWindowWidth;
        }

        //计算 Height
        viewHeight = mWindowHeight;

        //计算 TOP
        if( -1 == mScreenTop && -1!=mScreenBottom){
            mScreenTop = screenHeight - mScreenBottom - mWindowHeight;
        }

        //计算LEFT
        if(-1 == mScreenLeft && mScreenRight != -1){
            mScreenLeft = screenWidth - mScreenRight - mWindowWidth;
        }

        //Log.d("FloatWindowView", "FloatWindowView width=" + viewWidth + ", height=" + viewHeight + ", top=" + mScreenTop+ ", left=" + mScreenLeft);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.d("FloatWindowView", "dispatchTouchEvent() into");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        //Log.d("FloatWindowView", "onInterceptTouchEvent() into, result="+result);
        return result;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("FloatWindowView", "onTouchEvent() into");
        if(isCanMove()==false){
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                float xdiff = Math.abs(xDownInScreen - xInScreen);
                float ydiff = Math.abs(yDownInScreen - yInScreen);
                Log.d("FloatWindowView", "onTouchEvent() xdiff=" + xdiff + ", ydiff=" + ydiff);
                if (xdiff<=5 && ydiff<=5) {
                    Log.d("FloatWindowView", "onTouchEvent() single click event");
                    performClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        if(null!=mParams) {
            mParams.x = (int) (xInScreen - xInView);
            mParams.y = (int) (yInScreen - yInView);
            windowManager.updateViewLayout(this, mParams);
        }
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }


    public int getScreenTop() {
        return mScreenTop;
    }


    public int getScreenLeft() {
        return mScreenLeft;
    }


    public int getScreenRight() {
        return mScreenRight;
    }

    public boolean isCanMove() {
        return mCanMove;
    }

    public void setCanMove(boolean canMove) {
        this.mCanMove = canMove;
    }
}