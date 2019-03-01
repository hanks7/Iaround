package net.iaround.model.entity;


import android.content.Context;

import net.iaround.conf.Common;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.tools.SharedPreferenceUtil;

/**
 * Class: 基本信息
 * Author：gh
 * Date: 2017/1/3 18:51
 * Email：jt_gaohang@163.com
 */
public class UserProfileBean extends BaseServerBean{
    public String uid;
    public String vip;
    public String level;
    public String headPic;
    public String nickname;
    public String gender;
    public int age;
    public String birthday;
    public String horoscope;
    public int complete;
    public String withwho;
    public String isbind;
    public String firstlogin;
    public String switchs;
    public String newonoffs;
    public int opengame;
    public int openstatics;
    public String servertime;
    public String token;


    public void loginSuccess(Context context)
    {
        Me me = Common.getInstance( ).loginUser;
        if ( Long.valueOf(servertime) == 0 )
        {
            servertime = ""+System.currentTimeMillis( );
        }
        Common.getInstance( ).serverToClientTime = Long.valueOf(servertime) - System.currentTimeMillis( );
        SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance( context );

//        if(user!=null)
//        {
            me.setUid( Integer.valueOf(uid) );
            Common.getInstance( ).loginUser.setUid( Integer.valueOf(uid) );
            me.setNickname( nickname );
            me.setIcon( headPic );
            me.setViplevel( Integer.valueOf(level));
            me.setSVip( Integer.valueOf(vip));
            me.setSex( "m".equals( gender ) ? 1 : ( "f".equals( gender ) ? 2 : 0 ) );
            me.setAge( age);

            shareDate.putString( SharedPreferenceUtil.USERID, String.valueOf( me.getUid( ) ) );
//        }

//        me.setEmail( email );

        me.setWithWho( Integer.valueOf(withwho) );

        Common.getInstance( ).isShowGameCenter = opengame != 0;
//        me.setInfoTotal( total);
//
        me.setInfoComplete( complete );

        // 是否首次登录
        me.setFirstLogin( "y".equals( firstlogin ) );
        // 是否绑定帐号
        me.setBind( "y".equals( isbind ) );
        // 登录toket,用于推送
        shareDate.putString( SharedPreferenceUtil.TOKEN, token );

        // 第三方信息（用户身份token、用户ID、token过期时间）
//        if ( !TextUtils.isEmpty(accesstoken  ))
//        {
//            me.setAccesstoken( accesstoken );
//            me.setOpenid( openid );
//            me.setExpires( expires );
//        }

        // 登录时候判断是否显示手机验证，针对4.2国家政策不稳定原因
        shareDate.putString( SharedPreferenceUtil.NEWONOFFS, String.valueOf( newonoffs ) );

        shareDate.putString( SharedPreferenceUtil.SWITCH , String.valueOf( switchs )  );


    }


}
