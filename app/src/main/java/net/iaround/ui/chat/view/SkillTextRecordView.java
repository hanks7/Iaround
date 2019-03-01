package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.ui.view.HeadPhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/25 14:18
 * Email：15369302822@163.com
 */
public class SkillTextRecordView extends FriendBaseRecordView {

    @BindView(R.id.icon)
    HeadPhotoView icon;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivSVIP)
    ImageView ivSVIP;
    @BindView(R.id.tvIdentity_manager)
    TextView tvIdentityManager;
    @BindView(R.id.tvIdentity_owner)
    TextView tvIdentityOwner;
    @BindView(R.id.ll_charm_rank)
    LinearLayout llCharmRank;
    @BindView(R.id.tv_wealth_time)
    TextView tvWealthTime;
    @BindView(R.id.tv_wealth_top)
    TextView tvWealthTop;
    @BindView(R.id.ll_wealth_rank)
    LinearLayout llWealthRank;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.iv_skill_icon)
    ImageView ivSkillIcon;
    @BindView(R.id.ll_content)
    RelativeLayout llContent;
    @BindView(R.id.content_userinfo_ly)
    LinearLayout contentUserinfoLy;
    private Unbinder bind;

    public SkillTextRecordView(Context context) {
        super(context);
    }

    @Override
    protected void inflatView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_record_text_skill,this);
        bind = ButterKnife.bind(view);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        super.showRecord(context, record);
        content.setText(record.getContent());

        // 替换成未通过的头像
        if(record.getId() == Common.getInstance().loginUser.getUid()){
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                record.setIcon(verifyicon);
            }
        }

        content.setTag(R.id.im_preview_uri,record);
        setTag(record);
        //设置用户备注昵称
        setUserNotename(context, record);
        //设置用户昵称
        setUserNameDis(context, record,record.getGroupRole());
        // 设置头像
        setUserIcon(context, record, icon);
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        content.setEnabled(isEnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bind.unbind();
    }
}
