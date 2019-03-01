package net.iaround.ui.adapter.poweradapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

class LoadMoreScrollListener extends RecyclerView.OnScrollListener {
    private final RecyclerView.LayoutManager manager;
    private final PowerAdapter adapter;

    LoadMoreScrollListener(RecyclerView recyclerView) {
        manager = recyclerView.getLayoutManager();
        adapter = (PowerAdapter) recyclerView.getAdapter();
        if (null == manager) {
            throw new RuntimeException("You should call setLayoutManager() first!!");
        }
        if (null == adapter) {
            throw new RuntimeException("You should call setAdapter() first!!");
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (manager instanceof LinearLayoutManager) {
            int lastCompletelyVisibleItemPosition =
                ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();

            if (adapter.getItemCount() > 1 && lastCompletelyVisibleItemPosition >= adapter.getItemCount() - 1 && adapter
                .isHasMore()) {
                adapter.updateLoadingMore();
            }
        }
        if (manager instanceof StaggeredGridLayoutManager) {
            int count = ((StaggeredGridLayoutManager) manager).getSpanCount();
            int[] itemPositions = new int[count];
            ((StaggeredGridLayoutManager) manager).findLastVisibleItemPositions(itemPositions);
            int lastVisibleItemPosition = itemPositions[0];
            for (int i = count - 1; i > 0; i--) {
                if (lastVisibleItemPosition < itemPositions[i]) {
                    lastVisibleItemPosition = itemPositions[i];
                }
            }
            if (lastVisibleItemPosition >= adapter.getItemCount() - 1 && adapter.isHasMore()) {
                adapter.updateLoadingMore();
            }
        }
    }
}
