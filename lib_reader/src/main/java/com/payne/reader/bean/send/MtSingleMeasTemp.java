package com.payne.reader.bean.send;

import com.payne.reader.base.TempLabel2Info;
import com.payne.reader.bean.config.TagMeasOpt;
import com.payne.reader.bean.config.TempLabel2Flag;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.CheckUtils;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author naz
 * Date 2020/12/18
 */
public class MtSingleMeasTemp implements TempLabel2Info {
    private final byte[] info;

    MtSingleMeasTemp(Builder builder) {
        int pwLen = builder.passwords.length;
        int measLen = builder.tagMeasOpt.length;
        this.info = new byte[pwLen + measLen + 1];
        this.info[0] = TempLabel2Flag.SINGLE_MEAS_TEMP;
        System.arraycopy(builder.passwords, 0, info, 1, pwLen);
        System.arraycopy(builder.tagMeasOpt, 0, info, 1 + pwLen, measLen);
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

    public TagMeasOpt getTagMeasOpt() {
        int startIndex = info.length - 4;
        int value = ArrayUtils.byteArrayToInt(info, startIndex, 4);
        return TagMeasOpt.valueOf(value);
    }

    /**
     * Build a new {@link CustomSessionReadConfig}.
     */
    public static class Builder {
        private byte[] passwords;
        private byte[] tagMeasOpt;

        public Builder() {
            this.passwords = null;
            this.tagMeasOpt = null;
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
            byte[] identifiers = ArrayUtils.hexStringToBytes(hexPassword);
            return setPasswords(identifiers);
        }

        /**
         * Set label temperature measurement options
         *
         * @param tagMeasOpt label temperature measurement options
         * @return this
         */
        public Builder setTagMeasOpt(byte[] tagMeasOpt) {
            Objects.requireNonNull(tagMeasOpt);
            int demandLength = 4;
            if (tagMeasOpt.length < demandLength) {
                byte[] src = tagMeasOpt;
                tagMeasOpt = new byte[demandLength];
                System.arraycopy(src, 0, tagMeasOpt, 0, src.length);
            } else if (tagMeasOpt.length > demandLength) {
                byte[] src = tagMeasOpt;
                tagMeasOpt = new byte[demandLength];
                System.arraycopy(src, 0, tagMeasOpt, 0, tagMeasOpt.length);
            }
            this.tagMeasOpt = tagMeasOpt;
            return this;
        }

        /**
         * Set label temperature measurement options
         *
         * @param opt reference{@link TagMeasOpt}
         * @return this
         */
        public Builder setTagMeasOpt(TagMeasOpt opt) {
            Objects.requireNonNull(opt);
            return setTagMeasOpt(ArrayUtils.intToByteArray(opt.getValue()));
        }

        /**
         * Create the {@link MtSingleMeasTemp} instance using the configured values.
         */
        public MtSingleMeasTemp build() {
            if (passwords == null) {
                byte[] passwords = new byte[]{
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00,
                        (byte) 0x00
                };
                setPasswords(passwords);
            }
            if (tagMeasOpt == null) {
                setTagMeasOpt(TagMeasOpt.MeasTemp);
            }
            return new MtSingleMeasTemp(this);
        }
    }
}
