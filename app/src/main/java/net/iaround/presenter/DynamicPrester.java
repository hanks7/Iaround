package net.iaround.presenter;

import android.content.Context;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.contract.fragment.DynamicContract;
//import net.iaround.model.database.DynamicTable;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.obj.Dynamic;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.DynamicAdapter;


//import org.litepal.crud.DataSupport;

//import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 动态业务逻辑
 * Author：gh
 * Date: 2016/12/9 21:39
 * Email：jt_gaohang@163.com
 */
public class DynamicPrester implements DynamicContract.Presenter {


    @Override
    public long doDynamic(Context context, long userId, int pageNo, int pageSize, HttpCallBack httpCallBack) {
        long getUserDynamicFlag = DynamicHttpProtocol.getUserDynamicList(context, userId, pageNo, pageSize, httpCallBack);
        return getUserDynamicFlag;
    }

//    @Override
//    public void doDynamic(Context context,String dynid, String opt, HttpStringCallback callback) {
//        DynamicHttpProtocol.doDynamic(context,dynid,opt,callback);
//    }

//    @Override
//    public void doAdvert(Context context, HttpStringCallback callBack) {
//        DynamicHttpProtocol.doAdvert(context,callBack);
//    }

    //点赞成功
    @Override
    public void dynamicGreet(ArrayList<DynamicCenterListItemBean> dynamicCenterList,long dynamicId,PullToRefreshListView ptrlvContent,DynamicAdapter mAdapter) {
        for(int i=0;i<dynamicCenterList.size();i++){
            DynamicCenterListItemBean dynamicCenterListItemBean = dynamicCenterList.get(i);
            Dynamic dynamic = (Dynamic)dynamicCenterListItemBean.itemBean;
            if (dynamic.dynid == dynamicId) {
                dynamic.loved = 1;
                dynamic.loveCount++;
                updateDynamicTable(dynamic);
                dynamic.isCurrentHanleView = true;
                break;
            }
            dynamicCenterList.add(dynamicCenterListItemBean);
        }
        mAdapter.updateSingleRow(dynamicCenterList,ptrlvContent.getRefreshableView(),dynamicId);
    }

    //取消点赞成功
    @Override
    public void dynamicCancelGreet(ArrayList<DynamicCenterListItemBean> dynamicCenterList,long dynamicId,PullToRefreshListView ptrlvContent,DynamicAdapter mAdapter){
        for(int i=0;i<dynamicCenterList.size();i++){
            DynamicCenterListItemBean dynamicCenterListItemBean = dynamicCenterList.get(i);
            Dynamic dynamic = (Dynamic)dynamicCenterListItemBean.itemBean;
            if (dynamic.dynid == dynamicId) {
                dynamic.loved = 0;
                dynamic.loveCount--;
                updateDynamicTable(dynamic);
                dynamic.isCurrentHanleView = true;
                break;
            }
            dynamicCenterList.add(dynamicCenterListItemBean);
        }
        mAdapter.updateSingleRow(dynamicCenterList,ptrlvContent.getRefreshableView(),dynamicId);
    }

    @Override
    public void deleteDynamic(ArrayList<DynamicCenterListItemBean> dynamicCenterList,long dynamicId,DynamicAdapter mAdapter){
        for(int i=0;i<dynamicCenterList.size();i++){
            DynamicCenterListItemBean dynamicCenterListItemBean = dynamicCenterList.get(i);
            Dynamic dynamic = (Dynamic) dynamicCenterListItemBean.itemBean;
            if (dynamic.dynid == dynamicId){
                dynamicCenterList.remove(dynamicCenterListItemBean);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updataDynamic(ArrayList<DynamicCenterListItemBean> dynamicCenterList, Dynamic dynamicObj,PullToRefreshListView ptrlvContent,DynamicAdapter mAdapter){
        for(int i=0;i<dynamicCenterList.size();i++){
            DynamicCenterListItemBean dynamicCenterListItemBean = dynamicCenterList.get(i);
            Dynamic dynamic = (Dynamic) dynamicCenterListItemBean.itemBean;
            if (dynamic.dynid == dynamicObj.dynid){
                dynamic.loveCount = dynamicObj.loveCount;
                dynamic.commentCount = dynamicObj.commentCount;
                dynamic.loved = dynamicObj.loved;
                dynamic.isCurrentHanleView = dynamicObj.isCurrentHanleView;
                break;
            }
            dynamicCenterList.add(dynamicCenterListItemBean);
        }
        mAdapter.updateSingleRow(dynamicCenterList,ptrlvContent.getRefreshableView(),dynamicObj.dynid);
    }




    @Override
    public ArrayList addArrayList(ArrayList<Dynamic> list1, ArrayList<Dynamic> list2) {
        ArrayList list3 = new ArrayList();
        if (list1 == null || list1.size() == 0) {
            list3 = list2;
        } else if (list2 == null || list2.size() == 0) {
            list3 = list1;
        } else {
            for (int i = 0; i < list1.size(); i++) {// 遍历list1
                boolean isExist = false;
                for (int j = 0; j < list2.size(); j++) {
                    if (list1.get(i).dynid == list2.get(j).dynid) {
                        deleteDynamicTable(list1.get(i).dynid);
                        isExist = true;// 找到相同项，跳出本层循环
                        break;
                    }
                }
                if (!isExist) {
                    list3.add(list1.get(i));
                }
            }

            for (int k = 0; k < list2.size(); k++) {
                list3.add(list2.get(k));
            }
        }
        return list3;
    }

    // 查询新发布的所有动态
//    @Override
//    public List<DynamicTable> getDynamicTable(){
//        List<DynamicTable> allDynamic = DataSupport.findAll(DynamicTable.class);//查询所有数据
//        return  allDynamic;
//    }

    @Override
    public void deleteDynamicTable(long dynamicId){
//        DataSupport.deleteAll(DynamicTable.class, "dynid = ? ", ""+dynamicId);
    }
    @Override
    public void updateDynamicTable(Dynamic dynamic){
//        DynamicTable table = new DynamicTable();
//        table.setDynid(dynamic.dynid);
//        table.setDynamic(GsonUtil.getInstance().getStringFromJsonObject(dynamic));
//        table.updateAll("dynid = ? ", ""+dynamic.dynid);
    }
}
