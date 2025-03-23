package com.payne.reader.communication;

import com.payne.reader.bean.config.Header;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;

/**
 * Protocol data processing
 * Data (excluding data header, length, address, cmd and checksum)
 * // ---------------------------------------------------------
 * // | header | length | address |  cmd  | [data] | checksum |
 * // |  0xA0  |  0x04  |   0xFF  |  0x74 |  0x00  |   0xE9   |
 * // ---------------------------------------------------------
 *
 * @author naz
 * Date 2019/11/22
 */
public class DataPacket {
    private static final int MIN_PROTOCOL_DATA_LEN = 5;
    /**
     * Raw byte array
     */
    private byte[] rawBytes;

    private DataPacket() {
    }

    public DataPacket(byte address, byte cmd) {
        this(address, cmd, null);
    }

    /**
     * @param address   Reader address
     * @param cmd       Command code
     * @param coreBytes Core data
     */
    public DataPacket(byte address, byte cmd, byte[] coreBytes) {
        this(address, cmd, coreBytes, 0, coreBytes == null ? 0 : coreBytes.length);
    }

    /**
     * @param address    Reader address
     * @param cmd        Command code
     * @param data       Data to be copied
     * @param startIndex Start index of copied data
     * @param length     Data length copied
     */
    public DataPacket(byte address, byte cmd, byte[] data, int startIndex, int length) {
        this(Header.HEADER_A0, address, cmd, data, startIndex, length);
    }

    /**
     * @param head       Data header
     * @param address    Reader address
     * @param cmd        Command code
     * @param data       Data to be copied
     * @param startIndex Start index of copied data
     * @param length     Data length copied
     */
    public DataPacket(byte head, byte address, byte cmd, byte[] data, int startIndex, int length) {
        int btArrayLen = 0;
        if (!CheckUtils.isEmpty(data)) {
            if (length > data.length) {
                throw new InvalidParameterException("Invalid length");
            }
            if (startIndex < 0 || startIndex > length) {
                throw new InvalidParameterException("Invalid start index");
            }
            btArrayLen = length - startIndex;
        }
        byte[] sendBytes = new byte[5 + btArrayLen];
        sendBytes[0] = head;
        sendBytes[1] = (byte) (btArrayLen + 3);
        sendBytes[2] = address;
        sendBytes[3] = cmd;
        if (btArrayLen > 0) {
            System.arraycopy(data, startIndex, sendBytes, 4, btArrayLen);
        }
        byte checkSum = CheckUtils.getCheckSum(sendBytes, 0, sendBytes.length - 1);
        sendBytes[sendBytes.length - 1] = checkSum;
        this.rawBytes = sendBytes;
    }

    /**
     * @param bytes byte[] source data
     * @deprecated instead of DataPacket.parse
     */
    public DataPacket(byte[] bytes) throws InvalidParameterException {
        //Minimum protocol data length
        // ---------------------------------------------------------
        // | header | length | address |  cmd  | [data] | checksum |
        // |  0xA0  |  0x04  |   0xFF  |  0x74 |  0x00  |   0xE9   |
        // ---------------------------------------------------------
        if (bytes == null || bytes.length < MIN_PROTOCOL_DATA_LEN) {
            throw new InvalidParameterException("Incorrect byte array length");
        }
        //Check whether the received data checksum is correct
        if (!CheckUtils.verifyChecksum(bytes)) {
            throw new InvalidParameterException("checksum check failed!");
        }
        this.rawBytes = bytes;
    }

    /**
     * @param bytes byte[] source data
     */
    public static DataPacket parse(byte[] bytes) {
        //Minimum protocol data length
        // ---------------------------------------------------------
        // | header | length | address |  cmd  | [data] | checksum |
        // |  0xA0  |  0x04  |   0xFF  |  0x74 |  0x00  |   0xE9   |
        // ---------------------------------------------------------
        if (bytes == null || bytes.length < MIN_PROTOCOL_DATA_LEN) {
            throw new InvalidParameterException("Incorrect byte array length");
        }
        //Check whether the received data checksum is correct
        if (!CheckUtils.verifyChecksum(bytes)) {
            throw new InvalidParameterException("checksum check failed!");
        }
        DataPacket dataPacket = new DataPacket();
        dataPacket.rawBytes = bytes;
        return dataPacket;
    }

    /**
     * Get packet header, each packet starts with 0xA0
     *
     * @return First byte of packet
     */
    public byte getHead() {
        return rawBytes[0];
    }

    /**
     * Get data length, The number of bytes since the packet starts from Len, does not contain Len itself.
     *
     * @return The second byte of the packet
     */
    public byte getLen() {
        return rawBytes[1];
    }

    /**
     * Get reader address, Used when the RS-485 interface is connected in series. The general address is from 0 to 254 (0xFE) and 255 (0xFF) is the public address.
     * The reader receives commands for its own address and public address
     *
     * @return The third byte of the packet
     */
    public byte getAddress() {
        return rawBytes[2];
    }

    /**
     * Get command code
     *
     * @return The fourth byte of the packet
     */
    public byte getCmd() {
        return rawBytes[3];
    }

    /**
     * Get the length of the core data in the packet (Packet length - (head + len + address + cmd + checksum)), Need to check if the length is 0
     *
     * @return Core data length
     */
    public int getCoreDataLen() {
        return rawBytes.length - 5;
    }

    /**
     * Get the starting index of the core data in the data packet (the fifth byte, the index is 4)
     *
     * @return starting index
     */
    public static int fromIndex() {
        return 4;
    }

    /**
     * Get checksum, checksum of all bytes except the checksum itself
     *
     * @return The last byte of the packet
     */
    public byte getCheckSum() {
        return rawBytes[rawBytes.length - 1];
    }

    /**
     * Get raw data
     *
     * @return byte[]
     */
    public byte[] getData() {
        return rawBytes;
    }

    @Override
    public String toString() {
        return "DataPacket-[" +
                ArrayUtils.bytesToHexString(rawBytes, 0, rawBytes.length) +
                ']';
    }
}
