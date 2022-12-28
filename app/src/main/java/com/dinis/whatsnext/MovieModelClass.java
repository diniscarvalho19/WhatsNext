package com.dinis.whatsnext;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "movies")
public class MovieModelClass implements Serializable {
    @PrimaryKey @NonNull
    String id;
    @ColumnInfo(name = "title")
    String name;
    @ColumnInfo(name = "img")
    String img;

    @ColumnInfo(name = "loc")
    String locations;

    public MovieModelClass(String id, String name, String img, String loc) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.locations = loc;
    }

    public MovieModelClass() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }


}
