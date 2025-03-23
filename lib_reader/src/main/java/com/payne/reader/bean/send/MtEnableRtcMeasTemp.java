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
public class MtEnableRtcMeasTemp implements TempLabel2Info {
    private final byte[] info;

    MtEnableRtcMeasTemp(Builder builder) {
        int pwLen = builder.passwords.length;
        this.info = new byte[pwLen + 17];
        this.info[0] = TempLabel2Flag.ENABLE_RTC_MEAS_TEMP;
        System.arraycopy(builder.passwords, 0, info, 1, pwLen);
        this.info[pwLen + 1] = (byte) (builder.tLoggerOpt0 >> 24);
        this.info[pwLen + 2] = (byte) (builder.tLoggerOpt0 >> 16);
        this.info[pwLen + 3] = (byte) (builder.tLoggerOpt0 >> 8);
        this.info[pwLen + 4] = (byte) builder.tLoggerOpt0;
        this.info[pwLen + 5] = (byte) (builder.tLoggerOpt1 >> 24);
        this.info[pwLen + 6] = (byte) (builder.tLoggerOpt1 >> 16);
        this.info[pwLen + 7] = (byte) (builder.tLoggerOpt1 >> 8);
        this.info[pwLen + 8] = (byte) builder.tLoggerOpt1;
        this.info[pwLen + 9] = (byte) (builder.tLoggerOpt2 >> 24);
        this.info[pwLen + 10] = (byte) (builder.tLoggerOpt2 >> 16);
        this.info[pwLen + 11] = (byte) (builder.tLoggerOpt2 >> 8);
        this.info[pwLen + 12] = (byte) builder.tLoggerOpt2;
        this.info[pwLen + 13] = (byte) (builder.tLoggerOpt3 >> 24);
        this.info[pwLen + 14] = (byte) (builder.tLoggerOpt3 >> 16);
        this.info[pwLen + 15] = (byte) (builder.tLoggerOpt3 >> 8);
        this.info[pwLen + 16] = (byte) builder.tLoggerOpt3;
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
        private int tLoggerOpt0;
        private int tLoggerOpt1;
        private int tLoggerOpt2;
        private int tLoggerOpt3;

        public Builder() {
            this.passwords = null;
            this.tLoggerOpt0 = 0;
            this.tLoggerOpt1 = 0;
            this.tLoggerOpt2 = 0;
            this.tLoggerOpt3 = 0;
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
            byte[] identifiers = ArrayUtils.hexStringToBytes(hexPassword);
            return setPasswords(identifiers);
        }

        /**
         * Set delay temperature measurement time
         *
         * @param delayMeasTime Delay temperature measurement time
         * @return this
         */
        public Builder setDelayMeasTime(short delayMeasTime) {
            this.tLoggerOpt0 &= ((delayMeasTime << 16) | 0xFFFF);
            return this;
        }

        /**
         * Set temperature measurement interval
         *
         * @param measInterval Temperature measurement interval
         * @return this
         */
        public Builder setMeasInterval(short measInterval) {
            this.tLoggerOpt0 &= ((measInterval & 0xFFFF) | 0xFFFF0000);
            return this;
        }

        /**
         * Set the upper temperature limit
         *
         * @param max Upper temperature limit
         * @return this
         */
        public Builder setTempMax(short max) {
            this.tLoggerOpt1 &= ((max << 16) | 0xFFFF);
            return this;
        }

        /**
         * Set lower temperature limit
         *
         * @param min Lower temperature limit
         * @return this
         */
        public Builder setTempMin(short min) {
            this.tLoggerOpt1 &= ((min & 0xFFFF) | 0xFFFF0000);
            return this;
        }

        /**
         * Set reserved fields
         *
         * @param reserve reserved
         * @return this
         */
        public Builder setReserve(short reserve) {
            this.tLoggerOpt2 &= ((reserve << 19) | 0x0007FFFF);
            return this;
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
         * Set the number of measurements
         *
         * @param measCount Number of measurements
         * @return this
         */
        public Builder setMeasCount(short measCount) {
            this.tLoggerOpt2 &= ((measCount & 0xFFFF) | 0xFFFF0000);
            return this;
        }

        /**
         * Set tLoggerOpt3
         *
         * @param tLoggerOpt3 tLoggerOpt3
         * @return this
         */
        public Builder settLoggerOpt3(int tLoggerOpt3) {
            this.tLoggerOpt3 = tLoggerOpt3;
            return this;
        }

        /**
         * Create the {@link MtEnableRtcMeasTemp} instance using the configured values.
         *
         * @return instance
         */
        public MtEnableRtcMeasTemp build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setPasswords(passwords);
            }
            return new MtEnableRtcMeasTemp(this);
        }
    }
}
