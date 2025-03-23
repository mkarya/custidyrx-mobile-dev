package com.payne.reader.bean.config;

/**
 * Tag memory bank
 *
 * @author naz
 * Date 2020/7/14
 */
public enum LockMemBank {
    /**
     * User Memory
     */
    USER_MEMORY((byte) 0x01),
    /**
     * TID Memory
     */
    TID_MEMORY((byte) 0x02),
    /**
     * EPC Memory
     */
    EPC_MEMORY((byte) 0x03),
    /**
     * Access Password
     */
    ACCESS_PASSWORD((byte) 0x04),
    /**
     * Kill Password
     */
    KILL_PASSWORD((byte) 0x05);

    private final byte value;

    LockMemBank(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static LockMemBank valueOf(byte value) {
        switch (value) {
            case 0x02:
                return TID_MEMORY;
            case 0x03:
                return EPC_MEMORY;
            case 0x04:
                return ACCESS_PASSWORD;
            case 0x05:
                return KILL_PASSWORD;
            default:
                return USER_MEMORY;
        }
    }
}
