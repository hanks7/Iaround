package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.ChatRecord;

/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-通过什么方式找到你
 */

public class FindNoticeView extends AccostRecordView
{

	private TextView tvContent;
	private Context mContext;
	
	public FindNoticeView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_find_notice );
		mContext = context;
	}

	@Override
	public void build( ChatRecord record )
	{
		tvContent = (TextView) findViewById( R.id.tvContent );
		
		switch(record.getFrom( ))
		{
			case ChatFromType.UNKONW:
				record.setContent( "" ); //未知，不显示
				break;
			case ChatFromType.SearchID:
				record.setContent( mContext.getResources( ).getString( R.string.accost_from_type_search_id ));
				break;
			case ChatFromType.SearchNickName:
				record.setContent( mContext.getResources( ).getString( R.string.accost_from_type_search_niname ));
				break;
			case ChatFromType.NearList:
				record.setContent( mContext.getResources( ).getString( R.string.accost_from_type_nearlist ));
				break;
			case ChatFromType.NearMap:
				record.setContent( mContext.getResources( ).getString( R.string.accost_from_type_nearmap ));
				break;
			case ChatFromType.GrobalFocus:
				record.setContent( mContext.getResources( ).getString( R.string.accost_from_type_grobal_focus ));
				break;
			case ChatFromType.NearFocus:
				record.setContent( mContext.getResources( ).getString( R.string.accost_from_type_near_focus ));
				break;
			default:
				record.setContent( "");
				break;
		}
		
		
		tvContent.setText( record.getContent( ) );
	}
	
}
