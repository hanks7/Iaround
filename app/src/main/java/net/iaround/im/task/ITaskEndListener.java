package net.iaround.im.task;

import com.google.protobuf.nano.MessageNano;

/**
 * Created by liangyuanhuan on 15/12/2017.
 */

public interface ITaskEndListener {
    public void onTaskEnd(NanoMarsTaskWrapper taskWrapper, int errType, int errCode, MessageNano request, MessageNano response);
}
