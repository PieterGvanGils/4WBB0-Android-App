package com.example.myapplication2;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class FriendGroup {

    private String name;
    private List<String> members;

    // Required no-argument constructor for Firestore serialization
    public FriendGroup() {
        this.members = new ArrayList<>();
    }

    public FriendGroup(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("members")
    public List<String> getMembers() {
        return members;
    }

    @PropertyName("members")
    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void addMember(String userId) {
        if (!members.contains(userId)) {
            members.add(userId);
        }
    }
}

