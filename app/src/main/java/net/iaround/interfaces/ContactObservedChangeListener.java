package net.iaround.interfaces;

import net.iaround.ui.contacts.bean.FriendListBean;

/**
 * Created by admin on 2017/4/16.
 */

public interface ContactObservedChangeListener {
    void registContactChangeListener(ContactChangeListener listener);

    void removeContactChangeListener(ContactChangeListener listener);

    void notifyContactChangeListener(FriendListBean friendListBean);
}
