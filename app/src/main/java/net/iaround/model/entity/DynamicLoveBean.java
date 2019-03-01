package net.iaround.model.entity;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Class: 用户点赞
 * Author：gh
 * Date: 2016/12/19 17:44
 * Email：jt_gaohang@163.com
 */
public class DynamicLoveBean implements Parcelable{
    public String icon;
    public long uid;
    public int vip;
    public long time;
    public String nickname;
    public int gender;
    public long birthday;
    public int horoscope;
    public String moodtext;


    protected DynamicLoveBean(Parcel in) {
        icon = in.readString();
        uid = in.readLong();
        vip = in.readInt();
        time = in.readLong();
        nickname = in.readString();
        gender = in.readInt();
        birthday = in.readLong();
        horoscope = in.readInt();
        moodtext = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icon);
        dest.writeLong(uid);
        dest.writeInt(vip);
        dest.writeLong(time);
        dest.writeString(nickname);
        dest.writeInt(gender);
        dest.writeLong(birthday);
        dest.writeInt(horoscope);
        dest.writeString(moodtext);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DynamicLoveBean> CREATOR = new Creator<DynamicLoveBean>() {
        @Override
        public DynamicLoveBean createFromParcel(Parcel in) {
            return new DynamicLoveBean(in);
        }

        @Override
        public DynamicLoveBean[] newArray(int size) {
            return new DynamicLoveBean[size];
        }
    };
}
