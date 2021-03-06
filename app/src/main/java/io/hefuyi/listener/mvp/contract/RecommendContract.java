package io.hefuyi.listener.mvp.contract;

import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.mvp.presenter.BasePresenter;
import io.hefuyi.listener.mvp.view.BaseView;

/**
 * Created by liyanju on 2017/11/18.
 */

public interface RecommendContract {

    interface View extends BaseView {

        void showYoutubeData(YouTubeVideos youTubeVideos);

        void showEmptyView();
    }

    interface Presenter extends BasePresenter<RecommendContract.View> {

        void requestYoutube(String pageToken, String videoCategoryId);
    }
}
