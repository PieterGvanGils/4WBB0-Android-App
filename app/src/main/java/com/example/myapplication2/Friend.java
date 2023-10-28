package com.example.myapplication2;

import com.google.firebase.firestore.PropertyName;

public class Friend {

    private String email;
    private String friendGroup;

    // Required no-argument constructor for Firestore serialization
    public Friend() {
    }

    public Friend(String email, String friendGroup) {
        this.email = email;
        this.friendGroup = friendGroup;
    }

    @PropertyName("name")
    public String getEmail() {
        return email;
    }

    @PropertyName("friendGroup")
    public String getFriendGroup() {
        return friendGroup;
    }

    @PropertyName("friendGroup")
    public void setFriendGroup(String friendGroup) {
        this.friendGroup = friendGroup;
    }
}
