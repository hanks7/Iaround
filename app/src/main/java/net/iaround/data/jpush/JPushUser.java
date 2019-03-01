package net.iaround.data.jpush;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class: 推送消息
 * Author：gh
 * Date: 2017/9/26 18:19
 * Email：jt_gaohang@163.com
 */
public class JPushUser {

    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("vip")
    @Expose
    private int vip;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("age")
    @Expose
    private int age;
    @SerializedName("lat")
    @Expose
    private int lat;
    @SerializedName("lng")
    @Expose
    private int lng;
    @SerializedName("userid")
    @Expose
    private int userid;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("svip")
    @Expose
    private int svip;
    @SerializedName("nickname")
    @Expose
    private String nickname;
    @SerializedName("viplevel")
    @Expose
    private int viplevel;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLng() {
        return lng;
    }

    public void setLng(int lng) {
        this.lng = lng;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getSvip() {
        return svip;
    }

    public void setSvip(int svip) {
        this.svip = svip;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getViplevel() {
        return viplevel;
    }

    public void setViplevel(int viplevel) {
        this.viplevel = viplevel;
    }

}

