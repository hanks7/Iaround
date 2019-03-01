
package net.iaround.ui.datamodel;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.android.gms.games.Game;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.Device;
import net.iaround.model.im.JobStringArray;
import net.iaround.model.im.LatestDynamicBean;
import net.iaround.model.im.PrivacyInfor;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.StringUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.space.bean.UserRelationLink;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 用户实体类
 *
 * @author linyg
 */
public class User implements Serializable, Comparable<User> {


    /**
     *
     */
    private static final long serialVersionUID = -5047639957606829703L;
    public static final int VERSION = 7; // 数据版本（于此版本不同，缓存数据无效）
    public static final int RELATION_MYSELF = 0;
    public static final int RELATION_FRIEND = 1;
    public static final int RELATION_STRANGER = 2;
    public static final int RELATION_FOLLOWING = 3;
    public static final int RELATION_FANS = 4;
    public static final int RELATION_RECOMMENDATION = 5;

    private long uid; // 用户id
    private String nickname; // 用户昵称
    private String icon; // 头像
    private String sign; // 个性签名
    private float progress; // 升级进度
    private int level = -1; // 等级
    private GeoData loc; // 位置信息
    private int ranking; // 全球排名
    private int upRank; // 排名上升位数
    private boolean isOnline; // 是否在线

    private int sVip; // 包月会员等级 0为非vip  大于0-svip
    private int viplevel; // vip会员等级 0为非vip
    private int sex = -1; // 性别(1 -- 男 2 -- 女 0 -- 其他)
    private int age; // 年龄
    private String birth; // 生日
    private int bloodType; // 血型
    private int horoscope = -1; // 星座（0 - 12）
    private int loveStatus = -1; // 恋爱状况（0 - 3）


    private int currexp; // 当前经验值
    private int distance; // 与我的距离
    private long lastLoginTime; // 上次登录时间
    private int relationship; // 0:自己 ，1：好友 ，2：陌生人，3：我的关注，4：我的粉丝
    private int publicLocation; // 是否公开位置：1、仅好友可见 2、所有人可见，3、所有人不可见），旧版本y对应2，n对应3
    private boolean publicTrack; // 是否公开轨迹
    private Device device; // 设备
    private boolean showDeviceAndSource = true; // 是否显示设备和软件源
    private float rankPercent; // 排名百分比
    private int fansNum; // 粉丝数量
    private int newFans; // 新粉丝数量
    private int charisma; // 魅力值
    private int charismaRank; // 魅力值排名
    private float charismaRankPercent; // 魅力值排名百分比
    private String personalInfor; // 个人介绍
    private int goldnum; // 金币数量
    private int receiveGiftnum; // 收到的礼物总数
    private int mineGiftnum; // 私藏的礼物总数
    private ArrayList<Gift> receiveGifts = new ArrayList<Gift>(); // 收到的礼物
    private ArrayList<Gift> mineGifts = new ArrayList<Gift>(); // 私藏礼物
    private long onlineTime; // 最近在线时间
    private long lastSayTime; // 最后发言时间（0：表示未发言）
    private int lat; // 纬度
    private int lng; // 经度
    private int focusRemainTime; // 我是焦点的剩余分钟数
    private int groupRole; // 0：创建者，1：管理员，2：普通成员
    private int height; // 升高
    private String homePage; // 主页
    private int job = -1; // 职业信息
    private String hobbies; // 兴趣爱好
    private String educations; // 教育信息

    private String weiboString; // 微博信息（字符串）
    private ArrayList<WeiboState> weibo; // 微博状态 WeiboState[ ]
    private String realName; // 真实姓名
    private String email; // 地址
    private String phone; // 手机号码
    private boolean bIsBindPhone; // 是否绑定手机
    private String address; // 通信地址
    private int monthlySalaryIndex = -1; // 月薪索引(默认-1)
    private boolean emailVarified; // 邮箱是否已经验证
    private ArrayList<Photo> photos; // 资料页面显示用的照片列表
    private int visitNum;// 访问量
    private String realAddress; // 真实地址
    private String noteName; // 备注名称
    private boolean isForbid; // 是否被禁言
    private int wannaMeet; // 想认识
    private int weight; // 体重
    private int bodyType; // 体型

    private int infoTotal;// 个人资料总项数
    private int infoComplete;// 个人资料完成项

    // private int completeRate; // 资料完成率
    // private int basicRate; // 基本资料完成率
    // private int secretRate; // 私密资料完成率
    // private int weiboRate; // 私密资料完成率

    private String school; // 毕业学校
    private String department;// 院系
    private int schoolid = -1;// 学校id
    private int departmentid = -1;// 院系id
    private String hometown; // 家乡
    private ArrayList<Dialect> dialects = new ArrayList<Dialect>(); // 掌握的语言
    private String company; // 公司
    private String bindphone; // 绑定的电话号码
    private String turnoffs;// 开关

    private int[] wannaMeetDetail; // 想要找的人详情
    private PrivacyInfor privacyInfoDetail; // 私密资料详情
    private int dataVersion;
    private long focusNearbyDisplayTime;
    private boolean isBanned; // 是否被封停
    private int withWho;// 相见的人 0某位 1女生 2男生
    private int gamelevel;// 游戏达人
    private String mTag;// 标志
    private int photonum;// 图片数量

    @Deprecated //5.7废除
    private int todayphotos;// 今日剩余上传照片数
    private int photouploadleft;// 照片剩余可上传(针对用户的照片上限)
    private int todayphotostotal;// 每天最大上传
    private int photouploadtotal;// 图片上传总上限

    protected String favoritesids; //交友喜好ID,多个用逗号隔开
    protected String favorites;  //交友喜好名称,多个用逗号隔开

