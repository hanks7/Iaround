
package net.iaround.ui.view.menu;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

import net.iaround.R;


public class CustomContextMenu {
    private Context mContext;
    private PopupWindow mPopupWindow;
    private RelativeLayout mContentLayout;
    private LinearLayout mMenuLayout;
    private ObjectAnimator mShowAnimator, mHideAnimator;
    private SparseArray<MenuItem> mMenuItemArray;

    //	private int dp_10;
    private String title;

    public CustomContextMenu(Context context) {
        mContext = context;
//		dp_10 = ( int ) ( mContext.getResources( ).getDisplayMetrics( ).density * 10 );
        initView();
        initAnimation();

        mPopupWindow = new PopupWindow(mContentLayout, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);

        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mMenuItemArray = new SparseArray<MenuItem>();
    }

    public CustomContextMenu(Context context, String title) {
        mContext = context;
        this.title = title;
//		dp_10 = ( int ) ( mContext.getResources( ).getDisplayMetrics( ).density * 10 );
        initView();
        initAnimation();

        mPopupWindow = new PopupWindow(mContentLayout, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mMenuItemArray = new SparseArray<MenuItem>();
    }

    private void initAnimation() {
        mShowAnimator = ObjectAnimator.ofFloat(mContentLayout, "alpha", 0.0f, 1.0f);
        mShowAnimator.setDuration(300);

        mHideAnimator = ObjectAnimator.ofFloat(mContentLayout, "alpha", 1.0f, 0.0f);
        mHideAnimator.setDuration(300);
        mHideAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {

            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    private void initView() {
        mContentLayout = new RelativeLayout(mContext);
//		mContentLayout.setBackgroundColor(Color.parseColor("#F6F6F6"));//背景颜色
        mContentLayout.setBackgroundColor(Color.parseColor("#B0000000"));
        //取消按钮
        Button cancelButton = new Button(mContext);
        cancelButton.setBackgroundResource(R.color.common_white);
        //cancelButton.setBackgroundResource( R.drawable.custom_bottom_menu_bg );
//		cancelButton.setBackgroundResource(R.drawable.custom_context_menu_shape);
        RelativeLayout.LayoutParams cancelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//		cancelButtonParams.setMargins(0,R.dimen.group_info_more_divider,0,0);
        cancelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        cancelButton.setLayoutParams(cancelButtonParams);
        cancelButton.setText(R.string.cancel);
//		cancelButton.setTextSize( R.dimen.group_info_more_text_size );
        cancelButton.setTextSize(17);
//		cancelButton.setTextColor(Color.parseColor("#4A4A4A"));
        cancelButton.setTextColor(Color.BLACK);
//        cancelButton.setId(1001);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mContentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mMenuLayout = new LinearLayout(mContext);
        mMenuLayout.setOrientation(LinearLayout.VERTICAL);
        //mMenuLayout.setBackgroundResource( R.drawable.custom_bottom_menu_bg );
        mMenuLayout.setBackgroundResource(R.drawable.custom_context_menu_shape);
        RelativeLayout.LayoutParams menuLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
//		menuLayoutParams.leftMargin = dp_10;
//		menuLayoutParams.rightMargin = dp_10;
        menuLayoutParams.addRule(RelativeLayout.ABOVE, cancelButton.getId());
        mMenuLayout.setLayoutParams(menuLayoutParams);

//		MenuItem tipsItem = new MenuItem(mContext, Color.parseColor("#888888"), false);
//		if ( title == null  || "".equals( title ) ) {
//			//提示Item
//			tipsItem.button.setText(R.string.prompt);
//		}
//		else {
//			//标题Item
//			tipsItem.button.setText( title );
//		}
//		mMenuLayout.addView(tipsItem);


        mContentLayout.addView(cancelButton);
        mContentLayout.addView(mMenuLayout);//内容容器
    }

    /**
     * 加入一个菜单项
     *
     * @param index
     * @param text
     * @param listener
     */
    public void addMenuItem(int index, String text, OnClickListener listener, boolean isLastItem) {
        addMenuItem(index, text, Color.parseColor("#FFFFFF"), listener, isLastItem);    //Color.WHITE
    }

    /**
     * 加入一个菜单项
     *
     * @param index
     * @param text
     * @param listener
     */
    public void addMenuItemNew(int index, String text, OnClickListener listener, boolean isLastItem) {
        addMenuItem(index, text, Color.parseColor("#717171"), listener, isLastItem);    //Color.WHITE
    }
    public void addMenuItemNewRed( int index , String text , OnClickListener listener, boolean isLastItem)
    {
        addMenuItem( index , text , Color.parseColor("#FF4064") , listener, isLastItem);	//Color.Red
    }

    /**
     * 加入一个菜单项
     *
     * @param index
     * @param text
     * @param buttonColor
     * @param listener
     */
    public void addMenuItem(int index, String text, int buttonColor,
                            OnClickListener listener, boolean isLastItem) {
        if (mMenuItemArray.get(index) == null) {
            MenuItem item = makeMenuItem(text, buttonColor, listener, isLastItem);
            item.button.setTag(index);
            mMenuItemArray.put(index, item);
            mMenuLayout.addView(item.button);
        }
    }

    /**
     * 删除菜单项
     *
     * @param index
     */
    public void delMenuItem(int index) {
        MenuItem item = null;
        if ((item = mMenuItemArray.get(index)) != null) {
            mMenuItemArray.remove(index);
            mMenuLayout.removeView(item.button);
        }
    }

    /**
     * 隐藏菜单项
     *
     * @param index
     */
    public void hideMenuItem(int index) {
        setMenuItemVisible(index, View.GONE);
    }

    /**
     * 显示菜单项
     *
     * @param index
     */
    public void showMenuItem(int index) {
        setMenuItemVisible(index, View.VISIBLE);
    }

    /**
     * 显示菜单
     *
     * @param view 这个view只适用于获取WindowsToken，只要是当前Windows中的view都可以传递进来
     */
    public void showMenu(View view) {
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
            mShowAnimator.start();
        }
    }

    public void dismiss() {
        if (isShowing()) {
            mHideAnimator.start();
        }
    }

    public void dismiss(boolean isAnimator) {
        if (isShowing()) {
            if (isAnimator) {
                mHideAnimator.start();
            } else {
                mPopupWindow.dismiss();
            }
        }
    }

    public boolean isShowing() {
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }

    private void setMenuItemVisible(int index, int visible) {
        MenuItem item = null;
        if ((item = mMenuItemArray.get(index)) != null) {
            item.button.setVisibility(visible);
        }
    }

    private MenuItem makeMenuItem(String text, int buttonColor,
                                  final OnClickListener listener, boolean isLastItem) {
        MenuItem item = new MenuItem(mContext, buttonColor, isLastItem);
        item.button.setText(text);
        item.button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                dismiss();
            }
        });
        return item;
    }

    private class MenuItem {
        public Button button;
        private boolean isLastItem;

        public MenuItem(Context context, int buttonColor, boolean isLastItem) {
            button = new BottomSrokeButton(context, isLastItem);
            button.setTextColor(buttonColor);
            button.setTextSize(17);
            button.setBackgroundDrawable(null);
//			button.setLayoutParams( new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.MATCH_PARENT , dp_10 * 5 ) );
        }
    }

    private class BottomSrokeButton extends Button {
        private int sroke_width = 1;
        private Paint paint = null;
        public boolean isLastItem;

        public BottomSrokeButton(Context context, boolean isLastItem) {
            super(context);
            this.isLastItem = isLastItem;
            if (!isLastItem) {
                // 将边框设为黑色
                paint = new Paint();
                paint.setColor(Color.parseColor("#b2b2b2"));//android.graphics.Color.BLACK
            }

        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!isLastItem) {
                // 画TextView的底边
                canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth()
                        - sroke_width, this.getHeight() - sroke_width, paint);
            }
            super.onDraw(canvas);
        }
    }
}
