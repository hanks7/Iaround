// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilladdition;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;

public class SkillAddtionAdapter$SkillAddtionViewHolder_ViewBinding implements Unbinder {
  private SkillAddtionAdapter.SkillAddtionViewHolder target;

  @UiThread
  public SkillAddtionAdapter$SkillAddtionViewHolder_ViewBinding(SkillAddtionAdapter.SkillAddtionViewHolder target,
      View source) {
    this.target = target;

    target.tvRank = Utils.findRequiredViewAsType(source, R.id.tv_rank, "field 'tvRank'", TextView.class);
    target.tvGold = Utils.findRequiredViewAsType(source, R.id.tv_gold, "field 'tvGold'", TextView.class);
    target.tvCharm = Utils.findRequiredViewAsType(source, R.id.tv_charm, "field 'tvCharm'", TextView.class);
    target.rootLl = Utils.findRequiredViewAsType(source, R.id.root_ll, "field 'rootLl'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillAddtionAdapter.SkillAddtionViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvRank = null;
    target.tvGold = null;
    target.tvCharm = null;
    target.rootLl = null;
  }
}
