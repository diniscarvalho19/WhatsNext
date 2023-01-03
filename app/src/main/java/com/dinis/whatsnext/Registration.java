package com.dinis.whatsnext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dinis.whatsnext.TaskManager.TaskManager;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Registration extends AppCompatActivity implements TaskManager.Callback {


    TextView textView;
    TaskManager taskManager = new TaskManager();
    Registration reg;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = taskManager.executeGetCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        reg = this;
        textView = findViewById(R.id.loginNow);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });



        taskManager.executeRegistration(reg, this);


    }


    @Override
    public void PutDataIntoRecyclerView(List<MovieModelClass> movieList) {
    }

    @Override
    public void PutDataIntoRecyclerViewFriends(List<FriendRequestModelClass> everyoneList) {

    }

    @Override
    public void PutDataIntoRecyclerViewFriendsCommunity(List<FriendModelClass> everyoneList) {

    }

    @Override
    public void removeAt(int pos) {

    }

    @Override
    public void recHelper(ArrayList<String> recMovies, ArrayList<String> allMovies, @NonNull GroupAdapter.Viewholder holder) {

    }


}