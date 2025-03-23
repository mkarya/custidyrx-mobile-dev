package com.payne.reader;

import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.BaudRate;
import com.payne.reader.bean.config.Beeper;
import com.payne.reader.bean.config.ClearMaskId;
import com.payne.reader.bean.config.Cmd;
import com.payne.reader.bean.config.CmdStatus;
import com.payne.reader.bean.config.FastTidType;
import com.payne.reader.bean.config.GpioInType;
import com.payne.reader.bean.config.ProfileId;
import com.payne.reader.bean.config.QMode;
import com.payne.reader.bean.receive.AntConnectionDetector;
import com.payne.reader.bean.receive.E710LinkProfile;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.FreqRegionResult;
import com.payne.reader.bean.receive.GpioOut;
import com.payne.reader.bean.receive.ImpinjFastTid;
import com.payne.reader.bean.receive.MaskInfo;
import com.payne.reader.bean.receive.MatchInfo;
import com.payne.reader.bean.receive.OperationTag;
import com.payne.reader.bean.receive.OutputPower;
import com.payne.reader.bean.receive.QInfo;
import com.payne.reader.bean.receive.ReaderIdentifier;
import com.payne.reader.bean.receive.ReaderStatus;
import com.payne.reader.bean.receive.ReaderTemperature;
import com.payne.reader.bean.receive.ReceiveData;
import com.payne.reader.bean.receive.RfLinkProfile;
import com.payne.reader.bean.receive.RfPortReturnLoss;
import com.payne.reader.bean.receive.Success;
import com.payne.reader.bean.receive.TempLabel2;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.bean.receive.WorkAntenna;
import com.payne.reader.bean.send.CustomSessionReadConfig;
import com.payne.reader.bean.send.FreqNormal;
import com.payne.reader.bean.send.FreqUserDefine;
import com.payne.reader.bean.send.Identifier;
import com.payne.reader.bean.send.InventoryConfig;
import com.payne.reader.bean.send.KillConfig;
import com.payne.reader.bean.send.LockConfig;
import com.payne.reader.bean.send.MaskConfig;
import com.payne.reader.bean.send.MatchConfig;
import com.payne.reader.bean.send.MultiAntReadTagConfig;
import com.payne.reader.bean.send.OutputPowerConfig;
import com.payne.reader.bean.send.ReadConfig;
import com.payne.reader.bean.send.TempLabel2Config;
import com.payne.reader.bean.send.WriteConfig;
import com.payne.reader.communication.ConnectHandle;
import com.payne.reader.communication.DataPacket;

import java.security.InvalidParameterException;

/**
 * Reader class
 *
 * @author naz
 * Date 2020/7/14
 */
public interface Reader { /*region Reader connection part*/

    /**
     * Connect reader
     *
     * @param handle Connection handle{@link ConnectHandle}
     * @return Connection success or failure
     */
    boolean connect(ConnectHandle handle);

    /**
     * Is it connected
     *
     * @return Is it connected
     */
    boolean isConnected();

    /**
     * Disconnect reader
     */
    void disconnect();

    /**
     * Get the number of antennas
     *
     * @return AntennaCount
     */
    AntennaCount getAntennaCount();

    void setAntStartFrom1();

    /**
     * Switch antenna number
     *
     * @param antennaCount {@link AntennaCount}
     */
    void switchAntennaCount(AntennaCount antennaCount);
    //endregion

    /**
     * Processing timeout, When the set timeout time>0,
     * * it will judge whether the two commands are timeout before receiving the end sign,
     * * and cancel the timeout mechanism when <=0
     *
     * @param timeoutMillis Timeout period, in milliseconds
     */
    void setCmdTimeout(long timeoutMillis);

    //region Reader system configuration interface

    /**
     * Reset the specified address reader.
     * Operation successful: no data returned, reader restarted, buzzer sounded
     *
     * @param onError Return error command callback
     */
    void reset(Consumer<Failure> onError);

    /**
     * 国标
     */
    void isGB(Consumer<ReceiveData> onSuccess, Consumer<Failure> onError);

    void setGB(boolean setGb, Consumer<Success> onSuccess, Consumer<Failure> onError);


