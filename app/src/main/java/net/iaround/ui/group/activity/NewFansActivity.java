
package net.iaround.ui.group.activity;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.NewFansModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.friend.bean.BlacklistFilteringBean;
import net.iaround.ui.friend.bean.BlacklistFilteringBean.UserBlack;
import net.iaround.ui.friend.bean.NewFansBean;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author tanzy
 * @Description: 新增粉丝界面
 * @date 2015-4-17
 */
public class NewFansActivity extends BaseFragmentActivity implements OnClickListener {
    private static final int MSG_REFRESH_DATA = 1000;

    private TextView titleName;
    private ListView listView;

    private ArrayList<NewFansBean> dataList = new ArrayList<NewFansBean>();
    private HashMap<Long, Long> loveFlagToUid = new HashMap<Long, Long>();

    private long blacklistFilteringFlag;
    private DataAdapter adapter;
    private long enterTime = 0;// 进入此界面时数据库中最大的时间
    private boolean quit = false;// 是否离开本界面，用于判断onResume是否需要读数据库
    private long muid;

	/*
     * 进入此界面的时候先获取所有数据并记录此时数据库内最大的时间 不记录本地时间是怕本地时间不正确和无网络是无法获取与服务器的时间差
	 * 返回此界面时再查询比记录时间小的记录
	 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_fans_activity);

        titleName = (TextView) findViewById(R.id.tv_title);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setDividerHeight(0);
        listView.setDivider(null);
        muid = Common.getInstance().loginUser.getUid();


        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        titleName.setText(R.string.my_fans_msg);

        dataList = NewFansModel.getInstance().getNewFansList(mContext, muid);
        if (dataList != null && dataList.size() > 0)
            enterTime = dataList.get(0).datetime;
        blacklistFiltering();
    }

    /**
     * 黑名单过滤
     */
    private void blacklistFiltering(){
        if (dataList != null & dataList.size() > 0){
            String filter = "";
            for (int i = 0; i < dataList.size(); i++){
                NewFansBean newFansBean = dataList.get(i);
                if (i == dataList.size() - 1){
                    filter += newFansBean.userinfo.userid;
                }else{
                    filter += newFansBean.userinfo.userid + ",";
                }
            }
            blacklistFilteringFlag = FriendHttpProtocol.getBlacklistFiltering(this,filter,this);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (quit) {
            dataList = NewFansModel.getInstance()
                    .getNewFansList(mContext, enterTime, muid);
            quit = false;
        }

        if (adapter == null)
            adapter = new DataAdapter();
        listView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        quit = true;
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        if (loveFlagToUid.containsKey(flag)) {

            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (bean.isSuccess() || bean.error == 4301) {// 关注成功或已经关注
                long uid = loveFlagToUid.get(flag);
                NewFansModel.getInstance().updateRelation(mContext, uid, 1, muid);
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).userinfo.userid == uid) {
                        dataList.get(i).userinfo.relation = 1;
                    }
                }

                handler.sendEmptyMessage(MSG_REFRESH_DATA);
            } else {
                ErrorCode.showError(mContext, result);
            }

