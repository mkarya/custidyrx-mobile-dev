package com.payne.reader.bean.config;

/**
 * 8 Antenna selection
 *
 * @author naz
 * Date 2020/7/24
 */
public enum HighEightAntenna {
    /**
     * No antenna, Setting to this value means not polling the current antenna
     */
    NONE((byte) 0xFF),
    /**
     * Antenna 1
     */
    ANT_I((byte) 0x00),
    /**
     * Antenna 2
     */
    ANT_J((byte) 0x01),
    /**
     * Antenna 3
     */
    ANT_K((byte) 0x02),
    /**
     * Antenna 4
     */
    ANT_L((byte) 0x03),
    /**
     * Antenna 5
     */
    ANT_M((byte) 0x04),
    /**
     * Antenna 6
     */
    ANT_N((byte) 0x05),
    /**
     * Antenna 7
     */
    ANT_O((byte) 0x06),
    /**
     * Antenna 8
     */
    ANT_P((byte) 0x07);

    private final byte value;

    HighEightAntenna(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static HighEightAntenna valueOf(byte value) {
        switch (value) {
            case 0x00:
                return ANT_I;
            case 0x01:
                return ANT_J;
            case 0x02:
                return ANT_K;
            case 0x03:
                return ANT_L;
            case 0x04:
                return ANT_M;
            case 0x05:
                return ANT_N;
            case 0x06:
                return ANT_O;
            case 0x07:
                return ANT_P;
            default:
                return NONE;
        }
    }
}
