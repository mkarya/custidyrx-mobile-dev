package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/21
 */
public class ReaderTemperature extends Success {

    /**
     * Reader temperature value
     */
    private int temperature;

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "ReaderTemperature{" +
                "temperature=" + temperature +
                '}';
    }
}
