package com.payne.reader.base;

/**
 * @author naz
 * Date 2020/9/1
 */
public abstract class BaseInventory {
    /**
     * Get the byte array parameters of the inventory command
     *
     * @return byte[]
     */
    public abstract byte[] getInventoryParams();

    /**
     * Get the high eight-antenna byte array parameters of the inventory command
     * (the sixteen antenna inventory is divided into two groups of eight antennas
     * to inventory separately, this command is used to obtain the parameters of
     * another group of eight antenna inventory)
     *
     * @return byte[]
     */
    public abstract byte[] getHighEightAntennaInventoryParams();

    /**
     * Whether to enable the phase function
     *
     * @return bool
     */
    public abstract boolean enablePhase();
}
