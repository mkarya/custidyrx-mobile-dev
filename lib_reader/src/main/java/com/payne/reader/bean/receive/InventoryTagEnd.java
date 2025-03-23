package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/2/19
 */
public class InventoryTagEnd extends Success {

    /**
     * working antenne
     */
    private int currentAnt;
    /**
     * Reading speed of tag
     */
    private int readRate;
    /**
     * accumulate Data returned
     */
    private int totalRead;
    /**
     * Number of labels (the total number of labels that are not repeated after one inventory),
     * The tag Count defaults to -1, which means that the field data is invalid
     */
    private int tagCount;

    public InventoryTagEnd() {
        this.currentAnt = -1;
        this.readRate = 0;
        this.totalRead = 0;
        this.tagCount = -1;
    }

    public int getCurrentAnt() {
        return currentAnt;
    }

    /**
     * Instead of setAntId
     */
    @Deprecated
    public void setCurrentAnt(int currentAnt) {
        this.currentAnt = currentAnt;
    }

    public void setAntId(int currentAnt) {
        this.currentAnt = currentAnt;
    }

    public int getReadRate() {
        return readRate;
    }

    public void setReadRate(int readRate) {
        this.readRate = readRate;
    }

    public int getTotalRead() {
        return totalRead;
    }

    public void setTotalRead(int totalRead) {
        this.totalRead = totalRead;
    }

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }

    @Override
    public String toString() {
        return "InventoryTagEnd{" +
                "currentAnt=" + currentAnt +
                ", readRate=" + readRate +
                ", totalRead=" + totalRead +
                ", tagCount=" + tagCount +
                '}';
    }
}
