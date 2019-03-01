package net.iaround.model.im;

import android.content.Context;
import android.text.TextUtils;

import net.iaround.model.database.FriendModel;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.Dialect;
import net.iaround.ui.datamodel.WeiboState;

import java.util.ArrayList;

/**
 * Created by shifengxiong on 2016/2/20.
 * /user/info/basic_6_0
 * 最新协议版本 6.0
 */
public class UserBasic extends BaseServerBean
{
	public  BasicInfo basic;
	public BaseUserInfo user;
	public int total;//: 10,/总共项
	public int complete;//: 7,//已完成项
	public int ischat;
	ArrayList< Weibo > weibos;

	public class BasicInfo
	{
		public String address ;//": ",广东省,广州市,天河区",
		public  int height =-1;//": 162,
		public String birthday;//": "1981-11-06",
		public String blood;//": "O",
		public  int 	horoscope =-1;//": 8,
		public  int 	love =-1;//": 11,
		public String company;//": "男女短裤短裤",
		public String dialects;//": "100001,100002",
		public String hometown ;//": "1:中国,10:邯郸,5:河北",
		public  int 	salary =-1;//": -1,
		public String school;//": "和你打开自卖自夸",
		public String department ;//":"xxx院系",
		public  int schoolid =-1 ;//:1001
		public  int departmentid =-1;//:12
		public  int occupation =-1;//": 1,
		public String homepage ;//: "",
		public String turnoffs ;//": "111",
		public  int weight;//": 68,
		public String hobbies;//: "才符合环保 vv",
		public  int relation =-1;//: 2,
		public  int bodytype =-1; //: 5,
		public  int beginage =-1;//: 20,
		public  int endage =-1;//": 36,
		public String dialectsname;//": "普通話,粵語",
		public String bindphone ;//": null,
		public  int visitnum =-1;//": 135,
		public  int fansnum =-1;//": 6,
		public  int fanstatus =-1;//" 0,
		public String thinktext;//: "享受烛晚餐",
		public  int withwho ;//": 1,
		public String wantinfo ;//" null,
		public String secretinfo;//": null,
		public String favoritesids;//: "1,2" //交友喜好ids
		public String favorites; //: "萌萌大大叔，小鲜肉" //交友喜好名称
		public  int loveexp =-1;//: 0,//恋爱经历
		public  int income =-1;//": 0",//收入说明
		public  int house =-1;//: 0,//住房情况
		public  int car =-1; //: 0,//购车情况[新增]
		public int islocation =-1;
	}

	public class Weibo  //微博
	{
		public String wid;//:"3462t243343", //微博ID
		public String nickname;//:"李开复",  //微博昵称
		public String isauth;//:"y", //是否认证
		public String desc;//:"创新工场CEO"  //认证信息或者个人介绍
		public int type;//:1   //微博类型：（1：新浪微博，2：腾讯微博）
		public String leveltag; //:"1010" //微博等级标识
		public String accesstoken;//:"1010" //微博用户身份
		public String openid;//:"1010" //微博用户ID
		public long expires;//:"1010" //微博用户过期时间

	}

