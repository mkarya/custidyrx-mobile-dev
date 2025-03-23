package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/21
 */
public enum Freq {
    /**
     * 865.00 MHz
     */
    _865000((byte) 0x00),
    /**
     * 865.50 MHz
     */
    _865500((byte) 0x01),
    /**
     * 866.00 MHz
     */
    _866000((byte) 0x02),
    /**
     * 866.50 MHz
     */
    _866500((byte) 0x03),
    /**
     * 867.00 MHz
     */
    _867000((byte) 0x04),
    /**
     * 867.50 MHz
     */
    _867500((byte) 0x05),
    /**
     * 868.00 MHz
     */
    _868000((byte) 0x06),
    /**
     * 902.00 MHz
     */
    _902000((byte) 0x07),
    /**
     * 902.50 MHz
     */
    _902500((byte) 0x08),
    /**
     * 903.00 MHz
     */
    _903000((byte) 0x09),
    /**
     * 903.50 MHz
     */
    _903500((byte) 0x0A),
    /**
     * 904.00 MHz
     */
    _904000((byte) 0x0B),
    /**
     * 904.50 MHz
     */
    _904500((byte) 0x0C),
    /**
     * 905.00 MHz
     */
    _905000((byte) 0x0D),
    /**
     * 905.50 MHz
     */
    _905500((byte) 0x0E),
    /**
     * 906.00 MHz
     */
    _906000((byte) 0x0F),
    /**
     * 906.50 MHz
     */
    _906500((byte) 0x10),
    /**
     * 907.00 MHz
     */
    _907000((byte) 0x11),
    /**
     * 907.50 MHz
     */
    _907500((byte) 0x12),
    /**
     * 908.00 MHz
     */
    _908000((byte) 0x13),
    /**
     * 908.50 MHz
     */
    _908500((byte) 0x14),
    /**
     * 909.00 MHz
     */
    _909000((byte) 0x15),
    /**
     * 909.50 MHz
     */
    _909500((byte) 0x16),
    /**
     * 910.00 MHz
     */
    _910000((byte) 0x17),
    /**
     * 910.50 MHz
     */
    _910500((byte) 0x18),
    /**
     * 911.00 MHz
     */
    _911000((byte) 0x19),
    /**
     * 911.50 MHz
     */
    _911500((byte) 0x1A),
    /**
     * 912.00 MHz
     */
    _912000((byte) 0x1B),
    /**
     * 912.50 MHz
     */
    _912500((byte) 0x1C),
    /**
     * 913.00 MHz
     */
    _913000((byte) 0x1D),
    /**
     * 913.50 MHz
     */
    _913500((byte) 0x1E),
    /**
     * 914.00 MHz
     */
    _914000((byte) 0x1F),
    /**
     * 914.50 MHz
     */
    _914500((byte) 0x20),
    /**
     * 915.00 MHz
     */
    _915000((byte) 0x21),
    /**
     * 915.50 MHz
     */
    _915500((byte) 0x22),
    /**
     * 916.00 MHz
     */
    _916000((byte) 0x23),
    /**
     * 916.50 MHz
     */
    _916500((byte) 0x24),
    /**
     * 917.00 MHz
     */
    _917000((byte) 0x25),
    /**
     * 917.50 MHz
     */
    _917500((byte) 0x26),
    /**
     * 918.00 MHz
     */
    _918000((byte) 0x27),
    /**
     * 918.50 MHz
     */
    _918500((byte) 0x28),
    /**
     * 919.00 MHz
     */
    _919000((byte) 0x29),
    /**
     * 919.50 MHz
     */
    _919500((byte) 0x2A),
    /**
     * 920.00 MHz
     */
    _920000((byte) 0x2B),
    /**
     * 920.50 MHz
     */
    _920500((byte) 0x2C),
    /**
     * 921.00 MHz
     */
    _921000((byte) 0x2D),
    /**
     * 921.50 MHz
     */
    _921500((byte) 0x2E),
    /**
     * 922.00 MHz
     */
    _922000((byte) 0x2F),
    /**
     * 922.50 MHz
     */
    _922500((byte) 0x30),
    /**
     * 923.00 MHz
     */
    _923000((byte) 0x31),
    /**
     * 923.50 MHz
     */
    _923500((byte) 0x32),
    /**
     * 924.00 MHz
     */
    _924000((byte) 0x33),
    /**
     * 924.50 MHz
     */
    _924500((byte) 0x34),
    /**
     * 925.00 MHz
     */
    _925000((byte) 0x35),
    /**
     * 925.50 MHz
     */
    _925500((byte) 0x36),
    /**
     * 926.00 MHz
     */
    _926000((byte) 0x37),
    /**
     * 926.50 MHz
     */
    _926500((byte) 0x38),
    /**
     * 927.00 MHz
     */
    _927000((byte) 0x39),
    /**
     * 927.50 MHz
     */
    _927500((byte) 0x3A),
    /**
     * 928.00 MHz
     */
    _928000((byte) 0x3B);

    private final byte value;

    Freq(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Freq valueOf(byte value) {
        switch (value) {
            case 0x01:
                return _865500;
            case 0x02:
                return _866000;
            case 0x03:
                return _866500;
            case 0x04:
                return _867000;
            case 0x05:
                return _867500;
            case 0x06:
                return _868000;
            case 0x07:
                return _902000;
            case 0x08:
                return _902500;
            case 0x09:
                return _903000;
            case 0x0A:
                return _903500;
            case 0x0B:
                return _904000;
            case 0x0C:
                return _904500;
            case 0x0D:
                return _905000;
            case 0x0E:
                return _905500;
            case 0x0F:
                return _906000;
            case 0x10:
                return _906500;
            case 0x11:
                return _907000;
            case 0x12:
                return _907500;
            case 0x13:
                return _908000;
            case 0x14:
                return _908500;
            case 0x15:
                return _909000;
            case 0x16:
                return _909500;
            case 0x17:
                return _910000;
            case 0x18:
                return _910500;
            case 0x19:
                return _911000;
            case 0x1A:
                return _911500;
            case 0x1B:
                return _912000;
            case 0x1C:
                return _912500;
            case 0x1D:
                return _913000;
            case 0x1E:
                return _913500;
            case 0x1F:
                return _914000;
            case 0x20:
                return _914500;
            case 0x21:
                return _915000;
            case 0x22:
                return _915500;
            case 0x23:
                return _916000;
            case 0x24:
                return _916500;
            case 0x25:
                return _917000;
            case 0x26:
                return _917500;
            case 0x27:
                return _918000;
            case 0x28:
                return _918500;
            case 0x29:
                return _919000;
            case 0x2A:
                return _919500;
            case 0x2B:
                return _920000;
            case 0x2C:
                return _920500;
            case 0x2D:
                return _921000;
            case 0x2E:
                return _921500;
            case 0x2F:
                return _922000;
            case 0x30:
                return _922500;
            case 0x31:
                return _923000;
            case 0x32:
                return _923500;
            case 0x33:
                return _924000;
            case 0x34:
                return _924500;
            case 0x35:
                return _925000;
            case 0x36:
                return _925500;
            case 0x37:
                return _926000;
            case 0x38:
                return _926500;
            case 0x39:
                return _927000;
            case 0x3A:
                return _927500;
            case 0x3B:
                return _928000;
            default:
                return _865000;
        }
    }
}
