package com.custodyrx.app.src.ui.screens.Activities.PerformInventory.Pages;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custodyrx.app.BaseActivity;
import com.custodyrx.app.R;

import com.custodyrx.app.src.database.DatabaseHandler;
import com.custodyrx.app.src.ui.Storage.StorageHelper;
import com.custodyrx.app.src.ui.dialogs.LoaderDialog;
import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Items;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Activities.SelectResultActivity;
import com.custodyrx.app.src.ui.screens.Activities.TagList.Pages.TagListActivity;
import com.custodyrx.library.label.App;
import com.custodyrx.library.label.GlobalCfg;
import com.custodyrx.library.label.adapter.ReaderPowerAdapter;
import com.custodyrx.library.label.base.Command;
import com.custodyrx.library.label.base.ReaderBase;
import com.custodyrx.library.label.bean.InventoryParam;
import com.custodyrx.library.label.bean.InventoryTagBean;
import com.custodyrx.library.label.bean.SetPowerBean;
import com.custodyrx.library.label.bean.type.Key;
import com.custodyrx.library.label.bean.type.LinkType;

import com.custodyrx.library.label.model.BeeperHelper;
import com.custodyrx.library.label.model.ReaderHelper;

import com.custodyrx.library.label.model.ScannerSetting;
import com.custodyrx.library.label.model.TDCodeTagBuffer;
import com.custodyrx.library.label.ui.AfterTextWatcher;
import com.custodyrx.library.label.ui.home.inventory.ItemSpacingDecoration;
import com.custodyrx.library.label.ui.home.inventory.PerformInventoryTagAdapter;
import com.custodyrx.library.label.ui.home.inventory.PerformLocalInventoryTagAdapter;
import com.custodyrx.library.label.ui.widget.InventoryConfigView;
import com.custodyrx.library.label.ui.widget.VerticalDrawerLayout;
import com.custodyrx.library.label.util.PowerUtils;
import com.custodyrx.library.label.util.XLog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.nativec.tools.ModuleManager;
import com.nativec.tools.SerialPort;

import com.orhanobut.hawk.Hawk;

