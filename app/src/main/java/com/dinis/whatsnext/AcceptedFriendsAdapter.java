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

import java.util.ArrayList;
import java.util.List;

public class AcceptedFriendsAdapter extends RecyclerView.Adapter<AcceptedFriendsAdapter.MyViewHolder> implements TaskManager.Callback {

    private final RecyclerViewInterface recyclerViewInterface;
    private final Context mContext;
    private final List<FriendRequestModelClass> mData;
    TaskManager taskManager = new TaskManager();
    TaskManager.Callback callback;


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
        callback = this;
        return new MyViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = position;

        holder.name.setText(mData.get(position).getName());


        // Using Glide Library to display the image
        GlideApp.with(mContext)
                .load(R.drawable.user)
                .into(holder.image);

        holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.executeAcceptFriendRequest(pos, mData, callback);

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
    public void PutDataIntoRecyclerViewFriendsCommunity(List<FriendModelClass> everyoneList) {

    }
    @Override
    public void removeAt(int position) {
        Toast.makeText(mContext, "You've got a new friend :)",Toast.LENGTH_SHORT).show();
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    @Override
    public void recHelper(ArrayList<String> recMovies, ArrayList<String> allMovies, @NonNull GroupAdapter.Viewholder holder) {

    }


}
