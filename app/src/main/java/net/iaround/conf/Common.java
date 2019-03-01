package net.iaround.conf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Debug;

import net.iaround.BaseApplication;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.entity.InitBean;
import net.iaround.model.im.Me;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.RoundPicture;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.Photos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class:
 * Author：gh
 * Date: 2016/12/5 18:03
 * Emial：jt_gaohang@163.com
 */
public class Common {
    //消息数
    public static int newFaceCount = 0;// 新表情的数量

    private static Common instance = null;
    private int mNoReadMsgCount = 0;// 私聊未读数量
    private int mMyComments = 0;// 评论我的
    private int mDynamicViewCount = 0;// 好友动态
    private ArrayList<SuperActivity> bindActivityList = new ArrayList<SuperActivity>();
    private String userPwd;
    public static int windowWidth = 0; // 屏幕宽度
    public static int windowHeight = 0; // 屏幕高度
    public static int pushCount = 0;// 推送警告次数
    private int mDynamicCount = 0; // 最新动态数量

    private int mPostbarTopicCount = 0;// 推送贴吧话题数
    private int mMeetWantCount = 0;// 同意邂逅
    private int mMeetGameCount = 0; // 邂逅游戏
    private int mNewGameCount = 0; // 是否有新游戏

    public static int inmobiLoad; // InMobi是否加载
    private int defaultTopShow;//新首页页面默认显示那个功能，1 陪玩；2 语音

    private long mCurrentGroupId;  // 当前聊吧ID

    public int getNewGameCount() {
        return mNewGameCount;
    }

    public void setNewGameCount(int mNewGameCount) {
        this.mNewGameCount = mNewGameCount;
    }

    /**
     * 检查更新
     */
    public int versionFlag = 0; // 版本标识：是否需要升级，0:不需要，1:需要但不是必要，2:必需升级
    public String versionContent = "";  //版本介绍 url
    public String currentSoftVersion = "6.7.1"; // 服务端下发之软件当前版本
    public String currentSoftUrl = "http://www.iaround.com";//版本软件路径

    private ArrayList<Photos.PhotosBean> albumPic = new ArrayList<>(); //相册回传数据

    public static long availMemory = 0;    //有效内存 以M为单位

    private int defaultTab = 2;
    private int defaultTopOption = 1;

    private int geetestSwitch;//极验验证开关 0：关闭， 1：打开
    private String geetAuthKey;//验证key

    private int bindPhoneSwitch; //登陆时绑定手机号开关 1 ：打开，0：关闭

    private int blockStatus;//阻断关系开关

    private int debugSwitch = 0; //日志上传开关

    private Common() {

    }

    public static Common getInstance() {
        if (instance == null) {
            instance = new Common();
        }
        return instance;
    }

    public void setDefaultTopShow(int defaultTopShow) {
        this.defaultTopShow = defaultTopShow;
    }

    public int getDefaultTopShow() {
        return defaultTopShow;
    }

    private static class SingleInstance {
        public static Common INSTANCE = new Common();
    }

    public void reset() {
        SingleInstance.INSTANCE = new Common();
    }

    public Me loginUser = new Me(); // 当前登陆的用户信息

    /**
     * 是否第一次登录
     */
    private int firstlogin;

    /**
     * 头像是否认证
     */
    private int photovalid;

    /**
     * 上一次登录的时间
     */
    private long servertime;

    /**
     * 个人资料总共多少项
     */
    private int total;

    /**
     * 个人完成多少项
     */
    private int complete;

    public boolean vip;

    public boolean SVip;

    private Bitmap iconBitmap; // 头像的bitmap

    private ArrayList<DynamicCenterListItemBean> dynamicCenterListItemBeen;

    public boolean isShowGameCenter = true; // 是否显示游戏中心

    // 消息数
    private int mNoticeCount = 0; // 通知数量

    private String loginKey;

    private boolean isShowDailog; // 是否显示技能Dialog

    private String skillResult; // 无影脚技能使用缓存


    private String deviceToken;// 推送设备Token

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

    public String getUid() {
        return "" + Common.getInstance().loginUser.getUid();
    }

    public long getServertime() {
        return servertime;
    }

    public void setServertime(long servertime) {
        this.servertime = servertime;
    }


    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean isSVip() {
        return SVip;
    }

    public void setSVip(boolean SVip) {
        this.SVip = SVip;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        int r = iconBitmap.getWidth() * 6 / 64;
        iconBitmap = RoundPicture.getRoundedCornerBitmap(iconBitmap, r,
                iconBitmap.getWidth(), iconBitmap.getHeight());
    }

