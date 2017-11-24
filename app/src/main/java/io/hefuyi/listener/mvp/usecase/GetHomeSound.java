package io.hefuyi.listener.mvp.usecase;

import io.hefuyi.listener.mvp.model.HomeSound;
import io.hefuyi.listener.respository.interfaces.Repository;
import rx.Observable;

/**
 * Created by liyanju on 2017/11/18.
 */

public class GetHomeSound extends UseCase<GetHomeSound.RequestValues,GetHomeSound.ResponseValue>{

    private final Repository mRepository;

    public GetHomeSound(Repository repository){
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.getHomeSound());
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String action;

        public RequestValues(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<HomeSound> mListObservable;

        public ResponseValue(Observable<HomeSound> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<HomeSound> getHomeSound(){
            return mListObservable;
        }
    }
}
