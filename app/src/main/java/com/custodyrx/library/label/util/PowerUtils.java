package com.custodyrx.library.label.util;

import android.os.SystemClock;
import android.util.Log;


import com.custodyrx.library.label.GlobalCfg;
import com.custodyrx.library.label.bean.type.LinkType;
import com.naz.serial.port.ModuleManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 描述：上下电操作
 * <p>
 * <p>
 * Create on 2023-03-16
 */
public class PowerUtils {
    public static boolean powerOn() {
//        LCPowerUtils.power("1");
//        PL3PowerUtils.powerOn();
//        return true;
        return RPowerOn();
    }

    public static boolean powerOff() {
        LinkType linkType = GlobalCfg.get().getLinkType();
        XLog.i("linkType-->" + linkType);
        if (linkType != LinkType.LINK_TYPE_SERIAL_PORT) {
            return true;
        }

//        LCPowerUtils.power("0");
//        PL3PowerUtils.powerOff();
//        return true;
        return ModuleManager.newInstance().setUHFStatus(false);
    }

    private static boolean RPowerOn() {
        ModuleManager.newInstance().setUHFStatus(false);
        SystemClock.sleep(200);
        ModuleManager.newInstance().setUHFStatus(true);
        SystemClock.sleep(200);
        ModuleManager.newInstance().setUHFStatus(false);
        SystemClock.sleep(200);
        return ModuleManager.newInstance().setUHFStatus(true);
    }

    private static class LCPowerUtils {
        private static String s1 = "/proc/gpiocontrol/set_id";
        private static String s2 = "/proc/gpiocontrol/set_uhf";

        private static void power(String state) {
            try {
                FileWriter localFileWriterOn = new FileWriter(s2);
                localFileWriterOn.write(state);
                localFileWriterOn.close();
                Log.e("PowerUtil", "power=" + state + " Path=" + s2);
                FileWriter RaidPower = new FileWriter(s1);
                RaidPower.write(state);
                RaidPower.close();
                Log.e("PowerUtil", "power=" + state + " Path=" + s1);
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class PL3PowerUtils {
        //攀凌3上电
        private static final String vol33 = "/sys/class/rt5_gpio/gpio_uhf_ext_v33";
        private static final String vol5 = "/sys/class/rt5_gpio/gpio_uhf_ext_v5";
        private static final String power = "/sys/class/rt5_gpio/gpio_uhf_power";

        public static void powerOn() {
            Log.d("TAG", "pl_power_on");
            try {
                writeUhfFile(power, "1".getBytes());
                Thread.sleep(20);
                writeUhfFile(vol33, "1".getBytes());
                Thread.sleep(20);
                writeUhfFile(vol5, "1".getBytes());
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }

        public static void powerOff() {
            Log.d("TAG", "pl_power_off");
            try {
                writeUhfFile(vol5, "0".getBytes());
                Thread.sleep(10);
                writeUhfFile(vol33, "0".getBytes());
                Thread.sleep(10);
                writeUhfFile(power, "1".getBytes());
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }

        public static void writeUhfFile(String path, byte[] value) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);
                fos.write(value);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("TAG", "-----writeUhfFile-----e1=" + e.getMessage());
            } catch (IOException e) {
                Log.d("TAG", "-----writeUhfFile-----e2=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
