package net.iaround.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.contract.SecretContract;
import net.iaround.model.entity.SecretEntity;
import net.iaround.presenter.SecretPresenter;

public class SecretActivity extends TitleActivity implements SecretContract.View {

    public final static String KEY_IS_SECRET = "is_secret";

    private SecretPresenter persenter;
    private TextView tvIncome;
    private TextView tvOwnHouse;
    private TextView tvOwnCar;
    private TextView tvUniversity;
    private TextView tvCompany;
    private TextView tvOccupation;

    private boolean isMySelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        persenter = new SecretPresenter(this);
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.secret_userinfo), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_secret);
        tvIncome = findView(R.id.tv_user_income);
        tvOwnHouse = findView(R.id.tv_own_house);
        tvOwnCar = findView(R.id.tv_own_car);
        tvUniversity = findView(R.id.tv_user_university);
        tvCompany = findView(R.id.tv_user_company);
        tvOccupation = (TextView) findViewById(R.id.tv_user_occupation);
    }

    private void initDatas() {
        isMySelf = getIntent().getBooleanExtra(KEY_IS_SECRET, true);
        persenter.init();
    }

    private void initListeners() {

    }

    @Override
    public void setDatas(SecretEntity secret) {
        if (secret.getSalary() <= 0) {
            if (isMySelf) {
                tvIncome.setText(getString(R.string.space_modify_empty));
                tvIncome.setTextColor(getResColor(R.color.edit_user_tips));
            } else {
                findViewById(R.id.ll_user_secret_income).setVisibility(View.GONE);
            }
        } else {
            if (secret.getSalary() > 0) {
                tvIncome.setText(getResStringArr(R.array.income_data)[secret.getSalary() - 1]);
            } else {
                tvIncome.setText(getResStringArr(R.array.income_data)[secret.getSalary()]);
            }

            tvIncome.setTextColor(getResColor(R.color.edit_user_value));
        }
        if (secret.getHouse() <= 0) {
            if (isMySelf) {
                tvOwnHouse.setText(getString(R.string.space_modify_empty));
                tvOwnHouse.setTextColor(getResColor(R.color.edit_user_tips));
            } else {
                findViewById(R.id.ll_user_secret_own_house).setVisibility(View.GONE);
            }
        } else {
            tvOwnHouse.setText(getResStringArr(R.array.own_house_data)[secret.getHouse() - 1]);
            tvOwnHouse.setTextColor(getResColor(R.color.edit_user_value));
        }
        if (secret.getCar() <= 0) {
            if (isMySelf) {
                tvOwnCar.setText(getString(R.string.space_modify_empty));
                tvOwnCar.setTextColor(getResColor(R.color.edit_user_tips));
            } else {
                findViewById(R.id.ll_user_secret_own_car).setVisibility(View.GONE);
            }
        } else {
            tvOwnCar.setText(getResStringArr(R.array.own_car_data)[secret.getCar() - 1]);
            tvOwnCar.setTextColor(getResColor(R.color.edit_user_value));
        }
//        if (TextUtils.isEmpty(secret.getSchool())) {


        if (isMySelf) {
            if (TextUtils.isEmpty(secret.getSchool())) {
                tvUniversity.setText(getString(R.string.space_modify_empty));
                tvUniversity.setTextColor(getResColor(R.color.edit_user_tips));
            } else {
                String[] school = secret.getSchool().split(":");
                if (school.length > 0) {
                    tvUniversity.setText(school[0]);
                    tvUniversity.setTextColor(getResColor(R.color.edit_user_value));
                } else {
                    tvUniversity.setText(getString(R.string.space_modify_empty));
                    tvUniversity.setTextColor(getResColor(R.color.edit_user_tips));
                }
            }


        } else {
            if (TextUtils.isEmpty(secret.getSchool())) {
                findViewById(R.id.ll_user_secret_school).setVisibility(View.GONE);
            } else {
                if (secret.getSchool().contains(":")) {
                    String[] school = secret.getSchool().split(":");
                    if (school.length > 0) {
                        if (!"null".equals(school[0])) {
                            tvUniversity.setText(school[0]);
                            tvUniversity.setTextColor(getResColor(R.color.edit_user_value));
                        } else {
                            findViewById(R.id.ll_user_secret_school).setVisibility(View.GONE);
                        }

                    } else {
                        findViewById(R.id.ll_user_secret_school).setVisibility(View.GONE);
                    }

                }
            }

        }
//        } else {
////            SchEntity schEntity = GsonUtil.getInstance().getServerBean(secret.getSchool(), SchEntity.class);
//            String[] school = secret.getSchool().split(":");
//            if (school.length > 0){
//                tvUniversity.setText(school[0]);
//            }else{
//                findViewById(R.id.ll_user_secret_school).setVisibility(View.GONE);
//            }
//
//            tvUniversity.setTextColor(getResColor(R.color.edit_user_value));
//        }
        if (TextUtils.isEmpty(secret.getCompany()) || "".equals(secret.getCompany()) || secret.getCompany().contains("null")) {
            if (isMySelf) {
                tvCompany.setText(getString(R.string.space_modify_empty));
                tvCompany.setTextColor(getResColor(R.color.edit_user_tips));
            } else {
                findViewById(R.id.ll_user_secret_company).setVisibility(View.GONE);
            }
        } else {
            tvCompany.setText(secret.getCompany());
            tvCompany.setTextColor(getResColor(R.color.edit_user_value));
        }
        if (TextUtils.isEmpty(secret.getOccupation() + "")) {
            if (isMySelf) {
                tvOccupation.setText(getString(R.string.space_modify_empty));
                tvOccupation.setTextColor(getResColor(R.color.edit_user_tips));
            } else {
                findViewById(R.id.ll_user_secret_occupation).setVisibility(View.GONE);
            }
        } else {
            String[] jobs = getResStringArr(R.array.job);
            if (secret.getOccupation() >= 0) {
                tvOccupation.setText(jobs[secret.getOccupation()] + "");//-1
                tvOccupation.setTextColor(getResColor(R.color.edit_user_value));
            } else {
//                tvOccupation.setText(jobs[secret.getOccupation()] + "");
                tvOccupation.setTextColor(getResColor(R.color.edit_user_tips));
                tvOccupation.setText(getResString(R.string.space_modify_empty));
            }

        }
    }
}
