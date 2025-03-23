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
public class MtAuth implements TempLabel2Info {
    private final byte[] info;

    MtAuth(Builder builder) {
        int pwLen = builder.passwords.length;
        int measLen = builder.authPwd.length;
        this.info = new byte[pwLen + measLen + 1];
        this.info[0] = TempLabel2Flag.AUTH;
        System.arraycopy(builder.passwords, 0, info, 1, pwLen);
        System.arraycopy(builder.authPwd, 0, info, 1 + pwLen, measLen);
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
        private byte[] authPwd;

        public Builder() {
            this.passwords = null;
            this.authPwd = null;
        }

        /**
         * Set the access password,4 bytes.
         *
         * @param passwords Byte array password
         * @return this
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
         * @return this
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
         * @return this
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
         * Create the {@link MtAuth} instance using the configured values.
         *
         * @return instance
         */
        public MtAuth build() {
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
            return new MtAuth(this);
        }
    }
}
