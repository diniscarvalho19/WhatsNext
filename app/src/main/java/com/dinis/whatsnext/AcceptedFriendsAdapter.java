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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class AcceptedFriendsAdapter extends RecyclerView.Adapter<AcceptedFriendsAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private final Context mContext;
    private final List<FriendRequestModelClass> mData;
    FirebaseAuth auth;
    FirebaseUser user;


    public AcceptedFriendsAdapter(Context mContext, List<FriendRequestModelClass> mData, RecyclerViewInterface recyclerViewInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.friend_item, parent, false);
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
                //Initiate DB
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                assert user != null;
                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference mDatabase = database.getReference("friends_list").child(username).child(mData.get(pos).getName());
                mDatabase.setValue(true);

                Toast.makeText(mContext, "You've got a new friend :)",Toast.LENGTH_SHORT).show();
                removeAt(pos);

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

    private void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }



}
