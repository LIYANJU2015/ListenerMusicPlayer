package io.hefuyi.listener.ui.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.admodule.AdModule;
import com.afollestad.appthemeengine.ATEActivity;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import io.hefuyi.listener.R;
import io.hefuyi.listener.util.ATEUtil;
import io.hefuyi.listener.widget.CustomSwipeToRefresh;
import ren.yale.android.cachewebviewlib.CacheWebView;
import ren.yale.android.cachewebviewlib.utils.NetworkUtils;

/**
 * Created by liyanju on 2017/12/10.
 */

public class YouTubePlayerActivity extends ATEActivity {

    private CacheWebView mWebView;

    private ProgressBar mProgressBar;

    private ImageView mBackImageView;

    private PopupMenu mPopupMenu;

    protected String url;

    private ImageView mFinishImageView;

    private ImageView mMoreImageView;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    if (!mWebView.canGoBack()) {
                        finish();
                    }
                    break;
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.iv_more:
                    showPoPup(v);
                    break;

            }
        }
    };

    private void showPoPup(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this, view);
            mPopupMenu.inflate(R.menu.agentweb_toolbar_menu);
            mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.refresh:
                            mWebView.reload();
                            return true;
                        case R.id.copy:
                            toCopy(YouTubePlayerActivity.this, url);
                            return true;
                        case R.id.default_browser:
                            openBrowser(url);
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }
        mPopupMenu.show();
    }

    private void openBrowser(String targetUrl) {
        if (!TextUtils.isEmpty(targetUrl) && targetUrl.startsWith("file://")) {
            Toast.makeText(this, targetUrl + getString(R.string.browser_open_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse(targetUrl);
        intent.setData(url);
        startActivity(intent);
    }

    private void toCopy(Context context, String text) {
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        Toast.makeText(this, R.string.copy_link, Toast.LENGTH_SHORT).show();
    }

    private CustomSwipeToRefresh swipeRefreshLayout;
    private View mLineView;
    private TextView mTitleTV;

    private String title;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("url", url);
        outState.putString("title", title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player_layout);

        if (savedInstanceState != null) {
            url = savedInstanceState.getString("url");
            title = savedInstanceState.getString("title");
        } else {
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
        }

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(mOnClickListener);
        mFinishImageView = (ImageView) findViewById(R.id.iv_finish);
        mFinishImageView.setOnClickListener(mOnClickListener);
        mMoreImageView = (ImageView) findViewById(R.id.iv_more);
        mMoreImageView.setOnClickListener(mOnClickListener);
        mLineView = findViewById(R.id.view_line);
        mTitleTV = (TextView)findViewById(R.id.toolbar_title);

        mTitleTV.setText(title);
        mWebView = (CacheWebView) findViewById(R.id.webview);
        initSettings();
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.loadUrl(url);

        mProgressBar = (ProgressBar) findViewById(R.id.wb_loading);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mProgressBar.setProgress(newProgress);//设置进度值
                }
            }
        });

        swipeRefreshLayout = (CustomSwipeToRefresh) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mWebView != null) {
                    mWebView.reload();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeColors(ATEUtil.getThemePrimaryColor(this));
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mWebView.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mWebView.onPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pageNavigator(int tag) {
        mBackImageView.setVisibility(tag);
        mLineView.setVisibility(tag);
    }

    protected WebViewClient mWebViewClient = new WebViewClient() {

        @RequiresApi(api = 21)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @RequiresApi(api = 21)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl() + "");
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.equals(YouTubePlayerActivity.this.url)) {
                pageNavigator(View.GONE);
            } else {
                pageNavigator(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            swipeRefreshLayout.setRefreshing(false);
            super.onPageFinished(webView, url);
        }
    };

    private void initSettings() {
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDefaultTextEncodingName("UTF-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        if (NetworkUtils.isConnected(mWebView.getContext())) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        setCachePath();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String dateStr = String.valueOf(month) + String.valueOf(day);
        Log.v("MAIN", "dateStr:: "+dateStr);
        if (!dateStr.equals(Calendar.JANUARY + "7")) {
            AdModule.getInstance().getAdMob().showInterstitialAd();
        }
        try {
            clearWebView(mWebView);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static final void clearWebView(WebView m) {
        if (m == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        m.loadUrl("about:blank");
        m.stopLoading();
        if (m.getHandler() != null)
            m.getHandler().removeCallbacksAndMessages(null);
        m.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) m.getParent())) != null)
            mViewGroup.removeView(m);
        m.setWebChromeClient(null);
        m.setWebViewClient(null);
        m.setTag(null);
        m.clearHistory();
        m.destroy();
        m = null;
    }

    private void setCachePath() {

        File cacheFile = new File(mWebView.getContext().getCacheDir(), "appcache_name");
        String path = cacheFile.getAbsolutePath();

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(path);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    public static void launch(Activity activity, String url, String title) {
        Intent intent = new Intent(activity, YouTubePlayerActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }
}
