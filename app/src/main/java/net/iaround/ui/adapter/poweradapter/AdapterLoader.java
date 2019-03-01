package net.iaround.ui.adapter.poweradapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
public interface AdapterLoader<T> {

    /**
     *  STATE
     */
    int STATE_LOADING = 1;
    int STATE_LASTED = 2;


    int TYPE_BOTTOM = 0x80000000;
    int TYPE_EMPTY = 0x80000001;
    int TYPE_ERROR = 0x80000010;

    /**
     * This method should be called  when you load more !
     *
     * @param holder the current holder.
     * @param loadState the current state.
     */
    void onBottomViewHolderBind(AbsBottomViewHolder holder, OnLoadMoreListener listener, @LoadState int loadState);

    /**
     * If you want to create the specified bottom layout,you must call this method to add your specified layout !
     *
     * @param viewRes the specified bottom view layout res
     */
    void setLoadMoreView(@LayoutRes int viewRes);

    void setEmptyView(@NonNull View emptyView);

    void setErrorView(@NonNull View errorView);

    void showEmpty();

    /**
     * show the error view when load data error.
     *
     * @param force true would show the error view don't care there was data before.false would care about.
     */
    void showError(boolean force);

    /**
     * If you want to create the specified bottom layout ,you should implements this method to create your own
     * bottomViewHolder
     *
     * @param loadMore whether is loadingMore or not..
     */
    AbsBottomViewHolder onBottomViewHolderCreate(View loadMore);

    boolean isHasMore();

    void updateLoadingMore();

    void enableLoadMore(boolean loadMore);

    void onErrorHolderBind(@NonNull PowerHolder<T> holder);

    void onEmptyHolderBind(@NonNull PowerHolder<T> holder);

    /**
     * You can call this method to add data to RecycleView,if you want to append data,you should call
     * {@link #appendList(List)}
     *
     * @param data the data you want to add
     */
    void setList(@NonNull List<T> data);

    void clearList();

    /**
     * @param data the data you want to add
     */
    void appendList(@NonNull List<T> data);

    /**
     * remove the specified position in the list. If this method throw RecyclerView Exception when you delete the
     * last one.
     * @param position he specified position to remove
     * @return if successful return the removed object,otherwise null
     */
    T removeItem(int position);

    T getItem(int position);

    /**
     * remove the specified position in the list.
     *
     * @param position he specified position to remove
     */
    void insertItem(int position, T bean);

    /**
     * @param position the current pos .
     * @return the current Type.
     */
    int getItemViewTypes(int position);

    /**
     * @param holder current holder.
     * @param position current pos.
     */
    void onViewHolderBind(@NonNull PowerHolder<T> holder, int position);

    PowerHolder<T> onViewHolderCreate(@NonNull ViewGroup parent, int viewType);

    /**
     * Return the current size about {@link PowerAdapter#list}.
     *
     * @return current list size!
     */
    int getItemRealCount();

    void performClick(@NonNull View itemView, int position, T item);

    boolean performLongClick(@NonNull View itemView, int position, T item);

    /**
     * call this method after init RecyclerView(set LayoutManager)
     */
    void attachRecyclerView(@NonNull RecyclerView recyclerView);

    /**
     * call this method to get the first position of the viewType
     *
     * @param viewType the viewType you set ,default is 0
     * @return the index of list
     */
    int findFirstPositionOfType(int viewType);

    /**
     * call this method to get the last position of the viewType
     *
     * @param viewType the viewType you set ,default is 0
     * @return the index of list
     */
    int findLastPositionOfType(int viewType);

    /**
     * Interface definition for a callback to be invoked when
     * an item in this view has been selected.
     */
    interface OnItemSelectedListener {
        /**
         * <p>Callback method to be invoked when an item in this view has been
         * selected. This callback is invoked only when the newly selected
         * position is different from the previously selected position or if
         * there was no selected item.</p>
         * <p>
         * Impelmenters can call getItemAtPosition(position) if they need to access the
         * data associated with the selected item.
         *
         * @param view The view within the AdapterView that was clicked
         * @param position The position of the view in the adapter
         * @param isSelected The state of isSelected
         */
        void onItemSelected(@NonNull View view, int position, boolean isSelected);

        /**
         * Callback method to be invoked when the selection disappears from this
         * view. The selection can disappear for instance when touch is activated
         * or when the adapter becomes empty.
         */
        void onNothingSelected();
    }

    interface OnItemClickListener<T> {
        void onItemClick(@NonNull View itemView, int position, T item);
    }

    interface OnItemLongClickListener<T> {
        boolean onItemLongClick(@NonNull View itemView, int position, T item);
    }
}
