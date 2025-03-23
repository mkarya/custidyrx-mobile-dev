package com.payne.reader.bean.send;

import com.payne.reader.base.BaseFastSwitchAntennaInventory;
import com.payne.reader.bean.config.FourAntenna;
import com.payne.reader.bean.config.Session;
import com.payne.reader.bean.config.SwitchType;
import com.payne.reader.bean.config.Target;

import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/1
 */
public final class FastSwitchFourAntennaInventory extends BaseFastSwitchAntennaInventory {

    FastSwitchFourAntennaInventory(byte antennaA, byte stayA, byte antennaB, byte stayB,
                                   byte antennaC, byte stayC, byte antennaD, byte stayD,
                                   byte antennaE, byte stayE, byte antennaF, byte stayF,
                                   byte antennaG, byte stayG, byte antennaH, byte stayH,
                                   byte interval, byte reserve1, byte reserve2,
                                   byte reserve3, byte reserve4, byte reserve5,
                                   byte session, byte target, byte optimize, byte ongoing,
                                   byte targetQuantity, byte phase, byte repeat) {
        super(antennaA, stayA, antennaB, stayB,
                antennaC, stayC, antennaD, stayD,
                antennaE, stayE, antennaF, stayF,
                antennaG, stayG, antennaH, stayH,
                interval, reserve1, reserve2,
                reserve3, reserve4, reserve5,
                session, target, optimize, ongoing,
                targetQuantity, phase, repeat);
    }

    /**
     * Build a new {@link FastSwitchFourAntennaInventory}.
     */
    public static final class Builder {
        private byte antennaA;
        private byte stayA;
        private byte antennaB;
        private byte stayB;
        private byte antennaC;
        private byte stayC;
        private byte antennaD;
        private byte stayD;
        private byte antennaE;
        private byte stayE;
        private byte antennaF;
        private byte stayF;
        private byte antennaG;
        private byte stayG;
        private byte antennaH;
        private byte stayH;
        private byte interval;
        private byte reserve1;
        private byte reserve2;
        private byte reserve3;
        private byte reserve4;
        private byte reserve5;
        private byte session;
        private byte target;
        private byte optimize;
        private byte ongoing;
        private byte targetQuantity;
        private byte phase;
        private byte repeat;

        public Builder() {
            this.antennaA = FourAntenna.ANT_A.getValue();
            this.stayA = 0x01;
            this.antennaB = FourAntenna.ANT_B.getValue();
            this.stayB = 0x01;
            this.antennaC = FourAntenna.ANT_C.getValue();
            this.stayC = 0x01;
            this.antennaD = FourAntenna.ANT_D.getValue();
            this.stayD = 0x01;
            this.antennaE = FourAntenna.NONE.getValue();
            this.stayE = 0x00;
            this.antennaF = FourAntenna.NONE.getValue();
            this.stayF = 0x00;
            this.antennaG = FourAntenna.NONE.getValue();
            this.stayG = 0x00;
            this.antennaH = FourAntenna.NONE.getValue();
            this.stayH = 0x00;
            this.interval = 0x00;
            this.reserve1 = 0x00;
            this.reserve2 = 0x00;
            this.reserve3 = 0x00;
            this.reserve4 = 0x00;
            this.reserve5 = 0x00;
            this.session = Session.S0.getValue();
            this.target = Target.A.getValue();
            this.optimize = 0x00;
            this.ongoing = 0x00;
            this.targetQuantity = 0x00;
            this.phase = SwitchType.getValue(false);
            this.repeat = 0x01;
        }

        /**
         * Antenna to poll first
         *
         * @param antennaA refer{@link FourAntenna}
         * @return this
         */
        public Builder antennaA(FourAntenna antennaA) {
            Objects.requireNonNull(antennaA, "antennaA == null");
            this.antennaA = antennaA.getValue();
            return this;
        }

        /**
         * The number of times the antenna repeats polling.
         * Each antenna can be configured individually
         *
         * @param stayA The number of repeated polling of the antenna
         * @return this
         */
        public Builder stayA(byte stayA) {
            this.stayA = stayA;
            return this;
        }

        /**
         * Second polled antenna
         *
         * @param antennaB refer{@link FourAntenna}
         * @return this
         */
        public Builder antennaB(FourAntenna antennaB) {
            Objects.requireNonNull(antennaB, "antennaB == null");
            this.antennaB = antennaB.getValue();
            return this;
        }

        /**
         * The number of times the antenna repeats polling.
         * Each antenna can be configured individually
         *
         * @param stayB The number of repeated polling of the antenna
         * @return this
         */
        public Builder stayB(byte stayB) {
            this.stayB = stayB;
            return this;
        }

