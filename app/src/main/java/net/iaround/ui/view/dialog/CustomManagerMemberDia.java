package net.iaround.ui.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.ui.chat.GroupUserForbid;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.utils.eventbus.NickNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/17 14:37
 * Email：15369302822@163.com
 */
public class CustomManagerMemberDia extends Dialog implements View.OnClickListener{

    private CheckBox cbForbid,cbCancelForbid,cbSetManager,cbCancelManager,cbTickChatbar;
    private TextView tvForbid,tvCancelForbid,tvSetManager,tvCancelManager,tvTickChatbar;
    private Button btnCancel,btnSure;
    /** 禁言 */
    public static final int MODE_SILENCE = 2;
    /** 禁言用户的请求码 */
    private final int REQ_CODE_FORBID = 1;
    /** 取消禁言用户的请求码 */
    private final int REQ_CODE_UNFORBID = 2;
    /**
     * 禁言请求的Flag
     */
    private long forbidReqFlag;
    /**
     * 解除禁言请求的Flag
     */
    private long cancleForbidReqFlag;
    /**
     * 添加管理员的Flag
     */
    private long addManagerFlag;
    /**
     * 取消管理员的Flag
     */
    private long cancelManagerFlag;
    /**
     * 踢出聊吧的Flag
     */
    private long tickChatbarFlag;
    /** 提升管理员的flag */
    private HashMap< Long , GroupSearchUser > becomeManagerReqMap;
    /** 取消管理员的flag */
    private HashMap< Long , GroupSearchUser > cancleManagerReqMap;
    /** 踢出圈子的flag */
    private HashMap< Long , GroupSearchUser > kickUserReqMap;
    /***用来表示选中操作类型
     * type 1 禁言24小时
     *      2 取消禁言
     *      3 设置管理
     *      4 取消管理
     *      5 踢出聊吧
     * **/
    private int type = 0;
    /**上下文*/
    private Context mContext;
    /**当前操作的 用户**/
    private GroupSearchUser mCurrentSelUser;
    /**当前用户所在聊吧id**/
    private String mGroupId;
    /** 加载框 */
    private Dialog mWaitDialog;
    private Activity mActivity;
    /** 当前页面的模式 */
    private int viewMode = 1;
    /**需要通过EventBus传递的事件**/
    private NickNameNotifyEvent event;
    /**更新聊吧成员的接口回调*/
    private UdapterMemberDataListener upMemberListener;

    public CustomManagerMemberDia(Context context,UdapterMemberDataListener listener) {
        super(context,R.style.transparent_dialog);
        this.mContext = context;
        this.upMemberListener = listener;
    }

    public CustomManagerMemberDia(Context context, int themeResId) {
        super(context,R.style.transparent_dialog);
        this.mContext = context;
    }

