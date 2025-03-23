package com.payne.reader.bean.config;

/**
 * Type of buzzer sound when operating the label
 * (0x00:Quiet, 0x01:Beep after every inventory round if tag(s) identified,
 * 0x02:Beep after every tag has identified.)
 *
 * @author naz
 * Date 2020/7/14
 */
public enum Beeper {
    /**
     * be quiet
     */
    QUIET(0x00),
    /**
     * Sound after each inventory
     */
    ONCE_END_BEEP(0x01),
    /**
     * Every time a tag is read, it sounds
     */
    PER_TAG_BEEP(0x02);

    private final byte value;

    Beeper(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
