package net.iaround.conf;

import android.content.Context;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.connector.ConnectLogin;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.CloseAllActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误代码索引表
 * 
 * @author linyg
 * 
 */
public class ErrorCode {

	private static Context mContext = BaseApplication.appContext;
	private static Map<Integer, String> map = new HashMap<Integer, String>();
	private static String[] errArr = mContext.getResources().getStringArray(
			R.array.ErrorCode);
	private static String err_item1 = mContext.getResources().getStringArray(
			R.array.ErrorCode)[0];
	private static String err_item2 = mContext.getResources().getStringArray(
			R.array.ErrorCode)[1];
	private static String item2_desc = err_item2.split("@")[1];
	private static String item1_desc = err_item1.split("@")[1];
	private static String item1_id = err_item1.split("@")[0];

	/** 没有可用的网络，请设置 */
	public static final int E_100 = 100;
	/** 网络连接失败 */
	public static final int E_101 = 101;
	/** 发送数据失败 */
	public static final int E_102 = 102;
	/** 连接超时 */
	public static final int E_103 = 103;
	/** 请求失败 **/
	public static final int E_104 = 104;
	/** room服务连接中断 */
	public static final int E_105 = 105;
	/** session服务连接中断 */
	public static final int E_106 = 106;

	/** 请求数据失败 **/
	public static final int E_107 = 107;


	/** 登录账号密码错误 */
	public static final int E_11302 = 11302;
	/** authcode验证失败 */
	public static final int E_11901 = 11901;
	/** 注册成功 */
	public static final int E_11902 = 11902;
	/** 账号已经存在 */
	public static final int E_11102 = 11102;
	/** 注册参数无效 */
	public static final int E_11101 = 11101;


	/** 订单编号产生失败 **/
	public static final int E_5901 = 5901;
	/** 钻石不足 */
	public static final int E_5930 = 5930;

	/** 金币不足 **/
	public static final int E_4000 = 4000;

	/** 账号被管理员锁定 */
	public static final int E_4102 = 4102;

	/** 账号没有注册过 */
	public static final int E_4104 = 4104;

	/** 无法登录 **/
	public static final int E_4208 = 4208;

	/** 密等性校验失败（对链接的加密校验） */
	public static final int E_1000 = 1000;
	/** 被禁言用户不能发表话题 */
	public static final int E_6029 = 6029;
	/** 用户当前为“未登录”状态 */
	public static final int E_4100 = 4100;
	/** 赠送礼物失败 **/
	public static final int E_5804 = 5804;
	/** 该用户未被关注 **/
	public static final int E_5702 = 5702;


	static {
//		mContext = CloseAllActivity.getInstance().getTopActivity();
		errArr = mContext.getResources().getStringArray(R.array.ErrorCode);
		err_item1 = mContext.getResources().getStringArray(R.array.ErrorCode)[0];
		item1_id = err_item1.split("@")[0];
		item1_desc = err_item1.split("@")[1];
		for (int j = 0; j < errArr.length; j++) {
			String[] errdes = errArr[j].split("@");
			int a = Integer.parseInt(errdes[0]);
			String b = "";
			if(errdes.length>2)
			{
				for(int i = 1 ,iMax =errdes.length ;i<iMax ;i++ )
				{

					b +=errdes[i];
					if( i+1 < iMax )
					{
						b+="@" ;
					}

				}
			}
			else
			{
				b =errdes[1];
			}
			map.put(a, b);

		}

	}

	static public  void reset(){
		//重置静态变量，防止ExceptionInInitializerError
		try {
			mContext = CloseAllActivity.getInstance().getTopActivity();
			if (mContext == null) {
				return;
			}
			errArr = mContext.getResources().getStringArray(R.array.ErrorCode);
			err_item1 = mContext.getResources().getStringArray(R.array.ErrorCode)[0];
			item1_id = err_item1.split("@")[0];
			item1_desc = err_item1.split("@")[1];

			map.clear();
			for (int j = 0; j < errArr.length; j++) {
				String[] errdes = errArr[j].split("@");
				int a = Integer.parseInt(errdes[0]);
				String b = "";
				if (errdes.length > 2) {
					for (int i = 1, iMax = errdes.length; i < iMax; i++) {

						b += errdes[i];
						if (i + 1 < iMax) {
							b += "@";
						}

					}
				} else {
					b = errdes[1];
				}
				map.put(a, b);

			}
		}catch (ExceptionInInitializerError exceptionInInitializerError){
			exceptionInInitializerError.printStackTrace();
		}

	}

	/**
	 * 读取该类型信息的语言描述
	 *
	 * @param id
	 * @return 返回对应描述的id,默认返回e_1的id
	 * @time 2011-6-10 上午11:41:55
	 * @author:linyg
	 */
	public static String getErrorMessageId(int id) {
		if (map.containsKey(id)) {
			return map.get(id);
		}
		return item1_desc;
	}

