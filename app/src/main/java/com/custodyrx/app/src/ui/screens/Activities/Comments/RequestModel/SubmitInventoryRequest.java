package com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel;

import java.util.List;

public class SubmitInventoryRequest {
    private String userGuid;
    private String inventoryType;
    private String locationGuid;
    private String totalQty;
    private String comments;
    private String timestamp;
    private List<Products> products;

    public SubmitInventoryRequest(String userGuid, String inventoryType, String locationGuid, String totalQty, String comments, String timestamp, List<Products> products) {
        this.userGuid = userGuid;
        this.inventoryType = inventoryType;
        this.locationGuid = locationGuid;
        this.totalQty = totalQty;
        this.comments = comments;
        this.timestamp = timestamp;
        this.products = products;
    }


}
