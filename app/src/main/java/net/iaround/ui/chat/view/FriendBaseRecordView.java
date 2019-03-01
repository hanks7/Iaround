package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.Item;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;

import static net.iaround.R.id.tv_wealth_time;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月20日 下午9:37:23
 * @Description: 朋友的消息的基础类 [不包含FriendGameImageRecordView,
 * FriendGameQuestionRecordView, FriendGameTextRecordView]
 * 只有会展示在私聊和圈聊的View才需要继承
 */
public abstract class FriendBaseRecordView extends ChatRecordView {

    protected HeadPhotoView mIconView;// 用户头像
    protected LinearLayout llUserInfo;// 聊天用户名字和距离信息布局
    protected TextView tvName;// 用户的名字TextView
    protected ImageView ivSVIP;// svip标识
    protected TextView tvDistance;// 用户的距离TextView
    protected CheckBox checkbox;// 勾选的View
    /**
     * 收到信息用户的身份标识
     */
    protected TextView tvIdentityManager;//管理员
    protected TextView tvIdentityOwner;//吧主
    protected LinearLayout llCharmRank; //魅力排行榜布局
    protected TextView tvTime;//本周，上周，本月，上月
    protected TextView tvRank;//排名
    protected LinearLayout llWealthRank;//富豪排行榜布局
    protected TextView tvWealthTime;
    protected TextView tvWealthRank;


    private int grouprole;

    private GeoData geoData = LocationUtil.getCurrentGeo(getContext());

    protected final int NAME_DIS_TEXT_SIZE = 10;// 显示名字和距离的字体大小

    protected int contentBackgroundRes = R.drawable.chat_new_record_friend_bg;//内容的背景
    private UserTitleView userTitleView;

    public FriendBaseRecordView(Context context) {
        super(context);
        inflatView(context);

        mIconView = (HeadPhotoView) findViewById(R.id.icon);
        llUserInfo = (LinearLayout) findViewById(R.id.userinfo_ly);
        tvName = (TextView) findViewById(R.id.tvName);
        ivSVIP = (ImageView) findViewById(R.id.ivSVIP);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        checkbox = (CheckBox) findViewById(R.id.checkbox);

        /**
         * 好友信息
         */
        tvIdentityManager = (TextView) findViewById(R.id.tvIdentity_manager);
        tvIdentityOwner = (TextView) findViewById(R.id.tvIdentity_owner);
        llCharmRank = (LinearLayout) findViewById(R.id.ll_charm_rank);
        llWealthRank = (LinearLayout) findViewById(R.id.ll_wealth_rank);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWealthTime = (TextView) findViewById(tv_wealth_time);
        tvRank = (TextView) findViewById(R.id.tv_top);
        tvWealthRank = (TextView) findViewById(R.id.tv_wealth_top);
        userTitleView = (UserTitleView) findViewById(R.id.friend_ustitle);


//		tvFlag = (TextView) findViewById(R.id.tv_flag);
//
//		if ( context instanceof GroupChatTopicActivity )
//		{
//			contentBackgroundRes = R.drawable.z_common_message_bg;
//		}//jiqiang
    }

    /**
     * 实例化View,必须实现
     */
    protected abstract void inflatView(Context context);

    @Override
    public void initRecord(Context context, ChatRecord record) {
        setUserIconClickListener(context, mIconView);
        checkbox.setOnClickListener(mCheckBoxClickListener);
        setOnClickListener(mCheckBoxClickListener);
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
    }

    protected SpannableString getSpannableStr(Context context, String str) {
        return FaceManager.getInstance(context)
                .parseIconForString(tvName, context, str, NAME_DIS_TEXT_SIZE);
    }

