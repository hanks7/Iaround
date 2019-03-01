
package net.iaround.tools;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import net.iaround.R;


public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    private CustomProgressDialog customProgressDialog = null;

    public static CustomProgressDialog createDialog(Context context) {
        CustomProgressDialog
                customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.common_progressdialog);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        customProgressDialog.customProgressDialog = customProgressDialog;
        return customProgressDialog;
    }

    public static CustomProgressDialog createDialog(Context context, int styleRes, int layoutRes) {
        CustomProgressDialog
                customProgressDialog = new CustomProgressDialog(context,
                styleRes);
        customProgressDialog.setContentView(layoutRes);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        customProgressDialog.customProgressDialog = customProgressDialog;
        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (customProgressDialog == null) {
            return;
        }
//		ProgressBar imageView = ( ProgressBar ) customProgressDialog
//				.findView( R.id.loadingImageView );
//		AnimationDrawable animationDrawable = ( AnimationDrawable ) imageView.getBackground( );
//		animationDrawable.start( );

    }

    public CustomProgressDialog setTitile(String strTitle) {
        return customProgressDialog;
    }

    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.textview);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
        return customProgressDialog;
    }

    public void dismiss() {
        try {
            if (customProgressDialog != null
                    && customProgressDialog.isShowing()) {
                super.dismiss();
            }
        } catch (Throwable t) {
            CommonFunction.log(t);
        }
    }
}
