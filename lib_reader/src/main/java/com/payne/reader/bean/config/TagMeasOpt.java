package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/29
 */
public enum TagMeasOpt {
    /**
     * Measure temperature
     */
    MeasTemp(0x04),
    /**
     * Measure battery voltage
     */
    MeasVolt(0x10),
    /**
     * Measure external temperature sensor voltage
     */
    MeasExTempVolt(0x20),
    /**
     * Measure external pressure sensor voltage
     */
    MeasExPressVolt(0x30);

    private final int value;

    TagMeasOpt(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TagMeasOpt valueOf(int value) {
        switch (value) {
            case 0x10:
                return MeasVolt;
            case 0x20:
                return MeasExTempVolt;
            case 0x30:
                return MeasExPressVolt;
            default:
                return MeasTemp;
        }
    }
}
