package com.custodyrx.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.custodyrx.app.src.ui.Api.APIClient;
import com.custodyrx.app.src.ui.Api.APIInterface;
import com.custodyrx.app.src.ui.AutoLogoutSession.AutoLogoutSessionChecker;
import com.custodyrx.app.src.ui.Constants.AppUtils;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.AutoLogoutDialog;
import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.custodyrx.app.src.ui.screens.Activities.Login.LogoutResponseModel.LogoutResponse;
import com.custodyrx.app.src.ui.screens.Activities.Login.Pages.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";

    APIInterface apiInterface;
    StorageHelper storageHelper;

    LoaderDialog loaderDialog;
    AutoLogoutDialog autoLogoutDialog;
    private AutoLogoutSessionChecker sessionChecker;
    private final Handler sessionCheckHandler = new Handler();
    private final Runnable sessionCheckRunnable = new Runnable() {
        @Override
        public void run() {
            sessionChecker = new AutoLogoutSessionChecker(BaseActivity.this, storageHelper);
            if (sessionChecker.checkSessionExpiry()) {
                handleSessionExpired();
            }
            sessionCheckHandler.postDelayed(this, 1000); // Check every second
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        setupToolbar();

        storageHelper = new StorageHelper(BaseActivity.this);
        autoLogoutDialog = new AutoLogoutDialog(BaseActivity.this);

        sessionCheckHandler.post(sessionCheckRunnable);

    }

    @LayoutRes
    protected abstract int getLayoutResId();

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        }
    }

    private void handleSessionExpired() {
        callLogoutApi();
    }

    private void callLogoutApi() {
        if (AppUtils.isConnectedToInternet(BaseActivity.this)) {
            showProgressDialog(BaseActivity.this);
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Log.e(TAG, "Token : " + storageHelper.getToken());
            Call<LogoutResponse> call = apiInterface.logout("Bearer " + storageHelper.getToken());
            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                    hideProgressDialog();
                    Log.e(TAG, "LogoutResponse : " + new Gson().toJson(response));
                    LogoutResponse logoutResponse;
                    if (response.isSuccessful()) {
                        logoutResponse = response.body();
                        if (logoutResponse.getStatusCode() == 200) {
                            showSnackBar(getString(R.string.msg_auto_logout));
                            storageHelper.clearPreferences();
                            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            AppUtils.showSnackBar(BaseActivity.this, "" + logoutResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + logoutResponse.getMsg());
                        }

                    } else {
                        AppUtils.showSnackBar(BaseActivity.this, "" + response.message());
                        Log.e(TAG, "UnSuccessful :" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    hideProgressDialog();
                    Log.e(TAG, " " + t.getMessage());
                }
            });
        } else {
            AppUtils.showSnackBar(BaseActivity.this, "" + getString(R.string.no_internet));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionCheckHandler.removeCallbacks(sessionCheckRunnable); // Stop handler to avoid memory leaks
    }


    protected void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    protected String getCurrentDateTime() {
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Format it in "yyyy-MM-dd HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String currentDateTime = sdf.format(calendar.getTime());

        return currentDateTime;
    }

    protected String getSubmitInventoryTime() {
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Format it in "MMM dd yyyy hh:mm a"
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm a", Locale.getDefault());

        // Convert to string
        return sdf.format(calendar.getTime());
    }

    protected  String getCurrentUTCTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date()); // e.g., "2025-02-22T12:34:56Z"
    }

    protected void showSnackBar(String message) {
        ViewGroup view = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        viewGroup.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

        TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.white));

        SpannableString boldText = new SpannableString("CLOSE");
        boldText.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), 0);

        snackbar.setAction(boldText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }

    public void showProgressDialog(Context context) {
        try {

            loaderDialog = new LoaderDialog(context);
            loaderDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideProgressDialog() {
        try {
            if (loaderDialog != null && loaderDialog.isShowing()) {
                loaderDialog.dismiss();
                loaderDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
