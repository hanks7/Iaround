package net.iaround.ui.group.view;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;
import net.iaround.ui.group.GroupScaleType;
import net.iaround.ui.group.ICreateGroupParentCallback;
import net.iaround.ui.group.INextCheck;
import net.iaround.ui.group.activity.CreateGroupActivity;
import net.iaround.ui.group.bean.CreateGroupInfo;
import net.iaround.ui.group.bean.GroupNextStep;


public class CreateGroupSubmitView extends SuperView implements INextCheck {

	private ICreateGroupParentCallback mParentCallback;
	private SuperActivity mSuperActivity;
	private CreateGroupInfo mGroupInfo;
	
	private ImageView mGroupIcon;
	private TextView mGroupName;
	private TextView mGroupType;
	private TextView mGroupLocation;
	private TextView mGroupDesc;
	private Button mBtnSubmit;
	
	public CreateGroupSubmitView(SuperActivity activity ,
								 ICreateGroupParentCallback createGroupActivity)
	{
		super( activity , R.layout.view_create_group_submit );
		mSuperActivity = activity;
		this.mParentCallback = createGroupActivity;
	}
	
	public void refreshView(CreateGroupInfo groupInfo)
	{
		mGroupInfo = groupInfo;
		initViews( );
		setListeners( );
	}
	
	private void initViews( )
	{
		mGroupIcon = (ImageView) findViewById(R.id.group_icon);
		CommonFunction.log("group", "mGroupInfo.groupIconUrl***" + mGroupInfo.groupIconUrl);
		String iconUrl = null;
		if ( mGroupInfo.groupIconUrl != null
				&& !mGroupInfo.groupIconUrl.equals("")) {
			iconUrl = mGroupInfo.groupIconUrl;
//			ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView(
//					iconUrl, mGroupIcon, PicIndex.DEFAULT_GROUP_SMALL ,
//					PicIndex.DEFAULT_GROUP_SMALL , null , 100 );//jiqiang
			GlideUtil.loadCircleImage(BaseApplication.appContext,iconUrl,mGroupIcon,PicIndex.DEFAULT_GROUP_SMALL ,
					PicIndex.DEFAULT_GROUP_SMALL);
		}
		else {
//			iconUrl = mGroupInfo.groupTypeIcon;
			iconUrl = null;//默认显示本地图片
			mGroupIcon.setImageResource(R.drawable.create_group_default_img);
//			ImageViewUtil.getDefault().loadRoundedImageInConvertView(

//					iconUrl, mGroupIcon,
//					PicIndex.DEFAULT_GROUP_SMALL,
//					PicIndex.DEFAULT_GROUP_SMALL, null);//jiqiang
			GlideUtil.loadCircleImage(BaseApplication.appContext,iconUrl,mGroupIcon,PicIndex.DEFAULT_GROUP_SMALL,
					PicIndex.DEFAULT_GROUP_SMALL);
		}
				
		mGroupName = (TextView) findViewById(R.id.group_name);
		mGroupName.setText(mGroupInfo.groupName);
		
		mGroupType = (TextView) findViewById(R.id.group_type);
		if (mGroupInfo.groupTypeName != null
				&& !mGroupInfo.groupTypeName.equals("")) {
			String[ ] typeArray = mGroupInfo.groupTypeName.split( "\\|" );
			int langIndex = CommonFunction.getLanguageIndex( getContext( ) );
			try
			{
				mGroupType.setText( typeArray[ langIndex ] );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
						
		mGroupLocation = (TextView) findViewById(R.id.group_location);
		mGroupLocation.setText(mGroupInfo.groupBuildingName);
		
		mGroupDesc = (TextView) findViewById(R.id.group_introduction);
		mGroupDesc.setText(mGroupInfo.groupDesc);
		
		mBtnSubmit = (Button) findViewById( R.id.btn_submit );
		mBtnSubmit.setText(getContext().getString(R.string.create_group_info_submit));
	}
	
	private void setListeners( )
	{
		mBtnSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mGroupInfo.groupType == GroupScaleType.SMALL_GROUP_TYPE) {
					mBtnSubmit.setText(getContext().getString(R.string.submitting));
					mParentCallback.goNext( getGroupNextStep( ) );
				}
				else if (mGroupInfo.groupType == GroupScaleType.BIG_GROUP_TYPE) {
					String note = String.format(
							getContext( ).getString( R.string.create_big_group_msg ) ,
							mGroupInfo.diamondCost );
					
					DialogUtil.showOKCancelDialog( getContext( ) ,
							"" , note ,
							new View.OnClickListener( )
							{
								@Override
								public void onClick( View v )
								{								
									mBtnSubmit.setText(getContext().getString(R.string.submitting));
									mParentCallback.goNext( getGroupNextStep( ) );
								}
							} );
				}
				
			}
		});
	}

	@Override
	public void initData(BaseServerBean mServerBean, boolean isBack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GroupNextStep getGroupNextStep() {
		// TODO Auto-generated method stub
		GroupNextStep step = new GroupNextStep( );
		
		return step;
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		if ( flag == CreateGroupActivity.CREATE_GROUP_FLAG )
		{
			mBtnSubmit.setText(getContext( ).getString(R.string.create_group_info_submit));
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
	}
	
}
