package com.example.barterbuddy.models;

import com.google.firebase.firestore.DocumentReference;
import java.io.Serializable;

public class Trade implements Serializable {

  private String posterEmail;
  private DocumentReference posterItem;
  private String offeringEmail;
  private DocumentReference offeringItem;
  // Money indicates the additional funds the offering user is giving
  // Negative money values indicates the offering user wants to be paid for the trade
  private double money;
  private String stateOfCompletion;
  private int numberCounteroffersLeft;

  public Trade(
      String posterEmail,
      DocumentReference posterItem,
      String offeringEmail,
      DocumentReference offeringItem,
      double money,
      String stateOfCompletion) {
    this.posterEmail = posterEmail;
    this.posterItem = posterItem;
    this.offeringEmail = offeringEmail;
    this.offeringItem = offeringItem;
    this.money = money;
    this.stateOfCompletion = stateOfCompletion;
    numberCounteroffersLeft = 6;
  }

  public Trade() {}

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

  public String getStateOfCompletion() {
    return stateOfCompletion;
  }

  public void setStateOfCompletion(String stateOfCompletion) {
    this.stateOfCompletion = stateOfCompletion;
  }

  public boolean isNonNull() {
    if (posterEmail != null
        || offeringEmail != null
        || posterItem != null
        || offeringItem != null
        || stateOfCompletion != null) {
      return true;
    } else {
      return false;
    }
  }

  public int getNumberCounteroffersLeft() {
    return numberCounteroffersLeft;
  }

  public void setNumberCounteroffersLeft(int numberCounteroffersLeft) {
    this.numberCounteroffersLeft = numberCounteroffersLeft;
  }
}
