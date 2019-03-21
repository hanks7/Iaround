// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.chat.view;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.view.HeadPhotoView;

public class SkillTextRecordView_ViewBinding implements Unbinder {
  private SkillTextRecordView target;

  @UiThread
  public SkillTextRecordView_ViewBinding(SkillTextRecordView target) {
    this(target, target);
  }

  @UiThread
  public SkillTextRecordView_ViewBinding(SkillTextRecordView target, View source) {
    this.target = target;

    target.icon = Utils.findRequiredViewAsType(source, R.id.icon, "field 'icon'", HeadPhotoView.class);
    target.tvName = Utils.findRequiredViewAsType(source, R.id.tvName, "field 'tvName'", TextView.class);
    target.ivSVIP = Utils.findRequiredViewAsType(source, R.id.ivSVIP, "field 'ivSVIP'", ImageView.class);
    target.tvIdentityManager = Utils.findRequiredViewAsType(source, R.id.tvIdentity_manager, "field 'tvIdentityManager'", TextView.class);
    target.tvIdentityOwner = Utils.findRequiredViewAsType(source, R.id.tvIdentity_owner, "field 'tvIdentityOwner'", TextView.class);
    target.llCharmRank = Utils.findRequiredViewAsType(source, R.id.ll_charm_rank, "field 'llCharmRank'", LinearLayout.class);
    target.tvWealthTime = Utils.findRequiredViewAsType(source, R.id.tv_wealth_time, "field 'tvWealthTime'", TextView.class);
    target.tvWealthTop = Utils.findRequiredViewAsType(source, R.id.tv_wealth_top, "field 'tvWealthTop'", TextView.class);
    target.llWealthRank = Utils.findRequiredViewAsType(source, R.id.ll_wealth_rank, "field 'llWealthRank'", LinearLayout.class);
    target.content = Utils.findRequiredViewAsType(source, R.id.content, "field 'content'", TextView.class);
    target.ivSkillIcon = Utils.findRequiredViewAsType(source, R.id.iv_skill_icon, "field 'ivSkillIcon'", ImageView.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", RelativeLayout.class);
    target.contentUserinfoLy = Utils.findRequiredViewAsType(source, R.id.content_userinfo_ly, "field 'contentUserinfoLy'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillTextRecordView target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.icon = null;
    target.tvName = null;
    target.ivSVIP = null;
    target.tvIdentityManager = null;
    target.tvIdentityOwner = null;
    target.llCharmRank = null;
    target.tvWealthTime = null;
    target.tvWealthTop = null;
    target.llWealthRank = null;
    target.content = null;
    target.ivSkillIcon = null;
    target.llContent = null;
    target.contentUserinfoLy = null;
  }
}
