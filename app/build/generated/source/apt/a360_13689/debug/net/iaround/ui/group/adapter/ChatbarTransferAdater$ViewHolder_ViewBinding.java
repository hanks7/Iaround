// Generated code from Butter Knife. Do not modify!
package net.iaround.ui.group.adapter;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import net.iaround.R;
import net.iaround.ui.view.HeadPhotoView;

public class ChatbarTransferAdater$ViewHolder_ViewBinding implements Unbinder {
  private ChatbarTransferAdater.ViewHolder target;

  @UiThread
  public ChatbarTransferAdater$ViewHolder_ViewBinding(ChatbarTransferAdater.ViewHolder target,
      View source) {
    this.target = target;

    target.icon = Utils.findRequiredViewAsType(source, R.id.icon, "field 'icon'", HeadPhotoView.class);
    target.checkBox = Utils.findRequiredViewAsType(source, R.id.check_box, "field 'checkBox'", CheckBox.class);
    target.name = Utils.findRequiredViewAsType(source, R.id.name, "field 'name'", TextView.class);
    target.bottomLine = Utils.findRequiredView(source, R.id.bottom_line, "field 'bottomLine'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ChatbarTransferAdater.ViewHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.icon = null;
    target.checkBox = null;
    target.name = null;
    target.bottomLine = null;
  }
}
