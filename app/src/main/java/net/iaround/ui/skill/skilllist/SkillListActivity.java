package net.iaround.ui.skill.skilllist;

import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseActivity;
import net.iaround.manager.mvpbase.MvpBaseFragment;

public class SkillListActivity extends MvpBaseActivity {

    private SkillListFragment skillListFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skill_list;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.skill_list_fragment;
    }

    @Override
    protected MvpBaseFragment getCurrentFragment() {
        skillListFragment = SkillListFragment.getInstance();
        return skillListFragment;
    }

    @Override
    protected void initPresenterInstance() {
        new SkillListPresenter(skillListFragment);
    }

}
