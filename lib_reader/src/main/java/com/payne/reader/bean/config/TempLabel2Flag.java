package com.payne.reader.bean.config;

/**
 * @author naz
 * Date 2020/12/21
 */
public class TempLabel2Flag {
    /**
     * 1.Single temperature measurement
     */
    public final static byte SINGLE_MEAS_TEMP = 0x01;
    /**
     * 2.Turn on RTC temperature measurement
     */
    public final static byte ENABLE_RTC_MEAS_TEMP = 0x02;
    /**
     * 3.Stop RTC temperature measurement
     */
    public final static byte DISABLE_RTC_MEAS_TEMP = 0x03;
    /**
     * 4.Load configuration
     */
    public final static byte LOAD_CONFIG = 0x04;
    /**
     * 5.Read register
     */
    public final static byte READ_REGISTER = 0x10;
    /**
     * 6.Status query
     */
    public final static byte STATE_CHECK = 0x11;
    /**
     * 7.Certification
     */
    public final static byte AUTH = 0x12;
    /**
     * 8.Read memory
     */
    public final static byte READ_MEMORY = 0x13;
    /**
     * 9.Write memory
     */
    public final static byte WRITE_MEMORY = 0x14;
    /**
     * 10.Led control on and off
     */
    public final static byte LED_CONTROL = 0x20;
}
