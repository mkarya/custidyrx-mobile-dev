package com.payne.reader.communication;

import java.security.InvalidParameterException;

/**
 * @author naz
 * Date 2020/8/3
 */
public class RequestInfo {
    /**
     * Save protocol packet
     */
    private DataPacket dataPacket;
    /**
     * Whether the sent command need return data
     */
    private boolean needResponse;

    public RequestInfo(DataPacket dataPacket) {
        this(dataPacket, true);
    }

    public RequestInfo(DataPacket dataPacket, boolean needResponse) {
        if (dataPacket == null) {
            throw new InvalidParameterException("The data packet parameter cannot be null");
        }
        this.dataPacket = dataPacket;
        this.needResponse = needResponse;
    }

    /**
     * Get protocol packet
     *
     * @return {@link DataPacket}
     */
    public DataPacket getDataPacket() {
        return dataPacket;
    }

    /**
     * Will the device respond after sending the request to the device
     *
     * @return bool
     */
    public boolean isNeedResponse() {
        return needResponse;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "dataPacket=" + dataPacket +
                ", needResponse=" + needResponse +
                '}';
    }
}
