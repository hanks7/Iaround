
package net.iaround.ui.datamodel;


import android.content.Context;
import android.text.TextUtils;

import net.iaround.R;
import net.iaround.tools.CommonFunction;

import java.io.Serializable;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: GroupUser
 * @Description: 用户的实体类
 * @date 2013-12-10 上午11:27:21
 */
public class GroupUser extends BaseUserInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3951753111656339090L;

    /**
     * @return
     * @Title: getSex
     * @Description: 获取性别的int值（1男，2女）
     */
    public int getSex() {
        if (gender != null) {
            return gender.equals("m") ? 1 : 2;
        } else {
            return 0;
        }
    }

    /**
     * @param context
     * @return
     * @Title: getPersonalInfor
     * @Description: 获取签名介绍信息
     */
    public String getPersonalInfor(Context context) {
        if (CommonFunction.isEmptyOrNullStr(this.selftext)) {
            return context.getString(R.string.make_great_think);
        }
        return this.selftext;
    }

    /**
     * @param isNotes 是否需要显示备注
     * @return
     * @Title: getDisplayName
     * @Description: 获取显示的用户名称
     */
    public String getDisplayName(boolean isNotes) {
        if (isNotes && !CommonFunction.isEmptyOrNullStr(notes)) {
            return this.notes;
        } else {
            return this.nickname;
        }
    }

    /**
     * @return
     * @Title: isOnline
     * @Description: 当前用户是否在线
     */
    public boolean isOnline() {
        return this.isonline.equals("y");
    }

    /**
     * @return
     * @Title: isForbidUser
     * @Description: 用户是否封停
     */
    public boolean isForbidUser() {
        return this.forbid != 0;
    }

    public String getIcon() {
        return TextUtils.isEmpty(icon) ? "" : icon;
    }


}
