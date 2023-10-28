package com.example.barterbuddy.models;

import java.io.Serializable;

// Serializable means it can be put into an Intent with putExtra
/**
 * Stores all the data related to an Item in a structure identical to a document in the Firestore
 * Users collection.
 */
public class Item implements Serializable {
  private String title;
  private String description;

  /** the uri of the item's image in Firebase Cloud Storage */
  private String imageId;
  /** true if the item is the active item for trade and false if not */
  private boolean isActive;
  private String ownerUsername;
  private String ownerEmail;

  // default constructor
  public Item(
      String title,
      String description,
      String imageId,
      boolean isActive,
      String ownerUsername,
      String ownerEmail) {
    this.title = title;
    this.description = description;
    this.imageId = imageId;
    this.isActive = isActive;
    this.ownerUsername = ownerUsername;
    this.ownerEmail = ownerEmail;
  }

  // empty constructor necessary for Firebase
  public Item() {}

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageUri) {
    this.imageId = imageUri;
  }

  public boolean getActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    this.isActive = active;
  }

  public String getOwnerUsername() {
    return ownerUsername;
  }

  public void setOwnerUsername(String ownerUsername) {
    this.ownerUsername = ownerUsername;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }

  public String getId() {
    return this.getOwnerEmail() + "-" + this.getTitle();
  }
}
