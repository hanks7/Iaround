package net.iaround.ui.view.dynamic;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.DynamicReplyBean;
import net.iaround.model.entity.DynamicReviewItem;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-9 上午11:34:17
 * @Description: 单条评论的View
 */
public class CommentItemView extends RelativeLayout {

    public final static int COMMENT_KEY = R.string.dynamic_message_list;// 对于单条评论View设置[评论信息]的Tag的Key
    private OnClickListener commentOnClickListener;//评论的点击事件
    private OnLongClickListener commentOnLongClickListener;//评论的长按事件

    private HeadPhotoView ivImage;// 头像
    private TextView tvName;// 名字

    private LinearLayout lyAge;// 性别和年龄
    private ImageView ivAge;
    private TextView age;
    private TextView tvTime;// 时间
    private TextView tvNotes;

    public CommentItemView(Context context, OnClickListener clickListener, OnLongClickListener longClickListener) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.comment_item_view,
                this);
        commentOnClickListener = clickListener;
        commentOnLongClickListener = longClickListener;
        initView();
    }

    public CommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.comment_item_view,
                this);
        initView();
    }

    public void init(DynamicReplyBean bean) {

        User user = new User();
        user.setIcon(bean.headPic);
        user.setSVip(bean.vip);
        user.setUid(bean.uid);
        ivImage.execute(user);

        SpannableString spContent = FaceManager.getInstance(getContext())
                .parseIconForString(tvName, getContext(),
                        bean.nickname, 14);

        if (bean.vip > 0) {
            tvName.setTextColor(Color.parseColor("#ff0000"));
        } else {
            tvName.setTextColor(Color.parseColor("#000000"));
        }
        tvName.setText(spContent);

        if (bean.gender == 1) {
            ivAge.setImageResource(R.drawable.thread_register_man_select);
            lyAge.setBackgroundResource(R.drawable.encounter_dynamic_man_circle_bg);
        } else {
            ivAge.setImageResource(R.drawable.thread_register_woman_select);
            lyAge.setBackgroundResource(R.drawable.encounter_dynamic_woman_circle_bg);
        }

        try {
            age.setText("" + TimeFormat.getCurrentAgeByBirthdate(bean.birthday));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String timeStr = TimeFormat.timeFormat1(getContext(), bean.time);
        tvTime.setText(timeStr);

        SpannableString spNotesw = FaceManager.getInstance(getContext())
                .parseIconForString(getContext(), bean.content, 0, null);

        SpannableString spReply = FaceManager.getInstance(getContext())
                .parseIconForString(getContext(), bean.replynickame, 0, null);
        tvNotes.setText(spNotesw);
        if (bean.replynickame != null && !TextUtils.isEmpty(bean.replynickame)) {
            tvNotes.setText(getContext().getResources().getString(R.string.dynamic_details_comment_reply) + spReply + ":  " + spNotesw);
        }

        ivImage.setTag(bean);

        this.setTag(COMMENT_KEY, bean);// 设置评论的信息
    }

    public void init(DynamicReviewItem bean) {

        User user = new User();
        user.setIcon(bean.user.icon);
        user.setViplevel(bean.user.vip);
        user.setSVip(bean.user.svip);
        user.setUid(bean.user.userid);
        ivImage.execute(user);

        SpannableString spContent = FaceManager.getInstance(getContext())
                .parseIconForString(tvName, getContext(),
                        bean.user.nickname, 14);//nickname

        if (bean.user.vip > 0) {//vip
            tvName.setTextColor(Color.parseColor("#ff0000"));
        } else {
            tvName.setTextColor(Color.parseColor("#000000"));
        }
        tvName.setText(spContent);

        // 年龄、性别
        age.setText(String.valueOf(bean.user.age));
        if (age.getTag() == null
                || (bean.user.isMale() ^ (Boolean) age.getTag())) {
            if (bean.user.isMale()) {
                ivAge.setImageResource(R.drawable.thread_register_man_select);
                lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
            } else {
                ivAge.setImageResource(R.drawable.thread_register_woman_select);
                lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
            }
        }
//        if ("1".equals(bean.user.gender)) {
//            ivAge.setImageResource(R.drawable.thread_register_man_select);
//            lyAge.setBackgroundResource(R.drawable.encounter_dynamic_man_circle_bg);
//        } else {
//            ivAge.setImageResource(R.drawable.thread_register_woman_select);
//            lyAge.setBackgroundResource(R.drawable.encounter_dynamic_woman_circle_bg);
//        }
//
//        try {
//            age.setText("" + bean.user.age);//TimeFormat.getCurrentAgeByBirthdate(bean.user.birthday)
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String timeStr = TimeFormat.timeFormat1(getContext(), bean.datetime);
        tvTime.setText(timeStr);

        SpannableString spNotesw = FaceManager.getInstance(getContext())
                .parseIconForString(getContext(), bean.content, 0, null);
        tvNotes.setText(spNotesw);
        if (bean.reply != null) {
            SpannableString spReply = FaceManager.getInstance(getContext())
                    .parseIconForString(getContext(), bean.reply.nickname, 0, null);//replynickame

            if (bean.reply.nickname != null && !TextUtils.isEmpty(bean.reply.nickname)) {
                tvNotes.setText(getContext().getResources().getString(R.string.dynamic_details_comment_reply) + spReply + ":  " + spNotesw);
            }
        }


        ivImage.setTag(bean);

        this.setTag(COMMENT_KEY, bean);// 设置评论的信息
    }

    private void initView() {

        ivImage = (HeadPhotoView) findViewById(R.id.ivcomment_icon);// 头像
        tvName = (TextView) findViewById(R.id.user_comment_name);// 名字
        lyAge = (LinearLayout) findViewById(R.id.ly_comment_age);// 性别和年龄
        ivAge = (ImageView) findViewById(R.id.iv_comment_sex);
        age = (TextView) findViewById(R.id.tv_comment_age);
        tvTime = (TextView) findViewById(R.id.tv_comment_timer);// 时间
        tvNotes = (TextView) findViewById(R.id.user_notes);// 评论

        this.setOnClickListener(commentOnClickListener);
        this.setOnLongClickListener(commentOnLongClickListener);
        this.setPadding(0, 10, 0, 10);
    }


}
