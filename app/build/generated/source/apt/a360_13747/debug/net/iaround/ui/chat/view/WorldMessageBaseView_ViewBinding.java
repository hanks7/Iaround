// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.chat.view;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;

public class WorldMessageBaseView_ViewBinding implements Unbinder {
  private WorldMessageBaseView target;

  private View view2131756651;

  private View view2131756656;

  @UiThread
  public WorldMessageBaseView_ViewBinding(WorldMessageBaseView target) {
    this(target, target);
  }

  @UiThread
  public WorldMessageBaseView_ViewBinding(final WorldMessageBaseView target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.head_icon, "field 'headIcon', method 'onViewClicked', and method 'onLongClick'");
    target.headIcon = Utils.castView(view, R.id.head_icon, "field 'headIcon'", HeadPhotoView.class);
    view2131756651 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View p0) {
        return target.onLongClick();
      }
    });
    target.tvName = Utils.findRequiredViewAsType(source, R.id.tvName, "field 'tvName'", TextView.class);
    target.role_ll = Utils.findRequiredViewAsType(source, R.id.role_ll, "field 'role_ll'", LinearLayout.class);
    target.llWealthRank = Utils.findRequiredViewAsType(source, R.id.ll_wealth_rank, "field 'llWealthRank'", LinearLayout.class);
    target.friend_ustitle = Utils.findRequiredViewAsType(source, R.id.friend_ustitle, "field 'friend_ustitle'", UserTitleView.class);
    target.content = Utils.findRequiredViewAsType(source, R.id.content, "field 'content'", RichTextView.class);
    target.ivSkillGif = Utils.findRequiredViewAsType(source, R.id.iv_skill_gif, "field 'ivSkillGif'", ImageView.class);
    target.iv_skill_gif_first = Utils.findRequiredViewAsType(source, R.id.iv_skill_gif_first, "field 'iv_skill_gif_first'", ImageView.class);
    target.iv_skill_bg = Utils.findRequiredViewAsType(source, R.id.iv_skill_bg, "field 'iv_skill_bg'", ImageView.class);
    view = Utils.findRequiredView(source, R.id.btn_go_in, "field 'btnGoIn' and method 'onViewClicked'");
    target.btnGoIn = Utils.castView(view, R.id.btn_go_in, "field 'btnGoIn'", Button.class);
    view2131756656 = view;
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
    WorldMessageBaseView target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.headIcon = null;
    target.tvName = null;
    target.role_ll = null;
    target.llWealthRank = null;
    target.friend_ustitle = null;
    target.content = null;
    target.ivSkillGif = null;
    target.iv_skill_gif_first = null;
    target.iv_skill_bg = null;
    target.btnGoIn = null;

    view2131756651.setOnClickListener(null);
    view2131756651.setOnLongClickListener(null);
    view2131756651 = null;
    view2131756656.setOnClickListener(null);
    view2131756656 = null;
  }
}
