package com.example.viltrade2.models;

import java.io.Serializable;

public class NewProductsModel implements Serializable {
    private String productId;
    private String description;
    private String img_url;
    private String name;
    private String rating;
    private double price;
    private String type; // tambahkan properti kategori

    // Default constructor (required for Firebase)
    public NewProductsModel() {}

    // Parameterized constructor
    public NewProductsModel(String productId, String description, String name, String rating, String img_url, double price, String type) {
        this.productId = productId;
        this.description = description;
        this.name = name;
        this.rating = rating;
        this.img_url = img_url;
        this.price = price;
        this.type = type; // tambahkan inisialisasi kategori
    }

    // Getter and setter for productId
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for rating
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    // Getter and setter for img_url
    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    // Getter and setter for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and setter for type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
