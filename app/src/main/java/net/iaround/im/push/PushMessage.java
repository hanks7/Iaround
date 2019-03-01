package net.iaround.im.push;

/**
 * Created by liangyuanhuan on 07/12/2017.
 */

public class PushMessage {
    public PushMessage(int cmdId, byte[] buffer) {
        this.cmdId = cmdId;
        this.buffer = buffer;
    }

    public int cmdId;

    public byte[] buffer;
}
