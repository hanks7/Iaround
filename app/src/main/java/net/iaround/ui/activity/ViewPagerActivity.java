/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.picture.HackyViewPager;
import net.iaround.tools.picture.PhotoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ViewPagerActivity extends BaseActivity implements
		View.OnClickListener {
	private ArrayList<String> pathList;
	private ViewPager mViewPager;

	//标题栏
	private ImageView tvLeft;// 返回按钮
	private TextView tvTitle;// 标题
	private ImageView tvRight;// 确定按钮
	private FrameLayout flLeft;
	private LinearLayout llRight;

	private CheckBox cbSelect;// 选择按钮
	private TextView tvCount;//选中数量显示
	private Set<String> selectedPathSet = new HashSet<String>();
	private int maxCount;//可以选中的最大数量

	/**
	 * 
	 * @param context
	 * @param pathList 显示的图片路径list
	 * @param selectedPathList 已经选中的路径set
	 * @param maxCount 最多选中的数量
	 * @param requestCode 请求码
	 */
	public static void skipToViewPagerActivity(Context context,
			ArrayList<String> pathList, ArrayList<String> selectedPathList, int maxCount,
			int requestCode) {

		Intent intent = new Intent(context, ViewPagerActivity.class);
		intent.putStringArrayListExtra("PATH_LIST", pathList);
		intent.putStringArrayListExtra("SELETED_LIST", selectedPathList);
		intent.putExtra("POSITION", 0);
		intent.putExtra("MAX_COUNT", maxCount);
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	/**
	 * 
	 * @param context
	 * @param pathList 显示的图片路径list
	 * @param position 当前显示的图片position
	 * @param selectedPathList 已经选中的路径set
	 * @param maxCount 最多选中的数量
	 * @param requestCode 请求码
	 */
	public static void skipToViewPagerActivity(Context context,
			ArrayList<String> pathList, int position,
			ArrayList<String> selectedPathList, int maxCount, int requestCode) {

		Intent intent = new Intent(context, ViewPagerActivity.class);
		intent.putStringArrayListExtra("PATH_LIST", pathList);
		intent.putStringArrayListExtra("SELETED_LIST", selectedPathList);
		intent.putExtra("POSITION", position);
		intent.putExtra("MAX_COUNT", maxCount);
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

		pathList = getIntent().getStringArrayListExtra("PATH_LIST");
		ArrayList<String> seletedList = getIntent().getStringArrayListExtra(
				"SELETED_LIST");
		selectedPathSet.addAll(seletedList);

		int position = getIntent().getIntExtra("POSITION", 0);
		maxCount = getIntent().getIntExtra("MAX_COUNT", 1);

		setContentView(R.layout.activity_view_pager);

		initView();

		mViewPager.setCurrentItem(position);
		updateTitle(position);
		updateSelectBtn(position);
	}

	private void initView() {
		tvLeft = (ImageView) findViewById(R.id.iv_left);
		tvLeft.setImageResource(R.drawable.title_back);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvRight = (ImageView) findViewById(R.id.iv_right);
//		tvRight.setText("完成");
		tvRight.setImageResource(R.drawable.icon_publish);

		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById(R.id.fl_right).setOnClickListener(this);
		tvRight.setOnClickListener(this);
		tvLeft.setOnClickListener(this);

		cbSelect = (CheckBox) findViewById(R.id.cbSelect);
		cbSelect.setOnCheckedChangeListener(mCheckedChangeListener);
		cbSelect.setOnTouchListener(mTouchListener);
		
		tvCount = (TextView) findViewById(R.id.tvCount);
		tvCount.setOnClickListener(this);
		tvCount.setOnTouchListener(mTouchListener);

		FrameLayout flDisplay = (FrameLayout) findViewById(R.id.flDisplay);

		mViewPager = new HackyViewPager(this);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {

						updateTitle(arg0);
						updateSelectBtn(arg0);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});

		flDisplay.addView(mViewPager);
		mViewPager.setAdapter(new SamplePagerAdapter(pathList));
	}

	OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			int position = mViewPager.getCurrentItem();
			if (isChecked) {
				selectedPathSet.add(pathList.get(position));
			} else {
				selectedPathSet.remove(pathList.get(position));
			}
			updateSelectBtn(position);
		}
	};
	
	OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			
			String path = pathList.get(mViewPager.getCurrentItem());
			if(arg1.getAction() == MotionEvent.ACTION_DOWN)
			{

				if (getPicSize(path)[0] < 350 | getPicSize(path)[1] < 350){
					CommonFunction.showToast(ViewPagerActivity.this,getString(R.string.photo_size_error), Toast.LENGTH_SHORT);
					return true;
				}
				if (selectedPathSet.size() >= maxCount
						&& !selectedPathSet.contains(path)) {
					CommonFunction.toastMsg(mContext, "超出可选范围");
					return true;
				}
			}
			
			return false;
		}
	};

	@Override
	public void onClick(View arg0) {

		if (arg0.equals(tvRight) || arg0.getId() == R.id.fl_right) {
			finishForResult();
		} else if (arg0.equals(tvLeft) || arg0.getId() == R.id.fl_left) {
			finishForResult();
		}else if(arg0.equals(tvCount))
		{
			cbSelect.performClick();
		}
	}
	
	@Override
	public void onBackPressed() {
		finishForResult();
	}
	
	private void finishForResult()
	{
		ArrayList<String> selectedList = new ArrayList<String>(selectedPathSet.size());
		Iterator<String> iterator = selectedPathSet.iterator();
		while(iterator.hasNext())
		{
			selectedList.add(iterator.next());
		}
		
		Intent intent = new Intent();
		intent.putStringArrayListExtra("SELECTED_PATH", selectedList);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void updateTitle(int position) {
		String title = (position + 1) + "/" + pathList.size();
		tvTitle.setText(title);
	}

	private void updateSelectBtn(int position) {
		if (selectedPathSet.contains(pathList.get(position))) {
			cbSelect.setChecked(true);
			tvCount.setText("(" + selectedPathSet.size() + ")");
		} else {
			cbSelect.setChecked(false);
			tvCount.setText("选择");
		}
	}

	static class SamplePagerAdapter extends PagerAdapter {
		private ArrayList<String> list;

		public SamplePagerAdapter(ArrayList<String> list) {
			this.list = list;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {

			super.setPrimaryItem(container, position, object);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());

			try {
				Bitmap bitmap = createBitmap(list.get(position));
				photoView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	public static Bitmap createBitmap(String path) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		return createBitmap(fis);
	}

	public static Bitmap createBitmap(InputStream is) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = 2;
		return BitmapFactory.decodeStream(is, null, opt);
	}

	private int[] getPicSize(String path){
		int[] picSize = new int[2];
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile( path, opts);
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;
		BitmapFactory.decodeFile(path, opts);
		picSize[0]=opts.outWidth;
		picSize[1]=opts.outHeight;
		return picSize;
	}
}
