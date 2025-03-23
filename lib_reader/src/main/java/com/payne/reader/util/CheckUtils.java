package com.payne.reader.util;

/**
 * Utility class to check results
 *
 * @author naz
 * Date 2019/12/31
 */
public class CheckUtils {
    private CheckUtils() {
    }

    /**
     * Calculate checksum
     *
     * @param bytes     data
     * @param fromIndex start position
     * @param len       Checking length
     * @return Checksum
     */
    public static byte getCheckSum(byte[] bytes, int fromIndex, int len) {
        byte btSum = 0x00;

        for (int nloop = fromIndex; nloop < fromIndex + len; nloop++) {
            btSum += bytes[nloop];
        }
        return (byte) (((~btSum) + 1) & 0xFF);
    }

    /**
     * Check that the checksum is correct
     *
     * @param bytes Byte array
     * @return bool
     */
    public static boolean verifyChecksum(byte[] bytes) {
        if (isEmpty(bytes)) {
            return false;
        }
        byte sum = getCheckSum(bytes, 0, bytes.length - 1);
        return sum == bytes[bytes.length - 1];
    }

    /**
     * Checks that {@code fromIndex} and {@code toIndex} are in
     * the range and throws an exception if they aren't.
     */
    public static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }

    /**
     * Check byte array length validity
     *
     * @param bytes  Byte array
     * @param length Length
     * @return bool
     */
    public static boolean checkArrayLen(byte[] bytes, int length) {
        return bytes != null && bytes.length >= length;
    }

    /**
     * Check if bytes is null
     *
     * @param bytes Byte array
     * @return bool
     */
    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    /**
     * Determines whether the string is a hexadecimal string
     *
     * @param str String to check
     * @return bool
     */
    public static boolean isNotHexString(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return !str.matches("^[A-Fa-f0-9]+$");
    }
}
