package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.GetEarnDetailBean;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;

/**
 * Created by yz on 2018/8/15.
 * 主播收益界面
 */

public class GetEarnDetailActivity extends TitleActivity implements HttpCallBack, View.OnClickListener {

    private TextView mTvCanGetEarn;//今日可提现金额
    private TextView mTvGetEarn;//提现按钮
    private TextView mTvTodayMoney;//今日收入金额
    private TextView mTvNoWithdrawal;//暂不可提现金额
    private LinearLayout mLlDailyDetail;//每日明细
    private LinearLayout mLlIncomeDetail;//提现明细
    private LinearLayout mLlCommonQuestion;//提现说明
    private LinearLayout mLlVoiceDetail;//语音明细

    private double mCanTakeCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_C(getString(R.string.anchor_earn));
        setContent(R.layout.activity_get_earn);
        mTvCanGetEarn = (TextView) findViewById(R.id.tv_can_get_earn);
        mTvGetEarn = (TextView) findViewById(R.id.tv_get_earn);
        mTvTodayMoney = (TextView) findViewById(R.id.tv_today_money);
        mTvNoWithdrawal = (TextView) findViewById(R.id.tv_no_withdrawal);
        mLlDailyDetail = (LinearLayout) findViewById(R.id.ll_daily_detail);
        mLlVoiceDetail = (LinearLayout) findViewById(R.id.ll_voice_detail);
        mLlIncomeDetail = (LinearLayout) findViewById(R.id.ll_income_detail);
        mLlCommonQuestion = (LinearLayout) findViewById(R.id.ll_common_question);
        if (Common.getInstance().loginUser.getVoiceUserType() == 1) {
            mLlVoiceDetail.setVisibility(View.VISIBLE);
        }
        mTvGetEarn.setOnClickListener(this);
        mLlDailyDetail.setOnClickListener(this);
        mLlIncomeDetail.setOnClickListener(this);
        mLlCommonQuestion.setOnClickListener(this);
        mLlVoiceDetail.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showWaitDialog();
        GameChatHttpProtocol.getEarnDetail(this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_earn:
                if (Common.getInstance().loginUser.getVerifyPersion() == 0) {
                    Intent in = new Intent(this, AuthenticationActivity.class);
                    startActivity(in);
                    return;
                }
                Intent intent = new Intent(this, IncomeWithdrawalActivity.class);
                intent.putExtra("allMoney", mCanTakeCash);
                startActivity(intent);

                break;
            case R.id.ll_daily_detail:
                Intent intent1 = new Intent(this, DailyDetailActivity.class);
                startActivity(intent1);
                break;
            case R.id.ll_voice_detail:
                Intent intent3 = new Intent(this, DailyVoiceActivity.class);
                startActivity(intent3);
                break;
            case R.id.ll_income_detail:
                Intent intent2 = new Intent(this, IncomeDetailActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_common_question:
                InnerJump.Jump(this, "http://notice.iaround.com/gamchat_orders/withdrawal.html");
                break;
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        GetEarnDetailBean bean = GsonUtil.getInstance().getServerBean(result, GetEarnDetailBean.class);
        if (bean != null && bean.detail != null) {
            mCanTakeCash = bean.detail.returncash;
            mTvCanGetEarn.setText(bean.detail.returncash + "");
            mTvTodayMoney.setText(bean.detail.todaycash + "");
            mTvNoWithdrawal.setText(bean.detail.freezecash + "");
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }
}
