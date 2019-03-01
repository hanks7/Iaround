package net.iaround.model.database;

import android.content.Context;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.ContactInfo;
import net.iaround.model.im.ContactsBean;
import net.iaround.model.im.contactTextBean;
import net.iaround.tools.PathUtil;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.Model;
import net.iaround.ui.datamodel.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 好友搜索模块网络数据接口集合 主要是对于好友列表的数据处理
 *
 * @author 余勋杰
 */
public class FriendModel extends Model {
    private static boolean firstReq = true;
    private static ArrayList<User> friends = new ArrayList<User>();//只保留25个好友信息
    private static ArrayList<User> follows = new ArrayList<User>();//只保留25个关注信息
    private static ArrayList<User> fans = new ArrayList<User>();//只保留25个关注信息
    private ArrayList<User> allFriendList = new ArrayList<User>();// 保存所有好友的信息,用于进入好友列表和转发消息的页面使用
    private static contactTextBean Textbean;
    private static long friendsVersion;
    private static FriendModel friendModel;
    private Context context;

    private int myFriendsCount;
    private int myFollowsCount;
    private int myFansCount;
    private int myGroupCount;
    private ContactsBean bean;

    private FriendModel() {
        getContactsBufferFromFile();
    }

    /**
     * 判断是否第一次请求好友列表
     *
     * @return
     */
    public static boolean isFirstReq() {
        return firstReq;
    }

    public static FriendModel getInstance(Context context) {
        if (friendModel == null) {
            friendModel = new FriendModel();
        }
        if (context == null){
            friendModel.context = BaseApplication.appContext;
        }else {
            friendModel.context = context.getApplicationContext();
        }
        return friendModel;
    }

    /* 根据距离排列好友类表 */
    private static ArrayList<User> sortFriend(final List<User> array) {
        ArrayList<User> friends = new ArrayList<User>();
        if (array == null || array.size() <= 0) {
            return friends;
        }

        // 根据距离排序
        FriendItem orderByDistanceList = new FriendItem();
        orderByDistanceList.value = array.get(0);
        for (int i = 1, size = array.size(); i < size; i++) {
            FriendItem item = new FriendItem();
            item.value = array.get(i);
            int userDistance = item.value.getDistance();
            if (userDistance < 0) {
                userDistance = Integer.MAX_VALUE;
            }
            if (userDistance < orderByDistanceList.value.getDistance()) { // 更换为队首
                item.next = orderByDistanceList;
                orderByDistanceList.previous = item;
                orderByDistanceList = item;
                continue;
            }

            FriendItem tryItem = orderByDistanceList;
            boolean inserted = false;
            while (tryItem.next != null) {
                tryItem = tryItem.next;
                if (userDistance < tryItem.value.getDistance()) { // 插入到tryItem前面
                    item.previous = tryItem.previous;
                    tryItem.previous.next = item;
                    item.next = tryItem;
                    tryItem.previous = item;
                    inserted = true;
                    break;
                }
            }

            if (tryItem.next == null && !inserted) { // 插入到队尾
                tryItem.next = item;
                item.previous = tryItem;
            }
        }

        // 做在线与否的分离
        FriendItem item = orderByDistanceList;
        while (item != null) {
            User user = item.value;
            if (user.isOnline()) {
                friends.add(user);
            }
            item = item.next;
        }
        item = orderByDistanceList;
        while (item != null) {
            User user = item.value;
            if (!user.isOnline()) {
                friends.add(user);
            }
            item = item.next;
        }

        // 确保小秘书在队列头部
        User user114 = friends.get(0);
        if (user114.getUid() != Config.CUSTOM_SERVICE_UID) {
            for (User user : friends) {
                if (user.getUid() == Config.CUSTOM_SERVICE_UID) {
                    friends.remove(user);
                    friends.add(0, user);
                    break;
                }
            }
        }

        return friends;
    }

    /**
     * 通过给定的数据拼凑一个交友对象字符串
     * <p>
     * 这个串不能直接拿来显示
     */
    public String getSign(String thinktext, int withwho, int beginage,
                          int endage) {
        StringBuffer sign = new StringBuffer();
        sign.append(thinktext == null ? "" : thinktext).append('\r');
        sign.append(withwho).append('\r');
        sign.append(beginage).append('\r');
        sign.append(endage);
        return sign.toString();
    }

