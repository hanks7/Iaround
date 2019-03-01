package net.iaround.contract;

import android.content.Context;

/**
 * @author：liush on 2016/12/21 15:52
 */
public interface AuthenPhoneContract {

    interface View{
        void change2Verify();
        void change2Phone();
        Context getContext();
        void bindSuccess();
    }

    interface Presenter{}
}
