package net.iaround.ui.seach.news;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;

import java.util.ArrayList;

/**
 * 搜索用户
 * Created by gh on 2017/11/3.
 */

public class SearchUserActivity extends BaseActivity implements HttpCallBack {

    private EditText searchEdit;
    private PullToRefreshListView mListView;
    private EmptyLayout mEmptyLayou;

    private SearchUserAdapter mAdapter;
    private ArrayList<GroupSearchUser> mUserList = new ArrayList<GroupSearchUser>();

    private long FLAG_SEARCH_USER = -1;
    private int PAGE_SIZE = 20;
    private int mCurPage = 0;
    private int mTotalPage = 0;

    private String mKeyWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        initViews();
        initData();

    }

    private void initViews() {
        searchEdit = (EditText) findViewById(R.id.et_seach_seach);
        mListView = (PullToRefreshListView) findViewById(R.id.user_listview);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setFastScrollEnabled(false);
        mListView.setMode( PullToRefreshBase.Mode.PULL_FROM_END );

        mEmptyLayou = new EmptyLayout( SearchUserActivity.this , mListView.getRefreshableView( ) );
        mEmptyLayou.setEmptyMessage( getResources().getString( R.string.can_not_find_any_user ) );

        TextView titleTv = (TextView)findViewById(R.id.tv_title);
        titleTv.setText(getString(R.string.nearby_search_table_user));

        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEdit.setOnEditorActionListener( new TextView.OnEditorActionListener( )
        {
            @Override
            public boolean onEditorAction(TextView v , int actionId , KeyEvent event )
            {
                // TODO Auto-generated method stub
                if ( actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_UNSPECIFIED )
                {
                    mKeyWord = searchEdit.getText().toString();
                    if (!TextUtils.isEmpty(mKeyWord)){
                        mCurPage = 0;
                        requestPageData(mCurPage + 1);
                        showWaitDialog();
                    }else{
                        CommonFunction.toastMsg(SearchUserActivity.this,getResString(R.string.contacts_seach_keyword_hint));
                    }

                    return true;
                }
                return false;
            }
        } );

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurPage = 0;
                requestPageData(mCurPage + 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mCurPage < mTotalPage) {
                    requestPageData(mCurPage + 1);
                } else {
                    mListView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mListView.onRefreshComplete();
                        }
                    }, 200);
                }
            }
        });

        mEmptyLayou.setEmptyButtonClickListener( new View.OnClickListener( )
        {

            @Override
            public void onClick( View v )
            {
                mCurPage = 0;
                requestPageData(mCurPage + 1);
                showWaitDialog();
            }
        } );
        mEmptyLayou.setErrorButtonClickListener( new View.OnClickListener( )
        {

            @Override
            public void onClick( View v )
            {
                mCurPage = 0;
                requestPageData(mCurPage + 1);
                showWaitDialog();
            }
        } );
    }

    private void initData() {

        mKeyWord = getIntent().getStringExtra( "keyword" );
        mAdapter = new SearchUserAdapter(this);
        mAdapter.notifyData(mKeyWord,mUserList);
        mListView.setAdapter(mAdapter);

        if (mKeyWord != null && !TextUtils.isEmpty(mKeyWord)){
            if (mCurPage == 0) {
                showWaitDialog();
            }
            mCurPage = 0;
            requestPageData(mCurPage + 1);
            searchEdit.setText(mKeyWord);
            searchEdit.setSelection(mKeyWord.length());
        }else{
            showSoftInputFromWindow(getActivity());
        }

    }


    private void requestPageData(int pageIndex) {

        FLAG_SEARCH_USER = BusinessHttpProtocol.userSearch(SearchUserActivity.this, mKeyWord,
                pageIndex, PAGE_SIZE, this);
        if (FLAG_SEARCH_USER < 0) {
            handleDataFail(107, FLAG_SEARCH_USER);
        }
    }

    protected void handleDataFail(int e, long flag) {
        if (flag == FLAG_SEARCH_USER) {
            hideWaitDialog();
            mListView.onRefreshComplete();
            ErrorCode.toastError(SearchUserActivity.this, e);
            if ( mCurPage == 0 )
            {
                mEmptyLayou.showError( );
            }
        }
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == FLAG_SEARCH_USER) {
            hideWaitDialog();
            mListView.onRefreshComplete();
            GroupMemberSearchBean mUserBean = GsonUtil.getInstance().getServerBean(result,
                    GroupMemberSearchBean.class);
            if (mUserBean != null) {
                if (mUserBean.isSuccess()) {
                    if (mCurPage == 0) {
                        mUserList.clear();
                    }
                    mCurPage = mUserBean.pageno;
                    mTotalPage = mUserBean.amount / PAGE_SIZE;
                    if (mUserBean.amount % PAGE_SIZE > 0) {
                        mTotalPage++;
                    }
                    if (mUserBean.users != null) {
                        mUserList.addAll(mUserBean.users);
                    }
                    mAdapter.notifyData(mKeyWord,mUserList);
                    if (mUserList.isEmpty()) {
                        mEmptyLayou.showEmpty();
                    }
                } else {
                    handleDataFail(mUserBean.error, flag);
                }
            } else {
                handleDataFail(107, flag);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if ( flag == FLAG_SEARCH_USER )
        {
            hideWaitDialog( );
            mListView.onRefreshComplete( );
            ErrorCode.toastError( this , e );
            if ( mCurPage == 0 )
            {
                mEmptyLayou.showError( );
            }
        }
    }

    /**
     //     * EditText获取焦点并显示软键盘
     //     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        if (activity == null)return;
        if (editText == null)return;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
