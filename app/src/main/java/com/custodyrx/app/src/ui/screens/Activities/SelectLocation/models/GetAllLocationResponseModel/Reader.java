package com.custodyrx.app.src.ui.screens.Activities.SelectLocation.models.GetAllLocationResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reader {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("GUID")
    @Expose
    private String GUID;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("lastSeen")
    @Expose
    private String lastSeen;

    @SerializedName("connectonStatus")
    @Expose
    private int connectionStatus;

    @SerializedName("clientSecret")
    @Expose
    private String clientSecret;

    @SerializedName("bizLocationGuid")
    @Expose
    private String bizLocationGuid;

    @SerializedName("companyGuid")
    @Expose
    private String companyGuid;

    // Getters and Setters
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getBizLocationGuid() {
        return bizLocationGuid;
    }

    public void setBizLocationGuid(String bizLocationGuid) {
        this.bizLocationGuid = bizLocationGuid;
    }

    public String getCompanyGuid() {
        return companyGuid;
    }

    public void setCompanyGuid(String companyGuid) {
        this.companyGuid = companyGuid;
    }
}
