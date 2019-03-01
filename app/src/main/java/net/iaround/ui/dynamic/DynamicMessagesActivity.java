
package net.iaround.ui.dynamic;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.model.entity.ReviewsListServerBean;
import net.iaround.model.entity.ReviewsListServerBean.ReviewsItem;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.dynamic.adapter.DynamicMessagesAdapter;
import net.iaround.ui.dynamic.bean.DynamicMessagesItemBean;

import java.util.ArrayList;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-17 下午7:36:51
 * @Description: 动态消息
 * @Notice:[显示规则]<br> 新点赞, 历史点赞, 新评论, 历史评论<br>
 * 1.[新点赞] + [新评论]<br>
 * 显示点赞View + 显示评论View + [点击查看更多]<br>
 * <br>
 * 2.[新点赞] + [历史评论]<br>
 * 显示点赞View + 无 + 上拉刷新更多<br>
 * <br>
 * 3.[历史点赞] + [新评论]<br>
 * 显示点赞View + 显示评论View + [点击查看更多]/[共XX条](根据是否最后一页)<br>
 * <br>
 * 4.[历史点赞] + [历史评论]<br>
 * 显示点赞View + 显示评论View + 上拉刷新更多/[共XX条](根据是否最后一页)<br>
 */
public class DynamicMessagesActivity extends BaseFragmentActivity implements OnClickListener, HttpCallBack {

    /**
     * TitleLayoutDynamicMessageActivityni
     */
    private TextView titleName;
    private TextView titleRight;
    private ImageView titleLeft;

    private long mGetUnreadCommentFlag;// 获取未读评论的flag
    private long mGetCommentHistoryFlag;// 获取历史评论flag
    private long mClearAllFlag;// 清空所有flag
    private long mGetUnreadGreeterListFlag;// 获取未读点赞flag
    private long mGetGreeterHistoryListFlag;// 获取历史点赞flag

    private Dialog mProgressDialog;
    private PullToRefreshListView listView;
    private EmptyLayout empty;

    private DynamicMessagesAdapter adapter;
    private ArrayList<ReviewsItem> dataList;
    private ReviewsListServerBean mUnreadData;

    private DynamicMessagesItemBean greeterItemBean;
    private ArrayList<DynamicMessagesItemBean> reviews = new ArrayList<DynamicMessagesItemBean>();
    private DynamicMessagesItemBean moreItemBean;

    private ArrayList<DynamicMessagesItemBean> mDataList = new ArrayList<DynamicMessagesItemBean>();

    private int mPageNo = 1;// 历史评论的请求第几页
    private int mPageSize = 20;// 每页的请求数量
    private int mTotalPage = 1;// 总共的页数
    private long mLastTime = 0;// 最后一条的时间
    private boolean isGetNewGreeter = false;// 是否获取的是未读点赞
    private boolean isGetNewComment = false;// 是否获取的是未读评论

