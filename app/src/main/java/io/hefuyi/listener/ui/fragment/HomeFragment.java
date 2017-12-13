package io.hefuyi.listener.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.admodule.AdModule;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.paginate.Paginate;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.R;

import io.hefuyi.listener.injector.component.ApplicationComponent;
import io.hefuyi.listener.injector.component.DaggerHomeComponent;
import io.hefuyi.listener.injector.component.HomeComponent;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.HomeModule;
import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.ui.activity.YouTubePlayerActivity;
import io.hefuyi.listener.util.ATEUtil;
import io.hefuyi.listener.util.AdViewWrapperAdapter;
import io.hefuyi.listener.util.FileUtil;
import io.hefuyi.listener.util.HomeDiffCallBack;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by liyanju on 2017/11/18.
 */

public class HomeFragment extends Fragment implements HomeContract.View, SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks {

    @Inject
    HomeContract.Presenter mPresenter;

    @BindView(R.id.view_empty)
    ViewStub emptyView;

    @BindView(R.id.home_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.progressbar)
    MaterialProgressBar progressBar;

    @BindView(R.id.home_swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private static ArrayList<YouTubeVideos.Snippet> mDatas = new ArrayList<>();

    private Context mContext;

    private Activity activity;

    private CommonAdapter mCommonAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();

        mPresenter.attachView(this);
    }

    @Override
    public void onRefresh() {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (mIsLoadingMore) {
            return;
        }
        swipeRefreshLayout.setRefreshing(true);
        mPresenter.requestYoutube(sNextPageToken, "10");
    }

    @Override
    public void onLoadMore() {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (swipeRefreshLayout.isRefreshing()) {
            return;
        }
        mIsLoadingMore = true;
        mPresenter.requestYoutube(sNextPageToken, "10");
    }

    private static String sNextPageToken = "";

    private Paginate mPaginate;

    private boolean mIsLoadedAll;

    private boolean mIsLoadingMore;

    private AdViewWrapperAdapter adViewWrapperAdapter;

    private  RecyclerView.Adapter currentAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mDatas.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            mPresenter.requestYoutube(sNextPageToken, "10");
        }
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ATEUtil.getThemePrimaryColor(mContext));

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        mCommonAdapter = new CommonAdapter<YouTubeVideos.Snippet>(mContext,R.layout.home_video_item, mDatas) {
            @Override
            protected void convert(ViewHolder holder,  final YouTubeVideos.Snippet snippet, int position) {
                ImageView imageView = holder.getView(R.id.img);
                if (snippet.thumbnails.getStandard() != null) {
                    Glide.with(mContext).load(snippet.thumbnails.getStandard().getUrl()).crossFade()
                            .placeholder(R.drawable.mask).into(imageView);
                } else if (snippet.thumbnails.getHigh() != null) {
                    Glide.with(mContext).load(snippet.thumbnails.getHigh().getUrl()).crossFade()
                            .placeholder(R.drawable.mask).into(imageView);
                } else if (snippet.thumbnails.getStandard() != null) {
                    Glide.with(mContext).load(snippet.thumbnails.getStandard().getUrl()).crossFade()
                            .placeholder(R.drawable.mask).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.mask);
                }

                TextView titleTV = holder.getView(R.id.title);
                titleTV.setText(snippet.title);

                TextView descriptionTV = holder.getView(R.id.description);
                if (snippet.statistics != null) {
                    descriptionTV.setText(FileUtil
                            .sizeFormatNum2String(Long.parseLong(snippet.statistics.getViewCount())));
                } else {
                    descriptionTV.setText("");
                }

                TextView timeTV = holder.getView(R.id.time);
                if (snippet.contentDetails != null) {
                    timeTV.setText(FileUtil.convertDuration(snippet.contentDetails.duration));
                } else {
                    timeTV.setText("");
                }

                holder.setOnClickListener(R.id.card_view, new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        YouTubePlayerActivity.launch(activity,
                                "https://www.youtube.com/watch?v=" + snippet.vid, snippet.title);
                    }
                });
            }
        };

        setAapter();
    }

    public void setAapter() {
        if (currentAdapter == null) {
            NativeAd nativeAd = AdModule.getInstance().getFacebookAd().getNativeAd();
            if (nativeAd != null && nativeAd.isAdLoaded()) {
                adViewWrapperAdapter = new AdViewWrapperAdapter(mCommonAdapter);
                adViewWrapperAdapter.addAdView(22, new AdViewWrapperAdapter.
                        AdViewItem(setUpNativeAdView(nativeAd), 1));
                currentAdapter = adViewWrapperAdapter;
            } else {
                currentAdapter = mCommonAdapter;
            }

            recyclerView.setAdapter(currentAdapter);

            mPaginate = Paginate.with(recyclerView, this)
                    .setLoadingTriggerThreshold(2)
                    .build();
            mPaginate.setHasMoreDataToLoad(false);
        }
    }

    private View setUpNativeAdView(NativeAd nativeAd) {
        nativeAd.unregisterView();

        View adView = LayoutInflater.from(activity).inflate(R.layout.home_video_ad_item, null);

        FrameLayout adChoicesFrame = (FrameLayout)adView.findViewById(R.id.fb_adChoices);
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.fb_half_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.title);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.text);
        TextView nativeAdCallToAction = (TextView) adView.findViewById(R.id.fb_half_download);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(com.admodule.R.id.fb_half_mv);

        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Download and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        nativeAdMedia.setNativeAd(nativeAd);

        // Add adChoices icon
        AdChoicesView adChoicesView = new AdChoicesView(activity, nativeAd, true);
        adChoicesFrame.addView(adChoicesView, 0);
        adChoicesFrame.setVisibility(View.VISIBLE);

        nativeAd.registerViewForInteraction(adView);

        return adView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPaginate != null) {
            mPaginate.unbind();
        }
    }

    @Override
    public boolean isLoading() {
        return mIsLoadingMore;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mIsLoadedAll;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ListenerApp) getActivity().getApplication()).getApplicationComponent();
        HomeComponent homeComponent = DaggerHomeComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .homeModule(new HomeModule())
                .build();
        homeComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showYoutubeData(YouTubeVideos youTubeVideos) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        sNextPageToken = youTubeVideos.nextPageToken;
        mIsLoadingMore = false;
        mIsLoadedAll = TextUtils.isEmpty(youTubeVideos.nextPageToken);

        final ArrayList<YouTubeVideos.Snippet> newDatas = new ArrayList<>();
        newDatas.addAll(mDatas);
        newDatas.addAll(youTubeVideos.items);

        new AsyncTask<ArrayList<YouTubeVideos.Snippet>, Void, DiffUtil.DiffResult>(){
            @Override
            protected DiffUtil.DiffResult doInBackground(ArrayList<YouTubeVideos.Snippet>[] arrayLists) {
                return DiffUtil.calculateDiff(new HomeDiffCallBack(mDatas, arrayLists[0]), true);
            }

            @Override
            protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                super.onPostExecute(diffResult);
                diffResult.dispatchUpdatesTo(mCommonAdapter);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    mDatas.clear();
                }
                mDatas.addAll(newDatas);
            }
        }.execute(newDatas);
    }

    @Override
    public void showEmptyView() {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        mIsLoadingMore = false;

        if (mDatas.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else{
            emptyView.setVisibility(View.GONE);
        }

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
