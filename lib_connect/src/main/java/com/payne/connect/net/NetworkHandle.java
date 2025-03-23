package com.payne.connect.net;

import com.payne.reader.base.Consumer;
import com.payne.reader.communication.ConnectHandle;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author naz
 * Date 2020/8/3
 */
public class NetworkHandle implements ConnectHandle {
    private static final String HOSTNAME_REGEXP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    private Socket mSocket;
    private final String mHost;
    private final int mPort;
    private final int mTimeoutMillis;
    /**
     * Processing the thread receiving the data
     */
    private ReceiveThread mReceiveThread;
    private Consumer<byte[]> mConsumer;
    private Consumer<Exception> mExceptionConsumer;
    private OutputStream mOutputStream;
    private AtomicBoolean mIsConnected;
    private byte[] dataBuffer = new byte[0xFFFF];

    public NetworkHandle(String host, int port) {
        this(host, port, 4000);
    }

    public NetworkHandle(String host, int port, int timeoutMillis) {
        this.mHost = host;
        this.mPort = port;
        this.mTimeoutMillis = timeoutMillis;
        this.mIsConnected = new AtomicBoolean(false);
    }

    @Override
    public boolean onConnect() {
        synchronized (this) {
            if (!mHost.matches(HOSTNAME_REGEXP)) {
                return false;
            }
            onDisconnect();
            try {
                InetSocketAddress mRemoteAddress = new InetSocketAddress(mHost, mPort);
                mSocket = new Socket();
                mSocket.connect(mRemoteAddress, mTimeoutMillis);
                mOutputStream = mSocket.getOutputStream();
                mReceiveThread = new ReceiveThread(mSocket.getInputStream());
                mReceiveThread.start();
                this.mIsConnected.set(true);
                return true;
            } catch (Exception e) {
                System.err.println("can't connect..." + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return mIsConnected.get();
    }

    @Override
    public void onSend(byte[] bytes) {
        synchronized (this) {
            if (mOutputStream != null) {
                try {
                    mOutputStream.write(bytes);
                } catch (Exception e) {
                    mIsConnected.set(false);
                    if (mExceptionConsumer != null) {
                        try {
                            mExceptionConsumer.accept(e);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onReceive(Consumer<byte[]> consumer, Consumer<Exception> exceptionConsumer) {
        this.mConsumer = consumer;
        this.mExceptionConsumer = exceptionConsumer;
    }

    @Override
    public void onDisconnect() {
        synchronized (this) {
            this.mIsConnected.set(false);
            if (mReceiveThread != null) {
                mReceiveThread.interrupt();
                mReceiveThread = null;
            }
            this.mOutputStream = null;
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mSocket = null;
            }
        }
    }

    /**
     * This thread is designed to receive processing data and callbacks
     */
    private class ReceiveThread extends Thread {
        private InputStream mInputStream;

        ReceiveThread(InputStream inputStream) {
            this.mInputStream = inputStream;
        }

        @Override
        public void run() {
            while (mIsConnected.get()) {
                if (mInputStream == null) {
                    return;
                }
                try {
                    if (mInputStream.available() <= 0) {
                        continue;
                    }
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
            this.mInputStream = null;
            super.interrupt();
        }
    }
}
