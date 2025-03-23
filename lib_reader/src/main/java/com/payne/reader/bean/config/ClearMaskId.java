package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/8/5
 */
public enum ClearMaskId {
    /**
     * tag mask all
     */
    TAG_MASK_ALL(0x00),
    /**
     * tag mask No.1
     */
    TAG_MASK_NO1(0x01),
    /**
     * tag mask No.2
     */
    TAG_MASK_NO2(0x02),
    /**
     * tag mask No.3
     */
    TAG_MASK_NO3(0x03),
    /**
     * tag mask No.4
     */
    TAG_MASK_NO4(0x04),
    /**
     * tag mask No.5
     */
    TAG_MASK_NO5(0x05);

    private final byte value;

    ClearMaskId(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static ClearMaskId valueOf(byte value) {
        switch (value) {
            case 0x01:
                return TAG_MASK_NO1;
            case 0x02:
                return TAG_MASK_NO2;
            case 0x03:
                return TAG_MASK_NO3;
            case 0x04:
                return TAG_MASK_NO4;
            case 0x05:
                return TAG_MASK_NO5;
            default:
                return TAG_MASK_ALL;
        }
    }
}
