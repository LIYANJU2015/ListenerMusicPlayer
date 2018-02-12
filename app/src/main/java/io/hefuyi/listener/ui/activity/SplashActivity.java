package io.hefuyi.listener.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admodule.AdModule;
import com.admodule.adfb.IFacebookAd;
import com.facebook.ads.NativeAd;
import com.githang.statusbar.StatusBarCompat;

import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.R;

/**
 * Created by liyanju on 2017/12/11.
 */

public class SplashActivity extends AppCompatActivity implements IFacebookAd.FacebookAdListener {

    private LinearLayout adContainerLinear;

    private TextView countDownTV;

    private ImageView logoIV;

    private View adView;

    private CountDownTimer countDownTimer = new CountDownTimer(6 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            countDownTV.setText(String.valueOf(millisUntilFinished / 1000) + " SKIP");
        }

        @Override
        public void onFinish() {
            countDownTV.setText("0 SKIP");
            startMain();
        }
    };

    @Override
    public void onLoadedAd(NativeAd nativeAd) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (logoIV != null) {
            logoIV.animate().cancel();
        }
        AdModule.getInstance().getFacebookAd().cancelLoadListener();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.alpha_out);
    }

    @Override
    public void onLoadedAd(View view) {
        adView = view;
    }

    @Override
    public void onStartLoadAd(View view) {

    }

    @Override
    public void onLoadAdFailed(int i, String s) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_layout);

        if (!ListenerApp.sIsColdLaunch) {
            ListenerApp.sIsColdLaunch = false;
            ListenerApp.sIsAdDialog = true;
            startMain();
            return;
        }


        adContainerLinear = (LinearLayout) findViewById(R.id.ad_container_frame);
        countDownTV = (TextView) findViewById(R.id.count_down_tv);
        countDownTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startMain();
            }
        });
        logoIV = (ImageView) findViewById(R.id.logo_iv);

        AdModule.getInstance().getFacebookAd().setLoadListener(this);
        AdModule.getInstance().getFacebookAd().loadAd(false, "200998730474227_201002143807219");

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(logoIV, "Alpha", 0f, 1f);
        objectAnimator.setDuration(3500);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (adView != null) {
                    logoIV.animate().alpha(0f).setDuration(100)
                            .setListener(null).start();

                    super.onAnimationEnd(animation);
                    startCountDown();
                    adContainerLinear.removeAllViews();
                    adContainerLinear.addView(adView);
                    AdModule.getInstance().getFacebookAd().cancelLoadListener();
                    AdModule.getInstance().getFacebookAd().loadAd(true, "200998730474227_201002260473874");
                } else {
                    startMain();
                }
            }
        });
        objectAnimator.start();
    }

    private void startCountDown() {
        countDownTV.setText("6 SKIP");
        countDownTV.setVisibility(View.VISIBLE);
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void startMain() {
        finish();
        ListenerApp.sIsColdLaunch = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
