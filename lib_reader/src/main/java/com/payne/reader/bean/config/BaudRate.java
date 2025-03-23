package com.payne.reader.bean.config;

/**
 * Baud Rate type
 *
 * @author naz
 * Date 2020/7/14
 */
public enum BaudRate {
    _38400((byte) 0x03),
    _115200((byte) 0x04),
    _256000((byte) 0x05),
    _512000((byte) 0x06),
    _750000((byte) 0x06),
    _921600((byte) 0x07);

    private final byte value;

    BaudRate(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
