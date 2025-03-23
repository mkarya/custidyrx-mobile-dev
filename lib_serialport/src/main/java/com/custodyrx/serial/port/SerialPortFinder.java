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
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * Object to find serial port device;
 *
 * @author Administrator
 */
public class SerialPortFinder {

    /**
     * Object to describe  hardware device
     *
     * @author Administrator
     */
    public class Driver {
        Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        /**
         * All the objects to describe hardware device
         *
         * @return devices container
         */
        Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                if (files == null) {
                    return mDevices;
                }
                for (File file : files) {
                    if (file.getAbsolutePath().startsWith(mDeviceRoot)) {
                        mDevices.add(file);
                    }
                }
            }
            return mDevices;
        }

        String getName() {
            return mDriverName;
        }
    }

    private Vector<Driver> mDrivers = null;

    private Vector<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<>();
            LineNumberReader reader = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String str;
            while ((str = reader.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract driver name with split()
                String driverName = str.substring(0, 0x15).trim();
                String[] w = str.split(" +");
                if ((w.length >= 5) && ("serial".equals(w[w.length - 1]))) {
                    mDrivers.add(new Driver(driverName, w[w.length - 4]));
                }
            }
            reader.close();
        }
        return mDrivers;
    }

    /**
     * Obtain all the devices filenames
     *
     * @return all the filename characters
     */
    public String[] getAllDevices() {
        Vector<String> devices = new Vector<>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                for (File file : driver.getDevices()) {
                    String device = file.getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[0]);
    }

    /**
     * Obtain all the filepath
     *
     * @return all the filepath string array
     */
    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                for (File file : driver.getDevices()) {
                    String device = file.getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[0]);
    }
}
