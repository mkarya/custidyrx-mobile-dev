package com.payne.reader.bean.send;

import com.payne.reader.base.BaseInventory;

/**
 * @author naz
 * Date 2020/9/1
 */
public class BufferInventory extends BaseInventory {
    /**
     * Main parameter configuration of custom inventory
     */
    private byte[] inventoryParams;
    /**
     * Whether to open the phase function
     */
    private boolean enablePhase;

    BufferInventory(byte repeat) {
        this.inventoryParams = new byte[]{repeat};
        this.enablePhase = false;
    }

    @Override
    public byte[] getInventoryParams() {
        return inventoryParams;
    }

    @Override
    public byte[] getHighEightAntennaInventoryParams() {
        return inventoryParams;
    }

    @Override
    public boolean enablePhase() {
        return enablePhase;
    }

    /**
     * Build a new {@link BufferInventory}.
     */
    public static class Builder {
        private byte repeat;

        public Builder() {
            this.repeat = 1;
        }

        /**
         * Repeat time of inventory round. When Repeat = 255, The
         * inventory duration is minimized. For example, if the RF field
         * only has one or two tags, the inventory duration could be only
         * 30-50 mS, this function provides a possibility for fast
         * antenna switch applications on multi-ant devices.
         *
         * @param repeat byte
         * @return this
         */
        public Builder repeat(byte repeat) {
            this.repeat = repeat;
            return this;
        }

        /**
         * Create the {@link BufferInventory} instance using the configured values.
         *
         * @return instance
         */
        public BufferInventory build() {
            return new BufferInventory(repeat);
        }
    }
}
