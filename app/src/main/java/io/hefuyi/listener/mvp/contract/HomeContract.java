package io.hefuyi.listener.mvp.contract;

import io.hefuyi.listener.mvp.model.YouTubeModel;
import io.hefuyi.listener.mvp.presenter.BasePresenter;
import io.hefuyi.listener.mvp.view.BaseView;

/**
 * Created by liyanju on 2018/1/9.
 */

public class HomeContract {

    public interface View extends BaseView {

        void showYoutubeData(YouTubeModel youTubeModel);

        void showEmptyView();
    }

    public interface Presenter extends BasePresenter<HomeContract.View> {

        void getYoutubeHomeMusic(String region);
    }
}
