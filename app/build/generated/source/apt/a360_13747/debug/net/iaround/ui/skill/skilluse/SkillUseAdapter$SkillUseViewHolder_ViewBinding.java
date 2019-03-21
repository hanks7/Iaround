// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilluse;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.view.RatingBarView;

public class SkillUseAdapter$SkillUseViewHolder_ViewBinding implements Unbinder {
  private SkillUseAdapter.SkillUseViewHolder target;

  @UiThread
  public SkillUseAdapter$SkillUseViewHolder_ViewBinding(SkillUseAdapter.SkillUseViewHolder target,
      View source) {
    this.target = target;

    target.skillIcon = Utils.findRequiredViewAsType(source, R.id.skill_icon, "field 'skillIcon'", ImageView.class);
    target.iv_skill_ani = Utils.findRequiredViewAsType(source, R.id.iv_skill_ani, "field 'iv_skill_ani'", ImageView.class);
    target.iv_skill_first = Utils.findRequiredViewAsType(source, R.id.iv_skill_first, "field 'iv_skill_first'", ImageView.class);
    target.ratingBarView = Utils.findRequiredViewAsType(source, R.id.ratingBarView, "field 'ratingBarView'", RatingBarView.class);
    target.item_ll = Utils.findRequiredViewAsType(source, R.id.item_ll, "field 'item_ll'", LinearLayout.class);
    target.skill_name = Utils.findRequiredViewAsType(source, R.id.skill_name, "field 'skill_name'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillUseAdapter.SkillUseViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.skillIcon = null;
    target.iv_skill_ani = null;
    target.iv_skill_first = null;
    target.ratingBarView = null;
    target.item_ll = null;
    target.skill_name = null;
  }
}