            loveFlagToUid.remove(flag);
        }else if (blacklistFilteringFlag == flag){
            BlacklistFilteringBean bean = GsonUtil.getInstance().getServerBean(result,BlacklistFilteringBean.class);
            if (bean != null)
            {
                if (bean.isSuccess()){
                    if (bean.userid != null) {
                        for (UserBlack userBlack : bean.userid){
                            for (NewFansBean newFansBean : dataList){
                                if (userBlack.status == 1){
                                    if (newFansBean.userinfo.userid == userBlack.userid){
                                        NewFansModel.getInstance().deleteOneMessage(NewFansActivity.this,userBlack.userid,muid);
                                    }
                                }
                            }
                        }
                    }
            }

                dataList = NewFansModel.getInstance()
                        .getNewFansList(mContext, enterTime, muid);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        if (loveFlagToUid.containsKey(flag)) {
            ErrorCode.toastError(this, e);
            loveFlagToUid.remove(flag);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_DATA: {
                    adapter.notifyDataSetChanged();
                }
                break;
            }
        }

    };

    public class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public NewFansBean getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) NewFansActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.new_fans_list_item, parent, false);

                holder.fansIcon = (HeadPhotoView) convertView.findViewById(R.id.fans_icon);
                holder.fansName = (TextView) convertView.findViewById(R.id.fans_name);
                holder.ageSex = (TextView) convertView.findViewById(R.id.tvAge);
                holder.datetime = (TextView) convertView.findViewById(R.id.datetime);
                holder.leftText = (TextView) convertView.findViewById(R.id.left_text);
                holder.rightText = (TextView) convertView.findViewById(R.id.right_text);
                holder.line = convertView.findViewById(R.id.view1);
                holder.rlSexAge = (RelativeLayout) convertView.findViewById(R.id.rlAgeSex);
                holder.ivSex = (ImageView) convertView.findViewById(R.id.ivSex);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final NewFansBean bean = getItem(position);

            // 头像
            holder.fansIcon.execute(ChatFromType.UNKONW, bean.userinfo.convertBaseToUser(), null);

            // 昵称
            holder.fansName.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(holder.fansName, mContext, bean.userinfo.nickname, 20));

            if (bean.userinfo.svip > 0) {//gh  将vip改为svip
                holder.fansName.setTextColor(Color.parseColor("#FF4064"));
            } else {
                holder.fansName.setTextColor(Color.parseColor("#000000"));
            }

            // 时间
            holder.datetime.setText(TimeFormat.timeFormat4(getApplicationContext(), bean.datetime));

            // 年龄
            if (bean.userinfo.age <= 0) {
                holder.ageSex.setText(R.string.unknown);
            } else {
                holder.ageSex.setText(String.valueOf(bean.userinfo.age));
            }

            // 性别
            String sex = bean.userinfo.gender;
            if ("f".equals(sex)) {
                holder.ivSex.setImageResource(R.drawable.z_common_female_icon);
                holder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
            } else {
                holder.ivSex.setImageResource(R.drawable.z_common_male_icon);
                holder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_man_bg);

            }

            if (position >= getCount() - 1)
                holder.line.setVisibility(View.GONE);
            else
                holder.line.setVisibility(View.VISIBLE);


            // 按钮
            final int pos = position;
            final NewFansBean tmp = bean;
            if (bean.userinfo.relation == 1 || bean.userinfo.relation == 3) {// 已关注，显示删除和已关注
                holder.leftText.setText(R.string.delete);
                holder.rightText.setText(R.string.private_chat);
//                holder.rightText.setBackgroundResource(R.drawable.z_fans_btn_gray_bg);
                holder.rightText.setBackgroundResource(R.drawable.z_fans_btn_red_chat);
                holder.rightText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = new User();
                        user.setUid(bean.userinfo.userid);
                        user.setNickname(bean.userinfo.nickname);
                        user.setNoteName(bean.userinfo.notes);
                        user.setIcon(bean.userinfo.icon);
                        user.setViplevel(bean.userinfo.viplevel);
                        user.setPhotoNum(bean.userinfo.photonum);
                        user.setJob(bean.userinfo.occupation >= 0 ? bean.userinfo.occupation : -1);
                        user.setSign(bean.userinfo.selftext);
                        int sex = 0;
                        if ("f".equals(bean.userinfo.gender)) {
                            sex = 2;
                        } else if ("m".equals(bean.userinfo.gender)) {
                            sex = 1;
                        }
                        user.setSex(sex);
                        user.setLat(bean.userinfo.lat);
                        user.setLng(bean.userinfo.lng);
                        user.setAge(bean.userinfo.age);
                        user.setPersonalInfor(bean.userinfo.selftext);
                        user.setLastLoginTime(bean.userinfo.lastonlinetime);
                        user.setWeibo(bean.userinfo.weibo);
                        user.setDistance(bean.userinfo.distance);
                        user.setRelationship(bean.userinfo.relation);
                        ChatPersonal.skipToChatPersonal(mActivity,user);
                    }
                });
            } else {// 未关注，显示没兴趣和关注
                holder.leftText.setText(R.string.fans_msg_ignor);
                holder.rightText.setText(R.string.space_other_att);
                holder.rightText.setBackgroundResource(R.drawable.z_fans_btn_red_new);
                holder.rightText.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long flag = FriendHttpProtocol.userFanLove(mContext,
                                tmp.userinfo.userid, 0, 0, NewFansActivity.this);
                        loveFlagToUid.put(flag, tmp.userinfo.userid);
                    }
                });
            }

            holder.leftText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dataList.isEmpty() && dataList != null) {
                        dataList.remove(pos);
                        NewFansModel.getInstance().deleteOneMessage(mContext,
                                tmp.userinfo.userid, muid);
                        handler.sendEmptyMessage(MSG_REFRESH_DATA);
                    }

                }
            });


            return convertView;
        }

    }

    public class ViewHolder {
        public HeadPhotoView fansIcon;
        public TextView fansName;
        public TextView ageSex;
        public TextView datetime;
        public TextView leftText;
        public TextView rightText;
        public View line;
        public RelativeLayout rlSexAge;
        public ImageView ivSex;

    }

}
