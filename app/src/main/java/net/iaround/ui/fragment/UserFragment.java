package net.iaround.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.contract.fragment.UserContract;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.UserProfileBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.model.obj.FindExtensionListBean;
import net.iaround.model.obj.FindExtensionListBean.ExtensionItem;
import net.iaround.model.obj.MiguReadToken;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.ApplyQualificationActivity;
import net.iaround.ui.activity.AuthenPhoneActivity;
import net.iaround.ui.activity.AuthenPhoneGuideActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.ContactsActivity;
import net.iaround.ui.activity.FriendsDynamicActivity;
import net.iaround.ui.activity.GameOrderCenterActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.activity.RecentVisitorActivity;
import net.iaround.ui.activity.SettingActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.activity.UserVIPActivity;
import net.iaround.ui.activity.VerifyPasswordActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.activity.settings.DistrubSettingActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.skill.skilllist.SkillListActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.ui.view.popupwin.DisturbPopupWindow;
import net.iaround.ui.view.popupwin.RefundPopupWindow;
import net.iaround.utils.eventbus.HeadImageNotifyEvent;
import net.iaround.utils.eventbus.NickNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;


/**
 * Class:个人
 * Author：gh
 * Date: 2016/11/26 21:19
 * Emial：jt_gaohang@163.com
 */
public class UserFragment extends LazyLoadFragment implements UserContract.View, View.OnClickListener, MainFragmentActivity.PagerSelectUser, HttpCallBack {

    private HeadPhotoView circleImageView;
    private TextView tvNickname;
    private LinearLayout llUserVip;
    private LinearLayout llUserAuthen;
    private LinearLayout llUserWallet;
    private LinearLayout llFriendsAction;
    private LinearLayout llVisitor;
    private LinearLayout llContact;
    private LinearLayout llTask;
    private LinearLayout llSetting;
    private LinearLayout llApplyQualification;
    private LinearLayout llOrderList;
    private LinearLayout mLlVoiceNoDisturb;
    private LinearLayout mLlSubstituteCharge;
    private TextView tvApplyQualification;
    private TextView tvUserVip;
    private TextView tvVisitorExtra;
    private String headUrl;
    private TextView tvFriendAction;
    private TextView tvContactExtra;
    private int vipStatus;
    private int authenStatus;
    private LinearLayout userAddLay;//动态加载活动条目
    private LinearLayout llUserSkill;
    private TextView tvMyLevel;//用户等级
    private UserTitleView userTitleView;//称号
    private ImageView mCbDisturb;
    private TextView tvDisturbStatus;
    private View lineApplyQualification;
    private View lineOrderList;;

    private EditText et_test_url;

    private static final int REQ_PHONE_AUTHEN_SUC = 101;
    private static final int REQ_PHONE_AUTHEN_NO = 102;
    private static final int REQ_AUTHEN_VIVIFY_PWD = 103;
    private static final int REQ_PHONE_AUTHEN = 104;
    private static final int REQ_USER_NICKNAME = 105;

    /**
     * 跳转到咪咕阅读的标识
     */
    public static final String OPEN_MIGU_READ = "open_migu_read";
    public static final String OPEN_MIGU_READ_URL = "open_migu_read_url";

    /**
     * 获取用户信息的flag
     */
    private long FLAG_GET_USER_PRIVATE_DATA = 0;


    private long getFindListFlag = -1; //请求发现列表推荐侃啦与推广活动数据网络请求标记
    private String lastExtensionResult = null;
    private boolean voiceVotDisturb;
    private Me me;
    private boolean isCreate = false;
    private DisturbPopupWindow mWindow;
    private long getMiguTokenFlag = -1; //咪咕阅读token
    private String mMiguReadUrl = null;

    /**
     * 忽扰
     *
     * @return
     */
    private long VOICE_NO_DISTURB = 0;

    @Override
    protected int setContentView() {
        return R.layout.fragment_user;
    }

    @Override
    protected void lazyLoad() {
        initViews();
        initDatas(true);
        requestData();
        initListeners();
    }

