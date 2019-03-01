package net.iaround.ui.skill.skilllist;

import net.iaround.BaseApplication;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;
import net.iaround.data.responsitory.SkillResponsitory;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.tools.GsonUtil;
import net.iaround.model.skill.SkillBeanBase;

/**
 * 作者：zx on 2017/8/9 14:59
 */
public class SkillListPresenter extends SkillListContract.Presenter{
    private SkillDataSource skillResponsitory;
    private SkillListContract.View mView;

    public SkillListPresenter(SkillListContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        skillResponsitory = new SkillResponsitory();
    }


    @Override
    public void getSkillList() {
        mView.showLoading();
        addFlag(skillResponsitory.getSkillList(new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                SkillBeanBase skillBeanBase = GsonUtil.getInstance().getServerBean(result, SkillBeanBase.class);
                mView.updateListView(skillBeanBase);
            }

            @Override
            public void onGeneralError(int e, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
            }
        }));
    }

    @Override
    public void openSkill(String skillId) {
        addFlag(skillResponsitory.openSkill(skillId, new HttpCallBack(){

            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                SkillOpenBean skillOpenBean = GsonUtil.getInstance().getServerBean(result, SkillOpenBean.class);
                mView.updateSkillOpenStatus(skillOpenBean.skill);
            }

            @Override
            public void onGeneralError(int e, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
            }
        }));
    }


}
