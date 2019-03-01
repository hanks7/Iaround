package net.iaround.model.im;

import android.os.Parcel;
import android.os.Parcelable;

import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.entity.Item;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.User;


/**
 * 聊天记录实体
 * 
 * @author chenlb
 * 
 */
public class ChatRecord implements Parcelable {
	private long id; // 聊天记录id
	private long flag;//消息的时间戳,自己的消息与datetime一样,别人的消息就是别人定义的时间戳,datetime是服务端定义的时间戳
	
	// 我的信息
	private long uid; // 用户名
	private String nickname; // 用户昵称
	private String noteName; // 备注名称
	private String icon; // 头像
	private int vip; // 是否为vip会员 0为非Vip
	private int svip; // 是否为svip会员 0为非Vip
	private int level = -1; // 是否为svip会员 0为非Vip

	// 消息的基本信息
	private long datetime; // 时间
	private String type; // 消息类型 ，0：时间 1：文本，2：图片，3：声音，4：视频，5：位置 6：礼物 【41：关注 110:搭讪提示
							// （ChatRecordViewFactory的类型）】21：订单提示 22：订单接收或拒绝提示
	private String attachment; // 附件地址
	private String field1;//备用字段，存放json字符串，使其可扩展
	private String content; // 文本内容
	private int from; // 从哪里找到你 @ChatFromType
	private String reply;
	private int recruit;//0代表不可招募 1代表可招募
	private Item item;

	/**
	 * 关于消息状态的解释:<br>
	 *
	 * @ChatRecordStatus 对于一条消息，可以分为：发送与接收；私聊和圈聊。<br>
	 *                   发送-私聊：发送中、已达、已读、失败<br>
	 *                   发送-圈聊：发送中、已达、失败<br>
	 *                   接收-私聊：已达、已读<br>
	 *                   接收-圈聊：已达、已读<br>
	 *                   状态标识：1为发送中，2为已达， 3为已读，
	 *                   4为失败（备注，因为接收的消息已读没有作用，所以统一把接收到的消息状态设置为已达）
	 */
	private int status;
	private long locid; // 本地id，对应数据库的id

	private boolean isUpload; // 是否正在上传
	private int fileLen; // 文件总大小
	private int uploadLen; // 上传的大小

	// 语音相关的
	private boolean isPlaying; // 是否正在播放
	private boolean isBuff; // 是否正在缓冲
	private boolean isLoading; // 是否正在加载
	private int duration; // 语音的时长
	private int currentPosition; // 当前播放的位置

	private long flagId; // 私聊过程，好友的ID; 圈聊过程，圈子的ID

	// 私聊时，对方的信息
	private String fNickName; // 好友昵称
	private String fNoteName; // 好友的备注名称
	private String fIconUrl; // 好友的头像url
	private int fVipLevel; // 好友的VIP等级  //包月会员 ？ lyh
	private int fSVip; // 好友的SVIP等级     //终生会员 ？ lyh
	private int fLevel = -1; //好友的用户等级
	private int fSex; // 性别(0 -- 男 1 -- 女 其他 -- 保密)
	private int fAge; // 年龄
	private int fLat; // 纬度（圈聊中使用）
	private int fLng; // 经度（圈聊中使用）
	private int distance; // 距离（私聊中使用）

	private boolean isSelect; // 是否选中
	private int relationship; // 我与聊天者的关系

	private int giftStatus = 0; // 约会道具状态 0未品尝，1已品尝
	private int myGiftStatus = 0;// 自己发送约会道具 1未播放，0已播放

	private int sendType;// 消息是属于接收的消息还是发送的消息@MessageBelongType

	private int mgroupRole = -1;//自己当前圈子角色 -1:游客
	private int groupRole = -1;//好友当前圈子的角色 -1：游客，0：吧主，1：管理员，2：成员
	/**
	 * 好友身份标识
	 */
	private int cat;
	private int dataType;
	private int top;

	/**
	 * 个人身份标识
	 */
	private int mCat;
	private int mDataType;
	private int mTop;

	/**判断是聊吧邀请加入还是邀请聊天的标识
	 * 1 邀请加入
	 * 2 邀请聊天
	 * **/
	private String groupid;
	public int inviteFlag;

	/**
	 * 技能的消息的bean
	 * @return
	 */
	private SkillAttackResult skillAttackResult;


	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getRecruit() {
		return recruit;
	}

	public void setRecruit(int recruit) {
		this.recruit = recruit;
	}

	public SkillAttackResult getSkillAttackResult() {
		return skillAttackResult;
	}

