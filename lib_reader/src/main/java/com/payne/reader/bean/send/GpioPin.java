package com.payne.reader.bean.send;

/**
 * GPIO status
 *
 * @author naz
 * Date 2020/7/21
 */
public class GpioPin {
    /**
     * id number
     */
    private int id;
    /**
     * Level value
     * true - high
     * false - low
     */
    private boolean high;
    /**
     * GPIO direction
     * true - Output
     * false - enter
     */
    private boolean output;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHigh() {
        return high;
    }

    public void setHigh(boolean high) {
        this.high = high;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }
}
