package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.DataTag;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * Class: 预览
 * Author：gh
 * Date: 2016/12/12 20:14
 * Email：jt_gaohang@163.com
 */
public class PreviewActivity extends ActionBarActivity implements View.OnClickListener {

    private ListView lvPreview;
    private PreviewAdapter adapter;
    private View bootomView;

    private ArrayList<String> mSmallPhotoIds; // 图片ID列表

    private final int MAX_IMAGE_COUNT = 8;// 最多可以添加多少张图片
    private final int UPDATE_IMAGE_LAYOUT_FALG = 0XFF;
    private final int REQUEST_CODE_GALLERY = 1001;

    private int mPhotoIndex;
    private String dataTag;

    private Dialog loadDataProgressDialog;// 加载栏

    private TextView tvTitle;
    private ImageView ivLeft;
    private FrameLayout flLeft;

    private PublishHandler mHandler = new PublishHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);


//        setActionBarTitle(R.string.picture_preview_title);
        lvPreview = (ListView) findViewById(R.id.lv_preview);

        mPhotoIndex = getIntent().getIntExtra("position", 0);
        dataTag = getIntent().getStringExtra("dataTag");

        if (getIntent().hasExtra("smallPhotos")) {
            mSmallPhotoIds = getIntent().getStringArrayListExtra("smallPhotos");
        }

        if (adapter == null) {
            adapter = new PreviewAdapter(this);
            lvPreview.setAdapter(adapter);
        }

        if (dataTag.equals(DataTag.DYNAMIC_PUBLISH_IMAGE)) {
            bootomView = getLayoutInflater().inflate(R.layout.view_preview_bottom, null);
            lvPreview.addFooterView(bootomView);
            bootomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> mBeautifyPhotoIds = new ArrayList<>();
                    for (String str : mSmallPhotoIds){
                        mBeautifyPhotoIds.add(str.replace(PathUtil.getFILEPrefix(),""));
                    }
                    GalleryUtils.getInstance().openGalleryMuti(PreviewActivity.this,REQUEST_CODE_GALLERY,MAX_IMAGE_COUNT,mBeautifyPhotoIds,mOnHanlderResultCallback);
                }
            });
        }
        initAdapter();
        initActionbar();
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                showProgressDialog();
                final List<String> list = new ArrayList<>();
                for (PhotoInfo photoInfo : resultList){
                    list.add(photoInfo.getPhotoPath());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mSmallPhotoIds.clear();
                        for (int i = 0; i < list.size(); i++) {
                            rotaingImage(list.get(i));
                        }
                        mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FALG);
                    }
                }).start();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(PreviewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private void initActionbar() {
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);

        tvTitle.setText(R.string.picture_preview_title);
        ivLeft.setImageResource(R.drawable.title_back);

        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setReultFinsh();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setReultFinsh();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void setReultFinsh() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("dynamic_list", mSmallPhotoIds);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                setReultFinsh();
                break;
        }
    }

    public class PreviewAdapter extends BaseAdapter {
        private LayoutInflater mInflater;// 动态布局映射

        public PreviewAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mSmallPhotoIds.size() > 0 && mSmallPhotoIds != null ? mSmallPhotoIds.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.view_image, null);//根据布局文件实例化view
            }
            final ImageView img = (ImageView) convertView
                    .findViewById(R.id.iv_gallery);
            TextView delete = (TextView) convertView.findViewById(R.id.tv_delete);

            img.setBackgroundColor(getResources().getColor(R.color.common_white));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 24);
            img.setLayoutParams(lp);

            if (dataTag.equals(DataTag.DYNAMIC_PUBLISH_IMAGE)) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }

            final String url = mSmallPhotoIds.get(position);

            String picUrl;
            if (PicSize(url)){
                String[] paths = url.split("_");
                if (url.contains(".png")){
                    picUrl = paths[0]+"_"+paths[1]+".png";
                }else{
                    picUrl = paths[0]+"_"+paths[1]+".jpg";
                }

            }else{
                if (url.contains(".png")){
                    picUrl = url.replace("_s.png",".png");
                }else{
                    picUrl = url.replace("_s.jpg",".jpg");
                }

            }
//            GlideUtil.loadImage(mContext, picUrl, new GlideDrawableImageViewTarget(img) {
//                @Override
//                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                    super.onResourceReady(drawable, anim);
//                    //在这里添加一些图片加载完成的操作
//                    img.setBackgroundColor(getResources().getColor(R.color.common_white));
//                }
//            });
            GlideUtil.loadImage(BaseApplication.appContext,picUrl,new GlideDrawableImageViewTarget(img) {
                        @Override
                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                            //在这里添加一些图片加载完成的操作
                            img.setBackgroundColor(getResources().getColor(R.color.common_white));
                        }
                    });
