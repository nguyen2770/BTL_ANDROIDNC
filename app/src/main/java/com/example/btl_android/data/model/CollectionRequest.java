package com.example.btl_android.data.model;

// yêu cầu đặt lịch thu gom được gửi tù người có rác
public class CollectionRequest {
    private int requestID;        // Mã yêu cầu
    private int userID;           // Mã người gửi yêu cầu
    private int materialID;       // Loại vật liệu yêu cầu thu gom
    private double quantity;      // Khối lượng ước tính (kg)
    private String status;        // Trạng thái: "Pending", "In Progress", "Completed"
    private String requestDate;   // Ngày gửi yêu cầu
    private String pickupDate;    // Ngày thu gom dự kiến
    private String address;       // Địa điểm thu gom cụ thể

    public CollectionRequest(int requestID, int userID, int materialID, double quantity, String status, String requestDate, String pickupDate, String address) {
        this.requestID = requestID;
        this.userID = userID;
        this.materialID = materialID;
        this.quantity = quantity;
        this.status = status;
        this.requestDate = requestDate;
        this.pickupDate = pickupDate;
        this.address = address;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CollectionRequest() {}



}
