package net.iaround.ui.skill.skilladdition;

import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseActivity;
import net.iaround.manager.mvpbase.MvpBaseFragment;

public class SkillAdditionActivity extends MvpBaseActivity {

    private SkillAdditionFragment mFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skill_addition;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.skill_addition_fragment;
    }

    @Override
    protected MvpBaseFragment getCurrentFragment() {
        mFragment = SkillAdditionFragment.getInstance();
        return mFragment;
    }

    @Override
    protected void initPresenterInstance() {
        new SkillAdditionPresenter(mFragment);
    }

}
