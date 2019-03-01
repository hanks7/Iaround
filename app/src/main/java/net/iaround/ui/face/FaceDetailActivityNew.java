
package net.iaround.ui.face;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.analytics.ums.DataTag;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.model.entity.Face;
import net.iaround.model.entity.FaceAd;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.AnimationController;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceLogoIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonParseUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.ZipUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.FaceMainActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.FaceCenterModel;
import net.iaround.ui.datamodel.FaceCenterModel.FaceCenterReqTypes;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.ui.view.face.TextProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class FaceDetailActivityNew extends BaseFragmentActivity implements OnTouchListener, HttpCallBack

{
    private File dir;
    private Dialog pd;
    private int mFaceId;
    private int[] mImage;
    private View childView;
    private static Face face;
    private static int index;
    private TextView mTitleName;
    private GifImageView tv_img;
    private int lastPosition = -1;
    private MyGridView MyGridView;
    private ImageView mTitleBack;
    private MainHandler mMainHandler;
    private static TextView progressTextView;
    private String descriseResult = "";
    private static RelativeLayout bottomLayout;
    private static MyGridViewAdapter GridAdapter;
    private static TextProgressBar mTextProgressBar;
    /**
     * 本次操作是否购买了表情
     */
    public static boolean isBuyFace = false;
    public PopupWindow pop;// 表情预览PopupWindow
    public static Boolean isLongClickModule = false;
    private long FACE_DESCRIBE;// 请求表情描述的flag
    private long FileLenght = 0;// 下载表情文件大小
    private final int HANDLE_DOWLOAD_PROGRESS = 100;// 更新表情下载进度条
    private final int HANDLE_DOWLOAD_FAIL = 200;// 表情下载失败
    private final int HANDLE_DOWLOAD_SUCCESS = 300;// 下载完成
    private final int UNZIPFOLDER_FAIL = 400;// 解压失败
    private final int WHAT_GET_FACE_DETAIL_FAIL = 500;// 获取数据失败
    private final int WHAT_GET_FACE_DETAIL = 600;// 获取数据
    private final int WHAT_BUY_FACE_SUCCESS = 700;// 购买成功
    private final int WHAT_BUY_FACE_FAIL = 800;// 购买失败
    private final int UPDATE_PRECENT = 900;// 在表情中心或我的表情页下载表情，刷新本页面
    //保存在详情里下载的表情，若下载中退出该页面并重新进入，将进度赋值进来
    public static ArrayList<Face> faceList = new ArrayList<Face>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detail);

        mFaceId = getIntent().getIntExtra("faceid", -1);
        if (mFaceId == -1) {
            finish();
            return;
        }
        mMainHandler = new MainHandler();
        initView();
        initData();
    }

    private void initView() {
        mTitleName = (TextView) findViewById(R.id.tv_title);
        mTitleBack = (ImageView) findViewById(R.id.iv_left);
        mTitleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }

        pd = DialogUtil.showProgressDialog(this, getString(R.string.dialog_title),
                getString(R.string.please_wait), null);
        long flag = FaceCenterModel.getInstance(this).getFaceDetailData(this, mFaceId, this);
        if (flag < 0) {
            pd.dismiss();
        }
    }


    @Override
    public void onGeneralError(int e, long flag) {
        FaceCenterReqTypes reqType = FaceCenterModel.getInstance(this).getReqType(flag);
        if (reqType == FaceCenterReqTypes.FaceDetailData) {
            mMainHandler.sendEmptyMessage(WHAT_GET_FACE_DETAIL_FAIL);
        } else if (reqType == FaceCenterReqTypes.BuyFaceData) {
            mMainHandler.sendEmptyMessage(WHAT_BUY_FACE_FAIL);
        }
//		super.onGeneralError( e , flag );
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        HashMap<String, Object> response = FaceCenterModel.getInstance(this).getRes(result,
                flag);
        if (response.isEmpty()) {
            return;
        }
        FaceCenterReqTypes reqType = (FaceCenterReqTypes) (response.get("reqType"));
        if ((Integer) (response.get("status")) != 200) {
            // 网络访问出错了
            if (reqType == FaceCenterReqTypes.FaceDetailData) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(WHAT_GET_FACE_DETAIL_FAIL,
                        result));
            } else if (reqType == FaceCenterReqTypes.BuyFaceData) {
                mMainHandler
                        .sendMessage(mMainHandler.obtainMessage(WHAT_BUY_FACE_FAIL, result));
            }
        } else {
            if (reqType == FaceCenterReqTypes.FaceDetailData) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(WHAT_GET_FACE_DETAIL,
                        response));
            } else if (reqType == FaceCenterReqTypes.BuyFaceData) {
                mMainHandler.sendMessage(mMainHandler.obtainMessage(WHAT_BUY_FACE_SUCCESS,
                        response));
            } else if (flag == FACE_DESCRIBE) {
                BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                        BaseServerBean.class);
                if (bean != null && bean.isSuccess()) {
                    descriseResult = result;
                }
            }
        }
