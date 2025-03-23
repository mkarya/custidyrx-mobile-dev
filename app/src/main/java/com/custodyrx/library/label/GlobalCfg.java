package com.custodyrx.library.label;

import android.app.Activity;


import com.custodyrx.library.label.bean.MonitorDataBean;
import com.custodyrx.library.label.bean.type.Key;
import com.custodyrx.library.label.bean.type.LinkType;
import com.custodyrx.library.label.model.BeeperHelper;
import com.custodyrx.library.label.model.ReaderHelper;
import com.custodyrx.library.label.util.ActivityUtils;
import com.custodyrx.library.label.util.XLog;
import com.orhanobut.hawk.Hawk;
import com.payne.reader.Reader;
import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.Beeper;
import com.payne.reader.bean.receive.Success;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.util.ArrayUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 描述：
 * <p>
 * <p>
 * Create on 2023-03-21
 */
public class GlobalCfg {
    private static final GlobalCfg sI = new GlobalCfg();
    /**
     * 默认为 33
     * E710开放 36
     */
    private int mMaxOutPower = 33;
    /**
     * 设备连接类型
     */
    private LinkType mLinkType;
    /**
     * 版本信息
     */
    private volatile Version mVersion;

    /**
     * 是否开启了串口监控
     */
    private volatile boolean mIsEnableMonitor;
    /**
     * Epc集合
     */
    private final HashSet<String> mEpcSet = new HashSet<>();
    /**
     * 监控数据, 最多保存100条
     */
    private final LinkedList<MonitorDataBean> mMonitorDataBeanList = new LinkedList<>();

    private GlobalCfg() {
    }

    public static GlobalCfg get() {
        return sI;
    }

    public void init() {
        //默认未开启串口监控
        mIsEnableMonitor = Hawk.get(Key.ENABLE_MONITOR, false);

        Reader reader = ReaderHelper.getReader();
        reader.setOriginalDataCallback(bytes -> {
            String str = ArrayUtils.bytesToHexString(bytes, 0, bytes.length);
            XLog.i("--发送: " + str);

            if (mIsEnableMonitor) {
                MonitorDataBean bean = new MonitorDataBean(str, true);
                addMonitorData(bean);
            }
        }, bytes -> {
            String str = ArrayUtils.bytesToHexString(bytes, 0, bytes.length);
            XLog.i("--接收: " + str);

            if (mIsEnableMonitor) {
                MonitorDataBean bean = new MonitorDataBean(str, false);
                addMonitorData(bean);
            }
        });

        //<editor-fold desc="Beeper">
        if (BeeperHelper.sNeedSetBeeperType) {
            Consumer<Success> successConsumer = success -> BeeperHelper.setBeeperType(BeeperHelper.BEEPER_TYPE_ONCE_END);
            reader.setBeeperMode(Beeper.ONCE_END_BEEP, successConsumer, failure -> {
                reader.setBeeperMode(Beeper.ONCE_END_BEEP, successConsumer, null);
            });
        }
        //</editor-fold>

    }

    public void setVersion(Version version) {
        if (version == null) {
            return;
        }
        mVersion = version;
        Version.ChipType chipType = version.getChipType();
        switch (chipType) {
            case E710: {
                mMaxOutPower = 36;
            }
            break;
        }
    }

    public Version getVersion() {
        return mVersion;
    }

    public int getMaxOutPower() {
        return mMaxOutPower;
    }

    /**
     * 获取设备连接类型
     *
     * @return {@link LinkType}
     */
    public LinkType getLinkType() {
        return mLinkType;
    }

    /**
     * 设置设备连接类型
     *
     * @param linkType {@link LinkType}
     */
    public void setLinkType(LinkType linkType) {
        mLinkType = linkType;
    }

    public String[] getEpcList() {
        return mEpcSet.toArray(new String[0]);
    }

    public void addEpc(String epc) {
        mEpcSet.add(epc);
    }

    public void clearEpcList() {
        mEpcSet.clear();
    }

    /**
     * 获取监控数据
     *
     * @return List<MonitorDataBean>
     */
    public List<MonitorDataBean> getMonitorData() {
        return mMonitorDataBeanList;
    }

    /**
     * 是否开启监控数据
     *
     * @return bool
     */
    public boolean isEnableMonitor() {
        return mIsEnableMonitor;
    }

    /**
     * 保存监控状态
     *
     * @param isEnableMonitor 是否启用监控
     */
    public void saveMonitorStatus(boolean isEnableMonitor) {
        this.mIsEnableMonitor = isEnableMonitor;
        Hawk.put(Key.ENABLE_MONITOR, isEnableMonitor);
    }

    /**
     * 添加监控数据
     *
     * @param bean {@link MonitorDataBean}
     */
    public void addMonitorData(MonitorDataBean bean) {
        if (bean == null || !mIsEnableMonitor) {//是否开启监控
            return;
        }
        boolean isOverCapacity = mMonitorDataBeanList.size() >= 100;
        if (isOverCapacity) {//是否超过容量
            mMonitorDataBeanList.removeLast(); /*超过容量后删除最老的数据*/
        }
        mMonitorDataBeanList.addFirst(bean);
        if (ActivityUtils.isEmpty()) {
            return;
        }
        Activity activity = ActivityUtils.getLast();
        if (activity == null) {
            return;
        }
        /*if (activity.getClass().equals(MonitorActivity.class)) {
            //当前显示的是监控数据界面
            MonitorActivity monitorActivity = (MonitorActivity) activity;
            if (monitorActivity.isFinishing() || monitorActivity.isDestroyed()) {
                return;
            }
            //刷新监控数据
            monitorActivity.refreshData();
        }*/
    }

    /**
     * 移除所有数据
     */
    public void clearMonitorData() {
        mMonitorDataBeanList.clear();
    }
}
