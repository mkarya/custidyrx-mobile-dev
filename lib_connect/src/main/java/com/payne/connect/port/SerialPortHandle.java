package com.payne.connect.port;

import com.custodyrx.serial.port.SerialPort;
import com.payne.reader.base.Consumer;
import com.payne.reader.communication.ConnectHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author naz
 * Date 2020/8/3
 */
public class SerialPortHandle implements ConnectHandle {
    private SerialPort mSerialPort;
    private String mPort;
    private int mBaud;
    /**
     * Processing the thread receiving the data
     */
    private ReceiveThread mReceiveThread;
    /**
     * Thread lock object
     */
    private final Object mLock = new Object();
    private Consumer<byte[]> mConsumer;
    private OutputStream mOutputStream;
    private boolean mIsConnected;

    public SerialPortHandle(String port, int baud) {
        this.mPort = port;
        this.mBaud = baud;
        this.mIsConnected = false;
    }

    @Override
    public boolean onConnect() {
        try {
            mSerialPort = new SerialPort(new File(mPort), mBaud);
            mOutputStream = mSerialPort.getOutputStream();
            mReceiveThread = new ReceiveThread(mSerialPort.getInputStream());
            mReceiveThread.start();
            this.mIsConnected = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return mIsConnected;
    }

    @Override
    public void onSend(byte[] bytes) {
        if (mOutputStream != null) {
            try {
                synchronized (mLock) {
                    mOutputStream.write(bytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReceive(Consumer<byte[]> consumer, Consumer<Exception> exceptionConsumer) {
        this.mConsumer = consumer;
    }

    @Override
    public void onDisconnect() {
        if (mReceiveThread != null) {
            mReceiveThread.interrupt();
            mReceiveThread = null;
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutputStream = null;
        }
        this.mIsConnected = false;
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * This thread is designed to receive processing data and callbacks
     */
    private class ReceiveThread extends Thread {
        /**
         * Does the thread receiving the data loop need to be interrupted
         */
        private boolean mInterrupt;
        private InputStream mInputStream;
        private byte[] dataBuffer = new byte[0xFFFF];

        ReceiveThread(InputStream inputStream) {
            this.mInterrupt = false;
            this.mInputStream = inputStream;
        }

        @Override
        public void run() {
            while (!mInterrupt) {
                try {
                    int readLen = mInputStream.read(dataBuffer);
                    if (readLen > 0) {
                        // receiveBytes: Used to save the obtained raw data.
                        byte[] receiveBytes = new byte[readLen];
                        System.arraycopy(dataBuffer, 0, receiveBytes, 0, readLen);
                        if (mConsumer != null) {
                            mConsumer.accept(receiveBytes);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            this.mInterrupt = true;
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mInputStream = null;
            }
            super.interrupt();
        }
    }
}
