package com.payne.reader.bean.send;

import com.payne.reader.base.TempLabel2Info;
import com.payne.reader.bean.config.TempLabel2Flag;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/12/18
 */
public class MtReadRegister implements TempLabel2Info {
    private final byte[] info;

    MtReadRegister(Builder builder) {
        int pwLen = builder.passwords.length;
        int measLen = builder.ptr.length;
        this.info = new byte[pwLen + measLen + 1];
        this.info[0] = TempLabel2Flag.READ_REGISTER;
        System.arraycopy(builder.passwords, 0, info, 1, pwLen);
        System.arraycopy(builder.ptr, 0, info, 1 + pwLen, measLen);
    }

    /**
     * Get the read label configuration
     *
     * @return byte[]
     */
    @Override
    public byte[] getTempLabel2Info() {
        return info;
    }

    /**
     * Build a new {@link CustomSessionReadConfig}.
     */
    public static class Builder {
        private byte[] passwords;
        private byte[] ptr;

        public Builder() {
            this.passwords = null;
            this.ptr = null;
        }

        /**
         * Set the access password,4 bytes.
         *
         * @param passwords Byte array password
         */
        public Builder setPasswords(byte[] passwords) {
            Objects.requireNonNull(passwords);
            int demandLength = 4;
            if (passwords.length < demandLength) {
                byte[] src = passwords;
                passwords = new byte[demandLength];
                System.arraycopy(src, 0, passwords, 0, src.length);
                for (int i = src.length; i < passwords.length; i++) {
                    passwords[i] = (byte) 0xFF;
                }
            } else if (passwords.length > demandLength) {
                byte[] src = passwords;
                passwords = new byte[demandLength];
                System.arraycopy(src, 0, passwords, 0, passwords.length);
            }
            this.passwords = passwords;
            return this;
        }

        /**
         * Set the access password,4 bytes.
         *
         * @param hexPassword Hexadecimal string password
         */
        public Builder setPasswords(String hexPassword) {
            Objects.requireNonNull(hexPassword);
            if (CheckUtils.isNotHexString(hexPassword)) {
                throw new InvalidParameterException("hexPassword must be a hexadecimal string!");
            }
            byte[] passwords = ArrayUtils.hexStringToBytes(hexPassword);
            return setPasswords(passwords);
        }

        /**
         * Set register address
         *
         * @param ptr Register address
         * @return this
         */
        public Builder setPtr(byte[] ptr) {
            Objects.requireNonNull(ptr);
            int demandLength = 4;
            if (ptr.length < demandLength) {
                byte[] src = ptr;
                ptr = new byte[demandLength];
                System.arraycopy(src, 0, ptr, 0, src.length);
            } else if (ptr.length > demandLength) {
                byte[] src = ptr;
                ptr = new byte[demandLength];
                System.arraycopy(src, 0, ptr, 0, ptr.length);
            }
            this.ptr = ptr;
            return this;
        }

        /**
         * Set register address
         *
         * @param ptr Register address
         */
        public Builder setPtr(int ptr) {
            byte[] btPtr = ArrayUtils.intToByteArray(ptr);
            return setPtr(btPtr);
        }

        /**
         * Set register address
         *
         * @param hexPtr Register address
         */
        public Builder setPtr(String hexPtr) {
            Objects.requireNonNull(hexPtr);
            if (CheckUtils.isNotHexString(hexPtr)) {
                throw new InvalidParameterException("hexPtr must be a hexadecimal string!");
            }
            byte[] ptr = ArrayUtils.hexStringToBytes(hexPtr);
            return setPtr(ptr);
        }

        /**
         * Create the {@link MtReadRegister} instance using the configured values.
         */
        public MtReadRegister build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setPasswords(passwords);
            }
            if (ptr == null) {
                byte[] btPtr = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setPtr(btPtr);
            }
            return new MtReadRegister(this);
        }
    }
}
