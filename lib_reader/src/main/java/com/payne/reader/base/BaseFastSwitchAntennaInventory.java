package com.payne.reader.base;

import com.payne.reader.bean.config.SwitchType;

/**
 * @author naz
 * Date 2020/9/1
 */
public abstract class BaseFastSwitchAntennaInventory extends BaseInventory {
    /**
     * Main parameter configuration of custom inventory
     */
    private byte[] inventoryParams;
    /**
     * Save the high eight-antenna byte array parameters of the inventory command
     * (the sixteen antenna inventory is divided into two groups of eight antennas
     * to inventory separately, this command is used to obtain the parameters of
     * another group of eight antenna inventory)
     */
    private byte[] highEightAntennaInventoryParams;
    /**
     * Whether to open the phase function
     */
    private boolean enablePhase;

    protected BaseFastSwitchAntennaInventory(byte antennaA, byte stayA, byte antennaB, byte stayB,
                                             byte antennaC, byte stayC, byte antennaD, byte stayD,
                                             byte antennaE, byte stayE, byte antennaF, byte stayF,
                                             byte antennaG, byte stayG, byte antennaH, byte stayH,
                                             byte interval, byte reserve1, byte reserve2,
                                             byte reserve3, byte reserve4, byte reserve5,
                                             byte session, byte target, byte optimize, byte ongoing,
                                             byte targetQuantity, byte phase, byte repeat) {
        this.inventoryParams = new byte[]{
                antennaA, stayA, antennaB, stayB,
                antennaC, stayC, antennaD, stayD,
                antennaE, stayE, antennaF, stayF,
                antennaG, stayG, antennaH, stayH,
                interval, reserve1, reserve2,
                reserve3, reserve4, reserve5,
                session, target, optimize, ongoing,
                targetQuantity, phase, repeat
        };
        this.highEightAntennaInventoryParams = inventoryParams;
        this.enablePhase = phase == SwitchType.OPEN.getValue();
    }

    protected BaseFastSwitchAntennaInventory(byte antennaA, byte stayA, byte antennaB, byte stayB,
                                             byte antennaC, byte stayC, byte antennaD, byte stayD,
                                             byte antennaE, byte stayE, byte antennaF, byte stayF,
                                             byte antennaG, byte stayG, byte antennaH, byte stayH,
                                             byte antennaI, byte stayI, byte antennaJ, byte stayJ,
                                             byte antennaK, byte stayK, byte antennaL, byte stayL,
                                             byte antennaM, byte stayM, byte antennaN, byte stayN,
                                             byte antennaO, byte stayO, byte antennaP, byte stayP,
                                             byte interval, byte reserve1, byte reserve2,
                                             byte reserve3, byte reserve4, byte reserve5,
                                             byte session, byte target, byte optimize, byte ongoing,
                                             byte targetQuantity, byte phase, byte repeat) {
        this.inventoryParams = new byte[]{
                antennaA, stayA, antennaB, stayB,
                antennaC, stayC, antennaD, stayD,
                antennaE, stayE, antennaF, stayF,
                antennaG, stayG, antennaH, stayH,
                interval, reserve1, reserve2,
                reserve3, reserve4, reserve5,
                session, target, optimize, ongoing,
                targetQuantity, phase, repeat
        };
        this.highEightAntennaInventoryParams = new byte[]{
                antennaI, stayI, antennaJ, stayJ,
                antennaK, stayK, antennaL, stayL,
                antennaM, stayM, antennaN, stayN,
                antennaO, stayO, antennaP, stayP,
                interval, reserve1, reserve2,
                reserve3, reserve4, reserve5,
                session, target, optimize, ongoing,
                targetQuantity, phase, repeat
        };
        this.enablePhase = phase == SwitchType.OPEN.getValue();
    }

    @Override
    public byte[] getInventoryParams() {
        return inventoryParams;
    }

    @Override
    public byte[] getHighEightAntennaInventoryParams() {
        return highEightAntennaInventoryParams;
    }

    @Override
    public boolean enablePhase() {
        return enablePhase;
    }
}
