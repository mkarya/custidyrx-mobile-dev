package com.custodyrx.app.src.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.custodyrx.app.MainActivity;
import com.custodyrx.app.R;
import java.util.Objects;

public class LogoutDialog extends Dialog {

    Context mContext;
    View view;

    public static Button yes, no;

    public LogoutDialog(@NonNull Context context) {
        super(context);
        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_logout);
        view = Objects.requireNonNull(getWindow()).getDecorView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        view.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);


        view.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutDialog.this.dismiss();
            }
        });

    }

    public void setYesButtonClickListener(View.OnClickListener listener) {
        yes.setOnClickListener(listener);
    }
}
