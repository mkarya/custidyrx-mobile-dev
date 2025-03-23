package com.payne.reader.bean.config;

/**
 *
 */
public enum QMode {
    FIXED((byte) 0x0),
    DYNAMIC((byte) 0x1);

    byte v;

    QMode(byte i) {
        v = i;
    }

    public byte getValue() {
        return v;
    }

    public static QMode valueOf(byte v) {
        QMode[] values = values();

        for (QMode qMode : values) {
            if (qMode.v == v) {
                return qMode;
            }
        }
        return null;
    }
}
