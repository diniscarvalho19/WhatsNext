package com.dinis.whatsnext;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PopupCreateGroup implements RecyclerViewInterface {

    public PopupCreateGroup(Context context) {
        this.context = context;
    }

    Context context;

    //PopupWindow display method
    EditText groupName;
    RecyclerView recyclerView;
    Button createGroup;
    FirebaseAuth auth;
    FirebaseUser user;
    public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_group_create, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        groupName = (EditText) popupView.findViewById(R.id.group_name_input);
        recyclerView = (RecyclerView)popupView.findViewById(R.id.recycler_add_friend_to_group);
        createGroup = (Button) popupView.findViewById(R.id.confirm_group);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        ArrayList<FriendModelClass> arrayList = new ArrayList<FriendModelClass>();

        RecyclerViewInterface rvi = this;

        //TODO: FRIEND LIST
        //Initiate DB
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ArrayList<FriendModelClass> everyoneList = new ArrayList<>();
        List<String> friendList = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabase = database.getReference("friends_list");
        assert user != null;
        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];


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
                                                auth = FirebaseAuth.getInstance();
                                                user = auth.getCurrentUser();
                                                assert user != null;
                                                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                                                FirebaseDatabase databaseFB = FirebaseDatabase.getInstance();

                                                DatabaseReference mDatabase = databaseFB.getReference("groups")
                                                        .child(username)
                                                        .push()
                                                        .child(String.valueOf(groupName.getText()));

                                                mDatabase.setValue(popupFriendlistAdapter.getGroupMembers());
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



    }

    @Override
    public void onItemClick(int position) {

    }
}