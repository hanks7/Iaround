package net.iaround.utils;

/**
 * Created by liangyuanhuan on 12/09/2017.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.android.volley.CipherUtils;

public class YJConnectorConfig {
    static int serverIndex = 0;

    public YJConnectorConfig() {
    }

    public static void setServer(int server) {
        serverIndex = server;
    }

    public static String getSigniture(Map<String, Object> param, String key) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        int i = 0;
        String signiture;
        if(param != null) {
            for(Iterator kek = param.entrySet().iterator(); kek.hasNext(); ++i) {
                Entry param1 = (Entry)kek.next();
                signiture = "" + param1.getValue();
                if(i == 0) {
//                    try {
//                        sb.append((String)param1.getKey() + "=" + URLEncoder.encode(signiture, "UTF-8"));
//                    } catch (UnsupportedEncodingException var11) {
//                        sb.append((String)param1.getKey() + "=" + URLEncoder.encode(signiture));
//                    }
                    sb.append(param1.getKey() + "=" + signiture);

                    sb1.append(param1.getKey() + "=" + signiture);
                } else {
//                    try {
//                        sb.append("&" + (String)param1.getKey() + "=" + URLEncoder.encode(signiture, "UTF-8"));
//                    } catch (UnsupportedEncodingException var10) {
//                        sb.append("&" + (String)param1.getKey() + "=" + URLEncoder.encode(signiture));
//                    }
                    sb.append("&" + param1.getKey() + "=" + signiture);

                    sb1.append("&" + param1.getKey() + "=" + signiture);
                }
            }
        }

        String var12 = sb1.toString();
        if("".equals(var12)) {
            var12 = "key=" + key;
        } else {
            var12 = "key=" + key + "&" + var12;
        }

        String var13 = CipherUtils.getKeyBySeed(key, serverIndex);
        var12 = var12 + "&kek=" + var13;
        signiture = "";

        try {
            signiture = URLEncoder.encode(CipherUtils.sha1_base64(var12.getBytes("utf-8")));
        } catch (UnsupportedEncodingException var9) {
            var9.printStackTrace();
        }

        String paramStr = "signiture=" + signiture + "&key=" + key;
        return sb.toString().equals("")?paramStr:paramStr + "&" + sb.toString();
    }
}
