package com.payne.reader.util;

/**
 * Utility class for working with arrays
 *
 * @author naz
 * Date 2019/11/27
 */
public class ArrayUtils {
    private ArrayUtils() {
    }

    /**
     * Merge two byte arrays into a new array
     *
     * @param lBytes byte array
     * @param rBytes byte array
     * @return a new byte array
     */
    public static byte[] mergeBytes(byte[] lBytes, byte[] rBytes) {
        if (lBytes == null) {
            if (rBytes == null) {
                return new byte[0];
            } else {
                return rBytes;
            }
        } else if (rBytes == null) {
            return lBytes;
        }
        byte[] mergeData = new byte[lBytes.length + rBytes.length];
        System.arraycopy(lBytes, 0, mergeData, 0, lBytes.length);
        System.arraycopy(rBytes, 0, mergeData, lBytes.length, rBytes.length);
        return mergeData;
    }

    /**
     * Byte array to hex string
     *
     * @param bytes     Byte array
     * @param fromIndex start position
     * @param len       Conversion length
     * @return Hex string
     */
    public static String bytesToHexString(byte[] bytes, int fromIndex, int len) {
        if (fromIndex >= bytes.length) {
            return "";
        }
        if (fromIndex + len > bytes.length) {
            len = bytes.length - fromIndex;
        }
        int i;
        int endIndex = fromIndex + len;
        StringBuilder strResult = new StringBuilder(String.format("%02X", bytes[fromIndex]));
        for (i = fromIndex + 1; i < endIndex; i++) {
            String strTemp = String.format(" %02X", bytes[i]);
            strResult.append(strTemp);
        }
        return strResult.toString();
    }

    /**
     * Hex string to byte array
     *
     * @param hex Hex string
     * @return Byte array
     */
    public static byte[] hexStringToBytes(String hex) {
        if (hex == null || hex.length() == 0) {
            return new byte[0];
        }
        int hexLen = hex.length();
        int len = hexLen >> 1;
        int dataLen = len;
        boolean isOddNum = (hexLen & 0x01) != 0x00;
        if (isOddNum) {
            dataLen++;
        }
        byte[] data = new byte[dataLen];
        for (int i = 0; i < len; i++) {
            int pos = i << 1;
            data[i] = (byte) ((Character.digit(hex.charAt(pos), 16) << 4)
                    + Character.digit(hex.charAt(pos + 1), 16));
        }
        if (isOddNum) {
            data[len] = (byte) (Character.digit(hex.charAt(hexLen - 1), 16) << 4);
        }
        return data;
    }

    /**
     * Splice two bytes into signed integer data
     * The data in the data field are stored in little-endian, for example: data 0x1234, then lByte is 0x12 and rByte is 0x34
     *
     * @param lByte High byte
     * @param rByte Low byte
     * @return Int after splicing
     */
    public static int spliceByteToInt(byte lByte, byte rByte) {
        int result = lByte;
        result = (result << 8) | (0x00FF & rByte);
        return result;
    }

    /**
     * Convert byte array specified position to int value
     *
     * @param src       Byte array
     * @param fromIndex start position
     * @param len       Conversion length ,Cannot be greater than 4
     * @return int
     */
    public static int byteArrayToInt(byte[] src, int fromIndex, int len) {
        return byteArrayToInt(src, fromIndex, len, true);
    }

    /**
     * Convert byte array specified position to int value
     *
     * @param src       Byte array
     * @param fromIndex start position
     * @param len       Conversion length ,Cannot be greater than 4
     * @param bigEndian Is it big endian?
     * @return int
     */
    public static int byteArrayToInt(byte[] src, int fromIndex, int len, boolean bigEndian) {
        int interval = src.length - fromIndex;
        if (fromIndex < 0 || interval < 0 || interval < len || len > 4) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int value = 0;
        int end = fromIndex + len;
        for (int i = fromIndex; i < end; i++) {
            int offset;
            if (bigEndian) {
                offset = end - 1 - i << 3;
            } else {
                offset = i - fromIndex << 3;
            }
            value |= (src[i] & 0xFF) << offset;
        }
        return value;
    }

    /**
     * Convert int to byte array
     *
     * @param src int value
     * @return byte array
     */
    public static byte[] intToByteArray(int src) {
        return intToByteArray(src, 4);
    }

    public static byte[] intToByteArray(int src, int len) {
        byte[] dst = new byte[len];
        for (int i = 0; i < len; i++) {
            dst[i] = (byte) (src >> ((len - 1 - i) * 8));
        }
        return dst;
    }

    public static byte[] toBinBytes(int d) {
        int i = Integer.parseInt("" + d, 16);
        String s = Integer.toBinaryString(i);

        byte[] bytes = new byte[8];

        int length = s.length();
        int offset = 8 - length;
        for (int j = 0; j < bytes.length; j++) {
            if (j < offset) {
                // bytes[j] = 0;
            } else {
                String cStr = String.valueOf(s.charAt(j - offset));
                bytes[j] = Byte.parseByte(cStr);
            }
        }
        return bytes;
    }

    /**
     * 例：
     *
     * @param str   "A0 04 01 81 36 A4"
     * @param regex " "
     * @return {0xA0,0x04,0x01,0x81,0x36,0xA4}
     */
    public static byte[] hexStrToByteArr(String str, String regex) {
        if (str == null || str.length() == 0) {
            return new byte[0];
        }
        String[] split = str.split(regex);
        byte[] bytes = new byte[split.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(split[i], 16);
        }
        return bytes;
    }

    public static String CalcPC(int epc_length) {
        // [10h-14h][15h-1Fh]
        // xxxx xxxx xxxx xxxx
        // 1111 1000 0000 0000
        String pc = String.format("%04x", ((epc_length << 11) & 0xF800));
        return pc;
    }
}