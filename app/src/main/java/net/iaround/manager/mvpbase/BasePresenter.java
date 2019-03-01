package net.iaround.manager.mvpbase;

import net.iaround.BaseApplication;
import net.iaround.connector.ConnectorManage;

import java.util.ArrayList;

/**
 * 作者：zx on 2017/8/9 11:38
 */
public class BasePresenter {

    private ConnectorManage manage;
    private ArrayList<Long> netFlags = new ArrayList<>();

    public void onAttach(){
        manage = ConnectorManage.getInstance(BaseApplication.appContext);
    }

    public void onDettach(){
        if (null != netFlags && netFlags.size() > 0){
            for (Long flag : netFlags){
                if (null != manage){
                    manage.closeGeneralHttp(flag);
                }
            }
            manage = null;
        }
    }

    protected void addFlag(long flag){
        if (null != netFlags){
            netFlags.add(flag);
        }
    }


}
