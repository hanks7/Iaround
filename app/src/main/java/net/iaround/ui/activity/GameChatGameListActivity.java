package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.GameChatGameListBean;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.utils.OnClickUtil;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/5.
 * 陪玩分类
 */

public class GameChatGameListActivity extends TitleActivity implements HttpCallBack {
    private PullToRefreshListView mPtrlGame;
    private ArrayList<GameChatGameListBean.GameChatGameListItem> mGameList = new ArrayList<>();
    private GameListAdapter mGameListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_C(R.string.more_game_list);
        setContent(R.layout.activity_game_chat_game_list);
        mPtrlGame = (PullToRefreshListView) findViewById(R.id.ptrl_game);
        mGameListAdapter = new GameListAdapter(mGameList);
        mPtrlGame.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPtrlGame.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                GameChatHttpProtocol.getGameList(GameChatGameListActivity.this, GameChatGameListActivity.this);
            }
        });
        mPtrlGame.setAdapter(mGameListAdapter);
        showWaitDialog();
        GameChatHttpProtocol.getGameList(this, this);

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        mPtrlGame.onRefreshComplete();
        destroyWaitDialog();
        GameChatGameListBean bean = GsonUtil.getInstance().getServerBean(result, GameChatGameListBean.class);
        if (bean != null && bean.gamelists != null) {
            mGameList.clear();
            mGameList.addAll(bean.gamelists);
            mGameListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        mPtrlGame.onRefreshComplete();
        destroyWaitDialog();
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(OnClickUtil.isFastClick()){
                return;
            }
            GameChatGameListBean.GameListItem gameItem = (GameChatGameListBean.GameListItem) parent.getAdapter().getItem(position);
            Intent intent = new Intent(GameChatGameListActivity.this, GameListActivity.class);
            intent.putExtra("GameId", gameItem.GameID);
            startActivity(intent);
        }
    };


    class GameListAdapter extends BaseAdapter {
        ArrayList<GameChatGameListBean.GameChatGameListItem> gameList;

        public GameListAdapter(ArrayList<GameChatGameListBean.GameChatGameListItem> gameList) {
            this.gameList = gameList;
        }

        @Override
        public int getCount() {
            if (gameList != null) {
                return gameList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(GameChatGameListActivity.this).inflate(R.layout.item_game_chat_game_list, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_cate_name = (TextView) convertView.findViewById(R.id.tv_cate_name);
                viewHolder.mgv_game_list = (MyGridView) convertView.findViewById(R.id.mgv_game_list);
                viewHolder.v_line = (View) convertView.findViewById(R.id.v_line);
                viewHolder.itemAdapter = new GameListItemAdapter();
                viewHolder.mgv_game_list.setAdapter(viewHolder.itemAdapter);
                viewHolder.mgv_game_list.setOnItemClickListener(mOnItemClickListener);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_cate_name.setText(gameList.get(position).catename);
            viewHolder.itemAdapter.updateData(gameList.get(position).gamelist);
            if((gameList.size() -1) == position){
                viewHolder.v_line.setVisibility(View.GONE);
            }else {
                viewHolder.v_line.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            public TextView tv_cate_name;
            public MyGridView mgv_game_list;
            public GameListItemAdapter itemAdapter;
            public View v_line;
        }

        class GameListItemAdapter extends BaseAdapter {
            private ArrayList<GameChatGameListBean.GameListItem> items;

            public void updateData(ArrayList<GameChatGameListBean.GameListItem> items) {
                if (items != null) {
                    this.items = items;
                    this.notifyDataSetChanged();
                }
            }

            @Override
            public int getCount() {
                if (items != null) {
                    return items.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                if (items != null) {
                    return items.get(position);
                }
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ItemViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_game_chat_game_list_item, null);
                    viewHolder = new ItemViewHolder();
                    viewHolder.ivGameType = (ImageView) convertView.findViewById(R.id.iv_game_type);
                    viewHolder.tvGameTypeName = (TextView) convertView.findViewById(R.id.tv_game_tye_name);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ItemViewHolder) convertView.getTag();
                }
                GlideUtil.loadCircleImage(mContext, items.get(position).GameIcon, viewHolder.ivGameType);
                viewHolder.tvGameTypeName.setText(items.get(position).GameName);
                return convertView;
            }

            class ItemViewHolder {
                public ImageView ivGameType;
                public TextView tvGameTypeName;
            }


        }
    }

}
