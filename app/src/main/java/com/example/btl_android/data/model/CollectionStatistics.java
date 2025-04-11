package com.example.btl_android.data.model;

import java.util.HashMap;
import java.util.Map;

public class CollectionStatistics {
    private String collectorId;
    private double totalWeight; // Tổng khối lượng rác đã thu gom (kg)
    private int totalCollections; // Tổng số lần thu gom
    private double efficiency; // Hiệu suất (%)
    private long timestamp;
    private Map<String, Double> wasteTypeDistribution; // Phân bố loại rác
    private Map<Long, Double> timeSeriesData; // Dữ liệu theo thời gian

    public CollectionStatistics() {
        // Required empty constructor for Firebase
        wasteTypeDistribution = new HashMap<>();
        timeSeriesData = new HashMap<>();
    }

    public CollectionStatistics(String collectorId) {
        this.collectorId = collectorId;
        this.totalWeight = 0;
        this.totalCollections = 0;
        this.efficiency = 0;
        this.timestamp = System.currentTimeMillis();
        this.wasteTypeDistribution = new HashMap<>();
        this.timeSeriesData = new HashMap<>();
    }

    // Getters and Setters
    public String getCollectorId() { return collectorId; }
    public void setCollectorId(String collectorId) { this.collectorId = collectorId; }

    public double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(double totalWeight) { 
        this.totalWeight = totalWeight;
        calculateEfficiency();
    }

    public int getTotalCollections() { return totalCollections; }
    public void setTotalCollections(int totalCollections) { 
        this.totalCollections = totalCollections;
        calculateEfficiency();
    }

    public double getEfficiency() { return efficiency; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Map<String, Double> getWasteTypeDistribution() { return wasteTypeDistribution; }
    public void setWasteTypeDistribution(Map<String, Double> wasteTypeDistribution) {
        this.wasteTypeDistribution = wasteTypeDistribution;
    }

    public Map<Long, Double> getTimeSeriesData() { return timeSeriesData; }
    public void setTimeSeriesData(Map<Long, Double> timeSeriesData) {
        this.timeSeriesData = timeSeriesData;
    }

    // Thêm dữ liệu cho một loại rác
    public void addWasteType(String type, double weight) {
        wasteTypeDistribution.merge(type, weight, Double::sum);
    }

    // Thêm dữ liệu theo thời gian
    public void addTimeSeriesData(long timestamp, double weight) {
        timeSeriesData.put(timestamp, weight);
    }

    // Tính hiệu suất dựa trên tổng khối lượng và số lần thu gom
    private void calculateEfficiency() {
        if (totalCollections > 0) {
            // Giả sử mục tiêu là 100kg mỗi lần thu gom
            double target = totalCollections * 100.0;
            efficiency = (totalWeight / target) * 100;
        } else {
            efficiency = 0;
        }
    }
} 