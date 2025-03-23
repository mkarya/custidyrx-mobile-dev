package com.payne.reader.bean.send;

import com.payne.reader.base.BaseFastSwitchAntennaInventory;
import com.payne.reader.base.BaseInventory;
import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.Cmd;
import com.payne.reader.bean.receive.InventoryFailure;
import com.payne.reader.bean.receive.InventoryTag;
import com.payne.reader.bean.receive.InventoryTagEnd;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * Used to configure the entity class for saving inventory parameters.
 * When calling the start inventory, the corresponding inventory operation
 * will be performed according to the secondary parameter class
 *
 * @author naz
 * Date 2020/7/24
 */
public class InventoryConfig {
    private byte cmd;
    private byte[] inventoryParams;
    private byte[] highEightAntennaInventoryParams;
    private boolean isEnablePhase;
    private AntennaCount antennaCount;
    private Consumer<InventoryTag> onSuccess;
    private Consumer<InventoryTagEnd> onSuccessEnd;
    private Consumer<InventoryFailure> onFailure;

    private InventoryConfig(Builder builder) {
        this.cmd = builder.cmd;
        this.inventoryParams = builder.inventoryParams;
        this.highEightAntennaInventoryParams = builder.highEightAntennaInventoryParams;
        this.isEnablePhase = builder.isEnablePhase;
        this.antennaCount = builder.antennaCount;
        this.onSuccess = builder.onSuccess;
        this.onSuccessEnd = builder.onSuccessEnd;
        this.onFailure = builder.onFailure;
    }

    /**
     * Get the current operation command code
     *
     * @return byte
     */
    public byte getCmd() {
        return cmd;
    }

    /**
     * Get current inventory parameters
     *
     * @return byte[]
     */
    public byte[] getInventoryParams() {
        return inventoryParams;
    }

    /**
     * Get the high eight-antenna byte array parameters of the inventory command
     * (the sixteen antenna inventory is divided into two groups of eight antennas
     * to inventory separately, this command is used to obtain the parameters of
     * another group of eight antenna inventory)
     *
     * @return byte[]
     */
    public byte[] getHighEightAntennaInventoryParams() {
        return highEightAntennaInventoryParams;
    }

    /**
     * Whether the phase value function is turned on
     *
     * @return bool
     */
    public boolean isEnablePhase() {
        return isEnablePhase;
    }

    /**
     * Check whether the number of antennas currently initialized
     * is consistent with the number of incoming antennas (for fast multi-antenna inventory)
     *
     * @param count Number of incoming antennas
     * @return Is it consistent
     */
    public boolean checkAntennaCount(AntennaCount count) {
        if (cmd == Cmd.FAST_SWITCH_ANT_INVENTORY) {
            return antennaCount == count;
        }
        return true;
    }

    /**
     * Get inventory success callback
     *
     * @return Callback
     */
    public Consumer<InventoryTag> getOnSuccess() {
        return onSuccess;
    }

    /**
     * Get inventory success end callback
     *
     * @return Callback
     */
    public Consumer<InventoryTagEnd> getOnSuccessEnd() {
        return onSuccessEnd;
    }

    /**
     * Failed to get inventory callback
     *
     * @return Callback
     */
    public Consumer<InventoryFailure> getOnFailure() {
        return onFailure;
    }

    /**
     * Build a new {@link InventoryConfig}.
     */
    public static class Builder {
        private byte cmd;
        private byte[] inventoryParams;
        private byte[] highEightAntennaInventoryParams;
        private boolean isEnablePhase;
        private AntennaCount antennaCount;
        private Consumer<InventoryTag> onSuccess;
        private Consumer<InventoryTagEnd> onSuccessEnd;
        private Consumer<InventoryFailure> onFailure;

        public Builder() {
            this.cmd = 0;
            this.inventoryParams = null;
            this.highEightAntennaInventoryParams = null;
            this.isEnablePhase = false;
            this.antennaCount = AntennaCount.SINGLE_CHANNEL;
            this.onSuccess = null;
            this.onSuccessEnd = null;
            this.onFailure = null;
        }

        public Builder setInventory(BaseInventory baseInventory) {
            Objects.requireNonNull(baseInventory);
            if (baseInventory instanceof BufferInventory) {
                BufferInventory inventory = (BufferInventory) baseInventory;
                setInventory(inventory);
            } else if (baseInventory instanceof CustomSessionTargetInventory) {
                CustomSessionTargetInventory inventory = (CustomSessionTargetInventory) baseInventory;
                setInventory(inventory);
            } else if (baseInventory instanceof FastSwitchSingleAntennaInventory) {
                FastSwitchSingleAntennaInventory inventory = (FastSwitchSingleAntennaInventory) baseInventory;
                setInventory(inventory);
            } else if (baseInventory instanceof FastSwitchFourAntennaInventory) {
                FastSwitchFourAntennaInventory inventory = (FastSwitchFourAntennaInventory) baseInventory;
                setInventory(inventory);
            } else if (baseInventory instanceof FastSwitchEightAntennaInventory) {
                FastSwitchEightAntennaInventory inventory = (FastSwitchEightAntennaInventory) baseInventory;
                setInventory(inventory);
            } else if (baseInventory instanceof FastSwitchSixteenAntennaInventory) {
                FastSwitchSixteenAntennaInventory inventory = (FastSwitchSixteenAntennaInventory) baseInventory;
                setInventory(inventory);
            } else {
                throw new InvalidParameterException();
            }
            return this;
        }

