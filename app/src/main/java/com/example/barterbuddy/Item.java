package com.example.barterbuddy;

/*
 * This class is responsible for storing all data related to an item
 */
public class Item {
//  // title holds the title of the item
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
  public Item(
      String title,
      String description,
      String imageUri,
      boolean isActive) {
    this.title = title;
    this.description = description;
    this.imageUri = imageUri;
    this.isActive = isActive;
  }

  public Item() {}
}
