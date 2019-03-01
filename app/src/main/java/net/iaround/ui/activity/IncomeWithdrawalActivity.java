package net.iaround.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;

/**
 * Created by yz on 2018/8/22.
 * 提现界面 输入账号 金额界面
 */

public class IncomeWithdrawalActivity extends TitleActivity implements HttpCallBack {
    private static final String ACCOUNT = "account";

    private EditText mEtAlipayAccount;
    private EditText mEtMoney;
    private TextView mTvTodayCanTake;
    private TextView mTvTakeAllMoney;
    private TextView mTvSubmit;
    private double mMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMoney = getIntent().getDoubleExtra("allMoney", 0);
        setTitle_C(R.string.take__money);
        setContent(R.layout.activity_income_withdrawal);
        mEtAlipayAccount = (EditText) findViewById(R.id.et_alipay_account);
        mEtMoney = (EditText) findViewById(R.id.et_money);
        mTvTodayCanTake = (TextView) findViewById(R.id.tv_today_can_take);
        mTvTakeAllMoney = (TextView) findViewById(R.id.tv_take_all_money);
        mTvSubmit = (TextView) findViewById(R.id.tv_submit);
        mTvTodayCanTake.setText(getString(R.string.today_can_take_money) + "  ¥ " + mMoney);
        mTvTakeAllMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMoney > 0){
                    mEtMoney.setText(String.valueOf(mMoney));
                }
            }
        });
        mTvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double sum = Double.valueOf(mEtMoney.getText().toString().trim());
                if (sum > mMoney) {
                    CommonFunction.toastMsg(BaseApplication.appContext, R.string.cannot_take_money_tip);
                    return;
                }
                showWaitDialog();
                GameChatHttpProtocol.applyWithdrawal(mContext, sum, mEtAlipayAccount.getText().toString().trim(), IncomeWithdrawalActivity.this);

            }
        });
        mEtMoney.addTextChangedListener(textWatcher);
        mTvSubmit.setEnabled(false);
        mTvSubmit.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
        mTvSubmit.setTextColor(getResources().getColor(R.color.common_black));
        if (!TextUtils.isEmpty(SharedPreferenceUtil.getInstance(mContext).getString(ACCOUNT + Common.getInstance().loginUser.getUid()))) {
            mEtAlipayAccount.setText(SharedPreferenceUtil.getInstance(mContext).getString(ACCOUNT + Common.getInstance().loginUser.getUid()));
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String number = mEtMoney.getText().toString();
            String number1 = mEtAlipayAccount.getText().toString();

            if (number.length() > 0 && number1.length() > 0) {
                mTvSubmit.setEnabled(true);
                mTvSubmit.setBackgroundResource(R.drawable.button_bg);
                mTvSubmit.setTextColor(getResources().getColor(R.color.common_white));
            } else {
                mTvSubmit.setEnabled(false);
                mTvSubmit.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                mTvSubmit.setTextColor(getResources().getColor(R.color.common_black));
            }
        }
    };

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (bean != null && bean.isSuccess()) {
            SharedPreferenceUtil.getInstance(mContext).putString(ACCOUNT + Common.getInstance().loginUser.getUid(), mEtAlipayAccount.getText().toString().trim());
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.apply_join_group_success);
            finish();
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();

    }
}
