package net.iaround.ui.view.near;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 附近的人的第三方广告卡片(目前是广点通)
 * <p/>
 * Created by fireant on 16/8/3.
 */
public class NearThridGdtAdView extends RelativeLayout {

    private final String TAG = "GDT_LOG";

    private View itemView;
    private ImageView imgLogo;
    private TextView textName;
    private TextView textDesc;
    private TextView btnDownLoad;
    private RelativeLayout lyBanner;
    private Context mContext;

    private ArrayList<InMobiNative> mInMobiNatives = new ArrayList<>();

    /*填充 GDT 数据
     * */
    public NearThridGdtAdView(Context context, NativeADDataRef adItem) {
        super(context);
        this.mContext = context;
        initView();
        refreshView(adItem);
    }

    /**
     * 填充Inmobi数据
     *
     * @param context
     */
    public NearThridGdtAdView(Context context, View convertView, ViewGroup parent, int inmobi) {
        super(context);
        this.mContext = context;

        initData(inmobi, convertView, parent);

    }


    /**
     * 更新InMobi广告
     */
    public void notifyRefershView(ViewGroup parent, View convertView, int inmobi) {
        initData(inmobi, convertView, parent);
    }

    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.x_near_gdt_list_item, this);

        imgLogo = (ImageView) itemView.findViewById(R.id.img_logo);
        textName = (TextView) itemView.findViewById(R.id.text_name);
        textDesc = (TextView) itemView.findViewById(R.id.text_desc);
        btnDownLoad = (TextView) itemView.findViewById(R.id.btn_download);
        lyBanner = (RelativeLayout) itemView.findViewById(R.id.ly_inmobi_ban);
    }

    public void refreshView(NativeADDataRef nativeADDataRef) {
        CommonFunction.log(TAG, "刷新广告详情显示");
        if (lyBanner != null) {
            lyBanner.setVisibility(GONE);
            if (nativeADDataRef != null) {


                final NativeADDataRef adItem = nativeADDataRef;

                int defalutImage = R.drawable.default_avatar_round;

//            ImageViewUtil.getDefault().loadRoundFrameImageInConvertView(adItem.getIconUrl(), imgLogo,
//                    defalutImage, defalutImage, null, 0, "#00000000");
                GlideUtil.loadCircleImage(BaseApplication.appContext, adItem.getIconUrl(), imgLogo, defalutImage, defalutImage);
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
    }

    public void refreshView(final InMobiNative inMobiNative, View convertView, final ViewGroup parent) {
        lyBanner.removeAllViews();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inMobiNative.reportAdClickAndOpenLandingPage();
            }
        });

        GlideUtil.loadCircleImage(BaseApplication.appContext, inMobiNative.getAdIconUrl(), imgLogo);
        textName.setText(inMobiNative.getAdTitle());
        textDesc.setText(inMobiNative.getAdDescription());
        lyBanner.setVisibility(VISIBLE);
        lyBanner.addView(inMobiNative.getPrimaryViewOfWidth(mContext, convertView, parent, parent.getWidth()));
        if (inMobiNative.isAppDownload()) {

            btnDownLoad.setVisibility(VISIBLE);
            this.setOnClickListener(null);
            btnDownLoad.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    inMobiNative.reportAdClickAndOpenLandingPage();
                }
            });
        } else {
            btnDownLoad.setVisibility(GONE);
            btnDownLoad.setOnClickListener(null);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    inMobiNative.reportAdClickAndOpenLandingPage();

                }
            });
        }

    }

    private void initData(int inmobi, final View convertView, final ViewGroup parent) {
        if (inmobi != 0) {
            clearAds();
            InMobiNative inMobiNativeNear = new InMobiNative(mContext, Config.INMOBI_NEARBY_PLACEMENTID, new NativeAdEventListener() {
                @Override
                public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                    super.onAdLoadSucceeded(inMobiNative);
                    CommonFunction.log("Other", "onAdLoadSucceeded");
                    initView();
                    refreshView(inMobiNative, convertView, parent);
                }

                @Override
                public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    super.onAdLoadFailed(inMobiNative, inMobiAdRequestStatus);
                    CommonFunction.log("Other", "onAdLoadFailed: " + inMobiAdRequestStatus.getMessage());
                    //YC
                    NearThridGdtAdView.this.removeAllViews();
                }

                @Override
                public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
                    super.onAdFullScreenDismissed(inMobiNative);
                    CommonFunction.log("Other", "onAdFullScreenDismissed");

                }

                @Override
                public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {
                    super.onAdFullScreenWillDisplay(inMobiNative);
                    CommonFunction.log("Other", "onAdFullScreenWillDisplay");

                }

                @Override
                public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
                    super.onAdFullScreenDisplayed(inMobiNative);
                    CommonFunction.log("Other", "onAdFullScreenDisplayed");

                }

                @Override
                public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
                    super.onUserWillLeaveApplication(inMobiNative);
                    CommonFunction.log("Other", "onUserWillLeaveApplication");

                }

                @Override
                public void onAdImpressed(InMobiNative inMobiNative) {
                    super.onAdImpressed(inMobiNative);
                    CommonFunction.log("Other", "onAdImpressed");

                }

                @Override
                public void onAdClicked(InMobiNative inMobiNative) {
                    super.onAdClicked(inMobiNative);
                    CommonFunction.log("Other", "onAdClicked");
                }

                @Override
                public void onAdStatusChanged(InMobiNative inMobiNative) {
                    super.onAdStatusChanged(inMobiNative);
                    CommonFunction.log("Other", "onAdStatusChanged");

                }

                @Override
                public void onRequestPayloadCreated(byte[] bytes) {
                    super.onRequestPayloadCreated(bytes);
                    CommonFunction.log("Other", "onRequestPayloadCreated");

                }

                @Override
                public void onRequestPayloadCreationFailed(InMobiAdRequestStatus inMobiAdRequestStatus) {
                    super.onRequestPayloadCreationFailed(inMobiAdRequestStatus);
                    CommonFunction.log("Other", "onRequestPayloadCreationFailed");

                }
            });