    /**
     * Set the Serial Communication Baud rate.
     *
     * @param baudRate  Baud Rate{@link BaudRate}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setBaudRate(BaudRate baudRate, Consumer<Success> onSuccess, Consumer<Failure> onError);

    void setBaudRate(BaudRate baudRate, Consumer<Success> onSuccess, Consumer<Failure> onError, Consumer<Throwable> onConnectError);

    /**
     * Set Reader Address.
     *
     * @param address   New Reader Address,value range0-254(0x00-0xFE)
     *                  (0xFF Public Address)
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setReaderAddress(byte address, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Get the reader address, default 0xFF
     *
     * @return reader address, default 0xFF
     */
    byte getReaderAddress();

    /**
     * Get Reader Firmware Version.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getFirmwareVersion(Consumer<Version> onSuccess, Consumer<Failure> onError);

    /**
     * Set Buzzer behavior. <br>
     * Buzzer behavior 0x02(Beep after every tag has identified) occupies CPU
     * process time that affects anti-collision algorithm significantly. It is
     * recommended that this option should be used for tag test.
     *
     * @param beeper    Type of buzzer sound when operating the label{@link Beeper}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setBeeperMode(Beeper beeper, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Query internal temperature.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getReaderTemperature(Consumer<ReaderTemperature> onSuccess, Consumer<Failure> onError);

    /**
     * Read GPIO Level.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void readGpio(Consumer<GpioOut> onSuccess, Consumer<Failure> onError);

    /**
     * Write GPIO Level.
     *
     * @param inType    see{@link GpioInType}
     * @param high      Whether it is pulled high
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void writeGpio(GpioInType inType, boolean high, Consumer<Success> onSuccess, Consumer<Failure> onError);
    //endregion

    //region Reader radio frequency configuration interface

    /**
     * Set antenna connection detector status.
     *
     * @param detectorSensitivity status(0x00:close detector of antenna connection, other
     *                            values:sensitivity of antenna connection detector(return loss
     *                            of port),unit dB. The higher the value, the greater the
     *                            impedance matching requirements for port
     * @param onSuccess           Return success command callback
     * @param onError             Return error command callback
     */
    void setAntConnectionDetector(byte detectorSensitivity, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Get antenna connection detector status.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getAntConnectionDetector(Consumer<AntConnectionDetector> onSuccess, Consumer<Failure> onError);

    /**
     * Set work antenna.
     *
     * @param antennaId Ant ID(0x00:Ant 1, 0x01:Ant 2, 0x02:Ant 3, 0x03:Ant 4 ...)
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     * @throws InvalidParameterException Invalid parameter exception
     */
    void setWorkAntenna(int antennaId, Consumer<Success> onSuccess, Consumer<Failure> onError) throws InvalidParameterException;

    /**
     * Query working antenna.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getWorkAntenna(Consumer<WorkAntenna> onSuccess, Consumer<Failure> onError);

    /**
     * Get the antenna number, not directly from the hardware,
     * but the last cached antenna value
     *
     * @return Antenna id
     */
    int getCacheWorkAntenna();

    /**
     * Get the antenna group id, not directly from the hardware,
     * but the last cached antenna group id value
     *
     * @return Antenna group id
     */
    int getCacheAntennaGroup();

    /**
     * Set output power(Method 1). <br>
     * This command consumes more than 100mS. <br>
     * If you want you change the output power frequently, please use
     * Cmd_set_temporary_output_power command, which doesn't reduce the life of
     * the internal flash memory.
     *
     * @param powerConfig RF output power configuration,
     *                    E.g {@link com.payne.reader.bean.send.PowerSingleAntenna},
     *                    {@link com.payne.reader.bean.send.PowerFourAntenna},
     *                    {@link com.payne.reader.bean.send.PowerEightAntenna},
     *                    {@link com.payne.reader.bean.send.PowerSixteenAntenna},
     * @param onSuccess   Return success command callback
     * @param onError     Return error command callback
     * @throws InvalidParameterException Invalid parameter exception
     */
    void setOutputPower(OutputPowerConfig powerConfig, Consumer<Success> onSuccess, Consumer<Failure> onError) throws InvalidParameterException;

    /**
     * Query output power.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getOutputPower(Consumer<OutputPower> onSuccess, Consumer<Failure> onError);

    /**
     * Set the power of all antennas to uniform Power
     *
     * @param uniformPower RF output power, range from 20-33(0x14 - 0x21), the unit is
     *                     dBm.
     * @param isTemporary  Whether it is a temporary setting,
     *                     if it is a temporary output power setting. <br>
     *                     The output power value will Not be saved to the internal flash memory so
     *                     that the output power will be restored from the internal flash memory
     *                     after restart or power off.
     * @param onSuccess    Return success command callback
     * @param onError      Return error command callback
     */
    void setOutputPowerUniformly(byte uniformPower, boolean isTemporary, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Set frequency region(system default frequencies).
     *
     * @param freqNormal see{@link FreqNormal}
     * @param onSuccess  Return success command callback
     * @param onError    Return error command callback
     */
    void setFrequencyRegion(FreqNormal freqNormal, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Set frequency region(user defined frequencies).
     *
     * @param freqUserDefine see{@link FreqUserDefine}.
     * @param onSuccess      Return success command callback
     * @param onError        Return error command callback
     */
    void setUserDefineFrequency(FreqUserDefine freqUserDefine, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Query frequency region.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getFrequencyRegion(Consumer<FreqRegionResult> onSuccess, Consumer<Failure> onError);

    /**
     * Set Reader Identifier. <br>
     * The identifier is stored in internal flash.
     *
     * @param identifier Reader's identifier, refer{@link Identifier}
     * @param onSuccess  Return success command callback
     * @param onError    Return error command callback
     */
    void setReaderIdentifier(Identifier identifier, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Get Reader Identifier.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getReaderIdentifier(Consumer<ReaderIdentifier> onSuccess, Consumer<Failure> onError);


    /**
     * RF Link Setup. <br>
     * If this command succeeded, reader will be reset, and the profile
     * configuration is stored in the internal flash.
     *
     * @param profileId Communication rate{@link ProfileId}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setRfLinkProfile(ProfileId profileId, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Read RF Link.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getRfLinkProfile(Consumer<RfLinkProfile> onSuccess, Consumer<Failure> onError);

    void setE710LinkProfile(ProfileId profileId, byte drMode, Consumer<Success> onSuccess, Consumer<Failure> onError);

    void getE710LinkProfile(Consumer<E710LinkProfile> onSuccess, Consumer<Failure> onError);

    void setE710_Q(QInfo qInfo, Consumer<Success> onSuccess, Consumer<Failure> onError);

    void getE710_Q(Consumer<QInfo> onSuccess, Consumer<Failure> onError);

    /**
     * Measure RF Port Return Loss.
     *
     * @param frequency FreqParameter, system will measure the return loss of current
     *                  antenna port at the desired frequency.
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getRfPortReturnLoss(byte frequency, Consumer<RfPortReturnLoss> onSuccess, Consumer<Failure> onError);
    //endregion

    //region Inventory related

    /**
     * Configure inventory parameters
     *
     * @param config Inventory configuration, see{@link InventoryConfig}
     * @throws InvalidParameterException Invalid parameter exception
     */
    void setInventoryConfig(InventoryConfig config) throws InvalidParameterException;

    /**
     * Start cycle inventory
     *
     * @param loop reserved field
     */
    void startInventory(boolean loop);

    /**
     * Stop cycle inventory
     */
    void stopInventory();

    void stopInventory(boolean isFastMode);
    //endregion

    void setMultiAntReadTagConfig(MultiAntReadTagConfig config);

    void startMultiAntReadTag();

    void stopMultiAntReadTag();

    /**
     * stopReadTag，reset related params
     */
    void stopReadTag();

    /**
     * Read Tag. <br>
     * Attention: <br>
     * If two tags have the same EPC, but different read data, then these two
     * tags are considered different tags.
     *
     * @param config    Configure read label parameters, see{@link ReadConfig}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void readTag(ReadConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError);

    /**
     * Read Tag. <br>
     * Attention: <br>
     * If two tags have the same EPC, but different read data, then these two
     * tags are considered different tags.
     *
     * @param config    Configure custom Session Target to read label parameters, see{@link CustomSessionReadConfig}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void readTag(CustomSessionReadConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError);

    /**
     * Write Tag.
     *
     * @param config     Configure write tag data parameters, see{@link WriteConfig}
     * @param blockWrite The two label writing methods are different and independent of each other.
     *                   Recommend to use Block Write command, which is more efficient
     * @param onSuccess  Return success command callback
     * @param onError    Return error command callback
     */
    void writeTag(WriteConfig config, boolean blockWrite, Consumer<OperationTag> onSuccess, Consumer<Failure> onError);

    /**
     * Lock Tag.
     *
     * @param config    Configure lock tag data parameters, see{@link LockConfig}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void lockTag(LockConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError);

    /**
     * Kill Tag.
     *
     * @param config    Configure kill tag data parameters, see{@link KillConfig}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void killTag(KillConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError);

    /**
     * Set Impinj Monza FastTID. <br>
     * Attention: <br>
     * This function is only affective for some of Impinj Monza tag types. <br>
     * This function improves the performance of identifying tag's TID. <br>
     * When this function takes effect, tag's TID will be included to tag's EPC,
     * therefore, tag's EPC will be altered; the original data (PC + EPC) will
     * be changed to altered PC + EPC + EPC's CRC + TID. <br>
     * If error occurred during identifying TID, only the original data (PC +
     * EPC) will be sent. <br>
     * If you don't need this function, please turn it off, otherwise there will
     * be unnecessary time consumption. <br>
     * This command doesn't store the status to internal flash. After reset or
     * power on, the value stored in flash will be restored.
     *
     * @param fastType  Impinj Monza tag type, see{@link FastTidType}
     * @param save      Whether to store in FLASH
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setImpinjFastTid(FastTidType fastType, boolean save, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Query current set of FastTID.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getImpinjFastTid(Consumer<ImpinjFastTid> onSuccess, Consumer<Failure> onError);

    /**
     * Set EPC match(EPC match is effective,until next refresh).
     *
     * @param config    Match EPC parameter configuration class, see{@link MatchConfig}.
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setEpcMatch(MatchConfig config, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Query match EPC status.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getEpcMatch(Consumer<MatchInfo> onSuccess, Consumer<Failure> onError);

    /**
     * Clear EPC match.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void clearEpcMatch(Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Set the mask filter the Tag.
     *
     * @param config    Configuration class for filtering tags, see{@link MaskConfig}.
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setTagMask(MaskConfig config, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Get the mask setting.
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getTagMask(Consumer<MaskInfo> onSuccess, Consumer<Failure> onError);

    /**
     * Clear the mask setting.
     *
     * @param function  The mask function, see{@link ClearMaskId}
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void clearTagMask(ClearMaskId function, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Set reader status
     *
     * @param status    reader status
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void setReaderStatus(byte status, Consumer<Success> onSuccess, Consumer<Failure> onError);

    /**
     * Get reader status
     *
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void getReaderStatus(Consumer<ReaderStatus> onSuccess, Consumer<Failure> onError);

    /**
     * Temperature measurement label 2
     *
     * @param config    Temperature label configuration
     * @param onSuccess Return success command callback
     * @param onError   Return error command callback
     */
    void measTempLabel2(TempLabel2Config config, Consumer<TempLabel2> onSuccess, Consumer<Failure> onError);

    /**
     * Command status callback
     *
     * @param onStatus Callback
     */
    void setCommandStatusCallback(Consumer<CmdStatus> onStatus);

    /**
     * Set original data callback
     *
     * @param onSend    Callback of the original data sent
     * @param onReceive Callback of received original data
     */
    void setOriginalDataCallback(Consumer<byte[]> onSend, Consumer<byte[]> onReceive);

    void addOriginalDataReceivedCallback(Consumer<byte[]> onReceive);

    void removeOriginalDataReceivedCallback(Consumer<byte[]> onReceive);

    /**
     * Set the callback of undefined data (Usually the data returned by the device)
     *
     * @param onResult Listen for callbacks for undefined data
     */
    void setUndefinedResultCallback(Consumer<ReceiveData> onResult);

    void removeCallback(byte cmd);

    /**
     * Send custom request
     *
     * @param cmd  Command code
     * @param data Data (excluding data header, length, address, cmd and checksum)
     *             // ---------------------------------------------------------
     *             // | header | length | address |  cmd  | [data] | checksum |
     *             // |  0xA0  |  0x04  |   0xFF  |  0x74 |  0x00  |   0xE9   |
     *             // ---------------------------------------------------------
     * @deprecated This method is outdated and
     * recommended{@link #sendCustomRequest(DataPacket)}
     */
    void sendCustomRequest(byte cmd, byte[] data);

    /**
     * Send custom request
     *
     * @param packet Data packet
     */
    void sendCustomRequest(DataPacket packet);

    void sendCustomRequest(DataPacket packet, Consumer<Success> onSuccess, Consumer<Failure> onError);

    default String getSdkBuildInfo() {
        return "add cmd:89, packaged at 2022-10-13 04.00";
    }
}