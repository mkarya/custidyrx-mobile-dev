package com.custodyrx.app.src.ui.screens.Activities.Login.Pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.custodyrx.app.MainActivity;
import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.Api.APIClient;
import com.custodyrx.app.src.ui.Api.APIInterface;
import com.custodyrx.app.src.ui.Constants.AppUtils;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.custodyrx.app.src.ui.screens.Activities.Login.ResponseModel.LoginResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
    LoaderDialog loaderDialog;
    TextInputEditText etEmail, etPassword;
    TextInputLayout tilEmail, tilPassword;
    MaterialButton btLogin;
    APIInterface apiInterface;

    StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        initView();


    }

    private void initView() {
        etEmail = findViewById(R.id.etemail);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btLogin);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        storageHelper = new StorageHelper(LoginActivity.this);
        clickListeners();
    }

    private void clickListeners() {

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    tilEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    tilPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFields();
            }
        });
    }

    void validateFields() {
        String msg = null;
        if (TextUtils.isEmpty(AppUtils.getText(etEmail))) {
            msg = getString(R.string.enter_username_valid);
            tilEmail.setError(msg);
        }

        if (TextUtils.isEmpty(AppUtils.getText(etPassword))) {
            msg = getString(R.string.enter_password_valid);
            tilPassword.setError(msg);

        }

        if (msg == null) {
            callLoginApi();
        }
    }

    private void callLoginApi() {
        if (AppUtils.isConnectedToInternet(LoginActivity.this)) {
            showProgressDialog(LoginActivity.this);
            apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<LoginResponse> call = apiInterface.login(AppUtils.getText(etEmail), AppUtils.getText(etPassword));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    hideProgressDialog();
                    Log.e(TAG, "LoginResponse : " + new Gson().toJson(response));
                    LoginResponse loginResponse;
                    if (response.isSuccessful()) {
                        loginResponse = response.body();
                        if (loginResponse.getStatusCode() == 200) {
                            storageHelper.setLoginData(loginResponse.getData().getToken(), AppUtils.getText(etPassword), true, loginResponse.getData().getUserData());
                            storageHelper.setAutoLogoutMinute(30);
                            storageHelper.setSessionLoginTime();
                            storageHelper.setSessionLogoutTime(30);
                            storageHelper.setSyncOnLogin(false);
                            AppUtils.showSnackBar(LoginActivity.this, "" + loginResponse.getMsg());
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            AppUtils.showSnackBar(LoginActivity.this, "" + loginResponse.getMsg());
                            Log.e(TAG, "Other Status Code Msg :" + loginResponse.getMsg());
                        }
                    } else {
                        AppUtils.showSnackBar(LoginActivity.this, "" + response.message());
                        Log.e(TAG, "UnSuccessful :" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    hideProgressDialog();
                    Log.e(TAG, " " + t.getMessage());
                }
            });
        } else {
            AppUtils.showSnackBar(LoginActivity.this, "" + getString(R.string.no_internet));
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
}