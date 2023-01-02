package com.dinis.whatsnext.TaskManager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinis.whatsnext.FriendModelClass;
import com.dinis.whatsnext.GlideApp;
import com.dinis.whatsnext.GroupAdapter;
import com.dinis.whatsnext.GroupModel;
import com.dinis.whatsnext.Login;
import com.dinis.whatsnext.MainActivity;
import com.dinis.whatsnext.MovieModelClass;
import com.dinis.whatsnext.PopupFriendlistAdapter;
import com.dinis.whatsnext.ProfileFragment;
import com.dinis.whatsnext.R;
import com.dinis.whatsnext.RecyclerViewInterface;
import com.dinis.whatsnext.Registration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskManager {
    final Executor executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());
    List<MovieModelClass> movieList = new ArrayList<>();
    FirebaseUser thisUser;

    public interface Callback{
        void PutDataIntoRecyclerView(List<MovieModelClass> movieList);
        void removeAt(int pos);
    }



    public List<MovieModelClass> executeGetWatchlist(Callback callback){
        executor.execute(() -> {
            movieList.clear();
            //Initiate FB
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
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
        return movieList;
    }

    public void executeDeleteMovie(MovieModelClass deletedMovie, Callback callback){
        executor.execute(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            assert user != null;
            String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("watchlist");
            //Read DB
            handler.post(() ->{
                mDatabase.child(username).child(deletedMovie.getId()).removeValue();

            });
        });
    }

    public void executeUnDeleteMovie(MovieModelClass deletedMovie, Callback callback){
        executor.execute(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            assert user != null;
            String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("watchlist");

            handler.post(() ->{
                myRef.child(username).child(deletedMovie.getId()).child("name").setValue(deletedMovie.getName());
                myRef.child(username).child(deletedMovie.getId()).child("img").setValue(deletedMovie.getImg());
            });
        });
    }

    public void executeRegistration(Registration registration, Callback callback){
        executor.execute(() -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            TextView editTextEmail = registration.findViewById(R.id.email);
            TextView editTextPassword = registration.findViewById(R.id.password);
            Button buttonReg = registration.findViewById(R.id.btn_register);
            ProgressBar progressBar = registration.findViewById(R.id.progressBar);

            handler.post(() ->{
                buttonReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        String email, password;
                        email = String.valueOf(editTextEmail.getText());
                        password = String.valueOf(editTextPassword.getText());

                        if (TextUtils.isEmpty(email)){
                            Toast.makeText(registration, "Enter email", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        if (TextUtils.isEmpty(password)){
                            Toast.makeText(registration, "Enter password", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        Log.d("FireB", "Will try to create an user...");

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d("FireB", "Completed");
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            Log.d("FireB", ":D");
                                            Toast.makeText(registration, "Account created.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(registration.getApplicationContext(), Login.class);
                                            registration.startActivity(intent);
                                            registration.finish();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.d("FireB", "sowwy :((((");
                                            Toast.makeText(registration, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });
            });
        });
    }

    public FirebaseUser executeGetCurrentUser(){
        executor.execute(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            handler.post(() ->{
                thisUser = auth.getCurrentUser();
            });
        });
        return thisUser;
    }

    public void executeSignOut(){
        executor.execute(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            handler.post(auth::signOut);
        });
    }

    public void executeCreateGroup(Context context, RecyclerViewInterface rvi, View popupView, PopupWindow popupWindow){
        executor.execute(() -> {
            EditText groupName = (EditText) popupView.findViewById(R.id.group_name_input);
            RecyclerView recyclerView = (RecyclerView)popupView.findViewById(R.id.recycler_add_friend_to_group);
            Button createGroup = (Button) popupView.findViewById(R.id.confirm_group);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            ArrayList<FriendModelClass> arrayList = new ArrayList<FriendModelClass>();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            ArrayList<FriendModelClass> everyoneList = new ArrayList<>();
            List<String> friendList = new ArrayList<>();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference fDatabase = database.getReference("friends_list");
            assert user != null;
            String username = Objects.requireNonNull(user.getEmail()).split("@")[0];

            handler.post(() ->{
                fDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            if (task.getResult().getValue() != null){
                                String result = task.getResult().getValue().toString().substring(1);
                                result = result.substring(0, result.length() - 1);
                                String[] users = result.split("\\}, ");
                                int size = users.length;
                                int count = 1;
                                for(String usr: users){
                                    if (count != size){
                                        usr = usr + "}";
                                    }
                                    count++;
                                    String[] data = usr.split("=\\{");
                                    String name = data[0];
                                    data = Arrays.copyOfRange(data, 1, data.length);
                                    if(!name.equals(username)){
                                        for(String st : data){
                                            st = st.substring(0, st.length() - 1);
                                            for(String st2 : st.split(", ")){
                                                String usernameReq = st2.split("=")[0];
                                                String status = st2.split("=")[1];
                                                if(status.equals("true") && usernameReq.equals(username))
                                                    friendList.add(name);
                                            }
                                        }
                                    }
                                }
                                fDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                        else {
                                            if (task.getResult().getValue() != null){
                                                String result = task.getResult().getValue().toString().substring(1);
                                                result = result.substring(0, result.length() - 1);
                                                String[] users = result.split("\\}, ");
                                                int size = users.length;
                                                int count = 1;
                                                for(String usr: users){
                                                    if (count != size){
                                                        usr = usr + "}";
                                                    }
                                                    count++;
                                                    String[] data = usr.split("=\\{");
                                                    String name = data[0];
                                                    data = Arrays.copyOfRange(data, 1, data.length);
                                                    if(name.equals(username)){
                                                        for(String st : data){
                                                            st = st.substring(0, st.length() - 1);
                                                            for(String st2 : st.split(", ")){
                                                                String usernameReq = st2.split("=")[0];
                                                                String status = st2.split("=")[1];
                                                                if(status.equals("true") && friendList.contains(usernameReq))
                                                                    everyoneList.add(new FriendModelClass(usernameReq,"https://cdn-icons-png.flaticon.com/512/16/16363.png"));
                                                            }
                                                        }
                                                    }
                                                }
                                                PopupFriendlistAdapter popupFriendlistAdapter = new PopupFriendlistAdapter(context, everyoneList, rvi);
                                                recyclerView.setLayoutManager(linearLayoutManager);
                                                recyclerView.setAdapter(popupFriendlistAdapter);


                                                createGroup.setOnClickListener(new View.OnClickListener(){
                                                    @Override
                                                    public void onClick(View view) {
                                                        popupWindow.dismiss();
                                                        Toast toast = Toast.makeText(context,"Group created",Toast.LENGTH_SHORT);
                                                        toast.setMargin(50,50);
                                                        toast.show();
                                                        //Create Group in DB
                                                        //Initiate DB
                                                        assert user != null;
                                                        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                                                        FirebaseDatabase databaseFB = FirebaseDatabase.getInstance();

                                                        DatabaseReference mDatabase = databaseFB.getReference("groups")
                                                                .child(username)
                                                                .child(String.valueOf(groupName.getText()));
                                                        mDatabase.setValue(popupFriendlistAdapter.getGroupMembers());

                                                        ArrayList<String> members = popupFriendlistAdapter.getGroupMembers();
                                                        for(String name: members){
                                                            ArrayList<String> membersCopy = new ArrayList<>(members);
                                                            mDatabase = databaseFB.getReference("groups")
                                                                    .child(name)
                                                                    .child(String.valueOf(groupName.getText()));
                                                            membersCopy.remove(name);
                                                            membersCopy.add(username);
                                                            mDatabase.setValue(membersCopy);
                                                        }


                                                    }
                                                });

                                                //Handler for clicking on the inactive zone of the window
                                                popupView.setOnTouchListener(new View.OnTouchListener() {
                                                    @Override
                                                    public boolean onTouch(View v, MotionEvent event) {

                                                        //Close the window when clicked
                                                        popupWindow.dismiss();
                                                        return true;
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            });
        });
    }

    public void executePopulateProfileFrag(ProfileFragment profileFragment, View root){
        executor.execute(() -> {
            TextView textViewEmail = root.findViewById(R.id.email);
            TextView textViewUsername = root.findViewById(R.id.username);
            ImageView imageView = root.findViewById(R.id.profile_pic);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            handler.post(() ->{
                if (user == null){
                    Intent intent = new Intent(profileFragment.getActivity(), Login.class);
                    profileFragment.startActivity(intent);
                    profileFragment.getActivity().finish();
                }else{
                    textViewEmail.setText(user.getEmail());
                    textViewUsername.setText(Objects.requireNonNull(user.getEmail()).split("@")[0]);
                    GlideApp.with(profileFragment).load("https://cdn-icons-png.flaticon.com/512/16/16363.png").into(imageView);
                }
            });
        });
    }

    public void executeAddMovie(Bundle bundle, Context context){
        executor.execute(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            String image, title, id;
            image = bundle.getString("cover");
            title = bundle.getString("title");
            id = bundle.getString("id");

            handler.post(() ->{
                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("watchlist");
                myRef.child(username).child(id).child("name").setValue(title);
                myRef.child(username).child(id).child("img").setValue(image);
                Toast.makeText(context, "Movie added!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void executeUpdateLogin(){
        executor.execute(() -> {
            Date currentTime = Calendar.getInstance().getTime();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            handler.post(() ->{
                assert user != null;
                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.child(username).child("last_login").setValue(currentTime.toString());
            });
        });

    }

    public void executeLogin(Login login, String email, String password, ProgressBar progressBar){
        executor.execute(() -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            handler.post(() ->{
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(login, "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(login.getApplicationContext(), MainActivity.class);
                                    login.startActivity(intent);
                                    login.finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(login, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            });
        });

    }

    public void executeIsLogged(Login login){
        executor.execute(() -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            handler.post(() ->{

                if(currentUser != null){
                    Intent intent = new Intent(login.getApplicationContext(), MainActivity.class);
                    login.startActivity(intent);
                    login.finish();
                }

            });
        });

    }

    public void executeRemoveGroup(String username, GroupModel model, String[] members, Callback callback, int pos){
        executor.execute(() -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            //Initiate DB
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("groups").child(username).child(model.getGroupName());
            mDatabase.removeValue();

            for(String m : members){
                mDatabase = database.getReference("groups").child(m).child(model.getGroupName());
                mDatabase.removeValue();
            }

            handler.post(() ->{
                callback.removeAt(pos);

            });
        });

    }

}