//            Glide.with(mContext).load(url).centerCrop().into(new GlideDrawableImageViewTarget(img) {
//                @Override
//                public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                    super.onResourceReady(drawable, anim);
//                    //在这里添加一些图片加载完成的操作
//                    img.setBackgroundColor(getResources().getColor(R.color.common_white));
//                }
//            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSmallPhotoIds.remove(position);
                    notifyDataSetChanged();
                    if (0 == mSmallPhotoIds.size()) {
                        setReultFinsh();
                    }
                }
            });

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> mBeautifyPhotoIds = new ArrayList<>();
                    for (String str : mSmallPhotoIds){
                        mBeautifyPhotoIds.add(str.replace(PathUtil.getFILEPrefix(),""));
                    }
                    PictureDetailsActivity.launchStore(PreviewActivity.this,mBeautifyPhotoIds,position);
                }
            });

            return convertView;
        }

    }


    public static void launch(Context context, ArrayList<String> iconUrl, int position, String dataTag) {
        Intent i = new Intent(context, PreviewActivity.class);
        i.putExtra("position", position);
        i.putExtra("dataTag", dataTag);
        if (iconUrl != null && iconUrl.size() > 0) {
            i.putExtra("smallPhotos", iconUrl);
        }
        context.startActivity(i);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (RESULT_OK == resultCode) {
//            if (CommonFunction.TAKE_PHOTO_REQ == requestCode) {
//
//                if (data != null) {
//                    final String path = data
//                            .getStringExtra(PictureMultiSelectActivity.FILE_PATH);
//
//                    showProgressDialog();
//                    new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            rotaingImage(path);
//                            mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FALG);
//                        }
//                    }).start();
//                }
//
//            } else if (CommonFunction.PICK_PHOTO_REQ == requestCode) {
//                final ArrayList<String> list = data
//                        .getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);
//
//                showProgressDialog();
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        for (int i = 0; i < list.size(); i++) {
//                            rotaingImage(list.get(i));
//                        }
//                        mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FALG);
//                    }
//                }).start();
//            }
//
//        }
//    }

    // 处理图片旋转的问题
    private void rotaingImage(String path) {
        String outputPath = PathUtil.getImageRotatePath(path);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            int degree = PhotoCropActivity.readPictureDegree(path);
            if (degree == 0) {
                outputPath = path;

            } else {
                File outputFile = new File(outputPath);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    bitmap = PhotoCropActivity.rotaingImageView(degree, bitmap);
                    FileOutputStream os = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                }
            }
            //create一个小图
            File thumbFile = new File(CommonFunction.thumPicture(outputPath));
            Bitmap thumbBitmap = CommonFunction.centerSquareScaleBitmap(bitmap, 136);
            FileOutputStream thumbOs = new FileOutputStream(thumbFile);
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, thumbOs);
            thumbOs.flush();
            thumbOs.close();

        } catch (Exception e) {
            e.printStackTrace();
            outputPath = path;
        }
        String url = outputPath.contains(PathUtil.getFILEPrefix()) ? outputPath : PathUtil.getFILEPrefix() + outputPath;
        mSmallPhotoIds.add(url);
    }

    // 显示加载框
    private void showProgressDialog() {
        if (loadDataProgressDialog == null) {
            loadDataProgressDialog = DialogUtil.showProgressDialog(mContext,
                    R.string.dialog_title, R.string.content_is_loading, null);
            loadDataProgressDialog.setCancelable(false);
        }

        loadDataProgressDialog.show();
    }

    private class PublishHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_IMAGE_LAYOUT_FALG:
                    hideProgressDialog();
                    initAdapter();
                    break;
            }
        }
    }

    private void initAdapter() {
        lvPreview.postDelayed(new Runnable() {
            @Override
            public void run() {
                lvPreview.setSelection(mPhotoIndex);
            }
        }, 100);

        adapter.notifyDataSetChanged();
        if (mSmallPhotoIds.size() >= MAX_IMAGE_COUNT) {
            lvPreview.removeFooterView(bootomView);
        }

    }

    // 隐藏加载框
    private void hideProgressDialog() {
        if (loadDataProgressDialog != null) {
            loadDataProgressDialog.hide();
        }

    }

    /**
     * 判断图片长宽比
     * @param path
     * @return
     */
    private boolean PicSize(String path) {
        if (path.contains("storage/")) {
            return false;
        }
        String[] paths = path.split("_");
        String picAddres = paths[paths.length -1];
        //            String picAddres = paths[2];
//            String[] size = picAddres.split("x");
//            if (Integer.valueOf(size[1]) / Integer.valueOf(size[0]) >= 1) {
//                return true;
//            } else {
//                return false;
//            }
        return !picAddres.contains("/");
    }

}
