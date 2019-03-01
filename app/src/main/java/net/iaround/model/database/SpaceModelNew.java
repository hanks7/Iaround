
package net.iaround.model.database;


import android.app.Activity;
import android.content.Context;

import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;


/**
 * 2.4版本以后的新协议处理工具
 *
 * @author 余勋杰
 */
public class SpaceModelNew extends SpaceModel {

    public SpaceModelReqTypes getReqType(long reqCode) {
        SpaceModelReqTypes type = reqCodeToType.get(reqCode);
        if (type == null) {
            type = SpaceModel.getInstance(activity).getReqType(reqCode);
        }
        return type;
    }

    public static SpaceModelNew getInstance(Context activity) {
        SpaceModelNew spaceModel = new SpaceModelNew();
        spaceModel.activity = activity;
        return spaceModel;
    }

    /**
     * 基本资料
     */
    public void basicInforReq(Activity context, long uid, int cachetype,
                              HttpCallBack callback) throws Throwable {
        long flag = UserHttpProtocol.userInfoBasic( context, uid, cachetype, callback);
        mark(flag, SpaceModelReqTypes.BASIC_INFOR);
    }

    /**
     * @param context     上下文
     * @param countryCode 国家代码
     * @param phone       手机号码
     * @param type        类型（1：绑定手机号/2：找回密码）
     * @return 参数返回值等信息描述
     * @Title: getMsgCodeReq
     * @Description: 获取短信验证码
     */
    public long getMsgCodeReq(Activity context, String countryCode, String phone, int type,
                              HttpCallBack callback, String geetest_challenge, String geetest_validate,
                              String geetest_seccode) {
        long flag = LoginHttpProtocol.getMsgCode_662(context, countryCode, phone, type, callback, geetest_challenge, geetest_validate, geetest_seccode, "");
        mark(flag, SpaceModelReqTypes.GET_MSG_CODE);
        return flag;
    }

    /**
     * @param context     上下文
     * @param countryCode 国家代码
     * @param phone       手机号码
     * @param smsCode     短信验证码
     * @param userPwd     用户md5加密的密码
     * @return
     * @Title: bindUserTelphone
     * @Description: 绑定用户的手机号
     */
    public long findUserPwd(Activity context, String countryCode, String phone,
                            String smsCode, String userPwd, HttpCallBack callback) {
        long flag = LoginHttpProtocol.findUserPwd(context, countryCode, phone, smsCode,
                userPwd, callback);
        mark(flag, SpaceModelReqTypes.GET_USER_PASSWORD);
        return flag;
    }

    /**
     * @param context     上下文
     * @param countryCode 国家代码
     * @param phone       手机号码
     * @param smsCode     短信验证码
     * @param userPwd     用户md5加密的密码
     * @return
     * @Title: bindUserTelphone
     * @Description: 绑定手机 号
     */
    public long bindUserTelphone(Activity context, String countryCode, String phone,
                                 String smsCode, String userPwd, HttpCallBack callback) {
        long flag = BusinessHttpProtocol.bindUserPwd(context, countryCode, phone, smsCode,
                userPwd, callback);
        mark(flag, SpaceModelReqTypes.BIND_USER_TELPHONE);
        return flag;
    }

    /**
     * @Title: checkUserPwdReq
     * @Description: 验证用户密码
     * @param context
     * @param pwd
     * @return
     */
    public long checkUserPwdReq( Activity context , String pwd , HttpCallBack callback )
    {
        long flag = LoginHttpProtocol.checkPassword( activity , pwd , callback );
        mark( flag , SpaceModelReqTypes.CHECK_PASSWORD );
        return flag;
    }

    /** 个人信息 */
    public long privateDataReq( Context context , HttpCallBack callback )
            throws Throwable
    {
        long flag = UserHttpProtocol.userInfoPersion( context , callback );
        mark( flag , SpaceModelReqTypes.PRIVATE_DATA );
        return flag;
    }

    /** 修改密码 */
    public long modifyPasswordReq( Activity context , String password , String newpassword ,
                                   HttpCallBack callback ) throws Throwable
    {
        long flag = UserHttpProtocol.userPasswordUpdate( context , password , newpassword ,
                callback );
        mark( flag , SpaceModelReqTypes.MODIFY_PASSWORD );
        return flag;
    }
}
