package com.payne.reader.bean.config;

/**
 * Tag memory bank
 *
 * @author naz
 * Date 2020/7/14
 */
public enum MemBank {
    /**
     * Reserved
     */
    RESERVED((byte) 0x00),
    /**
     * Epc
     */
    EPC((byte) 0x01),
    /**
     * Tid
     */
    TID((byte) 0x02),
    /**
     * User
     */
    USER((byte) 0x03);

    private final byte value;

    MemBank(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static MemBank valueOf(byte value) {
        switch (value) {
            case 0x01:
                return EPC;
            case 0x02:
                return TID;
            case 0x03:
                return USER;
            default:
                return RESERVED;
        }
    }
}
