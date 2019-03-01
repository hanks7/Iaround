package net.iaround.entity;

/**
 * 作者：zx on 2017/7/10 18:22
 */
public class PipelineGift {

    public String sendUserAvatarUrl ;
    public String receiveUserAvatarUrl ;

    public long sendId;                     //送礼人的ID
    public String sendUser;                 //送礼人的名字
    public String receiveUser;              //接收者的名字
    public int giftNum;                     //送礼的数量
    public int currentGiftPosition;         //当前礼物动画的位置
    public int giftType;                    //礼物的类型  0无连送;1累计;2数组
    public String giftImgUrl;               //礼物的地址
    public String sendIcon;                 //送礼头像
    public int giftArrayNmber;              //单次送礼物的数组的数量（数组连送时用）
    public long giftId;                     //礼物的ID
    public long user_gift_id;               //单次送礼的ID
    public long groupRole;                  //送礼聊吧的角色
    public int svip;                        //会员
    public int viplevel;                    //

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
    public boolean isPlay = false;


    public String getSendUserAvatarUrl() {
        return sendUserAvatarUrl;
    }

    public void setSendUserAvatarUrl(String sendUserAvatarUrl) {
        this.sendUserAvatarUrl = sendUserAvatarUrl;
    }

    public String getReceiveUserAvatarUrl() {
        return receiveUserAvatarUrl;
    }

    public void setReceiveUserAvatarUrl(String receiveUserAvatarUrl) {
        this.receiveUserAvatarUrl = receiveUserAvatarUrl;
    }

    public long getSendId() {
        return sendId;
    }

    public void setSendId(long sendId) {
        this.sendId = sendId;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public int getCurrentGiftPosition() {
        return currentGiftPosition;
    }

    public void setCurrentGiftPosition(int currentGiftPosition) {
        this.currentGiftPosition = currentGiftPosition;
    }

    public String getSendIcon() {
        return sendIcon;
    }

    public void setSendIcon(String sendIcon) {
        this.sendIcon = sendIcon;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public String getGiftImgUrl() {
        return giftImgUrl;
    }

    public void setGiftImgUrl(String giftImgUrl) {
        this.giftImgUrl = giftImgUrl;
    }

    public int getGiftArrayNmber() {
        return giftArrayNmber;
    }

    public void setGiftArrayNmber(int giftArrayNmber) {
        this.giftArrayNmber = giftArrayNmber;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public long getUser_gift_id() {
        return user_gift_id;
    }

    public void setUser_gift_id(long user_gift_id) {
        this.user_gift_id = user_gift_id;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    /**
     * 大礼物Bean
     */
    public static class GiftLager{
        public long user_gift_id;               //单次送礼的ID
        public String combo_animation;
        public String combo_resource_url;
        public String time;
        public boolean isPlay;

        public GiftLager(String combo_animation, String combo_resource_url) {
            this.combo_animation = combo_animation;
            this.combo_resource_url = combo_resource_url;
        }

        public GiftLager(String combo_animation, String combo_resource_url, String time) {
            this.combo_animation = combo_animation;
            this.combo_resource_url = combo_resource_url;
            this.time = time;
        }

        public GiftLager(String combo_animation, String combo_resource_url, String time, boolean isPlay) {
            this.combo_animation = combo_animation;
            this.combo_resource_url = combo_resource_url;
            this.time = time;
            this.isPlay = isPlay;
        }

        public GiftLager(long user_gift_id, String combo_animation, String combo_resource_url, String time, boolean isPlay) {
            this.user_gift_id = user_gift_id;
            this.combo_animation = combo_animation;
            this.combo_resource_url = combo_resource_url;
            this.time = time;
            this.isPlay = isPlay;
        }

        public boolean isPlay() {
            return isPlay;
        }

        public void setPlay(boolean play) {
            isPlay = play;
        }
    }
}
