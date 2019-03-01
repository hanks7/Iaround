
package net.iaround.model.database;


import android.content.Context;

import net.iaround.conf.Common;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PhotoHttpProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.model.im.Me;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SettingUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.Model;
import net.iaround.ui.datamodel.Photo;
import net.iaround.ui.datamodel.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 个人资料模块数据解析
 */
public class SpaceModel extends Model {
    protected static HashMap<Long, SpaceModelReqTypes> reqCodeToType;
    protected static HashMap<SpaceModelReqTypes, Long> reqTypeTocode;
    protected Context activity;

    protected SpaceModel() {
        if (reqCodeToType == null) {
            reqCodeToType = new HashMap<Long, SpaceModelReqTypes>();
            reqTypeTocode = new HashMap<SpaceModelReqTypes, Long>();
        }
    }

    protected void mark(long flag, SpaceModelReqTypes reqType) {
        Long lastCode = reqTypeTocode.get(reqType);
        if (lastCode != null && lastCode > 0) {
            reqCodeToType.remove(lastCode);
        }
        reqCodeToType.put(flag, reqType);
        reqTypeTocode.put(reqType, flag);
    }

    public static SpaceModel getInstance(Context activity) {
        SpaceModel spaceModel = new SpaceModel();
        spaceModel.activity = activity;
        return spaceModel;
    }
    /** 收到的礼物详情 */
    public long giftReceiveReq( SuperActivity context , long userid , int pageno , int pagesize ,
                                HttpCallBack callback ) throws Throwable
    {
        long flag = UserHttpProtocol.userGiftReceived( context , userid , pageno , pagesize ,
                callback );
        mark( flag , SpaceModelReqTypes.GIFT_DETAIL_RECEIVE );
        return flag;
    }

    /**
     * 在此处添加修改昵称需要修改的代码
     */
    public void updateLocalUser(Me user) {
        List<User> userList = FriendModel.getInstance(activity).getFriends();
        for (User u : userList) {
            if (u.getUid() == user.getUid()) {
                u.setNoteName(user.getNoteName(false));
                break;
            }
        }
        FriendModel.getInstance(activity).updateFriendVersion();

        // 修改最近联系人
        long uid = Common.getInstance().loginUser.getUid();
        DatabaseFactory.getNearContactWorker(activity).updateLocalUser(uid, user.getUid(),
                user.getNoteName(false), user.getIcon());
    }


    /**
     * 释放
     */
    public void reset() {
        // UserBufferHelper.getInstance().save(Common.getInstance().loginUser);
        if (reqCodeToType != null) {
            reqCodeToType.clear();
        }
    }

    /**
     * 通过请求码获取请求类型
     */
    public SpaceModelReqTypes getReqType(long reqCode) {
        return reqCodeToType.get(reqCode);
    }

    /**
     * 通用的获取返回数据的方法
     * <p>
     * 如果是成功的res，会返回解析好的 HashMap，如果是错误的res，会返回下面固定 格式的HashMap：
     * <dl>
     * <dd>
     * status -- int<br>
     * error -- String<br>
     * </dl>
     * 此外，不管成功与否，都可以从HashMap中获取SpaceModelReqTypes类型的 reqType这个字段来确定请求的类型
     */
    public HashMap<String, Object> getRes(String jsonString, long reqCode) throws Throwable {
        HashMap<String, Object> resMap = new HashMap<String, Object>();
        JSONObject json = new JSONObject(jsonString);
        int status = json.optInt("status");
        resMap.put("status", status);
        SpaceModelReqTypes reqType = getReqType(reqCode);
        resMap.put("reqType", reqType);
        if (status != 200) {
            String errorString = CommonFunction.jsonOptString(json, "error");
            if (CommonFunction.isEmptyOrNullStr(errorString)) {
                resMap.put("error", 1000);
            } else {
                try {
                    int errorCode = Integer.valueOf(errorString);
                    resMap.put("error", errorCode);
                } catch (Exception e) {
                    resMap.put("error", 1000);
                    e.printStackTrace();
                }
            }
        } else if (reqType != null) {
            switch (reqType) {
                case MY_PROFILE:
//					myProfileRes( json , resMap );
                    break;
                case FRIEND_PROFILE:
//					friendProfileRes( json , resMap );
                    break;
                case FAVORIT_PHOTOS_LIST:
                    favoritPhotosListRes(json, resMap);
                    break;
                case GLOBAL_RANKING:
                    globalRankingRes(json, resMap);
                    break;
                case FRIEND_RANKING:
                    friendRankingRes(json, resMap);
                    break;
                case CITY_RANKING:
                    cityRankingRes(json, resMap);
                    break;
                case PHOTO_LIST:
                    photoListRes(json, resMap);
                    break;
                case MY_FAVORITE:
                    myFavoriteRes(json, resMap);
                    break;
                case CHARISMA_LIST:
                    charismaRankingRes(json, resMap);
                    break;
                case APP_RECOMM:
//					appRecommRes( json , resMap );
                    break;
                case GIFT_GROUP:
//					giftGroupRes( json , resMap );
                    break;
                case GIFT_LIST:
                    giftListRes(json, resMap);
                    break;
                case MY_GIFT:
                    myGiftRes(json, resMap);
                    break;
                case GIFT_DETAIL_RECEIVE:
                    giftReceiveRes(json, resMap);
                    break;
                case GIFT_DETAIL_MINE:
                    giftMineRes(json, resMap);
                    break;
                case GIFT_DETAIL_SEND:
                    giftSendRes(json, resMap);
                    break;
                case PRIVATE_DATA:
                    privateDataRes(json, resMap);
                    break;
                case SPACAE_VISIT_NUM:
                    visitNumReq(json, resMap);
                    break;
                case WEIBO_BIND:
                    bindWeiboRes(json, resMap);
                    break;
                case WEIBO_UNBIND:
                    unbindWeiboRes(json, resMap);
                    break;
                case MODIFY_PRIVACY_INFOR:
                    modifyPrivacyInfoRes(json, resMap);
                    break;
                case MODIFY_MY_PROFILE:
                    modifyProfileRes(json, resMap);
                    break;
                case MODIFY_WANNA_MEET:
                    modifyMannaMeetRes(json, resMap);
                    break;
                case HOTSPOT_CHARISMA_TODAY:
                case HOTSPOT_CHARISMA_CITY:
                case HOTSPOT_CHARISMA_GLOBAL:
                    hotspotRes(1, json, resMap);
                    break;
                case HOTSPOT_LEVEL_TODAY:
                case HOTSPOT_LEVEL_CITY:
                case HOTSPOT_LEVEL_GLOBAL:
                    hotspotRes(2, json, resMap);
                    break;
                case CHECK_PASSWORD:
                    checkUserPwdRes(json, resMap);
                    break;
                case UPLOAD_AVATAR_FROM_PHOTO:
                case UPLOAD_ICON:
                    uploadAvatarFromPhoto(json, resMap);
            }
        }
        return resMap;
    }

