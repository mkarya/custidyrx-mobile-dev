package com.payne.reader.bean.receive;

/**
 * @author naz
 * Date 2020/7/22
 */
public class ReaderIdentifier extends Success {

    /**
     * Identifier
     */
    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "ReaderIdentifier{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
