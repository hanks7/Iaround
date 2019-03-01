package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.model.entity.DynamicLoveBean;
import net.iaround.model.entity.GreetListBack;
import net.iaround.model.entity.GreetListItemBean;
import net.iaround.model.im.DynamicLoveInfo.LoverUser;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.UserLikeAdapter;

import java.util.ArrayList;

/**
 * Class: 点赞列表
 * Author：gh
 * Date: 2017/1/17 12:09
 * Email：jt_gaohang@163.com
 */
public class UserLikeListActivity extends ActionBarActivity implements HttpCallBack,View.OnClickListener{

    private static final String LIKE_DYNAMID_ID = "like_dynamic_id";
    private static final String LIKE_DYNAMID_COUMT = "like_dynamic_count";
    private static final String LIKE_DYNAMID = "like_dynamic";

    /** 协议标识 */
    private static long GREET_MEMBER_FLAG;

    private PullToRefreshListView likeListView;
    private UserLikeAdapter adapter;

    private TextView tvTitle;

    private ArrayList<GreetListItemBean> dataList;// 数据list

    private long dynamicId;

    private int currentPage = 1;// 当前请求服务端的页数
    private final int PAGE_SIZE = 10;// 每一页请求的数量
    private int totalPage;// 总共页数

    // msg.what
    private static final int MSG_UPDATE = 1;
    private static final int MSG_SHOW_TOAST = 2;
    private static final int MSG_COMPLETE = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_COMPLETE:
                    likeListView.onRefreshComplete();
                    break;
                case MSG_UPDATE:
                    refreshUI();
                    break;
                case MSG_SHOW_TOAST:
                    showToast( ( String ) msg.obj );
                    break;
            }
        }
    };

    /**
     * 跳转到点赞成员
     *
     * @param mContext
     * @param dynamicId
     * @param count
     */
    public static void skipToUserLike(Context mContext, long dynamicId, int count, ArrayList<DynamicLoveBean> list) {
        Intent intent = new Intent();
        intent.setClass(mContext, UserLikeListActivity.class);
        intent.putExtra(LIKE_DYNAMID_ID, dynamicId);
        intent.putExtra(LIKE_DYNAMID_COUMT, count);
        intent.putParcelableArrayListExtra(LIKE_DYNAMID, list);
        mContext.startActivity(intent);
    }

    public static void skipToUserLikeNew(Context mContext, long dynamicId, int count, ArrayList<LoverUser> list) {
        Intent intent = new Intent();
        intent.setClass(mContext, UserLikeListActivity.class);
        intent.putExtra(LIKE_DYNAMID_ID, dynamicId);
        intent.putExtra(LIKE_DYNAMID_COUMT, count);

        intent.putExtra(LIKE_DYNAMID, list);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_like_list);
//        setActionBarTitle(R.string.dynamic_like_title);

        initView();
        initData();
        requestData();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.dynamic_like_title);

        likeListView = (PullToRefreshListView) findViewById(R.id.pull_user_like);
        likeListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        likeListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        likeListView.getRefreshableView().setFastScrollEnabled(false);

        likeListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (currentPage <= totalPage) {
                    requestData();
                } else {
                    CommonFunction.toastMsg(mContext, R.string.no_more);
                    mHandler.sendEmptyMessage(MSG_COMPLETE);
                }
            }
        });

        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);

    }


    private void initData() {
        dynamicId = getIntent().getLongExtra(LIKE_DYNAMID_ID, 0);
        dataList = new ArrayList<GreetListItemBean>();
        adapter = new UserLikeAdapter(UserLikeListActivity.this, dataList);
        likeListView.getRefreshableView().setAdapter(adapter);
        likeListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

    }

    /**
     * 请求好友点赞列表数据
     */
    private long protocolReq() {
        return DynamicHttpProtocol.getUserDynamicGreetList(mContext,
                    dynamicId, currentPage, PAGE_SIZE, this);
    }

    private void requestData() {
        showWaitDialog();

        GREET_MEMBER_FLAG = protocolReq();
        if (GREET_MEMBER_FLAG == -1) {
            // 失败
            likeListView.onRefreshComplete();

            Message msg = new Message();
            msg.what = MSG_SHOW_TOAST;
            msg.obj = ErrorCode.getErrorMessageId(ErrorCode.E_104);
            mHandler.sendMessage(msg);
        }
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        GreetListBack bean = GsonUtil.getInstance().getServerBean(result,
                GreetListBack.class);
        if (flag == GREET_MEMBER_FLAG) {
            if (bean.isSuccess()) {
                if(bean.getLikeuserList() != null && bean.getLikeuserList().size( ) >0)
                {
                    dataList.addAll(bean.getLikeuserList());
                }
                currentPage = bean.pageno + 1;
                totalPage = (bean.amount / bean.pagesize)
                        + ((bean.amount % bean.pagesize) > 0 ? 1 : 0);
                mHandler.sendEmptyMessage(MSG_UPDATE);
            } else {
                ErrorCode.showError(mContext, result);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        hideWaitDialog();
        likeListView.onRefreshComplete();
        if (flag == GREET_MEMBER_FLAG) {
            // 失败
            ErrorCode.toastError(mContext, e);
        }
    }

    private void showToast( String content )
    {
        CommonFunction.showToast( mContext , content , Toast.LENGTH_SHORT );
    }

    private void refreshUI() {
        adapter.notifyDataSetChanged();
        likeListView.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_left:
            case R.id.fl_left:
                finish();
                break;
        }
    }
}
