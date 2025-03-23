package com.custodyrx.library.label.bean;

import com.payne.reader.base.BaseInventory;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.config.Session;
import com.payne.reader.bean.send.CustomSessionTargetInventory;
import com.payne.reader.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author naz
 * Date 2021/1/4
 */
public class InventoryParam {
    private AntennaCount antennaCount;
    private BaseInventory inventory;
    private final List<Integer> customSessionIds;
    private boolean isFastSwitch;
    private int lastAntIndex;

    public InventoryParam() {
        this.antennaCount = AntennaCount.SINGLE_CHANNEL;
        this.inventory = new CustomSessionTargetInventory.Builder().session(Session.S0).build();
        this.customSessionIds = new ArrayList<>();
        this.customSessionIds.add(0);
        this.isFastSwitch = false;
        this.lastAntIndex = 0;
    }

    public AntennaCount getAntennaCount() {
        return antennaCount;
    }

    public void setAntennaCount(AntennaCount antennaCount) {
        this.antennaCount = antennaCount;
    }

    public BaseInventory getInventory() {
        return inventory;
    }

    public void setInventory(BaseInventory inventory) {
        this.inventory = inventory;
    }

    public boolean isFastSwitch() {
        return isFastSwitch;
    }

    public void setFastSwitch(boolean fastSwitch) {
        isFastSwitch = fastSwitch;
    }

    public void clearCustomSessionIds() {
        this.customSessionIds.clear();
    }

    public void addCustomSessionId(int antId) {
        if (!customSessionIds.contains(antId)) {
            this.customSessionIds.add(antId);
        }
    }

    public boolean hasCheckedAnts() {
        return customSessionIds.size() > 0;
    }

    public int getAntennaId(boolean next) {
        if (!hasCheckedAnts()) {
            return -1;
        }
        if (next) {
            if (lastAntIndex == customSessionIds.size() - 1) {
                this.lastAntIndex = 0;
            } else {
                this.lastAntIndex++;
            }
        } else {
            this.lastAntIndex = 0;
        }
        return customSessionIds.get(lastAntIndex);
    }

    public boolean isLastAnt() {
        return lastAntIndex == customSessionIds.size() - 1;
    }

    @Override
    public String toString() {
        StringBuilder idu = new StringBuilder(32);
        for (Integer customSessionId : customSessionIds) {
            idu.append(customSessionId);
            idu.append(",");
        }
        if (idu.length() > 0) {
            idu.deleteCharAt(idu.length() - 1);
        }
        return "InventoryParam{" +
                "antennaCount=[" + antennaCount.name() +
                "], inventory=[" + ArrayUtils.bytesToHexString(inventory.getInventoryParams(), 0, inventory.getInventoryParams().length) +
                "], customSessionIds=[" + idu.toString() +
                "], isFastSwitch=[" + isFastSwitch +
                "], lastIndex=[" + lastAntIndex +
                "]}";
    }
}
