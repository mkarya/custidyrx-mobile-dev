package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/7/14
 */
public enum MaskAction {
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * | Assert SL or inventoried - A        |         Deassert SL or inventoried - B       |
     * --------------------------------------|-----------------------------------------------
     */
    Action0((byte) 0x00),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * | Assert SL or inventoried - A        |                   Do nothing                 |
     * --------------------------------------|-----------------------------------------------
     */
    Action1((byte) 0x01),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * |            Do nothing               |         Deassert SL or inventoried- B        |
     * --------------------------------------|-----------------------------------------------
     */
    Action2((byte) 0x02),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * |      Negate SL or(A- B,B- A)        |                   Do nothing                 |
     * --------------------------------------|-----------------------------------------------
     */
    Action3((byte) 0x03),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * |   Deassert SL or inventoried - B    |          Assert SL or inventoried- A         |
     * --------------------------------------|-----------------------------------------------
     */
    Action4((byte) 0x04),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * |   Deassert SL or inventoried - B    |                   Do nothing                 |
     * --------------------------------------|-----------------------------------------------
     */
    Action5((byte) 0x05),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * |            Do nothing               |          Assert SL or inventoried- A         |
     * --------------------------------------|-----------------------------------------------
     */
    Action6((byte) 0x06),
    /**
     * ---------Tag Matches Mask-------------|-----------Tag Doesn’t Match Mask--------------
     * |            Do nothing               |           Negate SL or (A- B, B- A)          |
     * --------------------------------------|-----------------------------------------------
     */
    Action7((byte) 0x07);

    private final byte value;

    MaskAction(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static MaskAction valueOf(byte value) {
        switch (value) {
            case 0x01:
                return Action1;
            case 0x02:
                return Action2;
            case 0x03:
                return Action3;
            case 0x04:
                return Action4;
            case 0x05:
                return Action5;
            case 0x06:
                return Action6;
            case 0x07:
                return Action7;
            default:
                return Action0;
        }
    }
}
