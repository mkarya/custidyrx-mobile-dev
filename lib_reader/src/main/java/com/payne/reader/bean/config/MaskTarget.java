package com.payne.reader.bean.config;

/**
 * Set the inventory way(s0,s1,s2 or s3 , SL),you must use you set
 * target to inventory the tag.
 *
 * @author naz
 * Date 2020/7/14
 */
public enum MaskTarget {
    /**
     * Inventoried S0
     */
    Inventoried_S0(0x00),
    /**
     * Inventoried S1
     */
    Inventoried_S1(0x01),
    /**
     * Inventoried S2
     */
    Inventoried_S2(0x02),
    /**
     * Inventoried S3
     */
    Inventoried_S3(0x03),
    /**
     * SL
     */
    SL(0x04),
    /**
     * Johar tag
     */
    JOHAR(0x07);

    private final byte value;

    MaskTarget(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static MaskTarget valueOf(byte value) {
        switch (value) {
            case 0x01:
                return Inventoried_S1;
            case 0x02:
                return Inventoried_S2;
            case 0x03:
                return Inventoried_S3;
            case 0x04:
                return SL;
            case 0x07:
                return JOHAR;
            default:
                return Inventoried_S0;
        }
    }
}
