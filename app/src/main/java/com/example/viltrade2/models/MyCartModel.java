package com.example.viltrade2.models;

public class MyCartModel {
    private String productId; // tambahkan properti productId
    private String productName;
    private double price;
    private String currentTime;
    private String currentDate;
    private int TotalQuantity;  // Ensure correct naming
    private double TotalPrice;
    private String img_url;

    private boolean isChecked;

    public MyCartModel() {
        // Default constructor required for calls to DataSnapshot.getValue(MyCartModel.class)
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getprice() {
        return price;
    }

    public void setprice(double price) {
        this.price = price;
    }

    public String getCurrentTime() {
        return currentTime;
    }



    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getTotalQuantity() {
        return TotalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.TotalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.TotalPrice = totalPrice;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
