package com.example.barterbuddy.models;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class TradeWithRef implements Serializable {
    private String posterEmail;

    public String getPosterEmail() {
        return posterEmail;
    }

    public void setPosterEmail(String posterEmail) {
        this.posterEmail = posterEmail;
    }

    public DocumentReference getPosterItem() {
        return posterItem;
    }

    public void setPosterItem(DocumentReference posterItem) {
        this.posterItem = posterItem;
    }

    public String getOfferingEmail() {
        return offeringEmail;
    }

    public void setOfferingEmail(String offeringEmail) {
        this.offeringEmail = offeringEmail;
    }

    public DocumentReference getOfferingItem() {
        return offeringItem;
    }

    public void setOfferingItem(DocumentReference offeringItem) {
        this.offeringItem = offeringItem;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    private DocumentReference posterItem;
    private String offeringEmail;
    private DocumentReference offeringItem;
    private double money;
}
