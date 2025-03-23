package com.payne.reader.bean.send;

import com.payne.reader.base.BaseInventory;
import com.payne.reader.bean.config.SelectFlag;
import com.payne.reader.bean.config.Session;
import com.payne.reader.bean.config.SwitchType;
import com.payne.reader.bean.config.Target;

import java.util.Objects;

/**
 * @author naz
 * Date 2020/9/1
 */
public class CustomSessionTargetInventory extends BaseInventory {
    /**
     * Main parameter configuration of custom inventory
     */
    private final byte[] inventoryParams;
    /**
     * Whether to open the phase function
     */
    private final boolean enablePhase;

    CustomSessionTargetInventory(byte session, byte target, byte selectFlag,
                                 byte phase, byte powerSave, byte repeat) {
        this.enablePhase = SwitchType.getValue(true) == phase;
        if (powerSave != 0) {
            if (SelectFlag.DISABLE.getValue() == selectFlag) {
                selectFlag = SelectFlag.SL0.getValue();
            }
            this.inventoryParams = new byte[]{
                    session, target, selectFlag, phase, powerSave, repeat
            };
        } else if (enablePhase) {
            if (SelectFlag.DISABLE.getValue() == selectFlag) {
                selectFlag = SelectFlag.SL0.getValue();
            }
            this.inventoryParams = new byte[]{
                    session, target, selectFlag, phase, repeat
            };
        } else if (SelectFlag.DISABLE.getValue() != selectFlag) {
            this.inventoryParams = new byte[]{
                    session, target, selectFlag, repeat
            };
        } else {
            this.inventoryParams = new byte[]{
                    session, target, repeat
            };
        }
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
     * Build a new {@link CustomSessionTargetInventory}.
     */
    public static class Builder {
        private byte session;
        private byte target;
        private byte selectFlag;
        private byte phase;
        private byte powerSave;
        private byte repeat;

        public Builder() {
            this.session = Session.S1.getValue();
            this.target = Target.A.getValue();
            this.selectFlag = SelectFlag.DISABLE.getValue();
            this.phase = SwitchType.getValue(false);
            this.powerSave = 0;
            this.repeat = 1;
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
         * Select Flag
         *
         * @param selectFlag refer{@link SelectFlag}
         * @return this
         */
        public Builder selectFlag(SelectFlag selectFlag) {
            Objects.requireNonNull(selectFlag, "selectFlag == null");
            this.selectFlag = selectFlag.getValue();
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
         * The delay before the instruction returns to inventory is successful, the unit is 2 ms/1 bit. (Recommended value: 100)
         *
         * @param powerSave byte
         * @return this
         */
        public Builder powerSave(byte powerSave) {
            this.powerSave = powerSave;
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
         * Create the {@link CustomSessionTargetInventory} instance using the configured values.
         *
         * @return instance
         */
        public CustomSessionTargetInventory build() {
            return new CustomSessionTargetInventory(
                    session, target, selectFlag,
                    phase, powerSave, repeat);
        }
    }
}
