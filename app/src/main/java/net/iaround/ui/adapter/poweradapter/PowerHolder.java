
package net.iaround.ui.adapter.poweradapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PowerHolder<T> extends RecyclerView.ViewHolder {
    public boolean enableCLick = true;

    public PowerHolder(View itemView) {
        super(itemView);
    }

    public PowerHolder(View itemView, boolean enableCLick) {
        super(itemView);
        this.enableCLick = enableCLick;
    }

    public void onBind(T t) {

    }

    public Context getContext() {
        return itemView.getContext();
    }
}
