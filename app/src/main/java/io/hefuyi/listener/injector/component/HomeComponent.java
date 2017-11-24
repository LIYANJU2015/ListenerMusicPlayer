package io.hefuyi.listener.injector.component;

import dagger.Component;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.HomeModule;
import io.hefuyi.listener.injector.module.SongsModule;
import io.hefuyi.listener.injector.scope.PerActivity;
import io.hefuyi.listener.mvp.model.HomeSound;
import io.hefuyi.listener.ui.fragment.HomeFragment;

/**
 * Created by liyanju on 2017/11/18.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, HomeModule.class})
public interface HomeComponent {

    void inject(HomeFragment homeFragment);
}
