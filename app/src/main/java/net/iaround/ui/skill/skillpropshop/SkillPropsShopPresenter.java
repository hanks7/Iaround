package net.iaround.ui.skill.skillpropshop;

import net.iaround.BaseApplication;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.data.SkillDataSource;
import net.iaround.data.responsitory.SkillResponsitory;
import net.iaround.model.skill.PropsShopBean;
import net.iaround.model.skill.PropsShopBuySuccess;
import net.iaround.tools.GsonUtil;

/**
 * 作者：zx on 2017/8/16 11:57
 */
public class SkillPropsShopPresenter extends SkillPropsShopContract.Presenter{

    private SkillPropsShopContract.View mView;
    private SkillDataSource mDataSource;

    public SkillPropsShopPresenter(SkillPropsShopContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
        mDataSource = new SkillResponsitory();
    }

    @Override
    public void getPropsShopData() {
        mView.showLoading();
        addFlag(mDataSource.getPropsShopData(new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                PropsShopBean propsShopBean = GsonUtil.getInstance().getServerBean(result, PropsShopBean.class);
                mView.updatePropsShopView(propsShopBean);
            }

            @Override
            public void onGeneralError(int e, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                ErrorCode.toastError(BaseApplication.appContext, e);
            }
        }));
    }

    @Override
    public void propsBuy(String propsShopId) {
        mView.showLoading();
        addFlag(mDataSource.propsShopBuy(propsShopId, new HttpCallBack(){

            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                if (!Constant.isSuccess(result)){
                    return;
                }
                PropsShopBuySuccess shopBuySuccess = GsonUtil.getInstance().getServerBean(result, PropsShopBuySuccess.class);
                mView.propsBuyResult(shopBuySuccess);
            }

            @Override
            public void onGeneralError(int e, long flag) {
                if (!mView.isActive()){
                    return;
                }
                mView.hideLoading();
                ErrorCode.toastError(BaseApplication.appContext, e);
            }
        }));
    }
}
