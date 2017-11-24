package io.hefuyi.listener.mvp.contract;

import io.hefuyi.listener.mvp.model.HomeSound;
import io.hefuyi.listener.mvp.presenter.BasePresenter;
import io.hefuyi.listener.mvp.view.BaseView;

/**
 * Created by liyanju on 2017/11/18.
 */

public interface HomeContract {

    interface View extends BaseView {

        void showHomeSound(HomeSound soundClound);

        void showEmptyView();
    }

    interface Presenter extends BasePresenter<HomeContract.View> {

        void requestHomeSound();
    }
}
