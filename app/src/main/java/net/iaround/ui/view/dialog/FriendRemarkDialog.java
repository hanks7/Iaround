package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.StringUtil;
import net.iaround.ui.interfaces.OnUpdateNickListener;
import net.iaround.utils.eventbus.NoteNameNotifyEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author：liush on 2017/2/13 12:02
 */
public class FriendRemarkDialog extends Dialog {

    private View.OnClickListener negativeListener;
    private View.OnClickListener positiveListener;
    private EditText etRemark;
    private TextView tvNickname;
    private TextView tvNegative;
    private TextView tvPositive;

    private String nickname;
    private String strContent;
    private OnUpdateNickListener listener;

    public long getTargetuserid() {
        return targetuserid;
    }

    public void setTargetuserid(long targetuserid) {
        this.targetuserid = targetuserid;
    }

    private long targetuserid;

    public FriendRemarkDialog(Context context, OnUpdateNickListener listener) {
        super(context,R.style.custom_dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
//        Window window = this.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        setCanceledOnTouchOutside(true);

    }

    private void initView(){
        setContentView(R.layout.dialog_friend_remark);
        Window window = this.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCanceledOnTouchOutside(true);
        etRemark = findView(R.id.et_remark);
        tvNickname = findView(R.id.tv_nickname);
        tvNegative = findView(R.id.tv_negative);
        tvPositive = findView(R.id.tv_positive);
        if (TextUtils.isEmpty(nickname)) {
            nickname = "";
        }
        tvNickname.setText(nickname);
        tvNickname.setVisibility(View.INVISIBLE);//YC 不显示昵称了
//        etRemark.setText();
        setOnclickListener();
        etRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//					etContent.setText( StringUtil.getEntireFaceString(s.toString(), 8) );	//s.subSequence( 0 , 8 )
//					etContent.setSelection( etContent.getText( ).length( ) );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = StringUtil.getLengthCN1(s.toString());
                if (length > 8) {
                    KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL);
                    etRemark.onKeyDown(KeyEvent.KEYCODE_DEL, keyEventDown);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        initView();
        if (tvNickname != null || !nickname.contains("null")) {
            etRemark.setText(nickname);
        }else
        {
            etRemark.setText("");
        }
    }

    @Override
    public void show() {
        super.show();
    }

    public void setOnclickListener() {
        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                strContent = CommonFunction.filterKeyWordAndReplaceEmoji(
                        getContext(), etRemark.getText().toString().trim());// 过滤关键字
                strContent = StringUtil.qj2bj(strContent);

                UserHttpProtocol.userNotesSetname(getContext(), targetuserid, strContent,
                        new HttpCallBack() {
                            @Override
                            public void onGeneralSuccess(String result, long flag) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfo_note_success), Toast.LENGTH_LONG).show();
                                EventBus.getDefault().post(
                                        new NoteNameNotifyEvent(strContent, targetuserid));
                                listener.update(strContent);
                            }

                            @Override
                            public void onGeneralError(int e, long flag) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfo_note_failure), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    public <T> T findView(int id) {
        return (T) findViewById(id);
    }
}
