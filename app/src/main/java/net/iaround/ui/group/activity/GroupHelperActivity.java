
package net.iaround.ui.group.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.type.ChatMessageType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.bean.GroupContact;
import net.iaround.ui.space.more.GroupsMsgSettingActivity;
import net.iaround.ui.view.dialog.CustomContextDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


/**
 * @author tanzy
 * @Description: 圈助手界面
 * @date 2015-6-29
 */
public class GroupHelperActivity extends BaseFragmentActivity implements OnClickListener, HttpCallBack {
    public GroupHelperActivity instant;

    private ImageView titleRight;
    private TextView titleName;
    private ListView listView;

    private DataAdapter adapter;
    private ArrayList<GroupContact> dataList = new ArrayList<GroupContact>();

    public static final int REFRESH_DATA = 1000;

    private CustomContextDialog mCustomContextDialog;
    private long setHelperFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_helper_activity);

        instant = this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);

        titleName = (TextView) findViewById(R.id.tv_title);
        titleName.setText(R.string.group_helper);

        titleRight = (ImageView) findViewById(R.id.iv_right);
        titleRight.setImageResource(R.drawable.title_more);
        titleRight.setOnClickListener(this);

        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        adapter = new DataAdapter();
        listView.setAdapter(adapter);

        initCustomContextDialog();
    }

    private void initCustomContextDialog() {
        if (GroupAffairModel.getInstance().getGroupHeplerTopAtMsg(GroupHelperActivity.this) == 0) {
            if (GroupAffairModel.getInstance().groupsMsgStatus.assistant == 1) {
                mCustomContextDialog = new CustomContextDialog(this, 17);
            } else {
                mCustomContextDialog = new CustomContextDialog(this, 18);
            }
        } else {
            if (GroupAffairModel.getInstance().groupsMsgStatus.assistant == 1) {
                mCustomContextDialog = new CustomContextDialog(this, 19);
            } else {
                mCustomContextDialog = new CustomContextDialog(this, 20);
            }
        }
        mCustomContextDialog.setListenner(moreOnClickListenr);
    }

    private void initData() {
        ArrayList<GroupContact> groups = GroupModel.getInstance().getGroupContactList(
                GroupHelperActivity.this);
        dataList.clear();
        for (int i = 0; i < groups.size(); i++) {
            if (GroupAffairModel.getInstance().getGroupMsgStatus(groups.get(i).groupID) == GroupMsgReceiveType.RECEIVE_NOT_NOTICE)
                dataList.add(groups.get(i));
        }
        if (dataList.size() <= 0) {
            findViewById(R.id.top_text).setVisibility(View.GONE);
            findViewById(R.id.listview).setVisibility(View.GONE);
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.top_text).setVisibility(View.VISIBLE);
            findViewById(R.id.listview).setVisibility(View.VISIBLE);
            findViewById(R.id.empty).setVisibility(View.GONE);

            sortByTime();
            adapter.notifyDataSetChanged();
        }

    }

    private OnClickListener moreOnClickListenr = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ((int) v.getTag()) {
                case 0://在圈消息置顶
                    if (GroupAffairModel.getInstance().getGroupHeplerTopAtMsg(GroupHelperActivity.this) == 0) {
                        GroupAffairModel.getInstance().setGroupHelperTopAtMsg(GroupHelperActivity.this, 1);
                    } else {
                        GroupAffairModel.getInstance().setGroupHelperTopAtMsg(GroupHelperActivity.this, 0);
                    }
                    break;
                case 1://圈消息设置
                    Intent intent = new Intent();
                    intent.setClass(GroupHelperActivity.this, GroupsMsgSettingActivity.class);
                    GroupHelperActivity.this.startActivity(intent);
                    break;
                case 2://关闭圈助手
                    if (GroupAffairModel.getInstance().groupsMsgStatus.assistant == 1) {
                        String value = "n";
                        setHelperFlag = GroupHttpProtocol.setGroupHelperStatus(mContext, value,
                                GroupHelperActivity.this);
                    } else {
                        String value = "y";
                        setHelperFlag = GroupHttpProtocol.setGroupHelperStatus(mContext, value,
                                GroupHelperActivity.this);
                    }
                    break;
            }
            mCustomContextDialog.dismiss();
        }

    };

    @Override
    public void onGeneralSuccess(String result, long flag) {
//        super.onGeneralSuccess(result, flag);
        if (flag == setHelperFlag) {
            if (Constant.isSuccess(result)) {
                if (GroupAffairModel.getInstance().groupsMsgStatus.assistant == 1) {
                    GroupAffairModel.getInstance().groupsMsgStatus.assistant = 0;
                } else {
                    GroupAffairModel.getInstance().groupsMsgStatus.assistant = 1;
                }
            } else {
                ErrorCode.showError(mContext, result);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
//        super.onGeneralError(e, flag);
    }
    //	private void showContextMenu( View view )
//	{
//		CustomContextMenu menu = new CustomContextMenu( mContext );
//		if ( GroupAffairModel.getInstance( ).getGroupHeplerTopAtMsg( mContext ) == 0 )
//			menu.addMenuItem( 0 , getString( R.string.top_at_message ) , topAtMessage , false );
//		else
//			menu.addMenuItem( 0 , getString( R.string.cancel_top_at_msg ) ,
//					cancelTopAtMessage , false );
//
//		menu.addMenuItem( 1 , getString( R.string.group_msg_setting ) , groupMsgSetting ,
//				false );
//
//		if ( GroupAffairModel.getInstance( ).groupsMsgStatus.assistant == 1 )
//			menu.addMenuItem( 2 , getString( R.string.close_group_helper ) , mContext
//					.getResources( ).getColor( R.color.c_d42f2b ) , closeHelper , true );
//		else
//			menu.addMenuItem( 2 , getString( R.string.open_group_helper ) , mContext
//					.getResources( ).getColor( R.color.c_d42f2b ) , openHelper , true );
//
//
//		menu.showMenu( view );
//	}
//
////	/** 在消息列表顶置 */
//	OnClickListener topAtMessage = new OnClickListener( )
//	{
//		@Override
//		public void onClick( View v )
//		{
//			GroupAffairModel.getInstance( ).setGroupHelperTopAtMsg( mContext , 1 );
//		}
//	};
//
//	/** 取消顶置 */
//	OnClickListener cancelTopAtMessage = new OnClickListener( )
//	{
//		@Override
//		public void onClick( View v )
//		{
//			GroupAffairModel.getInstance( ).setGroupHelperTopAtMsg( mContext , 0 );
//		}
//	};
//
//	/** 圈消息设置 */
//	OnClickListener groupMsgSetting = new OnClickListener( )
//	{
//		@Override
//		public void onClick( View v )
//		{
//			Intent intent = new Intent( );
//			intent.setClass( mContext , GroupsMsgSettingActivity.class );
//			mContext.startActivity( intent );
//		}
//	};
//
//	/** 关闭圈助手 */
//	OnClickListener closeHelper = new OnClickListener( )
//	{
//		@Override
//		public void onClick( View v )
//		{
//			GroupAffairModel.getInstance( ).groupsMsgStatus.assistant = 0;
//		}
//	};
//
//	/** 开启圈助手 */
//	OnClickListener openHelper = new OnClickListener( )
//	{
//		@Override
//		public void onClick( View v )
//		{
//			GroupAffairModel.getInstance( ).groupsMsgStatus.assistant = 1;
//		}
//	};

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left: {
                SharedPreferenceUtil.getInstance(GroupHelperActivity.this)
                        .putLong(
                                SharedPreferenceUtil.GROUP_HELPER_ENTER_TIME
                                        + Common.getInstance().loginUser.getUid(),
                                System.currentTimeMillis()
                                        + Common.getInstance().serverToClientTime);

                finish();
            }
            break;
            case R.id.iv_right: {
//				showContextMenu( v );
                initCustomContextDialog();
                mCustomContextDialog.show();
            }
            break;
        }
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_DATA: {
                    initData();
                }
                break;

            }
        }

    };

    private class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public GroupContact getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                LayoutInflater inflater = (LayoutInflater) GroupHelperActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater
                        .inflate(R.layout.message_group_helper_item, parent, false);
                holder = new ViewHolder();
                initMyNearItemHolder(convertView, holder);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GroupContact contact = getItem(position);

            if (dataList.size() == 1) {
                holder.dividerView.setVisibility(View.GONE);
            }
            // 状态
            holder.chat_status.setVisibility(View.GONE);

            // 圈图标
            holder.userIconView.execute(NetImageView.DEFAULT_AVATAR_ROUND_LIGHT,
                    contact.groupIcon, 12);
            holder.userIconView.setVipLevel(0);

            // 圈名
            holder.userNameView.setText(FaceManager.getInstance(GroupHelperActivity.this)
                    .parseIconForString(holder.userNameView, GroupHelperActivity.this, contact.groupName,
                            20));

            // 正文
            GroupChatMessage lastContact = JSON.parseObject(contact.lastContent, GroupChatMessage.class);
            if (lastContact == null) {
                holder.tvNotice
                        .setText(GroupHelperActivity.this.getString(R.string.receive_num_new_message));
            } else {
                holder.tvNotice.setText(FaceManager.getInstance(GroupHelperActivity.this)
                        .parseIconForString(holder.userNameView, GroupHelperActivity.this,
                                getGroupContentByType(lastContact, contact.isBeAt), 20));
            }

            // 时间
            CommonFunction.log("sherlock", "contact time == " + contact.time);
            holder.onlineTagView.setText(TimeFormat.timeFormat4(GroupHelperActivity.this, contact.time));

            // 未读数
            if (contact.noRead > 0) {
                holder.badge.setVisibility(View.VISIBLE);
                if (contact.noRead > 0 && contact.noRead < 10) {
                    holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num_gray);
                    holder.badge.setText(contact.noRead + "");
                } else if (contact.noRead > 10) {

                    holder.badge.setText(contact.noRead > 999 ? "999+" : contact.noRead + "");
                    holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num_gray1);
                }
            } else {
                holder.badge.setVisibility(View.GONE);
            }

            holder.layoutContent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupContact bean = dataList.get(position);
                    GroupModel.getInstance().EraseGroupNoReadNum(GroupHelperActivity.this, bean.groupID);
                    Intent intent = new Intent(GroupHelperActivity.this, GroupChatTopicActivity.class);
                    intent.putExtra("id", bean.groupID + "");
                    intent.putExtra("icon", bean.groupIcon);
                    intent.putExtra("name", bean.groupName);
                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
                    intent.putExtra("isChat", true);
