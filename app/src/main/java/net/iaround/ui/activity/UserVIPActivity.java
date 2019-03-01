package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.im.Me;
import net.iaround.pay.vip.VipPrivilegeBean;
import net.iaround.pay.vip.VipPrivilegeBean.Privileges;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.adapter.VIPBannerAdapter;
import net.iaround.ui.adapter.VIPPowerAdapter;
import net.iaround.ui.datamodel.ResourceBanner;
import net.iaround.ui.datamodel.ResourceListBean;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.utils.eventbus.HeadImageNotifyEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * @author：liush 会员中心界面
 */
public class UserVIPActivity extends TitleActivity implements View.OnClickListener, HttpCallBack {

    private ViewPager vpBanner;
    private int[] vpBannerRes = {R.drawable.vp_demo, R.drawable.vp_demo, R.drawable.vp_demo};
    private ArrayList<ImageView> views = new ArrayList<>();
    private ListView lvMorePower;
    private VIPPowerAdapter vipPowerAdapter;

    private HeadPhotoView ivHead;
    private View viewVIPStatus;
    private Button btnVipPay;
    private LinearLayout llPowerCeoLogo;
    private LinearLayout llPowerKonwVisitor;
    private LinearLayout llPowerLookPic;
    private LinearLayout llHelpPay;
    private TextView tvVipPay;
    private ArrayList<Privileges> privilegesHot;
    private ArrayList<Privileges> privilegesCustom;

    private int[] logoArr = {R.drawable.user_vip_logo_filter, R.drawable.user_vip_logo_name, R.drawable.user_vip_logo_secret, R.drawable.user_vip_logo_sort, R.drawable.user_vip_logo_gift
            , R.drawable.user_vip_logo_face, R.drawable.user_vip_logo_focus, R.drawable.user_vip_logo_circle, R.drawable.user_vip_logo_check_msg, R.drawable.user_vip_logo_add_msg
            , R.drawable.user_vip_logo_show_msg, R.drawable.user_vip_logo_blocked_msg, R.drawable.user_vip_logo_who_like_me, R.drawable.user_vip_logo_undo};
    private String[] powerArr;
    private String[] powerDetailArr;
    private long vipPrivilege;
    private long get_resouce_flag;
    private int vipStatus;
    private static final int SHOW_DATA = 1000;
    private static final int GET_DATA_FAIL = 1001;
    private static final int SHOW_AD_VIEW = 1002;
    private VipPrivilegeBean bean;
    private Me user;
    public ArrayList<ResourceBanner> topbanners;
    private VIPBannerAdapter adapter;
    private TextView tvNumFirst;
    private TextView tvNumSecond;
    private TextView tvNumThird;
    private ImageView ivNumFirst;
    private ImageView ivNumSecond;
    private ImageView ivNumThird;
    private TextView tvVipLight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.user_vip_title), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_user_vip);
        vpBanner = findView(R.id.vp_banner);
        ImageView view1 = findView(R.id.v1);
        view1.setSelected(true);
        views.add(view1);
        ImageView view2 = findView(R.id.v2);
        views.add(view2);
        ImageView view3 = findView(R.id.v3);
        views.add(view3);

        ivHead = findView(R.id.iv_head);
        viewVIPStatus = findView(R.id.view_vip_status);
        btnVipPay = findView(R.id.btn_vip_pay);
        llPowerCeoLogo = findView(R.id.ll_power_ceo_logo);
        llPowerKonwVisitor = findView(R.id.ll_power_know_visitor);
        llPowerLookPic = findView(R.id.ll_power_look_pic);

        lvMorePower = findView(R.id.lv_more_power);
        lvMorePower.setFocusable(false);//防止进入界面跳转到listview的位置

        llHelpPay = findView(R.id.ll_help_pay);

        tvVipPay = findView(R.id.tv_vip_pay);
        tvNumFirst = (TextView) findViewById(R.id.tv_NumFirst);
        tvNumSecond = (TextView) findViewById(R.id.tv_NumSecond);
        tvNumThird = (TextView) findViewById(R.id.tv_NumThird);
        ivNumFirst = (ImageView) findViewById(R.id.iv_NumFirst);
        ivNumSecond = (ImageView) findViewById(R.id.iv_NumSecond);
        ivNumThird = (ImageView) findViewById(R.id.iv_NumThird);
        tvVipLight = (TextView) findViewById(R.id.tv_vp_light);
    }

    private void initDatas() {
        topbanners = new ArrayList<>();
        privilegesHot = new ArrayList<>();
        privilegesCustom = new ArrayList<>();
        user = Common.getInstance().loginUser;
        powerArr = getResStringArr(R.array.user_vip_power);
        powerDetailArr = getResStringArr(R.array.user_vip_power_detail);
        Intent intent = getIntent();
//        ImageViewUtil.getDefault().loadImage(intent.getStringExtra(Constants.USER_HEAD_URL), ivHead, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);
//        GlideUtil.loadImage(mContext, intent.getStringExtra(Constants.USER_HEAD_URL), ivHead, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);

//        int vipStatus = intent.getIntExtra(Constants.USER_VIP_STATUS, 0);
        vipStatus = user.getSVip();
        if (vipStatus == Constants.USER_VIP || vipStatus == Constants.USER_SVIP) {
            viewVIPStatus.setBackgroundResource(R.drawable.user_vip_on_new);
            tvVipLight.setText(getResources().getString(R.string.user_vip_base_status));
            btnVipPay.setText(getString(R.string.user_vip_pay));
            tvVipPay.setText(getString(R.string.user_vip_pay));
        } else {
            tvVipLight.setText(getResources().getString(R.string.user_vip_base_status_no));
            viewVIPStatus.setBackgroundResource(R.drawable.user_vip_off);
            btnVipPay.setText(getString(R.string.user_vip_open));
            tvVipPay.setText(getString(R.string.user_vip_open));
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivHead.execute(Common.getInstance().loginUser,1);
//                ivHead.execute(ChatFromType.UNKONW, Common.getInstance().loginUser, null);
            }
        },300);



