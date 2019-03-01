package net.iaround.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.pay.vip.VipPrivilegeBean.Privileges;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.WebViewAvtivity;

import java.util.ArrayList;

import static net.iaround.tools.CommonFunction.getLangText;

/**
 * @author：liush on 2016/12/16 20:49
 */
public class VIPPowerAdapter extends BaseAdapter {

    private int[] logoArr;
    private String[] powerArr;
    private String[] powerDetailArr;
    private Context context;
    private ArrayList<Privileges> privileges;

    public VIPPowerAdapter(int[] logoArr, String[] powerArr, String[] powerDetailArr) {
        this.logoArr = logoArr;
        this.powerArr = powerArr;
        this.powerDetailArr = powerDetailArr;
    }

    public VIPPowerAdapter(Context context, ArrayList<Privileges> privileges) {
        this.context = context;
        this.privileges = privileges;
    }

    @Override
    public int getCount() {
        if (privileges == null) {
            return 0;
        }
        return privileges.size();
    }

    @Override
    public Object getItem(int position) {
        return privileges.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
//        convertView = View.inflate(parent.getContext(), R.layout.activity_user_vip_lv_item, null);
//        ImageView ivPowerLogo = (ImageView) convertView.findViewById(R.id.iv_power_logo);
//        ivPowerLogo.setImageResource(logoArr[position]);
//        TextView tvPower = (TextView) convertView.findViewById(R.id.tv_power);
//        tvPower.setText(powerArr[position]);
//        TextView tvPowerDetail = (TextView) convertView.findViewById(R.id.tv_power_detail);
//        tvPowerDetail.setText(powerDetailArr[position]);
        if (convertView == null) {

            convertView = View.inflate(parent.getContext(), R.layout.activity_user_vip_lv_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ivPowerLogo = (ImageView) convertView.findViewById(R.id.iv_power_logo);
            viewHolder.tvPower = (TextView) convertView.findViewById(R.id.tv_power);
            viewHolder.tvPowerDetail = (TextView) convertView.findViewById(R.id.tv_power_detail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (privileges != null) {
            GlideUtil.loadCircleImage(BaseApplication.appContext, privileges.get(position).icon, viewHolder.ivPowerLogo);
            viewHolder.tvPower.setText(getLangText(context, privileges.get(position).name));
            viewHolder.tvPowerDetail.setText(CommonFunction.getLangText(context, privileges.get(position).resume));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "跳转到webview页面", Toast.LENGTH_SHORT).show();
//                WebViewAvtivity.launchVerifyCode(context, 1, privileges.get(position).url);

                String url = CommonFunction.getLangText(context, privileges.get(position).url);
                Uri uri = Uri.parse(url);
                Intent i = new Intent(context, WebViewAvtivity.class);
                String name = CommonFunction.getLangText(context, privileges.get(position).name);
                i.putExtra(WebViewAvtivity.WEBVIEW_TITLE, name);
                i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
                context.startActivity(i);
            }
        });
        return convertView;
    }

    public void setDataNotify(ArrayList<Privileges> privileges) {
        this.privileges = privileges;
        this.notifyDataSetChanged();
    }

    public class ViewHolder {

        ImageView ivPowerLogo;
        TextView tvPower;
        TextView tvPowerDetail;
    }
}
