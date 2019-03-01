package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.DataTag;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.view.encounter.RangeSeekBar;

import java.util.ArrayList;

/**
 * Class: 筛选
 * Author：gh
 * Date: 2016/12/8 23:33
 * Emial：jt_gaohang@163.com
 */
public class FilterActivity extends TitleActivity implements View.OnClickListener{

    public static String KEY_FILTER = "filter";// 0 代表匹配筛选，1 代表动态筛选，2代表人筛选

    private TextView mTvTitle;
    private ImageView mIvLeft,mIvRight;
    private FrameLayout flLeft;

    private LinearLayout lyFilter;
    private TextView btnMan;
    private TextView btnWoMan;
    private TextView btnAll;
    private RangeSeekBar rsAge;
    private TextView tvAgeFilter;

    private RelativeLayout rlConstellation;
    private LinearLayout lyConstellation;
    private TextView tvConstellation;

    private LinearLayout lyTime;
    private TextView tvMinute;
    private TextView tvHours;
    private TextView tvDay;
    private TextView tv3Day;

    private OptionsPickerView singlePicker;

    private int wantpeople;
    private int wanttime;
    private int constellation;
    private int filter;
    private int maxAge = 60;
    private int minAge = 16;

    private boolean isVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
//        setActionBarTitle(R.string.title_filter);

