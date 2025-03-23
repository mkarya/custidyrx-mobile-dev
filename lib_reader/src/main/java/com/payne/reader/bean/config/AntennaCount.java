package com.payne.reader.bean.config;

/**
 * Module antenna channel number
 *
 * @author naz
 * Date 2020/7/29
 */
public enum AntennaCount {
    /**
     * Single channel antenna port
     */
    SINGLE_CHANNEL(1),
    /**
     * Four-channel antenna port
     */
    FOUR_CHANNELS(4),
    /**
     * Eight channel antenna port
     */
    EIGHT_CHANNELS(8),
    /**
     * 16-channel antenna port
     */
    SIXTEEN_CHANNELS(16);

    private final int value;

    AntennaCount(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
