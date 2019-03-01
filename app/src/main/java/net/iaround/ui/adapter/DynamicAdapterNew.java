package net.iaround.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.BuildConfig;
import net.iaround.R;
import net.iaround.annotation.IAroundAD;
import net.iaround.conf.Common;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.InnerJump;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.ResourceListBean;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.thirdadvert.AdLink;
import net.iaround.ui.dynamic.view.DynamicItemViewNew;
import java.util.ArrayList;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-14 下午5:13:47
 * @Description: 动态列表的适配器
 */
public class DynamicAdapterNew extends BaseAdapter {

    private Context mContext;
    private ArrayList<DynamicCenterListItemBean> dynamicsList;//动态列表
    private int showPlaceType;//展示的位置。@DynamicItemView

    private View.OnClickListener mClickListener;//动态点击的事件

    private View.OnClickListener mGreetBtnClickListener;//赞点击事件
    private View.OnClickListener mCommentBtnClickListener;//评论点击事件

    private final int BANNER_HOLDER_TAG = R.layout.dynamic_center_resource_banner_view;//
    private final int THRID_AD_HOLDER_TAG = R.layout.x_dynamic_gdt_list_item;//广告布局
    private final int NOTICE_HOLDER_TAG = R.layout.z_dynamic_center_footer_view;

    private Object nativeADDataRef; //com.qq.e.ads.nativ.NativeADDataRef

    private boolean isChange = true;
    private DynamicItemViewNew.ItemDynamicClick btnClickListener;//赞点击事件

    /*
    banner 广告数据
     */
    private ArrayList<AdLink> bannerAdData = new ArrayList<AdLink>();// 广告

    /**
     * 动态列表的适配器-构造函数
     *
     * @param mContext
     * @param list     动态实体的列表
     * @param type     显示的位置类型 @DynamicItemView
     * @param listener 单条动态的点击事件
     */
    public DynamicAdapterNew(Context mContext, ArrayList<DynamicCenterListItemBean> list, int type, OnClickListener listener) {
        this.mContext = mContext;
        dynamicsList = list;
        showPlaceType = type;
        mClickListener = listener;

        initThirdAd();
    }

    @IAroundAD
    private void initThirdAd(){
        if(BuildConfig.showAdvert==true) {
            nativeADDataRef = net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().getNextAdData();
        }
    }

    @Override
    public int getCount() {
        return dynamicsList != null ? dynamicsList.size() : 0;
    }

