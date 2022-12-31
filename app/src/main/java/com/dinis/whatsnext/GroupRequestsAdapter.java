package com.dinis.whatsnext;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.ArrayList;

public class GroupRequestsAdapter extends RecyclerView.Adapter<GroupRequestsAdapter.Viewholder>{
    private final Context context;
    private final ArrayList<GroupModel> groupModelArrayList;
    private final RecyclerViewInterface recyclerViewInterface;
    View view;
    public GroupRequestsAdapter(Context context, ArrayList<GroupModel> groupModelArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.groupModelArrayList = groupModelArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.group_request_item, parent, false);
        return new Viewholder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRequestsAdapter.Viewholder holder, int position) {
        GroupModel model = groupModelArrayList.get(position);
        holder.groupName.setText(model.getGroupName());
        holder.groupMembers.setText(model.getGroupMembers());
        ImageButton acceptRequest = view.findViewById(R.id.accept_group);
        ImageButton rejectRequest = view.findViewById(R.id.reject_group);
        acceptRequest.setOnClickListener(view -> {
            //TODO group logic
            groupModelArrayList.remove(position);
            this.notifyItemRemoved(position);

        });

        rejectRequest.setOnClickListener(view -> {
            //TODO group logic
            groupModelArrayList.remove(position);
            this.notifyItemRemoved(position);

        });


    }

    @Override
    public int getItemCount() {
        return groupModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView groupMembers;
        ImageButton acceptRequest;
        ImageButton rejectRequest;
        public Viewholder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_requests_name);
            groupMembers= itemView.findViewById(R.id.group_requests_members);
            acceptRequest=itemView.findViewById(R.id.accept_group);
            rejectRequest= itemView.findViewById(R.id.reject_group);
        }
    }
}