    private void initViews() {
        circleImageView = findView(R.id.iv_head);
        tvNickname = findView(R.id.tv_nickname);
        llUserVip = findView(R.id.ll_user_vip);
        llUserAuthen = findView(R.id.ll_user_authen);
        llUserWallet = findView(R.id.ll_user_wallet);
        llFriendsAction = findView(R.id.ll_friends_action);
        tvFriendAction = findView(R.id.tv_action_extra);
        llVisitor = findView(R.id.ll_visitor);
        tvVisitorExtra = findView(R.id.tv_visitor_extra);
        llContact = findView(R.id.ll_contact);
        tvContactExtra = findView(R.id.tv_contacts_extra);
        llTask = findView(R.id.ll_user_task);
        llSetting = findView(R.id.ll_setting);
        tvUserVip = findView(R.id.tv_user_vip);
        llApplyQualification = findView(R.id.ll_apply_qualification);
        lineApplyQualification = findView(R.id.line_apply_qualification);
        llOrderList = findView(R.id.ll_order_list);
        lineOrderList = findView(R.id.line_order_list);
        mLlVoiceNoDisturb = findView(R.id.ll_voice_no_disturb);
        mLlSubstituteCharge = findView(R.id.ll_substitute_charge);
        tvApplyQualification = findView(R.id.tv_apply_qualification);
        mCbDisturb = findView(R.id.cb_disturb);
        userAddLay = findView(R.id.user_add_lay);
        llUserSkill = findView(R.id.ll_user_skill);
        llUserSkill.setOnClickListener(this);
        userTitleView = findView(R.id.user_info_user_title);

        tvDisturbStatus = findView(R.id.tv_no_disturb_status);

        et_test_url = findView(R.id.et_test_url);

        //暂时去掉视频主播身份
        if (Common.getInstance().loginUser.getUserType() == 1) {
            findView(R.id.view_setting_1).setVisibility(View.VISIBLE);
            findView(R.id.ll_no_disturb).setVisibility(View.VISIBLE);
            findView(R.id.ll_no_disturb).setOnClickListener(this);
            String DisturbKey = SharedPreferenceUtil.NOT_DISTURB + Common.getInstance().getUid();
            boolean notDisturb = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean(DisturbKey);
            tvDisturbStatus.setText(notDisturb ? getString(R.string.setting_no_distrub_open) : getString(R.string.setting_no_distrub_close));
        }

        //如果是语音主播
        if (Common.getInstance().loginUser.getVoiceUserType() == 1) {
            mLlVoiceNoDisturb.setVisibility(View.VISIBLE);
            String voiceDisturbKey = SharedPreferenceUtil.VOICENOT_DISTURB + Common.getInstance().getUid();
            voiceVotDisturb = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean(voiceDisturbKey);
            mCbDisturb.setImageResource(voiceVotDisturb ? R.drawable.btn_open : R.drawable.btn_off);
        }
    }


    private void initDatas(boolean isChange) {
        me = Common.getInstance().loginUser;
        if (me != null) {
            Item item = Common.getInstance().loginUser.getItem();
            if (item != null) {
                userTitleView.setTitleText(item);
            }

        }
        //是否显示语言相关模块
        int isShowVoice = SharedPreferenceUtil.getInstance(getContext()).getInt(SharedPreferenceUtil.VOICE_IS_SHOW);
        if(isShowVoice == 1){//显示
            lineApplyQualification.setVisibility(View.VISIBLE);
            llApplyQualification.setVisibility(View.VISIBLE);
            llOrderList.setVisibility(View.VISIBLE);
            lineOrderList.setVisibility(View.VISIBLE);
            if (me.getGameUserType() == 1) {
                tvApplyQualification.setText(R.string.my_qualification);
            } else {
                tvApplyQualification.setText(R.string.user_fragment_apply_qualification);
            }
        }else {
            lineApplyQualification.setVisibility(View.GONE);
            llApplyQualification.setVisibility(View.GONE);
            llOrderList.setVisibility(View.GONE);
            lineOrderList.setVisibility(View.GONE);
        }
        privateDataReq();
        if (isChange) {
            if (!isCreate) {
                isCreate = true;
                setUserHead(me.getIcon());
            }

        } else {
            if (!TextUtils.isEmpty(me.getIcon())) {
                headUrl = CommonFunction.getThumPicUrl(me.getIcon());
            }
        }

        if (!TextUtils.isEmpty(me.getNickname())) {
            setUserNickname(me.getNickname());
        } else {
            setUserNickname(me.getUid() + "");
        }

        setVipStatus(me.getSVip());
        onMsgSelected(0);
        String phoneNum = me.getPhone();
        setPhoneAhthen(TextUtils.isEmpty(phoneNum) ? Constants.PHONE_AUTHEN_NO : Constants.PHONE_AUTHEN_SUC);

    }

