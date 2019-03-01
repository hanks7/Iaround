
package net.iaround.model.im;


import android.graphics.Bitmap;

import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.Item;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.RoundPicture;
import net.iaround.ui.datamodel.Dialect;
import net.iaround.ui.datamodel.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * 我的资料数据体
 *
 * @author 余勋杰
 */
public class Me extends User implements Cloneable {
    /**
     *
     */
    private static final long serialVersionUID = -8800025568570419146L;
    /**
     *
     */

    private static boolean firstReq = true; // 是否第一次请求我的资料
    private static Bitmap iconBitmap; // 头像的bitmap
    private int version; // 数据体版本
    private String bindPhone; // 绑定的手机号码
    private boolean isFirstLogin; // 是否第一次登录
    private int bindState; // 是否绑定帐号

    private int diamondnum;

    // 4.2新增
    private int hasPwd; // 是否有密码（1：有/0：无）
    private int canChangePhone; // 是否允许更换手机号（1：是/0：否）
    private String isauth;// 是否通过邮箱验证

    // 5.0新增
    /**
     * 是否有新粉丝
     */
    private boolean isHaveNewFans = false;
    /**
     * 最新那个粉丝的头像地址
     */
    private String newFansIcon = "";

    //5.1新增
    /**
     * 是否有新的最近来访
     */
    private boolean isHasNewVisitors = false;
    /**
     * 最新那个来访的头像地址
     */
    private String newVisitorsIcon = "";

    //5.2新增
    /**
     * 分享平台状态（1 可用，0：禁用）
     */
    private int facebookShareStatus;//facebook
    private int twitterShareStatus;//twitter
    private int wechatfriendsShareStatus;//微信朋友圈
    private int wechatShareStatus;//微信
    private int sinaweiboShareStatus;//新浪微博
    private int tencentweiboShareStatus;//腾讯微博
    private int qqShareStatus;//QQ好友
    private int qqzoneShareStatus;//QQ空间
    /**
     * 是否已经检查分享平台状态
     */
    private boolean isCheckShareStatus = false;

    //5.3新增
    /**
     * 发现内动态收到新动态提醒
     */
    private boolean hasNewFriendDynamic = false; //是否有新的好友动态
    private String newFriendDynamicIcon = "";//新好友动态头像

    /**
     * 第三方用户身份token
     */
    private String accesstoken;
    /**
     * 第三方用户ID
     */
    private String openid;
    /**
     * 第三方用户token过期时间
     */
    private long expires;

    //5.5
    /**
     * 未读好友推荐数
     */
    private int newRecommendFriendNum = 0;

    //7.5.0
    /**
     * 注册方式
     */
    private String regType;

    private Item item;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setFavorites(String favorites) {
        if (getFavorites() != favorites) {
            version++;
        }
        this.favorites = favorites;
    }


    public void setFavoritesids(String favoritesid) {
        if (getFavoritesids() != favoritesid) {
            version++;
        }
        this.favoritesids = favoritesid;
    }


    public String getIsauth() {
        return isauth == null ? "" : isauth;
    }

    public void setIsauth(String isauth) {
        this.isauth = isauth;
    }

    public void setRelationship(int relationship) {
        if (getRelationship() != relationship) {
            version++;
        }
        super.setRelationship(relationship);
    }

