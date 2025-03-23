package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/25
 */
public enum SelectFlag {
    /**
     * Disable this function, the data sent after disabling will not carry this parameter
     */
    DISABLE((byte)0xFF),
    /**
     * Select Flag 00
     */
    SL0((byte) 0x00),
    /**
     * Select Flag 01
     */
    SL1((byte) 0x01),
    /**
     * Select Flag 02
     */
    SL2((byte) 0x02),
    /**
     * Select Flag 03
     */
    SL3((byte) 0x03);

    private final byte value;

    SelectFlag(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
