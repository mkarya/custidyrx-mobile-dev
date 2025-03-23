package com.payne.reader.bean.config;

import java.lang.reflect.Field;

/**
 * Regarding UHF return code and description, please refer to Serial Protocol User's Guide
 *
 * @author naz
 * Date 2020/1/2
 */
public class ResultCode {
    /**
     * Command executed successfully
     */
    public final static byte SUCCESS = 0x10;
    /**
     * Command execution failed
     */
    public final static byte FAIL = 0x11;
    /**
     * CPU reset error
     */
    public final static byte MCU_RESET_ERROR = 0x20;
    /**
     * Turn on CW error
     */
    public final static byte CW_ON_ERROR = 0x21;
    /**
     * Antenna is not connected
     */
    public final static byte ANTENNA_MISSING_ERROR = 0x22;
    /**
     * Write flash error
     */
    public final static byte WRITE_FLASH_ERROR = 0x23;
    /**
     * Reading Flash ResultCode
     */
    public final static byte READ_FLASH_ERROR = 0x24;
    /**
     * Set transmit power error
     */
    public final static byte SET_OUTPUT_POWER_ERROR = 0x25;
    /**
     * Inventory label error
     */
    public final static byte TAG_INVENTORY_ERROR = 0x31;
    /**
     * Reading label error
     */
    public final static byte TAG_READ_ERROR = 0x32;
    /**
     * Write label error
     */
    public final static byte TAG_WRITE_ERROR = 0x33;
    /**
     * Lock tag error
     */
    public final static byte TAG_LOCK_ERROR = 0x34;
    /**
     * Kill tag error
     */
    public final static byte TAG_KILL_ERROR = 0x35;
    /**
     * No actionable label error
     */
    public final static byte NO_TAG_ERROR = 0x36;
    /**
     * Successful inventory but access failed
     */
    public final static byte INVENTORY_OK_BUT_ACCESS_FAIL = 0x37;
    /**
     * Cache is empty
     */
    public final static byte BUFFER_IS_EMPTY_ERROR = 0x38;
    /**
     * NXP chip custom instruction failed
     */
    public final static byte NXP_CUSTOM_COMMAND_FAIL = 0x3C;
    /**
     * Bad access tag or wrong access password
     */
    public final static byte ACCESS_OR_PASSWORD_ERROR = 0x40;
    /**
     * Invalid parameter
     */
    public final static byte PARAMETER_INVALID = 0x41;
    /**
     * word Cnt parameter exceeds specified length
     */
    public final static byte PARAMETER_INVALID_WORDCNT_TOO_LONG = 0x42;
    /**
     * Mem Bank parameters are out of range
     */
    public final static byte PARAMETER_INVALID_MEMBANK_OUT_OF_RANGE = 0x43;
    /**
     * Lock data area parameter is out of range
     */
    public final static byte PARAMETER_INVALID_LOCK_REGION_OUT_OF_RANGE = 0x44;
    /**
     * Lock Type parameter is out of range
     */
    public final static byte PARAMETER_INVALID_LOCK_ACTION_OUT_OF_RANGE = 0x45;
    /**
     * Reader address is invalid
     */
    public final static byte PARAMETER_READER_ADDRESS_INVALID = 0x46;
    /**
     * Antenna_id out of range
     */
    public final static byte PARAMETER_INVALID_ANTENNA_ID_OUT_OF_RANGE = 0x47;
    /**
     * Output power parameter is out of range
     */
    public final static byte PARAMETER_INVALID_OUTPUT_POWER_OUT_OF_RANGE = 0x48;
    /**
     * RF specification area parameter is out of range
     */
    public final static byte PARAMETER_INVALID_FREQUENCY_REGION_OUT_OF_RANGE = 0x49;
    /**
     * Baud rate parameter is out of range
     */
    public final static byte PARAMETER_INVALID_BAUDRATE_OUT_OF_RANGE = 0x4A;
    /**
     * Buzzer setting parameter is out of range
     */
    public final static byte PARAMETER_BEEPER_MODE_OUT_OF_RANGE = 0x4B;
    /**
     * EPC match length out of bounds
     */
    public final static byte PARAMETER_EPC_MATCH_LEN_TOO_LONG = 0x4C;
    /**
     * EPC match length error
     */
    public final static byte PARAMETER_EPC_MATCH_LEN_ERROR = 0x4D;
    /**
     * EPC matching parameters are out of range
     */
    public final static byte PARAMETER_INVALID_EPC_MATCH_MODE = 0x4E;
    /**
     * Frequency range setting parameter error
     */
    public final static byte PARAMETER_INVALID_FREQUENCY_RANGE = 0x4F;
    /**
     * Unable to receive tagged RN 16
     */
    public final static byte FAIL_TO_GET_RN16_FROM_TAG = 0x50;
    /**
     * DRM setting parameter error
     */
    public final static byte PARAMETER_INVALID_DRM_MODE = 0x51;
    /**
     * PLL cannot lock
     */
    public final static byte PLL_LOCK_FAIL = 0x52;
    /**
     * RF chip is not responding
     */
    public final static byte RF_CHIP_FAIL_TO_RESPONSE = 0x53;
    /**
     * The output does not reach the specified output power
     */
    public final static byte FAIL_TO_ACHIEVE_DESIRED_OUTPUT_POWER = 0x54;
    /**
     * Copyright certification failed
     */
    public final static byte COPYRIGHT_AUTHENTICATION_FAIL = 0x55;
    /**
     * Spectrum specification setting error
     */
    public final static byte SPECTRUM_REGULATION_ERROR = 0x56;
    /**
     * Output power is too low
     */
    public final static byte OUTPUT_POWER_TOO_LOW = 0x57;
    /**
     * Failed to measure return loss
     */
    public final static byte FAIL_TO_GET_RF_PORT_RETURN_LOSS = (byte) 0xEE;
    /**
     * unknown error
     */
    public final static byte UNKNOWN_ERROR = 0x58;
    /**
     * Send request queue is full, this request is invalid
     */
    public final static byte REQUEST_INVALID = (byte) 0xB0;
    /**
     * Send a request packet to the device,
     * and no response packet is received within the specified time
     */
    public final static byte REQUEST_TIMEOUT = (byte) 0xB1;

    public static String getNameForResultCode(byte resultCode) {
        String result = "unknown resultCode";

        try {
            Field[] fields = ResultCode.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                byte b = (Byte) field.get((Object) null);
                if (b == resultCode) {
                    result = field.getName();
                    break;
                }
            }
        } catch (Throwable var5) {
            var5.printStackTrace();
        }

        return result;
    }
}
