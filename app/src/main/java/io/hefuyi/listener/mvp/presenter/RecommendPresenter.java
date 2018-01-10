package io.hefuyi.listener.mvp.presenter;

import io.hefuyi.listener.mvp.contract.RecommendContract;
import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.mvp.usecase.GetYoutube;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by liyanju on 2017/11/18.
 */

public class RecommendPresenter implements RecommendContract.Presenter {

    private GetYoutube mGetSoundCloud;

    private CompositeSubscription mCompositeSubscription;

    private RecommendContract.View mView;

    public RecommendPresenter(GetYoutube getSoundCloud) {
        mGetSoundCloud = getSoundCloud;
    }

    @Override
    public void attachView(RecommendContract.View view) {
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
    public void requestYoutube(String pageToken, String videoCategoryId) {
        mCompositeSubscription.clear();
        Subscription subscription = mGetSoundCloud.execute(new GetYoutube.RequestValues(pageToken, videoCategoryId))
                .getYoutubeVideos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<YouTubeVideos>() {
                    @Override
                    public void call(YouTubeVideos youTubeVideos) {
                        if (youTubeVideos == null || youTubeVideos.items.size() == 0) {
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
