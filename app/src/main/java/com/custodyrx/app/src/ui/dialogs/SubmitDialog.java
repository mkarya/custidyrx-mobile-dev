package com.custodyrx.app.src.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.custodyrx.app.R;

import java.util.Objects;

public class SubmitDialog extends Dialog {

    Context mContext;
    View view;

    public SubmitDialog(@NonNull Context context) {
        super(context);
        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_submit);
        view = Objects.requireNonNull(getWindow()).getDecorView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        view.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        ImageView gifImageView = findViewById(R.id.gifImageView);

        // Load GIF using Glide
        Glide.with(mContext)
                .asGif()
                .load(R.drawable.loding_gif)
                .into(gifImageView);


    }
}
