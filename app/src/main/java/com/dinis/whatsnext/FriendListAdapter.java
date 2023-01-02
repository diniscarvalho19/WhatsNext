package com.dinis.whatsnext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinis.whatsnext.TaskManager.TaskManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> implements TaskManager.Callback {

    private final RecyclerViewInterface recyclerViewInterface;
    private final Context mContext;
    private final List<FriendRequestModelClass> mData;
    TaskManager taskManager = new TaskManager();
    TaskManager.Callback callback;
    View v;


    public FriendListAdapter(Context mContext, List<FriendRequestModelClass> mData, RecyclerViewInterface recyclerViewInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.unfriend_item, parent, false);
        callback = this;
        return new MyViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        holder.name.setText(mData.get(position).getName());


        // Using Glide Library to display the image
        GlideApp.with(mContext)
                .load("https://cdn-icons-png.flaticon.com/512/16/16363.png")
                .into(holder.image);



        holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.executeRemoveFriend(pos, mData, callback);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView image;
        Button addFriendBtn;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.imageView);
            addFriendBtn = itemView.findViewById(R.id.btn_addFriend);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface!=null){
                        int position = getAdapterPosition();
                        if (position!= RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public FriendRequestModelClass getItem(int position){
        return mData.get(position);
    }

    @Override
    public void PutDataIntoRecyclerView(List<MovieModelClass> movieList) {

    }

    @Override
    public void PutDataIntoRecyclerViewFriends(List<FriendRequestModelClass> everyoneList) {

    }

    @Override
    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }



}
