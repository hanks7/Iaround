package net.iaround.contract.fragment;

import android.content.Context;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.connector.HttpCallBack;
//import net.iaround.model.database.DynamicTable;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.obj.Dynamic;
import net.iaround.presenter.BasePresenter;
import net.iaround.ui.adapter.DynamicAdapter;
import net.iaround.ui.view.BaseView;

import java.util.ArrayList;
import java.util.List;


/**
 * Class: 契约-动态
 * Author：gh
 * Date: 2016/12/9 21:38
 * Email：jt_gaohang@163.com
 */
public interface DynamicContract  {

    interface View extends BaseView<Presenter> {

        void initView();
    }

    interface Presenter extends BasePresenter {

        long doDynamic(Context context,long userId,int pageNo,int pageSize, HttpCallBack httpCallBack);
//        void doDynamic(Context context,String dynid, String opt, HttpStringCallback callback );
//
//        void doAdvert(Context context,HttpStringCallback callBack);

        void dynamicGreet(ArrayList<DynamicCenterListItemBean> dynamicCenterList, long dynamicId, PullToRefreshListView ptrlvContent, DynamicAdapter mAdapter);

        void dynamicCancelGreet(ArrayList<DynamicCenterListItemBean> dynamicCenterList,long dynamicId,PullToRefreshListView ptrlvContent,DynamicAdapter mAdapter);

        void deleteDynamic(ArrayList<DynamicCenterListItemBean> dynamicCenterList,long dynamicId,DynamicAdapter mAdapter);

        void updataDynamic(ArrayList<DynamicCenterListItemBean> dynamicCenterList, Dynamic dynamicObj,PullToRefreshListView ptrlvContent,DynamicAdapter mAdapter);

        ArrayList addArrayList(ArrayList<Dynamic> list1, ArrayList<Dynamic> list2);

//        List<DynamicTable> getDynamicTable();//yuchao
//
        void deleteDynamicTable(long dynamicId);

        void updateDynamicTable(Dynamic dynamic);
    }

}
