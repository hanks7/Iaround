package net.iaround.im.task;

import android.os.Bundle;
import android.util.Log;

import net.iaround.im.aidl.ITaskWrapper;
import net.iaround.utils.BundleFormat;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liangyuanhuan on 07/12/2017.
 */

public abstract class AbstractTaskWrapper extends ITaskWrapper.Stub {
    public static final String TAG = "IAround_TaskWrapper";
    private static AtomicInteger sAtomicInteger = new AtomicInteger(199);

    private Bundle properties = new Bundle();
    private int taskID;

    public AbstractTaskWrapper() {
        // Reflects task properties
        final TaskProperty taskProperty = this.getClass().getAnnotation(TaskProperty.class);
        if (taskProperty != null) {
            setHttpRequest(taskProperty.host(), taskProperty.path());
            setShortChannelSupport(taskProperty.shortChannelSupport());
            setLongChannelSupport(taskProperty.longChannelSupport());
            setCmdID(taskProperty.cmdID());
        }
        taskID = sAtomicInteger.incrementAndGet();
    }

    @Override
    public int getTaskID(){
        return taskID;
    }

    @Override
    public Bundle getProperties() {
        return properties;
    }

    @Override
    public abstract void onTaskEnd(int errType, int errCode);

    public AbstractTaskWrapper setHttpRequest(String host, String path) {
        properties.putString(TaskProperty.OPTIONS_HOST, ("".equals(host) ? null : host));
        properties.putString(TaskProperty.OPTIONS_CGI_PATH, path);

        return this;
    }

    public AbstractTaskWrapper setShortChannelSupport(boolean support) {
        properties.putBoolean(TaskProperty.OPTIONS_CHANNEL_SHORT_SUPPORT, support);
        return this;
    }

    public AbstractTaskWrapper setLongChannelSupport(boolean support) {
        properties.putBoolean(TaskProperty.OPTIONS_CHANNEL_LONG_SUPPORT, support);
        return this;
    }

    public AbstractTaskWrapper setCmdID(int cmdID) {
        properties.putInt(TaskProperty.OPTIONS_CMD_ID, cmdID);
        return this;
    }

    @Override
    public String toString() {
        return "AbstractTaskWrapper: " + BundleFormat.toString(properties);
    }

    public static void log(String content){
        Log.d(TAG,content);
    }
}