import com.payne.connect.port.SerialPortHandle;
import com.payne.reader.Reader;
import com.payne.reader.base.BaseInventory;
import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.Cmd;
import com.payne.reader.bean.config.ResultCode;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.InventoryTag;
import com.payne.reader.bean.receive.InventoryTagEnd;
import com.payne.reader.bean.receive.OutputPower;
import com.payne.reader.bean.receive.Success;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.bean.send.InventoryConfig;
import com.payne.reader.bean.send.OutputPowerConfig;
import com.payne.reader.bean.send.PowerEightAntenna;
import com.payne.reader.bean.send.PowerFourAntenna;
import com.payne.reader.bean.send.PowerSingleAntenna;
import com.payne.reader.bean.send.PowerSixteenAntenna;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.ThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformInventoryActivity extends BaseActivity {


    private String TAG = "PerformInventoryActivity";
    private String mLastDevicePath = "devicePath";

    DatabaseHandler database;
    StorageHelper storageHelper;

    private Reader mReader;
    private ReaderHelper mReaderHelper;
    private ReaderBase mReaderBase;

    private SerialPort mSerialPort = null;

    private SerialPortHandle handle;

    private AntennaCount mAntennaCount;

    private HashMap<String, InventoryTagBean> mMap = new HashMap<>(100);
    private SimpleDateFormat mSdf = XLog.getSafeDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    TextView tv_qty, tv_unit_count;


    MaterialButton refreshClear, btstartStop, next;

    RecyclerView rvInventory, rvMatchInventory;

    ImageView ivMenu;

    VerticalDrawerLayout vdlMenu;

    InventoryConfigView configView;


    private PerformInventoryTagAdapter performInventoryTagAdapter;
    private PerformMatchedInventoryTagAdapter performMatchedInventoryTagAdapter;
    private PerformLocalInventoryTagAdapter performLocalInventoryTagAdapter;
    int unknownCount = 0;
    int unknownMatchCount = 0;
    /* Total tag Count */
    private int mTotalCount = 0;
    /* Last Second Count */
    private int mLastSecondCount = 0;

    private int mCountTag;
    private boolean mFreezerMode;
    private boolean mCanSaveTemp;
    private ArrayList<String> mCurrRecvList;
    private HashMap<String, Integer> mOverMap;
    private ArrayList<String> mBaseList;

    private volatile int mDelayMs;
    private volatile int mLoopCount = -1;
    private volatile int mRecvCount;
    /* Start RunTime */
    private long mStartRunTime;
    /* Cmd StartTime */
    private long mCmdStartTime;
    private InventoryParam mInventoryParam;

    private boolean mFastSwitch;
    private boolean mFastMode;

    private int mAntennaId;
    private boolean mSaveInventoryLog;
    private int mSaveId;

    private int mBeepInterval;
    private long mLastPerBeepTime;
    private long mLastEndBeepTime;

    private InnerConsumer<byte[]> mConsumer;
    private LinearLayoutManager mLayoutManager, matchInventoryLayoutManager;

    private List<InventoryTagBean> mData;

    List<AllProduct> allProducts;
    List<AllProduct> newAllProduct;

    private int totalQty;
    private int totalUnitCount;

    private GlobalCfg mCfg;
    private boolean mNotBTLink;
    private String mLogSavePath;

    private volatile byte mCurrPower;
    private volatile byte mSetPower;

    private ReaderPowerAdapter readerPowerAdapter;

    LoaderDialog loaderDialog;

    private static TDCodeTagBuffer m_curOperateBinDCodeTagbuffer;
    private LocalBroadcastManager lbm;


    private final BroadcastReceiver mRecv = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshBarcodeTagList();

        }
    };
    private List<TDCodeTagBuffer.BinDCodeTagMap> tagListData;


    private Consumer<OutputPower> mOnGetPowerSuccess = new Consumer<OutputPower>() {
        @Override
        public void accept(OutputPower outputPower) throws Exception {
            mCurrPower = outputPower.getOutputPower()[0];
            XLog.i("CurrPower:" + mCurrPower);
        }
    };
    private Consumer<Failure> mOnGetPowerError = new Consumer<Failure>() {
        @Override
        public void accept(Failure failure) throws Exception {
            mReader.getOutputPower(mOnGetPowerSuccess, mOnGetPowerError);
        }
    };
    private Consumer<Success> mOnSetPowerSuccess = new Consumer<Success>() {
        @Override
        public void accept(Success success) throws Exception {
            XLog.i("SetTempPower:" + mSetPower + ".OK");
            mCurrPower = mSetPower;
        }
    };
    private Consumer<Failure> mOnSetPowerError = new Consumer<Failure>() {
        @Override
        public void accept(Failure failure) throws Exception {
            mReader.setOutputPowerUniformly(mSetPower, true, mOnSetPowerSuccess, mOnSetPowerError);
        }
    };

    @SuppressLint("HandlerLeak")
    private InnerHandler mHandler = new InnerHandler(this);

    private AfterTextWatcher mWatcher = new AfterTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if ("-123456".equals(str)) {

                //StartActivityUtils.startActivity(this, LogActivity.class);
            }
        }
    };

    private VerticalDrawerLayout.SimpleDrawerListener mDrawerListener = new VerticalDrawerLayout.SimpleDrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            ivMenu.setRotation(slideOffset * 180);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mFastSwitch = configView.mBinding.cbFastSwitchAnt.isChecked();
            mFreezerMode = configView.mBinding.cbFreezer.isChecked();
            mFastMode = configView.mBinding.cbFastMode.isChecked();

            try {
                mLoopCount = Integer.parseInt(configView.mBinding.etLoopCount.getText().toString().trim());
            } catch (Exception e) {
                mLoopCount = -1;
            }
            if (mLoopCount <= 0) {
                mLoopCount = -1;
                configView.mBinding.etLoopCount.setText("-1");
            }
            mInventoryParam = configView.getInventoryParam();

            mReader.switchAntennaCount(mInventoryParam.getAntennaCount());

            BaseInventory inventory = mInventoryParam.getInventory();
            if (inventory != null) {
                boolean isPhase = inventory.enablePhase();
                performInventoryTagAdapter.enablePhase(isPhase);
            }
        }
    };

    public VerticalDrawerLayout getVdlMenu() {
        return vdlMenu;
    }


    private boolean mIsSleep;
    private boolean mIsRecharge = false;
    private final Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ReaderHelper.getDefaultHelper().getDevicePower(devicePower -> {
                        int v = devicePower.getDevicePower();
                        int powerLevel = (v - 3500) / 65;
                        runOnUiThread(() -> {
                            showToast("Power Leval : " + powerLevel);
                        });
                    },
                    failure -> {
                        XLog.i("获取电量失败");
                    });
            handler.removeCallbacks(this);
            handler.postDelayed(this, 14000);
        }
    };
    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LinkType linkType = GlobalCfg.get().getLinkType();
            String action = intent.getAction();

            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                mIsSleep = true;
                if (linkType == LinkType.LINK_TYPE_BLUETOOTH) {
                    ReaderHelper.getDefaultHelper().wakeupInterfaceBoard();
                    handler.removeCallbacks(runnable);
                    handler.post(runnable);
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {

                if (linkType == LinkType.LINK_TYPE_BLUETOOTH) {
                    handler.removeCallbacks(runnable);
                }
            }
        }
    };

    public boolean isSleep() {
        boolean isSleep = mIsSleep;
        this.mIsSleep = false;
        return isSleep;
    }

    private void setupReader() {
        LinkType linkType = GlobalCfg.get().getLinkType();
        XLog.i(linkType + "");
        switch (linkType) {
            case LINK_TYPE_BLUETOOTH:
                ReaderHelper readerHelper = ReaderHelper.getDefaultHelper();
                readerHelper.setTriggerKeyCallback(triggerKey ->
                                runOnUiThread(() -> {
//                                XLog.i("扣扳机: " + triggerKey.isEnable());
                                            if (mIsRecharge) {

                                                handler.postDelayed(() -> pressKeyEvent(true), 1000);
                                            } else {
                                                pressKeyEvent(true);
                                            }
                                        }
                                )
                );
                readerHelper.setRechargeCallback(rechargeBean ->
                                runOnUiThread(() -> {
//                                XLog.i("gpenghui-zz", "充电: " + rechargeBean.isRecharge());
                                            mIsRecharge = rechargeBean.isRecharge();
                                        }
                                )
                );
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2000);
                break;
            default:
                break;
        }
    }

    private void pressKeyEvent(boolean isPress) {
        mTotalCount = 0;
        mLastSecondCount = 0;

        mMap.clear();
        performInventoryTagAdapter.clear();
        mCfg.clearEpcList();

        if (mCountTag > 0) {
            mCountTag = 0;
            mCanSaveTemp = false;
            clear(mCurrRecvList, mBaseList);
            if (mOverMap != null) {
                mOverMap.clear();
            }
        }
        startStop(isPress);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F4) {
            pressKeyEvent(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F4) {
            pressKeyEvent(false);
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {

            exit();
        }
        return super.onKeyUp(keyCode, event);
    }

    private void exit() {
        handler.removeCallbacksAndMessages(null);
        try {
            unregisterReceiver(mScreenReceiver);
        } catch (Exception ignored) {
        }
        getApplication().onTerminate();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_inventory);


        initView();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (storageHelper.getTagScanMethode().equalsIgnoreCase("RFID")) {
                    if (btstartStop.isSelected()) {
                        startStop(false);
                    }
                }
                onBackPressed();
            }
        });


    }

    @Override
    protected void onStop() {
        if (storageHelper.getTagScanMethode().equalsIgnoreCase("Barcode")) {
            ModuleManager.newInstance().setScanStatus(false);
            ((App) getApplication()).onTerminate();
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        if (storageHelper.getTagScanMethode().equalsIgnoreCase("Barcode")) {
            if (mReaderBase != null) {
                if (!mReaderBase.IsAlive())
                    mReaderBase.StartWait();
            }
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (storageHelper.getTagScanMethode().equalsIgnoreCase("Barcode")) {
            if (mReaderBase != null)
                mReaderBase.signOut();

            if (mSerialPort != null)
                mSerialPort.close();

            mSerialPort = null;
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_perform_inventory;
    }

    private void initView() {
        refreshClear = findViewById(R.id.refreshClear);
        btstartStop = findViewById(R.id.startStop);
        next = findViewById(R.id.next);
        tv_qty = findViewById(R.id.tv_qty);
        tv_unit_count = findViewById(R.id.tv_unit_count);
        rvInventory = findViewById(R.id.rvInventory);
        rvMatchInventory = findViewById(R.id.rvMatchInventory);
        ivMenu = findViewById(R.id.iv_menu);
        vdlMenu = findViewById(R.id.vdl_menu);
        configView = findViewById(R.id.config_view);


        mReader = ReaderHelper.getReader();
        mCfg = GlobalCfg.get();
        mNotBTLink = mCfg.getLinkType() != LinkType.LINK_TYPE_BLUETOOTH;
        mConsumer = new InnerConsumer<>();
        mReader.addOriginalDataReceivedCallback(mConsumer);

// Run database & storage initialization in a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database = new DatabaseHandler(PerformInventoryActivity.this);
            storageHelper = new StorageHelper(PerformInventoryActivity.this);
        });


        setRFIDInventory();
        clickListeners();
        registScreenReceiver();
        clearDataWithUI();
        setMatchedRecyclerView();

// Delay UI operations to prevent blocking onCreate()
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (storageHelper.getTagScanMethode().equalsIgnoreCase("RFID")) {
                toRFIDConnect();
            } else if (storageHelper.getTagScanMethode().equalsIgnoreCase("Barcode")) {
                tobarcodeConnect();
            } else {
                //TODO Both connection RFID / Barcode
            }

        }, 500);

    }

    private void clearDataWithUI() {
        rvInventory.setVisibility(View.GONE);
        rvMatchInventory.setVisibility(View.GONE);
        totalUnitCount = 0;
        totalQty = 0;
        tv_qty.setText(totalQty + "x");
        tv_unit_count.setText("" + totalUnitCount);

    }

    private PerformMatchedInventoryTagAdapter.OnItemClickListener onItemClickListener = this::itemOnClick;

    private void setRFIDInventory() {
        mLayoutManager = new LinearLayoutManager(PerformInventoryActivity.this);
        performInventoryTagAdapter = new PerformInventoryTagAdapter(PerformInventoryActivity.this);
        mData = performInventoryTagAdapter.getData();
        rvInventory.setHasFixedSize(true);
        rvInventory.setLayoutManager(mLayoutManager);
        rvInventory.setAdapter(performInventoryTagAdapter);
        rvInventory.addItemDecoration(new ItemSpacingDecoration(10));
        rvInventory.setItemAnimator(null);

        mInventoryParam = new InventoryParam();
        vdlMenu.setDrawerListener(mDrawerListener);

    }

    private void itemOnClick(PerformMatchedInventoryTagAdapter adapter, View view, int position) {

        AllProduct allProduct = performMatchedInventoryTagAdapter.getData(position);
        Log.e(TAG, performMatchedInventoryTagAdapter.getData().size() + "Size : ");
        Log.e(TAG, "Selected Item " + new Gson().toJson(allProduct));


        if (allProduct != null) {
            Intent intent = new Intent(PerformInventoryActivity.this, TagListActivity.class);
            String allProductJson = new Gson().toJson(allProduct);
            intent.putExtra("ALL_PRODUCT", allProductJson);
            startActivity(intent);
        }
    }

    private void setMatchedRecyclerView() {
        rvMatchInventory.setVisibility(View.VISIBLE);
        matchInventoryLayoutManager = new LinearLayoutManager(PerformInventoryActivity.this);
        performMatchedInventoryTagAdapter = new PerformMatchedInventoryTagAdapter(PerformInventoryActivity.this);

        newAllProduct = performMatchedInventoryTagAdapter.getData();
        rvMatchInventory.setHasFixedSize(true);
        rvMatchInventory.setLayoutManager(matchInventoryLayoutManager);
        rvMatchInventory.setAdapter(performMatchedInventoryTagAdapter);
        rvMatchInventory.addItemDecoration(new ItemSpacingDecoration(10));
        rvMatchInventory.setItemAnimator(null);
        performMatchedInventoryTagAdapter.setOnItemChildClickListener(onItemClickListener);


    }

    private void setMatchedBarcodeInventory(){
        rvMatchInventory.setVisibility(View.VISIBLE);
        newAllProduct = new ArrayList<>();
        unknownMatchCount = 0;

        Set<String> uniqueEPCSet = new HashSet<>();
        Map<String, AllProduct> productMap = new HashMap<>();

        for (TDCodeTagBuffer.BinDCodeTagMap tag : tagListData) {
            String epc = tag.mBarCodeValue.replace(" ","");
            if (!uniqueEPCSet.add(epc)) {
                continue; // Skip duplicate EPC
            }

            String productGuid = database.getProductGuid(epc);
            if (productGuid != null) {
                String productName = database.getProductName(productGuid);

                AllProduct matchedItem = productMap.get(productGuid);
                Items item = new Items(productGuid, epc, 1);
                if (matchedItem == null) {


                    List<Items> itemsList = new ArrayList<>();
                    itemsList.add(item);


                    matchedItem = new AllProduct();
                    matchedItem.setName(productName);
                    matchedItem.setInventoryQuantity(1);
                    matchedItem.setTotalUnitCount(1);
                    matchedItem.setGuid(productGuid);
                    matchedItem.setProductItems(itemsList);

                    productMap.put(productGuid, matchedItem);
                    if (storageHelper.isBeepEveryTag()) {
                        toPlayBeep();
                    }
                } else {
                    matchedItem.setInventoryQuantity(matchedItem.getInventoryQuantity() + 1);
                    matchedItem.setTotalUnitCount(matchedItem.getTotalUnitCount() + 1);
                    matchedItem.getProductItems().add(new Items(productGuid, epc, 1));
                }
            }else {
                unknownMatchCount++;
            }

        }

        newAllProduct.addAll(productMap.values());

        if (!storageHelper.isIgnoreUnknownTag() && unknownMatchCount > 0) {
            AllProduct unknownItem = new AllProduct();
            unknownItem.setName("Unknown");
            unknownItem.setGuid(null);

            // Use HashSet to store unique unknown EPCs
            Set<String> uniqueUnknownEPCs = new HashSet<>();
            List<Items> unknownItemsList = new ArrayList<>();

            for (TDCodeTagBuffer.BinDCodeTagMap tag : tagListData) {
                String epc = tag.mBarCodeValue.replace(" ","");
                if (database.getProductGuid(epc) == null && uniqueUnknownEPCs.add(epc)) {
                    unknownItemsList.add(new Items(null, epc, 1));
                }
            }

            // Set the unique unknown items
            unknownItem.setProductItems(unknownItemsList);
            unknownItem.setInventoryQuantity(unknownItemsList.size());
            unknownItem.setTotalUnitCount(unknownItemsList.size());

            newAllProduct.add(unknownItem);
        }


        totalUnitCount = 0;
        totalQty = 0;

        for (AllProduct data : newAllProduct) {
            if (!"Unknown".equals(data.getName())) {
                totalUnitCount += data.getTotalUnitCount();
                totalQty += data.getInventoryQuantity();
            }
        }

        tv_qty.setText(totalQty + "x");
        tv_unit_count.setText(String.valueOf(totalUnitCount));

        performMatchedInventoryTagAdapter.setData(newAllProduct);
        performMatchedInventoryTagAdapter.notifyDataSetChanged();


        Log.e(TAG, "Bardcode Data List: " + new Gson().toJson(newAllProduct));
    }


    private void setMatchedInventory() {
        rvMatchInventory.setVisibility(View.VISIBLE);
        newAllProduct = new ArrayList<>();
        unknownMatchCount = 0;
        Log.e(TAG, "RFID List : " + new Gson().toJson(mData));

        Set<String> uniqueEPCSet = new HashSet<>();
        Map<String, AllProduct> productMap = new HashMap<>();

        for (InventoryTagBean inventoryTagBean : mData) {
            String epc = inventoryTagBean.getEpc().replace(" ", "");
            if (!uniqueEPCSet.add(epc)) {
                continue; // Skip duplicate EPC
            }

            String productGuid = database.getProductGuid(epc);

            if (productGuid != null) {
                String productName = database.getProductName(productGuid);

                AllProduct matchedItem = productMap.get(productGuid);
                Items item = new Items(productGuid, epc, 1);
                if (matchedItem == null) {


                    List<Items> itemsList = new ArrayList<>();
                    itemsList.add(item);


                    matchedItem = new AllProduct();
                    matchedItem.setName(productName);
                    matchedItem.setInventoryQuantity(1);
                    matchedItem.setTotalUnitCount(1);
                    matchedItem.setGuid(productGuid);
                    matchedItem.setProductItems(itemsList);

                    productMap.put(productGuid, matchedItem);
                    if (storageHelper.isBeepEveryTag()) {
                        toPlayBeep();
                    }
                } else {
                    matchedItem.setInventoryQuantity(matchedItem.getInventoryQuantity() + 1);
                    matchedItem.setTotalUnitCount(matchedItem.getTotalUnitCount() + 1);
                    matchedItem.getProductItems().add(new Items(productGuid, epc, 1));
                }
            } else {
                unknownMatchCount++;
            }
        }

        newAllProduct.addAll(productMap.values());

        if (!storageHelper.isIgnoreUnknownTag() && unknownMatchCount > 0) {
            AllProduct unknownItem = new AllProduct();
            unknownItem.setName("Unknown");
            unknownItem.setGuid(null);

            // Use HashSet to store unique unknown EPCs
            Set<String> uniqueUnknownEPCs = new HashSet<>();
            List<Items> unknownItemsList = new ArrayList<>();

            for (InventoryTagBean inventoryTagBean : mData) {
                String epc = inventoryTagBean.getEpc().replace(" ", "");
                if (database.getProductGuid(epc) == null && uniqueUnknownEPCs.add(epc)) {
                    unknownItemsList.add(new Items(null, epc, 1));
                }
            }

            // Set the unique unknown items
            unknownItem.setProductItems(unknownItemsList);
            unknownItem.setInventoryQuantity(unknownItemsList.size());
            unknownItem.setTotalUnitCount(unknownItemsList.size());

            newAllProduct.add(unknownItem);
        }


        totalUnitCount = 0;
        totalQty = 0;

        for (AllProduct data : newAllProduct) {
            if (!"Unknown".equals(data.getName())) {
                totalUnitCount += data.getTotalUnitCount();
                totalQty += data.getInventoryQuantity();
            }
        }

        tv_qty.setText(totalQty + "x");
        tv_unit_count.setText(String.valueOf(totalUnitCount));

        performMatchedInventoryTagAdapter.setData(newAllProduct);
        performMatchedInventoryTagAdapter.notifyDataSetChanged();


        Log.e(TAG, "New Data List: " + new Gson().toJson(newAllProduct));
    }


    private void toPlayBeep() {
        int soundFileType = BeeperHelper.SOUND_FILE_TYPE_SHORT;
        BeeperHelper.beeper(soundFileType);
    }


    /*private void refreshAdapter() {
        rvInventory.setVisibility(View.GONE);
        rvlocalInventory.setVisibility(View.VISIBLE);


        mLayoutManager2 = new LinearLayoutManager(PerformInventoryActivity.this);
        performLocalInventoryTagAdapter = new PerformLocalInventoryTagAdapter(PerformInventoryActivity.this);
        allProducts = performLocalInventoryTagAdapter.getData();

        if (allProducts == null) {
            allProducts = new ArrayList<>();
        }

        List<AllProduct> newItems = database.getAllProductsWithTotalUnitCount();

        for (AllProduct item : newItems) {
            if (item.getInventoryQuantity() > 0) {
                allProducts.add(item);
                toBeepOnRecv();
            } else {
                unknownCount++;
            }

        }


        if (unknownCount > 0) {
            AllProduct item = new AllProduct();
            item.setName("Unknown");
            item.setInventoryQuantity(unknownCount);
            item.setTotalUnitCount(unknownCount);
            item.setGuid(null);
            allProducts.add(item);
        }

        Log.e(TAG, "" + allProducts.size());
        Log.e("productItemData :", new Gson().toJson(allProducts));

        // rvlocalInventory
        rvlocalInventory.setHasFixedSize(true);
        rvlocalInventory.setLayoutManager(mLayoutManager2);
        rvlocalInventory.setAdapter(performLocalInventoryTagAdapter);
        rvlocalInventory.setItemAnimator(null);

        totalQty = 0;
        totalUnitCount = 0;
        totalQty = database.getTotalInventoryQuantity();
        totalUnitCount = database.getTotalUnitCount();
        tv_qty.setText(totalQty + "x");
        tv_unit_count.setText("" + totalUnitCount);

        performLocalInventoryTagAdapter.notifyDataSetChanged();
    }*/

   /* private void setLocalInventory() {
        rvInventory.setVisibility(View.GONE);
        rvlocalInventory.setVisibility(View.VISIBLE);


        mLayoutManager2 = new LinearLayoutManager(PerformInventoryActivity.this);
        performLocalInventoryTagAdapter = new PerformLocalInventoryTagAdapter(PerformInventoryActivity.this);
        allProducts = performLocalInventoryTagAdapter.getData();

        if (allProducts == null) {
            allProducts = new ArrayList<>();
        }

        List<AllProduct> newItems = database.getAllProductsWithTotalUnitCount();

        for (AllProduct item : newItems) {
            if (item.getInventoryQuantity() > 0) {
                allProducts.add(item);
                toBeepOnRecv();
            } else {
                unknownCount++;
            }

        }


        if (unknownCount > 0) {
            AllProduct item = new AllProduct();
            item.setName("Unknown");
            item.setInventoryQuantity(unknownCount);
            item.setTotalUnitCount(unknownCount);
            item.setGuid(null);
            allProducts.add(item);
        }

        Log.e(TAG, "" + allProducts.size());
        Log.e("productItemData :", new Gson().toJson(allProducts));

        // rvlocalInventory
        rvlocalInventory.setHasFixedSize(true);
        rvlocalInventory.setLayoutManager(mLayoutManager2);
        rvlocalInventory.setAdapter(performLocalInventoryTagAdapter);
        rvlocalInventory.addItemDecoration(new ItemSpacingDecoration(10));
        rvlocalInventory.setItemAnimator(null);

        totalQty = 0;
        totalUnitCount = 0;
        totalQty = database.getTotalInventoryQuantity();
        totalUnitCount = database.getTotalUnitCount();
        tv_qty.setText(totalQty + "x");
        tv_unit_count.setText("" + totalUnitCount);

        performLocalInventoryTagAdapter.notifyDataSetChanged();
    }*/

    private void toBeepOnRecv() {
        if (mNotBTLink) {
            int soundFileType = BeeperHelper.SOUND_FILE_TYPE_SHORT;
            if (mBeepInterval > 0) {
                long currentTime = System.currentTimeMillis();
                long ms = currentTime - mLastPerBeepTime;
                if (ms > mBeepInterval) {
                    mLastPerBeepTime = currentTime;
                    BeeperHelper.beeper(soundFileType);
                }
            } else {
                BeeperHelper.beeper(soundFileType);
            }
        }
    }


    private void getFirmwareVersion() {
        mReader.getFirmwareVersion(new Consumer<Version>() {
            @Override
            public void accept(Version version) throws Exception {

                runOnUiThread(() -> {
                    Log.e(TAG, "Success : " + version);
                    ReaderHelper.getReader().removeCallback(Cmd.GET_FIRMWARE_VERSION);
                    GlobalCfg.get().setVersion(version);
                });
                setRFOutputPower();
            }
        }, new Consumer<Failure>() {
            @Override
            public void accept(Failure failure) throws Exception {
                runOnUiThread(() -> {
                    Log.e(TAG, "Fail : " + failure.toString());
                });
            }
        });

    }

    private void tobarcodeConnect() {
        showConnectionDialog();
        try {
            if (ModuleManager.newInstance().getUHFStatus()) {
                ModuleManager.newInstance().setUHFStatus(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PerformInventoryActivity.this, "Please exit UHFDemo first!", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS1"), 9600, 0);

            if (!ModuleManager.newInstance().setScanStatus(true)) {
                throw new RuntimeException("Scan power on failure,may you open in other" +
                        "Process and do not exit it");
            }


            try {
                mReaderHelper = ReaderHelper.getDefaultHelper();
                mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
                mReaderBase = mReaderHelper.getReaderBase();

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().sleep(3000);
                        ScannerSetting.newInstance().defaultSettings();
                        Thread.currentThread().sleep(1000);
                        hideConnectionDialog();
                        showSnackBar("Barcode Scanner connected");
                        m_curOperateBinDCodeTagbuffer = mReaderHelper.getCurOperateTagBinDCodeBuffer();
                        tagListData = new ArrayList<TDCodeTagBuffer.BinDCodeTagMap>();
                        lbm = LocalBroadcastManager.getInstance(PerformInventoryActivity.this);
                        IntentFilter itent = new IntentFilter();
                        itent.addAction(ReaderHelper.BROADCAST_REFRESH_BAR_CODE);
                        lbm.registerReceiver(mRecv, itent);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
            }).start();


        } catch (SecurityException e) {
            Toast.makeText(
                    PerformInventoryActivity.this,
                    "This Port has no read and write permission.",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(
                    PerformInventoryActivity.this,
                    "Open Fail,Unknown Error",
                    Toast.LENGTH_SHORT).show();
        } catch (InvalidParameterException e) {
            Toast.makeText(
                    PerformInventoryActivity.this,
                    "Please configurate first.",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("where the exception!", "is here" + e.getMessage());
            /*catch exception test */
        }


    }

    private void toRFIDConnect() {
        if (!PowerUtils.powerOn()) {
            showToast(R.string.power_on_failed);
            return;
        }
        showConnectionDialog();
        com.naz.serial.port.ModuleManager.newInstance().setUHFStatus(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        com.naz.serial.port.ModuleManager.newInstance().setUHFStatus(false);
        handle = new SerialPortHandle("/dev/ttyS4", 115200);

        boolean success = mReader.connect(handle);
        String text = success ? "Connection SuccessFully" : "Connection Failed";

        if (success) {

            if (!com.naz.serial.port.ModuleManager.newInstance().setUHFStatus(true)) {

                Log.e(TAG, "UHF Status false");
            } else {
                Log.e(TAG, "UHF Status true");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFirmwareVersion();
                    }
                }, 5000);
            }
        } else {
            Log.e(TAG, "Connection Failed");
        }
    }


    private void setRFOutputPower() {

        readerPowerAdapter = new ReaderPowerAdapter(new ArrayList<>());
        mAntennaCount = ReaderHelper.getReader().getAntennaCount();
        int value = 36;
        for (int i = 0; i < value; i++) {
            readerPowerAdapter.addData(new SetPowerBean());
        }

        hideConnectionDialog();
        showSnackBar("Scanner connected");

        setupReader();


        List<SetPowerBean> data = readerPowerAdapter.getData();
        XLog.i("=======start=======: ");
        for (SetPowerBean datum : data) {
            XLog.i("value: " + datum.toString());
        }

        byte[] powers = new byte[mAntennaCount.getValue()];
        for (int i = 0; i < data.size(); i++) {
            SetPowerBean bean = data.get(i);
            if (!bean.isValid()) {
                //showToast(R.string.input_power);
                return;
            }
            powers[i] = (byte) bean.getPower();
        }

        OutputPowerConfig config;
        if (mAntennaCount == AntennaCount.FOUR_CHANNELS) {
            config = OutputPowerConfig.outputPower(new PowerFourAntenna.Builder().powers(powers).build());
        } else if (mAntennaCount == AntennaCount.EIGHT_CHANNELS) {
            config = OutputPowerConfig.outputPower(new PowerEightAntenna.Builder().powers(powers).build());
        } else if (mAntennaCount == AntennaCount.SIXTEEN_CHANNELS) {
            config = OutputPowerConfig.outputPower(new PowerSixteenAntenna.Builder().powers(powers).build());
        } else {
            config = OutputPowerConfig.outputPower(new PowerSingleAntenna.Builder().power(powers[0]).build());
        }
        ReaderHelper.getReader().setOutputPower(config, success -> {
        }, failure -> {
        });
       /* byte[] powers = new byte[21];
        OutputPowerConfig config = OutputPowerConfig.outputPower(new PowerSingleAntenna.Builder().power(powers[0]).build());
        mReader.setOutputPower(config, new Consumer<Success>() {
            @Override
            public void accept(Success success) throws Exception {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("RFPower Output Set up SuccessFully");
                    }
                });


            }
        }, new Consumer<Failure>() {
            @Override
            public void accept(Failure failure) throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("RFPower Output Setup failed, please try again");
                    }
                });
            }
        });*/

    }

    private void registScreenReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver, filter);
    }

    private void refreshBarcodeTagList() {
        //tagListData.addAll(m_curOperateBinDCodeTagbuffer.getIsTagList());
        tagListData.addAll(mReaderHelper.getCurOperateTagBinDCodeBuffer().getIsTagList());
        showSnackBar("Tag List :  : " + new Gson().toJson(tagListData));
        setMatchedBarcodeInventory();
    }


    private void clickListeners() {
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vdlMenu.isDrawerOpen()) {
                    vdlMenu.closeDrawer();
                } else {
                    startStop(false);/*打开配置页*/
                    vdlMenu.openDrawerView();
                    configView.initFastTid(mInventoryParam);
                }
            }
        });

        btstartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storageHelper.getTagScanMethode().equalsIgnoreCase("RFID")) {
                    if (mCountTag > 0) {
                        mCountTag = 0;
                        mCanSaveTemp = false;
                        clear(mCurrRecvList, mBaseList);
                        if (mOverMap != null) {
                            mOverMap.clear();
                        }
                    }

                    if (startStop(!btstartStop.isSelected())) {
                        setDuration();
                    }
                } else if (storageHelper.getTagScanMethode().equalsIgnoreCase("Barcode")) {
                    m_curOperateBinDCodeTagbuffer.getmRawData().clear();
                    m_curOperateBinDCodeTagbuffer.clearBuffer();
                    tagListData.clear();
                    lbm.sendBroadcast(new Intent(ReaderHelper.BROADCAST_REFRESH_BAR_CODE));
                    tagListData.addAll(m_curOperateBinDCodeTagbuffer.getIsTagList());
                    mReaderBase.sendMessage(PerformInventoryActivity.this, Command.DEFAULTE_FACTORY.getBytes());




                } else {

                }

            }
        });

        refreshClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearDataWithUI();

               /* mTotalCount = 0;
                mLastSecondCount = 0;

                mMap.clear();
                // mAdapter.clear();
                performInventoryTagAdapter.clear();
                mCfg.clearEpcList();

                if (mCountTag > 0) {
                    mCountTag = 0;
                    mCanSaveTemp = false;
                    clear(mCurrRecvList, mBaseList);
                    if (mOverMap != null) {
                        mOverMap.clear();
                    }
                }

                if (startStop(!refreshClear.isSelected())) {
                    setDuration();
                }*/
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<AllProduct> filteredProducts = new ArrayList<>();
                if (newAllProduct != null) {
                    if (newAllProduct.size() > 0) {
                        for (AllProduct product : newAllProduct) {
                            if (!"Unknown".equals(product.getName())) {
                                filteredProducts.add(product);
                            }
                        }

                        Gson gson = new Gson();
                        String productItemJson = gson.toJson(filteredProducts);
                        Log.e(TAG, "" + productItemJson);
                        Intent intent = new Intent(PerformInventoryActivity.this, SelectResultActivity.class);
                        intent.putExtra("ALL_PRODUCT", productItemJson);
                        intent.putExtra("LOCATION_GUID", getIntent().getStringExtra("LOCATION_GUID"));
                        intent.putExtra("LOCATION_NAME", getIntent().getStringExtra("LOCATION_NAME"));
                        startActivity(intent);


                    } else {
                        showSnackBar("Please scan inventory result!");
                    }
                } else {
                    showSnackBar("Please scan inventory result!");
                }

                /*if (newAllProduct != null) {
                    if (newAllProduct.size() > 0) {
                        for (AllProduct product : newAllProduct) {
                            if (!"Unknown".equals(product.getName())) {
                                filteredProducts.add(product);
                            }
                        }


                    }
                } else {
                    if (allProducts.size() > 0) {
                        for (AllProduct product : allProducts) {
                            if (!"Unknown".equals(product.getName())) {
                                filteredProducts.add(product);
                            }
                        }
                    }
                }*/


            }
        });

    }


    private void setDuration() {
        long duration;
        try {
            duration = Long.parseLong("", 10);
        } catch (Exception e) {
            duration = 0;
        }
        if (duration > 0) {
            Message msg = Message.obtain();
            msg.what = mHandler.MSG_STOP;
            msg.obj = mStartRunTime;
            mHandler.sendMessageDelayed(msg, duration * 1000);
        }
    }


    private static class InnerConsumer<T> implements Consumer<byte[]> {
        int count;

        @Override
        public void accept(byte[] bytes) throws Exception {
        }

        @Override
        public void onUnknownArr(byte[] bytes) throws Exception {
            XLog.w((++count) + ".onUnknownArr: " + ArrayUtils.bytesToHexString(bytes, 0, bytes.length));
            // handler.sendEmptyMessage(MSG_STOP);/*收到未知指令*/
        }
    }

    private static class InnerHandler extends Handler {
        private final int MSG_RESUME = 0;
        private final int MSG_STOP = 1;
        private final int MSG_ON_RECV = 2;
        private final int MSG_ON_END = 3;

        private WeakReference<PerformInventoryActivity> mWr;

        public InnerHandler(PerformInventoryActivity fragment) {
            mWr = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            PerformInventoryActivity f = mWr.get();
            if (f == null) {
                return;
            }

            switch (msg.what) {
                case MSG_RESUME:
                    f.doNextInventory(true);
                    break;
                case MSG_STOP: {
                    if (f.mStartRunTime == (Long) msg.obj) {
                        f.startStop(false);/*定时停止*/
                    }
                }
                break;
                case MSG_ON_RECV:
                    f.onRecv(((InventoryTag) msg.obj));
                    break;
                case MSG_ON_END: {
                    f.onEnd(((InventoryTagEnd) msg.obj));
                }
                break;
            }
        }

    }

    private void onRecv(InventoryTag inventoryTag) {
        mTotalCount++;


        String tagEpc = inventoryTag.getEpc();
        if (mCanSaveTemp) {
            if (mBaseList.contains(tagEpc)) {
                if (!mCurrRecvList.contains(tagEpc)) mCurrRecvList.add(tagEpc);
            } else {
                if (mOverMap.containsKey(tagEpc)) {
                    int v1 = mOverMap.get(tagEpc) + 1;
                    mOverMap.put(tagEpc, v1);
                } else {
                    mOverMap.put(tagEpc, 1);
                }
            }
        }

        InventoryTagBean mapBean = mMap.get(tagEpc);
        if (mapBean != null) {
            mapBean.addTimes();
            int savePosition = mapBean.getPosition();
            //  int position = mAdapter.getItemCount() - 1 - savePosition;
            int pos = performInventoryTagAdapter.getItemCount() - 1 - savePosition;
            //  mAdapter.notifyItemChanged(position);
            performInventoryTagAdapter.notifyItemChanged(pos);
        } else {
            // int size = mAdapter.getItemCount();
            int sizee = performInventoryTagAdapter.getItemCount();
            // InventoryTagBean bean = new InventoryTagBean(inventoryTag, size);
            InventoryTagBean bean1 = new InventoryTagBean(inventoryTag, sizee);

            // mData.add(0, bean);
            mData.add(0, bean1);
            //  mAdapter.notifyDataSetChanged();
            performInventoryTagAdapter.notifyDataSetChanged();

            mMap.put(tagEpc, bean1);
            mCfg.addEpc(tagEpc);
        }

        setMatchedInventory();
        Log.e(TAG, "Receive Data:" + new Gson().toJson(mData));

        refreshUi();
    }

    public List<InventoryTagBean> getTags() {
        return mData;
    }

    public boolean isPhase() {
        return performInventoryTagAdapter.isPhase();
    }

    public boolean startStop(boolean startStop) {
        if (startStop) {
            if (btstartStop.isSelected()) {
                XLog.i("Ignored.running...");
                return false;
            }
            if (mInventoryParam.getInventory() == null) {
                showToast(R.string.inventory_param_error);
                return false;
            }
            if (!mFastSwitch && !mInventoryParam.hasCheckedAnts()) {
                showToast(R.string.select_least_antenna);
                return false;
            }

            btstartStop.setSelected(true);
            btstartStop.setText(R.string.stop);
            btstartStop.setKeepScreenOn(true);


            mRecvCount = 0;

            try {
                mDelayMs = Integer.parseInt(configView.mBinding.etDelay.getText().toString().trim(), 10);
            } catch (Exception e) {
                mDelayMs = 0;
            }

            mSetPower = (byte) ((int) Hawk.get(Key.KEY_POWER, 28));
            mBeepInterval = Hawk.get(Key.MINI_NUM_BEEP_TIME, 0);
            mSaveInventoryLog = Hawk.get(Key.SAVE_INVENTORY_LOG, false);


            if (mSaveInventoryLog) {
                mSaveId = 1;
                mCmdStartTime = System.currentTimeMillis();
                if (mLogSavePath == null) {
                    mLogSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "InventoryTesting-log.txt";
                }
                byte repeat = InventoryConfigView.getByte(configView.mBinding.etRepeat, (byte) 1);

                ThreadPool.get().execute(() -> {
                    String title = mFastSwitch ? "------快速切换多天线盘存" : "------自定义Session盘存";

                    if (mFreezerMode) {
                        title += ".冰柜";
                    }
                    title += "." + mDelayMs + " ms.repeat:" + repeat + ".loop:" + mLoopCount + "\n";
                    saveInventoryLog(title);
                });
            }


            setInventoryConfig(mFastMode);

            mStartRunTime = System.currentTimeMillis();
            doNextInventory(false);
        } else {
            stopInventory();/*startStop（false）*/
        }
        return true;
    }

    private void stopInventory() {
        if (!btstartStop.isSelected()) {
            XLog.i("忽略.未运行");
            return;
        }
        mConsumer.count = 0;
        mHandler.removeMessages(mHandler.MSG_RESUME);
        mHandler.removeMessages(mHandler.MSG_STOP);

        refreshUi();
        //mAdapter.notifyDataSetChanged();
        performInventoryTagAdapter.notifyDataSetChanged();


        btstartStop.setSelected(false);
        btstartStop.setText(R.string.start);
        btstartStop.setKeepScreenOn(false);
        //setMatchedInventory();


        boolean isFastMode = configView.mBinding.cbFastMode.isChecked();
        XLog.i("stopInventory.isFastMode:" + isFastMode);
        mReader.stopInventory(isFastMode);

    }

    private void refreshUi() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
    }

    private void doNextInventory(boolean nextAnt) {
        if (mFreezerMode) {
            XLog.i("冰柜.清空界面");
        }
        if (!btstartStop.isSelected()) {
            XLog.i("已手动停止!!!");
            return;
        }

        mCmdStartTime = System.currentTimeMillis();

        if (mFastSwitch) {
            mReader.startInventory(true);
        } else {
            mAntennaId = mInventoryParam.getAntennaId(nextAnt);
            setInventoryAnt();
        }
    }

    private void setInventoryAnt() {
        int cacheWorkAntenna = mReader.getCacheWorkAntenna();
        if (mAntennaId == cacheWorkAntenna) {
            mReader.startInventory(mFastSwitch);
        } else {
            XLog.i("------setWorkAntenna：" + mAntennaId);
            mReader.setWorkAntenna(mAntennaId,
                    success -> mReader.startInventory(mFastSwitch),
                    failure -> setInventoryAnt());
        }
    }

    private void setInventoryConfig(boolean isFast) {
        InventoryConfig config = new InventoryConfig.Builder()
                .setInventory(mInventoryParam.getInventory())
                .setOnInventoryTagSuccess(inventoryTag -> {

                    Message msg = Message.obtain();
                    msg.what = mHandler.MSG_ON_RECV;
                    msg.obj = inventoryTag;
                    mHandler.sendMessage(msg);
                }).setOnInventoryTagEndSuccess(inventoryTagEnd -> {

                    Message msg = Message.obtain();
                    msg.what = mHandler.MSG_ON_END;
                    msg.obj = inventoryTagEnd;
                    mHandler.sendMessage(msg);
                }).setOnFailure(failure -> {
                    int antId = failure.getAntId();
                    byte cmd = failure.getCmd();
                    byte errorCode = failure.getErrorCode();

                    if (errorCode == ResultCode.REQUEST_INVALID) {
                        XLog.w("指令队列已满，暂不接收新指令");
                        return;
                    }
                    String cmdStr = Cmd.getNameForCmd(cmd);
                    if (errorCode == ResultCode.ANTENNA_MISSING_ERROR) {/*添加的指令数量超过最大值，忽略*/
                        if (mFastSwitch) {
                            XLog.w("天线（" + antId + "）丢失: " + cmdStr);
                            return;
                        }
                    }
                    if (XLog.sShowLog) {
                        String resultCodeStr = ResultCode.getNameForResultCode(errorCode);
                        XLog.w("天线（" + antId + "） OnFailure: " + cmdStr + "-->" + resultCodeStr);
                    }

                    if (mLoopCount == -1 || mRecvCount < mLoopCount) {
                        XLog.w("OnInventoryFailure,继续盘存");
                        Message msg = Message.obtain();
                        msg.what = mHandler.MSG_RESUME; /*OnInventoryFailure*/
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = mHandler.MSG_STOP;
                        msg.obj = mStartRunTime;
                        mHandler.sendMessage(msg);
                    }
                })
                .setFastInventory(isFast)/*目前只有E710支持这个方法传true,且需要在最后调用*/
                .build();
        mReader.setInventoryConfig(config);
    }

    private void onEnd(InventoryTagEnd inventoryTagEnd) {

        int readRate = inventoryTagEnd.getReadRate();
        if (readRate > 0) {
        }


        boolean isAnt16 = mReader.getAntennaCount() == AntennaCount.SIXTEEN_CHANNELS;
        int antennaGroup = mReader.getCacheAntennaGroup();
        if (mFastSwitch && isAnt16) {
            if (antennaGroup == 1) {
                mRecvCount++;
            }
        } else if (mInventoryParam.isLastAnt()) {
            mRecvCount++;
        }

        if (XLog.sShowLog || mSaveInventoryLog) {
            endLog(inventoryTagEnd);
            XLog.i("RecvCount/ExeCount=" + mRecvCount + "/" + mLoopCount);
        }

        if (mLoopCount > 0 && mRecvCount >= mLoopCount) {
            Message msg = Message.obtain();
            msg.what = mHandler.MSG_STOP;
            msg.obj = mStartRunTime;
            mHandler.sendMessage(msg);
            return;
        }
        if (!btstartStop.isSelected()) {
            XLog.i("已手动停止!");
            return;
        }
        if (mDelayMs <= 0
                || (mFastSwitch && isAnt16 && antennaGroup == 0)
                || (!mFastSwitch && !mInventoryParam.isLastAnt())) {
            doNextInventory(true);
        } else {
            XLog.i("下一轮延迟->" + mDelayMs + " ms");
            Message msg = Message.obtain();
            msg.what = mHandler.MSG_RESUME;
            mHandler.sendMessageDelayed(msg, mDelayMs);
        }

        Log.e(TAG, "Inventory Tag Finished!");
    }

    private void endLog(InventoryTagEnd inventoryTagEnd) {
        String antMsg;
        if (mFastSwitch) {
            antMsg = "] 工作天线组:[" + mReader.getCacheAntennaGroup();
        } else {
            antMsg = "] 工作天线:[" + mReader.getCacheWorkAntenna();
        }
        int size = mData.size();
        StringBuilder msgSb = new StringBuilder()
                .append("时间:[").append(mSdf.format(new Date())).append("]")
                .append("ID:[").append(mSaveId++).append(antMsg).append("]")
                .append("本轮读取标签:[").append(inventoryTagEnd.getTotalRead()).append("]")
                .append("本轮识别速率:[").append(inventoryTagEnd.getReadRate()).append("]")
                .append("本轮指令耗时:[").append(System.currentTimeMillis() - mCmdStartTime).append("ms]")
                .append("去重总数:[").append(size).append("]").append("\n");

        if (mFreezerMode && mCountTag > 0) {
            addFreezerLog(size, msgSb);
        }

        String msg = msgSb.toString();
        XLog.i(msg);

        if (mSaveInventoryLog) {
            saveInventoryLog(msg);
        }
    }

    private void addFreezerLog(int size, StringBuilder msgSb) {
        int baseSize = mBaseList.size();
        if (baseSize == 0) {
            if (mCountTag == size) {
                msgSb.append("BaseList：\n");
                for (int i = 0; i < mCountTag; i++) {
                    InventoryTagBean bean = mData.get(i);
                    String epc = bean.getEpc();
                    msgSb.append(i + 1).append("、").append(epc).append("\n");
                    mBaseList.add(epc);
                }
                mCanSaveTemp = true;
            }
        } else {
            int overSize = mOverMap.size();
            if (overSize > 0) {
                msgSb.append("Invalid：\n");
                Set<Map.Entry<String, Integer>> entries = mOverMap.entrySet();
                int i = 1;
                for (Map.Entry<String, Integer> entry : entries) {
                    msgSb.append(i++).append("、").append(entry).append("\n");
                }
                mOverMap.clear();
            }
            int validCount = 0;
            int recvSize = mCurrRecvList.size();
            if (recvSize > 0) {
                boolean addTitle = true;
                for (String epc : mBaseList) {
                    if (mCurrRecvList.contains(epc)) {
                        validCount++;
                    } else {
                        if (addTitle) {
                            addTitle = false;
                            msgSb.append("Lost：\n");
                        }
                        msgSb.append(epc).append("\n");
                    }
                }
                mCurrRecvList.clear();
            }
            if (validCount == 0) {
                msgSb.append("All lost!\n");
            }
        }
    }

    private void clear(List<?>... lists) {
        if (lists != null) {
            for (List<?> list : lists) {
                if (list != null) {
                    list.clear();
                }
            }
        }
    }

    protected void showToast(@StringRes int strResId) {
        runOnUiThread(() -> Toast.makeText(PerformInventoryActivity.this, getString(strResId), Toast.LENGTH_SHORT).show());
    }

    protected void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(PerformInventoryActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    private void saveInventoryLog(String strLog) {
        OutputStream os = null;
        try {
            File file = new File(mLogSavePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file, true);
            os.write(strLog.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void showConnectionDialog() {
        try {

            loaderDialog = new LoaderDialog(PerformInventoryActivity.this);
            loaderDialog.tv_title.setText("Connection in progress...");
            loaderDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideConnectionDialog() {
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





