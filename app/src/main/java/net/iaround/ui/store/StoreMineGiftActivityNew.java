package net.iaround.ui.store;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.chatbar.ChatBarBackpackBean;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.DownloadNewVersionTask;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.UserVIPActivity;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;
import java.util.List;

public class StoreMineGiftActivityNew extends SuperActivity implements View.OnClickListener,HttpCallBack{


    /**空布局*/
    private TextView empty_view;
    /**加载框*/
    private Dialog mProgressDialog;
    /**礼物RecyclerView*/
    private RecyclerView rvGift;
    /**礼物RecyclerView的适配器**/
    private StoreMineGiftAdapter storeMineGiftAdapter;
    private MyGridView giftGridView;
//    private GiftGridViewAdapter giftGridAdapter;
//    private PullToRefreshScrollView mPullScrollView;

    //标题栏
    private ImageView mTitleBack;
    private TextView mTitleName;
    private TextView mTitleRight;
    private FrameLayout flLeft;
    private TextView tvPrivageCount;
    /**礼物集合*/
    private List<ChatBarBackpackBean.ListBean> giftBagBeans;
    private List<ChatBarBackpackBean.ListBean> listBeens;
    /**请求背包礼物的flag*/
    private long getBagGiftFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_mine_activity_new);

        giftBagBeans = new ArrayList<>();
        listBeens = new ArrayList<>();
        initView();
        initData();
        initListener();
    }
    private void initView()
    {
        mTitleName = (TextView) findViewById(R.id.tv_title);
        mTitleBack = (ImageView) findViewById(R.id.iv_left);
        mTitleRight = (TextView) findViewById(R.id.tv_right);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        tvPrivageCount = (TextView) findViewById(R.id.minegift_textview);

        mTitleBack.setVisibility(View.VISIBLE);
        mTitleRight.setVisibility(View.VISIBLE);

        mTitleName.setText(R.string.private_gift);
        mTitleBack.setImageResource(R.drawable.title_back);
        mTitleRight.setText(getResources().getString(R.string.vip_recharge));
        empty_view = (TextView) findViewById(R.id.empty_view);

        rvGift = (RecyclerView) findViewById(R.id.rv_gift);

        String str = String.format(getResources().getString(R.string.iaround_mine_or_other_private_gift_toatal), 0);
        tvPrivageCount.setText(str);
    }
    private void initData()
    {
        GridLayoutManager manager = new GridLayoutManager(this,2);
        storeMineGiftAdapter = new StoreMineGiftAdapter(this, giftBagBeans, new StoreMineGiftAdapter.GiftNumCallback() {
            @Override
            public void getGiftNum(int allGiftNum) {
                if (allGiftNum > 0) {
                    String str = String.format(getResources().getString(R.string.iaround_mine_or_other_private_gift_toatal), allGiftNum);
                    tvPrivageCount.setText(str);
                }else
                {
                    String str = String.format(getResources().getString(R.string.iaround_mine_or_other_private_gift_toatal), 0);
                    tvPrivageCount.setText(str);
                }

            }
        });
        rvGift.setAdapter(storeMineGiftAdapter);
        rvGift.setLayoutManager(manager);
        reqBagGift();
    }
    private void initListener()
    {
        mTitleBack.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        mTitleRight.setOnClickListener(this);
    }

    /**
     * 请求礼物接口
     */
    private void reqBagGift()
    {
        getBagGiftFlag = GroupHttpProtocol.getBagGiftData(StoreMineGiftActivityNew.this,this);
    }
    @Override
    public void onClick(View v) {
        if (v == mTitleBack || v == flLeft)
        {
            finish();
        }else if (v == mTitleRight)
        {
            Intent vipIntent = new Intent(StoreMineGiftActivityNew.this, UserVIPActivity.class);
            startActivity(vipIntent);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        if (flag == getBagGiftFlag)
        {
            if (result != null)
            {
                ChatBarBackpackBean bean = GsonUtil.getInstance().getServerBean(result, ChatBarBackpackBean.class);
                handleBagGiftData(bean,result);
                storeMineGiftAdapter.refreshGiftData(giftBagBeans);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        ErrorCode.toastError(this,e);
    }
    /**
     * 获取礼物数据
     * @param bean
     * @param result
     */
    private void handleBagGiftData(ChatBarBackpackBean bean,String result)
    {
        if (bean != null)
        {
            if (bean.isSuccess())
            {
                giftBagBeans.clear();
                listBeens = bean.getList();
                if (listBeens != null && listBeens.size() > 0)
                {
                    for (int i = 0; i < listBeens.size(); i++)
                    {
                        giftBagBeans.add(listBeens.get(i));
                    }
                }
            }
            if (bean.status == -100)
            {
                ErrorCode.showError(mContext, result);
                //弹出升级提示框
                if (bean.error == 8000)
                {
                    DialogUtil.showOKCancelDialogFlatUpdate(mContext, mContext.getString(R.string.dialog_title), mContext.getString(R.string.group_chat_flat_update_title_xontent), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // 下载
                            DownloadNewVersionTask.getInstance(StoreMineGiftActivityNew.this, true).reset();
                            DownloadNewVersionTask.getInstance(StoreMineGiftActivityNew.this, true)
                                    .execute(Constants.CHAT_BAR_UPDATE_DIALOB_URL);

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }

            // TODO: 2017/7/31 弹出升级提示框
        }
    }
}
