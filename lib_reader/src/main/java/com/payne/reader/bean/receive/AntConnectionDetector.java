package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/21
 */
public class AntConnectionDetector extends Success {
    /**
     * Antenna connection detection is off
     */
    private boolean isClose;
    /**
     * Antenna sensitivity
     */
    private byte antDetector;

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

    public byte getAntDetector() {
        return antDetector;
    }

    public void setAntDetector(byte antDetector) {
        this.antDetector = antDetector;
    }

    @Override
    public String toString() {
        return "AntConnectionDetector{" +
                "isClose=" + isClose +
                ", antDetector=" + (antDetector & 0xFF) +
                '}';
    }
}
