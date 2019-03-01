package net.iaround.presenter;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.contract.BlackEditContract;
import net.iaround.model.entity.BaseEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;

import java.util.ArrayList;

/**
 * @author：liush on 2017/1/7 16:26
 */
public class BlackEditPresenter implements HttpCallBack {

    BlackEditContract.View view;
    private ArrayList<GroupSearchUser> dataList = new ArrayList<GroupSearchUser>();// 使用GroupSearchUser中的contentType字段是否为1来判断是否已选不可取消

    private long getListFlag;//获取黑名单/不看ta/不让ta看 列表
    private long getFriendFlag;//好友列表标志
    private long getFansFlag;//粉丝列表标志
    private long getAttentionFlag;//关注列表标志
    private long updateFlag;//移除好友标志

    private int pageNo = 1;//加载页数
    private int pageSize = 20;//每页内容数

    private int maxPage;
    private long uploadFlag;

    public BlackEditPresenter(BlackEditContract.View view) {
        this.view = view;
    }

    /**
     * 初始化不让ta看我状态，我不看ta状态，黑名单好友列表
     * @param type  不让ta看我状态，我不看ta状态，黑名单
     * 根据type获取不同类型的数据
     */
    public void init(int type, long time) {
        view.showDialog();
        getListFlag =BusinessHttpProtocol.getPrivacyDataList( BaseApplication.appContext , type , this );
    }

    /**
     * 请求数据
     * 添加不让ta看我状态，我不看ta状态，黑名单好友
     * @param type 好友，关注，粉丝
     */
    public void init(int type,boolean isAdd)
    {
        view.showDialog();
        if (isAdd) {
            switch (type)
            {
                case Constants.SECRET_SET_FRIENDS_LIST://加载好友列表
                    getFriendFlag = FriendHttpProtocol.friendsGet(BaseApplication.appContext,this);
                    break;
                case Constants.SECRET_SET_FANS_LIST://加载粉丝列表
                    getFansFlag = FriendHttpProtocol.userFans( BaseApplication.appContext ,
                            Common.getInstance( ).loginUser.getUid( ) , pageNo , pageSize , this );
                    break;
                case Constants.SECRET_SET_FOCUS_LIST://加载关注列表
                    getAttentionFlag = FriendHttpProtocol.userAttentions(BaseApplication.appContext,Common.getInstance().loginUser.getUid()
                    ,pageNo,pageSize,this);
            }
        }
    }

    /**
     * 将成员移除改列表
     * @param type 黑名单/不让ta看/不看ta
     * @param userId 要移除的用户id
     */
    public void deleteConfirm(int type,String userId)
    {
        if ("".equals(userId))
            return;
        view.showDialog();
        if ( type == Constants.SECRET_SET_INVISIABLE_OTHER_ACTION
                || type == Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION )
            updateFlag = BusinessHttpProtocol.updatePrivacyList( BaseApplication.appContext , type , "y" ,
                    userId.toString( ) , this );
        else if ( type == Constants.SECRET_SET_BLACK_LIST || type == Constants.SECRET_SET_INVISIBLE)
            updateFlag = BusinessHttpProtocol.updatePrivacyList( BaseApplication.appContext , type , "n" ,
                    userId , this );
    }


//    public void deleteConfirm(int type, String blackList) {
//
//        view.showDialog();
//        SettingProtocol.updateRelationList(type, Constants.SECRET_SET_HADLE_TYPE_DELETE, blackList, new HttpStringCallback() {
//            @Override
//            public void onGeneralSuccess(String result, int id) {
//                view.hideDialog();
//                BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
//                if (baseEntity.isSuccess()) {
//                    view.deleteComplete();
//                } else {
//                    ErrorCode.showError(BaseApplication.appContext, result);
//                }
//            }
//
//            @Override
//            public void onGeneralError(String error, Exception e, int id) {
//            }
//        });
//    }

