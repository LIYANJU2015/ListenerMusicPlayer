package io.hefuyi.listener.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.MusicPlayer;
import io.hefuyi.listener.R;
import io.hefuyi.listener.RxBus;
import io.hefuyi.listener.event.MetaChangedEvent;
import io.hefuyi.listener.mvp.model.EqualizerModel;
import io.hefuyi.listener.util.ATEUtil;
import io.hefuyi.listener.util.ListenerUtil;
import io.hefuyi.listener.util.NavigationUtil;
import io.hefuyi.listener.util.PreferencesUtility;
import io.hefuyi.listener.widget.AnalogController;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liyanju on 2017/12/11.
 */

public class EqualizerActivity extends BaseActivity {

    private ImageView backBtn;

    private LineSet dataset = new LineSet();
    private LineChartView chart;
    private Paint paint = new Paint();

    private int y = 0;

    private ImageView spinnerDropDownIcon;

    private short numberOfFrequencyBands;

    private SeekBar[] seekBarFinal = new SeekBar[5];

    /**
     *  bass 低音
     */
    private AnalogController bassController;

    /**
     * 3D
     */
    private AnalogController reverbController;

    private Spinner presetSpinner;

    private FrameLayout equalizerBlocker;

    private Context ctx;

    public static int themeColor = Color.parseColor("#B24242");

    /**
     * 示波器
     */
    private Visualizer mVisualizer;

    /**
     * 均衡器
     */
    private Equalizer mEqualizer;
    /**
     * 重低音调节器
     */
    private BassBoost bassBoost;
    /**
     * 预设音场控制器
     */
    private PresetReverb presetReverb;

    private SwitchCompat equalizerSwitch;

    private int[] seekbarpos = new int[5];

    public static int screen_width;
    public static int screen_height;

    private float ratio;
    private float ratio2;

    private static EqualizerModel sEqualizerModel = null;

