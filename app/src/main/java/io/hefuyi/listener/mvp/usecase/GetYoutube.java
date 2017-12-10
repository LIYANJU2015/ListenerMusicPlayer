package io.hefuyi.listener.mvp.usecase;

import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.respository.interfaces.Repository;
import rx.Observable;

/**
 * Created by liyanju on 2017/11/18.
 */

public class GetYoutube extends UseCase<GetYoutube.RequestValues,GetYoutube.ResponseValue>{

    private final Repository mRepository;

    public GetYoutube(Repository repository){
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.getYoutubeVideos(requestValues.getPageToken(), requestValues.getVideoCategoryId()));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String pageToken;
        private final String videoCategoryId;

        public RequestValues(String pageToken, String videoCategoryId) {
            this.pageToken = pageToken;
            this.videoCategoryId = videoCategoryId;
        }

        public String getPageToken() {
            return pageToken;
        }

        public String getVideoCategoryId() {
            return videoCategoryId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<YouTubeVideos> mListObservable;

        public ResponseValue(Observable<YouTubeVideos> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<YouTubeVideos> getYoutubeVideos(){
            return mListObservable;
        }
    }
}
