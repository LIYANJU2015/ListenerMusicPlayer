package io.hefuyi.listener.injector.module;

import dagger.Module;
import dagger.Provides;
import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.presenter.HomePresenter;
import io.hefuyi.listener.mvp.usecase.GetYoutube;
import io.hefuyi.listener.respository.interfaces.Repository;

/**
 * Created by liyanju on 2017/11/18.
 */
@Module
public class HomeModule {

    @Provides
    HomeContract.Presenter getHomePresenter(GetYoutube getSoundCloud) {
        return new HomePresenter(getSoundCloud);
    }

    @Provides
    GetYoutube getSongsUsecase(Repository repository) {
        return new GetYoutube(repository);
    }
}
