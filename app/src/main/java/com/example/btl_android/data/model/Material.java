package com.example.btl_android.data.model;

// thông tin về vật liệu tái chế
public class Material {
    private String materialID;       // Mã định danh vật liệu
    private String name;          // Tên vật liệu (VD: Nhựa, Giấy...)
    private String description;   // Mô tả chi tiết về vật liệu
    private String category;      // Nhóm vật liệu (VD: Nhựa mềm, Kim loại nặng...)
    private double pricePerKg;    // Giá trị ước tính trên mỗi kg (dùng để tính điểm)
    private String imageUrl;      // link ảnh của vaatj liệu

    public Material(String materialID, String name, String description, String category, double pricePerKg, String imageUrl) {
        this.materialID = materialID;
        this.name = name;
        this.description = description;
        this.category = category;
        this.pricePerKg = pricePerKg;
        this.imageUrl =  imageUrl;
    }

    public Material() {}

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
