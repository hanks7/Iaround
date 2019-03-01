package net.iaround.model.entity;

import android.app.Activity;

import com.google.gson.Gson;

import net.iaround.BaseApplication;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.CloseAllActivity;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class: 基础Bean
 * Author：gh
 * Date: 2016/12/12 21:57
 * Email：jt_gaohang@163.com
 */
public class BaseBean <T> implements Serializable {

    private static final long serialVersionUID = -3440061414071692254L;

    /**
     * 请求失败时的错误码
     */
    public int status = 1;

    /** 错误描述 */
    public String msg;

    /**
     * 数据
     */
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess( ){
        return status == 200;
    }

    public static BaseBean fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(BaseBean.class, clazz);
        BaseBean bean = null;
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
        Type objectType = type(BaseBean.class, clazz);
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


}
