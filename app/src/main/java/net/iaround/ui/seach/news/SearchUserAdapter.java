package net.iaround.ui.seach.news;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.SearchTextView;

import java.util.List;

/**
 * 搜索用户适配器
 * Created by Administrator on 2017/11/3.
 */

public class SearchUserAdapter extends BaseAdapter {

    private List<GroupSearchUser> mList;//数据源
    private LayoutInflater mInflater;//布局装载器对象
    private Context mContext;
    private GeoData userGeoData;
    private String mKeyWord;//关键字

    public SearchUserAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.userGeoData = LocationUtil.getCurrentGeo( BaseApplication.appContext );
    }

    /**
     * 更新数据
     * @param keyword
     * @param mList
     */
    public void notifyData(String keyword,List<GroupSearchUser> mList){
        this.mKeyWord = keyword;
        this.mList = mList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(parent.getContext(),
                    R.layout.user_nearby_list_item2, null);
            viewHolder.userIcon = (HeadPhotoView) convertView
                    .findViewById(R.id.friend_icon);
            viewHolder.tvNickName = (SearchTextView) convertView
                    .findViewById(R.id.tvNickName);
            viewHolder.tvAge = (TextView) convertView.findViewById(R.id.tvAge);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.tvDistance);
            viewHolder.tvState = (TextView) convertView.findViewById(R.id.tvState);
            viewHolder.tvSign = (TextView) convertView.findViewById(R.id.tvSign);
            viewHolder.forbidImage = (ImageView) convertView
                    .findViewById(R.id.bannedIcon);
            viewHolder.weiboIcon = (LinearLayout) convertView
                    .findViewById(R.id.llWeiboIcon);
            viewHolder.llSexAndAge = (LinearLayout) convertView.findViewById(R.id.info_center);
            viewHolder.ivSex = (ImageView) convertView.findViewById(R.id.iv_sex);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final GroupSearchUser groupUser = mList.get( position );

        // 头像
        viewHolder.userIcon.execute(groupUser.user.convertBaseToUser(),true);


        // 昵称
        String name = groupUser.user.getDisplayName( true );
        if ( name == null || name.length( ) <= 0 )
        {
            name = String.valueOf( groupUser.user.userid );
        }
        SpannableString spName = FaceManager.getInstance( parent.getContext( ) )
                .parseIconForString( viewHolder.tvNickName , parent.getContext( ) , name ,
                        20 );
        viewHolder.tvNickName.setSpecifiedTextsColor(String.valueOf(spName), mKeyWord, Color.parseColor("#FF0000"));


        // 年龄
        if ( groupUser.user.age <= 0 )
        {
            viewHolder.tvAge.setText( R.string.unknown );
        }
        else
        {
            viewHolder.tvAge.setText( String.valueOf( groupUser.user.age ) );
        }
        // 性别
        int sex = groupUser.user.getSex( );
        if ( sex == 2 )
        {
            viewHolder.ivSex.setImageResource(R.drawable.z_common_female_icon);
            viewHolder.llSexAndAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
        }
        else if (sex == 1)
        {
            viewHolder.ivSex.setImageResource(R.drawable.z_common_male_icon);
            viewHolder.llSexAndAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
        }
        // 距离
        int distance = -1;
        try
        {
            distance = LocationUtil.calculateDistance( userGeoData.getLng( ) ,
                    userGeoData.getLat( ) , groupUser.user.lng , groupUser.user.lat );
        }
        catch ( Exception e )
        {

        }
        if ( distance < 0 )
        { // 不可知
            viewHolder.distance.setText( R.string.unable_to_get_distance );
        }
        else
        {
            viewHolder.distance.setText( CommonFunction.covertSelfDistance( distance ) );
        }

        // 在线状态
        String time = TimeFormat.timeFormat1( viewHolder.tvState.getContext( ) ,
                groupUser.user.lastonlinetime );
        if ( time != null && time.length( ) > 0 )
        {
            viewHolder.tvState.setText( time );
        }
        else
        {
            viewHolder.tvState.setText( R.string.unable_to_get_time );
        }

        viewHolder.forbidImage
                .setVisibility( groupUser.user.isForbidUser( ) ? View.VISIBLE : View.GONE );

        viewHolder.tvSign.setText("");
        // 签名
        final String infor = groupUser.user.getPersonalInfor( mContext );
        if ( infor != null && !"".equals(infor))
        {
            SpannableString spSign = FaceManager.getInstance( parent.getContext( ) )
                    .parseIconForString( viewHolder.tvSign , parent.getContext( ) , infor ,
                            10 );
            viewHolder.tvSign.setText( spSign );
            viewHolder.tvSign.setVisibility( View.VISIBLE );

        }

        ImageView[ ] weibos = new ImageView[ 6 ];
        weibos[ 0 ] = (ImageView)convertView.findViewById( R.id.weibos_icon_1 );
        weibos[ 1 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_2 );
        weibos[ 2 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_3 );
        weibos[ 3 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_4 );
        weibos[ 4 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_5 );
        weibos[ 5 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_6 );
        CommonFunction.showWeibosIcon( weibos ,
                User.parseWeiboStr( groupUser.user.weibo ) , groupUser.user.occupation ,
                parent.getContext( ) );


        convertView.setOnClickListener( new View.OnClickListener( )
        {

            @Override
            public void onClick( View v )
            {
                User user = GroupSearchUser.convertToUser( groupUser );
                if (groupUser.user.userid == Common.getInstance().loginUser.getUid()){
                    Intent intent = new Intent();
                    intent.setClass(mContext, UserInfoActivity.class);
                    intent.putExtra("user",user);
                    intent.putExtra(Constants.UID,user.getUid());
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent();
                    intent.setClass(mContext, OtherInfoActivity.class);
                    intent.putExtra("user",user);
                    intent.putExtra(Constants.UID,user.getUid());
                    mContext.startActivity(intent);
                }
            }
        } );

        return convertView;
    }

    class ViewHolder {
        public HeadPhotoView userIcon;
        public SearchTextView tvNickName;
        public TextView tvAge;
        public TextView distance;
        public TextView tvState;
        public TextView tvSign;
        public ImageView forbidImage;
        public LinearLayout weiboIcon;
        public LinearLayout llSexAndAge;
        public ImageView ivSex;
    }
}