    private int incomeStatus = 0; // 收入状况（0-6）//服务端的初始值为0
    private int buycarStatus = 0; //购车情况（0-1）服务端的初始值为0
    private int houseStatus = 0; //住房情况（0-3）服务端的初始值为0
    private int loveExpStatus = 0; //恋爱经历（0-6）服务端的初始值为0

    /**
     * 是否允许更换绑定手机
     */
    public int isCanChangeTelphone = 1;

    private boolean flag;

    private ArrayList<Object> objects;

    private String[] meetPhotos;

    private ArrayList<Game> games = new ArrayList<Game>();// 正在玩的游戏
    private ArrayList<Group> groups = new ArrayList<Group>();// 加入的圈子
    private Group playGroup; //正在玩的聊吧

    private LatestDynamicBean latestDynamic;//最新动态
    private UserRelationLink relationLink;//关系链

    //	private ArrayList< SimpleThemeSquareInfo > postbars = new ArrayList< SimpleThemeSquareInfo >( );//加入的贴吧
    private int jointPostbarAmount;//加入贴吧数

    /**
     * 用户数据
     */
    public static final int CONTENT_TYPE_USER = 1;
    /**
     * 空用户数据
     */
    public static final int CONTENT_TYPE_EMPTY = 0;

    /**
     * 显示属性
     */
    public int contentType = CONTENT_TYPE_USER;

    private int upgradeexp = 0;//升下一级需要的经验

    public void setUpgradeexp(int upgradeexp) {
        this.upgradeexp = upgradeexp;
    }

    private long expire; //会员到期时间

    private int audio;//是否在麦上

    /**
     * 新增属性Cat,Top,Type
     */
    private int cat;
    private int top;
    private int Datatype;

    /**
     * 禁言属性
     */
    private int is_forbid;//是否被禁言
    private long expired_time;//禁言到期时间

    private String verifyicon;//审核头像Url

    private int setBlockStaus;//聊天阻断关系0：不能聊，1：能聊

    private int userType;//用户角色 0，普通用户 1，主播

    private int gameUserType;//是否为游戏主播 0-不是 1-是
    private int verifyPersion;//是否实名认证 0-没有 1-有
    private String verifyAlipay;//支付宝账户 如果已经绑定支付宝则不为空
    private int voiceUserType;//是否为语音主播 0-不是 1-是
    //7.6.0
    private String secret;//语音聊天密钥

