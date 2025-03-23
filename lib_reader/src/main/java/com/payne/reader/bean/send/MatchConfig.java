package com.payne.reader.bean.send;

import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class MatchConfig {
    private byte[] maskInfo;

    MatchConfig(Builder builder) {
        int valueLen = builder.epcValues.length;
        this.maskInfo = new byte[2 + valueLen];
        this.maskInfo[0] = builder.mode;
        this.maskInfo[1] = builder.epcLength;
        System.arraycopy(builder.epcValues, 0, maskInfo, 2, valueLen);
    }

    /**
     * Get selected label configuration
     *
     * @return byte[]
     */
    public byte[] getMaskInfo() {
        return maskInfo;
    }

    /**
     * Build a new {@link MatchConfig}.
     */
    public static class Builder {
        private byte mode;
        private byte epcLength;
        private byte[] epcValues;

        public Builder() {
            this.mode = 0x00;
            this.epcLength = 0x00;
            this.epcValues = null;
        }

        /**
         * Epc value
         *
         * @param epcValues byte[]
         * @return this
         */
        public Builder setMaskValue(byte[] epcValues) {
            Objects.requireNonNull(epcValues);
            this.epcValues = epcValues;
            this.epcLength = (byte) epcValues.length;
            return this;
        }

        /**
         * Mask value
         *
         * @param hexEpcValues Hexadecimal string epc value
         * @return this
         */
        public Builder setMaskValue(String hexEpcValues) {
            Objects.requireNonNull(hexEpcValues);
            if (CheckUtils.isNotHexString(hexEpcValues)) {
                throw new InvalidParameterException("hexEpcValues must be a hexadecimal string!");
            }
            byte[] data = ArrayUtils.hexStringToBytes(hexEpcValues);
            return setMaskValue(data);
        }

        /**
         * Create the {@link MatchConfig} instance using the configured values.
         *
         * @return instance
         */
        public MatchConfig build() {
            if (epcValues == null) {
                this.epcValues = new byte[epcLength & 0xFF];
            }
            return new MatchConfig(this);
        }
    }
}
