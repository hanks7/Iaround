package net.iaround.ui.seach.news;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.view.SearchTextView;

import java.util.ArrayList;

/**
 * 搜索聊吧适配器
 * Created by gh on 2017/11/6.
 */

public class SearchChatBarAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Group> mDataList;
    private String mKeyWord;//关键字

    public SearchChatBarAdapter(Context mContext, ArrayList<Group> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    /**
     * 更新适配器数据
     * @param keyword
     * @param mDataList
     */
    public void updateData(String keyword,ArrayList<Group> mDataList){
        this.mKeyWord = keyword;
        this.mDataList = mDataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.group_search_item, null);
            holder.groupHeader = (TextView) convertView.findViewById(R.id.header_text);
            holder.groupContent = (RelativeLayout) convertView
                    .findViewById(R.id.content_layout);
            holder.groupImage = (ImageView) convertView.findViewById(R.id.group_img);
            holder.groupTypeImg = (ImageView) convertView.findViewById(R.id.group_type);
            holder.groupName = (SearchTextView) convertView.findViewById(R.id.group_name);
            holder.groupOwnerIcon = (ImageView) convertView.findViewById(R.id.group_owner_gender_icon);
            holder.groupOwnerAgeSex = (LinearLayout) convertView.findViewById(R.id.ll_group_owner);
            holder.groupDesc = (TextView) convertView.findViewById(R.id.group_desc);
            holder.groupLevel = (RatingBar) convertView.findViewById(R.id.group_level);
            holder.groupMemberNum = (TextView) convertView
                    .findViewById(R.id.group_member_num);

            holder.headerPart = convertView.findViewById(R.id.header_part);
            holder.groupFlag = (TextView) convertView.findViewById(R.id.group_flag);
            holder.groupDistance = (TextView) convertView.findViewById(R.id.group_distance);
            holder.groupCurrentMembers = (TextView) convertView.findViewById(R.id.group_current_members);
            holder.splitLine = (TextView) convertView.findViewById(R.id.split_line);
            holder.groupMaxMembers = (TextView) convertView.findViewById(R.id.group_max_members);
            holder.groupCategory = (TextView) convertView.findViewById(R.id.group_category);
            holder.bottomDivider = convertView.findViewById(R.id.bottom_divider);
            holder.rlGroupDesc = (RelativeLayout) convertView.findViewById(R.id.rl_group_desc);
            holder.ivGroupNum = (ImageView) convertView.findViewById(R.id.group_members_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Group group = mDataList.get(position);
        if (group != null) {

            if (group.contentType == 1) {
                holder.headerPart.setVisibility(View.VISIBLE);
                holder.groupHeader.setVisibility(View.VISIBLE);
                holder.groupContent.setVisibility(View.GONE);
                holder.groupHeader.setText(group.landmarkname + "");
            } else {

                holder.headerPart.setVisibility(View.GONE);
                holder.groupHeader.setVisibility(View.GONE);
                holder.groupContent.setVisibility(View.VISIBLE);

                GlideUtil.loadRoundImage(BaseApplication.appContext, CommonFunction.thumPicture(group.icon), 180, holder.groupImage, R.drawable.group_item_new_default_icon,
                        R.drawable.group_item_new_default_icon);
                if (group.newcategoryid == 20)//
                {
                    holder.groupTypeImg.setVisibility(View.GONE);
                }

                /**
                 * 圈主性别和会员判断
                 */
//                if (group.user.viplevel > 0) {
//                    holder.groupOwnerAgeSex.setBackgroundResource(R.drawable.group_recommend_owner_bg);
//                    holder.groupOwnerIcon.setImageResource(R.drawable.group_owner_vip);
//                } else {
//                    if (group.user.getSex() == 1) {
//                        holder.groupOwnerAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
//                        holder.groupOwnerIcon.setImageResource(R.drawable.thread_register_man_select);
//
//                    } else if (group.user.getSex() == 2) {
//                        holder.groupOwnerAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
//                        holder.groupOwnerIcon.setImageResource(R.drawable.thread_register_woman_select);
//                    }
//                }

                holder.groupOwnerAgeSex.setVisibility(View.GONE);

                //圈子名称
                SpannableString groupName = FaceManager.getInstance(parent.getContext())
                        .parseIconForString(holder.groupName, parent.getContext(),
                                group.name + "   ", 16);
//                holder.groupName.setText(groupName);
                holder.groupName.setSpecifiedTextsColor(String.valueOf(groupName), mKeyWord, Color.parseColor("#FF0000"));

                //圈子描述
                SpannableString groupDesc = FaceManager.getInstance(parent.getContext())
                        .parseIconForString(holder.groupDesc, parent.getContext(),
                                group.getGroupDesc(mContext), 13);
                if (groupDesc != null || group.getGroupDesc(mContext) != null) {
                    holder.rlGroupDesc.setVisibility(View.VISIBLE);
                    holder.groupDesc.setText(groupDesc);
                }

                //圈子等级
                holder.groupLevel.setRating((float) (group.level / 2.0));
                holder.groupMemberNum.setText(group.usercount + "");

                //圈子标志
                if (group.flag == 1) {
                    //holder.groupFlag.setImageResource(R.drawable.chat_theme_new);
                    holder.groupFlag.setVisibility(View.GONE);
                    holder.groupName.
                            setCompoundDrawables(null, null, null, null);
                } else if (group.flag == 2) {
                    holder.groupFlag.setVisibility(View.GONE);
                    //holder.groupFlag.setImageResource(R.drawable.group_hot_flag_img);

                    holder.groupFlag.setText("hot");
                    int flagIcon = R.drawable.group_flag_hot;
                    holder.groupFlag.setCompoundDrawablesWithIntrinsicBounds(0, 0, flagIcon, 0);
                    holder.groupFlag.setBackgroundResource(R.drawable.group_flag_hot_bg);


                } else {
                    holder.groupFlag.setVisibility(View.GONE);
                    holder.groupName.
                            setCompoundDrawables(null, null, null, null);
                }

                holder.groupDistance.setText("  " + CommonFunction.covertSelfDistance(group.rang));

                /**
                 * 修改者于超
                 */
                //小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
                holder.groupCurrentMembers.setText(String.valueOf(group.usercount));
                if (group.classify == 1) {
                    holder.ivGroupNum.setImageResource(R.drawable.chat_bar_popular_search_member);//group_num_small_round
                    holder.splitLine.setVisibility(View.VISIBLE);
                    holder.groupMaxMembers.setText(String.valueOf(group.maxcount));
                } else if (group.classify == 2) {
                    holder.ivGroupNum.setImageResource(R.drawable.chat_bar_popular_search_member);//group_num_big_round
                    holder.splitLine.setVisibility(View.GONE);
                    holder.groupMaxMembers.setText("");
                } else if (group.classify == 3) {
                    holder.ivGroupNum.setImageResource(R.drawable.chat_bar_popular_search_member);//group_num_big_round
                    holder.splitLine.setVisibility(View.GONE);
                    holder.groupMaxMembers.setText("");
                }
                if (group.category != null) {
                    String[] typeArray = group.category.split("\\|");
                    int langIndex = CommonFunction.getLanguageIndex(mContext);
                    holder.groupCategory.setText(typeArray[langIndex]);
                }

            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView groupHeader;
        public RelativeLayout groupContent;
        public ImageView groupImage;
        public ImageView groupTypeImg;
        public SearchTextView groupName;
        public LinearLayout groupOwnerAgeSex;
        public ImageView groupOwnerIcon;
        public TextView groupDesc;
        public RatingBar groupLevel;
        public TextView groupMemberNum;
        public ImageView ivGroupNum;

        public View headerPart;
        public TextView groupFlag;
        public TextView groupDistance;
        public TextView groupCurrentMembers;
        public TextView splitLine;
        public TextView groupMaxMembers;
        public TextView groupCategory;
        public View bottomDivider;
        public RelativeLayout rlGroupDesc;
    }
}

