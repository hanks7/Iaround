
package net.iaround.connector.protocol;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.database.ReportLog;
import net.iaround.model.entity.EditUserInfoEntity;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;

import java.util.LinkedHashMap;


/**
 * 用户协议接口
 *
 * @author linyg
 * @date 2012-8-25 v2.6.0
 */
public class UserHttpProtocol extends HttpProtocol {
    public static long userPost(Context context, String url,
                                LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_USER, callback);
    }

    /**
     * 打招呼
     *
     * @param context
     * @param recUid  接受者uid
     * @param content 招呼内容
     * @return
     */
    public static long userLetterGreetting(Context context, long recUid, String content,
                                           HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("recevieid", recUid);
        map.put("content", content);
        return userPost(context, "/user/letter/greetting", map, callback);
    }

    /**
     * 消息模块 的谁访问过我
     *
     * @param context
     * @return -1请求失败
     */
    public static long userVisits(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return userPost(context, "/user/visits", map, callback);
    }

    public static long removeVisits(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        return userPost(context, "/user/remvisits", map, callback);
    }

    /**
     * 验证邮箱地址
     *
     * @param context
     * @param email
     * @return
     */
    public static long userEmailAuth(Context context, String email, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("email", email);
        entity.put("language", CommonFunction.getLang(context));
        return userPost(context, "/user/email/auth", entity, callback);
    }

    /**
     * 修改邮箱地址
     *
     * @param context
     * @param email
     * @param password
     * @return
     */
    public static long userEmailModify(Context context, String email, String password,
                                       HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("email", email);
        entity.put("password", password);
        return userPost(context, "/user/email/modify", entity, callback);
    }


    /**
     * 修改隐私设置信息
     *
     * @param context
     * @param type
     * @param content
     * @return
     */
    public static long userPrivacyUpdate(Context context, int type, String content,
                                         HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("type", type);
        entity.put("content", content);
        return userPost(context, "/user/privacy/update", entity, callback);
    }

    /**
     * 添加黑名单
     *
     * @param context
     * @param deviluserid
     * @return
     */
    public static long userDevilAdd(Context context, long deviluserid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("deviluserid", deviluserid);
        return userPost(context, "/user/devil/add", entity, callback);
    }


    /**
     * 删除黑名单
     *
     * @param context
     * @param devilid
     * @return
     */
    public static long userDevilDel(Context context, long devilid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("devilid", devilid);
        return userPost(context, "/user/devil/del", entity, callback);
    }

    /**
     * 举报
     *
     * @param type       @ReportType
     * @param targettype @ReportTargetType
     * @param targetid   被举报目标ID,如果类型是动态就是动态的id
     * @param content    举报内容（文本或者照片路径） 消息类型 （1：文本，2：图片，3：声音，4：视频，5.位置）
     *                   如果举报的是聊天内容，则内容为json串，内容样例：
     *                   {contents:[{type:1,content:"靠"}，{type
     *                   :2,content:"http://234.jpg"}]} 如果type=5，则按照“纬度,经度”进行构造
     *                   targetid为动态的时候,content为"";
     * @return
     */
    public static long systemReport(Context context, int type, int targettype, String targetid,
                                    String content, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("type", type);
        entity.put("targettype", targettype);
        entity.put("targetid", targetid);
        entity.put("content", content);

        if (ReportLog.getInstance().checkReport(entity)) {
            Toast.makeText(context, R.string.report_repeated, Toast.LENGTH_SHORT).show();
            return 1;
        } else {
            ReportLog.getInstance().logReport(entity);
            return userPost(context, "/system/report", entity, callback);
        }
    }


    /**
     * 下载精品推荐请求
     *
     * @param appid
     * @return
     * @throws Throwable
     */
    public static long appAccess(Context context, String appid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("appid", appid);
        entity.put("plat", 1);
        return userPost(context, "/app/access", entity, callback);
    }

    /**
     * 礼物列表请求
     *
     * @param categoryid
     * @param pageno
     * @param pagesize
     * @return
     */
    public static long giftList(Context context, int categoryid, int pageno, int pagesize,
                                HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("categoryid", categoryid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return userPost(context, "/gift/list", entity, callback);
    }


    /**
     * 赠送礼物请求   6.0新
     *
     * @param context
     * @param userid
     * @param giftid
     * @param paytype 1：金币购买，2：钻石购买，3：金币商品钻石直购
     * @return
     */
    public static long userGiftSend(Context context, long userid, int giftid, int paytype,
                                    HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", userid);
        entity.put("giftid", giftid);
        entity.put("paytype", paytype);
        return userPost(context, "/user/gift/send_6_0", entity, callback);
    }

    /**
     * 赠送礼物请求
     *
     * @param context
     * @param userid
     * @param giftid
     * @return
     */
    public static long mineGiftSend(Context context, long userid, int giftid,
                                    HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", userid);
        entity.put("storegiftid", giftid);
        return userPost(context, "/user/gift/send_3_3", entity, callback);
    }

    /**
     * 收到的礼物列表和详情
     *
     * @param context
     * @param userid
     * @param pageno
     * @param pagesize
     * @return
     */
    public static long userGiftReceivedDetails(Context context, long userid, int pageno,
                                               int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", userid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return userPost(context, "/user/gift/receiveddetails", entity, callback);
    }
    /**
     * 收到的礼物列表和详情
     *
     * @param context
     * @param userid
     * @param pageno
     * @param pagesize
     * @return
     */
    public static long userGiftReceived(Context context, long userid, int pageno,
                                               int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", userid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return userPost(context, "/v1/gift/receive/list", entity, callback);
    }

    /**
     * 私藏的礼物列表和详情
     *
     * @param context
     * @param userid
     * @param pageno
     * @param pagesize
     * @return
     */
    public static long userGiftMineDetails(Context context, long userid, int pageno,
                                           int pagesize, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", userid);
        entity.put("pageno", pageno);
        entity.put("pagesize", pagesize);
        return userPost(context, "/user/gift/storedetails", entity, callback);
    }

    /**
     * 上传头像
     *
     * @param context
     * @param logourl
     * @return
     */
    public static long userLogoUpdate(Context context, String logourl, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("logourl", logourl);
        return userPost(context, "/user/logo/update_5_2", entity, callback);
    }


    /**
     * 更新基本资料
     *
     * @param context
     * @param entity
     * @return
     */
    public static long userBasicUpdate(Context context, LinkedHashMap<String, Object> entity,
                                       HttpCallBack callback) {
        return userPost(context, "/user/basic/update_1_2", entity, callback);
    }

    /**
     * 绑定微博
     *
     * @param type
     * @param wid
     * @param nickname
     * @param selftext
     * @param anthtext
     * @param isauth
     * @param verifiedType
     * @return
     * @throws Throwable
     */
    public static long userWeiboBind(Context context, int type, String wid, String nickname,
                                     String selftext, String anthtext, boolean isauth, int verifiedType,
                                     HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("wid", wid);
        entity.put("nickname", nickname);
        entity.put("selftext", selftext);
        entity.put("authtext", anthtext);
        entity.put("type", type);
        switch (verifiedType) {
            case 0:
                entity.put("leveltag", "1000");
                break;
            case 220:
                entity.put("leveltag", "0100");
                break;
            case 2:
                entity.put("leveltag", "0001");
                break;
            default:
                entity.put("leveltag", "0000");
                break;
        }
        return userPost(context, "/user/weibo/bind_2_7", entity, callback);
    }

    /***********5.2 绑定新接口************/
    /**
     * 绑定微博
     *
     * @param type
     * @param wid
     * @param nickname
     * @param selftext
     * @param anthtext
     * @return
     * @throws Throwable
     */
    public static long userWeiboBind_5_2(Context context, int type, String wid, String nickname, String selftext,
                                         String anthtext, int leveltag, String accesstoken, String openid, String expires,
                                         HttpCallBack callback) {
        Log.v(AbstractShareUtils.SHARE_TAG, "userWeiboBind_5_2");
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("wid", wid);
        entity.put("nickname", nickname);
        entity.put("selftext", selftext);
        entity.put("authtext", anthtext);
        entity.put("type", type);
        entity.put("leveltag", leveltag);
        entity.put("accesstoken", accesstoken);
        entity.put("openid", openid);
        entity.put("expires", expires);
        return userPost(context, "/user/weibo/bind_5_2", entity, callback);
    }

    /**
     * 微博解绑
     *
     * @param type
     * @param wid
     * @return
     */
    public static long userWeiboCancel_5_2(Context context, int type, String wid,
                                           HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("wid", wid);
        entity.put("type", type);
        return userPost(context, "/user/weibo/cancel_5_2", entity, callback);
    }

    /*************************************/

    /**
     * 个人信息
     *
     * @param context
     * @return
     */
    public static long userInfoPersion(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        return userPost(context, "/user/info/persion", entity, callback);
    }

    public static long userInfoUpdate(Context context, EditUserInfoEntity entity, HttpCallBack callback) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
//        params.put("headPic", list2string(entity.getHeadPics()));//
        params.put("nickname", entity.getNickname());
        params.put("birthday", entity.getBirthday() + "");
        params.put("selftext", entity.getSignature());//moodtext?  线上是个人介绍
//        int loveIndex = 0;
//        if (entity.getLoveStatusIndex() == 1 | entity.getLoveStatusIndex() == 2 | entity.getLoveStatusIndex() == 3) {
//            loveIndex = entity.getLoveStatusIndex();
//        } else if (entity.getLoveStatusIndex() == 4) {
//            loveIndex = 10;
//        } else if (entity.getLoveStatusIndex() == 5) {
//            loveIndex = 11;
//        }

        params.put("love", entity.getLoveStatusIndex());
        params.put("occupation", entity.getJob());
        params.put("hometown", entity.getHometown());
        if (entity.getHeight() == 0) {
            params.put("height", -1);
        } else {
            params.put("height", entity.getHeight());
        }

        params.put("weight", entity.getWeight());
        params.put("salary", entity.getIncomeIndex());
        params.put("house", entity.getOwnHouseIndex());
        params.put("car", entity.getOwnCarIndex());
        params.put("school", entity.getUniversity());//entity.getUniversity()
        params.put("company", entity.getCompany());
        params.put("turnoffs", entity.getTurnoffs());

        String thinktext = null;
        int whotext = 0;//根据是否有签名来生成的
        int beginage = 0;//根据是否有签名来生成的
        int endage = 0;//根据是否有签名来生成的
        if (entity.getSignature() != null) {
            String[] parts = entity.getSignature().split("\r");
            if (parts != null && parts.length >= 4) {
                thinktext = parts[0];
                whotext = Integer.parseInt(parts[1]);
                beginage = Integer.parseInt(parts[2]);
                endage = Integer.parseInt("80+".equals(parts[3]) ? "81" : parts[3]);
            }
        } else {
            thinktext = context.getResources().getStringArray(R.array.wanttos)[0];
            whotext = 0;
            beginage = 18;
            endage = 81;
        }

        params.put("blood", "");//血型
        params.put("thinktext", thinktext);//看电影
        params.put("whotext", whotext);
        params.put("beginage", beginage);
        params.put("endage", endage);
        params.put("homepage", "");
        params.put("realname", entity.getNickname());//真实姓名
        params.put("hobbies", "");
        params.put("realaddress", entity.getRealaddress());
        params.put("bodytype", entity.getBodyType());
        params.put("dialects", "");//掌握的语言
        params.put("schoolid", entity.getSchoolid());//int// 学校id
        params.put("departmentid", 1);// 院系id

        params.put("favoritesids", "");////交友喜好ID,多个用逗号隔开
//        params.put( "loveexp" , entity.getLoveStatusIndex() );//恋爱经历（0-6）服务端的初始值为0\
        params.put("loveexp", 0);//"0"
        params.put("income", entity.getIncomeIndex());//收入状况（0-6）//服务端的初始值为0

        String sex = "";
        if (entity != null && entity.getSex() != null && !TextUtils.isEmpty(entity.getSex())) {
            sex = entity.getSex().equals(BaseApplication.appContext.getString(R.string.man)) ? "m" : "f";
        }
        if (!entity.getGenderCache().equals(sex)){
            params.put("gender", sex);//用户性别  男-m,女-f
        }else{
            params.put("gender", "");
        }

//        params.put("hobby",list2string(entity.getHobbys()));//废弃
//        params.put("showPhone",entity.getPhoneFlag()+"");//
//        params.put("showAddress",entity.getLastLocalFlag()+"");//
//        params.put("horoscope", entity.getHoroscope()+"");//线上没有
        return userPost(context, "/user/info/update_6_0", params, callback);
    }

    /**
     * 修改资料
     *
     * @param nickname
     * @param birthday
     * @param love
     * @param salary
     * @param height
     * @param blood
     * @param thinktext
     * @param whotext
     * @param beginage
     * @param endage
     * @param realname
     * @param school
     * @param occupation
     * @param hobbies
     * @param realaddress
     * @return
     * @v3.0 去掉gender参数，增加 weight，bodytype
     */
    public static long userInfoUpdate(Context context, String nickname, String birthday,
                                      int love, int salary, int height, String blood, String thinktext, int whotext,
                                      int beginage, int endage, String realname, int occupation, String hobbies,
                                      String realaddress, int weight, int bodytype, String hometown, String dialects,
                                      String company, String turnoffs, String school, String selftext, int schoolid, int departmentid,
                                      String favoritesids, int loveexp, int income, int house, int car, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("nickname", nickname);
        entity.put("birthday", birthday);
        entity.put("love", love);
        entity.put("salary", salary);
        entity.put("height", height);
        entity.put("blood", blood);
        entity.put("thinktext", thinktext);
        entity.put("whotext", whotext);
        entity.put("beginage", beginage);
        entity.put("endage", endage);
        entity.put("homepage", "");
        entity.put("realname", realname);
        entity.put("school", school);
        entity.put("occupation", occupation);
        entity.put("hobbies", hobbies);
        entity.put("realaddress", realaddress);
        entity.put("weight", weight);
        entity.put("bodytype", bodytype);
        entity.put("hometown", hometown);
        entity.put("dialects", dialects);
        entity.put("company", company);
        entity.put("turnoffs", turnoffs);
        entity.put("selftext", selftext);
        entity.put("schoolid", schoolid);
        entity.put("departmentid", departmentid);

        entity.put("favoritesids", favoritesids);
        entity.put("loveexp", loveexp);
        entity.put("income", income);
        entity.put("house", house);
        entity.put("car", car);

        return userPost(context, "/user/info/update_6_0", entity, callback);
    }

    /**
     * 基本资料
     *
     * @param uid
     *           用户id
     * @param cachetype
     *
     * @return
     * @v3.0 更改详见协议文档
     */
    public static long userInfoBasic(Context context, long uid, int cachetype, HttpCallBack callback) {

        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        return ConnectorManage.getInstance(context).asynPost("/v1/user/info/basic_6_0", entity, ConnectorManage.HTTP_LOGIN, callback);
    }

    /**
     * 我的资料--魅力排名
     *
     * @param context
     * @param uid
     * @return
     */
    public static long userInfoRank(Context context, long uid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        return userPost(context, "/user/info/rank", entity, callback);
    }


    /**
     * 我的资料--照片列表
     *
     * @param context
     * @param uid
     * @return
     * @v.3.0 添加一个number
     */
    public static long userInfoPhotos(Context context, long uid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        entity.put("number", 9);
        return userPost(context, "/user/info/photos_3_0", entity, callback);
    }


    /**
     * 我的资料--收到礼物列表
     *
     * @param context
     * @param uid
     * @return
     */
    public static long userReceiveGifts(Context context, long uid, int pageno, int pagesize,
                                        HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        return userPost(context, "/user/info/gifts", entity, callback);
    }

    /**
     * 我的资料--私藏礼物列表
     *
     * @param context
     * @param uid
     * @return
     */
    public static long userMineGifts(Context context, long uid, int pageno, int pagesize,
                                     HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        entity.put("pagesize", pagesize);
        return userPost(context, "/user/info/storegifts", entity, callback);
    }

    /**
     * 我的资料--设备信息
     *
     * @param context
     * @param uid
     * @return
     */
    public static long userInfoDevice(Context context, long uid, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        return userPost(context, "/user/info/device", entity, callback);
    }


    /**
     * 我的资料--交友喜好列表
     *
     * @param context
     * @return
     */
    public static long userGetFavoritesList(Context context, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        return userPost(context, "/user/favorites/list", entity, callback);
    }

    /**
     * 修改密码
     *
     * @param password
     * @param newpassword
     * @return
     */
    public static long userPasswordUpdate(Context context, String password, String newpassword,
                                          HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("password", CryptoUtil.md5(password));
        entity.put("newpassword", CryptoUtil.md5(newpassword));
        return userPost(context, "/user/password/update", entity, callback);
    }

    /**
     * 修改备注
     *
     * @param context
     * @param targetuserid
     * @param notes
     * @return
     */
    public static long userNotesSetname(Context context, long targetuserid, String notes,
                                        HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("targetuserid", targetuserid);
        entity.put("notes", notes);
        return userPost(context, "/user/notes/setname", entity, callback);
    }


    /**
     * 消息列表标记已读，下次登录没新的socket将不会下发
     *
     * @return
     */
    public static long messageListReadStatus(Context context, int type, long value,
                                             HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("type", type);
        entity.put("value", value);
        return userPost(context, "/user/markreadtag", entity, callback);
    }


    /**
     * 获取对方所在聊吧
     *
     * @return
     */
    public static long obtainInfo(Context context, long uid,HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("userid", uid);
        return userPost(context, "/user/info/chatroom", entity, callback);
    }


}
