
package net.iaround.tools;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: GsonUtil
 * @Description: 使用Gson的相关json操作
 * @date 2013-12-16 下午2:37:37
 */
public class GsonUtil {

    private Gson mGson;

    private static GsonUtil INSTANCE;

    private GsonUtil() {
        mGson = new Gson();
    }

    public static GsonUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GsonUtil();
        }
        return INSTANCE;
    }

    public Gson getGson() {

        return mGson;
    }

    /**
     * @param <T>
     * @param jsonStr json字符串
     * @param cls     需要转换成的类
     * @return
     * @Title: getServerBean
     * @Description: 将一个json字符串转换成对象
     */
    public <T> T getServerBean(String jsonStr, Class<T> cls) {
        T obj = null;
        try {
            obj = mGson.fromJson(jsonStr, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public <T> List<T> parse2List(String s, Class<T[]> clazz) {
        if (!TextUtils.isEmpty(s)) {
            T[] arr = mGson.fromJson(s, clazz);
            return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
        }
        return null;
    }

    /**
     * @param obj
     * @return
     * @Title: getStringFromJsonObject
     * @Description: 将一个object序列化为json字符串
     */
    public String getStringFromJsonObject(Object obj) {
        String jsonStr = "";
        try {
            jsonStr = mGson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonStr;
    }


    public static JSONObject objDataMerger(Object aimObj, Object orObj) {
        if (aimObj == null || orObj == null) {
            return null;
        }
        HashMap<String, Object> mapAim = toHashMap(aimObj);
        HashMap<String, Object> mapOrObj = toHashMap(orObj);

        JSONObject jsonObj = new JSONObject();
        for (HashMap.Entry<String, Object> entry : mapOrObj.entrySet()) {

            if (entry.getValue() != null) {
                mapAim.put(entry.getKey(), entry.getValue());
//				CommonFunction.log( "shifengxiong" ,
//						entry.getKey( ) + "====" + entry.getValue( ) );
            }

        }
        for (HashMap.Entry<String, Object> entry : mapAim.entrySet()) {
            try {
                jsonObj.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // mapAim.put( entry.getKey( ) , entry.getValue( ) );
            // CommonFunction.log( "shifengxiong" ,entry.getKey(
            // )+"=="+entry.getValue( ));
        }


        return jsonObj;
    }


    private static HashMap<String, Object> toHashMap(Object object) {
        HashMap<String, Object> data = new HashMap<String, Object>();

        // JSONObject jsonObject = JSONObject.
        // JSONObject jsonObject = GsonUtil.getInstance( ).getGson(
        // ).toJsonTree( object );
        String json = GsonUtil.getInstance().getStringFromJsonObject(object);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (jsonObject != null) {

            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = null;
                try {
                    if (!jsonObject.isNull(key)) {
                        value = jsonObject.get(key);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (value != null) {
                    data.put(key, value);
                }
            }
        }
        return data;
    }

    public static HashMap<String, Object> objToMap(Object obj) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        String strJson = GsonUtil.getInstance().getStringFromJsonObject(obj);

        return hashMap;
    }

    /**
     * 把json字符串转换为JavaBean。如果json的根节点就是一个集合，则使用此方法<p>
     * type参数的获取方式为：Type type = new TypeToken<集合泛型>(){}.getType();
     *
     * @param json json字符串
     * @return type 指定要解析成的数据类型
     */
    public <T> T json2Bean(String json, Type type) {
        T bean = null;
        try {
            bean = mGson.fromJson(json, type);
        } catch (Exception e) {
            Log.i("JsonUtil", "解析json数据时出现异常json = " + json, e);
        }
        return bean;
    }


}
