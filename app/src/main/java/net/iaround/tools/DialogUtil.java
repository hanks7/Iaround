
package net.iaround.tools;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.DataTag;
import net.iaround.conf.ErrorCode;
import net.iaround.model.skill.SkillUpdateEntity;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.activity.UserVipOpenActivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.UpDownTwoButtomDialog;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.view.dialog.CustomAdDialog;
import net.iaround.ui.view.dialog.EditUserSexDialog;
import net.iaround.ui.view.dialog.ReceivedCallDialog;
import net.iaround.ui.view.dialog.ReceivedSkillDialog;
import net.iaround.ui.view.dialog.RechargeLoveDialog;
import net.iaround.ui.view.dialog.RecruitAndCallDialog;
import net.iaround.ui.view.dialog.SkillUpdateDailog;

import java.util.ArrayList;

/**
 * 对话框工具类
 *
 * @author 余勋杰
 */
public final class DialogUtil {

    /**
     * 默认成为VIP的提示框
     */
    public static Dialog showTobeVipDialog(final Context context, int title, int message) {
        return showTobeVipDialog(context, title, message, -1, DataTag.UNKONW);
    }

    public static Dialog showTobeVipDialog(final Context context, int title, int message, final String fromTag) {
        return showTobeVipDialog(context, title, message, -1, fromTag);
    }

