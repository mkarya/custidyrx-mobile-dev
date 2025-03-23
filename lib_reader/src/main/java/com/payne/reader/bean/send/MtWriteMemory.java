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
public class MtWriteMemory implements TempLabel2Info {
    private final byte[] info;

    MtWriteMemory(Builder builder) {
        int pwLen = builder.passwords.length;
        int authPwdLen = builder.authPwd.length;
        int dataLen = builder.data.length;
        this.info = new byte[pwLen + authPwdLen + dataLen + 8];
        this.info[0] = TempLabel2Flag.WRITE_MEMORY;
        System.arraycopy(builder.passwords, 0, info, 1, pwLen);
        this.info[pwLen + 1] = (byte) (builder.tLoggerOpt2 >> 24);
        this.info[pwLen + 2] = (byte) (builder.tLoggerOpt2 >> 16);
        this.info[pwLen + 3] = (byte) (builder.tLoggerOpt2 >> 8);
        this.info[pwLen + 4] = (byte) builder.tLoggerOpt2;
        System.arraycopy(builder.authPwd, 0, info, pwLen + 5, authPwdLen);
        this.info[pwLen + authPwdLen + 5] = (byte) (builder.startAddress >> 8);
        this.info[pwLen + authPwdLen + 6] = (byte) builder.startAddress;
        this.info[pwLen + authPwdLen + 7] = builder.count;
        System.arraycopy(builder.data, 0, info, pwLen + authPwdLen + 8, dataLen);
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
        private int tLoggerOpt2;
        private byte[] authPwd;
        private short startAddress;
        private byte count;
        private byte[] data;

        public Builder() {
            this.passwords = null;
            this.tLoggerOpt2 = 0;
            this.authPwd = null;
            this.startAddress = 0;
            this.count = 4;
            this.data = null;
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
            byte[] identifiers = ArrayUtils.hexStringToBytes(hexPassword);
            return setPasswords(identifiers);
        }

        /**
         * Set the type of secret key
         *
         * @param secretKeyType Key type
         * @return this
         */
        public Builder setSecretKeyType(byte secretKeyType) {
            this.tLoggerOpt2 &= ((secretKeyType << 16) | ~(0x7 << 16));
            return this;
        }

        /**
         * Set authentication password
         *
         * @param authPwd Authentication password
         * @return this
         */
        public Builder setAuthPwd(byte[] authPwd) {
            Objects.requireNonNull(authPwd);
            int demandLength = 4;
            if (authPwd.length < demandLength) {
                byte[] src = authPwd;
                authPwd = new byte[demandLength];
                System.arraycopy(src, 0, authPwd, 0, src.length);
            } else if (authPwd.length > demandLength) {
                byte[] src = authPwd;
                authPwd = new byte[demandLength];
                System.arraycopy(src, 0, authPwd, 0, authPwd.length);
            }
            this.authPwd = authPwd;
            return this;
        }

        /**
         * Set authentication password
         *
         * @param hexAuthPwd Hexadecimal string password
         */
        public Builder setAuthPwd(String hexAuthPwd) {
            Objects.requireNonNull(hexAuthPwd);
            if (CheckUtils.isNotHexString(hexAuthPwd)) {
                throw new InvalidParameterException("hexPassword must be a hexadecimal string!");
            }
            byte[] passwords = ArrayUtils.hexStringToBytes(hexAuthPwd);
            return setAuthPwd(passwords);
        }

        /**
         * The starting address to be read
         *
         * @param startAddress start address
         * @return this
         */
        public Builder setStartAddress(short startAddress) {
            this.startAddress = startAddress;
            return this;
        }

        /**
         * Need to read the length
         *
         * @param count data length
         * @return this
         */
        public Builder setCount(byte count) {
            this.count = count;
            return this;
        }

        /**
         * Set the data to be written
         *
         * @param data data
         * @return this
         */
        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        /**
         * Create the {@link MtWriteMemory} instance using the configured values.
         */
        public MtWriteMemory build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setPasswords(passwords);
            }
            if (authPwd == null) {
                byte[] authPwd = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setAuthPwd(authPwd);
            }
            if (data == null) {
                byte[] btData = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setData(btData);
            }
            return new MtWriteMemory(this);
        }
    }
}
