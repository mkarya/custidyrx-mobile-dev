package com.payne.reader.bean.config;

/**
 * Label locking method
 *
 * @author naz
 * Date 2020/7/14
 */
public enum LockType {
    /**
     * open
     */
    OPEN((byte) 0x00),
    /**
     * locking
     */
    LOCK((byte) 0x01),
    /**
     * Open permanently
     */
    PERMANENT_OPEN((byte) 0x02),
    /**
     * Permanently locked
     */
    PERMANENT_LOCK((byte) 0x03),
    /**
     * Permanently lock R6 tags
     */
    PERMANENT_LOCK_R6((byte) 0x06);

    private final byte value;

    LockType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static LockType valueOf(byte value) {
        switch (value) {
            case 0x01:
                return LOCK;
            case 0x02:
                return PERMANENT_OPEN;
            case 0x03:
                return PERMANENT_LOCK;
            case 0x06:
                return PERMANENT_LOCK_R6;
            default:
                return OPEN;
        }
    }
}
