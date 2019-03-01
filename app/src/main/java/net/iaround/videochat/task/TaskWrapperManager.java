package net.iaround.videochat.task;

/**
 * Created by liangyuanhuan on 15/12/2017.
 */

public class TaskWrapperManager {
    private static TaskWrapperManager sTaskWraperManager = null;

    private String mToken = "";

    public static TaskWrapperManager getInstance(){
        if(null==sTaskWraperManager){
            synchronized (TaskWrapperManager.class){
                if(null==sTaskWraperManager){
                    sTaskWraperManager = new TaskWrapperManager();
                }
            }
        }
        return sTaskWraperManager;
    }

    public void setAccessToken(String token){
        mToken = token;
    }

    public String getAccessToken(){
        return mToken;
    }
}
