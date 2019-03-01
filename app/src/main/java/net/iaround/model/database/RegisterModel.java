
package net.iaround.model.database;


import android.content.Context;

import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.Hashon;
import net.iaround.ui.datamodel.Model;

import java.util.HashMap;


/**
 * 注册登录模块网络数据接口集合
 *
 * @author 余勋杰
 */
public class RegisterModel extends Model {
    private static RegisterModel registerModel;
    private HashMap<Long, RegisterModelReqTypes> reqCodeToType;
    private HashMap<RegisterModelReqTypes, Long> reqTypeTocode;
    private Hashon hashon;

    public enum RegisterModelReqTypes {
        BIND,
        RESET_PASSWORD,
        REGISTER,
        GROUPS_NEARBY,
        CHECK_ACCOUNT,
        TOTAL_USER,
        WEIBO_LOGIN,
        WEIBO_REGISTER,
        IMAGE_UPLOAD_KEY,
        QQ_LOGIN,
        QQ_REGISTER,
        FACEBOOK_LOGIN,
        FACEBOOK_REGISTER,
        TWITTER_LOGIN,
        TWITTER_REGISTER,
        WECHAT_LOGIN,
        WECHAT_REGISTER,
        GET_VERIFY_IMAGE,
        CHECK_VERIFY_CODE,
    }

    public static RegisterModel getInstance() {
        if (registerModel == null) {
            registerModel = new RegisterModel();
        }
        return registerModel;
    }

    private RegisterModel() {
        reqCodeToType = new HashMap<Long, RegisterModelReqTypes>();
        reqTypeTocode = new HashMap<RegisterModelReqTypes, Long>();
        hashon = new Hashon();
    }

    private void mark(long flag, RegisterModelReqTypes reqType) {
        Long lastCode = reqTypeTocode.get(reqType);
        if (lastCode != null && lastCode > 0) {
            reqCodeToType.remove(lastCode);
        }

        reqCodeToType.put(flag, reqType);
        reqTypeTocode.put(reqType, flag);
    }

    /**
     * 通过请求码获取请求类型
     */
    public RegisterModelReqTypes getReqType(long reqCode) {
        return reqCodeToType.get(reqCode);
    }

    /**
     * 通用的获取返回数据的方法
     * <p/>
     * 如果是成功的res，会返回解析好的 HashMap，如果是错误的res，会返回下面固定 格式的HashMap：
     * <dl>
     * <dd>
     * status -- int<br>
     * error -- String<br>
     * </dl>
     * 此外，只要是本Model注册过的接口，不管成功与否，都可以从HashMap中获取 RegisterModelReqTypes类型的
     * reqType这个字段来确定请求的类型
     */
    public HashMap<String, Object> getRes(Context context, String jsonString, long reqCode)
            throws Exception {
        RegisterModelReqTypes reqType = reqCodeToType.get(reqCode);
        if (reqType == null) { // 未注册接口
            return new HashMap<String, Object>();
        }

        HashMap<String, Object> resMap = hashon.fromJson(jsonString);
        resMap.put("reqType", reqType);
        Integer status = (Integer) resMap.get("status");
        if (status != null) { // 为null则协议错误
            /*
			 * if (status != 200) { String errorString =
			 * json.optString("error");
			 * if(CommonFunction.isEmptyOrNullStr(errorString)){
			 * resMap.put("error", 1000); }else{ try { int errorCode =
			 * Integer.valueOf(errorString); resMap.put("error", errorCode); }
			 * catch (Exception e) { resMap.put("error", 1000);
			 * e.printStackTrace(); } } }
			 */
            if (status.intValue() != 200) { // 错误返回
                if (!resMap.containsKey("error")) {
                    resMap.put("error", jsonString);
                }
            }
        }
        return resMap;
    }

    /**
     * @param context
     * @param jsonString
     * @param reqCode
     * @return
     * @throws Exception 参数返回值等信息描述
     * @Title: getAllRes
     * @Description: 所有的数据返回，包含未注册的（仅做数据解析用）
     */
    public HashMap<String, Object> getAllRes(Context context, String jsonString, long reqCode)
            throws Exception {
        HashMap<String, Object> resMap = hashon.fromJson(jsonString);
        try {
            String status = resMap.get("status").toString();
            if (CommonFunction.isEmptyOrNullStr(status) || status == "null") {
                if (Integer.valueOf(status) == 200 && !resMap.containsKey("errordesc")) {
                    resMap.put("errordesc", jsonString);
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resMap;
    }

    /*
         * 第三方登录
         */
    public long LoginReq(Context context, String token, String id, String verifyCode, HttpCallBack callback, RegisterModelReqTypes logintype, String unionid) {
        int accountType = 0;
        if (logintype == RegisterModelReqTypes.QQ_LOGIN) {
            accountType = 1;
        } else if (logintype == RegisterModelReqTypes.WEIBO_LOGIN) {
            accountType = 2;
        } else if (logintype == RegisterModelReqTypes.FACEBOOK_LOGIN) {
            accountType = 4;
        } else if (logintype == RegisterModelReqTypes.TWITTER_LOGIN) {
            accountType = 5;
        } else if (logintype == RegisterModelReqTypes.WECHAT_LOGIN) {
            accountType = 7;
        }

        long flag = LoginHttpProtocol.doLogin(context, id, token, verifyCode, accountType, callback,unionid);
        mark(flag, logintype);
        return flag;
    }



}