	public void setSkillAttackResult(SkillAttackResult skillAttackResult) {
		this.skillAttackResult = skillAttackResult;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public int getMgroupRole() {
		return mgroupRole;
	}

	public void setMgroupRole(int mgroupRole) {
		this.mgroupRole = mgroupRole;
	}

	public String getNoteName() {
		return noteName;
	}

	public int getmCat() {
		return mCat;
	}

	public void setmCat(int mCat) {
		this.mCat = mCat;
	}

	public int getmDataType() {
		return mDataType;
	}

	public void setmDataType(int mDataType) {
		this.mDataType = mDataType;
	}

	public int getmTop() {
		return mTop;
	}

	public void setmTop(int mTop) {
		this.mTop = mTop;
	}

	public int getCat() {
		return cat;
	}

	public void setCat(int cat) {
		this.cat = cat;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(int groupRole) {
		this.groupRole = groupRole;
	}

	// 初始化发送者的信息
	public ChatRecord initMineInfo(User me) {
		uid = me.getUid();
		nickname = me.getNickname();
		noteName = me.getNoteName(false);
		icon = me.getIcon();
		vip = me.getViplevel();
		svip = me.getSVip();
		level = me.getLevel();
		//新增排名字段
		mCat = me.getCat();
		mDataType = me.getType();
		mTop = me.getTop();

		return this;
	}

	//初始化接受者的信息
	public ChatRecord initFriendInfo(User friend) {
		flagId = friend.getUid();
		fNickName = friend.getNickname();
		fNoteName = friend.getNoteName(false);
		fIconUrl = friend.getIcon();
		fVipLevel = friend.getViplevel();
		fLevel = friend.getLevel();
		fSVip = friend.getSVip();
		fSex = friend.getSexIndex();
		fAge = friend.getAge();
		fLat = friend.getLat();
		fLng = friend.getLng();
		distance = friend.getDistance();

		cat = friend.getCat();
		dataType = friend.getDatatype();
		top = friend.getTop();
		return this;
	}

	public ChatRecord initBaseInfo(long dateTime, int recordType,
                                   String attachment, String content, int from) {
		setDatetime(dateTime);
		setType(String.valueOf(recordType));
		setAttachment(attachment);
		setContent(content);
		setFrome(from);
		return this;
	}

	public ChatRecord() {
		isUpload = false;
		isPlaying = false;
		isBuff = false;
		setLoading(false);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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
		if (useNickDefault && CommonFunction.isEmptyOrNullStr(res)) {
			return getNickname();
		}
		return noteName;
	}

	public void setNoteName(String noteName) {
		this.noteName = noteName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}
	
	public int getSVip() {
		return svip;
	}

	public void setSVip(int svip) {
		this.svip = svip;
	}

	public long getDatetime() {
		return datetime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean isUpload) {
		this.isUpload = isUpload;
	}

	public int getFileLen() {
		return fileLen;
	}

	public void setFileLen(int fileLen) {
		this.fileLen = fileLen;
	}

	public int getUploadLen() {
		return uploadLen;
	}

	public void setUploadLen(int uploadLen) {
		this.uploadLen = uploadLen;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getLocid() {
		return locid;
	}

	public void setLocid(long locid) {
		this.locid = locid;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean isBuff() {
		return isBuff;
	}

	public void setBuff(boolean isBuff) {
		this.isBuff = isBuff;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public long getFuid() {
		return flagId;
	}

	public void setFuid(long fuid) {
		this.flagId = fuid;
	}

	public String getfNickName() {
		return fNickName;
	}

	public void setfNickName(String fNickName) {
		this.fNickName = fNickName;
	}

	/**
	 * 如果想获取实际的备注名称，应当传递false下来，否则可能 会拿到nickname，而不是备注。所以对于{@link Me}的版本判
	 * 断来说，useNickDefault必须设置为false，可能每次都会被 认为需要执行刷新。简单来说，<b>用来传递的赋值false，用
	 * 来显示的赋值true</b>
	 * 
	 * @param useNickDefault
	 * @return
	 */
	public String getfNoteName(boolean useNickDefault) {
		String res = fNoteName;
		if (useNickDefault && CommonFunction.isEmptyOrNullStr(res)) {
			return getfNickName();
		}
		return fNoteName;
	}

	public void setfNoteName(String noteName) {
		this.fNoteName = noteName;
	}

	public String getfIconUrl() {
		return fIconUrl;
	}

	public void setfIconUrl(String fIconUrl) {
		this.fIconUrl = fIconUrl;
	}

	public int getfVipLevel() {
		return fVipLevel;
	}

	public void setfVipLevel(int fVipLevel) {
		this.fVipLevel = fVipLevel;
	}
	
	public int getfSVip() {
		return fSVip;
	}

	public void setfSVip(int fSvip) {
		this.fSVip = fSvip;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public int getfSex() {
		return fSex;
	}

	public void setfSex(int fSex) {
		this.fSex = fSex;
	}

	public int getfAge() {
		return fAge;
	}

	public void setfAge(int fAge) {
		this.fAge = fAge;
	}

	public int getfLat() {
		return fLat;
	}

	public void setfLat(int fLat) {
		this.fLat = fLat;
	}

	public int getfLng() {
		return fLng;
	}

	public void setfLng(int fLng) {
		this.fLng = fLng;
	}

	public int getRelationship() {
		return relationship;
	}

	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}

	public int getGiftStatus() {
		return giftStatus;
	}

	public void setGiftStatus(int giftStatus) {
		this.giftStatus = giftStatus;
	}

	public int getMyGiftStatus() {
		return myGiftStatus;
	}

	public void setMyGiftStatus(int myGiftStatus) {
		this.myGiftStatus = myGiftStatus;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setFrome(int from) {
		this.from = from;
	}

	public int getFrom() {
		return from;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public void setLevel(int level){
		this.level = level;
	}
	public int getLevel(){
		return this.level;
	}

	public void setFLevel(int level){
		this.fLevel = level;
	}
	public int getFLevel(){
		return this.fLevel;
	}

	public static final Parcelable.Creator<ChatRecord> CREATOR = new Creator<ChatRecord>() {

		@Override
		public ChatRecord[] newArray(int size) {
			return new ChatRecord[size];
		}

		@Override
		public ChatRecord createFromParcel(Parcel source) {
			return new ChatRecord(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(uid);
		dest.writeLong(flag);
		dest.writeString(nickname);
		dest.writeString(noteName);
		dest.writeString(icon);
		dest.writeInt(vip);
		dest.writeInt(svip);
		dest.writeInt(level);
		
		dest.writeLong(datetime);
		dest.writeString(type);
		dest.writeString(attachment);
		dest.writeString(content);
		dest.writeInt(from);
		
		dest.writeInt(status);
		dest.writeLong(locid);
		dest.writeInt(isUpload ? 1 : 0);//1为true,0为false
		dest.writeInt(fileLen);
		dest.writeInt(uploadLen);
		
		dest.writeInt(isPlaying ? 1 : 0);//1为true,0为false
		dest.writeInt(isBuff ? 1 : 0);//1为true,0为false
		dest.writeInt(isLoading ? 1 : 0);//1为true,0为false
		dest.writeInt(duration);
		dest.writeInt(currentPosition);
		dest.writeLong(flagId);
		
		dest.writeString(fNickName);
		dest.writeString(fNoteName);
		dest.writeString(fIconUrl);
		dest.writeInt(fVipLevel);
		dest.writeInt(fSVip);
		dest.writeInt(fLevel);
		dest.writeInt(fSex);
		dest.writeInt(fAge);
		dest.writeInt(fLat);
		dest.writeInt(fLng);
		dest.writeInt(distance);
		dest.writeInt(isSelect ? 1 : 0);//1为true,0为false
		
		dest.writeInt(relationship);
		dest.writeInt(giftStatus);
		dest.writeInt(myGiftStatus);
		dest.writeInt(sendType);
	}
	
	public ChatRecord(Parcel in){
		
		uid = in.readLong();
		flag = in.readLong();
		nickname = in.readString();
		noteName = in.readString();
		icon = in.readString();
		vip = in.readInt();
		svip = in.readInt();
		level = in.readInt();
		
		datetime = in.readLong();
		type = in.readString();
		attachment = in.readString();
		content = in.readString();
		from = in.readInt();
		
		status = in.readInt();
		locid = in.readLong();
		isUpload = (in.readInt() == 1);//1为true,0为false
		fileLen = in.readInt();
		uploadLen = in.readInt();
		
		isPlaying = (in.readInt() == 1);//1为true,0为false
		isBuff = (in.readInt() == 1);//1为true,0为false
		isLoading = (in.readInt() == 1);//1为true,0为false
		duration = in.readInt();
		currentPosition = in.readInt();
		flagId = in.readLong();
		
		fNickName = in.readString();
		fNoteName = in.readString();
		fIconUrl = in.readString();
		fVipLevel = in.readInt();
		fSVip = in.readInt();
		fLevel = in.readInt();
		fSex = in.readInt();
		fAge = in.readInt();
		fLat = in.readInt();
		fLng = in.readInt();
		distance = in.readInt();
		isSelect = (in.readInt() == 1);//1为true,0为false
		
		relationship = in.readInt();
		giftStatus = in.readInt();
		myGiftStatus = in.readInt();
		sendType = in.readInt();
	}

	public long getFlag() {
		return flag;
	}

	public void setFlag(long flag) {
		this.flag = flag;
	}

	public String getReply( )
	{
		return reply;
	}

	public void setReply( String reply )
	{
		this.reply = reply;
	}

	public String getField1( )
	{
		return field1;
	}

	public void setField1( String field1 )
	{
		this.field1 = field1;
	}
}
