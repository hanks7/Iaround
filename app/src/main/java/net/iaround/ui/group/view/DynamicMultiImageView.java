package net.iaround.ui.group.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-5 下午2:36:20
 * @Description: 动态中图片View，图片显示有1张~6张的情况
 */
public class DynamicMultiImageView extends LinearLayout {
	
	// 照片的Url列表
	private ArrayList<String> imagesList;

	private int defRes = R.drawable.default_pitcure_small_angle;

	private ArrayList<ImageView> viewList;

	public DynamicMultiImageView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(
				R.layout.dynamic_multi_image_layout, this);
		initVariable();
	}

	public DynamicMultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(
				R.layout.dynamic_multi_image_layout, this);
		initVariable();
	}
	
	public DynamicMultiImageView(Context context, AttributeSet attrs,
                                 int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(
				R.layout.dynamic_multi_image_layout, this);
		initVariable();
	}

	public void setList(ArrayList<String> lists) {
		imagesConvertThumbnail(lists);
		initView();
	}

	private void initVariable() {

		viewList = new ArrayList<ImageView>();
		ImageView iv1 = (ImageView) findViewById(R.id.ivPhoto1);
		iv1.setOnClickListener(ImageViewOnClickListener);
//		iv1.setTag(0);
		ImageView iv2 = (ImageView) findViewById(R.id.ivPhoto2);
		iv2.setOnClickListener(ImageViewOnClickListener);
//		iv2.setTag(1);
		ImageView iv3 = (ImageView) findViewById(R.id.ivPhoto3);
		iv3.setOnClickListener(ImageViewOnClickListener);
//		iv3.setTag(2);
		ImageView iv4 = (ImageView) findViewById(R.id.ivPhoto4);
		iv4.setOnClickListener(ImageViewOnClickListener);
//		iv4.setTag(3);
		ImageView iv5 = (ImageView) findViewById(R.id.ivPhoto5);
		iv5.setOnClickListener(ImageViewOnClickListener);
//		iv5.setTag(4);
		ImageView iv6 = (ImageView) findViewById(R.id.ivPhoto6);
		iv6.setOnClickListener(ImageViewOnClickListener);
//		iv6.setTag(5);
		viewList.add(iv1);
		viewList.add(iv2);
		viewList.add(iv3);
		viewList.add(iv4);
		viewList.add(iv5);
		viewList.add(iv6);

	}


	private void initView()
	{
		int count = viewList.size();
		for(int i = 0; i < count; i ++)
		{
			if(i < imagesList.size())
			{
				viewList.get(i).setVisibility( View.VISIBLE );
				String url = imagesList.get(i);
				if(!url.contains(PathUtil.getHTTPPrefix()))
				{
					url = PathUtil.getFILEPrefix() + url;
				}
//				ImageViewUtil.getDefault().loadImage(url, viewList.get(i), defRes, defRes);
				GlideUtil.loadImage(BaseApplication.appContext,url,viewList.get(i));
			}else
			{
				viewList.get(i).setVisibility(View.GONE);
			}
		}
	}

	// 图片点击事件
	private View.OnClickListener ImageViewOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			int position = (Integer) arg0.getTag();
			//gh
//			SpacePictureActivity.launch(getContext(), imagesList, position);
		}
	};

	// 把图片转化为小图
	private void imagesConvertThumbnail(ArrayList<String> lists) {
		if (lists == null) {
			return;
		}

		imagesList = new ArrayList<String>();
		for (int i = 0; i < lists.size(); i++) {
			String url = lists.get(i);
			if(url.contains(PathUtil.getHTTPPrefix()))
			{
				url = CommonFunction.thumPicture(url);
			}
			imagesList.add(url);
		}
	}
}
