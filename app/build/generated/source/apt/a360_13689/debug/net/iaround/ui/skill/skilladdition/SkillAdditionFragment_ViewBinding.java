// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilladdition;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;

public class SkillAdditionFragment_ViewBinding implements Unbinder {
  private SkillAdditionFragment target;

  private View view2131755657;

  @UiThread
  public SkillAdditionFragment_ViewBinding(final SkillAdditionFragment target, View source) {
    this.target = target;

    View view;
    target.tvTitle = Utils.findRequiredViewAsType(source, R.id.tv_title, "field 'tvTitle'", TextView.class);
    target.tvGoldDesc = Utils.findRequiredViewAsType(source, R.id.tv_gold_desc, "field 'tvGoldDesc'", TextView.class);
    target.tvExpendDesc = Utils.findRequiredViewAsType(source, R.id.tv_expend_desc, "field 'tvExpendDesc'", TextView.class);
    target.additionRecyclerView = Utils.findRequiredViewAsType(source, R.id.addition_recyclerView, "field 'additionRecyclerView'", RecyclerView.class);
    target.successLayout = Utils.findRequiredViewAsType(source, R.id.ly_success_rank, "field 'successLayout'", LinearLayout.class);
    target.additionSuccessRecyclerView = Utils.findRequiredViewAsType(source, R.id.addition_success_recyclerView, "field 'additionSuccessRecyclerView'", RecyclerView.class);
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
    SkillAdditionFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvTitle = null;
    target.tvGoldDesc = null;
    target.tvExpendDesc = null;
    target.additionRecyclerView = null;
    target.successLayout = null;
    target.additionSuccessRecyclerView = null;

    view2131755657.setOnClickListener(null);
    view2131755657 = null;
  }
}