    /**
     * 带事件统计的成为vip的提示框
     */
    public static Dialog showTobeVipDialog(final Context context, int title, int message, int tobe, final String fromTag) {
        View.OnClickListener getVipOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UserVipOpenActivity.class);
                context.startActivity(intent);
            }
        };
        try {
            int toBe = tobe > 0 ? tobe : R.string.tost_filter_vip_recharge;
            CustomDialog dialog = new CustomDialog(context, context.getString(title),
                    context.getString(message),
                    context.getString(R.string.tost_filter_vip_cancel), null,
                    context.getString(toBe), getVipOnClickListener);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 默认成为VIP的提示框
     */
    public static Dialog showTobeVipDialog(final Context context, int title, int message, View.OnClickListener getVipOnClickListener, View.OnClickListener cancelOnClickListener) {
        return showTobeVipDialog(context, title, message, -1, DataTag.UNKONW, getVipOnClickListener, cancelOnClickListener);
    }

    /**
     * 带事件统计的成为vip的提示框
     */
    public static Dialog showTobeVipDialog(final Context context, int title, int message, int tobe, final String fromTag, View.OnClickListener getVipOnClickListener, View.OnClickListener cancelOnClickListener) {
        try {
            int toBe = tobe > 0 ? tobe : R.string.tost_filter_vip_recharge;
            CustomDialog dialog = new CustomDialog(context, context.getString(title),
                    context.getString(message),
                    context.getString(R.string.tost_filter_vip_cancel), cancelOnClickListener,
                    context.getString(toBe), getVipOnClickListener);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    /**
     * 获取钻石对话框
     */
    public static void showDiamondDialog(final Activity activity, final String fromTag) {
        showTwoButtonDialog(activity, ErrorCode.getErrorMessageId(ErrorCode.E_5930),
                activity.getString(R.string.diamond_not_enough),
                activity.getString(R.string.cancel),
                activity.getString(R.string.to_get_diamond), null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// 前往钻石购买界面
//						FragmentPayDiamond.jumpPayDiamondActivity( activity , fromTag );
                        // TODO: 2017/5/2 暂时跳转到我的钱包界面  yuchao
                        Intent intent = new Intent();
                        intent.setClass(activity, MyWalletActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                });
    }

    /**
     * 获取钻石对话框
     */
    public static void showDiamondDialog(final Activity activity) {
        showDiamondDialog(activity, DataTag.UNKONW);
    }

    public static Dialog showOKCancelDialog(Context context, int title, int message,
                                            View.OnClickListener listener) {
        return showOKCancelDialog(context, context.getString(title),
                context.getString(message), listener, null);
    }

    public static Dialog showOKCancelDialog(Context context, String title, String message, boolean isTransferChatbar,
                                            View.OnClickListener listener) {
        return showTwoButtonDialog(context, title, message, isTransferChatbar,
                context.getString(R.string.ok), context.getString(R.string.cancel), listener, null);
    }

    public static Dialog showOKCancelDialog(Context context, String title, String message,
                                            View.OnClickListener listener) {
        return showTwoButtonDialog(context, title, message,
                context.getString(R.string.ok), context.getString(R.string.cancel), listener, null
        );
    }

    public static Dialog showOKCancelDialog(Context context, String title, String message, int okStringid,
                                            View.OnClickListener listener) {
        return showTwoButtonDialog(context, title, message,
                context.getString(okStringid), context.getString(R.string.cancel), listener, null
        );
    }

    /**
     * 有确定和取消的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener1
     * @return
     */
    public static Dialog showOKCancelDialog(Context context, String title, String message,
                                            View.OnClickListener listener1, View.OnClickListener listener2) {
        return showTwoButtonDialog(context, title, message,
                context.getString(R.string.ok), context.getString(R.string.cancel),
                listener1, listener2);
    }

    /**
     * 滚动条对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showProgressDialog(Context context, int title, int message,
                                            DialogInterface.OnCancelListener listener) {
        return showProgressDialog(context, context.getString(title),
                context.getString(message), listener);
    }

    /**
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return Dialog
     * @Title: showProgressDialog
     * @Description: 仅获取加载对话框，且直接显示
     */
    public static Dialog showProgressDialog(Context context, String title, String message,
                                            DialogInterface.OnCancelListener listener) {
        try {
            CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
            dialog.setMessage(context.getString(R.string.please_wait));
            dialog.setOnCancelListener(listener);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Dialog showTwoButtonDialog(Context context, String title, String message,
                                             String button1, String button2, View.OnClickListener listerner1,
                                             View.OnClickListener listener2) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, button1,
                    listerner1, button2, listener2);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 没有标题的Dialog
     *
     * @param context
     * @param message
     * @param button1
     * @param button2
     * @param listerner1
     * @return
     */
    public static Dialog showTwoButtonDialog(Context context, String message,
                                             String button1, String button2, View.OnClickListener listerner1) {
        try {
            CustomDialog dialog = new CustomDialog(context, message, button1,
                    listerner1, button2);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Dialog showTwoButtonDialog(Context context, String title, String message, boolean isTransferChatbr,
                                             String button1, String button2, View.OnClickListener listerner1,
                                             View.OnClickListener listener2) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, isTransferChatbr, button1,
                    listerner1, button2, listener2);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 只有确定按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showOKDialog(Context context, int title, int message,
                                      View.OnClickListener listener) {
        return showOKDialog(context, context.getString(title), context.getString(message),
                listener);
    }

    /**
     * 只有确定按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showOKDialog(Context context, CharSequence title, CharSequence message,
                                      View.OnClickListener listener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message,
                    context.getString(R.string.ok), listener);
            dialog.show();

            dialog.setCancelable(false);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 只有确定按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static CustomDialog showOKDialog(Context context, CharSequence title, CharSequence message,
                                            int layoutRes, View.OnClickListener listener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, layoutRes,
                    context.getString(R.string.ok), listener, "", null);
            dialog.show();

            dialog.setCancelable(false);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 滚动条对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog getProgressDialog(Context context, String title, String message,
                                           DialogInterface.OnCancelListener listener) {
        try {
            CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
            dialog.setMessage(message);
            dialog.setOnCancelListener(listener);

            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 有一个按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param btnStr
     * @param listener
     * @return
     */
    public static Dialog showOneButtonDialog(Context context, String title, String message,
                                             String btnStr, View.OnClickListener listener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, btnStr, listener);
            dialog.show();
            dialog.setCancelable(false);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 有两个按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param btnStr1
     * @param btnStr2
     * @return
     */
    public static Dialog showTowButtonDialog(Context context, CharSequence title,
                                             CharSequence message, CharSequence btnStr1, CharSequence btnStr2,
                                             View.OnClickListener listener1, View.OnClickListener listener2) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, btnStr1,
                    listener1, btnStr2, listener2);
            /*
             * AlertDialog dialog = new AlertDialog.Builder(context).create();
             * dialog.setIcon(android.R.drawable.ic_dialog_info);
             * dialog.setTitle(title); dialog.setMessage(message);
             * dialog.setButton(btnStr1, listener1); dialog.setButton2(btnStr2,
             * listener2);
             */
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 对话框
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static Dialog showDialog(Context context, CharSequence title, CharSequence message) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message);
            dialog.show();

            dialog.setCancelable(false);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showDialog(Context context, int title, int message,
                                    View.OnClickListener listener) {
        return showOKDialog(context, context.getString(title), context.getString(message),
                listener);
    }

    /**
     * 有两个按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param btnStr1
     * @param btnStr2
     * @param listener
     * @return
     */
    public static Dialog showTowButtonDialog(Context context, CharSequence title,
                                             CharSequence message, CharSequence btnStr1, CharSequence btnStr2,
                                             View.OnClickListener listener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, btnStr1, listener,
                    btnStr2, null);

            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 有两个按钮的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param btnStr1
     * @param btnStr2
     * @param listener
     * @return
     */
    public static Dialog showTwoButtonDialog(Context context, int title, int message,
                                             int btnStr1, int btnStr2, View.OnClickListener listener) {
        return showTowButtonDialog(context, context.getString(title),
                context.getString(message), context.getString(btnStr1),
                context.getString(btnStr2), listener);
    }

    public static Dialog showTwoButtonDialog(Context context, String title, String message,
                                             String button1, String button2, View.OnClickListener listener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, button1, listener,
                    button2, null);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Dialog showOKCancelDialogFlatUpdate(Context context, String title, String message,
                                                      View.OnClickListener listener1, View.OnClickListener listener2) {
        return showTwoButtonDialog(context, title, message,
                context.getString(R.string.cancel), context.getString(R.string.group_chat_flat_update_current),
                listener2, listener1);
    }

    /**
     * 有确定和取消的对话框
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static Dialog showOKCancelDialog(Context context, int title, int message,
                                            View.OnClickListener listener1, View.OnClickListener listener2) {
        return showOKCancelDialog(context, context.getString(title),
                context.getString(message), listener1, listener2);
    }

    /**
     * 错误消息对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showErrorDialog(Context context, String title, String message,
                                         View.OnClickListener listener) {
        Dialog dialog = showOKDialog(context, title, message, listener);
        return dialog;
    }

    /**
     * 错误消息对话框
     *
     * @param context
     * @param title
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showErrorDialog(Context context, int title, int message,
                                         View.OnClickListener listener) {
        return showErrorDialog(context, context.getString(title),
                context.getString(message), listener);
    }

    /**
     * 自定义内容的对话框
     *
     * @param context
     * @param title
     * @param content
     * @return
     */
    public static Dialog showCustomDialog(Context context, String title, View content) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setTitle(title);
            dialog.setContentView(content);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 自定义内容的对话框 可控制大小
     *
     * @param context
     * @param title
     * @param content
     * @return
     */
    public static Dialog showDialog(Context context, String title, View content) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setTitle(title);
            dialog.setContentView(content);
            Window window = dialog.getWindow();
            WindowManager manager = ((Activity) context).getWindowManager();
            Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高用
            WindowManager.LayoutParams lp = window.getAttributes();
            // lp.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
            // lp.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.95
            window.setAttributes(lp);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 自定义内容的对话框
     *
     * @param context
     * @param title
     * @param content
     * @return
     */
    public static Dialog showCustomDialog(Context context, int title, View content) {
        return showCustomDialog(context, context.getString(title), content);
    }

    /**
     * 是否取消上传的对话框
     *
     * @param context
     * @param title
     * @param message
     * @param okListener
     * @return
     */
    public static Dialog showCancelUploadDialog(Context context, String title, String message,
                                                View.OnClickListener okListener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message,
                    context.getString(R.string.cancel), null, context.getString(R.string.ok),
                    okListener);
            dialog.show();

            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 显示单选对话框
     */
    public static Dialog showSingleChoiceDialog(final Context context, String title,
                                                final String[] choices, final int selected,
                                                final DialogInterface.OnClickListener clListener) {
        // 初始化view
        ListView lvDialog = new ListView(context);
        lvDialog.setCacheColorHint(0);
        lvDialog.setBackgroundResource(R.color.common_white);
        lvDialog.setDivider(context.getResources().getDrawable(R.drawable.divider));
        BaseAdapter adapter = new BaseAdapter() {

            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    CheckedTextView ctvItem = new CheckedTextView(context);
//					ctvItem.setBackgroundResource( R.drawable.textview_click_status );
                    ctvItem.setTextColor(context.getResources().getColor(R.color.group_create_bg));
//					ctvItem.setTextSize( TypedValue.COMPLEX_UNIT_DIP , 18 );
                    ctvItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                    ctvItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.radio_btn,
                            0);
                    int dp_12 = CommonFunction.dipToPx(context, 12);
                    ctvItem.setPadding(dp_12, dp_12, dp_12, dp_12);
                    convertView = ctvItem;
                }

                CheckedTextView ctvItem = (CheckedTextView) convertView;
                ctvItem.setChecked(selected == position);
                ctvItem.setText(getItem(position));
                return convertView;
            }

            public long getItemId(int position) {
                return position;
            }

            public String getItem(int position) {
                return choices[position];
            }

            public int getCount() {
                return choices.length;
            }
        };
        lvDialog.setAdapter(adapter);

        // 显示弹窗
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setIcon( R.drawable.setting_pointer_down );
        builder.setTitle(title);
        builder.setView(lvDialog);
        final AlertDialog dialog = builder.create();
        DialogInterface.OnClickListener nullListener = null;
//		dialog.setButton( context.getResources( ).getString( R.string.dismiss ) , nullListener );
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.dismiss), nullListener);

        // 添加事件监听
        AdapterView.OnItemClickListener oicListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctvItem = (CheckedTextView) view;
                ctvItem.setSelected(true);
                if (clListener != null) {
                    clListener.onClick(dialog, position);
                }
                dialog.dismiss();
            }
        };
        lvDialog.setOnItemClickListener(oicListener);

        // 显示对话框
        dialog.show();
        //dialog.show()之后设置，要不容易报空指针
        Button button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (button != null) {
            button.setTextColor(Color.parseColor("#FF4064"));
        }
        lvDialog.setSelection(selected);
        return dialog;
    }

    public static CustomDialog showCheckDialog(Context context, CharSequence title, CharSequence message,
                                               int layoutRes, CharSequence okbtnTitle, View.OnClickListener listener, CharSequence cancelbtnTitle) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, layoutRes,
                    okbtnTitle, listener, cancelbtnTitle, null);
            dialog.show();

            dialog.setCancelable(true);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 滚动条对话框 ,6.0新增  加载框统一用
     *
     * @param context
     * @return
     */
    public static Dialog showProgressDialog_New(Context context) {
        return showProgressDialog_New(context, context.getString(R.string.pull_to_refresh_refreshing_label));
    }

    /**
     * @param context
     * @param message
     * @return Dialog
     * @Title: showProgressDialog
     * @Description: 仅获取加载对话框，不直接显示,6.0新增
     */
    public static Dialog showProgressDialog_New(Context context, String message) {
        try {
            CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
            dialog.setMessage(message);
            dialog.show();
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Dialog showEditHeadDialog(Context context, String title, final ArrayList<String> itemList, final DialogUtil.CliclCallback cliclCallback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_head, null);
        TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        ListView lvItem = (ListView) contentView.findViewById(R.id.lv_item);
        lvItem.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return itemList.size();
            }

            @Override
            public Object getItem(int position) {
                return itemList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = View.inflate(parent.getContext(), R.layout.item_dialog_edit_head, null);
                TextView tvItem = (TextView) view.findViewById(R.id.tv_item);
                tvItem.setText(itemList.get(position));
                return tvItem;
            }
        });
        builder.setView(contentView);
        final AlertDialog dialog = builder.create();
        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cliclCallback.click(position);
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public interface CliclCallback {
        void click(int position);
    }

    /**
     * 获取金币对话框
     */
    public static void showGoldDialog(final Activity activity) {
        showTwoButtonDialog(activity, ErrorCode.getErrorMessageId(ErrorCode.E_4000),
                activity.getString(R.string.face_gold_not_enough),
                activity.getString(R.string.cancel), activity.getString(R.string.exchange),
                null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {// 前往金币购买界面
                        FragmentPayBuyGlod.jumpPayBuyGlodActivity(activity);//jiqiang
                    }
                });
    }

    public static Dialog buildDialog(Context context, View view, int gravity) {
        int screenPixWidth = CommonFunction.getScreenPixWidth(context);
        Dialog dialog = null;
        if (dialog == null) {
            dialog = new Dialog(context, R.style.myDialogTheme);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.CENTER);
            lp.width = (int) (screenPixWidth * 0.875);
//			lp.height = ( int ) ( screenPixWidth * 0.75 );
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(lp);

            dialog.show();
        }

        return dialog;

    }

    /**
     * 钻石直购对话框
     *
     * @param mContext
     * @param type     类型 ， 金币或钻石
     * @param price    价格
     * @param discount 兑换金币折扣,80 -%
     * @param ratio    兑换比例 ，X个金币兑换一个钻石币
     * @return
     */
    public static Dialog showDiamonConvertBuyDialog(Context mContext, int type, int price,
                                                    int discount, int ratio, final android.view.View.OnClickListener positiveBtnClickListener
            , final android.view.View.OnClickListener rightBtnClickListener) {
        final Dialog payDialog = new Dialog(mContext, R.style.NewDialog);
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_diamon_exchange, null);
        payDialog.setContentView(view);
        payDialog.show();

        TextView title_left = (TextView) view.findViewById(R.id.dialog_title_left);
        TextView title_right = (TextView) view.findViewById(R.id.dialog_title_right);
        TextView dialog_content = (TextView) view.findViewById(R.id.dialog_content);

        String str = "";
        if (type == 1) {// 金币
            str = mContext.getResources().getString(R.string.face_price_neednt_gold_2);
        } else if (type == 2) {// 钻石
            str = mContext.getResources().getString(R.string.face_price_neednt_diamond);
        }
        String titleLeftStr = String.format(str, price);
        title_left.setText(titleLeftStr);

        String titleContentStr = "";
        int diamonAmount = (int) Math.ceil(price * ((double) discount / 100) / ratio);
        if (discount % 10 == 0) {
            int rebate = discount;
            if (CommonFunction.getLanguageIndex(mContext) == 1
                    || CommonFunction.getLanguageIndex(mContext) == 2) {
                rebate = discount / 10;
            }
            titleContentStr = mContext.getString((R.string.diamonconvertdialog_content),
                    diamonAmount, rebate);
        } else {
            double rebate = discount;
            if (CommonFunction.getLanguageIndex(mContext) == 1
                    || CommonFunction.getLanguageIndex(mContext) == 2) {
                rebate = (double) discount / 10;
            }
            titleContentStr = mContext.getString((R.string.diamonconvertdialog_content),
                    diamonAmount, rebate);
        }

        dialog_content.setText(titleContentStr);
        title_right.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 设置下划线
        title_right.getPaint().setAntiAlias(true);// 抗锯齿

        view.findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payDialog.dismiss();
            }
        });

        view.findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveBtnClickListener != null) {
                    positiveBtnClickListener.onClick(v);
                }
                payDialog.dismiss();
            }
        });

        title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightBtnClickListener != null) {
                    rightBtnClickListener.onClick(v);
                }
                payDialog.dismiss();
            }
        });

        return payDialog;
    }

    public static void showPayGiftDialog(Context mContext, View.OnClickListener cListener) {
        DialogUtil.showOKCancelDialog(mContext, R.string.dialog_title,
                R.string.pay_gift_check_true_tip, cListener, null);
    }


    /**
     * common\DialogUtil
     */

    public static CustomDialog showBackDialog(Context context, CharSequence title, CharSequence message,
                                              int layoutRes, View.OnClickListener listener) {
        try {
            CustomDialog dialog = new CustomDialog(context, title, message, layoutRes,
                    context.getString(R.string.back), listener, "", null);
            dialog.show();

            dialog.setCancelable(false);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }


    /**
     * @Description:显示两个按钮上下分布的对话框
     * @author tanzy
     * @date 2014-9-28
     */
    public static Dialog showUpDownTwoButtonDialog(Context context, String title,
                                                   String content, String upText, String downText, View.OnClickListener upClick,
                                                   View.OnClickListener downClick, boolean upAble, boolean downAble, boolean cancelable) {
        try {
            UpDownTwoButtomDialog dialog = new UpDownTwoButtomDialog(context, title,
                    content, upText, downText, upAble, downAble, upClick, downClick);
            dialog.show();
            dialog.setCancelable(cancelable);
            return dialog;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static Dialog showUpDownTwoButtonDialog(Context context, int title, int content,
                                                   int upText, int downText, View.OnClickListener upClick, View.OnClickListener downClick,
                                                   boolean upAble, boolean downAble, boolean cancelable) {
        return showUpDownTwoButtonDialog(context, context.getString(title),
                context.getString(content), context.getString(upText),
                context.getString(downText), upClick, downClick, upAble, downAble,
                cancelable);
    }


    public static Dialog payGiftDialog(Context mContext, Gift gift,
                                       android.view.View.OnClickListener cListener) {
        //注释没有导入style样式
//		final Dialog payDialog = new Dialog( mContext , R.style.MyDialog );
        final Dialog payDialog = new Dialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.store_pay_gift, null);
        int dp_284 = (int) (284 * mContext.getResources().getDisplayMetrics().density);
        payDialog.setContentView(view, new LinearLayout.LayoutParams(dp_284, dp_284));
        payDialog.show();
        NetImageView store_pay_gift_icon = (NetImageView) view
                .findViewById(R.id.store_pay_gift_icon);
        TextView store_pay_gift_name = (TextView) view.findViewById(R.id.store_pay_gift_name);
        TextView store_pay_gift_type = (TextView) view.findViewById(R.id.store_pay_gift_type);
        TextView store_pay_gift_price = (TextView) view.findViewById(R.id.store_pay_gift_price);
        TextView store_pay_gift_charm = (TextView) view.findViewById(R.id.store_pay_gift_charm);
        TextView store_pay_gift_pay = (TextView) view.findViewById(R.id.store_pay_gift_pay);
        store_pay_gift_icon.execute(PicIndex.DEFAULT_GIFT, gift.getIconUrl());
        store_pay_gift_name.setText(gift.getName(mContext));
        store_pay_gift_type.setText(mContext.getString(R.string.store_gift_type)
                + gift.getType(mContext));
        store_pay_gift_charm.setText(mContext.getString(R.string.charisma_title_new) + "+"
                + gift.getCharisma());
        if (gift.getCurrencytype() == 1) {
            store_pay_gift_price.setText(mContext.getString(R.string.pay_gold) + ":"
                    + gift.getPrice());
        } else if (gift.getCurrencytype() == 2) {
            store_pay_gift_price.setText(mContext.getString(R.string.diamond) + ":"
                    + gift.getPrice());
        }

        view.findViewById(R.id.store_pay_gift_exit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                payDialog.dismiss();
            }
        });

        store_pay_gift_pay.setOnClickListener(cListener);
        return payDialog;
    }

    /**
     * 显示广告
     *
     * @param mContext
     * @param type      0 = 小图，1 = 大图
     * @param cListener
     * @param url
     * @return
     */
    public static Dialog showAdDialogNormal(Context mContext, int type, String url, CustomAdDialog.SureClickListener cListener) {
        CustomAdDialog customAdDialog = new CustomAdDialog(mContext, type, url, cListener);
        customAdDialog.show();
        return customAdDialog;
    }

    /**
     * 显示招募广播
     *
     * @param context
     * @param onClickListener
     * @return
     */
    public static Dialog showRecruitDialog(Context context, RecruitAndCallDialog.SureClickListener onClickListener) {
        RecruitAndCallDialog recruitAndCallDialog = new RecruitAndCallDialog(context, 0, onClickListener);
        recruitAndCallDialog.show();
        return recruitAndCallDialog;
    }

    /**
     * 显示吧主召唤
     *
     * @param context
     * @param onClickListener
     * @return
     */
    public static Dialog showCallDialog(Context context, RecruitAndCallDialog.SureClickListener onClickListener) {
        RecruitAndCallDialog recruitAndCallDialog = new RecruitAndCallDialog(context, 1, onClickListener);
        recruitAndCallDialog.show();
        return recruitAndCallDialog;
    }

    /**
     * 显示收到吧主召唤
     *
     * @param context
     * @param onClickListener
     * @return
     */
    public static Dialog showReceivedCallDialog(Context context, String groupName, String content, ReceivedCallDialog.SureClickListener onClickListener) {
        ReceivedCallDialog receivedCallDialog = new ReceivedCallDialog(context, groupName, content, onClickListener);
        receivedCallDialog.show();
        return receivedCallDialog;
    }

    /**
     * 显示修改用户弹框
     *
     * @param context
     * @param onClickListener
     * @return
     */
    public static Dialog showUserSexDialog(Context context, EditUserSexDialog.SureClickListener onClickListener) {
        EditUserSexDialog editUserSexDialog = new EditUserSexDialog(context, onClickListener);
        editUserSexDialog.show();
        return editUserSexDialog;
    }

    /**
     * 显示推荐技能升级
     *
     * @param context
     * @param skillUpdateEntity
     * @param skillItemOnClick
     * @return
     */
    public static Dialog showSkillUpdateDialog(Context context, SkillUpdateEntity skillUpdateEntity, SkillUpdateDailog.SkillItemOnClick skillItemOnClick) {
        SkillUpdateDailog skillUpdateDailog = new SkillUpdateDailog(context, skillUpdateEntity, skillItemOnClick);
        skillUpdateDailog.show();
        return skillUpdateDailog;
    }

    /**
     * 显示爱心不足
     *
     * @param context
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showRechargeLove(Context context, String message, RechargeLoveDialog.OnRechargeClickListener listener) {
        RechargeLoveDialog dialog = new RechargeLoveDialog(context, message, listener);
        dialog.show();
        return dialog;
    }

}
