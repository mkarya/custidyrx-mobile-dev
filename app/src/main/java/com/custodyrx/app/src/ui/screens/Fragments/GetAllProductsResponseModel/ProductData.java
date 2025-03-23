package com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductData {

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

    @SerializedName("productType")
    @Expose
    private String productType;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;

    @SerializedName("productCode")
    @Expose
    private String productCode;

    @SerializedName("UNSPSCCode")
    @Expose
    private String unspscCode;

    @SerializedName("modelNumber")
    @Expose
    private String modelNumber;

    @SerializedName("brand")
    @Expose
    private String brand;

    @SerializedName("netContent")
    @Expose
    private String netContent;

    @SerializedName("ndc")
    @Expose
    private String ndc;

    @SerializedName("dossage")
    @Expose
    private String dossage;

    @SerializedName("strength")
    @Expose
    private String strength;

    @SerializedName("productCategory")
    @Expose
    private List<ProductCategory> productCategory;

    @SerializedName("expiryDate")
    @Expose
    private String expiryDate;

    @SerializedName("inventoryQuantity")
    @Expose
    private int inventoryQuantity;

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getUnspscCode() {
        return unspscCode;
    }

    public void setUnspscCode(String unspscCode) {
        this.unspscCode = unspscCode;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getNetContent() {
        return netContent;
    }

    public void setNetContent(String netContent) {
        this.netContent = netContent;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    public String getDossage() {
        return dossage;
    }

    public void setDossage(String dossage) {
        this.dossage = dossage;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public List<ProductCategory> getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(List<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }
}
