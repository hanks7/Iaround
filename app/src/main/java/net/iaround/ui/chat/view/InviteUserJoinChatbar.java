package net.iaround.ui.chat.view;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.DataTag;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.database.GroupSharePrefrenceUtil;
import net.iaround.model.entity.RecordShareBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.PathUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.GroupListToQuit;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.group.bean.JoinGroupBean;
import net.iaround.ui.view.HeadPhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/23 12:15
 * Email：15369302822@163.com
 */
public class InviteUserJoinChatbar extends FriendBaseRecordView implements View.OnClickListener {

    /**
     * 加入圈子
     */
    private long FLAG_JOIN_GROUP;
    /**
     * 带验证加入圈子
     */
    private long FLAG_JOIN_GROUP_WITH_CHECK;

    private HeadPhotoView userIcon;
    private ImageView chatbarIcon;
    private TextView tvTitle,tvDetail;//聊吧名称和描述
    private Button btnRefuse,btnAgree,btnJoin;//拒绝，同意，加入按钮
    private RelativeLayout rlContent;

    private final int JUMP_URL_TAG = R.layout.chat_record_share_other;// 跳转的tag
    private String lastLoadImagePath = "";//上一次加载图片的url

    private Context mContext;
    private String groupid;
    private CheckBox checkBox;
    private RecordShareBean bean;