    private void initListeners() {
        circleImageView.setOnClickListener(this);
        llUserVip.setOnClickListener(this);
        llUserAuthen.setOnClickListener(this);
        llUserWallet.setOnClickListener(this);
        llFriendsAction.setOnClickListener(this);
        llVisitor.setOnClickListener(this);
        llContact.setOnClickListener(this);
        llTask.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        llApplyQualification.setOnClickListener(this);
        llOrderList.setOnClickListener(this);
        mLlSubstituteCharge.setOnClickListener(this);
        mCbDisturb.setOnClickListener(this);
        if (Config.DEBUG) {
            findView(R.id.ll_setting_1).setVisibility(View.VISIBLE);
            findView(R.id.ll_setting_1).setOnClickListener(this);
        }
    }

    /**
     * 请求发现列表推荐侃啦与推广活动数据
     */
    private void requestData() {
        getFindListFlag = BusinessHttpProtocol.getFindList(getActivity(), this);
    }

    /**
     * 个人信息
     */
    public void privateDataReq() {

        try {
            FLAG_GET_USER_PRIVATE_DATA = SpaceModelNew.getInstance(getContext()).privateDataReq(getContext(), this);
            if (FLAG_GET_USER_PRIVATE_DATA == -1) {
                Toast.makeText(getContext(), R.string.network_req_failed, Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            CommonFunction.log(e);
            Toast.makeText(getContext(), R.string.operate_fail, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonFunction.log("UserFragment", "onViewCreated() into, savedInstanceState=" + savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonFunction.log("UserFragment", "onDestroy() into");
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler = null;
        }
    }

    @Subscribe
    public void onEventMainThread(NickNameNotifyEvent event) {
        if (tvNickname != null) {
            String name = "";
            if (!TextUtils.isEmpty(event.getMsg())) {
                name = FaceManager.getInstance(getContext()).parseIconForString(getContext(), event.getMsg(), 0, null).toString();
            }
            tvNickname.setText(name);
        }

    }

    @Subscribe
    public void onEventMainThread(HeadImageNotifyEvent event) {
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (circleImageView != null) {
                        User user = new User();
                        user.setUid(me.getUid());
                        user.setSex(me.getGender().equals("m") ? 1 : 0);
                        user.setAge(me.getAge());
                        user.setViplevel(me.getViplevel());
                        user.setSVip(me.getSVip());
                        user.setNickname(me.getNickname());
                        user.setNickname(me.getNoteName());
                        user.setIcon(me.getIcon());
                        if (me.getVerifyicon() != null && !TextUtils.isEmpty(me.getVerifyicon())) {
                            user.setIcon(me.getVerifyicon());
                        }
                        circleImageView.execute(user, 1);
                    }

                    if (Common.getInstance().loginUser.isVip() && tvNickname != null) {
                        tvNickname.setTextColor(Color.parseColor("#FF4064"));
                    } else {
                        if (tvNickname != null) {
                            tvNickname.setTextColor(Color.parseColor("#ffffff"));
                        }
                    }
                }
            }, 600);
        }
    }

