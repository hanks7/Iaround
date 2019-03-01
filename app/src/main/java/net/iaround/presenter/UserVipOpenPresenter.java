package net.iaround.presenter;

import net.iaround.contract.UserVipOpenContract;

/**
 * @author：liush on 2017/3/8 16:17
 */
public class UserVipOpenPresenter {

    private UserVipOpenContract.View userVipOpen;

    public UserVipOpenPresenter(UserVipOpenContract.View userVipOpen) {
        this.userVipOpen = userVipOpen;
    }

//    public void init(int area, String packageid){
//        userVipOpen.showDialog();
//        UserVipOpenProtocol.init(area, packageid, new HttpStringCallback() {
//            @Override
//            public void onGeneralSuccess(String result, int id) {
//                userVipOpen.hideDialog();
//                Log.e("xiaohua", "/user/buymemberlist result = " + result);
//                VIPOpenEntity vipOpenEntity = GsonUtil.getInstance().getServerBean(result, VIPOpenEntity.class);
//                if(vipOpenEntity!=null && vipOpenEntity.isSuccess()){
//                    userVipOpen.setPackageData(vipOpenEntity.getGoods());
//                }
//            }
//
//            @Override
//            public void onGeneralError(String error, Exception e, int id) {
//                userVipOpen.hideDialog();
//            }
//        });
//    }
}
