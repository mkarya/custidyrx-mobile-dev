package com.custodyrx.library.label.bean;


import com.custodyrx.library.label.GlobalCfg;

/**
 * @author naz
 * Date 2021/3/11
 */
public class SetPowerBean {
    private int power;
    private boolean valid = false;
    private int maxOutPower;

    public SetPowerBean() {
        maxOutPower = GlobalCfg.get().getMaxOutPower();
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
        this.valid = power > -1 && power <= maxOutPower;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public String toString() {
        return "SetPowerBean{" +
                "power=" + power +
                ", valid=" + valid +
                '}';
    }
}
