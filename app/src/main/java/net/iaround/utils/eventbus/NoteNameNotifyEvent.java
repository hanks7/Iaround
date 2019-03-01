package net.iaround.utils.eventbus;

/**
 * Created by Ray on 2017/5/27.
 */

public class NoteNameNotifyEvent {
    private String mMsg;
    private long mUserId;
    private int mAge;
    private String mUrl;

    public NoteNameNotifyEvent(String noteName, long uid) {
        // TODO Auto-generated constructor stub
        mMsg = noteName;
        mUserId = uid;

    }

    public String getMsg() {
        return mMsg;
    }

    public long getUserId() {
        return mUserId;
    }


}
