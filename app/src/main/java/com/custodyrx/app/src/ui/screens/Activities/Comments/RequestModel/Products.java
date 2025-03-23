package com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel;

import java.util.List;

public class Products {
    private String productGuid;
    private String name;
    private List<Items> items;

    public Products(String productGuid, String name, List<Items> items) {
        this.productGuid = productGuid;
        this.name = name;
        this.items = items;
    }
}