    /**
     * 解析特定结构的串，得到可以显示的交友目的
     */
    public String getSign(final String sign) {
        String res = "";

        if (sign != null) {
            String[] parts = sign.split("\r");
            if (parts.length >= 4) {
                if (parts[0] != null && !parts[0].equals("null")
                        && parts[0].trim().length() > 0) {
                    try {
                        int type = Integer.parseInt(parts[1]);
                        parts[1] = type == 0 ? context
                                .getString(R.string.someone)
                                : (type == 1 ? context
                                .getString(R.string.a_girl) : context
                                .getString(R.string.a_boy));

                    } catch (Exception ex) {
                        parts[1] = context.getString(R.string.someone);
                    }

                    // modify for bug:5405
                    if ("81".equals(parts[3]) || "80+".equals(parts[3])) {

                        res = String.format(
                                context.getString(R.string.sign_text_v2),
                                parts[1], parts[2], parts[0]);
                    } else {

                        // modify by shifx 2014-03-12
                        if (Integer.parseInt(parts[2]) == 0
                                || Integer.parseInt(parts[3]) == 0) {
                            parts[2] = "20";
                            parts[3] = "100";
                            res = String.format(
                                    context.getString(R.string.sign_text),
                                    parts[1], parts[2], parts[3], parts[0]);
                        } else {
                            res = String.format(
                                    context.getString(R.string.sign_text),
                                    parts[1], parts[2], parts[3], parts[0]);
                        }
                    }
                }
            }
        }
        return res;
    }

    /**
     * 获取关注列表
     */
    public ArrayList<User> getFollowsList() {
        return follows;
    }

    /**
     * 获取粉丝列表
     */
    public ArrayList<User> getFansList() {
        return fans;
    }

    /**
     * 返回好友列表
     */
    public List<User> getFriends() {
        return friends;
    }

    public ArrayList<User> getFriendsList() {
        return friends;
    }

    /**
     * 清空好友列表
     */
    public void clearFriends() {
        if (friends != null) {
            friends.clear();
        }
        friendsVersion = 0;
    }

    public void clearFollows() {
        follows.clear();
    }

    /**
     * 更新好友列表中的特定用户，如果没有这个用户， 就加入这个用户到列表里面
     */
    public void updateFriendList(User friend) {
        if (friend == null) {
            return;
        }
        long friendUid = friend.getUid();
        for (int i = 0, size = friends.size(); i < size; i++) {
            if (friends.get(i).getUid() == friendUid) {
                friends.remove(i);
                myFriendsCount--;
                break;
            }
        }
        friends.add(0, friend);
        myFriendsCount++;
        // friends = sortFriend( friends );

        friendsVersion++;
    }

    public void updateFriendVersion() {
        friendsVersion++;
    }

    public long getFriendListVersion() {
        return friendsVersion;
    }

    public void resetFriendListVersion() {
        friendsVersion = 0;
    }

    /**
     * 根据uid从好友列表删除用户
     */
    public void deleteFriend(long uid) {
        for (int i = 0, size = friends.size(); i < size; i++) {
            if (friends.get(i).getUid() == uid) {
                friends.remove(i);
                myFriendsCount--;
                friendsVersion++;
                break;
            }
        }
    }

    /**
     * 取消某个关注，同时删除好友
     */
    public void deleteFollow(long uid) {
        for (int i = 0; i < follows.size(); i++) {
            if (follows.get(i).getUid() == uid) {
                follows.remove(i);
                this.myFollowsCount--;
                break;
            }
        }
        deleteFriend(uid);
    }

