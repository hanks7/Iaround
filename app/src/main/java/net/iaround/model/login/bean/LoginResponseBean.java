package net.iaround.model.login.bean;

import android.content.Context;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.share.Base;
import com.tencent.bugly.crashreport.CrashReport;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.connector.ConnectorManage;
import net.iaround.model.entity.Item;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.model.ranking.WorldMessageContentEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.videochat.task.TaskWrapperManager;

import java.util.List;


/**
 * Created by Administrator on 2015/12/10.
 */
public class LoginResponseBean extends BaseServerBean {
    /**
     * 用户ID
     */
    public BaseUserInfo user;
    /**
     * 服务器当前时间
     */
    public long servertime = 0;
    /**
     * key
     */
    public String key;
    /**
     * token
     */
    public String token;

    public String firstlogin;

    public String isbind;

    public UrlInfo url;

    public int openstatics;//0、关闭第三方统计操作接口 1、开放第三方统计操作接口

    public int withwho;//v3.3想认识的性别

    public String email;//V3.4

    public int opengame;//0、关闭游戏 1、开放游戏

    public int available;//iphone专用
    /**
     * V5.0
     * 5.5以及以后此字段不再添加位数，由于客户端的历史原因，添加位数会导致低版本部分开关异常
     */
    public String newonoffs;//开关

    /**
     * 5.2
     */
    public int total;//总共项
    public int complete;//已完成项
    public String accesstoken;//微博用户身份
    public String openid;//微博用户ID
    public String unionid;//第三方用户唯一ID 微信
    public long expires;//微博用户过期时间
    public String account;//帐号
    public String password;//密码
    public List<WorldMessageContentEntity.RankingBean> rank;
    public Item item;

    public String usertoken; //即时通讯token

    //5.5开关

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * 新增开关字段 5.5以及以后的版本可在此字段添加位数作为开关
     * 第一位为客户端从大众点评获取圈中心信息以及发布动态选择地址（1：开 0：关）
     */
    public String switchs;//

    public AnchorSwitch anchorSwitch; //主播开关

    public int debugSwitch; //日志上传开关,能对用户进行动态调试 1:用户将日志上传到服务器 0:不用上传

    /**
     * 应用内开关
     */
    public class AnchorSwitch{
        public int anchor;

        public void setStatus() {
            Config.setVideoChatOpen(anchor);
        }
    }


    public void setUrl() {
        if (url != null) {
            url.setUrl();
        }
    }


    public void setStatus() {
        if (anchorSwitch != null) {
            anchorSwitch.setStatus();
        }
    }

    public void loginSuccess(Context context) {
        Me me = Common.getInstance().loginUser;
        if (servertime == 0) {
            servertime = System.currentTimeMillis();
        }
        Common.getInstance().serverToClientTime = servertime - System.currentTimeMillis();
        ConnectorManage.getInstance(context).setKey(key);
        SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(context);

        //只给视频通讯服务使用的token,设置任务 token
        TaskWrapperManager.getInstance().setAccessToken(usertoken);

        setUrl();  //登录的时候所有的URL 赋值

        setStatus();

        if (user != null) {
            me.setUid(user.userid);
            Common.getInstance().loginUser.setUid(user.userid);
            me.setNickname(user.nickname);
            me.setIcon(user.icon);
            me.setViplevel(user.viplevel);
            me.setSVip(user.svip);
            //增加用户等级
            me.setLevel(user.level);
            String gender = user.gender;
            me.setSex("m".equals(gender) ? 1 : ("f".equals(gender) ? 2 : 0));
            me.setAge(user.age);

            shareDate.putString(SharedPreferenceUtil.USERID, String.valueOf(me.getUid()));
            if(item != null){
                me.setItem(item);
            }
        }

        me.setEmail(email);

        me.setWithWho(withwho);

        Common.getInstance().isShowGameCenter = opengame != 0;

        me.setInfoTotal(total);

        me.setInfoComplete(complete);

        // 是否首次登录
        me.setFirstLogin("y".equals(firstlogin));
        // 是否绑定帐号
        me.setBind("y".equals(isbind));
        me.setBindphone(user.bindphone);
        // 登录toket,用于推送
        shareDate.putString(SharedPreferenceUtil.TOKEN, token);

        // 第三方信息（用户身份token、用户ID、token过期时间）
        if (!TextUtils.isEmpty(accesstoken)) {
            me.setAccesstoken(accesstoken);
            me.setOpenid(openid);
            me.setExpires(expires);
        }

        me.setUserType(user.userType);
        me.setVoiceUserType(user.voiceUserType);
        me.setSecret(user.secret);

        me.setGameUserType(user.gameUserType);
        me.setVerifyPersion(user.verifyPersion);
        me.setVerifyAlipay(user.verifyAlipay);
        // 登录时候判断是否显示手机验证，针对4.2国家政策不稳定原因
        shareDate.putString(SharedPreferenceUtil.NEWONOFFS, String.valueOf(newonoffs));

        shareDate.putString(SharedPreferenceUtil.SWITCH, String.valueOf("01"));
        CrashReport.setUserId(context, Common.getInstance().loginUser.getUid() + "");

        // 视屏主播的角色下免打扰有效
        if (user.userType == 1){
//            游戏主播
            String notDisturbKey = SharedPreferenceUtil.NOT_DISTURB + user.userid;
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(notDisturbKey, user.notDisturb == 1);//主播免打扰
        }

        if(user.voiceUserType == 1){
            //            语音主播
            String notVoiceDisturbKey = SharedPreferenceUtil.VOICENOT_DISTURB + user.userid;
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(notVoiceDisturbKey, user.voiceNotDisturb == 1);//主播免打扰
        }

        Common.getInstance().setDebugSwitch(debugSwitch);
    }
}

