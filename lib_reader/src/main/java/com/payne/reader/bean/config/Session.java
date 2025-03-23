package com.payne.reader.bean.config;

/**
 * Session type
 *
 * @author naz
 * Date 2020/7/14
 */
public enum Session {
    /**
     * Session 0
     */
    S0((byte) 0x00),
    /**
     * Session 1
     */
    S1((byte) 0x01),
    /**
     * Session 2
     */
    S2((byte) 0x02),
    /**
     * Session 3
     */
    S3((byte) 0x03);

    private final byte value;

    Session(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Session valueOf(byte value) {
        switch (value) {
            case 0x01:
                return S1;
            case 0x02:
                return S2;
            case 0x03:
                return S3;
            default:
                return S0;
        }
    }
}
