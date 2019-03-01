package net.iaround.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 *
 * 一个视图容器控件
 * 阻止 拦截 ontouch事件传递给其子控件
 * Created by Administrator on 2017/5/15.
 */
public class InterceptScrollContainerView extends LinearLayout {

    public InterceptScrollContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public InterceptScrollContainerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
//
//  @Override
//  public boolean dispatchTouchEvent(MotionEvent ev) {
//      // TODO Auto-generated method stub
//      //return super.dispatchTouchEvent(ev);
//      Log.i("pdwy","ScrollContainer dispatchTouchEvent");
//      return true;
//  }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        //return super.onInterceptTouchEvent(ev);
        return true;

        //return super.onInterceptTouchEvent(ev);
    }

//  @Override
//  public boolean onTouchEvent(MotionEvent event) {
//      // TODO Auto-generated method stub
//      Log.i("pdwy","ScrollContainer onTouchEvent");
//      return true;
//  }
}