package net.iaround.model.entity;

import android.content.Context;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.Photos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author：liush on 2016/12/15 14:50
 */
public class EditUserInfoEntity {
    private Context context;

    public EditUserInfoEntity(Context context) {
        this.context = context;
        sexArr = context.getResources().getStringArray(R.array.sex_data);
        loveStatusArr = context.getResources().getStringArray(R.array.love_status_data);
        ownHouseArr = context.getResources().getStringArray(R.array.own_house_data);
        ownCarArr = context.getResources().getStringArray(R.array.own_car_data);
        incomeArr = context.getResources().getStringArray(R.array.income_data);
    }

    public ArrayList<String> headPicsthum;
    public ArrayList<String> headPics;
    public List<Photos.PhotosBean> photos;

    public List<Photos.PhotosBean> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos.PhotosBean> photos) {
        this.photos = photos;
    }

    public String nickname;//昵称
    public String sex;//性别
    public int sexIndexNew;//根据返回的字符串来判断性别
    public String[] sexArr;
    public String birthday;//生日
    public String signature;//个性签名
    public int loveStatus;//恋爱状态
    public String[] loveStatusArr;
    public String job;//职业
    public String hometown;//家乡
    public int height;//身高
    public int weight;//体重
    public int ownHouse;//购房情况
    public String[] ownHouseArr;
    public int ownCar;//购车情况
    public String[] ownCarArr;
    public int salary;//月收入
    public String[] incomeArr;
    public String[] heightArr;//身高
    public String[] weightArr;//体重
    public String university;//毕业院校
    public String company;//就职公司
    public List<String> hobbys;//爱好
    public boolean phoneFlagB;//是否展示我的手机
    public boolean lastLocalFlagB;//是否展示我的位置
    public int phoneFlag;//展示我的手机
    public int lastLocalFlag;//展示我的位置
    public int schoolid;//学校
    public int departmentid;//专业
    public int userAge;

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(int schoolid) {
        this.schoolid = schoolid;
    }

    public int getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(int departmentid) {
        this.departmentid = departmentid;
    }

    public int horoscope;//星座

    public String turnoffs;// 开关

    public int bodyType;
    public String occupation;//职业

