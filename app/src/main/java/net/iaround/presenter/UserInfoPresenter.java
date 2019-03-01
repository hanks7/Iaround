package net.iaround.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.contract.OtherInfoContract;
import net.iaround.contract.UserInfoContract;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.ReportTargetType;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.UserInfoEntity;
import net.iaround.model.entity.UserInfoGameInfoBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.type.ReportType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.EditUserInfoActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.SecretActivity;
import net.iaround.ui.datamodel.Photos;
import net.iaround.ui.datamodel.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author：liush on 2016/12/5 14:31
 */
public class UserInfoPresenter implements UserInfoContract.Persenter {
    public static final int ACTIVITY_CREATE = 1;
    public static final int ACTIVITY_DESTROY = 2;

    private WeakReference<UserInfoContract.View> userInfo;
    private WeakReference<OtherInfoContract.View> otherInfo;
    private UserInfoEntity userInfoEntity;
    public UserInfoEntity.Data data;
    private int from = ChatFromType.UNKONW;// 用来记录用户从哪里进入，这和ProfileEntrance有区别

    private int mState = 0;

    private long REQUEST_USERINFO_FLAG = 0; //获取用户信息
    private long REQUEST_USERINFO_FLAG_2 = 0; //获取用户信息
    private long FOLLOW_USER_FLAG = 0; //关注
    private long UN_FOLLOW_USER_FLAG = 0; //取消关注
    private long REPORT_USER_FLAG = 0; //举报


    public UserInfoPresenter(UserInfoContract.View uerInfo) {
        this.userInfo = new WeakReference<UserInfoContract.View>(uerInfo);
    }

    public UserInfoPresenter(OtherInfoContract.View otherInfo) {
        this.otherInfo = new WeakReference<OtherInfoContract.View>(otherInfo);
    }


    /* Activity创建
     * */
    public void onActivityCreate() {
        mState = ACTIVITY_CREATE;
    }

    /* Activity销毁
     * */
    public void onActivityDestroy() {
        mState = ACTIVITY_DESTROY;
    }


    /**
     * 初始化个人信息
     */
    public void init(Context context, long uid, int cachetype) {
        // 网络请求用户资料
        REQUEST_USERINFO_FLAG = UserHttpProtocol.userInfoBasic(BaseApplication.appContext, uid, 0, new HttpCallBackImpl(this, uid));
    }

    private void handleRequestUserInfoSuccess(String result) {
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        UserInfoContract.View view = userInfo.get();
        if (view == null) {
            return;
        }

        userInfoEntity = GsonUtil.getInstance().getServerBean(result, UserInfoEntity.class);
        if (userInfoEntity == null) {
            return;
        }

        if (userInfoEntity.isSuccess()) {

            data = userInfoEntity.getData();
            perfectEntity();
            if (!TextUtils.isEmpty(data.getNotes())) {
                view.setTitle(data.getNotes());
            } else if (!TextUtils.isEmpty(data.getNickname())) {
                view.setTitle(data.getNickname());
            } else {
                view.setTitle(Common.getInstance().loginUser.getUid() + "");
            }


            ArrayList<String> headThum = new ArrayList<String>();
            if (data.getHeadPic() != null && data.getHeadPic().size() > 0) {
                for (String headUrl : data.getHeadPic()) {
                    String thumUrl = CommonFunction.getThumPicUrl(headUrl);
                    headThum.add(thumUrl);
                }
            }
            if (data.getHeadPhonts() != null && data.getHeadPhonts().size() > 0) {
                for (Photos.PhotosBean photosBean : data.getHeadPhonts()) {
                    String thumUrl = CommonFunction.getThumPicUrl(photosBean.getImage());
                    headThum.add(thumUrl);
                }
            }
            view.showHeadPics(headThum, data.getHeadPic(), data.getHeadPhonts());
            view.setAgeAndSex("" + data.getAge(), data.getGender());
            view.setHoroscope(data.getHoroscope());
            view.setSignature(data.getMoodtext());
            view.setAction(data.getDyCount(), data.getDynamic());
            view.setAccount(data.getSvip(), data.getViplevel(), data.getVipexpire(), data.getCharmnum(), data.getLevel());
            view.setAboutMe(data.getAboutme());
            view.setAboutMeComplete(data.getComplete());
            view.setAuthenInfo(data.getBind().getPhoneValid(), data.getBind().getPhotoValid());
            if (data.getLocationFlag() == Constants.LOACTION_FLAG_OPEN) {
                view.showLastLocal(true);
                view.setLastLocal(data.getLocation());
            } else {
                view.showLastLocal(false);
            }

            if (data.getPhoneFlag() == Constants.SHOW_PHONE_FLAG_OPEN && data.getUsephone() != null && !data.getUsephone().equals("")) {
                view.showUserPhone(true);
                view.setUserPhone(data.getUsephone());
            } else {
                view.showUserPhone(false);
            }
            view.showGift(data.getGift());

            view.showFriendsAction(data.getGift());

            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(Constants.PHONE_NUM, data.getBind().getPhone());
        } else {

        }
    }

