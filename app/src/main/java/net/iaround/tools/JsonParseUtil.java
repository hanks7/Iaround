
package net.iaround.tools;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonParseUtil {
    private JsonParseUtil() {
    }

    public static String getString(JSONObject obj, String name, String defaultValue) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            if (obj.isNull(name)) {
                return defaultValue;
            }
            try {
                return obj.getString(name);

            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static boolean getBoolean(JSONObject obj, String name, boolean defaultValue) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            try {
                return obj.getBoolean(name);
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static int getInt(JSONObject obj, String name, int defaultValue) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            try {
                return obj.getInt(name);
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static long getLong(JSONObject obj, String name, long defaultValue) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            try {
                return obj.getLong(name);
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static double getFloat(JSONObject obj, String name, double defaultValue) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            try {
                return obj.getDouble(name);
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static JSONArray getJSONArray(JSONObject obj, String name) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            try {
                return obj.getJSONArray(name);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONObject obj, String name) {
        if (obj != null && !TextUtils.isEmpty(name) && obj.has(name)) {
            try {
                return obj.getJSONObject(name);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public static JSONObject getJSONObject(JSONArray array, int i) {
        if (array != null && i >= 0) {
            try {
                return array.getJSONObject(i);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }
}
