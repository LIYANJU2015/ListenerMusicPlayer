package io.hefuyi.listener;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.admodule.AdModule;
import com.afollestad.appthemeengine.ATE;
import com.facebook.FacebookSdk;
import com.facebook.ads.Ad;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.bugly.crashreport.CrashReport;
import com.tubewebplayer.YouTubePlayerActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.hefuyi.listener.api.YouTubeModelDeseializer;
import io.hefuyi.listener.api.YoutubeApiService;
import io.hefuyi.listener.dataloader.SongLoader;
import io.hefuyi.listener.event.MediaUpdateEvent;
import io.hefuyi.listener.injector.component.ApplicationComponent;
import io.hefuyi.listener.injector.component.DaggerApplicationComponent;
import io.hefuyi.listener.injector.module.ApplicationModule;
import io.hefuyi.listener.injector.module.NetworkModule;
import io.hefuyi.listener.mvp.model.Song;
import io.hefuyi.listener.mvp.model.YouTubeModel;
import io.hefuyi.listener.permission.PermissionManager;
import io.hefuyi.listener.util.ListenerUtil;
import io.hefuyi.listener.util.PreferencesUtility;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hefuyi on 2016/10/4.
 */

public class ListenerApp extends MultiDexApplication implements AdModule.AdCallBack{

    private ApplicationComponent mApplicationComponent;

    public static Context sContext;

    public static boolean sIsColdLaunch = false;

    public static boolean sIsAdDialog = false;

    private static long time;

    public static boolean isCanShowAd() {
//        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//        int month = Calendar.getInstance().get(Calendar.MONTH);
//        String dateStr = String.valueOf(month) + String.valueOf(day);
//        boolean isCanShow = !dateStr.equals(Calendar.FEBRUARY + "12");
//        if (!isCanShow) {
//            if (Math.abs(System.currentTimeMillis() - time) >= 10 * 1000 * 60) {
//                return true;
//            } else {
//                time = System.currentTimeMillis();
//                return false;
//            }
//        }

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sIsColdLaunch = true;
        time = System.currentTimeMillis();
//        initLeakCanary();
//        setCrashHandler();
//        initStetho();
//        setStrictMode();
        setupInjector();
        initImageLoader();
        PermissionManager.init(this);
        updataMedia();
        setupATE();

        ListenerUtil.initRecommend();

        AdModule.init(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        AdModule.getInstance().getAdMob().initInterstitialAd();

        CrashReport.initCrashReport(this);

        AdModule.getInstance().getFacebookAd().loadAds("200998730474227_223664654874301");

        YouTubePlayerActivity.setDeveloperKey(Constants.DEVELOPER_KEY);

        initLocalHomeYoutube();
    }

    public static YouTubeModel sYoutubeModel;

    public static boolean isLoadLocalHomeYouTube() {
        long fristTime = PreferencesUtility.getInstance(sContext).getFristTime();
        boolean result = true;
        if (fristTime != 0 && Math.abs(System.currentTimeMillis() - fristTime) >= 1000 * 60 * 60 * 24 * 10) {
            PreferencesUtility.getInstance(sContext).setFristTime();
            result = false;
        }

        return result;
    }

    private void initLocalHomeYoutube() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    InputStream is = getAssets().open("ahameet.txt");
                    int lenght = is.available();
                    byte[]  buffer = new byte[lenght];
                    is.read(buffer);
                    String result = new String(buffer, "utf8");
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(YouTubeModel.class, new YouTubeModelDeseializer());
                    Gson gson = gsonBuilder.create();
                    sYoutubeModel = gson.fromJson(result, YouTubeModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public Application getApplication() {
        return this;
    }

    @Override
    public String getAppId() {
        return "ca-app-pub-9880857526519562~2662504827";
    }

    @Override
    public boolean isAdDebug() {
        return false;
    }

    @Override
    public boolean isLogDebug() {
        return false;
    }

    @Override
    public String getAdMobNativeAdId() {
        return null;
    }

    @Override
    public String getBannerAdId() {
        return "ca-app-pub-9880857526519562/1896218069";
    }

    @Override
    public String getInterstitialAdId() {
        return "ca-app-pub-9880857526519562/3784014804";
    }

    @Override
    public String getTestDevice() {
        return null;
    }

    @Override
    public String getRewardedVideoAdId() {
        return null;
    }

    @Override
    public String getFBNativeAdId() {
        return "200998730474227_223418144898952";
    }

    //    private void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
//    }

//    private void setStrictMode() {
//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
//        }
//    }

//    private void setCrashHandler() {
//        CrashHandler crashHandler = CrashHandler.getInstance(this);
//        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
//    }

//    private void initStetho() {
//        Stetho.initializeWithDefaults(this);
//    }

    private void setupInjector() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule(this)).build();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(localImageLoaderConfiguration);
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    //应用启动时通知系统刷新媒体库,
    private void updataMedia() {
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ListenerUtil.isMarshmallow() && !PermissionManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                return;
            }
            SongLoader.getAllSongs(this)
                    .map(new Func1<List<Song>, String[]>() {
                        @Override
                        public String[] call(List<Song> songList) {
                            List<String> folderPath = new ArrayList<String>();
                            int i = 0;
                            for (Song song : songList) {
                                folderPath.add(i, song.path);
                                i++;
                            }
                            return folderPath.toArray(new String[0]);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String[]>() {
                        @Override
                        public void call(String[] paths) {
                            MediaScannerConnection.scanFile(getApplicationContext(), paths, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            if (uri == null) {
                                                RxBus.getInstance().post(new MediaUpdateEvent());
                                            }
                                        }
                                    });
                        }
                    });
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));
        }

    }

    private void setupATE() {
        if (!ATE.config(this, "light_theme").isConfigured()) {
            ATE.config(this, "light_theme")
                    .activityTheme(R.style.AppThemeLight)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        }
        if (!ATE.config(this, "dark_theme").isConfigured()) {
            ATE.config(this, "dark_theme")
                    .activityTheme(R.style.AppThemeDark)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        }
    }
}
