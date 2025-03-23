package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/29
 */
public class OperationTag extends Success {
    /**
     * Whether it is the last tag to this inventory
     */
    private boolean isEndTag;
    /**
     * operation tag count.
     */
    private int tagCount;
    /**
     * PC value
     */
    private String strPc;
    /**
     * CRC value
     */
    private String strCrc;
    /**
     * EPC value
     */
    private String strEpc;
    /**
     * Data in total
     */
    private String strData;

    private int mRssi;
    /**
     * Length of data
     */
    private int dataLen;
    /**
     * Antenna ID
     */
    private int antId;
    /**
     * Operating times
     */
    private int readCount;
    private String freq;

    public boolean isEndTag() {
        return isEndTag;
    }

    public void setEndTag(boolean endTag) {
        isEndTag = endTag;
    }

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }

    public String getStrPc() {
        return strPc;
    }

    public void setStrPc(String strPc) {
        this.strPc = strPc;
    }

    public String getStrCrc() {
        return strCrc;
    }

    public void setStrCrc(String strCrc) {
        this.strCrc = strCrc;
    }

    public String getStrEpc() {
        return strEpc;
    }

    public void setStrEpc(String strEpc) {
        this.strEpc = strEpc;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
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

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getFreq() {
        return freq;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    @Override
    public String toString() {
        return "OperationTag{" +
                ", isEndTag=" + isEndTag +
                ", tagCount=" + tagCount +
                ", strPc='" + strPc +
                ", strCrc='" + strCrc +
                ", strEpc='" + strEpc +
                ", strData='" + strData +
                ", dataLen=" + dataLen +
                ", antId=" + antId +
                ", readCount=" + readCount +
                ", freq=" + freq +
                ", rssi=" + mRssi +
                '}';
    }
}
