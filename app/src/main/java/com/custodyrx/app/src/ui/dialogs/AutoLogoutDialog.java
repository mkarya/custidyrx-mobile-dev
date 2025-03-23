package com.custodyrx.app.src.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.custodyrx.app.R;

import java.util.Objects;

public class AutoLogoutDialog extends Dialog {

    Context mContext;
    View view;

    public static Button yes;

    public AutoLogoutDialog(@NonNull Context context) {
        super(context);
        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_auto_logout);
        view = Objects.requireNonNull(getWindow()).getDecorView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        view.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);


        yes = view.findViewById(R.id.yes);

    }

    public void setYesButtonClickListener(View.OnClickListener listener) {
        yes.setOnClickListener(listener);
    }
}
