package net.iaround.model.entity;

import net.iaround.ui.datamodel.Photos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：liush on 2016/12/10 20:16
 */
public class UserInfoEntity extends BaseEntity implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {

        private String nickname;//昵称

        public void setNotes(String notes) {
            this.notes = notes;
        }

        private String notes;//昵称
        private ArrayList<String> headPicThum;//照片缩略图集合
        private ArrayList<String> headPic;//头像集合
        private String birthday;//生日
        private int age;//年龄
        private String complete;
        private int charmnum;

        public int voiceUserType;//用户角色 0，普通用户 1，语音主播

        public int userType;//用户角色 0，普通用户 1，视频主播
        public int love = 2;//消耗爱心

        public int getCharmnum() {
            return charmnum;
        }

        public void setCharmnum(int charmnum) {
            this.charmnum = charmnum;
        }

        public String getComplete() {
            return complete;
        }

        public void setComplete(String complete) {
            this.complete = complete;
        }

        private ArrayList<Photos.PhotosBean> photos;

        public ArrayList<Photos.PhotosBean> getHeadPhonts() {
            return photos;
        }

        public void setHeadPhonts(ArrayList<Photos.PhotosBean> headPhonts) {
            this.photos = headPhonts;
        }

        private String gender;//性别
        private int horoscope; //星座
        private String moodtext; //签名

        private int dyCount;//动态数
        private ArrayList<String> dynamic;//动态的照片地址集合
        private int vip;//vip
        private int ischat;//是不是第一次聊

        public int getIschat() {
            return ischat;
        }

        public void setIschat(int ischat) {
            this.ischat = ischat;
        }

        private long vipexpire;//
        private int level;//vip等级
        private ArrayList<String> gift;//礼物照片的地址集合
        private Hobby hobby;//爱好
        private int relation;//好友关系1，好友 2，陌生人3.已关注，4，粉丝
        private long distance;//距离
        private long logouttime;//最后登录时间
        private int svip;
        private int viplevel;

        public int getSvip() {
            return svip;
        }

        public void setSvip(int svip) {
            this.svip = svip;
        }

        public int getViplevel() {
            return viplevel;
        }

        public void setViplevel(int viplevel) {
            this.viplevel = viplevel;
        }

        public int getRelation() {
            return relation;
        }

        public void setRelation(int relation) {
            this.relation = relation;
        }

        public long getDistance() {
            return distance;
        }

        public void setDistance(long distance) {
            this.distance = distance;
        }

        public long getLogouttime() {
            return logouttime;
        }

        public void setLogouttime(long logouttime) {
            this.logouttime = logouttime;
        }

        /**
         * 接收一个键值对的集合数组
         * 1：遇见ID  2:恋爱状态  3：身高  4：体重  6:职业  7：家乡
         * 后来兄弟看不懂代码别怪我，都是被**给坑的，本来定义好的数据结构非要改成这样，毫无可读性
         */
        private ArrayList<AboutMe> aboutme;//关于
        private AboutMe2 aboutMe2;

        private Authen bind;//认证信息
        private Location location;//最后位置
        private String usephone;//使用手机

        private Secret secret;//私密资料

        private int locationFlag;//是否显示最后位置
        private int phoneFlag;//是否显示使用手机

