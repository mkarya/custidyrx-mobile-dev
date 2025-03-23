package com.custodyrx.library.label.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author naz
 * Date 2020/4/7
 */
public class InputUtils {
    /**
     * 隐藏软键盘
     *
     * @param context Context
     * @param v       View
     * @return 是否隐藏成功
     */
    public static Boolean hideInputMethod(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return false;
    }

    /**
     * 判断当前点击屏幕的地方是否是软键盘
     *
     * @param v     View
     * @param event MotionEvent
     * @return true or false
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0],
                    top = leftTop[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            // 保留点击EditText的事件
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
}
