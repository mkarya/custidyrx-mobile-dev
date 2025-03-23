package com.custodyrx.serial.port;

/**
 * Manage the Modules power on or off.Set the module action follow the system(the module will power
 * off when screen out, power on lighting up) or only control via set module status.
 */
public class ModuleManager {
    private final int OFF = 0;
    private final int ON = 1;
    private final int SET_SUCCESSED = 1;

    private final int UHF_DEV_NO = 0;
    private final int SCAN_DEV_NO = 1;

    private final int NORMAL_STATUS = 0;
    private final int ALWAYS_STATUS = 1;

    private final int ALWAYS_ON = 3;
    private final int ALWAYS_OFF = 2;

    private final int ERROR = -1;

    private static final ModuleManager mModuleManager = new ModuleManager();

    static {
        System.loadLibrary("SerialPortRD");
    }

    /**
     * Get the instance of ModuleManager.
     *
     * @return The ModuleManager Object.
     */
    public static ModuleManager newInstance() {
        return mModuleManager;
    }

    /**
     * Get the status of UHF Module
     *
     * @return if true HUF is on,or UHF is off
     * @throws Exception it will throw exception if get error status or unknown status.
     */
    public boolean getUHFStatus() throws Exception {
        int i = JNIReadGPIO(UHF_DEV_NO, NORMAL_STATUS);
        switch (i) {
            case ON:
                return true;
            case OFF:
                return false;
            case ERROR:
                throw new Exception("Read status Exception!");
            default:
                throw new Exception("Return unknown value Exception! return error value = " + i);
        }
    }

    /**
     * set UHF module status
     *
     * @param flag if true on,or off
     * @return if true set success,or failure
     */
    public boolean setUHFStatus(boolean flag) {
        int i = 0;
        if (flag) {
            i = JNIWriteGPIO(ON, UHF_DEV_NO);
        } else {
            i = JNIWriteGPIO(OFF, UHF_DEV_NO);
        }
        if (i == SET_SUCCESSED) {
            return true;
        }
        return false;
    }

    /**
     * get the status of Scan Engine Module
     *
     * @return if true Scan Engine is on,or Scan Engine is off
     * @throws Exception it will throw exception if get error status or unknown status.
     */
    public boolean getScanStatus() throws Exception {
        int i = JNIReadGPIO(SCAN_DEV_NO, NORMAL_STATUS);
        switch (i) {
            case ON:
                return true;
            case OFF:
                return false;
            case ERROR:
                throw new Exception("Read status Exception!");
            default:
                throw new Exception("Return unknown value Exception! return error value = " + i);
        }
    }

    /**
     * set the status of Scan Engine Module
     *
     * @param flag if true on,or off
     * @return if true set success,or failure
     */
    public boolean setScanStatus(boolean flag) {
        int i = 0;
        if (flag) {
            i = JNIWriteGPIO(ON, SCAN_DEV_NO);
        } else {
            i = JNIWriteGPIO(OFF, SCAN_DEV_NO);
        }
        if (i == SET_SUCCESSED) {
            return true;
        }
        return false;
    }

    /**
     * Set UHF module action.
     *
     * @param flag if true the module follow the action of {@link #setUHFStatus(boolean)}
     *             if false the module will power off when the screen out, restore the status the screen light up.
     * @return if true set success,or failure
     */
    public boolean setUHFAction(boolean flag) {
        int i = 0;
        if (flag) {
            i = JNIWriteGPIO(ALWAYS_ON, UHF_DEV_NO);
        } else {
            i = JNIWriteGPIO(ALWAYS_OFF, UHF_DEV_NO);
        }
        if (i == SET_SUCCESSED) {
            return true;
        }
        return false;
    }

    /**
     * Get UHF module action.
     *
     * @return if true the module follow the action of {@link #setUHFStatus(boolean)},
     * or the module will power off when the screen out, restore the status the screen light up.
     * @throws Exception it will throw exception if get error status or unknown status.
     */
    public boolean getUHFAction() throws Exception {
        int i = JNIReadGPIO(UHF_DEV_NO, ALWAYS_STATUS);
        switch (i) {
            case ON:
                return true;
            case OFF:
                return false;
            case ERROR:
                throw new Exception("Read status Exception!");
            default:
                throw new Exception("Return unknown value Exception! return error value = " + i);
        }
    }

    /**
     * Set Scan module action.
     *
     * @param flag if true the module follow the action of {@link #setUHFStatus(boolean)}
     *             if false the module will power off when the screen out, restore the status the screen light up.
     * @return if true set success,or failure
     */
    public boolean setScanAction(boolean flag) {
        int i = 0;
        if (flag) {
            i = JNIWriteGPIO(ALWAYS_ON, SCAN_DEV_NO);
        } else {
            i = JNIWriteGPIO(ALWAYS_OFF, SCAN_DEV_NO);
        }
        if (i == SET_SUCCESSED) {
            return true;
        }
        return false;
    }

    /**
     * Get Scan module action.
     *
     * @return if true the module follow the action of {@link #setScanStatus(boolean)},
     * or the module will power off when the screen out, restore the status the screen light up.
     * @throws Exception it will throw exception if get error status or unknown status.
     */
    public boolean getScanAction() throws Exception {
        int i = JNIReadGPIO(SCAN_DEV_NO, ALWAYS_STATUS);
        switch (i) {
            case ON:
                return true;
            case OFF:
                return false;
            case ERROR:
                throw new Exception("Read status Exception!");
            default:
                throw new Exception("Return unknown value Exception! return error value = " + i);
        }
    }

    /**
     * Release the powered control device.
     */
    public void release() {
        JNIRelease();
    }

    private native int JNIWriteGPIO(int value, int devNo);

    private native int JNIReadGPIO(int devNo, int isAlways);

    private native int JNIRelease();
}
