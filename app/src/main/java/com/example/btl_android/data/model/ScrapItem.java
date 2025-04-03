package com.example.btl_android.data.model;

public class ScrapItem {
    private String id;
    private String name;
    private int point;
    private String imageUrl;
    private String description;


    public ScrapItem(String id, String name, int point, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.point = point;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public ScrapItem() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
