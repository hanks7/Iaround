package net.iaround.eventbus;

public class FirstEvent {

    private String mMsg;

    private String updateChatbarInfo;

    public String getUpdateChatbarInfo() {
        return updateChatbarInfo;
    }

    public void setUpdateChatbarInfo(String updateChatbarInfo) {
        this.updateChatbarInfo = updateChatbarInfo;
    }

    public FirstEvent(String msg) {
        // TODO Auto-generated constructor stub  
        mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }
}  