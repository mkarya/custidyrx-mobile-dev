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
public class MtLedControl implements TempLabel2Info {
    private final byte[] info;

    MtLedControl(Builder builder) {
        int pwLen = builder.passwords.length;
        this.info = new byte[pwLen + 5];
        this.info[0] = TempLabel2Flag.LED_CONTROL;
        System.arraycopy(builder.passwords, 0, info, 1, pwLen);
        this.info[pwLen + 1] = (byte) (builder.tLoggerOpt2 >> 24);
        this.info[pwLen + 2] = (byte) (builder.tLoggerOpt2 >> 16);
        this.info[pwLen + 3] = (byte) (builder.tLoggerOpt2 >> 8);
        this.info[pwLen + 4] = (byte) builder.tLoggerOpt2;
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

        public Builder() {
            this.passwords = null;
            this.tLoggerOpt2 = 0;
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
         * Set Led on and off
         *
         * @param enable Is it bright
         * @return this
         */
        public Builder setBright(boolean enable) {
            if (enable) {
                this.tLoggerOpt2 = 0x00080000;
            } else {
                this.tLoggerOpt2 = 0x00000000;
            }
            return this;
        }

        /**
         * Create the {@link MtLedControl} instance using the configured values.
         *
         * @return instance
         */
        public MtLedControl build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setPasswords(passwords);
            }
            return new MtLedControl(this);
        }
    }
}
