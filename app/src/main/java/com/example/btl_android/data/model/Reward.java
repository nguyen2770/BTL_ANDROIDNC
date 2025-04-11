package com.example.btl_android.data.model;


import com.google.firebase.Timestamp;

// Quản lý các phần thưởng dùng để đổi
public class Reward {
    private String rewardID;          // Mã phần thưởng
    private String title;          // Tên phần thưởng (VD: Giảm 20% đơn hàng...)
    private String description;    // Mô tả chi tiết phần thưởng
    private int pointsRequired;    // Số điểm cần để đổi
    private Timestamp expiryDate;     // Hạn sử dụng
    private String status;         // "Available", "Expired"
    private int number;
    private String imageUrl;


    public Reward(String rewardID, String title, String description, int pointsRequired, Timestamp expiryDate, String status, int number, String imageUrl) {
        this.rewardID = rewardID;
        this.title = title;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.expiryDate = expiryDate;
        this.status = status;
        this.number = number;
        this.imageUrl = imageUrl;
    }

    public Reward(){}

    public String getRewardID() {
        return rewardID;
    }

    public void setRewardID(String rewardID) {
        this.rewardID = rewardID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public Timestamp  getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp  expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
