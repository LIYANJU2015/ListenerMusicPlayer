package io.hefuyi.listener.mvp.presenter;

import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.model.HomeSound;
import io.hefuyi.listener.mvp.usecase.GetHomeSound;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by liyanju on 2017/11/18.
 */

public class HomePresenter implements HomeContract.Presenter {

    private GetHomeSound mGetSoundCloud;

    private CompositeSubscription mCompositeSubscription;

    private HomeContract.View mView;

    public HomePresenter(GetHomeSound getSoundCloud) {
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
    public void requestHomeSound() {
        mCompositeSubscription.clear();
        Subscription subscription = mGetSoundCloud.execute(new GetHomeSound.RequestValues(""))
                .getHomeSound()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HomeSound>() {
                    @Override
                    public void call(HomeSound soundClound) {
                        if (soundClound == null || soundClound.getContents().size() == 0) {
                            mView.showEmptyView();
                        } else {
                            mView.showHomeSound(soundClound);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);

    }
}