//                    GroupHelperActivity.this.startActivity(intent);
                    GroupChatTopicActivity.ToGroupChatTopicActivity(GroupHelperActivity.this,intent);
                }
            });

            holder.layoutContent.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    DialogUtil.showTwoButtonDialog(GroupHelperActivity.this,
                            GroupHelperActivity.this.getString(R.string.chat_remove_contact_title),
                            GroupHelperActivity.this.getString(R.string.chat_remove_contact_notice),
                            GroupHelperActivity.this.getString(R.string.cancel),
                            GroupHelperActivity.this.getString(R.string.chat_record_fun_del), null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GroupContact contact = dataList.get(position);
                                    GroupModel.getInstance().removeGroupAndAllMessage(
                                            GroupHelperActivity.this,
                                            Common.getInstance().loginUser.getUid() + "",
                                            contact.groupID + "");
                                    dataList.remove(contact);
                                    notifyDataSetChanged();
                                    handler.sendEmptyMessage(REFRESH_DATA);//jiqiang   MessagesPrivateView.REFRESH_DATA
                                }
                            });
                    return false;
                }
            });

            holder.userIconView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupContact bean = dataList.get(position);
                    Intent intent = new Intent(GroupHelperActivity.this, GroupInfoActivity.class);
                    intent.putExtra(GroupInfoActivity.GROUPID, bean.groupID + "");
                    GroupHelperActivity.this.startActivity(intent);
                }
            });

            return convertView;
        }

    }

    private void initMyNearItemHolder(View convertView, ViewHolder holder) {
        holder.layoutContent = (RelativeLayout) convertView
                .findViewById(R.id.layout_content);
        holder.userIconView = (NetImageView) convertView.findViewById(R.id.friend_icon);
        holder.userNameView = (TextView) convertView.findViewById(R.id.userName);
        holder.onlineTagView = (TextView) convertView.findViewById(R.id.onlineTag);
        holder.chat_status = (TextView) convertView.findViewById(R.id.chat_status);
        holder.badge = (TextView) convertView.findViewById(R.id.chat_num_status);
        holder.tvNotice = (TextView) convertView.findViewById(R.id.tv_notice);
        holder.dividerView = convertView.findViewById(R.id.divider_view);
    }

    private String getGroupContentByType(GroupChatMessage contact, boolean isBeAt) {
        String content = "";
        if (contact != null) {
            switch (contact.type) {
                case ChatMessageType.TEXT:// 普通对话，低版本的打招呼，通过表情来替代
                    if (contact.content.equals(GroupHelperActivity.this
                            .getString(R.string.greeting_content))) {
                        contact.content = "[#a" + (new Random().nextInt(5) + 1) + "#]";
                    }
                    content = contact.content.toString();
                    break;
                case ChatMessageType.IMAGE:// 图片
                    content = formatPackage(R.string.chat_picture);
                    break;
                case ChatMessageType.SOUND:// 语音
                    content = formatPackage(R.string.chat_sound);
                    break;
                case ChatMessageType.VIDEO:// 视频
                    content = formatPackage(R.string.chat_video);
                    break;
                case ChatMessageType.LOCATION:// 位置
                    content = formatPackage(R.string.chat_location);
                    break;
                case ChatMessageType.GIFE_REMIND:// 礼物
                    content = formatPackage(R.string.gift);
                    break;
                case ChatMessageType.MEET_GIFT:// 约会道具
                    content = formatPackage(R.string.appointprop);
                    break;
                case ChatMessageType.FACE:// 贴图
                    content = formatPackage(R.string.mapping);
                    break;
                case ChatRecordViewFactory.SHARE:// 5.5分享
                    content = formatPackage(R.string.share_you_a_message);
                    break;
                default:// 低版本无法显示
                    content = GroupHelperActivity.this.getString(R.string.low_version);
                    break;
            }

            if (Common.getInstance().loginUser == null) {
                CommonFunction.log("sherlock", "login user is null");
            } else if (contact.user == null) {
                CommonFunction.log("sherlock", "contact.user is null");
            } else if (contact.user.userid != Common.getInstance().loginUser.getUid()
                    && contact.user.userid != Config.CUSTOM_SERVICE_UID
                    && contact.user.lng != 0 && contact.user.lat != 0) {
                content = contact.user.getNickname() + ":" + content;
                GeoData geo = LocationUtil.getCurrentGeo(GroupHelperActivity.this);
                if (contact.user.userid > 1000) {// 5.7圈超级管理员距离问题，改为不显示距离@tanzy
                    // content = "["
                    // + CommonFunction.covertSelfDistance( LocationUtil
                    // .calculateDistance( contact.user.lng , contact.user.lat ,
                    // geo.getLng( ) , geo.getLat( ) ) ) + "]" + content;

                    String prefixContent;
                    if (isBeAt) {// 如果是被人At了,优先展示[某人@了你]
                        prefixContent = GroupHelperActivity.this.getResources().getString(
                                R.string.chat_at_context);
                    } else {// 否则显示距离
                        int distance = LocationUtil.calculateDistance(contact.user.lng,
                                contact.user.lat, geo.getLng(), geo.getLat());
                        prefixContent = CommonFunction.covertSelfDistance(distance);
                    }
                    content = "[" + prefixContent + "]" + content;
                }

                CommonFunction.log("sherlock",
                        "contact.user.lng , contact.user.lat ,geo.getLng( ) ,geo.getLat( ) == "
                                + contact.user.lng, contact.user.lat, geo.getLng(),
                        geo.getLat());
            }
        }
        return content;
    }

    private String formatPackage(int contentId) {
        String content = GroupHelperActivity.this.getString(contentId);
        return "[" + content + "]";
    }

    private class ViewHolder {
        public RelativeLayout layoutContent;
        public NetImageView userIconView;
        public TextView badge;
        public TextView userNameView;
        public TextView onlineTagView;
        public TextView chat_status;
        public TextView tvNotice;
        public View dividerView;
    }

    /**
     * 根据时间倒序排序
     */
    private void sortByTime() {
        Collections.sort(dataList, new Comparator<GroupContact>() {
            public int compare(GroupContact lhs, GroupContact rhs) {
                return -Long.valueOf(lhs.time).compareTo(rhs.time);
            }

        });
    }

}
