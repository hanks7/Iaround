
package net.iaround.ui.activity.im.accost;


import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.RecordGiftBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.utils.ImageViewUtil;


/**
 * @ClassName AccostGiftView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description:收到搭讪-礼物
 */

public class AccostGiftView extends AccostRecordView
{
	private Context mContext;
	private TextView tvTime , tvName , tvPrice , tvCharisma ,tvExp;
	private ImageView ivImage;
	private ImageView mGiftCount;
	private TextView mSendBackView;
	public AccostGiftView(Context context )
	{
		super( context );
		mContext = context;
		createView( R.layout.accost_record_gift );
	}
	
	@Override
	public void build(final ChatRecord record )
	{
		tvTime = (TextView) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );
		
	
		
		//区分金币礼物、钻石礼物
		RecordGiftBean bean = GsonUtil.getInstance( ).getServerBean( record.getContent( ) , RecordGiftBean.class );
		int strResID = bean.bIsDiamonGift( ) ? R.string.gift_price_2 : R.string.gift_price_1;
		
		tvName = (TextView)findViewById( R.id.name );
		tvName.setText( bean.giftname );
		
		tvPrice = (TextView)findViewById( R.id.price );
		tvPrice.setText( mContext.getResources( ).getString( strResID ) + " " +bean.price );
		
		tvCharisma = (TextView)findViewById( R.id.charisma );
		
		String strCharisma = String.format( mContext.getString( R.string.chat_charisma ) , "+" + bean.charmnum );
		
		tvCharisma.setText( Html.fromHtml( strCharisma ) );
//		tvCharisma.setText( strCharisma );
		
		
		
		ivImage = (ImageView)findViewById(R.id.img);
		String thumPicUrl = CommonFunction.thumPicture( record.getAttachment( ) );
		int defResId = PicIndex.DEFAULT_SMALL;
		ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView(thumPicUrl, ivImage, defResId, defResId, null,
				36);
		GlideUtil.loadRoundImage(BaseApplication.appContext,thumPicUrl,36,ivImage,defResId, defResId);
		mGiftCount = (ImageView)findViewById(R.id.count_img);
		int countRes = 0;
//		switch ( bean.giftnum )
//		{
//			case 10:
//				countRes = R.drawable.chatbar_gift_num_10;
//				break;
//			case 30:
//				countRes = R.drawable.chatbar_gift_num_30;
//				break;
//			case 66:
//				countRes = R.drawable.chatbar_gift_num_66;
//				break;
//			case 188:
//				countRes = R.drawable.chatbar_gift_num_188;
//				break;
//			case 520:
//				countRes = R.drawable.chatbar_gift_num_520;
//				break;
//			case 1314:
//				countRes = R.drawable.chatbar_gift_num_1314;
//				break;
//			default:
//				countRes = R.color.transparent;
//				break;
//		}
		mGiftCount.setImageResource( countRes );

		tvExp = (TextView)findViewById(R.id.exp);
		tvExp.setText( mContext.getString( R.string.chat_send_exp, bean.exp ) );

		mSendBackView = (TextView) findViewById( R.id.send_back );
		mSendBackView.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				User fUser = new User( );
				fUser.setUid( record.getFuid( ) );
				fUser.setSex( record.getfSex( ) );
				fUser.setViplevel( record.getfVipLevel( ) );
				fUser.setNickname( record.getfNickName( ) );
				fUser.setIcon( record.getfIconUrl( ) );
//				StoreGiftClassifyActivity.launchGiftClassifyToSent(mContext, fUser,
//						SuperChat.HandleMsgCode.MSG_SEND_GIFT_REQUEST, record.getFrom(), 1, 1);
				//TODO:回赠礼物跳转到私人送礼面板
				ChatPersonal.skipToChatPersonal((Activity) mContext, fUser, 201, false, ChatFromType.OTHERGIVEGIFT);
			}
		} );
	}
	
}
