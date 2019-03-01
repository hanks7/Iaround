package net.iaround.ui.comon;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.interfaces.SelDialogCallBack;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月1日 下午6:07:55
 * @Description: 通用选择框基类
 */
public abstract class CommonSelDialog extends DialogFragment implements View.OnClickListener{

	//选择框的确定,取消按钮的回调接口
	private SelDialogCallBack mCallback;
	
	private int titleResId;
	private String titleStr;
	private View baseView;
	private TextView tvTitle;
	private Button btnNegative;
	private Button btnPositive;
	
	private RelativeLayout rlContent;

	public CommonSelDialog(){

	}

	@SuppressLint("ValidFragment")
	public CommonSelDialog(String titleStr, SelDialogCallBack callback) {
		mCallback = callback;
		this.titleStr = titleStr;
	}

	@SuppressLint("ValidFragment")
	public CommonSelDialog(int titleResId, SelDialogCallBack callback) {
		mCallback = callback;
		this.titleResId = titleResId;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
		baseView = inflater.inflate(R.layout.common_sel_dialog_base_layout, container);
		initView(baseView);
		initContentView(inflater, rlContent);
		
		return baseView;
	}
	
	private void initView(View v)
	{
		tvTitle = (TextView) baseView.findViewById(R.id.tvDialogTitle);
		if(TextUtils.isEmpty(titleStr)){
			tvTitle.setText(titleResId);
		}else{
			tvTitle.setText(titleStr);
		}
		
		
		btnNegative = (Button) baseView.findViewById(R.id.btnNegative);
		btnPositive = (Button) baseView.findViewById(R.id.btnPositive);
		
		btnNegative.setOnClickListener(this);
		btnPositive.setOnClickListener(this);
		
		rlContent = (RelativeLayout) baseView.findViewById(R.id.rlContent);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(btnNegative))
		{
			mCallback.onCancelSelect();
			this.dismiss();
		}else if(v.equals(btnPositive)){
			mCallback.onSelectFinish(getSelectContent());
			this.dismiss();
		}
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		mCallback.onCancelSelect();
	}
	/**
	 * 获取需要反馈的内容以json的格式返回
	 * @return json
	 */
	public abstract String getSelectContent();
	
	/**
	 * 初始化Dialog内容的View
	 * @return
	 */
	public abstract View initContentView(LayoutInflater inflater, ViewGroup container);
	
	public abstract void reset();
}
