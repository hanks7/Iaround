package net.iaround.ui.comon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-12-6 下午4:59:41
 * @Description: 遇见通用下拉菜单
 */
public class PopMenu extends LinearLayout {

	private boolean isInited = false;
	private Context mContext;
	private LinearLayout llBackground;
	private LinearLayout llContainer;
	private ArrayList<View> itemViewList;
	
	private Animation animDismiss;
	private Animation animShow;
	
	public PopMenu(Context context) {
		super(context);
		mContext = context;
		LayoutInflater.from( getContext( ) ).inflate( R.layout.pop_menu_layout , this );
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		itemViewList = new ArrayList<View>();
		animDismiss = AnimationUtils.loadAnimation( mContext , R.anim.user_nearby_filter_dismiss );
		animShow = AnimationUtils.loadAnimation( mContext , R.anim.user_nearby_filter_show );
	}
	
	public PopMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater.from( getContext( ) ).inflate( R.layout.pop_menu_layout , this );
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
		llBackground = (LinearLayout) findViewById(R.id.llBackground);
		llBackground.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					hide();
				}	
				return false;
			}
		});
		itemViewList = new ArrayList<View>();
		animDismiss = AnimationUtils.loadAnimation( mContext , R.anim.user_nearby_filter_dismiss );
		animShow = AnimationUtils.loadAnimation( mContext , R.anim.user_nearby_filter_show );
	}

	public void init(ArrayList<PopMenuItemBean> list)
	{
		if(list == null || list.size() <= 0)
		{
			return;
		}
		
		for(int i = 0; i < list.size(); i++)
		{
			addItemView(list.get(i), i == list.size() - 1);
		}
		isInited = true;
	}
	
	
	public boolean isInited()
	{
		return isInited;
	}
	
	private void addItemView(PopMenuItemBean itemBean, boolean isLast)
	{
		View view = View.inflate(mContext, R.layout.pop_menu_item, null);
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		tvName.setText(itemBean.nameResId);
		if(itemBean.drawableResId != 0)
		{
			Drawable drawable = getResources( ).getDrawable( itemBean.drawableResId );
			drawable.setBounds( 0 , 0 , drawable.getMinimumWidth( ) , drawable.getMinimumHeight( ) );
			tvName.setCompoundDrawables(drawable, null, null, null);
			tvName.setCompoundDrawablePadding(10);
			tvName.setGravity(Gravity.LEFT);
		}else{
			tvName.setGravity(Gravity.CENTER);
		}
		tvName.setSelected(false);
		
		TextView tvSplit = (TextView) view.findViewById(R.id.tvSplit);
		if(isLast)
		{
			tvSplit.setVisibility(View.GONE);
		}else
		{
			tvSplit.setVisibility(View.VISIBLE);
		}
		
		view.setOnClickListener(new PopItemClickListener(itemBean.onClickListener));
		itemViewList.add(view);
		llContainer.addView(view);
	}
	
	/**
	 * index是要设置选中的按钮,从0开始计算
	 * @param index
	 */
	public void setSelection(int index)
	{
		if(index >= itemViewList.size())
		{
			return;
		}
		for(int i = 0; i < itemViewList.size(); i++)
		{
			if(i == index)
			{
				itemViewList.get(i).findViewById(R.id.tvName).setSelected(true);
			}else
			{
				itemViewList.get(i).findViewById(R.id.tvName).setSelected(false);
			}
		}
	}
	
	public void show()
	{
		if(!this.isShown())
		{
			this.startAnimation( animShow );
			this.setVisibility(View.VISIBLE);
		}
	}
	
	public void hide()
	{
		if(this.isShown())
		{
			this.startAnimation( animDismiss );
			this.setVisibility(View.GONE);
		}
	}
	
	public PopMenuItemBean initItemBean(int nameResId, int drawableResId, View.OnClickListener onClickListener) {
		return new PopMenuItemBean(nameResId, drawableResId, onClickListener);
	}

	public class PopMenuItemBean{

		public int nameResId;//按钮的字符串的资源id
		public int drawableResId;//按钮的图片资源id
		public View.OnClickListener onClickListener;//点击事件

		public PopMenuItemBean(int nameResId, int drawableResId, View.OnClickListener onClickListener) {
			this.nameResId = nameResId;
			this.drawableResId = drawableResId;
			this.onClickListener = onClickListener;
		}
	}

	//处理统一操作,点击后关闭列表
	class PopItemClickListener implements View.OnClickListener
	{
		private View.OnClickListener mListener;

		public PopItemClickListener(View.OnClickListener listener) {
			mListener = listener;
		}

		@Override
		public void onClick(View arg0) {
			PopMenu.this.hide();
			if(mListener != null)
			{
				mListener.onClick(arg0);
			}
		}
		
	}
}
