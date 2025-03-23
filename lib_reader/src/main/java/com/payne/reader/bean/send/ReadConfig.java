package com.payne.reader.bean.send;

import com.payne.reader.bean.config.MemBank;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class ReadConfig {
    private byte[] readInfo;

    ReadConfig(Builder builder) {
        int pwLen = builder.passwords.length;
        this.readInfo = new byte[3 + pwLen];
        this.readInfo[0] = builder.memBank;
        this.readInfo[1] = builder.wordStartAddress;
        this.readInfo[2] = builder.wordLength;
        System.arraycopy(builder.passwords, 0, readInfo, 3, pwLen);
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
     * Build a new {@link ReadConfig}.
     */
    public static class Builder {
        private byte[] passwords;
        private byte memBank;
        private byte wordStartAddress;
        private byte wordLength;

        public Builder() {
            this.passwords = null;
            this.memBank = MemBank.EPC.getValue();
            this.wordStartAddress = 0x00;
            this.wordLength = 0x00;
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
            byte[] password = ArrayUtils.hexStringToBytes(hexPassword);
            return setPasswords(password);
        }

        /**
         * Set tag memory bank
         *
         * @param memBank see{@link MemBank}
         */
        public Builder setMemBank(MemBank memBank) {
            Objects.requireNonNull(memBank);
            this.memBank = memBank.getValue();
            return this;
        }

        /**
         * Set tag memory bank
         *
         * @param memBank see{@link MemBank}
         */
        public Builder setMemBankByte(byte memBank) {
            this.memBank = memBank;
            return this;
        }

        /**
         * Read data start address, please see the tag's spec for more
         * information.
         *
         * @param wordStartAddress Read data start address
         */
        public Builder setWordStartAddress(byte wordStartAddress) {
            this.wordStartAddress = wordStartAddress;
            return this;
        }

        /**
         * Read data length,Data length in WORD(16bits) unit. Please see
         * the tag's spec for more information.
         *
         * @param wordLength Read data length,Data length in WORD(16bits) unit
         */
        public Builder setWordLength(byte wordLength) {
            this.wordLength = wordLength;
            return this;
        }

        /**
         * Create the {@link ReadConfig} instance using the configured values.
         */
        public ReadConfig build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF
                };
                setPasswords(passwords);
            }
            return new ReadConfig(this);
        }
    }
}
