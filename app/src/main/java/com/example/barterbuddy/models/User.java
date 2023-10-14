package com.example.barterbuddy.models;

/*
   This class stores all the data related to a User in a structure identical to
   a document in the Firestore Users collection.
*/
public class User {
  // the name of the user
  private String username;

  // constructors
  public User(String username) {
    this.username = username;
  }

  public User() {}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}