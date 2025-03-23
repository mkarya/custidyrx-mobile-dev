package com.payne.reader.bean.config;

/**
 * Spectrum regulation(0x01:FCC, 0x02:ETSI, 0x03:CHN)
 *
 * @author naz
 * Date 2020/7/14
 */
public enum Region {
    /**
     * Radio Frequency Specification FCC
     */
    FCC((byte) 0x01),
    /**
     * Radio Frequency Specification ETSI
     */
    ETSI((byte) 0x02),
    /**
     * Radio Frequency Specification CHN
     */
    CHN((byte) 0x03);

    private final byte value;

    Region(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Region valueOf(byte value) {
        switch (value) {
            case 0x02:
                return ETSI;
            case 0x03:
                return CHN;
            default:
                return FCC;
        }
    }
}
