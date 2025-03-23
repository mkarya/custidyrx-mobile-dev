package com.payne.reader.bean.send;

import com.payne.reader.bean.config.ReadMode;
import com.payne.reader.bean.config.Session;
import com.payne.reader.bean.config.Target;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class CustomSessionReadConfig {
    private byte[] readInfo;

    CustomSessionReadConfig(Builder builder) {
        int pwLen = builder.passwords.length;
        this.readInfo = new byte[pwLen + 10];
        this.readInfo[0] = builder.resStartAddress;
        this.readInfo[1] = builder.resLength;
        this.readInfo[2] = builder.tidStartAddress;
        this.readInfo[3] = builder.tidLength;
        this.readInfo[4] = builder.userStartAddress;
        this.readInfo[5] = builder.userLength;
        System.arraycopy(builder.passwords, 0, readInfo, 6, pwLen);
        this.readInfo[pwLen + 6] = builder.session;
        this.readInfo[pwLen + 7] = builder.target;
        this.readInfo[pwLen + 8] = builder.readMode;
        this.readInfo[pwLen + 9] = builder.timeout;
    }

    /**
     * Get the read label configuration
     *
     * @return byte[]
     */
    public byte[] getReadInfo() {
        return readInfo;
    }

    /**
     * Build a new {@link CustomSessionReadConfig}.
     */
    public static class Builder {
        private byte[] passwords;
        private byte resStartAddress;
        private byte resLength;
        private byte tidStartAddress;
        private byte tidLength;
        private byte userStartAddress;
        private byte userLength;
        private byte session;
        private byte target;
        private byte readMode;
        private byte timeout;

        public Builder() {
            this.passwords = null;
            this.resStartAddress = 0x00;
            this.resLength = 0x00;
            this.tidStartAddress = 0x00;
            this.tidLength = 0x00;
            this.userStartAddress = 0x00;
            this.userLength = 0x00;
            this.session = Session.S0.getValue();
            this.target = Target.A.getValue();
            this.readMode = ReadMode.MODE1.getValue();
            this.timeout = 0x05;
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
         * Set the starting address of the password area, Default 0
         *
         * @param resStartAddress Password area start address
         * @return this
         */
        public Builder setResStartAddress(byte resStartAddress) {
            this.resStartAddress = resStartAddress;
            return this;
        }

        /**
         * Set the read length of the password area, Default 0
         *
         * @param resLength byte
         * @return this
         */
        public Builder setResLength(byte resLength) {
            this.resLength = resLength;
            return this;
        }

        /**
         * Set the start address of the TID area, Default 0
         *
         * @param tidStartAddress byte
         * @return this
         */
        public Builder setTidStartAddress(byte tidStartAddress) {
            this.tidStartAddress = tidStartAddress;
            return this;
        }

        /**
         * Set the read length of the TID area, Default 0
         *
         * @param tidLength byte
         * @return this
         */
        public Builder setTidLength(byte tidLength) {
            this.tidLength = tidLength;
            return this;
        }

        /**
         * Set the start address of the user area, Default 0
         *
         * @param userStartAddress byte
         * @return this
         */
        public Builder setUserStartAddress(byte userStartAddress) {
            this.userStartAddress = userStartAddress;
            return this;
        }

        /**
         * Set user area read length, Default 0
         *
         * @param userLength byte
         * @return this
         */
        public Builder setUserLength(byte userLength) {
            this.userLength = userLength;
            return this;
        }

        /**
         * Set inventory session, Default Session.S1
         *
         * @param session see{@link Session}
         * @return this
         */
        public Builder setSession(Session session) {
            Objects.requireNonNull(session);
            this.session = session.getValue();
            return this;
        }

        /**
         * Set inventory target, Default Target.A
         *
         * @param target see{@link Target}
         * @return this
         */
        public Builder setTarget(Target target) {
            Objects.requireNonNull(target);
            this.target = target.getValue();
            return this;
        }

        /**
         * Set read label mode, Default ReadMode.MODE1
         *
         * @param readMode see{@link ReadMode}
         * @return this
         */
        public Builder setReadMode(ReadMode readMode) {
            Objects.requireNonNull(readMode);
            this.readMode = readMode.getValue();
            return this;
        }

        /**
         * Set tag response timeout control, unit: ms, default value: 5ms
         *
         * @param timeout byte
         * @return this
         */
        public Builder setTimeout(byte timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Create the {@link CustomSessionReadConfig} instance using the configured values.
         *
         * @return instance
         */
        public CustomSessionReadConfig build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF
                };
                setPasswords(passwords);
            }
            return new CustomSessionReadConfig(this);
        }
    }
}
