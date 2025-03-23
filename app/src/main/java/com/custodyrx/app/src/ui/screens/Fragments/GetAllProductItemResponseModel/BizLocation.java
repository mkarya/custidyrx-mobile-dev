package com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BizLocation {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("GUID")
    @Expose
    private String guid;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    @SerializedName("companyGuid")
    @Expose
    private String companyGuid;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("locationType")
    @Expose
    private String locationType;

    @SerializedName("vehicleUnitId")
    @Expose
    private String vehicleUnitId;

    @SerializedName("radioId")
    @Expose
    private String radioId;

    @SerializedName("licensePlateNumber")
    @Expose
    private String licensePlateNumber;

    @SerializedName("serviceDate")
    @Expose
    private String serviceDate;

    @SerializedName("vehicleType")
    @Expose
    private String vehicleType;

    @SerializedName("vehicleModel")
    @Expose
    private String vehicleModel;

    @SerializedName("vehicleModelYear")
    @Expose
    private int vehicleModelYear;

    @SerializedName("vehicleStatus")
    @Expose
    private String vehicleStatus;

    @SerializedName("vehicleFleetGuid")
    @Expose
    private String vehicleFleetGuid;

    @SerializedName("equipmentChecklistGuid")
    @Expose
    private String equipmentChecklistGuid;

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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCompanyGuid() {
        return companyGuid;
    }

    public void setCompanyGuid(String companyGuid) {
        this.companyGuid = companyGuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getVehicleUnitId() {
        return vehicleUnitId;
    }

    public void setVehicleUnitId(String vehicleUnitId) {
        this.vehicleUnitId = vehicleUnitId;
    }

    public String getRadioId() {
        return radioId;
    }

    public void setRadioId(String radioId) {
        this.radioId = radioId;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getVehicleModelYear() {
        return vehicleModelYear;
    }

    public void setVehicleModelYear(int vehicleModelYear) {
        this.vehicleModelYear = vehicleModelYear;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleFleetGuid() {
        return vehicleFleetGuid;
    }

    public void setVehicleFleetGuid(String vehicleFleetGuid) {
        this.vehicleFleetGuid = vehicleFleetGuid;
    }

    public String getEquipmentChecklistGuid() {
        return equipmentChecklistGuid;
    }

    public void setEquipmentChecklistGuid(String equipmentChecklistGuid) {
        this.equipmentChecklistGuid = equipmentChecklistGuid;
    }
}
