package com.payne.connect.otg;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.payne.reader.base.Consumer;
import com.payne.reader.communication.ConnectHandle;
import com.payne.reader.util.ThreadPool;

/**
 * @author naz
 * Date 2020/8/10
 */
public class UsbHandle implements ConnectHandle {
    private BroadcastReceiver mUsbReceiver;
    private Context mContext;
    private static final String ACTION_USB_PERMISSION = "com.payne.connect.USB_PERMISSION";
    private UsbDevice mUsbDevice;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;
    private Consumer<byte[]> mConsumer;
    /**
     * Processing the thread receiving the data
     */
    private ReceiveThread mReceiveThread;

    public UsbHandle(Context context) {
        this.mContext = context;
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mUsbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    //接受到自定义广播
                    synchronized (this) {
                        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (usbDevice != null) {
                                //Do something
                                mUsbDevice = usbDevice;
                            }
                        } else {
                            Toast.makeText(context, "用户未授权", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    //接收到设备插入广播
                    UsbDevice deviceAdd = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (deviceAdd != null) {
                        Toast.makeText(context, "设备插入", Toast.LENGTH_SHORT).show();
                        PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        if (mUsbManager != null) {
                            mUsbManager.requestPermission(mUsbDevice, permissionIntent);
                        } else {
                            Toast.makeText(context, "设备不支持OTG", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    //接收到设备拔出广播
                    UsbDevice deviceRemove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (deviceRemove != null) {
                        if (mUsbDevice == deviceRemove) {
                            mUsbDeviceConnection = null;
                            mUsbDevice = null;
                        }
                        Toast.makeText(context, "设备拔出", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    @Override
    public boolean onConnect() {
        if (mUsbManager != null && mUsbDevice != null) {
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            UsbEndpoint inEndpoint = usbInterface.getEndpoint(0);
            mReceiveThread = new ReceiveThread(inEndpoint);
            mReceiveThread.start();
            return isConnected();
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return mUsbDeviceConnection != null;
    }

    @Override
    public void onSend(final byte[] bytes) {
        if (isConnected()) {
            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            final UsbEndpoint outEndpoint = usbInterface.getEndpoint(1);
            ThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    int ret = mUsbDeviceConnection.bulkTransfer(outEndpoint, bytes, bytes.length, 800);
                    Log.e("gpenghui", "onSend: " + ret);
                }
            });
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
    }

    /**
     * This thread is designed to receive processing data and callbacks
     */
    private class ReceiveThread extends Thread {
        /**
         * Does the thread receiving the data loop need to be interrupted
         */
        private boolean mInterrupt;
        private UsbEndpoint mInEndpoint;

        ReceiveThread(UsbEndpoint inEndpoint) {
            this.mInterrupt = false;
            this.mInEndpoint = inEndpoint;
        }

        @Override
        public void run() {
            byte[] dataBuffer = new byte[64];
            while (!mInterrupt) {
                try {
                    if (!isConnected()) {
                        return;
                    }
                    int readLen = mUsbDeviceConnection.bulkTransfer(mInEndpoint, dataBuffer, dataBuffer.length, 800);
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
            super.interrupt();
        }
    }

    public void release() {
        if (mReceiveThread != null) {
            mReceiveThread.interrupt();
            mReceiveThread = null;
        }
        mContext.unregisterReceiver(mUsbReceiver);
        if (mUsbDeviceConnection != null) {
            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            mUsbDeviceConnection.releaseInterface(usbInterface);
            mUsbDeviceConnection.close();
            mUsbDeviceConnection = null;
        }
    }
}
