package net.iaround.ui.view.user;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iaround.R;

public class PullUpRefreshListView extends ListView {

	private View footerView;
	private ProgressBar progressBar;
	private TextView tv_info;

	/** 表示是否正在加载更多 */
	private boolean loadingMore;
	private OnLoadingMoreListener mOnLoadingMoreListener;

	public PullUpRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		setOnScrollListener(new OnScrollListener() {
			
			//  当ListView的滚动状态发生改变的时候会调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				showLoadingMore(true);
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE	// 如果ListView处于空闲状态了
						&& getLastVisiblePosition() == getCount() - 1	// 最后可见的是不是最后一条item
						&& !loadingMore									// 如果当前没有做加载更多的操作
						&& mOnLoadingMoreListener != null				// 监听器不为空
						) {
					loadingMore = true;
					mOnLoadingMoreListener.onLoadingMore();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		});
	}

	private void initView(Context context) {
		footerView = View.inflate(context, R.layout.load_more_lv_footer, null);
		addFooterView(footerView);
		showLoadingMore(false);
	}

	/**
	 *
	 * @param isShow
     */
	public void showLoadingMore(boolean isShow) {
		if(isShow){
			footerView.setPadding(0, footerView.getMeasuredHeight(), 0, 0);
			footerView.setVisibility(VISIBLE);
		} else {
			footerView.setPadding(0, -footerView.getMeasuredHeight(), 0, 0);
			footerView.setVisibility(GONE);
		}
	}

	public void setOnLoadingMoreListener(OnLoadingMoreListener mOnLoadingMoreListener) {
		this.mOnLoadingMoreListener = mOnLoadingMoreListener;
	}
	
	/** ListView正在加载更多 的监听 */
	public interface OnLoadingMoreListener {
		/** ListView处于正在加载更多的时候会调用这个方法  */
		void onLoadingMore();
	}

	public void onLoadingMoreComplete() {
		showLoadingMore(false);
		loadingMore = false;
	}

}
















