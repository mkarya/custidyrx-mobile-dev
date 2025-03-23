package com.payne.reader.bean.send;

import com.payne.reader.bean.config.LockMemBank;
import com.payne.reader.bean.config.LockType;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class LockConfig {
    private byte[] lockInfo;

    LockConfig(Builder builder) {
        int pwLen = builder.passwords.length;
        this.lockInfo = new byte[pwLen + 2];
        System.arraycopy(builder.passwords, 0, lockInfo, 0, pwLen);
        this.lockInfo[pwLen] = builder.memBank;
        this.lockInfo[pwLen + 1] = builder.lockType;
    }

    /**
     * Get the lock label configuration
     *
     * @return byte[]
     */
    public byte[] getLockInfo() {
        return lockInfo;
    }

    /**
     * Build a new {@link LockConfig}.
     */
    public static class Builder {
        private byte[] passwords;
        private byte memBank;
        private byte lockType;

        public Builder() {
            this.passwords = null;
            this.memBank = LockMemBank.EPC_MEMORY.getValue();
            this.lockType = LockType.OPEN.getValue();
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
         * Set tag memory bank
         *
         * @param memBank see{@link LockMemBank}
         * @return this
         */
        public Builder setMemBank(LockMemBank memBank) {
            Objects.requireNonNull(memBank);
            this.memBank = memBank.getValue();
            return this;
        }

        /**
         * Set lock label type
         *
         * @param lockType see{@link LockType}
         * @return this
         */
        public Builder setLockType(LockType lockType) {
            Objects.requireNonNull(lockType);
            this.lockType = lockType.getValue();
            return this;
        }

        /**
         * Create the {@link LockConfig} instance using the configured values.
         *
         * @return instance
         */
        public LockConfig build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF
                };
                setPasswords(passwords);
            }
            return new LockConfig(this);
        }
    }
}
