package cn.finalteam.galleryfinal;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.finalteam.galleryfinal.adapter.PhotoPreviewAdapter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.galleryfinal.widget.GFViewPager;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2015/12/29 0029 14:43
 */
public class PhotoPreviewActivity extends PhotoBaseActivity implements ViewPager.OnPageChangeListener{

    static final String PHOTO_LIST = "photo_list";
    static final String PHOTO_INDEX = "photo_index";
    static final String IS_PREVIEW = "is_preview";

    private RelativeLayout mTitleBar;
    private ImageView mIvBack;
    private TextView mTvIndicator;
    private TextView mTvChooseCount;

    private ImageView mIvCheck;
    private LinearLayout mSelectPreview;

    private GFViewPager mVpPager;
    private ArrayList<PhotoInfo> mPhotoList = new ArrayList<>();
    private PhotoPreviewAdapter mPhotoPreviewAdapter;

    private ThemeConfig mThemeConfig;

    private int currentIndex; // 当前索引

    private boolean isPreview; // 是否只是预览
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeConfig = GalleryFinal.getGalleryTheme();

        if ( mThemeConfig == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.gf_activity_photo_preview);
            findViews();
            setListener();
            setTheme();
            isPreview = getIntent().getBooleanExtra(IS_PREVIEW,false);
            ArrayList<PhotoInfo> list ;
            if (isPreview){
                mSelectPreview.setVisibility(View.GONE);
                mTvChooseCount.setVisibility(View.GONE);
                list = (ArrayList<PhotoInfo>) getIntent().getSerializableExtra(PHOTO_LIST);
            }else{
                mSelectPreview.setVisibility(View.VISIBLE);
                mTvChooseCount.setVisibility(View.VISIBLE);
                list = GalleryUtils.getInstance().getmCurrent();
            }

            currentIndex = getIntent().getIntExtra(PHOTO_INDEX,0);

            if (false == Utils.disableTakePhoto() && GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofImage()){
                mPhotoList.clear();
                for (PhotoInfo photoInfo : list){
                    if (photoInfo.getPhotoId() != -1){
                        mPhotoList.add(photoInfo);
                    }else{
                        currentIndex --;
                    }
                }
            }

            // 初始化预览状态
            if (mPhotoList.size() > 0){
                PhotoInfo photoInfo = mPhotoList.get(0);
                if (photoInfo.isSelect){
                    mIvCheck.setBackgroundColor(getResources().getColor(R.color.ok_pressed));
                }else {
                    mIvCheck.setBackgroundColor(getResources().getColor(R.color.select_normal));
                }
            }

            mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, mPhotoList);
            mVpPager.setAdapter(mPhotoPreviewAdapter);

            mVpPager.setCurrentItem(currentIndex);

            mTvChooseCount.setText(getString(R.string.selected, getSelectCount(), GalleryFinal.getFunctionConfig().getMaxSize()));

            mSelectPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPhotoList != null){
                        for (PhotoInfo photoInfo : mPhotoList){
                            if (photoInfo.getPhotoId() == mPhotoList.get(currentIndex).getPhotoId()){
                                if (getPicSize(photoInfo.getPhotoPath())[0] < 350 | getPicSize(photoInfo.getPhotoPath())[1] < 350){
                                    toast(getString(R.string.photo_size_error1));
                                    return;
                                }
                                if (GalleryFinal.getFunctionConfig().isMutiSelect() && getSelectCount() == GalleryFinal.getFunctionConfig().getMaxSize()) {
                                    toast(getString(R.string.select_max_tips));
                                    return;
                                } else {
                                    if (photoInfo.isSelect){
                                        mIvCheck.setBackgroundColor(getResources().getColor(R.color.select_normal));
                                        photoInfo.isSelect = false;
                                    }else {
                                        mIvCheck.setBackgroundColor(getResources().getColor(R.color.ok_pressed));
                                        photoInfo.isSelect = true;
                                    }
                                }

                            }
                        }
                        mTvChooseCount.setText(getString(R.string.selected, getSelectCount(), GalleryFinal.getFunctionConfig().getMaxSize()));
                    }
                }
            });
        }
    }

    private void findViews() {
        mSelectPreview = (LinearLayout) findViewById(R.id.rl_preview_select_item);
        mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvCheck = (ImageView) findViewById(R.id.preview_select_item_check);
        mTvIndicator = (TextView) findViewById(R.id.tv_indicator);
        mTvChooseCount = (TextView) findViewById(R.id.tv_choose_count);

        mVpPager = (GFViewPager) findViewById(R.id.vp_pager);
    }

    private void setListener() {
        mVpPager.addOnPageChangeListener(this);
        mIvBack.setOnClickListener(mBackListener);
    }

    private void setTheme() {
        if(mThemeConfig.getPreviewBg() != null) {
            mVpPager.setBackgroundDrawable(mThemeConfig.getPreviewBg());
        }
    }

    @Override
    protected void takeResult(PhotoInfo info) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTvIndicator.setText((position + 1) + "/" + mPhotoList.size());
    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        if (mPhotoList != null){
            PhotoInfo photoInfo = mPhotoList.get(position);
            if (photoInfo.isSelect){
                mIvCheck.setBackgroundColor(getResources().getColor(R.color.ok_pressed));
            }else {
                mIvCheck.setBackgroundColor(getResources().getColor(R.color.select_normal));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goBack();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            {
                goBack();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回选择相册
     */
    private void goBack(){
        if (false == Utils.disableTakePhoto() && GalleryFinal.getFunctionConfig().getMimeType() == PictureMimeType.ofImage()){
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setPhotoId(-1);
            photoInfo.setPhotoPath("");
            mPhotoList.add(0, photoInfo);
        }
        GalleryUtils.getInstance().setmCurrent(mPhotoList);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 选中的总数
     * @return
     */
    private int getSelectCount(){
        int size = 0;
        for (PhotoInfo photoInfo : mPhotoList){
            if (photoInfo.isSelect){
                size++;
            }
        }
        return size;
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
