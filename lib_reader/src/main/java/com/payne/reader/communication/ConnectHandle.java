package com.payne.reader.communication;

import com.payne.reader.base.Consumer;

/**
 * Connection handle
 *
 * @author naz
 * Date 2020/7/13
 */
public interface ConnectHandle {

    /**
     * Connected devices
     *
     * @return Whether the connection is successful
     */
    boolean onConnect();

    /**
     * Is it connected
     *
     * @return Is it connected
     */
    boolean isConnected();

    /**
     * Send data to device
     *
     * @param data byte[]
     */
    void onSend(byte[] data);

    /**
     * Callback data received from device
     *
     * @param consumer {@link Consumer}
     * @param exceptionConsumer
     */
    void onReceive(Consumer<byte[]> consumer, Consumer<Exception> exceptionConsumer);

    /**
     * Disconnect device
     */
    void onDisconnect();
}
