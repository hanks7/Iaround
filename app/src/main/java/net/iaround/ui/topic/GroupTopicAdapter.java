
package net.iaround.ui.topic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.PicIndex;
import net.iaround.tools.StringUtil;
import net.iaround.tools.StringUtil.StringFromTo;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.PictureDetailsActivity;
import net.iaround.ui.group.bean.TopicListContentBeen;
import net.iaround.ui.group.view.DynamicMultiImageView;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dynamic.DynamicImageLayout;

import java.util.ArrayList;


public class GroupTopicAdapter extends BaseAdapter
{
	private static final int MAX_LENGTH = 140;
	
	public static final  int ITEM_TAG =  R.id.adapter_holder ;
	public static final  int HOLDER = R.id.adapter_item ;

	private View.OnClickListener delectViewClickListener;
	private View.OnClickListener hotToPicOnClickListener;
	private View.OnClickListener itemViewClickListener;
	private View.OnClickListener shareViewClickListener;
	private View.OnClickListener greetViewClickListener;
	private View.OnClickListener reviewViewClickListener;
	private View.OnClickListener topicClickListener;
	private View.OnClickListener resendViewClickListener;

	private NotifyDataChanged dataChanged;
	private boolean iscanShare = true;

	public void setDelectClickListener( View.OnClickListener ll )
	{
		delectViewClickListener = ll;
	}

	public void setHotTopicClickListener( View.OnClickListener ll )
	{
		hotToPicOnClickListener = ll;
	}

	public void setItemClickListener( View.OnClickListener ll )
	{
		itemViewClickListener = ll;
	}

	public void setShareClickListener( View.OnClickListener ll )
	{
		shareViewClickListener = ll;
	}

	public void setRreetClickListener( View.OnClickListener ll )
	{
		greetViewClickListener = ll;
	}

	public void setReviewClickListener( View.OnClickListener ll )
	{
		reviewViewClickListener = ll;
	}

	public void setTopicClickListener( View.OnClickListener ll )
	{
		topicClickListener = ll;
	}

	public void setResendViewClickListener( View.OnClickListener ll )
	{
		resendViewClickListener = ll;
	}

	int grouprole;
	ArrayList<TopicListContentBeen> topicList;
	Context mContext;

	public GroupTopicAdapter(Context context , ArrayList< TopicListContentBeen > list ,
							 int grouprole , boolean iscanShare)
	{
		super( );
		mContext = context;
		topicList = list;
		this.grouprole = grouprole;
		this.iscanShare = iscanShare;

	}

	@Override
	public int getCount( )
	{
		return topicList.size( );
	}

