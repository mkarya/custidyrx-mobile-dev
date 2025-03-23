package com.payne.reader.process;

import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.Cmd;
import com.payne.reader.bean.config.CmdStatus;
import com.payne.reader.bean.config.FastTidType;
import com.payne.reader.bean.config.Freq;
import com.payne.reader.bean.config.Header;
import com.payne.reader.bean.config.MaskAction;
import com.payne.reader.bean.config.MaskId;
import com.payne.reader.bean.config.MaskTarget;
import com.payne.reader.bean.config.MemBank;
import com.payne.reader.bean.config.ProfileId;
import com.payne.reader.bean.config.QMode;
import com.payne.reader.bean.config.Region;
import com.payne.reader.bean.config.ResultCode;
import com.payne.reader.bean.config.TagMeasOpt;
import com.payne.reader.bean.config.TempLabel2Flag;
import com.payne.reader.bean.receive.AntConnectionDetector;
import com.payne.reader.bean.receive.E710LinkProfile;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.FreqRegionResult;
import com.payne.reader.bean.receive.GpioOut;
import com.payne.reader.bean.receive.ImpinjFastTid;
import com.payne.reader.bean.receive.InventoryFailure;
import com.payne.reader.bean.receive.InventoryTag;
import com.payne.reader.bean.receive.InventoryTagEnd;
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
import com.payne.reader.bean.send.FreqNormal;
import com.payne.reader.bean.send.FreqUserDefine;
import com.payne.reader.bean.send.GpioPin;
import com.payne.reader.bean.send.InventoryConfig;
import com.payne.reader.bean.send.MultiAntReadTagConfig;
import com.payne.reader.bean.send.OutputPowerConfig;
import com.payne.reader.bean.send.TempLabel2Config;
import com.payne.reader.communication.ConnectHandle;
import com.payne.reader.communication.DataPacket;
import com.payne.reader.communication.RequestInfo;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.LLLog;
import com.payne.reader.util.ThreadPool;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The core class, the format of processing data sent and received
 *
 * @author naz
 * Date 2020/7/16
 */
final class ResultProcess {
    private final static byte SET_ANTENNA_GROUP = 0x6C;
    private final static byte GET_ANTENNA_GROUP = 0x6D;

    private AntennaCount mAntennaCount;
    /**
     * Save success callback interface
     */
    private final Map<Byte, Consumer> mSuccessMap;
    /**
     * Save failed callback interface
     */
    private final Map<Byte, Consumer> mFailureMap;
    /**
     * Reader address
     */
    public byte mAddress = (byte) 0xFF;
    /**
     * Connection handle
     */
    private ConnectHandle mConnectHandle;
    /**
     * Used to store unprocessed byte data
     */
    private byte[] mUnprocessedBytes = null;
    /**
     * LinkedBlockingQueue is used to save unsent commands
     */
    private final LinkedBlockingQueue<RequestInfo> mTransmitQueue;
    /**
     * Processing timeout, When the set timeout time>0,
     * it will judge whether the two commands are timeout before receiving the end sign,
     * and cancel the timeout mechanism when <=0
     */
    private long mTimeout;
    /**
     * Whether the command has been sent waiting for the device to return the result,
     * and the command cannot be sent during this period, otherwise it is invalid
     */
    private final AtomicBoolean mTransmitWaiting;
    /**
     * Whether to cycle inventory labels
     */
    private final AtomicBoolean mFirstFlag;

    private final AtomicBoolean mLoopReadTag;
    /**
     * Current request parameters
     */
    private volatile RequestInfo mRequestInfo;
    /**
     * Save the requested configuration before switching antenna groups
     */
    private RequestInfo mAfterSwitchGroupRequestInfo;
    /**
     * Record the current set power parameters
     */
    private OutputPowerConfig mOutputPowerConfig;
    /**
     * Record current inventory data
     */
    private InventoryConfig mInventoryConfig;

    private MultiAntReadTagConfig multiAntReadTagConfig;
    /**
     * Use {@link ScheduledExecutorService} to implement timeout mechanism
     */
    private ScheduledExecutorService mScheduler;
    /**
     * Control whether to time out through {@link ScheduledFuture}
     */
    private ScheduledFuture<?> mScheduledFuture;
    /**
     * Whether it is a user-defined spectrum
     */
    private boolean mIsUserDefineRegion = false;
    /**
     * Custom starting frequency
     */
    private int mUserDefineFreqStart;
    /**
     * Custom channel spacing
     */
    public int mUserDefineFreqInterval = -1;
    private final AtomicInteger mCheckOperateTagCount;
    /**
     * The number of tags read from the reader when operating tags
     */
    private final AtomicInteger mOperateTagCount;
    /**
     * Callback of raw data sent and received
     */
    private Consumer<byte[]> mOriginalSendCallback, mOriginalReceiveCallback;
    private List<Consumer<byte[]>> mOriginalReceiveCallbacks = new ArrayList<>();
    /**
     * Callback
     */
    private Consumer<CmdStatus> mCmdStatusCallback;
    /**
     * Callback of undefined data (new instruction)
     */
    private Consumer<ReceiveData> mUndefinedResultCallback;
    /**
     * Antenna group ID
     */
    private int mAntennaGroupId;
    /**
     * Try to set the new antenna group ID
     */
    private int mTrySetAntennaGroupId;
    /**
     * Ant start index
     */
    private int mAntStartIndex = 0;
    /**
     * Antenna Id
     */
    private int mAntennaId;
    /**
     * Try to set the new antenna ID
     */
    private int mTrySetAntennaId;
    /**
     * Record the power value of the lower eight antennas of the sixteen antenna group
     */
    private byte[] mLowEightAntennaPowers;
    /**
     * Temperature label 2 flag
     */
    private byte mTempLabel2Flag;
    /**
     * Read memory length
     */
    private int mReadMemoryLen;
    /**
     * Single temperature measurement type
     */
    private TagMeasOpt mTagMeasOpt;

    private volatile Runnable mRunnable;
    private volatile boolean mNeedCreateQueue = true;
    private volatile LinkedBlockingQueue<DataPacket> mQueue; /* DataPacket queue */

