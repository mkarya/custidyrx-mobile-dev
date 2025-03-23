package com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductItemData implements Serializable {
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

    @SerializedName("unitCount")
    @Expose
    private int unitCount;

    @SerializedName("itemEPC")
    @Expose
    private String itemEPC;

    @SerializedName("itemType")
    @Expose
    private String itemType;

    @SerializedName("serialNumber")
    @Expose
    private String serialNumber;

    @SerializedName("lotNumber")
    @Expose
    private String lotNumber;

    @SerializedName("expirationDate")
    @Expose
    private String expirationDate;

    @SerializedName("assetId")
    @Expose
    private String assetId;

    @SerializedName("manufactureDate")
    @Expose
    private String manufactureDate;

    @SerializedName("vendor")
    @Expose
    private String vendor;

    @SerializedName("vendorNumber")
    @Expose
    private String vendorNumber;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("department")
    @Expose
    private String department;

    @SerializedName("purchaseDate")
    @Expose
    private String purchaseDate;

    @SerializedName("lastCheckIn")
    @Expose
    private String lastCheckIn;

    @SerializedName("productGuid")
    @Expose
    private String productGuid;

    @SerializedName("bizLocationGuid")
    @Expose
    private String bizLocationGuid;

    @SerializedName("packingEPC")
    @Expose
    private String packingEPC;

    @SerializedName("bizLocation")
    @Expose
    private BizLocation bizLocation;

    public ProductItemData() {
        // Default constructor
    }

    public ProductItemData(
            String createdAt, String updatedAt, String guid, boolean isActive, String companyGuid,
            int unitCount, String itemEPC, String itemType, String serialNumber, String lotNumber,
            String expirationDate, String assetId, String manufactureDate, String vendor,
            String vendorNumber, String status, String department, String purchaseDate,
            String lastCheckIn, String productGuid, String bizLocationGuid, String packingEPC,
            BizLocation bizLocation) {

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.guid = guid;
        this.isActive = isActive;
        this.companyGuid = companyGuid;
        this.unitCount = unitCount;
        this.itemEPC = itemEPC;
        this.itemType = itemType;
        this.serialNumber = serialNumber;
        this.lotNumber = lotNumber;
        this.expirationDate = expirationDate;
        this.assetId = assetId;
        this.manufactureDate = manufactureDate;
        this.vendor = vendor;
        this.vendorNumber = vendorNumber;
        this.status = status;
        this.department = department;
        this.purchaseDate = purchaseDate;
        this.lastCheckIn = lastCheckIn;
        this.productGuid = productGuid;
        this.bizLocationGuid = bizLocationGuid;
        this.packingEPC = packingEPC;
        this.bizLocation = bizLocation;
    }


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

    public int getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }

    public String getItemEPC() {
        return itemEPC;
    }

    public void setItemEPC(String itemEPC) {
        this.itemEPC = itemEPC;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(String lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }

    public String getProductGuid() {
        return productGuid;
    }

    public void setProductGuid(String productGuid) {
        this.productGuid = productGuid;
    }

    public String getBizLocationGuid() {
        return bizLocationGuid;
    }

    public void setBizLocationGuid(String bizLocationGuid) {
        this.bizLocationGuid = bizLocationGuid;
    }

    public String getPackingEPC() {
        return packingEPC;
    }

    public void setPackingEPC(String packingEPC) {
        this.packingEPC = packingEPC;
    }

    public BizLocation getBizLocation() {
        return bizLocation;
    }

    public void setBizLocation(BizLocation bizLocation) {
        this.bizLocation = bizLocation;
    }
}
