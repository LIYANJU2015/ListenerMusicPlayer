package io.hefuyi.listener.mvp.model;

import android.media.audiofx.PresetReverb;

import java.io.Serializable;

/**
 * Created by liyanju on 2017/11/30.
 */

public class EqualizerModel implements Serializable {

    private boolean isEqualizerEnabled;

    private int[] seekbarpos = new int[5];

    /**
     * 下拉列表postion选项
     */
    private int presetPos = 0;

    /**
     * 3D 值
     */
    private short reverbPreset = PresetReverb.PRESET_NONE;

    /**
     * -1 setStrength 不能设置
     * bass 低音值
     */
    private short bassStrength = -1;

    private float[] points = new float[5];

    public float[] getPoints() {
        return points;
    }

    public boolean isEqualizerEnabled() {
        return isEqualizerEnabled;
    }

    public void setEqualizerEnabled(boolean equalizerEnabled) {
        isEqualizerEnabled = equalizerEnabled;
    }

    public int[] getSeekbarpos() {
        return seekbarpos;
    }

    public void setSeekbarpos(int[] seekbarpos) {
        this.seekbarpos = seekbarpos;
    }

    public int getPresetPos() {
        return presetPos;
    }

    public void setPresetPos(int presetPos) {
        this.presetPos = presetPos;
    }

    public short getReverbPreset() {
        return reverbPreset;
    }

    public void setReverbPreset(short reverbPreset) {
        this.reverbPreset = reverbPreset;
    }

    public short getBassStrength() {
        return bassStrength;
    }

    public void setBassStrength(short bassStrength) {
        this.bassStrength = bassStrength;
    }
}
