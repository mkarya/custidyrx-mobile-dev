package com.custodyrx.app.src.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.custodyrx.app.R;
import com.custodyrx.app.src.ui.Storage.StorageHelper;

import java.util.Objects;

public class ScannerRFIDSettingDialog extends Dialog {

    Context mContext;
    View view;

    TextView text_rfpower;

    SeekBar seekBar;

    Switch beepEveryTag, ignoreUnknownTag;

    StorageHelper storageHelper;

    RadioGroup radioGroup;

    public ScannerRFIDSettingDialog(@NonNull Context context) {
        super(context);
        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_scanner_rfid_setting);
        view = Objects.requireNonNull(getWindow()).getDecorView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        view.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        storageHelper = new StorageHelper(mContext);
        text_rfpower = view.findViewById(R.id.text_rfpower);
        seekBar = view.findViewById(R.id.seekbar);
        beepEveryTag = view.findViewById(R.id.beepEveryTag);
        ignoreUnknownTag = view.findViewById(R.id.ignoreUnknownTag);
        radioGroup = view.findViewById(R.id.radioGroup);

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScannerRFIDSettingDialog.this.dismiss();
            }
        });


        seekBar.setProgress((int) (storageHelper.getRFPower() * 2));
        text_rfpower.setText(String.valueOf(storageHelper.getRFPower()));
        beepEveryTag.setChecked(storageHelper.isBeepEveryTag());
        ignoreUnknownTag.setChecked(storageHelper.isIgnoreUnknownTag());
        setRadioButtonSelection(storageHelper.getTagScanMethode());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                float scaledValue = progress / 2.0f;
                text_rfpower.setText(String.valueOf(scaledValue));
                storageHelper.setRfPower(scaledValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        beepEveryTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean beepEveryTag) {
                if (beepEveryTag) {
                    storageHelper.setBeepEveryTag(true);
                } else {
                    storageHelper.setBeepEveryTag(false);
                }
            }
        });

        ignoreUnknownTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ignoreUnknownTags) {
                if (ignoreUnknownTags) {
                    storageHelper.setIgnoreUnknownTags(true);
                } else {
                    storageHelper.setIgnoreUnknownTags(false);
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                if (selectedRadioButton != null) {
                    storageHelper.setTagScanMethode(selectedRadioButton.getText().toString());
                }
            }
        });


    }

    private void setRadioButtonSelection(String value) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getText().toString().equalsIgnoreCase(value)) {
                radioGroup.check(radioButton.getId());
                break;
            }
        }
    }
}
