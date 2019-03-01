package net.iaround.ui.group.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.im.Me;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.group.GroupScaleType;
import net.iaround.ui.group.bean.CreateGroupConditions;
import net.iaround.ui.group.bean.CreateGroupIntroduction;
import net.iaround.ui.space.more.BindTelphoneForOne;


public class CreateGroupPreviewActivity extends BaseFragmentActivity implements OnClickListener {

    //标题栏
    private ImageView closeText;
    private FrameLayout flLeft;


    //private TextView conditionText;
    private TextView suitGroupText;
    private TextView bigGroupCost;
    private RelativeLayout smallGroupIcon;
    private RelativeLayout bigGroupIcon;
    private Dialog mProgressDialog;
    private Dialog introductionDialog;

    private long GET_GROUP_INTRODUCTION_FLAG;
    private long GET_CREATE_GROUP_CONDITION_FLAG;

    /**
     * 创建大圈的费用
     */
    private int createBigGroupCost;
    /**
     * 创建小圈的费用
     */
    private int createSmallGroupCost;

    private CreateGroupConditions conditions;
    private CreateGroupIntroduction introduction;

    public static int REQUEST_CODE_CREATE_GROUP = 101;

    private final int REQ_BUY_DIAMOND = 10;
    private final int REQ_BIND_PHONE = 11;
    private final int REQ_CREATE_GROUP = 12;
    private HttpCallBack httpCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_preview);

        try {
            initViews();
            setListeners();
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Title: initViews
     * @Description: 初始化界面控件
     */
    private void initViews() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText(getString(R.string.create_group_preview_title));
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        closeText = (ImageView) findViewById(R.id.iv_left);
//        closeText.setText(getString(R.string.dismiss));
        closeText.setVisibility(View.VISIBLE);
        closeText.setImageResource(R.drawable.title_back);

        suitGroupText = (TextView) findViewById(R.id.create_group_tips);
//        suitGroupText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        //conditionText = (TextView)findViewById(R.id.unfit_condition_tips);
        bigGroupCost = (TextView) findViewById(R.id.big_group_text);

        smallGroupIcon = (RelativeLayout) findViewById(R.id.small_group_view);
        bigGroupIcon = (RelativeLayout) findViewById(R.id.big_group_view);
    }

    /**
     * @Title: setListeners
     * @Description: 设置监听器
     */
    private void setListeners() {
        flLeft.setOnClickListener(this);
        closeText.setOnClickListener(this);
        suitGroupText.setOnClickListener(this);
        httpCallBack = new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                hideProgressDialog();

                if (result.contains("\"status\":200")) {
                    if (GET_CREATE_GROUP_CONDITION_FLAG == flag) {
                        conditions = GsonUtil.getInstance().getServerBean(result, CreateGroupConditions.class);
                        refreshMainView();
                    }
                    if (GET_GROUP_INTRODUCTION_FLAG == flag) {
                        introduction = GsonUtil.getInstance().getServerBean(result, CreateGroupIntroduction.class);
                        if (introductionDialog == null) {
                            introductionDialog = DialogUtil.showOKDialog(mContext,
                                    getString(R.string.what_group_suits_me_tips), introduction.desc,
                                    R.layout.dialog_group_introduction_layout, null);
                        }
                        introductionDialog.show();
                    }
                }
            }

            @Override
            public void onGeneralError(int e, long flag) {
                hideProgressDialog();
                if (GET_GROUP_INTRODUCTION_FLAG == flag
                        || GET_CREATE_GROUP_CONDITION_FLAG == flag) {
                    ErrorCode.toastError(mContext, e);
                }
            }
        };
    }

    /**
     * @Title: initData
     * @Description: 初始化数据显示
     */
    private void initData() {
        showProgressDialog();
        GET_CREATE_GROUP_CONDITION_FLAG = GroupHttpProtocol.getCreateGroupCondition_5_3(mContext, httpCallBack);
        if (GET_CREATE_GROUP_CONDITION_FLAG == -1) {
            hideProgressDialog();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(this, "",
                    mActivity.getResources().getString(R.string.please_wait), null);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void refreshMainView() {
        /*if (conditions.bindphone == 0) {
            conditionText.setVisibility(View.VISIBLE);
			conditionText.setText( getString(R.string.create_group_bind_phone_tips) );
		}
		else if (conditions.groupnum < 1) {
			conditionText.setVisibility(View.VISIBLE);
			conditionText.setText( getString(R.string.create_group_quantity_limit_tips) );
		}
		else {
			conditionText.setVisibility(View.GONE);
		}*/

        if (conditions.items != null) {
            for (int i = 0; i < conditions.items.size(); i++) {
                if (conditions.items.get(i).type == GroupScaleType.BIG_GROUP_TYPE) {
                    createBigGroupCost = conditions.items.get(i).cost;
                    bigGroupCost.setText(String.format(getString(R.string.big_group_cost), createBigGroupCost));
                }
                if (conditions.items.get(i).type == GroupScaleType.SMALL_GROUP_TYPE) {
                    createSmallGroupCost = conditions.items.get(i).cost;
                }
            }

            smallGroupIcon.setOnClickListener(this);
            bigGroupIcon.setOnClickListener(this);
        } else {
            CommonFunction.toastMsg(mContext, getString(R.string.no_data));
        }
    }

//    @Override
//    public void onGeneralSuccess(final String result, final long flag) {
//        hideProgressDialog();
//
//        if (result.contains("\"status\":200")) {
//            if (GET_CREATE_GROUP_CONDITION_FLAG == flag) {
//                conditions = GsonUtil.getInstance().getServerBean(result, CreateGroupConditions.class);
//                refreshMainView();
//            }
//            if (GET_GROUP_INTRODUCTION_FLAG == flag) {
//                introduction = GsonUtil.getInstance().getServerBean(result, CreateGroupIntroduction.class);
//                if (introductionDialog == null) {
//                    introductionDialog = DialogUtil.showOKDialog(mContext,
//                            getString(R.string.what_group_suits_me_tips), introduction.desc,
//                            R.layout.dialog_group_introduction_layout, null);
//                }
//                introductionDialog.show();
//            }
//        }
//    }
//
//    public void onGeneralError(final int e, final long flag) {
//        hideProgressDialog();
//        if (GET_GROUP_INTRODUCTION_FLAG == flag
//                || GET_CREATE_GROUP_CONDITION_FLAG == flag) {
//            ErrorCode.toastError(mContext, e);
//        }
//    }

    private void checkCreateSmallGroupCondition() {
        if (conditions == null) {
//			CommonFunction.toastMsg( mContext , mContext.getString( ErrorCode.E_107 ) );
            ErrorCode.toastError(mContext, ErrorCode.E_107);
            return;
        }
        if (conditions.bindphone == 0) {    //判断是否绑定手机号
            DialogUtil.showTowButtonDialog(mContext, "",
                    getString(R.string.create_group_bind_phone_msg),
                    getString(R.string.cancel),
                    getString(R.string.bind_mobile_phone),
                    null, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Me me = Common.getInstance().loginUser;
                            if (me.getHasPwd() == 1) {
                                // 有密码
                                Intent intent = new Intent(mActivity, BindTelphoneForOne.class);
                                intent.putExtra("type", 1);// 需输入密码
                                mActivity.startActivityForResult(intent, REQ_BIND_PHONE);
                            } else {
                                Intent intent = new Intent(mActivity, BindTelphoneForOne.class);
                                intent.putExtra("type", 0);// 需设置密码
                                mActivity.startActivityForResult(intent, REQ_BIND_PHONE);
                            }
                        }
                    });
            return;
        } else if (conditions.groupnum < 1) {//判断剩余可以创建的圈子是否小于1
            DialogUtil.showOKDialog(this, "",
                    getString(R.string.create_group_quantity_limit_msg), null);
            return;
        }

        Intent intent = new Intent(CreateGroupPreviewActivity.this, CreateGroupActivity.class);
        intent.putExtra("group_type", GroupScaleType.SMALL_GROUP_TYPE);
        intent.putExtra("diamond_cost", createSmallGroupCost);
        startActivityForResult(intent, REQ_CREATE_GROUP);
    }

    private void checkCreateBigGroupCondition() {
        if (conditions == null) {
//			CommonFunction.toastMsg( mContext , mContext.getString( ErrorCode.E_107 ) );
            ErrorCode.toastError(mContext, ErrorCode.E_107);
            return;
        }
        if (conditions.bindphone == 0) {    //判断是否绑定手机号
            DialogUtil.showTowButtonDialog(mContext, "",
                    getString(R.string.create_group_bind_phone_msg),
                    getString(R.string.cancel),
                    getString(R.string.bind_mobile_phone),
                    null, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Me me = Common.getInstance().loginUser;
                            if (me.getHasPwd() == 1) {
                                // 有密码
                                Intent intent = new Intent(mActivity, BindTelphoneForOne.class);
                                intent.putExtra("type", 1);// 需输入密码
                                mActivity.startActivityForResult(intent, REQ_BIND_PHONE);
                            } else {
                                Intent intent = new Intent(mActivity, BindTelphoneForOne.class);
                                intent.putExtra("type", 0);// 需设置密码
                                mActivity.startActivityForResult(intent, REQ_BIND_PHONE);
                            }
                        }
                    });
            return;
        } else if (conditions.groupnum < 1) {//判断剩余可以创建的圈子是否小于1
            DialogUtil.showOKDialog(this, "",
                    getString(R.string.create_group_quantity_limit_msg), null);
            return;
        } else if (conditions.diamondnum < createBigGroupCost) {//判断创建大圈钻石余额是否不足
            String msg = String.format(getString(R.string.create_group_money_not_enough_msg), createBigGroupCost);
            DialogUtil.showTowButtonDialog(this, "", msg,
                    getString(R.string.cancel),
                    getString(R.string.buy_diamonds),
                    null, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                           FragmentPayDiamond.jumpPayDiamondActivityForResult(mContext, REQ_BUY_DIAMOND, DataTag.VIEW_group_createGroup);//jiqiang
                            Intent intent = new Intent();
                            intent.setClass(CreateGroupPreviewActivity.this, MyWalletActivity.class);
//                            intent.putExtra(Constants.USER_WALLET_PAGE_TYPE, Constants.USER_WALLET_PAGE_WALLET);
                            startActivity(intent);
                        }
                    });
            return;
        }

        Intent intent = new Intent(CreateGroupPreviewActivity.this, CreateGroupActivity.class);
        intent.putExtra("group_type", GroupScaleType.BIG_GROUP_TYPE);
        intent.putExtra("diamond_cost", createBigGroupCost);
        startActivityForResult(intent, REQ_CREATE_GROUP);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.small_group_view:
                checkCreateSmallGroupCondition();
                break;
            case R.id.big_group_view:
                checkCreateBigGroupCondition();
                break;
            case R.id.create_group_tips:
            /*if ( !ConnectorManage.getInstance( mContext ).CheckNetwork( mContext ) )// 网络是连接
            {
				CommonFunction.toastMsg( mContext , mContext.getString( R.string.e_101 ) );
				return;
			}
			if (introduction == null) {
				showProgressDialog();
				GET_GROUP_INTRODUCTION_FLAG = GroupHttpProtocol.getCreateGroupIntroduction(mContext, this);
			}
			else {
				if (introductionDialog == null) {
					introductionDialog = DialogUtil.showOKDialog(mContext,
							getString(R.string.what_group_suits_me_tips), introduction.desc,
							R.layout.dialog_group_introduction_layout, null);
				}
				introductionDialog.show();
			}*/
//                if (introductionDialog == null) {
//                    introductionDialog = DialogUtil.showOKDialog(mContext,
//                            getString(R.string.what_group_suits_me_tips),
//                            getString(R.string.what_group_suits_me_msg),
//                            R.layout.dialog_group_introduction_layout, null);
//                }
//                introductionDialog.show();

                DialogUtil.showOneButtonDialog(this, getString(R.string.what_group_suits_me_tips), getString(R.string.what_group_suits_me_msg), getString(R.string.pay_alipay_Ensure), new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_BUY_DIAMOND
                || requestCode == REQ_BIND_PHONE
                ) {
            initData();
        } else if (requestCode == REQ_CREATE_GROUP
                && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
