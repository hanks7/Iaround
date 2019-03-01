package net.iaround.ui.skill.skillpropshop;

import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.skill.PropsShopBean;
import net.iaround.model.skill.PropsShopBuySuccess;

/**
 * 作者：zx on 2017/8/16 11:57
 */
public interface SkillPropsShopContract {

    interface View extends BaseView<Presenter>{
        void updatePropsShopView(PropsShopBean propsShopBean);

        void propsBuyResult(PropsShopBuySuccess shopBuySuccess);
    }

    abstract class Presenter extends BasePresenter{

        public abstract void getPropsShopData();

        public abstract void propsBuy(String propsShopId);
    }
}
