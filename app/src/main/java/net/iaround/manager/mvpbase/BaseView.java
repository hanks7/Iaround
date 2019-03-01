package net.iaround.manager.mvpbase;

/**
 * 作者：zx on 2017/8/9 11:37
 */
public interface BaseView<P> {

    void setPresenter(P presenter);

    void showLoading();

    void hideLoading();

    boolean isActive();

}
