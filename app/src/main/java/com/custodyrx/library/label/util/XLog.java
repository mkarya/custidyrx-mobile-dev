package com.custodyrx.library.label.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.payne.reader.util.LLLog;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * adb开启日志：adb shell setprop log.tag.PCLog_ DEBUG
 * adb开启日志写入文件：adb shell setprop log.tag.PCLog_Save DEBUG
 */
public class XLog {

    private static final String TAG = "PCLog_";
    private static final String TAG_TO_FILE_ = "PCLog_Save";
    /**
     * 最大保存文件条数
     */
    private static final int FILES_LENGTH = 10;
    /**
     * 是否显示日志
     */
    public static boolean sShowLog;
    /**
     * 是否将日志写入文件
     */
    private static boolean sSaveLogToFile;
    /**
     * 日志文件路径
     */
    private static String sLocalLogDir;
    private static volatile ExecutorService sExecutor;
    public static Context sContext;

    private static final ThreadLocal<Map<String, SimpleDateFormat>> SDF_THREAD_LOCAL = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new HashMap<>();
        }
    };

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getSafeDateFormat(String pattern) {
        Map<String, SimpleDateFormat> sdfMap = SDF_THREAD_LOCAL.get();
        //No Inspection ConstantConditions
        SimpleDateFormat simpleDateFormat = sdfMap.get(pattern);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(pattern);
            sdfMap.put(pattern, simpleDateFormat);
        }
        return simpleDateFormat;
    }

    public static void init(Context context, boolean showLog, boolean saveLog) {
        sContext = context.getApplicationContext();
        sShowLog = showLog;
        sSaveLogToFile = saveLog;

        if (!sShowLog) {
            sShowLog = Log.isLoggable(TAG, Log.DEBUG);
        }
        if (!sSaveLogToFile) {
            sSaveLogToFile = saveLog || Log.isLoggable(TAG_TO_FILE_, Log.DEBUG);
        }

        createEx();
    }

    public static void i(String msg) {
        i(msg, 5);
    }

    public static void i(String tag, String msg) {
        i("[" + tag + "]" + msg, 5);
    }

    public static void i(String msg, int index) {
        if (sShowLog || sSaveLogToFile) {
            msg = "[" + getStackTrace(index) + "]\n" + msg;
        }
        if (sShowLog) {
            if (msg.length() > 3000) {
                boolean needPrefix = false;
                while (true) {
                    if (msg.length() > 3000) {
                        String logStr = msg.substring(0, 3000);
                        Log.i(TAG, needPrefix ? "[接上文]" + logStr : logStr);
                        msg = msg.substring(3000);
                    } else {
                        Log.i(TAG, "[接上文]" + msg + "\n---------------------------------------------------------------");
                        break;
                    }
                    needPrefix = true;
                }
            } else {
                Log.i(TAG, msg);
            }
        }
        if (sSaveLogToFile) {
            writeLocalLog(getCurrentFormattedTime() + "_i: " + msg);
        }
    }

    public static void d(String msg) {
        d(msg, 5);
    }

    public static void d(String tag, String msg) {
        d("[" + tag + "]" + msg, 5);
    }

    public static void d(String msg, int index) {
        if (sShowLog || sSaveLogToFile) {
            msg = "[" + getStackTrace(index) + "]\n" + msg;
        }
        if (sShowLog) {
            if (msg.length() > 3000) {
                boolean needPrefix = false;
                while (true) {
                    if (msg.length() > 3000) {
                        String logStr = msg.substring(0, 3000);
                        Log.d(TAG, needPrefix ? "[接上文]" + logStr : logStr);
                        msg = msg.substring(3000);
                    } else {
                        Log.d(TAG, "[接上文]" + msg + "\n---------------------------------------------------------------");
                        break;
                    }
                    needPrefix = true;
                }
            } else {
                Log.d(TAG, msg);
            }
        }
        if (sSaveLogToFile) {
            writeLocalLog(getCurrentFormattedTime() + "_d: " + msg);
        }
    }

    public static void w(String msg) {
        w(msg, 5);
    }

    public static void w(String tag, String msg) {
        w("[" + tag + "]" + msg, 5);
    }

    public static void w(String msg, int index) {
        if (sShowLog || sSaveLogToFile) {
            msg = "[" + getStackTrace(index) + "]\n" + msg;
        }
        if (sShowLog) {
            Log.w(TAG, msg);
        }
        if (sSaveLogToFile) {
            writeLocalLog(getCurrentFormattedTime() + "_w: " + msg);
        }
    }

    public static void e(String msg) {
        e(msg, 5);
    }

    public static void e(String tag, String msg) {
        e("[" + tag + "]" + msg, 5);
    }

    public static void e(String msg, int index) {
        if (sShowLog || sSaveLogToFile) {
            msg = "[" + getStackTrace(index) + "]\n" + msg;
        }
        if (sShowLog) {
            Log.e(TAG, "catchInfo------------->\n" + msg);
        }
        if (sSaveLogToFile) {
            writeLocalLog(getCurrentFormattedTime() + "_e: " + msg);
        }
    }

    private static void writeLocalLog(final String msg) {
        if (TextUtils.isEmpty(sLocalLogDir)) {
            Log.e(TAG, "LOCAL_LOG_DIR isEmpty, 无法写日志");
            return;
        }
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String fileName = getCurrentDate() + ".txt";
                File file = new File(sLocalLogDir, fileName);
                writeLocalLog(file, msg);
            }
        });
    }

    private static void deleteOldestFile(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > FILES_LENGTH) {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                }
            });
