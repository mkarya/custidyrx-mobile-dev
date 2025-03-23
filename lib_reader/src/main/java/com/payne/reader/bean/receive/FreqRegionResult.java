package com.payne.reader.bean.receive;

import com.payne.reader.bean.send.FreqNormal;
import com.payne.reader.bean.send.FreqUserDefine;

/**
 * @author naz
 * Date 2020/7/22
 */
public class FreqRegionResult extends Success {
    /**
     * Whether it is a user-defined frequency point
     */
    private boolean isUserDefine;
    /**
     * Default frequency
     */
    private FreqNormal freqNormal;
    /**
     * User-defined frequency point
     */
    private FreqUserDefine freqUserDefine;

    public boolean isUserDefine() {
        return isUserDefine;
    }

    public void setUserDefine(boolean userDefine) {
        isUserDefine = userDefine;
    }

    public FreqNormal getFreqNormal() {
        return freqNormal;
    }

    public void setFreqNormal(FreqNormal freqNormal) {
        this.freqNormal = freqNormal;
    }

    public FreqUserDefine getFreqUserDefine() {
        return freqUserDefine;
    }

    public void setFreqUserDefine(FreqUserDefine freqUserDefine) {
        this.freqUserDefine = freqUserDefine;
    }

    @Override
    public String toString() {
        return "FreqRegionResult{" +
                "isUserDefine=" + isUserDefine +
                ", freqNormal=" + (freqNormal == null ? "null" : freqNormal.toString()) +
                ", freqUserDefine=" + (freqUserDefine == null ? "null" : freqUserDefine.toString()) +
                '}';
    }
}
