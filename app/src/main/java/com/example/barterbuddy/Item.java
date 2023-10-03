package com.example.barterbuddy;

import java.io.Serializable;

/*
   This class stores all the data related to an Item in a structure identical to
   a document in the Firestore Users collection.
*/
// Serializable means it can be put into an Intent with putExtra
public class Item implements Serializable {
  //  title holds the title of the item
  private String title;
  // description holds the description of the item
  private String description;
  // imageUri holds the uri of the item's image in Firebase Cloud Storage
  private String imageUri;
  // isActive holds true if the item is the active item for trade and false if not
  private boolean isActive;

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getImageUri() {
    return imageUri;
  }

  public boolean getActive() {
    return isActive;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }

  public void setActive(boolean active) {
    this.isActive = active;
  }

  // constructors
  public Item(String title, String description, String imageUri, boolean isActive) {
    this.title = title;
    this.description = description;
    this.imageUri = imageUri;
    this.isActive = isActive;
  }

  public Item() {}
}