//		super.onGeneralSuccess( result , flag );
    }


    @SuppressLint("HandlerLeak")
    private class MainHandler extends Handler {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            switch (msg.what) {
                case WHAT_GET_FACE_DETAIL://成功获取数据
                    handlerDetailData((HashMap<String, Object>) msg.obj);
                    break;

                case WHAT_GET_FACE_DETAIL_FAIL://获取数据失败
                    initNetDataFail((String) msg.obj);
                    break;

                case WHAT_BUY_FACE_SUCCESS://购买表情成功
                    buySuccess();
                    break;

                case WHAT_BUY_FACE_FAIL://购买表情失败
                    buyFail((String) msg.obj);
                    break;

                case HANDLE_DOWLOAD_PROGRESS://在本页下载表情时，刷新进度条
                    handlerProgress(msg.arg1, msg.obj);
                    break;

                case HANDLE_DOWLOAD_SUCCESS:// 安装成功
                    handlerInstalledSuccess((String) msg.obj, msg.arg1);
                    break;

                case HANDLE_DOWLOAD_FAIL:// 下載失敗
                    unzipfolderFail((String) msg.obj, 2);
                    break;

                case UNZIPFOLDER_FAIL:// 解壓失敗
                    unzipfolderFail((String) msg.obj, 1);
                    break;

                case UPDATE_PRECENT://在表情中心或我的表情页下载表情时，更新view
                    displayDownloadingWidget(face.getPercent());
                    break;
            }
        }
    }

    /**
     * 处理获取详情数据的返回
     *
     * @param map
     */
    private void handlerDetailData(HashMap<String, Object> map) {
        HashMap<String, Object> response = map;
        face = ((Face) response.get("face"));
        if (faceList != null && faceList.size() > 0) {
            for (Face temp : faceList) {
                if (temp.getFaceid() == mFaceId) {
                    face.setPercent(temp.getPercent());
                }
            }
        }
        displayToView();
        if (!FaceCenterModel.upFaceDetailPrecentMap.isEmpty()) {
            mMainHandler.postDelayed(runnable, 0);// 启动执行runnable.
        }
    }

    /**
     * 处理获取数据失败的返回
     *
     * @param result
     */
    private void initNetDataFail(String result) {
        if (!TextUtils.isEmpty(result)) {
            JSONObject json = null;
            try {
                json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int error = JsonParseUtil.getInt(json, "error", -1);
            if (error == 5951) {
                CommonFunction.toastMsg(mContext, getString(R.string.face_not_exists));
            } else {
                ErrorCode.showError(mActivity, result);
            }
        } else {
            CommonFunction.toastMsg(mContext, getString(R.string.network_req_failed));
        }
    }

    /**
     * 处理安装成功的返回
     *
     * @param filename
     */
    private void handlerInstalledSuccess(String filename, int faceId) {
        FaceMainActivity.saveDescribeToFile(filename, faceId, descriseResult);
        FaceMainActivity.delFailFile(filename);// 删除缓存文件
        resetFace();// 更改表情后缀名
        initViewWithFaceTaskStatus();
        FaceMainActivity.isDownloadFace = true;
        face.setPercent(0);
        for (Face i : faceList) {
            if (i.getFaceid() == face.getFaceid()) {
                i.setPercent(0);
            }
        }
    }


    /**
     * 处理刷新进度条
     *
     * @param faceid
     * @param mPrecent
     */
    private void handlerProgress(int faceid, Object mPrecent) {

        if (face.getFaceid() == faceid) {
            int precent = (Integer) mPrecent;
            face.setPercent(precent);
            displayDownloadingWidget(precent);
        }
    }


    private void displayToView() {
        bottomLayout = (RelativeLayout) findViewById(R.id.progress_ly);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        bottomLayout.setLayoutParams(params);
        bottomLayout.setOnClickListener(onClickListener);
        progressTextView = (TextView) findViewById(R.id.progress_text);
        mTextProgressBar = (TextProgressBar) findViewById(R.id.progressBar);
        View divide_line = findViewById(R.id.divide_line);
        View leftView = findViewById(R.id.view1);
        View rightView = findViewById(R.id.view2);
        TextView longclick_show = (TextView) findViewById(R.id.longclick_show);

        String tagName = "";
        if (!CommonFunction.isEmptyOrNullStr(face.getTagname().trim())) {
            tagName = "(" + face.getTagname() + ")";
        }
        mTitleName.setText(face.getTitle() + tagName);
        TextView faceName = (TextView) findViewById(R.id.face_name);
        faceName.setText(face.getTitle() + tagName);
        TextView priceView = (TextView) findViewById(R.id.face_price_info);
        TextView vipFlag = (TextView) findViewById(R.id.vip_flag);
        TextView animationFlag = (TextView) findViewById(R.id.animation_flag);

        if (face.getDynamic() == 1)// 动态表情
        {
            animationFlag.setText(R.string.dynamic);
            animationFlag.setVisibility(View.VISIBLE);
            longclick_show.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.VISIBLE);
            rightView.setVisibility(View.VISIBLE);
        } else {
            animationFlag.setVisibility(View.GONE);
            divide_line.setVisibility(View.VISIBLE);
        }
        if (face.getFeetype() == 2)// VIP特享
        {
            vipFlag.setVisibility(View.VISIBLE);
            priceView.setVisibility(View.GONE);
        } else if (face.getFeetype() == 1)// 免费
        {
            String freeStr = getString(R.string.face_price_neednt_gold_tip);
            String oldPriceStr = String.format(getDisplayUnitByType(face.getCurrencytype()),
                    face.getGoldNum());
            String finalText = freeStr + " " + oldPriceStr;
            SpannableString spanText = new SpannableString(finalText);
            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6600")),
                    finalText.indexOf(freeStr), freeStr.length(),
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            spanText.setSpan(new StrikethroughSpan(), finalText.indexOf(oldPriceStr),
                    spanText.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            priceView.setTextColor(Color.GRAY);
            priceView.setText(spanText);
        } else if (face.getFeetype() == 3)// 收费
        {
            String priceText = getDisplayUnitByType3(face);
            SpannableString spanText = new SpannableString(priceText);
            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6600")), 0,
                    priceText.indexOf(getString(R.string.face_vip_price)),
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            priceView.setTextColor(Color.WHITE);
            priceView.setText(spanText);
        } else if (face.getFeetype() == 4)// 参与活动获取
        {
            priceView.setTextColor(Color.parseColor("#FF6600"));
            priceView.setText(getString(R.string.get_face_by_activite));
        } else if (face.getFeetype() == 5)// 限时免费
        {
            long curTime = System.currentTimeMillis();
            if (face.getEndTime() - curTime < 0) {// 过期
                priceView.setTextColor(Color.parseColor("#FF6600"));
                priceView.setText(String.format(getDisplayUnitByType(face.getCurrencytype()),
                        face.getGoldNum()));
            } else {
                priceView.setTextColor(Color.parseColor("#FF6600"));
                priceView.setText(getString(R.string.face_limit_free));
            }
        } else if (face.getFeetype() == 6)// 打折
        {
            String priceText = String.format(getDisplayUnitByType(face.getCurrencytype()),
                    face.getGoldNum());
            String discountText = String.format(getDisplayUnitByType2(face.getCurrencytype()),
                    face.getOldgoldnum());
            String finalText = priceText + " " + discountText;
            SpannableString spanText = new SpannableString(finalText);
            spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6600")), 0,
                    finalText.indexOf(discountText), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            spanText.setSpan(new StrikethroughSpan(), priceText.length() + 1,
                    spanText.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            priceView.setTextColor(Color.WHITE);
            priceView.setText(spanText);
        }

        TextView faceView = (TextView) findViewById(R.id.face_info);
        faceView.setText(face.getDescrib());
        ImageView authorImage = (ImageView) findViewById(R.id.face_author);
//		ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView( face.getAuthorIcon( ) ,
//				authorImage , R.drawable.default_face_small , R.drawable.default_face_small , null );
        GlideUtil.loadImage(BaseApplication.appContext, face.getAuthorIcon(), authorImage, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);
        TextView authorNameView = (TextView) findViewById(R.id.author_name);
        authorNameView.setText(face.getAuthorName());
        TextView authorInfoView = (TextView) findViewById(R.id.author_info);
        authorInfoView.setText(face.getAuthorDescribe());
        ImageLoad();
        initGridview();
        initViewWithFaceTaskStatus();
    }

    /**
     * 初始化Gridview
     */
    private void initGridview() {
        mImage = new int[face.getImagenum()];
        MyGridView = (MyGridView) findViewById(R.id.gridview);
        GridAdapter = new MyGridViewAdapter();
        MyGridView.setAdapter(GridAdapter);
        if (face.getDynamic() == 1) {
            MyGridView.setOnItemLongClickListener(OnItemLongClickListener);
            MyGridView.setOnTouchListener(this);
        }
    }


    class MyGridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mImage.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(FaceDetailActivityNew.this).inflate(
                        R.layout.face_detail_gridview_item, null);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.ItemImage);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            int faceIndex = position + 1;
            String imageUri = face.getBaseurl() + face.getFaceid() + "/" + face.getFaceid()
                    + "_" + faceIndex + ".png";

//			ImageViewUtil.getDefault( ).loadImageInConvertView( imageUri , viewHolder.image ,
//					NetImageView.DEFAULT_SMALL , NetImageView.DEFAULT_SMALL );//jiqiang
//            GlideUtil.loadImage(FaceDetailActivityNew.this, imageUri, viewHolder.image, NetImageView.DEFAULT_SMALL, NetImageView.DEFAULT_SMALL);
            final ViewHolder finalViewHolder = viewHolder;
            GlideUtil.loadImage(BaseApplication.appContext,imageUri,new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    BitmapDrawable drawable = new BitmapDrawable(getResources(),
                            resource);
                    finalViewHolder.image.setBackgroundDrawable(drawable);
                }
            });
