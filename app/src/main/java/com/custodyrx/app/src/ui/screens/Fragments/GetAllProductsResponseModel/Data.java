package com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("data")
    @Expose
    private List<ProductData> productData;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private int items;

    public List<ProductData> getProductData() {
        return productData;
    }

    public void setProductData(List<ProductData> productData) {
        this.productData = productData;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }
}
