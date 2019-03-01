package net.iaround.ui.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.ReviewsListServerBean.ReviewsItem;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.DynamicDetailActivity;
import net.iaround.ui.dynamic.bean.DynamicMessagesItemBean;
import net.iaround.ui.fragment.DynamicCenterFragment;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.slide.TipsView;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-17 下午8:19:58
 * @Description: 动态消息列表适配器
 */
public class DynamicMessagesAdapter extends BaseAdapter {
    private int clickTag = R.layout.dynamic_message_item;// 点击tag
    private Context mContext;
    public ArrayList<DynamicMessagesItemBean> dataList;
    private final int defIconRes = R.drawable.default_avatar_round_light;
    private final int defImageRes = R.drawable.default_pitcure_small;

    private View.OnClickListener greeterViewClickListener = null;// 点赞的View的点击事件
    private View.OnClickListener moreViewClickListener = null;// 更多的View的点击事件

    public DynamicMessagesAdapter(Context context,
                                  ArrayList<DynamicMessagesItemBean> list,
                                  View.OnClickListener greeterViewClickListener,
                                  View.OnClickListener moreViewClickListener) {
        mContext = context;
        dataList = list;
        this.greeterViewClickListener = greeterViewClickListener;
        this.moreViewClickListener = moreViewClickListener;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public DynamicMessagesItemBean getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DynamicMessagesItemBean data = getItem(position);
        if (convertView == null) {
            if (data.getItemType() == DynamicMessagesItemBean.CONTENT_TYPE) {
                convertView = View.inflate(mContext,
                        R.layout.dynamic_message_item_online, null);
                ReviewHolder reviewHolder = new ReviewHolder();
                reviewHolder.friendIcon = (HeadPhotoView) convertView
                        .findViewById(R.id.friend_icon);
                reviewHolder.nickName = (TextView) convertView
                        .findViewById(R.id.tvName);
                reviewHolder.rlSexAge = (RelativeLayout) convertView.findViewById(R.id.rlAgeSex);
                reviewHolder.ivSex = (ImageView) convertView.findViewById(R.id.ivSex);
                reviewHolder.age = (TextView) convertView.findViewById(R.id.tvAgeSex);
                reviewHolder.time = (TextView) convertView.findViewById(R.id.tvTime);
                reviewHolder.content = (TextView) convertView
                        .findViewById(R.id.tvContent);
                reviewHolder.dynamicIcon = (ImageView) convertView
                        .findViewById(R.id.dynamic_icon);
                reviewHolder.dynamicContent = (TextView) convertView
                        .findViewById(R.id.dynamic_content);
                reviewHolder.ivThumbs = (ImageView) convertView.findViewById(R.id.ivThumbs);

                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ReviewsItem itemBean = (ReviewsItem) arg0
                                .getTag(clickTag);
                        long dynamicId = itemBean.dynamic.dynamicid;
                        long userid = itemBean.msg.user.userid;
                        String userName = itemBean.msg.user.nickname;

//                        DynamicDetailActivity.skipToDynamicDetailAndReply(
//                                mContext, dynamicId, true, userid, userName);//jiqiang 详情还未做

                        Intent intent = new Intent();
                        intent.setClass(mContext, DynamicDetailActivity.class);
                        intent.putExtra(DynamicDetailActivity.mDynamicIdKey, ""+dynamicId);
                        intent.putExtra(DynamicDetailActivity.mUserIdKey, ""+userid);
                        mContext.startActivity(intent);
                    }
                });