    private void handleRequestUserInfoFail(int e, long flag) {
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        UserInfoContract.View view = userInfo.get();
        if (view == null) {
            return;
        }
        view.hideAllViews();
        Toast.makeText(BaseApplication.appContext, "网络错误，请返回重试", Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化他人信息
     *
     * @param uid
     */
    public void initOtherInfo(Context context, final long uid, int cachetype) {
        CommonFunction.log("UserInfoPresenter", "initOtherInfo() into, to call userInfoBasic");
        REQUEST_USERINFO_FLAG_2 = UserHttpProtocol.userInfoBasic(BaseApplication.appContext, uid, 0, new HttpCallBackImpl(this));

    }

    private void handleRequestUserInfoSuccess2(String result, long uid) {
        CommonFunction.log("UserInfoPresenter", "userInfoBasic() onGeneralSuccess() into");
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        OtherInfoContract.View view = otherInfo.get();
        if (null == view) {
            return;
        }

        userInfoEntity = GsonUtil.getInstance().getServerBean(result, UserInfoEntity.class);
        if (userInfoEntity == null) {
            CommonFunction.log("UserInfoPresenter", "userInfoBasic() onGeneralSuccess() user info null");
            return;
        }

        Item item = RankingTitleUtil.getInstance().getTitleItem(result);
        if (item != null) {
            view.setRankingTitle(item);
        }

        if (userInfoEntity.isSuccess()) {
            CommonFunction.log("UserInfoPresenter", "userInfoBasic() onGeneralSuccess() user info success");
            data = userInfoEntity.getData();
            ArrayList<String> headThum = new ArrayList<String>();
            if (data.getHeadPic() != null && data.getHeadPic().size() > 0) {
                for (String headUrl : data.getHeadPic()) {
                }
            }
            if (data.getHeadPhonts() != null && data.getHeadPhonts().size() > 0) {
                for (Photos.PhotosBean photosBean : data.getHeadPhonts()) {
                    String thumUrl = CommonFunction.getThumPicUrl(photosBean.getImage());
                    headThum.add(thumUrl);
                }
            }
            data.setHeadPicThum(headThum);
            view.setHeadPics(data.getHeadPicThum(), data.getHeadPic(), data.getHeadPhonts());

            view.setRelation(data.getRelation());//getRelation()
            if (data.getNotes() == null || TextUtils.isEmpty(data.getNotes()) || data.getNotes().contains("null")) {
                if (data.getNickname() != null || !TextUtils.isEmpty(data.getNickname()) || !data.getNickname().contains("null")) {
                    view.setNickname(data.getNickname(), "");//技能称号展示
                } else {
                    view.setNickname(String.valueOf(uid), "");
                }
            } else {
                view.setNickname(data.getNotes(), "");
            }
            view.setVoiceUserType(data.voiceUserType);

            //
            if (SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(SharedPreferenceUtil.ACCOMPANY_IS_SHOW) == 0) {
                if (data.getGamerInfo() != null && data.getGamerInfo().size() > 0) {
                    ArrayList<UserInfoGameInfoBean> list = new ArrayList<>();
                    for (UserInfoGameInfoBean bean : data.getGamerInfo()) {
                        // 新版语聊gameID 为13
                        if (Constants.AUDIO_CHAT_GAME_ID == bean.game_id) {
                            list.add(bean);
                        }else {
                            continue;
                        }
                    }
                    view.setGameList(list);//过滤完成后只显示语音信息

                }
            }else {
                view.setGameList(data.getGamerInfo());//已认证游戏列表
            }

            view.setTimeAndDistance(data.getLogouttime(), data.getDistance());
            view.setAgeAndSex("" + data.getAge(), data.getGender());
            view.setHoroscope(data.getHoroscope());
            view.setSignature(data.getMoodtext());
            view.setAction(data.getDyCount(), data.getDynamic());
            view.setAccount(data.getSvip(), data.getViplevel(), data.getVipexpire(), data.getCharmnum(), data.getLevel());
            view.setGift(data.getGift());
            view.saveIsChat(data.getRelation());
            view.setAboutMe(data.getAboutme());
            view.setAuthenInfo(data.getBind().getPhoneValid(), data.getBind().getPhotoValid());
            view.setSkillList(data.getSkill());//技能展示列表
            view.setSkillStatus(data.getAffect(), data.getPlayGroup());//技能状态展示
            if (data.getLocationFlag() == Constants.LOACTION_FLAG_OPEN) {
                view.setLocation(true, data.getLocation());
            } else {
                view.setLocation(false, data.getLocation());
            }

            if (data.getPhoneFlag() == Constants.SHOW_PHONE_FLAG_OPEN) {
                view.setPhone(true, data.getUsephone());
            } else {
                view.setPhone(false, "");
            }
            view.setSecret(data.getSecret());

            view.setUserType(data.userType, data.love);
            CommonFunction.log("UserInfoPresenter", "userInfoBasic() onGeneralSuccess() user info success end");
        } else {
            CommonFunction.log("UserInfoPresenter", "userInfoBasic() onGeneralSuccess() user info error");
            view.hideAllViews();
            ErrorCode.showError(BaseApplication.appContext, result);
        }
    }

    private void handleRequestUserInfoFail2(int e, long flag) {
        CommonFunction.log("UserInfoPresenter", "userInfoBasic() onGeneralError() into");
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        OtherInfoContract.View view = otherInfo.get();
        if (null == view) {
            return;
        }

        view.hideAllViews();
        Toast.makeText(BaseApplication.appContext, "网络错误，请返回重试", Toast.LENGTH_SHORT).show();
    }

    /*获得用户信息
     * */
    public UserInfoEntity.Data getData() {
        return data;
    }

    /**
     * 根据服务器返回回来的数据将entity进行完善
     */
    private void perfectEntity() {
        ArrayList<String> headThum = new ArrayList<String>();
        if (userInfoEntity.getData().getHeadPic() != null && userInfoEntity.getData().getHeadPic().size() > 0) {
            for (String headUrl : userInfoEntity.getData().getHeadPic()) {
                String thumUrl = CommonFunction.getThumPicUrl(headUrl);
                headThum.add(thumUrl);
            }
        }
        data.setHeadPicThum(headThum);
        /**
         * 改个接口，加了这么多烂代码，ccccc
         */
        ArrayList<UserInfoEntity.Data.AboutMe> aboutMe = userInfoEntity.getData().getAboutme();
        UserInfoEntity.Data.AboutMe2 aboutMe2 = new UserInfoEntity().new Data().new AboutMe2();
        for (int i = 0; i < aboutMe.size(); i++) {
            int uname = Integer.parseInt(aboutMe.get(i).getUname());
            switch (uname) {
                case 1:
                    aboutMe2.setUid(aboutMe.get(i).getUvalue());
                    break;
                case 2:
                    int love = Integer.parseInt(aboutMe.get(i).getUvalue());
                    aboutMe2.setLove(love);
                    break;
                case 3:
                    int height = Integer.parseInt(aboutMe.get(i).getUvalue());
                    aboutMe2.setHeight(height);
                    break;
                case 4:
                    int weight = Integer.parseInt(aboutMe.get(i).getUvalue());
                    aboutMe2.setWeight(weight);
                    break;
                case 6:
                    int occupation = Integer.parseInt(aboutMe.get(i).getUvalue());
                    aboutMe2.setOccupation(occupation);
                    break;
                case 7:
                    aboutMe2.setHometown(aboutMe.get(i).getUvalue());
                    break;
            }
        }
        data.setAboutMe2(aboutMe2);

    }

    /**
     * 更新与他人的关系(不看他动态、不让他看动态、拉入黑名单)
     *
     * @param type 1、黑名单
     *             2、不让ta看我的动态
     *             3、我不看ta的动态
     *             4、关注
     * @param opt  1、添加  2、删除
     *             5.type  1。代表取消关注2。添加关注
     */
    public void updateRelation(final Context context, int type, final User me, final int opt) {
        OtherInfoContract.View view = otherInfo.get();
        if (view == null) {
            return;
        }
        view.showDialog(context);
        if (type == 1) {
            UN_FOLLOW_USER_FLAG = FriendHttpProtocol.userFanDislike(BaseApplication.appContext, me.getUid(), new HttpCallBackImpl(this, opt));
        } else {
            int NODE_ID = (int) OtherInfoActivity.NODE_ID;
            try {
                int contact = OtherInfoActivity.DEFAULT_TYPE;
                if (from > 0) {
                    contact = from;
                } else {
                    if (me.getRelationLink() != null &&
                            me.getRelationLink().middle != null) {
                        if (me.getRelationLink().middle.contact > 0)
                            contact = me.getRelationLink().middle.contact;
                        else
                            contact = from;

                        if (me.getRelationLink().middle.id > 0) {
                            NODE_ID = me.getRelationLink().middle.id;
                        }
                    }
                }

                FOLLOW_USER_FLAG = FriendHttpProtocol.userFanLove(BaseApplication.appContext, me.getUid(), 3, NODE_ID, new HttpCallBackImpl(this, opt));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleFollowUserSuccess(String result, long opt) {
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        OtherInfoContract.View view = otherInfo.get();
        if (null == view) {
            return;
        }

        view.hideDialog();
        BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (serverBean.isSuccess()) {
            if (opt == Constants.SECRET_SET_HADLE_TYPE_ADD) {
                view.setRelation(3);//0、没有关系 1、朋友 2、陌生人3.我的关注4.我的粉丝
                data.setRelation(3);
                Toast.makeText(BaseApplication.appContext, BaseApplication.appContext.getResources().getString(R.string.other_info_follow1), Toast.LENGTH_LONG).show();

            } else {
                view.setRelation(2);
                data.setRelation(2);
            }
        } else {
            ErrorCode.showError(BaseApplication.appContext, result);
        }
    }


    private void handleUnFollowUserSuccess(String result, long opt) {
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        OtherInfoContract.View view = otherInfo.get();
        if (null == view) {
            return;
        }

        view.hideDialog();
        BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (serverBean.isSuccess()) {
            if (opt == Constants.SECRET_SET_HADLE_TYPE_DELETE) {
                view.setRelation(2);//0、没有关系 1、朋友 2、陌生人  3、关注  4、粉丝
                data.setRelation(2);
                Toast.makeText(BaseApplication.appContext, BaseApplication.appContext.getResources().getString(R.string.other_info_cancle_follow), Toast.LENGTH_LONG).show();
            } else {
                view.setRelation(3);
                data.setRelation(3);
            }
        } else {
            ErrorCode.showError(BaseApplication.appContext, result);
        }
    }

    /**
     * @param uid 被举报对象
     */
    public void report(long uid, int type) {
        REPORT_USER_FLAG = UserHttpProtocol.systemReport(BaseApplication.appContext, ReportType.INFORMATION_ILLEGAL, ReportTargetType.HUMAN, String.valueOf(uid), "", new HttpCallBackImpl(this));
    }

    private void handleReportUserSuccess(String result) {
        if (mState == ACTIVITY_DESTROY) {
            return;
        }
        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (bean != null) {
            if (bean.isSuccess()) {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.report_return_content_photo);
            } else {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        }
    }


    public void startSecretActivity(Context context) {
        Intent secretIntent = new Intent(context, SecretActivity.class);
        if (data != null) {
            secretIntent.putExtra(Constants.SECRET, data.getSecret());
            context.startActivity(secretIntent);
        }

    }

    public void startEditUserInfoActivity(Activity activity, int requestCode) {
        if (data != null) {
            Intent secretIntent = new Intent(activity, EditUserInfoActivity.class);
            secretIntent.putExtra(Constants.USERINFO, data);
            activity.startActivityForResult(secretIntent, requestCode);
        }
    }


    static class HttpCallBackImpl implements HttpCallBack {
        private WeakReference<UserInfoPresenter> mPresenter;
        private long mExtra;

        public HttpCallBackImpl(UserInfoPresenter presenter) {
            mPresenter = new WeakReference<UserInfoPresenter>(presenter);
        }

        public HttpCallBackImpl(UserInfoPresenter presenter, long extra) {
            mPresenter = new WeakReference<UserInfoPresenter>(presenter);
            mExtra = extra;
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            UserInfoPresenter presenter = mPresenter.get();
            if (presenter == null) {
                return;
            }
            if (presenter.REQUEST_USERINFO_FLAG == flag) {
                presenter.handleRequestUserInfoSuccess(result);
            } else if (presenter.REQUEST_USERINFO_FLAG_2 == flag) {
                presenter.handleRequestUserInfoSuccess2(result, mExtra);
            } else if (presenter.UN_FOLLOW_USER_FLAG == flag) {
                presenter.handleUnFollowUserSuccess(result, mExtra);
            } else if (presenter.FOLLOW_USER_FLAG == flag) {
                presenter.handleFollowUserSuccess(result, mExtra);
            } else if (presenter.REPORT_USER_FLAG == flag) {
                presenter.handleReportUserSuccess(result);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            UserInfoPresenter presenter = mPresenter.get();
            if (presenter == null) {
                return;
            }
            if (presenter.REQUEST_USERINFO_FLAG == flag) {
                presenter.handleRequestUserInfoFail(e, flag);
            } else if (presenter.REQUEST_USERINFO_FLAG_2 == flag) {
                presenter.handleRequestUserInfoFail2(e, flag);
            }
        }
    }
}
