package com.custodyrx.library.label;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

import com.custodyrx.library.label.bean.type.Key;
import com.custodyrx.library.label.bean.type.LinkType;
import com.custodyrx.library.label.model.BeeperHelper;
import com.custodyrx.library.label.model.ReaderHelper;
import com.custodyrx.library.label.model.RxBleHelper;
import com.custodyrx.library.label.util.ActivityUtils;
import com.custodyrx.library.label.util.PowerUtils;
import com.custodyrx.library.label.util.XLog;
import com.naz.serial.port.ModuleManager;
import com.orhanobut.hawk.Hawk;

/**
 * @author naz
 * Date 2019/11/20
 */
public class App extends MultiDexApplication {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        long l = System.currentTimeMillis();
        mContext = getApplicationContext();
        Hawk.init(this).build();

        boolean showLog;
        if (BuildConfig.DEBUG) {
            showLog = true;
            Hawk.put(Key.ENABLE_OPERATE_LOG, true);
        } else {
            showLog = Hawk.get(Key.ENABLE_OPERATE_LOG, false);
        }
        boolean saveLog = Hawk.get(Key.SAVE_INVENTORY_LOG, false);
        XLog.init(this, showLog, saveLog);

        BeeperHelper.init(this);
        ErrorReport.init();
//        XLog.i("hs.app1." + (System.currentTimeMillis() - l));
        GlobalCfg.get().init();
//        XLog.i("hs.app2." + (System.currentTimeMillis() - l));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //close the module
        PowerUtils.powerOff();
        ReaderHelper.getReader().disconnect();
        RxBleHelper.getInstance().release();
        ActivityUtils.finishAll();
        BeeperHelper.release();
        if (GlobalCfg.get().getLinkType() == LinkType.LINK_TYPE_SERIAL_PORT) {
            ModuleManager.newInstance().release();
        }
        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}