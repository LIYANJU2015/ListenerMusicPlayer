package io.hefuyi.listener.api;

import io.hefuyi.listener.api.model.KuGouRawLyric;
import io.hefuyi.listener.mvp.model.HomeSound;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liyanju on 2017/11/18.
 */

public interface HomeSoundApiService {

    @GET("module?product_id=1112&module_id=1474")
    Observable<HomeSound> getHomeSound(@Query("client") String client);
}
