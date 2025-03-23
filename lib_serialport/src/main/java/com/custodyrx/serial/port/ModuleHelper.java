package com.custodyrx.serial.port;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author naz
 * Date 2020/3/27
 */
public class ModuleHelper {
    private static final ModuleHelper MODULE_HELPER = new ModuleHelper();

    public static ModuleHelper getInstance() {
        return MODULE_HELPER;
    }

    public boolean isEnable(String fileName) {
        FileInputStream in = null;
        try {
            byte[] buffer = new byte[2];
            in = new FileInputStream(fileName);
            int len = in.read(buffer);
            if (len > 0) {
                return buffer[0] == "1".getBytes()[0];
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean setEnable(String fileName, boolean enable) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(enable ? "1".getBytes() : "0".getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
