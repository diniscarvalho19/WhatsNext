package com.dinis.whatsnext;

import android.content.Context;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PopupCreateGroup implements RecyclerViewInterface {

    public PopupCreateGroup(Context context) {
        this.context = context;
    }

    Context context;

    //PopupWindow display method
    EditText groupName;
    RecyclerView recyclerView;
    Button createGroup;
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
        arrayList.add(new FriendModelClass("joao", "1"));
        arrayList.add(new FriendModelClass("andre", "2"));
        arrayList.add(new FriendModelClass("dinis", "1"));
        arrayList.add(new FriendModelClass("joao", "1"));
        arrayList.add(new FriendModelClass("roger", "1"));

        PopupFriendlistAdapter popupFriendlistAdapter = new PopupFriendlistAdapter(context, arrayList, this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(popupFriendlistAdapter);

        createGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Toast toast =Toast.makeText(context,"Group created",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                //TODO Add this list to database
                for(String member: popupFriendlistAdapter.getGroupMembers()){
                    System.out.println(member);
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

    @Override
    public void onItemClick(int position) {

    }
}