    public void addConfirm(int type, String blackList) {
        view.showDialog();
        if (type == Constants.SECRET_SET_INVISIABLE_MYSELF_ACTION ||
                type == Constants.SECRET_SET_INVISIABLE_OTHER_ACTION) {
            uploadFlag = BusinessHttpProtocol.updatePrivacyList(BaseApplication.appContext, type, "n", blackList,this);
        } else if (type == Constants.SECRET_SET_BLACK_LIST || type == Constants.SECRET_SET_INVISIBLE)  {
            uploadFlag = BusinessHttpProtocol.updatePrivacyList(BaseApplication.appContext, type, "y", blackList,this);
        } else {
            CommonFunction.log("xiaohua", "add privacy no change so just finish");
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {

        if (flag == getListFlag) {
            view.hideDialog();
            CommonFunction.log("xiaohua", "black list result = " + result);
            GroupMemberSearchBean bean = GsonUtil.getInstance().getServerBean(result,
                    GroupMemberSearchBean.class);
            if (bean.isSuccess() && bean.users != null) {
                dataList.clear();
                for (int i = 0; i < bean.users.size(); i++) {
                    GroupSearchUser tmp = bean.users.get(i);
                    dataList.add(tmp);
                }
                view.showDatas(dataList, 0);
            } else if (!bean.isSuccess()) {
                ErrorCode.showError(BaseApplication.appContext, result);
            }else if (bean.isSuccess() && bean.users == null) {
                view.showDatas(dataList, 0);
            }
        } else if (flag == getFriendFlag) {
            view.hideDialog();
            CommonFunction.log("xiaohua", "black list result = " + result);
            GroupMemberSearchBean bean = GsonUtil.getInstance().getServerBean(result,
                    GroupMemberSearchBean.class);
            if (bean.isSuccess() && bean.users != null) {
                dataList.clear();
                for (int i = 0; i < bean.users.size(); i++) {
                    GroupSearchUser tmp = bean.users.get(i);
//                    if ( tmp.user.userid > 1000 )
                    {// 系统用户不显示，所以不添加进dataList
//                        if ( PrivacySettingDataBu.getInstance( ).getMap( privacyType )
//                                .containsKey( tmp.user.userid ) )
//                            tmp.contentType = 1;
//                        dataList.add( tmp );
                    }
                    dataList.add(tmp);
                }
                view.showDatas(dataList, 0);
            } else if (!bean.isSuccess()) {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        } else if (flag == getAttentionFlag)
        {
            view.hideDialog();
            GroupMemberSearchBean bean = GsonUtil.getInstance().getServerBean(result,
                    GroupMemberSearchBean.class);
            if (bean.isSuccess() && bean.fans != null)
            {
                maxPage = bean.amount / pageSize;
                if (bean.amount % pageSize != 0)
                    maxPage++;

                if (pageNo == 1)
                {
                    dataList.clear();
                    dataList = bean.fans;
                } else {
                    dataList.addAll(bean.fans);
                }
                view.showDatas(dataList, 0);
            }else if (!bean.isSuccess())
            {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        }else if (flag == getFansFlag)//获取粉丝列表
        {
            view.hideDialog();
            GroupMemberSearchBean bean = GsonUtil.getInstance( ).getServerBean( result ,
                    GroupMemberSearchBean.class );
            if ( bean.isSuccess( ) && bean.fans != null )
            {
                maxPage = bean.amount / pageSize;
                if ( bean.amount % pageSize != 0 )
                    maxPage++;
                if ( pageNo == 1 )
                {
                    dataList.clear( );
                    dataList = bean.fans;
                }
                else
                {
                    dataList.addAll( bean.fans );
                }
                //显示数据
                view.showDatas(dataList, 0);
            }
            else if ( !bean.isSuccess( ) )
            {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        }else if(flag == uploadFlag)//添加黑名单/不看ta/不让ta看 成员后，数据上传，更新后台数据
        {
            view.hideDialog();
            BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
            if (baseEntity.isSuccess()) {

                view.addComplete();

            } else {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        }else if (flag == updateFlag )//上传移除好友
        {
            view.hideDialog();
            BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
            if (baseEntity.isSuccess()) {
                view.deleteComplete();
            } else {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        }
    }
    @Override
    public void onGeneralError(int e, long flag) {

    }
}
