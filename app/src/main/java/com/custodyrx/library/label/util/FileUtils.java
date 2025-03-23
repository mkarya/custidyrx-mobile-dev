package com.custodyrx.library.label.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 *
 */
public class FileUtils {
    public static File getCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            return cacheDir;
        }
        return context.getCacheDir();
    }

    public static File getSaveLogDir(Context context) {
        File logs = context.getExternalFilesDir("Logs");
        if (logs == null) {
            logs = new File(context.getFilesDir().getAbsolutePath(), "/Logs");
        }
        return logs;
    }

    public static File getCrashFile(Context context) {
        File dir = context.getExternalFilesDir("crash");
        if (dir == null) {
            dir = context.getCacheDir();
        }
        return new File(dir, "LastCrash.txt");
    }

    /**
     * 换算文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "0.00KB";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String readFile2String(File file, String charsetName) {
        if (file == null) {
            return null;
        }

        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(charsetName.trim())) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\r\n");// windows系统换行为\r\n，Linux为\n
            }
            // 要去除最后的换行符
            return sb.delete(sb.length() - 2, sb.length()).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseUtils.close(reader);
        }
    }

    public static void open(Activity activity, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予临时权限别忘了
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri = FileProvider.getUriForFile(activity, "com.naz.label.fileprovider.provider", file);
        intent.setDataAndType(uri, "text/plain");
        activity.startActivity(intent);
    }
}
