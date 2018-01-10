package io.hefuyi.listener.injector.component;

import dagger.Component;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.RecommendModule;
import io.hefuyi.listener.injector.scope.PerActivity;
import io.hefuyi.listener.ui.fragment.RecommendFragment;

/**
 * Created by liyanju on 2017/11/18.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, RecommendModule.class})
public interface RecommendComponent {

    void inject(RecommendFragment homeFragment);
}
