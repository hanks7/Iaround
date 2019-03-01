package net.iaround.ui.skill.skilllist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseFragment;
import net.iaround.model.skill.EventSkillLevel;
import net.iaround.model.skill.SkillBean;
import net.iaround.model.skill.SkillBeanBase;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.ui.skill.RecycleItemClickListener;
import net.iaround.ui.skill.skilldetail.SkillDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：zx on 2017/8/9 14:59
 */
public class SkillListFragment extends MvpBaseFragment<SkillListContract.Presenter> implements SkillListContract.View, RecycleItemClickListener<SkillBean> {

    @BindView(R.id.skill_recyclerView) RecyclerView skill_recyclerView;
    @BindView(R.id.tv_title) TextView tv_title;

    private SkillListAdapter adapter;
    private int selectedPosition = 0;

    public static SkillListFragment getInstance(){
        return new SkillListFragment();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(SkillListFragment.this);
        init();
        mPresenter.getSkillList();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_skill_list;
    }

    @Override
    public void setPresenter(SkillListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateListView(SkillBeanBase skillBeanBase) {
        if (null != skillBeanBase){
            adapter.updateList(skillBeanBase.list);
        }
    }

    @Override
    public void updateSkillOpenStatus(SkillOpenBean.SkillOpenItem skill) {
        if (null != skill){
            adapter.updatePositionView(selectedPosition, skill);
        }
    }

    @OnClick({R.id.fl_back})
    public void onViewClicked(View view){
        if (isActive()){
            mActivity.finish();
        }
    }

    @Override
    public void onItemClick(int position, SkillBean itemBean) {
        if (null != itemBean){
            selectedPosition = position;
            switch (itemBean.Status){
                case 0:

                    break;
                case 1://升级
                    Intent intent = new Intent(getActivity(), SkillDetailActivity.class);
                    intent.putExtra("skillId", String.valueOf(itemBean.ID));
                    startActivity(intent);
                    break;
                case 2:
                    mPresenter.openSkill(String.valueOf(itemBean.ID));
                    break;
            }

        }
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

    private void init() {
        tv_title.setText(R.string.my_skill);
        adapter = new SkillListAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(BaseApplication.appContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        skill_recyclerView.setLayoutManager(manager);
        skill_recyclerView.setHasFixedSize(true);
        skill_recyclerView.setAdapter(adapter);
    }

    /**
     * 处理详细技能升级后的等级
     * @param result
     */
    @Subscribe
    public void receivePropsResult(EventSkillLevel result){
        int skillLevel = result.getSkillLevel();
        if (null == adapter){
            return;
        }
        adapter.updatePositionLevel(selectedPosition, skillLevel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SkillListFragment.this);
    }

}
