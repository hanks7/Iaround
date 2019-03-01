package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.skill.SkillBean;
import net.iaround.model.skill.SkillUpdateEntity;
import net.iaround.tools.CommonFunction;
import net.iaround.utils.SkillHandleUtils;
import net.iaround.utils.eventbus.SkillOpenEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * Class: 技能升级弹框
 * Author：gh
 * Date: 2017/10/16 18:10
 * Email：jt_gaohang@163.com
 */
public class SkillUpdateDailog extends Dialog implements DialogInterface.OnKeyListener {

    private Context mContext;
    private SkillItemOnClick skillItemOnClick;

    private TextView tvDiamond;
    private TextView tvGold;
    private ListView mListView;

    private SkillUpdateAdapter adapter;

    private SkillUpdateEntity skillUpdateEntity;

    // 技能推荐列表
    private List<SkillBean> skillBeanList;

    public SkillUpdateDailog(Context context, SkillUpdateEntity skillUpdateEntity,SkillItemOnClick skillItemOnClick) {
        super(context, R.style.LocatonDialogStyle);
        this.mContext = context;
        this.skillUpdateEntity = skillUpdateEntity;
        this.skillItemOnClick = skillItemOnClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(SkillUpdateDailog.this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载dialog的布局
        setContentView(R.layout.dialog_skill_update);
        //拿到布局控件进行处理

        findViewById(R.id.iv_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvDiamond = (TextView)findViewById(R.id.tv_dialog_skill_update_diamond);
        tvGold = (TextView)findViewById(R.id.tv_dialog_skill_update_gold);
        mListView = (ListView)findViewById(R.id.lv_dialog_skill_update);

        initData();

        //初始化布局的位置
        initLayoutParams();

    }

    /**
     * 初始化数据
     */
    private void initData(){
        if (skillBeanList == null)
            skillBeanList = new ArrayList<>();
        skillBeanList.clear();
        skillBeanList.addAll(skillUpdateEntity.skillUpgrade);

        tvDiamond.setText(""+skillUpdateEntity.diamond);
        tvGold.setText(""+skillUpdateEntity.coin);
        adapter = new SkillUpdateAdapter(mContext,skillBeanList);
        mListView.setAdapter(adapter);
    }

    // 初始化布局的参数
    private void initLayoutParams() {
        // 布局的参数
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1f;
        setCanceledOnTouchOutside(false);
        setOnKeyListener(this);//点击系统返回键不允许消失
        getWindow().setAttributes(params);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
    }

    @Subscribe
    public void onEventMainThread(SkillOpenEvent event) {
        for (SkillBean skillBean : skillBeanList){
            if (String.valueOf(skillBean.ID) == event.getSkillBean().skill.ID){
                skillBean.Status = event.getSkillBean().skill.Status;
                skillBean.Level = event.getSkillBean().skill.Level;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(SkillUpdateDailog.this);
    }

    private class SkillUpdateAdapter extends BaseAdapter {
        private List<SkillBean> mList;//数据源
        private LayoutInflater mInflater;//布局装载器对象

        public final int iconsbg[] = {
                R.drawable.skill_background_juhuacan,
                R.drawable.skill_background_dameme,
                R.drawable.skill_background_jinguzhou,
                R.drawable.skill_background_wuyingjiao,
                R.drawable.skill_background_baohufei
        };

        public SkillUpdateAdapter(Context context, List<SkillBean> list) {
            mList = list;
            mInflater = LayoutInflater.from(context);
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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.dialog_item_skill_update, null);

                holder.ctl = (LinearLayout) convertView.findViewById(R.id.ll_item_skill_update);
                holder.skill_icon = (ImageView) convertView.findViewById(R.id.iv_item_skill_update_icon);
                holder.iv_skill_first = (ImageView) convertView.findViewById(R.id.iv_item_skill_update_first);
                holder.skill_name = (TextView) convertView.findViewById(R.id.tv_item_skill_update_name);
                holder.skill_desc = (TextView) convertView.findViewById(R.id.tv_item_skill_update_desc);
                holder.btn_update = (TextView) convertView.findViewById(R.id.btn_skill_update);

                convertView.setTag(holder);
            }else{//如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
                holder = (ViewHolder) convertView.getTag();
            }

            SkillBean skillBean = mList.get(position);
            SkillHandleUtils.setFirstFrameImage(BaseApplication.appContext, skillBean.Gif, holder.skill_icon);

            int starSelectedDrawablebg = iconsbg[skillBean.ID - 1];
            holder.iv_skill_first.setBackgroundResource(starSelectedDrawablebg);
            holder.skill_name.setText(CommonFunction.getLangText(skillBean.Name));

            holder.skill_desc.setText(CommonFunction.getLangText(skillBean.Desc));
            holder.btn_update.setText(skillBean.Status == 1 ?  BaseApplication.appContext.getString(R.string.update) : BaseApplication.appContext.getString(R.string.setting_notice_receive_new_msg_open));
            holder.btn_update.setBackgroundResource(skillBean.Status > 0 ? R.drawable.touch_bg_red : R.drawable.touch_bg_gray);
            holder.btn_update.setOnClickListener(onClickListener);
            holder.btn_update.setTag(R.id.btn_skill_update,skillBean);
            holder.ctl.setOnClickListener(onClickListener);
            holder.ctl.setTag(R.id.btn_skill_update,skillBean);

            return convertView;
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkillBean skillBean = (SkillBean) v.getTag(R.id.btn_skill_update);
                if (skillBean.Status == 0){
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.come_as)+ skillBean.NeedUserLevel +BaseApplication.appContext.getString(R.string.open_skill_level));
                    return;
                }
                if (skillItemOnClick != null){
                    skillItemOnClick.onItemClick(skillBean);
                }
            }
        };

        // ViewHolder用于缓存控件，三个属性分别对应item布局文件的三个控件
        class ViewHolder{
            ImageView skill_icon;
            ImageView iv_skill_first;
            TextView skill_name;
            TextView skill_desc;
            TextView btn_update;
            LinearLayout ctl;
        }
    }

    public interface SkillItemOnClick{
        void onItemClick(SkillBean skillBean);
    }

}