    @Override
    public DynamicCenterListItemBean getItem(int arg0) {
        return dynamicsList != null ? dynamicsList.get(arg0) : null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getItemViewType(int position) {
        return dynamicsList != null ? dynamicsList.get(position).itemType : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }


    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        DynamicCenterListItemBean bean = getItem(arg0);

        if (bean.itemType == 5) {
            //Inmobi
            if(BuildConfig.showAdvert==true) {
                convertView = handleInmobiItemView(convertView);
            }
        } else if (bean.itemType == DynamicCenterListItemBean.IMAGE_TEXT) {
            DynamicItemViewNew itemView;
            DynamicItemBean itemBean = (DynamicItemBean) bean.itemBean;
            if (convertView == null) {
                convertView = DynamicItemViewNew.initDynamicView(mContext);
                itemView = (DynamicItemViewNew) convertView;
                itemView.setBtnClickListener(btnClickListener);
                itemView.setOnClickListener(mClickListener);
            } else {
                itemView = (DynamicItemViewNew) convertView;
            }
            itemView.Build(itemBean, DynamicItemViewNew.DYNAMIC_CENTER);

        } else if (bean.itemType == 4) {
            //广点通
            if(BuildConfig.showAdvert==true) {
                convertView = handleGDTItemView(convertView);
            }
        } else if (bean.itemType == DynamicCenterListItemBean.RESOURCE_BANNER) {
            ResourceBannerHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.dynamic_center_resource_banner_view, null);
                holder = new ResourceBannerHolder();
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
                convertView.setTag(BANNER_HOLDER_TAG, holder);

                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ResourceListBean.ResourceItemBean itemBean = (ResourceListBean.ResourceItemBean) v.getTag();
                        if (itemBean != null && itemBean.banner != null && !TextUtils.isEmpty(itemBean.banner.getLink())) {
                            InnerJump.Jump(mContext, itemBean.banner.getLink());
                            itemBean.banner.getRequest(true);
                        }
                    }
                });
            } else {
                holder = (ResourceBannerHolder) convertView.getTag(BANNER_HOLDER_TAG);
            }
            ResourceListBean.ResourceItemBean itemBean = (ResourceListBean.ResourceItemBean) bean.itemBean;
            GlideUtil.loadImage(BaseApplication.appContext,itemBean.banner.getImageUrl(),holder.ivPicture);
            convertView.setTag(itemBean);
        } else if (bean.itemType == DynamicCenterListItemBean.TOTOAL_NOTICE) {
            NoticeHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.z_dynamic_center_footer_view, null);
                holder = new NoticeHolder();
                holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);
                convertView.setTag(NOTICE_HOLDER_TAG, holder);
            } else {
                holder = (NoticeHolder) convertView.getTag(NOTICE_HOLDER_TAG);
            }

            int count = (Integer) bean.itemBean;
            String str = String.format(mContext.getString(R.string.dynamic_count_total_text), count);
            holder.tvCount.setText(str);
        }
        if (convertView != null){
            convertView.setTag(bean.itemBean);
        }
        return convertView;
    }

    @IAroundAD
    private View handleInmobiItemView(View convertView){
        if(BuildConfig.showAdvert==true) {
            if (convertView == null) {
                convertView = new net.iaround.ui.dynamic.view.DynamicThridGdtAdView(mContext, Common.inmobiLoad);
            } else {
                ((net.iaround.ui.dynamic.view.DynamicThridGdtAdView) convertView).notifyRefreshView(Common.inmobiLoad);
            }

            if (Common.inmobiLoad == 2) {
                Common.inmobiLoad = 1;
            } else if (Common.inmobiLoad == 1) {
                Common.inmobiLoad = 0;
            }
        }
        return convertView;
    }

    @IAroundAD
    private View handleGDTItemView(View convertView){
        if(BuildConfig.showAdvert==true) {
            ThridHolder holder;
            if (convertView == null) {
                convertView = new net.iaround.ui.dynamic.view.DynamicThridGdtAdView(mContext, (com.qq.e.ads.nativ.NativeADDataRef) nativeADDataRef);
                holder = new ThridHolder();
                holder.thridGdtAdView = convertView;
                convertView.setTag(THRID_AD_HOLDER_TAG, holder);
            } else {
                holder = (ThridHolder) convertView.getTag(THRID_AD_HOLDER_TAG);
            }
            ((net.iaround.ui.dynamic.view.DynamicThridGdtAdView)holder.thridGdtAdView).refreshView((com.qq.e.ads.nativ.NativeADDataRef) nativeADDataRef);
        }
        return convertView;
    }

    /**
     * 设置功能按键的监听器
     *
     * @param greetListener   赞
     * @param commentListener 评论
     */
    public void setFuctionBarClickListener(
            View.OnClickListener greetListener,
            View.OnClickListener commentListener) {
        mGreetBtnClickListener = greetListener;
        mCommentBtnClickListener = commentListener;
    }

    private class ResourceBannerHolder {
        ImageView ivPicture;
    }

    private class NoticeHolder {
        TextView tvCount;
    }

    private class ThridHolder {
        Object thridGdtAdView;
    }

    @Override
    public void notifyDataSetChanged() {
        CommonFunction.log("DataStatistics", "动态 notifyDataSetChanged");
        super.notifyDataSetChanged();
    }

    /**
     * 设置功能按键的监听器
     *
     * @param btnClickListener
     */
    public void setFuctionBarClickListener(DynamicItemViewNew.ItemDynamicClick btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    /**
     * 是否修改广告数据
     *
     * @param isChange
     */
    public void setChange(boolean isChange) {
        this.isChange = isChange;
    }

    public void initAdBannerData() {
        bannerAdData.clear();
        //添加动态空判断
        if (dynamicsList != null && dynamicsList.size() > 0) {
            for (int i = 0; i < dynamicsList.size(); i++) {
                if (dynamicsList.get(i).itemType == DynamicCenterListItemBean.RESOURCE_BANNER) {
                    AdLink ad = new AdLink();
                    ad.position = i;
                    ad.isShowing = false;
                    if (dynamicsList.get(i).itemBean != null) {
                        ResourceListBean.ResourceItemBean resourceItemBean = (ResourceListBean.ResourceItemBean) dynamicsList.get(i).itemBean;
                        if (resourceItemBean.banner == null) {
                            continue;
                        }
                        ad.ADid = resourceItemBean.banner.id;
                        ad.banner = resourceItemBean.banner;
                        ad.banner.setThirtReport(mContext);
                    }
                    bannerAdData.add(ad);
                }
            }
        }
        initThridAdData();
    }

    @IAroundAD
    private void initThridAdData(){
        if(BuildConfig.showAdvert==true) {
            if (isChange) {
                nativeADDataRef = net.iaround.ui.dynamic.thirdadvert.ThridAdServiceCenter.getInstance().getNextAdData();
            }
        }
    }

    public void setBannerAdData() {
        CommonFunction.log("DataStatistics", "设置isShowing=false");
        for (int i = 0; i < bannerAdData.size(); i++) {
            bannerAdData.get(i).isShowing = false;
        }//广告
    }

    /*
    广告展示的时候上报统计数据
     */
    public void uploadAaData(Context context, int firstItem, int totleItem) {
        if (bannerAdData != null && bannerAdData.size() > 0) {
            for (int i = 0; i < bannerAdData.size(); i++) {
                int index = bannerAdData.get(i).position;
                if (firstItem <= index && index < totleItem + firstItem) {
                    if (!bannerAdData.get(i).isShowing) {
                        CommonFunction.log("DataStatistics", "index:" + index + ":ADid :" + bannerAdData.get(i).ADid);
                        bannerAdData.get(i).banner.getRequest(false);
                    }
                    bannerAdData.get(i).isShowing = true;
                } else {
                    bannerAdData.get(i).isShowing = false;
                }

            }
        }
    }

    public void setFreshData(ArrayList<DynamicCenterListItemBean> dynamicsList) {
        this.dynamicsList = dynamicsList;
        notifyDataSetChanged();
    }
}
