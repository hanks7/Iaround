package net.iaround.model.entity;

import java.util.List;

/**
 * @author：liush on 2016/12/8 19:29
 */
public class VisitorRecord extends BaseEntity {

    private VisitorData data;

    public static final int EXIST_MORE_DATA = 1;
    public static final int NO_MORE_DATA = 2;

    public VisitorData getData() {
        return data;
    }

    public void setData(VisitorData data) {
        this.data = data;
    }

    public class VisitorData{
        private List<Visitor> whoseeme;
        private int existData;

        public List<Visitor> getWhoseeme() {
            return whoseeme;
        }

        public void setWhoseeme(List<Visitor> whoseeme) {
            this.whoseeme = whoseeme;
        }

        public int getExistData() {
            return existData;
        }

        public void setExistData(int existData) {
            this.existData = existData;
        }
    }

    public class Visitor{
        private long uid;
        private String nickname;
        private int gender;
        private long birthday;
        private long time;
        private int vip;
        private double distance;
        private String moodtext;
        private String headPic;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public long getBirthday() {
            return birthday;
        }

        public void setBirthday(long birthday) {
            this.birthday = birthday;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getVip() {
            return vip;
        }

        public void setVip(int vip) {
            this.vip = vip;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getMoodtext() {
            return moodtext;
        }

        public void setMoodtext(String moodtext) {
            this.moodtext = moodtext;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        @Override
        public String toString() {
            return "Visitor{" +
                    "uid=" + uid +
                    ", nickname='" + nickname + '\'' +
                    ", gender=" + gender +
                    ", birthday=" + birthday +
                    ", time=" + time +
                    ", vip=" + vip +
                    ", distance=" + distance +
                    ", moodtext='" + moodtext + '\'' +
                    ", headPic='" + headPic + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VisitorRecord{" +
                "visistors=" + data +
                '}';
    }
}
