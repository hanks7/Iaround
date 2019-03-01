package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.LocationUtil;

import java.util.LinkedHashMap;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/6/12 11:46
 * Email：15369302822@163.com
 */
public class ChatBarHttpProtocol {

    /**关注接口*/
    private static String ATTENTION = "/v1/chatbar/top/my";
    /**热门接口*/
    private static String POPULAR = "/v1/chatbar/top/hot";
    /**家族接口*/
    private static String FAMILY = "/v1/chatbar/top/family";

    public static ChatBarHttpProtocol instance;

    public static ChatBarHttpProtocol getInstance() {
        if (instance == null) {
            instance = new ChatBarHttpProtocol();
        }
        return instance;
    }

    public static long getData(Context context, String url,
                                 LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map,
                ConnectorManage.HTTP_CHATBAR, callback);
    }
    /**
     * 获取聊吧首页关注列表数据
     * @param pageNum
     *              页码
     * @param pageSize
     *              页面条数
     * @return
     */
    public long getChatBarAttentionData(Context context,int pageNum,int pageSize,HttpCallBack callBack)
    {
        LinkedHashMap<String,Object> params = new LinkedHashMap<>();
//        String contentTransType = "011";
//        params.put("contentTransType",contentTransType);
        params.put("page_no",pageNum);
        params.put("page_size",pageSize);
        return getData(context,ATTENTION,params,callBack);
    }
    public long getChatBarPopularData(Context context,int pageNum,int pageSize,HttpCallBack callBack)
    {
        LinkedHashMap<String,Object> params = new LinkedHashMap<>();
        params.put("plat",1);
        params.put("province" , LocationUtil.getCurrentGeo( context ).getProvince( ));
        params.put("city" ,LocationUtil.getCurrentGeo( context ).getCity( ));
        params.put("page_no",pageNum);
        params.put("page_size",pageSize);
        return getData(context,POPULAR,params,callBack);
    }
    public long getChatBarFamilyData(Context context,int pageNum,int pageSize,HttpCallBack callBack)
    {
        LinkedHashMap<String,Object> params = new LinkedHashMap<>();
//        String contentTransType = "011";
//        params.put("contentTransType",contentTransType);
        params.put("page_no",pageNum);
        params.put("page_size",pageSize);
        return getData(context,FAMILY,params,callBack);
    }
}
