// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skilldetail;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.RatingBarView;
import net.iaround.ui.view.face.MyGridView;

public class SkillDetailFragment_ViewBinding implements Unbinder {
  private SkillDetailFragment target;

  private View view2131757210;

  private View view2131757214;

  private View view2131757219;

  private View view2131757227;

  private View view2131757224;

  private View view2131757148;

  private View view2131757232;

  private View view2131757237;

  private View view2131757217;

  private View view2131755657;

  @UiThread
  public SkillDetailFragment_ViewBinding(final SkillDetailFragment target, View source) {
    this.target = target;

    View view;
    target.tv_title = Utils.findRequiredViewAsType(source, R.id.tv_title, "field 'tv_title'", TextView.class);
    target.iv_user_head = Utils.findRequiredViewAsType(source, R.id.iv_user_head, "field 'iv_user_head'", HeadPhotoView.class);
    target.tv_user_name = Utils.findRequiredViewAsType(source, R.id.tv_user_name, "field 'tv_user_name'", TextView.class);
    target.tv_diamond_num = Utils.findRequiredViewAsType(source, R.id.tv_diamond_num, "field 'tv_diamond_num'", TextView.class);
    target.tv_star_num = Utils.findRequiredViewAsType(source, R.id.tv_gold_num, "field 'tv_star_num'", TextView.class);
    target.tv_gold_num = Utils.findRequiredViewAsType(source, R.id.tv_star_num, "field 'tv_gold_num'", TextView.class);
    target.skill_icon = Utils.findRequiredViewAsType(source, R.id.skill_icon, "field 'skill_icon'", ImageView.class);
    target.skill_icon_first = Utils.findRequiredViewAsType(source, R.id.skill_icon_first, "field 'skill_icon_first'", ImageView.class);
    target.skill_name = Utils.findRequiredViewAsType(source, R.id.skill_name, "field 'skill_name'", TextView.class);
    target.skill_name_icon_below = Utils.findRequiredViewAsType(source, R.id.skill_name_icon_below, "field 'skill_name_icon_below'", TextView.class);
    target.ratingBarView = Utils.findRequiredViewAsType(source, R.id.ratingBarView, "field 'ratingBarView'", RatingBarView.class);
    target.tv_current_effect_desc = Utils.findRequiredViewAsType(source, R.id.tv_current_effect_desc, "field 'tv_current_effect_desc'", TextView.class);
    target.tv_update_effect_desc = Utils.findRequiredViewAsType(source, R.id.tv_update_effect_desc, "field 'tv_update_effect_desc'", TextView.class);
    target.pb_proficiency = Utils.findRequiredViewAsType(source, R.id.pb_proficiency, "field 'pb_proficiency'", ProgressBar.class);
    target.tv_proficiency_desc = Utils.findRequiredViewAsType(source, R.id.tv_proficiency_desc, "field 'tv_proficiency_desc'", TextView.class);
    target.iv_prop = Utils.findRequiredViewAsType(source, R.id.iv_prop, "field 'iv_prop'", ImageView.class);
    target.tv_success = Utils.findRequiredViewAsType(source, R.id.tv_success, "field 'tv_success'", TextView.class);
    target.tv_moon_gold_expend = Utils.findRequiredViewAsType(source, R.id.tv_moon_gold_expend, "field 'tv_moon_gold_expend'", TextView.class);
    target.tv_moon_gold_successful = Utils.findRequiredViewAsType(source, R.id.tv_moon_gold_successful, "field 'tv_moon_gold_successful'", TextView.class);
    target.tv_moon_silver_expend = Utils.findRequiredViewAsType(source, R.id.tv_moon_silver_expend, "field 'tv_moon_silver_expend'", TextView.class);
    target.tv_diamond_charge = Utils.findRequiredViewAsType(source, R.id.tv_diamond_charge, "field 'tv_diamond_charge'", TextView.class);
    target.tv_star_charge = Utils.findRequiredViewAsType(source, R.id.tv_star_charge, "field 'tv_star_charge'", TextView.class);
    target.tv_diamond_successful = Utils.findRequiredViewAsType(source, R.id.tv_diamond_successful, "field 'tv_diamond_successful'", TextView.class);
    target.tv_star_successful = Utils.findRequiredViewAsType(source, R.id.tv_star_successful, "field 'tv_star_successful'", TextView.class);
    target.tv_gold_charge = Utils.findRequiredViewAsType(source, R.id.tv_gold_charge, "field 'tv_gold_charge'", TextView.class);
    target.tv_skill_level = Utils.findRequiredViewAsType(source, R.id.tv_skill_level, "field 'tv_skill_level'", TextView.class);
    target.tv_person_grade = Utils.findRequiredViewAsType(source, R.id.tv_person_grade, "field 'tv_person_grade'", TextView.class);
    view = Utils.findRequiredView(source, R.id.moon_gold_rl, "field 'moon_gold_rl' and method 'onViewClicked'");
    target.moon_gold_rl = Utils.castView(view, R.id.moon_gold_rl, "field 'moon_gold_rl'", RelativeLayout.class);
    view2131757210 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.moon_silver_rl, "field 'moon_silver_rl' and method 'onViewClicked'");
    target.moon_silver_rl = Utils.castView(view, R.id.moon_silver_rl, "field 'moon_silver_rl'", RelativeLayout.class);
    view2131757214 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.diamond_charge_rl, "field 'diamond_charge_rl' and method 'onViewClicked'");
    target.diamond_charge_rl = Utils.castView(view, R.id.diamond_charge_rl, "field 'diamond_charge_rl'", RelativeLayout.class);
    view2131757219 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.star_charge_rl, "field 'star_charge_rl' and method 'onViewClicked'");
    target.star_charge_rl = Utils.castView(view, R.id.star_charge_rl, "field 'star_charge_rl'", RelativeLayout.class);
    view2131757227 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.gold_charge_rl, "field 'gold_charge_rl' and method 'onViewClicked'");
    target.gold_charge_rl = Utils.castView(view, R.id.gold_charge_rl, "field 'gold_charge_rl'", RelativeLayout.class);
    view2131757224 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.root_view_rl = Utils.findRequiredViewAsType(source, R.id.root_view_rl, "field 'root_view_rl'", RelativeLayout.class);
    target.constraintLayout = Utils.findRequiredViewAsType(source, R.id.constraintLayout, "field 'constraintLayout'", ConstraintLayout.class);
    target.seat_line = Utils.findRequiredView(source, R.id.seat_line, "field 'seat_line'");
    target.gv_success_list = Utils.findRequiredViewAsType(source, R.id.gv_success_list, "field 'gv_success_list'", MyGridView.class);
    view = Utils.findRequiredView(source, R.id.btn_charge, "method 'onViewClicked'");
    view2131757148 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_increase, "method 'onViewClicked'");
    view2131757232 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_update, "method 'onViewClicked'");
    view2131757237 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_get_moon, "method 'onViewClicked'");
    view2131757217 = view;
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
    SkillDetailFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tv_title = null;
    target.iv_user_head = null;
    target.tv_user_name = null;
    target.tv_diamond_num = null;
    target.tv_star_num = null;
    target.tv_gold_num = null;
    target.skill_icon = null;
    target.skill_icon_first = null;
    target.skill_name = null;
    target.skill_name_icon_below = null;
    target.ratingBarView = null;
    target.tv_current_effect_desc = null;
    target.tv_update_effect_desc = null;
    target.pb_proficiency = null;
    target.tv_proficiency_desc = null;
    target.iv_prop = null;
    target.tv_success = null;
    target.tv_moon_gold_expend = null;
    target.tv_moon_gold_successful = null;
    target.tv_moon_silver_expend = null;
    target.tv_diamond_charge = null;
    target.tv_star_charge = null;
    target.tv_diamond_successful = null;
    target.tv_star_successful = null;
    target.tv_gold_charge = null;
    target.tv_skill_level = null;
    target.tv_person_grade = null;
    target.moon_gold_rl = null;
    target.moon_silver_rl = null;
    target.diamond_charge_rl = null;
    target.star_charge_rl = null;
    target.gold_charge_rl = null;
    target.root_view_rl = null;
    target.constraintLayout = null;
    target.seat_line = null;
    target.gv_success_list = null;

    view2131757210.setOnClickListener(null);
    view2131757210 = null;
    view2131757214.setOnClickListener(null);
    view2131757214 = null;
    view2131757219.setOnClickListener(null);
    view2131757219 = null;
    view2131757227.setOnClickListener(null);
    view2131757227 = null;
    view2131757224.setOnClickListener(null);
    view2131757224 = null;
    view2131757148.setOnClickListener(null);
    view2131757148 = null;
    view2131757232.setOnClickListener(null);
    view2131757232 = null;
    view2131757237.setOnClickListener(null);
    view2131757237 = null;
    view2131757217.setOnClickListener(null);
    view2131757217 = null;
    view2131755657.setOnClickListener(null);
    view2131755657 = null;
  }
}
