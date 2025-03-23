package com.payne.reader.bean.config;

/**
 * 4 Antenna selection
 *
 * @author naz
 * Date 2020/7/24
 */
public enum SingleAntenna {
    /**
     * No antenna
     */
    NONE((byte) 0xFF),
    /**
     * Antenna 1
     */
    ANT_A((byte) 0x00);

    private final byte value;

    SingleAntenna(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
