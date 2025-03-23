package com.payne.reader.bean.send;

import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class KillConfig {
    private byte[] killInfo;

    KillConfig(Builder builder) {
        int pwLen = builder.passwords.length;
        this.killInfo = new byte[pwLen];
        System.arraycopy(builder.passwords, 0, killInfo, 0, pwLen);
    }

    /**
     * Get the kill label configuration
     *
     * @return byte[]
     */
    public byte[] getKillInfo() {
        return killInfo;
    }

    /**
     * Build a new {@link KillConfig}.
     */
    public static class Builder {
        private byte[] passwords;

        public Builder() {
            this.passwords = null;
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
            byte[] password = ArrayUtils.hexStringToBytes(hexPassword);
            return setPasswords(password);
        }

        /**
         * Create the {@link KillConfig} instance using the configured values.
         *
         * @return instance
         */
        public KillConfig build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF
                };
                setPasswords(passwords);
            }
            return new KillConfig(this);
        }
    }
}
