package com.example.barterbuddy.models;

import java.io.Serializable;

public class Trade implements Serializable{

    private String posterEmail;
    private Item posterItem;
    private String offeringEmail;
    private Item offeringItem;
    // Money indicates the additional funds the offering user is giving
    // Negative money values indicates the offering user wants to be paid for the trade
    private double money;
    private String stateOfCompletion;

    public Trade(String posterEmail, Item posterItem, String offeringEmail, Item offeringItem, double money, String stateOfCompletion) {
        this.posterEmail = posterEmail;
        this.posterItem = posterItem;
        this.offeringEmail = offeringEmail;
        this.offeringItem = offeringItem;
        this.money = money;
        this.stateOfCompletion = stateOfCompletion;
    }


    public Trade() {
    }

    public String getPosterEmail() {
        return posterEmail;
    }

    public Item getPosterItem() {
        return posterItem;
    }

    public String getOfferingEmail() {
        return offeringEmail;
    }

    public Item getOfferingItem() {
        return offeringItem;
    }

    public double getMoney() {
        return money;
    }


    public void setPosterEmail(String posterEmail) {
        this.posterEmail = posterEmail;
    }

    public void setPosterItem(Item posterItem) {
        this.posterItem = posterItem;
    }

    public void setOfferingEmail(String offeringEmail) {
        this.offeringEmail = offeringEmail;
    }

    public void setOfferingItem(Item offeringItem) {
        this.offeringItem = offeringItem;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getStateOfCompletion() {
        return stateOfCompletion;
    }

    public void setStateOfCompletion(String stateOfCompletion) {
        this.stateOfCompletion = stateOfCompletion;
    }

    public boolean isNonNull() {
        if(posterEmail == null ||
            offeringEmail == null ||
            posterItem == null ||
            offeringItem == null ||
            stateOfCompletion == null) {
            return false;
        }
        return true;
    }
}

