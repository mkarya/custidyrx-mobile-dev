package com.custodyrx.library.label.ui;

import android.text.TextWatcher;

/**
 * @author naz
 * Date 2020/4/7
 */
public abstract class AfterTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // ignore
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // ignore
    }
}
