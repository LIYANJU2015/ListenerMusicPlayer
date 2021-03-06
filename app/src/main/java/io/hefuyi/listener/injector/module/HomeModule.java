package io.hefuyi.listener.injector.module;

import dagger.Module;
import dagger.Provides;
import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.contract.RecommendContract;
import io.hefuyi.listener.mvp.presenter.HomePresenter;
import io.hefuyi.listener.mvp.presenter.RecommendPresenter;
import io.hefuyi.listener.mvp.usecase.GetHomeList;
import io.hefuyi.listener.mvp.usecase.GetYoutube;
import io.hefuyi.listener.respository.interfaces.Repository;

/**
 * Created by liyanju on 2017/11/18.
 */
@Module
public class HomeModule {

    @Provides
    HomeContract.Presenter getHomePresenter(GetHomeList getSoundCloud) {
        return new HomePresenter(getSoundCloud);
    }

    @Provides
    GetHomeList getSongsUsecase(Repository repository) {
        return new GetHomeList(repository);
    }
}
