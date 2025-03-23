package com.custodyrx.app.src.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.Storage.StorageHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GeneralSettingDialog extends Dialog {

    Context mContext;
    View view;

    Switch syncOnLogin;

    Spinner spLogoutMinute;

    StorageHelper storageHelper;

    public GeneralSettingDialog(@NonNull Context context) {
        super(context);
        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_general_setting);
        view = Objects.requireNonNull(getWindow()).getDecorView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        view.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        storageHelper = new StorageHelper(mContext);

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralSettingDialog.this.dismiss();
            }
        });

        syncOnLogin = view.findViewById(R.id.syncSwitch);
        spLogoutMinute = view.findViewById(R.id.splogoutMinute);

        syncOnLogin.setChecked(storageHelper.isSyncOnLogin());

        String[] logoutTimers = mContext.getResources().getStringArray(R.array.logout_timers);
        List<String> timerList = Arrays.asList(logoutTimers);
        int position = timerList.indexOf(String.valueOf(storageHelper.getAutoLogoutMinute()));
        spLogoutMinute.setSelection(position);

        syncOnLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSyncOnLogin) {
                if (isSyncOnLogin) {
                    storageHelper.setSyncOnLogin(true);
                } else {
                    storageHelper.setSyncOnLogin(false);
                }
            }
        });



        spLogoutMinute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                Log.e("Settings  : ", "" + adapterView.getItemAtPosition(position));
                storageHelper.setAutoLogoutMinute(Integer.parseInt(adapterView.getItemAtPosition(position).toString()));
                storageHelper.setSessionLogoutTime(Integer.parseInt(adapterView.getItemAtPosition(position).toString()));


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


}