//            InMobiNative inMobiNativeNea = new InMobiNative(mContext, Config.INMOBI_NEARBY_PLACEMENTID, new InMobiNative.NativeAdListener() {
//                @Override
//                public void onAdLoadSucceeded(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onAdLoadSucceeded");
//                    refreshView(inMobiNative, convertView,parent);
//                }
//
//                @Override
//                public void onAdLoadFailed(@NonNull InMobiNative inMobiNative, @NonNull InMobiAdRequestStatus inMobiAdRequestStatus) {
//                    CommonFunction.log("Other", "onAdLoadFailed");
//                    //YC
//                    NativeADDataRef nativeADDataRef = ThridAdServiceCenter.getInstance().getNextAdData();
//                    refreshView(nativeADDataRef);
//                }
//
//                @Override
//                public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onAdFullScreenDismissed");
//                }
//
//                @Override
//                public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onAdFullScreenDismissed");
//                }
//
//                @Override
//                public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onUserWillLeaveApplication");
//                }
//
//                @Override
//                public void onAdImpressed(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onAdImpressed");
//                }
//
//                @Override
//                public void onAdClicked(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onAdClicked");
//                }
//
//                @Override
//                public void onMediaPlaybackComplete(@NonNull InMobiNative inMobiNative) {
//                    CommonFunction.log("Other", "onMediaPlaybackComplete");
//                }
//            });
            mInMobiNatives.add(inMobiNativeNear);
            Map<String, String> map = new HashMap<>();
            inMobiNativeNear.setExtras(map);
            inMobiNativeNear.load();
        }
    }

    public void clearAds() {
        for (InMobiNative inMobiNative : mInMobiNatives) {
            inMobiNative.destroy();
        }
        mInMobiNatives.clear();
    }

}
