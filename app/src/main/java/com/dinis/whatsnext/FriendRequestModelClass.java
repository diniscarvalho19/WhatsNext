package com.dinis.whatsnext;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FriendRequestModelClass {
    public String name;
    public Boolean accepted;
    public String id;

    public FriendRequestModelClass(String name, Boolean accepted){
        this.name = name;
        this.accepted = accepted;
    }

    public FriendRequestModelClass() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public String getId() {
        return id;
    }

    public FriendRequestModelClass setId(String id) {
        this.id = id;
        return this;
    }

}
