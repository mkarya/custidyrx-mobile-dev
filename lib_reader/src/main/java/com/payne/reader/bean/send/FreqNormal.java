package com.payne.reader.bean.send;

import com.payne.reader.bean.config.Freq;
import com.payne.reader.bean.config.Region;

import java.util.Objects;

/**
 * @author naz
 * Date 2020/7/22
 */
public class FreqNormal {
    private Region region;
    private Freq freqStart;
    private Freq freqEnd;

    FreqNormal(Builder builder) {
        this.region = builder.region;
        this.freqStart = builder.freqStart;
        this.freqEnd = builder.freqEnd;
    }

    /**
     * Get frequency region
     *
     * @return see{@link Region}
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Get frequency start value,Start frequency of the spectrum
     *
     * @return see{@link Freq}
     */
    public Freq getFreqStart() {
        return freqStart;
    }

    /**
     * Get end frequency of the spectrum,Setup the range of the RF output
     * spectrum. The rules are: 1,Start frequency and end frequency
     * should be in the range of the specified regulation. 2,Start
     * frequency should be equal or lower than end frequency. 3, End
     * frequency equals start frequency means use single frequency
     * point.
     *
     * @return see{@link Freq}
     */
    public Freq getFreqEnd() {
        return freqEnd;
    }

    /**
     * Build a new {@link FreqNormal}.
     */
    public static class Builder {
        private Region region;
        private Freq freqStart;
        private Freq freqEnd;

        public Builder() {
            this.region = Region.FCC;
            this.freqStart = Freq._902000;
            this.freqEnd = Freq._928000;
        }

        /**
         * Set frequency region
         *
         * @param region see{@link Region}
         */
        public Builder setRegion(Region region) {
            Objects.requireNonNull(region);
            this.region = region;
            return this;
        }

        /**
         * Set frequency start value,Start frequency of the spectrum
         *
         * @param freqStart see{@link Freq}
         * @return this
         */
        public Builder setFreqStart(Freq freqStart) {
            Objects.requireNonNull(freqStart);
            this.freqStart = freqStart;
            return this;
        }

        /**
         * Set end frequency of the spectrum,Setup the range of the RF output
         * spectrum. The rules are: 1,Start frequency and end frequency
         * should be in the range of the specified regulation. 2,Start
         * frequency should be equal or lower than end frequency. 3, End
         * frequency equals start frequency means use single frequency
         * point.
         *
         * @param freqEnd see{@link Freq}
         * @return this
         */
        public Builder setFreqEnd(Freq freqEnd) {
            Objects.requireNonNull(freqEnd);
            this.freqEnd = freqEnd;
            return this;
        }

        /**
         * Create the {@link FreqNormal} instance using the configured values.
         *
         * @return instance
         */
        public FreqNormal build() {
            return new FreqNormal(this);
        }
    }

    @Override
    public String toString() {
        return "FreqNormal{" +
                "region=" + region.toString() +
                ", freqStart=" + freqStart.toString() +
                ", freqEnd=" + freqEnd.toString() +
                '}';
    }
}
