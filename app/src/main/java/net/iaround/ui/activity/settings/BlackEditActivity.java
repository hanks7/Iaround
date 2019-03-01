package net.iaround.ui.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.contract.BlackEditContract;
import net.iaround.presenter.BlackEditPresenter;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.adapter.BlackEditGVAdapter;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.group.bean.GroupSearchUser;

import java.util.ArrayList;

/**
 * 点击隐私设置中（不让他看我动态，不看他动态，黑名单）的条目，都跳转到这个界面，
 * 根据上个界面传递过来的type去请求对应的数据，并展示出来
 * 关系部分都是以一黑名单为入口进行调试的，所以(不看他动态、不让他看我动态、黑名单)命名都是以black为前缀的
 */

public class BlackEditActivity extends TitleActivity implements BlackEditContract.View, View.OnClickListener {
    //标题
    private TextView tvTitle;
    private ImageView mIvRight;

    private BlackEditPresenter presenter;
    private TextView tvTips;
    private GridView gvBlackList;
    private ArrayList<GroupSearchUser> blackList = new ArrayList<>();
    private ArrayList<GroupSearchUser> deleteList = new ArrayList<>();
    private BlackEditGVAdapter blackAdapter;
//    private TextView tvRight;
    private ImageView ivRight;
    private int setType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new BlackEditPresenter(this);
        initViews();
        initDatas();
        initListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isRefreshData = getIntent().getBooleanExtra(Constants.IS_REFRESH_DATA, false);
        if(isRefreshData){
            initDatas();
        }
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, null, null, false, R.drawable.icon_publish, null, null);

        tvTitle = findView(R.id.tv_title);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mIvRight.setVisibility(View.GONE);
        setContent(R.layout.activity_edit_black);
        gvBlackList = findView(R.id.gv_black_list);
        tvTips = findView(R.id.tv_tips);
    }

    /**
     * 初始化页面标题，提示，没想列表数据
     */
    private void initDatas() {
        setType = getIntent().getIntExtra(Constants.SECRET_SET_TYPE,0);
        switch (setType){
            case Constants.SECRET_SET_INVISIBLE://隐私设置
                tvTitle.setText(getString(R.string.setting_invisiable_myself));
                tvTips.setText(getString(R.string.setting_invisible));
                presenter.init(Constants.SECRET_SET_INVISIBLE, 0);
                break;
            case Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION:
                tvTitle.setText(getString(R.string.setting_invisiable_myself_action));
                tvTips.setText(getString(R.string.setting_invisiable_myself_action_tips));
                presenter.init(Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION, 0);
                break;
            case Constants.SECRET_SET_INVISIABLE_OTHER_ACTION:
                tvTitle.setText(getString(R.string.setting_invisiable_other_action));
                tvTips.setText(getString(R.string.setting_invisiable_other_action_tips));
                presenter.init(Constants.SECRET_SET_INVISIABLE_OTHER_ACTION, 0);
                break;
            case Constants.SECRET_SET_BLACK_LIST:
                tvTitle.setText(getString(R.string.setting_black));
                tvTips.setText(getString(R.string.setting_black_tips));
                presenter.init(Constants.SECRET_SET_BLACK_LIST, 0);
                break;
        }
    }

    /**
     * 好友，粉丝，关注添加删除时的监听
     */
    private void initListeners() {
        gvBlackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == blackAdapter.getCount()-2){
                    startAddBlackActivity();
                } else if(position == blackAdapter.getCount()-1) {
                    blackAdapter.setShowDeleteIcon(true);
                    blackAdapter.notifyDataSetChanged();
                    mIvRight.setVisibility(View.VISIBLE);

                } else {
                    if(blackAdapter.getShowDeleteIcon()){
                        deleteList.add(blackList.get(position));
                        blackList.remove(position);
                        blackAdapter.notifyDataSetChanged();
                    } else {
                        Intent intent = new Intent(getActivity(), OtherInfoActivity.class);
                        intent.putExtra(Constants.UID, blackList.get(position).user.userid);
                        getActivity().startActivity(intent);
                    }
                }
            }
        });
//        tvRight.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);
        mIvRight.setOnClickListener(this);
    }

    private void startAddBlackActivity() {
        int type = getIntent().getIntExtra(Constants.SECRET_SET_TYPE,0);
        Intent intent = new Intent(this, BlackSelectTypeActivity.class);
        intent.putExtra(Constants.SECRET_SET_TYPE, type);
        startActivity(intent);
    }

    @Override
    public void showDatas(ArrayList<GroupSearchUser> datas, int isMoreData) {
        /*for(int i=0; i<13; i++){
            BlackEntity blackEntity = new BlackEntity();
            blackEntity.setHeadPic("http://p3.iaround.com/201612/13/FACE/f7d6ac79b0bf46f93f964d263dbd9f35.jpg");
            blackEntity.setNickname("本来就很帅"+i);
            blackEntity.setUid(123456);
            blackList.add(blackEntity);
        }*/
        blackList.clear();
        blackList.addAll(datas);
        blackAdapter = new BlackEditGVAdapter(this,blackList);
        gvBlackList.setAdapter(blackAdapter);
    }

    @Override
    public void deleteComplete() {
        hideWaitDialog();
        blackAdapter.setShowDeleteIcon(false);
        blackAdapter.notifyDataSetChanged();
        mIvRight.setVisibility(View.GONE);
        deleteList.clear();
    }

    @Override
    public void addComplete() {

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            dofinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回键的操作
     */
    private void dofinish()
    {
        if (deleteList.isEmpty())
        {
            finish();
        }else
            DialogUtil.showCancelUploadDialog( mContext , getString( R.string.dialog_title ) ,
                    getString( R.string.sure_cancel_setting ) , new View.OnClickListener( )
                    {
                        @Override
                        public void onClick( View v )
                        {
                            finish( );
                        }
                    } );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
            case R.id.fl_left:
                dofinish();
                break;
            case R.id.fl_right:
            case R.id.iv_right://标题栏右侧按钮
                // TODO: 2017/4/22 提交更新数据
                StringBuilder stringBuilder = new StringBuilder();
                if(deleteList!=null & deleteList.size()>0)
                {
                    stringBuilder.append(deleteList.get(0).user.userid);
                    for(int i=1;i<deleteList.size();i++)
                    {
                        stringBuilder.append(","+deleteList.get(i).user.userid);
                    }
                    presenter.deleteConfirm(setType, stringBuilder.toString());
                } else
                {
                    deleteComplete();
                }
                break;
        }
    }

    /**
     * 由于Activity被设置成了singleTask，要想通过getIntent获取数据，需要复写以下方法
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