//            Glide.with(FaceDetailActivityNew.this).load(imageUri).asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    BitmapDrawable drawable = new BitmapDrawable(getResources(),
//                            resource);
//                    finalViewHolder.image.setBackgroundDrawable(drawable);
//                }
//            });

            return convertView;
        }
    }


    /**
     * 展示弹出窗口
     *
     * @param view
     * @param position
     */
    public void showPopupWindow(final View view, int position) {
        if (pop != null && pop.isShowing() || position == -1) {
            return;
        }
        lastPosition = position;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.face_detail_gridview_pop, null);
        tv_img = (GifImageView) v.findViewById(R.id.tv_img);

        loadGif(position);// 加载gif图
        int dip_100 = CommonFunction.dipToPx(mContext, 100);
        pop = new PopupWindow(v, dip_100, dip_100, false);
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.facedetail_pop_bg));
        pop.setOutsideTouchable(true);
//		pop.setAnimationStyle( R.style.AnimationFade );//淡入淡出效果
        pop.setFocusable(true);
        pop.update();
        v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int xoff = (location[0] + view.getWidth() / 2) - pop.getWidth() / 2;
        pop.showAtLocation(view, Gravity.NO_GRAVITY, xoff, location[1] - pop.getHeight());
    }

    //长按表情贴图的监听
    private AdapterView.OnItemLongClickListener OnItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showPopupWindow(view, position);
            isLongClickModule = true;
            return true;
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                if (isLongClickModule == true) {
                    int mDownInScreenX = (int) event.getX();
                    int mDownInScreenY = (int) event.getY();
                    int motionPosition = MyGridView.pointToPosition(mDownInScreenX, mDownInScreenY);
                    childView = MyGridView.getChildAt(motionPosition);
                    if (lastPosition != motionPosition) {
                        if (pop != null && pop.isShowing()) {
                            pop.dismiss();
                        }
                        if (motionPosition != -1) {
                            showPopupWindow(childView, motionPosition);
                        } else {
                            lastPosition = -1;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isLongClickModule = false;
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                }
                break;
        }
        return false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pop != null) {
            pop.dismiss();
            pop = null;
        }
    }

    /**
     * 加载表情GIF
     *
     * @param position
     */
    private void loadGif(int position) {
        int faceIndex = position + 1;
        String gifUri = face.getBaseurl() + face.getFaceid() + "/" + face.getFaceid() + "_"
                + faceIndex + ".gif";

        String imageloaderPath = CryptoUtil.generate(gifUri);
        String filePath = PathUtil.getFaceDir() + imageloaderPath;

        File gifFile = new File(filePath);
        if (gifFile.exists()) {
            try {
                GifDrawable gifDrawable = new GifDrawable(gifFile);
                tv_img.setImageDrawable(gifDrawable);
            } catch (Exception e) {
                gifFile.delete();
                tv_img.setImageResource(NetImageView.DEFAULT_SMALL);
            }
        } else {
            DownloadFileCallback callback = new DownloadFileCallback() {
                @Override
                public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded,
                                                   int flag) {
                }

                @Override
                public void onDownloadFileFinish(int flag, String fileName, String savePath) {
                    String completedFileName = "";
                    if (fileName.contains(PathUtil.getTMPPostfix())) {
                        int endIndex = fileName.lastIndexOf(PathUtil.getTMPPostfix());
                        completedFileName = (String) fileName.subSequence(0, endIndex);
                        File tmpFile = new File(savePath + fileName);
                        File completedFile = new File(savePath + completedFileName);
                        if (tmpFile.exists()) {
                            tmpFile.renameTo(completedFile);
                        }
                    }
                    handlerUIDisplay(mContext, flag, completedFileName, savePath);
                }

                @Override
                public void onDownloadFileError(int flag, String fileName, String savePath) {
                    FaceMainActivity.delFailFile(fileName + savePath);
                }
            };
            new DownLoadGifThread(mContext, gifUri, callback).start();
        }
    }

    /**
     * 下载gif图
     *
     * @author Administrator
     */
    class DownLoadGifThread extends Thread {
        private Context context;
        private DownloadFileCallback callback;
        private String fileUrl;

        public DownLoadGifThread(Context context, String url, DownloadFileCallback callback) {
            this.context = context;
            fileUrl = url;
            this.callback = callback;
        }

        @Override
        public void run() {
            String fileName = CryptoUtil.generate(fileUrl);
            String fileNameTemp = fileName + PathUtil.getTMPPostfix();
            try {
                FileDownloadManager manager = new FileDownloadManager(context, callback,
                        fileUrl, fileNameTemp, PathUtil.getFaceDir(), 0);
                manager.run();
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    /**
     * 显示表情gif
     *
     * @param context
     * @param flag
     * @param fileName
     * @param savePath
     */

    private void handlerUIDisplay(Context context, int flag, final String fileName,
                                  final String savePath) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String gifPath = savePath + fileName;
                File gifFile = new File(gifPath);
                try {
                    GifDrawable gifDrawable = new GifDrawable(gifFile);
                    tv_img.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                    gifFile.delete();
                }
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!FaceCenterModel.upFaceDetailPrecentMap.isEmpty()) {
                Iterator<Face> keys = FaceCenterModel.upFaceDetailPrecentMap.keySet().iterator();
                while (keys.hasNext()) {
                    Face key = keys.next();
                    if (key != null && mFaceId == key.getFaceid()) {
                        int value = FaceCenterModel.upFaceDetailPrecentMap.get(key);
                        face.setPercent(value);
                        sendMessage(UPDATE_PRECENT, face, mFaceId);
                        break;
                    }
                }
                resetFailList();
                mMainHandler.postDelayed(this, 1000); // 在这里实现每秒执行一次
            } else {
                resetFailList();
                mMainHandler.removeCallbacks(this);
            }
        }
    };

    /**
     * 开启线程下载表情
     */
    private void downLoad() {
        if (!ConnectorManage.getInstance(mContext).CheckNetwork(mContext))// 网络未连接
        {
            CommonFunction.toastMsg(mContext, getString(R.string.network_req_failed));
            return;
        } else {
            face.setPercent(1);
            displayDownloadingWidget(1);
            FaceCenterModel.upFacemainViewPrecentMap.put(face, 1);
            FaceCenterModel.upMyfaceViewPrecentMap.put(face, 1);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    FACE_DESCRIBE = BusinessHttpProtocol.getFaceDescribe(mContext,
                            face.getFaceid(), FaceDetailActivityNew.this);

                    /** 表情保存路径 */
                    String path = PathUtil.getFaceDir() + Common.getInstance().loginUser.getUid();
                    FileDownloadManager manager;
                    try {
                        if (faceList.contains(face) == false) {
                            faceList.add(face);
                        }
                        manager = new FileDownloadManager(mContext, callback,
                                face.getDownUrl(), String.valueOf(face.getFaceid()) + ".face",
                                path, face.getFaceid());

                        manager.run();
                    } catch (ConnectionException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 下载表情的回调函数
     */
    private DownloadFileCallback callback = new DownloadFileCallback() {
        // 下载进度条
        @Override
        public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded, int flag) {
            FileLenght = lenghtOfFile;
            double dPrecent = (double) LengthOfDownloaded / lenghtOfFile;
            int precent = (int) (dPrecent * 100);
            savePrecent(flag, precent);
            sendMessage(HANDLE_DOWLOAD_PROGRESS, precent, flag);
        }

        // 下载完成
        @Override
        public void onDownloadFileFinish(int flag, String fileName, final String savePath) {
            // 下载完成后解压该表情包
            dir = new File(savePath + fileName);
            if (dir.exists()) {
                UnZipFolder(dir, fileName, savePath, flag);
            }
        }

        // 下载失败
        @Override
        public void onDownloadFileError(int flag, String fileName, String savePath) {
            sendMessage(HANDLE_DOWLOAD_FAIL, savePath + fileName, flag);
        }
    };

    /**
     * 表情安装失败 what = 1 = 解压失败   2 =下载失败
     *
     * @param fileName
     */
    private void unzipfolderFail(String fileName, int what) {
        for (Face i : faceList) {
            if (i.getFaceid() == face.getFaceid()) {
                i.setPercent(0);
            }
        }
        face.setPercent(0);
        mTextProgressBar.setProgress(0);
        displayNoDownloadWidget();
        FaceCenterModel.upMyfaceViewFailList.add(face);
        FaceCenterModel.upFacemainViewFailList.add(face);
        FaceMainActivity.resetMap(FaceCenterModel.upFacemainViewPrecentMap, face);
        FaceMainActivity.resetMap(FaceCenterModel.upMyfaceViewPrecentMap, face);

        if (what == 1) {
            CommonFunction.toastMsg(mContext, getString(R.string.unZipfolder_fail));
        } else {
            CommonFunction.toastMsg(mContext, getString(R.string.download_fail));
        }

        FaceMainActivity.delFailFile(fileName);// 删除不完整压缩包
    }

    /**
     * 更改表情后缀名，初始化动态表情
     */
    private void resetFace() {
        String id = String.valueOf(mFaceId);
        String path = PathUtil.getFaceDir() + Common.getInstance().loginUser.getUid() + "//"
                + id;
        File file = new File(path);
        if (file.exists()) {
            CommonFunction.reFaceFileName(path);// 更改文件后缀名
        }

        FaceManager.resetOtherFace();// 初始化动态表情
        sendBroadcast(new Intent().setAction(FaceManager.FACE_INIT_ACTION));
    }

    /**
     * 如果表情下载失败，将进度置为零
     */
    private void resetFailList() {
        if (!FaceCenterModel.upFaceDetailFailList.isEmpty()) {
            Iterator<Face> it = FaceCenterModel.upFaceDetailFailList.iterator();
            while (it.hasNext()) {
                Face key = it.next();
                if (key.getFaceid() == face.getFaceid()) {
                    face.setPercent(0);
                    displayNoDownloadWidget();
                    it.remove();
                }
            }
        }
    }


    private void initViewWithFaceTaskStatus() {
        int appStatus = FaceMainActivity.checkFaceInstalled(face.getFaceid());
        if (appStatus == 1)// 已安装
        {
            displayOwnFaceWidget();
        } else if (appStatus == -1)// 未安装
        {
            if (face.getPercent() > 0) {
                displayDownloadingWidget(face.getPercent());
            } else {
                displayNoDownloadWidget();
            }
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int appStatus = FaceMainActivity.checkFaceInstalled(face.getFaceid());
            if (appStatus == 1)// 已安装
            {
                skipToSendFace();
            } else if (appStatus == -1) {
                if (face.getOwn() == 0) {// 未拥有
//					addBtnEvent( DataTag.BTN_find_face_item_get);//jiqiang
                    handleBuyCondition();
                } else {
                    buySuccess();
                }
            }
        }
    };

    private void buyFace() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }

        pd = DialogUtil.showProgressDialog(this, getString(R.string.dialog_title),
                getString(R.string.please_wait), null);
        long flag = FaceCenterModel.getInstance(this).buyFace(this, face.getFaceid(), this);
        if (flag < 0) {
            pd.dismiss();
        }
    }

    private void buySuccess() {
        isBuyFace = true;
        FaceMainActivity.buyFace.add(face);
        FaceLogoIcon faceLogoIcon = null;
        for (FaceLogoIcon logoIcon : FaceManager.mOwnGifFaces) {
            if (logoIcon.pkgId == face.getFaceid()) {
                faceLogoIcon = logoIcon;
                break;
            }
        }
        if (faceLogoIcon != null) {
            // 移除掉原来过期的表情
            FaceManager.mOwnGifFaces.remove(faceLogoIcon);
        }

        FaceManager.mOwnGifFaces.add(new FaceLogoIcon(face.getFaceid()));
        downLoad();
    }

    /**
     * 购买规则，如Vip,限时免费等
     */
    protected void handleBuyCondition() {
        long curTime = System.currentTimeMillis();
        if (face.getFeetype() == 2)// VIP免费
        {
            if (!Common.getInstance().loginUser.isVip()
                    && !Common.getInstance().loginUser.isSVip()) {
                DialogUtil.showTobeVipDialog(mContext, R.string.vip_face,
                        R.string.tost_face_vip_privilege);
                return;
            }
        } else if (face.getFeetype() == 3 || face.getFeetype() == 6
                || (face.getFeetype() == 5 && face.getEndTime() < curTime)) {// ** 5.6 NEW **购买类型为打折或者限时免费，价格没有是否VIP之分
            int money = 0;
            if (face.getFeetype() == 6
                    || (face.getFeetype() == 5 && face.getEndTime() < curTime)) {
                money = face.getGoldNum();
            } else {
                if (!Common.getInstance().loginUser.isVip()
                        && !Common.getInstance().loginUser.isSVip()) {
                    money = face.getGoldNum();
                } else {
                    money = face.getVipgoldnum();
                }
            }
            String buyInfo = "";
            if (face.getCurrencytype() == 2) {
                buyInfo = String.format(getString(R.string.face_pay_price_diamond_msg), ""+money);
            } else {
                buyInfo = String.format(getString(R.string.face_pay_price_msg), ""+money);
            }
            DialogUtil.showTwoButtonDialog(FaceDetailActivityNew.this,
                    getString(R.string.store_get_gift_tip), buyInfo,
                    getString(R.string.cancel), getString(R.string.ok), null,
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buyFace();
                        }
                    });
            return;
        }

        buyFace();
    }

    private void buyFail(String result) {
        if (!TextUtils.isEmpty(result)) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(result);
                int status = obj.optInt("status");
                int error = obj.optInt("error");
                if (status == -400) {
                    error = 4000;
                }

                if (error == 4000) {
                    if (face.getCurrencytype() == 1) {
                        DialogUtil.showTwoButtonDialog(FaceDetailActivityNew.this,
                                ErrorCode.getErrorMessageId(ErrorCode.E_4000),
                                getString(R.string.face_diamond_not_enough),//yuchao
                                getString(R.string.cancel),
                                getString(R.string.diamond_for_gold_ok), null,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext);  //yuchao  取消金币兑换功能
//										i.putExtra( EventBuffer.TAG_NAME , DataTag.VIEW_find_faceCenter_faceDetail );
                                    }

                                });
                    } else if (face.getCurrencytype() == 2) {
                        DialogUtil.showDiamondDialog(mActivity, DataTag.VIEW_find_faceCenter_faceDetail);
                    }
                    return;
                } else if (error == 5954)// 未参加活动
                {
                    DialogUtil.showTwoButtonDialog(FaceDetailActivityNew.this,
                            getString(R.string.dialog_title),
                            getString(R.string.face_not_participate_active),
                            getString(R.string.ok),
                            getString(R.string.face_participate_active), null,
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SkipWebView();
                                }
                            });
                    return;
                } else if (error == 5930) {
                    DialogUtil.showDiamondDialog(mActivity);
                    return;
                } else if (error == 5953) {
                    CommonFunction.log("", getString(R.string.face_vip_can_used));
                    return;
                } else {
                    ErrorCode.showError(mActivity, result);
                }
            } catch (JSONException e) {
            }
        } else {
            Toast.makeText(FaceDetailActivityNew.this, R.string.start_reconnect, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void SkipWebView() {
        try {

            if (face != null) {
                switch (face.getOpenType()) {

                    case FaceAd.TYPE_WEBVIEW_URL:
                        Uri uri = Uri.parse(face.getActiveurl());
                        Intent i = new Intent(mContext, WebViewAvtivity.class);
                        i.putExtra(WebViewAvtivity.WEBVIEW_TITLE, face.getHeadcontent());
                        i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
                        startActivity(i);
                        break;
                    case FaceAd.TYPE_URL:
                        Uri uri2 = Uri.parse(face.getActiveurl());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri2);
                        startActivity(intent);
                        break;                    case FaceAd.TYPE_FACE:
                        Uri uri3 = Uri.parse(face.getActiveurl());
                        Intent i1 = new Intent(mContext, WebViewAvtivity.class);
                        i1.putExtra(WebViewAvtivity.WEBVIEW_TITLE, face.getHeadcontent());
                        i1.putExtra(WebViewAvtivity.WEBVIEW_URL, uri3.toString());
                        startActivity(i1);
                        break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(FaceDetailActivityNew.this, R.string.game_center_ad_game_link_error,
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void savePrecent(int flag, int precent) {

        for (Face temp : faceList) {
            if (temp.getFaceid() == flag) {
                temp.setPercent(precent);
                FaceCenterModel.upFacemainViewPrecentMap.put(temp, precent);
                FaceCenterModel.upMyfaceViewPrecentMap.put(temp, precent);
            }
        }
    }


    /**
     * 解压表情文件包
     *
     * @param faceid
     * @param file
     * @param savePath
     */
    private void UnZipFolder(File file, String fileName, String savePath, int faceid) {
        if (file.length() == FileLenght) {
            try {
                ZipUtil.UnZipFolder(dir.getAbsolutePath(), savePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendMessage(HANDLE_DOWLOAD_SUCCESS, savePath + fileName, faceid);
        } else {
            sendMessage(UNZIPFOLDER_FAIL, savePath + fileName, faceid);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainHandler.removeCallbacks(runnable); // 停止
    }

    private void sendMessage(int what, Object obj, int faceid) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = faceid;
        msg.obj = obj;
        mMainHandler.sendMessage(msg);
    }

    private void displayOwnFaceWidget() {
        mTextProgressBar.setVisibility(View.INVISIBLE);
        progressTextView.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.face_detail_has);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        progressTextView.setCompoundDrawables(drawable, null, null, null);
        progressTextView.setText("  " + getString(R.string.sendFace_toFriends));
        bottomLayout.setClickable(true);
    }

    private void displayNoDownloadWidget() {
        mTextProgressBar.setVisibility(View.INVISIBLE);
        progressTextView.setVisibility(View.VISIBLE);
        if (face.getOwn() == 1) {// 未下载
            progressTextView
                    .setText("  " + getString(R.string.face_detail_not_download));
        } else {// 获取表情
            progressTextView
                    .setText("  " + getString(R.string.face_detail_task_download));
        }
        bottomLayout.setClickable(true);
        Drawable drawable = getResources().getDrawable(R.drawable.face_detail_shopmark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        progressTextView.setCompoundDrawables(drawable, null, null, null);
    }

    private void displayDownloadingWidget(int percent) {
        progressTextView.setVisibility(View.INVISIBLE);
        mTextProgressBar.setVisibility(View.VISIBLE);
        mTextProgressBar.setTextSize(13 * getResources().getDisplayMetrics().density);
        mTextProgressBar.setTextColor(getResources().getColor(R.color.c_cccccc));
        mTextProgressBar.setImage(null);
        mTextProgressBar.setMax(100);
        bottomLayout.setClickable(false);
        if (face.getPercent() == 100) {
            int appStatus = FaceMainActivity.checkFaceInstalled(face.getFaceid());
            if (appStatus == 1)// 已安装
            {
                displayOwnFaceWidget();
            } else if (appStatus == -1)// 未安装
            {
                displayNoDownloadWidget();
            }
            face.setPercent(0);
            for (Face i : faceList) {
                if (i.getFaceid() == face.getFaceid()) {
                    i.setPercent(0);
                }
            }
            FaceMainActivity.resetMap(FaceCenterModel.upFaceDetailPrecentMap, face);
        } else {
            mTextProgressBar.setProgress(percent);
        }
    }

    /**
     * 跳转到发送表情给好友的页面
     */
    private void skipToSendFace() {
        setFaceIndex(face);
        Intent i = new Intent(mContext, SendFaceToFriends.class);
        startActivity(i);
        finish();
    }

    /**
     * 跳转到表情详情页面
     */
    public static void launch(Activity activity, int faceId) {
        Intent i = new Intent(activity, FaceDetailActivityNew.class);
        String str = String.valueOf(faceId);
        if (str.contains(".0")) {
            str = str.replaceAll("\\.0", "");
            faceId = Integer.parseInt(str);
        }
        i.putExtra("faceid", faceId);
        activity.startActivity(i);
    }

    /**
     * 跳转到表情详情页面
     */
    public static void launchForResult(Activity activity, int faceId) {
        Intent i = new Intent(activity, FaceDetailActivityNew.class);
        i.putExtra("faceid", faceId);
        activity.startActivityForResult(i, 1);
    }

    /**
     * @param currencyType
     * @return
     * @Title: getDisplayUnitByType
     * @Description: 获取金币/钻石显示内容1
     */
    private String getDisplayUnitByType(int currencyType) {
        if (currencyType == 1) {// 金币商品
            return getString(R.string.face_price_neednt_gold_2);
        } else if (currencyType == 2) {// 钻石商品
            return getString(R.string.face_price_neednt_diamond);
        }
        return "";
    }

    /**
     * @param currencyType
     * @return
     * @Title: getDisplayUnitByType2
     * @Description: 获取金币/钻石显示内容2
     */
    private String getDisplayUnitByType2(int currencyType) {
        if (currencyType == 1) {// 金币商品
            return getString(R.string.face_price_discounts);
        } else if (currencyType == 2) {// 钻石商品
            return getString(R.string.face_price_diamond);
        }
        return "";
    }

    /**
     * @return
     * @Title: getDisplayUnitByType3
     * @Description: 获取金币/钻石显示内容3
     */
    private String getDisplayUnitByType3(Face face) {
        if (face.getCurrencytype() == 1) {// 金币商品
            return String.format(getString(R.string.face_price), ""+face.getGoldNum(),
                    ""+face.getVipgoldnum());
        } else if (face.getCurrencytype() == 2) {
            return String.format(getString(R.string.face_price_diamond_two_price),
                    ""+face.getGoldNum(), ""+face.getVipgoldnum());
        }
        return "";
    }

    @SuppressWarnings("static-access")
    private void setFaceIndex(Face face) {
        int index = 0;
        for (FaceLogoIcon Logo : FaceManager.mFaceGroupLogos) {
            if (face.getFaceid() == Logo.pkgId) {
                index = FaceManager.mFaceGroupLogos.indexOf(Logo);
            }
        }

        if (index != -1) {
            this.index = index;
        } else {
            index = 1;
            this.index = index;
        }
    }

    public static int getFaceIndex() {
        return index;
    }

    private void ImageLoad() {

        GlideUtil.loadImageCrop(BaseApplication.appContext,face.getBackground(),new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (loadedImage != null && !loadedImage.isRecycled()) {
                            View iconLy = findViewById(R.id.icon_ly);
                            AnimationController.fadeIn(iconLy, 400, 0);

                            BitmapDrawable drawable = new BitmapDrawable(getResources(),
                                    loadedImage);
                            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                            drawable.setDither(true);
                            iconLy.setBackgroundDrawable(drawable);
                        }

                        final ImageView icon = (ImageView) findViewById(R.id.icon_img);
                        icon.setVisibility(View.GONE);
                        GlideUtil.loadImageCrop(BaseApplication.appContext,face.getImage(),new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (resource != null && !resource.isRecycled()) {
                                            icon.setImageBitmap(null);
                                            Bitmap decomPression = CommonFunction
                                                    .decomBackGroundPression(BaseApplication.appContext, resource);
                                            icon.setImageBitmap(decomPression);
                                            icon.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
//                        Glide.with(FaceDetailActivityNew.this).load(face.getImage()).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                if (resource != null && !resource.isRecycled()) {
//                                    ((ImageView) icon).setImageBitmap(null);
//                                    Bitmap decomPression = CommonFunction
//                                            .decomBackGroundPression(mContext, resource);
//                                    ((ImageView) icon).setImageBitmap(decomPression);
//                                    icon.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        });
                    }
                });


//        Glide.with(this).load(face.getBackground()).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> glideAnimation) {
//                if (loadedImage != null && !loadedImage.isRecycled()) {
//                    View iconLy = findViewById(R.id.icon_ly);
//                    AnimationController.fadeIn(iconLy, 400, 0);
//
//                    BitmapDrawable drawable = new BitmapDrawable(getResources(),
//                            loadedImage);
//                    drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//                    drawable.setDither(true);
//                    iconLy.setBackgroundDrawable(drawable);
//                }
//
//                final ImageView icon = (ImageView) findViewById(R.id.icon_img);
//                icon.setVisibility(View.GONE);
//                Glide.with(FaceDetailActivityNew.this).load(face.getImage()).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        if (resource != null && !resource.isRecycled()) {
//                            ((ImageView) icon).setImageBitmap(null);
//                            Bitmap decomPression = CommonFunction
//                                    .decomBackGroundPression(mContext, resource);
//                            ((ImageView) icon).setImageBitmap(decomPression);
//                            icon.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
//            }
//        });

    }

    static class ViewHolder {
        ImageView image;
    }

    /**
     * @param urlpath
     * @return Bitmap
     * 根据url获取布局背景的对象
     */
    public static BitmapDrawable getDrawable(String urlpath) {
        BitmapDrawable d = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            d = (BitmapDrawable) BitmapDrawable.createFromStream(in, "background.jpg");
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }
}

