package com.payne.reader.bean.config;

/**
 * Label locking method
 *
 * @author naz
 * Date 2020/7/14
 */
public enum FastTidType {
    /**
     * Disable
     */
    DISABLE((byte) 0x00),
    /**
     * Focus tag
     */
    FOCUS_TAG((byte) 0x8C),
    /**
     * Fast tag
     */
    FAST_TAG((byte) 0x8D),
    /**
     * Johar tag
     */
    JOHAR_TAG((byte) 0x90);

    private final byte value;

    FastTidType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static FastTidType valueOf(byte value) {
        switch (value) {
            case (byte) 0x8C:
                return FOCUS_TAG;
            case (byte) 0x8D:
                return FAST_TAG;
            case (byte) 0x90:
                return JOHAR_TAG;
            default:
                return DISABLE;
        }
    }
}
