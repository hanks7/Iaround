package net.iaround.ui.dynamic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.CommonSelDialog;
import net.iaround.ui.comon.DrawableCenterCheckBox;
import net.iaround.ui.dynamic.bean.DynamicFilterSelectBean;
import net.iaround.ui.interfaces.SelDialogCallBack;


/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月1日 下午8:33:30
 * @Description: 动态过滤的对话框
 */
public class DynamicFilterDialog extends CommonSelDialog implements
		OnCheckedChangeListener {

	private String defSetting = "";// 默认设置json,如果为空那么就为默认设置
	private View contentView;

	// 筛选的CheckBox
	private DrawableCenterCheckBox cbAll;// 这个cbAll不包括性别的选择
	private DrawableCenterCheckBox cbFollow;
	private DrawableCenterCheckBox cbNear;
	private DrawableCenterCheckBox cbHot;
	private DrawableCenterCheckBox cbMale;
	private DrawableCenterCheckBox cbFemale;

	public DynamicFilterDialog(){

	}

	@SuppressLint("ValidFragment")
	public DynamicFilterDialog(String titleStr, String defaultSetting,
							   SelDialogCallBack callback) {
		super(titleStr, callback);
		defSetting = defaultSetting;
	}

	@SuppressLint("ValidFragment")
	public DynamicFilterDialog(int titleResId, String defaultSetting,
							   SelDialogCallBack callback) {
		super(titleResId, callback);
		defSetting = defaultSetting;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 设置打开的动画效果
		getDialog().getWindow().getAttributes().windowAnimations = R.style.umeng_socialize_dialog_animations;
		getDialog().getWindow().getAttributes().dimAmount = 0.6f;
	}

	@Override
	public String getSelectContent() {
		DynamicFilterSelectBean bean = new DynamicFilterSelectBean();
		bean.isAll = cbAll.isChecked();
		bean.isFollow = cbFollow.isChecked();
		bean.isNear = cbNear.isChecked();
		bean.isHot = cbHot.isChecked();
		bean.isMale = cbMale.isChecked();
		bean.isFemale = cbFemale.isChecked();
		return GsonUtil.getInstance().getStringFromJsonObject(bean);
	}

	@Override
	public View initContentView(LayoutInflater inflater, ViewGroup container) {
		contentView = inflater.inflate(R.layout.dynamic_sel_dialog_content,
				container);

		cbAll = (DrawableCenterCheckBox) contentView.findViewById(R.id.cbAll);
		cbFollow = (DrawableCenterCheckBox) contentView.findViewById(R.id.cbFollow);
		cbNear = (DrawableCenterCheckBox) contentView.findViewById(R.id.cbNear);
		cbHot = (DrawableCenterCheckBox) contentView.findViewById(R.id.cbHot);
		cbMale = (DrawableCenterCheckBox) contentView.findViewById(R.id.cbMale);
		cbFemale = (DrawableCenterCheckBox) contentView.findViewById(R.id.cbFemale);

		cbAll.setOnCheckedChangeListener(this);
		cbFollow.setOnCheckedChangeListener(this);
		cbNear.setOnCheckedChangeListener(this);
		cbHot.setOnCheckedChangeListener(this);
		cbMale.setOnCheckedChangeListener(this);
		cbFemale.setOnCheckedChangeListener(this);

		if (TextUtils.isEmpty(defSetting)) {
			reset();
		} else {
			initCheckBox();
		}

		return contentView;
	}

	@Override
	public void reset() {
		cbAll.setChecked(true);
		cbFollow.setChecked(true);
		cbNear.setChecked(true);
		cbHot.setChecked(true);
		cbMale.setChecked(true);
		cbFemale.setChecked(true);
	}

	private void initCheckBox() {
		DynamicFilterSelectBean bean = GsonUtil.getInstance().getServerBean(
				defSetting, DynamicFilterSelectBean.class);
		cbAll.setChecked(bean.isAll);
		cbFollow.setChecked(bean.isFollow);
		cbNear.setChecked(bean.isNear);
		cbHot.setChecked(bean.isHot);
		cbMale.setChecked(bean.isMale);
		cbFemale.setChecked(bean.isFemale);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int viewId = buttonView.getId();
		switch (viewId) {
		case R.id.cbAll:
			if (isChecked) {
				cbAll.setChecked(true);
				cbFollow.setChecked(true);
				cbNear.setChecked(true);
				cbHot.setChecked(true);
			} else if (isAllCheck())
				buttonView.setChecked(true);
			break;
		case R.id.cbFollow:
		case R.id.cbNear:
		case R.id.cbHot:
			if (isChecked) {
				if (isAllCheck())
					cbAll.setChecked(isChecked);
				else
					buttonView.setChecked(isChecked);
			} else {
				if (isAllUnCheck())
					buttonView.setChecked(true);
				else {
					cbAll.setChecked(isChecked);
					buttonView.setChecked(isChecked);
				}
			}
			break;
		case R.id.cbMale:
			if(!isChecked){
				if(!checkVip()){
					buttonView.setChecked(true);
					return;
				}
				
				if(cbFemale.isChecked()){
					buttonView.setChecked(isChecked);
				}else{
					buttonView.setChecked(true);
				}
			}else{
				buttonView.setChecked(isChecked);
			}
			break;
		case R.id.cbFemale:
			if(!isChecked){
				if(!checkVip()){
					buttonView.setChecked(true);
					return;
				}
				if(cbMale.isChecked()){
					buttonView.setChecked(isChecked);
				}else{
					buttonView.setChecked(true);
				}
			}else{
				buttonView.setChecked(isChecked);
			}
			break;
		default:
			break;
		}

	}

	/** 判断是否全部都选中了 这里不包括性别的筛选 */
	private boolean isAllCheck() {
        return !(!cbFollow.isChecked() || !cbNear.isChecked() || !cbHot.isChecked());
    }

	private boolean isAllUnCheck() {
        return !(cbFollow.isChecked() || cbNear.isChecked() || cbHot.isChecked());
    }
	
	private boolean checkVip(){
		boolean isVip = Common.getInstance().loginUser.isVip();
		if(!isVip){
//			String title = getResources().getString(R.string.tost_filter_vip_title);
//			String message = getResources().getString(R.string.tost_filter_vip_privilege);
//			String cancel = getResources().getString(R.string.tost_filter_vip_cancel);
//			String becomeVip = getResources().getString(R.string.tost_filter_vip_recharge);
//			DialogUtil.showTowButtonDialog(this.getActivity(), title, message, cancel, becomeVip, null, new View.OnClickListener() {
//
//						@Override
//						public void onClick( View v )
//						{
//							VipBuyMemberActivity.lanchVipBuyMemberActivity( getActivity( ) ,
//									DataTag.VIEW_dynamic_filtrate );
//						}
//			});
			DialogUtil
				.showTobeVipDialog( getActivity( ), R.string.tost_filter_vip_title,
					R.string.tost_filter_vip_privilege );

		}
		return isVip;
	}
}
