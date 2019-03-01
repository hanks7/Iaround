package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.handmark.pulltorefresh.library.internal.RecycleViewLoadingLayout;
import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.HomepageHttpProtocol;
import net.iaround.model.entity.VoiceChatMessageListData;
import net.iaround.model.entity.VoiceChatMessageListItemData;
import net.iaround.model.entity.VoiceChatMessageListItemDeteleData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.VoiceChatMessageListActivityAdapter;
import net.iaround.ui.adapter.poweradapter.AdapterLoader;
import net.iaround.ui.adapter.poweradapter.OnLoadMoreListener;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;

import java.util.List;

/**
 * 语音聊天消息列表
 */
public class VoiceChatMessageListActivity extends TitleActivity implements HttpCallBack, PowerAdapter.OnEmptyClickListener, PowerAdapter.OnErrorClickListener, OnLoadMoreListener, AdapterLoader.OnItemLongClickListener<VoiceChatMessageListItemData>,AdapterLoader.OnItemClickListener<VoiceChatMessageListItemData> {

    private static final String TAG = VoiceChatMessageListActivity.class.getSimpleName();
    private PullToRefreshRecyclerView mPtrlvVoicePage;

    private RecyclerView mRecyclerView;

    private VoiceChatMessageListActivityAdapter voiceFragmentAdapter;

    private int mCurrentPageno = 1;

    private int pagesize = 24;

    private Runnable loadMoreAction;

    private boolean isRun;

    private static final int DEFAULT_TIME = 1000;

    private long mFlagVoiceListInfo;

    private long mFlagDeteleVoiceListItemInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.voice_chat), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_voice_chat_message_list);
        mPtrlvVoicePage = (PullToRefreshRecyclerView) findViewById(R.id.pull_refresh_recycler_chatlist);
        mPtrlvVoicePage.setHeaderLayout(new RecycleViewLoadingLayout(BaseApplication.appContext));
        mRecyclerView = mPtrlvVoicePage.getRefreshableView();
        LinearLayoutManager manager = new LinearLayoutManager(BaseApplication.appContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        voiceFragmentAdapter = new VoiceChatMessageListActivityAdapter(VoiceChatMessageListActivity.this);
        voiceFragmentAdapter.attachRecyclerView(mRecyclerView);
        voiceFragmentAdapter.setLoadMoreListener(this);
        voiceFragmentAdapter.setEmptyClickListener(this);
        voiceFragmentAdapter.setErrorClickListener(this);
        voiceFragmentAdapter.setOnItemClickListener(this);
        voiceFragmentAdapter.setOnItemLongClickListener(this);
        loadMoreAction = new Runnable() {
            @Override
            public void run() {
                initdata(2);
            }
        };
        mPtrlvVoicePage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                initdata(1);
            }
        });
        showWaitDialog();
        initdata(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initdata(int type) {
        if(type == 1){
            mFlagVoiceListInfo = HomepageHttpProtocol.getVoiceListInfo(BaseApplication.appContext, 1, pagesize, VoiceChatMessageListActivity.this);
        }else if(type == 2){
            mFlagVoiceListInfo = HomepageHttpProtocol.getVoiceListInfo(BaseApplication.appContext, mCurrentPageno + 1, pagesize, VoiceChatMessageListActivity.this);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        if(mFlagVoiceListInfo == flag){
            stopPulling();
            destroyWaitDialog();
            VoiceChatMessageListData mVoiceListData = GsonUtil.getInstance().getServerBean(result, VoiceChatMessageListData.class);
            if (mVoiceListData != null && mVoiceListData.isSuccess()) {
                mCurrentPageno = mVoiceListData.pageno;
                List<VoiceChatMessageListItemData> list = mVoiceListData.chatlist;
                if (list != null && list.size() > 0) {
                    voiceFragmentAdapter.setTotalCount(mVoiceListData.count);
                    if (mVoiceListData.pageno <= 1) {
                        voiceFragmentAdapter.setList(list);
                    } else {
                        voiceFragmentAdapter.appendList(list);
                    }
                } else {
                    emptyAnderrorView();
                }
            } else {
                emptyAnderrorView();
            }
            isRun = false;
        }else if(mFlagDeteleVoiceListItemInfo == flag){
            VoiceChatMessageListItemDeteleData serverBean = GsonUtil.getInstance().getServerBean(result, VoiceChatMessageListItemDeteleData.class);
            if (serverBean != null && serverBean.isSuccess()) {
                int num = serverBean.num;
                long voiceRoomListID = serverBean.voiceRoomListID;
                voiceFragmentAdapter.setTotalCount(num);
                List<VoiceChatMessageListItemData> list = voiceFragmentAdapter.getList();
                if(list != null && list.size() > 0){
                    for (int i = 0; i < list.size(); i++) {
                        VoiceChatMessageListItemData voiceChatMessageListItemData = list.get(i);
                        if(voiceRoomListID == voiceChatMessageListItemData.voiceRoomListID){
                            voiceFragmentAdapter.removeItem(i);
                            int itemRealCount = voiceFragmentAdapter.getItemRealCount();
                            if(itemRealCount <= 0){
                                voiceFragmentAdapter.setEmptyView(View.inflate(BaseApplication.appContext, R.layout.no_data_view, null));
                                voiceFragmentAdapter.showEmpty();
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if(mFlagVoiceListInfo == flag){
            stopPulling();
            destroyWaitDialog();
            ErrorCode.toastError(BaseApplication.appContext, e);
            emptyAnderrorView();
            isRun = false;
        }else if(mFlagDeteleVoiceListItemInfo == flag){
            destroyWaitDialog();
            ErrorCode.toastError(BaseApplication.appContext, e);
        }
    }

    public void stopPulling() {
        if (mPtrlvVoicePage != null) {
            mPtrlvVoicePage.onRefreshComplete();
        }
    }

    @Override
    public void onEmptyClick(View view) {
        showWaitDialog();
        initdata(1);
    }

    @Override
    public void onErrorClick(View view) {
        showWaitDialog();
        initdata(1);
    }

    public void emptyAnderrorView(){
        if (mCurrentPageno <= 1 && voiceFragmentAdapter.getItemRealCount()<= 0) {
            voiceFragmentAdapter.setErrorView(View.inflate(BaseApplication.appContext, R.layout.no_data_view, null));
            voiceFragmentAdapter.showError(true);
        }
    }

    @Override
    public void onLoadMore() {
        if (isRun) {
            CommonFunction.log(TAG, "onLoadMore:正在执行，直接返回。。。 ");
            return;
        }
        CommonFunction.log(TAG, "onLoadMore: ");
        isRun = true;
        mRecyclerView.postDelayed(loadMoreAction, DEFAULT_TIME);
    }

    @Override
    public boolean onItemLongClick(@NonNull View itemView, int position, final VoiceChatMessageListItemData item) {
        DialogUtil.showTwoButtonDialog(mContext,
                mContext.getString(R.string.chat_remove_contact_title),
                mContext.getString(R.string.chat_remove_contact_notice),
                mContext.getString(R.string.cancel),
                mContext.getString(R.string.chat_record_fun_del), null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWaitDialog();
                        mFlagDeteleVoiceListItemInfo = HomepageHttpProtocol.deteleVoiceListItemInfo(BaseApplication.appContext, item.voiceRoomListID, VoiceChatMessageListActivity.this);
                    }
                });
        return false;
    }

    @Override
    public void onItemClick(@NonNull View itemView, int position, VoiceChatMessageListItemData item) {
        if (item != null) {
            if(item.isanchor == 0){
                return;
            }
            Intent intent = new Intent(VoiceChatMessageListActivity.this, AudioDetailsActivity.class);
            intent.putExtra("UserId", item.userid);
            startActivity(intent);
        }
    }
}
