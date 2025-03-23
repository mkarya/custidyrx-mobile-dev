package com.custodyrx.library.label.bean;

import androidx.annotation.NonNull;

import com.payne.reader.bean.receive.OperationTag;

/**
 * @author naz
 * Date 2020/4/3
 */
public class ReadJoharBean {
    private OperationTag tagBean;
    private String trueEpc;
    private String temperature;
    private int times;

    public ReadJoharBean(@NonNull OperationTag bean, String temperature) {
        this.times = 0;
        setTagBean(bean, temperature);
    }

    public void setTagBean(OperationTag tagBean, String temperature) {
        this.times++;
        this.tagBean = tagBean;
        this.temperature = temperature;
        String epc = tagBean.getStrEpc();
        if (epc.length() > 5) {
            trueEpc = epc.substring(0, 5);
        } else {
            trueEpc = epc;
        }
    }

    public String getEpc() {
        return trueEpc;
    }

    public String getPc() {
        return tagBean.getStrPc();
    }

    public String getCrc() {
        return tagBean.getStrCrc();
    }

    public String getTemperature() {
        return temperature;
    }

    public int getAntId() {
        return tagBean.getAntId();
    }

    public String getTimes() {
        return String.valueOf(times);
    }
}
