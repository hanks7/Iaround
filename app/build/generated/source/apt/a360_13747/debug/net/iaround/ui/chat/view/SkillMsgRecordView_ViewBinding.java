// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.chat.view;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;

public class SkillMsgRecordView_ViewBinding implements Unbinder {
  private SkillMsgRecordView target;

  private View view2131756651;

  @UiThread
  public SkillMsgRecordView_ViewBinding(SkillMsgRecordView target) {
    this(target, target);
  }

  @UiThread
  public SkillMsgRecordView_ViewBinding(final SkillMsgRecordView target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.head_icon, "field 'headIcon' and method 'onHeadClick'");
    target.headIcon = Utils.castView(view, R.id.head_icon, "field 'headIcon'", HeadPhotoView.class);
    view2131756651 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onHeadClick();
      }
    });
    target.tvName = Utils.findRequiredViewAsType(source, R.id.tvName, "field 'tvName'", TextView.class);
    target.friend_ustitle = Utils.findRequiredViewAsType(source, R.id.friend_ustitle, "field 'friend_ustitle'", UserTitleView.class);
    target.llWealthRank = Utils.findRequiredViewAsType(source, R.id.ll_wealth_rank, "field 'llWealthRank'", LinearLayout.class);
    target.content = Utils.findRequiredViewAsType(source, R.id.content, "field 'content'", RichTextView.class);
    target.ivSkillGif = Utils.findRequiredViewAsType(source, R.id.iv_skill_gif, "field 'ivSkillGif'", ImageView.class);
    target.iv_skill_gif_first = Utils.findRequiredViewAsType(source, R.id.iv_skill_gif_first, "field 'iv_skill_gif_first'", ImageView.class);
    target.iv_skill_bg = Utils.findRequiredViewAsType(source, R.id.iv_skill_bg, "field 'iv_skill_bg'", ImageView.class);
    target.role_ll = Utils.findRequiredViewAsType(source, R.id.role_ll, "field 'role_ll'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillMsgRecordView target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.headIcon = null;
    target.tvName = null;
    target.friend_ustitle = null;
    target.llWealthRank = null;
    target.content = null;
    target.ivSkillGif = null;
    target.iv_skill_gif_first = null;
    target.iv_skill_bg = null;
    target.role_ll = null;

    view2131756651.setOnClickListener(null);
    view2131756651 = null;
  }
}
