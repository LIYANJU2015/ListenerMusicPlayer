package io.hefuyi.listener.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admodule.AdModule;
import com.admodule.Utils;
import com.admodule.admob.AdMobBanner;
import com.afollestad.appthemeengine.ATEActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdListener;
import com.tubewebplayer.YouTubePlayerActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.R;
import io.hefuyi.listener.mvp.model.YouTubeModel;
import io.hefuyi.listener.util.ATEUtil;
import io.hefuyi.listener.util.AdViewWrapperAdapter;
import io.hefuyi.listener.util.ListenerUtil;
import io.hefuyi.listener.util.PreferencesUtility;
import io.hefuyi.listener.widget.GlideRoundTransform;

/**
 * Created by liyanju on 2018/1/10.
 */

public class HomeListActivity extends ATEActivity{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_empty)
    RelativeLayout empty;

    private String mTitle;

    private ArrayList<YouTubeModel.YouTubeContent> mData = new ArrayList<>();

    public static void launch(Activity activity, String title, ArrayList<YouTubeModel.YouTubeContent> list) {
        Intent intent = new Intent(activity, HomeListActivity.class);
        intent.putExtra("list", list);
        intent.putExtra("title", title);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    private FloatingActionButton mRecomFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_youtube_list_layout);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("title");
            ArrayList<YouTubeModel.YouTubeContent> data = (ArrayList<YouTubeModel.YouTubeContent>)savedInstanceState
                    .getSerializable("list");
            if (data != null) {
                mData.clear();
                mData.addAll(data);
            }
        } else {
            mTitle = getIntent().getStringExtra("title");
            ArrayList<YouTubeModel.YouTubeContent> data = (ArrayList<YouTubeModel.YouTubeContent>) getIntent()
                    .getSerializableExtra("list");
            mData.clear();
            mData.addAll(data);
        }

        setUpToolBar();

        setUpRecyclerView();

        initAdBannerView();

        mRecomFab = (FloatingActionButton) findViewById(R.id.recom_fab);
        mRecomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecomFab.hide();
                PreferencesUtility.getInstance(ListenerApp.sContext).setNotShowRecomFAB();
                mRecomFab.post(new Runnable() {
                    @Override
                    public void run() {
                        ListenerUtil.gotoGP(ListenerApp.sContext, ListenerUtil.sRecommendPageName);
                    }
                });
            }
        });
        if (PreferencesUtility.getInstance(ListenerApp.sContext).isShowRecomFAB()
                && !ListenerUtil.checkRecommendExist(ListenerApp.sContext, ListenerUtil.sRecommendPageName)) {
            mRecomFab.setVisibility(View.VISIBLE);
        } else {
            mRecomFab.setVisibility(View.GONE);
        }
    }

    private AdMobBanner adView;

    private void initAdBannerView() {
        adView = AdModule.getInstance().getAdMob().createBannerAdView();
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (isFinishing() || adView == null) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ListenerApp.isCanShowAd()) {
            AdModule.getInstance().getFacebookAd().loadAd(true, "1305172892959949_1313403128803592");
            AdModule.getInstance().getAdMob().showInterstitialAd();
        }

        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    private AdViewWrapperAdapter adViewWrapperAdapter;
    private CommonAdapter commonadapter;

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commonadapter = new CommonAdapter<YouTubeModel.YouTubeContent>(this,
                R.layout.home_youtube_list_item_layout, mData) {
            @Override
            protected void convert(final ViewHolder holder, final YouTubeModel.YouTubeContent content,
                                   int position) {
                Glide.with(HomeListActivity.this).load(content.icon)
                        .crossFade().placeholder(R.drawable.default_album_art)
                        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext,6))
                        .into((ImageView) holder.getView(R.id.image));

                TextView titleTV = holder.getView(R.id.title);
                titleTV.setText(content.name);


                holder.setOnClickListener(R.id.list_item_frame, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LocalWebYouTubePlayerActivity.launch(HomeListActivity.this,
                                content.extra, content.name);
                    }
                });
            }
        };

        adViewWrapperAdapter = new AdViewWrapperAdapter(commonadapter);

        NativeAd nativeAd = AdModule.getInstance().getFacebookAd().getNativeAd();

        if (ListenerApp.isCanShowAd() && nativeAd != null && nativeAd.isAdLoaded() && mData.size() > 3
                && !adViewWrapperAdapter.isAddAdView()) {
            adViewWrapperAdapter.addAdView(22, new AdViewWrapperAdapter.
                    AdViewItem(setUpNativeAdView(nativeAd), 1));
        }
        recyclerView.setAdapter(adViewWrapperAdapter);
    }

    private View setUpNativeAdView(NativeAd nativeAd) {
        nativeAd.unregisterView();

        View adView = LayoutInflater.from(this).inflate(R.layout.home_list_ad_item2, null);

        FrameLayout adChoicesFrame = (FrameLayout)adView.findViewById(R.id.fb_adChoices2);
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.image_ad);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.title);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.text);
        TextView nativeAdCallToAction = (TextView) adView.findViewById(R.id.call_btn_tv);

        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Add adChoices icon
        AdChoicesView adChoicesView = new AdChoicesView(this, nativeAd, true);
        adChoicesFrame.addView(adChoicesView, 0);
        adChoicesFrame.setVisibility(View.VISIBLE);

        nativeAd.registerViewForInteraction(adView);

        return adView;
    }

    private void setUpToolBar() {
        toolbar.setBackgroundColor(ATEUtil.getThemePrimaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
