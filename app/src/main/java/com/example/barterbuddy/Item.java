package com.example.barterbuddy;

/*
 * This class is responsible for storing all data related to an item
 */
public class Item {
  // owner_id holds the user id used by firebase for teh owner of the item
  private String owner_id;
  // item_id holds the id used by Firestore
  private String item_id;
  // title holds the title of an the item
  private String title;
  // description holds the description of the item
  private String description;
  // image_id holds the id of the item's image in Firebase
  private String image_id;
  // is_active holds true if the item is the active item for trade and false if not
  private boolean is_active;

  // getter methods
  public String getOwner_id() {
    return owner_id;
  }

  public String getItem_id() {
    return item_id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getImage_id() {
    return image_id;
  }

  public boolean getIs_active() {
    return is_active;
  }

  // setter methods
  public void setOwner_id(String owner_id) {
    this.owner_id = owner_id;
  }

  public void setItem_id(String item_id) {
    this.item_id = item_id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImage_id(String image_id) {
    this.image_id = image_id;
  }

  public void setIs_active(boolean is_active) {
    this.is_active = is_active;
  }

  // constructors
  public Item(
      String owner_id,
      String item_id,
      String title,
      String description,
      String image_id,
      boolean is_active) {
    this.owner_id = owner_id;
    this.item_id = item_id;
    this.title = title;
    this.description = description;
    this.image_id = image_id;
    this.is_active = is_active;
  }

  public Item() {}
}
