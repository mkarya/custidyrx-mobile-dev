package com.payne.reader.bean.send;

import com.payne.reader.base.TempLabel2Info;
import com.payne.reader.bean.config.TagMeasOpt;

import java.util.Objects;

/**
 * @author naz
 * Date 2020/12/18
 */
public class TempLabel2Config {
    private final byte[] mData;
    /**
     * Read memory length
     */
    private int mReadMemoryLen = 0;
    /**
     * Single temperature measurement type
     */
    private TagMeasOpt mTagMeasOpt = null;

    public TempLabel2Config(TempLabel2Info tempLabel2Info) {
        Objects.requireNonNull(tempLabel2Info);
        if (tempLabel2Info instanceof MtSingleMeasTemp) {
            MtSingleMeasTemp measTemp = (MtSingleMeasTemp) tempLabel2Info;
            this.mTagMeasOpt = measTemp.getTagMeasOpt();
        } else if (tempLabel2Info instanceof MtReadMemory) {
            MtReadMemory measTemp = (MtReadMemory) tempLabel2Info;
            this.mReadMemoryLen = measTemp.getReadLength();
        }
        this.mData = tempLabel2Info.getTempLabel2Info();
    }

    public byte[] getData() {
        return this.mData;
    }

    /**
     * Temperature label 2 flag
     */
    public byte getTempLabel2Flag() {
        return this.mData[0];
    }

    public int getReadMemoryLen() {
        return this.mReadMemoryLen;
    }

    public TagMeasOpt getTagMeasOpt() {
        return this.mTagMeasOpt;
    }
}
