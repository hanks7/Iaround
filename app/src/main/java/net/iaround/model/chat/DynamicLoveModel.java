package net.iaround.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class: 动态点赞
 * Author：gh
 * Date: 2017/2/18 14:42
 * Email：jt_gaohang@163.com
 */
public class DynamicLoveModel implements Parcelable {

    public long uid;
    public long dynid;
    public String headPic;
    public String nickname;
    public int vip;
    public int gender;
    public long time;
    public long birthday;
    public String pic;
    public String content;

    protected DynamicLoveModel(Parcel in) {
        uid = in.readLong();
        dynid = in.readLong();
        headPic = in.readString();
        nickname = in.readString();
        vip = in.readInt();
        gender = in.readInt();
        time = in.readLong();
        birthday = in.readLong();
        pic = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeLong(dynid);
        dest.writeString(headPic);
        dest.writeString(nickname);
        dest.writeInt(vip);
        dest.writeInt(gender);
        dest.writeLong(time);
        dest.writeLong(birthday);
        dest.writeString(pic);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DynamicLoveModel> CREATOR = new Creator<DynamicLoveModel>() {
        @Override
        public DynamicLoveModel createFromParcel(Parcel in) {
            return new DynamicLoveModel(in);
        }

        @Override
        public DynamicLoveModel[] newArray(int size) {
            return new DynamicLoveModel[size];
        }
    };
}