    public InviteUserJoinChatbar(Context context) {
        super(context);

        this.mContext = context;
        checkBox = (CheckBox) findViewById(R.id.check_box);
        userIcon = (HeadPhotoView) findViewById(R.id.icon);
        chatbarIcon = (ImageView) findViewById(R.id.img);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDetail = (TextView) findViewById(R.id.tvDetail);
        btnRefuse = (Button) findViewById(R.id.btn_refuse);
        btnAgree = (Button) findViewById(R.id.btn_agree);
        btnJoin = (Button) findViewById(R.id.btn_join_group);
        rlContent = (RelativeLayout) findViewById(R.id.content);
    }
    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_record_join_chatbar,this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);

        rlContent.setOnClickListener(this);
        rlContent.setOnLongClickListener(mRecordLongClickListener);
        btnRefuse.setOnClickListener(this);
        btnAgree.setOnClickListener(this);
        btnJoin.setOnClickListener(this);

    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        super.showRecord(context, record);
        bean = GsonUtil.getInstance().getServerBean(
                record.getContent(), RecordShareBean.class);

        if(bean == null) return;

        String title = bean.title;
        if(TextUtils.isEmpty(title)){
            SpannableString spContent = FaceManager.getInstance(getContext())
                    .parseIconForString(tvTitle, getContext(), "",
                            14);
            tvTitle.setText(spContent);
        }else{
            SpannableString spContent = FaceManager.getInstance(getContext())
                    .parseIconForString(tvTitle, getContext(), title,
                            14);
            tvTitle.setText(spContent);
        }

        if (TextUtils.isEmpty(bean.content)) {
            tvTitle.setMaxLines(4);
            tvDetail.setMaxLines(1);
        } else {
            tvTitle.setMaxLines(2);
            tvDetail.setMaxLines(3);
            tvDetail.setVisibility(VISIBLE);
        }

        SpannableString spContent = FaceManager.getInstance(getContext())
                .parseIconForString(tvDetail, getContext(), bean.content,
                        14);
        tvDetail.setText(spContent);

        String thumbPicUrl = bean.thumb;
        if(TextUtils.isEmpty(lastLoadImagePath) || !lastLoadImagePath.equals(thumbPicUrl)){
            GlideUtil.loadRoundImage(BaseApplication.appContext,thumbPicUrl,(int)context.getResources().getDimension(R.dimen.x5),chatbarIcon, R.drawable.dynamic_no_data_icon, R.drawable.dynamic_no_data_icon);
            lastLoadImagePath = thumbPicUrl;
        }

        // 替换成未通过的头像
        if(record.getId() == Common.getInstance().loginUser.getUid()){
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                record.setIcon(verifyicon);
            }
        }

        rlContent.setTag(R.id.im_preview_uri,record);
        checkbox.setChecked(record.isSelect());
        checkbox.setTag(record);
        rlContent.setTag(JUMP_URL_TAG, bean.link);
        setTag(record);

        setUserNotename(context, record);
        setUserNameDis(context, record);
        // 设置头像点击事件
        setUserIcon(context, record, mIconView);
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        rlContent.setEnabled(isEnable);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId())
        {
            case R.id.btn_refuse:
                // TODO: 2017/8/24 拒绝加入聊吧 没有操作只是toast提示
                btnRefuse.setBackgroundResource(R.drawable.chatpersonl_refuse_press_bg);
                btnRefuse.setOnClickListener(null);
                btnAgree.setBackgroundResource(R.drawable.chatpersonl_refuse_press_bg);
                btnAgree.setOnClickListener(null);
                CommonFunction.toastMsg(BaseApplication.appContext,"您拒绝了对方的邀请");
                break;
            case R.id.btn_agree:
                // TODO: 2017/8/24  同意加入聊吧 请求加入聊吧，走加入流程
                btnAgree.setBackgroundResource(R.drawable.chatpersonl_refuse_press_bg);
                btnAgree.setOnClickListener(null);
                btnRefuse.setBackgroundResource(R.drawable.chatpersonl_refuse_press_bg);
                btnRefuse.setOnClickListener(null);
                joinGroup();
                break;
            case R.id.content:
                //点击聊吧内容区域
                String url = (String) v.getTag(JUMP_URL_TAG);
                // 跳转到页面
                String httpPrefix = PathUtil.getHTTPPrefix();
                boolean isHttpUrl = url.startsWith(httpPrefix);

                if (isHttpUrl && url.contains("gamecenter")) {

                } else if (isHttpUrl) {
                    intent.setClass(getContext(), WebViewAvtivity.class);
                    intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
                    intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE,
                            true);
                    getContext().startActivity(intent);
                } else {
                    InnerJump.Jump(getContext(), url);
                }
                break;
        }
    }
    /**
     * @Title: joinGroup
     * @Description: 加入圈子
     */
    private void joinGroup() {
        if (CommonFunction.isEmptyOrNullStr(Common.getInstance().loginUser.getIcon())) {
            DialogUtil.showOKDialog(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.icon_group_tip1), null);
        } else {
            // 记录用户加入的圈子，不管是否加入成功，用于加入成功并进入圈聊之后给出提示
            GroupSharePrefrenceUtil util = new GroupSharePrefrenceUtil();
            util.putBoolean(mContext, GroupSharePrefrenceUtil.FIRST_ENTER_GROUP, groupid,
                    true);
            groupid = String.valueOf(bean.groupid);
            FLAG_JOIN_GROUP = GroupHttpProtocol.joinChatbar(mContext,String.valueOf(bean.groupid), PhoneInfoUtil.getInstance(mContext).loginCode(mContext),joinHttpCallback);
        }
    }

    /**
     * 回调
     */
    private HttpCallBack joinHttpCallback = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            if (result == null)
                return;
            if (flag == FLAG_JOIN_GROUP)
            {//加入聊吧
                JoinGroupBean bean = GsonUtil.getInstance().getServerBean(
                        result, JoinGroupBean.class);
                if (bean.isSuccess())
                {
                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
                    intent.putExtra("id",String.valueOf( bean.groupid));
                    // 判断是否有新圈消息
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                    if(old!=null){
                        old.isGroupIn = true;
                        old.finish();
                    }
//                    mContext.startActivity(intent);
                    GroupChatTopicActivity.ToGroupChatTopicActivity(mContext, intent);
                }else
                {
                    if (bean.error == 6041)
                    {
                        FLAG_JOIN_GROUP_WITH_CHECK = GroupHttpProtocol.submitJoinGroup(mContext,
                                groupid, "", joinHttpCallback);
                    }
                }
            }else if (flag == FLAG_JOIN_GROUP_WITH_CHECK)
            {//申请加入聊吧
                if (Constant.isSuccess(result))
                {
                    CommonFunction.toastMsg(mContext, R.string.apply_join_group_success);
                }else
                {
                    // 加入圈子失败
                    JSONObject json;
                    try {
                        json = new JSONObject(result);
                        int error = json.optInt("error");
                        if (error == 6032 || error == 6007) {
                            if (Common.getInstance().loginUser.isVip()
                                    || Common.getInstance().loginUser.isSVip()) {
                                // VIP提示加入的圈子数量已经达到上限
                                DialogUtil.showTowButtonDialog(mContext,
                                        mContext.getString(R.string.group_full),
                                        mContext.getString(R.string.group_full_content),
                                        mContext.getString(R.string.cancel),
                                        mContext.getString(R.string.go_to_del), null,
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.setClass(mContext, GroupListToQuit.class);
                                                mContext.startActivity(intent);//jiqiang
                                            }
                                        });
                            } else {
                                DialogUtil.showTobeVipDialog(mContext, R.string.join_group, R.string.vip_join_group2, DataTag.VIEW_group);
                            }
                        } else {
                            ErrorCode.showError(mContext, result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            if (flag == FLAG_JOIN_GROUP_WITH_CHECK)
            {
                ErrorCode.toastError(mContext,e);
            }else if (flag == FLAG_JOIN_GROUP)
            {
                if (e == 6041)
                {
                    FLAG_JOIN_GROUP_WITH_CHECK = GroupHttpProtocol.submitJoinGroup(mContext,
                            groupid, "", joinHttpCallback);
                }else
                {
                    ErrorCode.toastError(mContext,e);
                }
            }
        }
    };
}
