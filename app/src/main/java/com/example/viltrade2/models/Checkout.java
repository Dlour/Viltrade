package com.example.viltrade2.models;

import java.util.List;

public class Checkout {
    private String userName;
    private String userAddress;
    private List<CheckoutItem> items;
    private String totalPrice;
    private String shippingCost;

    public Checkout() {
        // Diperlukan untuk Firebase
    }

    public Checkout(String userName, String userAddress, List<CheckoutItem> items, String totalPrice, String shippingCost) {
        this.userName = userName;
        this.userAddress = userAddress;
        this.items = items;
        this.totalPrice = totalPrice;
        this.shippingCost = shippingCost;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public List<CheckoutItem> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItem> items) {
        this.items = items;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(String shippingCost) {
        this.shippingCost = shippingCost;
    }
}

