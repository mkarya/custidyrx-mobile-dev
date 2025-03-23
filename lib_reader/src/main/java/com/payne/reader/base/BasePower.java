package com.payne.reader.base;

/**
 * @author naz
 * Date 2020/9/3
 */
public abstract class BasePower {
    /**
     * Get module power parameter value
     *
     * @return byte[]
     */
    public abstract byte[] getPowers();

    /**
     * Get the power parameter value of the high eight antenna module
     * (Dividing sixteen antennas into two groups to set the power,
     * each group of eight antennas set the power separately,
     * this command is used to obtain the power value of another group of eight antennas)
     *
     * @return byte[]
     */
    public abstract byte[] getHighEightAntennaPowers();
}
