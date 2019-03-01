package net.iaround.ui.interfaces;

import net.iaround.model.im.ChatRecord;

/**
 * Created by Ray on 2017/7/18.
 */

public interface SendPersonalSocketListener {
    void update(long messageId, ChatRecord chatRecord);

    void showStatus();
}
