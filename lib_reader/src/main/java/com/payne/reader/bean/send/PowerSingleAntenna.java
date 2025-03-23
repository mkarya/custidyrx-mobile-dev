package com.payne.reader.bean.send;

import com.payne.reader.base.BasePower;

/**
 * @author naz
 * Date 2020/9/3
 */
public final class PowerSingleAntenna extends BasePower {
    /**
     * Main parameter configuration of custom inventory
     */
    private byte[] powers;

    PowerSingleAntenna(byte power) {
        this.powers = new byte[]{power};
    }

    @Override
    public byte[] getPowers() {
        return powers;
    }

    @Override
    public byte[] getHighEightAntennaPowers() {
        return powers;
    }

    /**
     * Build a new {@link PowerSingleAntenna}.
     */
    public static class Builder {
        private byte power;

        public Builder() {
            this.power = 0x21;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder power(byte power) {
            this.power = power;
            return this;
        }

        /**
         * Create the {@link PowerSingleAntenna} instance using the configured values.
         */
        public PowerSingleAntenna build() {
            return new PowerSingleAntenna(power);
        }
    }
}
