package io.hefuyi.listener.mvp.presenter;

import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.contract.RecommendContract;
import io.hefuyi.listener.mvp.model.YouTubeModel;
import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.mvp.usecase.GetHomeList;
import io.hefuyi.listener.mvp.usecase.GetYoutube;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by liyanju on 2017/11/18.
 */

public class HomePresenter implements HomeContract.Presenter {

    private GetHomeList mGetSoundCloud;

    private CompositeSubscription mCompositeSubscription;

    private HomeContract.View mView;

    public HomePresenter(GetHomeList getSoundCloud) {
        mGetSoundCloud = getSoundCloud;
    }

    @Override
    public void attachView(HomeContract.View view) {
        mView = view;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeSubscription.clear();
    }

    @Override
    public void getYoutubeHomeMusic(String region) {
        mCompositeSubscription.clear();
        Subscription subscription = mGetSoundCloud.execute(new GetHomeList.RequestValues(region))
                .getYoutubeVideos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<YouTubeModel>() {
                    @Override
                    public void call(YouTubeModel youTubeVideos) {
                        if (youTubeVideos == null || youTubeVideos.youTubeItems.size() == 0) {
                            mView.showEmptyView();
                        } else {
                            mView.showYoutubeData(youTubeVideos);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mView.showEmptyView();
                    }
                });
        mCompositeSubscription.add(subscription);

    }
}
