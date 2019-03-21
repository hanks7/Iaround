// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.worldmsg;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;

public class WorldMessageFragment_ViewBinding implements Unbinder {
  private WorldMessageFragment target;

  private View view2131756118;

  @UiThread
  public WorldMessageFragment_ViewBinding(final WorldMessageFragment target, View source) {
    this.target = target;

    View view;
    target.message_list = Utils.findRequiredViewAsType(source, R.id.message_list, "field 'message_list'", PullToRefreshListView.class);
    target.editContent = Utils.findRequiredViewAsType(source, R.id.editContent, "field 'editContent'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnSend, "field 'btnSend' and method 'onViewClicked'");
    target.btnSend = Utils.castView(view, R.id.btnSend, "field 'btnSend'", TextView.class);
    view2131756118 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.rootLayout = Utils.findRequiredViewAsType(source, R.id.rootLayout, "field 'rootLayout'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WorldMessageFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.message_list = null;
    target.editContent = null;
    target.btnSend = null;
    target.rootLayout = null;

    view2131756118.setOnClickListener(null);
    view2131756118 = null;
  }
}
