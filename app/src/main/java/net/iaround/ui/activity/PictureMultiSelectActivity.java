package net.iaround.ui.activity;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PathUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.picture.DoubleClickListener;
import net.iaround.utils.ImageViewUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-10-27 上午10:16:17
 * @Description: 多图选择的Activity
 */
public class PictureMultiSelectActivity extends BaseActivity implements
        LoaderCallbacks<Cursor>, OnClickListener {

    private FrameLayout flLeft;
    private ImageView ivLeft;// 返回按钮
    private TextView tvTitle;// 标题
    private ImageView tvRight;// 确定按钮

    private TextView tvAlbum;// 相册按钮
    private TextView tvDisplay;// 预览按钮
    private RelativeLayout rlAlbum;// 相册背景布局
    private ListView lvAlbum;// 相册listView
    private GridView mGridView;// 图片gridView

    private ArrayList<String> pathList = new ArrayList<String>();// 照片路径的list
    private SparseArray<String> buckSpareseArray = new SparseArray<String>();// 相册名字SparseArray
    private SparseArray<String> buckPageSpareseArray = new SparseArray<String>();// 相册封面SparseArray
    //	private HashSet<String> selectedPathSet = new HashSet<String>();
    private ArrayList<String> selectedPathList = new ArrayList<String>();

    private BuckNameAdapter albumAdapter;
    private AlbumGridAdapter picGridAdapter;

    private String ALBUM_ALL;
    private boolean isFirst = true;// 是否第一次载入相册
    private int IMAGEVIEW_WIDTH = 0;// 每张图片View的宽度
    private int currentCanSelectMaxSize = 9;// 当前可选择图片的数量
    private boolean IS_CROP = false;// 默认是不裁剪
    private boolean IS_ONE_CHOICE = false;// 是否只选一张照片
    private boolean IS_TAKE_PHOTO_ONLY = false;// 是否只拍照
    private String path = "";// 保存拍照返回的路径

    public static String PATH_LIST = "pathList";// 进入相册返回的List的key
    public static final int MAX_PHOTOT_SIZE = 8;//选择照片的最大数量
    public static final int IMAGE_TAG = 100;//选择照片的最大数量
    private static final String MAX_SIZE_KEY = "MAX_SIZE_KEY";
    private static final String IS_CROP_KEY = "IS_CROP";
    private static final String IS_ONE_CHOICE_KEY = "IS_ONE_CHOICE";// 是否只选一张照片的key
    private static final String IS_TAKE_PHOTO_ONLY_KEY = "IS_TAKE_PHOTO_ONLY";// 是否只拍照的key
    private final int CROP_REQUEST_CODE = 0xff;// 截图的请求码
    private final int DISPLAY_REQUEST_CODE = 0xfe;// 大图展示请求码

    private String TITLE_TEXT;
    int defRes = R.drawable.default_pitcure_small_angle;

    public static final String OUTPUT_PATH = "outputPath";
    public static final String ORIGINAL_PATH = "originalPath";
    public static final String FILE_PATH = "filePath";

    final String[] columns = {MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATE_ADDED};

    String seletion = null;// buckName的选择条件,全部的时候为null
    String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";// 照片排序,事件倒序

    private final int VIEW_TAG = R.layout.multi_select_item;// View,setTag时候的标签,Value为View
    private final int PATH_TAG = R.layout.activity_picture_multi_select;// View,setTag时候的标签,Value为图片的路径
    private final int BUCK_NAME_TAG = R.id.abTitle;// 相册的名字的Tag
    private final int BUCK_ID_TAG = R.id.accordion;// 相册的ID的Tag

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    /**
     * 跳转到多图选择的Activity, 只选一张照片
     *
     * @param context
     * @param requestCode
     */
    public static void skipToPictureMultiSelectAlbumOneChoise(Context context,
                                                              int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, PictureMultiSelectActivity.class);
        intent.putExtra(IS_CROP_KEY, false);
        intent.putExtra(MAX_SIZE_KEY, 1);
        intent.putExtra(IS_ONE_CHOICE_KEY, true);
        intent.putExtra("IS_TAKE_PHOTO_ONLY_KEY", false);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到多图选择的Activity
     *
     * @param context
     * @param requestCode
     * @param MaxSeleted  最大照片选择数
     */
    public static void skipToPictureMultiSelectAlbum(Context context,
                                                     int requestCode, int MaxSeleted) {
        Intent intent = new Intent();
        intent.setClass(context, PictureMultiSelectActivity.class);
        intent.putExtra(MAX_SIZE_KEY, MaxSeleted);
        intent.putExtra(IS_CROP_KEY, false);
        intent.putExtra(IS_ONE_CHOICE_KEY, false);
        intent.putExtra("IS_TAKE_PHOTO_ONLY_KEY", false);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到多图选择的Activity
     *
     * @param context
     * @param requestCode
     * @param MaxSeleted  最大照片选择数
     */
    public static void skipToPictureMultiSelectAlbum(Context context, ArrayList<String> imgList,
                                                     int requestCode, int MaxSeleted) {
        Intent intent = new Intent();
        intent.setClass(context, PictureMultiSelectActivity.class);
        intent.putExtra(MAX_SIZE_KEY, MaxSeleted);
        intent.putExtra(IS_CROP_KEY, false);
        intent.putExtra(IS_ONE_CHOICE_KEY, false);
        intent.putExtra("IS_TAKE_PHOTO_ONLY_KEY", false);
        intent.putStringArrayListExtra("PIC_SELECT", imgList);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到多图选择的Activity,且需要裁减
     *
     * @param context
     * @param requestCode
     */
    public static void skipToPictureMultiSelectAlbumCrop(Context context,
                                                         int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, PictureMultiSelectActivity.class);
        intent.putExtra(MAX_SIZE_KEY, 1);
        intent.putExtra(IS_CROP_KEY, true);
        intent.putExtra(IS_ONE_CHOICE_KEY, false);
        intent.putExtra("IS_TAKE_PHOTO_ONLY_KEY", false);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void skipToPictureMultiSelectAlbumTakePhotoOnly(
            Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, PictureMultiSelectActivity.class);
        intent.putExtra(MAX_SIZE_KEY, 1);
        intent.putExtra("IS_CROP", false);
        intent.putExtra(IS_ONE_CHOICE_KEY, false);
        intent.putExtra(IS_TAKE_PHOTO_ONLY_KEY, true);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public void takePhotoByCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = setUpPhotoFile();
            path = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            path = null;
        }

        startActivityForResult(takePictureIntent, CommonFunction.TAKE_PHOTO_REQ);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getExtra();
        ALBUM_ALL = getResString(R.string.picture_multi_selected_all);
        TITLE_TEXT = getResString(R.string.picture_multi_selected_title);
        if (IS_TAKE_PHOTO_ONLY) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = null;
            try {
                f = setUpPhotoFile();
                path = f.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                path = null;
            }

            startActivityForResult(takePictureIntent, CommonFunction.TAKE_PHOTO_REQ);

        } else {
            setContentView(R.layout.activity_picture_multi_select);
            initData();
            initView();
            if (IS_CROP || IS_ONE_CHOICE) {
                tvRight.setVisibility(View.GONE);
                tvDisplay.setVisibility(View.GONE);
            }

            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PATH", path);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        path = outState.getString("PATH");
    }

    // 获取activity的设置
    private void getExtra() {
        currentCanSelectMaxSize = getIntent().getIntExtra(MAX_SIZE_KEY, 1);
        IS_CROP = getIntent().getBooleanExtra(IS_CROP_KEY, false);
        IS_ONE_CHOICE = getIntent().getBooleanExtra(IS_ONE_CHOICE_KEY, false);
        IS_TAKE_PHOTO_ONLY = getIntent().getBooleanExtra(
                IS_TAKE_PHOTO_ONLY_KEY, false);
        if (getIntent().hasExtra("PIC_SELECT"))
            selectedPathList = getIntent().getStringArrayListExtra("PIC_SELECT");
    }

    // 通过屏幕的宽度,计算图片的显示大小
    private int getWith() {
        if (IMAGEVIEW_WIDTH == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            dm = this.getResources().getDisplayMetrics();
            IMAGEVIEW_WIDTH = (dm.widthPixels - 4) / 3;
        }

        return IMAGEVIEW_WIDTH;
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.gvPic);
        mGridView.setAdapter(picGridAdapter);
        mGridView.setOnItemClickListener(pictureClickListener);

        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivLeft.setBackgroundResource(R.drawable.title_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        updateTitle();
        tvRight = (ImageView) findViewById(R.id.iv_right);
//		tvRight.setText(R.string.ok);
        tvRight.setImageResource(R.drawable.icon_publish);

        flLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        tvTitle.setOnTouchListener(new DoubleClickListener(this));

        tvDisplay = (TextView) findViewById(R.id.tvDisplay);
        tvDisplay.setOnClickListener(this);
        updateDisplayBtn();

        tvAlbum = (TextView) findViewById(R.id.tvAlbum);
        tvAlbum.setOnClickListener(this);
        rlAlbum = (RelativeLayout) findViewById(R.id.rlAlbum);
        lvAlbum = (ListView) findViewById(R.id.lvAlbum);
        lvAlbum.setAdapter(albumAdapter);
    }

    private void updateDisplayBtn() {
        if (selectedPathList.size() > 0) {
//			int colorValue = Color.argb(255, 181, 181, 181);
            tvDisplay.setTextColor(getResources().getColor(R.color.chat_update_message_count));
            tvDisplay.setClickable(true);
            String formatStr = getResString(R.string.picture_multi_selected_view_number);
            String sizeStr = String.valueOf(selectedPathList.size());
            tvDisplay.setText(String.format(formatStr, sizeStr));
        } else {
//			int colorValue = Color.argb(55, 181, 181, 181);
            tvDisplay.setTextColor(getResources().getColor(R.color.chat_update_message_count));
            tvDisplay.setClickable(false);
            tvDisplay.setText(getResString(R.string.picture_multi_selected_view));
        }
    }

    // 更新title
    private void updateTitle() {
        String args = selectedPathList.size() + "/" + currentCanSelectMaxSize;
        String title = String.format(TITLE_TEXT, args);
        tvTitle.setText(title);
    }

    private void initData() {

        picGridAdapter = new AlbumGridAdapter(pathList);
        albumAdapter = new BuckNameAdapter(buckSpareseArray, buckPageSpareseArray);
    }

    @Override
    public void onClick(View arg0) {

        if (arg0.equals(tvRight)) {

            if (selectedPathList.size() > 0) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(PATH_LIST, selectedPathList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                CommonFunction.toastMsg(mContext, R.string.picture_multi_selected_choose_content);
            }

        } else if (arg0.equals(ivLeft) || arg0.equals(flLeft)) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (arg0.equals(tvTitle)) {
            // gridView滑动到顶端
            ScrollToTop();
        } else if (arg0.equals(tvAlbum)) {
            albumBtnClick();
        } else if (arg0.equals(tvDisplay)) {
            ViewPagerActivity.skipToViewPagerActivity(mContext,
                    selectedPathList, selectedPathList, currentCanSelectMaxSize,
                    DISPLAY_REQUEST_CODE);
        }
    }

    // 相册按钮点击
    private void albumBtnClick() {
        if (rlAlbum != null && rlAlbum.getVisibility() == View.GONE) {
            showAlbum();
        } else {
            hideAlbum();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        return new CursorLoader(mContext,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                seletion, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {

        pathList.clear();
        if (arg1.getCount() == 0) {
            pathList.add(ALBUM_ALL);
        }

        if (arg1.moveToFirst()) {
            if (isFirst) {
                buckSpareseArray.put(Integer.MIN_VALUE, ALBUM_ALL);


            }
            if (seletion == null)// 如果选择的相册是全部的话,有照相的入口
            {
                pathList.add(ALBUM_ALL);
            }

            while (!arg1.isAfterLast()) {
                int dataColumnIndex = arg1
                        .getColumnIndex(MediaStore.Images.Media.DATA);
                String path = arg1.getString(dataColumnIndex);

                File file = new File(path);

                if (file.exists()) {
                    if (file.length() > 0) {
                        pathList.add(path);
                    }

                    if (isFirst) {
                        int buckColumnIndex = arg1
                                .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                        String buckName = arg1.getString(buckColumnIndex);

                        int buckIdColumnIndex = arg1
                                .getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                        int buckId = arg1.getInt(buckIdColumnIndex);


                        if (buckSpareseArray.indexOfKey(buckId) < 0) {
                            buckSpareseArray.put(buckId, buckName);
                        }


                        if (buckPageSpareseArray.indexOfKey(Integer.MIN_VALUE) < 0) {
                            buckPageSpareseArray.put(Integer.MIN_VALUE, path);
                        }
                        if (buckPageSpareseArray.indexOfKey(buckId) < 0) {
                            buckPageSpareseArray.put(buckId, path);
                        }
                    }
                }

                arg1.moveToNext();
            }
        }
        isFirst = false;
        picGridAdapter.notifyDataSetChanged();
        rlAlbum.bringToFront();
        rlAlbum.invalidate();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {

    }

    // 相册Grid的适配器
    private class AlbumGridAdapter extends BaseAdapter {

        private ArrayList<String> albumList;
        RelativeLayout.LayoutParams params;

        public AlbumGridAdapter(ArrayList<String> list) {
            this.albumList = list;
            params = new RelativeLayout.LayoutParams(getWith(), getWith());
        }

        @Override
        public int getCount() {

            return albumList == null ? 0 : albumList.size();
        }

        @Override
        public Object getItem(int arg0) {

            return albumList == null ? null : albumList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {

            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder holder;
            if (arg1 == null) {
                arg1 = View.inflate(mContext, R.layout.multi_select_item, null);

                holder = new ViewHolder();
                holder.ivMask = (ImageView) arg1.findViewById(R.id.ivMask);
                holder.mCheckBox = (CheckBox) arg1.findViewById(R.id.cbSelect);
                holder.mImageView = (ImageView) arg1.findViewById(R.id.ivImage);


                holder.mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
                holder.mCheckBox.setOnTouchListener(mTouchListener);

                holder.ivMask.setLayoutParams(params);
                holder.mImageView.setLayoutParams(params);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            initViewHolder(holder, arg1, arg0);
            arg1.setTag(holder);
            return arg1;
        }

        private class ViewHolder {
            public ImageView ivMask;
            public ImageView mImageView;
            public CheckBox mCheckBox;
        }

        private void initViewHolder(ViewHolder holder, View arg1, int position) {
            String path = pathList.get(position);


            if (path.equals(ALBUM_ALL)) {// 照相

                holder.mCheckBox.setVisibility(View.GONE);
                holder.mImageView.setScaleType(ScaleType.CENTER);

                ImageViewUtil.getDefault().loadImage(
                        "drawable://" + R.drawable.album_take_photo_icon,
                        holder.mImageView, defRes, defRes);
            } else {
                holder.mImageView.setScaleType(ScaleType.CENTER_CROP);

                holder.mImageView.setTag(position);
                ImageViewUtil.getDefault().loadImage(PathUtil.getFILEPrefix() + path,
                        holder.mImageView, defRes, defRes);

                int colorId = selectedPathList.contains(path) ? R.color.c_33000000
                        : R.color.transparent;
                holder.ivMask.setBackgroundColor(getResources().getColor(
                        colorId));


                if (IS_CROP || IS_ONE_CHOICE) {
                    holder.mCheckBox.setVisibility(View.GONE);
                } else {
                    holder.mCheckBox.setVisibility(View.VISIBLE);
                    holder.mCheckBox.setTag(VIEW_TAG, holder);
                    holder.mCheckBox.setTag(PATH_TAG, path);

                    holder.mCheckBox.setChecked(selectedPathList.contains(path));
                }

            }

        }

        OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                ViewHolder holder = (ViewHolder) buttonView.getTag(VIEW_TAG);
                String path = (String) buttonView.getTag(PATH_TAG);
                if (isChecked) {
                    if (!selectedPathList.contains(path)) {
                        selectedPathList.add(path);
                    }
                } else {
                    selectedPathList.remove(path);
                }
                updateTitle();
                updateDisplayBtn();
                int colorId = isChecked ? R.color.c_33000000
                        : R.color.transparent;
                holder.ivMask.setBackgroundColor(getResources().getColor(
                        colorId));
            }
        };

        OnTouchListener mTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    String path = (String) arg0.getTag(PATH_TAG);
                    if (getPicSize(path)[0] < 350 | getPicSize(path)[1] < 350){
                        CommonFunction.showToast(PictureMultiSelectActivity.this,getString(R.string.photo_size_error),Toast.LENGTH_SHORT);
                        return true;
                    }
                    if (selectedPathList.size() >= currentCanSelectMaxSize
                            && !selectedPathList.contains(path)) {
                        CommonFunction.toastMsg(mContext, R.string.picture_multi_selected_choose_over);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (CommonFunction.TAKE_PHOTO_REQ == requestCode)// 拍照成功
            {
                if (IS_CROP) {// 需要跳刀裁剪的PhotoCropActivity

                    try {
                        Intent intent = new Intent();
                        intent.setClass(mContext, PhotoCropActivity.class);
                        intent.putExtra(PhotoCropActivity.BITMAP, path);
                        intent.putExtra(PhotoCropActivity.OUTPUT_X, 640);
                        intent.putExtra(PhotoCropActivity.OUTPUT_Y, 640);
                        String oriPath = PathUtil.getSDPath()
                                + System.currentTimeMillis();
                        intent.putExtra(PhotoCropActivity.OUTPUT, oriPath);
                        intent.putExtra(PhotoCropActivity.RETURN_ORI_BITMAP, false);
                        intent.putExtra(PhotoCropActivity.ISCHAT_KEY, false);

                        startActivityForResult(intent, CROP_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    try {
                        Intent intent = new Intent();
                        ArrayList<String> pathArray = new ArrayList<String>();
//						path = CommonFunction.rotaingImage( path );
                        pathArray.add(path);
                        intent.putStringArrayListExtra(PATH_LIST, pathArray);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (CROP_REQUEST_CODE == requestCode)// 裁剪成功
            {
                String outPutPath = data
                        .getStringExtra(OUTPUT_PATH);

                Intent intent = new Intent();
                ArrayList<String> pathArray = new ArrayList<String>();
                pathArray.add(outPutPath);
                intent.putStringArrayListExtra(PATH_LIST, pathArray);
                setResult(RESULT_OK, intent);
                finish();
            } else if (DISPLAY_REQUEST_CODE == requestCode) {
                ArrayList<String> seletedList = data
                        .getStringArrayListExtra("SELECTED_PATH");
                if (seletedList == null) {
                    return;
                }
                selectedPathList.clear();
                for (int i = 0; i < seletedList.size(); i++) {
                    selectedPathList.add(seletedList.get(i));
                }
                picGridAdapter.notifyDataSetChanged();
            }

        }
    }

    // 显示相册选择器
    private void showAlbum() {
        if (rlAlbum != null) {
            rlAlbum.setVisibility(View.VISIBLE);
        }
    }

    // 显示相册选择器
    private void hideAlbum() {
        if (rlAlbum != null) {
            rlAlbum.setVisibility(View.GONE);
        }
    }

    // 照片GridView滑动到顶端
    private void ScrollToTop() {
        if (mGridView != null) {
            mGridView.setSelection(0);
        }
    }

    // GridView的Item的点击事件
    private AdapterView.OnItemClickListener pictureClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            int position = arg2;
            String selectedPath = pathList.get(position);
            if (selectedPath.equals(ALBUM_ALL)) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = null;
                try {
                    f = setUpPhotoFile();
                    path = f.getAbsolutePath();
                    Uri uri = FileProvider.getUriForFile(PictureMultiSelectActivity.this, getPackageName() + ".FileProvider", f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    path = null;
                }

                startActivityForResult(takePictureIntent, CommonFunction.TAKE_PHOTO_REQ);

            } else if (IS_CROP || IS_ONE_CHOICE) {

                if (IS_CROP)// 如果是要裁减,就需要跳转到PhotoCropActivity
                {

                    Intent intent = new Intent();
                    intent.setClass(mContext, PhotoCropActivity.class);
                    intent.putExtra(PhotoCropActivity.BITMAP, selectedPath);
                    intent.putExtra(PhotoCropActivity.OUTPUT_X, 640);
                    intent.putExtra(PhotoCropActivity.OUTPUT_Y, 640);
                    String oriPath = PathUtil.getSDPath()
                            + System.currentTimeMillis();
                    intent.putExtra(PhotoCropActivity.OUTPUT, oriPath);
                    intent.putExtra(PhotoCropActivity.RETURN_ORI_BITMAP, false);
                    intent.putExtra(PhotoCropActivity.ISCHAT_KEY, false);

                    startActivityForResult(intent, CROP_REQUEST_CODE);

                } else// 直接返回
                {
                    Intent intent = new Intent();
                    ArrayList<String> pathArray = new ArrayList<String>();
                    pathArray.add(selectedPath);
                    intent.putStringArrayListExtra(PATH_LIST, pathArray);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            } else {
                ArrayList<String> tempList = new ArrayList<String>();
                tempList.addAll(pathList);
                if (tempList.get(0).equals(ALBUM_ALL)) {
                    tempList.remove(0);
                    position--;
                }
                ViewPagerActivity.skipToViewPagerActivity(mContext, tempList,
                        position, selectedPathList, currentCanSelectMaxSize,
                        DISPLAY_REQUEST_CODE);
            }
        }

    };


    // 相册选择点击事件
    private OnClickListener selectClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            String buckName = (String) arg0.getTag(BUCK_NAME_TAG);
            int buckId = (Integer) arg0.getTag(BUCK_ID_TAG);
            if (buckName == ALBUM_ALL) {
                seletion = null;
            } else {
                seletion = MediaStore.Images.Media.BUCKET_ID + "=" + "'"
                        + buckId + "'";
            }
            hideAlbum();
            getLoaderManager().restartLoader(0, null,
                    PictureMultiSelectActivity.this);
        }
    };

    // 相册Adapter
    private class BuckNameAdapter extends BaseAdapter {

        private SparseArray<String> albumArray;
        private SparseArray<String> albumCoverArray;

        public BuckNameAdapter(SparseArray<String> albumArray, SparseArray<String> albumCoverArray) {
            this.albumArray = albumArray;
            this.albumCoverArray = albumCoverArray;
        }

        @Override
        public int getCount() {

            return albumArray == null ? 0 : albumArray.size();
        }

        @Override
        public Object getItem(int arg0) {

            return albumArray == null ? null : albumArray.valueAt(arg0);
        }

        @Override
        public long getItemId(int arg0) {

            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            ViewHolder viewHolder = null;
            if (arg1 == null) {
                arg1 = View.inflate(mContext, R.layout.mult_select_album_item,
                        null);

                TextView tvName = (TextView) arg1.findViewById(R.id.tvBuckname);
                ImageView ivImage = (ImageView) arg1.findViewById(R.id.ivAlbum);
                viewHolder = new ViewHolder(tvName, ivImage);
                arg1.setOnClickListener(selectClickListener);
                arg1.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) arg1.getTag();
            }

            viewHolder.tvName.setText(albumArray.valueAt(arg0));

            String path = PathUtil.getFILEPrefix() + albumCoverArray.valueAt(arg0);
            ImageViewUtil.getDefault().loadImage(path, viewHolder.ivImage, defRes, defRes);
            arg1.setTag(BUCK_NAME_TAG, albumArray.valueAt(arg0));
            arg1.setTag(BUCK_ID_TAG, albumArray.keyAt(arg0));
            return arg1;
        }

        class ViewHolder {

            public ViewHolder(TextView tv, ImageView iv) {
                tvName = tv;
                ivImage = iv;
            }

            private TextView tvName;
            private ImageView ivImage;
        }
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        path = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = String.valueOf(TimeFormat.getCurrentTimeMillis());
        File albumF = new File(PathUtil.getPictureDir());
        File imageF = File.createTempFile(imageFileName, PathUtil.getJPGPostfix(), albumF);
        return imageF;
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
