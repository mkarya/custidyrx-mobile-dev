package com.custodyrx.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.custodyrx.app.src.database.DatabaseHandler;
import com.custodyrx.app.src.ui.Api.APIClient;
import com.custodyrx.app.src.ui.Api.APIInterface;
import com.custodyrx.app.src.ui.AutoLogoutSession.AutoLogoutSessionChecker;
import com.custodyrx.app.src.ui.Constants.AppUtils;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.AutoLogoutDialog;
import com.custodyrx.app.src.ui.dialogs.GeneralSettingDialog;
import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.custodyrx.app.src.ui.dialogs.SyncProgressDialog;
import com.custodyrx.app.src.ui.dialogs.LogoutDialog;
import com.custodyrx.app.src.ui.dialogs.ScannerRFIDSettingDialog;
import com.custodyrx.app.src.ui.screens.Activities.InventoryTypeActivity;
import com.custodyrx.app.src.ui.screens.Activities.Login.LogoutResponseModel.LogoutResponse;
import com.custodyrx.app.src.ui.screens.Activities.Login.Pages.LoginActivity;
import com.custodyrx.app.src.ui.screens.Fragments.DashboardFragment;
import com.custodyrx.library.label.GlobalCfg;
import com.custodyrx.library.label.bean.type.Key;
import com.custodyrx.library.label.bean.type.LinkType;
import com.custodyrx.library.label.model.BeeperHelper;
import com.custodyrx.library.label.model.ReaderHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.naz.serial.port.SerialPortFinder;
import com.orhanobut.hawk.Hawk;
import com.payne.connect.port.SerialPortHandle;
import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.BaudRate;
import com.payne.reader.bean.config.Cmd;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.communication.ConnectHandle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    public static TextView title;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawerLayout;
    LinearLayout ll_dashboard, ll_inventory, ll_checklist, ll_transfer, ll_settings, ll_childofsetting, ll_general, ll_scannerRfid, ll_sync, ll_logout, ll_appbar_logout;

    Fragment fFragment;
    FrameLayout content_frame;

    TextView txtName, txtEmail;

    private String TAG = "MainActivity";
    LoaderDialog loaderDialog;

    StorageHelper storageHelper;
    APIInterface apiInterface;
    AutoLogoutDialog autoLogoutDialog;
    private AutoLogoutSessionChecker sessionChecker;
    private final Handler sessionCheckHandler = new Handler();
    private final Runnable sessionCheckRunnable = new Runnable() {
        @Override
        public void run() {
            sessionChecker = new AutoLogoutSessionChecker(MainActivity.this, storageHelper);
            if (sessionChecker.checkSessionExpiry()) {
                handleSessionExpired();
            }
            sessionCheckHandler.postDelayed(this, 1000); // Check every second
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        initViewsForDrawer();
        clickListeners();
    }


    private void handleSessionExpired() {
        callAutoLogoutApi();
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.itemsight);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        loadFragment(new DashboardFragment());

    }

    public void loadFragment(Fragment fragment) {

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
//            if (!(fragment instanceof HomeFragment)){
            fFragment = fragment;

            ft.addToBackStack(null);
//            }
            ft.commit();
        }

    }

    private void initViewsForDrawer() {
        ll_dashboard = findViewById(R.id.ll_dashboard);
        ll_inventory = findViewById(R.id.ll_inventory);
        ll_checklist = findViewById(R.id.ll_checklist);
        ll_transfer = findViewById(R.id.ll_transfer);
        ll_settings = findViewById(R.id.ll_settings);
        ll_childofsetting = findViewById(R.id.ll_childofsetting);
        ll_general = findViewById(R.id.ll_general);
        ll_scannerRfid = findViewById(R.id.ll_scanner_rfid);
        ll_sync = findViewById(R.id.ll_sync);
        ll_logout = findViewById(R.id.ll_logout);
        content_frame = findViewById(R.id.content_frame);
        ll_appbar_logout = findViewById(R.id.ll_appbar_logout);
        txtName = findViewById(R.id.txtname);
        txtEmail = findViewById(R.id.txtEmail);
        storageHelper = new StorageHelper(MainActivity.this);
        autoLogoutDialog = new AutoLogoutDialog(this);


        /*ll_checklist.setAlpha(0.5f);
        ll_checklist.setEnabled(false);
        ll_transfer.setAlpha(0.5f);
        ll_transfer.setEnabled(false);*/

        txtName.setText(storageHelper.getName());
        txtEmail.setText(storageHelper.getEmail());

        sessionCheckHandler.post(sessionCheckRunnable);

        Log.e(TAG, "Login : " + storageHelper.getSessionLoginTime());
        Log.e(TAG, "Logout : " + storageHelper.getSessionLogoutTime());


    }

    private void clickListeners() {
        ll_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_childofsetting.getVisibility() == View.VISIBLE) {
                    ll_childofsetting.setVisibility(View.GONE);
                } else {
                    ll_childofsetting.setVisibility(View.VISIBLE);
                }
            }
        });

        ll_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                startActivity(new Intent(MainActivity.this, InventoryTypeActivity.class));
            }
        });

        ll_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                SyncProgressDialog loadingDialog = new SyncProgressDialog(MainActivity.this);
                loadingDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        showSnackBar("Sync Completed");
                    }
                }, 3000);
            }
        });

        ll_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                GeneralSettingDialog generalSettingDialog = new GeneralSettingDialog(MainActivity.this);
                generalSettingDialog.show();
            }
        });

        ll_scannerRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                ScannerRFIDSettingDialog scannerRFIDSettingDialog = new ScannerRFIDSettingDialog(MainActivity.this);
                scannerRFIDSettingDialog.show();
            }
        });
        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }


                LogoutDialog logoutDialog = new LogoutDialog(MainActivity.this);
                logoutDialog.show();

                logoutDialog.setYesButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();
                        callLogoutApi();
                    }
                });
            }
        });
        ll_appbar_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutDialog logoutDialog = new LogoutDialog(MainActivity.this);
                logoutDialog.show();
                logoutDialog.setYesButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();
                        callLogoutApi();

                    }
                });

            }
        });
    }


    private void callAutoLogoutApi() {
        if (AppUtils.isConnectedToInternet(MainActivity.this)) {
            showProgressDialog(MainActivity.this);
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
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            AppUtils.showSnackBar(MainActivity.this, "" + logoutResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + logoutResponse.getMsg());
                        }

                    } else {
                        AppUtils.showSnackBar(MainActivity.this, "" + response.message());
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
            AppUtils.showSnackBar(MainActivity.this, "" + getString(R.string.no_internet));
        }
    }

    private void callLogoutApi() {
        if (AppUtils.isConnectedToInternet(MainActivity.this)) {
            showProgressDialog(MainActivity.this);
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
                            storageHelper.clearPreferences();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            AppUtils.showSnackBar(MainActivity.this, "" + logoutResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + logoutResponse.getMsg());
                        }

                    } else {
                        AppUtils.showSnackBar(MainActivity.this, "" + response.message());
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
            AppUtils.showSnackBar(MainActivity.this, "" + getString(R.string.no_internet));
        }
    }

    public void showProgressDialog(Context ctx) {
        try {

            loaderDialog = new LoaderDialog(ctx);
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

    private void disableViewGroup(ViewGroup viewGroup) {
        viewGroup.setEnabled(false);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(false);
        }
    }


    public Fragment currentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionCheckHandler.removeCallbacks(sessionCheckRunnable); // Stop handler to avoid memory leaks
    }


    @Override
    public void onBackPressed() {
        if (((currentFragment()) instanceof DashboardFragment)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

}