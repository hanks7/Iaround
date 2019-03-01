package net.iaround.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import net.iaround.R;
import net.iaround.model.entity.DynamicCenterListItemBean;
import net.iaround.model.entity.DynamicReviewItem;
import net.iaround.model.obj.Dynamic;
import net.iaround.model.obj.DynamicDetails;
import net.iaround.model.obj.DynamicHeader;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.view.dynamic.CommentItemView;
import net.iaround.ui.view.dynamic.DynamicDetailsHeadView;
import net.iaround.ui.view.dynamic.DynamicDetailsHeadView.ItemDynamicClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class: 动态详情适配器
 * Author：gh
 * Date: 2017/1/3 22:14
 * Email：jt_gaohang@163.com
 */
public class DynamicDetailsAdapter extends BaseAdapter {

    private Context mContext;
    private List<DynamicDetails> dynamicsList;//动态列表
    private long dynamicId;

//    //定义hashMap 用来存放之前创建的每一项item
//    HashMap<Long, View> lmap = new HashMap<Long, View>();

    private final int DYNAMIC_COMMENT = R.layout.comment_item_view;
    private final int DYNAMIC_TAG = R.layout.item_dynamic;
    private final int TEXT_LOVE = R.layout.view_dynamic_resource;

    private OnClickListener commentContentClickListener;//动态点击的事件
    private OnLongClickListener commentLongClickListener;//评论长按

    private ItemDynamicClick btnClickListener;//赞点击事件

    /**
     * 动态列表的适配器-构造函数
     *
     * @param mContext
     * @param list     动态实体的列表
     */
    public DynamicDetailsAdapter(Context mContext, List<DynamicDetails> list) {
        this.mContext = mContext;
        dynamicsList = list;
    }

    public void updateData(List<DynamicDetails> list) {
        this.dynamicsList = list;
        notifyDataSetChanged();
    }

    public void setCommentContentClickListener(OnClickListener clickListener) {
        this.commentContentClickListener = clickListener;
    }

    public void setCommentLongClickListener(View.OnLongClickListener clickListener) {
        this.commentLongClickListener = clickListener;
    }

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    @Override
    public int getCount() {
        return dynamicsList != null ? dynamicsList.size() : 0;
    }

    @Override
    public DynamicDetails getItem(int arg0) {
        return dynamicsList != null ? dynamicsList.get(arg0) : null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getItemViewType(int position) {
        if (dynamicsList.get(position).itemType == DynamicDetails.IMAGE_TEXT) {
            return DynamicDetails.IMAGE_TEXT;
        } else if (dynamicsList.get(position).itemType == DynamicDetails.TEXT_COMMENT) {
            return DynamicDetails.TEXT_COMMENT;
        } else {
            return DynamicDetails.TEXT_LOVE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

//    public void updateSingleRow(ArrayList<DynamicDetails> dynamic, ListView listView, long id) {
//        if (listView != null) {
//            this.dynamicsList = dynamic;
//            for (int i = 0; i < dynamicsList.size(); i++) {
//                DynamicDetails bean = getItem(i);
//                if (bean.itemType == DynamicCenterListItemBean.IMAGE_TEXT) {
//
//                    if (id == ((DynamicHeader) bean.itemBean).dynamic.dynid) {
//                        DynamicDetailsHeadView itemView = (DynamicDetailsHeadView) lmap.get(((Dynamic) bean.itemBean).dynid);
//                        if (itemView != null) {
//                            itemView.Build((DynamicItemBean) bean.itemBean);
//                            itemView.setDetails(true);
//                        }
//                        return;
//                    }
//                }
//            }
//        }
//    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        DynamicDetails bean = getItem(arg0);
        if (bean.itemType == DynamicDetails.IMAGE_TEXT) {
            DynamicHolder holder = null;
            DynamicItemBean itemBean = (DynamicItemBean) bean.itemBean;
//            if (convertView == null) {
                holder = new DynamicHolder();
                convertView = DynamicDetailsHeadView.initDynamicView(mContext);
                holder.itemView = (DynamicDetailsHeadView) convertView;
//                lmap.put(itemBean.getDynamicInfo().dynamicid, convertView);//   lmap.put(itemBean.dynami,convertView);
//                convertView.setTag(DYNAMIC_TAG, holder);
//            } else {
////                convertView = lmap.get(itemBean.getDynamicInfo().dynamicid);
//                holder = (DynamicHolder) convertView.getTag(DYNAMIC_TAG);
//            }
            holder.itemView.setDetails(true);
            holder.itemView.Build(itemBean);
            holder.itemView.setBtnClickListener(btnClickListener);
            holder.itemView.setOnClickListener(null);
            convertView.setTag(bean.itemBean);
        } else if (bean.itemType == DynamicDetails.TEXT_COMMENT) {
            DynamicCommentHolder holder = null;
            if (convertView == null) {
                holder = new DynamicCommentHolder();
                convertView = new CommentItemView(mContext, commentContentClickListener,
                        commentLongClickListener);
                holder.itemView = (CommentItemView) convertView;
                convertView.setTag(DYNAMIC_COMMENT, holder);
            } else {
                holder = (DynamicCommentHolder) convertView.getTag(DYNAMIC_COMMENT);
            }
//            DynamicReplyBean reviews = (DynamicReplyBean) bean.itemBean;
            DynamicReviewItem reviews = (DynamicReviewItem) bean.itemBean;
            if (reviews != null){
                holder.itemView.init(reviews);
                convertView.setTag(arg0);
            }
        }
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

    private class DynamicCommentHolder {
        CommentItemView itemView;
    }

    private class DynamicHolder {
        DynamicDetailsHeadView itemView;

    }

}
