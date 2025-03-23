package com.custodyrx.app.src.ui.Constants;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.google.android.material.snackbar.Snackbar;
import com.custodyrx.app.R;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AppUtils {

    public static final String CACHEFOLDER = ".custodyRx";

    public static LoaderDialog loaderDialog;


    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
            }
        }
        return false;
    }

    public static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
//        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = android.util.Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        if (extension != null) {
            extension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        Log.e("FileUtil", " extension :" + extension);
        return extension;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static void showSnackBar(Context context, String message) {
        ViewGroup view = (ViewGroup) ((ViewGroup) Objects.requireNonNull((Activity) context).findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        viewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.black));

        TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.white));

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


    public static void showProgressDialog(Context ctx) {
        try {

            if (!loaderDialog.isShowing()) {
                loaderDialog = new LoaderDialog(ctx);
                loaderDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideProgressDialog() {
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
