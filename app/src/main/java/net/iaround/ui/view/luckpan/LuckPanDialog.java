package net.iaround.ui.view.luckpan;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import net.iaround.R;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LuckPanProtocol;
import net.iaround.model.im.LuckPanBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CustomDialog;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;

/* 抽奖规则
* 1）每天免费抽奖10次，免费次数用完后提示抽奖需要20钻石
* 2) 钻石抽奖，每次消耗20钻石，每天10次，超过次数提示明天再来抽奖，钻石不够提示充值
*
* */
public class LuckPanDialog extends Dialog implements HttpCallBack {
    private static final String TAG = "LuckPanDialog";
    private static boolean sShowingLuckPan = false; //正在显示抽奖界面
    private static final int LUCKPAN_FREE_CHANCE = 10; //每天免费抽奖的次数 和 每天钻石抽奖的次数
    private Context mContext;
    private OnLuckBingoListener mListener;  //抽奖回调接口
    private LuckPanLayout2 mLuckPanLayout;
    private boolean mLucking = false; //抽奖中标记
    private LuckPanBean mLuckPanbean = null;
    private int[] mAmount = null;


    //抽奖回调接口
    public interface OnLuckBingoListener{
        void onLuckBingo(int propType);
    }

    protected LuckPanDialog(Context context, LuckPanBean bean, int[] amount,OnLuckBingoListener listener) {
        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;
        this.mListener = listener;
        this.mLucking = false;
        this.mLuckPanbean = bean;
        this.mAmount = amount;

    }

    protected LuckPanDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        this.mLucking = false;
    }

    /* free 用户剩余的每日免费抽奖次数
    * diamond 用户剩余的每日钻石抽奖次数
    * */
    public static void showLuckPanDialog(Context mContext,int free, int diamond,int[] amount,OnLuckBingoListener listener)
    {
        if(sShowingLuckPan == true){
            return;
        }
        sShowingLuckPan = true;
        LuckPanBean bean = new LuckPanBean(free, diamond);
        LuckPanDialog customChatBarDialog = new LuckPanDialog(mContext,bean,amount,listener);
        customChatBarDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sShowingLuckPan = false;
            }
        });
        customChatBarDialog.show();
    }

    public void setLuckBingoListener(OnLuckBingoListener listener){
        this.mListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbar_luckpan);

        initView();

    }

    private void initView() {

        Window window = this.getWindow();
        window.setGravity(Gravity.TOP);

        //底部弹出样式
        //window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setCancelable(true); //BACK 按钮
        setCanceledOnTouchOutside(false);

        mLuckPanLayout = (LuckPanLayout2) findViewById(R.id.lottery_layout);
        //设置奖品数量
        mLuckPanLayout.setAmount(mAmount);
        //点击开始抽奖按钮
        mLuckPanLayout.setStartListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先向服务器抽奖接口请求中奖项，再在网络请求成功回调通知里转到中奖项
                //mLuckPanLayout.rotate(6);
                luckPanStart();
            }
        });

        //点击关闭按钮
        mLuckPanLayout.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLucking == true){
                    return;
                }
                LuckPanDialog.this.dismiss();
            }
        });

        //点击规则按钮
        mLuckPanLayout.setRuleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLucking == true){
                    return;
                }
                DialogUtil.showOneButtonDialog(mContext,mContext.getString(R.string.luckpan_rule),mContext.getString(R.string.luckpan_rule_1)+ "\n" + mContext.getString(R.string.luckpan_rule_2),mContext.getString(R.string.group_create_i_know),null );
            }
        });

    }

    /***
     *  开始抽奖
     */
    private void luckPanStart() {
        if(mLucking == true){
            return;
        }

        if(mLuckPanbean!=null && mLuckPanbean.getCount()!=null){
            mLucking = true;
            if(mLuckPanbean.getCount().getFree() == 0 && mLuckPanbean.getCount().getDiamond() ==0) {
                //今天次数已经用完
                CommonFunction.showToast(mContext, mContext.getResources().getString(R.string.luckpan_changce_zero), 0);
                mLucking = false;
                return;
            }
            if(mLuckPanbean.getCount().getFree() == 0 && mLuckPanbean.getCount().getDiamond() == LUCKPAN_FREE_CHANCE) {
                //每天钻石抽奖的第一次要弹出提示要花费钻石
                CustomDialog dialog = new CustomDialog( mContext , mContext.getString(R.string.prompt) , mContext.getString(R.string.luckpan_free_chance_zero) ,
                        mContext.getString(R.string.cancel),
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                mLucking = false;
                            }
                        },  mContext.getString(R.string.ok) ,
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                //抽奖请求
                                sendRequest();
                            }
                        } );
                dialog.show();
                return;
            }else{
                //抽奖请求
                sendRequest();
            }
        }
    }

    /***
     *  发送抽奖请求给服务器
     */
    private void sendRequest(){
        LuckPanProtocol.getUserLuckPanInfo(mContext,LuckPanProtocol.sLuckPanProtocolUrl, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (result != null) {
            LuckPanBean bean = GsonUtil.getInstance().getServerBean(result,LuckPanBean.class);
            if (null!=bean && bean.isSuccess()) {
                if(bean.getMsg()!=null && bean.getMsg().equals("ok")){
                    if(bean.getPropId()>=0 && bean.getPropId()<=9) {
                        mLuckPanbean = bean;
                        mLuckPanLayout.rotate(bean.getPropId());
                        if(mListener!=null){
                            mListener.onLuckBingo(mLuckPanbean.getPropType());
                        }
                    }
                }else{
                    CommonFunction.showToast(mContext, mContext.getResources().getString(R.string.luckpan_service_fail), 0);
                }
            } else if(null!=bean){
                if(32767 == bean.error){
                    //ErrorCode.showError(mContext, result);
                    CommonFunction.showToast(mContext, mContext.getResources().getString(R.string.user_have_no_chatbar), 0);
                }else if(32768 == bean.error){
                    CommonFunction.showToast(mContext, mContext.getResources().getString(R.string.luckpan_changce_zero), 0);
                }else if(5930 == bean.error){
                    CommonFunction.showToast(mContext, mContext.getResources().getString(R.string.luckpan_have_no_diamond), 0);
                }else{
                    CommonFunction.showToast(mContext,mContext.getResources().getString(R.string.luckpan_service_fail), 0);
                }
            }else{
                CommonFunction.showToast(mContext,mContext.getResources().getString(R.string.luckpan_service_fail), 0);
            }
        }else{
            CommonFunction.showToast(mContext,mContext.getResources().getString(R.string.e_107), 0);
        }
        mLucking = false;
    }

    @Override
    public void onGeneralError(int e, long flag) {
        CommonFunction.showToast(mContext,mContext.getResources().getString(R.string.e_107), 0);
        mLucking = false;
    }


}
