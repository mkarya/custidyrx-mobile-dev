package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/2/18
 */
public class WorkAntenna extends Success {

    /**
     * Current working antenna
     */
    private int workAntenna;

    public int getWorkAntenna() {
        return workAntenna;
    }

    public void setWorkAntenna(int workAntenna) {
        this.workAntenna = workAntenna;
    }

    @Override
    public String toString() {
        return "WorkAntenna{" +
                "workAntenna=" + workAntenna +
                '}';
    }
}
