package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.*;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.FaceManager;

import java.math.BigDecimal;

/**
 * Class: 收到的技能
 * Author：gh
 * Date: 2017/8/22 11:17
 * Email：jt_gaohang@163.com
 */
public class ReceivedSkillDialog extends Dialog implements View.OnClickListener,DialogInterface.OnKeyListener {

    private Context context;

    private SureClickListener onClickListener;

    private ImageView iconIv;
    private ImageView aniIv;
    private TextView contentlTv;

    private SkillAttackResult skillAttackResult;  // 临时技能缓存

    private long targetUserId; // 攻击者ID

    private static ReceivedSkillDialog instance = null;

    private int skillId; //当前技能ID

    public static ReceivedSkillDialog getInstance(Context context, SkillAttackResult skillAttackResult, SureClickListener onClickListener) {
        if (instance == null) {
            instance = new ReceivedSkillDialog(context, skillAttackResult, onClickListener);
        }
        return instance;
    }

    public ReceivedSkillDialog(Context context,SkillAttackResult skillAttackResult,SureClickListener onClickListener) {
        // 更改样式,把背景设置为透明的
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        this.skillAttackResult = skillAttackResult;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载dialog的布局
        setContentView(R.layout.dialog_received_skill);
        //拿到布局控件进行处理

        iconIv = (ImageView) findViewById(R.id.iv_dialog_received_skill_icon);
        aniIv = (ImageView) findViewById(R.id.iv_dialog_received_skill_icon_ani);
        contentlTv = (TextView) findViewById(R.id.edt_dialog_received_skill_content);
        TextView refuseTv = (TextView) findViewById(R.id.tv_dialog_received_skill_convince);
        TextView agreeTv = (TextView) findViewById(R.id.tv_dialog_received_skill_not);

        refershView(context,skillAttackResult,onClickListener);
        agreeTv.setOnClickListener(this);
        refuseTv.setOnClickListener(this);
        //初始化布局的位置
        initLayoutParams();

    }

    /**
     * 刷新技能攻击View
     * @param skillAttackResult
     */
    public void refershView(Context context,SkillAttackResult skillAttackResult,SureClickListener onClickListener){
        if (skillId == 4)return;
        this.context = context;
        this.onClickListener = onClickListener;
        this.targetUserId = skillAttackResult.user.UserID;
        this.skillId = skillAttackResult.skillId;

        if (skillAttackResult.skillId == 1){
            iconIv.setBackgroundResource(R.drawable.icon_skill_top_1);
        }else if (skillAttackResult.skillId == 2){
            iconIv.setBackgroundResource(R.drawable.icon_skill_top_2);
        }else if (skillAttackResult.skillId == 3){
            iconIv.setBackgroundResource(R.drawable.icon_skill_top_3);
        }else if (skillAttackResult.skillId == 4){
            iconIv.setBackgroundResource(R.drawable.icon_skill_top_4);
        }else if (skillAttackResult.skillId == 5){
            iconIv.setBackgroundResource(R.drawable.icon_skill_top_5);
        }
//        GlideUtil.loadImageNoCache(context,skillAttackResult.skillIcon,iconIv);
//        iconIv.setImageResource(R.drawable.icon_skill_magic_spell);
        playFrame(skillAttackResult.gif,aniIv);

        SpannableString name = FaceManager.getInstance(getContext()).parseIconForString(getContext(), skillAttackResult.user.NickName,
                0, null);
        int time = 0;
        String content = "";
        switch (skillAttackResult.skillId){
            case 1:
                content = String.format(context.getString(R.string.dialog_received_skill_content_1),name,"" + skillAttackResult.charm);
                break;
            case 2:
                return;
            case 3:
                if (skillAttackResult.affectTime > 60){
                    time = new BigDecimal( skillAttackResult.affectTime / 60 ).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                }else {
                    time = 1;
                }
                if (skillAttackResult.isHit == 1){
                    content = String.format(context.getString(R.string.dialog_received_skill_content_3_success),name,""+time);
                }else {
                    content = String.format(context.getString(R.string.dialog_received_skill_content_3_error),name);
                }
                break;
            case 4:
                if (skillAttackResult.affectTime > 60){
                    time = new BigDecimal( skillAttackResult.affectTime / 60 ).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                }else {
                    time = 1;
                }
                if (skillAttackResult.isHit == 1){
                    content = String.format(context.getString(R.string.dialog_received_skill_content_4_success),name,""+time);
                }else {
                    content = String.format(context.getString(R.string.dialog_received_skill_content_4_error),name);
                }
                break;
            case 5:
                if (skillAttackResult.isHit == 1){
                    content = String.format(context.getString(R.string.dialog_received_skill_content_5_success),name,"" + skillAttackResult.affectGoldNum);
                }else {
                    content = String.format(context.getString(R.string.dialog_received_skill_content_5_error),name);
                }
                break;

        }
        contentlTv.setText(Html.fromHtml(content));
    }

    public void playFrame(String skillName, final ImageView iv_frame){
        if (skillName != null){
            if (!TextUtils.isEmpty(skillName) & skillName.contains("_")){
                String[] split = skillName.split("_");
                skillName = "frame_" + split[0];
                int drawableId = context.getResources().getIdentifier(skillName, "drawable", context.getPackageName());
                AnimationDrawable frameAnim = (AnimationDrawable) context.getResources().getDrawable(drawableId);
                // 是否播放一次
                frameAnim.setOneShot(false);
                iv_frame.setBackgroundDrawable(frameAnim);
                frameAnim.start();
            }
        }

    }

    // 初始化布局的参数
    private void initLayoutParams() {
        // 布局的参数
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1f;
        setCanceledOnTouchOutside(false);
        setOnKeyListener(this);//点击系统返回键不允许消失
        getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_dialog_received_skill_convince:
                if (onClickListener != null){
                    onClickListener.onSure();
                }
                this.dismiss();
                break;
            case R.id.tv_dialog_received_skill_not:
                if (onClickListener != null){
                    onClickListener.onCancel();
                }
                if(skillId != 4){
                    this.dismiss();
                }
                break;
        }
    }

    public interface SureClickListener{

        void onSure();
        void onCancel();
    }

    public SkillAttackResult getSkillAttackResult() {
        return skillAttackResult;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public int getSkillId() {
        return skillId;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
    }
}
