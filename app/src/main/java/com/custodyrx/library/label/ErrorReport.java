package com.custodyrx.library.label;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.custodyrx.library.label.util.FileUtils;
import com.custodyrx.library.label.util.XLog;

import java.io.File;
import java.io.PrintWriter;

/**
 * 异常信息的记录
 *
 * @author FengLing
 */
public class ErrorReport implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static class ClassHolder {
        private static final ErrorReport INSTANCE = new ErrorReport();
    }

    private ErrorReport() {

    }

    public static void init() {
        // 获取系统默认的UncaughtException处理器
        ClassHolder.INSTANCE.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(ClassHolder.INSTANCE);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        if (!handleException(throwable) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            SystemClock.sleep(1000);
            ((App) XLog.sContext).onTerminate();
        }
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        String msg = Log.getStackTraceString(throwable);
        XLog.i(msg);

        Context context = App.getContext();
        File file = FileUtils.getCrashFile(context);

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(msg);
            pw.flush();
        } catch (Throwable ignored) {
        }
        return true;
    }
}