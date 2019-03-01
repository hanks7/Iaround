package net.iaround.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
//import net.iaround.model.database.Lable;
import net.iaround.model.entity.HobbyType;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.view.user.OneLineFlowLayout;

import java.util.List;

/**
 * @author：liush on 2016/12/7 15:12
 */
public class EditUserInfoHobbysAdapter extends BaseAdapter {

    List<HobbyType> hobbyTypes;

    public EditUserInfoHobbysAdapter(List<HobbyType> hobbyTypes) {
        this.hobbyTypes = hobbyTypes;
    }

    @Override
    public int getCount() {
        if(hobbyTypes == null){
            return 0;
        }
        return hobbyTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return hobbyTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(parent.getContext(), R.layout.activity_edit_userinfo_hobby_item, null);
        HobbyType hobbyType = hobbyTypes.get(position);
        if(position == 0){
            view.findViewById(R.id.view_divider).setVisibility(View.GONE);
        }
        ImageView ivHobbytypeIcon = (ImageView) view.findViewById(R.id.iv_hobby_type_icon);
        ivHobbytypeIcon.setImageResource(hobbyType.getIconId());
        TextView tvType = (TextView) view.findViewById(R.id.tv_hobby_type);
        tvType.setTextColor(hobbyType.getTypeColor());
        tvType.setText(hobbyType.getType());
        TextView tvDetails = (TextView) view.findViewById(R.id.tv_details);
        StringBuilder lables = new StringBuilder();
//        List<Lable> lableList = hobbyType.getDetails();
//        if(lableList!=null && lableList.size() > 0){
//            lables.append(lableList.get(0).getLableValue());
//            for (int i=1; i<lableList.size(); i++) {
//                lables.append("，" + lableList.get(i).getLableValue());
//            }
//        }//yuchao
        tvDetails.setText(lables);

        return view;
    }
}
