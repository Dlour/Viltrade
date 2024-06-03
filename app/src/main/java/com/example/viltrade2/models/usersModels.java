package com.example.viltrade2.models;

import java.security.Timestamp;

public class usersModels {
    private String NamaLengkap;
    private String NamaPanggilan;
    private  String UserEmail;
    private String isDistributor;

    private String isKonsumen;

    private String fcmToken;

    public usersModels() {
    }

    public usersModels(String NamaLengkap, String NamaPanggilan, String UserEmail, String fcmToken, String isDistributor,String isKonsumen) {
        this.NamaLengkap = NamaLengkap;
        this.NamaPanggilan = NamaPanggilan;
        this.UserEmail = UserEmail;
        this.fcmToken = fcmToken;
        this.isDistributor = isDistributor;
        this.isKonsumen = isKonsumen;
    }

    public String getIsDistributor() {
        return isDistributor;
    }

    public void setIsDistributor(String isDistributor) {
        this.isDistributor = isDistributor;
    }

    public String getIsKonsumen() {
        return isKonsumen;
    }

    public void setIsKonsumen(String isKonsumen) {
        this.isKonsumen = isKonsumen;
    }

    public String getNamaLengkap() {
        return NamaLengkap;
    }

    public void setNamaLengkap(String NamaLengkap) {
        this.NamaLengkap = NamaLengkap;
    }

    public String  getNamaPanggilan() {
        return NamaPanggilan;
    }

    public void setNamaPanggilan(String NamaPanggilan) {
        this.NamaPanggilan = NamaPanggilan;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
