package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/29
 */
public class InventoryFailure extends Failure {
    /**
     * If antId is not -1, it means {@link Failure#getErrorCode()} error in the current antenna number
     */
    private int antId;

    public InventoryFailure() {
        this.antId = -1;
    }

    public int getAntId() {
        return antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    @Override
    public String toString() {
        return "InventoryFailure{" +
                "antId=" + antId +
                ", errorCode=" + (getErrorCode() & 0xFF) +
                '}';
    }
}
