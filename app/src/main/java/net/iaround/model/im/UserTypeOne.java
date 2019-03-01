package net.iaround.model.im;

import android.text.TextUtils;

import net.iaround.ui.datamodel.BaseUserInfo;

/**
 * @ClassName UserTypeOne.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-10 下午8:23:25
 * @Description: 用户实体类型比较多，暂时通过名字区分
 * @相关协议：81011
 */

public class UserTypeOne extends BaseUserInfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5858950548572134825L;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


	public String getGender() {
		if(TextUtils.isEmpty(gender)){
			gender = "";
		}
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIcon() {
		if(TextUtils.isEmpty(icon)){
			icon = "";
		}
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
