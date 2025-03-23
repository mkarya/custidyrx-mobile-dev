package com.custodyrx.library.label.bean.type;

/**
 * @author naz
 * Date 2020/4/9
 */
public class Key {
    public static final String TID_SAVE = "tid_save";
    /**
     * 频点类型
     */
//    public static final String REGION_TYPE = "region_type";
    /**
     * 自定义频点的起始频点
     */
//    public static final String START_FREQUENCY = "start_frequency";
    /**
     * 自定义频点的频点间隔
     */
//    public static final String FREQUENCY_INTERVAL = "frequency_interval";
    /**
     * 蜂鸣器类型
     */
    public static final String BEEPER_TYPE = "beeper_type";
    /**
     * 语言类型
     */
    public static final String LANGUAGE_TYPE = "language_type";
    //=================================
    /**
     * 语言类型-------跟随系统
     */
    public static final int LANGUAGE_TYPE_FOLLOW_SYSTEM = 0;
    /**
     * 语言类型-------中文
     */
    public static final int LANGUAGE_TYPE_CHINESE = 1;
    /**
     * 语言类型------英文
     */
    public static final int LANGUAGE_TYPE_ENGLISH = 2;
    //===================================
    /**
     * 是否启用监控数据
     */
    public static final String ENABLE_MONITOR = "enable_monitor";
    /**
     * 是否启用操作日志
     */
    public static final String ENABLE_OPERATE_LOG = "enable_operate_log";
    /**
     * 是否保存盘存日志
     */
    public static final String SAVE_INVENTORY_LOG = "save_inventory_log";
    /**
     * 最小蜂鸣器间隔时间
     */
    public static final String MINI_NUM_BEEP_TIME = "minimum_beep_time";
    /**
     * 发送命令超时时间
     */
    public static final String CMD_TIMEOUT = "cmd_timeout";
    /**
     * 监控的电量
     */
    public static final String KEY_POWER = "key_power";
    /**
     * 导出路径
     */
    public static final String KEY_EXEL_PATH = "key_excel_path";
    /**
     * 波特率
     */
    public static final String KEY_BAUD_RATE = "key_baud_rate";
}
