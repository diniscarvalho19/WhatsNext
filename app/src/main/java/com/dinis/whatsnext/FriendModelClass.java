package com.dinis.whatsnext;

public class FriendModelClass {
    String username;
    String image;


    String status;

    public FriendModelClass (String username, String image){
        this.username = username;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public FriendModelClass setStatus(String status) {
        this.status = status;
        return this;
    }


}
