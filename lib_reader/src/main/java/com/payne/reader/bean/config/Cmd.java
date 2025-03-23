package com.payne.reader.bean.config;

import java.lang.reflect.Field;

/**
 * Data command code protocol class, Command code and description, please refer to Serial Protocol User's Guide
 *
 * @author naz
 * Date 2019/11/28
 */
public class Cmd {
    /**
     * Reset reader
     */
    public final static byte RESET = 0x70;
    /**
     * Set the serial communication baud rate
     */
    public final static byte SET_SERIAL_PORT_BAUD_RATE = 0x71;
    /**
     * Read reader firmware version
     */
    public final static byte GET_FIRMWARE_VERSION = 0x72;
    /**
     * Set the reader address
     */
    public final static byte SET_READER_ADDRESS = 0x73;
    /**
     * Set the reader antenna
     */
    public final static byte SET_WORK_ANTENNA = 0x74;
    /**
     * Query the current antenna working antenna
     */
    public final static byte GET_WORK_ANTENNA = 0x75;
    /**
     * Set the reader RF output power
     */
    public final static byte SET_OUTPUT_POWER = 0x76;
    /**
     * Query reader current output power
     */
    public final static byte GET_OUTPUT_POWER = 0x77;
    /**
     * Query the current reader power, eight ports
     */
    public final static byte GET_OUTPUT_POWER_EIGHT = (byte) 0x97;
    /**
     * Set the reader frequency range
     */
    public final static byte SET_FREQUENCY_REGION = 0x78;
    /**
     * Query reader working frequency range
     */
    public final static byte GET_FREQUENCY_REGION = 0x79;
    /**
     * Setting the buzzer state
     */
    public final static byte SET_BEEPER_MODE = 0x7A;
    /**
     * Query the working temperature of the current device
     */
    public final static byte GET_READER_TEMPERATURE = 0x7B;
    /**
     * Read GPIO level
     */
    public final static byte READ_GPIO_VALUE = 0x60;
    /**
     * Set GPIO level
     */
    public final static byte WRITE_GPIO_VALUE = 0x61;
    /**
     * Setting the antenna connection detector status
     */
    public final static byte SET_ANT_CONNECTION_DETECTOR = 0x62;
    /**
     * Read antenna connection detector status
     */
    public final static byte GET_ANT_CONNECTION_DETECTOR = 0x63;
    /**
     * Set the temporary RF output power of the reader
     */
    public final static byte SET_TEMPORARY_OUTPUT_POWER = 0x66;
    /**
     * Set Reader ID
     */
    public final static byte SET_READER_IDENTIFIER = 0x67;
    /**
     * Read reader ID
     */
    public final static byte GET_READER_IDENTIFIER = 0x68;
    /**
     * Set the communication rate of the RF link
     */
    public final static byte SET_RF_LINK_PROFILE = 0x69;
    /**
     * Read the communication rate of the RF link
     */
    public final static byte GET_RF_LINK_PROFILE = 0x6A;

    public final static byte SET_E710_LINK_PROFILE = (byte) 0xF0;
    public final static byte GET_E710_LINK_PROFILE = (byte) 0xF1;
    public final static byte SET_E710_Q = (byte) 0xF2;
    public final static byte GET_E710_Q = (byte) 0xF3;
    /**
     * Measure the return loss of the antenna port
     */
    public final static byte GET_RF_PORT_RETURN_LOSS = 0x7E;
    /**
     * Inventory tags
     */
    public final static byte INVENTORY = (byte) 0x80;
    /**
     * Read tag
     */
    public final static byte READ_TAG = (byte) 0x81;
    /**
     * Write tags
     */
    public final static byte WRITE_TAG = (byte) 0x82;
    /**
     * Lock tag
     */
    public final static byte LOCK_TAG = (byte) 0x83;
    /**
     * Inactivate tag
     */
    public final static byte KILL_TAG = (byte) 0x84;
    /**
     * EPC number matching ACCESS operation
     */
    public final static byte SET_ACCESS_EPC_MATCH = (byte) 0x85;
    /**
     * Query matching EPC status
     */
    public final static byte GET_ACCESS_EPC_MATCH = (byte) 0x86;
    /**
     * Quickly poll multiple antenna inventory tags
     */
    public final static byte FAST_SWITCH_ANT_INVENTORY = (byte) 0x8A;
    /**
     * Custom session and target inventory
     */
    public final static byte CUSTOMIZED_SESSION_TARGET_INVENTORY = (byte) 0x8B;
    public final static byte FAST_INVENTORY = (byte) 0x89;
    /**
     * Set Monza tag to read TID quickly (settings are not saved to internal FLASH)
     */
    public final static byte SET_IMPINJ_FAST_TID = (byte) 0x8C;
    /**
     * Set Monza tag to read TID quickly (settings are saved to internal FLASH)
     */
    public final static byte SET_AND_SAVE_IMPINJ_FAST_TID = (byte) 0x8D;
    /**
     * Query the current fast TID setting
     */
    public final static byte GET_IMPINJ_FAST_TID = (byte) 0x8E;
    /**
     * Block write tag
     */
    public final static byte BLOCK_WRITE_TAG = (byte) 0x94;
    /**
     * Filter tag (cmd tag select) command
     */
    public final static byte OPERATE_TAG_MASK = (byte) 0x98;
    /**
     * Set the status of the reader (active reading, etc.)
     */
    public final static byte SET_READER_STATUS = (byte) 0xA0;
    /**
     * Query reader status
     */
    public final static byte QUERY_READER_STATUS = (byte) 0xA1;
    /**
     * Temperature label 2 Command
     */
    public final static byte TEMPERATURE_LABEL_COMMAND = (byte) 0xFD;
    /**
     * TM670才有国标
     */
    public final static byte SET_GB = (byte) 0x6E;
    /**
     * TM670才有国标
     */
    public final static byte GET_IS_GB = (byte) 0X6F;

    public static String getNameForCmd(byte cmd) {
        String result = "unknown cmd";
        try {
            Field[] fields = Cmd.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                byte b = (byte) field.get(null);
                if (b == cmd) {
                    result = field.getName();
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
}