    /**
     * 通用的获取上载文件返回数据的方法
     */
    public HashMap<String, Object> getUploadFileRes(String jsonString) throws Throwable {
        HashMap<String, Object> resMap = new HashMap<String, Object>();
        JSONObject json = new JSONObject(jsonString);
        int status = json.optInt("status");
        resMap.put("status", status);
        if (status == 200) {
            resMap.put("attid", json.opt("attid"));
            resMap.put("url", json.opt("url"));
        } else {
            resMap.put("error", jsonString);
        }
        return resMap;
    }

    /**
     * 喜欢的相片列表返回
     * <p>
     *
     * @return<dd> status -- int<br>
     * pageno -- int<br>
     * pagesize -- int<br>
     * amount -- int<br>
     * photos -- List<Photo><dd>
     */
    private void favoritPhotosListRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        List<Photo> photos = new ArrayList<Photo>();
        resMap.put("photos", photos);
        if (json.has("photos")) {
            JSONArray photosJson = json.optJSONArray("photos");
            if (photosJson != null) {
                for (int i = 0, size = photosJson.length(); i < size; i++) {
                    JSONObject photoJson = photosJson.optJSONObject(i);
                    if (photoJson != null) {
                        Photo photo = new Photo();
                        if (photoJson.has("photoid")) {
                            photo.setId(CommonFunction.jsonOptString(photoJson, "photoid"));
                        } else if (photoJson.has("phontoid")) {
                            photo.setId(CommonFunction.jsonOptString(photoJson, "phontoid"));
                        }
                        photo.setUri(CommonFunction.jsonOptString(photoJson, "image"));
                        photo.setHasHD(photoJson.optInt("ishdimage", 1) == 0);
                        photos.add(photo);
                    }
                }
            }
        }
    }

    /**
     * 全球排名返回
     * <p>
     *
     * @return<dd> status -- int<br>
     * rank -- int<br>
     * rankpercent -- float<br>
     * score -- int<br>
     * pageno -- int<br>
     * pagesize -- int<br>
     * amount -- int<br>
     * users -- List<User><br>
     */
    private void globalRankingRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("rank", json.optInt("rank"));
        resMap.put("rankpercent", (float) json.optDouble("rankpercent", 0));
        resMap.put("score", json.optInt("score"));
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        List<User> users = new ArrayList<User>();
        resMap.put("users", users);
        JSONArray usersJson = json.optJSONArray("users");
        if (usersJson != null) {
            for (int i = 0, size = usersJson.length(); i < size; i++) {
                JSONObject userJson = usersJson.optJSONObject(i);
                if (userJson != null) {
                    JSONObject tmpJson = userJson.optJSONObject("user");
                    User user = parseUser(tmpJson, 1);
                    user.setRanking(userJson.optInt("rank"));
                    user.setLevel(userJson.optInt("score"));
                    users.add(user);
                }
            }
        }
    }

    /**
     * 好友排名返回
     * <p>
     *
     * @return<dd> status -- int<br>
     * rank -- int<br>
     * rankpercent -- float<br>
     * score -- int<br>
     * pageno -- int<br>
     * pagesize -- int<br>
     * amount -- int<br>
     * users -- List<User><br>
     */
    private void friendRankingRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("rank", json.optInt("rank"));
        resMap.put("rankpercent", (float) json.optDouble("rankpercent", 0));
        resMap.put("score", json.optInt("score"));
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        List<User> users = new ArrayList<User>();
        resMap.put("users", users);
        JSONArray usersJson = json.optJSONArray("users");
        if (usersJson != null) {
            for (int i = 0, size = usersJson.length(); i < size; i++) {
                JSONObject userJson = usersJson.optJSONObject(i);
                if (userJson != null) {
                    JSONObject tmpJson = userJson.optJSONObject("user");
                    User user = parseUser(tmpJson, 1);
                    user.setRanking(userJson.optInt("rank"));
                    user.setLevel(userJson.optInt("score"));
                    users.add(user);
                }
            }
        }
    }

    /**
     * 同城排名返回
     * <p>
     *
     * @return<dd> status -- int<br>
     * rank -- int<br>
     * rankpercent -- float<br>
     * score -- int<br>
     * pageno -- int<br>
     * pagesize -- int<br>
     * amount -- int<br>
     * users -- List<User>
     */
    private void cityRankingRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("rank", json.optInt("rank"));
        resMap.put("rankpercent", (float) json.optDouble("rankpercent", 0));
        resMap.put("score", json.optInt("score"));
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        List<User> users = new ArrayList<User>();
        resMap.put("users", users);
        JSONArray usersJson = json.optJSONArray("users");
        if (usersJson != null) {
            for (int i = 0, size = usersJson.length(); i < size; i++) {
                JSONObject userJson = usersJson.optJSONObject(i);
                if (userJson != null) {
                    JSONObject tmpJson = userJson.optJSONObject("user");
                    User user = parseUser(tmpJson, 1);
                    user.setRanking(userJson.optInt("rank"));
                    user.setLevel(userJson.optInt("score"));
                    users.add(user);
                }
            }
        }
    }


    /**
     * 相片列表返回
     * <p>
     *
     * @return<dd> status -- int<br>
     * pageno -- int<br>
     * pagesize -- int<br>
     * amount -- int<br>
     * photos -- List<Photo>
     */
    private void photoListRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        JSONArray photosJson = json.optJSONArray("photos");
        List<Photo> photos = new ArrayList<Photo>();
        resMap.put("photos", photos);
        if (photosJson != null) {
            for (int i = 0, size = photosJson.length(); i < size; i++) {
                Photo photo = new Photo();
                JSONObject photoJson = photosJson.optJSONObject(i);
                if (photoJson != null) {
                    photo.setId(CommonFunction.jsonOptString(photoJson, "photoid"));
                    photo.setUri(CommonFunction.jsonOptString(photoJson, "image"));
                    photo.setHasHD(photoJson.optInt("ishdimage", 1) == 0);
                    photos.add(photo);
                }
            }
        }
    }


    /**
     * 我的收藏返回
     * <p>
     *
     * @return<dd> status -- int<br>
     * pageno -- int<br>
     * pagesize -- int<br>
     * amount -- int<br>
     * favorites -- List<HashMap<String, Object>><br>
     * <b>每一个favorite包含如下内容：</b>
     * <dl>
     * <dd> type -- int<br>
     * user -- User<br>
     * datetime -- long<br>
     * content -- String<br>
     * targetid -- int<br>
     * image -- String<br>
     * topic -- String<br>
     * likecount -- int<br>
     * reviewcount -- int<br>
     * </dl>
     */
    private void myFavoriteRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        List<HashMap<String, Object>> favorites = new ArrayList<HashMap<String, Object>>();
        JSONArray favoritesJason = json.optJSONArray("favorites");
        if (favoritesJason != null) {
            for (int i = 0, size = favoritesJason.length(); i < size; i++) {
                JSONObject favoriteJason = favoritesJason.optJSONObject(i);
                if (favoriteJason != null) {
                    HashMap<String, Object> favorite = new HashMap<String, Object>();
                    favorite.put("type", favoriteJason.optInt("type"));
                    JSONObject userJson = favoriteJason.optJSONObject("user");
                    User user = parseUser(userJson, 1);
                    favorite.put("user", user);
                    favorite.put("datetime", favoriteJason.optLong("datetime"));
                    favorite.put("content",
                            CommonFunction.jsonOptString(favoriteJason, "content"));
                    favorite.put("targetid", favoriteJason.optInt("targetid"));
                    favorite.put("image", CommonFunction.jsonOptString(favoriteJason, "image"));
                    favorite.put("topic", CommonFunction.jsonOptString(favoriteJason, "topic"));
                    favorite.put("likecount", favoriteJason.optInt("likecount"));
                    favorite.put("reviewcount", favoriteJason.optInt("reviewcount"));
                    favorites.add(favorite);
                }
            }
        }
        resMap.put("favorites", favorites);
    }

    /**
     * 魅力值排名返回
     */
    private void charismaRankingRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        JSONObject userJson = json.optJSONObject("user");
        if (userJson != null) {
            User user = new User();
            user.setCharismaRank(userJson.optInt("rank"));
            user.setCharismaRankPercent((float) userJson.optDouble("rankpercent"));
            user.setCharisma(userJson.optInt("charmscore"));
            if (user.getCharisma() <= 0) {
                user.setCharisma(userJson.optInt("charmNum"));
            }
            user.setUid(Common.getInstance().loginUser.getUid());
            user.setNickname(Common.getInstance().loginUser.getNickname());
            user.setNoteName(Common.getInstance().loginUser.getNoteName(false));
            user.setIcon(Common.getInstance().loginUser.getIcon());
            user.setViplevel(Common.getInstance().loginUser.getViplevel());
            resMap.put("user", user);
        }
        ArrayList<User> users = new ArrayList<User>();
        resMap.put("users", users);
        JSONArray usersJson = json.optJSONArray("users");
        if (usersJson != null) {
            for (int i = 0, size = usersJson.length(); i < size; i++) {
                JSONObject itemJson = usersJson.optJSONObject(i);
                if (itemJson != null) {
                    JSONObject userItemJson = itemJson.optJSONObject("user");
                    if (userItemJson != null) {
                        User user = parseUser(userItemJson, 1);
                        user.setCharismaRank(itemJson.optInt("rank"));
                        user.setCharisma(itemJson.optInt("charmscore"));
                        if (user.getCharisma() <= 0) {
                            user.setCharisma(itemJson.optInt("charmNum"));
                        }
                        users.add(user);
                    }
                }
            }
        }
    }


    /**
     * 分类礼物列表返回
     */
    private void giftListRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        resMap.put("goldnum", json.optInt("goldnum"));
        resMap.put("discount", json.optInt("discount"));
        resMap.put("ratio", json.optInt("ratio"));
        resMap.put("diamondnum", json.optInt("diamondnum"));
        JSONArray giftsJson = json.optJSONArray("gifts");
        int size = giftsJson == null ? 0 : giftsJson.length();
        ArrayList<Gift> gifts = new ArrayList<Gift>();
        resMap.put("gifts", gifts);
        for (int i = 0; i < size; i++) {
            JSONObject giftJson = giftsJson.optJSONObject(i);
            if (giftJson != null) {
                Gift gift = new Gift();
                gift.setId(giftJson.optInt("giftid"));
                gift.setName(CommonFunction.jsonOptString(giftJson, "name"));
                gift.setIconUrl(CommonFunction.jsonOptString(giftJson, "icon"));
                gift.setPrice(giftJson.optInt("goldnum"));
                gift.setVipLevel(giftJson.optInt("viptype"));
                gift.setCharisma(giftJson.optInt("charmnum"));
                gift.setExperience(giftJson.optInt("expvalue"));
                String color = CommonFunction.jsonOptString(giftJson, "color");
                gift.setType(CommonFunction.jsonOptString(giftJson, "categoryname"));
                gift.setCurrencytype(giftJson.optInt("currencytype"));
                gift.setIsNew(giftJson.optInt("isnew"));
                gift.setIsHot(giftJson.optInt("ishot"));
                gift.setDiscountgoldnum(giftJson.optString("discountgoldnum"));
                gift.setCategoryname(giftJson.optString("categoryname"));
                if (color.startsWith("#")) {
                    color = color.substring(1);
                }
                color = "ff" + color;
                int argb = 0;
                try {
                    argb = (int) Long.parseLong(color.toLowerCase(), 16);
                    gift.setLabelBackColor(argb);
                } catch (NumberFormatException e) {
                }
                gifts.add(gift);
            }
        }
    }

    /**
     * 我的礼物列表返回
     */
    private void myGiftRes(JSONObject json, HashMap<String, Object> resMap) throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        resMap.put("goldnum", json.optInt("goldnum"));
        resMap.put("diamondnum", json.optInt("diamondnum"));
        JSONArray giftsJson = json.optJSONArray("gifts");
        int size = giftsJson == null ? 0 : giftsJson.length();
        ArrayList<Gift> gifts = new ArrayList<Gift>();
        resMap.put("gifts", gifts);
        for (int i = 0; i < size; i++) {
            JSONObject giftJson = giftsJson.optJSONObject(i);
            if (giftJson != null) {
                Gift gift = new Gift();
                gift.setName(CommonFunction.jsonOptString(giftJson, "giftname"));
                gift.setIconUrl(CommonFunction.jsonOptString(giftJson, "icon"));
                gift.setType(CommonFunction.jsonOptString(giftJson, "categoryname"));
                gift.setCurrencytype(giftJson.optInt("currencytype"));
                String color = CommonFunction.jsonOptString(giftJson, "color");
                if (color.startsWith("#")) {
                    color = color.substring(1);
                }
                color = "ff" + color;
                int argb = 0;
                try {
                    argb = (int) Long.parseLong(color.toLowerCase(), 16);
                    gift.setLabelBackColor(argb);
                } catch (NumberFormatException e) {
                }
                gifts.add(gift);
            }
        }
    }

    private void giftReceiveRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        resMap.put("storenum", json.optInt("storenum"));
        JSONArray giftsJson = json.optJSONArray("gifts");
        int size = giftsJson == null ? 0 : giftsJson.length();
        ArrayList<Gift> gifts = new ArrayList<Gift>();
        resMap.put("gifts", gifts);
        for (int i = 0; i < size; i++) {
            JSONObject giftJson = giftsJson.optJSONObject(i);
            if (giftJson != null) {
                Gift gift = new Gift();
                JSONObject userJson = giftJson.optJSONObject("user");
                if (userJson != null) {
                    User user = parseUser(userJson, 1);
                    gift.setUser(user);
                }
                JSONObject giftInner = giftJson.optJSONObject("gift");
                if (giftInner != null) {
                    gift.setName(CommonFunction.jsonOptString(giftInner, "name"));
                    gift.setIconUrl(CommonFunction.jsonOptString(giftInner, "icon"));
                    gift.setCurrencytype(giftInner.optInt("currencytype"));
                    String color = CommonFunction.jsonOptString(giftInner, "color");
                    if (color == null || color.length() <= 0 || "null".equals(color)) {
                        color = "ffffff";
                    }
                    if (color.startsWith("#")) {
                        color = color.substring(1);
                    }
                    color = "ff" + color;
                    int argb = 0;
                    try {
                        argb = (int) Long.parseLong(color.toLowerCase(), 16);
                        gift.setLabelBackColor(argb);
                    } catch (NumberFormatException e) {
                    }
                    gift.setPrice(giftInner.optInt("goldnum"));
                }
                gift.setTime(giftJson.optLong("datetime"));
                gift.setCharisma(giftJson.optInt("charmnum"));
                gift.setExperience(giftInner.optInt("expvalue"));
                gifts.add(gift);
            }
        }
    }

    private void giftMineRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        resMap.put("goldnum", json.optInt("goldnum"));
        resMap.put("diamondnum", json.optInt("diamondnum"));
        JSONArray giftsJson = json.optJSONArray("gifts");
        int size = giftsJson == null ? 0 : giftsJson.length();
        ArrayList<Gift> gifts = new ArrayList<Gift>();
        resMap.put("gifts", gifts);
        for (int i = 0; i < size; i++) {
            JSONObject giftJson = giftsJson.optJSONObject(i);
            if (giftJson != null) {
                Gift gift = new Gift();
                gift.setMid(giftJson.optInt("storegiftid"));
                JSONObject giftInner = giftJson.optJSONObject("gift");
                if (giftInner != null) {
                    gift.setName(CommonFunction.jsonOptString(giftInner, "name"));
                    gift.setIconUrl(CommonFunction.jsonOptString(giftInner, "icon"));
                    gift.setCurrencytype(giftInner.optInt("currencytype"));
                    String color = CommonFunction.jsonOptString(giftInner, "color");
                    if (color == null || color.length() <= 0 || "null".equals(color)) {
                        color = "ffffff";
                    }
                    if (color.startsWith("#")) {
                        color = color.substring(1);
                    }
                    color = "ff" + color;
                    int argb = 0;
                    try {
                        argb = (int) Long.parseLong(color.toLowerCase(), 16);
                        gift.setLabelBackColor(argb);
                    } catch (NumberFormatException e) {
                    }

                    gift.setPrice(giftInner.optInt("goldnum"));
                    gift.setDiscountgoldnum(giftInner.optString("discountgoldnum"));
                    gift.setExperience(giftInner.optInt("expvalue"));
                }
                gift.setTime(giftJson.optLong("datetime"));
                gift.setCharisma(giftJson.optInt("charmnum"));
                gift.setId(giftJson.optInt("giftid"));
                gift.setVipLevel(giftJson.optInt("viptype"));
                gifts.add(gift);
            }
        }
    }

    private void giftSendRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));
        JSONArray giftsJson = json.optJSONArray("gifts");
        int size = giftsJson == null ? 0 : giftsJson.length();
        ArrayList<Gift> gifts = new ArrayList<Gift>();
        resMap.put("gifts", gifts);
        for (int i = 0; i < size; i++) {
            JSONObject giftJson = giftsJson.optJSONObject(i);
            if (giftJson != null) {
                Gift gift = new Gift();
                JSONObject userJson = giftJson.optJSONObject("user");
                if (userJson != null) {
                    User user = parseUser(userJson, 1);
                    gift.setUser(user);
                }
                JSONObject giftInner = giftJson.optJSONObject("gift");
                if (giftInner != null) {
                    gift.setName(CommonFunction.jsonOptString(giftInner, "name"));
                    gift.setIconUrl(CommonFunction.jsonOptString(giftInner, "icon"));
                    String color = CommonFunction.jsonOptString(giftInner, "color");
                    if (color == null || color.length() <= 0 || "null".equals(color)) {
                        color = "ffffff";
                    }
                    if (color.startsWith("#")) {
                        color = color.substring(1);
                    }
                    color = "ff" + color;
                    int argb = (int) Long.parseLong(color.toLowerCase(), 16);
                    gift.setLabelBackColor(argb);
                    gift.setPrice(giftInner.optInt("goldnum"));
                }
                gift.setTime(giftJson.optLong("datetime"));
                gift.setCharisma(giftJson.optInt("charmnum"));
                gift.setExperience(giftJson.optInt("expvalue"));
                gifts.add(gift);
            }
        }
    }

    /**
     * 个人信息
     */
    public void privateDataRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        JSONObject persionJson = json.optJSONObject("persion");
        if (persionJson != null) {
            String realname = CommonFunction.jsonOptString(persionJson, "realname");
            if (CommonFunction.isEmptyOrNullStr(realname)) {
                realname = "";
            }
            resMap.put("realname", realname);
            String email = CommonFunction.jsonOptString(persionJson, "email");
            if (CommonFunction.isEmptyOrNullStr(email)) {
                email = "";
            }
            resMap.put("email", email);
            resMap.put("isauth", CommonFunction.jsonOptString(persionJson, "isauth"));
            String phone = CommonFunction.jsonOptString(persionJson, "phone");
            if (CommonFunction.isEmptyOrNullStr(phone)) {
                phone = "";
            }
            resMap.put("phone", phone);
            String address = CommonFunction.jsonOptString(persionJson, "address");
            if (CommonFunction.isEmptyOrNullStr(address)) {
                address = "";
            }
            resMap.put("address", address);

            int haspwd = persionJson.optInt("haspwd");
            resMap.put("haspwd", haspwd);

            int canChangePhone = persionJson.optInt("canchgphone");
            resMap.put("canchgphone", canChangePhone);

            String regType = CommonFunction.jsonOptString(persionJson, "regType");
            if (CommonFunction.isEmptyOrNullStr(regType)) {
                regType = "";
            }
            resMap.put("regType", regType);
        }
    }


    private void visitNumReq(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("visitnum", json.optInt("visitnum"));
    }

    /**
     * 微博绑定返回
     */
    private void bindWeiboRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        JSONObject shareinfoJson = json.optJSONObject("shareinfo");
        if (shareinfoJson != null) {
            resMap.put("userid", shareinfoJson.optLong("userid"));
            resMap.put("nickname", CommonFunction.jsonOptString(shareinfoJson, "nickname"));
            resMap.put("advertise", CommonFunction.jsonOptString(shareinfoJson, "advertise"));
            resMap.put("horoscope", shareinfoJson.optLong("horoscope"));
            resMap.put("backgroundimg",
                    CommonFunction.jsonOptString(shareinfoJson, "backgroundimg"));
        }
        resMap.put("type", json.optInt("type"));
