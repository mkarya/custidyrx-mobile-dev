package com.payne.reader.bean.send;

import com.payne.reader.bean.config.MaskAction;
import com.payne.reader.bean.config.MaskId;
import com.payne.reader.bean.config.MaskTarget;
import com.payne.reader.bean.config.MemBank;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class MaskConfig {
    private byte[] maskInfo;

    MaskConfig(Builder builder) {
        int valueLen = builder.maskValue.length;
        this.maskInfo = new byte[7 + valueLen];
        this.maskInfo[0] = builder.function;
        this.maskInfo[1] = builder.target;
        this.maskInfo[2] = builder.action;
        this.maskInfo[3] = builder.memBank;
        this.maskInfo[4] = builder.maskBitStartAddress;
        this.maskInfo[5] = builder.maskBitLength;
        System.arraycopy(builder.maskValue, 0, maskInfo, 6, valueLen);
        this.maskInfo[maskInfo.length - 1] = builder.truncate;
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
     * Build a new {@link MaskConfig}.
     */
    public static class Builder {
        private byte function;
        private byte target;
        private byte action;
        private byte memBank;
        private byte maskBitStartAddress;
        private byte maskBitLength;
        private byte[] maskValue;
        private byte truncate;

        public Builder() {
            this.function = MaskId.TAG_MASK_NO4.getValue();
            this.target = MaskTarget.Inventoried_S0.getValue();
            this.action = MaskAction.Action0.getValue();
            this.memBank = MemBank.EPC.getValue();
            this.maskBitStartAddress = 0x00;
            this.maskBitLength = 0x00;
            this.maskValue = null;
            this.truncate = 0x00;
        }

        /**
         * Set the mask function
         *
         * @param function see{@link MaskId}
         * @return this
         */
        public Builder setFunction(MaskId function) {
            Objects.requireNonNull(function);
            this.function = function.getValue();
            return this;
        }

        /**
         * Set the inventory way
         *
         * @param target see{@link MaskTarget}
         * @return this
         */
        public Builder setTarget(MaskTarget target) {
            Objects.requireNonNull(target);
            this.target = target.getValue();
            return this;
        }

        /**
         * Set the match tag or not Action
         *
         * @param action see{@link MaskAction}
         * @return this
         */
        public Builder setAction(MaskAction action) {
            Objects.requireNonNull(action);
            this.action = action.getValue();
            return this;
        }

        /**
         * Set the select mask region
         *
         * @param memBank see{@link MemBank}
         * @return this
         */
        public Builder setMemBank(MemBank memBank) {
            Objects.requireNonNull(memBank);
            this.memBank = memBank.getValue();
            return this;
        }

        /**
         * Mask data start address, Data length in bit unit. please see
         * the tag's spec for more information.
         *
         * @param maskBitStartAddress Mask data start address
         * @return this
         */
        public Builder setMaskBitStartAddress(byte maskBitStartAddress) {
            this.maskBitStartAddress = maskBitStartAddress;
            return this;
        }

        /**
         * Mask data length, Data length in bit unit. Please see
         * the tag's spec for more information.
         *
         * @param maskBitLength Mask data length
         * @return this
         */
        public Builder setMaskBitLength(byte maskBitLength) {
            this.maskBitLength = maskBitLength;
            return this;
        }

        /**
         * Mask value
         *
         * @param maskValue byte[]
         * @return this
         */
        public Builder setMaskValue(byte[] maskValue) {
            Objects.requireNonNull(maskValue);
            this.maskValue = maskValue;
            return this;
        }

        /**
         * Mask value
         *
         * @param hexMaskValue Hexadecimal string mask value
         * @return this
         */
        public Builder setMaskValue(String hexMaskValue) {
            Objects.requireNonNull(hexMaskValue);
            if (CheckUtils.isNotHexString(hexMaskValue)) {
                throw new InvalidParameterException("hexMaskValue must be a hexadecimal string!");
            }
            byte[] data = ArrayUtils.hexStringToBytes(hexMaskValue);
            return setMaskValue(data);
        }

        /**
         * Set truncate
         *
         * @param truncate byte
         * @return this
         */
        public Builder setTruncate(byte truncate) {
            this.truncate = truncate;
            return this;
        }

        /**
         * Create the {@link MaskConfig} instance using the configured values.
         *
         * @return instance
         */
        public MaskConfig build() {
            if (maskValue == null) {
                this.maskValue = new byte[maskBitLength >>> 3];
            } else if (maskBitLength == (byte) 0) {
                int len = maskValue.length;
                this.maskBitLength = (byte) (len << 3);
            }
            return new MaskConfig(this);
        }
    }
}