    public void setUid(int uid) {
        if (getUid() != uid) {
            version++;
        }
        super.setUid(uid);
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public void setOnline(boolean isOnline) {
        if (isOnline() != isOnline) {
            version++;
        }
        super.setOnline(isOnline);
    }

    public void setNickname(String nickname) {
        if (!getNickname().equals(nickname)) {
            version++;
        }
        super.setNickname(nickname);
    }

    public void setViplevel(int viplevel) {
        if (getViplevel() != viplevel) {
            version++;
        }
        super.setViplevel(viplevel);
    }

    public void setIcon(String icon) {
        if (getIcon() == null || (icon != null && !icon.equals(getIcon()))) {
            version++;
        }
        super.setIcon(icon);
    }

    public void setSign(String sign) {
        if (!getSign().equals(sign)) {
            version++;
        }
        super.setSign(sign);
    }

    public void setFansNum(int fansNum) {
        if (getFansNum() != fansNum) {
            version++;
        }
        super.setFansNum(fansNum);
    }

    public void setProgress(float progress) {
        float prTmp = progress > 1 ? 1 : (progress < 0 ? 0 : progress);
        if (getProgress() != prTmp) {
            version++;
        }
        super.setProgress(progress);
    }

    public void setLevel(int level) {
        if (getLevel() != level) {
            version++;
        }
        super.setLevel(level);
    }

    public void setLoc(GeoData loc) {
        if (loc.equals(getLoc())) {
            version++;
        }
        super.setLoc(loc);
    }

    public void setRanking(int ranking) {
        if (getRanking() != ranking) {
            version++;
        }
        super.setRanking(ranking);
    }

    public void setUpRank(int upRank) {
        if (getUpRank() != upRank) {
            version++;
        }
        super.setUpRank(upRank);
    }

    public void setSex(int sex) {
        if (getSexIndex() != sex) {
            version++;
        }
        super.setSex(sex);
    }

    public void setAge(int age) {
        if (getAge() != age) {
            version++;
        }
        super.setAge(age);
    }

    public void setBirth(String birth, boolean calAge) {
        if (birth == null || birth.equals(getBirth())) {
            return;
        }

        version++;
        super.setBirth(birth);
        if (calAge) {
            setHoroscope(calHoroscope(birth));
            setAge(calAge(birth));
        }
    }

    private int calHoroscope(String birth) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = df.parse(birth);
            int birthMoth = date.getMonth() + 1;
            int birthDay = date.getDate();
            int MMdd = birthMoth * 100 + birthDay;
            if (MMdd >= 321 && MMdd <= 420) {
                return 1;
            } else if (MMdd >= 421 && MMdd <= 520) {
                return 2;
            } else if (MMdd >= 521 && MMdd <= 621) {
                return 3;
            } else if (MMdd >= 622 && MMdd <= 722) {
                return 4;
            } else if (MMdd >= 723 && MMdd <= 822) {
                return 5;
            } else if (MMdd >= 823 && MMdd <= 922) {
                return 6;
            } else if (MMdd >= 923 && MMdd <= 1022) {
                return 7;
            } else if (MMdd >= 1023 && MMdd <= 1121) {
                return 8;
            } else if (MMdd >= 1122 && MMdd <= 1221) {
                return 9;
            } else if (MMdd >= 1222 || MMdd <= 119) {
                return 10;
            } else if (MMdd >= 120 && MMdd <= 218) {
                return 11;
            } else if (MMdd >= 219 && MMdd <= 320) {
                return 12;
            }
        } catch (ParseException e) {
        }
        return 0;
    }

    private int calAge(String birth) {
        Date dat;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dat = dateFormat.parse(birth);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(dat);
            int birthYear = birthCalendar.get(Calendar.YEAR);
            int birthMonth = birthCalendar.get(Calendar.MONTH);
            int birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH);
            return calAge(birthYear, birthMonth, birthDay);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setBloodType(int bloodType) {
        if (getBloodTypeIndex() != bloodType) {
            version++;
        }
        super.setBloodType(bloodType);
    }

    public void setBloodType(String bloodType) {
        int curBloodType = getBloodTypeIndex();
        super.setBloodType(bloodType);
        int newBloodType = getBloodTypeIndex();
        if (curBloodType != newBloodType) {
            version++;
        }
    }

    public void setHoroscope(int horoscope) {
        if (getHoroscopeIndex() != horoscope) {
            version++;
        }
        super.setHoroscope(horoscope);
    }

    public void setIncome(int income) {
        if (getIncomeIndex() != income) {
            version++;
        }
        super.setIncome(income);
    }

    public void setBuyCar(int car) {
        if (getBuyCarIndex() != car) {
            version++;
        }
        super.setBuyCar(car);
    }

    public void setHouse(int house) {
        if (getHouseIndex() != house) {
            version++;
        }
        super.setHouse(house);
    }

    public void setLoveExp(int love) {
        if (getLoveExpIndex() != love) {
            version++;
        }
        super.setLoveExp(love);
    }

    public void setLoveStatus(int loveStatus) {
        if (getLoveStatusIndex() != loveStatus) {
            version++;
        }
        super.setLoveStatus(loveStatus);
    }

    public void setMonthlySalary(int salary) {
        if (getMonthlySalary() != salary) {
            version++;
        }
        super.setMonthlySalary(salary);
    }

    public void setCurrexp(int currexp) {
//		if ( getCurrexp( ) != currexp )
//		{
//			version++;
//		}
        super.setCurrexp(currexp);
    }

    public void setDistance(int distance) {
        int distanceInTenMeter = distance / 10; // 抹掉小数点后第三位
        float distanceInKm = ((float) distanceInTenMeter) / 100; // 仅保留小数点后两位
        if (distanceInKm > 1000) {
            distanceInKm = (int) distanceInKm; // 大于一千公里，去掉小数点后的数据
        }
        int distanceForShow = (int) (distanceInKm * 1000);

        if (getDistance() != distanceForShow) {
            version++;
        }
        super.setDistance(distanceForShow);
    }

    public int getVersion() {
        return version;
    }

    public void clear() {
        version = 0;
        firstReq = true;
        if (iconBitmap != null && !iconBitmap.isRecycled()) {
            iconBitmap.recycle();
            iconBitmap = null;
        }
        CommonFunction.log("System.out",
                "My data has been clear, verion of my data has been reset to 0");
    }

    public boolean isFirstReq() {
        return firstReq;
    }

    public void clearFirstReqLock() {
        firstReq = false;
        CommonFunction.log("System.out", "firstReq lock released");
    }

    /**
     * 获取一个当前的备份
     *
     * @return 备份
     */
    public Me backUp() {
        try {
            return (Me) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBindPhone(String bindPhone) {
        if (!this.bindPhone.equals(bindPhone)) {
            version++;
        }
        this.bindPhone = bindPhone;
    }

    public String getBindPhone() {
        return bindPhone;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        version++;
        int r = iconBitmap.getWidth() * 6 / 64;
        Me.iconBitmap = RoundPicture.getRoundedCornerBitmap(iconBitmap, r,
                iconBitmap.getWidth(), iconBitmap.getHeight());
    }

    /**
     * 1--未绑定；2--绑定；其他--无效值
     */
    public int getBindState() {
        return bindState;
    }

    public void setBind(boolean bindState) {
        this.bindState = bindState ? 2 : 1;
    }

    public void setHeight(int height) {
        if (height != getHeight()) {
            version++;
        }
        super.setHeight(height);
    }

    public void setJob(int job) {
        if (getJob() != job) {
            version++;
        }
        super.setJob(job);
    }

    public void setHobbies(String hobbies) {
        if (getHobbies() == null || !getHobbies().equals(hobbies)) {
            version++;
        }
        super.setHobbies(hobbies);
    }

    public void setWeight(int weight) {
        if (weight != 0 && getWeight() != weight) {
            version++;
        }
        super.setWeight(weight);
    }

    @Override
    public void setPersonalInfor(String personalInfor) {
        if (!getPersonalInforNoDefualt().equals(personalInfor)) {
            version++;
        }
        super.setPersonalInfor(personalInfor);
    }

    public void setBodyType(int bodyType) {
        if (bodyType != 0 && getBodyType() != bodyType) {
            CommonFunction.log("fan", "++++++++++");
            version++;
        }
        super.setBodyType(bodyType);
    }

    public void setWithWho(int withWho) {
        if (getWithWho() != withWho) {
            version++;
        }
        super.setWithWho(withWho);
    }

    /**
     * @return 1：是/0：否
     * @Title: getHasPwd
     * @Description: 是否有密码
     */
    public int getHasPwd() {
        return hasPwd;
    }

    /**
     * @param hasPwd 1：是/0：否
     * @Title: setHasPwd
     * @Description: 设置是否有密码
     */
    public void setHasPwd(int hasPwd) {
        this.hasPwd = hasPwd;
    }

    /**
     * @return 1：是/0：否
     * @Title: getCanChangePhone
     * @Description: 是否允许更换手机
     */
    public int getCanChangePhone() {
        return canChangePhone;
    }

    /**
     * @param canChangePhone 1：是/0：否
     * @Title: setCanChangePhone
     * @Description: 设置是否允许更换手机
     */
    public void setCanChangePhone(int canChangePhone) {
        this.canChangePhone = canChangePhone;
    }

    /**
     * @return
     * @Title: isHaveNewFans
     * @Description: 是否有新粉丝
     */
    public boolean isHaveNewFans() {
        return isHaveNewFans;
    }

    /**
     * @param isHaveNewFans
     * @Title: setHaveNewFans
     * @Description: 设置是否有新粉丝
     */
    public void setHaveNewFans(boolean isHaveNewFans) {
        this.isHaveNewFans = isHaveNewFans;
    }

    /**
     * @return
     * @Title: getNewFansIcon
     * @Description: 获取最新粉丝的头像
     */
    public String getNewFansIcon() {
        return newFansIcon;
    }

    /**
     * @param newFansIcon
     * @Title: setNewFansIcon
     * @Description: 设置新粉丝头像
     */
    public void setNewFansIcon(String newFansIcon) {
        this.newFansIcon = newFansIcon;
    }

    public void setBirth(String birth) {
        if (!getBirth().equals(birth)) {
            version++;
        }
        super.setBirth(birth);
    }

    public void setHometown(String hometown) {
        if (!getHometown().equals(hometown)) {
            version++;
        }
        super.setHometown(hometown);
    }

    public void setDialects(ArrayList<Dialect> dialects) {
        if (!getDialects().equals(dialects)) {
            version++;
        }
        super.setDialects(dialects);
    }

    public void setCompany(String company) {
        if (!getCompany().equals(company)) {
            version++;
        }
        super.setCompany(company);
    }

    public void setSchool(String school) {
        if (!getSchool().equals(school)) {
            version++;
        }
        super.setSchool(school);
    }

    public void setSchoolid(int schoolID) {
        if (getSchoolid() != schoolID) {
            version++;
        }
        super.setSchoolid(schoolID);
    }

    public void setDepartment(String department) {
        if (!getDepatrment().equals(department)) {
            version++;
        }
        super.setDepatrment(department);
    }

    public void setDepartmentid(int departmentid) {
        if (getDepartmentid() != departmentid) {
            version++;
        }
        super.setDepartmentid(departmentid);
    }

    public void setShowWeibo(boolean isshow) {
        if (isShowWeibo() != isshow) {
            version++;
        }
        super.setShowWeibo(isshow);
    }

    public void setShowLocation(boolean isshow) {
        if (isShowLocation() != isshow) {
            version++;
        }
        super.setShowLocation(isshow);
    }

    public void setShowDevice(boolean isshow) {
        if (isShowDevice() != isshow) {
            version++;
        }
        super.setShowDevice(isshow);
    }

    public String getNewVisitorsIcon() {
        return newVisitorsIcon;
    }

    public void setNewVisitorsIcon(String newVisitorsIcon) {
        this.newVisitorsIcon = newVisitorsIcon;
    }

    public boolean isHasNewVisitors() {
        return isHasNewVisitors;
    }

    public void setHasNewVisitors(boolean isHasNewVisitors) {
        this.isHasNewVisitors = isHasNewVisitors;
    }

    public int getFacebookShareStatus() {
        return facebookShareStatus;
    }

    public void setFacebookShareStatus(int facebookShareStatus) {
        this.facebookShareStatus = facebookShareStatus;
    }

    public int getTwitterShareStatus() {
        return twitterShareStatus;
    }

    public void setTwitterShareStatus(int twitterShareStatus) {
        this.twitterShareStatus = twitterShareStatus;
    }

    public int getWechatfriendsShareStatus() {
        return wechatfriendsShareStatus;
    }

    public void setWechatfriendsShareStatus(int wechatfriendsShareStatus) {
        this.wechatfriendsShareStatus = wechatfriendsShareStatus;
    }

    public int getWechatShareStatus() {
        return wechatShareStatus;
    }

    public void setWechatShareStatus(int wechatShareStatus) {
        this.wechatShareStatus = wechatShareStatus;
    }

    public int getSinaweiboShareStatus() {
        return sinaweiboShareStatus;
    }

    public void setSinaweiboShareStatus(int sinaweiboShareStatus) {
        this.sinaweiboShareStatus = sinaweiboShareStatus;
    }

    public int getTencentweiboShareStatus() {
        return tencentweiboShareStatus;
    }

    public void setTencentweiboShareStatus(int tencentweiboShareStatus) {
        this.tencentweiboShareStatus = tencentweiboShareStatus;
    }

    public int getQqShareStatus() {
        return qqShareStatus;
    }

    public void setQqShareStatus(int qqShareStatus) {
        this.qqShareStatus = qqShareStatus;
    }

    public int getQqzoneShareStatus() {
        return qqzoneShareStatus;
    }

    public void setQqzoneShareStatus(int qqzoneShareStatus) {
        this.qqzoneShareStatus = qqzoneShareStatus;
    }

    public boolean isCheckShareStatus() {
        return isCheckShareStatus;
    }

    public void setCheckShareStatus(boolean isCheckShareStatus) {
        this.isCheckShareStatus = isCheckShareStatus;
    }

//	public void setShareStatus(SharePlatformStatusBean bean)
//	{
//		setCheckShareStatus(true);
//
//		setFacebookShareStatus(bean.facebook);
//		setTwitterShareStatus(bean.twitter);
//		setWechatfriendsShareStatus(bean.wechatfriends);
//		setWechatShareStatus(bean.wechat);
//		setSinaweiboShareStatus(bean.sinaweibo);
//		setTencentweiboShareStatus(bean.tencentweibo);
//		setQqShareStatus(bean.qq);
//		setQqzoneShareStatus(bean.qzone);
//	}

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public void setHasNewFriendDynamic(boolean hasNewFriendDynamic) {
        this.hasNewFriendDynamic = hasNewFriendDynamic;
    }

    public boolean getHasNewFriendDynamic() {
        return hasNewFriendDynamic;
    }

    public void setNewFriendDynamicIcon(String Icon) {
        this.newFriendDynamicIcon = Icon;
    }

    public String getNewFriendDynamicIcon() {
        return newFriendDynamicIcon;
    }

    public void setDiamondNum(int num) {
        this.diamondnum = num;
    }

    public int getDiamondNum() {
        return diamondnum;
    }

    public int getNewRecommendFriendNum() {
        return newRecommendFriendNum;
    }

    public void setNewRecommendFriendNum(int num) {
        this.newRecommendFriendNum = num;
    }


    /**
     * 包月会员 svip
     *
     * @param sVip
     */
    public void setSVip(int sVip) {
        if (getSVip() != sVip) {
            version++;
        }
        super.setSVip(sVip);
    }

    public String getRegType() {
        return regType;
    }

    public void setRegType(String regType) {
        this.regType = regType;
    }
}
