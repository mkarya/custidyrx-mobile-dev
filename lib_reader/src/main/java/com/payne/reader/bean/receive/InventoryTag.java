package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/2/20
 */
public class InventoryTag extends Success {

    /**
     * PC value
     */
    private String pc;
    /**
     * EPC value
     */
    private String epc;
    /**
     * antenna ID
     */
    private int antId;
    /**
     * RSSI value
     */
    private int rssi;
    /**
     * Phase value
     */
    private int phase;
    /**
     * carrier frequency
     */
    private String freq;

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public int getAntId() {
        return antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    @Override
    public String toString() {
        return "InventoryTag{" +
                "pc='" + pc + '\'' +
                ", epc='" + epc + '\'' +
                ", antId=" + antId +
                ", rssi=" + rssi +
                ", phase=" + phase +
                ", freq='" + freq + '\'' +
                '}';
    }
}
