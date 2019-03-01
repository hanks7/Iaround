package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.contract.UserVipOpenContract;
import net.iaround.pay.bean.VipBuyMemberListBean;
import net.iaround.presenter.UserVipOpenPresenter;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.adapter.VIPPowerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：liush 开通会员（当在某一界面需要引导用户开通会员的时候，跳转至此界面）
 */
public class UserVipPayActivity extends TitleActivity implements View.OnClickListener, UserVipOpenContract.View{

    private UserVipOpenPresenter presenter;
    private List<LinearLayout> packageList = new ArrayList<>();
    private List<Boolean> packageListStatus = new ArrayList<>();

    private int lastSelected = 0;
    private LinearLayout llOpenVipSixMonth;
    private LinearLayout llOpenVipTwelveMonth;
    private LinearLayout llOpenVipThreeMonth;
    private LinearLayout llOpenVipOneMonth;

    private ListView lvMorePower;

    private int[] logoArr = {R.drawable.user_vip_logo_filter, R.drawable.user_vip_logo_name, R.drawable.user_vip_logo_secret, R.drawable.user_vip_logo_sort, R.drawable.user_vip_logo_gift
            , R.drawable.user_vip_logo_face, R.drawable.user_vip_logo_focus, R.drawable.user_vip_logo_circle, R.drawable.user_vip_logo_check_msg, R.drawable.user_vip_logo_add_msg
            , R.drawable.user_vip_logo_show_msg, R.drawable.user_vip_logo_blocked_msg, R.drawable.user_vip_logo_who_like_me, R.drawable.user_vip_logo_undo};
    private String[] powerArr;
    private String[] powerDetailArr;

