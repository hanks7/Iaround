package net.iaround.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.pay.bean.BrillantDetailBean;
import net.iaround.pay.bean.BrillantDetailBean.BrillantDetals;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 钻石详情介绍界面
 *
 * @author aif
 */
public class DiamondDetailFragment extends BaseFragment implements HttpCallBack {

    private PullToRefreshListView bdlistview;
    private BrillantDetailBean bean;
    private Dialog mProgressDialog;
    private long brillantDetail;
    public Context mContext;
    private BrillantAdapter briAdapter;
    private static final int SHOW_DATA = 1000;
    private static final int GET_DATA_FAIL = 1001;
    private TextView nullStr;
    private int mCurPage = 1;
    private int mTotalPage = 1;
    private final int PAGE_SIZE = 15;
    private String chongZhi, buyCoin, buyGift, buyExp, createCir, circlePur,
            wordFoucs, buyGiftPac, diamondReturn, upgradeSkill, skillUse ,payProps;
    private ArrayList<BrillantDetals> mList = new ArrayList<BrillantDetals>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_brillant_detail, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        initView(getView());
        requestData(1);
    }


    private void requestData(int nextPage) {
        showProgressDialog();
        brillantDetail = GoldHttpProtocol.getBrillantDetailList(mContext,
                nextPage, PAGE_SIZE, this);

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        // TODO Auto-generated method stub
        if (flag == brillantDetail) {
            BrillantDetailBean data = GsonUtil.getInstance().getServerBean(
                    result, BrillantDetailBean.class);
            if (data != null && data.isSuccess()) {

                if (data != null && data.details != null
                        && !"".equals(data.details)) {
                    if (data.details.size() > 0) {
                        Message msg = Message.obtain();
                        msg.what = SHOW_DATA;
                        msg.obj = data;
                        mHandler.sendMessage(msg);
                    }
                } else {
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
        // TODO Auto-generated method stub
        if (flag == brillantDetail) {
            bdlistview.onRefreshComplete();
            hideProgressDialog();
            ErrorCode.toastError(mContext, e);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATA:
                    bean = (BrillantDetailBean) msg.obj;
                    handleShowData();
                    break;

                case GET_DATA_FAIL:
                    hideProgressDialog();
                    bdlistview.onRefreshComplete();
                    nullStr.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void handleShowData() {

        mCurPage = bean.pageno;
        int total = bean.amount;
        mTotalPage = total / PAGE_SIZE;
        if (total % PAGE_SIZE > 0) {
            mTotalPage++;
        }
        if (mCurPage <= 1) {
            if (mList == null) {
                mList = new ArrayList<BrillantDetals>();
            } else {
                mList.clear();
                mList = bean.details;
            }
        } else {
            mList.addAll(bean.details);
        }
//		if (mTotalPage == mCurPage) {
//			bdlistview.setMode(Mode.DISABLED);
//		} else {
//			bdlistview.setMode(Mode.PULL_FROM_END);
//		}
        if (mList.isEmpty()) {
            nullStr.setVisibility(View.VISIBLE);
        }
        bdlistview.onRefreshComplete();
        hideProgressDialog();
        briAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {

        nullStr = (TextView) view.findViewById(R.id.nullStr);
        briAdapter = new BrillantAdapter();
        bdlistview = (PullToRefreshListView) view.findViewById(R.id.brillant_detail_list);
        bdlistview.getRefreshableView().setAdapter(briAdapter);
        bdlistview.setMode(Mode.BOTH);
        bdlistview
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // TODO Auto-generated method stub
                        requestData(1);

                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // TODO Auto-generated method stub
                        Log.e("TAG", "onPullUpToRefresh");
                        // 这里写上拉加载更多的任务
                        if (mCurPage < mTotalPage) {
                            requestData(mCurPage + 1);
                        } else {
                            refreshView.postDelayed(new Runnable() {
                                public void run() {
                                    Toast.makeText(getActivity(),
                                            R.string.no_more,
                                            Toast.LENGTH_SHORT).show();
                                    bdlistview.onRefreshComplete();
                                }
                            }, 200);
                        }

                    }
                });

        chongZhi = mContext.getResources().getString(R.string.bri_det_type_re);
        buyCoin = mContext.getResources().getString(
                R.string.bri_det_type_buyjin);
        buyGift = mContext.getResources().getString(
                R.string.bri_det_type_buygif);
        buyExp = mContext.getResources().getString(
                R.string.bri_det_type_buyexpression);
        createCir = mContext.getResources().getString(
                R.string.bri_det_type_create_circle);
        circlePur = mContext.getResources().getString(
                R.string.bri_det_type_circle_pur);
        wordFoucs = mContext.getResources().getString(
                R.string.bri_det_type_word_foucs);
        buyGiftPac = mContext.getResources().getString(
                R.string.bri_det_type_buygift_pac);
        diamondReturn = mContext.getResources().getString(
                R.string.bri_det_type_buygift_zsfh);
        upgradeSkill = mContext.getResources().getString(
                R.string.upgrade_skill_success_dialog_title);
        skillUse = mContext.getResources().getString(
                R.string.skill_user);
        payProps = mContext.getResources().getString(
                R.string.buy_props);
    }

    private class BrillantAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size() > 0 ? mList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.x_brillant_detail_list_item, null);
                viewHolder.account = (TextView) convertView
                        .findViewById(R.id.brDe_account);
                viewHolder.type = (TextView) convertView
                        .findViewById(R.id.brDe_type);
                viewHolder.time = (TextView) convertView
                        .findViewById(R.id.brDe_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BrillantDetals data = mList.get(position);

            if (data != null) {
                int num = data.diamondnum;
                int consumetype = data.consumetype;

                if (consumetype == 0 || consumetype == 8) {
                    viewHolder.account.setText("+" + num + "");
                }
                if (consumetype > 0 && consumetype != 8) {
                    viewHolder.account.setText("-" + num + "");

                }

                if (consumetype == 0) {
                    viewHolder.type.setText(chongZhi);
                } else if (consumetype == 1) {
                    viewHolder.type.setText(buyCoin);
                } else if (consumetype == 2) {
                    viewHolder.type.setText(buyGift);
                } else if (consumetype == 3) {
                    viewHolder.type.setText(buyExp);
                } else if (consumetype == 4) {
                    viewHolder.type.setText(createCir);
                } else if (consumetype == 5) {
                    viewHolder.type.setText(circlePur);
                } else if (consumetype == 6) {
                    viewHolder.type.setText(wordFoucs);
                } else if (consumetype == 7) {
                    viewHolder.type.setText(buyGiftPac);
                } else if (consumetype == 8) {
                    viewHolder.type.setText(diamondReturn);
                } else if (consumetype == 9) {
                    viewHolder.type.setText(upgradeSkill);
                } else if (consumetype == 10) {
                    viewHolder.type.setText(skillUse);
                } else if (consumetype == 13) {
                    viewHolder.type.setText(payProps);
                } else {
                    viewHolder.type.setText(consumetype + "");
                }

                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                viewHolder.time.setText(sdf.format(data.datetime));

            }
            return convertView;
        }

    }

    class ViewHolder {
        TextView account;
        TextView type;
        TextView time;
    }

    // 显示加载框
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(mContext, "",
                    getString(R.string.please_wait), null);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

}
