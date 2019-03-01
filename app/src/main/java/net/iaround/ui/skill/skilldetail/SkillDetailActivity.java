package net.iaround.ui.skill.skilldetail;

import android.os.Bundle;

import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseActivity;
import net.iaround.manager.mvpbase.MvpBaseFragment;

public class SkillDetailActivity extends MvpBaseActivity {

    private SkillDetailFragment detailFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skill_detail;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.skill_detail_fragment;
    }

    @Override
    protected MvpBaseFragment getCurrentFragment() {
        detailFragment = SkillDetailFragment.getInstance(getBundles());
        return detailFragment;
    }

    private Bundle getBundles() {
        return getIntent().getExtras();
    }

    @Override
    protected void initPresenterInstance() {
        new SkillDetailPresenter(detailFragment);
    }
}