    /**
     * 判断是否为好友
     */
    public boolean isMyFriend(long uid) {
        for (int i = 0, size = friends.size(); i < size; i++) {
            if (friends.get(i).getUid() == uid) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取某一个好友
     */
    public User getUser(long uid) {
        for (int i = 0, size = friends.size(); i < size; i++) {
            User user = friends.get(i);
            if (user.getUid() == uid) {
                return user;
            }
        }

        for (int i = 0, size = follows.size(); i < size; i++) {
            User user = follows.get(i);
            if (user.getUid() == uid) {
                return user;
            }
        }
        return null;
    }

    /**
     * 释放
     */
    public void reset() {
        saveContactsBufferToFile();

        friendModel = null;
        firstReq = true;
        friendsVersion = 0;
        friends.clear();
        follows.clear();
    }

    /**
     * 新附近好友协议返回
     */
    public void friendNearbyRes(String result, HashMap<String, Object> resMap)
            throws Exception {
        JSONObject json = new JSONObject(result);
        // resMap.put( "reqType" , FriendModelReqTypes.USER_NEARBY );
        int status = json.optInt("status");
        resMap.put("status", status);
        ArrayList<User> users = new ArrayList<User>();
        resMap.put("users", users);
        if (status != 200) {
            resMap.put("error", result);
        } else {
            JSONArray usersJson = json.optJSONArray("users");
            if (usersJson != null) {
                for (int i = 0, size = usersJson.length(); i < size; i++) {
                    JSONObject userJson = usersJson.optJSONObject(i);
                    if (userJson != null) {
                        JSONObject userTmpJson = userJson.optJSONObject("user");
                        User user = parseUser(userTmpJson, 1);
                        GeoData geo = new GeoData();
                        geo.setLat(userJson.optInt("lat"));
                        geo.setLng(userJson.optInt("lng"));
                        user.setLoc(geo);
                        user.setDistance(userJson.optInt("distance"));
                        user.setPersonalInfor(userJson
                                .optString("selftext", ""));
                        users.add(user);
                    }
                }
            }
        }
    }

    public void setContactText(contactTextBean TextBean) {
        Textbean = TextBean;
    }

    public contactTextBean getContactText() {
        return Textbean;
    }

    public static class FriendItem {
        public FriendItem previous;
        public User value;
        public FriendItem next;
    }

    /**
     * 缓存我的好友数据, 以id的倒序排序
     */
    public void cacheMyFriends(ArrayList<User> users) {
        Collections.sort(users, new UserIdComparator());
        FriendModel.friends.clear();
        FriendModel.friends.addAll(users);
        firstReq = false;
        friendsVersion++;
    }

    /**
     * 缓存我的关注
     */
    public void cacheMyFollows(ArrayList<User> users) {
        follows.clear();
        follows.addAll(users);
    }

    /**
     * 缓存我的关注
     */
    public void cacheMyFans(ArrayList<User> users) {
        fans.clear();
        fans.addAll(users);
    }

    /**
     * 添加一个关注
     */
    public void addFollow(User user) {
        for (int i = 0; i < follows.size(); i++) {
            if (follows.get(i).getUid() == user.getUid()) {
                follows.remove(follows.get(i));
                this.myFollowsCount--;
                break;
            }
        }
        follows.add(0, user);
        // follows = sortFriend(follows);
        this.myFollowsCount++;
    }

    public void setMyFriendsCount(int count) {
        this.myFriendsCount = count;
    }

    public void setMyFollowsCount(int count) {
        this.myFollowsCount = count;
    }

    public void setMyGroupCount(int count) {
        this.myGroupCount = count;
    }

    public int getMyFriendsCount() {
        return this.myFriendsCount;
    }

    public int getMyFollowsCount() {
        return this.myFollowsCount;
    }

    public int getMyGroupCount() {
        return this.myGroupCount;
    }

    public int getMyFansCount() {
        return myFansCount;
    }

    public void setMyFansCount(int myFansCount) {
        this.myFansCount = myFansCount;
    }

    /**
     * 更新好友和关注列表的备注显示
     */
    public void updateFriendAttentionRemark(long userId, String remark) {
        for (User friend : friends) {
            if (friend.getUid() == userId) {
                friend.setNoteName(remark);
            }
        }
        for (User follow : follows) {
            if (follow.getUid() == userId) {
                follow.setNoteName(remark);
            }
        }
    }

    /**
     * 将大部分协议下发的BaseUser转换成User
     */
    public User convertBaseUserToUser(BaseUserInfo user) {
        User tmp = new User();
        tmp.setIcon(user.icon);
        tmp.setUid(user.userid);
        tmp.setLat(user.lat);
        tmp.setLng(user.lng);
        tmp.setWeibo(user.weibo);
        tmp.setNoteName(user.notes);
        tmp.setViplevel(user.viplevel);
        tmp.setSVip(user.svip);
        tmp.setJob(user.occupation);
        tmp.setAge(user.age);
        tmp.setSex("m".equals(user.gender) ? 1 : 2);
        tmp.setLastLoginTime(user.lastonlinetime);
        tmp.setOnline(!"n".equals(user.isonline));
        tmp.setNickname(user.nickname);
        tmp.setDistance(user.distance);
        tmp.setPhotoNum(user.photonum);
        tmp.setForbid(user.forbid != 0);
        tmp.setPhotouploadleft(user.photouploadleft);
        tmp.setTodayphotostotal(user.todayphotostotal);
        tmp.setPhotouploadtotal(user.photouploadtotal);
        tmp.setHoroscope(user.horoscope);
        return tmp;
    }

    /**
     * 根据id倒序
     */
    private class UserIdComparator implements Comparator<User> {

        @Override
        public int compare(User lhs, User rhs) {
            long lhsUserId = lhs.getUid();
            long rhsUserId = rhs.getUid();
            if (lhsUserId > rhsUserId) {
                return -1;
            } else {
                return 1;
            }

        }
    }

    /**
     * 提取缓存
     */
    private void getContactsBufferFromFile() {
        bean = new ContactsBean();
        try {
            bean = (ContactsBean) getBufferFromFile(PathUtil
                    .getContactsFilePath());
        } catch (Exception e) {
            bean = null;
        }

        if (bean != null) {
            cacheMyFollows(bean.attentions);
            cacheMyFriends(bean.friends);

            setMyFollowsCount(bean.attentionsnum);
            setMyFriendsCount(bean.friendsnum);
            setMyGroupCount(bean.groupnum);

            Common.getInstance().loginUser.setFansNum(bean.fansnum);
        }
    }

    public void saveContactsBufferToFile() {
        bean = new ContactsBean();
        bean.attentions = getFollowsList();
        bean.attentionsnum = getMyFollowsCount();
        bean.friends = getFriendsList();
        bean.friendsnum = getMyFriendsCount();
        bean.fansnum = Common.getInstance().loginUser.getFansNum();
        bean.groupnum = myGroupCount;

        saveBufferToFile(PathUtil.getContactsFilePath(), bean);
    }

    public ArrayList<User> getAllFriendList() {
        return allFriendList;
    }

    public void setAllFriendList(ArrayList<User> allFriendList) {
        this.allFriendList = allFriendList;
    }

}