	/**
	 * @Title: toastError
	 * @Description: toast一个错误消息
	 * @param context
	 *            上下文
	 * @param id
	 *            错误号
	 */
	public static void toastError(Context context, int id) {

		String errorMsg = item1_desc;
		if (map.containsKey(id)) {

			errorMsg = map.get(id);

		}
		Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示网络返回的错误提示
	 *
	 * @param context
	 * @param result
	 *            服务端下发的消息体
	 * @time 2011-7-1 上午09:38:14
	 * @author:linyg
	 */
	public static void showError(Context context, String result) {
		if (context != null)
			context = BaseApplication.appContext;

		// 重新登录,不报错
		if (CommonFunction.isEmptyOrNullStr(result)) {
			return;
		}

		try {
			JSONObject json = new JSONObject(result);
			int e = json.optInt("error");
			int status = json.optInt("status");
			if (status == 200) {
				return;
			}
			String problem = CommonFunction.jsonOptString(json, "errordesc");
			// 该用户当前未登录
			if (e == 4100) {
				try {
//					ConnectLogin.getInstance(context).reDoLogin(context);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				// 当签名验证出错时，特意设置一下key
				if (e == 1000) {
					String key = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.IAROUND_SESSIONKEY);
					if (!CommonFunction.isEmptyOrNullStr(key)) {
						ConnectorManage.getInstance(context).setKey(key);
					}
				}else if (e == 4208){
					if (!Common.getInstance().isUserLogin){
						return;
					}
				} else if (e == 10000){
					CommonFunction.showToast(context,problem, 0);
					return;
				}else if (e == 32767){
					String[] problems = problem.split("###");
					String error = "";
					if (problem.contains("###")){
						if (problems.length >= 3){
							if (context == null) {
								context = BaseApplication.appContext;
							}
							int languageIndex = CommonFunction.getLanguageIndex(context);
							if (languageIndex == 0){
								// 英文
								error = problems[0];
							}else if (languageIndex == 1){
								// 简体
								error = problems[1];
							}else {
								// 繁体
								error = problems[2];
							}
						}
					}else{
						error = problem;
					}
					CommonFunction.showToast(context,error, 0);

					return;
				}else if (e == 4102 || e == 8201 || e == 8202){
					return;
				}

				String error = ErrorCode.getErrorMessage(context, e, problem);
				if (!CommonFunction.isEmptyOrNullStr(error)) {
					CommonFunction.showToast(context, error, 0);
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * 显示错误
	 *
	 * @param context
	 * @param res
	 */
	public static void showErrorByMap(Context context,
									  HashMap<String, Object> res) {
		if (res == null) {
			return;
		}
		int error = 0;
		try {
			if (res.get("error") != null) {
				error = Integer.parseInt(String.valueOf(res.get("error")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getErrorMessage(context, error);
	}

	/**
	 * 语言描述
	 *
	 * @param context
	 * @param id
	 *            服务端下发的错误描述，可能有些接口没有下发，该参数作为旧版的兼容性
	 * @return 返回对应描述的string，默认返回“出错啦”
	 * @time 2011-6-10 上午11:42:23
	 * @author:linyg
	 */
	public static String getErrorMessage(Context context, int id) {

		// 若错误码，在本地有，则提取出响应的错误信息响应
		if (map.containsKey(id)) {
			if (id == 4100) {
//				try {
//					ConnectLogin.getInstance(context).reDoLogin(context);
//				} catch (ConnectionException e) {
//					e.printStackTrace();
//				}
				return context.getString(R.string.server_exception);
			} else if (id == 4208){
				if (!Common.getInstance( ).isUserLogin){
					return "";
				}
			} else {
				String error = "";
				if (id > 103) {
					error = String.format(item2_desc, id);
				}

				return map.get(id);
			}
		}
		// 当errordesc为空时，则返回未知错误；否则返回服务端下发的错误信息

		return item1_desc + String.format(item2_desc, id);

	}

	/**
	 * 语言描述
	 *
	 * @param context
	 * @param id
	 * @param errordesc
	 *            服务端下发的错误描述，可能有些接口没有下发，该参数作为旧版的兼容性
	 * @return 返回对应描述的string，默认返回“出错啦”
	 * @time 2011-6-10 上午11:42:23
	 * @author:linyg
	 */
	public static String getErrorMessage(Context context, int id, String errordesc) {
		// 若错误码，在本地有，则提取出响应的错误信息响应
		if (map.containsKey(id)) {
			if (id == 4100) {
//				if (Common.getInstance( ).isUserLogin){
//					return context.getString(R.string.server_exception);
//				}
				return "";
			} else if (id == 4208){
				if (!Common.getInstance( ).isUserLogin){
					return "";
				}
			} else {
				String error = "";
				if (id > 103) {
					error = String.format(item2_desc, id);
				}

				return map.get(id);
			}
		}
		// 当errordesc为空时，则返回未知错误；否则返回服务端下发的错误信息
		if (CommonFunction.isEmptyOrNullStr(errordesc)) { // test
			return item1_desc + String.format(item2_desc, id);
		} else {
			return errordesc;
		}
	}

}
