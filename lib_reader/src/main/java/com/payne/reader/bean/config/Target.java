package com.payne.reader.bean.config;

/**
 * Inventory label Target type
 *
 * @author naz
 * Date 2020/7/14
 */
public enum Target {
    /**
     * Inventoried Flag A
     */
    A((byte) 0x00),
    /**
     * Inventoried Flag B
     */
    B((byte) 0x01);

    private final byte value;

    Target(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Target valueOf(byte value) {
        if (value == 0) {
            return A;
        }
        return B;
    }
}
