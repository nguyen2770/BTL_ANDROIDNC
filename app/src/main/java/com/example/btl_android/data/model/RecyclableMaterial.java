package com.example.btl_android.data.model;

public class RecyclableMaterial {
    private String id;         // ID của vật liệu
    private double quantity;   // Khối lượng (kg)

    public RecyclableMaterial() {
        // Firestore cần constructor rỗng
    }

    public RecyclableMaterial(String id, double quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
