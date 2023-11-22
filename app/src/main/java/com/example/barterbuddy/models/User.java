package com.example.barterbuddy.models;

import java.io.Serializable;

/*
   This class stores all the data related to a User in a structure identical to
   a document in the Firestore Users collection.
*/
public class User implements Serializable {
  // the name of the user
  private String username;
  private String email;
  private String password;
  private int numOfTimesRated;
  private int currentAverageRating;

  // constructors
  public User(
      String username,
      String email,
      String password,
      int numOfTimesRated,
      int currentAverageRating) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.numOfTimesRated = numOfTimesRated;
    this.currentAverageRating = currentAverageRating;
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

  public int getCurrentAverageRating() {
    return currentAverageRating;
  }

  public int getNumOfTimesRated() {
    return numOfTimesRated;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setCurrentAverageRating(int rating) {
    this.currentAverageRating = currentAverageRating;
  }

  public void setNumOfTimesRated(int numOfTimesRated) {
    this.numOfTimesRated = numOfTimesRated;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
