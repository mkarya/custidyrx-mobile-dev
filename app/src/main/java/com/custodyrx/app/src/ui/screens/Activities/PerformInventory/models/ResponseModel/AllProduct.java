package com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel;

import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Items;

import java.util.List;

public class AllProduct {
    private String name;
    private int inventoryQuantity;
    private String guid;
    private int totalUnitCount;

    private List<Items> productItems;


    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getInventoryQuantity() { return inventoryQuantity; }
    public void setInventoryQuantity(int inventoryQuantity) { this.inventoryQuantity = inventoryQuantity; }

    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }

    public int getTotalUnitCount() { return totalUnitCount; }
    public void setTotalUnitCount(int totalUnitCount) { this.totalUnitCount = totalUnitCount; }

    public List<Items> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<Items> productItems) {
        this.productItems = productItems;
    }
}