    private TextView tvPackageSixMonth;
    private TextView tvPackageSixDollar;
    private TextView tvPackageSixSave;
    private TextView tvPackageTwelveMonth;
    private TextView tvPackageTwelveDollar;
    private TextView tvPackageTwelveSave;
    private TextView tvPackageThreeMonth;
    private TextView tvPackageThreeDollar;
    private TextView tvPackageThreeSave;
    private TextView tvPackageOneMonth;
    private TextView tvPackageOneDollar;
    private TextView tvPackageOneSave;
    private String packagePrefix;
    private String packageSuffix;
    private LinearLayout llPowerCeoLogo;
    private LinearLayout llPowerKnowVisitor;
    private LinearLayout llPowerLookPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new UserVipOpenPresenter(this);
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
        }, getString(R.string.user_vip_pay_title), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_user_vip_pay);
        llOpenVipSixMonth = findView(R.id.ll_open_vip_six_month);
        tvPackageSixMonth = findView(R.id.tv_package_six_month);
        tvPackageSixDollar = findView(R.id.tv_package_six_dollar);
        tvPackageSixSave = findView(R.id.tv_package_six_save);
        packageList.add(llOpenVipSixMonth);
        packageListStatus.add(false);
        setSelectedPackage(0);
        llOpenVipTwelveMonth = findView(R.id.ll_open_vip_twelve_month);
        tvPackageTwelveMonth = findView(R.id.tv_package_twelve_month);
        tvPackageTwelveDollar = findView(R.id.tv_package_twelve_dollar);
        tvPackageTwelveSave = findView(R.id.tv_package_twelve_save);
        packageList.add(llOpenVipTwelveMonth);
        packageListStatus.add(false);
        llOpenVipThreeMonth = findView(R.id.ll_open_vip_three_month);
        tvPackageThreeMonth = findView(R.id.tv_package_three_month);
        tvPackageThreeDollar = findView(R.id.tv_package_three_dollar);
        tvPackageThreeSave = findView(R.id.tv_package_three_save);
        packageList.add(llOpenVipThreeMonth);
        packageListStatus.add(false);
        llOpenVipOneMonth = findView(R.id.ll_open_vip_one_month);
        tvPackageOneMonth = findView(R.id.tv_package_one_month);
        tvPackageOneDollar = findView(R.id.tv_package_one_dollar);
        tvPackageOneSave = findView(R.id.tv_package_one_save);
        packageList.add(llOpenVipOneMonth);
        packageListStatus.add(false);
        llPowerCeoLogo = findView(R.id.ll_power_ceo_logo);
        llPowerKnowVisitor = findView(R.id.ll_power_know_visitor);
        llPowerLookPic = findView(R.id.ll_power_look_pic);
        lvMorePower = findView(R.id.lv_more_power);
        lvMorePower.setFocusable(false);//防止进入界面跳转到listview的位置
    }

    private void initDatas() {
        packagePrefix = getResString(R.string.user_vip_open_month_prefix);
        packageSuffix = getResString(R.string.user_vip_open_price_suffix);
        powerArr = getResStringArr(R.array.user_vip_power);
        powerDetailArr = getResStringArr(R.array.user_vip_power_detail);
        lvMorePower.setAdapter(new VIPPowerAdapter(logoArr, powerArr, powerDetailArr));
        CommonFunction.setListViewHeightBasedOnChildren(lvMorePower);
//        presenter.init(CommonFunction.getLanguageIndex( mContext ), CommonFunction.getPackageMetaData(this));
    }

    private void initListeners() {
        llOpenVipSixMonth.setOnClickListener(this);
        llOpenVipTwelveMonth.setOnClickListener(this);
        llOpenVipThreeMonth.setOnClickListener(this);
        llOpenVipOneMonth.setOnClickListener(this);
        llPowerCeoLogo.setOnClickListener(this);
        llPowerKnowVisitor.setOnClickListener(this);
        llPowerLookPic.setOnClickListener(this);
        lvMorePower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "根据position的值，跳转至相应的界面", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSelectedPackage(int index){
        packageList.get(lastSelected).setSelected(false);
        packageListStatus.set(lastSelected, false);
        lastSelected = index;
        packageList.get(index).setSelected(true);
        packageListStatus.set(index, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open_vip_six_month:
                if (packageListStatus.get(0)) {
                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
                } else {
                    setSelectedPackage(0);
                }
                break;
            case R.id.ll_open_vip_twelve_month:
                if (packageListStatus.get(1)) {
                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
                } else {
                    setSelectedPackage(1);
                }
                break;
            case R.id.ll_open_vip_three_month:
                if (packageListStatus.get(2)) {
                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
                } else {
                    setSelectedPackage(2);
                }
                break;
            case R.id.ll_open_vip_one_month:
                if (packageListStatus.get(3)) {
                    Toast.makeText(this, "跳转到充值界面", Toast.LENGTH_SHORT).show();
                } else {
                    setSelectedPackage(3);
                }
                break;
            case R.id.ll_power_ceo_logo:
                Toast.makeText(this, "专属名牌标识", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_power_know_visitor:
                Toast.makeText(this, "访客全知道", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_power_look_pic:
                Toast.makeText(this, "照片随心看", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static void startAction(Context context){
        Intent intent = new Intent(context, UserVipPayActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setPackageData(ArrayList<VipBuyMemberListBean.Goods> goods) {
        /**
         * 由于用的老接口，新的项目数据顺序有改动 goods:   index 0:十二个月    1：一个月   2：三个月   3：六个月（不同语言顺序不一样）
         * 这个新版本不同语言顺序一样，后台直接改顺序即可
         */
        if(goods!=null && goods.size()>0){
            tvPackageSixMonth.setText(goods.get(0).month+packageSuffix);
            tvPackageSixDollar.setText(packagePrefix+goods.get(0).price);
            if(!TextUtils.isEmpty(goods.get(0).discount)){
                tvPackageSixSave.setVisibility(View.VISIBLE);
                tvPackageSixSave.setText(CommonFunction.getLangText(this, goods.get(0).discount));
            } else {
                tvPackageSixSave.setVisibility(View.GONE);
            }

            tvPackageTwelveMonth.setText(goods.get(1).month+packageSuffix);
            tvPackageTwelveDollar.setText(packagePrefix+goods.get(1).price);
            if(!TextUtils.isEmpty(goods.get(1).discount)){
                tvPackageTwelveSave.setVisibility(View.VISIBLE);
                tvPackageTwelveSave.setText(CommonFunction.getLangText(this, goods.get(1).discount));
            } else {
                tvPackageTwelveSave.setVisibility(View.GONE);
            }

            tvPackageThreeMonth.setText(goods.get(2).month+packageSuffix);
            tvPackageThreeDollar.setText(packagePrefix+goods.get(2).price);
            if(!TextUtils.isEmpty(goods.get(2).discount)){
                tvPackageThreeSave.setVisibility(View.VISIBLE);
                tvPackageThreeSave.setText(CommonFunction.getLangText(this, goods.get(2).discount));
            } else {
                tvPackageThreeSave.setVisibility(View.GONE);
            }

            tvPackageOneMonth.setText(goods.get(3).month+packageSuffix);
            tvPackageOneDollar.setText(packagePrefix+goods.get(3).price);
            if(!TextUtils.isEmpty(goods.get(3).discount)){
                tvPackageOneSave.setVisibility(View.VISIBLE);
                tvPackageOneSave.setText(CommonFunction.getLangText(this, goods.get(3).discount));
            } else {
                tvPackageOneSave.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showDialog() {
        showWaitDialog();
    }

    @Override
    public void hideDialog() {
        hideWaitDialog();
    }
}