//		resMap.put( "complrate" , json.optInt( "complrate" ) );
    }

    /**
     * 微博解除绑定返回
     */
    private void unbindWeiboRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("type", json.optInt("type"));
//		resMap.put( "complrate" , json.optInt( "complrate" ) );
    }

    /**
     * 修改私密资料返回
     */
    private void modifyPrivacyInfoRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
//		resMap.put( "complrate" , json.opt( "complrate" ) );
    }

    /**
     * 修改基本资料返回
     */
    private void modifyProfileRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("total", json.opt("total"));
        resMap.put("complete", json.opt("complete"));
//		resMap.put( "complrate" , json.opt( "complrate" ) );
//		resMap.put( "basicrate" , json.opt( "basicrate" ) );
//		resMap.put( "secretrate" , json.opt( "secretrate" ) );
//		resMap.put( "weiborate" , json.opt( "weiborate" ) );
    }

    /**
     * 修改想要找的人返回
     */
    private void modifyMannaMeetRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
//		resMap.put( "complrate" , json.opt( "complrate" ) );
    }

    /**
     * 热点返回
     *
     * @param type   1：魅力；2：积分；0：错误
     * @param json
     * @param resMap
     * @throws Throwable
     */
    public void hotspotRes(int type, JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {
        resMap.put("pageno", json.optInt("pageno"));
        resMap.put("pagesize", json.optInt("pagesize"));
        resMap.put("amount", json.optInt("amount"));

        if (type == 1) { // 魅力
            JSONObject userJson = json.optJSONObject("user");
            if (userJson != null && userJson.optInt("rank") > 0) {
                User user = new User();
                user.setCharismaRank(userJson.optInt("rank"));
                user.setCharismaRankPercent((float) userJson.optDouble("rankpercent"));
                user.setCharisma(userJson.optInt("charmscore"));
                resMap.put("user", user);
            }
        } else if (type == 2) { // 积分
            if (json.optInt("rank") > 0) {
                User user = new User();
                user.setRanking(json.optInt("rank"));
                user.setRankPercent((float) json.optDouble("rankpercent"));
                user.setLevel(json.optInt("score"));
                resMap.put("user", user);
            }
        }

        JSONArray usersJson = json.optJSONArray("users");
        if (usersJson != null) {
            ArrayList<User> users = new ArrayList<User>();
            for (int i = 0, size = usersJson.length(); i < size; i++) {
                JSONObject itemJson = usersJson.optJSONObject(i);
                if (itemJson == null) {
                    continue;
                }

                JSONObject userJson = itemJson.optJSONObject("user");
                if (userJson != null) {
                    User user = new User();
                    user.setUid(userJson.optLong("userid"));
                    user.setNickname(CommonFunction.jsonOptString(userJson, "nickname"));
                    user.setIcon(CommonFunction.jsonOptString(userJson, "icon"));
                    user.setViplevel(userJson.optInt("viplevel"));

                    if (type == 1) { // 魅力
                        user.setCharismaRank(itemJson.optInt("rank"));
                        user.setCharisma(itemJson.optInt("charmscore"));
                    } else if (type == 2) { // 积分
                        user.setRanking(itemJson.optInt("rank"));
                        user.setLevel(itemJson.optInt("score"));
                    }

                    users.add(user);
                }
            }
            resMap.put("users", users);
        }
    }

    /**
     * @param json
     * @param resMap
     * @throws
     * @Title: checkUserPwdRes
     * @Description: 验证密码返回
     */
    private void checkUserPwdRes(JSONObject json, HashMap<String, Object> resMap)
            throws Throwable {

    }

    private void uploadAvatarFromPhoto(JSONObject json, HashMap<String, Object> resMap) {
//		resMap.put( "complrate" , json.opt( "complrate" ) );
//		resMap.put( "basicrate" , json.opt( "basicrate" ) );
//		resMap.put( "secretrate" , json.opt( "secretrate" ) );
//		resMap.put( "weiborate" , json.opt( "weiborate" ) );
    }

    /**
     * 本类保存了space模块中所有请求的类型
     */
    public enum SpaceModelReqTypes {
        MY_PROFILE,
        MODIFY_MY_PROFILE,
        INVITE_FRIEND_FROM_ADDR_BOOK,
        FAVORIT_PHOTOS_LIST,
        ADD_EDU,
        MODIFY_EDU,
        DELETE_EDU,
        ADD_JOB,
        MODIFY_JOB,
        DELETE_JOB,
        MODIFY_HOBBY,
        GLOBAL_RANKING,
        FRIEND_RANKING,
        CITY_RANKING,
        MODIFY_PRIVACY_SETTING,
        //		BLACK_LIST ,
        BLACK_LIST_ADD,
        SAY_HELLO,
        SEND_PRIVATE_MSG,
        REPORT,
        PHOTO_LIST,
        LIVE_TRACK,
        MY_FAVORITE,
        BLACK_LIST_DEL,
        UPLOAD_CITY,
        COUNTRY_LIST,
        CITY_LIST_OF_COUNTRY,
        FRIEND_PROFILE,
        DEFAULT_SETTINGS,
        INVITE_FRIEND_FROM_SNS,
        INVITE_FRIEND_FROM_CONTENT,
        INVITE_FRIEND_FROM_EMAIL,
        UPLOAD_ICON,
        CHARISMA_LIST,
        MORE_INFOR,
        APP_RECOMM,
        APP_RECOMM_ACCESS,
        GIFT_GROUP,
        GIFT_LIST,
        MY_GIFT,
        SEND_GIFT,
        SEND_MINE_GIFT,
        GIFT_DETAIL_RECEIVE,
        GIFT_DETAIL_MINE,
        GIFT_DETAIL_SEND,
        WEIBO_BIND,
        WEIBO_UNBIND,
        PRIVATE_DATA,
        BASIC_INFOR,
        UPLOAD_AVATAR_FROM_PHOTO,
        SPCAE_INFOR_CHRISMA,
        SPCAE_INFOR_PHOTOES,
        SPCAE_INFOR_GIFTS,
        SPCAE_INFOR_RECEIVE_GIFTS,
        SPCAE_INFOR_MINE_GIFTS,
        SPCAE_INFOR_DEIVCE,
        SPACAE_VISIT_NUM,
        MODIFY_PASSWORD,
        MODIFY_NOTE_NAME,
        MODIFY_PRIVACY_INFOR,
        MODIFY_WANNA_MEET,
        HOTSPOT_CHARISMA_TODAY,
        HOTSPOT_CHARISMA_CITY,
        HOTSPOT_CHARISMA_GLOBAL,
        HOTSPOT_LEVEL_TODAY,
        HOTSPOT_LEVEL_CITY,
        HOTSPOT_LEVEL_GLOBAL,
        SPCAE_INFOR_PLAY_GAMES,
        CHECK_PASSWORD,
        GET_MSG_CODE,
        GET_USER_PASSWORD,
        BIND_USER_TELPHONE,
        GIFT_DETAIL
    }

    /**
     * 收到的礼物详情
     */
    public long giftMineReq(SuperActivity context, long userid, int pageno, int pagesize,
                            HttpCallBack callback) throws Throwable {
        long flag = UserHttpProtocol.userGiftMineDetails(context, userid, pageno, pagesize,
                callback);
        mark(flag, SpaceModelReqTypes.GIFT_DETAIL_MINE);
        return flag;
    }

    /**
     * 赠送私藏礼物请求
     */
    public long sendMineGiftReq(SuperActivity context, long userid, int minegiftid,
                                HttpCallBack callback) throws Throwable {
        long flag = UserHttpProtocol.mineGiftSend(context, userid, minegiftid, callback);
        mark(flag, SpaceModelReqTypes.SEND_MINE_GIFT);
        return flag;
    }

    /**
     * 礼物列表请求
     */
    public long giftListReq(SuperActivity context, int categoryid, int pageno, int pagesize,
                            HttpCallBack callback) throws Throwable {
        long flag = UserHttpProtocol.giftList(context, categoryid, pageno, pagesize, callback);
        mark(flag, SpaceModelReqTypes.GIFT_LIST);
        return flag;
    }

    /**
     * 赠送礼物请求
     */
    public long sendGiftReq(SuperActivity context, long userid, int giftid, int paytype,
                            HttpCallBack callback) throws Throwable {
        long flag = UserHttpProtocol.userGiftSend(context, userid, giftid, paytype, callback);
        mark(flag, SpaceModelReqTypes.SEND_GIFT);
        return flag;
    }

    /**
     * 举报
     *
     * @param type       举报的类型（1：色情，2：暴力，3：骚扰，4：谩骂，5：广告，6：欺诈，7：反动，8：政治，9：其他，10：色情谩骂政治等，
     *                   11：个人资料不当，12：盗用他人照片）
     * @param targettype 举报目标类型（1:人 ，2:相片，3:评论，4：话题，5:群组，6：聊天内容）
     * @param targetid   被举报目标ID
     * @param content    举报内容（文本或者照片路径） 消息类型 （1：文本，2：图片，3：声音，4：视频，5.位置）
     *                   如果举报的是聊天内容，则内容为json串，内容样例：
     *                   {contents:[{type:1,content:"靠"}，{type
     *                   :2,content:"http://234.jpg"}]} 如果type=5，则按照“纬度,经度”进行构造
     * @return
     * @throws Throwable
     */
    public long reportReq(Context context, int type, int targettype, String targetid,
                          String content, HttpCallBack callback) throws Throwable {
        long flag = UserHttpProtocol.systemReport(context, type, targettype, targetid,
                content, callback);
        mark(flag, SpaceModelReqTypes.REPORT);
        return flag;
    }

    /**
     * 判断是否自动登录
     */
    public boolean isAutoLogin() {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        String uid = null;
        if (sp.has(SharedPreferenceUtil.USERID)) {
            uid = sp.getString(SharedPreferenceUtil.USERID);
        }
        if (uid == null || uid.length() <= 0) {
            return true;
        }

        // 判断注销状态
        if (isUserLogouted(uid)) {
            return false;
        }

        // 判断自动登录
        String userKey = SharedPreferenceUtil.AUTO_LOGIN + uid;
        if (!sp.has(userKey)) {
            return true;
        }

        // String[] items = data.split("%");
        boolean autoLogin = true;
        if (SettingUtil.getInstance().has(activity, userKey)) {
            autoLogin = SettingUtil.getInstance().getBoolean(activity, userKey);
        }
        return autoLogin;
    }

    /**
     * 判断指定用户是否已经注销了
     */
    public boolean isUserLogouted(String uid) {
        if (uid == null || uid.length() <= 0) { // 默认自动登录
            return true;
        }

        // 判断注销状态
        String userKey = SharedPreferenceUtil.USER_STATE + uid;
        boolean userState = true;
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        if (sp.has(userKey)) {
            userState = sp.getBoolean(userKey);
        }
        return !userState;
    }

    /**
     * 判断是否自动登录
     *
     * @param checked
     */
    public void setAutoLogin(boolean checked) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);

        String autoLogin = SharedPreferenceUtil.AUTO_LOGIN + Common.getInstance().loginUser.getUid();
        if (sp.has(autoLogin)) {
            sp.putBoolean(autoLogin, checked);
        } else {
            sp.putBoolean(autoLogin, checked);
        }

        String userStateKey = SharedPreferenceUtil.USER_STATE + Common.getInstance().loginUser.getUid();
        sp.putBoolean(userStateKey, true);
    }

    /**
     * 清除用户状态
     */
    public void clearUserState(long uid) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        String userKey = SharedPreferenceUtil.USER_STATE + uid;
        sp.putBoolean(userKey, false);
    }

    /**
     * 上传头像
     */
    public long uploadHeadIconReq(Context context, String logourl, HttpCallBack callback)
            throws Throwable {
        if (logourl != null && logourl.contains("_s")) {
            logourl = logourl.replace("_s", "");
        }
        long flag = UserHttpProtocol.userLogoUpdate(context, logourl, callback);
        mark(flag, SpaceModelReqTypes.UPLOAD_ICON);
        return flag;
    }
    /** 喜欢的相片列表 */
    public long favoritPhotosListReq( long userid , int pageno , int pagesize ,
                                      HttpCallBack callback )
    {
        long reqCode = PhotoHttpProtocol.photoLove( activity , userid , pageno , pagesize ,
                callback );
        mark( reqCode , SpaceModelReqTypes.FAVORIT_PHOTOS_LIST );
        return reqCode;
    }

    /** 相片列表 */
    public long photoListReq( long userid , int pageno , int pagesize , HttpCallBack callback )
    {
        long reqCode = PhotoHttpProtocol.photoUser( activity , userid , pageno , pagesize ,
                callback );
        mark( reqCode , SpaceModelReqTypes.PHOTO_LIST );
        return reqCode;
    }

}
