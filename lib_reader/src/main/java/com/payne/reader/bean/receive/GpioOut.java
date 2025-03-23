package com.payne.reader.bean.receive;

import com.payne.reader.bean.send.GpioPin;

import java.util.Arrays;

/**
 * @author naz
 * Date 2020/7/21
 */
public class GpioOut extends Success {
    private GpioPin[] gpios;

    public GpioPin[] getGpios() {
        return gpios;
    }

    public void setGpios(GpioPin[] gpios) {
        this.gpios = gpios;
    }

    @Override
    public String toString() {
        return "GpioOut{" +
                "gpios=" + Arrays.toString(gpios) +
                '}';
    }
}
