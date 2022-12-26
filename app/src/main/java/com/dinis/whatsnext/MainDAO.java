package com.dinis.whatsnext;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MainDAO {

    @Insert(onConflict = REPLACE)
    void insert(MovieModelClass movie);

    @Query("SELECT * FROM movies")
    List<MovieModelClass> getAll();

    @Delete
    void delete(MovieModelClass movie);

}