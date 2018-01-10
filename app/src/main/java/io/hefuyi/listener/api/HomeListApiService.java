package io.hefuyi.listener.api;


import io.hefuyi.listener.mvp.model.YouTubeModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by liyanju on 2018/1/9.
 */

public interface HomeListApiService {

    @GET("music/{region}")
    Observable<YouTubeModel> getYoutubeMusic(@Path("region") String region);
}
