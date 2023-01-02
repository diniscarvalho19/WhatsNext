package com.dinis.whatsnext.TaskManager;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dinis.whatsnext.MovieModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskManager {
    final Executor executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());
    FirebaseAuth auth;
    FirebaseUser user;


    public interface Callback{
        void PutDataIntoRecyclerView(List<MovieModelClass> movieList);
    }



    public void executeGetWatchlist(Callback callback){

        executor.execute(() -> {
            //Initiate FB
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            List<MovieModelClass> movieList = new ArrayList<>();

            //Username
            assert user != null;
            String username = Objects.requireNonNull(user.getEmail()).split("@")[0];

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("watchlist");

            //Read DB
            handler.post(() ->{
                mDatabase.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            if (task.getResult().getValue() != null){
                                String[] movies = Objects.requireNonNull(task.getResult().getValue()).toString().split(", t");
                                for(String movie: movies){
                                    String[] movieDetails = movie.replaceAll("\\{","").replaceAll("\\}","").split("=img=");
                                    String id = movieDetails[0];
                                    movieDetails = movieDetails[1].split(", name=");
                                    String img = movieDetails[0];
                                    String name = movieDetails[1];
                                    movieList.add(new MovieModelClass(id,name,img,""));

                                }

                                callback.PutDataIntoRecyclerView(movieList);
                            }


                        }

                    }
                });


            });
        });
    }


}
