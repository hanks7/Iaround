package net.iaround.ui.view.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.entity.PipelineGift;
import net.iaround.floatwindow.FloatWindowView;
import net.iaround.floatwindow.WindowManagerHelper;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

/**
 * Class: 技能攻击-全局横幅提示
 * Author：gh
 * Date: 2017/8/27 17:49
 * Email：jt_gaohang@163.com
 */
public class SkillAttactBannerView {

    private Context mContext;
    private FloatWindowView mView;

    private HeadPhotoView photoView;
    private TextView userNameView;
    private TextView contentView;

    private SkillAttackResult result;

    private static SkillAttactBannerView instance = null;

    public static SkillAttactBannerView getInstance(Context mContext) {
        if (instance == null) {
            instance = new SkillAttactBannerView(mContext);
        }
        return instance;
    }

    private SkillAttactBannerView(Context mContext) {
        this.mContext = mContext;
        mView = (FloatWindowView) LayoutInflater.from(mContext).inflate(R.layout.view_skill_attact, null);
        initView();
    }

    private void initView(){
        photoView = (HeadPhotoView)mView.findViewById(R.id.skill_attact_user_icon);
        userNameView = (TextView)mView.findViewById(R.id.skill_attact_user_name);
        contentView = (TextView)mView.findViewById(R.id.skill_attact_user_content);
        RelativeLayout skillAttactLayout = (RelativeLayout)mView.findViewById(R.id.rl_skill_attact);

        skillAttactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeView();
                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                User user = new User();
                user.setUid(result.user.UserID);
                user.setNickname(result.user.NickName);
                user.setNoteName(result.user.Notes);
                user.setIcon(result.user.ICON);
                user.setViplevel(result.user.VipLevel);
                int sex = 0;
                if ("f".equals(result.user.Gender)) {
                    sex = 2;
                } else if ("m".equals(result.user.Gender)) {
                    sex = 1;
                }
                user.setSex(sex);
                user.setAge(result.user.Age);
                CloseAllActivity.getInstance().closeTarget(ChatPersonal.class);
                ChatPersonal.skipToChatPersonal(activity, user);

            }
        });
    }

    /**
     * 更新View
     */
    public void refershData(SkillAttackResult result){
        this.result = result;
        User user = new User();
        user.setIcon(result.user.ICON);
        user.setViplevel(result.user.VipLevel);
        user.setSVip(result.user.VIP);
        user.setUid(result.user.UserID);
        photoView.execute(user);

        SpannableString spName = null;
        if (TextUtils.isEmpty(result.user.NickName) || result.user.NickName == null) {

            if (!TextUtils.isEmpty(result.user.NickName)) {
                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, result.user.NickName,
                        0, null);
            } else {
                spName = FaceManager.getInstance(mContext).parseIconForString(mContext
                        , String.valueOf(result.user.UserID), 0, null);
            }

//            userNameView.setText(spName);
        } else if (!TextUtils.isEmpty(result.user.NickName) || result.user.NickName != null) {
            spName = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, result.user.NickName, 0, null);
//            userNameView.setText(spName);
        }

        String content = "";
        if (result.isHit == 1){
            content = String.format(mContext.getResources().getString(R.string.all_received_skill_content_success),spName, CommonFunction.getNameByLanguage(mContext,result.skillName,"|"));
        }else {
            if (result.skillId == 2){
                content = String.format(mContext.getResources().getString(R.string.all_received_skill_content_success),spName, CommonFunction.getNameByLanguage(mContext,result.skillName,"|"));
            }else{
                content = String.format(mContext.getResources().getString(R.string.all_received_skill_content_error),spName,CommonFunction.getNameByLanguage(mContext,result.skillName,"|"));
            }
        }

        int index[] = new int[2];
        index[0] = content.indexOf(CommonFunction.getNameByLanguage(mContext,result.skillName,"|"));
        index[1] = content.indexOf(spName.toString());
        SpannableStringBuilder style=new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ffff1e")),index[0],index[0]+CommonFunction.getNameByLanguage(mContext,result.skillName,"|").length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ffff1e")),index[1],index[1]+spName.toString().length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        contentView.setText(style);

    }

    /**
     * 创建全局技能攻击View
     * @param time
     */
    public void createView(int time){
        WindowManagerHelper.getInstance().createWindow(mView,time);
    }

    /**
     * 移除全局攻击View
     */
    public void removeView(){
        WindowManagerHelper.getInstance().removeWindow(mView);
    }

}