//        vpBanner.setAdapter(new VIPBannerAdapter(vpBannerRes));
//        vipPowerAdapter = new VIPPowerAdapter(logoArr, powerArr, powerDetailArr);
//        vipPowerAdapter = new VIPPowerAdapter(UserVIPActivity.this, privilegesCustom);
//        lvMorePower.setAdapter(new VIPPowerAdapter(UserVIPActivity.this,privilegesCustom));
//        lvMorePower.setAdapter(new VIPPowerAdapter(logoArr, powerArr, powerDetailArr));
//        CommonFunction.setListViewHeightBasedOnChildren(lvMorePower);
//        CommonFunction.setListViewHeightBasedOnChildrenOne(lvMorePower);
        requestData();
    }

    private void initListeners() {
        vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < views.size(); i++) {
                    if (position == i) {
                        views.get(i).setSelected(true);
                    } else {
                        views.get(i).setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        lvMorePower.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(), "跳转到webview页面", Toast.LENGTH_SHORT).show();
            }
        });

        btnVipPay.setOnClickListener(this);
        tvVipPay.setOnClickListener(this);
        llPowerCeoLogo.setOnClickListener(this);
        llPowerKonwVisitor.setOnClickListener(this);
        llPowerLookPic.setOnClickListener(this);
        llHelpPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_vip_pay:
            case R.id.tv_vip_pay:
                intent.setClass(this, UserVipOpenActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_power_ceo_logo:
//                Toast.makeText(this, "跳转至专属名牌标识界面", Toast.LENGTH_SHORT).show();
                if (privilegesHot != null) {
//                    WebViewAvtivity.launchVerifyCode(UserVIPActivity.this, 1, privilegesHot.get(0).url);

                    String url = CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(0).url);
                    Uri uri = Uri.parse(url);
                    Intent i = new Intent(UserVIPActivity.this, WebViewAvtivity.class);
                    String name = CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(0).name);
                    i.putExtra(WebViewAvtivity.WEBVIEW_TITLE, name);
                    i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
                    startActivity(i);
                }
                break;
            case R.id.ll_power_know_visitor:
//                Toast.makeText(this, "跳转至访客全知道界面", Toast.LENGTH_SHORT).show();
                if (privilegesHot != null) {
//                    WebViewAvtivity.launchVerifyCode(UserVIPActivity.this, 1, privilegesHot.get(1).url);

                    String url = CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(1).url);
                    Uri uri = Uri.parse(url);
                    Intent i = new Intent(UserVIPActivity.this, WebViewAvtivity.class);
                    String name = CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(1).name);
                    i.putExtra(WebViewAvtivity.WEBVIEW_TITLE, name);
                    i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
                    startActivity(i);
                }
                break;
            case R.id.ll_power_look_pic:
//                Toast.makeText(this, "跳转至照片随便看界面", Toast.LENGTH_SHORT).show();
                if (privilegesHot != null) {
//                    WebViewAvtivity.launchVerifyCode(UserVIPActivity.this, 1, privilegesHot.get(2).url);

                    String url = CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(2).url);
                    Uri uri = Uri.parse(url);
                    Intent i = new Intent(UserVIPActivity.this, WebViewAvtivity.class);
                    String name = CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(2).name);
                    i.putExtra(WebViewAvtivity.WEBVIEW_TITLE, name);
                    i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
                    startActivity(i);
                }
                break;
            case R.id.ll_help_pay:
//                Toast.makeText(this, "跳转至充值帮助识界面", Toast.LENGTH_SHORT).show();
                jumpWebViewActivity(2);
                break;
        }
    }

    public static void startAction(Context context, String headUrl, int vipStatus) {
        Intent intent = new Intent(context, UserVIPActivity.class);
        intent.putExtra(Constants.USER_HEAD_URL, headUrl);
        intent.putExtra(Constants.USER_VIP_STATUS, vipStatus);
        context.startActivity(intent);
    }

    private void requestData() {
        vipPrivilege = GoldHttpProtocol.getMemberPrivilegeList(mContext, this);
        get_resouce_flag = BusinessHttpProtocol.getResourceList(mContext, Config.PLAT, 6, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == vipPrivilege) {
            VipPrivilegeBean data = GsonUtil.getInstance().getServerBean(result, VipPrivilegeBean.class);
            if (data != null && data.isSuccess()) {
                Message msg = Message.obtain();
                msg.what = SHOW_DATA;
                msg.obj = data;
                mHandler.sendMessage(msg);


                String cacheStr = GsonUtil.getInstance().getStringFromJsonObject(data);
                SharedPreferenceCache.getInstance(mContext).putString(
                        "vip_priviliege" + user.getUid(), cacheStr);
            } else {
                Message msg = Message.obtain();
                msg.what = GET_DATA_FAIL;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        } else if (flag == get_resouce_flag) {

            ResourceListBean bean = GsonUtil.getInstance().getServerBean(result,
                    ResourceListBean.class);
            if (bean != null && bean.isSuccess() && bean.topbanners != null) {
                topbanners.addAll(bean.topbanners);
                Message msg = Message.obtain();
                msg.what = SHOW_AD_VIEW;
                mHandler.sendMessage(msg);
            } else {
                //广告资源获取失败
                Message msg = Message.obtain();
                msg.what = GET_DATA_FAIL;
                msg.obj = result;
                mHandler.sendMessage(msg);

            }

        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATA:
                    bean = (VipPrivilegeBean) msg.obj;
                    vipStatus = bean.svip;
                    initVipLight();
                    initVipPrivileHot();
                    initVipPrivileCustom();
                    Common.getInstance().loginUser.setSVip(bean.svip);
                    Common.getInstance().loginUser.setViplevel(bean.viplevel);
                    handleShowData();
                    break;

                case GET_DATA_FAIL:
//                    hideProgressDialog();
                    break;
                case SHOW_AD_VIEW:
                    handleShowTopBanners();
                    break;
            }
        }
    };

    private void handleShowTopBanners() {

        if (topbanners != null && topbanners.size() > 0) {
            for (int i = 0; i < topbanners.size(); i++) {
                String iconUrl = topbanners.get(i).getImageUrl();
                GlideUtil.loadImage(BaseApplication.appContext, iconUrl, views.get(i));

            }
            vpBanner.setAdapter(new VIPBannerAdapter(UserVIPActivity.this, topbanners));
//            adapter = new VIPBannerAdapter(UserVIPActivity.this, topbanners);
//            vpBanner.setAdapter(adapter);
//            adapter.setDataChange(topbanners);
        }
    }

    public void handleShowData() {
        showVipPrivileHot();
        showVipPrivileCustom();
        initVipLight();
    }

    public void initVipPrivileHot() {
        if (bean != null) {
            if (bean.privileges.size() > 0) {
                for (int i = 0; i < 3; i++) {
                    privilegesHot.add(bean.privileges.get(i));

                }
            }
        }

    }

    public void initVipLight() {
        if (vipStatus == Constants.USER_VIP || vipStatus == Constants.USER_SVIP) {
            viewVIPStatus.setBackgroundResource(R.drawable.user_vip_on);
            tvVipLight.setText(getResources().getString(R.string.user_vip_base_status));
            btnVipPay.setText(getString(R.string.user_vip_pay));
            tvVipPay.setText(getString(R.string.user_vip_pay));
        } else {
            viewVIPStatus.setBackgroundResource(R.drawable.user_vip_off);
            tvVipLight.setText(getResources().getString(R.string.user_vip_base_status_no));
            btnVipPay.setText(getString(R.string.user_vip_open));
            tvVipPay.setText(getString(R.string.user_vip_open));
        }
    }

    public void initVipPrivileCustom() {
        if (bean != null) {
            if (bean.privileges.size() > 0) {
                for (int i = 3; i < bean.privileges.size(); i++) {
                    privilegesCustom.add(bean.privileges.get(i));

                }
            }
        }
    }

    public void showVipPrivileCustom() {
//        vipPowerAdapter.setDataNotify(privilegesCustom);

        lvMorePower.setAdapter(new VIPPowerAdapter(UserVIPActivity.this, privilegesCustom));
        CommonFunction.setListViewHeightBasedOnChildrenOne(lvMorePower);

    }

    public void showVipPrivileHot() {
        if (privilegesHot.size() > 0) {
//            String title = CommonFunction.getLangText(mContext, data.name);
//            String content = CommonFunction.getLangText( mContext, data.resume);
            tvNumFirst.setText(CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(0).name));
            tvNumSecond.setText(CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(1).name));
            tvNumThird.setText(CommonFunction.getLangText(UserVIPActivity.this, privilegesHot.get(2).name));
            GlideUtil.loadCircleImage(BaseApplication.appContext, privilegesHot.get(0).icon, ivNumFirst);
            GlideUtil.loadCircleImage(BaseApplication.appContext, privilegesHot.get(1).icon, ivNumSecond);
            GlideUtil.loadCircleImage(BaseApplication.appContext, privilegesHot.get(2).icon, ivNumThird);
        }
    }

    private void jumpWebViewActivity(int type) {
        String str = "";
        String url = "";
        if (type == 1) {
            str = getString(R.string.vip_protocol);
            url = CommonFunction.getLangText(mContext, Config.VIP_AGREEMENT_URL);
        } else {
            url = CommonFunction.getLangText(mContext, Config.iAroundPayFAQUrl);
            str = getResString(R.string.common_questions);
        }
        Intent intent = new Intent(mContext, WebViewAvtivity.class);
        intent.putExtra(WebViewAvtivity.WEBVIEW_TITLE, str);
        intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        startActivity(intent);
    }


    @Subscribe
    public void onEventMainThread(HeadImageNotifyEvent event) {
        if (ivHead != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(ivHead != null) {
                        ivHead.execute(Common.getInstance().loginUser, 1);
                    }

                    if (viewVIPStatus != null && tvVipLight != null && btnVipPay != null && tvVipPay != null) {
                        viewVIPStatus.setBackgroundResource(R.drawable.user_vip_on_new);
                        tvVipLight.setText(getResources().getString(R.string.user_vip_base_status));
                        btnVipPay.setText(getString(R.string.user_vip_pay));
                        tvVipPay.setText(getString(R.string.user_vip_pay));
                    }
                }
            });

        }
    }

}
