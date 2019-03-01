package net.iaround.ui.datamodel;

import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.GroupHistoryWorker;
import net.iaround.ui.group.bean.GroupHistoryBean;

import java.util.ArrayList;

/**
 * 聊吧访问历史记录
 * Created by gh on 2017/11/2.
 */

public class GroupHistoryModel {


    private static GroupHistoryModel model;

    public static GroupHistoryModel getInstance( )
    {
        if ( model == null )
            model = new GroupHistoryModel( );
        return model;
    }

    /**
     * 往数据库插入一条浏览过的聊吧
     * @param context
     * @param groupId
     * @param groupName
     * @param groupIcon
     * @return
     */
    public long insertOrder(Context context, long groupId, String groupName, String groupIcon,long time)
    {
        GroupHistoryWorker worker = DatabaseFactory.getGroupHistoryWorker( context );

        long uid = Common.getInstance( ).loginUser.getUid( );
        Cursor cursor = worker.selectObj( uid ,groupId);

        if (cursorToBean(cursor) != null){
            if(cursor!=null){
                cursor.close();
            }
            return worker.update(uid,groupId,time);
        }
        if(cursor!=null){
            cursor.close();
        }
        return worker.insetOrder( uid , groupId , groupName , groupIcon ,time);
    }


    /**
     * 删除数据库的一条记录
     * @param context
     * @param groupId
     */
    public void deleteOrder(Context context,long groupId)
    {
        long uid = Common.getInstance( ).loginUser.getUid( );
        GroupHistoryWorker worker = DatabaseFactory.getGroupHistoryWorker( context );
        worker.delete( uid,groupId );
    }

    /**
     * 删除多余的数据
     * @param max
     */
    public void deleteUsed(Context context,int max){
        GroupHistoryWorker worker = DatabaseFactory.getGroupHistoryWorker( context );
        worker.deleteUsed(max);
    }

    /** 获取聊吧信息 */
    public ArrayList< GroupHistoryBean > getGroupHistoryList(Context context )
    {
        GroupHistoryWorker db = DatabaseFactory.getGroupHistoryWorker( context );
        ArrayList<GroupHistoryBean> beans = new ArrayList< GroupHistoryBean >( );
        long uid = Common.getInstance( ).loginUser.getUid( );
        Cursor cursor = db.onSelectPage( uid );
        beans = cursorToBeans( cursor );

        if ( db != null )
            db.onClose( );
        if ( cursor != null )
            cursor.close( );
        return beans;
    }

    /** 将表结构转换成GroupHistoryBean */
    private ArrayList< GroupHistoryBean > cursorToBeans(Cursor cursor )
    {
        ArrayList< GroupHistoryBean > beans = new ArrayList< GroupHistoryBean >( );
        if ( cursor != null )
        {
            for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( ) ; cursor.moveToNext( ) )
            {
                GroupHistoryBean bean = new GroupHistoryBean( );
                bean.uId = cursor
                        .getLong( cursor.getColumnIndex( GroupHistoryWorker.UID ) );
                bean.groupId = cursor
                        .getLong( cursor.getColumnIndex( GroupHistoryWorker.GROUPID ) );
                if (bean.groupId <= 0) {
                    continue;
                }
                bean.groupName = cursor
                        .getString( cursor.getColumnIndex( GroupHistoryWorker.GROUPNAME ) );
                bean.groupIcon = cursor
                        .getString( cursor.getColumnIndex( GroupHistoryWorker.GROUPICON ) );
                bean.time = cursor.getLong(cursor.getColumnIndex(GroupHistoryWorker.TIME));

                beans.add( bean );
            }
        }
        return beans;
    }


    /** 将表结构转换成GroupHistoryBean */
    private GroupHistoryBean cursorToBean(Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()){
                GroupHistoryBean bean = new GroupHistoryBean();
                bean.uId = cursor
                        .getLong(cursor.getColumnIndex(GroupHistoryWorker.UID));
                bean.groupId = cursor
                        .getLong(cursor.getColumnIndex(GroupHistoryWorker.GROUPID));
                bean.groupName = cursor
                        .getString(cursor.getColumnIndex(GroupHistoryWorker.GROUPNAME));
                bean.groupIcon = cursor
                        .getString(cursor.getColumnIndex(GroupHistoryWorker.GROUPICON));
                bean.time = cursor.getLong(cursor.getColumnIndex(GroupHistoryWorker.TIME));

                return bean;
            }
        }
        return null;
    }

}
