package com.example.btl_android.data.model;

import com.google.firebase.Timestamp;

public class SavedAddress {
    private String documentId;
    private String label;
    private String address;
    private double latitude;
    private double longitude;
    private Timestamp createdAt;

    public SavedAddress() {}

    public SavedAddress(String documentId, String label, String address, double latitude, double longitude) {
        this.documentId = documentId;
        this.label = label;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