        /**
         * Inventory Tag. <br>
         * When reader gets this command, the inventory for EPC GEN2 tags starts,
         * tag data will be stored in the internal buffer. <br>
         * Attention: <br>
         * When sets Repeat parameter to 255(0xFF), the anti-collision algorithm is
         * optimized for applications with small tag quantity, which provide better
         * efficiency and less response time.
         *
         * @param inventory Cache inventory mode, refer{@link BufferInventory}
         */
        public Builder setInventory(BufferInventory inventory) {
            Objects.requireNonNull(inventory);
            try {
                this.cmd = Cmd.INVENTORY;
                this.isEnablePhase = inventory.enablePhase();
                this.inventoryParams = inventory.getInventoryParams();
                this.highEightAntennaInventoryParams = inventory.getHighEightAntennaInventoryParams();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * Set the current inventory mode to custom session target inventory
         *
         * @param inventory Custom session target inventory, refer{@link CustomSessionTargetInventory}
         * @return this
         */
        public Builder setInventory(CustomSessionTargetInventory inventory) {
            Objects.requireNonNull(inventory);
            try {
                this.cmd = Cmd.CUSTOMIZED_SESSION_TARGET_INVENTORY;
                this.isEnablePhase = inventory.enablePhase();
                this.inventoryParams = inventory.getInventoryParams();
                this.highEightAntennaInventoryParams = inventory.getHighEightAntennaInventoryParams();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        // public Builder setInventory(FastInventory inventory) {
        //     Objects.requireNonNull(inventory);
        //     try {
        //         this.cmd = Cmd.FAST_INVENTORY;
        //         this.isEnablePhase = inventory.enablePhase();
        //         this.inventoryParams = inventory.getInventoryParams();
        //         this.highEightAntennaInventoryParams = inventory.getHighEightAntennaInventoryParams();
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        //     return this;
        // }

        /**
         * Set the current inventory mode to fast switch multi-antenna inventory
         *
         * @param inventory Quickly switch single-port inventory, refer{@link FastSwitchSingleAntennaInventory}
         * @return this
         */
        public Builder setInventory(FastSwitchSingleAntennaInventory inventory) {
            this.antennaCount = AntennaCount.SINGLE_CHANNEL;
            return configInventory(inventory);
        }

        /**
         * Set the current inventory mode to fast switch multi-antenna inventory
         *
         * @param inventory Quickly switch four-port inventory, refer{@link FastSwitchFourAntennaInventory}
         * @return this
         */
        public Builder setInventory(FastSwitchFourAntennaInventory inventory) {
            this.antennaCount = AntennaCount.FOUR_CHANNELS;
            return configInventory(inventory);
        }

        /**
         * Set the current inventory mode to fast switch multi-antenna inventory
         *
         * @param inventory Quickly switch eight-port inventory, refer{@link FastSwitchEightAntennaInventory}
         * @return this
         */
        public Builder setInventory(FastSwitchEightAntennaInventory inventory) {
            this.antennaCount = AntennaCount.EIGHT_CHANNELS;
            return configInventory(inventory);
        }

        /**
         * Set the current inventory mode to fast switch multi-antenna inventory
         *
         * @param inventory Quickly switch sixteen-port inventory, refer{@link FastSwitchSixteenAntennaInventory}
         * @return this
         */
        public Builder setInventory(FastSwitchSixteenAntennaInventory inventory) {
            this.antennaCount = AntennaCount.SIXTEEN_CHANNELS;
            return configInventory(inventory);
        }

        /**
         * Quickly switch the common implementation part of multi-antenna inventory
         *
         * @param inventory refer{@link BaseFastSwitchAntennaInventory}
         * @return this
         */
        private Builder configInventory(BaseFastSwitchAntennaInventory inventory) {
            Objects.requireNonNull(inventory);
            try {
                this.cmd = Cmd.FAST_SWITCH_ANT_INVENTORY;
                this.isEnablePhase = inventory.enablePhase();
                this.inventoryParams = inventory.getInventoryParams();
                this.highEightAntennaInventoryParams = inventory.getHighEightAntennaInventoryParams();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * Set inventory success result callback
         *
         * @param onSuccess Inventory success callback
         * @return this
         */
        public Builder setOnInventoryTagSuccess(Consumer<InventoryTag> onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }

        /**
         * Set inventory success result callback
         *
         * @param onSuccess Inventory success callback
         * @return this
         */
        public Builder setOnInventoryTagEndSuccess(Consumer<InventoryTagEnd> onSuccess) {
            this.onSuccessEnd = onSuccess;
            return this;
        }

        /**
         * Set inventory failure result callback
         *
         * @param onFailure Callback for rough disk failure
         * @return this
         */
        public Builder setOnFailure(Consumer<InventoryFailure> onFailure) {
            this.onFailure = onFailure;
            return this;
        }

        public Builder setFastInventory(boolean fast) {
            if (fast) {
                cmd = Cmd.FAST_INVENTORY;
                inventoryParams = new byte[]{0x00, 0x00};
            }
            return this;
        }

        /**
         * Create the {@link InventoryConfig} instance using the configured values.
         *
         * @return instance
         */
        public InventoryConfig build() {
            if (onFailure == null) {
                throw new InvalidParameterException("Unimplemented inventory failure callback");
            }
            if (inventoryParams == null) {
                throw new InvalidParameterException("InventoryParams is null");
            }
            return new InventoryConfig(this);
        }
    }
}