    private Runnable mTimingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTransmitWaiting.compareAndSet(true, false)) {
                LLLog.w("超时." + mRequestInfo);
                if (mRequestInfo != null && mRequestInfo.isNeedResponse()) {
                    DataPacket packet = mRequestInfo.getDataPacket();

                    byte cmd = packet.getCmd();
                    boolean resultFailure = resultFailure(cmd, ResultCode.REQUEST_TIMEOUT);
                    if (!resultFailure) {
                        addRequest(mRequestInfo);
                    }
                } else {
                    sendRequest();
                }
            } else {
                LLLog.w("超时.忽略...");
            }
        }
    };

    @SuppressWarnings("InfiniteLoopStatement")
    ResultProcess(AntennaCount antennaCount) {
        if (antennaCount == null) {
            antennaCount = AntennaCount.SINGLE_CHANNEL;
        }
        mTimeout = 3000;
        mAntennaCount = antennaCount;
        mTransmitQueue = new LinkedBlockingQueue<>(5);
        mTransmitWaiting = new AtomicBoolean(false);
        mFirstFlag = new AtomicBoolean(true);
        mLoopReadTag = new AtomicBoolean(false);
        mCheckOperateTagCount = new AtomicInteger(0);
        mOperateTagCount = new AtomicInteger(0);
        mSuccessMap = new ConcurrentHashMap<>();
        mFailureMap = new ConcurrentHashMap<>();

        if (mNeedCreateQueue) {
            mNeedCreateQueue = false;
            mQueue = new LinkedBlockingQueue<>(999);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            DataPacket packet = mQueue.take();
                            /* Analyze the data */
                            analyzeExtractData(packet);/*Queue*/
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            ThreadPool.get().execute(mRunnable);
        }
    }

    public void setAntStartFrom1() {
        mAntStartIndex = 1;
    }

    /**
     * Switch antenna number
     *
     * @param antennaCount {@link AntennaCount}
     */
    public void switchAntennaCount(AntennaCount antennaCount) {
        this.mAntennaCount = antennaCount;
    }

    /**
     * Get the number of antennas
     *
     * @return AntennaCount
     */
    public AntennaCount getAntennaCount() {
        return this.mAntennaCount;
    }

    /**
     * Connect the reader
     */
    boolean connect(ConnectHandle handle) {
        this.mConnectHandle = handle;
        this.mConnectHandle.onReceive(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) throws Exception {
                processReceive(bytes);
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) throws Exception {
                if (e instanceof SocketException) {
                    int tryConnectCount = 0;
                    boolean noConnect = false;
                    while (!noConnect) {
                        LLLog.i("try to connect---" + (++tryConnectCount));
                        Thread.sleep(1200);
                        noConnect = mConnectHandle.onConnect();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        return handle.onConnect();
    }

    /**
     * Is it connected
     *
     * @return Is it connected
     */
    boolean isConnected() {
        return mConnectHandle != null && mConnectHandle.isConnected();
    }

    /**
     * Set result callback
     *
     * @param cmd       Command code，see{@link Cmd}
     * @param onSuccess Successful result callback
     * @param onError   Failed result callback
     */
    final void setResultCallback(byte cmd, Consumer<? extends Success> onSuccess, Consumer<? extends Failure> onError) {
        if (onSuccess != null) {
            mSuccessMap.put(cmd, onSuccess);
        } else {
            mSuccessMap.remove(cmd);
        }
        if (onError != null) {
            mFailureMap.put(cmd, onError);
        } else {
            mFailureMap.remove(cmd);
        }
    }

    /**
     * Processing timeout, When the set timeout time>0,
     * * it will judge whether the two commands are timeout before receiving the end sign,
     * * and cancel the timeout mechanism when <=0
     *
     * @param timeoutMillis Timeout period, in milliseconds
     */
    final void setTimeout(long timeoutMillis) {
        this.mTimeout = timeoutMillis;
    }

    /**
     * Set the configuration of the temperature tag
     *
     * @param config set{@link TempLabel2Config}
     */
    final void setTempLabel2Config(TempLabel2Config config) {
        this.mTempLabel2Flag = config.getTempLabel2Flag();
        this.mReadMemoryLen = config.getReadMemoryLen();
        this.mTagMeasOpt = config.getTagMeasOpt();
    }

    final void setFreqNormal() {
        this.mIsUserDefineRegion = false;
    }

    final void setFreqUserDefine(int userDefineFreqStart, int userDefineFreqInterval) {
        this.mIsUserDefineRegion = true;
        this.mUserDefineFreqStart = userDefineFreqStart;
        this.mUserDefineFreqInterval = userDefineFreqInterval;
    }

    /**
     * Configure power parameters
     *
     * @param config Power configuration, reference{@link OutputPowerConfig}
     */
    void setOutputPower(OutputPowerConfig config) {
        this.mOutputPowerConfig = config;
        DataPacket packet = new DataPacket(mAddress, Cmd.SET_OUTPUT_POWER, config.getPowers());
        RequestInfo requestInfo = new RequestInfo(packet);
        if (mAntennaGroupId != 0) {
            setAntennaGroup(0, requestInfo);
        } else {
            addRequest(requestInfo);
        }
    }

    /**
     * Configure inventory parameters
     *
     * @param config Inventory configuration, see{@link InventoryConfig}
     */
    void setInventoryConfig(InventoryConfig config) {
        this.mInventoryConfig = config;
    }

    public InventoryConfig getInventoryConfig() {
        return mInventoryConfig;
    }

    /**
     * Start inventory
     *
     * @param loopCount loop Count
     */
    void startInventory(int loopCount) {
        if (mInventoryConfig == null) {
            throw new RuntimeException("Please call setInventoryConfig first");
        }

        byte cmd = mInventoryConfig.getCmd();
        if (cmd == Cmd.FAST_SWITCH_ANT_INVENTORY && mAntennaCount == AntennaCount.SIXTEEN_CHANNELS) {
            if (mFirstFlag.get()) {
                doNextFastSwitchAnt(0);
            } else {
                int newAntennaGroupId = (++mAntennaGroupId) % 2;
                doNextFastSwitchAnt(newAntennaGroupId);
            }
        } else {
            DataPacket packet = new DataPacket(mAddress, cmd, mInventoryConfig.getInventoryParams());
            addRequest(new RequestInfo(packet));
        }
        mFirstFlag.set(false);
    }

    private void doNextFastSwitchAnt(int newAntennaGroupId) {
        RequestInfo requestInfo = null;
        if (newAntennaGroupId == 0) {
            byte cmd = mInventoryConfig.getCmd();
            byte[] params = mInventoryConfig.getInventoryParams();
            DataPacket packet = new DataPacket(mAddress, cmd, params);
            requestInfo = new RequestInfo(packet);
        } else {
            byte cmd = mInventoryConfig.getCmd();
            byte[] params = mInventoryConfig.getHighEightAntennaInventoryParams();
            if (params == null || params.length == 0) {
                LLLog.w("HighPrams is null");
            }
            DataPacket packet = new DataPacket(mAddress, cmd, params);
            requestInfo = new RequestInfo(packet);
        }
        setAntennaGroup(newAntennaGroupId, requestInfo); /* 16 antenna switch antenna group */
    }

    /**
     * Stop inventory
     */
    void stopInventory() {
        mFirstFlag.set(true);
        cancelTiming();/*stopInventory*/
        mLoopReadTag.set(false);
        mTransmitQueue.clear();
        mTransmitWaiting.set(false);
    }

    void setMultiAntReadTagConfig(MultiAntReadTagConfig config) {
        this.multiAntReadTagConfig = config;
    }

    void startMultiAntReadTag() {
        if (multiAntReadTagConfig == null) {
            throw new RuntimeException("Please call setMultiAntReadTagConfig first");
        }
        this.mLoopReadTag.set(true);
        byte cmd = Cmd.READ_TAG;
        DataPacket packet;
        if (multiAntReadTagConfig.isReadConfigFlag()) {
            packet = new DataPacket(mAddress, cmd, multiAntReadTagConfig.getCustomReadConfig());
        } else {
            packet = new DataPacket(mAddress, cmd, multiAntReadTagConfig.getSingleReadConfig());
        }
        RequestInfo requestInfo = new RequestInfo(packet);
        addRequest(requestInfo);
    }

    void stopMultiAntReadTag() {
        stopInventory();
    }

    /**
     * Set up antenna group
     *
     * @param groupId          0x00:Set up the first group of eight to work 0x01:Set up a
     *                         second group of eight to work
     * @param afterRequestInfo What to do after setting up the antenna group
     */
    void setAntennaGroup(int groupId, RequestInfo afterRequestInfo) {
        this.mTrySetAntennaGroupId = groupId;
        this.mAfterSwitchGroupRequestInfo = afterRequestInfo;

        byte[] btAryData = new byte[1];
        btAryData[0] = (byte) groupId;

        DataPacket packet = new DataPacket(mAddress, SET_ANTENNA_GROUP, btAryData);
        RequestInfo requestInfo = new RequestInfo(packet);
        addRequest(requestInfo);
    }

    /**
     * Try to modify the antenna id
     *
     * @param tryAntennaId Antenna Id
     */
    void setTryAntennaId(int tryAntennaId) {
        this.mTrySetAntennaId = tryAntennaId;
    }

    /**
     * Get antenna group
     *
     * @param afterRequestInfo What to do after getting the antenna group
     */
    void getAntennaGroup(RequestInfo afterRequestInfo) {
        this.mAfterSwitchGroupRequestInfo = afterRequestInfo;

        DataPacket packet = new DataPacket(mAddress, GET_ANTENNA_GROUP);
        RequestInfo requestInfo = new RequestInfo(packet);
        addRequest(requestInfo);
    }

    /**
     * Get current antenna group
     *
     * @return AntennaGroupId
     */
    int getAntennaGroupId() {
        return mAntennaGroupId;
    }

    int getCacheAntennaId() {
        return mAntennaId;
    }

    /**
     * Add sending request to the request queue
     *
     * @param requestInfo Requested configuration parameter class
     */
    void addRequest(RequestInfo requestInfo) {
        addRequest(requestInfo, null);
    }

    public void addRequest(RequestInfo requestInfo, Consumer<Throwable> onError) {
        if (LLLog.isDebug()) {
            LLLog.i("addRequest.requestInfo:" + requestInfo + "--->" + mTransmitQueue.size());
        }
        try {
            if (requestInfo == null) {
                if (onError != null) {
                    onError.accept(new RuntimeException("RequestConfig is null"));
                }
                return;
            }
            if (mConnectHandle == null) {
                if (onError != null) {
                    onError.accept(new RuntimeException("Please connect the reader first"));
                }
                return;
            }
        } catch (Exception e) {
            LLLog.w(e.getMessage());
            return;
        }
        // The command has been sent to wait for the result.
        // During this time, the new request is saved to the sending queue
        boolean success = mTransmitQueue.offer(requestInfo);
        if (!success) {
            DataPacket packet = requestInfo.getDataPacket();
            byte cmd = packet.getCmd();
            resultFailure(cmd, ResultCode.REQUEST_INVALID);
        }
        // Try to pull the request from the request queue and send it to the reader
        sendRequest();
    }

    /**
     * Try to pull the request from the request queue and send it to the reader
     */
    private void sendRequest() {
        if (mTransmitQueue.size() == 0) {
            if (LLLog.isDebug()) LLLog.i("忽略.队列无请求");
            return;
        }
        if (mTransmitWaiting.compareAndSet(false, true)) {
            RequestInfo pollNext = mTransmitQueue.poll();

            if (LLLog.isDebug()) {
                LLLog.i("mTransmitQueue.size:" + mTransmitQueue.size() + ", pollNext:" + pollNext);
            }

            mRequestInfo = pollNext;
            DataPacket packet = mRequestInfo.getDataPacket();

            if (mOriginalSendCallback != null) {
                ThreadPool.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mOriginalSendCallback.accept(packet.getData());
                        } catch (Exception e) {
                            LLLog.i(e.getMessage());
                        }
                    }
                });
            }
            if (mConnectHandle != null) {
                ThreadPool.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        mConnectHandle.onSend(packet.getData());
                    }
                });
            }
            startTiming(); /* Start timing (implement request timeout mechanism) */
        } else {
            LLLog.i("忽略.等待ing...");
        }
    }

    /**
     * Process the received data and perform subcontracting according to the protocol
     *
     * @param bytes source data
     */
    int minOnePacketDataLen = 5; // The minimum length of a packet of data

    private void processReceive(byte[] bytes) {
        // ---------------------------------------------------------
        // | header | length | address |  cmd  | [data] | checksum |
        // |  0xA0  |  0x04  |   0xFF  |  0x74 |  0x00  |   0xE9   |
        // ---------------------------------------------------------
        boolean showLog = LLLog.isDebug();
//        if (showLog) {
//            LLLog.i("readBytes---->\n" + ArrayUtils.bytesToHexString(bytes, 0, bytes.length));
//        }
        byte[] mergeBytes = bytes;
        if (mUnprocessedBytes != null) {
            mergeBytes = ArrayUtils.mergeBytes(mUnprocessedBytes, bytes);
        }

//        if (showLog) {
//            LLLog.i("mergeBytes->\n" + ArrayUtils.bytesToHexString(mergeBytes, 0, mergeBytes.length));
//        }

        int nowTimesStartIndex = 0;
        int index = 0;
        while (index < mergeBytes.length) {
            if (mergeBytes[index] != Header.HEADER_A0) {
                index++;
                continue;
            }
            // Data length is not enough
            if (index + minOnePacketDataLen > mergeBytes.length) {
                saveUnprocessedData(mergeBytes, nowTimesStartIndex);
                return;
            }

            int lenIndex = index + 1;
            if (lenIndex >= mergeBytes.length) {
                saveUnprocessedData(mergeBytes, nowTimesStartIndex);
                return;
            }

            int dataLen = mergeBytes[lenIndex] & 0xFF;
            int onePacketDataLen = dataLen + 2;
            if (index + onePacketDataLen > mergeBytes.length) {
                if (mergeBytes[lenIndex] == Header.HEADER_A0) {
                    index++;
                    continue;
                }
                saveUnprocessedData(mergeBytes, nowTimesStartIndex);
                return;
            }
            byte[] extractData = new byte[onePacketDataLen];
            System.arraycopy(mergeBytes, index, extractData, 0, onePacketDataLen);

            DataPacket packet;
            try {
                packet = DataPacket.parse(extractData);
            } catch (Throwable e) {
                if (showLog) {
                    LLLog.w("errorBytes.msg->" + e.getMessage() +
                            "\nerrorBytes.arr->" + ArrayUtils.bytesToHexString(extractData, 0, extractData.length));
                }
                index++;
                continue;
            }

            int numLen = index - nowTimesStartIndex;
            if (numLen > 0) {
                byte[] strArr = new byte[numLen];
                System.arraycopy(mergeBytes, nowTimesStartIndex, strArr, 0, numLen);

                ThreadPool.get().execute(() -> {
                    for (Consumer<byte[]> consumer : mOriginalReceiveCallbacks) {
                        try {
                            consumer.onUnknownArr(strArr);
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
            if (mQueue == null) {
                analyzeExtractData(packet);/*direct*/
            } else {
                if (showLog) {
                    LLLog.i("mQueue.ready put:" + mQueue.size());
                }
                try {
                    mQueue.put(packet);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
//                System.out.println("mQueue.put ok." + mQueue.size());
            }

            nowTimesStartIndex = index += onePacketDataLen;
        }

        // if (!foundA0) {
        //     LLLog.i("found no header 0xA0...");
        // }
        if (nowTimesStartIndex < mergeBytes.length) {
            saveUnprocessedData(mergeBytes, nowTimesStartIndex);
        } else {
            mUnprocessedBytes = null;
        }
    }

    /**
     * Save unprocessed data for next processing
     *
     * @param mergeBytes source data
     * @param index      Unprocessed data index
     */
    private void saveUnprocessedData(byte[] mergeBytes, int index) {
        if (index > 0) {
            // The data has been extracted. Use the new array to save the unprocessed data for next use.
            mUnprocessedBytes = new byte[mergeBytes.length - index];
            System.arraycopy(mergeBytes, index, mUnprocessedBytes, 0, mUnprocessedBytes.length);
        } else {
            mUnprocessedBytes = mergeBytes;
        }
        if (LLLog.isDebug()) {
            LLLog.i("saveUnprocessedData:" + ArrayUtils.bytesToHexString(mUnprocessedBytes, 0, mUnprocessedBytes.length));
        }
    }

    /**
     * Analyze and extract received data
     *
     * @param protocol data
     */
    private void analyzeExtractData(DataPacket protocol) {
        byte[] bytes = protocol.getData();
        //Save the reader address
        this.mAddress = protocol.getAddress();
        byte requestCmd = 0x00;
        if (mRequestInfo != null) {
            DataPacket packet = mRequestInfo.getDataPacket();
            requestCmd = packet.getCmd();
        }

        //<editor-fold desc="callback">
        if (mOriginalReceiveCallback != null) {
            ThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mOriginalReceiveCallback.accept(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        for (Consumer<byte[]> consumer : mOriginalReceiveCallbacks) {
                            consumer.accept(bytes);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //</editor-fold>

        //Analyze the data and perform corresponding processing according to the protocol
        analyzeData(protocol);
        // Check whether the command has been executed (the sent CMD is the same as the received CMD)
        checkRequestAccepted(requestCmd, protocol);
    }

    /**
     * First check whether the received data is the data returned by the device after sending this request.
     * If yes, check whether the data received after sending this request is complete.
     * If the reception is completed, close the timeout mechanism and change the sending status to complete,
     * And send the next request
     *
     * @param requestCmd    Cmd to send the requested data, see{@link Cmd}
     * @param receivePacket Packet received
     */
    private void checkRequestAccepted(byte requestCmd, DataPacket receivePacket) {
        boolean showLog = LLLog.isDebug();

        byte receiveCmd = receivePacket.getCmd();//Cmd of the received data
        if (requestCmd == receiveCmd) {
            cancelTiming();// Cancel this timeout mechanism
            boolean isReceived;// Is the result of this request completed?
            if (receiveCmd == Cmd.FAST_SWITCH_ANT_INVENTORY) {
                int len = receivePacket.getCoreDataLen();
                isReceived = len == 7;
            } else if (receiveCmd == Cmd.CUSTOMIZED_SESSION_TARGET_INVENTORY) {
                int len = receivePacket.getCoreDataLen();
                isReceived = len <= 1 || len == 7;
            } else if (receiveCmd == Cmd.READ_TAG
                    || receiveCmd == Cmd.WRITE_TAG
                    || receiveCmd == Cmd.BLOCK_WRITE_TAG
                    || receiveCmd == Cmd.LOCK_TAG
                    || receiveCmd == Cmd.KILL_TAG) {
                int len = receivePacket.getCoreDataLen();
                if (len == 1) {
                    isReceived = true;
                } else {
                    try {
                        byte[] data = receivePacket.getData();
                        int fromIndex = DataPacket.fromIndex();
                        int tagCount = ((data[fromIndex] & 0xFF) << 8) + (data[fromIndex + 1] & 0xFF);
                        mCheckOperateTagCount.incrementAndGet();/* Increase the number of received data */
                        isReceived = mCheckOperateTagCount.compareAndSet(tagCount, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isReceived = false;
                    }
                }
            } else if (requestCmd == Cmd.FAST_INVENTORY) {
                if (showLog) {
                    LLLog.i("快速模式.不处理接收状态");
                }
                return;
            } else {
                isReceived = true;
            }

            checkLoopReadTag(requestCmd, isReceived); /* Check whether to recycle ReadTag */

            if (isReceived) {
                // Try to pull the request from the request queue and send it to the reader
                if (showLog) {
                    LLLog.i("requestCmd:" + Cmd.getNameForCmd(requestCmd) + " isReceived OK");
                }

                mTransmitWaiting.set(false);
                sendRequest();/*checkRequestAccepted is ok,to resume*/
            } else {
                // Start timing (implement request timeout mechanism)
                startTiming();
            }
        } else {
            if (showLog) {
                LLLog.w("send cmd:" + String.format("%02X", requestCmd) + ", receive cmd:" + String.format("%02X", receiveCmd));
            }
        }
    }

    private void checkLoopReadTag(byte cmd, boolean isReceived) {
        //Whether to continue the loop
        if (isReceived && mLoopReadTag.get() && multiAntReadTagConfig != null) {
            if (cmd == Cmd.READ_TAG) {
                if (mAntennaCount == AntennaCount.SIXTEEN_CHANNELS) {
                    if (mAntennaGroupId == 0) {
                        if (mAntennaId == 7) {
                            mTrySetAntennaId = 0;
                            byte[] bArry = new byte[1];
                            bArry[0] = (byte) mTrySetAntennaId;
                            DataPacket packet = new DataPacket(mAddress, Cmd.SET_WORK_ANTENNA, bArry);
                            RequestInfo requestInfo = new RequestInfo(packet);
                            setAntennaGroup(1, requestInfo);
                        } else {
                            int tmpAntId = mAntennaId;
                            tmpAntId++;
                            mTrySetAntennaId = tmpAntId;
                            byte[] bArry = new byte[1];
                            bArry[0] = (byte) mTrySetAntennaId;
                            DataPacket packet = new DataPacket(mAddress, Cmd.SET_WORK_ANTENNA, bArry);
                            RequestInfo requestInfo = new RequestInfo(packet);
                            addRequest(requestInfo);
                        }
                    } else {
                        if (mAntennaId == 7) {
                            mTrySetAntennaId = 0;
                            byte[] bArry = new byte[1];
                            bArry[0] = (byte) mTrySetAntennaId;
                            DataPacket packet = new DataPacket(mAddress, Cmd.SET_WORK_ANTENNA, bArry);
                            RequestInfo requestInfo = new RequestInfo(packet);
                            setAntennaGroup(0, requestInfo);
                        } else {
                            int tmpAntId = mAntennaId;
                            tmpAntId++;
                            mTrySetAntennaId = tmpAntId;
                            byte[] bArry = new byte[1];
                            bArry[0] = (byte) mTrySetAntennaId;
                            DataPacket packet = new DataPacket(mAddress, Cmd.SET_WORK_ANTENNA, bArry);
                            RequestInfo requestInfo = new RequestInfo(packet);
                            addRequest(requestInfo);
                        }
                    }
                } else {
                    LLLog.i("--------   checkLoopReadTag    Cmd.READ_TAG   ---------");
                    int tmpAntId = mAntennaId;
                    tmpAntId++;
                    if (tmpAntId >= mAntennaCount.getValue()) {
                        mTrySetAntennaId = 0;
                    } else {
                        mTrySetAntennaId = tmpAntId;
                    }
                    byte[] bArry = new byte[1];
                    bArry[0] = (byte) mTrySetAntennaId;
                    DataPacket packet = new DataPacket(mAddress, Cmd.SET_WORK_ANTENNA, bArry);
                    RequestInfo requestInfo = new RequestInfo(packet);
                    if (mAntennaGroupId == 1) {
                        setAntennaGroup(0, requestInfo);
                    } else {
                        addRequest(requestInfo);
                    }
                }
            } else if (cmd == Cmd.SET_WORK_ANTENNA) {
                LLLog.i("--------   checkLoopReadTag    Cmd.SET_WORK_ANTENNA ---------");
                DataPacket packet;
                if (multiAntReadTagConfig.isReadConfigFlag()) {
                    packet = new DataPacket(mAddress, Cmd.READ_TAG, multiAntReadTagConfig.getCustomReadConfig());
                } else {
                    packet = new DataPacket(mAddress, Cmd.READ_TAG, multiAntReadTagConfig.getSingleReadConfig());
                }
                RequestInfo requestInfo = new RequestInfo(packet);
                addRequest(requestInfo);
            }
        }
    }

    /**
     * Analyze the data and perform corresponding processing according to the protocol
     *
     * @param protocol Data extracted from the received data according to the protocol
     */
    protected void analyzeData(DataPacket protocol) {
        switch (protocol.getCmd()) {
            case Cmd.RESET:
                processReset(protocol);
                break;
            case Cmd.GET_IS_GB:
                processGet(protocol);
                break;
            case Cmd.SET_GB:
                processSet(protocol);
                break;
            case Cmd.SET_SERIAL_PORT_BAUD_RATE:
                processSet(protocol);
                break;
            case Cmd.SET_E710_LINK_PROFILE: {
                int dataLen = protocol.getCoreDataLen();
                if (dataLen == 0x01) {
                    processSet(protocol);
                } else {
                    processUndefined(protocol);/*指令重合*/
                }
            }
            break;
            case Cmd.GET_FIRMWARE_VERSION:
                processGetFirmwareVersion(protocol);
                break;
            case Cmd.SET_READER_ADDRESS:
                processSetReaderAddress(protocol);
                break;
            case Cmd.SET_WORK_ANTENNA:
                processSetWorkAntenna(protocol);
                break;
            case Cmd.GET_WORK_ANTENNA:
                processGetWorkAntenna(protocol);
                break;
            case Cmd.SET_OUTPUT_POWER:
                processSetOutputPower(protocol);
                break;
            case Cmd.GET_OUTPUT_POWER:
            case Cmd.GET_OUTPUT_POWER_EIGHT:
                processGetOutputPower(protocol);
                break;
            case Cmd.SET_FREQUENCY_REGION:
                processSetFrequencyRegion(protocol);
                break;
            case Cmd.GET_FREQUENCY_REGION:
                processGetFrequencyRegion(protocol);
                break;
            case Cmd.SET_BEEPER_MODE:
                processSetBeeperMode(protocol);
                break;
            case Cmd.GET_READER_TEMPERATURE:
                processGetReaderTemperature(protocol);
                break;
            case Cmd.READ_GPIO_VALUE:
                processReadGpioValue(protocol);
                break;
            case Cmd.WRITE_GPIO_VALUE:
                processWriteGpioValue(protocol);
                break;
            case Cmd.SET_ANT_CONNECTION_DETECTOR:
                processSetAntConnectionDetector(protocol);
                break;
            case Cmd.GET_ANT_CONNECTION_DETECTOR:
                processGetAntConnectionDetector(protocol);
                break;
            case Cmd.SET_TEMPORARY_OUTPUT_POWER:
                processSetTemporaryOutputPower(protocol);
                break;
            case Cmd.SET_READER_IDENTIFIER:
                processSetReaderIdentifier(protocol);
                break;
            case Cmd.GET_READER_IDENTIFIER:
                processGetReaderIdentifier(protocol);
                break;
            case Cmd.SET_RF_LINK_PROFILE:
                processSetRfLinkProfile(protocol);
                break;
            case Cmd.GET_RF_LINK_PROFILE:
                processGetRfLinkProfile(protocol);
                break;
            case Cmd.GET_E710_LINK_PROFILE: {
                int dataLen = protocol.getCoreDataLen();
                if (dataLen == 0x02) {
                    processGetE710LinkProfile(protocol);
                } else {
                    processUndefined(protocol);/*指令重合*/
                }
            }
            break;
            case Cmd.SET_E710_Q: {
                int dataLen = protocol.getCoreDataLen();
                if (dataLen == 0x01) {
                    processSetE710Q(protocol);
                } else {
                    processUndefined(protocol);/*指令重合*/
                }
            }
            break;
            case Cmd.GET_E710_Q:
                int dataLen = protocol.getCoreDataLen();
                if (dataLen == 0x04 || dataLen == 0x06) {
                    processGetE710Q(protocol);
                } else {
                    processUndefined(protocol);/*指令重合*/
                }
                break;
            case Cmd.GET_RF_PORT_RETURN_LOSS:
                processGetRfPortReturnLoss(protocol);
                break;
            case SET_ANTENNA_GROUP:
                processSetAntennaGroup(protocol);
                break;
            case GET_ANTENNA_GROUP:
                processGetAntennaGroup(protocol);
                break;
            case Cmd.INVENTORY:
                processInventory(protocol);
                break;
            case Cmd.FAST_SWITCH_ANT_INVENTORY:
                processFastSwitchInventory(protocol);
                break;
            case Cmd.CUSTOMIZED_SESSION_TARGET_INVENTORY:
            case Cmd.FAST_INVENTORY:
                processCustomizedSessionTargetInventory(protocol);
                break;
            case Cmd.READ_TAG:
                processReadTag(protocol);
                break;
            case Cmd.WRITE_TAG:
                processWriteTag(protocol);
                break;
            case Cmd.LOCK_TAG:
                processLockTag(protocol);
                break;
            case Cmd.KILL_TAG:
                processKillTag(protocol);
                break;
            case Cmd.SET_IMPINJ_FAST_TID:
                processSetImpinjFastTid(protocol);
                break;
            case Cmd.SET_AND_SAVE_IMPINJ_FAST_TID:
                processSetAndSaveImpinjFastTid(protocol);
                break;
            case Cmd.GET_IMPINJ_FAST_TID:
                processGetImpinjFastTid(protocol);
                break;
            case Cmd.BLOCK_WRITE_TAG:
                processBlockTag(protocol);
                break;
            case Cmd.SET_ACCESS_EPC_MATCH:
                processSetAccessEpcMatch(protocol);
                break;
            case Cmd.GET_ACCESS_EPC_MATCH:
                processGetAccessEpcMatch(protocol);
                break;
            case Cmd.OPERATE_TAG_MASK:
                processTagMask(protocol);
                break;
            case Cmd.QUERY_READER_STATUS:
                processQueryReaderStatus(protocol);
                break;
            case Cmd.SET_READER_STATUS:
                processSetReaderStatus(protocol);
                break;
            case Cmd.TEMPERATURE_LABEL_COMMAND:
                processTempLabel2Command(protocol);
                break;
            default:
                processUndefined(protocol);
        }
    }

    private void processGet(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        byte[] data = protocol.getData();
        int dataLen = protocol.getCoreDataLen();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen > 0) {
            ReceiveData receiveData = new ReceiveData();
            receiveData.setCmd(cmd);

            byte[] resData = new byte[dataLen];
            System.arraycopy(data, fromIndex, resData, 0, dataLen);
            receiveData.setData(resData);
            resultSuccess(cmd, receiveData);
            return;
        }
        resultFailure(cmd, ResultCode.FAIL);
    }

    private void processReset(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        // The device has a return result code
        boolean hasResultCode = protocol.getCoreDataLen() == 0x01;
        byte resultCode = protocol.getData()[DataPacket.fromIndex()];

        if (hasResultCode) {
            resultFailure(cmd, resultCode);
        }
    }

    /**
     * Parse all feedback of set command.
     *
     * @param protocol {@link DataPacket}
     */
    private boolean processSet(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        // The device has a return result code
        boolean hasResultCode = protocol.getCoreDataLen() == 0x01;
        byte resultCode = protocol.getData()[DataPacket.fromIndex()];

        if (hasResultCode) {
            if (resultCode == ResultCode.SUCCESS) {
                if (cmd == Cmd.SET_WORK_ANTENNA) {
                    mAntennaId = mTrySetAntennaId;
                }
                Success success = new Success();
                success.setCmd(cmd);
                resultSuccess(cmd, success);
                return true;
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
        return false;
    }

    /**
     * Process the data packet to get the version number
     *
     * @param protocol Protocol packet
     */
    private void processGetFirmwareVersion(DataPacket protocol) {
        int dataLen = protocol.getCoreDataLen();
        byte cmd = protocol.getCmd();

        byte[] data = protocol.getData();
        if (dataLen == 0x02) {
            Version.ChipType chipType = null;

            int fromIndex = DataPacket.fromIndex();

            byte majorByte = data[fromIndex];
            int hv = majorByte >> 5;
            int type = (hv & 0x07);

            switch (type) {
                default:
                case 0x0: {
                    chipType = Version.ChipType.R2000;
                }
                break;
                case 0x1: {/*暂时没有*/
                    // chipType = Version.ChipType.IBAT2000;
                }
                break;
                case 0x2: {
                    chipType = Version.ChipType.E710;
                }
                break;
                case 0x3: {
                    chipType = Version.ChipType.TM670;
                }
                break;
                case 0x4: {
                    chipType = Version.ChipType.FDW;
                }
                break;
            }

            int major = majorByte & 0x1F;
            int minor = data[fromIndex + 1] & 0xFF;
            String strVersion = major + "." + minor;

            Version fVersion = new Version();
            fVersion.setCmd(cmd);
            fVersion.setVersion(strVersion);
            fVersion.setChipType(chipType);
            resultSuccess(cmd, fVersion);
        } else if (dataLen == 0x01) {
            byte resultCode = data[DataPacket.fromIndex()];
            resultFailure(cmd, resultCode);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetReaderAddress(DataPacket protocol) {
        processSet(protocol);
    }

    private void processSetWorkAntenna(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetWorkAntenna(DataPacket protocol) {
        int dataLen = protocol.getCoreDataLen();
        byte cmd = protocol.getCmd();

        if (dataLen == 0x01) {
            byte resultCode = protocol.getData()[DataPacket.fromIndex()];
            if (resultCode == 0x00 || resultCode == 0x01 || resultCode == 0x02 || resultCode == 0x03) {
                mAntennaId = (resultCode & 0xFF) + (mAntennaGroupId << 3);
                WorkAntenna antenna = new WorkAntenna();
                antenna.setCmd(cmd);
                antenna.setWorkAntenna(mAntennaId);
                resultSuccess(cmd, antenna);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetOutputPower(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        // The device has a return result code
        boolean hasResultCode = protocol.getCoreDataLen() == 0x01;
        byte resultCode = protocol.getData()[DataPacket.fromIndex()];

        if (hasResultCode) {
            if (resultCode == ResultCode.SUCCESS) {
                if (mAntennaGroupId == 0 && mAntennaCount == AntennaCount.SIXTEEN_CHANNELS) {
                    //Sixteen antennas high eight antennas
                    if (mOutputPowerConfig != null) {
                        DataPacket packet = new DataPacket(mAddress, Cmd.SET_OUTPUT_POWER, mOutputPowerConfig.getHighEightAntennaPowers());
                        RequestInfo requestInfo = new RequestInfo(packet);
                        setAntennaGroup(1, requestInfo);
                    }
                } else {
                    Success success = new Success();
                    success.setCmd(cmd);
                    resultSuccess(cmd, success);
                }
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processGetOutputPower(DataPacket protocol) {
        int dataLen = protocol.getCoreDataLen();
        byte cmd = protocol.getCmd();

        if (dataLen == 0x01 || dataLen == 0x04) {
            byte[] outputPowers = new byte[dataLen];
            System.arraycopy(protocol.getData(), DataPacket.fromIndex(), outputPowers, 0, dataLen);
            OutputPower power = new OutputPower();
            power.setCmd(cmd);
            power.setOutputPower(outputPowers);
            resultSuccess(cmd, power);
        } else if (dataLen == 0x08) {
            byte[] outputPowers = new byte[dataLen];
            System.arraycopy(protocol.getData(), DataPacket.fromIndex(), outputPowers, 0, dataLen);
            if (mAntennaCount == AntennaCount.SIXTEEN_CHANNELS && mAntennaGroupId == 0) {
                mLowEightAntennaPowers = outputPowers;
                setAntennaGroup(1, mRequestInfo);
            } else {
                OutputPower power = new OutputPower();
                power.setCmd(cmd);
                if (mLowEightAntennaPowers != null) {
                    byte[] newPowers = new byte[mLowEightAntennaPowers.length + outputPowers.length];
                    System.arraycopy(mLowEightAntennaPowers, 0, newPowers, 0, mLowEightAntennaPowers.length);
                    System.arraycopy(outputPowers, 0, newPowers, mLowEightAntennaPowers.length, outputPowers.length);
                    power.setOutputPower(newPowers);
                    mLowEightAntennaPowers = null;
                } else {
                    power.setOutputPower(outputPowers);
                }
                resultSuccess(cmd, power);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetFrequencyRegion(DataPacket protocol) {
        boolean success = processSet(protocol);
        if (!success) {
            mUserDefineFreqInterval = -1;
        }
    }

    private void processGetFrequencyRegion(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x03) {
            mIsUserDefineRegion = false;
            mUserDefineFreqInterval = 0;
            // Default frequency range parameters
            FreqNormal freqNormal = new FreqNormal.Builder()
                    .setRegion(Region.valueOf(data[fromIndex]))
                    .setFreqStart(Freq.valueOf(data[fromIndex + 1]))
                    .setFreqEnd(Freq.valueOf(data[fromIndex + 2]))
                    .build();
            // Result function
            FreqRegionResult freqRegionResult = new FreqRegionResult();
            freqRegionResult.setCmd(cmd);
            freqRegionResult.setUserDefine(false);
            freqRegionResult.setFreqNormal(freqNormal);
            resultSuccess(cmd, freqRegionResult);
        } else if (dataLen == 0x06) {
            mIsUserDefineRegion = true;
            mUserDefineFreqInterval = (data[fromIndex + 1] & 0xFF) * 10;
            mUserDefineFreqStart = ((data[fromIndex + 3] & 0xFF) << 16) +
                    ((data[fromIndex + 4] & 0xFF) << 8) +
                    (data[fromIndex + 5] & 0xFF);
            // User-defined frequency range
            FreqUserDefine freqUserDefine = new FreqUserDefine.Builder()
                    .setFreqStart(mUserDefineFreqStart)
                    .setFreqInterval(mUserDefineFreqInterval)
                    .setFreqQuantity(data[fromIndex + 2])
                    .build();
            // Result function
            FreqRegionResult freqRegionResult = new FreqRegionResult();
            freqRegionResult.setCmd(cmd);
            freqRegionResult.setUserDefine(true);
            freqRegionResult.setFreqUserDefine(freqUserDefine);
            resultSuccess(cmd, freqRegionResult);
        } else if (dataLen == 0x01) {
            resultFailure(cmd, data[fromIndex]);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetBeeperMode(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetReaderTemperature(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x02) {
            //Whether the temperature is above zero degrees Celsius
            boolean isAboveZero = data[fromIndex] == (byte) 0x01;
            int temperature = data[fromIndex + 1];
            if (!isAboveZero) {
                temperature = -temperature;
            }
            ReaderTemperature readerTemperature = new ReaderTemperature();
            readerTemperature.setCmd(cmd);
            readerTemperature.setTemperature(temperature);
            resultSuccess(cmd, readerTemperature);
        } else if (dataLen == 0x01) {
            byte resultCode = data[fromIndex];
            resultFailure(cmd, resultCode);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processReadGpioValue(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x02) {
            //Gpio1
            GpioPin pin1 = new GpioPin();
            pin1.setId(1);
            pin1.setHigh(data[fromIndex] == (byte) 0x01);
            pin1.setOutput(true);
            //Gpio2
            GpioPin pin2 = new GpioPin();
            pin2.setId(2);
            pin2.setHigh(data[fromIndex + 1] == (byte) 0x01);
            pin2.setOutput(true);
            //GpioOut
            GpioOut gpioOut = new GpioOut();
            gpioOut.setCmd(cmd);
            gpioOut.setGpios(new GpioPin[]{pin1, pin2});
            resultSuccess(cmd, gpioOut);
        } else if (dataLen == 0x01) {
            byte resultCode = data[fromIndex];
            resultFailure(cmd, resultCode);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processWriteGpioValue(DataPacket protocol) {
        processSet(protocol);
    }

    private void processSetAntConnectionDetector(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetAntConnectionDetector(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();

        if (dataLen == 0x01) {
            byte antValue = protocol.getData()[DataPacket.fromIndex()];
            AntConnectionDetector detector = new AntConnectionDetector();
            detector.setCmd(cmd);
            detector.setClose(antValue == 0x00);
            detector.setAntDetector(antValue);
            resultSuccess(cmd, detector);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetTemporaryOutputPower(DataPacket protocol) {
        processSetOutputPower(protocol);
    }

    private void processSetReaderIdentifier(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetReaderIdentifier(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x0C) {
            byte[] identifier = new byte[12];
            System.arraycopy(data, fromIndex, identifier, 0, dataLen);
            String strIdentifier = ArrayUtils.bytesToHexString(identifier, 0, identifier.length);
            ReaderIdentifier readerIdentifier = new ReaderIdentifier();
            readerIdentifier.setCmd(cmd);
            readerIdentifier.setIdentifier(strIdentifier);
            resultSuccess(cmd, readerIdentifier);
        } else if (dataLen == 0x01) {
            byte resultCode = data[fromIndex];
            resultFailure(cmd, resultCode);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetRfLinkProfile(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetRfLinkProfile(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();

        if (dataLen == 0x01) {
            byte[] data = protocol.getData();
            int fromIndex = DataPacket.fromIndex();
            byte resultCode = data[fromIndex];
            ProfileId profileId = ProfileId.valueOf(resultCode);
            if (profileId != ProfileId.UNKNOWN) {
                RfLinkProfile profile = new RfLinkProfile();
                profile.setCmd(cmd);
                profile.setLinkProfile(profileId);
                resultSuccess(cmd, profile);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processGetE710LinkProfile(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();

        if (dataLen == 0x02) {
            byte[] data = protocol.getData();
            int fromIndex = DataPacket.fromIndex();
            byte resultCode = data[fromIndex];
            byte drMode = data[fromIndex + 1];
            ProfileId profileId = ProfileId.valueOf(resultCode);
            if (profileId != ProfileId.UNKNOWN) {
                E710LinkProfile profile = new E710LinkProfile();
                profile.setCmd(cmd);
                profile.setLinkProfile(profileId);
                profile.setDrMode(drMode);
                resultSuccess(cmd, profile);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private boolean processSetE710Q(DataPacket protocol) {
        return processSet(protocol);
    }

    private void processGetE710Q(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        if (dataLen == 0x04) {
            byte[] data = protocol.getData();
            int fromIndex = DataPacket.fromIndex();

            byte resultCode = data[fromIndex];
            QMode qMode = QMode.valueOf(resultCode);
            if (qMode != null) {
                QInfo qInfo = new QInfo();
                qInfo.qMode = qMode;
                qInfo.qInit = data[fromIndex + 1];
                qInfo.qMax = data[fromIndex + 2];
                qInfo.qMin = data[fromIndex + 3];

                resultSuccess(cmd, qInfo);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else if (dataLen == 0x06) {
            byte[] data = protocol.getData();
            int fromIndex = DataPacket.fromIndex();

            byte resultCode = data[fromIndex];
            QMode qMode = QMode.valueOf(resultCode);
            if (qMode != null) {
                QInfo qInfo = new QInfo();
                qInfo.qMode = qMode;
                qInfo.qInit = data[fromIndex + 1];
                qInfo.qMax = data[fromIndex + 2];
                qInfo.qMin = data[fromIndex + 3];
                qInfo.numMinQCycles = data[fromIndex + 4];
                qInfo.maxQuerySinceEPC = data[fromIndex + 5];

                resultSuccess(cmd, qInfo);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processGetRfPortReturnLoss(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();

        if (dataLen == 0x01) {
            byte returnLoss = protocol.getData()[DataPacket.fromIndex()];
            RfPortReturnLoss rfPortReturnLoss = new RfPortReturnLoss();
            rfPortReturnLoss.setCmd(cmd);
            rfPortReturnLoss.setReturnLoss(returnLoss);
            resultSuccess(cmd, rfPortReturnLoss);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetAntennaGroup(DataPacket protocol) {
        // The device has a return result code
        boolean hasResultCode = protocol.getCoreDataLen() == 0x01;
        byte resultCode = protocol.getData()[DataPacket.fromIndex()];

        if (hasResultCode && resultCode == ResultCode.SUCCESS) {
            this.mAntennaGroupId = mTrySetAntennaGroupId;
            if (mAfterSwitchGroupRequestInfo != null) {
                addRequest(mAfterSwitchGroupRequestInfo);
                mAfterSwitchGroupRequestInfo = null;
            }
        } else if (mAfterSwitchGroupRequestInfo != null) {
            // Return the corresponding error according to the corresponding
            // request before switching the antenna group
            DataPacket packet = mAfterSwitchGroupRequestInfo.getDataPacket();
            byte afterCmd = packet.getCmd();
            byte btResCode = hasResultCode ? resultCode : ResultCode.UNKNOWN_ERROR;
            resultFailure(afterCmd, btResCode);
            mAfterSwitchGroupRequestInfo = null;
        } else {
            LLLog.i("um...");
        }
    }

    private void processGetAntennaGroup(DataPacket protocol) {
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x01) {
            this.mAntennaGroupId = data[fromIndex] & 0xFF;
            if (mAfterSwitchGroupRequestInfo != null) {
                addRequest(mAfterSwitchGroupRequestInfo);
                mAfterSwitchGroupRequestInfo = null;
            }
        } else {
            if (mAfterSwitchGroupRequestInfo != null) {
                // Return the corresponding error according to the corresponding
                // request before switching the antenna group
                DataPacket packet = mAfterSwitchGroupRequestInfo.getDataPacket();
                byte afterCmd = packet.getCmd();
                byte btResCode = ResultCode.UNKNOWN_ERROR;
                resultFailure(afterCmd, btResCode);
                mAfterSwitchGroupRequestInfo = null;
            }
        }
    }

    private void processInventory(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x09) {
            if (mInventoryConfig != null) {
                Consumer<InventoryTagEnd> successConsumer = mInventoryConfig.getOnSuccessEnd();
                if (successConsumer != null) {
                    try {
                        int currentAnt = data[fromIndex] & 0xFF + (mAntennaGroupId > 0 ? 8 : 0);
                        int tagCount = ((data[fromIndex + 1] & 0xFF) << 8) + (data[fromIndex + 2] & 0xFF);
                        int readRate = ((data[fromIndex + 3] & 0xFF) << 8) + (data[fromIndex + 4] & 0xFF);
                        int totalRead = ((data[fromIndex + 5] & 0xFF) << 24) +
                                ((data[fromIndex + 6] & 0xFF) << 16) +
                                ((data[fromIndex + 7] & 0xFF) << 8) +
                                (data[fromIndex + 8] & 0xFF);
                        InventoryTagEnd tagEnd = new InventoryTagEnd();
                        tagEnd.setAntId(currentAnt + mAntStartIndex);/*processInventory onEnd*/
                        tagEnd.setReadRate(readRate);
                        tagEnd.setTotalRead(totalRead);
                        tagEnd.setTagCount(tagCount);
                        successConsumer.accept(tagEnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mCmdStatusCallback != null) {
                try {
                    mCmdStatusCallback.accept(new CmdStatus(cmd, ResultCode.SUCCESS, mAntennaId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (dataLen == 0x01) {
            byte resultCode = data[fromIndex];
            resultFailure(cmd, resultCode);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processFastSwitchInventory(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();
        int endIndex = fromIndex + dataLen;

        if (dataLen == 0x01) {
            byte errorCode = data[fromIndex];
            resultFailure(cmd, errorCode);
        } else if (dataLen == 0x02) {
            int antId = data[fromIndex] & 0xFF;
            byte errorCode = data[fromIndex + 1];
            resultFailure(cmd, errorCode, antId);
        } else if (dataLen == 0x07) {
            if (mInventoryConfig != null) {
                Consumer<InventoryTagEnd> successConsumer = mInventoryConfig.getOnSuccessEnd();
                if (successConsumer != null) {
                    try {
                        int nSwitchTotal = ((data[fromIndex] & 0xFF) << 16) +
                                ((data[fromIndex + 1] & 0xFF) << 8) +
                                (data[fromIndex + 2] & 0xFF);
                        int nSwitchTime = ((data[fromIndex + 3] & 0xFF) << 24) +
                                ((data[fromIndex + 4] & 0xFF) << 16) +
                                ((data[fromIndex + 5] & 0xFF) << 8) +
                                (data[fromIndex + 6] & 0xFF);
                        InventoryTagEnd inventoryTagEnd = new InventoryTagEnd();
                        inventoryTagEnd.setCmd(cmd);
                        inventoryTagEnd.setTotalRead(nSwitchTotal);
                        if (nSwitchTime > 0) {
                            inventoryTagEnd.setReadRate((int) (nSwitchTotal * 1f / nSwitchTime * 1000));
                        } else {
                            inventoryTagEnd.setReadRate(0);
                        }
                        successConsumer.accept(inventoryTagEnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mCmdStatusCallback != null) {
                try {
                    mCmdStatusCallback.accept(new CmdStatus(cmd, ResultCode.SUCCESS, mAntennaId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mAntennaId = 0x07 + (mAntennaGroupId << 3);
        } else {
            if (mInventoryConfig != null) {
                Consumer<InventoryTag> successConsumer = mInventoryConfig.getOnSuccess();
                if (successConsumer != null) {
                    try {
                        boolean isEnablePhase = mInventoryConfig.isEnablePhase();
                        int offset = isEnablePhase ? 3 : 1;
                        int nEpcLength = dataLen - 3 - offset;
                        String strEpc = ArrayUtils.bytesToHexString(data, fromIndex + 3, nEpcLength);
                        String strPc = ArrayUtils.bytesToHexString(data, fromIndex + 1, 2);
                        int rssi = data[endIndex - offset] & 0x7F;
                        int nPhase = 0;
                        if (isEnablePhase) {
                            nPhase = ArrayUtils.spliceByteToInt(data[endIndex - 2], data[endIndex - 1]);
                        }
                        byte btTemp = data[fromIndex];
                        int antId = (btTemp & 0x03)
                                + ((data[endIndex - offset] & 0xFF) >> 7) * 4
                                + (mAntennaGroupId > 0 ? 8 : 0);
                        int freqQuantity = (btTemp & 0xFF) >> 2;
                        String strFreq = getFreqString(freqQuantity);
                        InventoryTag inventoryTag = new InventoryTag();
                        inventoryTag.setCmd(cmd);
                        inventoryTag.setPc(strPc);
                        inventoryTag.setEpc(strEpc);
                        inventoryTag.setRssi(rssi - 129);
                        inventoryTag.setPhase(nPhase);
                        inventoryTag.setFreq(strFreq);
                        inventoryTag.setAntId(antId + mAntStartIndex);/*processFastSwitch*/
                        successConsumer.accept(inventoryTag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void processCustomizedSessionTargetInventory(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();
        int endIndex = fromIndex + dataLen;

        if (dataLen == 0x01) {
            byte errorCode = data[fromIndex];
            resultFailure(cmd, errorCode);
        } else if (dataLen == 0x07) {
            if (mInventoryConfig != null) {
                Consumer<InventoryTagEnd> successConsumer = mInventoryConfig.getOnSuccessEnd();
                if (successConsumer != null) {
                    try {
                        int currentAnt = data[fromIndex] & 0xFF + (mAntennaGroupId > 0 ? 8 : 0);
                        int readRate = ((data[fromIndex + 1] & 0xFF) << 8) + (data[fromIndex + 2] & 0xFF);
                        int totalRead = ((data[fromIndex + 3] & 0xFF) << 24) +
                                ((data[fromIndex + 4] & 0xFF) << 16) +
                                ((data[fromIndex + 5] & 0xFF) << 8) +
                                (data[fromIndex + 6] & 0xFF);
                        InventoryTagEnd inventoryTagEnd = new InventoryTagEnd();
                        inventoryTagEnd.setCmd(cmd);
                        inventoryTagEnd.setAntId(currentAnt + mAntStartIndex);/*processCustomizedSession onEnd*/
                        inventoryTagEnd.setReadRate(readRate);
                        inventoryTagEnd.setTotalRead(totalRead);
                        successConsumer.accept(inventoryTagEnd);/*盘存结果：每一轮标签结束*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mCmdStatusCallback != null) {
                try {
                    mCmdStatusCallback.accept(new CmdStatus(cmd, ResultCode.SUCCESS, mAntennaId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (mInventoryConfig != null) {
                Consumer<InventoryTag> successConsumer = mInventoryConfig.getOnSuccess();
                if (successConsumer != null) {
                    try {
                        boolean isEnablePhase = mInventoryConfig.isEnablePhase();
                        int offset = isEnablePhase ? 3 : 1;
                        int nEpcLength = dataLen - 3 - offset;
                        String strEpc = "";
                        if (nEpcLength != 0) {
                            strEpc = ArrayUtils.bytesToHexString(data, fromIndex + 3, nEpcLength);
                        }
                        String strPc = ArrayUtils.bytesToHexString(data, fromIndex + 1, 2);
                        int rssi = data[endIndex - offset] & 0x7F;
                        int nPhase = 0;
                        if (isEnablePhase) {
                            nPhase = ArrayUtils.spliceByteToInt(data[endIndex - 2], data[endIndex - 1]);
                        }
                        byte btTemp = data[fromIndex];
                        int antId = (btTemp & 0x03)
                                + ((data[endIndex - offset] & 0xFF) >> 7) * 4
                                + (mAntennaGroupId > 0 ? 8 : 0);
                        int freqQuantity = (btTemp & 0xFF) >> 2;
                        String strFreq = getFreqString(freqQuantity);
                        InventoryTag inventoryTag = new InventoryTag();
                        inventoryTag.setCmd(cmd);
                        inventoryTag.setPc(strPc);
                        inventoryTag.setEpc(strEpc);
                        inventoryTag.setRssi(rssi - 129);
                        inventoryTag.setPhase(nPhase);
                        inventoryTag.setFreq(strFreq);
                        inventoryTag.setAntId(antId + mAntStartIndex);/*processCustomizedSession onRecv*/
                        successConsumer.accept(inventoryTag);/*盘存结果：每一个标签结束*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void processReadTag(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x01) {
            byte resultCode = data[fromIndex];
            resultFailure(cmd, resultCode);
        } else {
            int tagCount = ((data[fromIndex] & 0xFF) << 8) | (data[fromIndex + 1] & 0xFF);
            int nDataLen = (data[fromIndex + dataLen - 3] & 0xFF);
            int nEpcLen = (data[fromIndex + 2] & 0xFF) - nDataLen - 4;

            int rssi = -1;
            if (nDataLen % 2 != 0) {/*有Rssi*/
                nDataLen--;
                int i = data.length - 5;
                rssi = data[i] & 0xFF;
            }

            int index = fromIndex + 3;
            String strPc = ArrayUtils.bytesToHexString(data, index, 2);
            index = index + 2;
            String strEpc = ArrayUtils.bytesToHexString(data, index, nEpcLen);
            index = index + nEpcLen;
            String strCrc = ArrayUtils.bytesToHexString(data, index, 2);
            index = index + 2;
            String strData = ArrayUtils.bytesToHexString(data, index, nDataLen);

            byte btTemp = data[fromIndex + dataLen - 2];
            int freqQuantity = (btTemp & 0xFF) >> 2;
            String strFreq = getFreqString(freqQuantity);

            byte btAntId = (byte) ((btTemp & 0x03)
                    + ((data[fromIndex + dataLen - 1] & 0xFF) >> 7) * 4
                    + (mAntennaGroupId > 0 ? 8 : 0));
            int nReadCount = data[fromIndex + dataLen - 1] & 0x7F;
            if (multiAntReadTagConfig != null) {
                try {
                    Consumer<OperationTag> onSuccessConsumer = multiAntReadTagConfig.getOnSuccess();
                    if (onSuccessConsumer != null) {
                        OperationTag operationTag = new OperationTag();
                        operationTag.setCmd(cmd);
                        mOperateTagCount.incrementAndGet();
                        boolean isEnd = mOperateTagCount.compareAndSet(tagCount, 0);
                        operationTag.setEndTag(isEnd);
                        operationTag.setTagCount(tagCount);
                        operationTag.setStrPc(strPc);
                        operationTag.setStrCrc(strCrc);
                        operationTag.setStrEpc(strEpc);
                        operationTag.setStrData(strData);
                        operationTag.setDataLen(nDataLen);
                        operationTag.setAntId(btAntId + mAntStartIndex);/*processReadTag multi ant*/
                        operationTag.setRssi(rssi);
                        operationTag.setFreq(strFreq);
                        operationTag.setReadCount(nReadCount);
                        onSuccessConsumer.accept(operationTag);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                OperationTag operationTag = new OperationTag();
                operationTag.setCmd(cmd);
                mOperateTagCount.incrementAndGet();
                boolean isEnd = mOperateTagCount.compareAndSet(tagCount, 0);
                operationTag.setEndTag(isEnd);
                operationTag.setTagCount(tagCount);
                operationTag.setStrPc(strPc);
                operationTag.setStrCrc(strCrc);
                operationTag.setStrEpc(strEpc);
                operationTag.setStrData(strData);
                operationTag.setDataLen(nDataLen);
                operationTag.setAntId(btAntId + mAntStartIndex);/*processReadTag*/
                operationTag.setRssi(rssi);
                operationTag.setFreq(strFreq);
                operationTag.setReadCount(nReadCount);
                resultSuccess(cmd, operationTag);
            }
        }
    }

    private void processWriteTag(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x01) {
            resultFailure(cmd, data[fromIndex]);
        } else {
            int nEpcLen = (data[fromIndex + 2] & 0xFF) - 4;
            byte resultCode = data[fromIndex + dataLen - 3];
            if (resultCode != ResultCode.SUCCESS) {
                resultFailure(cmd, resultCode);
                return;
            }
            int tagCount = ((data[fromIndex] & 0xFF) << 8) + (data[fromIndex + 1] & 0xFF);
            int nReadCount = data[fromIndex + dataLen - 1] & 0x7F;

            String strPc = ArrayUtils.bytesToHexString(data, fromIndex + 3, 2);
            String strEpc = ArrayUtils.bytesToHexString(data, fromIndex + 5, nEpcLen);
            String strCrc = ArrayUtils.bytesToHexString(data, fromIndex + 5 + nEpcLen, 2);
            // String strData = "";
            int coreDataLen = data[fromIndex + 2] & 0xFF;
            String strData = ArrayUtils.bytesToHexString(data, fromIndex + 3, coreDataLen);
            byte btTemp = data[fromIndex + dataLen - 2];
            byte btAntId = (byte) ((btTemp & 0x03)
                    + ((data[fromIndex + dataLen - 1] & 0xFF) >> 7) * 4
                    + (mAntennaGroupId > 0 ? 8 : 0));

            OperationTag operationTag = new OperationTag();
            operationTag.setCmd(cmd);
            mOperateTagCount.incrementAndGet();
            boolean isEnd = mOperateTagCount.compareAndSet(tagCount, 0);
            operationTag.setEndTag(isEnd);
            operationTag.setTagCount(tagCount);
            operationTag.setStrPc(strPc);
            operationTag.setStrCrc(strCrc);
            operationTag.setStrEpc(strEpc);
            operationTag.setStrData(strData);
            operationTag.setDataLen(coreDataLen);
            operationTag.setAntId(btAntId + mAntStartIndex);/*processWriteTag*/
            operationTag.setReadCount(nReadCount);
            resultSuccess(cmd, operationTag);
        }
    }

    private void processLockTag(DataPacket protocol) {
        processWriteTag(protocol);
    }

    private void processKillTag(DataPacket protocol) {
        processWriteTag(protocol);
    }

    private void processSetImpinjFastTid(DataPacket protocol) {
        processSet(protocol);
    }

    private void processSetAndSaveImpinjFastTid(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetImpinjFastTid(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x01) {
            ImpinjFastTid fastTid = new ImpinjFastTid();
            fastTid.setCmd(cmd);
            fastTid.setTidType(FastTidType.valueOf(data[fromIndex]));
            resultSuccess(cmd, fastTid);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processBlockTag(DataPacket protocol) {
        processWriteTag(protocol);
    }

    private void processSetAccessEpcMatch(DataPacket protocol) {
        processSet(protocol);
    }

    private void processGetAccessEpcMatch(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        byte resultCode = data[fromIndex];
        if (dataLen == 0x01) {
            if (resultCode == 0x01) {
                resultFailure(cmd, ResultCode.FAIL);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else {
            if (resultCode == 0x00) {
                String matchEpcValue = ArrayUtils.bytesToHexString(data, fromIndex + 2, data[fromIndex + 1] & 0xFF);
                MatchInfo info = new MatchInfo();
                info.setCmd(cmd);
                info.setMatchEpcValue(matchEpcValue);
                resultSuccess(cmd, info);
            } else {
                resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
            }
        }
    }

    private void processTagMask(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x01) {
            byte resultCode = data[fromIndex];
            if (resultCode == ResultCode.SUCCESS) {
                Success success = new Success();
                success.setCmd(cmd);
                resultSuccess(cmd, success);
            } else {
                resultFailure(cmd, resultCode);
            }
        } else if (dataLen > 7) {
            byte btMaskId = data[fromIndex];
            byte btMaskQuantity = data[fromIndex + 1];
            byte btTarget = data[fromIndex + 2];
            byte btAction = data[fromIndex + 3];
            byte btMemBank = data[fromIndex + 4];
            byte btStartMaskAddress = data[fromIndex + 5];
            byte btMaskBitLen = data[fromIndex + 6];
            byte[] maskValue = new byte[dataLen - 8];
            for (int i = 0; i < maskValue.length; i++) {
                maskValue[i] = data[fromIndex + 7 + i];
            }
            String strMaskValue = ArrayUtils.bytesToHexString(maskValue, 0, maskValue.length);
            byte btTruncate = data[fromIndex + dataLen - 1];
            MaskInfo info = new MaskInfo();
            info.setCmd(cmd);
            info.setMaskId(MaskId.valueOf(btMaskId));
            info.setMaskQuantity(btMaskQuantity);
            info.setMaskTarget(MaskTarget.valueOf(btTarget));
            info.setMaskAction(MaskAction.valueOf(btAction));
            info.setMemBank(MemBank.valueOf(btMemBank));
            info.setMaskBitStartAddress(btStartMaskAddress);
            info.setMaskBitLength(btMaskBitLen);
            info.setMaskValue(strMaskValue);
            info.setTruncate(btTruncate);
            resultSuccess(cmd, info);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processQueryReaderStatus(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();

        if (dataLen == 0x01) {
            ReaderStatus readerStatus = new ReaderStatus();
            readerStatus.setCmd(cmd);
            readerStatus.setStatus(data[fromIndex]);
            resultSuccess(cmd, readerStatus);
        } else {
            resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
        }
    }

    private void processSetReaderStatus(DataPacket protocol) {
        processSet(protocol);
    }

    private void processTempLabel2Command(DataPacket protocol) {
        byte cmd = protocol.getCmd();
        int dataLen = protocol.getCoreDataLen();
        byte[] data = protocol.getData();
        int fromIndex = DataPacket.fromIndex();
        if (dataLen == 1) {
            //失败
            resultFailure(cmd, data[fromIndex]);
            return;
        }
        int tagCount = ((data[fromIndex] & 0xFF) << 8) | (data[fromIndex + 1] & 0xFF);
        byte resultCode = data[fromIndex + dataLen - 3];
        int antId = data[fromIndex + dataLen - 2] & 0x3;
        int readCount = data[fromIndex + dataLen - 1] & 0xFF;

        int nDataLen = 0;
        String strData = "";
        switch (mTempLabel2Flag) {
            case TempLabel2Flag.SINGLE_MEAS_TEMP:
                if (resultCode == ResultCode.SUCCESS) {
                    nDataLen = 2;
                    if (mTagMeasOpt == null) {
                        resultFailure(cmd, ResultCode.UNKNOWN_ERROR);
                        return;
                    }
                    int value = ((data[fromIndex + dataLen - 5] & 0xFF) << 8) | (data[fromIndex + dataLen - 4] & 0xFF);
                    if (mTagMeasOpt == TagMeasOpt.MeasTemp) {
                        strData = String.format(Locale.getDefault(), "%.2f", value / 4.0) + "°C";
                    } else {
                        strData = String.format(Locale.getDefault(), "%.2f", value / 8192.0 * 2.5) + " V";
                    }
                }
                break;
            case TempLabel2Flag.READ_REGISTER:
            case TempLabel2Flag.STATE_CHECK:
                if (resultCode == ResultCode.SUCCESS) {
                    nDataLen = 2;
                }
                break;
            case TempLabel2Flag.READ_MEMORY:
                if (resultCode == ResultCode.SUCCESS) {
                    nDataLen = mReadMemoryLen;
                }
                break;
            default:
        }

        int nEpcLen = (data[fromIndex + 2] & 0xFF) - 4 - nDataLen;
        String strPc = ArrayUtils.bytesToHexString(data, fromIndex + 3, 2);
        String strEpc = ArrayUtils.bytesToHexString(data, fromIndex + 5, nEpcLen);
        String strCrc = ArrayUtils.bytesToHexString(data, fromIndex + 5 + nEpcLen, 2);
        if (nDataLen > 0 && strData.isEmpty()) {
            strData = ArrayUtils.bytesToHexString(data, fromIndex + 7 + nEpcLen, nDataLen);
        }
        TempLabel2 label = new TempLabel2();
        label.setCmd(cmd);
        label.setType(mTempLabel2Flag);
        label.setResultCode(resultCode);
        label.setTagCount(tagCount);
        label.setAntId(antId + mAntStartIndex);
        label.setReadCount(readCount);
        label.setStrPc(strPc);
        label.setStrEpc(strEpc);
        label.setStrCrc(strCrc);
        label.setStrData(strData);
        resultSuccess(cmd, label);
    }

    /**
     * Process undefined new instructions
     *
     * @param protocol {@link DataPacket}
     */
    private void processUndefined(DataPacket protocol) {
        final ReceiveData rawData = new ReceiveData();
        rawData.setCmd(protocol.getCmd());
        int dataLen = protocol.getCoreDataLen();
        if (dataLen > 0) {
            byte[] data = new byte[dataLen];
            System.arraycopy(protocol.getData(), DataPacket.fromIndex(), data, 0, dataLen);
            rawData.setData(data);
        } else {
            rawData.setData(new byte[0]);
        }
        if (mUndefinedResultCallback != null) {
            try {
                mUndefinedResultCallback.accept(rawData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get frequency string
     *
     * @param freqQuantity The number of frequency points,
     *                     including the number of frequency points of the starting frequency,
     *                     1 means transmitting at the fixed frequency of the starting frequency.
     *                     This parameter must be greater than 0
     * @return Current frequency string
     */
    private String getFreqString(int freqQuantity) {
        if (mIsUserDefineRegion) {
            int nExtraFrequency = freqQuantity * mUserDefineFreqInterval;
            float nStart = (mUserDefineFreqStart + nExtraFrequency) / 1000f;
            return String.format(Locale.getDefault(), "%.3f", nStart);
        } else {
            if (freqQuantity < 0x07) {
                float nStart = 865.00f + freqQuantity * 0.5f;
                return String.format(Locale.getDefault(), "%.2f", nStart);
            } else {
                float nStart = 902.00f + (freqQuantity - 7) * 0.5f;
                return String.format(Locale.getDefault(), "%.2f", nStart);
            }
        }
    }

    /**
     * Successful execution callback
     *
     * @param cmd     The command code of the data packet returned by the device
     * @param success {@link Success}
     */
    private void resultSuccess(byte cmd, Success success) {
        Consumer<Success> onSuccess = mSuccessMap.get(cmd);
        if (onSuccess != null) {
            try {
                onSuccess.accept(success);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCmdStatusCallback != null) {
            try {
                mCmdStatusCallback.accept(new CmdStatus(cmd, ResultCode.SUCCESS, mAntennaId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handling return failure generic type callback
     *
     * @param cmd       The command code of the data packet returned by the device
     * @param errorCode Error code, reference{@link ResultCode}
     */
    private boolean resultFailure(byte cmd, byte errorCode) {
        return resultFailure(cmd, errorCode, mAntennaId);
    }

    /**
     * Handling return failure generic type callback
     *
     * @param cmd       The command code of the data packet returned by the device
     * @param errorCode Error code, reference{@link ResultCode}
     * @param antId     Antenna id, The default is -1
     */
    private boolean resultFailure(byte cmd, byte errorCode, int antId) {
        if (cmd == Cmd.CUSTOMIZED_SESSION_TARGET_INVENTORY || cmd == Cmd.FAST_INVENTORY
                || cmd == Cmd.FAST_SWITCH_ANT_INVENTORY || cmd == Cmd.INVENTORY) {
            if (mInventoryConfig != null) {
                try {
                    InventoryFailure failure = new InventoryFailure();
                    failure.setCmd(cmd);
                    failure.setAntId(antId + mAntStartIndex);/*resultFailure*/
                    failure.setErrorCode(errorCode);
                    mInventoryConfig.getOnFailure().accept(failure);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Failure failure = new Failure();
            failure.setCmd(cmd);
            failure.setErrorCode(errorCode);
            Consumer<Failure> onError = mFailureMap.get(cmd);
            if (onError != null) {
                try {
                    onError.accept(failure);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LLLog.e("超时.cmd=" + cmd + ":" + Cmd.getNameForCmd(cmd));
                return false;
            }
        }
        if (mCmdStatusCallback != null) {
            try {
                mCmdStatusCallback.accept(new CmdStatus(cmd, errorCode, antId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Start timing, monitor whether this request has timed out
     */
    private void startTiming() {
        if (mTimeout <= 0) {
            return;
        }
        if (mScheduler == null) {
            mScheduler = Executors.newSingleThreadScheduledExecutor();
        }
        mScheduledFuture = mScheduler.schedule(mTimingRunnable, mTimeout, TimeUnit.MILLISECONDS);
    }

    public void setCmdStatusCallback(Consumer<CmdStatus> onStatus) {
        this.mCmdStatusCallback = onStatus;
    }

    void setOriginalDataCallback(Consumer<byte[]> onSend, Consumer<byte[]> onReceive) {
        this.mOriginalSendCallback = onSend;
        this.mOriginalReceiveCallback = onReceive;
    }

    void addOriginalDataReceivedCallback(Consumer<byte[]> onReceive) {
        if (!mOriginalReceiveCallbacks.contains(onReceive)) {
            mOriginalReceiveCallbacks.add(onReceive);
        }
    }

    void removeOriginalDataReceivedCallback(Consumer<byte[]> onReceive) {
        mOriginalReceiveCallbacks.remove(onReceive);
    }

    void setUndefinedResultCallback(Consumer<ReceiveData> onResult) {
        this.mUndefinedResultCallback = onResult;
    }

    /**
     * Cancel this timeout mechanism
     */
    private void cancelTiming() {
        if (mScheduledFuture != null) {
            mScheduledFuture.cancel(true);
            mScheduledFuture = null;
        }
    }

    /**
     * Disconnect the reader and release resources
     */
    void disconnect() {
        stopInventory();
        reset();
        if (mConnectHandle != null) {
            mConnectHandle.onDisconnect();
        }
        mConnectHandle = null;
    }

    void reset() {
        LLLog.i("reset status...");
        cancelTiming();/*reset*/
        mQueue.clear();
        mTransmitQueue.clear();
        mTransmitWaiting.set(false);
        if (mScheduler != null) {
            mScheduler.shutdownNow();
            mScheduler = null;
        }
        mCheckOperateTagCount.set(0);
    }
}
