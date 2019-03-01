package net.iaround.im.push;

/**
 * Created by liangyuanhuan on 07/12/2017.
 */

public interface IPushMessageHandler {
    //返回假下一个IPushMessageHandler继续处理，返回假自己处理
    public  boolean handleReceiveMessage(PushMessage pushMessage);
}
