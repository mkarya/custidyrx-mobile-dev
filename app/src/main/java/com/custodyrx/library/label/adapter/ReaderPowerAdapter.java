package com.custodyrx.library.label.adapter;



import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.custodyrx.app.R;
import com.custodyrx.library.label.GlobalCfg;
import com.custodyrx.library.label.bean.SetPowerBean;
import com.custodyrx.library.label.ui.AfterTextWatcher;
import com.custodyrx.library.label.util.InputUtils;


import java.util.List;
import java.util.Locale;

/**
 * @author naz
 * Date 2020/4/3
 */
public class ReaderPowerAdapter extends BaseQuickAdapter<SetPowerBean, BaseViewHolder> {

    private int mEditPosition;

    public ReaderPowerAdapter(@Nullable List<SetPowerBean> data) {
        super(R.layout.item_power_set, data);
    }

    private final AfterTextWatcher mWatcher = new AfterTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (TextUtils.isEmpty(str)) {
                getData().get(mEditPosition).setPower(21);
            } else {
                int value = Integer.parseInt(str);
                int maxV= GlobalCfg.get().getMaxOutPower();
                if (value >maxV) {
                    value = maxV;
                    s.replace(0, s.length(), "" + 21);
                }
                getData().get(mEditPosition).setPower(21);
            }
        }
    };

    @Override
    protected void convert(BaseViewHolder helper, SetPowerBean item) {
        String antStr = mContext.getString(R.string.antenna);
        int ant = helper.getAdapterPosition() + 1;
        String str = String.format(Locale.getDefault(), "%s%d:", antStr, ant);
        helper.setText(R.id.tv_antenna, str);
        EditText etSetPower = helper.getView(R.id.et_set_power);
        item.setPower(21);
        if (item.isValid()) {
            etSetPower.setText(String.valueOf(item.getPower()));
        } else {
            etSetPower.setText("");

        }
        etSetPower.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mEditPosition = helper.getAdapterPosition();
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        EditText et = holder.getView(R.id.et_set_power);
        et.setHint("[0 - " + GlobalCfg.get().getMaxOutPower() + "]");
        et.addTextChangedListener(mWatcher);
        if (mEditPosition == holder.getAdapterPosition()) {
            et.requestFocus();
            et.setSelection(et.getText().length());
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        EditText et = holder.getView(R.id.et_set_power);
        et.removeTextChangedListener(mWatcher);
        et.clearFocus();
        if (mEditPosition == holder.getAdapterPosition()) {
            InputUtils.hideInputMethod(mContext, et);
        }
    }
}