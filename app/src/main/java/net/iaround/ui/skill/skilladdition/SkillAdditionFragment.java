package net.iaround.ui.skill.skilladdition;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseFragment;
import net.iaround.model.skill.SkillAdditionBean;
import net.iaround.ui.skill.FullyGridLayoutManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：zx on 2017/8/26 11:41
 */
public class SkillAdditionFragment extends MvpBaseFragment<SkillAdditionContract.Presenter> implements SkillAdditionContract.View {

    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_gold_desc) TextView tvGoldDesc;
    @BindView(R.id.tv_expend_desc) TextView tvExpendDesc;
    @BindView(R.id.addition_recyclerView) RecyclerView additionRecyclerView;
    @BindView(R.id.ly_success_rank) LinearLayout successLayout;
    @BindView(R.id.addition_success_recyclerView) RecyclerView additionSuccessRecyclerView;

    private SkillAddtionAdapter adapter;
    private SkillAddtionAdapter successAdapter;

    public static SkillAdditionFragment getInstance() {
        SkillAdditionFragment fragment = new SkillAdditionFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_skill_addition;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        init();
        mPresenter.getSkillAddition();
    }

    private void init() {
        tvTitle.setText(R.string.skill_addtion_title);

        FullyGridLayoutManager manager = new FullyGridLayoutManager(BaseApplication.appContext, 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        manager.setSmoothScrollbarEnabled(true);
        manager.canScrollVertically();

        adapter = new SkillAddtionAdapter();
        additionRecyclerView.setHasFixedSize(true);
        additionRecyclerView.setLayoutManager(manager);
        additionRecyclerView.setAdapter(adapter);

        successAdapter = new SkillAddtionAdapter();
        additionSuccessRecyclerView.setHasFixedSize(true);
        additionSuccessRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        additionSuccessRecyclerView.setAdapter(successAdapter);
    }


    @Override
    public void showAdditionInfo(SkillAdditionBean skillAddition) {
        if (null != skillAddition){
            adapter.updateList(skillAddition.ranking);

            //升级成功率可控
            if (skillAddition.successRanking != null && skillAddition.successRanking.size() > 0){
                successAdapter.updateList(1,skillAddition.successRanking);
                successLayout.setVisibility(View.VISIBLE);
            }

            String currencyRate = getString(R.string.update_skill_expend_diamond_rate) + "<font color='#fc2452'>" + "+" + skillAddition.currencyRate + "%" +"</font>";
            tvGoldDesc.setText(Html.fromHtml(currencyRate));
            String expendRate = getString(R.string.update_skill_expend_props_rate) + "<font color='#fc2452'>" + "+" + skillAddition.expendRate + "%" +"</font>";
            tvExpendDesc.setText(Html.fromHtml(expendRate));
        }
    }

    @OnClick({R.id.fl_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                mActivity.finish();
                break;
        }
    }

    @Override
    public void setPresenter(SkillAdditionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        showWaitDialog();
    }

    @Override
    public void hideLoading() {
        hideWaitDialog();
    }

    @Override
    public boolean isActive() {
        return isLive();
    }

}