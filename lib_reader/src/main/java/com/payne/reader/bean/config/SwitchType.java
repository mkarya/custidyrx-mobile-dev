package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/14
 */
public enum SwitchType {
    /**
     * Close function
     */
    CLOSE((byte) 0x00),
    /**
     * Open function
     */
    OPEN((byte) 0x01);

    private final byte value;

    SwitchType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static byte getValue(boolean enable) {
        return enable ? OPEN.value : CLOSE.value;
    }
}
