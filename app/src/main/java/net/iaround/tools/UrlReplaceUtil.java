package net.iaround.tools;

import net.iaround.conf.Common;
import net.iaround.conf.Config;

import java.util.HashMap;

/**
 * Created by liangyuanhuan on 14/09/2017.
 * 作用：将 URL 中的域名替换成服务器配置指定的域名
 * 原因：URL中的域名可能被封，但这些URL已经写入到数据库中，接口服务返回这些被封的URL，
 * 为了正确访问，所以要做替换。
 * 1) 图片域名替换
 * Config.sPictureUrlBak 为服务端返回的替换原则。
 * Config.sPictureUrlBak 的 格式为：  n1:w1;n2:w2 或者为 "" 字符串
 * <p>
 * 2) 其他需要替换的
 */

public class UrlReplaceUtil {

    public static UrlReplaceUtil getInstance() {
        if (sInstance == null) {
            synchronized (UrlReplaceUtil.class) {
                if (sInstance == null) {
                    sInstance = new UrlReplaceUtil();
                }
            }
        }
        return sInstance;
    }

    private UrlReplaceUtil() {
        if (Config.DEBUG) {
            Config.sPictureUrlBak = "p1.iaround.com:p1.iaround.net;p2.iaround.com:p2.iaround.net;p3.iaround.com:p3.iaround.net;p4.iaround.com:p4.iaround.net;f.iaround.com:f.iaround.net";
        }
    }

    public String replaceImageUrl(String originalUrl) {

        try {
            if (false == mParseImageReplaceUrl) {
                parseImageReplaceRule();
            }
            if (null == mImageReplaceRule || mImageReplaceRule.size() == 0) {
                return originalUrl;
            }
            if (!originalUrl.startsWith("http://")) {
                return originalUrl;
            }

            int end = originalUrl.indexOf("/", 8);
            if (-1 == end) {
                return originalUrl;
            }

            String name = originalUrl.substring(7, end);
            if (null == name) {
                return originalUrl;
            }
            String replace = mImageReplaceRule.get(name);
            if (replace != null) {
                String url = "http://" + replace + originalUrl.substring(end);
                return url;
            }
        } catch (Exception e) {

        }

        return originalUrl;
    }

    private void parseImageReplaceRule() {
        if (true == mParseImageReplaceUrl) {
            return;
        }
        mParseImageReplaceUrl = true;

        if (CommonFunction.isEmptyOrNullStr(Config.sPictureUrlBak)) {
            return;
        }

        String[] strs = Config.sPictureUrlBak.split(";");
        if (null == strs) {
            return;
        }
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            String[] place = str.split(":");
            if (null != place && place.length == 2) {
                if (place[0].length() > 0 && place[1].length() > 0) {
                    if (null == mImageReplaceRule) {
                        mImageReplaceRule = new HashMap<String, String>();
                    }
                    mImageReplaceRule.put(place[0], place[1]);
                }
            }
        }
    }

    private static UrlReplaceUtil sInstance = null;

    //图片域名替换
    private boolean mParseImageReplaceUrl = false; //是否已经解析
    private HashMap<String, String> mImageReplaceRule = null;

}
