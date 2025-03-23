package com.custodyrx.library.label.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.custodyrx.app.R;
import com.custodyrx.app.databinding.ViewInventoryConfigBinding;
import com.custodyrx.library.label.App;
import com.custodyrx.library.label.GlobalCfg;
import com.custodyrx.library.label.bean.InventoryParam;
import com.custodyrx.library.label.model.ReaderHelper;
import com.custodyrx.library.label.util.ScreenUtils;
import com.custodyrx.library.label.util.XLog;
import com.payne.reader.base.BaseInventory;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.EightAntenna;
import com.payne.reader.bean.config.FastTidType;
import com.payne.reader.bean.config.FourAntenna;
import com.payne.reader.bean.config.HighEightAntenna;
import com.payne.reader.bean.config.Session;
import com.payne.reader.bean.config.Target;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.bean.send.CustomSessionTargetInventory;
import com.payne.reader.bean.send.FastSwitchEightAntennaInventory;
import com.payne.reader.bean.send.FastSwitchFourAntennaInventory;
import com.payne.reader.bean.send.FastSwitchSingleAntennaInventory;
import com.payne.reader.bean.send.FastSwitchSixteenAntennaInventory;

import java.util.Arrays;
import java.util.List;

/**
 * @author naz
 * Date 2020/4/10
 */
public class InventoryConfigView extends FrameLayout {
    public ViewInventoryConfigBinding mBinding;
    private Context mContext;
    private InventoryParam mParam;
    private ConfigCustomSessionAdapter mCustomSessionAdapter;
    private ConfigFastSwitchAdapter mFastSwitchAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    public InventoryConfigView(@NonNull Context context) {
        this(context, null);
    }

    public InventoryConfigView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InventoryConfigView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mBinding = ViewInventoryConfigBinding.inflate(LayoutInflater.from(context), this, true);
        int paddingEnd = ScreenUtils.dp2px(getContext(), 15);
        int paddingStart = ScreenUtils.dp2px(getContext(), 5);
        int verticalPadding = ScreenUtils.dp2px(getContext(), 3);
        mBinding.spAntennaCount.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt1.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt2.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt3.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt4.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt5.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt6.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt7.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt8.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt9.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt10.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt11.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt12.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt13.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt14.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt15.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spFastAnt16.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spSession.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);
        mBinding.spTarget.setPadding(paddingStart, verticalPadding, paddingEnd, verticalPadding);

        //Fast Tid选中监听
        mBinding.cbFastTid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && mBinding.cbTagFocus.isChecked()) {
                mBinding.cbTagFocus.setChecked(false);
            }
        });
        //Tag Focus选中监听
        mBinding.cbTagFocus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && mBinding.cbFastTid.isChecked()) {
                mBinding.cbFastTid.setChecked(false);
            }
        });
        mBinding.cbFastTid.setOnClickListener(this::setFastType);
        mBinding.cbTagFocus.setOnClickListener(this::setFastType);

        mBinding.spAntennaCount.setOnSpinnerItemSelectedListener((parent, view, position, id) -> antCountChange(position));
        mBinding.cbFastSwitchAnt.setOnClickListener(v -> {
            antCountChange(mBinding.spAntennaCount.getSelectedIndex());
            boolean checked = mBinding.cbFastSwitchAnt.isChecked();
            if (checked) {
                mBinding.cbFastMode.setChecked(false);
            } else {
                mBinding.cbFreezer.setChecked(false);
            }
            mBinding.cbFastMode.setEnabled(!checked);
            mBinding.cbFreezer.setEnabled(checked);
        });