        /**
         * The third polled antenna
         *
         * @param antennaC refer{@link FourAntenna}
         * @return this
         */
        public Builder antennaC(FourAntenna antennaC) {
            Objects.requireNonNull(antennaC, "antennaC == null");
            this.antennaC = antennaC.getValue();
            return this;
        }

        /**
         * The number of times the antenna repeats polling.
         * Each antenna can be configured individually
         *
         * @param stayC The number of repeated polling of the antenna
         * @return this
         */
        public Builder stayC(byte stayC) {
            this.stayC = stayC;
            return this;
        }

        /**
         * The fourth polled antenna
         *
         * @param antennaD refer{@link FourAntenna}
         * @return this
         */
        public Builder antennaD(FourAntenna antennaD) {
            Objects.requireNonNull(antennaD, "antennaD == null");
            this.antennaD = antennaD.getValue();
            return this;
        }

        /**
         * The number of times the antenna repeats polling.
         * Each antenna can be configured individually
         *
         * @param stayD The number of repeated polling of the antenna
         * @return this
         */
        public Builder stayD(byte stayD) {
            this.stayD = stayD;
            return this;
        }

        /**
         * Rest time between antennas. The unit is m S.
         * No RF output during rest, which can reduce power consumption.
         *
         * @param interval Rest time between antennas. The unit is m S
         * @return this
         */
        public Builder interval(byte interval) {
            this.interval = interval;
            return this;
        }

        /**
         * Reserved byte
         *
         * @param reserve1 Reserved byte
         * @return this
         */
        public Builder reserve1(byte reserve1) {
            this.reserve1 = reserve1;
            return this;
        }

        /**
         * Reserved byte
         *
         * @param reserve2 Reserved byte
         * @return this
         */
        public Builder reserve2(byte reserve2) {
            this.reserve2 = reserve2;
            return this;
        }

        /**
         * Reserved byte
         *
         * @param reserve3 Reserved byte
         * @return this
         */
        public Builder reserve3(byte reserve3) {
            this.reserve3 = reserve3;
            return this;
        }

        /**
         * Reserved byte
         *
         * @param reserve4 Reserved byte
         * @return this
         */
        public Builder reserve4(byte reserve4) {
            this.reserve4 = reserve4;
            return this;
        }

        /**
         * Reserved byte
         *
         * @param reserve5 Reserved byte
         * @return this
         */
        public Builder reserve5(byte reserve5) {
            this.reserve5 = reserve5;
            return this;
        }

        /**
         * Set specify the session for inventory
         *
         * @param session refer{@link Session}
         * @return this
         */
        public Builder session(Session session) {
            Objects.requireNonNull(session, "session == null");
            this.session = session.getValue();
            return this;
        }

        /**
         * Specify the Inventoried Flag for inventory
         *
         * @param target refer{@link Target}
         * @return this
         */
        public Builder target(Target target) {
            Objects.requireNonNull(target, "target == null");
            this.target = target.getValue();
            return this;
        }

        /**
         * A1~AF: optimize 1~15 times, real-time + cache;
         * 51~5F: optimize 1~15 times, only cache.
         * 00 is to close this function. Suggest to close
         *
         * @param optimize byte
         * @return this
         */
        public Builder optimize(byte optimize) {
            this.optimize = optimize;
            return this;
        }

        /**
         * 00 is to turn off this function, after turning it on,
         * it can be continuously inventoried without time interval;
         * A1~AF: execute 1~15 times. Suggested value: A1
         *
         * @param ongoing byte
         * @return this
         */
        public Builder ongoing(byte ongoing) {
            this.ongoing = ongoing;
            return this;
        }

        /**
         * The target number of the inventory label this time,
         * 00 means to close this function.
         *
         * @param targetQuantity byte
         * @return this
         */
        public Builder targetQuantity(byte targetQuantity) {
            this.targetQuantity = targetQuantity;
            return this;
        }

        /**
         * Open or close phase value
         *
         * @param enablePhase Whether to open the phase value
         * @return this
         */
        public Builder enablePhase(boolean enablePhase) {
            this.phase = SwitchType.getValue(enablePhase);
            return this;
        }


        /**
         * The number of repetitions of the inventory process.
         *
         * @param repeat byte
         * @return this
         */
        public Builder repeat(byte repeat) {
            this.repeat = repeat;
            return this;
        }

        /**
         * Create the {@link FastSwitchFourAntennaInventory} instance using the configured values.
         *
         * @return instance
         */
        public FastSwitchFourAntennaInventory build() {
            return new FastSwitchFourAntennaInventory(
                    antennaA, stayA, antennaB, stayB,
                    antennaC, stayC, antennaD, stayD,
                    antennaE, stayE, antennaF, stayF,
                    antennaG, stayG, antennaH, stayH,
                    interval, reserve1, reserve2,
                    reserve3, reserve4, reserve5,
                    session, target, optimize, ongoing,
                    targetQuantity, phase, repeat);
        }
    }
}
