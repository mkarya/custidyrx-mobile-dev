package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/29
 */
public enum ReadMode {
    /**
     * Single label mode, no session control;
     * The fastest speed. Target and Session values will be ignored
     */
    MODE1((byte) 0x00),
    /**
     * Single label mode with session control
     */
    MODE2((byte) 0x01),
    /**
     * Multi-tab mode with session control
     */
    MODE3((byte) 0x02),

    MODE20((byte) 0x20);

    private final byte value;

    ReadMode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ReadMode valueOf(byte value) {
        switch (value) {
            case 0x01:
                return MODE2;
            case 0x02:
                return MODE3;
            case 0x20:
                return MODE20;
            default:
                return MODE1;
        }
    }
}
