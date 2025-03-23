package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/12/18
 */
public class TempLabel2 extends Success {
    /**
     * Type, reference{@link com.payne.reader.bean.config.TempLabel2Flag}
     */
    private byte type;
    /**
     * Result code
     */
    private byte resultCode;
    /**
     * Number of labels
     */
    private int tagCount;
    /**
     * Antenna interface
     */
    private int antId;
    /**
     * Read times
     */
    private int readCount;
    /**
     * pc
     */
    private String strPc;
    /**
     * epc
     */
    private String strEpc;
    /**
     * crc
     */
    private String strCrc;
    /**
     * Data, may be an empty string, judge according to the result code
     */
    private String strData;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getResultCode() {
        return resultCode;
    }

    public void setResultCode(byte resultCode) {
        this.resultCode = resultCode;
    }

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }

    public int getAntId() {
        return antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public String getStrPc() {
        return strPc;
    }

    public void setStrPc(String strPc) {
        this.strPc = strPc;
    }

    public String getStrEpc() {
        return strEpc;
    }

    public void setStrEpc(String strEpc) {
        this.strEpc = strEpc;
    }

    public String getStrCrc() {
        return strCrc;
    }

    public void setStrCrc(String strCrc) {
        this.strCrc = strCrc;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }
}
