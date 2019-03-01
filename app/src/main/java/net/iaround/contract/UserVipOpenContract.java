package net.iaround.contract;

import net.iaround.pay.bean.VipBuyMemberListBean;

import java.util.ArrayList;

/**
 * @author：liush on 2017/3/8 16:19
 */
public interface UserVipOpenContract {

    interface View {
        void setPackageData(ArrayList<VipBuyMemberListBean.Goods> goods);

        void showDialog();

        void hideDialog();
    }

    interface Presenter {
    }
}
