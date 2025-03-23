package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/31
 */
public class ReaderStatus extends Success {

    /**
     * Reader status
     */
    private byte status;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReaderStatus{" +
                "status=" + (status & 0xFF) +
                '}';
    }
}
