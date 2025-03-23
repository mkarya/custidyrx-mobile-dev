package com.payne.reader.bean.send;

import com.payne.reader.base.BasePower;

/**
 * @author naz
 * Date 2020/8/19
 */
public class PowerEightAntenna extends BasePower {
    /**
     * The output power of the antenna array, range from 0 to 33(0x00 - 0x21),
     * the unit is dBm.
     */
    private final byte[] powers;

    PowerEightAntenna(byte[] powers) {
        this.powers = powers;
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
     * Build a new {@link PowerEightAntenna}.
     */
    public static class Builder {
        private byte[] powers;

        public Builder() {
            this.powers = new byte[8];
            this.powers[0] = 0x21;
            this.powers[1] = 0x21;
            this.powers[2] = 0x21;
            this.powers[3] = 0x21;
            this.powers[4] = 0x21;
            this.powers[5] = 0x21;
            this.powers[6] = 0x21;
            this.powers[7] = 0x21;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerA(byte power) {
            this.powers[0] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerB(byte power) {
            this.powers[1] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerC(byte power) {
            this.powers[2] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerD(byte power) {
            this.powers[3] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerE(byte power) {
            this.powers[4] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerF(byte power) {
            this.powers[5] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerG(byte power) {
            this.powers[6] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerH(byte power) {
            this.powers[7] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param powers bytes
         */
        public Builder powers(byte[] powers) {
            if (powers == null) {
                throw new NullPointerException();
            } else if (powers.length != this.powers.length) {
                throw new IllegalArgumentException();
            }
            this.powers = powers;
            return this;
        }

        /**
         * Create the {@link PowerEightAntenna} instance using the configured values.
         */
        public PowerEightAntenna build() {
            return new PowerEightAntenna(powers);
        }
    }
}
