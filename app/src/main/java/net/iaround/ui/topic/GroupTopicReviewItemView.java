
package net.iaround.ui.topic;


import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.group.bean.TopicListContentBeen;
import net.iaround.ui.group.bean.TopicReviewBasicInfo;
import net.iaround.ui.view.HeadPhotoView;


/**
 * 圈话题评论者的列表类
 */
public class GroupTopicReviewItemView extends RelativeLayout
{
//	public final static int COMMENT_KEY = R.string.postbar_category_text;// 对于单条评论View设置[评论信息]的Tag的Key  YC 侃拉类型
	
	public HeadPhotoView reviewerIcon;
	public TextView reviewerName;
	public TextView reviewerAge;
	public RelativeLayout rlAgeSex;
	public ImageView ivSex;
	public TextView publisherFlag;
	
	public TextView floorNum;
	public TextView reviewTime;
	public TextView reviewContent;
	public TextView reviewDistance;
	public TextView reviewAddress;
	
	public TextView reviewTargetFloor;
	public TextView reviewTargetName;
	public TextView reviewTargetContent;
	public TextView tvReviewContentName;
	public View reviewTargetView;
	public View reviewFirstView;
	
	public View reviewLocationView;
	
	public GroupTopicReviewItemView(Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
		LayoutInflater.from( getContext( ) ).inflate( R.layout.theme_topic_floor_item , this );
	}
	
	public void initView( )
	{
		reviewerIcon = ( HeadPhotoView ) findViewById( R.id.topic_reviewer_icon );
		reviewerName = (TextView) findViewById( R.id.topic_reviewer_name );
		reviewerAge = (TextView) findViewById( R.id.topic_reviewer_age );
		rlAgeSex = (RelativeLayout) findViewById(R.id.rlAgeSex);
		ivSex = (ImageView) findViewById(R.id.ivSex);
		floorNum = (TextView) findViewById( R.id.topic_review_floor_num_text );
		reviewTime = (TextView) findViewById( R.id.topic_review_time );
		reviewContent = (TextView) findViewById( R.id.topic_review_content );
		publisherFlag = (TextView) findViewById( R.id.topic_publisher_flag );
		
		reviewDistance = (TextView) findViewById( R.id.distance_text );
		reviewAddress = (TextView) findViewById( R.id.address_text );
		
		reviewTargetFloor = (TextView) findViewById( R.id.topic_review_target_floor );
		reviewTargetName = (TextView) findViewById( R.id.topic_review_target_name );
		reviewTargetContent = (TextView) findViewById( R.id.topic_review_target_content );
		reviewTargetView = findViewById( R.id.topic_review_target_view );
		reviewFirstView = findViewById( R.id.topic_first_review_view );
		tvReviewContentName = (TextView) findViewById(R.id.reviewCommentName);

		
		reviewLocationView = findViewById( R.id.review_location_info );
	}
	
	public void initData(TopicListContentBeen topicInfo , final TopicReviewBasicInfo bean ,
						 View.OnClickListener listener )
	{
		initView( );
		
		if ( bean.user != null )
		{
			// 发布者头像

			reviewerIcon.execute(ChatFromType.GroupTopicComment,bean.user.convertBaseToUser(),null );
			
			// 发布者名称
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
					.parseIconForString( reviewerName , getContext( ) , bean.user.nickname ,
							14 );
			reviewerName.setText( spContent );
			
			// 年龄、性别
			reviewerAge.setText( String.valueOf( bean.user.age ) );
			if ( bean.user.gender != null && !bean.user.gender.equals( "" ) )
			{
				if ( bean.user.gender.equals( "f" ) )
				{
					rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
					ivSex.setImageResource(R.drawable.thread_register_woman_select);
				}
				else
				{
					rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
					ivSex.setImageResource(R.drawable.thread_register_man_select);
				}
			}
			
			if ( topicInfo.user.userid == bean.user.userid )
			{
				publisherFlag.setVisibility( View.VISIBLE );
			}
			else
			{
				publisherFlag.setVisibility( View.GONE );
			}
			
			// 楼层数
			String floorNumStr = String.format(
					getContext( ).getString( R.string.review_floor_num_text ) , bean.floor );
			floorNum.setText( floorNumStr );
			
			// 评论时间
			String timeStr = TimeFormat.timeFormat4( getContext( ) , bean.datetime );
			reviewTime.setText( timeStr );
			
			// 评论内容
			SpannableString spReview = FaceManager.getInstance( getContext( ) )
					.parseIconForString( reviewContent , getContext( ) , bean.content , 14 );
			reviewContent.setText( spReview );
			
			// 发表评论的位置信息
			if ( bean.status == 1 )
			{
				reviewLocationView.setVisibility( View.GONE );
				reviewDistance.setText( "" );
				reviewDistance.setCompoundDrawables( null , null , null , null );
				reviewAddress.setText( "" );
			}
			else
			{
				reviewLocationView.setVisibility( View.VISIBLE );
				if ( bean.distance <= 1 )
				{
					reviewDistance.setVisibility( GONE );
					reviewAddress.setVisibility( GONE );
				}
				else
				{
					reviewDistance.setVisibility( VISIBLE );
					reviewAddress.setVisibility( VISIBLE );
					reviewDistance
							.setText( CommonFunction.covertSelfDistance( bean.distance ) );
					reviewDistance.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.location_info_icon , 0 , 0 , 0 );
					reviewAddress.setText( bean.address );
				}
			}
			
			// 回复评论部分
			if ( bean.reply != null )
			{
				reviewTargetView.setVisibility( View.VISIBLE );
				String targetFloorStr = String.format(
						getContext( ).getString( R.string.review_target_floor_num_text ) ,
						bean.reply.floor );
				reviewTargetFloor.setText( targetFloorStr );
				reviewTargetFloor.setVisibility(GONE);
				
				if ( bean.reply.user != null )
				{
					SpannableString targetName = FaceManager.getInstance( getContext( ) )
							.parseIconForString( reviewTargetName , getContext( ) ,
									bean.reply.user.nickname , 14 );
					reviewTargetName.setText( targetName );
					reviewTargetName.setVisibility(GONE);
					tvReviewContentName.setText(getResources().getString(R.string.dynamic_answer_text)+"  "+ targetName +":");
				}
				
				SpannableString targetContent = FaceManager.getInstance( getContext( ) )
						.parseIconForString( reviewTargetContent , getContext( ) ,
								bean.reply.content , 14 );
				reviewTargetContent.setText( targetContent );
				reviewTargetContent.setVisibility(GONE);
				
				LayoutParams params = (LayoutParams) reviewFirstView.getLayoutParams( );
				params.setMargins( 20 , 20 , 20 , 0 );
				reviewFirstView.setLayoutParams( params );
			}
			else
			{
				reviewTargetView.setVisibility( View.GONE );
				LayoutParams params = (LayoutParams) reviewFirstView.getLayoutParams( );
				params.setMargins( 0 , 0 , 0 , 0 );
				reviewFirstView.setLayoutParams( params );
			}
			
			this.setOnClickListener( listener );
			
		}
		
	}
	

}
