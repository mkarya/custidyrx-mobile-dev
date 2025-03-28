package com.custodyrx.library.label.model;

import java.util.ArrayList;
import java.util.List;

public class TDCodeTagBuffer {
    /**
     * The 2D code information entity
     */
    public static class BinDCodeTagMap{
        //It is null at most time.
        private String type;
        public String mBarCodeValue;
        public BinDCodeTagMap(){
            type = "";
            mBarCodeValue = "";
        }
        public BinDCodeTagMap(String type, String barCodeValue){
            this.type = type;
            mBarCodeValue = barCodeValue;
        }
    }
    private List<String> mRawData;
    private  List<BinDCodeTagMap> lsTagList;
    public TDCodeTagBuffer(){
        lsTagList = new ArrayList<BinDCodeTagMap>();
        mRawData = new ArrayList<String>();
    }

    /**
     * Get the collection of raw data.
     * @return
     */
    public synchronized List<String> getmRawData(){
        return mRawData;
    }
    /**
     * Get the collection of BinDCodeTagMap.
     * @return
     */
    public List<BinDCodeTagMap>  getIsTagList(){
        List<String> strings = getmRawData();
        if (strings.size() != 0) {
            lsTagList.clear();
            for (String string : strings) {
                lsTagList.add(new BinDCodeTagMap("",string));
            }
        }
        return lsTagList;
    }
    /**
     * clear the Buffer.
     */
    public final void clearBuffer() {
        lsTagList.clear();
        mRawData.clear();
    }
}