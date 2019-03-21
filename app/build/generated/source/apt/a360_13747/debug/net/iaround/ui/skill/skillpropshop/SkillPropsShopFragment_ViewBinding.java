// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skillpropshop;

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

public class SkillPropsShopFragment_ViewBinding implements Unbinder {
  private SkillPropsShopFragment target;

  private View view2131757148;

  private View view2131755657;

  @UiThread
  public SkillPropsShopFragment_ViewBinding(final SkillPropsShopFragment target, View source) {
    this.target = target;

    View view;
    target.moon_recyclerView = Utils.findRequiredViewAsType(source, R.id.moon_recyclerView, "field 'moon_recyclerView'", RecyclerView.class);
    target.tv_mime_diamond = Utils.findRequiredViewAsType(source, R.id.tv_mime_diamond, "field 'tv_mime_diamond'", TextView.class);
    target.tv_mime_gold = Utils.findRequiredViewAsType(source, R.id.tv_mime_gold, "field 'tv_mime_gold'", TextView.class);
    target.tv_mime_star = Utils.findRequiredViewAsType(source, R.id.tv_mime_star, "field 'tv_mime_star'", TextView.class);
    target.tv_title = Utils.findRequiredViewAsType(source, R.id.tv_title, "field 'tv_title'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btn_charge, "method 'onViewClicked'");
    view2131757148 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
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
    SkillPropsShopFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.moon_recyclerView = null;
    target.tv_mime_diamond = null;
    target.tv_mime_gold = null;
    target.tv_mime_star = null;
    target.tv_title = null;

    view2131757148.setOnClickListener(null);
    view2131757148 = null;
    view2131755657.setOnClickListener(null);
    view2131755657 = null;
  }
}
