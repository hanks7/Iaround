package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.api.share.Base;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.ChatbarFansBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.ChatbarFansAdpater;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;
import java.util.List;


public class ChatbarFansActivity extends SuperActivity implements View.OnClickListener{

    /**标题栏**/
    private TextView mTvTitle;
    private FrameLayout mFlLeft;
    private ImageView mIvLeft;
    /**点赞列表*/
    private PullToRefreshListView mLvGreet;
    private ChatbarFansAdpater mAdatper;
    /**数据集合**/
    private List<ChatbarFansBean.FansBean> msgs;
    /**空布局**/
    private RelativeLayout llEmptyLayout;
    private ImageView ivEmpty;
    private TextView tvEmpty;
    /**圈子id**/
    private long groupid;
    /**关注的总数量**/
    private long amount;
    /**页数**/
    private int pageno = 1;
    /**每页展示数量**/
    private final int PAGE_SIZE = 10;
    /**总页数**/
    private int mTotalPage;
    /**时间**/
    private long time = 0;
    /**user**/
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbar_fans);

        msgs = new ArrayList<>();
        initView();
        initData();
        initListener();
    }

    private void initView()
    {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mFlLeft = (FrameLayout) findViewById(R.id.fl_left);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mLvGreet = (PullToRefreshListView) findViewById(R.id.ptrflvGreetList);
        mAdatper = new ChatbarFansAdpater(ChatbarFansActivity.this,null);
        llEmptyLayout = (RelativeLayout) findViewById(R.id.empty);

        mTvTitle.setText(R.string.fans_activity);
    }
    private void initListener()
    {
        mFlLeft.setOnClickListener(this);
        mIvLeft.setOnClickListener(this);
        mLvGreet.getRefreshableView().setAdapter(mAdatper);
        mLvGreet.setMode(PullToRefreshBase.Mode.BOTH);
        mLvGreet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatbarFansBean.FansBean itemBean = msgs.get(position - 1);
                if (itemBean.getUser().getUserid() == Common.getInstance().loginUser.getUid())
                {
                    Intent intent = new Intent(ChatbarFansActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                }else
                {
                    convertToUser(itemBean);
                    Intent intent = new Intent(ChatbarFansActivity.this, OtherInfoActivity.class);
                    intent.putExtra("user",user);
                    intent.putExtra(Constants.UID,user.getUid());
                    startActivity(intent);
                }
            }
        });

        mLvGreet.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageno = 1;
                reqFansData(pageno);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (pageno < mTotalPage)
                {
                    ++pageno;
                    reqFansData(pageno);
                }else
                {
                    mLvGreet.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLvGreet.onRefreshComplete();
                            CommonFunction.toastMsg(ChatbarFansActivity.this, R.string.no_more_data);
                        }
                    },200);
                }
            }
        });
    }
    private void initData()
    {
        Intent intent = getIntent();
        groupid = Long.valueOf(intent.getStringExtra("group_id"));
        reqFansData(pageno);
    }

    private void reqFansData(int pageno)
    {
//        FriendHttpProtocol.userAttentions(BaseApplication.appContext, Common.getInstance().loginUser.getUid()
//                ,pageno, PAGE_SIZE,httpCallBack);
        GroupHttpProtocol.getChatbarFansList(BaseApplication.appContext,pageno,PAGE_SIZE,groupid,httpCallBack);
    }

    /**
     * 请求回调
     */
    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            if (result == null)
                return;
            mLvGreet.onRefreshComplete();
            ChatbarFansBean bean = GsonUtil.getInstance().getServerBean(result,ChatbarFansBean.class);
            handleFansListData(result,bean);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            llEmptyLayout.setVisibility(View.VISIBLE);
            ErrorCode.toastError(ChatbarFansActivity.this,e);
        }
    };

    /**
     * 处理请求数据
     * @param result
     * @param bean
     */
    private void handleFansListData(String result,ChatbarFansBean bean)
    {
        if (bean == null)
            return;
        if (bean.isSuccess())
        {
            if (bean.getFans().size() == 0) {
                llEmptyLayout.setVisibility(View.VISIBLE);
                mLvGreet.setVisibility(View.GONE);
            }else
            {
                llEmptyLayout.setVisibility(View.GONE);
                pageno = bean.getPageno();
                amount = bean.getAmount();

                if (pageno == 1)
                {
                    pageno = 1;
                    msgs.clear();
                }
                if (amount % PAGE_SIZE == 0)
                {
                    mTotalPage = (int) amount / PAGE_SIZE;
                }else
                {
                    mTotalPage = (int) amount / PAGE_SIZE + 1;
                }
                msgs.addAll(bean.getFans());
                mAdatper.updateData(msgs);
            }
        }
        if (bean.status == -100)
        {
            ErrorCode.showError(ChatbarFansActivity.this,result);
        }
    }

    /**
     * 将UserBean转换为User
     */
    private void convertToUser(ChatbarFansBean.FansBean userBean)
    {
        if (userBean == null)
            return;
        if (user == null)
            user = new User();
        user.setIcon(userBean.getUser().getIcon());
        user.setUid(userBean.getUser().getUserid());
        user.setAge(userBean.getUser().getAge());
        user.setSex("m".equals(userBean.getUser().getGender()) ? 1 : 2);
        user.setSVip(userBean.getUser().getSvip());
        user.setRelationship(userBean.getRelation());
        user.setNickname(userBean.getUser().getNickname());
        user.setDistance(userBean.getDistance());
        user.setViplevel(userBean.getUser().getViplevel());
    }
    @Override
    public void onClick(View v) {
        if (v == mFlLeft || v == mIvLeft)
        {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return true;
    }
}
