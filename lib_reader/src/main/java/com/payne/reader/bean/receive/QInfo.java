package com.payne.reader.bean.receive;

import com.payne.reader.bean.config.QMode;

/**
 *
 */
public class QInfo extends Success {
    public QMode qMode;
    public byte qInit;
    public byte qMax;
    public byte qMin;
    public byte numMinQCycles = 1;
    public byte maxQuerySinceEPC;

    @Override
    public String toString() {
        return "QInfo{" +
                "qMode=" + qMode +
                ", qInit=" + qInit +
                ", qMax=" + qMax +
                ", qMin=" + qMin +
                ", numMinQCycles=" + numMinQCycles +
                ", maxQuerySinceEPC=" + maxQuerySinceEPC +
                '}';
    }
}
