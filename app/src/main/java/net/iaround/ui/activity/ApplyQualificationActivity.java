package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.ApplyQualificationBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/3.
 * 申请资质（入驻）界面
 */

public class ApplyQualificationActivity extends TitleActivity implements HttpCallBack {

    private PullToRefreshListView mPtrlvApply;
    private ApplyQuaAdapter mApplyQuaAdapter;
    private ArrayList<ApplyQualificationBean.GameItem> mAppliedList = new ArrayList<>();
    private ArrayList<ApplyQualificationBean.NotApplyList> mNotApplyList = new ArrayList<>();
    private boolean isAgreeRegister = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_C(R.string.user_fragment_apply_qualification);
        setContent(R.layout.activity_apply_qualification);
        mPtrlvApply = findView(R.id.ptrlv_apply);
//        mLlRegister = (LinearLayout) findViewById(R.id.ll_register);
//        mTvRegister = (TextView) findViewById(R.id.tv_register);
//        mIvRegister = (ImageView) findViewById(R.id.iv_register);
        initListener();
        mPtrlvApply.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mApplyQuaAdapter = new ApplyQuaAdapter(mAppliedList, mNotApplyList);
        mPtrlvApply.setAdapter(mApplyQuaAdapter);
        GameChatHttpProtocol.applyQualification(this, this);
        showWaitDialog();
        mPtrlvApply.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                GameChatHttpProtocol.applyQualification(ApplyQualificationActivity.this, ApplyQualificationActivity.this);
            }
        });
    }

    private void initListener() {
//        mTvRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        mIvRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isAgreeRegister) {
//                    isAgreeRegister = false;
//                    ((ImageView) v).setImageResource(R.drawable.btn_unselect);
//                } else {
//                    isAgreeRegister = true;
//                    ((ImageView) v).setImageResource(R.drawable.btn_select);
//                }
//            }
//        });

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        mPtrlvApply.onRefreshComplete();
        ApplyQualificationBean bean = GsonUtil.getInstance().getServerBean(result, ApplyQualificationBean.class);
        if (bean != null) {
            if (bean.appliedList != null && bean.appliedList.size() > 0) {
                getTvRight().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                setTitle_R(0, getString(R.string.order_to_set), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ApplyQualificationActivity.this, OrderToSetActivity.class);
                        startActivity(intent);
                    }
                });
                mAppliedList.clear();
                mAppliedList.addAll(bean.appliedList);
                getTvRight().setVisibility(View.VISIBLE);
            } else {
                getTvRight().setVisibility(View.GONE);
            }
            if (bean.notApplyList != null) {
                mNotApplyList.clear();
                mNotApplyList.addAll(bean.notApplyList);
            }
        }
        mApplyQuaAdapter.notifyDataSetChanged();


    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        mPtrlvApply.onRefreshComplete();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }

    private AdapterView.OnItemClickListener itmeClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (isAgreeRegister) {
                ApplyQualificationBean.GameItem gameItem = (ApplyQualificationBean.GameItem) parent.getAdapter().getItem(position);
                if (gameItem != null) {
                    Intent intent = new Intent(ApplyQualificationActivity.this, QualificationActivity.class);
                    intent.putExtra("GameItem", gameItem);
                    startActivity(intent);
                }
            } else {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.please_agree_register_tip);
            }
        }
    };

    class ApplyQuaAdapter extends BaseAdapter {

        ArrayList<ApplyQualificationBean.GameItem> appliedList;
        ArrayList<ApplyQualificationBean.NotApplyList> notApplyList;


        public ApplyQuaAdapter(ArrayList<ApplyQualificationBean.GameItem> appliedList, ArrayList<ApplyQualificationBean.NotApplyList> notApplyList) {
            this.appliedList = appliedList;
            this.notApplyList = notApplyList;

        }

        @Override
        public int getCount() {
            if (notApplyList != null) {
                return notApplyList.size() + 1;
            }
            if (appliedList != null) {
                return 1;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ApplyQualificationActivity.this).inflate(R.layout.item_apply_qualification, null);
                viewHolder = new ViewHolder();
                viewHolder.placeholder = convertView.findViewById(R.id.placeholder);
                viewHolder.mgv_my_qua = (MyGridView) convertView.findViewById(R.id.mgv_my_qua);
                viewHolder.tv_my_qua_title = (TextView) convertView.findViewById(R.id.tv_my_qua_title);
                viewHolder.tv_my_qua_subtitle = (TextView) convertView.findViewById(R.id.tv_my_qua_subtitle);
                viewHolder.tv_my_qua_hint = (TextView) convertView.findViewById(R.id.tv_my_qua_hint);
                viewHolder.ll_register = (LinearLayout) convertView.findViewById(R.id.ll_register);
                viewHolder.tv_register = (TextView) convertView.findViewById(R.id.tv_register);
                viewHolder.iv_register = (ImageView) convertView.findViewById(R.id.iv_register);
                viewHolder.applyQuaItemAdapter = new ApplyQuaItemAdapter();
                viewHolder.mgv_my_qua.setAdapter(viewHolder.applyQuaItemAdapter);
                viewHolder.mgv_my_qua.setOnItemClickListener(itmeClickListener);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                viewHolder.placeholder.setVisibility(View.GONE);
                viewHolder.ll_register.setVisibility(View.GONE);
                viewHolder.tv_my_qua_subtitle.setVisibility(View.GONE);
                viewHolder.tv_my_qua_title.setVisibility(View.VISIBLE);
                viewHolder.tv_my_qua_title.setText(R.string.my_qualification);
                if (appliedList != null && appliedList.size() > 0) {
                    viewHolder.mgv_my_qua.setVisibility(View.VISIBLE);
                    viewHolder.applyQuaItemAdapter.updateData(appliedList);
                    viewHolder.tv_my_qua_hint.setVisibility(View.GONE);
                } else {
                    viewHolder.tv_my_qua_hint.setVisibility(View.VISIBLE);
                    viewHolder.mgv_my_qua.setVisibility(View.GONE);
                }
            } else if (position == 1) {
                viewHolder.ll_register.setVisibility(View.GONE);
                viewHolder.placeholder.setVisibility(View.VISIBLE);
                viewHolder.tv_my_qua_title.setVisibility(View.VISIBLE);
                viewHolder.tv_my_qua_title.setText(R.string.not_open_qualification);
                viewHolder.tv_my_qua_hint.setVisibility(View.GONE);
                if (notApplyList != null && notApplyList.get(position - 1) != null) {
                    if (notApplyList.get(position - 1).type != null) {
                        viewHolder.tv_my_qua_subtitle.setVisibility(View.VISIBLE);
                        viewHolder.tv_my_qua_subtitle.setText(notApplyList.get(position - 1).type);
                    }

                    if (notApplyList.get(position - 1).gameList != null) {
                        viewHolder.mgv_my_qua.setVisibility(View.VISIBLE);
                        viewHolder.applyQuaItemAdapter.updateData(notApplyList.get(position - 1).gameList);
                    }
                }
            } else {
                viewHolder.placeholder.setVisibility(View.GONE);
                viewHolder.placeholder.setVisibility(View.GONE);
                viewHolder.tv_my_qua_title.setVisibility(View.GONE);
                viewHolder.tv_my_qua_hint.setVisibility(View.GONE);
                if (notApplyList != null && notApplyList.get(position - 1) != null) {
                    if (notApplyList.get(position - 1).type != null) {
                        viewHolder.tv_my_qua_subtitle.setVisibility(View.VISIBLE);
                        viewHolder.tv_my_qua_subtitle.setText(notApplyList.get(position - 1).type);
                    }

                    if (notApplyList.get(position - 1).gameList != null) {
                        viewHolder.mgv_my_qua.setVisibility(View.VISIBLE);
                        viewHolder.applyQuaItemAdapter.updateData(notApplyList.get(position - 1).gameList);
                    }
                }
            }
            if (position == getCount() - 1) {
                viewHolder.ll_register.setVisibility(View.VISIBLE);
                viewHolder.tv_register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InnerJump.Jump(mContext,"http://notice.iaround.com/gamchat_orders/agreement.html");
                    }
                });
                viewHolder.iv_register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAgreeRegister) {
                            isAgreeRegister = false;
                            ((ImageView) v).setImageResource(R.drawable.btn_unselect);
                        } else {
                            isAgreeRegister = true;
                            ((ImageView) v).setImageResource(R.drawable.btn_select);
                        }
                    }
                });
            }

            return convertView;
        }

        class ViewHolder {
            public View placeholder;
            public TextView tv_my_qua_title;
            public TextView tv_my_qua_subtitle;
            public TextView tv_my_qua_hint;
            public MyGridView mgv_my_qua;
            public LinearLayout ll_register;
            public ImageView iv_register;
            public TextView tv_register;
            public ApplyQuaItemAdapter applyQuaItemAdapter;
        }
    }

    class ApplyQuaItemAdapter extends BaseAdapter {
        ArrayList<ApplyQualificationBean.GameItem> gameList;

        private void updateData(ArrayList<ApplyQualificationBean.GameItem> gameList) {
            this.gameList = gameList;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (gameList != null) {
                return gameList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (gameList != null) {
                return gameList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv_game_name;
            if (convertView == null) {
                convertView = LayoutInflater.from(ApplyQualificationActivity.this).inflate(R.layout.item_apply_qualification_item, null);
                tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
                convertView.setTag(tv_game_name);
            } else {
                tv_game_name = (TextView) convertView.getTag();
            }
            if (gameList.get(position) != null) {

                tv_game_name.setText(gameList.get(position).gameName);
            }
            return convertView;
        }
    }
}
