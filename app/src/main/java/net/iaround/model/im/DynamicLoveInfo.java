package net.iaround.model.im;

import net.iaround.ui.activity.DynamicDetailActivity;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.User;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-25 上午9:52:06
 * @Description: 动态中赞的实体
 */
public class DynamicLoveInfo implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 8211189387342658261L;
    public int total;//点赞总数
    public byte curruserlove;//当前用户是否点赞（0-否，1-是）
    public ArrayList<LoverUser> loveusers;//点赞用户列表

    public DynamicLoveInfo() {
        loveusers = new ArrayList<LoverUser>();
    }

    /**
     * 点赞的用户
     */
    public static class LoverUser extends BaseUserInfo implements Serializable {
//        public int horoscope;//星座 改版点赞用户新加字段
//        public long birthday;//生日  新加字段
        public String moodtext;//改版新加字段

//        public int getHoroscope() {
//            return horoscope;
//        }
//
//        public void setHoroscope(int horoscope) {
//            this.horoscope = horoscope;
//        }
//
//        public long getBirthday() {
//            return birthday;
//        }
//
//        public void setBirthday(long birthday) {
//            this.birthday = birthday;
//        }
//
//        public String getMoodtext() {
//            return moodtext;
//        }
//
//        public void setMoodtext(String moodtext) {
//            this.moodtext = moodtext;
//        }

        /**
         *
         */
        private static final long serialVersionUID = -5513839235398561221L;


    }

    //添加自己
    public void addLoverUser(User me) {
        LoverUser user = new LoverUser();
        user.userid = me.getUid();
        user.nickname = me.getNickname();
        user.icon = me.getIcon();
        user.viplevel = me.getViplevel();
        user.age = me.getAge();
        user.gender = me.getGender();
        user.lastonlinetime = me.getOnlineTime();
        user.selftext = me.getSign();
        if (loveusers == null) {
            loveusers = new ArrayList<LoverUser>();
        }
        if (DynamicDetailActivity.isRequestDetailSuccess) {
            loveusers.add(user);
        }
        curruserlove = 1;
        total++;
    }

    //移除自己
    public void removeLoverUser(long userid) {
        if (loveusers == null) {
            return;
        }

        if (DynamicDetailActivity.isRequestDetailSuccess) {
            for (int i = 0; i < loveusers.size(); i++) {
                if (loveusers.get(i).userid == userid) {
                    loveusers.remove(i);
                    loveusers.trimToSize();
                    curruserlove = 0;
                    total--;
                    break;
                }
            }
        } else {
            curruserlove = 0;
            total--;
            if (total < 0) {
                total = 0;
            }
        }
    }
}
