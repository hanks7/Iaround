package net.iaround.ui.dynamic.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.listeners.NativeAdEventListener;
import com.qq.e.ads.nativ.NativeADDataRef;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.presenter.SplashPresenter;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.SplashActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.dynamic.inmobi.InmobiDynamic;
import net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter;
import net.iaround.utils.ImageViewUtil;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


/**
 * 动态列表中的第三方广告卡片(目前是广点通)
 * <p/>
 * Created by fireant on 16/8/3.
 */
public class DynamicThridGdtAdView extends RelativeLayout {

    private final String TAG = "GDT_LOG";

    private ImageView imgLogo;
    private TextView textName;
    private TextView textDesc;
    private ImageView imgPoster;
    private TextView btnDownLoad;
    private LinearLayout lyPoster;

    private Context mContext;

    public DynamicThridGdtAdView(Context context, NativeADDataRef adItem) {
        super(context);
        this.mContext = context;
        initView();
        refreshView(adItem);
    }

    public DynamicThridGdtAdView(Context context, int inmobi) {
        super(context);
        this.mContext = context;
        initView();
        initData(inmobi);
    }

    /**
     * 更新InMobi广告
     */
    public void notifyRefreshView(int inmobi) {
        initData(inmobi);
    }


    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.x_dynamic_gdt_list_item, this);

        imgLogo = (ImageView) itemView.findViewById(R.id.img_logo);
        textName = (TextView) itemView.findViewById(R.id.text_name);
        textDesc = (TextView) itemView.findViewById(R.id.text_desc);
        imgPoster = (ImageView) itemView.findViewById(R.id.img_poster);
        btnDownLoad = (TextView) itemView.findViewById(R.id.btn_download);
        lyPoster = (LinearLayout) itemView.findViewById(R.id.ly_poster);
    }

    public void refreshView(NativeADDataRef nativeADDataRef) {
        CommonFunction.log(TAG, "刷新广告详情显示");
        if (lyPoster!=null && nativeADDataRef != null) {
            lyPoster.setVisibility(GONE);
            final NativeADDataRef adItem = nativeADDataRef;

            int defalutImage = R.drawable.default_avatar_round;
            lyPoster.setVisibility(GONE);
            imgPoster.setVisibility(VISIBLE);
            GlideUtil.loadCircleImage(BaseApplication.appContext, adItem.getIconUrl(), imgLogo, defalutImage, defalutImage);

            int defResGig = R.drawable.thrid_ad_pic;

            GlideUtil.loadImage(BaseApplication.appContext, adItem.getImgUrl(), imgPoster, defResGig, defResGig);
            textName.setText(adItem.getTitle());
            textDesc.setText(adItem.getDesc());

            adItem.onExposured(this);

            if (adItem.isAPP()) {
                btnDownLoad.setVisibility(View.VISIBLE);
                this.setOnClickListener(null);
                btnDownLoad.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adItem.onClicked(v);
                    }
                });
            } else {
                btnDownLoad.setVisibility(View.GONE);
                btnDownLoad.setOnClickListener(null);
                this.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adItem.onClicked(v);
                    }
                });
            }
        }
    }

    public void refreshView(final InMobiNative inmobiDynamic) {
        lyPoster.setVisibility(VISIBLE);
        imgPoster.setVisibility(GONE);

        int defalutImage = R.drawable.default_avatar_round;
        GlideUtil.loadCircleImage(BaseApplication.appContext, inmobiDynamic.getAdIconUrl(), imgLogo, defalutImage, defalutImage);
        textName.setText(inmobiDynamic.getAdTitle());
        textDesc.setText(inmobiDynamic.getAdDescription());
        lyPoster.removeAllViews();
        View view = inmobiDynamic.getPrimaryViewOfWidth(mContext,lyPoster, lyPoster, lyPoster.getWidth());
        lyPoster.addView(view);
        if (inmobiDynamic.isAppDownload()) {
            btnDownLoad.setVisibility(VISIBLE);
            this.setOnClickListener(null);
            btnDownLoad.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    inmobiDynamic.reportAdClickAndOpenLandingPage();
                }
            });
        } else {
            btnDownLoad.setVisibility(GONE);
            btnDownLoad.setOnClickListener(null);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    inmobiDynamic.reportAdClickAndOpenLandingPage();
                }
            });
        }

    }

    private void initData(int inmobi){
        if (inmobi != 0) {
            InMobiNative inMobiNative = new InMobiNative(mContext, Config.INMOBI_DYNAMIC_PLACEMENTID, new NativeAdEventListener() {
                @Override
                public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                    super.onAdLoadSucceeded(inMobiNative);
                    CommonFunction.log("Other", "Dynamic onAdLoadSucceeded");
                    refreshView(inMobiNative);
                }

                @Override
                public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    super.onAdLoadFailed(inMobiNative, inMobiAdRequestStatus);
                    CommonFunction.log("Other", "Dynamic onAdLoadFailed");
                    //YC
//                    NativeADDataRef nativeADDataRef = ThridAdServiceCenter.getInstance().getNextAdData();
//                    refreshView(nativeADDataRef);
                    DynamicThridGdtAdView.this.removeAllViews();
                }

                @Override
                public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
                    super.onAdFullScreenDismissed(inMobiNative);
                }

                @Override
                public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {
                    super.onAdFullScreenWillDisplay(inMobiNative);
                }

                @Override
                public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
                    super.onAdFullScreenDisplayed(inMobiNative);
                }

                @Override
                public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
                    super.onUserWillLeaveApplication(inMobiNative);
                }

                @Override
                public void onAdImpressed(InMobiNative inMobiNative) {
                    super.onAdImpressed(inMobiNative);
                }

                @Override
                public void onAdClicked(InMobiNative inMobiNative) {
                    super.onAdClicked(inMobiNative);
                }

                @Override
                public void onAdStatusChanged(InMobiNative inMobiNative) {
                    super.onAdStatusChanged(inMobiNative);
                }

                @Override
                public void onRequestPayloadCreated(byte[] bytes) {
                    super.onRequestPayloadCreated(bytes);
                }

                @Override
                public void onRequestPayloadCreationFailed(InMobiAdRequestStatus inMobiAdRequestStatus) {
                    super.onRequestPayloadCreationFailed(inMobiAdRequestStatus);
                }
            });
//            InMobiNative inMobiNativeDynamic = new InMobiNative(mContext, Config.INMOBI_DYNAMIC_PLACEMENTID, new InMobiNative.NativeAdListener() {
//                @Override
//                public void onAdLoadSucceeded(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onAdLoadSucceeded");
//                    refreshView(inMobiNative);
//                }
//
//                @Override
//                public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
//                    CommonFunction.log("Other", "Dynamic onAdLoadFailed");
//                    //YC
//                    NativeADDataRef nativeADDataRef = ThridAdServiceCenter.getInstance().getNextAdData();
//                    refreshView(nativeADDataRef);
//                }
//
//                @Override
//                public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onAdFullScreenDismissed");
//                }
//
//                @Override
//                public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onAdFullScreenDisplayed");
//                }
//
//                @Override
//                public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onUserWillLeaveApplication");
//                }
//
//                @Override
//                public void onAdImpressed(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onAdImpressed");
//                }
//
//                @Override
//                public void onAdClicked(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onAdClicked");
//                }
//
//                @Override
//                public void onMediaPlaybackComplete(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "Dynamic onMediaPlaybackComplete");
//                }
//            });
            Map<String, String> map = new HashMap<>();
            inMobiNative.setExtras(map);
            inMobiNative.load();
        }
    }

}
