package net.iaround.ui.chatbar.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import net.iaround.R;
import net.iaround.tools.DensityUtils;
import net.iaround.ui.chatbar.adapter.ChatBarGiftPopWindowAdapter;

public class GroupChatBarGiftPopWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private Context mContext;
    private MItemSelectListener mItemSelectListener;
    private ChatBarGiftPopWindowAdapter myAdapter;
    private int popupWidth;
    private int popupHeight;
    private int mComNum;

    public GroupChatBarGiftPopWindow(Context context,int comNum) {
        super(context);

        this.mContext = context;
        this.mComNum=comNum;
        init();
    }

    private void init() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_bar_gift_popwindow, null);
        setContentView(view);
        //获取自身的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();
        mListView = (ListView) view.findViewById(R.id.lv_group_chat_bar_gift_popwindow);
        if(mComNum > 7){
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = DensityUtils.dp2px(mContext,203);
            mListView.setLayoutParams(params);
        }
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);//设置为true的时候才能点击列表中的item
        ColorDrawable cd = new ColorDrawable(Color.TRANSPARENT);
//        ColorDrawable dw = new ColorDrawable(-00000);// 半透明
        setBackgroundDrawable(cd);// 设置背景图片，不能在布局中设置，要通过代码来设置
        setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功，如上
//        this.setAnimationStyle(R.style.popwin_anim_style);//设置从底部弹出的动画


        mListView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(i);
        }
        dismiss();
    }

    //设置适配器
    public void setAdapter(ChatBarGiftPopWindowAdapter myAdapter) {
        this.myAdapter = myAdapter;
        this.mListView.setAdapter(this.myAdapter);
    }

    //更新列表
    public void setNotify() {
        this.myAdapter.notifyDataSetChanged();
    }

    //设置监听
    public void setItemSelectListener(MItemSelectListener mItemSelectListener) {
        this.mItemSelectListener = mItemSelectListener;
    }

    //自定义list中item的点击监听
    public interface MItemSelectListener {
        void onItemClick(int position);
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     *
     * @param v
     */
    public void showUp(View v, int count,View parent) {
        if (myAdapter != null) {
            View listItem = myAdapter.getView(0, null, mListView);
            listItem.measure(0, 0);
            int itemHeight = listItem.getMeasuredHeight();
            //获取需要在其上方显示的控件的位置信息
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            //在控件上方显示
            int i = location[1] - itemHeight * 5 - popupHeight;
            int width=v.getWidth();
            int height=v.getHeight();
            int left=v.getLeft();
            int right=v.getRight();
//            showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - itemHeight * 5 - v.getHeight()/2 - popupHeight);
//            showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
//            showAtLocation(v, Gravity.RIGHT | Gravity.BOTTOM, (int)(v.getWidth()*0.75) , v.getHeight());
            showAtLocation(v, Gravity.RIGHT | Gravity.BOTTOM, (int) (parent.getWidth()-v.getWidth()*0.5), v.getHeight());
        }
    }


    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     *
     * @param v
     */
    public void showUp2(View v) {
//        //获取需要在其上方显示的控件的位置信息
//        int[] location = new int[2];
//        v.getLocationOnScreen(location);
//        //在控件上方显示
//        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);


        if (myAdapter != null) {
            View listItem = myAdapter.getView(0, null, mListView);
            listItem.measure(0, 0);
            int itemHeight = listItem.getMeasuredHeight();
            //获取需要在其上方显示的控件的位置信息
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            //在控件上方显示
            showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        }
    }
}