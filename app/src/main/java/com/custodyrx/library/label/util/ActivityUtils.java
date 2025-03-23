package com.custodyrx.library.label.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author gpenghui
 * @date 2018/5/15
 */

public class ActivityUtils {
    private static final LinkedList<Activity> ACTIVITIES = new LinkedList<>();

    /**
     * 跳转到新的activity
     *
     * @param activity    当前activity
     * @param targetClass 新的activity的class
     */
    public static void startActivity(Activity activity, @NonNull Class<?> targetClass) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, targetClass);
        activity.startActivity(intent);
    }

    /**
     * 跳转到获取权限的activity
     *
     * @param activity 当前的activity
     */
    public static void startPermissionActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    /**
     * 向List中添加一个活动
     *
     * @param activity 活动
     */
    public static void addActivity(Activity activity) {
        ACTIVITIES.add(activity);
    }

    /**
     * 从List中移除活动
     *
     * @param activity 活动
     */
    public static void removeActivity(Activity activity) {
        ACTIVITIES.remove(activity);
    }

    /**
     * 将List中存储的Activity全部销毁掉
     */
    public static void finishAll() {
        Iterator<Activity> it = ACTIVITIES.iterator();
        while (it.hasNext()) {
            Activity activity = it.next();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 删除除了需要保留的Activity以外的所有Activity
     *
     * @param retainActivity 需要保留的Activity
     */
    public static void finishOthers(Activity retainActivity) {
        for (Activity activity : ACTIVITIES) {
            if (activity != retainActivity) {
                activity.finish();
            }
        }
    }

    public static void finish(Class<?> cls) {
        for (Activity activity : ACTIVITIES) {
            if (activity.getClass() == cls) {
                activity.finish();
            }
        }
    }

    public static boolean isEmpty() {
        return ACTIVITIES.isEmpty();
    }

    public static Activity getLast() {
        return ACTIVITIES.getLast();
    }
}