package net.iaround.ui.space;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.comon.QuickActionWidget;
import net.iaround.ui.comon.QuickActionWidget.OnQuickActionClickListener;


/**
 * 个人资料复制遇见ID的功能
 * 
 * @author fanzb
 * 
 */
public class SpaceTextClickListener implements OnLongClickListener {
	
	private Context mContext;
	private String content;
	private int barView;// QuickActionBar将会根据这个view创建一个弹出对话框
//	private QuickActionChatBar mBar;
	
	public SpaceTextClickListener(Context context, String content, int viewId)
	{
		this.mContext = context;
		this.barView = viewId;
		this.content = content;
	}
	
	private void prepareActionBar()
	{
//		mBar = new QuickActionChatBar( mContext , true );
//		mBar.addQuickAction( new QuickAction( mContext , null ,
//				R.string.chat_record_fun_copy ) );
//
//		mBar.setOnQuickActionClickListener( mActionListener );
	}
	
	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener( )
	{
		public void onQuickActionClicked(QuickActionWidget widget , int position )
		{
			copyContent(content);
		}
	};
	
	/**
	 * 拷贝ID
	 * 
	 * @param content
	 *            内容
	 */
	private void copyContent( String content )
	{
		if ( CommonFunction.isEmptyOrNullStr( content ) )
		{
			return;
		}
		
		boolean iscopy = CommonFunction.setClipboard( mContext , content );
		if ( iscopy )
		{
			Toast.makeText( mContext ,
					mContext.getResources( ).getString( R.string.chat_copy_text_succ ) ,
					Toast.LENGTH_SHORT ).show( );
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		prepareActionBar();
		View view = v.findViewById( barView ); // 获取遇见ID内容的气泡框，弹出的框将相对于TextView
//		mBar.show( view );

		return false;
	}

}
