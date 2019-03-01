package net.iaround.ui.seach.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.datamodel.GroupHistoryModel;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.group.bean.GroupHistoryBean;
import net.iaround.ui.seach.GroupListBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 搜索聊吧
 * Created by gh on 2017/11/3.
 */

public class SearchChatBarActivity extends BaseActivity implements HttpCallBack {

    private RelativeLayout recomentView;
    private RecyclerView recomentCy;
    private EditText searchEdit;
    private PullToRefreshListView mListView;
    private EmptyLayout mEmptyLayou;

    private SearchChatBarAdapter mAdapter;

    private String mKeyWord;

    private ArrayList< Group > mDataList = new ArrayList< Group >( );

    private long FLAG_SEARCH_GROUP;
    private int mCurPage = 0;
    private int mTotalPage;
    private int PAGE_SIZE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chatbar);

        initViews();
        initData();

    }

    private void initViews(){
        recomentView = (RelativeLayout)findViewById(R.id.ly_recoment_keword);
        recomentCy = (RecyclerView)findViewById(R.id.rv_searh_chatbar_recoment);

        searchEdit = (EditText) findViewById(R.id.et_seach_seach);
        mListView = (PullToRefreshListView) findViewById(R.id.group_listview);
        mListView.setMode( PullToRefreshBase.Mode.PULL_FROM_END );

        mEmptyLayou = new EmptyLayout( SearchChatBarActivity.this , mListView.getRefreshableView( ) );
        mEmptyLayou.setEmptyMessage( getResources().getString( R.string.empty_search_group ) );

        recomentCy.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recomentCy.setHasFixedSize(true);

        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTv = (TextView)findViewById(R.id.tv_title);
        titleTv.setText(getString(R.string.nearby_search_table_group));

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
                        recomentView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                    }else{
                        CommonFunction.toastMsg(SearchChatBarActivity.this,getResString(R.string.contacts_seach_keyword_hint));
                    }

                    return true;
                }
                return false;
            }
        } );

        mListView.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2<ListView>( )
        {

            @Override
            public void onPullDownToRefresh( PullToRefreshBase< ListView > refreshView )
            {
//                initData( );
            }

            @Override
            public void onPullUpToRefresh( PullToRefreshBase< ListView > refreshView )
            {
                if ( mCurPage < mTotalPage )
                {
                    requestPageData( mCurPage + 1 );
                }
                else
                {
                    mListView.postDelayed( new Runnable( )
                    {
                        public void run( )
                        {
                            mListView.onRefreshComplete( );
                        }
                    } , 200 );
                }
            }
        } );
        mListView.setOnItemClickListener( new AdapterView.OnItemClickListener( )
        {

            @Override
            public void onItemClick(AdapterView< ? > arg0 , View arg1 , int position ,
                                    long arg3 )
            {
                Group group = null;
                try
                {
                    group = mDataList.get( position - 1 );
                }
                catch ( Exception e )
                {
                    e.printStackTrace( );
                }
                if ( group != null )
                {
                    GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                    if(old!=null){
                        old.isGroupIn = true;
                        old.finish();
                    }
                    Intent intent = new Intent(SearchChatBarActivity.this, GroupChatTopicActivity.class);
                    intent.putExtra("id", group.id + "");
                    intent.putExtra("icon", group.icon);
                    intent.putExtra("name", group.name);
                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
                    intent.putExtra("isChat", true);
//                    startActivity(intent);
                    GroupChatTopicActivity.ToGroupChatTopicActivity(SearchChatBarActivity.this,intent);
                }
            }
        } );

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

    private void initData(){

        List<GroupHistoryBean> groupHistoryBeanList = GroupHistoryModel.getInstance().getGroupHistoryList(getActivity());
        if (groupHistoryBeanList.size() > 8) {
            GroupHistoryModel.getInstance().deleteUsed(getActivity(), groupHistoryBeanList.size() - 7);
        }
        Collections.sort( groupHistoryBeanList , comparator );
        SearchChatBarHistoryAdapter historyAdapter = new SearchChatBarHistoryAdapter(this,R.layout.item_seach_chatbar_history,groupHistoryBeanList);

        historyAdapter.setOnItemClickListener(new SearchChatBarHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GroupHistoryBean bean) {
                GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                if(old!=null){
                    old.isGroupIn = true;
                    old.finish();
                }
                Intent intent = new Intent(SearchChatBarActivity.this, GroupChatTopicActivity.class);
                intent.putExtra("id", bean.groupId + "");
                intent.putExtra("isChat", true);
//                startActivity(intent);
                GroupChatTopicActivity.ToGroupChatTopicActivity(SearchChatBarActivity.this,intent);
            }
        });


        recomentCy.setAdapter(historyAdapter);

        mAdapter = new SearchChatBarAdapter(this,mDataList);
        mListView.setAdapter( mAdapter );

        mCurPage = 0;
        mKeyWord = getIntent().getStringExtra( "keyword" );

        if (mKeyWord != null && !TextUtils.isEmpty(mKeyWord)){
            if (mCurPage == 0) {
                showWaitDialog();
            }
            mCurPage = 0;
            requestPageData(mCurPage + 1);
            searchEdit.setText(mKeyWord);
            searchEdit.setSelection(mKeyWord.length());
            recomentView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }else{
            if (groupHistoryBeanList.size() <= 0){
                recomentView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
            showSoftInputFromWindow(getActivity());
        }

    }

    private void requestPageData( int pageIndex )
    {
//        showWaitDialog( );
        int lat = 0;
        int lng = 0;
        if ( LocationUtil.getCurrentGeo( BaseApplication.appContext ) != null )
        {
            lat = LocationUtil.getCurrentGeo(BaseApplication.appContext).getLat( );
            lng = LocationUtil.getCurrentGeo( BaseApplication.appContext ).getLng( );
        }
        FLAG_SEARCH_GROUP = GroupHttpProtocol.searchGroup( this , lat , lng , mKeyWord ,
                "" , 0 , 0 , 0 , pageIndex , PAGE_SIZE , this );
        if ( FLAG_SEARCH_GROUP < 0 )
        {
            hideWaitDialog( );
            onGeneralError( 107 , FLAG_SEARCH_GROUP );
        }
    }



    //浏览历史聊吧比较
    private Comparator<GroupHistoryBean> comparator = new Comparator<GroupHistoryBean>() {
        @Override
        public int compare(GroupHistoryBean p1 , GroupHistoryBean p2 ) {
            if( p1.time < p2.time ){
                return 1 ;  //正数
            }else if ( p1.time > p2.time) {
                return -1 ;  //负数
            }else {
                return 0;  //相等为0
            }
        }
    };


    @Override
    public void onGeneralSuccess(String result, long flag) {
        if ( flag == FLAG_SEARCH_GROUP )
        {
            hideWaitDialog( );
            mListView.onRefreshComplete( );
            GroupListBean mResultBean = GsonUtil.getInstance( ).getServerBean( result , GroupListBean.class );
            if ( mResultBean != null )
            {
                if ( mResultBean.isSuccess( ) )
                {
                    if ( mCurPage == 0 )
                    {
                        mDataList.clear( );
                    }
                    mCurPage = mResultBean.pageno;
                    mTotalPage = mResultBean.amount / PAGE_SIZE;
                    if ( mResultBean.amount % PAGE_SIZE > 0 )
                    {
                        mTotalPage++;
                    }

                    if ( mResultBean.groups != null )
                    {
                        int length = mResultBean.groups.size();
                        int distance;
                        GeoData mCurrentGeo = LocationUtil
                                .getCurrentGeo( this );
                        if (length == 1) {
                            for ( Group group : mResultBean.groups )
                            {
                                distance = LocationUtil.calculateDistance( mCurrentGeo.getLng( ) ,
                                        mCurrentGeo.getLat( ) , group.lng , group.lat );
                                group.rang = distance;
                                group.isShowDivider = 0;
                            }
                        }
                        else if (length > 1) {
                            for (int i = 0; i < length; i++) {
                                Group group = mResultBean.groups.get( i );
                                if ( i < (length - 1 ) ) {
                                    group.isShowDivider = 1;
                                }
                                else {
                                    group.isShowDivider = 0;
                                }
                                distance = LocationUtil.calculateDistance( mCurrentGeo.getLng( ) ,
                                        mCurrentGeo.getLat( ) , group.lng , group.lat );
                                group.rang = distance;
                            }
                        }

                        mDataList.addAll( mResultBean.groups );
                    }

                    mAdapter.updateData(mKeyWord, mDataList);
                    if ( mDataList.isEmpty( ) )
                    {
                        // 显示空布局
                        mEmptyLayou.showEmpty( );
                    }
                }
                else
                {
                    onGeneralError( mResultBean.error , flag );
                }
            }
            else
            {
                onGeneralError( 107 , flag );
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if ( flag == FLAG_SEARCH_GROUP )
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
}
