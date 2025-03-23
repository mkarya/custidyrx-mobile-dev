package com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("data")
    @Expose
    private List<ProductItemData> itemList;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("page")
    @Expose
    private int page;

    @SerializedName("rows")
    @Expose
    private int rows;

    @SerializedName("items")
    @Expose
    private int items;

    // Getters and Setters

    public List<ProductItemData> getItemList() {
        return itemList;
    }

    public void setItemList(List<ProductItemData> itemList) {
        this.itemList = itemList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }
}
