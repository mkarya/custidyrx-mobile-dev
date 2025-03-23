package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/21
 */
public enum GpioInType {
    /**
     * GPIO port 3
     */
    Gpio3((byte) 0x03),
    /**
     * GPIO port 4
     */
    Gpio4((byte) 0x04);

    private final byte value;

    GpioInType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
