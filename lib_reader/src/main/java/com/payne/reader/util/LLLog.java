package com.payne.reader.util;

public class LLLog {
    private static OnLogL sL;
    private static boolean sDebug = false;

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    public static boolean isDebug() {
        return sDebug || sL != null;
    }

    static public void setLogLs(OnLogL l) {
        sL = l;
    }

    static public void i(String msg) {
        if (sL != null) {
            sL.onLogI(msg);
            return;
        }
        if (!sDebug) {
            return;
        }
        System.out.println("LLL------->" + getStackTrace(4) + "\n--->" + msg);
    }

    static public void w(String msg) {
        if (sL != null) {
            sL.onLogW(msg);
            return;
        }
        if (!sDebug) {
            return;
        }
        System.out.println("LLL------->" + getStackTrace(4) + "\n--->" + msg);
    }

    static public void e(String msg) {
        if (sL != null) {
            sL.onLogE(msg);
            return;
        }
        if (!sDebug) {
            return;
        }
        System.out.println("LLL------->" + getStackTrace(4) + "\n--->" + msg);
    }

    public static String getStackTrace(int index) {
        StackTraceElement[] ses = Thread.currentThread().getStackTrace();

        if (index > -1 && index < ses.length) {
            return ses[index].toString();
        } else {
            return "OutOfRange: " + index + " for " + ses.length + "\n";
        }
    }

    public interface OnLogL {
        void onLogI(String msg);

        void onLogW(String msg);

        void onLogE(String msg);
    }
}