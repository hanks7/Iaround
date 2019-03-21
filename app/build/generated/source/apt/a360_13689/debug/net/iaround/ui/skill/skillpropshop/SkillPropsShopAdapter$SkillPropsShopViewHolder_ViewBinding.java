// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skillpropshop;

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

public class SkillPropsShopAdapter$SkillPropsShopViewHolder_ViewBinding implements Unbinder {
  private SkillPropsShopAdapter.SkillPropsShopViewHolder target;

  @UiThread
  public SkillPropsShopAdapter$SkillPropsShopViewHolder_ViewBinding(SkillPropsShopAdapter.SkillPropsShopViewHolder target,
      View source) {
    this.target = target;

    target.tvPropsName = Utils.findRequiredViewAsType(source, R.id.tv_props_name, "field 'tvPropsName'", TextView.class);
    target.ivProps = Utils.findRequiredViewAsType(source, R.id.iv_props, "field 'ivProps'", ImageView.class);
    target.tvPropsNum = Utils.findRequiredViewAsType(source, R.id.tv_props_num, "field 'tvPropsNum'", TextView.class);
    target.tvUpdateSuccessful = Utils.findRequiredViewAsType(source, R.id.tv_update_successful, "field 'tvUpdateSuccessful'", TextView.class);
    target.tvExpend = Utils.findRequiredViewAsType(source, R.id.tv_expend, "field 'tvExpend'", TextView.class);
    target.item_ll = Utils.findRequiredViewAsType(source, R.id.item_ll, "field 'item_ll'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillPropsShopAdapter.SkillPropsShopViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tvPropsName = null;
    target.ivProps = null;
    target.tvPropsNum = null;
    target.tvUpdateSuccessful = null;
    target.tvExpend = null;
    target.item_ll = null;
  }
}
