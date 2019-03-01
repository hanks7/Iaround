package net.iaround.ui.group.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.group.adapter.ChatbarTransferAdater;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatbarTransferActivity extends GroupHandleActivity implements View.OnClickListener{


    private ImageView ivLeft;
    private FrameLayout flLeft;
    private TextView tvTitle;
    private LinearLayout rlRight;
    private TextView tvRight;
    private RelativeLayout rlActionbar;
    private PullToRefreshListView memberListview;

    /***聊吧id*/
    private String mGroupid;
    /**获取成员列表*/
    private long FLAG_GET_MEMBER_LIST;
    /**转让的flag**/
    private long FLAG_TRANSFER_CHATBRA;
    /** 每页数 */
    private int PAGE_SIZE = 20;
    /** 当前页数 */
    private int mCurrentPage = 1;
    /** 总页数 */
    private int mTotalPage = 0;
    /**成员bean*/
    private GroupMemberSearchBean mGroupUserBean;
    /**数据集合*/
    private List<GroupSearchUser> mMemberList;
    private List<Boolean > selectUserStatus ;
    private ChatbarTransferAdater mAdapter;

    private long uid = 0;
    /**被转让的用户*/
    private GroupSearchUser user = new GroupSearchUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbar_transfer);

        mMemberList = new ArrayList<>();
        selectUserStatus = new ArrayList<>();

        initView();
        initData();
        initListener();

    }

    private void initView()
    {

        ivLeft = (ImageView) findViewById(R.id.iv_left);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        rlRight = (LinearLayout) findViewById(R.id.fl_right);
        tvRight = (TextView) findViewById(R.id.tv_right);
        memberListview = (PullToRefreshListView) findViewById(R.id.member_listview);

        tvTitle.setText(R.string.group_info_item_member);
        tvRight.setText(R.string.ok);
        tvRight.setTextColor(getResColor(R.color.login_btn));
        tvRight.setVisibility(View.VISIBLE);

        memberListview.setMode( PullToRefreshBase.Mode.BOTH );
        memberListview.getRefreshableView( ).setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        memberListview.getRefreshableView( ).setDescendantFocusability(
                ListView.FOCUS_BLOCK_DESCENDANTS );
        mAdapter = new ChatbarTransferAdater(ChatbarTransferActivity.this, mMemberList, selectUserStatus, new ChatbarTransferAdater.SeletUserToTransgerCallback() {
            @Override
            public void setSelectUser(GroupSearchUser selectUser) {
                if (selectUser != null)
                    user = selectUser;
            }
        });
        memberListview.setAdapter(mAdapter);
    }

    private void initData()
    {
        mGroupid = getIntent().getStringExtra("groupId");
        reqMemberData(mCurrentPage);
    }

    private void reqMemberData(int pageno)
    {
        FLAG_GET_MEMBER_LIST = GroupHttpProtocol.groupMember( mContext , mGroupid , pageno ,
                PAGE_SIZE , httpCallBack );
    }

    private void initListener()
    {
        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        rlRight.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        memberListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 1;
                reqMemberData(mCurrentPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mCurrentPage < mTotalPage)
                {
                    ++mCurrentPage;
                    reqMemberData(mCurrentPage);
                }else
                {
                    memberListview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        memberListview.onRefreshComplete();
                        CommonFunction.toastMsg(ChatbarTransferActivity.this, R.string.no_more_data);

                    }
                    }, 200);
                }
            }
        });

    }
    @Override
    protected String getGroupId() {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == ivLeft || v == flLeft)
        {
            finish();
        }else if (v == rlRight || v == tvRight)
        {
            CommonFunction.toastMsg(ChatbarTransferActivity.this,"转让圈子");
            if (user == null)
                return;
            DialogUtil
                    .showOKCancelDialog(
                            mContext,
                            getResString(R.string.group_inf_group_transfer_title),
                            user.user.nickname,true, new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated
                                    String uid = String
                                            .valueOf(user.user.userid);
                                    FLAG_TRANSFER_CHATBRA = GroupHttpProtocol.transferGroup(ChatbarTransferActivity.this,
                                            mGroupid, uid, httpCallBack);
                                }
                            });
        }
    }

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            if (result == null)
                return;
            memberListview.onRefreshComplete();
            handleDataSuccess(result,flag);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            ErrorCode.toastError(ChatbarTransferActivity.this,e);
        }
    };

    public String getColor()
    {
        return "#FF4064";
    }
    /**
     * 处理成功数据
     */
    private void handleDataSuccess(String result,long flag) {
        if (flag == FLAG_GET_MEMBER_LIST)
        {
            mGroupUserBean =  GsonUtil.getInstance( ).getServerBean(
                    result , GroupMemberSearchBean.class );
            if ( mGroupUserBean == null )
                return;
            if ( mGroupUserBean.isSuccess( ) )
            {
                if ( mCurrentPage == 1 )
                {
                    mMemberList.clear( );
                }
                mCurrentPage = mGroupUserBean.pageno;
                mTotalPage = mGroupUserBean.amount / PAGE_SIZE;

                mMemberList.addAll(mGroupUserBean.users);

                if (mGroupUserBean.users != null  && mGroupUserBean.users.size() > 0)
                {
                    for (int i = 0; i < mGroupUserBean.users.size() ; i++) {
                        selectUserStatus.add(false);
                    }
                }

                mAdapter.updateUserData(mMemberList,selectUserStatus);
            }
            else
            {
                ErrorCode.showError( mContext , result );
                memberListview.onRefreshComplete( );
            }
        }else if (flag == FLAG_TRANSFER_CHATBRA)
        {
            // TODO: 2017/8/28 转让聊吧
            try
            {
                JSONObject json = new JSONObject( result );

                if ( json.optInt( "status" ) == 200 )
                {
                    DialogUtil.showOKDialog( mContext , getResString( R.string.prompt ) ,
                            getResString( R.string.group_inf_group_transfer_server_back ) ,
                            null );
                }
                else if ( result.contains( "error" ) )
                {
                    ErrorCode.showError( mContext , result );
                }

            }
            catch ( Exception e )
            {
                // TODO: handle exception
            }
        }
    }
}