    private int requestCount = 0;// 用来判断是否所有的请求都收到回复
    private boolean isGetDataFail = false;// 是否所有请求都失败

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dynamic_messages);

        initView();
        initData();
    }

    // 初始化布局
    private void initView() {
        titleName = (TextView) findViewById(R.id.tv_title);
        titleName.setText(R.string.dynamic_message_list);

        titleRight = (TextView) findViewById(R.id.tv_right);
        titleRight.setText(R.string.empty);
        titleRight.setOnClickListener(this);

        titleLeft = (ImageView) findViewById(R.id.iv_left);
        titleLeft.setImageResource(R.drawable.title_back);
        findViewById(R.id.fl_left).setOnClickListener(this);
        titleLeft.setOnClickListener(this);

        mProgressDialog = DialogUtil.showProgressDialog(mActivity, "", BaseApplication.appContext
                .getResources().getString(R.string.please_wait), null);

        listView = (PullToRefreshListView) findViewById(R.id.ptrlvMessagesList);
        listView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        listView.getRefreshableView().setFastScrollEnabled(false);
        listView.setMode(Mode.DISABLED);

        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mPageNo > mTotalPage) {
                    CommonFunction.toastMsg(mContext, R.string.no_more_data);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                            listView.setMode(Mode.DISABLED);
                        }
                    }, 500);
                    refreshData();
                } else {
                    requestHistoryComment();
                }
            }
        });

        moreItemBean = new DynamicMessagesItemBean();
        moreItemBean.setItemType(DynamicMessagesItemBean.FOOTER_TYPE);
        moreItemBean.setHasMore(true);

        empty = new EmptyLayout(mContext, listView.getRefreshableView());
    }

    /**
     * 点赞的头像的点击事件
     *
     * @param view
     */
    public void OnGreeterIconClick(View view) {

        User user = (User) view.getTag();
//		SpaceOther.launchUser( mContext , user.getUid( ) , user , ChatFromType.UNKONW );//jiqiang 查看个人资料未做
    }

    private View.OnClickListener moreOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            requestHistoryComment();
        }
    };

    private void initData() {

        DynamicNewNumberBean newNumBean = DynamicModel.getInstent().getNewNumBean();

        isGetNewComment = newNumBean != null && newNumBean.getCommentNum() > 0;
        isGetNewGreeter = newNumBean != null && newNumBean.getLikenum() > 0;

        if (isGetNewComment && isGetNewGreeter) {
            requestNewComment();
            requestNewGreeter();
        } else if (isGetNewComment) {
            requestHistoryGreeter();
            requestNewComment();
        } else if (isGetNewGreeter) {
            requestNewGreeter();
        } else {
            requestHistoryComment();
            requestHistoryGreeter();
        }

        empty.showLoading();
    }

    private void requestNewComment() {
        requestCount++;
        mGetUnreadCommentFlag = DynamicHttpProtocol.getUserUnreadCommentList(mContext, this);
    }

    private void requestHistoryComment() {
        requestCount++;
        mGetCommentHistoryFlag = DynamicHttpProtocol.getUserHistoryCommentList(mContext,
                mPageNo, mPageSize, mLastTime, this);
    }

    private void requestNewGreeter() {
        requestCount++;
        mGetUnreadGreeterListFlag = DynamicHttpProtocol.getUnreadGreeterList(mContext, this);
    }

    private void requestHistoryGreeter() {
        requestCount++;
        mGetGreeterHistoryListFlag = DynamicHttpProtocol.getGreeterHistoryList(mContext, 1,
                7, 0, this);
    }

    private void deleteAll() {
        mClearAllFlag = DynamicHttpProtocol.delAllCommentList(mContext, this);
    }

    private void refreshData() {

        if (requestCount > 0) {
            return;
        }

        mDataList.clear();

        // 添加点赞View的数据
        if (greeterItemBean != null && greeterItemBean.getGreeterList() != null
                && greeterItemBean.getGreeterList().size() > 0) {
            mDataList.add(greeterItemBean);
        }

        // 添加评论View的数据
        if (reviews != null && reviews.size() > 0) {
            mDataList.addAll(reviews);
        }

        // 添加点击更多View的数据
        if (moreItemBean != null) {

            if ((!isGetNewComment && mPageNo <= mTotalPage) || isGetDataFail
                    || reviews.size() <= 0) {
                listView.setMode(Mode.PULL_FROM_END);
            } else if (isGetNewComment) {
                isGetNewComment = false;
                moreItemBean.setHasMore(true);
                mDataList.add(moreItemBean);
            } else {
                if (mPageNo > mTotalPage) {
                    moreItemBean.setHasMore(false);
                    moreItemBean.setItemCount(reviews.size());
                    listView.setMode(Mode.DISABLED);
                    mDataList.add(moreItemBean);
                } else {
                    moreItemBean.setHasMore(true);
                    mDataList.add(moreItemBean);
                }
            }
        }

        if (adapter == null) {
            adapter = new DynamicMessagesAdapter(mContext, mDataList,
                    greeterOnClickListener, moreOnClickListener);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        if (mDataList.size() <= 0) {
            empty.showEmpty();
        }
    }

//	public void onGeneralSuccess(String result , long flag )
//	{
//		mProgressDialog.hide( );
//		if ( flag == mGetUnreadCommentFlag )
//		{
//			requestBack( true );
//			listView.onRefreshComplete( );
//			ReviewsListServerBean data = GsonUtil.getInstance( ).getServerBean( result ,
//					ReviewsListServerBean.class );
//
//			DynamicNewNumberBean newNumBean = DynamicModel.getInstent( ).getNewNumBean( );
//
//			if ( data.isSuccess( ) )
//			{
//				if ( newNumBean != null )
//					newNumBean.setNum( 0 );
//
//				if ( data.msgs != null )
//				{
//					dataList = data.msgs;
//					for ( int i = 0 ; i < dataList.size( ) ; i++ )
//					{
//						DynamicMessagesItemBean item = new DynamicMessagesItemBean( );
//						item.setItemType( DynamicMessagesItemBean.CONTENT_TYPE );
//						item.setReviewItem( dataList.get( i ) );
//						reviews.add( item );
//					}
//					mLastTime = dataList.get( dataList.size( ) - 1 ).msg.datetime;
//					DynamicModel.getInstent( ).setNewNumBean( null );
//				}
//				refreshData( );
//			}
//			else
//			{
//				empty.showEmpty( );
//				ErrorCode.showError( mContext , result );
//			}
//		}
//		else if ( flag == mGetCommentHistoryFlag )
//		{
//			requestBack( true );
//			listView.onRefreshComplete( );
//			ReviewsListServerBean data = GsonUtil.getInstance( ).getServerBean( result ,
//					ReviewsListServerBean.class );
//
//			if ( data.isSuccess( ) )
//			{
//				mPageNo++;
//				if ( data.msgs != null )
//				{
//					int all = data.amount;
//					mTotalPage = all % mPageSize == 0 ? all / mPageSize
//							: ( all / mPageSize ) + 1;
//					if ( dataList == null )
//					{
//						dataList = data.msgs;
//					}
//					else
//					{
//						dataList.addAll( data.msgs );
//					}
//					for ( int i = 0 ; i < data.msgs.size( ) ; i++ )
//					{
//						DynamicMessagesItemBean item = new DynamicMessagesItemBean( );
//						item.setItemType( DynamicMessagesItemBean.CONTENT_TYPE );
//						item.setReviewItem( data.msgs.get( i ) );
//						reviews.add( item );
//					}
//					mLastTime = dataList.get( dataList.size( ) - 1 ).msg.datetime;
//				}
//				refreshData( );
//			}
//			else
//			{
//				empty.showEmpty( );
//				ErrorCode.showError( mContext , result );
//			}
//		}
//		else if ( flag == mClearAllFlag )
//		{
//			if ( Constant.isSuccess( result ) )
//			{
//				if ( dataList != null )
//				{
//					dataList.clear( );
//				}
//				if ( reviews != null )
//				{
//					reviews.clear( );
//				}
//				greeterItemBean = null;
//				if ( mDataList != null )
//				{
//					mDataList.clear( );
//				}
//				refreshData( );
//			}
//			else
//			{
//				ErrorCode.showError( mContext , result );
//			}
//		}
//		else if ( flag == mGetUnreadGreeterListFlag )
//		{
//			requestBack( true );
//			mUnreadData = GsonUtil.getInstance( ).getServerBean( result ,
//					ReviewsListServerBean.class );
//
//			DynamicModel.getInstent( ).setNewNumBean( null );
//
//			greeterItemBean = new DynamicMessagesItemBean( );
//			greeterItemBean.setItemType( DynamicMessagesItemBean.HEAD_TYPE );
//			greeterItemBean.setGreeterList( mUnreadData.msgs );
//
//			refreshData( );
//
//		}
//		else if ( flag == mGetGreeterHistoryListFlag )
//		{
//			requestBack( true );
//			ReviewsListServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
//					ReviewsListServerBean.class );
//
//			greeterItemBean = new DynamicMessagesItemBean( );
//			greeterItemBean.setItemType( DynamicMessagesItemBean.HEAD_TYPE );
//			greeterItemBean.setGreeterList( bean.msgs );
//
//			refreshData( );
//		}
//	};

    @Override
    public void onGeneralSuccess(String result, long flag) {
        mProgressDialog.hide();
        if (flag == mGetUnreadCommentFlag) {
            requestBack(true);
            listView.onRefreshComplete();
            ReviewsListServerBean data = GsonUtil.getInstance().getServerBean(result,
                    ReviewsListServerBean.class);

            DynamicNewNumberBean newNumBean = DynamicModel.getInstent().getNewNumBean();

            if (data.isSuccess()) {
                if (newNumBean != null)
                    newNumBean.setNum(0);

                if (data.msgs != null) {
                    dataList = data.msgs;
                    for (int i = 0; i < dataList.size(); i++) {
                        DynamicMessagesItemBean item = new DynamicMessagesItemBean();
                        item.setItemType(DynamicMessagesItemBean.CONTENT_TYPE);
                        item.setReviewItem(dataList.get(i));
                        reviews.add(item);
                    }
                    mLastTime = dataList.get(dataList.size() - 1).msg.datetime;
                    DynamicModel.getInstent().setNewNumBean(null);
                }
                refreshData();
            } else {
                empty.showEmpty();
                ErrorCode.showError(mContext, result);
            }
        } else if (flag == mGetCommentHistoryFlag) {
            requestBack(true);
            listView.onRefreshComplete();
            ReviewsListServerBean data = GsonUtil.getInstance().getServerBean(result,
                    ReviewsListServerBean.class);

            if (data.isSuccess()) {
                mPageNo++;
                if (data.msgs != null) {
                    int all = data.amount;
                    mTotalPage = all % mPageSize == 0 ? all / mPageSize
                            : (all / mPageSize) + 1;
                    if (dataList == null) {
                        dataList = data.msgs;
                    } else {
                        dataList.addAll(data.msgs);
                    }
                    for (int i = 0; i < data.msgs.size(); i++) {
                        DynamicMessagesItemBean item = new DynamicMessagesItemBean();
                        item.setItemType(DynamicMessagesItemBean.CONTENT_TYPE);
                        item.setReviewItem(data.msgs.get(i));
                        reviews.add(item);
                    }
                    mLastTime = dataList.get(dataList.size() - 1).msg.datetime;
                }
                refreshData();
            } else {
                empty.showEmpty();
                ErrorCode.showError(mContext, result);
            }
        } else if (flag == mClearAllFlag) {
            if (Constant.isSuccess(result)) {
                if (dataList != null) {
                    dataList.clear();
                }
                if (reviews != null) {
                    reviews.clear();
                }
                greeterItemBean = null;
                if (mDataList != null) {
                    mDataList.clear();
                }
                refreshData();
            } else {
                ErrorCode.showError(mContext, result);
            }
        } else if (flag == mGetUnreadGreeterListFlag) {
            requestBack(true);
            mUnreadData = GsonUtil.getInstance().getServerBean(result,
                    ReviewsListServerBean.class);

            DynamicModel.getInstent().setNewNumBean(null);

            greeterItemBean = new DynamicMessagesItemBean();
            greeterItemBean.setItemType(DynamicMessagesItemBean.HEAD_TYPE);
            greeterItemBean.setGreeterList(mUnreadData.msgs);

            refreshData();

        } else if (flag == mGetGreeterHistoryListFlag) {
            requestBack(true);
            ReviewsListServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    ReviewsListServerBean.class);

            greeterItemBean = new DynamicMessagesItemBean();
            greeterItemBean.setItemType(DynamicMessagesItemBean.HEAD_TYPE);
            greeterItemBean.setGreeterList(bean.msgs);

            refreshData();
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
//		super.onGeneralError( e , flag );
        mProgressDialog.hide();
        ErrorCode.toastError(BaseApplication.appContext, e);
        empty.showError();

        if (flag == mGetUnreadCommentFlag || flag == mGetCommentHistoryFlag
                || flag == mGetUnreadGreeterListFlag || flag == mGetUnreadGreeterListFlag) {
            requestBack(false);
        }
    }

    private void requestBack(boolean isSuccess) {
        if (isSuccess) {
            requestCount--;
            isGetDataFail = false;
        } else {
            requestCount--;
            isGetDataFail = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_right:
                // 没有内容的时候不能清空
                if ((reviews == null || reviews.size() <= 0)
                        && (greeterItemBean == null
                        || greeterItemBean.getGreeterList() == null || greeterItemBean
                        .getGreeterList().size() <= 0)) {
                    return;
                }
                DialogUtil.showOKCancelDialog(mContext, R.string.dialog_title,
                        R.string.clear_all_message, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAll();
                            }
                        });
                break;
        }
    }

    View.OnClickListener greeterOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), DynamicGreetersActivity.class);
            if (isGetNewGreeter) {
                // 传递所有未读点赞列表的信息
                String unreadJson = GsonUtil.getInstance().getStringFromJsonObject(
                        mUnreadData);
                intent.putExtra(DynamicGreetersActivity.UNREAD_DATA_KEY, unreadJson);
            }

            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }

}
