package net.iaround.ui.skill.skilldetail;

import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;
import net.iaround.data.responsitory.SkillResponsitory;
import net.iaround.model.skill.SkillDetailBean;
import net.iaround.tools.GsonUtil;

/**
 * 作者：zx on 2017/8/10 19:02
 */
public class SkillDetailPresenter extends SkillDetailContract.Presenter{

    private SkillDetailContract.View mView;
    private SkillDataSource skillResponsitory;

    public SkillDetailPresenter(SkillDetailContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        this.skillResponsitory = new SkillResponsitory();
    }

    @Override
    public void getSkillDetail(String skillId) {
        mView.showLoading();
        addFlag(skillResponsitory.getSkillDetail(skillId, new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()) {
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                SkillDetailBean skillDetail = GsonUtil.getInstance().getServerBean(result, SkillDetailBean.class);
                mView.updateDetailView(skillDetail);
            }

            @Override
            public void onGeneralError(int e, long flag) {
                if (!mView.isActive()) {
                    return;
                }
                mView.hideLoading();
            }
        }));
    }

    @Override
    public void updateSkill(String skillId, String propId, String currencyType) {
        mView.showLoading();
        addFlag(skillResponsitory.updateSkill(skillId, propId, currencyType, new HttpCallBack(){

            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()) {
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                SkillDetailBean skillDetail = GsonUtil.getInstance().getServerBean(result, SkillDetailBean.class);
                mView.updateSkillResult(skillDetail);
            }

            @Override
            public void onGeneralError(int e, long flag) {
                if (!mView.isActive()) {
                    return;
                }
                mView.hideLoading();
            }
        }));
    }
}
