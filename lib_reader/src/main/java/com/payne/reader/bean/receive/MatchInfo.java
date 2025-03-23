package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/8/5
 */
public class MatchInfo extends Success {

    /**
     * match epc value
     */
    private String matchEpcValue;

    public String getMatchEpcValue() {
        return matchEpcValue;
    }

    public void setMatchEpcValue(String matchEpcValue) {
        this.matchEpcValue = matchEpcValue;
    }
}
