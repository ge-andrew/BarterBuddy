package com.example.barterbuddy.models;

import java.io.Serializable;

// Serializable means it can be put into an Intent with putExtra
/**
 * Stores all the data related to an Item in a structure identical to a document in the Firestore
 * Users collection.
 */
public class Item implements Serializable {
  /** the title of the item */
  private String title;

  /** the description of the item */
  private String description;

  /** the uri of the item's image in Firebase Cloud Storage */
  private String imageId;

  /** true if the item is the active item for trade and false if not */
  private boolean isActive;

  /** username of person who owns this item */
  private String username;

  /** email of user who owns this item */
  private String email;

  // constructors
  public Item(
      String title,
      String description,
      String imageId,
      boolean isActive,
      String username,
      String email) {
    this.title = title;
    this.description = description;
    this.imageId = imageId;
    this.isActive = isActive;
    this.username = username;
    this.email = email;
  }

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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
