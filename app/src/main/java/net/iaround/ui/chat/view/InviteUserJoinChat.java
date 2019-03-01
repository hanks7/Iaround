package net.iaround.ui.chat.view;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.entity.RecordShareBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.HeadPhotoView;

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

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/23 12:12
 * Email：15369302822@163.com
 */
public class InviteUserJoinChat extends FriendBaseRecordView implements View.OnClickListener {

    private CheckBox checkBox;
    private HeadPhotoView userIcon;
    private ImageView chatbarIcon;
    private TextView tvTitle,tvDetail;//聊吧名称和描述
    private Button btnRefuse,btnAgree,btnJoin;//拒绝，同意，加入按钮
    private RelativeLayout rlContent;

    private final int JUMP_URL_TAG = R.layout.chat_record_share_other;// 跳转的tag
    private String lastLoadImagePath = "";//上一次加载图片的url

    private Context mContext;
    private RecordShareBean bean;

    public InviteUserJoinChat(Context context) {
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
        LayoutInflater.from(context).inflate(R.layout.chat_record_join_chat,this);
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

        if(bean == null)
            return;
        btnJoin.setText("加入");

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
            case R.id.btn_join_group:
                GroupChatTopicActivity old = (GroupChatTopicActivity)CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                if(old!=null){
                    old.isGroupIn = true;
                    old.finish();
                }
                intent.setClass(mContext, GroupChatTopicActivity.class);
                intent.putExtra("id",String.valueOf(bean.groupid));
                mContext.startActivity(intent);
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
}
