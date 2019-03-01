package net.iaround.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.contract.BlackEditContract;
import net.iaround.model.entity.VisitorRecord;
import net.iaround.presenter.BlackEditPresenter;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.adapter.BlackFriendsListAdapter;
import net.iaround.ui.group.bean.GroupSearchUser;

import java.util.ArrayList;

/**
 * 黑名单选择并确定选择人员页面
 */
public class BlackSelectFriendsActivity extends TitleActivity implements BlackEditContract.View,View.OnClickListener{

    //标题
    private TextView tvTitle;//标题
//    private TextView tvRight;//标题右侧文字

    private TextView tvType;
    private ArrayList<GroupSearchUser> blackList = new ArrayList<>();
    private ArrayList<GroupSearchUser> addList = new ArrayList<>();
    private ArrayList<Long> tmpUid = new ArrayList<>();
    private PullToRefreshListView lvFriendsList;
    private BlackEditPresenter presenter;
    private int setType;
    private int selectType;

    private boolean moreData = false;
    private BlackFriendsListAdapter blackFriendsListAdapter;

    private int pageNo = 1;
    private int pageSize = 20;
    private int maxPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new BlackEditPresenter(this);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null, false, R.drawable.icon_publish, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = findView(R.id.tv_title);
        setContent(R.layout.activity_black_select_friends);
        tvType = findView(R.id.tv_type);
        lvFriendsList = findView(R.id.lv_friends_list);
        lvFriendsList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lvFriendsList.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        lvFriendsList.getRefreshableView().setFastScrollEnabled(false);
        lvFriendsList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (moreData) {
                    if (blackList != null && blackList.size() != 0) {
                        presenter.init(0, 0);
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.no_more), Toast.LENGTH_SHORT).show();
                    lvFriendsList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lvFriendsList.onRefreshComplete();
                        }
                    }, 200);
                }
            }
        });
    }

    private void initDatas() {
        //设置标题
        setType = getIntent().getIntExtra(Constants.SECRET_SET_TYPE, 0);
        switch (setType) {
            case Constants.SECRET_SET_INVISIBLE:
                tvTitle.setText(getString(R.string.setting_invisiable_myself));
                break;
            case Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION:
                tvTitle.setText(getString(R.string.setting_invisiable_myself_action));
                break;
            case Constants.SECRET_SET_INVISIABLE_OTHER_ACTION:
                tvTitle.setText(getString(R.string.setting_invisiable_other_action));
                break;
            case Constants.SECRET_SET_BLACK_LIST:
                tvTitle.setText(getString(R.string.setting_black));
                break;
        }
        //设置顶部提示：好友，粉丝，关注
        selectType = getIntent().getIntExtra(Constants.SECRET_SELECT_TYPE, 0);
        switch (selectType) {
            case Constants.SECRET_SET_FRIENDS_LIST:
                tvType.setText(getString(R.string.setting_relative_type_friends));
                break;
            case Constants.SECRET_SET_FOCUS_LIST:
                tvType.setText(getString(R.string.setting_relative_type_focus));
                break;
            case Constants.SECRET_SET_FANS_LIST:
                tvType.setText(getString(R.string.setting_relative_type_fans));
                break;
        }

        /*for(int i=0; i<13; i++){
            BlackEntity blackEntity = new BlackEntity();
            blackEntity.setHeadPic("http://p3.iaround.com/201612/13/FACE/f7d6ac79b0bf46f93f964d263dbd9f35.jpg");
            blackEntity.setNickname("本来就很帅"+i);
            blackEntity.setUid(123456);
            blackList.add(blackEntity);
        }*/
        presenter.init(selectType, true);
    }

    private void initListeners() {
//        lvFriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CheckBox ivState = (CheckBox) view.findViewById(R.id.check_box);
//                if (ivState.isSelected()) {
//                    ivState.setSelected(false);
//                    addList.remove(blackList.get(position - 1));
//                } else {
//                    ivState.setSelected(true);
//                    addList.add(blackList.get(position - 1));
//                }
//            }
//        });
        findViewById(R.id.iv_right).setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);
    }
    @Override
    public void showDatas(ArrayList<GroupSearchUser> datas, int isMoreData) {
        lvFriendsList.onRefreshComplete();
        if (blackList.size() == 0) {
            blackList.addAll(datas);
            blackFriendsListAdapter = new BlackFriendsListAdapter(this, blackList);
            lvFriendsList.setAdapter(blackFriendsListAdapter);
        } else {
            blackList.addAll(datas);
            blackFriendsListAdapter.notifyDataSetChanged();
        }
        moreData = isMoreData == VisitorRecord.EXIST_MORE_DATA;
    }

    @Override
    public void deleteComplete() {
    }

    @Override
    public void addComplete() {
        Intent intent = new Intent(this, BlackEditActivity.class);
        intent.putExtra(Constants.IS_REFRESH_DATA, true);
        intent.putExtra(Constants.SECRET_SET_TYPE, setType);
        startActivity(intent);
    }

    @Override
    public void showDialog() {
        showWaitDialog();
    }

    @Override
    public void hideDialog() {
        hideWaitDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fl_right:
            case R.id.iv_right:
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < blackList.size(); i++) {
                    if (blackList.get(i).isCheck) {
                        tmpUid.add(blackList.get(i).user.userid);
                        stringBuilder.append(blackList.get(i).user.userid + ",");
                    }
                }
                String tmp = "";
                if (stringBuilder.length() > 0)
                {
                    tmp = stringBuilder.substring(0,stringBuilder.length() - 1).toString();
                }
                presenter.addConfirm(setType, tmp);
                break;
        }
    }
}
