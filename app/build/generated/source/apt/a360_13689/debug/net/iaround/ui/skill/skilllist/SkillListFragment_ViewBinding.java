// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilllist;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;

public class SkillListFragment_ViewBinding implements Unbinder {
  private SkillListFragment target;

  private View view2131755657;

  @UiThread
  public SkillListFragment_ViewBinding(final SkillListFragment target, View source) {
    this.target = target;

    View view;
    target.skill_recyclerView = Utils.findRequiredViewAsType(source, R.id.skill_recyclerView, "field 'skill_recyclerView'", RecyclerView.class);
    target.tv_title = Utils.findRequiredViewAsType(source, R.id.tv_title, "field 'tv_title'", TextView.class);
    view = Utils.findRequiredView(source, R.id.fl_back, "method 'onViewClicked'");
    view2131755657 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.skill_recyclerView = null;
    target.tv_title = null;

    view2131755657.setOnClickListener(null);
    view2131755657 = null;
  }
}
