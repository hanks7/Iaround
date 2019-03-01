package net.iaround.model.login.bean;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.model.entity.Item;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.model.ranking.WorldMessageContentEntity;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.videochat.task.TaskWrapperManager;

import java.util.List;


/**
 * Created by Administrator on 2015/12/10.
 */
public class HadRegister {

    public int status; //状态码

    public int hadReg; //是否注册过

    public int geetestSwitch; //极验开关

    public String countryCode; //国家区号

}

