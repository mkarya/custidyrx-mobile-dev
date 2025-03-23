package com.payne.reader.bean.send;

import com.payne.reader.base.BasePower;
import com.payne.reader.bean.config.AntennaCount;

import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/10
 */
public class OutputPowerConfig {
    private byte[] powers;
    private byte[] highEightAntennaPowers;
    private AntennaCount antennaCount;

    private OutputPowerConfig() {
    }

    /**
     * Get the power that needs to be set
     *
     * @return byte[]
     */
    public byte[] getPowers() {
        return powers;
    }

    /**
     * Get the power of the high eight antennas to be set (the other set of sixteen antennas)
     *
     * @return byte[]
     */
    public byte[] getHighEightAntennaPowers() {
        return highEightAntennaPowers;
    }

    /**
     * Check whether the number of antennas currently initialized
     * is consistent with the number of incoming antennas (High eight antenna power of sixteen antennas)
     *
     * @param count Number of incoming antennas
     * @return Is it consistent
     */
    public boolean checkAntennaCount(AntennaCount count) {
        return antennaCount == count;
    }

    /**
     * Single antenna power
     *
     * @param power refer{@link PowerSingleAntenna}
     */
    public static OutputPowerConfig outputPower(PowerSingleAntenna power) {
        return configPowers(power, AntennaCount.SINGLE_CHANNEL);
    }

    /**
     * Four antenna power
     *
     * @param power refer{@link PowerFourAntenna}
     */
    public static OutputPowerConfig outputPower(PowerFourAntenna power) {
        return configPowers(power, AntennaCount.FOUR_CHANNELS);
    }

    /**
     * Eight antenna power
     *
     * @param power refer{@link PowerEightAntenna}
     */
    public static OutputPowerConfig outputPower(PowerEightAntenna power) {
        return configPowers(power, AntennaCount.EIGHT_CHANNELS);
    }

    /**
     * 16 antenna power
     *
     * @param power refer{@link PowerSixteenAntenna}
     */
    public static OutputPowerConfig outputPower(PowerSixteenAntenna power) {
        return configPowers(power, AntennaCount.SIXTEEN_CHANNELS);
    }

    /**
     * Configure power and other parameters
     *
     * @param power refer{@link BasePower}
     */
    private static OutputPowerConfig configPowers(BasePower power, AntennaCount antennaCount) {
        Objects.requireNonNull(power);
        OutputPowerConfig config = new OutputPowerConfig();
        config.powers = power.getPowers();
        config.highEightAntennaPowers = power.getHighEightAntennaPowers();
        config.antennaCount = antennaCount;
        return config;
    }
}
