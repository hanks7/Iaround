
package net.iaround.model.entity;


import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;

import net.iaround.BaseApplication;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.CloseAllActivity;

import java.io.Serializable;
import java.net.Proxy;
import java.util.List;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @ClassName: BaseServerBean
 * @Description: 从服务端返回的基础数据bean
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-5 下午7:59:42
 * 
 */
public class BaseListBean<T> implements Serializable {
	private static final long serialVersionUID = -369558847578246550L;
	/**
	 * 请求失败时的错误码
	 */
	public int status = 1;
	
	/** 错误描述 */
	public String msg;

	public List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public static BaseListBean fromJson(String json, Class clazz) {
		Gson gson = new Gson();
		Type objectType = type(BaseListBean.class, clazz);
		BaseListBean bean = null;
		try {
			bean = gson.fromJson(json, objectType);
		}catch (Exception e){
			json = "{\"status\":202,\"msg\":\"Parse Error\",\"data\":{\"ret\":1}}";
			bean = gson.fromJson(json, objectType);
		}
		return bean;
	}

	public String toJson(Class<T> clazz) {
		Gson gson = new Gson();
		Type objectType = type(BaseListBean.class, clazz);
		return gson.toJson(this, objectType);
	}

	static ParameterizedType type(final Class raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}

	public boolean isSuccess( )
	{
		return status == 200;
	}
}
