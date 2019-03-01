package net.iaround.floatwindow;

/**
 * Created by liangyuanhuan on 26/08/2017.
 */

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class FloatToast {
    private static final String TAG = "FloatToast";

    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;

    private Toast toast;
    private Context mContext;
    private int mDuration = LENGTH_ALWAYS;
    private int animations = -1;
    private boolean isShow = false;

    private Object mTN;
    private Method show;
    private Method hide;
    private WindowManager mWM;
    private WindowManager.LayoutParams params;
    private View mView;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x = 0;
    private float y = 0;
    private int width = -1;
    private int height = -1;

    private boolean mCanMove = false;

    //private Handler handler = new Handler();

    private FloatToast(Context context){
        this.mContext = context;
        Toast toast = Toast.makeText(context,"",0); //Toast.LENGTH_SHORT);
        this.toast  = toast;
//        if (toast == null) {
//            toast = new Toast(mContext);
//        }
        //LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mView = inflate.inflate(R.layout.float_window_small, null);
        //mView.setOnTouchListener(this);
    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Show the view for the specified duration.
     */
    public void show(){
        if (isShow)
            return;

        ///TextView tv = (TextView)mView.findViewById(R.id.msg_content);
        ///tv.setText("悬浮窗");

        if(null!=toast && mView!=null) {
            toast.setView(mView);
        }

        initTN();

        try {
            show.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = true;

        //判断duration，如果大于#LENGTH_ALWAYS 则设置消失时间
//        if (mDuration > LENGTH_ALWAYS) {
//            handler.postDelayed(hideRunnable, mDuration * 1000);
//        }
    }

    /**
     * Close the view if it's showing, or don't show it if it isn't showing yet.
     * You do not normally have to call this.  Normally view will disappear on its own
     * after the appropriate duration.
     */
    public void hide(){
        if(!isShow)
            return;
        try {
            hide.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = false;
    }

    public void setView(View view) {
        if(null!=toast && view !=null) {
            toast.setView(view);
        }

        mView = view;
        if(null!=view) {
            //mView.setOnTouchListener(this);
        }
    }

    public View getView() {
        return  mView; //toast.getView();
    }

    /**
     * Set how long to show the view for.
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     * @see #LENGTH_ALWAYS
     */
//    public void setDuration(int duration) {
//        mDuration = duration;
//    }
//
//    public int getDuration() {
//        return mDuration;
//    }

//    public void setMargin(float horizontalMargin, float verticalMargin) {
//        toast.setMargin(horizontalMargin,verticalMargin);
//    }
//
//    public float getHorizontalMargin() {
//        return toast.getHorizontalMargin();
//    }
//
//    public float getVerticalMargin() {
//        return toast.getVerticalMargin();
//    }

    public void setX(int pos){
        x = pos;
    }

    public void setY(int pos){
        y = pos;
    }

    public void setWidth(int length){
        width = length;
    }

    public void setHeight(int length){
        height = length;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity,xOffset,yOffset);
    }

    public int getGravity() {
        return toast.getGravity();
    }
//
//    public int getXOffset() {
//        return toast.getXOffset();
//    }
//
//    public int getYOffset() {
//        return toast.getYOffset();
//    }


    public static synchronized FloatToast makeToast(Context context) {
//        Toast toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        FloatToast exToast = new FloatToast(context);
//        exToast.toast = toast;
        //exToast.mDuration =  LENGTH_ALWAYS; //duration;

        return exToast;
    }


//    public void setText(int resId) {
//        setText(mContext.getText(resId));
//    }
//
//    public void setText(CharSequence s) {
//        toast.setText(s);
//    }

    public int getAnimations() {
        return animations;
    }

    public void setAnimations(int animations) {
        this.animations = animations;
    }

    private void initTN() {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //设置长宽
            if(-1!=width){
                params.width = width;
            }
            if(-1!=height){
                params.height = height;
            }
            params.x = (int)x;
            params.y = (int)y;

            if(Build.VERSION.SDK_INT > 24){
                // >= 7.1
                //params.type = WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL;//WindowManager.LayoutParams.TYPE_PHONE;
                //Log.d("TipViewController", ">= 7.1");
                //params.privateFlags |= WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
                //params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

            }

            /**设置动画*/
            if (animations != -1) {
                params.windowAnimations = animations;
            }

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            mWM = (WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGravity(Gravity.LEFT | Gravity.TOP,(int)x ,(int)y);
    }

//    private void updateViewPosition(){
//        //更新浮动窗口位置参数
//        params.x=(int) (x-mTouchStartX);
//        params.y=(int) (y-mTouchStartY);
//        mWM.updateViewLayout(toast.getView(), params);  //刷新显示
//    }

    /*
    * 取消touch移动功能
    * */
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if(mCanMove == false){
//            Log.i(TAG, "onTouch() cannot move");
//            return true;
//        }
//        //获取相对屏幕的坐标，即以屏幕左上角为原点
//        x = event.getRawX();
//        y = event.getRawY();
//        Log.i(TAG, "onTouch() currentX="+x+", currentY="+y);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:    //捕获手指触摸按下动作
//                //获取相对View的坐标，即以此View左上角为原点
//                mTouchStartX =  event.getX();
//                mTouchStartY =  event.getY();
//                Log.i(TAG,"startX="+mTouchStartX+", startY="+mTouchStartY);
//                break;
//            case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作
//                updateViewPosition();
//                break;
//            case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作
//                updateViewPosition();
//                break;
//        }
//        return true;
//    }

//    public void setCanMove(boolean move){
//        this.mCanMove = move;
//    }

    public WindowManager.LayoutParams getLayoutParams(){
        return params;
    }

}
