package com.payne.reader.bean.config;

public enum MemBankTm670 {
    /**
     * Tid
     */
    TID((byte) 0x00),
    /**
     * Epc
     */
    EPC((byte) 0x10),
    /**
     * Reserved
     */
    RESERVED((byte) 0x20),
    /**
     * User
     */
    USER((byte) 0x30);

    private final byte value;

    MemBankTm670(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static MemBankTm670 valueOf(byte value) {
        switch (value) {
            case 0x00:
                return TID;
            case 0x10:
                return EPC;
            case 0x20:
                return RESERVED;
            default:
                return USER;
        }
    }
}
