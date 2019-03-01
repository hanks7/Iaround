
package net.iaround.tools;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;

import net.iaround.model.entity.GeoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 使用大众点评的api或者用高德地图获取周边数据
 */
public class MapSearchIaround implements OnPoiSearchListener {
    List<GeoData> addresses;


    Context mContext;


    private SearchIaroundResultListener onListenerResult;

    final String[] categorys =
            {"生活服务", "美食", "购物", "汽车服务", "旅行社", "酒店", "运动健身", "休闲娱乐"};

    int subCategory;
    int radius;

    private boolean hasNextPage;
    private int pageNo;
    private int totlePage;
    private int totleDataGet = 0;

    private PoiResult poiResult;
    private List<PoiItem> poiItems;// poi数据
    private boolean isSwitchDianpin = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {


            switch (msg.what) {

                case 0:
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(String.valueOf(msg.obj));
                        JSONArray busineesses = (JSONArray) jsonObject.get("businesses");
                        int total = jsonObject.optInt("total_count");
                        int count = jsonObject.optInt("count");

                        totleDataGet += count;

                        if (totleDataGet >= total) {
                            subCategory++;
                            if (subCategory >= categorys.length)
                            // if(radius>=10000)
                            {
                                hasNextPage = false;
                            } else {
                                pageNo = 0;
                                totleDataGet = 0;
                                radius += 200;
                                totlePage++;
                            }
                        }

                        int mLat;
                        int mLng;
                        long curTime = System.currentTimeMillis();
                        addresses.clear();
                        for (int i = 0, iMax = busineesses.length(); i < iMax; i++) {
                            GeoData addInfo = new GeoData();

                            addInfo.setAddress(busineesses.getJSONObject(i).optString(
                                    "address"));

                            String city = busineesses.getJSONObject(i).optString(
                                    "city");
                            addInfo.setSimpleAddress(city + "," + busineesses.getJSONObject(i)
                                    .optString("name"));
                            mLat = (int) (busineesses.getJSONObject(i).optDouble(
                                    "latitude") * 1E6);
                            mLng = (int) (busineesses.getJSONObject(i).optDouble(
                                    "longitude") * 1E6);
                            addInfo.setUpdateTime(curTime);

                            addInfo.setLat(mLat);
                            addInfo.setLng(mLng);

                            addresses.add(addInfo);

                        }
                        if (onListenerResult != null) {
                            onListenerResult.onSearchResulted(addresses);
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }

                    break;

                case 1:


                    break;
                case 2:

                    if (poiItems != null && poiItems.size() > 0) {

                        int mLat;
                        int mLng;
                        long curTime = System.currentTimeMillis();
                        addresses.clear();
                        for (int i = 0, iMax = poiItems.size(); i < iMax; i++) {
                            GeoData addInfo = new GeoData();
                            PoiItem poi = poiItems.get(i);
                            String city = poiItems.get(i).getCityName();
                            addInfo.setAddress(poiItems.get(i).getSnippet());
                            addInfo.setSimpleAddress(city + "," + poiItems.get(i).toString());

                            mLat = (int) poiItems.get(i).getLatLonPoint().getLatitude();
                            mLng = (int) poiItems.get(i).getLatLonPoint().getLongitude();
                            addInfo.setLat(mLat);
                            addInfo.setCurrentTime(curTime);
                            addInfo.setLng(mLng);

                            addresses.add(addInfo);

                        }


                    }
                    if (onListenerResult != null) {
                        onListenerResult.onSearchResulted(addresses);
                    }
                    break;
            }
        }
    };

    protected void init(Context context) {
        mContext = context;

        addresses = new ArrayList<GeoData>();

        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(mContext);
        if (sp.has(SharedPreferenceUtil.SWITCH)) {
            String currentItemStatus = sp.getString(SharedPreferenceUtil.SWITCH);
            if (currentItemStatus.length() >= 1) {
                isSwitchDianpin = currentItemStatus.charAt(0) == '1';
            }

        }

        pageNo = 1;
        totlePage = 2;

    }

