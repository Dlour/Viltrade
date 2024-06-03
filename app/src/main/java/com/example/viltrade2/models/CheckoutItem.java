package com.example.viltrade2.models;

public class CheckoutItem {
    private String productId;
    private String img_url;
    private double totalPrice,productPrice;
    private int totalQuantity,quantity;
    private String userId;

    private String productName;

    public CheckoutItem() {}

    public CheckoutItem(String productId, String img_url, double totalPrice, int totalQuantity, String userId) {
        this.productId = productId;
        this.img_url = img_url;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    // Getter dan setter untuk productId
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    // Getter dan setter lainnya
    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
