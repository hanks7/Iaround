// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.skill.skillmsg;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;

public class SkillMessageFragment_ViewBinding implements Unbinder {
  private SkillMessageFragment target;

  @UiThread
  public SkillMessageFragment_ViewBinding(SkillMessageFragment target, View source) {
    this.target = target;

    target.skillList = Utils.findRequiredViewAsType(source, R.id.skill_list, "field 'skillList'", PullToRefreshListView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SkillMessageFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.skillList = null;
  }
}
