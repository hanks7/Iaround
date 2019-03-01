package net.iaround.model.ranking;

import net.iaround.model.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 2017/8/23.
 */

public class SkillRankingEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 7635127122654526974L;

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * ID : 1
         * Name : 菊花残
         * ICON :
         * Status : 0
         * CreateTime : 0
         * UpdateTIme : 0
         * activeList : [{"UserID":61001617,"NickName":"八两","ICON":"http://p1.dev.iaround.com/201707/26/FACE/401ea864b7e9d01875cd576f5c4a91f2_s.jpg","VIP":0,"Notes":"","VipLevel":0,"Age":17,"Gender":"m"}]
         * updateList : []
         */

        private int ID;
        private String Name;
        private String ICON;
        private int Status;
        private int CreateTime;
        private int UpdateTIme;
        private List<ActiveBean> activeList;
        private List<ActiveBean> updateList;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
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

        public int getStatus() {
            return Status;
        }

        public void setStatus(int Status) {
            this.Status = Status;
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

        public List<ActiveBean> getActiveList() {
            return activeList;
        }

        public void setActiveList(List<ActiveBean> activeList) {
            this.activeList = activeList;
        }

        public List<ActiveBean> getUpdateList() {
            return updateList;
        }

        public void setUpdateList(List<ActiveBean> updateList) {
            this.updateList = updateList;
        }

        public static class ActiveBean {
            /**
             * UserID : 61001617
             * NickName : 八两
             * ICON : http://p1.dev.iaround.com/201707/26/FACE/401ea864b7e9d01875cd576f5c4a91f2_s.jpg
             * VIP : 0
             * Notes :
             * VipLevel : 0
             * Age : 17
             * Gender : m
             */

            private int UserID;
            private String NickName;
            private String ICON;
            private int VIP;
            private String Notes;
            private int VipLevel;
            private int Age;
            private String Gender;

            public int getUserID() {
                return UserID;
            }

            public void setUserID(int UserID) {
                this.UserID = UserID;
            }

            public String getNickName() {
                return NickName;
            }

            public void setNickName(String NickName) {
                this.NickName = NickName;
            }

            public String getICON() {
                return ICON;
            }

            public void setICON(String ICON) {
                this.ICON = ICON;
            }

            public int getVIP() {
                return VIP;
            }

            public void setVIP(int VIP) {
                this.VIP = VIP;
            }

            public String getNotes() {
                return Notes;
            }

            public void setNotes(String Notes) {
                this.Notes = Notes;
            }

            public int getVipLevel() {
                return VipLevel;
            }

            public void setVipLevel(int VipLevel) {
                this.VipLevel = VipLevel;
            }

            public int getAge() {
                return Age;
            }

            public void setAge(int Age) {
                this.Age = Age;
            }

            public String getGender() {
                return Gender;
            }

            public void setGender(String Gender) {
                this.Gender = Gender;
            }

            private List<RankBean> rank;

            public List<RankBean> getRank() {
                return rank;
            }

            public void setRank(List<RankBean> Rank) {
                this.rank = Rank;
            }

            public  class RankBean implements Serializable{



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

                public int getActiveWeekSkill1() {
                    return activeWeekSkill1;
                }

                public void setActiveWeekSkill1(int activeWeekSkill1) {
                    this.activeWeekSkill1 = activeWeekSkill1;
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

                private int value;

                public int getActiveMonthSkill1() {
                    return activeMonthSkill1;
                }

                public void setActiveMonthSkill1(int activeMonthSkill1) {
                    this.activeMonthSkill1 = activeMonthSkill1;
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

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }
        }
    }
}