//    private int hadsetname;//性别是否修改过

    private String genderCache;//当前性别

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getBodyType() {
        return bodyType;
    }

    /**
     * 体型，从1开始
     */
    public void setBodyType(int bodyType) {
        this.bodyType = bodyType;
    }

    public int getHoroscope() {
        return horoscope;
    }

    public void setHoroscope(int horoscope) {
        this.horoscope = horoscope;
    }

    public ArrayList<String> getHeadPicsthum() {
        return headPicsthum;
    }

    public void setHeadPicsthum(ArrayList<String> headPicsthum) {
        this.headPicsthum = headPicsthum;
    }

    public ArrayList<String> getHeadPics() {
        return headPics;
    }

    public void setHeadPics(ArrayList<String> headPics) {
        this.headPics = headPics;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSexIndex() {
        return sexIndexNew;
    }//sex

    public String getSex() {

        return sexArr[sexIndexNew];
    }

    public void setSex(String sex) {
        if (sex != null && !sex.equals("")) {
            if ("m".equals(sex)) {
                sexIndexNew = 1;
            } else if ("f".equals(sex)) {
                sexIndexNew = 0;
            } else {
                sexIndexNew = 0;
            }
        }

    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getLoveStatusIndex() {
        return loveStatus;
    }

    public String getLoveStatus() {
        if (loveStatus <= 0) {
            return context.getString(R.string.edit_love_status_empty_tips);
        }

        if (loveStatus >= 1 && loveStatus <= 3) {
            return loveStatusArr[loveStatus - 1];
        } else if (loveStatus == 10) {
            return loveStatusArr[3];
        } else if (loveStatus == 11) {
            return loveStatusArr[4];
        } else {
            return loveStatusArr[0];
        }

    }

    public void setLoveStatus(int loveStatus) {
        this.loveStatus = loveStatus;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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

    public int getIncomeIndex() {
        return salary;
    }

    public String getIncome() {
        if (salary <= 0) {
            return context.getString(R.string.edit_income_empty_tips);
        }
        return incomeArr[salary - 1];
    }

    public void setIncome(int income) {
        this.salary = income;
    }

    public int getOwnHouseIndex() {
        return ownHouse;
    }

    public String getOwnHouse() {
        if (ownHouse <= 0) {
            return context.getString(R.string.edit_own_car_title);
        }
        return ownHouseArr[ownHouse - 1];
    }

    public void setOwnHouse(int ownHouse) {
        this.ownHouse = ownHouse;
    }

    public int getOwnCarIndex() {
        return ownCar;
    }

    public String getOwnCar() {
        if (ownCar <= 0) {
            return context.getString(R.string.edit_own_house_title);
        }
        return ownCarArr[ownCar - 1];
    }

    public void setOwnCar(int ownCar) {
        this.ownCar = ownCar;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public List<String> getHobbys() {
        return hobbys;
    }

    public void setHobbys(List<String> hobbys) {
        this.hobbys = hobbys;
    }

    public boolean getPhoneFlagB() {
        return phoneFlagB;
    }

    public void setPhoneFlagB(boolean phoneFlagB) {
        this.phoneFlagB = phoneFlagB;
        if (phoneFlagB) {
            phoneFlag = Constants.SHOW_PHONE_FLAG_OPEN;
        } else {
            phoneFlag = Constants.SHOW_PHONE_FLAG_CLOSE;
        }
    }

    public boolean getLastLocalFlagB() {
        return lastLocalFlagB;
    }

    public void setLastLocalFlagB(boolean lastLocalFlagB) {
        this.lastLocalFlagB = lastLocalFlagB;
        if (lastLocalFlagB) {
            lastLocalFlag = Constants.LOACTION_FLAG_OPEN;
        } else {
            lastLocalFlag = Constants.LOACTION_FLAG_CLOSE;
        }
    }

    public int getPhoneFlag() {
        return phoneFlag;
    }

    public void setPhoneFlag(int phoneFlag) {
        this.phoneFlag = phoneFlag;
        phoneFlagB = phoneFlag == Constants.SHOW_PHONE_FLAG_OPEN;
    }

    public String getTurnoffs() {
        return turnoffs == null ? "000" : turnoffs;
    }

    public int getLastLocalFlag() {
        return lastLocalFlag;
    }

    public void setLastLocalFlag(int lastLocalFlag) {
        this.lastLocalFlag = lastLocalFlag;
        lastLocalFlagB = lastLocalFlag == Constants.SHOW_PHONE_FLAG_OPEN;
    }

    public void setShowLocation(boolean isshow) {
        setTurnoffs(1, isshow);
    }

    public void setShowDevice(boolean isshow) {
        setTurnoffs(2, isshow);
    }

    private void setTurnoffs(int index, boolean turnoff) {
        if (CommonFunction.isEmptyOrNullStr(turnoffs)) {
            turnoffs = "000";
        }

        if (turnoffs.length() > index) {
            StringBuffer str = new StringBuffer(turnoffs);
            str.setCharAt(index, turnoff ? '1' : '0');
            turnoffs = str.toString();
        }
    }

    public String getRealaddress() {
        return Common.getInstance().loginUser.getAddress();
    }

//    public int getHadsetname() {
//        return hadsetname;
//    }

//    public void setHadsetname(int hadsetname) {
//        this.hadsetname = hadsetname;
//    }

    public String getGenderCache() {
        return genderCache;
    }

    public void setGenderCache(String genderCache) {
        this.genderCache = genderCache;
    }

    @Override
    public String toString() {
        return "EditUserInfoEntity{" +
                "context=" + context +
                ", headPicsthum=" + headPicsthum +
                ", headPics=" + headPics +
                ", photos=" + photos +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", sexIndexNew=" + sexIndexNew +
                ", sexArr=" + Arrays.toString(sexArr) +
                ", birthday='" + birthday + '\'' +
                ", signature='" + signature + '\'' +
                ", loveStatus=" + loveStatus +
                ", loveStatusArr=" + Arrays.toString(loveStatusArr) +
                ", job='" + job + '\'' +
                ", hometown='" + hometown + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", ownHouse=" + ownHouse +
                ", ownHouseArr=" + Arrays.toString(ownHouseArr) +
                ", ownCar=" + ownCar +
                ", ownCarArr=" + Arrays.toString(ownCarArr) +
                ", salary=" + salary +
                ", incomeArr=" + Arrays.toString(incomeArr) +
                ", heightArr=" + Arrays.toString(heightArr) +
                ", weightArr=" + Arrays.toString(weightArr) +
                ", university='" + university + '\'' +
                ", company='" + company + '\'' +
                ", hobbys=" + hobbys +
                ", phoneFlagB=" + phoneFlagB +
                ", lastLocalFlagB=" + lastLocalFlagB +
                ", phoneFlag=" + phoneFlag +
                ", lastLocalFlag=" + lastLocalFlag +
                ", horoscope=" + horoscope +
                ", turnoffs='" + turnoffs + '\'' +
                ", bodyType=" + bodyType +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}
