package net.iaround.ui.skill.skilluse;

import net.iaround.BaseApplication;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;
import net.iaround.data.responsitory.SkillResponsitory;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.model.skill.SkillUsedInfoBean;
import net.iaround.model.skill.SkillUsedItemBean;
import net.iaround.tools.GsonUtil;

/**
 * 作者：zx on 2017/8/15 17:43
 */
public class SkillUsePresenter extends SkillUseContract.Presenter{

    private SkillUseContract.View mView;
    private SkillDataSource skillResponsitory;

    public SkillUsePresenter(SkillUseContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        skillResponsitory = new SkillResponsitory();
    }

    @Override
    public void getSkillUsedInfo(String targetUserId) {
        mView.showLoading();
        addFlag(skillResponsitory.getSkillUsedInfo(targetUserId, new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }

                SkillUsedInfoBean usedInfoBean = GsonUtil.getInstance().getServerBean(result, SkillUsedInfoBean.class);
                mView.showSkillUsedInfo(usedInfoBean);
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
    public void skillAttack(String targetUserId, String skillId, String groupId, String currencyType, String propsId) {
        mView.showLoading();
        addFlag(skillResponsitory.skillAttack(targetUserId, skillId, groupId, currencyType, propsId, new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                SkillAttackResult attackResult = GsonUtil.getInstance().getServerBean(result, SkillAttackResult.class);
                mView.handleAttackResult(attackResult);
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
    public void openSkill(final SkillUsedItemBean itemBean) {
        addFlag(skillResponsitory.openSkill(itemBean.skillId, new HttpCallBack(){

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
                mView.updateSkillOpenStatus(itemBean, skillOpenBean.skill);
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
