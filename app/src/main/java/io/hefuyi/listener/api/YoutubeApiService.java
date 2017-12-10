package io.hefuyi.listener.api;

import io.hefuyi.listener.Constants;
import io.hefuyi.listener.api.model.KuGouRawLyric;
import io.hefuyi.listener.mvp.model.HomeSound;
import io.hefuyi.listener.mvp.model.YouTubeVideos;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liyanju on 2017/11/18.
 */

public interface YoutubeApiService {

    @GET("videos?part=snippet,contentDetails,statistics&chart=mostPopular&maxResults=15&key="+ Constants.DEVELOPER_KEY)
    Observable<YouTubeVideos> getYoutubeVideos(@Query("pageToken") String pageToken,
                                               @Query("videoCategoryId") String videoCategoryId);
}
