package com.dinis.whatsnext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Viewholder>{
    private final Context context;
    private final ArrayList<GroupModel> groupModelArrayList;
    private final RecyclerViewInterface recyclerViewInterface;
    View view;
    FirebaseAuth auth;
    FirebaseUser user;


    public GroupAdapter(Context context, ArrayList<GroupModel> groupModelArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.groupModelArrayList = groupModelArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new Viewholder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.Viewholder holder, int position) {
        int pos = position;
        GroupModel model = groupModelArrayList.get(position);
        holder.groupName.setText(model.getGroupName());
        holder.groupMembers.setText(model.getGroupMembers());
        holder.recommendation.setText(model.getGroupMembers());

        ImageButton arrow;
        LinearLayout hiddenView;
        CardView cardView;
        cardView = view.findViewById(R.id.base_cardview);
        arrow = view.findViewById(R.id.expand_button);
        hiddenView = view.findViewById(R.id.hidden_view);

        holder.removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initiate DB
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                assert user != null;
                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                String[] members = model.getGroupMembers().split(", ");
                DatabaseReference mDatabase = database.getReference("groups").child(username).child(model.getGroupName());
                mDatabase.removeValue();

                for(String m : members){
                    mDatabase = database.getReference("groups").child(m).child(model.getGroupName());
                    mDatabase.removeValue();
                }


                removeAt(pos);
            }
        });



        arrow.setOnClickListener(view -> {
            // If the CardView is already expanded, set its visibility
            // to gone and change the expand less icon to expand more.
            if (hiddenView.getVisibility() == View.VISIBLE) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenView.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }

            // If the CardView is not expanded, set its visibility to
            // visible and change the expand more icon to expand less.
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenView.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView groupName, groupMembers, recommendation;
        ImageButton removeGroup;

        public Viewholder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_title);
            groupMembers = itemView.findViewById(R.id.group_members);
            recommendation = itemView.findViewById(R.id.recommendation);
            removeGroup = itemView.findViewById(R.id.leave_group);
        }
    }
    public GroupModel getItem(int position){
        return groupModelArrayList.get(position);
    }

    private void removeAt(int position) {
        groupModelArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, groupModelArrayList.size());
    }

    public String getId(String username, String title){
        return "";
    }

}
