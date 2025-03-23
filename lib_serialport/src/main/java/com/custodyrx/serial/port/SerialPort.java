/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.custodyrx.serial.port;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Open the special serial port device via new {@link #SerialPort(File, int, int)}.Then get InputStream via {@link #getInputStream()}
 * and OutputStream via {@link #getOutputStream()} to operate the serial port device.
 * Close opened serial port device release the resource and you must invoke System.exit(0) if you invoke {@link #close()}
 */
public class SerialPort {

    private static final String TAG = "SerialPort";

    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    /**
     * Constructor without checking read and write permissions
     * 不检查读写权限的构造方法
     *
     * @param device   device file
     * @param baudRate baudRate
     * @throws SecurityException Unauthorized read file
     * @throws IOException       Device file read and write exceptions
     */
    public SerialPort(File device, int baudRate) throws SecurityException, IOException {
        this(false, "", device, baudRate, 0);
    }

    /**
     * Construction method for checking read and write permissions, su path is "/ system / bin / su"
     * 检查读写权限的构造方法，su路径为"/system/bin/su"
     *
     * @param device   device file
     * @param baudRate baudRate
     * @param flags    read and write mode of device files
     * @throws SecurityException Unauthorized read file
     * @throws IOException       Device file read and write exceptions
     */
    public SerialPort(File device, int baudRate, int flags) throws SecurityException, IOException {
        this(true, "/system/bin/su", device, baudRate, flags);
    }

    /**
     * Build SerialPort Object
     *
     * @param checkPermission Whether to check file read and write permissions
     * @param cmdSuPath       Command su path
     * @param device          device file
     * @param baudRate        baudRate
     * @param flags           read and write mode of device files
     * @throws SecurityException Unauthorized read file
     * @throws IOException       Device file read and write exceptions
     */
    public SerialPort(boolean checkPermission, String cmdSuPath, File device, int baudRate, int flags) throws SecurityException, IOException {
        if (checkPermission) {
            /* Check access permission */
            if (!device.canRead() || !device.canWrite()) {
                try {
                    /* Missing read/write permission, trying to chmod the file */
                    Process su;
                    su = Runtime.getRuntime().exec(cmdSuPath);
                    String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                    su.getOutputStream().write(cmd.getBytes());
                    if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                        throw new SecurityException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SecurityException();
                }
            }
        }
        System.out.println("FileName:" + device.getAbsolutePath());
        mFd = open(device.getAbsolutePath(), baudRate, flags);
        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    /**
     * Get the files InputStream
     *
     * @return Returns the InputStream object
     */
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    /**
     * Get the files OutputStream
     *
     * @return Returns the OutputStream object
     */
    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }


    /**
     * Open serial port device file
     *
     * @param path     Device file path
     * @param baudRate Set the serial port baud rate
     * @param flags    Read and write mode of device files
     * @return Device file description object
     */
    private native static FileDescriptor open(String path, int baudRate, int flags);

    /**
     * Close device file
     */
    public native void close();

    static {
        System.loadLibrary("SerialPortRD");
    }
}
