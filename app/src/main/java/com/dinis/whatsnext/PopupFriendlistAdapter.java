package com.dinis.whatsnext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class PopupFriendlistAdapter extends RecyclerView.Adapter<PopupFriendlistAdapter.Viewholder>{
    private final Context context;
    private final ArrayList<FriendModelClass> friendModelClassArrayList;
    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<String> groupMembers;
    View view;

    public PopupFriendlistAdapter(Context context, ArrayList<FriendModelClass> friendModelClass,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.friendModelClassArrayList = friendModelClass;
        this.recyclerViewInterface = recyclerViewInterface;
        this.groupMembers = new ArrayList<String>();
    }


    @NonNull
    @Override
    public PopupFriendlistAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.friend_item, parent, false);
        return new PopupFriendlistAdapter.Viewholder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PopupFriendlistAdapter.Viewholder holder, int position) {
        FriendModelClass model = friendModelClassArrayList.get(position);
        holder.username.setText(model.getUsername());
        Button add = view.findViewById(R.id.btn_addFriend);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                add.setAlpha(.4f);
                add.setClickable(false);
                groupMembers.add(model.getUsername());
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendModelClassArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView username;

        public Viewholder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            Button add = itemView.findViewById(R.id.btn_addFriend);
        }
    }

    public ArrayList<String> getGroupMembers(){
        return groupMembers;
    }
}
