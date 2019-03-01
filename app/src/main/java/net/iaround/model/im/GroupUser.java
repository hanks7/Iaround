package net.iaround.model.im;


/**
 * Class: 聊吧内用户信息
 * Author：gh
 * Date: 2017/6/15 16:58
 * Email：jt_gaohang@163.com
 */
public class GroupUser {

    private long userid;
    private String nickname;
    private String icon;
    private int vip;
    private int svip;
    private int viplevel;
    private String notes;//备注
    private int age;
    private String gender;
    private int group_role;//0=>群主,1=>管理,2=>普通成员,3=>非成员,4=>关注
    private int distance;
    private String horoscope;//星座
    private String moodtext;//个性签名
    private int relation;//关系
    private int audio;//是否在麦上 0 否  1是

    private int top;//是否排名上榜
    private int cat;//1 魅力榜 2 富豪榜
    private int type;// 1 本周 2 上周 3 本月 4 上月

    private int is_forbid;//是否被禁言
    private long expired_time;//禁言到期时间

    private int level;

    // TODO: 2017/8/21 添加等级字段
    

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIs_forbid() {
        return is_forbid;
    }

    public void setIs_forbid(int is_forbid) {
        this.is_forbid = is_forbid;
    }

    public long getExpired_time() {
        return expired_time;
    }

    public void setExpired_time(long expired_time) {
        this.expired_time = expired_time;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSvip() {
        return svip;
    }

    public void setSvip(int svip) {
        this.svip = svip;
    }

    public String getHoroscope() {
        return horoscope;
    }

    public void setHoroscope(String horoscope) {
        this.horoscope = horoscope;
    }

    public String getMoodtext() {
        return moodtext;
    }

    public void setMoodtext(String moodtext) {
        this.moodtext = moodtext;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }


    public int getDistance() {

        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }



    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getViplevel() {
        return viplevel;
    }

    public void setViplevel(int viplevel) {
        this.viplevel = viplevel;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getGroup_role() {
        return group_role;
    }

    public void setGroup_role(int group_role) {
        this.group_role = group_role;
    }
}
