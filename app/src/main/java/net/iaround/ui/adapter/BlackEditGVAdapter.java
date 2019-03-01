package net.iaround.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.group.bean.GroupSearchUser;

import java.util.ArrayList;

/**
 * @author：liush on 2017/1/6 22:33
 */
public class BlackEditGVAdapter extends BaseAdapter {

    private ArrayList<GroupSearchUser> datas;
    private Context context;
    private Boolean isShowDeleteIcon = false;

    public BlackEditGVAdapter(Context context, ArrayList<GroupSearchUser> datas) {
        this.context = context;
        this.datas = datas;
    }

    public ArrayList<GroupSearchUser> getDatas() {
        if(datas == null){
            datas = new ArrayList<>();
        }
        return datas;
    }

    public Boolean getShowDeleteIcon() {
        return isShowDeleteIcon;
    }

    public void setShowDeleteIcon(Boolean showDeleteIcon) {
        isShowDeleteIcon = showDeleteIcon;
    }

    @Override
    public int getCount() {
        if(datas == null){
            return 2;
        }
        return datas.size()+2;
    }

    @Override
    public Object getItem(int position) {
        if(position<datas.size()){
            return datas.get(position);
        }
        return  null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(parent.getContext(), R.layout.item_black_gv, null);
        ImageView ivHead = (ImageView) view.findViewById(R.id.iv_head);
        ImageView ivDeleteIcon = (ImageView) view.findViewById(R.id.iv_delete_icon);
        TextView tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
        if(position == getCount()-2){
            ivHead.setImageResource(R.drawable.black_add);
            ivDeleteIcon.setVisibility(View.GONE);
            tvNickname.setVisibility(View.INVISIBLE);
        } else if(position == getCount()-1){
            if(getCount() <= 2){
                ivHead.setVisibility(View.GONE);
                ivDeleteIcon.setVisibility(View.GONE);
                tvNickname.setVisibility(View.GONE);
            } else {
                ivHead.setImageResource(R.drawable.black_delete);
                ivDeleteIcon.setVisibility(View.GONE);
                tvNickname.setVisibility(View.INVISIBLE);
            }
        } else {
            GroupSearchUser blackEntity = datas.get(position);
            GlideUtil.loadCircleImage(BaseApplication.appContext, CommonFunction.getThumPicUrl(blackEntity.user.getIcon()), ivHead, R.drawable.default_avatar_round_light, R.drawable.default_avatar_round_light);
            tvNickname.setText(blackEntity.user.nickname);
            if(getShowDeleteIcon()){
                ivDeleteIcon.setVisibility(View.VISIBLE);
            } else {
                ivDeleteIcon.setVisibility(View.GONE);
            }
        }

        return view;
    }
}
