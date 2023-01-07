package com.dinis.whatsnext;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinis.whatsnext.TaskManager.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> implements TaskManager.Callback {

    private final RecyclerViewInterface recyclerViewInterface;
    private final Context mContext;
    private final List<FriendModelClass> mData;
    TaskManager.Callback callback;
    TaskManager taskManager = new TaskManager();


    public CommunityAdapter(Context mContext, List<FriendModelClass> mData, RecyclerViewInterface recyclerViewInterface) {
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

        callback = this;
        int pos = position;

        holder.name.setText(mData.get(position).getUsername());

        if(mData.get(position).getStatus().equals("not accepted")){
            holder.name.setTextColor(Color.parseColor("#E3E2E6"));
        }else if(mData.get(position).getStatus().equals("accepted")){
            holder.name.setTextColor(Color.parseColor("#72F964"));
            holder.removeButton();
        }else{
            holder.name.setTextColor(Color.parseColor("#F8FC63"));
            holder.removeButton();
        }



        // Using Glide Library to display the image
        GlideApp.with(mContext)
                .load(mData.get(position).getImage())
                .into(holder.image);

        holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskManager.executeSendFriendRequest(mData, pos, callback);
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
                            Log.d("Groups",recyclerViewInterface + "" + position);
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });

        }
        public void removeButton(){
            ViewGroup layout = (ViewGroup) addFriendBtn.getParent();
            if(null!=layout) //for safety only  as you are doing onClick
                layout.removeView(addFriendBtn);

        }
    }

    public FriendModelClass getItem(int position){
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
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    @Override
    public void recHelper(ArrayList<String> recMovies, ArrayList<String> allMovies, @NonNull GroupAdapter.Viewholder holder) {

    }


}
