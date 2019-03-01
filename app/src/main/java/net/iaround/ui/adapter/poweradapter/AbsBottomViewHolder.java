package net.iaround.ui.adapter.poweradapter;

import android.view.View;

/**
 * Created by joe on 2018/9/27.
 * Email: lovejjfg@gmail.com
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbsBottomViewHolder extends PowerHolder {

    public AbsBottomViewHolder(View itemView) {
        super(itemView, false);
    }

    public abstract void onBind(OnLoadMoreListener loadMoreListener, @LoadState int loadState);
}