    private Handler mHandler = new Handler();

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_head:
                intent.setClass(getContext(), UserInfoActivity.class);
                intent.putExtra(Constants.USER_WALLET_PAGE_TYPE, Constants.USER_WALLET_PAGE_WALLET);
                startActivity(intent);
                break;
            case R.id.ll_user_vip:
                UserVIPActivity.startAction(getContext(), headUrl, vipStatus);
                break;
            case R.id.ll_user_authen://认证
                /**
                 * 再次初始化数据，防止修改数据不同步
                 */
                initDatas(false);
                intent.setClass(getActivity(), AuthenPhoneGuideActivity.class);
                switch (authenStatus) {
                    case Constants.PHONE_AUTHEN_SUC://已绑定手机号
                        intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_SUC);
                        intent.putExtra(Constants.PHONE_NUM, Common.getInstance().loginUser.getPhone());
                        startActivityForResult(intent, REQ_PHONE_AUTHEN_SUC);
                        break;
                    case Constants.PHONE_AUTHEN_NO:
                        intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_NO);
                        startActivityForResult(intent, REQ_PHONE_AUTHEN_NO);
                        break;
                }
                break;
            case R.id.ll_user_wallet://钱包
                intent.setClass(getContext(), MyWalletActivity.class);
                intent.putExtra(Constants.USER_WALLET_PAGE_TYPE, Constants.USER_WALLET_PAGE_WALLET);
                startActivity(intent);
                break;
            case R.id.ll_friends_action://好友动态
                FriendsDynamicActivity.skipToOtherDynamicsActivity(getContext(), this, 3, 0);
                break;
            case R.id.ll_visitor://谁看过我
                intent.setClass(getContext(), RecentVisitorActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_contact://联系人
                intent.setClass(getContext(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_setting://设置
                intent.setClass(getContext(), SettingActivity.class);
                intent.putExtra(Constants.USER_HEAD_URL, Common.getInstance().loginUser.getIcon());
                startActivity(intent);
                break;
            case R.id.ll_setting_1:// 测试入口
                if (!TextUtils.isEmpty(et_test_url.getText().toString())) {

                    InnerJump.Jump(getActivity(), et_test_url.getText().toString(), true);
                } else {
                    InnerJump.Jump(getActivity(), "http://notice.iaround.com/active_2018_10_19/index.html", true);
                }
                break;
            case R.id.ll_user_skill://技能列表
                intent.setClass(getContext(), SkillListActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_user_task://跳转任务
                InnerJump.Jump(getActivity(), Config.sSkillTask, true);
                break;
            case R.id.ll_no_disturb://主播免打扰设置
                Intent disturbIntent = new Intent(getActivity(), DistrubSettingActivity.class);
                startActivityForResult(disturbIntent, 201);
                break;
            case R.id.ll_order_list://订单中心列表
                Intent disturbIntent1 = new Intent(getActivity(), GameOrderCenterActivity.class);
                startActivity(disturbIntent1);
                break;
            case R.id.ll_apply_qualification://申请资质
                Intent disturbIntent2 = new Intent(getActivity(), ApplyQualificationActivity.class);
                startActivity(disturbIntent2);
                break;
            case R.id.cb_disturb://语音主播勿扰
                mWindow = new DisturbPopupWindow(getActivity(), voiceVotDisturb);
                mWindow.showPopup();
                mWindow.setRefundReasonListener(new DisturbPopupWindow.RefundReasonListener() {
                    @Override
                    public void onRefundReason() {
                        if (voiceVotDisturb) {
                            VOICE_NO_DISTURB = GameChatHttpProtocol.setvoiceanchorstatus(BaseApplication.appContext, 0, UserFragment.this);
                        } else {
                            VOICE_NO_DISTURB = GameChatHttpProtocol.setvoiceanchorstatus(BaseApplication.appContext, 1, UserFragment.this);
                        }
                    }
                });
                break;
            case R.id.ll_substitute_charge://代充后台入口
                InnerJump.Jump(getActivity(), "http://notice.iaround.com/topUp/index.html", true);
                break;
        }
    }

    @Override
    public void setUserHead(String url) {
        if (!TextUtils.isEmpty(url)) {
            headUrl = CommonFunction.getThumPicUrl(url);
        }
        if (circleImageView != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    User user = new User();
                    user.setUid(me.getUid());
                    user.setSex(me.getGender().equals("m") ? 1 : 0);
                    user.setAge(me.getAge());
                    user.setViplevel(me.getViplevel());
                    user.setSVip(me.getSVip());
                    user.setNickname(me.getNickname());
                    user.setNickname(me.getNoteName());
                    user.setIcon(me.getIcon());
                    if (me.getVerifyicon() != null && !TextUtils.isEmpty(me.getVerifyicon())) {
                        user.setIcon(me.getVerifyicon());
                    }
                    circleImageView.execute(user, 1);
                }
            }, 600);

        }

    }


    @Override
    public void setUserNickname(String nickname) {
        String name = FaceManager.getInstance(getContext()).parseIconForString(getContext(), nickname, 0, null).toString();
        tvNickname.setText(name);
    }

    @Override
    public void setVipStatus(int status) {
        vipStatus = status;
        if (status == Constants.USER_VIP && tvUserVip != null) {
            tvUserVip.setText(getString(R.string.user_fragment_vip));
        } else if (status == Constants.USER_SVIP && tvUserVip != null) {
            tvUserVip.setText(getString(R.string.user_fragment_vip));
        } else {
            if (tvUserVip != null) {
                tvUserVip.setText(getString(R.string.user_fragment_vip));
            }

        }
    }

    @Override
    public void setPhoneAhthen(int status) {
        authenStatus = status;
    }

    @Override
    public void setFriendsAction(int num) {
        tvFriendAction.setText(num + "");
    }

    @Override
    public void setVisitor(int num) {
        tvVisitorExtra.setText(num + "");
    }

    @Override
    public void setContact(int num) {
        tvContactExtra.setText(num + "");
    }

    @Override
    public void showDialog() {
//        showWaitDialog();
    }

    @Override
    public void hideDialog() {
        hideWaiDialog();
    }

    @Override
    public void setUserTitle() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            switch (requestCode) {
                case REQ_PHONE_AUTHEN_SUC:
                    intent.setClass(getActivity(), VerifyPasswordActivity.class);
                    startActivityForResult(intent, REQ_AUTHEN_VIVIFY_PWD);
                    break;
                case REQ_PHONE_AUTHEN_NO:
                    if (CommonFunction.getLoginType(getActivity()) != Constants.ACCOUNT_LOGIN) {
                        intent.setClass(getActivity(), AuthenPhoneActivity.class);
                        intent.putExtra(Constants.HAVE_PASSWORD, false);
                        startActivityForResult(intent, REQ_PHONE_AUTHEN);
                    } else {//邮箱登录
                        intent.setClass(getActivity(), VerifyPasswordActivity.class);
                        startActivityForResult(intent, REQ_AUTHEN_VIVIFY_PWD);
                    }
                    break;
                case REQ_AUTHEN_VIVIFY_PWD:
                    intent.setClass(getActivity(), AuthenPhoneActivity.class);
                    intent.putExtra(Constants.HAVE_PASSWORD, true);
                    startActivityForResult(intent, REQ_PHONE_AUTHEN);
                    break;
//                case REQ_USER_NICKNAME:
//                    String nickName = data.getStringExtra("nickname");
//                    tvNickname.setText(nickName);
//                    break;
            }
        }
        // 勿扰模式回传数据
        if (requestCode == 201) {
            boolean notDisturb = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean(SharedPreferenceUtil.NOT_DISTURB + Common.getInstance().getUid());

            tvDisturbStatus.setText(notDisturb ? getString(R.string.setting_no_distrub_open) : getString(R.string.setting_no_distrub_close));
        }
    }

    @Override
    public void onMsgSelected(int pageMode) {
        setVipStatus(Common.getInstance().loginUser.getSVip());
        if (Common.getInstance().loginUser.isVip() && tvNickname != null) {
            tvNickname.setTextColor(Color.parseColor("#FF4064"));
        } else {
            if (tvNickname != null) {
                tvNickname.setTextColor(Color.parseColor("#ffffff"));
            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (flag == getFindListFlag) {
            CommonFunction.log("UserFragment", "--->getFindList onGeneralError " + e);
        } else if (getMiguTokenFlag == flag) {
            if (CommonFunction.isEmptyOrNullStr(mMiguReadUrl)) {
                return;
            }
            //跳去咪咕阅读页面
            Intent intent = new Intent();
            intent.setClass(getContext(), WebViewAvtivity.class);
            intent.putExtra(WebViewAvtivity.WEBVIEW_URL, mMiguReadUrl);
            intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE, true);
            startActivity(intent);
        } else if (VOICE_NO_DISTURB == flag) {
            mWindow.dismissWindow();
            mWindow = null;
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == getFindListFlag) {
            if (CommonFunction.isEmptyOrNullStr(result))
                return;
            FindExtensionListBean data = GsonUtil.getInstance().getServerBean(result, FindExtensionListBean.class);
            if (data != null) {
                if (data.isSuccess()) {
                    if (!result.equals(lastExtensionResult)) {
                        lastExtensionResult = result;
                        addShowExtensionView(data);
                    }
                } else {
                    userAddLay.setVisibility(View.GONE);
                    CommonFunction.log("sherlock", "--->getFindList error " + result);
                }
            }
        } else if (flag == FLAG_GET_USER_PRIVATE_DATA) {
            UserProfileBean bean = GsonUtil.getInstance().getServerBean(
                    result, UserProfileBean.class);
            if (bean != null) {
                if (bean.isSuccess()) {

                } else {
                    ErrorCode.showError(getContext(), result);
                }
            }

            SpaceModelNew netwrokInterface = SpaceModelNew.getInstance(getContext());
            HashMap<String, Object> res = null;
            try {
                res = netwrokInterface.getRes(result, flag);
            } catch (Throwable e) {
                CommonFunction.log(e);
            }
            if (res != null) {
                SpaceModel.SpaceModelReqTypes reqType = (SpaceModel.SpaceModelReqTypes) res.get("reqType");
                if (reqType != null) {
                    switch (reqType) {
                        case PRIVATE_DATA: {
                            int status = (Integer) res.get("status");
                            if (status == 200) {

                                me.setRealName((String) res.get("realname"));
                                me.setEmail((String) res.get("email"));
                                me.setIsauth((String) res.get("isauth"));
                                me.setPhone((String) res.get("phone"));
                                me.setRealAddress((String) res.get("address"));
                                me.setHasPwd((Integer) res.get("haspwd"));
                                me.setCanChangePhone((Integer) res
                                        .get("canchgphone"));

                                SharedPreferenceUtil.getInstance(getContext()).putString(Constants.PHONE_NUM, me.getPhone());
                                if (!CommonFunction.isEmptyOrNullStr(me.getPhone())) {
                                    me.setBindPhone(true);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } else if (getMiguTokenFlag == flag) {
            if (CommonFunction.isEmptyOrNullStr(mMiguReadUrl)) {
                return;
            }
            MiguReadToken tokenBean = GsonUtil.getInstance().getServerBean(result, MiguReadToken.class);
            if (tokenBean != null && tokenBean.isSuccess()) {
                if (!CommonFunction.isEmptyOrNullStr(tokenBean.token)) {
                    mMiguReadUrl = mMiguReadUrl + "?" + tokenBean.token;
                }
            }
            CommonFunction.log("UserFragment", "mMiguReadUrl ---> " + mMiguReadUrl);
            //跳去咪咕阅读页面
            Intent intent = new Intent();
            intent.setClass(getContext(), WebViewAvtivity.class);
            intent.putExtra(WebViewAvtivity.WEBVIEW_URL, mMiguReadUrl);
            intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE, true);
            startActivity(intent);
        } else if (VOICE_NO_DISTURB == flag) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
            if (bean != null && bean.isSuccess()) {
                voiceVotDisturb = !voiceVotDisturb;
                mCbDisturb.setImageResource(voiceVotDisturb ? R.drawable.btn_open : R.drawable.btn_off);
                String notVoiceDisturbKey = SharedPreferenceUtil.VOICENOT_DISTURB + Common.getInstance().getUid();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(notVoiceDisturbKey, voiceVotDisturb);
                mWindow.dismissWindow();
                mWindow = null;
            }
        }
    }

    /**
     * 展示推广栏目
     *
     * @param data
     */
    private void addShowExtensionView(FindExtensionListBean data) {
        // 发现列表数据为空时
        if (data.discoverylist == null || data.discoverylist.size() == 0) {
            Common.getInstance().setNewGameCount(0);
            userAddLay.setVisibility(View.GONE);
            return;
        } else {
            // 生成view并加入到levelAdvert中

            refreshPhotoViewNew(data.discoverylist);
        }
    }


    private void refreshPhotoViewNew(List<ExtensionItem> list) {
        if (list != null && list.size() > 0) {
            userAddLay.setVisibility(View.VISIBLE);
            userAddLay.removeAllViews();
            for (int index = 0; index < list.size(); index++) {
                ExtensionItem bean = list.get(index);
                if (bean != null) {
                    View jobView = createPhotoView(bean);
                    userAddLay.addView(jobView);
                }

            }
        }
    }

    /**
     * 咪咕游戏
     *
     * @param bean
     * @return
     */
    private View createPhotoView(final ExtensionItem bean) {
        Context mContext = getContext();
        if (mContext == null)
            mContext = BaseApplication.appContext;
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_add_activity_view, null);
        TextView nameTv = (TextView) view.findViewById(R.id.tv_game_center);
        ImageView iconIv = (ImageView) view.findViewById(R.id.iv_game_center);

        nameTv.setText(bean.title);
        GlideUtil.loadCircleImage(BaseApplication.appContext, bean.icon, iconIv);

        // TODO: 2017/7/24 添加判断是否绑定手机号的逻辑
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatas(false);
                Intent intent = new Intent();
                if ("游戏中心".equals(bean.title)) {
                    Common.getInstance().setNewGameCount(0);// 先将新游戏数量清零，使new标志消失
                    intent.setClass(getContext(), WebViewAvtivity.class);
                    intent.putExtra(WebViewAvtivity.WEBVIEW_URL, bean.url);
                    intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE, true);
                    startActivity(intent);
                } else if ("咪咕遇见悦读会".equals(bean.title)) {
                    initDatas(false);
                    CloseAllActivity.getInstance().closeExcept(MainFragmentActivity.class);
                    switch (authenStatus) {
                        case Constants.PHONE_AUTHEN_SUC:
                            Common.getInstance().setNewGameCount(0);// 先将新游戏数量清零，使new标志消失
                            requestMiguReadToken(bean.url);
                            break;
                        case Constants.PHONE_AUTHEN_NO:
                            intent.setClass(getActivity(), AuthenPhoneGuideActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_NO);
                            startActivityForResult(intent, REQ_PHONE_AUTHEN_NO);
                            SharedPreferenceUtil.getInstance(getActivity()).putInt(OPEN_MIGU_READ, 1);
                            SharedPreferenceUtil.getInstance(getActivity()).putString(OPEN_MIGU_READ_URL, bean.url);
                            break;
                    }
                }
            }
        });
        return view;
    }

    private void requestMonthlyPay1() {
        String url = "http://www.test.com/voice/index.html";
        Intent i = new Intent(getActivity(), WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        startActivityForResult(i, 201);
    }


    private void requestMiguReadToken(String url) {

        //获取咪咕阅读token
        mMiguReadUrl = url;
        getMiguTokenFlag = BusinessHttpProtocol.getMiguReadToken(getActivity(), this);
    }

}
