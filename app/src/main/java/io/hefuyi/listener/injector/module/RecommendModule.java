package io.hefuyi.listener.injector.module;

import dagger.Module;
import dagger.Provides;
import io.hefuyi.listener.mvp.contract.RecommendContract;
import io.hefuyi.listener.mvp.presenter.RecommendPresenter;
import io.hefuyi.listener.mvp.usecase.GetYoutube;
import io.hefuyi.listener.respository.interfaces.Repository;

/**
 * Created by liyanju on 2017/11/18.
 */
@Module
public class RecommendModule {

    @Provides
    RecommendContract.Presenter getRecommedPresenter(GetYoutube getSoundCloud) {
        return new RecommendPresenter(getSoundCloud);
    }

    @Provides
    GetYoutube getSongsUsecase(Repository repository) {
        return new GetYoutube(repository);
    }
}
