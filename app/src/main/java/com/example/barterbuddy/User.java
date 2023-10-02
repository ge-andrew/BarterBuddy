package com.example.barterbuddy;

/*
    This class stores all the data related to a User in a structure identical to
    a document in the Firestore Users collection.
 */
public class User {
    // the name of the user
    private String username;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // constructors
    public User(String username) {
        this.username = username;
    }

    public User() {}
    //is it possible to add in rating field here so its attached to user
}