    /**
     * 用户头像
     *
     * @return
     */
    public String getIcon() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.USER_AVATAR);
    }

    /**
     * 服务器与本地的时间差值 ，校正方式为：获取当前时间+这个差值
     **/
    public long serverToClientTime = 0;

    /**
     * 用于缓存用户填写的用于发短信的手机号码（防止用户对号码重复发短信验证）
     */
    public static HashMap<String, Long> telphoneMsgMap = new HashMap<String, Long>();

    public String getChatSignture() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.CHAT_SIGNTURE);
    }

    public String getUserProfile() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.USER_PROFILE);
    }

    private InitBean initBean;

    public InitBean getInitBean() {
        return initBean;
    }

    public void setInitBean(InitBean initBean) {
        this.initBean = initBean;
    }

    public int getNoticeCount() {
        return mNoticeCount;
    }

    public void setNoticeCount(int count) {
        mNoticeCount = count;
    }

    public void setNoReadMsgCount(int mNoReadMsgCount) {
        this.mNoReadMsgCount = Math.max(0, mNoReadMsgCount);
    }

    public int getNoReadMsgCount() {
        return mNoReadMsgCount;
    }

    /**
     * 用户记录圈子被踢或圈子解散map的列表
     */
    public static HashMap<String, TransportMessage> groupKickDisbandedMap = new HashMap<String, TransportMessage>();

    /**
     * @param activity
     * @Title: addBindActivity
     * @Description: 添加用于绑定（手机号/邮箱）操作的Activity
     */
    public void addBindActivity(SuperActivity activity) {
        if (bindActivityList != null && activity != null) {
            bindActivityList.add(activity);
        }
    }

    /**
     * @Title: finishAllBindActivity
     * @Description: 关闭所有用于绑定（手机号/邮箱）操作的Activity
     */
    public void finishAllBindActivity() {
        if (bindActivityList != null) {
            for (SuperActivity activity : bindActivityList) {
                if (activity != null) {
                    try {
                        activity.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            bindActivityList.clear();
        }
    }

    /**
     * @param userPwd
     * @Title: setUserPwd
     * @Description: 保存用户的md5密码
     */
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public boolean isUserLogin = false;

    private int[] loginFlag;
    private int[] registerFlag;

    public int[] getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(int[] loginFlag) {
        this.loginFlag = loginFlag;
    }

    public int[] getRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(int[] registerFlag) {
        this.registerFlag = registerFlag;
    }

    public int getmMyComments() {
        return mMyComments;
    }

    public void setmMyComments(int mMyComments) {
        this.mMyComments = Math.max(0, mMyComments);
    }

    public int getmDynamicViewCount() {
        return mDynamicViewCount;
    }

    public void setmDynamicViewCount(int mDynamicViewCount) {
        this.mDynamicViewCount = Math.max(0, mDynamicViewCount);
    }

    public int getmPostbarTopicCount() {
        return mPostbarTopicCount;
    }

    public void setmPostbarTopicCount(int mPostbarTopicCount) {
        this.mPostbarTopicCount = mPostbarTopicCount;
    }

    public int getDynamicCount() {
        return mDynamicCount;
    }

    public void setDynamicCount(int mDynamicCount) {
        this.mDynamicCount = Math.max(0, mDynamicCount);
    }

    public int getMeetWantCount() {
        return mMeetWantCount;
    }

    public void setMeetWantCount(int mMeetWantCount) {
        this.mMeetWantCount = Math.max(0, mMeetWantCount);
    }

    public int getMeetGameCount() {
        return mMeetGameCount;
    }

    public void setMeetGameCount(int mMeetameCount) {
        this.mMeetGameCount = Math.max(0, mMeetameCount);
    }

    public static void setAvailMem(Context context) {
        ActivityManager _ActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo minfo = new ActivityManager.MemoryInfo();
        _ActivityManager.getMemoryInfo(minfo);

        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            memoryClass = am.getLargeMemoryClass();
        }


        int pid = android.os.Process.myPid();

        int[] myMempid = new int[]{pid};

        Debug.MemoryInfo[] memoryInfoArray = _ActivityManager.getProcessMemoryInfo(myMempid);
        int memSize = memoryInfoArray[0].dalvikPrivateDirty;

        int nativeSize = memoryInfoArray[0].nativePrivateDirty;

        availMemory = minfo.availMem / (1024 * 1024);

        long total = nativeSize + memSize;

        if (total / 1024 > (memoryClass - memoryClass / 4)) {
            CommonFunction.log("Memory", "Out of memory ----my System.gc");
            System.gc();
        }

        CommonFunction.log("Memory", total / 1024 + "MB" + "=" + memSize / 1024 + "MB" + "------------------Memory=" + memoryClass + "MB;");
    }

    public ArrayList<Photos.PhotosBean> getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(ArrayList<Photos.PhotosBean> albumPic) {
        this.albumPic = albumPic;
    }


    public long getmCurrentGroupId() {
        return mCurrentGroupId;
    }

    public void setmCurrentGroupId(long mCurrentGroupId) {
        this.mCurrentGroupId = mCurrentGroupId;
    }

    public boolean isShowDailog() {
        return isShowDailog;
    }

    public void setShowDailog(boolean showDailog) {
        isShowDailog = showDailog;
    }

    public String getSkillResult() {
        return skillResult;
    }

    public void setSkillResult(String skillResult) {
        this.skillResult = skillResult;
    }

    public int getDefaultTab() {
        return defaultTab;
    }

    public void setDefaultTab(int defaultTab) {
        this.defaultTab = defaultTab;
    }

    public int getDefaultTopOption() {
        return defaultTopOption;
    }

    public void setDefaultTopOption(int defaultTopOption) {
        this.defaultTopOption = defaultTopOption;
    }

    public int getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(int blockStatus) {
        this.blockStatus = blockStatus;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getGeetestSwitch() {
        return geetestSwitch;
    }

    public void setGeetestSwitch(int geetestSwitch) {
        this.geetestSwitch = geetestSwitch;
    }

    public String getGeetAuthKey() {
        return geetAuthKey;
    }

    public void setGeetAuthKey(String geetAuthKey) {
        this.geetAuthKey = geetAuthKey;
    }

    public int getBindPhoneSwitch() {
        return bindPhoneSwitch;
    }

    public void setBindPhoneSwitch(int open) {
        this.bindPhoneSwitch = open;
    }

    public int getDebugSwitch() {
        return debugSwitch;
    }

    public void setDebugSwitch(int open) {
        this.debugSwitch = open;
    }
}
