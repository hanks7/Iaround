
package net.iaround.ui.group.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.Text;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.model.database.SpaceModel;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;

import java.util.HashMap;
import java.util.Map;


/**
 * 老的举报页面，现在仅给举报圈子使用,话题也用
 * 
 * @author 余勋杰
 */
public class SpaceReport extends SuperActivity implements OnClickListener {
	//标题
	private FrameLayout flLeft;
	private TextView mTitleName;
	private ImageView mTitleCancel;
	private TextView mTitleSend;

	private EditText etReportComtent;
	private ListView lvType;
	private Dialog pd;
	private Handler handler;
	private String targetId; // 被举报目标ID
	private int type; // 举报的类型（1：色情，2：暴力，3：骚扰，4：谩骂，5：广告，6：欺诈，7：反动，8：政治）@ReportType
	private int targetType; // 举报目标类型（1:人 ，2:相片，3:评论，4：话题，5:群组，6：聊天内容 11,：聊吧）@ReportTargetType
	private long send_flag = 0;
	long firstReportTime = 0;//举报的时间
	private int fromType;// 来自哪里

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case SpaceMe.MSG_CLOSE_PD: {
						if (pd != null && pd.isShowing()) {
							pd.dismiss();

						}
					}
					break;
					case SpaceMe.MSG_SHOW_ERROR: {
						if (msg.obj != null) {
							try {
								ErrorCode.showError(mActivity, String.valueOf(msg.obj));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					break;
					case SpaceMe.MSG_REFRESH: {
						DialogUtil.showOKDialog(mActivity, R.string.report,
								R.string.report_sent, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										finish();
									}
								});
					}
					break;
				}
			}
		};

		setContentView(R.layout.space_report);
		targetId = getIntent().getStringExtra("targetId");
		targetType = getIntent().getIntExtra("targetType", 0);
		firstReportTime = getIntent().getIntExtra("reportTime",0);
		fromType = getIntent().getIntExtra("fromType", 0);
		init(this);
	}

	protected void init(SuperActivity context) {
		mTitleName = (TextView) findViewById(R.id.tv_title);
		mTitleCancel = (ImageView) findViewById(R.id.iv_left);
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mTitleSend = (TextView) findViewById(R.id.tv_right);
		mTitleName.setText(R.string.report);
		mTitleCancel.setVisibility(View.VISIBLE);
		mTitleSend.setVisibility(View.VISIBLE);


//		mTitleCancel.setText( R.string.cancel );
		mTitleCancel.setImageResource(R.drawable.title_back);
		mTitleSend.setText(R.string.chat_room_send);
		mTitleSend.setTextColor(getResources().getColor(R.color.chat_update_message_count));

		flLeft.setOnClickListener(this);
		mTitleCancel.setOnClickListener(this);
		mTitleSend.setOnClickListener(this);

		// btnCancel = (ImageButton) findViewById(R.id.cancel);
		// btnCancel.setOnClickListener(this);
		// btnSend = (Button) findViewById(R.id.send);
		// btnSend.setOnClickListener(this);
		final TextView tvCounter = (TextView) findViewById(R.id.tvCounter);
		tvCounter.setText(Html.fromHtml("<font color=red>0</font> / 500"));
		etReportComtent = (EditText) findViewById(R.id.report_comtent);
		etReportComtent.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// int byteLen =
				// CommonFunction.getStringLength(etReportComtent.getText().toString());
				tvCounter.setText(Html.fromHtml("<font color=red>"
						+ etReportComtent.getText().toString().length() + "</font> / 500"));
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});

		lvType = (ListView) findViewById(R.id.lvType);
		final String[] types;
		if (fromType == 0){
			types = context.getResources().getStringArray(R.array.report_types1);
		} else {
			types = context.getResources().getStringArray(R.array.report_types3);
		}
