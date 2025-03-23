package com.payne.reader.process;

import com.payne.reader.Reader;
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
import com.payne.reader.communication.RequestInfo;
import com.payne.reader.util.Converter;
import com.payne.reader.util.LLLog;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/7/8
 */
public class ReaderImpl implements Reader {
    private final ResultProcess mResultProcess;

    private ReaderImpl(AntennaCount antennaCount) {
        this.mResultProcess = new ResultProcess(antennaCount);
    }

    /**
     * @param channelCount Module antenna channel number, see{@link AntennaCount}
     * @return {@link Reader}
     */
    public static Reader create(AntennaCount channelCount) {
        return new ReaderImpl(channelCount);
    }

    @Override
    public boolean connect(ConnectHandle handle) {
        boolean success;
        synchronized (this) {
            Objects.requireNonNull(handle);
            success = mResultProcess.connect(handle);
        }
        return success;
    }

    @Override
    public boolean isConnected() {
        synchronized (this) {
            return mResultProcess.isConnected();
        }
    }

    @Override
    public void disconnect() {
        synchronized (this) {
            mResultProcess.disconnect();
        }
    }

    @Override
    public byte getReaderAddress() {
        return mResultProcess.mAddress;
    }

    @Override
    public AntennaCount getAntennaCount() {
        return this.mResultProcess.getAntennaCount();
    }

    @Override
    public void setAntStartFrom1() {
        mResultProcess.setAntStartFrom1();
    }

    @Override
    public void switchAntennaCount(AntennaCount antennaCount) {
        this.mResultProcess.switchAntennaCount(antennaCount);
    }

    @Override
    public void setCmdTimeout(long timeoutMillis) {
        this.mResultProcess.setTimeout(timeoutMillis);
    }

