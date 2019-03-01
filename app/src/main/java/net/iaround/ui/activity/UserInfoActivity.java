package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.contract.UserInfoContract;
import net.iaround.model.entity.UserInfoEntity;
import net.iaround.presenter.UserInfoPresenter;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.adapter.UserInfoPicVPAdapter;
import net.iaround.ui.adapter.UserInfoUserActionAdapter;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.Photos;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.dynamic.PublishDynamicActivity;
import net.iaround.ui.map.MapUtils;
import net.iaround.ui.store.StoreReceiveGiftActivity;
import net.iaround.ui.view.MyViewPager;
import net.iaround.ui.view.user.FlowLayout;
import net.iaround.utils.eventbus.HeadImageNotifyEvent;
import net.iaround.utils.eventbus.NickNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static net.iaround.R.id.iv_right;
import static net.iaround.R.id.nullStr;

//import net.iaround.model.database.Lable;
//import org.litepal.crud.DataSupport;

/**
 * @author：liush on 2016/12/3 15:42
 */
public class UserInfoActivity extends TitleActivity implements UserInfoContract.View, View.OnClickListener {

    private MyViewPager vpPicture;
    private UserInfoContract.Persenter userInfoPersenter;
    private RelativeLayout rlAgeSex;
    private ImageView ivSex;
    private TextView tvAge;
    private TextView tvActionNum;
    private TextView tvHoroscope;
    private TextView tvSignature;
    private FlowLayout flowLayoutHobby;
    //    private GridView gvFriendsAction;
    private TextView tvTitle;
    private TextView tvEdit;
    private GridView gvUserAction;
    private GridView gvUsergift;
    private LinearLayout llUserAction;
    private LinearLayout llUserVip;
    private LinearLayout llUserLevelInfo;
    private LinearLayout llUserLevel;
    private ImageView ivUserVipIcon;
    private TextView tvVipStatus;
    private TextView tvUserLevel;
    private LinearLayout llUserGift;
    private TextView tvUserId;
    private TextView tvLoveStatus;
    private TextView tvUserHeight;
    private TextView tvUserWeight;
    private TextView tvUserBirthday;
    private TextView tvUserHometown;
    private LinearLayout llUserSecret;
    //    private LinearLayout llFriendsAction;
    private LinearLayout llUserPhoneAuthen;
    private TextView tvPhoneAuthenStatus;
    private LinearLayout llUserCameraAuthen;
    private TextView tvCameraAuthenStatus;
    private TextView tvUserLastLocal;
    private Bundle locationBundle = new Bundle();
    private TextView tvUserPhone;
    private TextView tvUserJob;
    private ImageView ivHeadPic;
    private TextView tvAboutMePercent;
    private ImageView iv_dynamic;

    private int picAuthenStatus;
    private int phoneAuthenStatus = 2;

    private long lat;
    private long lng;


    private static final int REQ_EDIT_USERINFO = 98;
    private static final int REQ_AUTHEN_PIC = 99;
    private static final int REQ_RELEASE_AUTHEN_PIC = 100;
    private static final int REQ_PHONE_AUTHEN_SUC = 101;
    private static final int REQ_PHONE_AUTHEN_NO = 102;
    private static final int REQ_AUTHEN_VIVIFY_PWD = 103;
    private static final int REQ_PHONE_AUTHEN = 104;

    private final static int PUBLISH_REQUEST_CODE = 1002;// 发布动态requestCode

    private ArrayList<String> gifts;
    private LinearLayout llUserLastLocal;
    private LinearLayout llUserPhone;
    private LinearLayout llUserId;
    private LinearLayout llLoveStatus;
    private LinearLayout llUserHeight;
    private LinearLayout llUserWeight;
    private LinearLayout llUserJob;
    private LinearLayout llUserHometown;

    private User tempuser;
    private User loginuser;
    private User frienduser;
    private boolean isMine;

    /**
     * 标识会员状态，控制点击会员跳转
     */
    private int vipStatus = 0;
    /**
     * 用户头像压缩图
     */
    private String headUrlThum;
    private TextView tvUserActionEmptyTips;
    private TextView tvUserGiftEmptyTips;
    private LinearLayout llUserHobbys;
    private ScrollView svContainer;
    private ImageView ivRight;
    private TextView tvAboutmeIsme;
    private TextView tvExpireLevel;
    //    private String nickName;
    private boolean noDynamic;
    private ImageView ivLoginGiftOne;
    private ImageView ivLoginGiftTwo;
    private ImageView ivLoginGiftThree;
    private ImageView ivLoginGiftFour;