    public static int getVERSION() {
        return VERSION;
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

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getDatatype() {
        return Datatype;
    }

    public void setDatatype(int datatype) {
        Datatype = datatype;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long exp) {
        expire = exp;
    }

    public int getUpgradeexp() {
        return upgradeexp;
    }

    public String getFavorites() {
        return favorites;
    }

    public String getFavoritesids() {
        return favoritesids;
    }

    public String[] getMeetPhotos() {
        return meetPhotos;
    }

    public void setMeetPhotos(String[] meetPhotos) {
        this.meetPhotos = meetPhotos;
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects) {
        this.objects = objects;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private long createtimel;
    private int handle; // 邂逅用字段，标识是否有处理过该用户的邂逅消息（1：有，0：没有）
    private int type;// 邂逅用字段，标志邂逅消息类型（1：别人发给我的邂逅消息，2：别人同意我发出的邂逅消息）

    public long getCreatetimel() {
        return createtimel;
    }

    public int getPhotoNum() {
        return photonum;
    }

    public void setPhotoNum(int num) {
        photonum = num;
    }

    public void setCreatetimel(long createtimel) {
        this.createtimel = createtimel;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    public long getFocusNearbyDisplayTime() {
        return focusNearbyDisplayTime;
    }

    public String getFocusNearbyDisplayTimeString(Context context) {
        long min = focusNearbyDisplayTime / 1000 / 60;
        long hours = min / 60;
        min = min % 60;
        if (hours > 0) {
            return hours + " " + context.getString(R.string.hour);
        }
        if (min > 0) {
            return min + " " + context.getString(R.string.minute);
        }
        return context.getString(R.string.less_than_one_min);
    }

    /**
     * @param format
     * @return String（格式00:00）
     * @Title: getFocusRemainTime
     * @Description: 获取用户剩余时间
     */
    public String getFocusRemainTime(DecimalFormat format) {
        long minute = focusNearbyDisplayTime / 1000 / 60;
        long hour = minute / 60;
        minute = minute % 60;
        return format.format(hour) + ":" + format.format(minute);
    }

    public void setFocusNearbyDisplayTime(long focusNearbyDisplayTime) {
        this.focusNearbyDisplayTime = focusNearbyDisplayTime;
    }

    /**
     * 如果想获取实际的备注名称，应当传递false下来，否则可能 会拿到nickname，而不是备注。所以对于{@link Me}的版本判
     * 断来说，useNickDefault必须设置为false，可能每次都会被 认为需要执行刷新。简单来说，<b>用来传递的赋值false，用
     * 来显示的赋值true</b>
     *
     * @param useNickDefault
     * @return
     */
    public String getNoteName(boolean useNickDefault) {
        String res = noteName;
        if (res == null || TextUtils.isEmpty(res) || "".equals(res) || "null".equals(res)) {
            return getNickname();
        } else if (useNickDefault && CommonFunction.isEmptyOrNullStr(res) | res.equals("null")) {
            return getNickname();
        }
        return noteName;
    }

    public String getNoteName() {
        return this.noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = StringUtil.qj2bj(noteName);
    }

    public String getRealAddress() {
        return realAddress == null ? "" : realAddress;
    }

    public void setRealAddress(String realAddress) {
        this.realAddress = realAddress;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public ArrayList<Photo> getPhotos() {
        return photos == null ? new ArrayList<Photo>() : photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public boolean getEmailVarified() {
        return emailVarified;
    }

    public void setEmailVarified(boolean emailVarified) {
        this.emailVarified = emailVarified;
    }

    public String getMonthlySalary(Context context) {
        String[] monthlySalaryArray = context.getResources().getStringArray(
                R.array.monthly_salary);
        if (monthlySalaryIndex >= 0 && monthlySalaryIndex < monthlySalaryArray.length) {
            return monthlySalaryArray[monthlySalaryIndex];
        } else {
            return "";
        }
    }

    public int getMonthlySalary() {
        return monthlySalaryIndex;
    }

    public void setMonthlySalary(int monthlySalary) {
        monthlySalaryIndex = monthlySalary;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        CommonFunction.log("demo", "setEmail:" + email);
        try {
            this.email = CommonFunction.full2HalfChange(email);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            this.email = email;
        }
    }

    public String getRealName() {
        return realName == null ? "" : realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public ArrayList<WeiboState> getWeibo() {
        if (weibo == null) {
            weibo = new ArrayList<WeiboState>();// new WeiboState[ 0 ];
        }
        return weibo;
    }

    public WeiboState getWeibo(int id) {
        if (weibo != null) {
            CommonFunction.log("share", "getWeibo addWeibo>>>>>>>>");
            /*
			 * WeiboState state = findWeibo(id); if (state != null) { return
			 * state; } else { return addWeibo( id ); }
			 */
            return findWeibo(id);
        } else {
			/*
			 * weibo = new WeiboState[ 1 ]; weibo[ 0 ] = new WeiboState( );
			 */
            return null;// weibo[ 0 ];
        }
    }

    public WeiboState findWeibo(int id) {
        if (weibo != null) {
            for (WeiboState state : weibo) {
                if (state != null) {
                    if (state.getType() == id) {
                        return state;
                    }
                }
            }
        }

        return null;
    }

    public WeiboState addWeibo(int id) {
        if (weibo == null) {
            weibo = new ArrayList<WeiboState>();// new WeiboState[ 0 ];
        }

        CommonFunction.log("share", "length***" + weibo.size());
        for (WeiboState state : weibo) {
            if (state == null) {
                CommonFunction.log("share", "state***null");
                break;
            } else {
                if (state.getType() == id) {
                    return state;
                }
            }
        }

        WeiboState state = new WeiboState();
        state.setType(id);
		/*
		 * WeiboState[ ] newStates = new WeiboState[ weibo.length + 1 ];
		 * System.arraycopy( weibo , 0 , newStates , 0 , weibo.length );
		 * newStates[ weibo.length ] = state; weibo = newStates;
		 */
        weibo.add(state);
        CommonFunction.log("share", "add weibo***success");
        return state;
    }

    public void setWeiboId(int weiboType, String weiboUid) {
        if (weibo != null) {
            for (WeiboState state : weibo) {
                if (state != null) {
                    if (state.getType() == weiboType) {
                        state.setId(weiboUid);
                    }
                }
            }
        }
    }

    public void delWeibo(int id) {
        if (weibo == null) {
            return;
        }

        int delIndex = -1;
        int size = weibo.size();
        for (int i = 0; i < size; i++) {
            if (weibo.get(i) != null) {
                if (weibo.get(i).getType() == id) {
                    delIndex = i;
                }
            }
        }

        if (delIndex > -1) {
            CommonFunction.log("share", "delete weibo***success");
			/*
			 * WeiboState[ ] newStates = new WeiboState[ weibo.length - 1 ];
			 * System.arraycopy( weibo , 0 , newStates , 0 , delIndex );
			 * System.arraycopy( weibo , Math.min( weibo.length - 1 , delIndex +
			 * 1 ) , newStates , 0 , weibo.length - delIndex - 1 ); weibo =
			 * newStates;
			 */
            weibo.remove(delIndex);
        }
    }

    @SuppressLint("UseSparseArrays")
    public static ArrayList<WeiboState> parseWeiboStr(String weiboes) {
        ArrayList<WeiboState> weiboStates = new ArrayList<WeiboState>();

        if (!CommonFunction.isEmptyOrNullStr(weiboes)) {
            String[] weiboesStr = weiboes.split(",");
            HashMap<Integer, WeiboState> map = new HashMap<Integer, WeiboState>();
            for (String weiboStr : weiboesStr) {
                if (weiboStr == null || weiboStr.startsWith("0"))
                    continue;

                if (weiboStr.startsWith("12")) {// 新浪微博
                    if (weiboStr.length() >= 2) {
                        boolean verified = false;
                        int verifiedType = -1;

                        if (weiboStr.length() >= 3 && weiboStr.substring(2, 3).equals("1")) {// 加v
                            verified = true;
                            verifiedType = 0;
                        }
                        if (weiboStr.length() >= 4 && weiboStr.substring(3, 4).equals("1")) {// 达人
                            verifiedType = 220;
                        }
                        if (weiboStr.length() >= 5 && weiboStr.substring(4, 5).equals("1")) {// 会员

                        }
                        if (weiboStr.length() >= 6 && weiboStr.substring(5, 6).equals("1")) {// 机构
                            verified = true;
                            verifiedType = 2;
                        }

                        WeiboState weibo = null;
                        weibo = new WeiboState();
                        weibo.setType(12);
                        weibo.setAuthed(verified);
                        weibo.setVerifiedType(verifiedType);
                        if (weibo != null) {
                            map.put(weibo.getType(), weibo);
                        }
                    }
                } else if (weiboStr.startsWith("1")) {// 腾讯微博
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(1);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.startsWith("25")) {// qq空间
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(25);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.startsWith("24")) {// facebook
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(24);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.startsWith("23")) {// Twitter
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(23);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.equals("981")) {// 圈主
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(981);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.equals("982")) {// 小辣椒
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(982);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.equals("992")) {// 真心话大冒险
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(992);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                } else if (weiboStr.equals("991")) {//游戏
                    WeiboState weibo = null;
                    weibo = new WeiboState();
                    weibo.setType(991);
                    if (weibo != null) {
                        map.put(weibo.getType(), weibo);
                    }
                }
            }

//			int i = 0;
//			for ( Entry< Integer , WeiboState > entry : map.entrySet( ) )
//			{
//				weiboStates.add( entry.getValue( ) );
//				i++;
//			}

            Iterator iter = map.entrySet().iterator();

            while (iter.hasNext()) {
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) iter.next();
                weiboStates.add((WeiboState) entry.getValue());
            }

        }
        return weiboStates;
    }

    public void setWeibo(String weiboes) {
        weiboString = weiboes;
        //ArrayList< WeiboState > weibo = parseWeiboStr( weiboes );
        setWeibo(weibo);
    }

    /**
     * @return
     * @Title: getWeiboString
     * @Description: 获取微博字符串
     */
    public String getWeiboString() {
        return this.weiboString;
    }

    public void setWeibo(ArrayList<WeiboState> weibo)// WeiboState[ ]
    {
        if (weibo == null) {
            this.weibo = new ArrayList<WeiboState>();// new WeiboState[ 0 ];
        } else {
            this.weibo = weibo;
        }
    }

    public String getHobbies() {
        return hobbies == null ? "" : hobbies;
    }

    /**
     * 解析代码时，需要将所有项目的数值减一，因为服务端从1开始，而客户端从0开始
     */
    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getJob(Context context, boolean showFirst) {
        if (!showFirst && job < 0) {
            return "";
        }

        String[] jobs = JobStringArray.getInstant(context).getAllJobs();
        int[] ids = new int[jobs.length];
        for (int i = 0; i < ids.length; i++) {
            String[] data = jobs[i].split(",");
            ids[i] = Integer.parseInt(data[0]);
            jobs[i] = data[1];
        }

        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == job) {
                return jobs[i];
            }
        }
        return "";
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public String getHomePage() {
        return homePage == null ? "" : homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getHeightStr() {
        String height = "";
        if (this.height >= 100 && this.height <= 230) {
            height = String.valueOf(this.height) + "cm";
        }
        return height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFocusRemainTime() {
        return focusRemainTime;
    }

    public String getFocusRemainTimeString(Context context) {
        int hours = focusRemainTime / 60;
        int min = focusRemainTime % 60;
        if (hours > 0) {
            if (min >= 10) {
                return hours + ":" + min;
            } else {
                return hours + ":" + "0" + min;
            }
        }
        if (min >= 10) {
            return "0:" + min;
        } else if (min > 1) {
            return "0:0" + min;
        }

        return context.getString(R.string.less_than_one_min);
    }

    public void setFocusRemainTime(long focusRemainTimeMS) {
        this.focusRemainTime = (int) (focusRemainTimeMS / 1000 / 60);
    }

    public int getRelationship() {
        return relationship;
    }

    public void setRelationship(int relationship) {
        this.relationship = relationship;
    }

    /**
     * 用户id
     *
     * @return
     */
    public long getUid() {
        return uid;
    }

    /**
     * 用户id
     *
     * @param uid
     */
    public void setUid(long uid) {
        this.uid = uid;
    }

    /**
     * 是否在线
     *
     * @return
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * 是否在线
     *
     * @param isOnline
     */
    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * 用户昵称
     *
     * @return
     */
    public String getNickname() {
        return nickname == null ? "" : nickname;
    }

    /**
     * 用户昵称
     *
     * @param nickname
     */
    public void setNickname(String nickname) {

        this.nickname = StringUtil.qj2bj(nickname);
    }

    /**
     * 是否为vip会员
     *
     * @return
     */
    public int getViplevel() {
        return viplevel;
    }

    /**
     * 是否为vip会员
     *
     * @param viplevel
     */
    public void setViplevel(int viplevel) {
        this.viplevel = viplevel;
    }

    /**
     * 用户头像
     *
     * @return
     */
    public String getIcon() {
        return icon == null ? "" : icon;
    }

    /**
     * 用户头像
     *
     * @return
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSign() {
        return sign == null ? "" : sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress > 1 ? 1 : (progress < 0 ? 0 : progress);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public GeoData getLoc() {
        return loc;
    }

    public void setLoc(GeoData loc) {
        this.loc = loc;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getUpRank() {
        return upRank;
    }

    public void setUpRank(int upRank) {
        this.upRank = upRank;
    }

    public String getSex(Context context) {
        String[] genders = context.getResources().getStringArray(R.array.genders);
        if (sex >= 0 && sex < genders.length) {
            return genders[sex];
        }
        return context.getString(R.string.unknown);
    }

    /**
     * 获得性别:m男,f女,all其它
     *
     * @return
     */
    public String getGender() {
        if (sex == 1) {
            return "m";
        } else if (sex == 2) {
            return "f";
        } else {
            return "all";
        }
    }

    public int getSexIndex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirth() {

        if (TextUtils.isEmpty(birth)) {
            long curServerTime = System.currentTimeMillis()
                    + Common.getInstance().serverToClientTime;
            birth = TimeFormat.convertTimeLong2String(curServerTime, Calendar.DATE);
        } else {
            String[] birthes = birth.split("-");
            int yearBirth = 0;
            int monthBirth = 0;
            int dayOfMonthBirth = 0;
            try {
                yearBirth = Integer.parseInt(birthes[0]);
                monthBirth = Integer.parseInt(birthes[1]);
                dayOfMonthBirth = Integer.parseInt(birthes[2]);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (yearBirth == 0 || monthBirth == 0 || dayOfMonthBirth == 0) {
                long curServerTime = System.currentTimeMillis()
                        + Common.getInstance().serverToClientTime;
                birth = TimeFormat.convertTimeLong2String(curServerTime, Calendar.DATE);
            }
        }
        return birth;
    }

    public String getBirthCanEmptyString() {
        if (TextUtils.isEmpty(birth)) {
            return "";
        } else {
            String[] birthes = birth.split("-");
            int yearBirth = 0;
            int monthBirth = 0;
            int dayOfMonthBirth = 0;
            try {
                yearBirth = Integer.parseInt(birthes[0]);
                monthBirth = Integer.parseInt(birthes[1]);
                dayOfMonthBirth = Integer.parseInt(birthes[2]);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (yearBirth == 0 || monthBirth == 0 || dayOfMonthBirth == 0) {
                long curServerTime = System.currentTimeMillis()
                        + Common.getInstance().serverToClientTime;
                birth = TimeFormat.convertTimeLong2String(curServerTime, Calendar.DATE);
            }
        }
        return birth;
    }

    public int[] getIntBirth() {
        int[] iBirth = new int[3];
        try {
            String[] birthDay = getBirth().split("-");
            iBirth[0] = Integer.parseInt(birthDay[0]);
            iBirth[1] = Integer.parseInt(birthDay[1]);
            iBirth[2] = Integer.parseInt(birthDay[2]);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (iBirth[0] == 0 || iBirth[1] == 0 || iBirth[2] == 0) {
            Calendar cal = Calendar.getInstance();
            cal.set(1990, 01, 01);
            iBirth[0] = cal.get(Calendar.YEAR);
            iBirth[1] = cal.get(Calendar.MONTH) + 1;
            iBirth[2] = cal.get(Calendar.DAY_OF_MONTH);
        }
        return iBirth;
    }

    public void setBirth(String birth) {
        this.birth = birth == null ? "" : birth;
    }

    public String getBloodType(Context context, boolean localize) {
        String[] bloodTypes;
        if (localize) {
            bloodTypes = context.getResources().getStringArray(R.array.bloods);
        } else {
            bloodTypes = new String[]
                    {"", "A", "B", "AB", "O"};
        }
        if (bloodType >= 0 && bloodType < bloodTypes.length) {
            return bloodTypes[bloodType];
        }
        return context.getString(R.string.unknown);
    }

    public void setBloodType(int bloodType) {
        this.bloodType = bloodType;
    }

    public void setBloodType(String bloodType) {
        if (bloodType.toUpperCase().equals("A")) {
            this.bloodType = 1;
        } else if (bloodType.toUpperCase().equals("B")) {
            this.bloodType = 2;
        } else if (bloodType.toUpperCase().equals("AB")) {
            this.bloodType = 3;
        } else if (bloodType.toUpperCase().equals("O")) {
            this.bloodType = 4;
        } else {
            this.bloodType = 0;
        }
    }

    public int getBloodTypeIndex() {
        return bloodType;
    }

    public String getHoroscope(Context context) {
        String[] horoscopes = context.getResources().getStringArray(R.array.starts_no_date);
        if (horoscope >= 0 && horoscope < horoscopes.length) {
            return horoscopes[horoscope];
        }
        return context.getString(R.string.unknown);
    }

    public int getHoroscopeIndex() {
        return horoscope;
    }

    public void setHoroscope(int horoscope) {
        this.horoscope = horoscope;
    }

    public String getLoveStatus(Context context) {
        String[] marriges = context.getResources().getStringArray(R.array.marriges);
        if (loveStatus >= 0 && loveStatus < marriges.length) {
            return marriges[loveStatus];
        }
        return context.getString(R.string.unknown);
    }

    public int getLoveStatusIndex() {
        return loveStatus;
    }

    public void setLoveStatus(int loveStatus) {
        this.loveStatus = loveStatus;
    }

    public int getCurrexp() {
        return currexp;
    }

    public void setCurrexp(int currexp) {
        this.currexp = currexp;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        int distanceInTenMeter = distance / 10; // 抹掉小数点后第三位
        float distanceInKm = ((float) distanceInTenMeter) / 100; // 仅保留小数点后两位
        if (distanceInKm > 1000) {
            distanceInKm = (int) distanceInKm; // 大于一千公里，去掉小数点后的数据
        }
        this.distance = (int) (distanceInKm * 1000);

        // this.distance = distance;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isVip() {

        return getViplevel() > 0 || getSVip() > 0;
    }

    public int getPublicLocation() {
        return publicLocation;
    }

    public void setPublicLocation(int publicLocation) {
        this.publicLocation = publicLocation;
    }

    public boolean isPublicTrack() {
        return publicTrack;
    }

    public void setPublicTrack(boolean publicTrack) {
        this.publicTrack = publicTrack;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public boolean isShowDeviceAndSource() {
        return showDeviceAndSource;
    }

    public void setShowDeviceAndSource(boolean showDeviceAndSource) {
        this.showDeviceAndSource = showDeviceAndSource;
    }

    public float getRankPercent() {
        return rankPercent;
    }

    public void setRankPercent(float rankPercent) {
        this.rankPercent = rankPercent;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getNewFans() {
        return newFans;
    }

    public void setNewFans(int newFans) {
        this.newFans = newFans;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getFansNum() {
        return fansNum < 0 ? 0 : fansNum;
    }

    /*
	 * 返回签名，签名为空显示为空、于5.1版本修改
	 */
    public String getPersonalInfor(Context context) {
        if (CommonFunction.isEmptyOrNullStr(this.personalInfor)) {
            // return context.getString( R.string.make_great_think );
            personalInfor = "";
        }
        return personalInfor;
    }

    /**
     * 读取个人签名，不使用默认值
     *
     * @return
     */
    public String getPersonalInforNoDefualt() {
        return personalInfor == null ? "" : personalInfor;
    }

    public void setPersonalInfor(String personalInfor) {
        this.personalInfor = personalInfor;
    }

    public int getCharismaRank() {
        return charismaRank;
    }

    public void setCharismaRank(int charismaRank) {
        this.charismaRank = charismaRank;
    }

    public float getCharismaRankPercent() {
        return charismaRankPercent;
    }

    public void setCharismaRankPercent(float charismaRankPercent) {
        this.charismaRankPercent = charismaRankPercent;
    }

    public int getGoldnum() {
        return goldnum;
    }

    public void setGoldnum(int goldnum) {
        this.goldnum = goldnum;
    }

    public ArrayList<Gift> getReceiveGifts() {
        return receiveGifts;
    }

    public ArrayList<Gift> getMineGifts() {
        return mineGifts;
    }

    public void setReceiveGifts(ArrayList<Gift> gifts) {
        this.receiveGifts = gifts;
        if (this.receiveGifts == null) {
            this.receiveGifts = new ArrayList<Gift>();
        }
    }

    public void setMineGifts(ArrayList<Gift> gifts) {
        this.mineGifts = gifts;
        if (this.mineGifts == null) {
            this.mineGifts = new ArrayList<Gift>();
        }
    }

    public int getReceiveGiftnum() {
        return receiveGiftnum;
    }

    public void setReceiveGiftnum(int giftnum) {
        this.receiveGiftnum = giftnum;
    }

    public int getMineGiftnum() {
        return mineGiftnum;
    }

    public void setMineGiftnum(int giftnum) {
        this.mineGiftnum = giftnum;
    }

    public long getLastSayTime() {
        return lastSayTime;
    }

    public void setLastSayTime(long lastSayTime) {
        this.lastSayTime = lastSayTime;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
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

    public int getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(int groupRole) {
        this.groupRole = groupRole;
    }

    public boolean isForbid() {
        return isForbid;
    }

    public void setForbid(boolean isForbid) {
        this.isForbid = isForbid;
    }


    public int calAge() {
        int[] births = getIntBirth();
        int yearBirth = births[0];
        int monthBirth = births[1];
        int dayOfMonthBirth = births[2];
        return calAge(yearBirth, monthBirth, dayOfMonthBirth);
    }

    public int calAge(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() + Common.getInstance().serverToClientTime);
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        int yearBirth = year;
        int monthBirth = month;
        int dayOfMonthBirth = day;

        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    public void setEducations(String educations) {
        this.educations = educations;
    }

    public String getEducations() {
        return educations == null ? "" : educations;
    }

    /**
     * 是否绑定通讯录
     */
    public boolean isBindPhone() {
        return bIsBindPhone;
    }

    /**
     * 是否绑定通讯录
     */
    public void setBindPhone(boolean bindPhone) {
        this.bIsBindPhone = bindPhone;
    }

    /**
     * 判断是否有头像
     */
    public boolean haveIcon() {
        return !CommonFunction.isEmptyOrNullStr(icon);
    }

    /**
     * 是否我的粉丝
     */
    public boolean isMyFans() {
        return (relationship == RELATION_FRIEND) || (relationship == RELATION_FANS);
    }

    /**
     * 是否我的关注
     */
    public boolean isMyFollowing() {
        return (relationship == RELATION_FRIEND) || (relationship == RELATION_FOLLOWING);
    }

    public boolean isMyFriend() {
        return (relationship == RELATION_FRIEND);
    }

    public void setWannaMeet(int wannaMeet) {
        this.wannaMeet = wannaMeet;
    }

    public void setWannaMeet(boolean male, boolean female) {
        if (male) {
            if (female) {
                wannaMeet = 3;
            } else {
                wannaMeet = 2;
            }
        } else {
            if (female) {
                wannaMeet = 1;
            } else {
                wannaMeet = 0;
            }
        }
    }

    public int getWannaMeet() {
        return wannaMeet;
    }

    public boolean[] getWannaMeetGroup() {
        switch (wannaMeet) {
            case 1:
                return new boolean[]
                        {false, true};
            case 2:
                return new boolean[]
                        {true, false};
            case 3:
                return new boolean[]
                        {true, true};
            default:
                return new boolean[]
                        {false, false};
        }
    }

    /**
     * 获取原始头像地址
     */
    public String getLargeFace() {
        if (icon == null) {
            return null;
        }

        String suffix = ""; // .jpg or .png
        String largeFace = icon;
        int index = icon.lastIndexOf('.');
        if (index >= 0) {
            largeFace = icon.substring(0, index);
            suffix = icon.substring(index);
        }

        if (largeFace.endsWith("_s")) {
            largeFace = largeFace.substring(0, largeFace.length() - 2);
        }
        return (largeFace + suffix);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getWeightStr() {
        return weight + "kg";
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

    public String getBodyTypeStr(Context context) {
        String[] horoscopes = context.getResources().getStringArray(R.array.body_type_array);
        if (bodyType > 0 && bodyType <= horoscopes.length) {
            return horoscopes[bodyType - 1];
        }
        return context.getString(R.string.unknown);
    }

    // ***********************个人资料完成项相关*************************/
    public String getTotalCompleteString() {
        return infoComplete + " / " + infoTotal;
    }

    public void setInfoTotal(int mInfoTotal) {
        infoTotal = mInfoTotal;
    }

    public void setInfoComplete(int mInfoComplete) {
        infoComplete = mInfoComplete;
    }

    /**
     * 返回资料完成度，百分比为单位
     */
    public int getInfoCompleteRate() {
        float rate = (float) infoComplete * 100 / infoTotal;
        int r = (int) rate;
        return r;
    }

    public int getInfoTotal() {
        return infoTotal;
    }

    public int getInfoComplete() {
        return infoComplete;
    }

    // ***********************个人资料完成项相关*************************/

    // public String getCompleteRateStr( )
    // {
    // return completeRate + "%";
    // }
    //
    // public String getBasicRateStr( )
    // {
    // return basicRate + "%";
    // }
    //
    // public String getSecretRateStr( )
    // {
    // return secretRate + "%";
    // }
    //
    // public String getWeiboRateStr( )
    // {
    // return weiboRate + "%";
    // }
    //
    // public int getCompleteRate( )
    // {
    // return completeRate;
    // }
    //
    // public void setCompleteRate( int completeRate )
    // {
    // this.completeRate = completeRate;
    // }

    public PrivacyInfor getPrivacyInfoDetail() {
        if (privacyInfoDetail == null) {
            privacyInfoDetail = new PrivacyInfor();
        }
        return privacyInfoDetail;
    }

    public boolean isDefaultPrivacyInfo() {
        if (privacyInfoDetail == null) {
            privacyInfoDetail = new PrivacyInfor();
        }
        return privacyInfoDetail.isDefault();
    }

    public String getPrivacyInfoDetailStr() {
        if (privacyInfoDetail == null) {
            privacyInfoDetail = new PrivacyInfor();
        }
        return privacyInfoDetail.toString();
    }

    public void setPrivacyInfoDetail(String privacyInfoDetail) {
        if (this.privacyInfoDetail == null) {
            this.privacyInfoDetail = new PrivacyInfor();
        }
        this.privacyInfoDetail.set(privacyInfoDetail);
    }

    public int[] getWannaMeetDetail() {
        if (wannaMeetDetail == null) {
            wannaMeetDetail = new int[9];
        }
        if (wannaMeetDetail.length < 9) {
            wannaMeetDetail = new int[9];
        }
        return wannaMeetDetail;
    }

    public String getWannaMeetDetailStr() {
        if (wannaMeetDetail == null || wannaMeetDetail.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wannaMeetDetail.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(wannaMeetDetail[i]);
        }
        return sb.toString();
    }

    public void setWannaMeetDetail(int[] wannaMeetDetail) {
        this.wannaMeetDetail = wannaMeetDetail;
        if (this.wannaMeetDetail == null) {
            this.wannaMeetDetail = new int[9];
        }
    }

    public void setWannaMeetDetail(String wannaMeetDetail) {
        String[] wannaMeet = wannaMeetDetail.split(",");
        this.wannaMeetDetail = new int[wannaMeet.length];
        for (int i = 0; i < wannaMeet.length; i++) {
            try {
                this.wannaMeetDetail[i] = Integer.parseInt(wannaMeet[i]);
            } catch (Throwable t) {
            }
        }
    }

    public void setDataVersion(int version) {
        this.dataVersion = version;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        if (o != null && o instanceof User) {
            if (((User) o).getUid() == uid) {
                return true;
            }
        }
        return false;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(int banned) {
        this.isBanned = banned == 1;
    }

    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    /**
     * 相见的人
     *
     * @return 1女生，2男生，0不限
     */
    public int getWithWho() {
        return withWho;
    }

    public void setWithWho(int withWho) {
        this.withWho = withWho;
    }

    public int getGamelevel() {
        return gamelevel;
    }

    public void setGamelevel(int gamelevel) {
        this.gamelevel = gamelevel;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 比较两个User，如果uid一致，则判定为同一个用户
	 */
    @Override
    public int compareTo(User another) {
        if (this.getUid() == another.getUid()) {
            return 0;
        } else {
            return -1;
        }
    }

    public String getHometown() {
        return hometown == null ? "" : hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public ArrayList<Dialect> getDialects() {
        return dialects;
    }

    public void setDialects(ArrayList<Dialect> dialects) {
        this.dialects = dialects;
    }

    public String getCompany() {
        return company == null ? "" : company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBindphone() {
        return bindphone == null ? "" : bindphone;
    }

    public void setBindphone(String bindphone) {
        this.bindphone = bindphone;
    }

    public String getSchool() {
        return school == null ? "" : school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public boolean isShowWeibo() {
        return checkTurnoffs(0);
    }

    public boolean isShowLocation() {
        return checkTurnoffs(1);
    }

    public boolean isShowDevice() {
        return checkTurnoffs(2);
    }

    public void setTurnoffs(String turnoffs) {
        this.turnoffs = turnoffs;
    }

    public String getTurnoffs() {
        return turnoffs == null ? "000" : turnoffs;
    }

    private boolean checkTurnoffs(int index) {
        if (CommonFunction.isEmptyOrNullStr(turnoffs)) {
            turnoffs = "000";
        }

        if (turnoffs.length() > index) {
            if ('1' == turnoffs.charAt(index)) {
                return true;
            }
        }

        return false;
    }

    public void setShowWeibo(boolean isshow) {
        setTurnoffs(0, isshow);
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

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        if (this.groups != null && this.groups.size() > 0) {
            this.groups.clear();
        }
        this.groups = groups;
    }

    public Group getPlayGroup() {
        return playGroup;
    }

    public void setPlayGroup(Group group) {
        this.playGroup = group;
    }
    // public int getBasicRate( )
    // {
    // return basicRate;
    // }
    //
    // public void setBasicRate( int basicRate )
    // {
    // this.basicRate = basicRate;
    // }
    //
    // public int getSecretRate( )
    // {
    // return secretRate;
    // }
    //
    // public void setSecretRate( int secretRate )
    // {
    // this.secretRate = secretRate;
    // }
    //
    // public int getWeiboRate( )
    // {
    // return weiboRate;
    // }
    //
    // public void setWeiboRate( int weiboRate )
    // {
    // this.weiboRate = weiboRate;
    // }

    @Deprecated //5.7废除
    public int getTodayphotos() {
        return todayphotos;
    }

    @Deprecated //5.7废除
    public void setTodayphotos(int todayphotos) {
        this.todayphotos = todayphotos;
    }

    public int getPhotouploadleft() {
        return photouploadleft;
    }

    public void setPhotouploadleft(int photouploadleft) {
        this.photouploadleft = photouploadleft;
    }

    public int getTodayphotostotal() {
        return todayphotostotal;
    }

    public void setTodayphotostotal(int todayphotostotal) {
        this.todayphotostotal = todayphotostotal;
    }

    public int getPhotouploadtotal() {
        return photouploadtotal;
    }

    public void setPhotouploadtotal(int photouploadtotal) {
        this.photouploadtotal = photouploadtotal;
    }

    public LatestDynamicBean getLatestDynamic() {
        if (latestDynamic == null) {
            this.latestDynamic = new LatestDynamicBean();
        }
        return latestDynamic;
    }

    public void setLatestDynamic(LatestDynamicBean latestDynamic) {
        if (this.latestDynamic == null) {
            this.latestDynamic = new LatestDynamicBean();
        }
        this.latestDynamic = latestDynamic;
    }

    public UserRelationLink getRelationLink() {
        if (this.relationLink == null) {
            this.relationLink = new UserRelationLink();
            this.relationLink.middle = relationLink.new MiddleNode();
        }
        return relationLink;
    }

    public void setRelationLink(UserRelationLink relationLink) {
        if (this.relationLink == null) {
            this.relationLink = new UserRelationLink();
            this.relationLink.middle = relationLink.new MiddleNode();
        }
        this.relationLink = relationLink;
    }

    public String getDepatrment() {
        return department == null ? "" : department;
    }

    public void setDepatrment(String depatrment) {
        this.department = depatrment;
    }

//	public ArrayList< SimpleThemeSquareInfo > getPostbars( )
//	{
//		return postbars;
//	}
//
//	public void setPostbars( ArrayList< SimpleThemeSquareInfo > postbars )
//	{
//		this.postbars = postbars;
//	}

    public int getJointPostbarAmount() {
        return jointPostbarAmount;
    }

    public void setJointPostbarAmount(int jointPostbarAmount) {
        this.jointPostbarAmount = jointPostbarAmount;
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

    /**
     * 是否显示更多项（显示用）
     */
    public boolean isShowMore = false;

    /**
     * 更多的数量（显示用）仅isShowMore为true时有效
     */
    public int moreCount;


    public String getIncome(Context context) {
        String[] marriges = context.getResources().getStringArray(R.array.modify_income);
        if (incomeStatus > 0 && incomeStatus <= marriges.length) {
            return marriges[incomeStatus - 1];
        }
        return context.getString(R.string.space_modify_empty);
    }

    public int getIncomeIndex() {
        return incomeStatus;
    }

    public void setIncome(int income) {
        this.incomeStatus = income;
    }

    public String getBuyCar(Context context) {
        String[] marriges = context.getResources().getStringArray(R.array.modify_buy_car);
        if (buycarStatus > 0 && buycarStatus <= marriges.length) {
            return marriges[buycarStatus - 1];
        }
        return context.getString(R.string.space_modify_empty);
    }

    public int getBuyCarIndex() {
        return buycarStatus;
    }

    public void setBuyCar(int buycar) {
        this.buycarStatus = buycar;
    }


    public String getHouse(Context context) {
        String[] marriges = context.getResources().getStringArray(R.array.modify_house);
        if (houseStatus > 0 && houseStatus <= marriges.length) {
            return marriges[houseStatus - 1];
        }
        return context.getString(R.string.space_modify_empty);
    }

    public int getHouseIndex() {
        return houseStatus;
    }

    public void setHouse(int house) {
        this.houseStatus = house;
    }

    public String getLoveExp(Context context) {
        String[] marriges = context.getResources().getStringArray(R.array.modify_love_experiences);
        if (loveExpStatus > 0 && loveExpStatus <= marriges.length) {
            return marriges[loveExpStatus - 1];
        }
        return context.getString(R.string.space_modify_empty);
    }

    public int getLoveExpIndex() {
        return loveExpStatus;
    }

    public void setLoveExp(int love) {
        this.loveExpStatus = love;
    }

    /**
     * 是否为包月会员 svip
     *
     * @return
     */
    public int getSVip() {
        return sVip;
    }

    /**
     * 是否为包月会员 svip
     *
     * @param sVip
     */
    public void setSVip(int sVip) {
        this.sVip = sVip;
    }

    /**
     * 是否为包月会员 svip
     *
     * @return
     */
    public boolean isSVip() {
        return getSVip() > 0;
    }

    public String getVerifyicon() {
        return verifyicon;
    }

    public void setVerifyicon(String verifyicon) {
        this.verifyicon = verifyicon;
    }

    public int getBlockStaus() {
        return setBlockStaus;
    }

    public void setBlockStaus(int blockStaus) {
        this.setBlockStaus = blockStaus;
    }

    public int getUserType() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(uid + "user_type");
    }

    public void setUserType(int userType) {
        this.userType = userType;
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt(uid + "user_type", userType);
    }

    public String getVerifyAlipay() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(uid + "ali_pay");

    }

    public void setVerifyAlipay(String verifyAlipay) {
        this.verifyAlipay = verifyAlipay;
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(uid + "ali_pay", verifyAlipay);
    }

    public int getVerifyPersion() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(uid + "verify_person");
    }

    public void setVerifyPersion(int verifyPersion) {
        this.verifyPersion = verifyPersion;
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt(uid + "verify_person", verifyPersion);

    }

    public int getGameUserType() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(uid + "game_user_type");
    }

    public void setGameUserType(int gameUserType) {
        this.gameUserType = gameUserType;
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt(uid + "game_user_type", gameUserType);
    }

    public int getVoiceUserType() {
        return SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(uid + "voice_user_type");
    }

    public void setVoiceUserType(int voiceUserType) {
        this.voiceUserType = voiceUserType;
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt(uid + "voice_user_type", voiceUserType);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