    /**
     * TM670才有国标
     */
    @Override
    public void isGB(Consumer<ReceiveData> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_IS_GB;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    /**
     * TM670才有国标
     */
    @Override
    public void setGB(boolean setGb, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_GB;

        byte[] btArray = new byte[1];
        btArray[0] = (byte) (setGb ? 0x01 : 0x00);

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void reset(Consumer<Failure> onError) {
        byte cmd = Cmd.RESET;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet, false);
        mResultProcess.setResultCallback(cmd, null, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setBaudRate(BaudRate baudRate, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        setBaudRate(baudRate, onSuccess, onError, null);
    }

    @Override
    public final void setBaudRate(BaudRate baudRate, Consumer<Success> onSuccess, Consumer<Failure> onError, Consumer<Throwable> onConnectError) {
        byte cmd = Cmd.SET_SERIAL_PORT_BAUD_RATE;

        byte[] btArray = new byte[1];
        btArray[0] = baudRate.getValue();
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo, onConnectError);
    }

    @Override
    public void setReaderAddress(byte address, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_READER_ADDRESS;

        byte[] btArray = new byte[1];
        btArray[0] = address;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getFirmwareVersion(Consumer<Version> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_FIRMWARE_VERSION;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setBeeperMode(Beeper beeper, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_BEEPER_MODE;

        byte[] btArray = new byte[1];
        btArray[0] = beeper.getValue();
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getReaderTemperature(Consumer<ReaderTemperature> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_READER_TEMPERATURE;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void readGpio(Consumer<GpioOut> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.READ_GPIO_VALUE;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void writeGpio(GpioInType inType, boolean high, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.WRITE_GPIO_VALUE;

        byte[] btArray = new byte[2];
        btArray[0] = inType.getValue();
        btArray[1] = high ? (byte) 0x01 : 0x00;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setAntConnectionDetector(byte detectorSensitivity, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_ANT_CONNECTION_DETECTOR;

        byte[] btArray = new byte[1];
        btArray[0] = detectorSensitivity;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getAntConnectionDetector(Consumer<AntConnectionDetector> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_ANT_CONNECTION_DETECTOR;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setWorkAntenna(int antennaId, Consumer<Success> onSuccess, Consumer<Failure> onError) throws InvalidParameterException {
        byte cmd = Cmd.SET_WORK_ANTENNA;

        byte[] btArray = new byte[1];
        int antennaCountValue = mResultProcess.getAntennaCount().getValue();
        if (antennaId >= antennaCountValue) {
            throw new InvalidParameterException("AntennaId is out of reader channels, Current is " + antennaCountValue + " channels");
        }
        int groupId;
        if (antennaId >= 8) {
            groupId = 1;
            btArray[0] = (byte) (antennaId - 8);
        } else {
            groupId = 0;
            btArray[0] = (byte) antennaId;
        }
        mResultProcess.setTryAntennaId(antennaId);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);

        int antennaGroupId = mResultProcess.getAntennaGroupId();
        if (antennaGroupId == groupId && mResultProcess.getAntennaCount() != AntennaCount.SIXTEEN_CHANNELS) {
            mResultProcess.addRequest(requestInfo);
        } else {
            mResultProcess.setAntennaGroup(groupId, requestInfo);
        }
    }

    @Override
    public void getWorkAntenna(Consumer<WorkAntenna> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_WORK_ANTENNA;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.getAntennaGroup(requestInfo);
    }

    @Override
    public int getCacheWorkAntenna() {
        return mResultProcess.getCacheAntennaId();
    }

    @Override
    public int getCacheAntennaGroup() {
        return mResultProcess.getAntennaGroupId();
    }

    @Override
    public void setOutputPower(OutputPowerConfig config, Consumer<Success> onSuccess, Consumer<Failure> onError) throws InvalidParameterException {
        byte cmd = Cmd.SET_OUTPUT_POWER;

        Objects.requireNonNull(config);
        if (!config.checkAntennaCount(mResultProcess.getAntennaCount())) {
            throw new InvalidParameterException("powers length is greater than the number of reader channels, " +
                    "The current reader object is " + mResultProcess.getAntennaCount().getValue() + " channels");
        }
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.setOutputPower(config);
    }

    @Override
    public void getOutputPower(Consumer<OutputPower> onSuccess, Consumer<Failure> onError) {
        AntennaCount antennaCount = mResultProcess.getAntennaCount();
        byte cmd;
        if (antennaCount == AntennaCount.SIXTEEN_CHANNELS || antennaCount == AntennaCount.EIGHT_CHANNELS) {
            cmd = Cmd.GET_OUTPUT_POWER_EIGHT;
        } else {
            cmd = Cmd.GET_OUTPUT_POWER;
        }

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        if (antennaCount == AntennaCount.SIXTEEN_CHANNELS) {
            mResultProcess.setAntennaGroup(0, requestInfo);
        } else {
            mResultProcess.addRequest(requestInfo);
        }
    }

    @Override
    public void setOutputPowerUniformly(byte uniformPower, boolean isTemporary, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd;
        if (isTemporary) {
            cmd = Cmd.SET_TEMPORARY_OUTPUT_POWER;
        } else {
            cmd = Cmd.SET_OUTPUT_POWER;
        }
        byte[] btArray = new byte[1];
        btArray[0] = uniformPower;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        if (mResultProcess.getAntennaCount() == AntennaCount.SIXTEEN_CHANNELS) {
            mResultProcess.setAntennaGroup(0, requestInfo);
        } else {
            mResultProcess.addRequest(requestInfo);
        }
    }

    @Override
    public void setFrequencyRegion(FreqNormal freqNormal, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_FREQUENCY_REGION;

        Objects.requireNonNull(freqNormal);
        byte[] btArray = new byte[3];
        btArray[0] = freqNormal.getRegion().getValue();
        btArray[1] = freqNormal.getFreqStart().getValue();
        btArray[2] = freqNormal.getFreqEnd().getValue();
        mResultProcess.setFreqNormal();
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setUserDefineFrequency(FreqUserDefine freqUserDefine, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_FREQUENCY_REGION;

        Objects.requireNonNull(freqUserDefine);
        byte[] btArray = new byte[6];
        byte[] btAryFreq = Converter.getBytes(freqUserDefine.getFreqStart(), Converter.BIG_ENDIAN);
        btArray[0] = 0x04;
        btArray[1] = (byte) (freqUserDefine.getFreqInterval() / 10);
        btArray[2] = freqUserDefine.getFreqQuantity();
        btArray[3] = btAryFreq[2];
        btArray[4] = btAryFreq[1];
        btArray[5] = btAryFreq[0];

        mResultProcess.setFreqUserDefine(freqUserDefine.getFreqStart(), freqUserDefine.getFreqInterval());
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getFrequencyRegion(Consumer<FreqRegionResult> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_FREQUENCY_REGION;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setReaderIdentifier(Identifier identifier, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_READER_IDENTIFIER;

        byte[] identifiers = identifier.getIdentifiers();
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, identifiers);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getReaderIdentifier(Consumer<ReaderIdentifier> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_READER_IDENTIFIER;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setRfLinkProfile(ProfileId profileId, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_RF_LINK_PROFILE;

        byte[] btArray = new byte[1];
        btArray[0] = profileId.getValue();
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getRfLinkProfile(Consumer<RfLinkProfile> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_RF_LINK_PROFILE;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo config = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(config);
    }

    @Override
    public void setE710LinkProfile(ProfileId profileId, byte drMode, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_E710_LINK_PROFILE;

        byte[] btArray = new byte[2];
        btArray[0] = profileId.getValue();
        btArray[1] = drMode;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getE710LinkProfile(Consumer<E710LinkProfile> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_E710_LINK_PROFILE;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo config = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(config);
    }

    @Override
    public void setE710_Q(QInfo qInfo, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_E710_Q;
        byte[] btArray;

        if (qInfo.qMode == QMode.FIXED) {
            btArray = new byte[4];
            btArray[0] = qInfo.qMode.getValue();
            btArray[1] = qInfo.qInit;
            btArray[2] = qInfo.qMax;
            btArray[3] = qInfo.qMin;
        } else {
            btArray = new byte[6];
            btArray[0] = qInfo.qMode.getValue();
            btArray[1] = qInfo.qInit;
            btArray[2] = qInfo.qMax;
            btArray[3] = qInfo.qMin;
            btArray[4] = qInfo.numMinQCycles;
            btArray[5] = qInfo.maxQuerySinceEPC;
        }
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getE710_Q(Consumer<QInfo> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_E710_Q;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo config = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(config);
    }

    @Override
    public void getRfPortReturnLoss(byte frequency, Consumer<RfPortReturnLoss> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_RF_PORT_RETURN_LOSS;

        byte[] btArray = new byte[1];
        btArray[0] = frequency;
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setInventoryConfig(InventoryConfig config) throws InvalidParameterException {
        Objects.requireNonNull(config);
        if (!config.checkAntennaCount(mResultProcess.getAntennaCount())) {
            throw new InvalidParameterException("The number of antennas in the fast multi-antenna inventory " +
                    "in the inventory configuration does not match the number of reader channels, " +
                    "The current reader object is " + mResultProcess.getAntennaCount().getValue() + " channels");
        }
        mResultProcess.setInventoryConfig(config);
    }

    @Override
    public void startInventory(boolean loop) {
        int interval = mResultProcess.mUserDefineFreqInterval;
        if (interval < 0) {
            getFrequencyRegion(null, null);
        }
        mResultProcess.startInventory(-1);
    }

    @Override
    public void stopInventory() {
        stopInventory(false);
    }

    @Override
    public void stopInventory(boolean isFastMode) {
        mResultProcess.stopInventory();
        if (isFastMode) {
            DataPacket packet = new DataPacket(mResultProcess.mAddress, Cmd.FAST_INVENTORY, new byte[]{0x01, 0x00});
            RequestInfo requestInfo = new RequestInfo(packet, false);
            mResultProcess.addRequest(requestInfo);
        }
    }

    @Override
    public void setMultiAntReadTagConfig(MultiAntReadTagConfig config) {
        Objects.requireNonNull(config);
        mResultProcess.setMultiAntReadTagConfig(config);
    }

    @Override
    public void startMultiAntReadTag() {
        mResultProcess.startMultiAntReadTag();
    }

    @Override
    public void stopMultiAntReadTag() {
        mResultProcess.stopMultiAntReadTag();
    }

    /**
     * stopReadTag，reset related params
     */
    @Override
    public void stopReadTag() {
        mResultProcess.reset();
    }

    @Override
    public void readTag(ReadConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.READ_TAG;

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getReadInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void readTag(CustomSessionReadConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.READ_TAG;

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getReadInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void writeTag(WriteConfig config, boolean blockWrite, Consumer<OperationTag> onSuccess, Consumer<Failure> onError) {
        byte cmd;
        if (blockWrite) {
            cmd = Cmd.BLOCK_WRITE_TAG;
        } else {
            cmd = Cmd.WRITE_TAG;
        }

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getWriteInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void lockTag(LockConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.LOCK_TAG;

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getLockInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void killTag(KillConfig config, Consumer<OperationTag> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.KILL_TAG;

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getKillInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setImpinjFastTid(FastTidType fastType, boolean save, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = (save ? Cmd.SET_AND_SAVE_IMPINJ_FAST_TID : Cmd.SET_IMPINJ_FAST_TID);

        byte[] btArray = new byte[1];
        btArray[0] = fastType.getValue();
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getImpinjFastTid(Consumer<ImpinjFastTid> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_IMPINJ_FAST_TID;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setEpcMatch(MatchConfig config, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_ACCESS_EPC_MATCH;

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getMaskInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getEpcMatch(Consumer<MatchInfo> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.GET_ACCESS_EPC_MATCH;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void clearEpcMatch(Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_ACCESS_EPC_MATCH;

        byte[] btArray = {0x01};
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setTagMask(MaskConfig config, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.OPERATE_TAG_MASK;

        Objects.requireNonNull(config);
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getMaskInfo());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getTagMask(Consumer<MaskInfo> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.OPERATE_TAG_MASK;

        byte[] btArray = {(byte) 0x20};
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void clearTagMask(ClearMaskId function, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.OPERATE_TAG_MASK;

        byte[] btArray = {function.getValue()};
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setReaderStatus(byte status, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.SET_READER_STATUS;

        byte[] btArray = {status};
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, btArray);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void getReaderStatus(Consumer<ReaderStatus> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.QUERY_READER_STATUS;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd);
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void measTempLabel2(TempLabel2Config config, Consumer<TempLabel2> onSuccess, Consumer<Failure> onError) {
        byte cmd = Cmd.TEMPERATURE_LABEL_COMMAND;

        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, config.getData());
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setTempLabel2Config(config);
        mResultProcess.setResultCallback(cmd, onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void setCommandStatusCallback(Consumer<CmdStatus> onStatus) {
        mResultProcess.setCmdStatusCallback(onStatus);
    }

    @Override
    public void setOriginalDataCallback(Consumer<byte[]> onSend, Consumer<byte[]> onReceive) {
        mResultProcess.setOriginalDataCallback(onSend, onReceive);
    }

    @Override
    public void addOriginalDataReceivedCallback(Consumer<byte[]> onReceive) {
        mResultProcess.addOriginalDataReceivedCallback(onReceive);
    }

    @Override
    public void removeOriginalDataReceivedCallback(Consumer<byte[]> onReceive) {
        mResultProcess.removeOriginalDataReceivedCallback(onReceive);
    }

    @Override
    public void setUndefinedResultCallback(Consumer<ReceiveData> onResult) {
        mResultProcess.setUndefinedResultCallback(onResult);
    }

    @Deprecated
    @Override
    public void sendCustomRequest(byte cmd, byte[] data) {
        DataPacket packet = new DataPacket(mResultProcess.mAddress, cmd, data);
        sendCustomRequest(packet);
    }

    @Override
    public void sendCustomRequest(DataPacket packet) {
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void sendCustomRequest(DataPacket packet, Consumer<Success> onSuccess, Consumer<Failure> onError) {
        RequestInfo requestInfo = new RequestInfo(packet);
        mResultProcess.setResultCallback(packet.getCmd(), onSuccess, onError);
        mResultProcess.addRequest(requestInfo);
    }

    @Override
    public void removeCallback(byte cmd) {
        try {
            mResultProcess.setResultCallback(cmd, null, null);
        } catch (Exception e) {
            LLLog.i(e.getMessage());
        }
    }
}
