package com.payne.reader.bean.send;

import com.payne.reader.base.BasePower;

/**
 * @author naz
 * Date 2020/8/19
 */
public class PowerSixteenAntenna extends BasePower {
    /**
     * The output power of the antenna array, range from 0 to 33(0x00 - 0x21),
     * the unit is dBm.
     */
    private final byte[] powers;
    private final byte[] highEightAntennaPowers;

    PowerSixteenAntenna(byte[] powers, byte[] highEightAntennaPowers) {
        this.powers = powers;
        this.highEightAntennaPowers = highEightAntennaPowers;
    }

    @Override
    public byte[] getPowers() {
        return powers;
    }

    @Override
    public byte[] getHighEightAntennaPowers() {
        return highEightAntennaPowers;
    }

    /**
     * Build a new {@link PowerSixteenAntenna}.
     */
    public static class Builder {
        private byte[] powers;

        public Builder() {
            this.powers = new byte[16];
            this.powers[0] = 0x21;
            this.powers[1] = 0x21;
            this.powers[2] = 0x21;
            this.powers[3] = 0x21;
            this.powers[4] = 0x21;
            this.powers[5] = 0x21;
            this.powers[6] = 0x21;
            this.powers[7] = 0x21;

            this.powers[8] = 0x21;
            this.powers[9] = 0x21;
            this.powers[10] = 0x21;
            this.powers[11] = 0x21;
            this.powers[12] = 0x21;
            this.powers[13] = 0x21;
            this.powers[14] = 0x21;
            this.powers[15] = 0x21;
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
         * @param power byte
         */
        public Builder powerI(byte power) {
            this.powers[8] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerJ(byte power) {
            this.powers[9] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerK(byte power) {
            this.powers[10] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerL(byte power) {
            this.powers[11] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerM(byte power) {
            this.powers[12] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerN(byte power) {
            this.powers[13] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerO(byte power) {
            this.powers[14] = power;
            return this;
        }

        /**
         * Output power, range from 0 to 33(0x00 - 0x21),
         * the unit is dBm.
         *
         * @param power byte
         */
        public Builder powerP(byte power) {
            this.powers[15] = power;
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
         * Create the {@link PowerSixteenAntenna} instance using the configured values.
         */
        public PowerSixteenAntenna build() {
            byte[] lowPowers = new byte[8];
            byte[] highEightAntennaPowers = new byte[8];
            System.arraycopy(powers, 0, lowPowers, 0, lowPowers.length);
            System.arraycopy(powers, lowPowers.length, highEightAntennaPowers, 0, highEightAntennaPowers.length);
            return new PowerSixteenAntenna(lowPowers, highEightAntennaPowers);
        }
    }
}
