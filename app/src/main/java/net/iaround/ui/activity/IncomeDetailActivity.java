package net.iaround.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liangmutian.mypicker.DatePickerDialog;
import com.example.liangmutian.mypicker.DateUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.IncomeDetailBean;
import net.iaround.model.entity.IncomeDetailEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.IncomeDetailAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 提现明细界面
 * Created by Administrator on 2018/4/11.
 */

public class IncomeDetailActivity extends TitleActivity implements HttpCallBack {
    private static final String TAG = "IncomeDetailActivity";
    private static final int SHOW_DATA = 1000;
    private static final int GET_DATA_FAIL = 1001;
    private static final int GET_DATA_ERROR = 1002;
    private long mDailyDetailFlag;
    private int mPageNo = 1;
    private int mPageSize = 24;
    private PullToRefreshExpandableListView mLvDailyDetail;
    private TextView mTvNullStr;
    private IncomeDetailEntity mDailyDetailEntity;
    private IncomeDetailAdapter mDailyDetailAdapter;
    private ExpandableListView refreshableView;
    private Dialog mDateDialog;//日期选择dialog

    private LinkedHashMap<String,ArrayList<IncomeDetailEntity.DailyDetail>> stringDailyDetailHashMap;

    private ArrayList<IncomeDetailBean> incomeDetailBeanArrayList ;

    private int totalNums = 0;

    private String mDay = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATA:
                    mDailyDetailEntity = (IncomeDetailEntity) msg.obj;
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
        requestData(mDay);
    }

    private void initView() {
            setTitle_C(R.string.income_details_tab2);
            getIvRight().getLayoutParams().height = DensityUtils.dp2px(this,17);
            getIvRight().getLayoutParams().width = DensityUtils.dp2px(this,17);
            setTitle_R(R.drawable.icon_select, null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateDialog(DateUtil.getDateForString("2018-08-01"));
                }
            });
        setContent(R.layout.activity_daily_detail);
        mDailyDetailAdapter = new IncomeDetailAdapter(this);
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
                requestData(mDay);
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
                        requestData(mDay);
                    }
                }
            }
        });
        mTvNullStr = (TextView) findViewById(R.id.tv_nullStr);
        mTvNullStr.setText(getString(R.string.income_detail_null));
    }

    private void requestData(String day) {
        showWaitDialog();
        mDailyDetailFlag = GameChatHttpProtocol.getWithDrawList(this, mPageNo, mPageSize,day, this);
    }

    private void showDateDialog(List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int[] dates) {
                String day = dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                        + (dates[2] > 9 ? dates[2] : ("0" + dates[2]));
                CommonFunction.toastMsg(BaseApplication.appContext,day);
                mPageNo = 1;
                mDay = day;
                requestData(mDay);
            }

            @Override
            public void onCancel() {

            }
        })

                .setSelectYear(date.get(0) - 1)
                .setSelectMonth(date.get(1) - 1)
                .setSelectDay(date.get(2) - 1);

        builder.setMaxYear(DateUtil.getYear());
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        mDateDialog = builder.create();
        mDateDialog.show();
    }
    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (mDailyDetailFlag == flag) {
            IncomeDetailEntity data = GsonUtil.getInstance().getServerBean(result, IncomeDetailEntity.class);
            if (data != null && data.isSuccess()) {
                if (data.Details != null) {
                        Message msg = Message.obtain();
                        msg.what = SHOW_DATA;
                        msg.obj = data;
                        mHandler.sendMessage(msg);
//                  {"pageno":1,"pagesize":24,"amount":0,"Details":[],"status":200}
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
        Log.e("tag","e=="+e+"==flag=="+flag);
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
        incomeDetailBeanArrayList.clear();
        incomeDetailBeanArrayList = null;
    }

    private void holdShowData() {

        totalNums = 0;
        if (mPageNo <= 1) {
            if(stringDailyDetailHashMap == null){
                stringDailyDetailHashMap = new LinkedHashMap<String,ArrayList<IncomeDetailEntity.DailyDetail>>();
            }else {
                stringDailyDetailHashMap.clear();
            }

            if(incomeDetailBeanArrayList == null){
                incomeDetailBeanArrayList = new ArrayList<>();
            }else {
                incomeDetailBeanArrayList.clear();
            }
        }

        if(mDailyDetailEntity != null){
            ArrayList<IncomeDetailEntity.DetailsList> details= mDailyDetailEntity.Details;
            if(details != null && details.size() > 0){
                for (int i = 0; i < details.size(); i++) {
                    IncomeDetailEntity.DetailsList detailsList = details.get(i);
                    if(detailsList != null){

                        String earntime = detailsList.earntime;
                        String monthtotal = detailsList.monthtotal;
                        String monthcash = detailsList.monthcash;

                        IncomeDetailBean incomeDetailBean = new IncomeDetailBean(earntime,monthtotal,monthcash);

                        if(incomeDetailBeanArrayList != null && incomeDetailBeanArrayList.size() > 0){
                            for (int j = 0; j < incomeDetailBeanArrayList.size(); j++) {
                                IncomeDetailBean incomeDetailBean1 = incomeDetailBeanArrayList.get(j);
                                if(!incomeDetailBean1.earntime.equals(incomeDetailBean.earntime)){
                                    incomeDetailBeanArrayList.add(incomeDetailBean);
                                }
                            }
                        }else {
                            incomeDetailBeanArrayList.add(incomeDetailBean);
                        }


                        ArrayList<IncomeDetailEntity.DailyDetail> detail= detailsList.lists;

                        if(detail != null && detail.size() > 0){
                            if(!stringDailyDetailHashMap.containsKey(earntime)){
                                stringDailyDetailHashMap.put(earntime,detail);
                            }else {
                                ArrayList<IncomeDetailEntity.DailyDetail> dailyDetails = stringDailyDetailHashMap.get(earntime);
                                dailyDetails.addAll(detail);
                            }
                        }
                    }
                }
            }
        }

        for(ArrayList<IncomeDetailEntity.DailyDetail> v:stringDailyDetailHashMap.values()) {
            Log.e("tag","dailyDetails.size()=="+v.size());
            totalNums += v.size();
        }

        if (totalNums == 0) {
            mTvNullStr.setVisibility(View.VISIBLE);
        } else {
            mTvNullStr.setVisibility(View.GONE);
        }
        Log.e("tag","totalNums=="+totalNums);





        mLvDailyDetail.onRefreshComplete();
        mDailyDetailAdapter.updateList(stringDailyDetailHashMap,incomeDetailBeanArrayList);



        //设置自动展开
        //自动展开必须写在onResume方法中，否则会发生错误
        for (int i = 0; i < mDailyDetailAdapter.getGroupCount(); i++) {
            refreshableView.expandGroup(i);
        }



        hideWaitDialog();
    }

}
