package com.payne.reader.bean.receive;

import com.payne.reader.bean.config.FastTidType;

/**
 * @author naz
 * Date 2020/7/29
 */
public class ImpinjFastTid extends Success {
    /**
     * Monza TID switch status
     */
    private FastTidType tidType;

    public FastTidType getTidType() {
        return tidType;
    }

    public void setTidType(FastTidType tidType) {
        this.tidType = tidType;
    }

    @Override
    public String toString() {
        return "ImpinjFastTid{" +
                "tidType=" + tidType.toString() +
                '}';
    }
}
