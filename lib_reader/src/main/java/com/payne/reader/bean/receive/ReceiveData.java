package com.payne.reader.bean.receive;

import java.util.Arrays;

/**
 * @author naz
 * Date 2020/8/3
 */
public class ReceiveData extends Success {
    /**
     * Core data received
     */
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReceiveData{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
