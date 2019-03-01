package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.glide.GlideUtil;

/**
 * Class: 公共的广告
 * Author：gh
 * Date: 2017/8/21 15:50
 * Email：jt_gaohang@163.com
 */
public class CustomAdDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private ImageView smallAdIv;
    private ImageView largeAdIv;

    // 广告点击
    private SureClickListener onClickListener;
    // type 0 = 小图，1 = 大图
    private int type;
    // 广告展示内容
    private String url;

    public CustomAdDialog(Context context,int type,String url,SureClickListener onClickListener) {
        // 更改样式,把背景设置为透明的
        super(context, R.style.LocatonDialogStyle);
        this.context = context;
        this.type = type;
        this.url = url;
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载dialog的布局
        setContentView(R.layout.dialog_custom_ad_normal);
        //拿到布局控件进行处理
        smallAdIv = (ImageView) findViewById(R.id.iv_dialog_custom_ad_normal_small_bg);
        largeAdIv = (ImageView) findViewById(R.id.iv_dialog_custom_ad_normal_large_bg);
        ImageView close = (ImageView) findViewById(R.id.iv_dialog_custom_ad_normal_close);

        if (type == 0){
            smallAdIv.setVisibility(View.VISIBLE);
            largeAdIv.setVisibility(View.GONE);
            GlideUtil.loadImage(BaseApplication.appContext,url,smallAdIv);
            smallAdIv.setOnClickListener(this);
        }else {
            largeAdIv.setVisibility(View.VISIBLE);
            smallAdIv.setVisibility(View.GONE);
            GlideUtil.loadImage(BaseApplication.appContext,url,largeAdIv);
            largeAdIv.setOnClickListener(this);
        }

        close.setOnClickListener(this);
        //初始化布局的位置
        initLayoutParams();
    }

    // 初始化布局的参数
    private void initLayoutParams() {
        // 布局的参数
        LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1f;
        setCanceledOnTouchOutside(false);
        getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_dialog_custom_ad_normal_small_bg:
            case R.id.iv_dialog_custom_ad_normal_large_bg:
                if (onClickListener != null){
                    onClickListener.onClick();
                }
                break;
        }
        dismiss();
    }

    public interface SureClickListener{
        void onClick();
    }

}
