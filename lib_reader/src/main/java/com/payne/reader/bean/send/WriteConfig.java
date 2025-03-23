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
public final class WriteConfig {
    private final byte[] writeInfo;

    WriteConfig(Builder builder) {
        int pwLen = builder.passwords.length;
        int dataLen = builder.wordLength << 1;
        this.writeInfo = new byte[pwLen + 3 + dataLen];
        System.arraycopy(builder.passwords, 0, writeInfo, 0, pwLen);
        this.writeInfo[pwLen] = builder.memBank;
        this.writeInfo[pwLen + 1] = builder.wordStartAddress;
        this.writeInfo[pwLen + 2] = builder.wordLength;
        System.arraycopy(builder.writeData, 0, writeInfo, pwLen + 3, Math.min(dataLen, builder.writeData.length));
    }

    /**
     * Get the write label configuration
     *
     * @return byte[]
     */
    public byte[] getWriteInfo() {
        return writeInfo;
    }

    /**
     * Build a new {@link WriteConfig}.
     */
    public static class Builder {
        private byte[] passwords;
        private byte memBank;
        private byte wordStartAddress;
        /**
         * Write data length,Data length in WORD(16bits) unit. Please see
         * the tag's spec for more information.
         */
        private byte wordLength;
        private byte[] writeData;

        public Builder() {
            this.passwords = null;
            this.memBank = MemBank.EPC.getValue();
            this.wordStartAddress = 0x00;
            this.wordLength = 0x00;
            this.writeData = null;
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
         * Write data start address, please see the tag's spec for more
         * information.
         *
         * @param wordStartAddress byte
         */
        public Builder setWordStartAddress(byte wordStartAddress) {
            this.wordStartAddress = wordStartAddress;
            return this;
        }

        /**
         * Write data length,Data length in WORD(16bits) unit. Please see
         * the tag's spec for more information.
         *
         * @param wordLength Write data length,Data length in WORD(16bits) unit
         */
        public Builder setWordLength(byte wordLength) {
            this.wordLength = wordLength;
            return this;
        }

        /**
         * Set the data written to the tag.
         * If not {@link #setWordLength(byte)},
         * the length of the byte array must be an even number,
         * if it is a base, the last byte will be discarded by default.
         *
         * @param writeData byte[]
         */
        public Builder setWriteData(byte[] writeData) {
            Objects.requireNonNull(writeData);
            this.writeData = writeData;
            return this;
        }

        /**
         * Set the data written to the tag
         *
         * @param hexData Hexadecimal string data
         */
        public Builder setWriteData(String hexData) {
            Objects.requireNonNull(hexData);
            if (CheckUtils.isNotHexString(hexData)) {
                throw new InvalidParameterException("hexData must be a hexadecimal string!");
            }
            byte[] data = ArrayUtils.hexStringToBytes(hexData);
            return setWriteData(data);
        }

        /**
         * Create the {@link WriteConfig} instance using the configured values.
         */
        public WriteConfig build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF,
                        (byte) 0xFF
                };
                setPasswords(passwords);
            }
            if (writeData == null) {
                this.writeData = new byte[wordLength << 1];
            } else if (wordLength == (byte) 0) {
                int len = writeData.length;
                // Ensure that the length is always an even number
//                if ((len % 2) != 0) {
//                    len = len + 1;
//                }
                this.wordLength = (byte) ((len >>> 1) + (len & 0x01));
            }
            return new WriteConfig(this);
        }
    }
}
