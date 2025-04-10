package com.example.btl_android.data.model;


import com.google.firebase.Timestamp;

// phần thưởng mà người dùng đã đổi
public class UserReward {
    private String userRewardID;     // Mã giao dịch đổi thưởng
    private int userID;           // Mã người dùng
    private String rewardID;         // Mã phần thưởng
    private Timestamp redeemedDate;  // Ngày đổi thưởng
    private String status;        // "Redeemed" (đã dùng), "Pending" (chưa nhận)

    // Thêm các trường từ Reward
    private String title;
    private String description;
    private Timestamp expiryDate;
    private String imageUrl;

    public UserReward(String userRewardID, int userID, String rewardID, Timestamp redeemedDate, String status) {
        this.userRewardID = userRewardID;
        this.userID = userID;
        this.rewardID = rewardID;
        this.redeemedDate = redeemedDate;
        this.status = status;
    }

    public UserReward() {}

    public String getUserRewardID() {
        return userRewardID;
    }

    public void setUserRewardID(String userRewardID) {
        this.userRewardID = userRewardID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getRewardID() {
        return rewardID;
    }

    public void setRewardID(String rewardID) {
        this.rewardID = rewardID;
    }

    public Timestamp  getRedeemedDate() {
        return redeemedDate;
    }

    public void setRedeemedDate(Timestamp  redeemedDate) {
        this.redeemedDate = redeemedDate;
    }

    public String getStatus() {
        return status;
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

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
