package net.iaround.model.im;

import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/6/16 21:29
 * Email：15369302822@163.com
 */
public class ChatbarClickUser extends BaseServerBean {
    private GroupSimpleUser user;

    private int deny_flag;//0 不展示 1 展示

    public TopBean top;
    public List<SkillBean> skill;

    public class TopBean
    {
        public int charmTop;
        public int pluteTop;
    }

    public class SkillBean
    {

        /**
         * ID : 61
         * UserID : 61001276
         * SkillID : 1
         * SkillLevel : 1
         * Mastery : 0
         * NextLevel : 2
         * CreateTime : 1503302486
         * UpdateTIme : 1503302486
         * Name : 菊花残
         * ICON :
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
        private String StaticBg;

        public String getStaticBg() {
            return StaticBg;
        }

        public void setStaticBg(String staticBg) {
            StaticBg = staticBg;
        }

        public String getGif() {
            return Gif;
        }

        public void setGif(String gif) {
            Gif = gif;
        }

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
    }

    public int getDeny_flag() {
        return deny_flag;
    }

    public void setDeny_flag(int deny_flag) {
        this.deny_flag = deny_flag;
    }

    public GroupSimpleUser getUser() {
        return user;
    }

    public void setUser(GroupSimpleUser user) {
        this.user = user;
    }
}