    /**
     * 设置用户的名字和距离 只有圈聊显示名字和距离
     */
    protected void setUserNameDis(Context context, ChatRecord record) {
        if (context instanceof GroupChatTopicActivity || context instanceof ChatPersonal) {
            llUserInfo.setVisibility(View.VISIBLE);

            int svip = record.getfSVip();
            if (svip == 0) {
                ivSVIP.setVisibility(GONE);
                tvName.setTextColor(getResources().getColor(R.color.c_999999));
            } else {
                ivSVIP.setVisibility(GONE);
                tvName.setTextColor(getResources().getColor(R.color.c_ee4552));
            }

            int distance = LocationUtil
                    .calculateDistance(geoData.getLng(), geoData.getLat(), record.getfLng(),
                            record.getfLat());

            String userName = record.getfNoteName(true);//好友昵称
            String userNameMe = record.getNoteName(true);//个人昵称

            if (context instanceof GroupChatTopicActivity) {
                /***
                 * 判断是否是系统用户
                 */
                if (!bIsSystemUser(record.getFuid())) {
                    String str = userName;
                    SpannableString spContent = FaceManager.getInstance(getContext())
                            .parseIconForString(tvName, getContext(), str, 12);
                    tvName.setText(spContent);

                } else {// 5.7圈聊中系统用户不显示距离 tanzy
                    tvName.setText(userName);
                    tvDistance.setText("");
                }
            }
        } else {
            /**
             * 私聊中不展示
             */
            llUserInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 设置用户的名字和距离 只有圈聊显示名字和距离
     */
    protected void setUserNameDis(final Context context, final ChatRecord record, int grouprole) {
        if (context instanceof GroupChatTopicActivity || context instanceof ChatPersonal) {
            llUserInfo.setVisibility(View.VISIBLE);
            llWealthRank.setVisibility(GONE);

            int svip = record.getfSVip();
            if (svip == 0) {
                ivSVIP.setVisibility(GONE);
                tvName.setTextColor(getResources().getColor(R.color.c_999999));
            } else {
                ivSVIP.setVisibility(GONE);
                tvName.setTextColor(getResources().getColor(R.color.c_ee4552));
            }

            int distance = LocationUtil
                    .calculateDistance(geoData.getLng(), geoData.getLat(), record.getfLng(),
                            record.getfLat());

            String userName = record.getfNoteName(true);
            String userNameMe = record.getNoteName(true);
            if (context instanceof GroupChatTopicActivity) {

                /***
                 * 判断是否是系统用户
                 */
                if (!bIsSystemUser(record.getFuid())) {
                    String str = userName;
                    SpannableString spContent = FaceManager.getInstance(getContext())
                            .parseIconForString(tvName, getContext(), str, 12);
                    tvName.setText(spContent);
                    llCharmRank.setVisibility(GONE);
                    llWealthRank.setVisibility(GONE);

                    /**
                     * 判断收到信息的用户的身份标识
                     */
                    if (grouprole == 0) {
                        tvIdentityOwner.setVisibility(VISIBLE);
                        tvIdentityManager.setVisibility(GONE);
                        tvIdentityOwner.setBackgroundResource(R.drawable.chatbar_groupowner_bg);
                        tvIdentityOwner.setText(R.string.chatbar_owner);
                    } else if (grouprole == 1) {
                        tvIdentityManager.setVisibility(VISIBLE);
                        tvIdentityOwner.setVisibility(GONE);
                        tvIdentityManager.setBackgroundResource(R.drawable.chatbar_groupmanager_bg);
                        tvIdentityManager.setText(R.string.chatbar_manager);
                    } else if (grouprole == 2) {
                        tvIdentityManager.setVisibility(GONE);
                        tvIdentityOwner.setVisibility(GONE);
                        int languageIndex = CommonFunction.getLanguageIndex(context);
                        if (languageIndex == 0) {
                            tvWealthTime.setTextSize(10);
                        }

                        if (record != null) {

                            if (record.getRecruit() == 1) {
                                if (record.getMgroupRole() == 0 || record.getMgroupRole() == 1) {
                                    llWealthRank.setVisibility(VISIBLE);
                                    llWealthRank.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            GroupHttpProtocol.chatbarInviteUser(context, record.getGroupid(), record.getFuid() + "", new HttpCallBack() {
                                                @Override
                                                public void onGeneralSuccess(String result, long flag) {
                                                    CommonFunction.toastMsg(context, context.getString(R.string.chat_bar_recuit_success));
                                                }

                                                @Override
                                                public void onGeneralError(int e, long flag) {

                                                }
                                            });
                                        }
                                    });
                                } else {
                                    llWealthRank.setVisibility(GONE);

                                }

                            } else {
                                llWealthRank.setVisibility(GONE);
                            }


                        }
                    }

                    Item item = record.getItem();
                    if (item != null) {
//						llCharmRank.setVisibility(VISIBLE);
//						RankingTitleUtil.getInstance().handleReallyRankNew(item.key,item.value,tvTime);
                        userTitleView.setVisibility(VISIBLE);
                        userTitleView.setTitleText(item);
                    } else {
                        userTitleView.setVisibility(GONE);
//						llCharmRank.setVisibility(GONE);
                    }

                } else {// 5.7圈聊中系统用户不显示距离 tanzy
                    tvName.setText(userName);
                    tvDistance.setText("");
                }
            }
        } else {
            llUserInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void showCheckBox(boolean isShow) {
        checkbox.setVisibility(isShow ? View.VISIBLE : View.GONE);
        setEnabled(isShow);
        setContentClickEnabled(!isShow);
    }

    public abstract void setContentClickEnabled(boolean isEnable);

    /**
     * 判断是否系统用户
     */
    protected boolean bIsSystemUser(long uid) {
        return uid <= Config.SYSTEM_UID;
    }

//	@Override
//	public void setGrouprole(int grouprole) {
//		super.setGrouprole(grouprole);
//		this.grouprole = grouprole;
//		Log.i("+++++++++", "++++++++++++++++setGrouprole:FriendBaseRecordView:+ grouprole "+grouprole);
//	}
}
