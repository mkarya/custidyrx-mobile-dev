package com.payne.reader.bean.send;

import java.security.InvalidParameterException;

/**
 * @author naz
 * Date 2020/7/22
 */
public class FreqUserDefine {
    private int freqStart;
    private int freqInterval;
    private byte freqQuantity;

    FreqUserDefine(Builder builder) {
        this.freqStart = builder.freqStart;
        this.freqInterval = builder.freqInterval;
        this.freqQuantity = builder.freqQuantity;
    }

    /**
     * Get start frequency,
     * for example: freqStart = 866000, freqEnd = freqStart + freqInterval * freqQuantity
     *
     * @return start frequency
     */
    public int getFreqStart() {
        return freqStart;
    }

    /**
     * Get frequency interval
     *
     * @return Frequency interval
     */
    public int getFreqInterval() {
        return freqInterval;
    }

    /**
     * Get frequency points, this quantity includes the start
     * frequency, if set this byte to 1, it means use start frequency
     * as the single carrier frequency . This byte should be larger
     * than 0
     *
     * @return Frequency points
     */
    public byte getFreqQuantity() {
        return freqQuantity;
    }

    /**
     * Build a new {@link FreqUserDefine}.
     */
    public static class Builder {
        private int freqStart;
        private int freqInterval;
        private byte freqQuantity;

        public Builder() {
            this.freqStart = 902000;
            this.freqInterval = 500;
            this.freqQuantity = 52;
        }

        /**
         * Set start frequency,
         * for example: freqStart = 866000, freqEnd = freqStart + freqInterval * freqQuantity
         *
         * @param freqStart start frequency
         */
        public Builder setFreqStart(int freqStart) {
            if (freqStart <= 0) {
                throw new InvalidParameterException("Start frequency must be greater than zero");
            }
            this.freqStart = freqStart;
            return this;
        }

        /**
         * Set frequency interval
         *
         * @param freqInterval Frequency interval
         * @return this
         */
        public Builder setFreqInterval(int freqInterval) {
            if (freqInterval <= 0) {
                throw new InvalidParameterException("Frequency interval must be greater than zero");
            }
            this.freqInterval = freqInterval;
            return this;
        }

        /**
         * Set frequency points, this quantity includes the start
         * frequency, if set this byte to 1, it means use start frequency
         * as the single carrier frequency . This byte should be larger
         * than 0
         *
         * @param freqQuantity Frequency points
         * @return this
         */
        public Builder setFreqQuantity(byte freqQuantity) {
            this.freqQuantity = freqQuantity;
            return this;
        }

        /**
         * Create the {@link FreqUserDefine} instance using the configured values.
         *
         * @return instance
         */
        public FreqUserDefine build() {
            return new FreqUserDefine(this);
        }
    }

    @Override
    public String toString() {
        return "FreqUserDefine{" +
                "freqStart=" + freqStart +
                ", freqInterval=" + freqInterval +
                ", freqQuantity=" + (freqQuantity & 0xFF) +
                '}';
    }
}
