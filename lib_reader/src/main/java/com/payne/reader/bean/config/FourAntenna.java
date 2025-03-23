package com.payne.reader.bean.config;

/**
 * 4 Antenna selection
 *
 * @author naz
 * Date 2020/7/24
 */
public enum FourAntenna {
    /**
     * No antenna
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
    ANT_D((byte) 0x03);

    private final byte value;

    FourAntenna(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static FourAntenna valueOf(byte value) {
        switch (value) {
            case 0x00:
                return ANT_A;
            case 0x01:
                return ANT_B;
            case 0x02:
                return ANT_C;
            case 0x03:
                return ANT_D;
            default:
                return NONE;
        }
    }
}
