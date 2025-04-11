package com.example.btl_android.data.model;

// đánh giá về app
public class Feedback {
    private int feedbackID;       // Mã đánh giá
    private int userID;           // Người gửi đánh giá
    private String message;       // Nội dung đánh giá
    private int rating;           // Số sao (1 đến 5)
    private String feedbackDate;  // Ngày gửi đánh giá
    private String image;         // URL ảnh vật liệu

    public Feedback(int feedbackID, int userID, String message, int rating, String feedbackDate, String image) {
        this.feedbackID = feedbackID;
        this.userID = userID;
        this.message = message;
        this.rating = rating;
        this.feedbackDate = feedbackDate;
        this.image = image;
    }
    public Feedback(){}

    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(String feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