//            for (File file : files) {
//                Log.i(TAG, "after sort->" + file);
//            }

            for (int i = FILES_LENGTH; i < files.length; i++) {
                if (!files[i].delete()) {
                    Log.w(TAG, "delete.err->" + files[i]);
                }
            }
        }
    }

    private static void writeLocalLog(File file, String msg) {
        synchronized (XLog.class) {
            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw, true);
            ) {
                pw.println(msg);
                pw.flush();
            } catch (Exception e) {
                Log.w(TAG, "writeLocalLog Error:" + e.getMessage());
            }
        }
    }

    /**
     * 获取堆栈的方法名
     */
    private static String getStackTrace(int index) {
        StackTraceElement[] ses = Thread.currentThread().getStackTrace();

        if (index > -1 && index < ses.length) {
            return ses[index].toString();
        } else {
            return "OutOfRange: " + index + " for " + ses.length + "\n";
        }
    }

    private static String getCurrentDate() {
        /* 获取当前日期, _HH按小时存 */
        Date date = new Date(System.currentTimeMillis());
        return getSafeDateFormat("yyyy_MM_dd").format(date);
    }

    public static String getCurrentFormattedTime() {
        /*获取当前时间*/
        Date date = new Date(System.currentTimeMillis());
        return getSafeDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static void showStackTrace() {
        if (!sShowLog) {
            return;
        }
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : trace) {
            sb.append(ste).append("\n");
        }
        i(sb.append("StackTrace->").toString(), 5);
    }

    public static void enableShowLog(boolean enable) {
        sShowLog = enable;
        if (sShowLog) {
            LLLog.setLogLs(new ILog());
        } else {
            LLLog.setLogLs(null);
        }
    }

    public static void enableSaveLog(boolean enable) {
        sSaveLogToFile = enable;
        createEx();
    }

    private static void createEx() {
        if (sSaveLogToFile) {
            File saveLogDir = FileUtils.getSaveLogDir(sContext);
            sLocalLogDir = saveLogDir.getAbsolutePath();
            System.out.println("sLocalLogDir:" + sLocalLogDir);

            if (sExecutor == null) {
                synchronized (XLog.class) {
                    if (sExecutor == null) {
                        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(900);
                        sExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, workQueue);

                        saveLogDir.mkdirs();
                        deleteOldestFile(saveLogDir);
                    }
                }
            }
        }
    }

    public static class ILog implements LLLog.OnLogL {

        @Override
        public void onLogI(String s) {
            XLog.i("LLLog->" + s, 6);
        }

        @Override
        public void onLogW(String s) {
            XLog.w("LLLog->" + s, 6);
        }

        @Override
        public void onLogE(String s) {
            XLog.e("LLLog->" + s, 6);
        }
    }
}