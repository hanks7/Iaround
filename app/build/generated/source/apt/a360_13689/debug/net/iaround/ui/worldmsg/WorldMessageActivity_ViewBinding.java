// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.worldmsg;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.lucode.hackware.magicindicator.MagicIndicator;

public class WorldMessageActivity_ViewBinding implements Unbinder {
  private WorldMessageActivity target;

  private View view2131756863;

  @UiThread
  public WorldMessageActivity_ViewBinding(WorldMessageActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WorldMessageActivity_ViewBinding(final WorldMessageActivity target, View source) {
    this.target = target;

    View view;
    target.mMagicIndicator = Utils.findRequiredViewAsType(source, R.id.tb_detailed_indicator, "field 'mMagicIndicator'", MagicIndicator.class);
    target.viewPager = Utils.findRequiredViewAsType(source, R.id.vp_detailed_viewPager, "field 'viewPager'", ViewPager.class);
    view = Utils.findRequiredView(source, R.id.fl_detailed_back, "method 'onBack'");
    view2131756863 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onBack();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    WorldMessageActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mMagicIndicator = null;
    target.viewPager = null;

    view2131756863.setOnClickListener(null);
    view2131756863 = null;
  }
}
