package com.custodyrx.app.src.ui.screens.Activities.SelectLocation.models.GetAllLocationResponseModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("data")
    @Expose
    private List<LocationData> locationData;

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

    public List<LocationData> getLocationData() {
        return locationData;
    }

    public void setLocationData(List<LocationData> locationData) {
        this.locationData = locationData;
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
