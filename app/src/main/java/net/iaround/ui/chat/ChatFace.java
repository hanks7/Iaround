package net.iaround.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceIcon;
import net.iaround.tools.FaceManager.FaceLogoIcon;
import net.iaround.ui.chat.view.ChatFacePointView;
import net.iaround.ui.face.FaceMainActivityNew;
import net.iaround.ui.face.SendFaceToFriends;
import net.iaround.ui.view.dynamic.CirclePageIndicator;
import net.iaround.ui.view.dynamic.GrapeGridview;
import net.iaround.ui.view.dynamic.MeBaseAdapter;

import java.util.ArrayList;

/**
 * 表情菜单视图
 * 
 * @author chenlb
 * 
 */
public class ChatFace extends LinearLayout implements OnPageChangeListener {
	public static final int TYPE_NOMAL = 1;// 经典表情
	public static final int TYPE_CAT = 2;// 有猫表情的
	public static final int TYPE_MORE = 3;// 全部表情
	private static final int FACE_PAGE_NUM = 20;
	private static final int GIFFACE_PAGE_NUM = 8;
	private int chatType;
	private FaceManager faceManager;
	protected Context mContext;
	protected LayoutInflater vInflater; // xml 解析器对象
	private int imWid, imCount, pWidth,lastIndex = 0, mSelectedTab = 0;
	private float scrLength;
	private Handler mHandler;
	private OnItemClickListener iconClickListener;

	private ViewPager parentViewPager;
	private ParentPagerAdapter parentPagerAdapter;//放置表情的容器
	private RelativeLayout faceIconView;
	private LinearLayout bottomBar;//表情底部的导航条
	private ImageView ivBuyIcon;//跳转到表情商城的按钮
	private ImageView ivNewPoint;//是否有新的贴图表情更新提示图标
	private View vFaceBuy;
	private CirclePageIndicator pageIndicator;
	private ChatFacePointView pointsView;//表情底部的圆点指示器
	private HorizontalScrollView scrollView;
	private View[] faceLogos;
	private ArrayList<GridChatFace> mIconList=new ArrayList<GridChatFace>(  );

	private ArrayList<FaceLogoIcon> logos; // 分组
	private ArrayList<Integer> firstIndexs;

	public ChatFace(Context context, ViewGroup viewGroup, int type)
	{
		super(context);
		chatType = type;
		init(context);
		addView(vInflater.inflate(R.layout.chat_face, viewGroup, false),
			new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		initialComponent();
		initIconGroup();
	}

	public ChatFace(Context context, ViewGroup viewGroup)
	{
		this( context, viewGroup, TYPE_MORE );
	}


	public ChatFace(Context context, int Type) {
		super(context);
		this.chatType = Type;
		init(context);
		addView(vInflater.inflate(R.layout.chat_face, null), new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		initialComponent();
		initIconGroup();
	}

	public ChatFace(Context context){
		super(context);
		init(context);
		initialComponent();
		initIconGroup();
	}

	private void init(Context context) {
		DisplayMetrics metric = getResources().getDisplayMetrics();
		pWidth = metric.widthPixels; // 屏幕宽度（像素）
		mContext = context;
		mHandler = new Handler();
		vInflater = LayoutInflater.from(context);
	}

	protected void initialComponent() {
		faceManager = FaceManager.getInstance(mContext);
		ArrayList<FaceLogoIcon> logosArrayList = FaceManager
			.getGifFaceGroupLogos();
		if (chatType == TYPE_NOMAL) {
			logos = new ArrayList<FaceLogoIcon>();
			logos.add(logosArrayList.get(2));
		} else if (chatType == TYPE_CAT) {
			logos = new ArrayList<FaceLogoIcon>();
			logos.add(logosArrayList.get(1));
			logos.add(logosArrayList.get(2));
		} else if (chatType == TYPE_MORE) {
			logos = new ArrayList<FaceLogoIcon>();
			logos.addAll(logosArrayList);
		}

		int faceGroupCount = logos.size();
		scrollView = (HorizontalScrollView) findViewById(R.id.face_scrollView);
		faceIconView = (RelativeLayout) findViewById(R.id.faceIconView);
		parentViewPager = (ViewPager) findViewById(R.id.main_viewpager);
		pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		pageIndicator.setVisibility( View.GONE );
		pointsView=(ChatFacePointView )findViewById( R.id.chatface_point );
		bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
		vFaceBuy = findViewById(R.id.rlShop);
		ivBuyIcon = (ImageView) vFaceBuy.findViewById(R.id.faceLogo);

		ivNewPoint = (ImageView) vFaceBuy.findViewById(R.id.ivNewPoint);
		vFaceBuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Common.newFaceCount = 0;
				Intent i = new Intent(mContext, FaceMainActivityNew.class);
				mContext.startActivity(i);
			}
		});

