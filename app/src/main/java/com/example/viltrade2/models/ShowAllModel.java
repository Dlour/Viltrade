package com.example.viltrade2.models;

public class ShowAllModel {
    private String productId;
    private String description;
    private String name;
    private String rating;
    private String img_url;
    private String type;
    private int price;

    public ShowAllModel() {
    }

    public ShowAllModel(String description, String name, String rating, String img_url, String type, int price) {
        this.description = description;
        this.name = name;
        this.rating = rating;
        this.img_url = img_url;
        this.type = type;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
