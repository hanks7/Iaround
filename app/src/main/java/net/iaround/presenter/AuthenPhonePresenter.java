package net.iaround.presenter;

import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.AuthenHttpProtocol;
import net.iaround.contract.AuthenPhoneContract;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;

/**
 * @author：liush on 2016/12/21 15:52
 */
public class AuthenPhonePresenter {

    AuthenPhoneContract.View authenPhone;

    public AuthenPhonePresenter(AuthenPhoneContract.View authenPhone) {
        this.authenPhone = authenPhone;
    }

    public void getVerifyCode(String phoneNum){
        AuthenHttpProtocol.authenPhone(authenPhone.getContext(),"",phoneNum,"",new HttpCallBack(){
            @Override
            public void onGeneralError(int e, long flag) {

            }

            @Override
            public void onGeneralSuccess(String result, long flag) {
                BaseServerBean baseEntity = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
                if(baseEntity.isSuccess()){
                    authenPhone.change2Verify();
                } else {
                    ErrorCode.showError(authenPhone.getContext(), result);
                }
            }
        });
    }

    public void bind(String verifyCode, String phoneNum, String password){
        AuthenHttpProtocol.authenPhone(authenPhone.getContext(),verifyCode,phoneNum,password,new HttpCallBack(){
            @Override
            public void onGeneralError(int e, long flag) {

            }

            @Override
            public void onGeneralSuccess(String result, long flag) {
                BaseServerBean baseEntity = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
                if(baseEntity.isSuccess()){
                    authenPhone.change2Verify();
                } else {
                    ErrorCode.showError(authenPhone.getContext(), result);
                }
            }
        });
    }

}