		// 检查是否表情有更新
		updateNewFace();
		faceLogos = new View[faceGroupCount];
		for (int i = 0; i < faceGroupCount; i++) {
			faceLogos[i] = View.inflate(mContext,
				R.layout.chat_face_item_layout, null);
			ImageView logo = (ImageView) faceLogos[i]
				.findViewById(R.id.faceLogo);
			FaceLogoIcon logoIcon = logos.get(i);
			faceLogos[i].setTag(logoIcon.key);
			faceManager.showFaceSelect(logo, logoIcon.iconPath);
			bottomBar.addView(faceLogos[i]);
			faceLogos[i]
				.setOnClickListener(new ClickListener(i,logoIcon.valid));
		}

		if (chatType == TYPE_MORE) {
			vFaceBuy.setVisibility(View.VISIBLE);
		} else {
			vFaceBuy.setVisibility(View.GONE);
		}
	}

	public void initIconGroup(){
		firstIndexs=new ArrayList<Integer>(  );
		int firstPage=0;
		for(int i=0;i<logos.size();i++)
		{
			String tabKey = logos.get( i ).key;
			ArrayList<FaceIcon> faceLocalList=faceManager.getFaceLocalList(tabKey);
			if (i > 0 && i < 3 || chatType == ChatFace.TYPE_NOMAL || chatType == ChatFace.TYPE_CAT)
			{
				int iconSize = faceLocalList.size( );
				// 表情分组后的组数
				int iconGroup = iconSize / FACE_PAGE_NUM;
				if ( ( iconSize % FACE_PAGE_NUM ) != 0 )
				{
					iconGroup++;
				}
				for ( int j = 0; j < iconGroup; j++ )
				{
					GridChatFace cf=new GridChatFace();
					cf.iconList=getEachIconGroupList( faceLocalList, j );
					cf.type= GridChatFace.EACH_ICON;
					if(j==0){
						cf.isFirst=true;
					}
					cf.page=j;
					cf.totalPage=iconGroup;
					cf.firstPage=firstPage;
					mIconList.add( cf );
				}
				firstIndexs.add(firstPage);
				firstPage+=iconGroup;
			}else{
				int iconSize = faceLocalList.size();
				// 表情分组后的组数
				int iconGroup = iconSize / GIFFACE_PAGE_NUM;
				if ((iconSize % GIFFACE_PAGE_NUM) != 0) {
					iconGroup++;
				}
				if(iconSize>0)
				{
					for ( int j = 0; j < iconGroup; j++ )
					{
						GridChatFace cf = new GridChatFace( );
						cf.iconList = getGifEachIconGroupList( faceLocalList, j );
						cf.type = GridChatFace.GIF_ICON;
						if ( j == 0 )
						{
							cf.isFirst = true;
						}
						cf.page = j;
						cf.totalPage = iconGroup;
						cf.firstPage = firstPage;
						mIconList.add( cf );
					}
				}else{
					GridChatFace cf = new GridChatFace( );
					cf.iconList = new ArrayList< FaceIcon >(  );
					cf.type = GridChatFace.GIF_ICON;
					cf.isFirst = true;
					cf.page = 0;
					cf.totalPage = 1;
					cf.firstPage = firstPage;
					mIconList.add( cf );
					iconGroup=1;
				}
				firstIndexs.add(firstPage);
				firstPage+=iconGroup;
			}
		}
		parentPagerAdapter=new ParentPagerAdapter( mIconList );
		parentViewPager.setAdapter( parentPagerAdapter );
		parentViewPager.setOnPageChangeListener( this );
		parentViewPager.setOffscreenPageLimit( 2 );
	}

	@Override
	public void onPageSelected( int page )
	{
		scrLength = scrollView.getScrollX( );
		int index=firstIndexs.indexOf(mIconList.get( page ).firstPage);
		imWid = ivBuyIcon.getWidth( );
		if(index!=lastIndex)
		{
			if ( index > 1 )
			{
				if ( imWid == 0 )
				{
					return;
				}
				imCount = ( pWidth - imWid ) / imWid;
				if ( ( index + 1 ) * imWid >= scrLength + pWidth - imWid && index > lastIndex &&
					lastIndex - index == -1 )
				{
					scrollView.scrollTo( ( index + 1 - imCount ) * imWid, 0 );
				}
				if ( lastIndex > index && index * imWid < scrLength )
				{
					scrollView.scrollTo( index * imWid, 0 );
				}

			}
			else
			{
				scrollView.scrollTo( 0, 0 );
			}
			setCurrentTab( index );
		}
		lastIndex = index;
	}

	float lastPositionOffset;
	@Override
	public void onPageScrolled( int page, float positionOffset, int positionOffsetPixels )
	{
		pointsView.setCurrentItem( mIconList.get( page ).page );
		pointsView.setPageOffset( positionOffset );
		pointsView.setCount( mIconList.get( page ).totalPage );
		pointsView.invalidate();
		lastPositionOffset=positionOffset;
	}

	@Override
	public void onPageScrollStateChanged( int i )
	{

	}

	/**
	 * 设置第几个选项高亮和有效
	 */
	public void setCurrentTab(final int index) {
		bottomBar.getChildAt(mSelectedTab).setSelected(false);
		bottomBar.getChildAt(mSelectedTab).setEnabled(true);
		setCurrentFaceLogo(mSelectedTab, false);

		mSelectedTab = index;
		bottomBar.getChildAt(index).setSelected(true);
		bottomBar.getChildAt(index).setEnabled(false);
		setCurrentFaceLogo(index, true);

		if (SendFaceToFriends.isFromSendFriends == true) {
			bottomBar.getChildAt(index).setFocusable(true);
			bottomBar.getChildAt(index).setFocusableInTouchMode(true);
			bottomBar.getChildAt(index).requestFocus();
		}

	}

	private void setCurrentFaceLogo(int index, boolean isSelected) {
		boolean isEnabled = !isSelected;

		faceLogos[index].setSelected(isSelected);
		faceLogos[index].setEnabled(isEnabled);
	}

	/**
	 * ViewPager适配器
	 */
	private class ParentPagerAdapter extends PagerAdapter {

		private ArrayList<GridChatFace> iconsList;

		public ParentPagerAdapter(ArrayList<GridChatFace> iconsList) {
			this.iconsList = iconsList;
		}

		@Override
		public int getCount() {
			return iconsList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			View view;
			if(iconsList.get( position ).type== GridChatFace.EACH_ICON)
			{
				GrapeGridview gv = (GrapeGridview) vInflater.inflate(
					R.layout.chat_face_icon_grid, null);
				DataAdapter adapter = new DataAdapter( iconsList.get( position ).iconList, false );
//				gv.setLayoutParams(params);
				gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
				gv.setOnItemClickListener(iconClickListener);
				gv.setAdapter( adapter );
				container.addView(gv, 0);
				view=gv;
			}else{
				GridView gv = (GridView) vInflater.inflate(
					R.layout.chat_face_icon_grid_2, null);
				DataAdapter adapter = new DataAdapter( iconsList.get( position ).iconList, true );
				gv.setLayoutParams(params);
				gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
				gv.setOnItemClickListener(iconClickListener);
				gv.setAdapter( adapter );
				container.addView(gv, 0);
				view=gv;
			}

			return view;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/**
	 * 底部导航栏适配器
	 */
	private class DataAdapter extends MeBaseAdapter {
		private boolean isGif;

		protected DataAdapter(ArrayList<?> arrayList, boolean isGif) {
			super(arrayList);
			this.isGif = isGif;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				if (isGif) {
					convertView = vInflater.inflate(
						R.layout.chat_face_icon_view_2, null);
				} else {
					convertView = vInflater.inflate(
						R.layout.chat_face_icon_view, null);
				}
			}

			ImageView iconView = (ImageView) convertView
				.findViewById(R.id.icon);
			TextView textView = (TextView) convertView.findViewById(R.id.text);
			FaceIcon icon = (FaceIcon) getItem(position);

			if (isGif) {
				textView.setText(icon.description);
				faceManager.showFaceSelect(iconView, icon.iconPath);
			} else {
				faceManager.showFaceSelect(iconView, icon.iconId);
			}
			convertView.setTag(icon);

			return convertView;
		}

		@Override
		public View createView(int position) {
			return null;
		}
	}

	/**
	 * @Title: updateNewFace
	 * @Description: 更新是否有新表情的图标显示
	 */
	public void updateNewFace() {
		// 如果有新表情贴图，则显示new的图标
		if (ivNewPoint != null) {
			if (Common.newFaceCount > 0) {
				ivNewPoint.setVisibility(View.VISIBLE);
			} else {
				ivNewPoint.setVisibility(View.GONE);
			}
		}
	}

	private class ClickListener implements OnClickListener {
		private final int index;
		// 是否过期
		private final int valid;

		private ClickListener(int index, int valid) {
			this.index = index;
			this.valid = valid;
		}


		public void onClick(View v) {

			clickTab(index, valid);
		}
	}

	/**
	 * 获取每个分组的表情列表
	 *
	 * @param iconsList
	 *            表情列表
	 * @param group
	 *            当前的组
	 * @return
	 */
	private ArrayList<FaceIcon> getEachIconGroupList(
            ArrayList<FaceIcon> iconsList, int group) {
		ArrayList<FaceIcon> list = new ArrayList<FaceIcon>();

		// 开始位置和结束位置
		int startPosition = group * FACE_PAGE_NUM;
		int endPosition = startPosition + FACE_PAGE_NUM;

		for (int i = startPosition; i < endPosition; i++) {
			if ((i + 1) > iconsList.size()) { // 是否越界
				FaceIcon ic = new FaceIcon();
				ic.key = "";
				ic.iconId = -1;
				list.add(ic);
				continue;
				// break;
			}

			list.add(iconsList.get(i));
		}

		FaceIcon ic = new FaceIcon();
		ic.key = "back";
		ic.iconId = R.raw.delete_face_unselect;
		ic.iconPath = "drawable://" + R.raw.delete_face_unselect;
		list.add(ic);
		return list;
	}

	private ArrayList<FaceIcon> getGifEachIconGroupList(
            ArrayList<FaceIcon> iconsList, int group) {
		ArrayList<FaceIcon> list = new ArrayList<FaceIcon>();

		// 开始位置和结束位置
		int startPosition = group * GIFFACE_PAGE_NUM;
		int endPosition = startPosition + GIFFACE_PAGE_NUM;

		for (int i = startPosition; i < endPosition; i++) {
			if ((i + 1) > iconsList.size()) { // 是否越界
				FaceIcon ic = new FaceIcon();
				ic.key = "";
				ic.iconId = -1;
				list.add(ic);
				continue;
				// break;
			}

			list.add(iconsList.get(i));
		}

		return list;
	}

	class GridChatFace{
		public static final int EACH_ICON=1; //静态表情图
		public static final int GIF_ICON=2; //动态表情图
		public ArrayList<FaceIcon> iconList;
		public int type;
		public boolean isFirst;//是不是所属表情包的第一次
		public int firstPage;//所属表情包的第一页下标
		public int totalPage;//所属表情包的总页数
		public int page;//当前是所属表情包的第几页
	}

	/**
	 * 设置点击表情图片的响应事件
	 */
	public void setIconClickListener(OnItemClickListener l) {
		iconClickListener = l;
	}

	/**
	 * 初始化表情
	 */
	public void initFace() {
		if (chatType == ChatFace.TYPE_NOMAL || chatType == ChatFace.TYPE_CAT) {
			clickTab(0,1);
		} else {
			clickTab(1,1);
		}

		// 检查是否有新表情
		updateNewFace();
	}

	/**
	 * 选择当前显示的view
	 *
	 * @param index
	 */
	public void clickTab(int index, int valid) {
		setCurrentTab(index);
		parentViewPager.setCurrentItem(firstIndexs.get( index ));
		SendFaceToFriends.isFromSendFriends = false;
		pointsView.setCount( mIconList.get( firstIndexs.get( index ) ).totalPage );
		pointsView.setCurrentItem( 0 );
		pointsView.setPageOffset( 0 );
		pointsView.invalidate();
	}

	public void setKeyboardClickListener(OnClickListener l) {
		// keyboard.setOnClickListener(l);
	}
}
