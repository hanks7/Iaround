
package net.iaround.ui.group.bean;


import com.google.gson.annotations.Expose;

import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.datamodel.User;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: GroupSearchUser
 * @Description: 群成员实体信息
 * @date 2013-12-11 上午11:57:59
 */
public class GroupSearchUser implements Comparable<GroupSearchUser> {

    /**
     * 标题类型——标题
     */
    public final static int HEADER = 0;
    /**
     * 标题类型——内容
     */
    public final static int CONTENT = 1;


    /**
     * 用户基础信息
     */
    public GroupUser user;

    /**
     * 用户角色(0：创建者，1：管理员，2：普通成员)
     */
    public int grouprole;

    /**
     * 距离 单位（m） ，如果为-1 则表示无法得到距离
     */
    public double distance;

    /**
     * 是否在线，y：在线，n：离线
     */
    public String isonline;

    /**
     * 最近登录时间 （在线时为0）
     */
    public long lastonlinetime;

    /**
     * 最后发言时间 （未发言时为0）
     */
    public String talktime;

    /**
     * 是否被禁言，y：禁言，n：未禁言
     */
    public String isforbid = "y";

    /**
     * 是否被禁言，1：禁言，0：未禁言
     */
    public int forbid;

    /**
     * 是否选中（显示用）
     */
    @Expose(deserialize = false, serialize = false)
    public boolean isCheck = false;

    /**
     * 标题类型，显示用，用户数据默认为-1
     */
    @Expose(deserialize = false, serialize = false)
    public int headerType = 1;

    /**
     * 显示其他内容（显示用）
     */
    @Expose(deserialize = false, serialize = false)
    public int contentType = 0;

    /** 粉丝专用 */

    /**
     * 关系， 0:自己 ，1：好友 ，2：陌生人，3：关注，4：粉丝
     */
    public int relation;

    /**
     * 1新粉丝；0旧粉丝
     */
    public int newflag;

    /**
     * 来源类型
     */
    public int contact;

    /**
     * @Title: isOnline
     * @Description: 是否在线
     */
    public boolean isOnline() {
        return isonline.equals("y");
    }

    @Override
    public int compareTo(GroupSearchUser another) {
        return grouprole - another.grouprole;
    }

    public static User convertToUser(GroupSearchUser contactUser) {
        if (contactUser != null) {
            User user = new User();

            user.setDistance((int) contactUser.distance);
            user.setIcon(contactUser.user.icon);
            user.setUid(contactUser.user.userid);
            user.setNoteName(contactUser.user.notes);
            user.setViplevel(contactUser.user.viplevel);
            user.setSVip(contactUser.user.svip);
            user.setNickname(contactUser.user.nickname);
            user.setPhotoNum(contactUser.user.photonum);

            int sex = 2;
            if (!CommonFunction.isEmptyOrNullStr(contactUser.user.gender)) {
                if (contactUser.user.gender.equals("m")) {
                    sex = 1;
                } else if (contactUser.user.gender.equals("f")) {
                    sex = 2;
                }
            }
            user.setSex(sex);
            user.setAge(contactUser.user.age);
            user.setLat(contactUser.user.lat);
            user.setLng(contactUser.user.lng);
            user.setPersonalInfor(contactUser.user.selftext);
            user.setSign(contactUser.user.selftext);
            user.setOnline(contactUser.user.isOnline());
            user.setForbid(contactUser.user.isForbidUser());
            user.setLastLoginTime(contactUser.user.lastonlinetime);
            user.setWeibo(contactUser.user.weibo);

            return user;
        }
        return null;
    }


}
