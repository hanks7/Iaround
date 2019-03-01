package net.iaround.ui.skill.skillpropshop;

import net.iaround.R;
import net.iaround.manager.mvpbase.MvpBaseActivity;
import net.iaround.manager.mvpbase.MvpBaseFragment;


public class SkillPropsShopActivity extends MvpBaseActivity {

    private SkillPropsShopFragment propsShopFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_skill_props_shop;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.moon_fragment;
    }

    @Override
    protected MvpBaseFragment getCurrentFragment() {
        propsShopFragment = new SkillPropsShopFragment();
        return propsShopFragment;
    }

    @Override
    protected void initPresenterInstance() {
        new SkillPropsShopPresenter(propsShopFragment);
    }
}
