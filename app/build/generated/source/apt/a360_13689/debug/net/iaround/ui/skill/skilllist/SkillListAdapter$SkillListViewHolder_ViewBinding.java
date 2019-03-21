// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilllist;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.zhy.android.percent.support.PercentRelativeLayout;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.view.RatingBarView;

public class SkillListAdapter$SkillListViewHolder_ViewBinding implements Unbinder {
  private SkillListAdapter.SkillListViewHolder target;

  @UiThread
  public SkillListAdapter$SkillListViewHolder_ViewBinding(SkillListAdapter.SkillListViewHolder target,
      View source) {
    this.target = target;

    target.skill_icon = Utils.findRequiredViewAsType(source, R.id.skill_icon, "field 'skill_icon'", ImageView.class);
    target.iv_skill_first = Utils.findRequiredViewAsType(source, R.id.iv_skill_first, "field 'iv_skill_first'", ImageView.class);
    target.skill_name = Utils.findRequiredViewAsType(source, R.id.skill_name, "field 'skill_name'", TextView.class);
    target.skill_name_icon_below = Utils.findRequiredViewAsType(source, R.id.skill_name_icon_below, "field 'skill_name_icon_below'", TextView.class);
    target.ratingBarView = Utils.findRequiredViewAsType(source, R.id.ratingBarView, "field 'ratingBarView'", RatingBarView.class);
    target.skill_desc = Utils.findRequiredViewAsType(source, R.id.skill_desc, "field 'skill_desc'", TextView.class);
    target.tv_open_desc = Utils.findRequiredViewAsType(source, R.id.tv_open_desc, "field 'tv_open_desc'", TextView.class);
    target.btn_update = Utils.findRequiredViewAsType(source, R.id.btn_update, "field 'btn_update'", Button.class);
    target.ctl = Utils.findRequiredViewAsType(source, R.id.ctl, "field 'ctl'", PercentRelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillListAdapter.SkillListViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.skill_icon = null;
    target.iv_skill_first = null;
    target.skill_name = null;
    target.skill_name_icon_below = null;
    target.ratingBarView = null;
    target.skill_desc = null;
    target.tv_open_desc = null;
    target.btn_update = null;
    target.ctl = null;
  }
}
