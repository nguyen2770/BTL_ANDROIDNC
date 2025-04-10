package com.example.btl_android.data.model;

// lưu lịch sử các đơn đã thu gom thành công
public class CollectionHistory {
    private int historyID;         // Mã lịch sử
    private int requestID;         // Gắn với yêu cầu thu gom gốc
    private int collectorID;       // Người thực hiện thu gom
    private int userID;            // Người có rác
    private int materialID;        // Loại vật liệu đã thu gom
    private double quantity;       // Khối lượng thực tế thu gom
    private String collectedDate;  // Ngày thu gom
    private String notes;          // Ghi chú thêm (VD: hủy bỏ, sai địa chỉ...)

    public CollectionHistory(int historyID, int requestID, int collectorID, int userID, int materialID, double quantity, String collectedDate, String notes) {
        this.historyID = historyID;
        this.requestID = requestID;
        this.collectorID = collectorID;
        this.userID = userID;
        this.materialID = materialID;
        this.quantity = quantity;
        this.collectedDate = collectedDate;
        this.notes = notes;
    }

    public CollectionHistory(){}

    public int getHistoryID() {
        return historyID;
    }

    public void setHistoryID(int historyID) {
        this.historyID = historyID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getCollectorID() {
        return collectorID;
    }

    public void setCollectorID(int collectorID) {
        this.collectorID = collectorID;
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

    public String getCollectedDate() {
        return collectedDate;
    }

    public void setCollectedDate(String collectedDate) {
        this.collectedDate = collectedDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
