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
  private boolean active;

  private String username;
  private String email;
  private String perceivedValue;
  private String id;

  // default constructor
  public Item(
      String id,
      String title,
      String description,
      String imageId,
      boolean active,
      String username,
      String ownerEmail,
      String perceivedValue) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.imageId = imageId;
    this.active = active;
    this.username = username;
    this.email = ownerEmail;
    this.perceivedValue = perceivedValue;
  }

  // empty constructor necessary for Firebase
  public Item() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
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

  public String generateId() {
    return this.getEmail() + "-" + this.getTitle();
  }

  public String getPerceivedValue() {
    return perceivedValue;
  }

  public void setPerceivedValue(String perceivedValue) {
    this.perceivedValue = perceivedValue;
  }
}
