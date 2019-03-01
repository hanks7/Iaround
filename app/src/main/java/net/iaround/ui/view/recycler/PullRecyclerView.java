package net.iaround.ui.view.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import net.iaround.R;
import net.iaround.ui.adapter.ChatBarAdapter;



public class PullRecyclerView extends RecyclerView {


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    boolean isLoadingData = false;
    OnLoadMoreListener onLoadMoreListener;
    public View footView;
    public View loadingView, noMoreDataView, loadingbar;
    boolean needLoadmore = false;//是否需要加载更多

    public PullRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public PullRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(context);

    }


    void init(Context ctx) {
        footView = LayoutInflater.from(ctx).inflate(R.layout.item_loading, null);
        loadingView = footView.findViewById(R.id.loading);
//        noMoreDataView = footView.findViewById(R.id.no_more_data);
        loadingbar = footView.findViewById(R.id.loadingbar);
    }


    // call after setAdapter
    public void showFootView(boolean show) {
        if (show) {
            needLoadmore = true;
            Adapter adapter = getAdapter();
            if (adapter instanceof ChatBarAdapter) {
                ((ChatBarAdapter) adapter).setFooterView(footView);
            }
        }
    }

    public void canLoadMore(boolean can) {
        needLoadmore = can;

        if (!can) {
            Adapter adapter = getAdapter();
            if (adapter instanceof ChatBarAdapter) {
                ((ChatBarAdapter) adapter).setFooterView(null);
            }

        }
    }


    public void setOnLoadMoreListener(OnLoadMoreListener lis) {
        this.onLoadMoreListener = lis;
    }


    public void serLoadMoreComplete() {
        isLoadingData = false;
        footView.setVisibility(View.GONE);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (needLoadmore && state == SCROLL_STATE_IDLE && onLoadMoreListener != null && !isLoadingData) {
            LayoutManager manager = getLayoutManager();

            int lastVisiblePosition = -1;

            if (manager instanceof LinearLayoutManager) {
                lastVisiblePosition = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
            }

            if (manager.getChildCount() > 1 && lastVisiblePosition == manager.getItemCount() - 1) {
                isLoadingData = true;
                onLoadMoreListener.onLoadMore();
                footView.setVisibility(View.VISIBLE);
            }
        }
    }


}