        initView();
        initActionBar();
        filter = getIntent().getIntExtra(KEY_FILTER,0);
        initData();

//        leftActionBarEvent(getString(R.string.cancel));
//        rightActionBarEvent(getString(R.string.ok));
    }
    private void initActionBar()
    {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);

        mIvRight.setVisibility(View.VISIBLE);
        mIvLeft.setVisibility(View.VISIBLE);
        mIvRight.setVisibility(View.VISIBLE);

        mTvTitle.setText(getResources().getString(R.string.title_filter));
        mIvLeft.setImageResource(R.drawable.title_back);
        mIvRight.setImageResource(R.drawable.icon_publish);

        mIvRight.setOnClickListener(this);
        mIvLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
    }
    private void initView(){
        lyFilter = (LinearLayout) findViewById(R.id.ly_filter);
        btnMan = (TextView) findViewById(R.id.tv_filter_man);
        btnWoMan = (TextView) findViewById(R.id.tv_filter_woman);
        btnAll = (TextView) findViewById(R.id.tv_filter_all);
        tvAgeFilter = (TextView) findViewById(R.id.tv_filter_age);
        rsAge = (RangeSeekBar) findViewById(R.id.rs_filter_age);
        rlConstellation = (RelativeLayout) findViewById(R.id.rl_constellation);
        lyConstellation = (LinearLayout) findViewById(R.id.ly_constellation);
        tvConstellation = (TextView) findViewById(R.id.tv_constellation);
        lyTime = (LinearLayout) findViewById(R.id.ly_filter_time);
        tvMinute = (TextView) findViewById(R.id.tv_filter_minute);
        tvHours = (TextView) findViewById(R.id.tv_filter_hours);
        tvDay = (TextView) findViewById(R.id.tv_filter_day);
        tv3Day = (TextView) findViewById(R.id.tv_filter_3day);

        btnMan.setOnClickListener(this);
        btnWoMan.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        lyConstellation.setOnClickListener(this);

        tvMinute.setOnClickListener(this);
        tvHours.setOnClickListener(this);
        tvDay.setOnClickListener(this);
        tv3Day.setOnClickListener(this);

        rsAge.setOnClickListener(this);

    }

    private void initData(){
//        BaseBean<UserProfileBean> bean = BaseBean.fromJson(Common.getInstance().getUserProfile(),UserProfileBean.class);
//        if (bean == null)
//            return;
//        if (bean != null){
//            if (bean.getData() != null) {
//
//            }
//        }
        isVip = Common.getInstance( ).loginUser.isVip( );
        String[] items = getResources().getStringArray(R.array.horoscope_date);
        final ArrayList<String> list = new ArrayList<String>();
        for (String constellation : items){
            list.add(constellation);
        }


        if (filter == 0){//匹配筛选
            maxAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.ENCOUNTER_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(),60);
            minAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.ENCOUNTER_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(),16);
            wantpeople = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.ENCOUNTER_FILTER_GENDER + Common.getInstance().loginUser.getUid());
        }else if (filter == 1){//动态筛选
//            lyFilter.setVisibility(View.VISIBLE);
//            lyTime.setVisibility(View.VISIBLE);
            maxAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.DYNAMIC_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(),60);
            minAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.DYNAMIC_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(),16);
            wantpeople = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.DYNAMIC_FILTER_GENDER + Common.getInstance().loginUser.getUid());
        }else if (filter == 2){//附近人筛选
            lyTime.setVisibility(View.VISIBLE);
            tvAgeFilter.setVisibility(View.VISIBLE);
            rsAge.setVisibility(View.VISIBLE);
            lyFilter.setVisibility(View.VISIBLE);
            rlConstellation.setVisibility(View.VISIBLE);
            maxAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(),60);
            minAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(),16);
            wantpeople = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_GENDER + Common.getInstance().loginUser.getUid());
            constellation = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_CONSTELLATION + Common.getInstance().loginUser.getUid());

            wanttime = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_TIME + Common.getInstance().loginUser.getUid(),3);
            timeChek( wanttime );

            tvConstellation.setText(list.get(constellation));
        }

        rsAge.setRangeValues(16,80);
        rsAge.setSelectedMaxValue(maxAge);
        rsAge.setSelectedMinValue(minAge);

        genderChek(wantpeople);

        singlePicker = new OptionsPickerView(this);
        singlePicker.setPicker(list);
        singlePicker.setTitle("");
        singlePicker.setCyclic(false);
        singlePicker.setSelectOptions(constellation);
        singlePicker.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                constellation = options1;
                tvConstellation.setText(list.get(options1));
            }
        });

    }

    @Override
    public void onClick(View view) {

            switch (view.getId()){
                case R.id.tv_filter_man://男生
                    if (filter == 1 && !isVip){
                        DialogUtil
                                .showTobeVipDialog( FilterActivity.this, R.string.tost_filter_vip_title,
                                        R.string.tost_filter_vip_privilege );

                        return;
                    }
                    wantpeople = 1;
                    genderChek(wantpeople);
                    break;
                case R.id.tv_filter_woman://女生
                    if (filter == 1 && !isVip){
                        DialogUtil
                                .showTobeVipDialog( FilterActivity.this, R.string.tost_filter_vip_title,
                                        R.string.tost_filter_vip_privilege );

                        return;
                    }
                    wantpeople = 2;
                    genderChek(wantpeople);
                    break;
                case R.id.tv_filter_all://不限
                    if (filter == 1 && !isVip){
                        DialogUtil
                                .showTobeVipDialog( FilterActivity.this, R.string.tost_filter_vip_title,
                                        R.string.tost_filter_vip_privilege );

                        return;
                    }
                    wantpeople = 0;
                    genderChek(wantpeople);
                    break;
                case R.id.ly_constellation://星座
                    if (!isVip){
                        DialogUtil.showTobeVipDialog( FilterActivity.this ,
                                R.string.nearby_vip_promption_dailog_title ,
                                R.string.nearby_vip_promption_dailog_msg , DataTag.VIEW_near_filtrate);
                        return;
                    }
                    singlePicker.show();
                    break;
                case R.id.rs_filter_age://年龄
                    if (!isVip)
                    {
                        DialogUtil.showTobeVipDialog( FilterActivity.this ,
                                R.string.nearby_vip_promption_dailog_title ,
                                R.string.nearby_vip_promption_dailog_msg , DataTag.VIEW_near_filtrate);
                        return;
                    }

                    break;
                case R.id.tv_filter_minute://15分钟
                    wanttime = 0;
                    timeChek(wanttime);
                    break;
                case R.id.tv_filter_hours://1小时
                    wanttime = 1;
                    timeChek(wanttime);
                    break;
                case R.id.tv_filter_day://1天
                    wanttime = 2;
                    timeChek(wanttime);
                    break;
                case R.id.tv_filter_3day://3天
                    wanttime = 3;
                    timeChek(wanttime);
                    break;
                case R.id.iv_left:
                case R.id.fl_left:
                    finish();
                    break;
                case R.id.iv_right:
                    if (!isVip && filter == 1)
                    {
                        DialogUtil.showTobeVipDialog( FilterActivity.this ,
                                R.string.nearby_vip_promption_dailog_title ,
                                R.string.nearby_vip_promption_dailog_msg , DataTag.VIEW_near_filtrate);
                        return;
                    }
                    actionBarRightGoPressed();
                    break;
            }
    }

    private void genderChek(final int i){

        if (i == 1){
            btnMan.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            btnWoMan.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            btnAll.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            btnMan.setTextColor(getResources().getColor(R.color.common_white));
            btnWoMan.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            btnAll.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
        }else if(i == 2){
            btnMan.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            btnWoMan.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            btnAll.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            btnMan.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            btnWoMan.setTextColor(getResources().getColor(R.color.common_white));
            btnAll.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
        }else if (i == 0){
            btnMan.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            btnWoMan.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            btnAll.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            btnMan.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            btnWoMan.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            btnAll.setTextColor(getResources().getColor(R.color.common_white));
        }

    }

    private void timeChek(final int i){

       if (i == 3){
            tvMinute.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvHours.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvDay.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tv3Day.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            tvMinute.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tvHours.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tvDay.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tv3Day.setTextColor(getResources().getColor(R.color.common_white));
        }else if (i == 2){
            tvMinute.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvHours.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvDay.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            tv3Day.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvMinute.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tvHours.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tvDay.setTextColor(getResources().getColor(R.color.common_white));
            tv3Day.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
        }else if (i == 1){
            tvMinute.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvHours.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            tvDay.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tv3Day.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvMinute.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tvHours.setTextColor(getResources().getColor(R.color.common_white));
            tvDay.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tv3Day.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
        }else if (i == 0){
            tvMinute.setBackgroundResource(R.drawable.btn_encounter_filter_sex_bg);
            tvHours.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvDay.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tv3Day.setBackgroundResource(R.drawable.btn_encounter_filter_sex_unbg);
            tvMinute.setTextColor(getResources().getColor(R.color.common_white));
            tvHours.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tvDay.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
            tv3Day.setTextColor(getResources().getColor(R.color.encounter_filter_sex_selcet));
        }

    }