//        mBinding.spAntennaCount.setOnSpinnerItemSelectedListener((parent, view, position, id) -> changeId(position));
//        mBinding.cbFastSwitchAnt.setOnClickListener(v -> {
//            if (mBinding.cbFastSwitchAnt.isChecked()) {
//                if (mFastSwitchAdapter == null) {
//                    mFastSwitchAdapter = new ConfigFastSwitchAdapter(context);
//                }
//                if(mLinearLayoutManager == null){
//                    mLinearLayoutManager = new LinearLayoutManager(context);
//                }
//                mBinding.rvConfig.setLayoutManager(mLinearLayoutManager);
//                mBinding.rvConfig.setAdapter(mFastSwitchAdapter);
//            } else {
//                if (mCustomSessionAdapter == null) {
//                    mCustomSessionAdapter = new ConfigCustomSessionAdapter();
//                }
//                if(mGridLayoutManager == null){
//                    mGridLayoutManager = new GridLayoutManager(context, 4);
//                }
//                mBinding.rvConfig.setLayoutManager(mGridLayoutManager);
//                mBinding.rvConfig.setAdapter(mCustomSessionAdapter);
//            }
//            changeId(mBinding.spAntennaCount.getSelectedIndex());
//        });
    }

    private void changeId(int position) {
        boolean isFastSwitch = mBinding.cbFastSwitchAnt.isChecked();
        AntennaCount antennaCount;
        if (position == 3) {
            antennaCount = AntennaCount.SIXTEEN_CHANNELS;
        } else if (position == 2) {
            antennaCount = AntennaCount.EIGHT_CHANNELS;
        } else if (position == 1) {
            antennaCount = AntennaCount.FOUR_CHANNELS;
        } else {
            antennaCount = AntennaCount.SINGLE_CHANNEL;
        }
        if (isFastSwitch) {
            mFastSwitchAdapter.refreshAntCount(antennaCount);
        } else {
            mCustomSessionAdapter.refreshAntCount(antennaCount);
        }
    }

    private void antCountChange(int position) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mBinding.clContent);

        boolean isFastSwitch = mBinding.cbFastSwitchAnt.isChecked();
        boolean isShow = (position == 1 || position == 2 || position == 3) && !isFastSwitch;
        int showHide = isShow ? View.VISIBLE : View.GONE;
        set.setVisibility(mBinding.cbAnt1.getId(), showHide);
        set.setVisibility(mBinding.cbAnt2.getId(), showHide);
        set.setVisibility(mBinding.cbAnt3.getId(), showHide);
        set.setVisibility(mBinding.cbAnt4.getId(), showHide);

        isShow = (position == 2 || position == 3) && !isFastSwitch;
        showHide = isShow ? View.VISIBLE : View.GONE;
        set.setVisibility(mBinding.cbAnt5.getId(), showHide);
        set.setVisibility(mBinding.cbAnt6.getId(), showHide);
        set.setVisibility(mBinding.cbAnt7.getId(), showHide);
        set.setVisibility(mBinding.cbAnt8.getId(), showHide);

        showHide = (position == 3 && !isFastSwitch) ? View.VISIBLE : View.GONE;
        set.setVisibility(mBinding.cbAnt9.getId(), showHide);
        set.setVisibility(mBinding.cbAnt10.getId(), showHide);
        set.setVisibility(mBinding.cbAnt11.getId(), showHide);
        set.setVisibility(mBinding.cbAnt12.getId(), showHide);
        set.setVisibility(mBinding.cbAnt13.getId(), showHide);
        set.setVisibility(mBinding.cbAnt14.getId(), showHide);
        set.setVisibility(mBinding.cbAnt15.getId(), showHide);
        set.setVisibility(mBinding.cbAnt16.getId(), showHide);

        isShow = (position == 1 || position == 2 || position == 3) && isFastSwitch;
        showHide = isShow ? View.VISIBLE : View.GONE;
        set.setVisibility(mBinding.tvFastAnt1Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt1.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt1TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt1Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt2Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt2.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt2TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt2Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt3Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt3.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt3TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt3Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt4Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt4.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt4TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt4Time.getId(), showHide);

        isShow = (position == 2 || position == 3) && isFastSwitch;
        showHide = isShow ? View.VISIBLE : View.GONE;
        set.setVisibility(mBinding.tvFastAnt5Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt5.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt5TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt5Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt6Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt6.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt6TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt6Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt7Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt7.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt7TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt7Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt8Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt8.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt8TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt8Time.getId(), showHide);

        showHide = (position == 3 && isFastSwitch) ? View.VISIBLE : View.GONE;
        set.setVisibility(mBinding.tvFastAnt9Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt9.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt9TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt9Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt10Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt10.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt10TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt10Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt11Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt11.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt11TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt11Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt12Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt12.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt12TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt12Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt13Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt13.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt13TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt13Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt14Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt14.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt14TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt14Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt15Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt15.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt15TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt15Time.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt16Tip.getId(), showHide);
        set.setVisibility(mBinding.spFastAnt16.getId(), showHide);
        set.setVisibility(mBinding.tvFastAnt16TimesTip.getId(), showHide);
        set.setVisibility(mBinding.etFastAnt16Time.getId(), showHide);

        TransitionManager.beginDelayedTransition(mBinding.clContent);
        set.applyTo(mBinding.clContent);
        if (isFastSwitch) {
            switch (position) {
                case 3:
                    mBinding.spFastAnt9.setSelectedIndex(1);
                    mBinding.spFastAnt10.setSelectedIndex(2);
                    mBinding.spFastAnt11.setSelectedIndex(3);
                    mBinding.spFastAnt12.setSelectedIndex(4);
                    mBinding.spFastAnt13.setSelectedIndex(5);
                    mBinding.spFastAnt14.setSelectedIndex(6);
                    mBinding.spFastAnt15.setSelectedIndex(7);
                    mBinding.spFastAnt16.setSelectedIndex(8);
                case 2:
                    List<String> data = Arrays.asList(mContext.getResources().getStringArray(R.array.eight_channel));
                    mBinding.spFastAnt1.attachDataSource(data);
                    mBinding.spFastAnt2.attachDataSource(data);
                    mBinding.spFastAnt3.attachDataSource(data);
                    mBinding.spFastAnt4.attachDataSource(data);
                    mBinding.spFastAnt1.setSelectedIndex(1);
                    mBinding.spFastAnt2.setSelectedIndex(2);
                    mBinding.spFastAnt3.setSelectedIndex(3);
                    mBinding.spFastAnt4.setSelectedIndex(4);
                    mBinding.spFastAnt5.setSelectedIndex(5);
                    mBinding.spFastAnt6.setSelectedIndex(6);
                    mBinding.spFastAnt7.setSelectedIndex(7);
                    mBinding.spFastAnt8.setSelectedIndex(8);
                    break;
                case 1:
                    data = Arrays.asList(mContext.getResources().getStringArray(R.array.four_channel));
                    mBinding.spFastAnt1.attachDataSource(data);
                    mBinding.spFastAnt2.attachDataSource(data);
                    mBinding.spFastAnt3.attachDataSource(data);
                    mBinding.spFastAnt4.attachDataSource(data);
                    mBinding.spFastAnt1.setSelectedIndex(1);
                    mBinding.spFastAnt2.setSelectedIndex(2);
                    mBinding.spFastAnt3.setSelectedIndex(3);
                    mBinding.spFastAnt4.setSelectedIndex(4);
                    break;
                default:
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //消费点击事件
        return true;
    }

    public void initFastTid(InventoryParam param) {
        this.mParam = param;

          Version.ChipType chipType = GlobalCfg.get().getVersion().getChipType();
        switch (chipType) {
            case E710:
                mBinding.cbFastMode.setVisibility(VISIBLE);
                break;
            case TM670:
                mBinding.cbFastTid.setVisibility(GONE);
                mBinding.tvFastTidTip.setVisibility(GONE);
                mBinding.cbTagFocus.setVisibility(GONE);
                mBinding.tvTagFocusTip.setVisibility(GONE);
                return;
        }
        mBinding.cbFastTid.setVisibility(VISIBLE);
        mBinding.tvFastTidTip.setVisibility(VISIBLE);
        mBinding.cbTagFocus.setVisibility(VISIBLE);
        mBinding.tvTagFocusTip.setVisibility(VISIBLE);

        ReaderHelper.getReader().getImpinjFastTid(impinjFastTid -> {
            Activity activity = (Activity) mContext;
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() -> {
                FastTidType tidType = impinjFastTid.getTidType();
                mBinding.cbTagFocus.setChecked(tidType == FastTidType.FOCUS_TAG);
                mBinding.cbFastTid.setChecked(tidType == FastTidType.FAST_TAG);
            });
        }, failure -> {
            // XLog.i(getResources().getString(R.string.get_fast_tid_error));
            Toast.makeText(null,"Failed to get Fast TID status, please try again", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 设置类型
     */
    private void setFastType(View v) {
        FastTidType type;
        if (v.getId() == mBinding.cbFastTid.getId()) {
            type = mBinding.cbFastTid.isChecked() ? FastTidType.FAST_TAG : FastTidType.DISABLE;
        } else {
            type = mBinding.cbTagFocus.isChecked() ? FastTidType.FOCUS_TAG : FastTidType.DISABLE;
        }
        ReaderHelper.getReader().setImpinjFastTid(type, true,
                success -> setResult(v, true),
                failure -> setResult(v, false)
        );
    }

    private void setResult(View v, boolean success) {
        Activity activity = (Activity) mContext;
        if (activity != null) {
            activity.runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(activity,"Set up successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Setup failed, please try again", Toast.LENGTH_SHORT).show();
                    if (v.getId() == mBinding.cbFastTid.getId()) {
                        mBinding.cbFastTid.setChecked(!mBinding.cbFastTid.isChecked());
                    } else {
                        mBinding.cbTagFocus.setChecked(!mBinding.cbTagFocus.isChecked());
                    }
                }
            });
        }
    }

    public InventoryParam getInventoryParam() {
        if (mParam == null) {
            mParam = new InventoryParam();
        }
        int position = mBinding.spAntennaCount.getSelectedIndex();
        if (position == 0) {
            mParam.setAntennaCount(AntennaCount.SINGLE_CHANNEL);
        } else if (position == 1) {
            mParam.setAntennaCount(AntennaCount.FOUR_CHANNELS);
        } else if (position == 2) {
            mParam.setAntennaCount(AntennaCount.EIGHT_CHANNELS);
        } else if (position == 3) {
            mParam.setAntennaCount(AntennaCount.SIXTEEN_CHANNELS);
        } else {//?
            Toast.makeText(App.getContext(), "Ant count set error?!", Toast.LENGTH_SHORT).show();
        }
        boolean isFastSwitch = mBinding.cbFastSwitchAnt.isChecked();
        mParam.setFastSwitch(isFastSwitch);

        Session session = Session.valueOf((byte) mBinding.spSession.getSelectedIndex());
        Target target = Target.valueOf((byte) mBinding.spTarget.getSelectedIndex());
        byte repeat = getByte(mBinding.etRepeat, (byte) 1);
        byte interval = getByte(mBinding.etInterval, (byte) 0);
        boolean enablePhase = mBinding.cbPhase.isChecked();
        boolean isFreezer = mBinding.cbFreezer.isChecked();
        if (isFastSwitch) {
            BaseInventory inventory = null;
            if (position == 0) {/* 快速单天线盘存 */
                try {
                    FastSwitchSingleAntennaInventory.Builder builder = new FastSwitchSingleAntennaInventory.Builder()
                            .session(session)
                            .target(target)
                            .enablePhase(enablePhase)
                            .repeat(repeat)
                            .interval(interval);
                    if (isFreezer) {
                        builder.reserve1((byte) 0x5A);
                        builder.reserve2((byte) 0xA5);
                    } else {
                        builder.reserve1((byte) 0x00);
                        builder.reserve2((byte) 0x00);
                    }
                    inventory = builder.build();
                } catch (Exception e) {
                    XLog.w(Log.getStackTraceString(e));
                }
            } else if (position == 1) {/* 快速4天线盘存 */
                try {
                    FastSwitchFourAntennaInventory.Builder builder = new FastSwitchFourAntennaInventory.Builder()
                            .antennaA(FourAntenna.valueOf((byte) (mBinding.spFastAnt1.getSelectedIndex() - 1)))
                            .stayA(Byte.parseByte(mBinding.etFastAnt1Time.getText().toString()))
                            .antennaB(FourAntenna.valueOf((byte) (mBinding.spFastAnt2.getSelectedIndex() - 1)))
                            .stayB(Byte.parseByte(mBinding.etFastAnt2Time.getText().toString()))
                            .antennaC(FourAntenna.valueOf((byte) (mBinding.spFastAnt3.getSelectedIndex() - 1)))
                            .stayC(Byte.parseByte(mBinding.etFastAnt3Time.getText().toString()))
                            .antennaD(FourAntenna.valueOf((byte) (mBinding.spFastAnt4.getSelectedIndex() - 1)))
                            .stayD(Byte.parseByte(mBinding.etFastAnt4Time.getText().toString()))
                            .session(session)
                            .target(target)
                            .enablePhase(enablePhase)
                            .repeat(repeat)
                            .interval(interval);
                    if (isFreezer) {
                        builder.reserve1((byte) 0x5A);
                        builder.reserve2((byte) 0xA5);
                    } else {
                        builder.reserve1((byte) 0x00);
                        builder.reserve2((byte) 0x00);
                    }
                    inventory = builder.build();
                } catch (Exception e) {
                    XLog.w(Log.getStackTraceString(e));
                }
            } else if (position == 2) {/* 快速8天线盘存 */
                try {
                    FastSwitchEightAntennaInventory.Builder builder = new FastSwitchEightAntennaInventory.Builder()
                            .antennaA(EightAntenna.valueOf((byte) (mBinding.spFastAnt1.getSelectedIndex() - 1)))
                            .stayA(Byte.parseByte(mBinding.etFastAnt1Time.getText().toString()))
                            .antennaB(EightAntenna.valueOf((byte) (mBinding.spFastAnt2.getSelectedIndex() - 1)))
                            .stayB(Byte.parseByte(mBinding.etFastAnt2Time.getText().toString()))
                            .antennaC(EightAntenna.valueOf((byte) (mBinding.spFastAnt3.getSelectedIndex() - 1)))
                            .stayC(Byte.parseByte(mBinding.etFastAnt3Time.getText().toString()))
                            .antennaD(EightAntenna.valueOf((byte) (mBinding.spFastAnt4.getSelectedIndex() - 1)))
                            .stayD(Byte.parseByte(mBinding.etFastAnt4Time.getText().toString()))
                            .antennaE(EightAntenna.valueOf((byte) (mBinding.spFastAnt5.getSelectedIndex() - 1)))
                            .stayE(Byte.parseByte(mBinding.etFastAnt5Time.getText().toString()))
                            .antennaF(EightAntenna.valueOf((byte) (mBinding.spFastAnt6.getSelectedIndex() - 1)))
                            .stayF(Byte.parseByte(mBinding.etFastAnt6Time.getText().toString()))
                            .antennaG(EightAntenna.valueOf((byte) (mBinding.spFastAnt7.getSelectedIndex() - 1)))
                            .stayG(Byte.parseByte(mBinding.etFastAnt7Time.getText().toString()))
                            .antennaH(EightAntenna.valueOf((byte) (mBinding.spFastAnt8.getSelectedIndex() - 1)))
                            .stayH(Byte.parseByte(mBinding.etFastAnt8Time.getText().toString()))
                            .session(session)
                            .target(target)
                            .enablePhase(enablePhase)
                            .repeat(repeat)
                            .interval(interval);
                    if (isFreezer) {
                        builder.reserve1((byte) 0x5A);
                        builder.reserve2((byte) 0xA5);
                    } else {
                        builder.reserve1((byte) 0x00);
                        builder.reserve2((byte) 0x00);
                    }
                    inventory = builder.build();
                } catch (Exception e) {
                    XLog.w(Log.getStackTraceString(e));
                }
            } else if (position == 3) {/* 快速16天线盘存 */
                try {
                    FastSwitchSixteenAntennaInventory.Builder builder = new FastSwitchSixteenAntennaInventory.Builder()
                            .antennaA(EightAntenna.valueOf((byte) (mBinding.spFastAnt1.getSelectedIndex() - 1)))
                            .stayA(Byte.parseByte(mBinding.etFastAnt1Time.getText().toString()))
                            .antennaB(EightAntenna.valueOf((byte) (mBinding.spFastAnt2.getSelectedIndex() - 1)))
                            .stayB(Byte.parseByte(mBinding.etFastAnt2Time.getText().toString()))
                            .antennaC(EightAntenna.valueOf((byte) (mBinding.spFastAnt3.getSelectedIndex() - 1)))
                            .stayC(Byte.parseByte(mBinding.etFastAnt3Time.getText().toString()))
                            .antennaD(EightAntenna.valueOf((byte) (mBinding.spFastAnt4.getSelectedIndex() - 1)))
                            .stayD(Byte.parseByte(mBinding.etFastAnt4Time.getText().toString()))
                            .antennaE(EightAntenna.valueOf((byte) (mBinding.spFastAnt5.getSelectedIndex() - 1)))
                            .stayE(Byte.parseByte(mBinding.etFastAnt5Time.getText().toString()))
                            .antennaF(EightAntenna.valueOf((byte) (mBinding.spFastAnt6.getSelectedIndex() - 1)))
                            .stayF(Byte.parseByte(mBinding.etFastAnt6Time.getText().toString()))
                            .antennaG(EightAntenna.valueOf((byte) (mBinding.spFastAnt7.getSelectedIndex() - 1)))
                            .stayG(Byte.parseByte(mBinding.etFastAnt7Time.getText().toString()))
                            .antennaH(EightAntenna.valueOf((byte) (mBinding.spFastAnt8.getSelectedIndex() - 1)))
                            .stayH(Byte.parseByte(mBinding.etFastAnt8Time.getText().toString()))
                            .antennaI(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt9.getSelectedIndex() - 1)))
                            .stayI(Byte.parseByte(mBinding.etFastAnt9Time.getText().toString()))
                            .antennaJ(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt10.getSelectedIndex() - 1)))
                            .stayJ(Byte.parseByte(mBinding.etFastAnt10Time.getText().toString()))
                            .antennaK(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt11.getSelectedIndex() - 1)))
                            .stayK(Byte.parseByte(mBinding.etFastAnt11Time.getText().toString()))
                            .antennaL(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt12.getSelectedIndex() - 1)))
                            .stayL(Byte.parseByte(mBinding.etFastAnt12Time.getText().toString()))
                            .antennaM(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt13.getSelectedIndex() - 1)))
                            .stayM(Byte.parseByte(mBinding.etFastAnt13Time.getText().toString()))
                            .antennaN(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt14.getSelectedIndex() - 1)))
                            .stayN(Byte.parseByte(mBinding.etFastAnt14Time.getText().toString()))
                            .antennaO(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt15.getSelectedIndex() - 1)))
                            .stayO(Byte.parseByte(mBinding.etFastAnt15Time.getText().toString()))
                            .antennaP(HighEightAntenna.valueOf((byte) (mBinding.spFastAnt16.getSelectedIndex() - 1)))
                            .stayP(Byte.parseByte(mBinding.etFastAnt16Time.getText().toString()))
                            .session(session)
                            .target(target)
                            .enablePhase(enablePhase)
                            .repeat(repeat)
                            .interval(interval);
                    if (isFreezer) {
                        builder.reserve1((byte) 0x5A);
                        builder.reserve2((byte) 0xA5);
                    } else {
                        builder.reserve1((byte) 0x00);
                        builder.reserve2((byte) 0x00);
                    }
                    inventory = builder.build();
                } catch (Exception e) {
                    XLog.w(Log.getStackTraceString(e));
                }
            }
            mParam.setInventory(inventory);
        } else {
            mParam.clearCustomSessionIds();
            if (position > 0) {
                if (mBinding.cbAnt1.isChecked()) {
                    mParam.addCustomSessionId(0);
                }
                if (mBinding.cbAnt2.isChecked()) {
                    mParam.addCustomSessionId(1);
                }
                if (mBinding.cbAnt3.isChecked()) {
                    mParam.addCustomSessionId(2);
                }
                if (mBinding.cbAnt4.isChecked()) {
                    mParam.addCustomSessionId(3);
                }
                if (position > 1) {
                    if (mBinding.cbAnt5.isChecked()) {
                        mParam.addCustomSessionId(4);
                    }
                    if (mBinding.cbAnt6.isChecked()) {
                        mParam.addCustomSessionId(5);
                    }
                    if (mBinding.cbAnt7.isChecked()) {
                        mParam.addCustomSessionId(6);
                    }
                    if (mBinding.cbAnt8.isChecked()) {
                        mParam.addCustomSessionId(7);
                    }
                    if (position > 2) {
                        if (mBinding.cbAnt9.isChecked()) {
                            mParam.addCustomSessionId(8);
                        }
                        if (mBinding.cbAnt10.isChecked()) {
                            mParam.addCustomSessionId(9);
                        }
                        if (mBinding.cbAnt11.isChecked()) {
                            mParam.addCustomSessionId(10);
                        }
                        if (mBinding.cbAnt12.isChecked()) {
                            mParam.addCustomSessionId(11);
                        }
                        if (mBinding.cbAnt13.isChecked()) {
                            mParam.addCustomSessionId(12);
                        }
                        if (mBinding.cbAnt14.isChecked()) {
                            mParam.addCustomSessionId(13);
                        }
                        if (mBinding.cbAnt15.isChecked()) {
                            mParam.addCustomSessionId(14);
                        }
                        if (mBinding.cbAnt16.isChecked()) {
                            mParam.addCustomSessionId(15);
                        }
                    }
                }
            } else {
                mParam.addCustomSessionId(0);
            }
            BaseInventory inventory = new CustomSessionTargetInventory.Builder()
                    .session(session)
                    .target(target)
                    .enablePhase(enablePhase)
                    .repeat(repeat)
                    .build();
            mParam.setInventory(inventory);
        }
        return mParam;
    }

    public static byte getByte(EditText et, byte miniNum) {
        byte value;
        try {
            value = (byte) Integer.parseInt(et.getText().toString(), 10);
        } catch (Exception ignored) {
            value = miniNum;
        }
        if (value <= miniNum) {
            value = miniNum;
            et.setText("" + miniNum);
        }
        return value;
    }
}