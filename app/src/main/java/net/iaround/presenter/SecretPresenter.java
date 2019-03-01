package net.iaround.presenter;

import net.iaround.conf.Constants;
import net.iaround.contract.SecretContract;
import net.iaround.model.entity.SecretEntity;
import net.iaround.model.entity.UserInfoEntity;
import net.iaround.ui.activity.SecretActivity;

/**
 * @author：liush on 2016/12/12 15:59
 */
public class SecretPresenter {

    private SecretContract.View secretView;

    public SecretPresenter(SecretContract.View secretView) {
        this.secretView = secretView;
    }

    public void init(){
        SecretEntity secret = (SecretEntity) ((SecretActivity)secretView).getIntent().getSerializableExtra(Constants.SECRET);
        secretView.setDatas(secret);
    }

}
