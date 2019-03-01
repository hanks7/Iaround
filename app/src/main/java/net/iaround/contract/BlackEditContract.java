package net.iaround.contract;

import net.iaround.ui.group.bean.GroupSearchUser;

import java.util.ArrayList;

/**
 * @author：liush on 2017/1/7 16:27
 */
public interface BlackEditContract {

    interface View{
        void showDatas(ArrayList<GroupSearchUser> datas, int isMoreData);
        void deleteComplete();
        void addComplete();
        void showDialog();
        void hideDialog();
    }

    interface Presenter{}
}
