package net.iaround.ui.skill.skilladdition;

import net.iaround.conf.Constant;
import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;
import net.iaround.data.responsitory.SkillResponsitory;
import net.iaround.model.skill.SkillAdditionBean;
import net.iaround.tools.GsonUtil;

/**
 * 作者：zx on 2017/8/26 11:35
 */
public class SkillAdditionPresenter extends SkillAdditionContract.Presenter{

    private SkillAdditionContract.View mView;
    private SkillDataSource mSkillResponsitory;

    public SkillAdditionPresenter(SkillAdditionContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        mSkillResponsitory = new SkillResponsitory();
    }


    @Override
    public void getSkillAddition() {
        mView.showLoading();
        addFlag(mSkillResponsitory.getSkillAddition(new HttpCallBack(){

            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                SkillAdditionBean skillAddition = GsonUtil.getInstance().getServerBean(result, SkillAdditionBean.class);
                mView.showAdditionInfo(skillAddition);
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