	@Override
	public Object getItem(int position )
	{
		return topicList.get( position );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView(int position , View convertView , ViewGroup parent )
	{
		TopicItemHolder holder = null;

		TopicListContentBeen topic = topicList.get( position );

		if ( null == convertView )
		{
			convertView = LayoutInflater.from( mContext ).inflate(
					R.layout.topic_list_image_view , null );
			holder = initView( convertView );
			convertView.setTag( HOLDER ,holder );
		}
		else
		{
			holder = (TopicItemHolder)convertView.getTag( HOLDER ) ;
		}

		if ( topic != null )
		{


			if ( topic.hotTopic != null && topic.hotTopic.size( ) > 0 )
			{
				holder.topic_ly.setVisibility( View.GONE );
				holder.llHotTopic.setVisibility( View.VISIBLE );
				holder.rlInfo.setVisibility( View.VISIBLE );
				holder.mainImageView.setVisibility( View.GONE );

				LinearLayout Row1 = (LinearLayout) holder.llHotTopic
						.findViewById( R.id.hot_topic_text_one );
				Row1.removeAllViews( );
				if ( topic.hotTopic != null && topic.hotTopic.size( ) > 0 )
				{

					showHottopicList( holder , topic.hotTopic );

				}
				else
				{
					holder.topic_ly.setVisibility( View.GONE );

					holder.rlInfo.setVisibility( View.GONE );
					holder.llContent.setVisibility( View.GONE );
					holder.mainImageView.setVisibility( View.GONE );
				}

			}
			else
			{
				if ( topic.sendStatus != 0 )
				{
					//YC 后期需要调整发送话题逻辑
					holder.llSendstatus.setVisibility( View.GONE );
					// 正在发送中
					if ( topic.sendStatus == 1 )
					{
						holder.llBtnslayout.setVisibility( View.GONE );
						holder.tvSendingStatus.setText( R.string.group_topic_sending_tips );
					}
					else
					{
						holder.tvSendingStatus.setText( R.string.group_topic_send_fail_tips );
						holder.llBtnslayout.setVisibility( View.VISIBLE );
						// 发送失败
						holder.tvDelectBtn.setTag( topic );
						holder.tvResendBtn.setTag( topic );

						holder.tvDelectBtn.setOnClickListener( delectViewClickListener );
						holder.tvResendBtn.setOnClickListener( resendViewClickListener );
					}

				}
				else
				{
					holder.llSendstatus.setVisibility( View.GONE );
				}

				holder.llHotTopic.setVisibility( View.GONE );
				holder.topic_ly.setVisibility( View.VISIBLE );
				holder.rlInfo.setVisibility( View.VISIBLE );
				holder.llContent.setVisibility( View.VISIBLE );
				holder.mainImageView.setVisibility( View.GONE );

				holder.userNameView.setTag( topic );
				holder.llHotTopic.setVisibility( View.GONE );
				// 用户头像
				holder.userIconView.setFocusable( false );
				holder.userIconView.execute(ChatFromType.GroupTopic,topic.user.convertBaseToUser( ) ,null  );

				holder.userIconView.setTag( topic.user.convertBaseToUser( ) ) ;

				// 用户昵称
				holder.userNameView.setText( FaceManager.getInstance( mContext )
						.parseIconForString( holder.userNameView , mContext ,
								topic.user.nickname , 20 ) );

				//vip
				if ( topic.user.svip > 0  ){//仅为svip
					 holder.userNameView.setTextColor(Color.parseColor("#ee4552"));
				 }else {//非vip
					 holder.userNameView.setTextColor(Color.parseColor("#222222"));
				}


				//性别年龄
				holder.tvAgeSex.setText( String.valueOf( topic.user.age ) );
				if ( topic.user.gender != null && topic.user.gender.equals( "f" ) )
				{
					holder.rlAgeSex.setBackgroundResource( R.drawable.group_member_age_girl_bg );
					holder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
				}
				else
				{
					holder.rlAgeSex.setBackgroundResource( R.drawable.group_member_age_man_bg );
					holder.ivSex.setImageResource(R.drawable.thread_register_man_select);
				}

				holder.mainImageView.setVisibility( View.GONE );

				DynamicImageLayout layout = new DynamicImageLayout(mContext);
				final ArrayList< String > templist = topic.getPhotoList( );
				if (templist.size() > 0 || topic.topic.type == 2)
				{
					holder.llImageContent.setVisibility(View.VISIBLE);
					if (templist.get(0).contains("storage/")){
						layout.buildSDImage(holder.llImageContent, templist);
					}else{
						layout.buildImage(holder.llImageContent, templist);
					}

				} else
				{
					holder.llImageContent.setVisibility(View.GONE);
				}

				layout.setImageListenter(new DynamicImageLayout.ImageListenter() {
					@Override
					public void imageList(int position) {
						PictureDetailsActivity.launch(mContext, templist, position);
					}
				});

				//魅力值
				int[ ] charismaSymbole = CommonFunction.getCharismaSymbol( topic.user.charmnum );
				holder.tvCharmnum .setVisibility( View.GONE );
				if(charismaSymbole.length >=6)
				{
					int rank =	charismaSymbole[5];
					if ( rank >= 20 )
					{
						holder.tvCharmnum.setVisibility( View.GONE );//YC 取消魅力值的展示
						int index= (rank-1)/20;
						index = index> 4?  4:index;
						holder.tvCharmnum.setBackgroundResource( PicIndex.drawCharm[ index ] );
						holder.tvCharmnum .setText( rank + mContext.getString( R.string.charm_lv ) );
					}
				}


				// 时间
				holder.timeView.setText( TimeFormat.timeFormat4( mContext ,
						topic.topic.datetime ) );
				// 话题的文本内容
				holder.mainContentView.setVisibility( View.GONE );
				if ( !CommonFunction.isEmptyOrNullStr( topic.topic.content ) )
				{
					String contentStr = StringUtil.getEntireFaceString( topic.topic.content ,
							MAX_LENGTH );
					if ( topic.topic.content.length( ) > MAX_LENGTH )
					{
						contentStr += "...";
					}
					holder.mainContentView.setVisibility( View.VISIBLE );

					SpannableString span = FaceManager.getInstance( mContext )
							.parseIconForString( holder.mainContentView , mContext ,
									contentStr , 0 );
					StringFromTo subfromto = CommonFunction.getHotGroupTopic( contentStr );
					if ( subfromto.start != subfromto.end )
					{
						holder.mainContentView.setMovementMethod( LinkMovementMethod
								.getInstance( ) );

						span.setSpan( new Clickable( topicClickListener ) , subfromto.start ,
								subfromto.end , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
						holder.mainContentView.setTag( subfromto );
					}

					holder.mainContentView.setText( span );
				}


				// 话题的状态标签

				int newFlag = topic.topic.isnew == 0 ? View.GONE : View.VISIBLE;
				int topFlag = topic.topic.istop ;
				int essenceFlag = topic.topic.isessence == 0 ? View.GONE : View.VISIBLE;
				int hotFlag = topic.topic.ishot == 0 ? View.GONE : View.VISIBLE;
//				holder.tvEssenceFlg.setVisibility( essenceFlag );
//				holder.tvHotFlag.setVisibility( hotFlag );
//				holder.tvNewFlag.setVisibility( newFlag );//YC 取消展示
				if (topFlag == 0)
				{
					holder.tvTopFlag.setVisibility(View.GONE);
				}else
				{
					holder.tvTopFlag.setVisibility(View.VISIBLE);
				}
				holder.ivTvsyncFlag.setVisibility( View.GONE );

//				if ( topic.topic.synctype == 1 )
//				{
//					holder.ivTvsyncFlag.setVisibility( View.GONE );//取消展示
//					holder.ivTvsyncFlag.setImageResource( R.drawable.z_group_sync_to_img_icon );
//				}
//				else if ( topic.topic.synctype == 2 )
//				{
//					holder.ivTvsyncFlag.setVisibility( View.GONE );//取消展示
//					holder.ivTvsyncFlag.setImageResource( R.drawable.z_group_sync_from_img_icon );
//				}
//				else
//				{
//					holder.ivTvsyncFlag.setVisibility( View.GONE );
//				}//YC 取消展示

				//定位距离
				if(TextUtils.isEmpty( topic.topic.address  ))
				{
					holder.rlAddress.setVisibility(View.GONE);
					holder.commentAddress.setVisibility( View.GONE ) ;
				}
				else
				{
					holder.rlAddress.setVisibility(View.VISIBLE);
					holder.commentAddress.setVisibility( View.VISIBLE ) ;
					holder.commentAddress.setText( topic.topic.address );
				}
				// 评论次数
				int commentNum = topic.reviewcount;

				int commentCount = topic.reviewinfo != null ? topic.reviewinfo.total
						: topic.reviewcount;
//
				String commentStr = String.format(mContext.getResources( ).getString( R.string.dynamic_comment_btn ),
						commentCount );

				holder.commentNumView.setText(commentStr);

				holder.rlReview.setTag( topic );
				if ( reviewViewClickListener != null && topic.grouprole <= 2 )
				{
					holder.rlReview.setOnClickListener( reviewViewClickListener );
				}
				else
				{
					holder.commentNumView.setTextColor( mContext.getResources( ).getColor(
							R.color.chat_update_gray_text_color ) );
				}

				// 喜欢次数
				int likeCount = topic.likeinfo != null ? topic.likeinfo.total
						: topic.likecount;


				int loveType = topic.likeinfo != null ? topic.likeinfo.curruserlike
						: topic.curruserlike;
				String greetStr = "";
				if ( loveType == 0 )// 没有点过赞
				{

					if ( topic.isCurrentHanleView )
					{
						if (topic.topic != null)
						{
							if (topic.topic.photos.size() > 0)
							{
								playCancelLikeAnimation( holder.greetAnimationImg );
							}
						}
						topic.isCurrentHanleView = false;
					}
					holder.ivGreet.setImageResource( R.drawable.dynamic_like_pres );
//
					greetStr = String.format(mContext.getResources( ).getString( R.string.dynamic_greet_btn ),
							likeCount );
					holder.likeNumView.setTypeface( Typeface.DEFAULT );

				}
				else
				{
					if ( topic.isCurrentHanleView )
					{
						if (topic.topic != null)
						{
							if (topic.topic.photos.size() > 0)
							{
								playLikeAnimation( holder.greetAnimationImg );
							}
						}

						topic.isCurrentHanleView = false;
					}
					holder.ivGreet.setImageResource( R.drawable.dynamic_like_normal );
					greetStr = String.format(mContext.getResources( ).getString( R.string.dynamic_greet_btn ),
							likeCount );
					holder.likeNumView.setTypeface( Typeface.DEFAULT_BOLD );

				}

				holder.likeNumView.setText(greetStr);

				holder.rlGreat.setTag( topic );
				if ( greetViewClickListener != null )
				{
					holder.rlGreat.setOnClickListener( greetViewClickListener );
				}
				else
				{
					holder.likeNumView.setTextColor( mContext.getResources( ).getColor(
							R.color.c_999999 ) );
				}


				holder.rlShare.setTag( topic );
				holder.rlShare.setOnClickListener( shareViewClickListener );

				if ( grouprole >= 3 )
				{
					if(!iscanShare)
					{
						holder.rlShare.setClickable( false );
						((TextView)holder.rlShare.findViewById( R.id.btnShare )).setTextColor(
								mContext.getResources( ).getColor( R.color.c_999999 ) );
					}
					holder.rlGreat.setClickable( false );
					holder.rlReview.setClickable( false );
					holder.likeNumView.setTextColor( mContext.getResources( ).getColor(
							R.color.c_999999 ) );
					holder.commentNumView.setTextColor( mContext.getResources( ).getColor(
							R.color.c_999999 ) );

				}
			}
		}
		convertView.setTag( ITEM_TAG ,topic );
		convertView.setOnClickListener( itemViewClickListener );


		return convertView;
	}

	@Override
	public void notifyDataSetChanged( )
	{
		super.notifyDataSetChanged( );
		if ( dataChanged != null )
		{
			dataChanged.notifyDataHaveChanged( );
		}
	}

	private void showHottopicList( TopicItemHolder holder , ArrayList< String > hotTopic )
	{

		LinearLayout Row1 = (LinearLayout) holder.llHotTopic
				.findViewById( R.id.hot_topic_text_one );
		Row1.removeAllViews( );



		int screenW = CommonFunction.getScreenPixWidth( mContext );
		int pix = 14; // CommonFunction.dipToPx( getAttachActivity() , 11 )
						// ;
		int marginlr = 11;// CommonFunction.dipToPx( getAttachActivity() , 5
							// ) ;
		int margint = 5;// CommonFunction.dipToPx( getAttachActivity() , 9
							// ) ;
		CommonFunction.log( "shifengxiong" , "pix =" + pix + ":mar:" + marginlr );
		int[ ] bkIds =
			{ R.drawable.hotbg0 , R.drawable.hotbg1 , R.drawable.hotbg2 , R.drawable.hotbg3 ,
					R.drawable.hotbg4 };
		int index = 0;

		int totalLen = 0;
		if ( hotTopic != null && hotTopic.size( ) > 0 )
		{

			while ( index < hotTopic.size( ) )
			{
				int first = hotTopic.size( ) >= 3 ? 3 : hotTopic.size( );

				LinearLayout rowLayout = new LinearLayout( mContext );

				LinearLayout.LayoutParams rowlayoutParam = ( LinearLayout.LayoutParams ) Row1
						.getLayoutParams( );

				// LayoutParams rowlayoutParam = new LayoutParams(
				// LayoutParams.MATCH_PARENT ,
				// LayoutParams.WRAP_CONTENT );
//				rowlayoutParam.setMargins( 0 , 0 , 0 , 0 );

				rowLayout.setOrientation( LinearLayout.HORIZONTAL );
				rowLayout.setLayoutParams( rowlayoutParam );
				Row1.addView( rowLayout );



				totalLen = 0;
				for ( int i = 0 ; i < first ; i++ )
				{
					// LayoutParams layoutParam = new LayoutParams(
					// LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT
					// );

					LinearLayout.LayoutParams layoutParam = ( LinearLayout.LayoutParams ) rowLayout
							.getLayoutParams( );

					layoutParam.setMargins( 6 , margint , 10 , margint );

					TextView tv = new TextView( mContext );

					tv.setLayoutParams( layoutParam );

					int bkindex = index % bkIds.length;

					tv.setBackgroundResource( bkIds[ bkindex ] );
					tv.setTextColor( mContext.getResources( ).getColor( R.color.white )  );
					tv.setText( hotTopic.get( index ) );
					tv.setOnClickListener( hotToPicOnClickListener );

					tv.setTag( hotTopic.get( index ) );
					tv.setTextSize( pix );
					tv.setSingleLine( true );

					tv.setPadding(10 , 0 , 10 , 0 );
					totalLen += tv.getTextSize( ) * hotTopic.get( index ).length( ) + marginlr
							* 2 +20;


					if ( totalLen >= screenW )
					{
						break;
					}

					rowLayout.addView( tv );

					CommonFunction.log( "shifengxiong" , "totalLen = " + totalLen + ";index="
							+ index );

					index++;
					if ( index >= hotTopic.size( ) )
					{
						break;
					}
				}
			}


		}

	}

	private TopicItemHolder initView( View convertView )
	{

		TopicItemHolder holder = new TopicItemHolder( );

		holder.topic_ly = (RelativeLayout) convertView.findViewById( R.id.topic_ly );

		holder.llHotTopic = (LinearLayout) convertView.findViewById( R.id.ll_hot_topic_text );

		holder.llContent = (LinearLayout) convertView.findViewById( R.id.llContent );
		/** 用户信息 **/
		holder.rlInfo = (RelativeLayout) convertView.findViewById( R.id.rlInfo );
		// 头像
		holder.userIconView = (HeadPhotoView) convertView.findViewById( R.id.ivPhoto );
		// 昵称
		holder.userNameView = (TextView) convertView.findViewById( R.id.tvName );
		// 时间
		holder.timeView = (TextView) convertView.findViewById( R.id.tvTime );
		// 性别年龄
		holder.tvAgeSex = (TextView) convertView.findViewById( R.id.tvAgeSex );
		holder.rlAgeSex = (RelativeLayout) convertView.findViewById(R.id.rlAgeSex);
		holder.ivSex = (ImageView) convertView.findViewById(R.id.ivSex);
		//魅力值
		holder.tvCharmnum = (TextView) convertView.findViewById( R.id.user_charmnum );

		// 动态的文本内容
		holder.mainContentView = (TextView) convertView.findViewById( R.id.tvContent );

		// 多图显示
		holder.mainImageView = (DynamicMultiImageView) convertView
				.findViewById( R.id.dmivPic );
		holder.llImageContent = (LinearLayout) convertView.findViewById(R.id.llImageContent);
		// 地址
		holder.commentAddress = (TextView) convertView.findViewById( R.id.tvAddDis );
		holder.rlAddress = (LinearLayout) convertView.findViewById(R.id.ly_dynamic_address);
		// 赞动画
		holder.greetAnimationImg = (ImageView) convertView
				.findViewById( R.id.ivGreetAnimationImg );

		holder.ivGreet = (ImageView) convertView.findViewById( R.id.ivGreet );
		// 评论
		holder.commentNumView = (TextView) convertView.findViewById( R.id.btnReply );
		// 赞
		holder.likeNumView = (TextView) convertView.findViewById( R.id.btnGreet );

		holder.rlGreat = (RelativeLayout) convertView.findViewById( R.id.rlGreet );
		holder.rlReview = (RelativeLayout) convertView.findViewById( R.id.rlReply );
		holder.rlShare = (RelativeLayout) convertView.findViewById( R.id.rlShare );


		holder.tvHotFlag = (TextView) convertView.findViewById( R.id.tvHotFlag );
		holder.tvTopFlag = (TextView) convertView.findViewById( R.id.tvtopFlg );
		holder.tvNewFlag = (TextView) convertView.findViewById( R.id.tvnewFlg );
		holder.tvEssenceFlg = (TextView) convertView.findViewById( R.id.tvisessenceFlg );
		holder.ivTvsyncFlag = (ImageView) convertView.findViewById( R.id.tvsync );
		holder.llSendstatus = (LinearLayout) convertView.findViewById( R.id.ll_send_status );

		holder.llBtnslayout = (LinearLayout) convertView.findViewById( R.id.ll_btn_layout );
		holder.tvDelectBtn = (TextView) convertView.findViewById( R.id.btn_del );
		holder.tvResendBtn = (TextView) convertView.findViewById( R.id.btn_re_send );
		holder.tvSendingStatus = (TextView) convertView
				.findViewById( R.id.tv_send_status_text );

		return holder;
	}


	private static class TopicItemHolder
	{
		public RelativeLayout topic_ly;

		// 热门推荐话题
		public LinearLayout llHotTopic;
		public RelativeLayout rlInfo;
		public LinearLayout llContent;
		// 图片信息
		public DynamicMultiImageView mainImageView;
		public LinearLayout llImageContent;

		// 头像 昵称 时间
		public HeadPhotoView userIconView;//头像
		public TextView userNameView;//用户名
		public TextView timeView;//发布时间
		public RelativeLayout rlAgeSex;//性别背景
		public ImageView ivSex;//性别
		public TextView tvAgeSex;//年龄
		public TextView tvCharmnum ;//魅力值

		// 文本信息
		public TextView mainContentView;


		// 地址 分享 点赞 评论
		public TextView commentAddress;
		public LinearLayout rlAddress;

		public TextView commentNumView;
		public TextView likeNumView;

		public ImageView ivGreet;

		public RelativeLayout rlGreat;
		public RelativeLayout rlReview;
		public RelativeLayout rlShare;

		public ImageView greetAnimationImg;

		// 话题的状态标签 置顶、精华、热门、同步出去、同步进来（贴吧、圈话题、个人动态）

		public TextView tvHotFlag;
		public TextView tvNewFlag;
		public TextView tvTopFlag;//置顶
		public ImageView ivTvsyncFlag;
		public TextView tvEssenceFlg;

		public LinearLayout llSendstatus;
		public LinearLayout llBtnslayout;
		public TextView tvDelectBtn;
		public TextView tvResendBtn;
		public TextView tvSendingStatus;

	}


	class Clickable extends ClickableSpan implements OnClickListener
	{
		private final View.OnClickListener mListener;

		public Clickable( View.OnClickListener l )
		{
			mListener = l;
		}
		
		@Override
		public void onClick( View v )
		{
			mListener.onClick( v );
		}
	}
	
	
	
	/** 播放点赞的动画 */
	public void playLikeAnimation( final ImageView greetAnimationImg )
	{
		greetAnimationImg.setVisibility(View.VISIBLE);
		Animation mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.dynamic_love);
		mAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				greetAnimationImg.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		greetAnimationImg.startAnimation(mAnimation);
//		greetAnimationImg.setVisibility( View.VISIBLE );
//		ScaleAnimation scaleAnimation = new ScaleAnimation( 2.0f , 1.0f , 2.0f , 1.0f ,
//				Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f );
//		scaleAnimation.setDuration( 300 );
//		scaleAnimation.setFillBefore( true );
//		greetAnimationImg.startAnimation( scaleAnimation );
//		scaleAnimation.setAnimationListener( new AnimationListener( )
//		{
//
//			@Override
//			public void onAnimationStart( Animation animation )
//			{
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationRepeat( Animation animation )
//			{
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationEnd( Animation animation )
//			{
//				// TODO Auto-generated method stub
//				greetAnimationImg.setVisibility( View.GONE );
//			}
//		} );
		
	}
	
	/** 播放取消赞的动画 */
	public void playCancelLikeAnimation( final ImageView greetAnimationImg )
	{
//		greetAnimationImg.setVisibility( View.VISIBLE );
//
//		ScaleAnimation scaleAnimation = new ScaleAnimation( 1.0f , 2.0f , 1.0f , 2.0f ,
//				Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f );
//		TranslateAnimation translateAnimation = new TranslateAnimation(
//				Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , -0.5f ,
//				Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , -2.0f );
//		RotateAnimation rotateAnimation = new RotateAnimation( 0 , -20 ,
//				Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f );
//		AlphaAnimation alphaAnimation = new AlphaAnimation( 1.0f , 0.3f );
//
//		AnimationSet animationSet = new AnimationSet( true );
//		animationSet.addAnimation( scaleAnimation );
//		animationSet.addAnimation( translateAnimation );
//		animationSet.addAnimation( rotateAnimation );
//		animationSet.addAnimation( alphaAnimation );
//		animationSet.setFillBefore( true );
//		animationSet.setDuration( 200 );
//
//		greetAnimationImg.startAnimation( animationSet );
//		scaleAnimation.setAnimationListener( new AnimationListener( )
//		{
//
//			@Override
//			public void onAnimationStart( Animation animation )
//			{
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationRepeat( Animation animation )
//			{
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationEnd( Animation animation )
//			{
//				// TODO Auto-generated method stub
//				greetAnimationImg.setVisibility( View.GONE );
//			}
//		} );
		
		
	}
	
	public interface NotifyDataChanged
	{
		
		void notifyDataHaveChanged();
		
	}
	
	public void setOnNotifyDataChanged( NotifyDataChanged haveChanged )
	{
		dataChanged = haveChanged;
	}
	
}
