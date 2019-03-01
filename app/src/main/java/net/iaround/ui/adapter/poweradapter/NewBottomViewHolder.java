package net.iaround.ui.adapter.poweradapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.iaround.R;

@SuppressWarnings("Convert2Lambda")
public class NewBottomViewHolder extends AbsBottomViewHolder {
    private LinearLayout container;
    private TextView content;
    private final ProgressBar pb;

    public NewBottomViewHolder(View itemView) {
        super(itemView);
        container = (LinearLayout) itemView.findViewById(R.id.footer_container);
        content = (TextView) itemView.findViewById(R.id.content);
        pb = (ProgressBar) itemView.findViewById(R.id.progressbar);
    }

    @Override
    public void onBind(OnLoadMoreListener loadMoreListener, int loadState) {
        switch (loadState) {
            case AdapterLoader.STATE_LOADING:
                content.setVisibility(View.VISIBLE);
                content.setText(R.string.visitor_loading_more);
                pb.setVisibility(View.VISIBLE);
                if (loadMoreListener != null) {
                    loadMoreListener.onLoadMore();
                }
                break;
            case AdapterLoader.STATE_LASTED:
                pb.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                content.setText(R.string.no_more);
                break;
        }
    }

}
