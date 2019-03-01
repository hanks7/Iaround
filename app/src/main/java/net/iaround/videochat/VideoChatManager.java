package net.iaround.videochat;

/*
 * 视频会话管理
 */

import net.iaround.conf.Common;

public class VideoChatManager {
    //会话状态机
    public static final int VIDEO_CHAT_CALL_STATE_IDLE = 0; //空闲状态
    public static final int VIDEO_CHAT_CALL_STATE_SEND_INVITE = 1; //发起会话邀请
    public static final int VIDEO_CHAT_CALL_STATE_CANCEL_INVITE = 2; //取消会话邀请
    public static final int VIDEO_CHAT_CALL_STATE_RECEIVE_INVITE = 3; //收到会话邀请
    public static final int VIDEO_CHAT_CALL_STATE_CONFORM_INVITE = 4; //同意会话邀请
    public static final int VIDEO_CHAT_CALL_STATE_REJECT_INVITE = 5; //拒绝会话邀请
    public static final int VIDEO_CHAT_CALL_STATE_START_VIDEO = 6; //开始推拉流
    public static final int VIDEO_CHAT_CALL_STATE_CLOSED = 10; //会话结束


    //会话类型 1 登陆用户发起呼叫 2 登陆用户接收呼叫
    public static final int VIDEO_CHAT_CALL_TYPE_DEFAULT = 0; //空类型
    public static final int VIDEO_CHAT_CALL_TYPE_SEND_CALL = 1; //当前登陆用户发起会话邀请
    public static final int VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL = 2; //当前登陆用户收到会话邀请

    private int mCallState = VIDEO_CHAT_CALL_STATE_IDLE;  //当前呼叫状态机
    private int mCallType = VIDEO_CHAT_CALL_TYPE_DEFAULT;

    public static class VideoChatUser{
        public long uid;
        public String name;
        public String icon;
        public String city;
        public int vip;
        public int svip;
        public int follow;
        public int anchor_follow;
        public long lng;
        public long lat;
    }

    private static VideoChatManager sVideoChatManager = null;

    private volatile VideoChatUser mOther; //对方用户
    private volatile VideoChatUser mCurrent; //当前用户
    private long mRoomID = -1;

    public static VideoChatManager getInstance(){
        if(sVideoChatManager == null){
            synchronized (VideoChatManager.class){
                if(sVideoChatManager == null){
                    sVideoChatManager = new VideoChatManager();
                }
            }
        }
        return sVideoChatManager;
    }

    /* 恢复所有的数据和状态
    * */
    public void reset(){
        mOther = null;
        mCurrent = null;
        mCallState = VIDEO_CHAT_CALL_STATE_IDLE;
        mCallType = VIDEO_CHAT_CALL_TYPE_DEFAULT;
    }

    public boolean isIdle(){
        if(VIDEO_CHAT_CALL_STATE_IDLE == mCallState){
            return true;
        }
        return false;
    }

    public void setState(int state){
        this.mCallState = state;
    }

    public int getState(){
        return this.mCallState;
    }

    public void setType(int t){
        this.mCallType = t;
    }

    public int getType(){
        return this.mCallType;
    }

    public void setCurrent(VideoChatUser user){
        this.mCurrent = user;
    }

    public VideoChatUser getCurrent(){
        return this.mCurrent;
    }

    public void setOther(VideoChatUser user){
        this.mOther = user;
    }

    public VideoChatUser getOther(){
        return this.mOther;
    }

    public void setRoom(long room){
        this.mRoomID = room;
    }

    public long getRoom(){
        return this.mRoomID;
    }

    //判断当前用户是不是主播
    public boolean loginUserIsAnchor(){
        int ut = Common.getInstance().loginUser.getUserType();
        return ut==0?false:true;
    }

    private VideoChatManager(){

    }

}