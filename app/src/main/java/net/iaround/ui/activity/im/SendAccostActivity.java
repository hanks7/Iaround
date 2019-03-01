
package net.iaround.ui.activity.im;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.NearContact;
import net.iaround.model.im.SocketFailWithFlagResponse;
import net.iaround.model.type.ChatMessageType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.view.HeadPhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/*
 * 发出搭讪列表
 */

public class SendAccostActivity extends BaseFragmentActivity implements OnClickListener {
    private ImageView ivLeft;
    AccostAdapter adapter;
    ListView sendListView;
    ArrayList<NearContact> nearContactList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_accost_acitvity);

        ivLeft = (ImageView) findViewById(R.id.iv_left);
        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.send_accost);
        context = this;

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setImageResource(R.drawable.title_back);
        sendListView = (ListView) findViewById(R.id.accost_listview);
        sendListView.setDividerHeight(0);

        findViewById(R.id.fl_left).setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        nearContactList = ChatPersonalModel.getInstance().getSendAccost(mContext,
                Common.getInstance().loginUser.getUid() + "", 0, 100);

        if (adapter == null) {
            adapter = new AccostAdapter(SendAccostActivity.this, nearContactList);
            sendListView.setAdapter(adapter);
        } else {
            adapter.mList = nearContactList;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left: {
                finish();
            }
            break;
        }
    }

    class AccostAdapter extends BaseAdapter {
        Context context;
        public ArrayList<NearContact> mList;
        LayoutInflater inflater;

        public AccostAdapter(Context context, ArrayList<NearContact> list) {
            this.mList = list;
            this.context = context;
            mContext = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            if (mList != null) {
                return mList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater
                        .inflate(R.layout.message_contact_item, parent, false);

                holder = new ViewHolder();
                holder.icon = (HeadPhotoView) convertView.findViewById(R.id.friend_icon);
                holder.tvNotice = (TextView) convertView.findViewById(R.id.tv_notice);
                holder.name = (TextView) convertView.findViewById(R.id.userName);
                holder.time = (TextView) convertView.findViewById(R.id.onlineTag);
                holder.chat_status = (TextView) convertView.findViewById(R.id.chat_status);
                holder.num = (TextView) convertView.findViewById(R.id.chat_num_status);

                holder.tvNotice.setTextColor(mContext.getResources().getColor(R.color.gray));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final NearContact msg = (NearContact) getItem(position);
            if (msg != null) {
                // 发送状态
                holder.chat_status.setVisibility(View.VISIBLE);
                holder.chat_status.setText("");
                if (msg.getChatStatus() == ChatRecordStatus.SENDING) {// 发送中
                    holder.chat_status.setBackgroundResource(R.drawable.message_send_ing);
                } else if (msg.getChatStatus() == ChatRecordStatus.FAIL) {// 失败
                    holder.chat_status.setBackgroundResource(R.drawable.message_send_fail);
                } else {// 无状态
                    holder.chat_status.setVisibility(View.GONE);
                }

                // 时间
                holder.time.setText(
                        TimeFormat.timeFormat4(mContext, msg.getUser().getLastSayTime()));

                // 昵称
                holder.name.setText(FaceManager.getInstance(mContext).parseIconForString(
                        holder.name, mContext, msg.getUser().getNickname(), 20));

                // 头像

                holder.icon.execute(ChatFromType.UNKONW, msg.getUser(), null);

                // 正文

                String content = getContentByType(msg);
                GeoData geo = LocationUtil.getCurrentGeo(mContext);
                String distance = "["
                        + CommonFunction.covertSelfDistance(LocationUtil.calculateDistance(
                        msg.getUser().getLng(), msg.getUser().getLat(),
                        geo.getLng(), geo.getLat())) + "]";

                holder.tvNotice.setText(FaceManager.getInstance(mContext)
                        .parseIconForString(holder.tvNotice, mContext, distance + content,
                                18));

                holder.num.setVisibility(View.GONE);

                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (msg.getUser().getUid() <= 0) { // 用户不存在
                            CommonFunction.showToast(mContext,
                                    mContext.getString(R.string.none_user), 0);
                            return;
                        }
                        ChatPersonal.skipToChatPersonal((Activity) mContext,
                                msg.getUser(), 0);
                    }
                });

                convertView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        DialogUtil.showTwoButtonDialog(mContext,
                                mContext.getString(R.string.chat_remove_contact_title),
                                mContext.getString(R.string.chat_remove_contact_notice),
                                mContext.getString(R.string.cancel),
                                mContext.getString(R.string.chat_record_fun_del), null,
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        int id = MessageModel
                                                .getInstance()
                                                .deleteRecordWithPerson(
                                                        String.valueOf(Common.getInstance().loginUser
                                                                .getUid()),
                                                        String.valueOf(msg.getfUid()),
                                                        mContext);
                                        if (id > 0) {
                                            mList.remove(position);
                                        }
                                        notifyDataSetChanged();
                                    }
                                });
                        return false;
                    }
                });


            }
            return convertView;
        }

        private String getContentByType(NearContact msg) {
            String content = "";

            switch (msg.getType()) {
                case ChatMessageType.TEXT:// 普通对话，低版本的打招呼，通过表情来替代
//                    if (msg.getContent().equals(
//                            mContext.getString(R.string.greeting_content))) {
                    //YC
                    if (mContext.getString(R.string.greeting_content).equals(msg.getContent()))
                    {
                        msg.setContent("[#a" + (new Random().nextInt(5) + 1) + "#]");
                    }
                    content = msg.getContent();
                    break;
                case ChatMessageType.IMAGE:// 图片
                    content = formatPackage(R.string.chat_picture);
                    break;
                case ChatMessageType.SOUND:// 语音
                    content = formatPackage(R.string.chat_sound);
                    break;
                case ChatMessageType.VIDEO:// 视频
                    content = formatPackage(R.string.chat_video);
                    break;
                case ChatMessageType.LOCATION:// 位置
                    content = formatPackage(R.string.chat_location);
                    break;
                case ChatMessageType.GIFE_REMIND:// 礼物
                    content = formatPackage(R.string.gift);
                    break;
                case ChatMessageType.MEET_GIFT:// 约会道具
                    content = formatPackage(R.string.appointprop);
                    break;
                case ChatMessageType.GAME:
                    try {
                        JSONObject obj = new JSONObject(msg.getContent());
                        content = CommonFunction.jsonOptString(obj, "content");
                    } catch (JSONException e) {

                        content = "";
                    }
                    break;
                case ChatMessageType.FACE:// 贴图
                    content = formatPackage(R.string.mapping);
                    break;
                case ChatMessageType.TELETEXT:// 5.1版本 小秘书图文
                    try {
                        JSONObject contentobj = new JSONObject(msg.getContent());
                        String title = CommonFunction.jsonOptString(contentobj, "title");
                        if (!TextUtils.isEmpty(title)) {
                            content = title;
                        } else {
                            content = formatPackage(R.string.chat_teletext);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ChatRecordViewFactory.FOLLOW:// 5.3关注
                {
                    content = msg.getContent();
                }
                break;
                case ChatRecordViewFactory.ACCOST_GAME_ANS: {
                    JSONObject obj = null;
                    int answerway = 0;
                    try {

                        obj = new JSONObject(msg.getContent());
                        answerway = obj.getInt("answerway");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (answerway == 0) {
                        content = formatPackage(R.string.reply_true_word);
                    } else {
                        content = formatPackage(R.string.reply_adventure);
                    }
                }
                break;
                case ChatRecordViewFactory.SHARE:// 5.5分享
                {
                    content = formatPackage(R.string.share_success);
                }
                break;
                case ChatRecordViewFactory.ERROR_PROMT:
                    content = mContext.getString(R.string.se_200083010);
                    break;
                case ChatRecordViewFactory.GIFT_LIMIT_PROMT:
                    content = msg.getContent();
                    break;
                default:// 低版本无法显示
                    content = mContext.getString(R.string.low_version);
            }

            return content;
        }

        private String formatPackage(int contentId) {
            if (mContext == null)
                mContext = BaseApplication.appContext;
            String content = mContext.getString(contentId);
            return "[" + content + "]";
        }
    }


    static class ViewHolder {
        public HeadPhotoView icon;
        public TextView name;
        public TextView time;
        public TextView tvNotice;
        public TextView chat_status;
        public TextView num;
    }

}