    protected CustomManagerMemberDia(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static void launchCustomManagerMemberDia(Context context, Activity activity,GroupSearchUser groupSearchUser, String groupId,UdapterMemberDataListener listener)
    {
        CustomManagerMemberDia customManagerMemberDia = new CustomManagerMemberDia(context,listener);
        customManagerMemberDia.setUser(groupSearchUser);
        customManagerMemberDia.setGroupId(groupId);
        customManagerMemberDia.setActivity(activity);
        customManagerMemberDia.show();
    }
    private void setActivity(Activity activity)
    {
        this.mActivity = activity;
    }
    private void setUser(GroupSearchUser groupSearchUser)
    {
        this.mCurrentSelUser = groupSearchUser;
    }
    private void setGroupId(String groupId)
    {
        this.mGroupId = groupId;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_manager_member_dia);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        becomeManagerReqMap = new HashMap< Long , GroupSearchUser >( );
        cancleManagerReqMap = new HashMap< Long , GroupSearchUser >( );
        kickUserReqMap = new HashMap< Long , GroupSearchUser >( );

        event = new NickNameNotifyEvent();

        initView();
        initListener();
    }
    private void initView()
    {
        cbForbid = (CheckBox) findViewById(R.id.checkbox_forbid);
        cbCancelForbid = (CheckBox) findViewById(R.id.checkbox_cancel_forbid);
        cbSetManager = (CheckBox) findViewById(R.id.checkbox_set_manager);
        cbCancelManager = (CheckBox) findViewById(R.id.checkbox_cancel_manager);
        cbTickChatbar = (CheckBox) findViewById(R.id.checkbox_tick_chatbar);

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSure = (Button) findViewById(R.id.btn_sure);

        mWaitDialog = DialogUtil.getProgressDialog( mContext , "" ,
                mContext.getString( R.string.please_wait ) , new android.content.DialogInterface.OnCancelListener( )
                {

                    @Override
                    public void onCancel( android.content.DialogInterface dialog )
                    {

                    }
                } );

    }
    private void initListener()
    {
        cbForbid.setOnClickListener(this);
        cbCancelForbid.setOnClickListener(this);
        cbSetManager.setOnClickListener(this);
        cbCancelManager.setOnClickListener(this);
        cbTickChatbar.setOnClickListener(this);
        findViewById(R.id.ll_forbid).setOnClickListener(this);
        findViewById(R.id.ll_cancel_forbid).setOnClickListener(this);
        findViewById(R.id.ll_set_manager).setOnClickListener(this);
        findViewById(R.id.ll_cancel_manager).setOnClickListener(this);
        findViewById(R.id.ll_tick_chatbar).setOnClickListener(this);

        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.checkbox_forbid:
            case R.id.ll_forbid:
                cbForbid.setChecked(true);
                cbCancelForbid.setChecked(false);
                cbSetManager.setChecked(false);
                cbCancelManager.setChecked(false);
                cbTickChatbar.setChecked(false);
                type = 1;
                break;
            case R.id.checkbox_cancel_forbid:
            case R.id.ll_cancel_forbid:
                cbForbid.setChecked(false);
                cbCancelForbid.setChecked(true);
                cbSetManager.setChecked(false);
                cbCancelManager.setChecked(false);
                cbTickChatbar.setChecked(false);
                type = 2;
                break;
            case R.id.checkbox_set_manager:
            case R.id.ll_set_manager:
                cbForbid.setChecked(false);
                cbCancelForbid.setChecked(false);
                cbSetManager.setChecked(true);
                cbCancelManager.setChecked(false);
                cbTickChatbar.setChecked(false);
                type = 3;
                break;
            case R.id.checkbox_cancel_manager:
            case R.id.ll_cancel_manager:
                cbForbid.setChecked(false);
                cbCancelForbid.setChecked(false);
                cbSetManager.setChecked(false);
                cbCancelManager.setChecked(true);
                cbTickChatbar.setChecked(false);
                type = 4;
                break;
            case R.id.checkbox_tick_chatbar:
            case R.id.ll_tick_chatbar:
                cbForbid.setChecked(false);
                cbCancelForbid.setChecked(false);
                cbSetManager.setChecked(false);
                cbCancelManager.setChecked(false);
                cbTickChatbar.setChecked(true);
                type = 5;
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_sure:
                // TODO: 2017/8/17 执行后续操作
                if (type == 1)
                {//禁言
                    forbidUser(REQ_CODE_FORBID);
                }else if (type == 2)
                {//取消禁言
                    forbidUser(REQ_CODE_UNFORBID);
                }else if (type == 3)
                {//设为管理员
                    setGroupManager();
                }else if (type == 4)
                {//取消管理员
                    cancelManager();
                }else if (type == 5)
                {// 踢出圈子
                    kickUser();
                }
                dismiss();
        }
    }
    /**
     * @Title: setGroupManager
     * @Description: 设置为管理员
     */
    private void setGroupManager( )
    {
        DialogUtil.showTwoButtonDialog( mContext , mContext.getString( R.string.dialog_title ) ,
                mContext.getString( R.string.set_manager_info ) , mContext.getString( R.string.cancel ) ,
                mContext.getString( R.string.ok ) , null , new View.OnClickListener( )
                {
                    @Override
                    public void onClick( View v )
                    {
                        showWaitDialog( true );
                        addManagerFlag = GroupHttpProtocol.groupManagerAdd( mContext , mGroupId ,
                                mCurrentSelUser.user.userid + "" ,
                                httpCallBack);
//                        becomeManagerReqMap.put( flag , mCurrentSelUser );
                    }
                } );
    }
    /**
     * @Title: cancelManager
     * @Description: 取消管理员
     */
    private void cancelManager( )
    {

        DialogUtil.showTwoButtonDialog( mContext , mContext.getString( R.string.dialog_title ) , String
                        .format( mContext.getString( R.string.group_user_list_cancle_manager_msg ) ,
                                mCurrentSelUser.user.nickname ) , mContext.getString( R.string.cancel ) ,
                mContext.getString( R.string.ok ) , null , new View.OnClickListener( )
                {

                    @Override
                    public void onClick( View v )
                    {
                        showWaitDialog( true );
                        cancelManagerFlag = GroupHttpProtocol.groupManagerCancel( mContext ,mGroupId,
                                mCurrentSelUser.user.userid + "" ,
                                httpCallBack);
//                        cancleManagerReqMap.put( flag , mCurrentSelUser );
                    }
                } );

    }
    /**
     * @Title: kickUser
     * @Description: 踢出圈子
     */
    private void kickUser( )
    {
        DialogUtil.showTwoButtonDialog( mContext , mContext.getString( R.string.dialog_title ) ,
                mContext.getString( R.string.del_group_member_info ) , mContext.getString( R.string.cancel ) ,
                mContext.getString( R.string.ok ) , null , new View.OnClickListener( )
                {

                    @Override
                    public void onClick( View v )
                    {
                        CommonFunction.log( "fan" , "mCurrentSelUser==="
                                + mCurrentSelUser.user.userid );
                        CommonFunction.log( "fan" , "mCurrentSelUser***"
                                + mCurrentSelUser.user.nickname );
                        tickChatbarFlag = GroupHttpProtocol.batchKickUser(mContext, mGroupId ,
                                mCurrentSelUser.user.userid+"",httpCallBack );
//                        kickUserReqMap.put( flag , mCurrentSelUser );
                    }

                } );
    }
    /**
     * @Title: forbidUser
     * @Description: 禁言/解禁用户
     */
    private void forbidUser( int type )
    {
        if (type == REQ_CODE_FORBID)
        {
            // TODO: 2017/8/17 默认写死时间，后台控制具体禁言时间
            forbidReqFlag = GroupHttpProtocol.groupManageForbid( mActivity , mGroupId ,
                    String.valueOf(mCurrentSelUser.user.userid) ,86400000 ,httpCallBack);
        }else if (type == REQ_CODE_UNFORBID)
        {
            cancleForbidReqFlag = GroupHttpProtocol.groupManagerCacelForbid( mActivity ,mGroupId ,
                    String.valueOf(mCurrentSelUser.user.userid ), httpCallBack );
        }
    }
    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            if (result == null)
                return;
            handleDataSuccess(result,flag);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            ErrorCode.toastError(mContext,e);
        }
    };
    /**
     * @Title: handleDataSuccess
     * @Description: 处理成功返回
     * @param result
     * @param flag
     */
    private void handleDataSuccess(String result , long flag )
    {
        if (flag == addManagerFlag)
        {
            // 添加到管理员列表的缓存中
            BaseServerBean beanback = GsonUtil.getInstance( ).getServerBean( result ,
                    BaseServerBean.class );
            if ( beanback.isSuccess( ) )
            {
                showWaitDialog(false);
                long userId = mCurrentSelUser.user.userid;
                GroupModel.getInstance( ).addToManagerIdList( String.valueOf( userId ) );

                mCurrentSelUser.grouprole = 1;
                upMemberListener.becameManagerListener(mCurrentSelUser);
//                event.setGroupSearchUser(mCurrentSelUser);
//                EventBus.getDefault().post(event);
//                mAdapter.notifyDataSetChanged( );
                CommonFunction.toastMsg(mContext,R.string.operate_success );
            } else
            {
                showWaitDialog(false);
//                ErrorCode.showError( mContext , result );
            }
        } else if ( flag == cancelManagerFlag )
        {
            // 取消管理员
            // 从管理员列表缓存中删除对应id
            BaseServerBean beanback = GsonUtil.getInstance( ).getServerBean( result ,
                    BaseServerBean.class );
            if ( beanback.isSuccess( ) )
            {
                showWaitDialog(false);
                long userId = mCurrentSelUser.user.userid;
                GroupModel.getInstance( ).delFromManagerIdList( String.valueOf( userId ) );

                mCurrentSelUser.grouprole = 2;
                upMemberListener.cancelManagerListener(mCurrentSelUser);
//                event.setGroupSearchUser(mCurrentSelUser);
//                EventBus.getDefault().post(event);
//                mAdapter.notifyDataSetChanged( );

                CommonFunction.toastMsg( mContext , R.string.operate_success );
            } else
            {
                showWaitDialog(false);
//                ErrorCode.showError( mContext , result );
            }

        } else if (tickChatbarFlag == flag)
        {
            BaseServerBean beanback = GsonUtil.getInstance( ).getServerBean( result ,
                    BaseServerBean.class );
            if ( beanback.isSuccess( ) )
            {
                showWaitDialog(false);
                // 从管理员列表缓存中删除对应id
                long userId =  mCurrentSelUser.user.userid;
                GroupModel.getInstance( ).delFromManagerIdList( String.valueOf( userId ) );
                // 踢出圈子
                mCurrentSelUser.grouprole = 3;
                upMemberListener.tickChatbarListener(mCurrentSelUser);
//                event.setUpdateUser("updateUser");
//                event.setGroupSearchUser(mCurrentSelUser);
//                EventBus.getDefault().post(event);
                CommonFunction.toastMsg( mContext , R.string.operate_success );
            } else
            {
                showWaitDialog(false);
                ErrorCode.showError( mContext , result );
            }

        }else if (forbidReqFlag == flag)
        {//禁言
            Map< String , Object > map = JsonUtil.jsonToMap( result );

            if ( map != null && map.containsKey( "status" ) )
            {
                int status = (Integer) map.get( "status" );
                if ( status == 200 )
                {
                    CommonFunction.showToast( mActivity ,
                            mContext.getResources().getString( R.string.operate_success ) , 0 );
                } else
                {
                    if ( map.containsKey( "error" ) )
                    {
                        int e = (Integer) map.get( "error" );
                        ErrorCode.showError( mActivity , result );
                    }
                }
            }
            else
            {
                showWaitDialog(false);
                CommonFunction.showToast( mActivity , mContext.getString( R.string.operate_fail ) , 0 );
            }
        }else if (cancleForbidReqFlag == flag)
        {//取消禁言
            Map< String , Object > map = JsonUtil.jsonToMap( result );

            if ( map != null && map.containsKey( "status" ) )
            {
                int status = (Integer) map.get( "status" );
                if ( status == 200 )
                {
                    CommonFunction.showToast( mActivity ,
                            mContext.getString( R.string.operate_success ) , 0 );
                }
                else
                {
                    if ( map.containsKey( "error" ) )
                    {
                        ErrorCode.showError( mActivity , result );
                    }
                }
            }
            else
            {
                showWaitDialog(false);
                CommonFunction.showToast( mActivity , mContext.getString( R.string.operate_fail ) , 0 );
            }
        }
    }
    /**
     * @Title: showWaitDialog
     * @Description: 显示加载框
     * @param isShow
     */
    private void showWaitDialog( boolean isShow )
    {
        if ( mWaitDialog != null )
        {
            if ( isShow )
            {
                mWaitDialog.show( );
            }
            else
            {
                mWaitDialog.dismiss( );
            }
        }
    }
    public interface UdapterMemberDataListener
    {
        void becameManagerListener(GroupSearchUser groupSearchUser);
        void cancelManagerListener(GroupSearchUser groupSearchUser);
        void tickChatbarListener(GroupSearchUser groupSearchUser);
    }

}