//		ArrayAdapter< String > adapter = new ArrayAdapter< String >( this ,
//				R.layout.chat_normal_phrase_list_item , types );
		SpaceReportAdapter adapter = new SpaceReportAdapter();
		adapter.setData(this, types);
		lvType.setAdapter(adapter);
		lvType.setSelection(0);

		adapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				if (fromType == 0){
					type = position + 1;
				} else {
					switch (position){
						case 0:
							type = 10;
							break;
						case 1:
							type = 3;
							break;
						case  2:
							type = 5;
							break;
						case 3:
							type = 2;
							break;
						case 4:
							type = 8;
							break;

					}
				}

			}
		});

	}

	public void onClick(View v) {
		if (v.equals(mTitleCancel) || v.equals(flLeft)) {
			hiddenKeyBoard(mActivity);
			onReturn();
		} else if (v.equals(mTitleSend)) {
			hiddenKeyBoard(mActivity);
			report();
		}
	}

	/*
	 * 发送举报消息，如果对同一个id的两次举报时间间隔在10秒以内，第二次会被忽略发送
	 */
	private void report( )
	{
		if ( type == 0 )
		{
			DialogUtil.showOKDialog( mActivity , R.string.error ,
					R.string.have_not_select_type , null );
			return;
		}
		if ( pd != null && pd.isShowing( ) )
		{
			pd.dismiss( );
		}
		pd = DialogUtil.showProgressDialog( this , R.string.report , R.string.please_wait ,
				null );
		
		try
		{
			// String text = etReportComtent.getText().toString().trim();
			if ( etReportComtent.getText( ).toString( ).length( ) > 500 )
			{
				pd.dismiss( );
				Toast.makeText( mActivity , R.string.text_too_long , Toast.LENGTH_SHORT )
						.show( );
				return;
			}
			if ( !CommonFunction.forbidSay( this ) )
			{
				send_flag = SpaceModel.getInstance( mActivity ).reportReq( mActivity , type ,
						targetType , targetId , etReportComtent.getText( ).toString( ) , this );
				if ( send_flag == 1 )
				{
					if ( pd != null && pd.isShowing( ) )
					{
						pd.dismiss( );
					}
				}
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace( );
			pd.dismiss( );
			Toast.makeText( mActivity , R.string.network_req_failed , Toast.LENGTH_SHORT )
					.show( );
			// DialogUtil.showOKDialog(mActivity, R.string.error,
			// R.string.network_req_failed, null);
		}
	}

	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		Message msg = new Message( );
		msg.what = SpaceMe.MSG_CLOSE_PD;
		handler.sendMessage( msg );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		if ( send_flag == flag )
		{
			Message msg = new Message( );
			msg.what = SpaceMe.MSG_CLOSE_PD;
			handler.sendMessage( msg );
			
			HashMap< String , Object > res = null;
			try
			{
				res = SpaceModel.getInstance( this ).getRes( result , flag );
			}
			catch ( Throwable e )
			{
				e.printStackTrace( );
			}
			
			if ( res == null )
			{
				return;
			}
			if ( (Integer) res.get( "status" ) != 200 )
			{
				Message msge = new Message( );
				msge.what = SpaceMe.MSG_SHOW_ERROR;
				msge.obj = result;
				handler.sendMessage( msge );
			} else
			{
				SpaceModel.SpaceModelReqTypes objType = (SpaceModel.SpaceModelReqTypes) res.get( "reqType" );
				switch ( objType )
				{
					case REPORT :
					{
						Message msgref = new Message( );
						msgref.what = SpaceMe.MSG_REFRESH;
						handler.sendMessage( msgref );
					}
						break;
				}
			}
		}
	}

	/**
	 * 适配器
	 */
	private class SpaceReportAdapter extends BaseAdapter
	{
		private String[] types;
		private Context mContext;
		private Map<String,Boolean> keyMap;

		private OnItemClickListener onItemClickListener;

		public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
			this.onItemClickListener = onItemClickListener;
		}

		private void setData(Context context, String[]types)
		{
			this.mContext = context;
			this.types = types;
			keyMap = new HashMap<>();

			for (String str : types){
				keyMap.put(str,false);
			}
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			return types != null && types.length > 0 ? types.length : 0;
		}

		@Override
		public Object getItem(int position) {
			return types[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final MyViewHolder viewHolder;

			if (convertView == null)
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_normal_phrase_list_item,parent,false);
			}
			viewHolder = new MyViewHolder(convertView);
			convertView.setTag(viewHolder);
			viewHolder.tvText.setText(types[position]);

			if(keyMap.get(types[position])){
				viewHolder.mIvChecked.setImageResource(R.drawable.round_check_true);
			}else{
				viewHolder.mIvChecked.setImageResource(R.drawable.round_check_false);
			}

			viewHolder.rlReportLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (String str : types){
						if (str.equals(types[position])){
							keyMap.put(str,true);
							notifyDataSetChanged();
							if (onItemClickListener != null){
								onItemClickListener.onItemClick(position);
							}
						}else{
							keyMap.put(str,false);
						}
					}
				}
			});


			return convertView;
		}

		class MyViewHolder
		{
			public RelativeLayout rlReportLayout;
			public TextView tvText;
			public ImageView mIvChecked;

			public MyViewHolder(View convertView)
			{
				rlReportLayout = (RelativeLayout) convertView.findViewById(R.id.rl_reportLayout);
				tvText = (TextView) convertView.findViewById(R.id.text1);
				mIvChecked = (ImageView) convertView.findViewById(R.id.check_box);
			}
		}


	}

	interface OnItemClickListener{
		void onItemClick(int position);
	}
}
