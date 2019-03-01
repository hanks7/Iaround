package net.iaround.ui.chat.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.skill.skilldetail.SkillDetailActivity;
import net.iaround.ui.skill.skilluse.SkillUseDialogFragment;
import net.iaround.ui.skill.skilluse.SkillUsePresenter;
import net.iaround.ui.view.RatingBarView;
import net.iaround.utils.AssetUtils;
import net.iaround.utils.SkillHandleUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/26 18:14
 * Email：15369302822@163.com
 */
public class FriendSkillMsgChatPersonalView extends MySelfBaseRecordView implements View.OnClickListener{

    /**技能消息新增view*/
    /**技能名称**/
    private TextView tvSkillName;
    /**被攻击技能等级**/
    private RatingBarView rbvSkillLevelFriend;
    /**向谁使用技能**/
    private RichTextView tvSkillDetail;
    /**技能背景图**/
    private ImageView ivSkillIcon;
    /**技能动图*/
    private ImageView ivSkillAnimation;
    /**不服，反击**/
    private Button btnAgain;
    /**服了，求饶**/
    private Button btnFail;
    /**我的技能等级**/
    private RatingBarView rbvSkillLevelMine;
    /**立即升级按钮**/
    private TextView tvUpgrade;
    private LinearLayout llContentLayout;
    private Context mContext;
    private SkillAttackResult bean;

    public FriendSkillMsgChatPersonalView(Context context) {
        super(context);
        this.mContext = context;
        tvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        rbvSkillLevelFriend = (RatingBarView) findViewById(R.id.rbv_skill_level_friend);
        rbvSkillLevelMine = (RatingBarView) findViewById(R.id.rbv_skill_level_mine);
        tvSkillDetail = (RichTextView) findViewById(R.id.content);
        ivSkillIcon = (ImageView) findViewById(R.id.iv_skill);
        btnAgain = (Button) findViewById(R.id.btn_agin);
        btnFail = (Button) findViewById(R.id.btn_fail);
        llContentLayout = (LinearLayout) findViewById(R.id.ll_content_layout);
        tvUpgrade = (TextView) findViewById(R.id.tv_upgrade);
        btnAgain.setText(R.string.chat_skill_msg_friends_btn2);
        ivSkillAnimation = (ImageView) findViewById(R.id.iv_skill_ani);

        btnFail.setOnClickListener(this);
        btnAgain.setOnClickListener(this);
        tvUpgrade.setOnClickListener(this);
    }

    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.skill_msg_view_other, this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        String message = record.getContent();
        try {
            JSONObject jsonObject = new JSONObject(message);
            String content = jsonObject.getString("content");
            bean = GsonUtil.getInstance().getServerBean(content,SkillAttackResult.class);
            if (bean == null)
                return;

            // 替换成未通过的头像
            if(record.getId() == Common.getInstance().loginUser.getUid()){
                String verifyicon = Common.getInstance().loginUser.getVerifyicon();
                if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                    record.setIcon(verifyicon);
                }
            }

            setUserIcon(context,record,mIconView);
            if (bean.user != null && bean.targetUser != null) {
//                //用户昵称
//                SpannableString name;
//                if ("".equals(bean.user.NickName))
//                    name = SpannableString.valueOf(bean.targetUser.UserID + "");
//                else
//                    name = FaceManager.getInstance(context).parseIconForString(context, bean.user.NickName, 13, null);
//                tvNameMe.setText(name);
                //技能名称
                String skillname = CommonFunction.getNameByLanguage(context,bean.skillName,"|");
                tvSkillName.setText(skillname);
                //我的技能等级
                rbvSkillLevelMine.setStarView(bean.targetSkillLevel);
                rbvSkillLevelFriend.setStarView(bean.userSkillLevel);
                //技能内容
                String format = AssetUtils.getChatDesc(0, bean);
                tvSkillDetail.setText(Html.fromHtml(format));
                //技能图标
                GlideUtil.loadImage(BaseApplication.appContext, bean.skillIcon, ivSkillIcon);
                //技能动图
                SkillHandleUtils.playFrame(context,bean.gif,ivSkillAnimation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reset() {
        tvSkillName.setText("");
        tvSkillDetail.setText("");
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        llContentLayout.setEnabled(isEnable);
    }

    @Override
    public void onClick(View v) {
        if (v == tvUpgrade)
        {
            // TODO: 2017/8/27 立即升级
            if (bean.isOpen== 0)
            {
                CommonFunction.toastMsg(mContext,R.string.chat_skill_msg_friends_upgrade_hint);
            }else {
                Intent intent = new Intent(mContext, SkillDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("skillId",String.valueOf(bean.skillId));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        }else if (v == btnAgain)
        {//不服，反击
            // TODO: 2017/8/27 打开技能攻击面板
            Bundle bundle = new Bundle();
            bundle.putString("targetUserId", bean.user.UserID + "");
            SkillUseDialogFragment skillUseDialogFragment = SkillUseDialogFragment.getInstance(bundle);
            new SkillUsePresenter(skillUseDialogFragment);
            FragmentManager manager = ((ChatPersonal) mContext).getFragmentManager();
            skillUseDialogFragment.show(manager, "skillUseDialogFragment");
        }else if (v == btnFail)
        {//服了，求饶
            // TODO: 2017/8/27 发送一条普通消息
            String content = mContext.getString(R.string.received_skill_surrender);
//            long mReqSendMsgFlag = System.currentTimeMillis();

//            long flag = SocketSessionProtocol.sessionPrivateMsg(mContext, mReqSendMsgFlag,
//                    bean.user.UserID, 1, String.valueOf(ChatRecordViewFactory.TEXT),
//                    "", 0, content);

            EventBus.getDefault().post("refer_chat_bar_skill"+content);

        }
    }

}
