package com.payne.reader.bean.config;

/**
 * Set mask function type
 *
 * @author naz
 * Date 2020/7/14
 */
public enum MaskId {
    /**
     * tag mask No.1
     */
    TAG_MASK_NO1((byte) 0x01),
    /**
     * tag mask No.2
     */
    TAG_MASK_NO2((byte) 0x02),
    /**
     * tag mask No.3
     */
    TAG_MASK_NO3((byte) 0x03),
    /**
     * tag mask No.4
     */
    TAG_MASK_NO4((byte) 0x04),
    /**
     * tag mask No.5
     */
    TAG_MASK_NO5((byte) 0x05);

    private final byte value;

    MaskId(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static MaskId valueOf(byte value) {
        switch (value) {
            case 0x02:
                return TAG_MASK_NO2;
            case 0x03:
                return TAG_MASK_NO3;
            case 0x04:
                return TAG_MASK_NO4;
            case 0x05:
                return TAG_MASK_NO5;
            default:
                return TAG_MASK_NO1;
        }
    }
}
