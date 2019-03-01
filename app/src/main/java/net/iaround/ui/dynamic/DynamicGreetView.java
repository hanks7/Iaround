package net.iaround.ui.dynamic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.DynamicLoveInfo;
import net.iaround.model.im.DynamicLoveInfo.LoverUser;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.PostbarUserInfo;
import net.iaround.ui.group.bean.TopicLikeInfBeen;
import net.iaround.ui.group.bean.TopicLikeInfBeen.LikeUser;
import net.iaround.ui.group.bean.TopicListContentBeen;
import net.iaround.ui.postbar.TopicLikeBasicInfo;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-5 下午2:40:47
 * @Description: 动态点赞的用户头像的View
 */
public class DynamicGreetView extends LinearLayout {

	private final int defaultImage = R.drawable.iaround_default_img;

	private LinearLayout llRow1;
	private LinearLayout llRow2;

	private ArrayList<View> imagesList;

	public DynamicGreetView(Context context) {
		super(context);
		LayoutInflater.from(getContext()).inflate(
				R.layout.dynamic_comment_and_greet_view, this);
		initView();
	}

	public DynamicGreetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(getContext()).inflate(
				R.layout.dynamic_comment_and_greet_view, this);
		initView();
	}

	private void initView() {
		llRow1 = (LinearLayout) findViewById(R.id.llRow1);
		llRow2 = (LinearLayout) findViewById(R.id.llRow2);
		int childCount = llRow1.getChildCount( );
		imagesList = new ArrayList<View>(childCount);
		String contentDes = getResources().getString(R.string.greet);
		this.findViewsWithText(imagesList, contentDes,
				View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);


	}

	public void init(DynamicLoveInfo loveInfo) {

		if (loveInfo != null && loveInfo.loveusers != null
				&& loveInfo.loveusers.size() > 0) {
			this.setVisibility(View.VISIBLE);
			setGreetLayout(loveInfo);
		} else {
			this.setVisibility(View.GONE);
		}
	}

	public void init(TopicListContentBeen bean) {

		if (bean.likeinfo != null && bean.likeinfo.likeusers != null
				&& bean.likeinfo.likeusers.size() > 0) {
			CommonFunction.log( "" , "--->bean.likeinfo.likeusers=="+bean.likeinfo.likeusers );
			this.setVisibility(View.VISIBLE);
			setGreetLayout(bean.likeinfo);
		} else {
			this.setVisibility(View.GONE);
		}
	}

//	public void init(PostbarTopicInfo bean) {
//
//		if (bean.likeinfo != null && bean.likeinfo.likeusers != null
//				&& bean.likeinfo.likeusers.size() > 0) {
//			this.setVisibility(View.VISIBLE);
//			setGreetLayout(bean.likeinfo);
//
//			View spliterView = findViewById( R.id.vSplit );
//			LayoutParams params = (LayoutParams) spliterView.getLayoutParams( );
//			params.setMargins( 40 , 0 , 40 , 0 );
//			//spliterView.setPaddingRelative( 40 , 0 , 40 , 0 );
//		} else {
//			this.setVisibility(View.GONE);
//		}
//	}//jiqiang

	/**
	 * 设置赞的布局，包括数字和头像
	 */
	public void setGreetLayout(DynamicLoveInfo loveInfo) {

		if (loveInfo == null || loveInfo.loveusers == null) {
			this.setVisibility(View.GONE);
			return;
		}

		int PicCount = loveInfo.loveusers.size();
		if (PicCount <= 0) {
			this.setVisibility(View.GONE);
			return;
		}
		int MAX_COUNT = 16;

		for (int i = 0; i < MAX_COUNT; i++) {

			HeadPhotoView ivPic = ((HeadPhotoView) imagesList.get(i));
			if (i < PicCount) {
				ivPic.setVisibility(View.VISIBLE);

				LoverUser user = loveInfo.loveusers.get(i);
				User tempUser = user.convertBaseToUser();

				ivPic.setTag(tempUser);

				ivPic.execute( ChatFromType.UNKONW,user.convertBaseToUser(),null );

			} else {
				ivPic.setVisibility(View.INVISIBLE);
			}
		}
		if(PicCount <= 8){
			llRow2.setVisibility(View.GONE);
		}else{
			llRow2.setVisibility(View.VISIBLE);
		}


	}

	/**
	 * 设置赞的布局，包括数字和头像
	 */
	public void setGreetLayout(TopicLikeInfBeen loveInfo) {

		if (loveInfo == null || loveInfo.likeusers == null) {
			this.setVisibility(View.GONE);
			return;
		}

		int PicCount = loveInfo.likeusers.size();
		if (PicCount <= 0) {
			this.setVisibility(View.GONE);
			return;
		}

		int MAX_COUNT = 6;

		for (int i = 0; i < MAX_COUNT; i++) {
			HeadPhotoView ivPic = ((HeadPhotoView) imagesList.get(i));
			if (i < PicCount) {
				ivPic.setVisibility( View.VISIBLE );

				LikeUser user = loveInfo.likeusers.get(i);
				User tempUser = user.convertBaseToUser( );
				ivPic.setTag( tempUser );
				ivPic.setScaleType(ScaleType.CENTER_CROP);
				ivPic.executeGreet(  defaultImage,tempUser,null);
			} else {
				ivPic.setVisibility(View.INVISIBLE);
			}
		}
		if(PicCount <= 8){
			llRow2.setVisibility(View.GONE);
		}else{
			llRow2.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 设置赞的布局，包括数字和头像
	 */
	public void setGreetLayout(TopicLikeBasicInfo topicinfo) {

		if (topicinfo == null || topicinfo.likeusers == null) {
			this.setVisibility(View.GONE);
			return;
		}

		int PicCount = topicinfo.likeusers.size();
		if (PicCount <= 0) {
			this.setVisibility(View.GONE);
			return;
		}
		int MAX_COUNT = 16;

		for (int i = 0; i < MAX_COUNT; i++) {

			HeadPhotoView ivPic = ((HeadPhotoView) imagesList.get(i));
			if (i < PicCount) {
				ivPic.setVisibility(View.VISIBLE);
				PostbarUserInfo user = topicinfo.likeusers.get(i);

				User tempUser = user.convertBaseToUser();
				ivPic.setTag(tempUser);
				ivPic.executeGreet(ChatFromType.DynamicLike,tempUser,null  );

			} else {
				ivPic.setVisibility(View.INVISIBLE);
			}
		}
		if(PicCount <= 8){
			llRow2.setVisibility(View.GONE);
		}else{
			llRow2.setVisibility(View.VISIBLE);
		}

	}
}
