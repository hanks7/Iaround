package net.iaround.ui.chat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import net.iaround.R;

import butterknife.ButterKnife;

/**
 * 作者：zx on 2017/8/24 20:02
 */
public class WorldMessageSelftRecordView extends WorldMessageBaseView{

    public WorldMessageSelftRecordView(Context context) {
        super(context);
    }

    @Override
    protected View initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_record_world_msg_self, null);
        ButterKnife.bind(this, view);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        return view;
    }
}