        private int hadsetname;//性别是否修改过

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }


        public String getNotes() {
            if (notes == null){
                notes = "";
            }else if (notes.equals("null")){
                notes = "";
            }
            return notes;
        }

        public ArrayList<String> getHeadPicThum() {
            return headPicThum;
        }

        public void setHeadPicThum(ArrayList<String> headPicThum) {
            this.headPicThum = headPicThum;
        }

        public ArrayList<String> getHeadPic() {
            return headPic;
        }

        public void setHeadPic(ArrayList<String> headPic) {
            this.headPic = headPic;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public int getHoroscope() {
            return horoscope;
        }

        public void setHoroscope(int horoscope) {
            this.horoscope = horoscope;
        }

        public String getMoodtext() {
            return moodtext;
        }

        public void setMoodtext(String moodtext) {
            this.moodtext = moodtext;
        }

        public int getDyCount() {
            return dyCount;
        }

        public void setDyCount(int dyCount) {
            this.dyCount = dyCount;
        }

        public ArrayList<String> getDynamic() {
            if (dynamic == null){
                dynamic = new ArrayList<>();
            }
            return dynamic;
        }

        public void setDynamic(ArrayList<String> dynamic) {
            this.dynamic = dynamic;
        }

        public int getVip() {
            return vip;
        }

        public void setVip(int vip) {
            this.vip = vip;
        }

        public long getVipexpire() {
            return vipexpire;
        }

        public void setVipexpire(long vipexpire) {
            this.vipexpire = vipexpire;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public ArrayList<String> getGift() {
            return gift;
        }

        public void setGift(ArrayList<String> gift) {
            this.gift = gift;
        }

        public Hobby getHobby() {
            return hobby;
        }

        public void setHobby(Hobby hobby) {
            this.hobby = hobby;
        }

        public ArrayList<AboutMe> getAboutme() {
            return aboutme;
        }

        public void setAboutme(ArrayList<AboutMe> aboutme) {
            this.aboutme = aboutme;
        }

        public AboutMe2 getAboutMe2() {
            return aboutMe2;
        }

        public void setAboutMe2(AboutMe2 aboutMe2) {
            this.aboutMe2 = aboutMe2;
        }

        public Authen getBind() {
            return bind;
        }

        public void setBind(Authen bind) {
            this.bind = bind;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getUsephone() {
            return usephone;
        }

        public void setUsephone(String usephone) {
            this.usephone = usephone;
        }

        public Secret getSecret() {
            return secret;
        }

        public void setSecret(Secret secret) {
            this.secret = secret;
        }

        public int getLocationFlag() {
            return locationFlag;
        }

        public void setLocationFlag(int locationFlag) {
            this.locationFlag = locationFlag;
        }

        public int getPhoneFlag() {
            return phoneFlag;
        }

        public void setPhoneFlag(int phoneFlag) {
            this.phoneFlag = phoneFlag;
        }

        public class Hobby implements Serializable {
            private ArrayList<String> common;
            private ArrayList<String> special;

            public ArrayList<String> getCommon() {
                return common;
            }

            public void setCommon(ArrayList<String> common) {
                this.common = common;
            }

            public ArrayList<String> getSpecial() {
                return special;
            }

            public void setSpecial(ArrayList<String> special) {
                this.special = special;
            }
        }

        public class Authen implements Serializable {
            private int phoneValid;//是否过期
            private String phone;//
            private int photoValid;

            public int getPhoneValid() {
                return phoneValid;
            }

            public void setPhoneValid(int phoneValid) {
                this.phoneValid = phoneValid;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public int getPhotoValid() {
                return photoValid;
            }

            public void setPhotoValid(int photoValid) {
                this.photoValid = photoValid;
            }
        }

        public class AboutMe implements Serializable {
            private String uname;//键
            private String uvalue;//值

            public String getUname() {
                return uname;
            }

            public void setUname(String uname) {
                this.uname = uname;
            }

            public String getUvalue() {
                return uvalue;
            }

            public void setUvalue(String uvalue) {
                this.uvalue = uvalue;
            }
        }

        public class AboutMe2 implements Serializable {
            /**
             * 前面已经说了，本来设计好的字段，现在被迫改了，只能改成2了
             */
            private String uid;//uid
            private int love;//恋爱状态
            private int occupation;//职业
            private String hometown;//家乡
            private int height;//身高
            private int weight;//体重

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public int getLove() {
                return love;
            }

            public void setLove(int love) {
                this.love = love;
            }

            public int getOccupation() {
                return occupation;
            }

            public void setOccupation(int occupation) {
                this.occupation = occupation;
            }

            public String getHometown() {
                return hometown;
            }

            public void setHometown(String hometown) {
                this.hometown = hometown;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWeight() {
                return weight;
            }

            public void setWeight(int weight) {
                this.weight = weight;
            }
        }

        public class Secret extends SecretEntity {
        }

        public class Location implements Serializable {

            private String address;//地址
            private long lat;//经度
            private long lng;//维度

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public long getLat() {
                return lat;
            }

            public void setLat(long lat) {
                this.lat = lat;
            }

            public long getLng() {
                return lng;
            }

            public void setLng(long lng) {
                this.lng = lng;
            }
        }


        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }


        private List<SkillBean> skill;

        public List<SkillBean> getSkill() {
            return skill;
        }

        public void setSkill(List<SkillBean> skill) {
            this.skill = skill;
        }

        public  class SkillBean implements Serializable{
            public SkillBean() {
            }

            /**
             * ID : 1
             * UserID : 61001617
             * SkillID : 3
             * SkillLevel : 4
             * Mastery : 0
             * NextLevel : 5
             * CreateTime : 1502682151
             * UpdateTIme : 1503132240
             * Name : 保护费
             * ICON :
             * Gif : protect_2000_29_first
             */

            private int ID;
            private int UserID;
            private int SkillID;
            private int SkillLevel;
            private int Mastery;
            private int NextLevel;
            private int CreateTime;
            private int UpdateTIme;
            private String Name;
            private String ICON;
            private String Gif;
            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public int getUserID() {
                return UserID;
            }

            public void setUserID(int UserID) {
                this.UserID = UserID;
            }

            public int getSkillID() {
                return SkillID;
            }

            public void setSkillID(int SkillID) {
                this.SkillID = SkillID;
            }

            public int getSkillLevel() {
                return SkillLevel;
            }

            public void setSkillLevel(int SkillLevel) {
                this.SkillLevel = SkillLevel;
            }

            public int getMastery() {
                return Mastery;
            }

            public void setMastery(int Mastery) {
                this.Mastery = Mastery;
            }

            public int getNextLevel() {
                return NextLevel;
            }

            public void setNextLevel(int NextLevel) {
                this.NextLevel = NextLevel;
            }

            public int getCreateTime() {
                return CreateTime;
            }

            public void setCreateTime(int CreateTime) {
                this.CreateTime = CreateTime;
            }

            public int getUpdateTIme() {
                return UpdateTIme;
            }

            public void setUpdateTIme(int UpdateTIme) {
                this.UpdateTIme = UpdateTIme;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getICON() {
                return ICON;
            }

            public void setICON(String ICON) {
                this.ICON = ICON;
            }

            public String getGif() {
                return Gif;
            }

            public void setGif(String Gif) {
                this.Gif = Gif;
            }
        }

        /**
         * ranking : {"regalWeek":1,"regalMonth":1}
         */

//        public String ranking;

        private List<RankingBean> ranking;

        public List<RankingBean> getRanking() {
            return ranking;
        }

        public void setRanking(List<RankingBean> ranking) {
            this.ranking = ranking;
        }

        public  class RankingBean implements Serializable{
            /**
             * activeWeekSkill1 : 1
             * activeMonthSkill1 : 1
             * activeWeekSkill3 : 1
             * activeMonthSkill3 : 1
             */

            private int charmWeek;
            private int charmMonth;
            private int regalWeek;
            private int regalMonth;
            private int activeWeekSkill1;
            private int activeMonthSkill1;
            private int updateWeekSkill1;
            private int updateMonthSkill1;
            private int activeWeekSkill2;
            private int activeMonthSkill2;
            private int updateWeekSkill2;
            private int updateMonthSkill2;
            private int activeWeekSkill3;
            private int activeMonthSkill3;
            private int updateWeekSkill3;
            private int updateMonthSkill3;
            private int activeWeekSkill4;
            private int activeMonthSkill4;

            private int updateWeekSkill4;
            private int updateMonthSkill4;
            private int activeWeekSkill5;
            private int activeMonthSkill5;
            private int updateWeekSkill5;
            private int updateMonthSkill5;

            public int getCharmWeek() {
                return charmWeek;
            }

            public void setCharmWeek(int charmWeek) {
                this.charmWeek = charmWeek;
            }

            public int getCharmMonth() {
                return charmMonth;
            }

            public void setCharmMonth(int charmMonth) {
                this.charmMonth = charmMonth;
            }

            public int getRegalWeek() {
                return regalWeek;
            }

            public void setRegalWeek(int regalWeek) {
                this.regalWeek = regalWeek;
            }

            public int getRegalMonth() {
                return regalMonth;
            }

            public void setRegalMonth(int regalMonth) {
                this.regalMonth = regalMonth;
            }

            public int getUpdateWeekSkill1() {
                return updateWeekSkill1;
            }

            public void setUpdateWeekSkill1(int updateWeekSkill1) {
                this.updateWeekSkill1 = updateWeekSkill1;
            }

            public int getUpdateMonthSkill1() {
                return updateMonthSkill1;
            }

            public void setUpdateMonthSkill1(int updateMonthSkill1) {
                this.updateMonthSkill1 = updateMonthSkill1;
            }

            public int getActiveWeekSkill2() {
                return activeWeekSkill2;
            }

            public void setActiveWeekSkill2(int activeWeekSkill2) {
                this.activeWeekSkill2 = activeWeekSkill2;
            }

            public int getActiveMonthSkill2() {
                return activeMonthSkill2;
            }

            public void setActiveMonthSkill2(int activeMonthSkill2) {
                this.activeMonthSkill2 = activeMonthSkill2;
            }

            public int getUpdateWeekSkill2() {
                return updateWeekSkill2;
            }

            public void setUpdateWeekSkill2(int updateWeekSkill2) {
                this.updateWeekSkill2 = updateWeekSkill2;
            }

            public int getUpdateMonthSkill2() {
                return updateMonthSkill2;
            }

            public void setUpdateMonthSkill2(int updateMonthSkill2) {
                this.updateMonthSkill2 = updateMonthSkill2;
            }

            public int getUpdateWeekSkill3() {
                return updateWeekSkill3;
            }

            public void setUpdateWeekSkill3(int updateWeekSkill3) {
                this.updateWeekSkill3 = updateWeekSkill3;
            }

            public int getUpdateMonthSkill3() {
                return updateMonthSkill3;
            }

            public void setUpdateMonthSkill3(int updateMonthSkill3) {
                this.updateMonthSkill3 = updateMonthSkill3;
            }

            public int getActiveWeekSkill4() {
                return activeWeekSkill4;
            }

            public void setActiveWeekSkill4(int activeWeekSkill4) {
                this.activeWeekSkill4 = activeWeekSkill4;
            }

            public int getActiveMonthSkill4() {
                return activeMonthSkill4;
            }

            public void setActiveMonthSkill4(int activeMonthSkill4) {
                this.activeMonthSkill4 = activeMonthSkill4;
            }

            public int getUpdateWeekSkill4() {
                return updateWeekSkill4;
            }

            public void setUpdateWeekSkill4(int updateWeekSkill4) {
                this.updateWeekSkill4 = updateWeekSkill4;
            }

            public int getUpdateMonthSkill4() {
                return updateMonthSkill4;
            }

            public void setUpdateMonthSkill4(int updateMonthSkill4) {
                this.updateMonthSkill4 = updateMonthSkill4;
            }

            public int getActiveWeekSkill5() {
                return activeWeekSkill5;
            }

            public void setActiveWeekSkill5(int activeWeekSkill5) {
                this.activeWeekSkill5 = activeWeekSkill5;
            }

            public int getActiveMonthSkill5() {
                return activeMonthSkill5;
            }

            public void setActiveMonthSkill5(int activeMonthSkill5) {
                this.activeMonthSkill5 = activeMonthSkill5;
            }

            public int getUpdateWeekSkill5() {
                return updateWeekSkill5;
            }

            public void setUpdateWeekSkill5(int updateWeekSkill5) {
                this.updateWeekSkill5 = updateWeekSkill5;
            }

            public int getUpdateMonthSkill5() {
                return updateMonthSkill5;
            }

            public void setUpdateMonthSkill5(int updateMonthSkill5) {
                this.updateMonthSkill5 = updateMonthSkill5;
            }



            public int getActiveWeekSkill1() {
                return activeWeekSkill1;
            }

            public void setActiveWeekSkill1(int activeWeekSkill1) {
                this.activeWeekSkill1 = activeWeekSkill1;
            }

            public int getActiveMonthSkill1() {
                return activeMonthSkill1;
            }

            public void setActiveMonthSkill1(int activeMonthSkill1) {
                this.activeMonthSkill1 = activeMonthSkill1;
            }

            public int getActiveWeekSkill3() {
                return activeWeekSkill3;
            }

            public void setActiveWeekSkill3(int activeWeekSkill3) {
                this.activeWeekSkill3 = activeWeekSkill3;
            }

            public int getActiveMonthSkill3() {
                return activeMonthSkill3;
            }

            public void setActiveMonthSkill3(int activeMonthSkill3) {
                this.activeMonthSkill3 = activeMonthSkill3;
            }
        }

        /**
         * affect : {"gag":0,"deny":0}
         */

        private AffectBean affect;

        public AffectBean getAffect() {
            return affect;
        }

        public void setAffect(AffectBean affect) {
            this.affect = affect;
        }

        public  class AffectBean implements  Serializable{
            /**
             * gag : 0
             * deny : 0
             */

            private int gag; //0 代表未禁言 1 代表禁言
            private int deny;// 0代表未被提出 1代表提出

            public int getGag() {
                return gag;
            }

            public void setGag(int gag) {
                this.gag = gag;
            }

            public int getDeny() {
                return deny;
            }

            public void setDeny(int deny) {
                this.deny = deny;
            }
        }

        private PlayGroupBean playGroup;

        public PlayGroupBean getPlayGroup() {
            return playGroup;
        }

        public void setPlayGroup(PlayGroupBean playGroup) {
            this.playGroup = playGroup;
        }

        public  class PlayGroupBean implements Serializable{

            private int chatGroupID;
            private int chatGroupSubCateID;
            private String chatGroupIcon;
            private String chatGroupName;
            private int userID;
            private int lat;
            private int lng;
            private String address;
            private int maxUserNumber;
            private int maxRange;
            private int isHot;
            private int level;
            private long createTime;
            private int status;
            private String geoHash;
            private int historyMaxRange;
            private String categoryicon;
            private String content;
            private String landmarkid;
            private String landmarkname;
            private int chatgroupNewCateID;
            private String category;
            private int usercount;
            private int classify;
            private int newLevel;
            private int postbarID;

            public int getChatGroupID() {
                return chatGroupID;
            }

            public void setChatGroupID(int chatGroupID) {
                this.chatGroupID = chatGroupID;
            }

            public int getChatGroupSubCateID() {
                return chatGroupSubCateID;
            }

            public void setChatGroupSubCateID(int chatGroupSubCateID) {
                this.chatGroupSubCateID = chatGroupSubCateID;
            }

            public String getChatGroupIcon() {
                return chatGroupIcon;
            }

            public void setChatGroupIcon(String chatGroupIcon) {
                this.chatGroupIcon = chatGroupIcon;
            }

            public String getChatGroupName() {
                return chatGroupName;
            }

            public void setChatGroupName(String chatGroupName) {
                this.chatGroupName = chatGroupName;
            }

            public int getUserID() {
                return userID;
            }

            public void setUserID(int userID) {
                this.userID = userID;
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

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public int getMaxUserNumber() {
                return maxUserNumber;
            }

            public void setMaxUserNumber(int maxUserNumber) {
                this.maxUserNumber = maxUserNumber;
            }

            public int getMaxRange() {
                return maxRange;
            }

            public void setMaxRange(int maxRange) {
                this.maxRange = maxRange;
            }

            public int getIsHot() {
                return isHot;
            }

            public void setIsHot(int isHot) {
                this.isHot = isHot;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getGeoHash() {
                return geoHash;
            }

            public void setGeoHash(String geoHash) {
                this.geoHash = geoHash;
            }

            public int getHistoryMaxRange() {
                return historyMaxRange;
            }

            public void setHistoryMaxRange(int historyMaxRange) {
                this.historyMaxRange = historyMaxRange;
            }

            public String getCategoryicon() {
                return categoryicon;
            }

            public void setCategoryicon(String categoryicon) {
                this.categoryicon = categoryicon;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getLandmarkid() {
                return landmarkid;
            }

            public void setLandmarkid(String landmarkid) {
                this.landmarkid = landmarkid;
            }

            public String getLandmarkname() {
                return landmarkname;
            }

            public void setLandmarkname(String landmarkname) {
                this.landmarkname = landmarkname;
            }

            public int getChatgroupNewCateID() {
                return chatgroupNewCateID;
            }

            public void setChatgroupNewCateID(int chatgroupNewCateID) {
                this.chatgroupNewCateID = chatgroupNewCateID;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public int getUsercount() {
                return usercount;
            }

            public void setUsercount(int usercount) {
                this.usercount = usercount;
            }

            public int getClassify() {
                return classify;
            }

            public void setClassify(int classify) {
                this.classify = classify;
            }

            public int getNewLevel() {
                return newLevel;
            }

            public void setNewLevel(int newLevel) {
                this.newLevel = newLevel;
            }

            public int getPostbarID() {
                return postbarID;
            }

            public void setPostbarID(int postbarID) {
                this.postbarID = postbarID;
            }
        }



        //用户所在聊吧信息 是一个数组 可以有很多聊吧 但现在规定只能加入一个聊吧 要兼容老版本
        private ArrayList<GroupBean> groups;

        public ArrayList<GroupBean> getGroups() {
            return groups;
        }

        public void setGroups(ArrayList<GroupBean> groups) {
            this.groups = groups;
        }

        public  class GroupBean implements Serializable {
            private int id;
            private String icon;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public boolean isHadsetname() {
            return hadsetname == 1;
        }

        public int getHadsetname() {
            return hadsetname;
        }


        private ArrayList<UserInfoGameInfoBean> gamerInfo;

        public ArrayList<UserInfoGameInfoBean> getGamerInfo() {
            return gamerInfo;
        }

        public void setGamerInfo(ArrayList<UserInfoGameInfoBean> gamerInfo) {
            this.gamerInfo = gamerInfo;
        }

    }
}
