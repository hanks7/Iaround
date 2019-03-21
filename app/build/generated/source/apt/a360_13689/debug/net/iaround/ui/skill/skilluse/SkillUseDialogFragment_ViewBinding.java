// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilluse;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.view.RatingBarView;

public class SkillUseDialogFragment_ViewBinding implements Unbinder {
  private SkillUseDialogFragment target;

  private View view2131756972;

  private View view2131756997;

  private View view2131756987;

  private View view2131756990;

  private View view2131756993;

  private View view2131756995;

  @UiThread
  public SkillUseDialogFragment_ViewBinding(final SkillUseDialogFragment target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.btn_close, "field 'btn_close' and method 'onViewClicked'");
    target.btn_close = Utils.castView(view, R.id.btn_close, "field 'btn_close'", ImageView.class);
    view2131756972 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.skillUseRecyclerView = Utils.findRequiredViewAsType(source, R.id.skill_use_recyclerView, "field 'skillUseRecyclerView'", RecyclerView.class);
    target.progressBarOthers = Utils.findRequiredViewAsType(source, R.id.progressBar_others, "field 'progressBarOthers'", ProgressBar.class);
    target.progressBarSelf = Utils.findRequiredViewAsType(source, R.id.progressBar_self, "field 'progressBarSelf'", ProgressBar.class);
    target.ratingBarView_others = Utils.findRequiredViewAsType(source, R.id.ratingBarView_others, "field 'ratingBarView_others'", RatingBarView.class);
    target.ratingBarView_self = Utils.findRequiredViewAsType(source, R.id.ratingBarView_self, "field 'ratingBarView_self'", RatingBarView.class);
    target.tv_skill_effect = Utils.findRequiredViewAsType(source, R.id.tv_skill_effect, "field 'tv_skill_effect'", TextView.class);
    target.iv_prop = Utils.findRequiredViewAsType(source, R.id.iv_prop, "field 'iv_prop'", ImageView.class);
    target.tv_moon_expend = Utils.findRequiredViewAsType(source, R.id.tv_moon_expend, "field 'tv_moon_expend'", TextView.class);
    target.tv_gold_expend = Utils.findRequiredViewAsType(source, R.id.tv_gold_expend, "field 'tv_gold_expend'", TextView.class);
    target.tv_diamond_expend = Utils.findRequiredViewAsType(source, R.id.tv_diamond_expend, "field 'tv_diamond_expend'", TextView.class);
    target.tv_star_expend = Utils.findRequiredViewAsType(source, R.id.tv_star_expend, "field 'tv_star_expend'", TextView.class);
    target.tv_hit_rate = Utils.findRequiredViewAsType(source, R.id.tv_hit_rate, "field 'tv_hit_rate'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btn_use_skill, "field 'btn_use_skill' and method 'onViewClicked'");
    target.btn_use_skill = Utils.castView(view, R.id.btn_use_skill, "field 'btn_use_skill'", Button.class);
    view2131756997 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.skill_use_ll = Utils.findRequiredViewAsType(source, R.id.skill_use_ll, "field 'skill_use_ll'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.props_rl, "field 'props_rl' and method 'onViewClicked'");
    target.props_rl = Utils.castView(view, R.id.props_rl, "field 'props_rl'", RelativeLayout.class);
    view2131756987 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.gold_rl, "field 'gold_rl' and method 'onViewClicked'");
    target.gold_rl = Utils.castView(view, R.id.gold_rl, "field 'gold_rl'", RelativeLayout.class);
    view2131756990 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.diamond_rl, "field 'diamond_rl' and method 'onViewClicked'");
    target.diamond_rl = Utils.castView(view, R.id.diamond_rl, "field 'diamond_rl'", RelativeLayout.class);
    view2131756993 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.star_rl, "field 'star_rl' and method 'onViewClicked'");
    target.star_rl = Utils.castView(view, R.id.star_rl, "field 'star_rl'", RelativeLayout.class);
    view2131756995 = view;
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
    SkillUseDialogFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_close = null;
    target.skillUseRecyclerView = null;
    target.progressBarOthers = null;
    target.progressBarSelf = null;
    target.ratingBarView_others = null;
    target.ratingBarView_self = null;
    target.tv_skill_effect = null;
    target.iv_prop = null;
    target.tv_moon_expend = null;
    target.tv_gold_expend = null;
    target.tv_diamond_expend = null;
    target.tv_star_expend = null;
    target.tv_hit_rate = null;
    target.btn_use_skill = null;
    target.skill_use_ll = null;
    target.props_rl = null;
    target.gold_rl = null;
    target.diamond_rl = null;
    target.star_rl = null;

    view2131756972.setOnClickListener(null);
    view2131756972 = null;
    view2131756997.setOnClickListener(null);
    view2131756997 = null;
    view2131756987.setOnClickListener(null);
    view2131756987 = null;
    view2131756990.setOnClickListener(null);
    view2131756990 = null;
    view2131756993.setOnClickListener(null);
    view2131756993 = null;
    view2131756995.setOnClickListener(null);
    view2131756995 = null;
  }
}
