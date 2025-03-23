package com.payne.reader.bean.config;

/**
 * Communication rate of RF
 * 0xD0:Tari 25uS,FM0 40KHz
 * 0xD1:Tari 25uS,Miller 4 250KHz
 * 0xD2:Tari 25uS,Miller 4 300KHz
 * 0xD3:Tari 6.25uS,FM0 400KHz
 *
 * @author naz
 * Date 2020/7/22
 */
public enum ProfileId {
    UNKNOWN((byte) 0x00),

    PROFILE_0((byte) 0xD0), /* Tari 25uS,FM0 40KHz */
    PROFILE_1((byte) 0xD1), /* Tari 25uS,Miller 4 250KHz */
    PROFILE_2((byte) 0xD2), /* Tari 25uS,Miller 4 300KHz */
    PROFILE_3((byte) 0xD3), /* Tari 6.25uS,FM0 400KHz */

    //<editor-fold desc="E710">
    PROFILE_E710_103((byte) 0xF9), // "Mode 103：DSB,    Tari 6.25uS, Miller 1 640KHz");
    PROFILE_E710_241((byte) 0xFE), // "Mode 241：PR-ASK, Tari 20uS，Miller 4 320KHz");
    PROFILE_E710_244((byte) 0xFF), // "Mode 244：PR-ASK, Tari 20uS，Miller 4 250KHz"););
    PROFILE_E710_285((byte) 0xED); // "Mode 285：PR-ASK, Tari 20uS，Miller 8 160KHz");
    //</editor-fold>

    private final byte value;

    ProfileId(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ProfileId valueOf(byte v) {
        ProfileId[] values = values();

        for (ProfileId profileId : values) {
            if (profileId.value == v) {
                return profileId;
            }
        }
        return UNKNOWN;
    }
}
