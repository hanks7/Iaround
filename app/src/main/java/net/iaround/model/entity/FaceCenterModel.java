
package net.iaround.model.entity;


import android.content.Context;
import android.support.v4.util.LongSparseArray;

import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.tools.FaceManager.FaceLogoIcon;
import net.iaround.tools.JsonParseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FaceCenterModel{
	public enum FaceCenterReqTypes
	{
		FaceCenterMainData ,
		FaceDetailData ,
		BuyFaceData ,
		OwnFacesData
	}
	
	private static FaceCenterModel faceCenterModel;
	private LongSparseArray< FaceCenterReqTypes > reqCodeToType;
	private Map< FaceCenterReqTypes , Long > reqTypeTocode;
	private Context context;
	
	/** 已拥有的表情列表 */
	public static ArrayList< Face > ownFace = new ArrayList< Face >( );
    /** 已删除的表情列表 */
	public static ArrayList< Face > delFace = new ArrayList< Face >( );
	
	/** 在表情中心下载表情 ，若下载失败保存该表情，通知我的表情页更新*/
	public static ArrayList< Face > upMyfaceViewFailList = new ArrayList< Face  >();
	/** 在表情中心下载表情 ，保存进度数据，提供给我的表情页进行刷新*/
	public static Map< Face , Integer > upMyfaceViewPrecentMap = new HashMap< Face , Integer >( );
	
	/** 在我的表情页下载表情 ，若下载失败保存该表情，通知表情中心更新*/
	public static ArrayList< Face > upFacemainViewFailList = new ArrayList< Face >();
	/** 在我的表情页下载表情 ，保存进度数据，提供给表情中心进行刷新*/
	public static Map< Face , Integer > upFacemainViewPrecentMap = new HashMap< Face , Integer >( );
	
	/** 在我的表情页或表情中心下载表情 ，若下载失败保存该表情，通知表情详情更新*/
	public static ArrayList< Face > upFaceDetailFailList = new ArrayList< Face >();
	/** 在我的表情页或表情中心下载表情 ，保存进度数据，提供给表情详情进行刷新*/
	public static Map< Face , Integer > upFaceDetailPrecentMap = new HashMap< Face , Integer >( );

	
	private FaceCenterModel(Context context )
	{
		this.reqCodeToType = new LongSparseArray< FaceCenterReqTypes >( );
		this.reqTypeTocode = new ConcurrentHashMap< FaceCenterReqTypes , Long >( );
		this.context = context;
	}

    private void mark( long flag , FaceCenterReqTypes reqType )
	{
		Long lastCode = reqTypeTocode.get( reqType );
		if ( lastCode != null && lastCode > 0 )
		{
			reqCodeToType.remove( lastCode );
		}
		
		reqCodeToType.put( flag , reqType );
		reqTypeTocode.put( reqType , flag );
	}
	
	public static FaceCenterModel getInstance( Context context )
	{
		if ( faceCenterModel == null )
		{
			faceCenterModel = new FaceCenterModel( context );
		}
		return faceCenterModel;
	}
	
	public FaceCenterReqTypes getReqType( long reqCode )
	{
		return reqCodeToType.get( reqCode );
	}

	public long getFaceCenterData( Context context , int pageNo , int pageSize ,
								   HttpCallBack callback )
	{
		long flag = BusinessHttpProtocol.getFaceMainData( context , pageNo , pageSize , callback );
		return 0;
	}

	public long buyFace( Context context , int faceId , HttpCallBack callback )
	{
		long flag = BusinessHttpProtocol.buyFace( context , faceId , callback );
//		mark( flag , FaceCenterReqTypes.BuyFaceData );
		return 0;
	}

	public long getFaceDetailData( Context context , int faceId , HttpCallBack callback )
	{
		long flag = BusinessHttpProtocol.getFaceDetailData( context , faceId , callback );
//		mark( flag , FaceCenterReqTypes.FaceDetailData );
		return flag;
	}
	
	public HashMap< String , Object > getRes( String jsonString , long reqCode )
	{
		try
		{
			JSONObject json = new JSONObject( jsonString );
			HashMap< String , Object > resMap = new HashMap< String , Object >( );
			int status = JsonParseUtil.getInt( json , "status" , -1 );
			resMap.put( "status" , status );
			FaceCenterReqTypes reqType = getReqType( reqCode );
			reqCodeToType.remove( reqCode );
			resMap.put( "reqType" , reqType );
			if ( status != 200 )
			{
				resMap.put( "error" , jsonString );
			}
			else if ( reqType != null )
			{
				switch ( reqType )
				{
					case FaceCenterMainData :
						getFaceCenterDataFromRes( json , resMap );
						break;
					case FaceDetailData :
						getFaceDetailDataFromRes( json , resMap );
						break;
					case OwnFacesData :
						getOwnFacesDataFromRes( json , resMap );
					default :
						break;
				}
			}
			return resMap;
		}
		catch ( JSONException e )
		{
			return new HashMap< String , Object >();
		}
	}
	
	private void getFaceCenterDataFromRes( JSONObject json , HashMap< String , Object > resMap )
	{
		int pageno = JsonParseUtil.getInt( json , "pageno" , 0 );
		int pagesize = JsonParseUtil.getInt( json , "pagesize" , 0 );
		int amount = JsonParseUtil.getInt( json , "amount" , 0 );
		resMap.put( "pageno" , pageno );
		int pagecount = amount % pagesize > 0 ? amount / pagesize + 1 : amount / pagesize;
		resMap.put( "pagecount" , pagecount );
		resMap.put( "amount" , amount );
		JSONArray adArray = JsonParseUtil.getJSONArray( json , "banners" );
		if ( adArray != null )
		{
			int length = adArray.length( );
			List< FaceAd > faceAds = new ArrayList< FaceAd >( );
			for ( int i = 0 ; i < length ; i++ )
			{
				JSONObject obj = JsonParseUtil.getJSONObject( adArray , i );
				if ( obj != null )
				{
					FaceAd faceAd = new FaceAd( );
					faceAd.setId( JsonParseUtil.getInt( obj , "bannerid" , -1 ) );
					faceAd.setImgUrl( JsonParseUtil.getString( obj , "image" , "" ) );
					faceAd.setFaceId( JsonParseUtil.getInt( obj , "pkgid" , -1 ) );
					faceAd.setType( JsonParseUtil.getInt( obj , "opentype" , -1 ) );
					faceAd.setCurrencyType( JsonParseUtil.getInt( obj , "currencytype" , -1 ) );
					faceAd.setContent( JsonParseUtil.getString( obj , "content" , "" ) );
					faceAd.setJumpUrl( JsonParseUtil.getString( obj , "url" , "" ) );
					faceAds.add( faceAd );
				}
			}
			resMap.put( "ads" , faceAds );
		}
		
		JSONArray mapArray = JsonParseUtil.getJSONArray( json , "maps" );
		if ( mapArray != null )
		{
			int length = mapArray.length( );
			List< Face > faces = new ArrayList< Face >( );
			for ( int i = 0 ; i < length ; i++ )
			{
				JSONObject obj = JsonParseUtil.getJSONObject( mapArray , i );
				if ( obj != null )
				{
					Face face = new Face( );
					face.setFaceid( JsonParseUtil.getInt( obj , "pkgid" , -1 ) );
					face.setTitle( JsonParseUtil.getString( obj , "title" , "" ) );
					face.setOwn( JsonParseUtil.getInt( obj , "own" , -1 ) );
					face.setDownUrl( JsonParseUtil.getString( obj , "downurl" , "" ) );
					face.setGoldNum( JsonParseUtil.getInt( obj , "goldnum" , -1 ) );
					face.setVipgoldnum( JsonParseUtil.getInt( obj , "vipgoldnum" , -1 ) );
					face.setName( JsonParseUtil.getString( obj , "name" , "" ) );
					face.setIcon( JsonParseUtil.getString( obj , "icon" , "" ) );
					face.setNewflag( JsonParseUtil.getInt( obj , "newflag" , -1 ) );
					face.setFeetype( JsonParseUtil.getInt( obj , "feetype" , -1 ) );
					face.setDynamic( JsonParseUtil.getInt( obj , "dynamic" , -1 ) );
					face.setTagname( JsonParseUtil.getString( obj , "tagname" , "" ) );
					face.setCurrencytype( JsonParseUtil.getInt( obj , "currencytype" , -1 ) );
					face.setOldgoldnum( JsonParseUtil.getString( obj , "oldgoldnum" , "" ) );
					face.setEndTime( JsonParseUtil.getLong( obj , "endTime" , 0 ) );
					face.setOpenType( JsonParseUtil.getInt( obj , "opentype" , 0 ) );
					face.setActiveurl( JsonParseUtil.getString( obj , "activeurl" , "" ) );
					
					faces.add( face );
				}
			}
			resMap.put( "faces" , faces );
		}
	}
	
	private void getFaceDetailDataFromRes( JSONObject json , HashMap< String , Object > resMap )
	{
		Face face = new Face( );
		JSONObject obj = JsonParseUtil.getJSONObject( json , "map" );
		if ( obj != null )
		{
			face.setFaceid( JsonParseUtil.getInt( obj , "pkgid" , -1 ) );
			face.setOwn( JsonParseUtil.getInt( obj , "own" , -1 ) );
			face.setDownUrl( JsonParseUtil.getString( obj , "downurl" , "" ) );
			face.setGoldNum( JsonParseUtil.getInt( obj , "goldnum" , -1 ) );
			face.setVipgoldnum( JsonParseUtil.getInt( obj , "vipgoldnum" , -1 ) );
			face.setIcon( JsonParseUtil.getString( obj , "contentimage" , "" ) );
			face.setBackground( JsonParseUtil.getString( obj , "background" , "" ) );
			face.setDescrib( JsonParseUtil.getString( obj , "describ" , "" ) );
			face.setImage( JsonParseUtil.getString( obj , "image" , "" ) );
			face.setTitle( JsonParseUtil.getString( obj , "title" , "" ) );
			face.setNewflag( JsonParseUtil.getInt( obj , "newflag" , -1 ) );
			face.setFeetype( JsonParseUtil.getInt( obj , "feetype" , -1 ) );
			face.setDynamic( JsonParseUtil.getInt( obj , "dynamic" , -1 ) );
			face.setTagname( JsonParseUtil.getString( obj , "tagname" , "" ) );
			face.setOldgoldnum( JsonParseUtil.getString( obj , "oldgoldnum" , "" ) );
			face.setHeadcontent( JsonParseUtil.getString( obj , "headcontent" , "" ) );
			face.setImplcontent( JsonParseUtil.getString( obj , "implcontent" , "" ) );
			face.setActiveurl( JsonParseUtil.getString( obj , "activeurl" , "" ) );
			face.setAttend( JsonParseUtil.getInt( obj , "attend" , -1 ) );
			face.setOpenType( JsonParseUtil.getInt( obj , "opentype" , -1 ) );
			face.setCurrencytype( JsonParseUtil.getInt( obj , "currencytype" , -1 ) );
			face.setEndTime( JsonParseUtil.getLong( obj , "endTime" , 0 ) );
			face.setImagenum( JsonParseUtil.getInt( obj , "imagenum" , 0 ) );
			face.setBaseurl(  JsonParseUtil.getString( obj , "baseurl" , "" )  );
		}
		
		JSONObject authorObj = JsonParseUtil.getJSONObject( json , "author" );
		if ( authorObj != null )
		{
			face.setAuthorIcon( JsonParseUtil.getString( authorObj , "icon" , "" ) );
			face.setAuthorDescribe( JsonParseUtil.getString( authorObj , "describe" , "" ) );
			face.setAuthorName( JsonParseUtil.getString( authorObj , "name" , "" ) );
		}
		
		resMap.put( "face" , face );
	}
	
	private void getOwnFacesDataFromRes( JSONObject json , HashMap< String , Object > resMap )
	{
		JSONArray mapArray = JsonParseUtil.getJSONArray( json , "maps" );
		if ( mapArray != null )
		{
			int length = mapArray.length( );
			List<FaceLogoIcon> faces = new ArrayList<FaceLogoIcon>( );
			for ( int i = 0 ; i < length ; i++ )
			{
				JSONObject obj = JsonParseUtil.getJSONObject( mapArray , i );
				if ( obj != null )
				{
					FaceLogoIcon face = new FaceLogoIcon( );
					face.pkgId = JsonParseUtil.getInt( obj , "pkgid" , -1 );
					face.feetType = JsonParseUtil.getInt( obj , "feetype" , -1 );
					face.valid = JsonParseUtil.getInt( obj , "valid" , -1 );
					faces.add( face );
				}
			}
			resMap.put( "faces" , faces );
		}
	}
	
	
	/**
	 * 释放资源
	 */
	public void reset()
	{
		ownFace.clear( );
		delFace.clear( );
		clearPrecentData( );
	}
	
	/**
	 * 清除下载进度条的数据
	 */
	private void clearPrecentData()
	{
		upFacemainViewFailList.clear( );
		upFacemainViewPrecentMap.clear( );
		upMyfaceViewFailList.clear( );
		upMyfaceViewPrecentMap.clear( );
		upFaceDetailFailList.clear( );
		upFaceDetailPrecentMap.clear( );
	}
	
}