//	public void request( int page ,String keyWord) throws Exception
//	{
//
//		final int pagenum = page;
//
//		new Thread( )
//		{
//			public void run( )
//			{
//				try
//				{
//					String apiUrl = "http://api.dianping.com/v1/business/find_businesses";
//					String appKey = "88715318"; // 请替换为自己的 App Key 和 App secret
//					String secret = "67215dadd62d4f1fb3cc29cbff006de3";
//					Map< String , String > paramMap = new HashMap< String , String >( );
//
//					String category = categorys[ subCategory ];
//					GeoData geo = LocationUtil.getCurrentGeo( mContext );
//
//					Double latitude = geo.getLat( ) * 1.0 / 1E6;
//					Double longitude = geo.getLng( ) * 1.0 / 1E6;
//
//
//					paramMap.put( "latitude" , latitude + "" );
//					paramMap.put( "longitude" , longitude + "" );
//					paramMap.put( "category" , category );
//					paramMap.put( "limit" , "20" );
//
//					// paramMap.put( "radius" , String.valueOf( radius ) );
//					paramMap.put( "offset_type" , "1" );
//					paramMap.put( "platform" , "2" );
//					paramMap.put( "out_offset_type" , "1" );
//					paramMap.put( "sort" , "7" );
//					paramMap.put( "format" , "json" );
//					paramMap.put( "page" , pagenum + "" );
//
//					StringBuilder stringBuilder = new StringBuilder( );
//
//					// 对参数名进行字典排序
//					String[ ] keyArray = paramMap.keySet( ).toArray( new String[ 0 ] );
//					Arrays.sort( keyArray );
//					// 拼接有序的参数名-值串
//					stringBuilder.append( appKey );
//					for ( String key : keyArray )
//					{
//						stringBuilder.append( key ).append( paramMap.get( key ) );
//					}
//					String codes = stringBuilder.append( secret ).toString( );
//					// SHA-1编码，
//					// 这里使用的是Apache-codec，即可获得签名(shaHex()会首先将中文转换为UTF8编码然后进行sha1计算，使用其他的工具包请注意UTF8编码转换)
//					String sign = DigestUtils.shaHex( codes )
//							.toUpperCase( );
//
//					// 添加签名
//					stringBuilder = new StringBuilder( );
//					stringBuilder.append( "appkey=" ).append( appKey ).append( "&sign=" )
//							.append( sign );
//					for ( Entry< String , String > entry : paramMap.entrySet( ) )
//					{
//						stringBuilder.append( '&' ).append( entry.getKey( ) ).append( '=' )
//								.append( entry.getValue( ) );
//					}
//					String queryString = stringBuilder.toString( );
//
//					StringBuffer response = new StringBuffer( );
//					HttpClientParams httpConnectionParams = new HttpClientParams( );
//					httpConnectionParams.setConnectionManagerTimeout( 1000 );
//					HttpClient client = new HttpClient( httpConnectionParams );
//					HttpMethod method = new GetMethod( apiUrl );
//
//					BufferedReader reader = null;
//					String encodeQuery = URIUtil.encodeQuery( queryString , "UTF-8" ); // UTF-8
//																						// 请求
//					method.setQueryString( encodeQuery );
//					client.executeMethod( method );
//					reader = new BufferedReader( new InputStreamReader(
//							method.getResponseBodyAsStream( ) , "UTF-8" ) );
//					String line = null;
//					while ( ( line = reader.readLine( ) ) != null )
//					{
//						response.append( line )
//								.append( System.getProperty( "line.separator" ) );
//					}
//					reader.close( );
//					method.releaseConnection( );
//
//					// System.out.println(response.toString());
//
//					//
//					CommonFunction.log( "shifengxiong" , "result==" + response.toString( ) );
//
//					Message msg = new Message( );
//					msg.what = 0;
//					msg.obj = response.toString( );
//					mHandler.sendMessage( msg );
//				}
//				catch ( Exception e )
//				{
//					// TODO: handle exception
//				}
//			}
//		}.start( );
//	}

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {
        CommonFunction.log("shifengxiong", "PoiItemDetail resutl =" + i);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

        // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
        if (poiResult != null) {
            poiItems = poiResult.getPois();
            totlePage = poiResult.getPageCount();
            CommonFunction.log("shifengxiong", "getPageCount  === " + totlePage);
        }
        mHandler.sendEmptyMessage(2);
    }

    /*
     * 通过高德地图搜索周边
     */
    protected void doSearchIaroundByAmap(int page, String keyword) {
        // 高德地图方式搜索
        String key = "写字楼|小区|学校|餐饮|酒店|景点|影院";
        if (!CommonFunction.isEmptyOrNullStr(keyword)) {
            key = keyword;
        }
        Query query = new PoiSearch.Query("", key, "");//
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）

        query.setPageSize(20);
        query.setPageNum(page - 1);


        GeoData geo = LocationUtil.getCurrentGeo(mContext);


        Double latitude = geo.getLat() * 1.0 / 1E6;
        Double longitude = geo.getLng() * 1.0 / 1E6;
        SearchBound bound = new PoiSearch.SearchBound(
                new LatLonPoint(latitude, longitude), 2000);
        //
        //
        PoiSearch poiSearch = new PoiSearch(mContext, query);
        //
        poiSearch.setBound(bound);
        //
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();

    }


    public interface SearchIaroundResultListener {
        // 返回搜索到的周边数据，
        void onSearchResulted(List<GeoData> geoDatas);
    }

    public void setSearchIaroundResult(SearchIaroundResultListener result) {
        onListenerResult = result;
    }

    public Boolean doSearchIaround(Context context, int searchPage, String keyWord) {
        if (mContext == null) {
            init(context);
        }

        if (totlePage < searchPage)
            return false;

        if (isSwitchDianpin) {
            try {
//				request( searchPage ,keyWord);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        } else {
            doSearchIaroundByAmap(searchPage, keyWord);
        }

        return hasNextPage;
    }


}
