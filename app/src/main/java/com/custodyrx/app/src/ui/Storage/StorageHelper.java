package com.custodyrx.app.src.ui.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.screens.Activities.Login.ResponseModel.UserData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StorageHelper {
    private SharedPreferences mPrefs;

    private static final String ISLOGIN = "is_login";
    private static final String TOKEN = "token";
    private static final String GUID = "GUID";
    private static final String isActive = "isActive";
    private static final String companyGuid = "companyGuid";
    private static final String systemRole = "systemRole";
    private static final String username = "username";
    private static final String name = "name";
    private static final String email = "email";
    private static final String password = "password";
    private static final String phone = "phone";
    private static final String avatar = "avatar";
    private static final String providerRole = "providerRole";
    private static final String providerNumber = "providerNumber";
    private static final String autoLogout = "autoLogout";
    private static final String SYNC_ON_LOGIN = "syncOnLogin";

    private static final String sessionLoginTime = "sessionLoginTime";
    private static final String sessionLogoutTime = "sessionLogoutTime";
    private static final String RF_POWER = "rfPower";

    private static final String BEEP_EVERY_TAG = "beepEveryTag";
    private static final String IGNORE_UNKNOWN_TAGS = "ignoreUnknownTags";
    private static final String TAG_SCAN_METHODE = "tagScanMethode";


    public StorageHelper(Context context) {
        mPrefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void setLoginData(String token, String securePassword, boolean isLogin, UserData userData) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putString(TOKEN, token);
            editor.putBoolean(ISLOGIN, isLogin);
            editor.putString(GUID, userData.getGuid());
            editor.putBoolean(isActive, userData.isActive());
            editor.putString(companyGuid, userData.getCompanyGuid());
            editor.putString(systemRole, userData.getSystemRole());
            editor.putString(username, userData.getUsername());
            editor.putString(name, userData.getName());
            editor.putString(email, userData.getEmail());
            editor.putString(password, securePassword);
            editor.putString(phone, userData.getPhone());
            editor.putString(avatar, userData.getAvatar());
            editor.putString(providerRole, userData.getProviderRole());
            editor.putString(providerNumber, userData.getProviderNumber());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setTagScanMethode(String tagScanMethode) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putString(TAG_SCAN_METHODE, tagScanMethode);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBeepEveryTag(boolean beepEveryTag) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putBoolean(BEEP_EVERY_TAG, beepEveryTag);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIgnoreUnknownTags(boolean ignoreUnknownTags) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putBoolean(IGNORE_UNKNOWN_TAGS, ignoreUnknownTags);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSyncOnLogin(boolean syncOnLogin) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putBoolean(SYNC_ON_LOGIN, syncOnLogin);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRfPower(float rfPower) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putFloat(RF_POWER, rfPower);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoLogoutMinute(int minutes) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            editor.putInt(autoLogout, minutes);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSessionLoginTime() {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());

            editor.putString(sessionLoginTime, currentDateTime);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSessionLogoutTime(int minutes) {
        SharedPreferences.Editor editor = mPrefs.edit();
        try {
            // Retrieve stored login time
            String loginTimeStr = mPrefs.getString(sessionLoginTime, null);

            if (loginTimeStr != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date loginDate = sdf.parse(loginTimeStr);

                if (loginDate != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(loginDate);


                    calendar.add(Calendar.MINUTE, minutes);

                    // Format and save the new logout time
                    String logoutTime = sdf.format(calendar.getTime());
                    editor.putString(sessionLogoutTime, logoutTime);
                    editor.apply();
                }
            } else {
                // If login time is missing, fallback to current time + minutes
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, minutes);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String logoutTime = sdf.format(calendar.getTime());

                editor.putString(sessionLogoutTime, logoutTime);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getRFPower() {
        return mPrefs.getFloat(RF_POWER, 18f);
    }


    public boolean isBeepEveryTag() {
        return mPrefs.getBoolean(BEEP_EVERY_TAG, true);
    }

    public boolean isIgnoreUnknownTag() {
        return mPrefs.getBoolean(IGNORE_UNKNOWN_TAGS, false);
    }

    public String getTagScanMethode() {
        return mPrefs.getString(TAG_SCAN_METHODE, "RFID");
    }

    public boolean isSyncOnLogin() {
        return mPrefs.getBoolean(SYNC_ON_LOGIN, false);
    }

    public String getSessionLoginTime() {
        return mPrefs.getString(sessionLoginTime, "");
    }

    public String getSessionLogoutTime() {
        return mPrefs.getString(sessionLogoutTime, "");
    }

    public int getAutoLogoutMinute() {
        return mPrefs.getInt(autoLogout, 30);
    }


    public boolean isLogin() {
        return mPrefs.getBoolean(ISLOGIN, false);
    }

    public String getToken() {
        return mPrefs.getString(TOKEN, "");
    }

    public String getGuid() {
        return mPrefs.getString(GUID, "");
    }

    public boolean isActive() {
        return mPrefs.getBoolean(isActive, false);
    }

    public String getCompanyGuid() {
        return mPrefs.getString(companyGuid, "");
    }

    public String getSystemRole() {
        return mPrefs.getString(systemRole, "");
    }

    public String getUsername() {
        return mPrefs.getString(username, "");
    }

    public String getName() {
        return mPrefs.getString(name, "");
    }

    public String getEmail() {
        return mPrefs.getString(email, "");
    }

    public String getPassword() {
        return mPrefs.getString(password, "");
    }

    public String getPhone() {
        return mPrefs.getString(phone, "");
    }

    public String getAvatar() {
        return mPrefs.getString(avatar, "");
    }

    public String getProviderRole() {
        return mPrefs.getString(providerRole, "");
    }

    public String getProviderNumber() {
        return mPrefs.getString(providerNumber, "");
    }


    public void clearPreferences() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear().apply();
    }
}
