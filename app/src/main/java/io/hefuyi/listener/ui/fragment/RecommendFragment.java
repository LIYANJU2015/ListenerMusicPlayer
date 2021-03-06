package io.hefuyi.listener.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.admodule.admob.AdMobBanner;
import com.bumptech.glide.Glide;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdListener;
import com.paginate.Paginate;
import com.tubewebplayer.YouTubePlayerActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.R;
import io.hefuyi.listener.injector.component.ApplicationComponent;

import io.hefuyi.listener.injector.component.DaggerRecommendComponent;
import io.hefuyi.listener.injector.component.RecommendComponent;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.RecommendModule;
import io.hefuyi.listener.mvp.contract.RecommendContract;
import io.hefuyi.listener.mvp.model.YouTubeVideos;
import io.hefuyi.listener.util.ATEUtil;
import io.hefuyi.listener.util.AdViewWrapperAdapter;
import io.hefuyi.listener.util.FileUtil;
import io.hefuyi.listener.util.ListenerUtil;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by liyanju on 2017/11/18.
 */

public class RecommendFragment extends Fragment implements RecommendContract.View, SwipeRefreshLayout.OnRefreshListener, Paginate.Callbacks {

    @Inject
    RecommendContract.Presenter mPresenter;

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

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
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

        initAdBannerView();

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ATEUtil.getThemePrimaryColor(mContext));

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);

        CommonAdapter  commonAdapter = new CommonAdapter<YouTubeVideos.Snippet>(mContext,R.layout.home_video_item, mDatas) {
            @Override
            protected void convert(ViewHolder holder,  final YouTubeVideos.Snippet snippet, int position) {
                ImageView imageView = holder.getView(R.id.img);
                if (snippet.thumbnails.getStandard() != null) {
                    Glide.with(mContext).load(snippet.thumbnails.getStandard().getUrl()).crossFade()
                            .placeholder(R.drawable.default_image).into(imageView);
                } else if (snippet.thumbnails.getHigh() != null) {
                    Glide.with(mContext).load(snippet.thumbnails.getHigh().getUrl()).crossFade()
                            .placeholder(R.drawable.default_image).into(imageView);
                } else if (snippet.thumbnails.getStandard() != null) {
                    Glide.with(mContext).load(snippet.thumbnails.getStandard().getUrl()).crossFade()
                            .placeholder(R.drawable.default_image).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.default_image);
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
                        isClickPlay = true;
                        YouTubePlayerActivity.launch(activity, snippet.vid);
                    }
                });
            }
        };

        adViewWrapperAdapter = new AdViewWrapperAdapter(commonAdapter);
        recyclerView.setAdapter(adViewWrapperAdapter);

        mPaginate = Paginate.with(recyclerView, this)
                .setLoadingTriggerThreshold(2)
                .build();
        mPaginate.setHasMoreDataToLoad(false);

        if (mDatas.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            mPresenter.requestYoutube(sNextPageToken, "10");
        }
    }

    private boolean isClickPlay;


    private AdMobBanner adView;

    private void initAdBannerView() {
        adView = AdModule.getInstance().getAdMob().createBannerAdView();
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (!isAdded() || adView == null) {
                    return;
                }
                if (ListenerApp.isCanShowAd() && adViewWrapperAdapter != null && !adViewWrapperAdapter.isAddAdView()
                        && adViewWrapperAdapter.getItemCount() > 3) {
                    adView.getAdView().setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT));
                    adViewWrapperAdapter.addAdView(22, new AdViewWrapperAdapter.
                            AdViewItem(adView.getAdView(), 1));

                    adViewWrapperAdapter.notifyItemInserted(1);
                }
            }
        });
        adView.getAdView().loadAd(AdModule.getInstance().getAdMob().createAdRequest());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (isClickPlay && ListenerApp.isCanShowAd()) {
            isClickPlay = false;
            AdModule.getInstance().getAdMob().showInterstitialAd();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adView != null) {
            adView.destroy();
            adView = null;
        }

        mPresenter.unsubscribe();
    }

    private View setUpNativeAdView(NativeAd nativeAd) {
        nativeAd.unregisterView();

        View adView = LayoutInflater.from(activity).inflate(R.layout.home_video_ad_item, null);

        FrameLayout adChoicesFrame = (FrameLayout) adView.findViewById(R.id.fb_adChoices);
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.fb_half_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.fb_banner_title);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.fb_banner_desc);
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

        if (adView != null) {
            adView.destroy();
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


    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ListenerApp) getActivity().getApplication()).getApplicationComponent();
        RecommendComponent homeComponent = DaggerRecommendComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .recommendModule(new RecommendModule())
                .build();
        homeComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recomm_fragment_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showYoutubeData(YouTubeVideos youTubeVideos) {
        if (youTubeVideos == null || activity == null || activity.isFinishing()) {
            return;
        }

        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        sNextPageToken = youTubeVideos.nextPageToken;
        mIsLoadingMore = false;
        mIsLoadedAll = TextUtils.isEmpty(youTubeVideos.nextPageToken);

        NativeAd nativeAd = AdModule.getInstance().getFacebookAd().getNativeAd();
        if (nativeAd == null || !nativeAd.isAdLoaded()) {
            nativeAd = AdModule.getInstance().getFacebookAd().nextNativieAd();
        }
        if (ListenerApp.isCanShowAd() && nativeAd != null && nativeAd.isAdLoaded() && !adViewWrapperAdapter.isAddAdView()) {
            adViewWrapperAdapter.addAdView(22, new AdViewWrapperAdapter.
                    AdViewItem(setUpNativeAdView(nativeAd), 1));
            currentAdapter = adViewWrapperAdapter;
        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            mDatas.clear();
            mDatas.addAll(youTubeVideos.items);
            adViewWrapperAdapter.notifyDataSetChanged();
        } else {
            int positonStart = adViewWrapperAdapter.getItemCount();
            mDatas.addAll(youTubeVideos.items);
            adViewWrapperAdapter.notifyItemRangeInserted(positonStart, youTubeVideos.items.size());
        }
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
        } else {
            emptyView.setVisibility(View.GONE);
        }

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