	public void setUserBasicInfor(Context context, Me me )
	{
		if(me==null)return;
		if(basic!=null)
		{
			me.setFavoritesids( basic.favoritesids ) ;
			me.setFavorites( basic.favorites ) ;
			me.setHouse( basic.house );
			me.setLoveExp( basic.loveexp );
			me.setBuyCar( basic.car );
			me.setIncome( basic.income );

			me.setAddress( basic.address );
			me.setRelationship( basic.relation );
			me.setVisitNum( basic.visitnum );
			me.setFansNum( basic.fansnum );
			me.setBirth( basic.birthday, false );
			me.setBloodType( basic.blood);
			me.setHoroscope( basic.horoscope);
			String thinktext = basic.thinktext;
			int withwho = basic.withwho;
			int beginage = basic.beginage;
			int endage = basic.endage;
			me.setSign( FriendModel.getInstance( context ).getSign( thinktext , withwho ,
				beginage , endage ) );
			int salary = basic.salary ;
			if ( salary >= 0 )
			{
				salary--;
			}
			me.setMonthlySalary( salary );
			me.setHeight( basic.height );
			me.setWeight( basic.weight );
			me.setBodyType( basic.bodytype );
			me.setLoveStatus(basic.love );
			String edus = basic.school;
			if ( CommonFunction.isEmptyOrNullStr( edus ) )
			{
				edus = "";
			}
			me.setEducations( edus );
			me.setTurnoffs( basic.turnoffs );
			me.setJob( basic.occupation );
			String hobbies = basic.hobbies;
			me.setHobbies( hobbies );
			String school = basic.school;
			me.setSchool( school == null ? "" : school );
			me.setSchoolid( basic.schoolid );
			String department = basic.department;
			me.setDepatrment( CommonFunction.isEmptyOrNullStr( department ) ? "" : department );
			me.setDepartmentid( basic.departmentid);
			String hometown = basic.hometown ;
			me.setHometown( hometown == null ? "" : hometown );
			String company = basic.company ;
			me.setCompany( company == null ? "" : company );
			if ( !TextUtils.isEmpty( basic.dialects)
				&& !TextUtils.isEmpty( basic.dialectsname ) )
			{
				String[ ] dialectids = basic.dialects.split( "[,]" );
				String[ ] dialectnames = basic.dialectsname.split( "[,]" );
				if ( dialectids != null && dialectnames != null
					&& dialectids.length == dialectnames.length )
				{
					ArrayList< Dialect > dialects = new ArrayList< Dialect >( );
					int size = dialectids.length;
					for ( int i = 0 ; i < size ; i++ )
					{
						Dialect dialect = new Dialect( );
						dialect.id = Integer.valueOf( dialectids[ i ] );
						dialect.name = dialectnames[ i ];
						dialects.add( dialect );
					}
					me.setDialects( dialects );
				}
			}

			String homePage = basic.homepage ;
			if ( CommonFunction.isEmptyOrNullStr( homePage ) )
			{
				homePage = "";
			}
			me.setHomePage( homePage );
			me.setPublicLocation( basic.islocation  );
		}
		if(user!=null)
		{
			me.setUid( user.userid );
			me.setNickname( user.nickname );
			me.setNoteName( user.notes );
			me.setIcon( user.icon );
			me.setViplevel( user.viplevel );
			me.setSVip( user.svip );
			String gender = user. gender;
			me.setSex( "m".equals( gender ) ? 1 : ( "f".equals( gender ) ? 2 : 0 ) );
			me.setLat( user.lat );
			me.setLng( user.lng );
			me.setAge( user.age );
			me.setBanned( user.forbid );
			me.setPersonalInfor( user.selftext );
			me.setOnline( "y".equals( user.isonline ) );
			me.setLastLoginTime( user.lastonlinetime );

			me.setTodayphotos( user.todayphotos );
			me.setTodayphotostotal( user.todayphotostotal );

			me.setPhotouploadleft( user.photouploadleft );
			me.setPhotouploadtotal( user.photouploadtotal );

			me.setDistance( user.distance );
			me.setPhotoNum( user.photonum );

			me.setInfoTotal( total );
			me.setInfoComplete( complete );

			me.setExpire( user.expire );

		}
		if(weibos!=null)
		{
			int size = weibos.size();
			ArrayList<WeiboState> weiboes = new ArrayList< WeiboState >( );
			for ( int i = 0 ; i < size ; i++ )
			{
				Weibo weiboJson = weibos.get( i );
				if ( weiboJson != null )
				{
					WeiboState weibo = new WeiboState( );
					weibo.setRegistered( true );
					weibo.setId( weiboJson.wid );
					weibo.setOpenid( weiboJson.openid );
					weibo.setExpires( weiboJson.expires );
					weibo.setAccesstoken( weiboJson.accesstoken );
					weibo.setName( weiboJson.nickname );
					weibo.setAuthed( "y".equals( weiboJson.isauth ) );
					weibo.setSign( weiboJson.desc );
					weibo.setVerifiedReason( weiboJson.desc );
					weibo.setType( weiboJson.type );

					weiboes.add( weibo );
				}
			}
			WeiboState[ ] states = new WeiboState[ weiboes.size( ) ];

			for ( int i = 0 ; i < states.length ; i++ )
			{
				states[ i ] = weiboes.get( i );
			}
			me.setWeibo( weiboes );
		}
	}
}
