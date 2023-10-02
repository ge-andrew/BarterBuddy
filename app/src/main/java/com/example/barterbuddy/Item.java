package com.example.barterbuddy;

import android.graphics.Bitmap;

/*
   This class stores all the data related to an Item in a structure identical to
   a document in the Firestore Users collection.
*/
public class Item {
  //  // title holds the title of the item
  private String title;
  // description holds the description of the item
  private String description;
  // imageUri holds the uri of the item's image in Firebase Cloud Storage
  private String imageUri;
  // imageBitmap holds the bitmap of the image directly in Firestore (URI no longer necessary)
  private Bitmap photo;
  // isActive holds true if the item is the active item for trade and false if not
  private boolean isActive;
  // userName holds username of person who own this item
  private String userName;

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getImageUri() {
    return imageUri;
  }
  public Bitmap getPhoto() { return photo; }

  public boolean getActive() {
    return isActive;
  }

  public String getUserName() { return userName; }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImageUri(String imageUri) {
    this.imageUri = imageUri;
  }

  public void setImageBitmap(Bitmap photo) { this.photo = photo; }

  public void setActive(boolean active) {
    this.isActive = active;
  }

  public void setUserName(String userName) { this.userName = userName; }

  // constructors
  public Item(String title, String description, Bitmap photo, boolean isActive) {
    this.title = title;
    this.description = description;
    this.photo = photo;
    this.isActive = isActive;
  }

  public Item() {}
}
