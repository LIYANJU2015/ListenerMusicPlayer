package io.hefuyi.listener.mvp.usecase;

import io.hefuyi.listener.mvp.model.YouTubeModel;
import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.respository.interfaces.Repository;
import rx.Observable;

/**
 * Created by liyanju on 2018/1/9.
 */

public class GetHomeList extends UseCase<GetHomeList.RequestValues,GetHomeList.ResponseValue>{
    private final Repository mRepository;

    public GetHomeList(Repository repository){
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new GetHomeList.ResponseValue(mRepository.getYoutubeHomeMusic(requestValues.getRegion()));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String region;

        public RequestValues(String region) {
            this.region = region;
        }

        public String getRegion() {
            return region;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<YouTubeModel> mListObservable;

        public ResponseValue(Observable<YouTubeModel> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<YouTubeModel> getYoutubeVideos(){
            return mListObservable;
        }
    }
}
