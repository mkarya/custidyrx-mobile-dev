package com.payne.reader.bean.config;

/**
 * 8 Antenna selection
 *
 * @author naz
 * Date 2020/7/24
 */
public enum EightAntenna {
    /**
     * No antenna, Setting to this value means not polling the current antenna
     */
    NONE((byte) 0xFF),
    /**
     * Antenna 1
     */
    ANT_A((byte) 0x00),
    /**
     * Antenna 2
     */
    ANT_B((byte) 0x01),
    /**
     * Antenna 3
     */
    ANT_C((byte) 0x02),
    /**
     * Antenna 4
     */
    ANT_D((byte) 0x03),
    /**
     * Antenna 5
     */
    ANT_E((byte) 0x04),
    /**
     * Antenna 6
     */
    ANT_F((byte) 0x05),
    /**
     * Antenna 7
     */
    ANT_G((byte) 0x06),
    /**
     * Antenna 8
     */
    ANT_H((byte) 0x07);

    private final byte value;

    EightAntenna(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static EightAntenna valueOf(byte value) {
        switch (value) {
            case 0x00:
                return ANT_A;
            case 0x01:
                return ANT_B;
            case 0x02:
                return ANT_C;
            case 0x03:
                return ANT_D;
            case 0x04:
                return ANT_E;
            case 0x05:
                return ANT_F;
            case 0x06:
                return ANT_G;
            case 0x07:
                return ANT_H;
            default:
                return NONE;
        }
    }
}
