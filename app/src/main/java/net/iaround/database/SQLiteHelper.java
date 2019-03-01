/************************************
 * Copyright 2011 u6 to MeYou
 *
 * @Description:sqlite操作helper
 * @Date 2011-05-17 14:26
 * @Author linyg
 * @Version v2.0
 */

package net.iaround.database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alibaba.fastjson.JSON;

import net.iaround.model.im.GroupChatMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class SQLiteHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "iaround_im.db";
    private static SQLiteHelper db;
    private Context context;
    /**
     * 数据库的版本号有改动，请与当前应用的版本id对应 例如：版本为5.4.0，数据库对应的版本号为540
     */
    private static int VERSION = 727;

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.getWritableDatabase();
        this.context = context;
    }

    public static SQLiteHelper getInstance(Context context) {
        if (db == null) {
            db = new SQLiteHelper(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(PersonalMessageWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_PersonalMessageWorker", e.getMessage());
        }
        try {
            db.execSQL(MessageWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_MessageWorker", e.getMessage());
        }
        try {
            db.execSQL(NearContactWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_NearContactWorker", e.getMessage());
        }
        try {
			db.execSQL( KeyWordWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_KeyWordWorker", e.getMessage());
        }
        try {
            db.execSQL(GroupMessageWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_GroupMessageWorker", e.getMessage());
        }
        try {
//			db.execSQL( DraftsWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_DraftsWorker", e.getMessage());
        }
        try {
			db.execSQL( PayOrderWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_DraftsWorker", e.getMessage());
        }
        try {
			db.execSQL( MeetGameWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_MeetGameWorker", e.getMessage());
        }

        try {
			db.execSQL( ChatThemeWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_ChatThemeWorker", e.getMessage());
        }
        try {
            db.execSQL(GroupContactWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_GroupContactWorker", e.getMessage());
        }
        try {
            db.execSQL(DynamicWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_DynamicWorker", e.getMessage());
        }
        try {
            db.execSQL(GroupNoticeWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_GroupNoticeWorker", e.getMessage());
        }
        try {
            db.execSQL(NewFansWorker.tableSql);
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_NewFriendWorker", e.getMessage());
        }
        try {
//			db.execSQL( ChatBarNoticeWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_ChatBarNoticeWorker", e.getMessage());
        }

        try {
            db.execSQL( GroupHistoryWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_DraftsWorker", e.getMessage());
        }

        try {
            db.execSQL( VideoChatWorker.tableSql );
        } catch (Exception e) {
            CommonFunction.log("SQLiteHelper_DraftsWorker", e.getMessage());
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 版本1->2
        if (oldVersion == 1) {
            try {
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.SERVER_ID + " INTEGER(11) ;");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // 在最近联系人表中添加好友基本信息
        if (oldVersion < 5) {
            try {
                db.execSQL("alter table " + NearContactWorker.TB_NAME + " add " +
                        NearContactWorker.FUSERINFO + " TEXT ;");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                db.execSQL(
                        "alter table " + NearContactWorker.TB_NAME + " add " + NearContactWorker.FUID +
                                " VERCHAR(15) ;");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("alter table " + NearContactWorker.TB_NAME + " add " +
                        NearContactWorker.FNICKNAME + " VERCHAR(50) ;");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(
                        "alter table " + NearContactWorker.TB_NAME + " add " + NearContactWorker.FICON +
                                " VERCHAR(100) ;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 将圈子的消息id改成不唯一
        if (oldVersion < 7) {
            try {
                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " change " +
                        GroupMessageWorker.MESSAGEID + " INTEGER NOT NULL ;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 添加备注
        if (oldVersion < 8) {
            try {
                db.execSQL(
                        "alter table " + NearContactWorker.TB_NAME + " add " + NearContactWorker.FNOTE +
                                " VERCHAR(50) ;");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        // 添加草稿箱
        if (oldVersion < 9) {
            try {
//				db.execSQL( DraftsWorker.tableSql );
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_DraftsWorker", e.getMessage());
            }
        }

        // 添加订单状态
        if (oldVersion < 10) {
            try {
                db.execSQL(PayOrderWorker.tableSql);
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_PayOrderWorker", e.getMessage());
            }
        }

        if (oldVersion < 11) {
            // 添加邂逅游戏
            try {
				db.execSQL( MeetGameWorker.tableSql );
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_MeetGameWorker", e.getMessage());
            }
        }

        if (oldVersion < 12) {
            // 私聊消息添加 发送类型，是接收，还是发送
            try {
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.SENDTYPE + " CHAR(1) DEFAULT 0 ;");
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        if (oldVersion < 13) {
            // 添加场景
            try {
				db.execSQL( ChatThemeWorker.tableSql );
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_ChatThemeWorker", e.getMessage());
            }
        }
        if (oldVersion < 14) {
            // 私聊时，约会道具的状态
            try {
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.GIFTSTATUS + " CHAR(1) DEFAULT 0 ;");
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        if (oldVersion < 17) {// 圈子ni
            try {
                db.execSQL(GroupContactWorker.tableSql);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        if (oldVersion < 16) {// 聊天列表私聊送达已读状态
            try {
                db.execSQL("alter table " + NearContactWorker.TB_NAME + " add " +
                        NearContactWorker.CHAT_STATUS + " VERCHAR(15) ;");

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        if (oldVersion < 18) {
            // 在GroupMessageWorker 和 GroupContactWorker 中加入status字段
            try {
                db.execSQL("alter table " + GroupContactWorker.TB_NAME + " add " +
                        GroupContactWorker.STATUS + " VERCHAR(15) DEFAULT 0;");
            } catch (Exception e) {

                e.printStackTrace();
            }

            try {
                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " add " +
                        GroupMessageWorker.STATUS + " VERCHAR(15) DEFAULT 0;");
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        if (oldVersion < 19) {
            // 添加动态表
            try {
				db.execSQL( DynamicWorker.tableSql );
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        if (oldVersion < 20) {
            // 私聊时，约会道具的状态
            try {
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.DISTANCE + " INTEGER(11) DEFAULT -1;");

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        if (oldVersion < 540) {
            // 在最近联系人表中新增分组字段
            try {
                db.execSQL("alter table " + NearContactWorker.TB_NAME + " add " +
                        NearContactWorker.SUBGROUP + " INTEGER(11) DEFAULT 1;");

            } catch (Exception e) {

                e.printStackTrace();
            }

            // 在消息表中新增消息的类型
            try {
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.MESSAGE_TYPE + " INTEGER(11) DEFAULT 1;");

            } catch (Exception e) {

                e.printStackTrace();
            }

            // 在消息表中新增消息从哪里找到的类型
            try {
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.FROM + " INTEGER(11) DEFAULT 0;");

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        if (oldVersion < 600) {
            try {// 添加圈通知表
                db.execSQL(GroupNoticeWorker.tableSql);
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_GroupNoticeWorker", e.getMessage());
            }
            try {// 添加新增粉丝表
                db.execSQL(NewFansWorker.tableSql);
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_GroupNoticeWorker", e.getMessage());
            }

            try {
                // 私聊消息表新增时间戳字段
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.TIMESTAMP + " INTEGER(11) DEFAULT 0;");
            } catch (Exception e) {

                e.printStackTrace();
            }

            try {
                // 聊天圈子列表新增AtFlag
                db.execSQL("alter table " + GroupContactWorker.TB_NAME + " add " +
                        GroupContactWorker.ATFLAG + " INTEGER DEFAULT 0;");
            } catch (Exception e) {

                e.printStackTrace();
            }

            try {
                // 聊天圈子消息新增INCREASEID,DELETEFLAG,TIMESTAMP
                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " add " +
                        GroupMessageWorker.INCREASEID + " INTEGER DEFAULT 0;");
            } catch (Exception e) {

                e.printStackTrace();
            }

            try {
                // 聊天圈子消息新增INCREASEID,DELETEFLAG,TIMESTAMP
                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " add " +
                        GroupMessageWorker.DELETEFLAG + " INTEGER DEFAULT 0;");
            } catch (Exception e) {

                e.printStackTrace();
            }

            try {

                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " add " +
                        GroupMessageWorker.TIMESTAMP + " INTEGER DEFAULT 0;");

                String[] columns = {GroupMessageWorker.ID, GroupMessageWorker.CONTENT,
                        GroupMessageWorker.TIMESTAMP};
                String selection = GroupMessageWorker.TIMESTAMP + " = 0";
                Cursor cursor = db
                        .query(GroupMessageWorker.TB_NAME, columns, selection, null, null, null,
                                null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {

                        do {
                            long localId = cursor
                                    .getLong(cursor.getColumnIndex(GroupMessageWorker.ID));

                            long dataTime = 0;
                            String content = cursor
                                    .getString(cursor.getColumnIndex(GroupMessageWorker.CONTENT));
                            try {
                                dataTime = new JSONObject(content).getLong("datetime");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ContentValues values = new ContentValues();
                            values.put(GroupMessageWorker.TIMESTAMP, dataTime);

                            db.update(GroupMessageWorker.TB_NAME, values,
                                    GroupMessageWorker.ID + " = " + localId, null);
                        }
                        while (cursor.moveToNext());
                    }
                    cursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 601) {
            try {//私聊消息列表添加是否已悄悄查看字段
                db.execSQL("alter table " + NearContactWorker.TB_NAME + " add " +
                        NearContactWorker.QUIETSEEN + " INTEGER DEFAULT 0;");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {//私聊消息表添加是否是新的消息的字段
                db.execSQL("alter table " + PersonalMessageWorker.TB_NAME + " add " +
                        PersonalMessageWorker.NEWFLAG + " INTEGER DEFAULT 0;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 620) {
            try {//圈聊消息表添加消息类型字段
                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " add " +
                        GroupMessageWorker.MESSAGETYPE + " INTEGER ;");


                String[] columns = {GroupMessageWorker.ID, GroupMessageWorker.CONTENT,
                        GroupMessageWorker.MESSAGETYPE};

                String selection = GroupMessageWorker.DELETEFLAG + " = 0";
                Cursor cursor = db
                        .query(GroupMessageWorker.TB_NAME, columns, selection, null, null, null,
                                null);

                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String content = cursor
                                .getString(cursor.getColumnIndex(GroupMessageWorker.CONTENT));
                        int id = cursor.getInt(cursor.getColumnIndex(GroupMessageWorker.ID));
                        GroupChatMessage bean = JSON.parseObject(content,GroupChatMessage.class);
                        ContentValues values = new ContentValues();
                        values.put(GroupMessageWorker.MESSAGETYPE, bean.type);

                        db.update(GroupMessageWorker.TB_NAME, values,
                                GroupMessageWorker.ID + " = " + id, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {//圈聊消息表添加发送者id字段
                db.execSQL("alter table " + GroupMessageWorker.TB_NAME + " add " +
                        GroupMessageWorker.SENDERID + " INTEGER ;");


                String[] columns = {GroupMessageWorker.ID, GroupMessageWorker.CONTENT,
                        GroupMessageWorker.SENDERID};

                String selection = GroupMessageWorker.DELETEFLAG + " = 0";
                Cursor cursor = db
                        .query(GroupMessageWorker.TB_NAME, columns, selection, null, null, null,
                                null);

                if (cursor != null && cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String content = cursor
                                .getString(cursor.getColumnIndex(GroupMessageWorker.CONTENT));
                        int id = cursor.getInt(cursor.getColumnIndex(GroupMessageWorker.ID));
                        GroupChatMessage bean = JSON.parseObject(content,GroupChatMessage.class);
                        ContentValues values = new ContentValues();
                        values.put(GroupMessageWorker.SENDERID, bean.user.userid);

                        db.update(GroupMessageWorker.TB_NAME, values,
                                GroupMessageWorker.ID + " = " + id, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 640) {
            try {// 添加聊吧通知表
//				db.execSQL( ChatBarNoticeWorker.tableSql );
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_GroupNoticeWorker", e.getMessage());
            }
        }
        if (oldVersion < 641) {
            try {//添加
//				db.execSQL( "alter table " + KeyWordWorker.TB_NAME + " add " +
//					KeyWordWorker.K_KEYWORD_LEVEL + " INTEGER DEFAULT 0;" );
            } catch (Exception e) {
                CommonFunction.log("SQLiteHelper_KeyWordWorker.K_KEYWORD_LEVEL", e.getMessage());
            }
        }

        if (oldVersion < 726){
            try {
                db.execSQL(GroupHistoryWorker.tableSql);
            }catch (Exception e) {
                CommonFunction.log("SQLiteHelper_GroupHistoryWorker.K_KEYWORD_LEVEL", e.getMessage());
            }
        }

        if (oldVersion < 727){
            try {
                db.execSQL(VideoChatWorker.tableSql);
            }catch (Exception e) {
                CommonFunction.log("SQLiteHelper_GroupHistoryWorker.K_KEYWORD_LEVEL", e.getMessage());
            }
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public synchronized void close() {
        super.close();
        db = null;
    }
}