    private ArrayList<String> picList;//图库列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        //接收跳转所需要的参数
//        initParams();
        isLoginUser();
        userInfoPersenter = new UserInfoPresenter(this);
        userInfoPersenter.onActivityCreate();
        initViews();
        initDatas();
        initListeners();
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, false, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivRight = findView(iv_right);
        setContent(R.layout.activity_userinfo);
        svContainer = findView(R.id.sv_container);
        tvTitle = findView(R.id.tv_title);
        tvEdit = findView(R.id.tv_right);
        vpPicture = findView(R.id.vp_picture);
        ivHeadPic = (ImageView) findViewById(R.id.iv_head_picture);
        rlAgeSex = findView(R.id.rlAgeSex);
        ivSex = (ImageView) findViewById(R.id.ivSex);
        tvAge = findView(R.id.tv_age);
        tvHoroscope = findView(R.id.tv_horoscope);
        tvSignature = findView(R.id.tv_signature);
        tvActionNum = findView(R.id.tv_action_num);
        llUserAction = findView(R.id.ll_user_action);
        gvUserAction = findView(R.id.gv_user_action);
        gvUserAction.setFocusable(false);
        tvUserActionEmptyTips = findView(R.id.tv_user_action_empty_tips);
        llUserVip = findView(R.id.ll_user_vip);
        ivUserVipIcon = findView(R.id.iv_user_vip_icon);
        tvVipStatus = findView(R.id.tv_vip_status);
        llUserLevelInfo = findView(R.id.ll_user_level_info);
        llUserLevel = findView(R.id.ll_user_level);
        tvUserLevel = findView(R.id.tv_user_level);
        llUserGift = findView(R.id.ll_user_gift);
        gvUsergift = findView(R.id.gv_user_gift);
        gvUsergift.setFocusable(false);
        tvUserGiftEmptyTips = findView(R.id.tv_user_gift_empty_tips);
        llUserHobbys = findView(R.id.ll_user_hobbys);
        flowLayoutHobby = findView(R.id.flowlayout_hobby);
        llUserId = findView(R.id.ll_user_id);
        tvUserId = findView(R.id.tv_user_id);
        llLoveStatus = findView(R.id.ll_love_status);
        tvLoveStatus = findView(R.id.tv_love_status);
        llUserHeight = findView(R.id.ll_user_height);
        tvUserHeight = findView(R.id.tv_user_height);
        llUserWeight = findView(R.id.ll_user_weight);
        tvUserWeight = findView(R.id.tv_user_weight);
        tvUserBirthday = findView(R.id.tv_user_birthday);
        llUserJob = findView(R.id.ll_user_job);
        tvUserJob = findView(R.id.tv_user_job);
        llUserHometown = findView(R.id.ll_user_himetown);
        tvUserHometown = findView(R.id.tv_user_hometown);
        llUserSecret = findView(R.id.ll_user_secret);
//        llFriendsAction = findView(R.id.ll_friends_action);
//        gvFriendsAction = findView(R.id.gv_friends_action);
        llUserPhoneAuthen = findView(R.id.ll_user_phone_authen);
        tvPhoneAuthenStatus = findView(R.id.tv_phone_authen_status);
        llUserCameraAuthen = findView(R.id.ll_user_camera_authen);
        tvCameraAuthenStatus = findView(R.id.tv_camera_authen_status);
        llUserLastLocal = findView(R.id.ll_user_last_local);
        tvUserLastLocal = findView(R.id.tv_user_last_local);
        llUserPhone = findView(R.id.ll_user_phone);
        tvUserPhone = findView(R.id.tv_user_phone);
        tvAboutMePercent = (TextView) findViewById(R.id.tv_aboutme_percent);
        tvAboutmeIsme = (TextView) findViewById(R.id.tv_aboutme_isme);
        tvAboutmeIsme.setText(getResources().getString(R.string.userinfo_about_me));
        iv_dynamic = (ImageView) findViewById(R.id.iv_dynamic);
        tvExpireLevel = (TextView) findViewById(R.id.tv_user_expire_level);
        ivLoginGiftOne = (ImageView) findViewById(R.id.iv_login_user_gift_one);
        ivLoginGiftTwo = (ImageView) findViewById(R.id.iv_login_user_gift_two);
        ivLoginGiftThree = (ImageView) findViewById(R.id.iv_login_user_gift_three);
        ivLoginGiftFour = (ImageView) findViewById(R.id.iv_login_user_gift_four);
    }

    private void initDatas() {
        gifts = new ArrayList<>();
        userInfoPersenter.init(UserInfoActivity.this, Common.getInstance().loginUser.getUid(), 0);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.drawable.near_dynamic_publish);
    }

    private void initListeners() {
        llUserAction.setOnClickListener(this);
        llUserVip.setOnClickListener(this);
        llUserLevelInfo.setOnClickListener(this);
        llUserLevel.setOnClickListener(this);
        llUserGift.setOnClickListener(this);
        llUserSecret.setOnClickListener(this);
        llUserPhoneAuthen.setOnClickListener(this);
        llUserCameraAuthen.setOnClickListener(this);
        llUserLastLocal.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.fl_right:
            case iv_right://编辑按钮
                userInfoPersenter.startEditUserInfoActivity(UserInfoActivity.this, REQ_EDIT_USERINFO);
                break;
            case R.id.ll_user_action://个人动态页面
//                if (Common.getInstance().loginUser != null) {
//                    PersonalDynamicActivity.skipToPersonalDynamicActivity(this, this, Common.getInstance().loginUser, 0);
//                }


                if (noDynamic) {
                    if (DynamicModel.getInstent().getUnSendSuccessList().size() > 0) {
                        Toast.makeText(mContext, R.string.dynamic_unsend_notice,
                                Toast.LENGTH_SHORT).show();
                    } else {
//				DataStatistics.get(mContext).addButtonEvent(
//						DataTag.BTN_dynamic_publish);//jiqiang
                        PublishDynamicActivity.skipToPublishDynamicActivity(mContext,
                                UserInfoActivity.this, PUBLISH_REQUEST_CODE);
                    }
                } else {
                    if (Common.getInstance().loginUser != null) {
                        PersonalDynamicActivity.skipToPersonalDynamicActivity(this, this, Common.getInstance().loginUser, 0);
                    }
                }

                break;
            case R.id.ll_user_vip:
                if (vipStatus == Constants.USER_VIP || vipStatus == Constants.USER_SVIP) {
//                    UserVIPActivity.startAction(this, headUrlThum, vipStatus);
                    Intent intent1 = new Intent(UserInfoActivity.this, UserVipOpenActivity.class);
                    startActivity(intent1);
                } else {
//                    UserVIPActivity.startAction(this, headUrlThum, vipStatus);
                    Intent intent1 = new Intent(UserInfoActivity.this, UserVipOpenActivity.class);
                    startActivity(intent1);
//                    UserVipPayActivity.startAction(this);
                }
                break;
            case R.id.ll_user_level:
            case R.id.ll_user_level_info:
//                Toast.makeText(this, "跳转至用户等级webview界面界面", Toast.LENGTH_SHORT).show();
                String url = Config.getlevelDescUrl(CommonFunction.getLang(UserInfoActivity.this));
                Intent i = new Intent(UserInfoActivity.this, WebViewAvtivity.class);
                i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
                startActivity(i);
                break;
            case R.id.ll_user_gift://礼物界面
//                if (gifts != null) {

//                ShowGiftActivity.startAction(this, gifts, 1);
//                }

                //新加跳转到收到礼物页面
                if (Common.getInstance().loginUser != null) {
                    StoreReceiveGiftActivity.launchMineGiftToLook(mContext, Common.getInstance().loginUser);
                }

                break;
            case R.id.ll_user_secret:
                userInfoPersenter.startSecretActivity(this);
                break;
            /*case R.id.ll_friends_action:
                Toast.makeText(this, "跳转至个人用朋友圈界面", Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.ll_user_phone_authen:
                intent.setClass(this, AuthenPhoneGuideActivity.class);
                switch (phoneAuthenStatus) {
                    case Constants.PHONE_AUTHEN_SUC:
                        if (Common.getInstance().loginUser != null) {
                            String phoneNum = Common.getInstance().loginUser.getBindPhone();
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_SUC);
                            startActivityForResult(intent, REQ_PHONE_AUTHEN_SUC);
                        }

                        break;
                    case Constants.PHONE_AUTHEN_NO:
                        intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_NO);
                        startActivityForResult(intent, REQ_PHONE_AUTHEN_NO);
                        break;
                }
                break;
            case R.id.ll_user_camera_authen:
                switch (picAuthenStatus) {
                    case Constants.PIC_AUTHEN_NO:
                        intent.setClass(this, AuthenPicGuideActivity.class);
                        startActivityForResult(intent, REQ_AUTHEN_PIC);
                        break;
                    case Constants.PIC_AUTHEN_ING:
                        break;
                    case Constants.PIC_AUTHEN_SUC:
                        intent.setClass(this, AuthenPicStatusActivity.class);
                        intent.putExtra(Constants.AUTHEN_PIC_STATUS, true);
                        startActivityForResult(intent, REQ_RELEASE_AUTHEN_PIC);
                        break;
                    case Constants.PIC_AUTHEN_FAIL:
                        intent.setClass(this, AuthenPicStatusActivity.class);
                        intent.putExtra(Constants.AUTHEN_PIC_STATUS, false);
                        startActivity(intent);
                        break;
                }
                break;
            case R.id.ll_user_last_local://最后位置

                MapUtils.showOnePositionMap(UserInfoActivity.this, MapUtils.LOAD_TYPE_POS_MAP,
                        (int) lat, (int) lng, "", "");
                break;
        }
    }

    @Override
    public void setTitle(String title) {
        SpannableString spTitle = FaceManager.getInstance(this).parseIconForString(this, title, 0, null);
        tvTitle.setText(spTitle);
        tvEdit.setTextColor(0xFFFF4064);
//        nickName = title;
        if (title != null) {
            EventBus.getDefault().post(
                    new NickNameNotifyEvent(title, Common.getInstance().loginUser.getUid()));
        }

    }

    @Override
    public void showHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, ArrayList<Photos.PhotosBean> photos) {
        if (picList == null)
            picList = new ArrayList();
        picList.clear();
        if (photos != null && photos.size() > 0) {
            for (Photos.PhotosBean photosBean : photos) {
                picList.add(photosBean.getImage());
            }
        }

        String url = "";
        if (pics != null && pics.size() > 0) {
            String iconUrl = pics.get(0);
            if (iconUrl != null && !"".equals(iconUrl)) {
                if (iconUrl.contains(".jpg")) {
                    url = iconUrl.replace("_s.jpg", ".jpg");
                } else if (iconUrl.contains(".png")) {
                    url = iconUrl.replace("_s.png", ".png");
                }
            }

            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)) {
                url = verifyicon;
            }
            if (picList.size() > 0) {
                picList.add(0, url);
            } else {
                picList.add(url);
            }

        }

        vpPicture.setAdapter(new UserInfoPicVPAdapter(picList, picList));

    }

    @Override
    public void setAgeAndSex(String age, String sex) {
        if ("m".equals(sex)) {//sex == 1
//            tvAge.setBackgroundResource(R.drawable.man_icon);
            rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
            ivSex.setImageResource(R.drawable.thread_register_man_select);
        } else {
//            tvAge.setBackgroundResource(R.drawable.feman_icon);
            rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
            ivSex.setImageResource(R.drawable.thread_register_woman_select);
        }
        try {
//           tvAge.setText(TimeFormat.getAgeByBirthdate(age) + "");
            tvAge.setText(age + "");
        } catch (Exception e) {
            tvAge.setText("0");
            e.printStackTrace();
        }
//        tvUserBirthday.setText(TimeFormat.convertTimeLong2String(age, Calendar.DATE));
        tvUserBirthday.setText(age + "");
    }

    @Override
    public void setHoroscope(int horoscope) {
        tvHoroscope.setText(getResStringArr(R.array.horoscope_date)[horoscope]);
        CommonFunction.changeColor(tvHoroscope, 0xFFFFCD6C);
    }

    @Override
    public void setSignature(String signature) {
        if (TextUtils.isEmpty(signature)) {
            tvSignature.setText(getString(R.string.edit_signature_tips));
        } else {
            SpannableString spSignature = FaceManager.getInstance(this).parseIconForString(this, signature, 0, null);
            tvSignature.setText(spSignature);
        }
    }

    @Override
    public void setAction(int count, List<String> actionPics) {
        String actionNum = getString(R.string.userinfo_action) + "  <font color= '#ff0000'>" + count + "</font>";
        tvActionNum.setText(Html.fromHtml(actionNum));
        if (count == 0) {
            tvActionNum.setText(getString(R.string.userinfo_action));
            iv_dynamic.setVisibility(View.VISIBLE);
            tvUserActionEmptyTips.setVisibility(View.VISIBLE);
            gvUserAction.setVisibility(View.GONE);
            noDynamic = true;
        } else if (actionPics == null || actionPics.size() <= 0) {
            iv_dynamic.setVisibility(View.VISIBLE);

        } else {
            tvUserActionEmptyTips.setVisibility(View.GONE);
            gvUserAction.setVisibility(View.VISIBLE);
            gvUserAction.setAdapter(new UserInfoUserActionAdapter(actionPics));
        }
    }

    @Override
    public void setAccount(int vip, int level, long vipexpire, int charmnum, int userlevel) {
        vipStatus = vip;
        if (vip == Constants.USER_VIP) {
            ivUserVipIcon.setImageResource(R.drawable.user_info_vip);
            tvVipStatus.setText(getString(R.string.userinfo_expire_vip) + TimeFormat.convertTimeLong2String(vipexpire, Calendar.DATE));
        } else if (vip == Constants.USER_SVIP) {
            ivUserVipIcon.setImageResource(R.drawable.user_info_vip);
            tvVipStatus.setText(getString(R.string.userinfo_forever_vip));
        } else {
            ivUserVipIcon.setImageResource(R.drawable.user_info_vip_drak);
            tvVipStatus.setText(getString(R.string.userinfo_open_vip));
            llUserVip.setOnClickListener(this);
        }
        tvUserLevel.setText("Lv." + userlevel + getResources().getString(R.string.charm_lv));
        // 魅力图形
        int[] charismaSymbole = CommonFunction.getCharismaSymbol(charmnum);
        if (charismaSymbole.length >= 6) {
            int rank = charismaSymbole[5];
            int index = (rank - 1) / 20;
            index = index > 4 ? 4 : index;
            tvExpireLevel.setText("Lv." + rank + getResources().getString(R.string.charm_lv));
        } else {
            tvExpireLevel.setText("Lv." + charmnum + getResources().getString(R.string.charm_lv));
        }


    }

    @Override
    public void showGift(ArrayList<String> gifts) {
        this.gifts = gifts;
        if (gifts == null || gifts.size() == 0) {
            tvUserGiftEmptyTips.setVisibility(View.VISIBLE);
            gvUsergift.setVisibility(View.GONE);
        } else {
            tvUserGiftEmptyTips.setVisibility(View.GONE);
            updateLoginGift(gifts);
//            gvUsergift.setAdapter(new UserInfoUserActionAdapterGift(gifts));
        }
    }

    public void updateLoginGift(ArrayList<String> gifts) {
        for (int i = 0; i < gifts.size(); i++) {
            switch (i) {
                case 0:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;
                case 1:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftTwo, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;
                case 2:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftThree, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;
                case 3:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftFour, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setHobby(ArrayList<String> hobbyIds) {
        flowLayoutHobby.removeAllViews();
        if (hobbyIds == null || hobbyIds.size() == 0) {
            llUserHobbys.setVisibility(View.GONE);
            return;
        } else {
            llUserHobbys.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setAboutMe(ArrayList<UserInfoEntity.Data.AboutMe> aboutMe) {

        for (int i = 0; i < aboutMe.size(); i++) {
            int uname = Integer.parseInt(aboutMe.get(i).getUname());
            switch (uname) {
                case 1:
                    llUserId.setVisibility(View.VISIBLE);
                    tvUserId.setText(aboutMe.get(i).getUvalue());
                    break;
                case 2:
                    int love = Integer.parseInt(aboutMe.get(i).getUvalue());
                    String[] loveArr = BaseApplication.appContext.getResources().getStringArray(R.array.love_status_data);
//                    if (love > 0 && love <= loveArr.length) {
//                        llLoveStatus.setVisibility(View.VISIBLE);
//                        tvLoveStatus.setText(loveArr[love - 1]);
//                    }
                    if (love >= 1 && love <= 3) {
                        llLoveStatus.setVisibility(View.VISIBLE);
                        tvLoveStatus.setText(loveArr[love - 1]);
                    } else if (love == 10) {
                        llLoveStatus.setVisibility(View.VISIBLE);
                        tvLoveStatus.setText(loveArr[3]);
                    } else if (love == 11) {
                        llLoveStatus.setVisibility(View.VISIBLE);
                        tvLoveStatus.setText(loveArr[4]);
                    } else {

                    }

                    break;
                case 3:
//                    final String[] heightArr = getResources().getStringArray(R.array.height_data);
                    llUserHeight.setVisibility(View.VISIBLE);
                    tvUserHeight.setText(aboutMe.get(i).getUvalue() + "cm");
//                    tvUserHeight.setText(heightArr[Integer.parseInt(aboutMe.get(i).getUvalue())] + "");
                    break;
                case 4:
//                    final String[] weightArr = getResources().getStringArray(R.array.weight_data);
                    llUserWeight.setVisibility(View.VISIBLE);
                    tvUserWeight.setText(aboutMe.get(i).getUvalue() + "kg");
//                    tvUserWeight.setText(weightArr[Integer.parseInt(aboutMe.get(i).getUvalue() + "")]);
                    break;
                case 6:
                    int occupation = Integer.parseInt(aboutMe.get(i).getUvalue());
                    if (occupation > 0) {
                        llUserJob.setVisibility(View.VISIBLE);
                        tvUserJob.setText(getResStringArr(R.array.job)[occupation - 1]);
                    }
                    break;
                case 7:
                    //添加非空判断
                    if ("null".equals(aboutMe.get(i).getUvalue()) || TextUtils.isEmpty(aboutMe.get(i).getUvalue())) {
                        llUserHometown.setVisibility(View.GONE);
                    } else {
                        llUserHometown.setVisibility(View.VISIBLE);
                    }
                    if (aboutMe.get(i).getUvalue() != null) {
                        String home = aboutMe.get(i).getUvalue().replace(":", ",");
                        String[] hometownEntity = home.split(",");
                        if (hometownEntity.length > 5) {
                            tvUserHometown.setText(hometownEntity[5] + " " + hometownEntity[3]);
                        } else if (hometownEntity.length >= 2) {
                            tvUserHometown.setText(hometownEntity[1] + "");
                        } else {

                        }

                    }


//                    tvUserHometown.setText(setHometownView(aboutMe.get(i).getUvalue()));
//                    String[] hometownEntity = aboutMe.get(i).getUvalue().split(",");
//                    tvUserHometown.setText("");
//                    for (int j = 0; j < hometownEntity.length; j++) {
//                        tvUserHometown.append(hometownEntity[j] + " ");
//                    }

                    break;
            }
        }


    }


    @Override
    public void showFriendsAction(List<String> actions) {

    }

    @Override
    public void setAuthenInfo(int phoneStatus, int picStatus) {
        phoneAuthenStatus = phoneStatus;
        picAuthenStatus = picStatus;
        switch (phoneStatus) {
            case Constants.PHONE_AUTHEN_SUC:
                tvPhoneAuthenStatus.setText(getString(R.string.authen_status_suc));
                break;
            case Constants.PHONE_AUTHEN_NO:
                tvPhoneAuthenStatus.setText(getString(R.string.authen_status_no));
                break;
            default:
                tvPhoneAuthenStatus.setText(getString(R.string.authen_status_no));
        }

        switch (picStatus) {
            case Constants.PIC_AUTHEN_SUC:
                tvCameraAuthenStatus.setText(getString(R.string.authen_status_suc));
                break;
            case Constants.PIC_AUTHEN_FAIL:
                tvCameraAuthenStatus.setText(getString(R.string.authen_status_fail));
                break;
            case Constants.PIC_AUTHEN_ING:
                tvCameraAuthenStatus.setText(getString(R.string.authen_status_ing));
                break;
            case Constants.PIC_AUTHEN_NO:
                tvCameraAuthenStatus.setText(getString(R.string.authen_status_no));
                break;
            default:
        }
    }

    @Override
    public void showLastLocal(Boolean isShow) {
        if (isShow) {
            llUserLastLocal.setVisibility(View.VISIBLE);
        } else {
            llUserLastLocal.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLastLocal(UserInfoEntity.Data.Location location) {
        if (location != null && location.getAddress() != null && !"".equals(location.getAddress()) && !"null".equals(location.getAddress())) {
            locationBundle.putDouble(Constants.LATITUDE_KEY, location.getLat() * 1.0 / 1e6);
            locationBundle.putDouble(Constants.LONGITUDE_KEY, 116.337323 * 1.0 / 1e6);
            locationBundle.putString(Constants.ADDRESS_KEY, location.getAddress());
//            tvUserLastLocal.setText(location.getAddress());
            if (location.getAddress().contains("+")) {
                String address = location.getAddress().replace("+", " ");
                tvUserLastLocal.setText(address);
            } else {
                tvUserLastLocal.setText(location.getAddress());
            }

            lat = location.getLat();
            lng = location.getLng();
        } else {
            llUserLastLocal.setVisibility(View.GONE);
        }
    }

    @Override
    public void showUserPhone(Boolean isShow) {
        if (isShow) {
            llUserPhone.setVisibility(View.VISIBLE);
        } else {
            llUserPhone.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserPhone(String phone) {
        tvUserPhone.setText(phone);
    }

    @Override
    public void hideAllViews() {
        svContainer.setVisibility(View.GONE);
        ivRight.setVisibility(View.GONE);
    }

    @Override
    public void setAboutMeComplete(String complete) {
        if (complete != null && !"".equals(complete)) {
            tvAboutMePercent.setText("(" + complete + ")");
        } else {
            tvAboutMePercent.setVisibility(View.GONE);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            switch (requestCode) {
                case REQ_EDIT_USERINFO:
                    userInfoPersenter.init(UserInfoActivity.this, Common.getInstance().loginUser.getUid(), 0);
                    break;
                case REQ_AUTHEN_PIC:
                    tvCameraAuthenStatus.setText(getString(R.string.authen_status_ing));
                    picAuthenStatus = Constants.PIC_AUTHEN_ING;
                    break;
                case REQ_RELEASE_AUTHEN_PIC:
                    tvCameraAuthenStatus.setText(getString(R.string.authen_status_no));
                    picAuthenStatus = Constants.PIC_AUTHEN_NO;
                    break;
                case REQ_PHONE_AUTHEN_SUC:
                    intent.setClass(this, VerifyPasswordActivity.class);
                    startActivityForResult(intent, REQ_AUTHEN_VIVIFY_PWD);
                    break;
                case REQ_PHONE_AUTHEN_NO:
                    if (CommonFunction.getLoginType(this) != Constants.ACCOUNT_LOGIN) {
                        intent.setClass(this, AuthenPhoneActivity.class);
                        intent.putExtra(Constants.HAVE_PASSWORD, false);
                        startActivityForResult(intent, REQ_PHONE_AUTHEN);
                    } else {//邮箱登录
                        intent.setClass(this, VerifyPasswordActivity.class);
                        startActivityForResult(intent, REQ_AUTHEN_VIVIFY_PWD);
                    }
                    break;
                case REQ_AUTHEN_VIVIFY_PWD:
                    intent.setClass(this, AuthenPhoneActivity.class);
                    intent.putExtra(Constants.HAVE_PASSWORD, true);
                    startActivityForResult(intent, REQ_PHONE_AUTHEN);
                    break;
                case REQ_PHONE_AUTHEN:
                    tvPhoneAuthenStatus.setText(getString(R.string.authen_status_suc));
                    phoneAuthenStatus = Constants.PHONE_AUTHEN_SUC;
                    break;
            }
        }
    }

    public static void launchUser(Context context, long uid, User user, int from) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("user", user);
        intent.putExtra("from", from);
        context.startActivity(intent);
    }

    public void isLoginUser() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfoPersenter.onActivityDestroy();
        userInfoPersenter = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(HeadImageNotifyEvent event) {
        referAvatar();
    }

    /**
     * 更新头像
     */
    private void referAvatar() {
        String verifyicon = Common.getInstance().loginUser.getVerifyicon();
        if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
            if (picList != null && picList.size() > 0){
                picList.remove(0);
                picList.add(0,Common.getInstance().loginUser.getVerifyicon());
            }else {
                picList.add(Common.getInstance().loginUser.getVerifyicon());
            }
            vpPicture.setAdapter(new UserInfoPicVPAdapter(picList, picList));
        }

    }

}
