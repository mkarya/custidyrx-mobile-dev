package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/18
 */
public class Version extends Success {
    /*000：R2000
      010：E710
      011：TM670
      100：FDW
      */
    public enum ChipType {
        IBAT2000,
        R2000,
        E710,
        TM670,
        FDW
    }

    private ChipType mChipType;
    /**
     * Firmware version
     */
    private String version;

    public ChipType getChipType() {
        return mChipType;
    }

    public void setChipType(ChipType chipType) {
        this.mChipType = chipType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Version{" +
                "ChipType='" + mChipType + '\'' +
                "version='" + version + '\'' +
                '}';
    }
}
