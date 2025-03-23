package com.payne.reader.bean.receive;

import com.payne.reader.bean.config.MaskAction;
import com.payne.reader.bean.config.MaskId;
import com.payne.reader.bean.config.MaskTarget;
import com.payne.reader.bean.config.MemBank;

/**
 * @author naz
 * Date 2020/8/5
 */
public class MaskInfo extends Success {
    private MaskId maskId;
    private byte maskQuantity;
    private MaskTarget maskTarget;
    private MaskAction maskAction;
    private MemBank memBank;
    private byte maskBitStartAddress;
    private byte maskBitLength;
    private String maskValue;
    private byte truncate;

    public MaskId getMaskId() {
        return maskId;
    }

    public void setMaskId(MaskId maskId) {
        this.maskId = maskId;
    }

    /**
     * Indicates how many mask(s) has (have) been set.
     *
     * @return byte
     */
    public byte getMaskQuantity() {
        return maskQuantity;
    }

    public void setMaskQuantity(byte maskQuantity) {
        this.maskQuantity = maskQuantity;
    }

    public MaskTarget getMaskTarget() {
        return maskTarget;
    }

    public void setMaskTarget(MaskTarget maskTarget) {
        this.maskTarget = maskTarget;
    }

    public MaskAction getMaskAction() {
        return maskAction;
    }

    public void setMaskAction(MaskAction maskAction) {
        this.maskAction = maskAction;
    }

    public MemBank getMemBank() {
        return memBank;
    }

    public void setMemBank(MemBank memBank) {
        this.memBank = memBank;
    }

    public byte getMaskBitStartAddress() {
        return maskBitStartAddress;
    }

    public MaskInfo setMaskBitStartAddress(byte maskBitStartAddress) {
        this.maskBitStartAddress = maskBitStartAddress;
        return this;
    }

    public byte getMaskBitLength() {
        return maskBitLength;
    }

    public MaskInfo setMaskBitLength(byte maskBitLength) {
        this.maskBitLength = maskBitLength;
        return this;
    }

    public String getMaskValue() {
        return maskValue;
    }

    public void setMaskValue(String maskValue) {
        this.maskValue = maskValue;
    }

    public byte getTruncate() {
        return truncate;
    }

    public void setTruncate(byte truncate) {
        this.truncate = truncate;
    }

    @Override
    public String toString() {
        return "MaskInfo{" +
                "maskId=" + maskId.toString() +
                ", maskQuantity=" + (maskQuantity & 0xFF) +
                ", maskTarget=" + maskTarget.toString() +
                ", maskAction=" + maskAction.toString() +
                ", memBank=" + memBank.toString() +
                ", maskBitStartAddress=" + (maskBitStartAddress & 0xFF) +
                ", maskBitLength=" + (maskBitLength & 0xFF) +
                ", maskValue='" + maskValue + '\'' +
                ", truncate=" + (truncate & 0xFF) +
                '}';
    }
}