                convertView.setTag(reviewHolder);
            } else if (data.getItemType() == DynamicMessagesItemBean.HEAD_TYPE) {

                convertView = View.inflate(mContext,
                        R.layout.l_dynamic_msg_list_greet_title, null);
                GreetHolder greetHolder = new GreetHolder();

                greetHolder.ivGreeter1 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter1);
                greetHolder.ivGreeter2 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter2);
                greetHolder.ivGreeter3 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter3);
                greetHolder.ivGreeter4 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter4);
                greetHolder.ivGreeter5 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter5);
                greetHolder.ivGreeter6 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter6);
                greetHolder.ivGreeter7 = (HeadPhotoView) convertView
                        .findViewById(R.id.ivGreeter7);
                greetHolder.addImageViewItem();

                convertView.setOnClickListener(greeterViewClickListener);
                convertView.setTag(greetHolder);
            } else if (data.getItemType() == DynamicMessagesItemBean.FOOTER_TYPE) {

                MoreHolder moreHolder = new MoreHolder();

                convertView = View.inflate(mContext,
                        R.layout.z_dynamic_center_footer_view, null);
                int padding = 32;
                convertView.setPadding(0, padding, 0, padding);
                moreHolder.tvCount = (TextView) convertView
                        .findViewById(R.id.tvCount);
                convertView.setOnClickListener(moreViewClickListener);
                convertView.setTag(moreHolder);
            }
        }

        if (data.getItemType() == DynamicMessagesItemBean.CONTENT_TYPE) {
            ReviewHolder reviewHolder = (ReviewHolder) convertView.getTag();
            ReviewsItem reviewItem = data.getReviewItem();

            // 初始化
            reviewHolder.nickName.setText("");
            reviewHolder.age.setText("");
            reviewHolder.time.setText("");
            reviewHolder.content.setText("");
            reviewHolder.content.setBackgroundResource(R.drawable.trans_white);
            reviewHolder.dynamicContent.setText("");

            // 头像

            reviewHolder.friendIcon.execute(ChatFromType.UNKONW, reviewItem.msg.user.convertBaseToUser(), null);

            reviewHolder.friendIcon.setTag(reviewItem.msg.user.convertBaseToUser());

            // 昵称
            reviewHolder.nickName.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(reviewHolder.nickName, mContext,
                            reviewItem.msg.user.nickname, 18));

            // 年龄
            reviewHolder.age.setText(reviewItem.msg.user.age + "");
            // 性别
            if (reviewItem.msg.user.gender!=null && reviewItem.msg.user.gender.equals("m"))
            {
                reviewHolder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
                reviewHolder.ivSex.setImageResource(R.drawable.thread_register_man_select);
            }else {
                reviewHolder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                reviewHolder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
            }
            // 时间
            String time = TimeFormat.timeFormat4(mContext,
                    reviewItem.msg.datetime);
            if (!TextUtils.isEmpty(time)) {
                reviewHolder.time.setText(time);
            } else {
                reviewHolder.time.setText(R.string.unable_to_get_time);
            }

            // 正文
            if (reviewItem.msg.type == 1) {
                reviewHolder.content.setVisibility(View.VISIBLE);
                reviewHolder.ivThumbs.setVisibility(View.GONE);
                reviewHolder.content.setText(FaceManager.getInstance(mContext)
                        .parseIconForString(reviewHolder.content, mContext,
                                reviewItem.msg.content, 13));
            }else
            {
                reviewHolder.content.setVisibility(View.GONE);
                reviewHolder.ivThumbs.setVisibility(View.VISIBLE);
                reviewHolder.ivThumbs
                        .setBackgroundResource(R.drawable.dynamic_like_normal);
            }


            if (reviewItem.dynamic != null) {
                String imageUrl = reviewItem.dynamic.image;
                if (TextUtils.isEmpty(imageUrl)) {
                    // 动态正文
                    reviewHolder.dynamicContent.setText(FaceManager.getInstance(
                            mContext).parseIconForString(reviewHolder.dynamicContent,
                            mContext, reviewItem.dynamic.content, 18));
                    reviewHolder.dynamicContent.setVisibility(View.VISIBLE);
                    reviewHolder.dynamicIcon.setVisibility(View.GONE);
                } else {
                    // 动态头像
//                    ImageViewUtil.getDefault().fadeInLoadImageInConvertView(
//                            imageUrl, reviewHolder.dynamicIcon, defImageRes,
//                            defImageRes);//jiqiang
//                    GlideUtil.loadCircleImage(mContext,imageUrl,reviewHolder.dynamicIcon,defImageRes,defImageRes);
                    GlideUtil.loadImage(BaseApplication.appContext,imageUrl,reviewHolder.dynamicIcon,defImageRes,defImageRes);
                    reviewHolder.dynamicIcon.setVisibility(View.VISIBLE);
                    reviewHolder.dynamicContent.setVisibility(View.GONE);
                }

                convertView.setEnabled(true);
                convertView.setTag(clickTag, reviewItem);
            } else {
                reviewHolder.dynamicContent.setText(R.string.dynamic_deleted_or_gone);
                reviewHolder.dynamicIcon.setVisibility(View.GONE);
                reviewHolder.dynamicContent.setVisibility(View.VISIBLE);

                convertView.setEnabled(false);
            }
        } else if (data.getItemType() == DynamicMessagesItemBean.HEAD_TYPE) {

            GreetHolder greetHolder = (GreetHolder) convertView.getTag();
            ArrayList<HeadPhotoView> greeterList = greetHolder.ivGreeters;
            ArrayList<ReviewsItem> mUnreadData = data.getGreeterList();

            int count = greeterList.size();
            for (int i = 0; i < count; i++) {
                HeadPhotoView iv = greeterList.get(i);
                if (i < mUnreadData.size()) {
                    iv.executeGreet(ChatFromType.UNKONW, mUnreadData.get(i).msg.user.convertBaseToUser(), null);
                } else {
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        } else if (data.getItemType() == DynamicMessagesItemBean.FOOTER_TYPE) {
            MoreHolder moreHolder = (MoreHolder) convertView.getTag();
            boolean isHasMore = data.isHasMore();

            String content = "";
            if (isHasMore) {
                content = mContext.getResources().getString(
                        R.string.hot_more_look);
                convertView.setEnabled(true);
            } else {
                String formatStr = mContext.getResources().getString(
                        R.string.dynamic_messages_count_total_text);
                content = String.format(formatStr, data.getItemCount());
                convertView.setEnabled(false);
            }
            moreHolder.tvCount.setText(content);
        }

        return convertView;
    }

    public class ReviewHolder {
        public HeadPhotoView friendIcon;
        public TextView nickName;
        public RelativeLayout rlSexAge;
        public ImageView ivSex;
        public TextView age;
        public TextView time;
        public TextView content;
        public ImageView dynamicIcon;
        public TextView dynamicContent;
        public ImageView ivThumbs;
    }

    public class GreetHolder {

        public HeadPhotoView ivGreeter1;
        public HeadPhotoView ivGreeter2;
        public HeadPhotoView ivGreeter3;
        public HeadPhotoView ivGreeter4;
        public HeadPhotoView ivGreeter5;
        public HeadPhotoView ivGreeter6;
        public HeadPhotoView ivGreeter7;

        public ArrayList<HeadPhotoView> ivGreeters = new ArrayList<HeadPhotoView>();

        public void addImageViewItem() {
            ivGreeters.add(ivGreeter1);
            ivGreeters.add(ivGreeter2);
            ivGreeters.add(ivGreeter3);
            ivGreeters.add(ivGreeter4);
            ivGreeters.add(ivGreeter5);
            ivGreeters.add(ivGreeter6);
            ivGreeters.add(ivGreeter7);
        }
    }

    public class MoreHolder {
        public TextView tvCount;
    }

}