    @Override
    public boolean onIsStatusBarTransparent() {
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, EqualizerActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    private void subscribeMetaChangedEvent() {
        Subscription subscription = RxBus.getInstance()
                .toObservable(MetaChangedEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(new Action1<MetaChangedEvent>() {
                    @Override
                    public void call(MetaChangedEvent event) {
                        onPlayStateChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        RxBus.getInstance().addSubscription(this, subscription);
    }

    public void onPlayStateChanged() {
        if (sEqualizerModel == null || !sEqualizerModel.isEqualizerEnabled()) {
            return;
        }
        if (MusicPlayer.isPlaying()) {
            setupVisualizerFxAndUI();
        }
    }

    private boolean initVisualizer() {
        try {
            Log.v("xx", "initVisualizer getAudioSessionId " + MusicPlayer.getAudioSessionId());
            mVisualizer = new Visualizer(MusicPlayer.getAudioSessionId());
            mEqualizer = new Equalizer(0, MusicPlayer.getAudioSessionId());
            mEqualizer.setEnabled(sEqualizerModel.isEqualizerEnabled());

            try {
                bassBoost = new BassBoost(0, MusicPlayer.getAudioSessionId());
                bassBoost.setEnabled(sEqualizerModel.isEqualizerEnabled());
                BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
                BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
                bassBoostSetting.strength = (1000 / 19);
                bassBoost.setProperties(bassBoostSetting);

                presetReverb = new PresetReverb(0, MusicPlayer.getAudioSessionId());
                presetReverb.setPreset(PresetReverb.PRESET_NONE);
                presetReverb.setEnabled(sEqualizerModel.isEqualizerEnabled());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void setupVisualizerFxAndUI() {
        if (!initVisualizer()) {
            Toast.makeText(this, getResources().getString(R.string.no_audio_ID), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            bassBoost.setEnabled(true);
            BassBoost.Settings bassBoostSettingTemp = bassBoost.getProperties();
            BassBoost.Settings bassBoostSetting = new BassBoost.Settings(bassBoostSettingTemp.toString());
            if (sEqualizerModel.getBassStrength() == -1) {
                bassBoostSetting.strength = (1000 / 19);
            } else {
                bassBoostSetting.strength = sEqualizerModel.getBassStrength();
            }
            bassBoost.setProperties(bassBoostSetting);

            presetReverb.setPreset(sEqualizerModel.getReverbPreset());

            presetReverb.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int pos = sEqualizerModel.getPresetPos();
            if (pos != 0) {
                mEqualizer.usePreset((short) (pos - 1));
            } else {
                for (short i = 0; i < 5; i++) {
                    mEqualizer.setBandLevel(i, (short) sEqualizerModel.getSeekbarpos()[i]);
                }
            }
            if (sEqualizerModel.getBassStrength() != -1 && sEqualizerModel.getReverbPreset() != -1) {
                bassBoost.setEnabled(true);
                bassBoost.setStrength(sEqualizerModel.getBassStrength());
                presetReverb.setEnabled(true);
                presetReverb.setPreset(sEqualizerModel.getReverbPreset());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            mVisualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        public void onWaveFormDataCapture(Visualizer visualizer,
                                                          byte[] bytes, int samplingRate) {
                        }

                        public void onFftDataCapture(Visualizer visualizer,
                                                     byte[] bytes, int samplingRate) {
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBassAndReverbController() {
        int bassStrenght = sEqualizerModel.getBassStrength();
        if (bassStrenght != -1) {
            int x = ((bassStrenght * 19) / 1000);
            if (x == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(x);
            }
        } else {
            bassController.setProgress(1);
        }

        short reverbPreset = sEqualizerModel.getReverbPreset();
        int y = (reverbPreset * 19) / 6;
        if (y == 0) {
            reverbController.setProgress(1);
        } else {
            reverbController.setProgress(y);
        }
    }

    private void openEqualizer() {
        sEqualizerModel.setEqualizerEnabled(true);
        if (sEqualizerModel.getPresetPos() != 0) {
            mEqualizer.usePreset((short) (sEqualizerModel.getPresetPos() - 1));
        } else {
            for (short i = 0; i < 5; i++) {
                mEqualizer.setBandLevel(i, (short) seekbarpos[i]);
            }
            if (sEqualizerModel.getBassStrength() != -1) {
                bassBoost.setEnabled(true);
                bassBoost.setStrength(sEqualizerModel.getBassStrength());
                presetReverb.setEnabled(true);
                presetReverb.setPreset(sEqualizerModel.getReverbPreset());
            }
        }
    }

    private void closeEqualizer() {
        sEqualizerModel.setEqualizerEnabled(false);
        mEqualizer.usePreset((short) 0);
        bassBoost.setStrength((short) (((float) 1000 / 19) * (1)));
        presetReverb.setPreset((short) 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#292930"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equalizer_layout);
        ctx = getApplication();
        themeColor = ATEUtil.getThemePrimaryColor(ListenerApp.sContext);

        if (sEqualizerModel == null) {
            sEqualizerModel = PreferencesUtility.getInstance(ctx).getEqualizerModel();
        }

        if (!initVisualizer()) {
            Toast.makeText(this, getResources().getString(R.string.no_audio_ID), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screen_width = display.getWidth();
        screen_height = display.getHeight();
        ratio = (float) screen_height / (float) 1920;
        ratio2 = (float) screen_width / (float) 1080;
        ratio = Math.min(ratio, ratio2);

        ImageView settingIV = (ImageView) findViewById(R.id.setting_iv);
        settingIV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NavigationUtil.navigateToEqualizer(EqualizerActivity.this);
            }
        });
        equalizerSwitch = (SwitchCompat) findViewById(R.id.equalizer_switch);
        equalizerSwitch.setChecked(sEqualizerModel.isEqualizerEnabled());
        equalizerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openEqualizer();
                    equalizerBlocker.setVisibility(View.GONE);
                } else {
                    closeEqualizer();
                    equalizerBlocker.setVisibility(View.VISIBLE);
                }
            }
        });
        equalizerBlocker = (FrameLayout) findViewById(R.id.equalizerBlocker);
        if (!sEqualizerModel.isEqualizerEnabled()) {
            equalizerBlocker.setVisibility(View.VISIBLE);
        } else {
            equalizerBlocker.setVisibility(View.GONE);
        }

        backBtn = (ImageView) findViewById(R.id.equalizer_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinnerDropDownIcon = (ImageView) findViewById(R.id.spinner_dropdown_icon);
        spinnerDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presetSpinner.performClick();
            }
        });

        presetSpinner = (Spinner) findViewById(R.id.equalizer_preset_spinner);

        chart = (LineChartView) findViewById(R.id.lineChart);

        bassController = (AnalogController) findViewById(R.id.controllerBass);
        reverbController = (AnalogController) findViewById(R.id.controller3D);

        bassController.setLabel("BASS");
        reverbController.setLabel("3D");

        bassController.circlePaint2.setColor(themeColor);
        bassController.linePaint.setColor(themeColor);
        bassController.invalidate();
        reverbController.circlePaint2.setColor(themeColor);
        bassController.linePaint.setColor(themeColor);
        reverbController.invalidate();

        initBassAndReverbController();

        bassController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                short bassStrength = (short) (((float) 1000 / 19) * (progress));
                try {
                    bassBoost.setStrength(bassStrength);
                    sEqualizerModel.setBassStrength(bassStrength);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        reverbController.setOnProgressChangedListener(new AnalogController.onProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress) {
                short reverbPreset = (short) ((progress * 6) / 19);
                try {
                    presetReverb.setPreset(reverbPreset);
                    sEqualizerModel.setReverbPreset(reverbPreset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                y = progress;
            }
        });

        initVerticalSeekBar();

        equalizeSound();

        initLineChartView();

        subscribeMetaChangedEvent();
    }

    private void initVerticalSeekBar() {
        numberOfFrequencyBands = 5;

        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < numberOfFrequencyBands; i++) {
            final short equalizerBandIndex = i;

            SeekBar seekBar = null;
            TextView textView = null;
            switch (i) {
                case 0:
                    seekBar = (SeekBar) findViewById(R.id.seekBar1);
                    textView = (TextView) findViewById(R.id.textView1);
                    break;
                case 1:
                    seekBar = (SeekBar) findViewById(R.id.seekBar2);
                    textView = (TextView) findViewById(R.id.textView2);
                    break;
                case 2:
                    seekBar = (SeekBar) findViewById(R.id.seekBar3);
                    textView = (TextView) findViewById(R.id.textView3);
                    break;
                case 3:
                    seekBar = (SeekBar) findViewById(R.id.seekBar4);
                    textView = (TextView) findViewById(R.id.textView4);
                    break;
                case 4:
                    seekBar = (SeekBar) findViewById(R.id.seekBar5);
                    textView = (TextView) findViewById(R.id.textView5);
                    break;
            }
            seekBarFinal[i] = seekBar;
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN));
            seekBar.getThumb().setColorFilter(new PorterDuffColorFilter(themeColor, PorterDuff.Mode.SRC_IN));
            seekBar.setId(i);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

            textView.setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + "Hz");

            if (sEqualizerModel.getSeekbarpos()[i] != 0) {
                sEqualizerModel.getPoints()[i] = sEqualizerModel.getSeekbarpos()[i] - lowerEqualizerBandLevel;
                dataset.addPoint(textView.getText().toString(), sEqualizerModel.getPoints()[i]);
                seekBar.setProgress(sEqualizerModel.getSeekbarpos()[i] - lowerEqualizerBandLevel);
            } else {
                sEqualizerModel.getPoints()[i] = mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                dataset.addPoint(textView.getText().toString(), sEqualizerModel.getPoints()[i]);
                seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                seekbarpos[i] = mEqualizer.getBandLevel(equalizerBandIndex);
            }
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                    sEqualizerModel.getPoints()[seekBar.getId()] = mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel;
                    seekbarpos[seekBar.getId()] = (progress + lowerEqualizerBandLevel);
                    sEqualizerModel.getSeekbarpos()[seekBar.getId()] = (progress + lowerEqualizerBandLevel);

                    dataset.updateValues(sEqualizerModel.getPoints());
                    chart.notifyDataUpdate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    presetSpinner.setSelection(0);
                    sEqualizerModel.setPresetPos(0);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    private void initLineChartView() {
        paint.setColor(Color.parseColor("#555555"));
        paint.setStrokeWidth((float) (1.10 * ratio));

        dataset.setColor(themeColor);
        dataset.setSmooth(true);
        dataset.setThickness(5);

        chart.setXAxis(false);
        chart.setYAxis(false);

        chart.setYLabels(AxisRenderer.LabelPosition.NONE);
        chart.setXLabels(AxisRenderer.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.NONE, 7, 10, paint);

        chart.setAxisBorderValues(-300, 3300);

        chart.addData(dataset);
        chart.show();
    }

    public void equalizeSound() {
        ArrayList<String> equalizerPresetNames = new ArrayList<>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2);
        equalizerPresetNames.add("Custom");

        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);
        presetSpinner.setSelection(sEqualizerModel.getPresetPos());

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0) {
                        mEqualizer.usePreset((short) (position - 1));
                        sEqualizerModel.setPresetPos(position);
                        short numberOfFreqBands = 5;

                        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];

                        for (short i = 0; i < numberOfFreqBands; i++) {
                            seekBarFinal[i].setProgress(mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel);
                            sEqualizerModel.getPoints()[i] = mEqualizer.getBandLevel(i) - lowerEqualizerBandLevel;
                            seekbarpos[i] = mEqualizer.getBandLevel(i);
                            sEqualizerModel.getSeekbarpos()[i] = mEqualizer.getBandLevel(i);
                        }
                        dataset.updateValues(sEqualizerModel.getPoints());
                        chart.notifyDataUpdate();
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, "Error while updating Equalizer", Toast.LENGTH_SHORT).show();
                }
                sEqualizerModel.setPresetPos(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListenerUtil.runSingleThread(new Runnable() {
            @Override
            public void run() {
                PreferencesUtility.getInstance(ctx).saveEqualizerModel(sEqualizerModel);
            }
        });
    }
}
