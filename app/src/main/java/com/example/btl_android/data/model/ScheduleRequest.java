package com.example.btl_android.data.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Model lưu trữ đơn thu gom
public class ScheduleRequest implements Serializable {
    private String id; // ID của đơn thu gom (tự động tạo bởi Firestore)
    private String userId; // ID của người dùng tạo đơn
    private String collectorId; // ID của người thu gom (khởi tạo là null khi chưa có người nhận)
    private String address; // Địa chỉ thu gom
    private double latitude;
    private double longitude;

    private boolean isWeekend;
    private String timeRange; // Khoảng thời gian thu gom (vd: "08:00 - 10:00")
    private String status; // Trạng thái của đơn (Pending, Confirmed, InProgress, Completed)
    private List<RecyclableMaterial> materials; // Danh sách vật liệu tái chế
    private double totalWeight; // Tổng cân nặng (ban đầu là 0)
    private String createdAt; // Thời gian tạo đơn

    public ScheduleRequest() {}

    public ScheduleRequest(String id, String userId, String collectorId, String address, double latitude, double longitude, String timeRange, String status, List<RecyclableMaterial> materials, double totalWeight, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.collectorId = collectorId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeRange = timeRange;
        this.status = status;
        this.materials = materials;
        this.totalWeight = totalWeight;
        this.createdAt = createdAt;
    }


    @SuppressLint("NewApi")
    public ScheduleRequest(String id, String userId, String address, double latitude, double longitude, String timeRange, String status, boolean isWeekend) {
        this.id = id;
        this.userId = userId;
        this.collectorId = null;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeRange = timeRange;
        this.status = status;
        this.materials = new ArrayList<>();
        this.totalWeight = 0.0;
        this.createdAt = LocalDateTime.now().toString();
        this.isWeekend = isWeekend;
    }



    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RecyclableMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<RecyclableMaterial> materials) {
        this.materials = materials;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setWeekend(boolean weekend) {
        isWeekend = weekend;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}