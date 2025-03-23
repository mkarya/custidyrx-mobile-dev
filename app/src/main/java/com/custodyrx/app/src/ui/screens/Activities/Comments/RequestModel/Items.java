package com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel;

public class Items {
    private String itemGuid;
    private String itemEpc;
    private int unitCount;

    public Items(){

    }

    public Items(String itemGuid, String itemEpc, int unitCount) {
        this.itemGuid = itemGuid;
        this.itemEpc = itemEpc;
        this.unitCount = unitCount;
    }

    public String getItemGuid() {
        return itemGuid;
    }

    public void setItemGuid(String itemGuid) {
        this.itemGuid = itemGuid;
    }

    public String getItemEpc() {
        return itemEpc;
    }

    public void setItemEpc(String itemEpc) {
        this.itemEpc = itemEpc;
    }

    public int getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }
}
