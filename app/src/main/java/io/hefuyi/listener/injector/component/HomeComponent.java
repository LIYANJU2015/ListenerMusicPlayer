package io.hefuyi.listener.injector.component;

import dagger.Component;
import dagger.Module;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.HomeModule;
import io.hefuyi.listener.injector.module.RecommendModule;
import io.hefuyi.listener.injector.scope.PerActivity;
import io.hefuyi.listener.ui.fragment.HomeFragment;
import io.hefuyi.listener.ui.fragment.RecommendFragment;

/**
 * Created by liyanju on 2017/11/18.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, HomeModule.class})
public interface HomeComponent {

    void inject(HomeFragment homeFragment);
}
