package net.iaround.ui.chat.view;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
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
import net.iaround.conf.ErrorCode;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.skill.skilluse.SkillUseDialogFragment;
import net.iaround.ui.skill.skilluse.SkillUsePresenter;
import net.iaround.ui.view.RatingBarView;
import net.iaround.utils.AssetUtils;
import net.iaround.utils.SkillHandleUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import static net.iaround.tools.VerCodeTimerTask.mContext;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/26 18:14
 * Email：15369302822@163.com
 */
public class MySkillMsgChatPersonalView extends MySelfBaseRecordView implements View.OnClickListener{

    /**技能消息新增view*/
    /**技能名称**/
    private TextView tvSkillName;
    /**技能等级**/
    private RatingBarView rbvSkillLevel;
    /**向谁使用技能**/
    private RichTextView tvSkillDetail;
    /**技能背景图**/
    private ImageView ivSkillIcon;
    /**技能动图**/
    private ImageView ivSkillAnimation;
    /**再来一次按钮**/
    private Button btnAgain;
    private LinearLayout llContentLayout;
    private SkillAttackResult bean;

    private Context mContext;


    public MySkillMsgChatPersonalView(Context context) {
        super(context);

        this.mContext = context;

        tvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        rbvSkillLevel = (RatingBarView) findViewById(R.id.rbv_skill_level);
        tvSkillDetail = (RichTextView) findViewById(R.id.tvDetail);
        ivSkillIcon = (ImageView) findViewById(R.id.iv_skill);
        btnAgain = (Button) findViewById(R.id.btn_again);
        llContentLayout = (LinearLayout) findViewById(R.id.ll_content_layout);
        ivSkillAnimation = (ImageView) findViewById(R.id.iv_skill_ani);

        btnAgain.setText(R.string.chatpersonal_skill_msg_btn);
        
        btnAgain.setOnClickListener(this);
    }

    @Override
    protected void inflatView(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.skill_msg_view_mine,this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);
    }

    @Override
    public void showRecord(Context context, ChatRecord record) {
        if (record == null)
            return;
        String message = record.getContent();
        if (message == null)
            return;
        try {
            JSONObject jsonObject = new JSONObject(message);
            String content = jsonObject.getString("content");
            bean = GsonUtil.getInstance().getServerBean(content,SkillAttackResult.class);

            // 替换成未通过的头像
            if(record.getId() == Common.getInstance().loginUser.getUid()){
                String verifyicon = Common.getInstance().loginUser.getVerifyicon();
                if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                    record.setIcon(verifyicon);
                }
            }

            // 设置头像点击事件
            setUserIcon(context, record, mIconView);

            if (bean == null)
                return;
            if (bean.user != null && bean.targetUser != null) {
                //用户昵称
//                SpannableString name;
//                if ("".equals(bean.user.NickName))
//                    name = SpannableString.valueOf(bean.user.UserID + "");
//                else
//                    name = FaceManager.getInstance(context).parseIconForString(context, bean.user.NickName, 13, null);
//                tvNameMe.setText(name);
                //技能名称
                String skillname = CommonFunction.getNameByLanguage(context,bean.skillName,"|");
                tvSkillName.setText(skillname);
                //技能等级
                rbvSkillLevel.setStarView(bean.userSkillLevel);
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
        if (v == btnAgain)
        {
            //打开技能使用面板
            Bundle bundle = new Bundle();
//            bundle.putString("groupId",String.valueOf(bean.groupId));
            bundle.putString("targetUserId", bean.targetUser.UserID + "");
            SkillUseDialogFragment skillUseDialogFragment = SkillUseDialogFragment.getInstance(bundle);
            new SkillUsePresenter(skillUseDialogFragment);
            FragmentManager manager = ((ChatPersonal) mContext).getFragmentManager();
            skillUseDialogFragment.show(manager, "skillUseDialogFragment");
        }
    }
}
