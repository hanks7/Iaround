package net.iaround.contract;

import android.content.Context;

import net.iaround.model.entity.SecretEntity;
import net.iaround.model.entity.UserInfoEntity;

/**
 * @author：liush on 2016/12/12 15:59
 */
public interface SecretContract {

    interface View{
        void setDatas(SecretEntity secret);
    }

    interface  Presenter{

    }
}
