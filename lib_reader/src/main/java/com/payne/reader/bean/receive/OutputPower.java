package com.payne.reader.bean.receive;

import java.util.Arrays;

/**
 * @author naz
 * Date 2020/2/19
 */
public class OutputPower extends Success {

    /**
     * Output power
     */
    private byte[] outputPower;

    public byte[] getOutputPower() {
        return outputPower;
    }

    public void setOutputPower(byte[] outputPower) {
        this.outputPower = outputPower;
    }

    @Override
    public String toString() {
        return "OutputPower{" +
                "outputPower=" + Arrays.toString(outputPower) +
                '}';
    }
}
