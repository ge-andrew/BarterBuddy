package com.example.barterbuddy.models;

/*
   This class stores all the data related to a User in a structure identical to
   a document in the Firestore Users collection.
*/
public class User {
  // the name of the user
  private String username;
  private String email;
  private String password;

  // constructors
  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User() {}

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
