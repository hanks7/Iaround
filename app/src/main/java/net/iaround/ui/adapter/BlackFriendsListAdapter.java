package net.iaround.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.entity.type.ChatFromType;
import net.iaround.tools.FaceManager;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;

/**
 * @author：liush on 2017/1/7 15:16
 */
public class BlackFriendsListAdapter extends BaseAdapter {

    private Context context;
//    private ArrayList<BlackEntity> datas;
    private ArrayList<GroupSearchUser> datas;

    public BlackFriendsListAdapter(Context context, ArrayList<GroupSearchUser> datas) {
        this.context = context;
        this.datas = datas;
    }

    public ArrayList<GroupSearchUser> getDatas() {
        if(datas == null){
            datas = new ArrayList<>();
        }
        return datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public GroupSearchUser getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        {// 当为其他需要显示头像的界面时
            if ( convertView == null )
            {
                convertView = View.inflate( context ,
                        R.layout.invisible_setting_user_item , null );
            }

            // 头像
            ( (HeadPhotoView) convertView.findViewById( R.id.icon ) ).execute( ChatFromType.UNKONW,
                    getItem( position ).user.convertBaseToUser(),null );


            // 名字
            ( (TextView) convertView.findViewById( R.id.name ) ).setText( FaceManager
                    .getInstance( context ).parseIconForString(
                            ( TextView ) convertView.findViewById( R.id.name ) , context ,
                            getItem( position ).user.nickname , 18 ) );

            // 选中
             final CheckBox box = ( ( CheckBox ) convertView.findViewById( R.id.check_box ) );
            if ( getItem( position ).contentType == 1 )
            {
                box.setEnabled( false );
                box.setBackgroundResource( R.drawable.round_check_gray_selector );
                box.setChecked( true );
            }
            else
            {
                box.setEnabled( true );
                box.setBackgroundResource( R.drawable.round_check_selector );
                box.setChecked( getItem( position ).isCheck );
            }

//            box.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener( )
//            {
//                @Override
//                public void onCheckedChanged( CompoundButton buttonView , boolean isChecked )
//                {
//
//                }
//            } );

            box.setOnClickListener( new View.OnClickListener( )
            {
                @Override
                public void onClick( View v )
                {
                    getItem( position ).isCheck = ( ( CheckBox ) v ).isChecked( );
                }
            } );

            convertView.findViewById(R.id.rl_friends).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isCheck = !box.isChecked();
                    box.setChecked(isCheck);
                    getItem( position ).isCheck = isCheck;
                }
            });
        }

//        if ( position == 0 )
//            convertView.findViewById( R.id.bottom_line ).setVisibility( View.INVISIBLE );
//        else
//            convertView.findViewById( R.id.bottom_line ).setVisibility( View.VISIBLE );

//        ViewHolder viewHolder;
//        if(convertView == null){
//            convertView = View.inflate(context, R.layout.item_black_friends_list, null);
//            viewHolder = new ViewHolder();
//            viewHolder.viewDivider = convertView.findViewById(R.id.view_divider);
//            viewHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
//            viewHolder.tvNickname = (TextView) convertView.findViewById(R.id.tv_nickname);
//            viewHolder.ivState = (ImageView) convertView.findViewById(R.id.iv_state);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        if(position == 0){
//            viewHolder.viewDivider.setVisibility(View.GONE);
//        }
//        BlackEntity blackEntity = datas.get(position);
//        GlideUtil.loadImage(context, CommonFunction.getThumPicUrl(blackEntity.getHeadPic()), viewHolder.ivHead, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);
//        viewHolder.tvNickname.setText(blackEntity.getNickname());
        return convertView;
    }

//    class ViewHolder{
//        View viewDivider;
//        ImageView ivHead;
//        TextView tvNickname;
//        ImageView ivState;
//    }
}
