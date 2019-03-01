package net.iaround.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.entity.DynamicAdvertBean;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.obj.Dynamic;
import net.iaround.tools.InnerJump;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.dynamic.DynamicItemView;
import net.iaround.ui.view.dynamic.DynamicItemView.ItemDynamicClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-14 下午5:13:47
 * @Description: 动态列表的适配器
 */
public class DynamicAdapter extends BaseAdapter {

    private Context mContext;
    private List<DynamicCenterListItemBean> dynamicsList;//动态列表

    private final int BANNER_HOLDER_TAG = R.layout.view_dynamic_resource;
    private final int DYNAMIC_TAG = R.layout.item_dynamic;

    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Long, View> lmap = new HashMap<Long, View>();

    private OnClickListener mClickListener;//动态点击的事件

    private ItemDynamicClick btnClickListener;//赞点击事件

    /**
     * 动态列表的适配器-构造函数
     *
     * @param mContext
     * @param list     动态实体的列表
     * @param onClickListener 单条动态的点击事件
     */
    public DynamicAdapter(Context mContext, List<DynamicCenterListItemBean> list, OnClickListener onClickListener) {
        this.mContext = mContext;
        this.dynamicsList = list;
        mClickListener = onClickListener;
    }

    public void notifyDataChanged(List<DynamicCenterListItemBean> list){
        this.dynamicsList = list;
        notifyDataSetChanged();
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
        return 3;
    }

    public void updateSingleRow(ArrayList<DynamicCenterListItemBean> dynamic,ListView listView, long id) {
        if (listView != null) {
            this.dynamicsList = dynamic;
            for (int i = 0; i < dynamicsList.size(); i++){
                DynamicCenterListItemBean bean = getItem( i );
                if (bean.itemType == DynamicCenterListItemBean.IMAGE_TEXT){
                    if (id ==((Dynamic) bean.itemBean).dynid) {
                        DynamicItemView itemView = (DynamicItemView)lmap.get(((Dynamic) bean.itemBean).dynid);
                        if( itemView != null){
                            itemView.Build((Dynamic) bean.itemBean);
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        DynamicCenterListItemBean bean = getItem(arg0);
        if (bean.itemType == DynamicCenterListItemBean.IMAGE_TEXT ) {
            Dynamic itemBean = (Dynamic) bean.itemBean;
            DynamicHolder holder = null;
            if (lmap.get(itemBean.dynid) == null) {
                holder = new DynamicHolder();
                convertView  = DynamicItemView.initDynamicView(mContext);
                holder.itemView = (DynamicItemView)convertView;
                convertView.setTag(DYNAMIC_TAG,holder);
                lmap.put(itemBean.dynid,convertView);
                holder.itemView.Build(itemBean);
            }else{
                convertView = lmap.get(itemBean.dynid);
                holder = (DynamicHolder) convertView.getTag(DYNAMIC_TAG);
            }

            holder.itemView.setBtnClickListener(btnClickListener);
            holder.itemView.setOnClickListener(mClickListener);
        } else if (bean.itemType == DynamicCenterListItemBean.RESOURCE_BANNER) {
            ResourceBannerHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.view_dynamic_resource, null);
                holder = new ResourceBannerHolder();
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
                convertView.setTag(BANNER_HOLDER_TAG, holder);

                convertView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        DynamicAdvertBean itemBean = (DynamicAdvertBean) v.getTag();
                        if (itemBean != null && !TextUtils.isEmpty(itemBean.dest)) {
                            InnerJump.Jump(mContext, itemBean.dest);
                        }
                    }
                });
            } else {
                holder = (ResourceBannerHolder) convertView.getTag(BANNER_HOLDER_TAG);
            }
            int defRes = R.drawable.default_picture_big;
            DynamicAdvertBean itemBean = (DynamicAdvertBean) bean.itemBean;
//            ImageViewUtil.getDefault().loadRoundedImage(itemBean.pic,holder.ivPicture,defRes,defRes,null);
            GlideUtil.loadImage(BaseApplication.appContext,itemBean.pic,holder.ivPicture, defRes, defRes);
            convertView.setTag(itemBean);
        } else if (bean.itemType == DynamicCenterListItemBean.TOTOAL_NOTICE) {

//            NoticeHolder holder = null;
//            if (convertView == null) {
//                convertView = View.inflate(mContext, R.layout.z_dynamic_center_footer_view, null);
//                holder = new NoticeHolder();
//                holder.tvCount = (TextView) convertView.findView(R.id.tvCount);
//                convertView.setTag(NOTICE_HOLDER_TAG, holder);
//            } else {
//                holder = (NoticeHolder) convertView.getTag(NOTICE_HOLDER_TAG);
//            }
//
//            int count = (Integer) bean.itemBean;
//            String str = String.format(mContext.getString(R.string.dynamic_count_total_text), count);
//            holder.tvCount.setText(str);
        }

        convertView.setTag(bean.itemBean);
        return convertView;
    }




    /**
     * 设置功能按键的监听器
     *
     * @param btnClickListener
     */
    public void setFuctionBarClickListener(
            ItemDynamicClick btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    private class ResourceBannerHolder {
        ImageView ivPicture;
    }

    private class DynamicHolder {
        DynamicItemView itemView;

    }

}
