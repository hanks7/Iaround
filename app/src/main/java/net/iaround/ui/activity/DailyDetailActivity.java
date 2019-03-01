package net.iaround.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.DailyDetailEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.DailyDetailAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 主播每日明细
 * Created by Administrator on 2018/4/11.
 */

public class DailyDetailActivity extends TitleActivity implements HttpCallBack {
    private static final String TAG = "DailyDetailActivity";
    private static final int SHOW_DATA = 1000;
    private static final int GET_DATA_FAIL = 1001;
    private static final int GET_DATA_ERROR = 1002;
    private long mDailyDetailFlag;
    private int mPageNo = 1;
    private int mPageSize = 24;
    private PullToRefreshExpandableListView mLvDailyDetail;
    private TextView mTvNullStr;
    private DailyDetailEntity mDailyDetailEntity;
    private DailyDetailAdapter mDailyDetailAdapter;

    private ExpandableListView refreshableView;

    private LinkedHashMap<String,ArrayList<DailyDetailEntity.DailyDetail>> stringDailyDetailHashMap;

    private int totalNums = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATA:
                    mDailyDetailEntity = (DailyDetailEntity) msg.obj;
                    holdShowData();
                    break;
                case GET_DATA_FAIL:
                    hideWaitDialog();
                    mLvDailyDetail.onRefreshComplete();
                    if (totalNums <= 0) {
                        mTvNullStr.setVisibility(View.VISIBLE);
                    }
                    break;
                case GET_DATA_ERROR:
                    int e = (int) msg.obj;
                    mLvDailyDetail.onRefreshComplete();
                    hideWaitDialog();
                    ErrorCode.toastError(mContext, e);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        requestData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        setTitle_C(R.string.daily_detail_title);
        setContent(R.layout.activity_daily_detail);
        mDailyDetailAdapter = new DailyDetailAdapter(this);
        mLvDailyDetail = (PullToRefreshExpandableListView) findViewById(R.id.lv_daily_detail);
        refreshableView = mLvDailyDetail.getRefreshableView();
        refreshableView.setAdapter(mDailyDetailAdapter);
        //将ExpandableListView groupitem中系统自带的下拉箭头图标去掉
        refreshableView.setGroupIndicator(null);
        mLvDailyDetail.setMode(PullToRefreshBase.Mode.BOTH);
//       //设置分组项的点击监听事件
        refreshableView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
            // 请务必返回 false，否则分组不会展开
            return true;
            }
        });

        mLvDailyDetail.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                mPageNo = 1;
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                CommonFunction.log(TAG, "onPullUpToRefresh");
                if(mDailyDetailEntity != null){
                    if (totalNums >= mDailyDetailEntity.amount) {
                        refreshView.postDelayed(new Runnable() {
                            public void run() {
                                CommonFunction.toastMsg(BaseApplication.appContext, R.string.no_more);
                                mLvDailyDetail.onRefreshComplete();
                            }
                        }, 200);
                    }else {
                        mPageNo = mDailyDetailEntity.pageno + 1;
                        requestData();
                    }
                }
            }
        });
        mTvNullStr = (TextView) findViewById(R.id.tv_nullStr);
        mTvNullStr.setText(getString(R.string.income_detail_null));
    }

    private void requestData() {
        showWaitDialog();
        mDailyDetailFlag = GameChatHttpProtocol.getDailyEarn(this, mPageNo, mPageSize, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (mDailyDetailFlag == flag) {
            DailyDetailEntity data = GsonUtil.getInstance().getServerBean(result, DailyDetailEntity.class);
            if (data != null && data.status == 200) {
                if (data.Details != null) {
                        Message msg = Message.obtain();
                        msg.what = SHOW_DATA;
                        msg.obj = data;
                        mHandler.sendMessage(msg);
                }else {
                    Message msg = Message.obtain();
                    msg.what = GET_DATA_FAIL;
                    mHandler.sendMessage(msg);
                }
            } else {
                Message msg = Message.obtain();
                msg.what = GET_DATA_FAIL;
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (mDailyDetailFlag == flag) {
            Message msg = Message.obtain();
            msg.what = GET_DATA_ERROR;
            msg.obj = e;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyWaitDialog();
        stringDailyDetailHashMap.clear();
        stringDailyDetailHashMap=null;
    }

    private void holdShowData() {
        totalNums = 0;
        if (mPageNo <= 1) {
            if(stringDailyDetailHashMap == null){
                stringDailyDetailHashMap = new LinkedHashMap<String,ArrayList<DailyDetailEntity.DailyDetail>>();
            }else {
                stringDailyDetailHashMap.clear();
            }
        }

        if(mDailyDetailEntity != null){
            ArrayList<DailyDetailEntity.DetailsList> details= mDailyDetailEntity.Details;
            if(details != null && details.size() > 0){
                for (int i = 0; i < details.size(); i++) {
                    DailyDetailEntity.DetailsList detailsList = details.get(i);
                    if(detailsList != null){
                        String earntime = detailsList.earntime;
                        ArrayList<DailyDetailEntity.DailyDetail> detail= detailsList.detail;
                        if(detail != null && detail.size() > 0){
                            if(!stringDailyDetailHashMap.containsKey(earntime)){
                                stringDailyDetailHashMap.put(earntime,detail);
                            }else {
                                ArrayList<DailyDetailEntity.DailyDetail> dailyDetails = stringDailyDetailHashMap.get(earntime);
                                dailyDetails.addAll(detail);
                            }
                        }
                    }
                }
            }
        }

        for(ArrayList<DailyDetailEntity.DailyDetail> v:stringDailyDetailHashMap.values()) {
            totalNums += v.size();
        }

        if (totalNums == 0) {
            mTvNullStr.setVisibility(View.VISIBLE);
        } else {
            mTvNullStr.setVisibility(View.GONE);
        }

        mLvDailyDetail.onRefreshComplete();
        mDailyDetailAdapter.updateList(stringDailyDetailHashMap);

        //设置自动展开
        //自动展开必须写在onResume方法中，否则会发生错误
        for (int i = 0; i < mDailyDetailAdapter.getGroupCount(); i++) {
            refreshableView.expandGroup(i);
        }

        hideWaitDialog();
    }
}
