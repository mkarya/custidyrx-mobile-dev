package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/22
 */
public class RfPortReturnLoss extends Success {
    /**
     * Return loss value in dB. VSWR = (10 RL / 20 + 1) / (10 RL / 20-1)
     */
    private byte returnLoss;

    public byte getReturnLoss() {
        return returnLoss;
    }

    public void setReturnLoss(byte returnLoss) {
        this.returnLoss = returnLoss;
    }

    @Override
    public String toString() {
        return "RfPortReturnLoss{" +
                "returnLoss=" + (returnLoss & 0xFF) +
                '}';
    }
}
