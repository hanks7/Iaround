package net.iaround.model.im;

import net.iaround.entity.GroupOwnerUser;

import java.util.List;

/**
 * Class: 获取聊吧拉取资料接口
 * Author：gh
 * Date: 2017/6/15 16:49
 * Email：jt_gaohang@163.com
 */
public class GroupTopicSimpleBean extends BaseServerBean {


    private int update_flag;//是否展示升级提示框 1 要升级 0 不用升级
    private long group_id;
    private long group_user_id;
    private String group_name;
    private String group_icon;
    private long group_fans_amount;
//    private List<GroupManagerListBean> group_manger_list;
    private String group_manger_list;
    private long online_amount;
    public List<GroupSimpleUser> online_list;
    private GroupSimpleUser user;
    private GroupSimpleUser audio_user;
    private GroupOwnerUser own_user;

    public GroupOwnerUser getOwn_user() {
        return own_user;
    }

    public void setOwn_user(GroupOwnerUser own_user) {
        this.own_user = own_user;
    }

    private int concat_audio;   // 二麦的状态

    private long time;

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    private String welcome;//以吧主身份发送欢迎语


    public int getUpdate_flag() {
        return update_flag;
    }

    public void setUpdate_flag(int update_flag) {
        this.update_flag = update_flag;
    }

    public long getGroup_id() {
        return group_id;
    }

    public long getGroup_user_id() {
        return group_user_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_icon() {
        return group_icon;
    }

    public long getGroup_fans_amount() {
        return group_fans_amount;
    }

//    public List<GroupManagerListBean> getGroup_manger_list() {
//        return group_manger_list;
//    }
    public String getGroup_manger_list()
    {
        return group_manger_list;
    }

    public long getOnline_amount() {
        return online_amount;
    }

    public List<GroupSimpleUser> getOnline_list() {
        return online_list;
    }

    public GroupSimpleUser getUser() {
        return user;
    }

    public GroupSimpleUser getAudio_user() {
        return audio_user;
    }

    public void setAudio_user(GroupSimpleUser audio_user) {
        this.audio_user = audio_user;
    }

    public long getTime() {
        return time;
    }

    public int getConcat_audio() {
        return concat_audio;
    }

    public void setConcat_audio(int concat_audio) {
        this.concat_audio = concat_audio;
    }

    //抽奖次数
    private LuckPanBean.CountBean lotteryCount;
    public LuckPanBean.CountBean getLotteryCount() {
        return lotteryCount;
    }

    public void setLotteryCount(LuckPanBean.CountBean lotteryCount) {
        this.lotteryCount = lotteryCount;
    }

    public boolean getLotterySwitch() {
        return lotterySwitch;
    }

    public void setLotterySwitch(boolean lotterySwitch) {
        this.lotterySwitch = lotterySwitch;
    }

    private boolean lotterySwitch;

    public int[] getLotteryAmount() {
        return lotteryAmount;
    }

    public void setLotteryAmount(int[] lotteryAmount) {
        this.lotteryAmount = lotteryAmount;
    }

    private int[] lotteryAmount; //抽奖奖品的数量,动态配置在后台,顺序要固定

    private long noticeTime;//以秒为单位 0为关

    private String sharepic;
    public String getSharepic() {
        return sharepic;
    }

    public void setSharepic(String sharepic) {
        this.sharepic = sharepic;
    }

    public boolean getShareSwitch() {
        return shareSwitch;
    }

    public void setShareSwitch(boolean shareSwitch) {
        this.shareSwitch = shareSwitch;
    }

    private boolean shareSwitch;

    public long getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(long noticeTime) {
        this.noticeTime = noticeTime;
    }

    public int getShareFreeLottery() {
        return shareFreeLottery;
    }

    public void setShareFreeLottery(int shareFreeLottery) {
        this.shareFreeLottery = shareFreeLottery;
    }

    private int shareFreeLottery; //每次聊吧分享成功后获得的免费抽奖次数

    private int chatbar_hot; //聊吧热度

    public int getChatbar_hot() {
        return chatbar_hot;
    }

    public void setChatbar_hot(int chatbar_hot) {
        this.chatbar_hot = chatbar_hot;
    }

}