//    @Override
    protected void actionBarRightGoPressed() {
//        super.actionBarRightGoPressed();

        int currentMinAge = rsAge.getSelectedMinValue().intValue();
        int currentMaxAge = rsAge.getSelectedMaxValue().intValue();

        if (currentMinAge == currentMaxAge){
            CommonFunction.toastMsg(FilterActivity.this,getResString(R.string.filter_age_error));
            return;
        }

        if (filter == 0){
            int minAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.ENCOUNTER_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid());
            int maxAge = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.ENCOUNTER_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid());
            int people = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.ENCOUNTER_FILTER_GENDER + Common.getInstance().loginUser.getUid());
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.ENCOUNTER_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(),currentMinAge);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.ENCOUNTER_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(),currentMaxAge);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.ENCOUNTER_FILTER_GENDER + Common.getInstance().loginUser.getUid(),wantpeople);
            if (currentMinAge != minAge || currentMaxAge != maxAge || people != wantpeople){
                setResult(RESULT_OK);
            }
        }else if (filter == 1){

            int people = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.DYNAMIC_FILTER_GENDER + Common.getInstance().loginUser.getUid());
            if (!isVip) {
                if (wantpeople != people) {
                    DialogUtil
                            .showTobeVipDialog(FilterActivity.this, R.string.tost_filter_dynamic_vip_title,
                                    R.string.tost_filter_vip_privilege);
                    return;
                }
            }

            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.DYNAMIC_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(),currentMinAge);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.DYNAMIC_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(),currentMaxAge);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.DYNAMIC_FILTER_GENDER + Common.getInstance().loginUser.getUid(),wantpeople);
            if (currentMinAge != minAge || currentMaxAge != maxAge || people != wantpeople){
                setResult(RESULT_OK);
            }
        }else if (filter == 2){
            int people = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_GENDER + Common.getInstance().loginUser.getUid());
            int horoscope = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_CONSTELLATION + Common.getInstance().loginUser.getUid());
            int time = SharedPreferenceUtil.getInstance(this).getInt(SharedPreferenceUtil.NEAR_FILTER_TIME + Common.getInstance().loginUser.getUid());
            if (!isVip)
            {
                if (currentMinAge != minAge | currentMaxAge != maxAge){
                    DialogUtil
                            .showTobeVipDialog( FilterActivity.this, R.string.nearby_vip_promption_dailog_title,
                                    R.string.nearby_vip_promption_dailog_msg );
                    return;
                }
            }

            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.NEAR_FILTER_MIN_AGE + Common.getInstance().loginUser.getUid(),currentMinAge);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.NEAR_FILTER_MAX_AGE + Common.getInstance().loginUser.getUid(),currentMaxAge);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.NEAR_FILTER_GENDER  + Common.getInstance().loginUser.getUid(),wantpeople);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.NEAR_FILTER_CONSTELLATION + Common.getInstance().loginUser.getUid(),constellation);
            SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.NEAR_FILTER_TIME + Common.getInstance().loginUser.getUid(),wanttime);
            if (currentMinAge != minAge || currentMaxAge != maxAge || people != wantpeople || horoscope != constellation || time != wanttime){
                setResult(RESULT_OK);
            }
        }
        finish();
    }

    /**
     * 跳转到筛选
     *
     * @param mContext
     * @param fragment
     * @param requestCode
     */
    public static void skipToFilter(Context mContext, Fragment fragment,
                                           int filter, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mContext, DynamicDetailActivity.class);
        intent.putExtra(KEY_FILTER, filter);
        fragment.startActivityForResult(intent, requestCode);
    }
}
