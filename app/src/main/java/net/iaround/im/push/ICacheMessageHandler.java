package net.iaround.im.push;

import java.util.ArrayList;

/**
 * Created by liangyuanhuan on 07/12/2017.
 */

public interface ICacheMessageHandler {
    //从  pushMessages 去掉不需要处理的
    public  ArrayList<PushMessage> handleCacheMessage(ArrayList<PushMessage> pushMessages);
}
