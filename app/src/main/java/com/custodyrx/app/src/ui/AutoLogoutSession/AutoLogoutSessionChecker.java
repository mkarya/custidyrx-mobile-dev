package com.custodyrx.app.src.ui.AutoLogoutSession;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.custodyrx.app.MainActivity;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.AutoLogoutDialog;
import com.custodyrx.app.src.ui.screens.Activities.Login.Pages.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AutoLogoutSessionChecker {
    private Context context;

    private StorageHelper storageHelper;

    public AutoLogoutSessionChecker(Context context,StorageHelper storageHelper) {
        this.context = context;
        this.storageHelper = storageHelper;
    }

    protected String getCurrentDateTime() {
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Format it in "yyyy-MM-dd HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    public boolean checkSessionExpiry() {
        String currentDateTime = getCurrentDateTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        try {
            Date currentDate = sdf.parse(currentDateTime);
            Date sessionLogoutDate = sdf.parse(storageHelper.getSessionLogoutTime());

            if (currentDate != null && sessionLogoutDate != null) {
                return currentDate.compareTo(sessionLogoutDate) >= 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return false;
    }

}
