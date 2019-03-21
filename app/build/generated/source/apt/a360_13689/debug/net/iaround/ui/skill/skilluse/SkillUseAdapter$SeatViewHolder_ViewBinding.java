// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilluse;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;

public class SkillUseAdapter$SeatViewHolder_ViewBinding implements Unbinder {
  private SkillUseAdapter.SeatViewHolder target;

  @UiThread
  public SkillUseAdapter$SeatViewHolder_ViewBinding(SkillUseAdapter.SeatViewHolder target,
      View source) {
    this.target = target;

    target.skillIcon = Utils.findRequiredViewAsType(source, R.id.skill_icon, "field 'skillIcon'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillUseAdapter.SeatViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.skillIcon = null;
  }
}
