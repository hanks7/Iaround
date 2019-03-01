package net.iaround.ui.activity.im.accost;

import android.content.Context;

import net.iaround.tools.CommonFunction;


/**
 * @ClassName ChatRecordViewFactory
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-4-14 上午10:41:46
 * @Description: 统一处理聊天记录-简单工厂模式
 * @linkClass ChatPersonal GroupChatTopicActivity
 */

public class AccostRecordFactory
{
	//类型最大总数数量
	public final static int TYPE_COUNT = 153;
	
	public final static int TYPE_OFFSET = 50;//判断对方还是自己的偏移
	
	public final static int TEXT = 1;			//文本
	public final static int IMAGE = 2;			//图片
	public final static int SOUND = 3;			//语音
	public final static int VIDEO = 4;			//录像
	public final static int LOCATION = 5;		//位置
	public final static int GIFE_REMIND = 6;		//礼物提醒
	@Deprecated
	public final static int THEME_GIFT = 7;		//主题礼物 5.7抛弃
	public final static int GAME = 8;			//游戏 - 不用的
	public final static int FACE = 9;			//贴图
	public final static int PICTEXT = 10;		//文本 - 不用的
	public final static int ACCOST_GAME_QUE = 11;	//搭讪问题
	public final static int ACCOST_GAME_ANS = 12;	//搭讪回答
	public final static int ACCOST_SHARE = 13;	//搭讪回答
	
	public final static int FRIEND_TEXT = 51;			//文本
	public final static int FRIEND_IMAGE = 52;			//图片
	public final static int FRIEND_SOUND = 53;			//语音
	public final static int FRIEND_VIDEO = 54;			//录像
	public final static int FRIEND_LOCATION = 55;		//位置
	public final static int FRIEND_GIFE_REMIND = 56;	//礼物提醒
	@Deprecated
	public final static int FRIEND_THEME_GIFT = 57;		//主题礼物 5.7抛弃
	public final static int FRIEND_GAME = 58;			//游戏
	public final static int FRIEND_FACE = 59;			//贴图
	public final static int FRIEND_PICTEXT = 60;		//文本
	
	/** Notice View Flag**/
	public final static int FORBID = 40;			//禁言提示
	public final static int FOLLOW = 41;			//关注提示
	public final static int ACCOST_NOTICE = 42;		//搭讪游戏提示
	public final static int TIME_LINE = 0;				//时间提示
	
	public final static int ACCOST_GAME_FIND_NOTICE = 100; //搭讪游戏-从哪里搭讪
	public final static int ACCOST_GAME_ANS_TEXT = 101;	//搭讪回答-文本_自己的
	public final static int ACCOST_GAME_ANS_IMAGE = 102;	//搭讪回答-图片_自己的
	
	public final static int FRIEND_ACCOST_GAME_ANS_TEXT = 151;	//搭讪回答-文本_自己的
	public final static int FRIEND_ACCOST_GAME_ANS_IMAGE = 152;	//搭讪回答-图片_自己的
	
	/**
	 * @Description 根据类型获取相应的ChatRecord
	 * @param mContext
	 * @param type 详情见 {@link AccostRecordFactory}
	 * @return {@link ChatRecordView }
	 */
	public static AccostRecordView createChatRecordView(Context mContext, int type)
	{
		AccostRecordView view = null;
		switch ( type )
		{
			case TEXT:
				{
					view = new AccostTextView( mContext );
				}
				break;
			case FRIEND_TEXT:
				{
					view = new AccostTextView( mContext );
				}
				break;
			case IMAGE:
				{
					view = new AccostImageView( mContext );
				}
				break;
			case FRIEND_IMAGE:
				{
					view = new AccostImageView( mContext );
				}
				break;
			case SOUND:
				{
					view = new AccostAudioView( mContext );
				}
				break;
			case FRIEND_SOUND:
				{
					view = new AccostAudioView( mContext );
				}
				break;
			case VIDEO:
				{
					view = new AccostVideoView( mContext );
				}
				break;
			case FRIEND_VIDEO:
				{
					view = new AccostVideoView( mContext );
				}
				break;
			case LOCATION:
				{
					view = new AccostLocationView( mContext );
				}
				break;
			case FRIEND_LOCATION:
				{
					view = new AccostLocationView( mContext );
				}
				break;
			case GIFE_REMIND:
				{
					view = new AccostGiftView( mContext );
				}
				break;
			case FRIEND_GIFE_REMIND:
				{
					view = new AccostGiftView( mContext );
				}
				break;
			case FACE:
				{
					view = new AccostFaceView( mContext );
				}
				break;
			case FRIEND_FACE:
				{
					view = new AccostFaceView( mContext );
				}
				break;
			case ACCOST_SHARE:
				{
					view = new AccostShareRecordView(mContext);
				}
				break;
			case ACCOST_NOTICE:
				{
					view = new FindNoticeView( mContext );
				}
				break;
			case ACCOST_GAME_ANS_TEXT:
				{
					view = new AccostGameTextView( mContext );
				}
				break;
			case ACCOST_GAME_ANS_IMAGE:
				{
					view = new AccostGameImageView( mContext );
				}
				break;
			case FRIEND_ACCOST_GAME_ANS_TEXT:
				{
					view = new AccostGameTextView( mContext );
				}
				break;
			case FRIEND_ACCOST_GAME_ANS_IMAGE:
				{
					view = new AccostGameImageView( mContext );
				}
				break;
			default :
				{
					view = new AccostTextView( mContext );
					CommonFunction.log( "ChatRecordViewFactory", "Type Error" );
				}
				break;
		}
		return view;
	}

}
