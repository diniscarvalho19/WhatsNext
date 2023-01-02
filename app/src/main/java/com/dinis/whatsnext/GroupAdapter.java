package com.dinis.whatsnext;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.dinis.whatsnext.TaskManager.TaskManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Viewholder> implements TaskManager.Callback {
    private final Context context;
    private final ArrayList<GroupModel> groupModelArrayList;
    private final RecyclerViewInterface recyclerViewInterface;
    View view;
    FirebaseAuth auth;
    FirebaseUser user;
    TaskManager taskManager = new TaskManager();
    TaskManager.Callback callback;


    public GroupAdapter(Context context, ArrayList<GroupModel> groupModelArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.groupModelArrayList = groupModelArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
        callback = this;
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
        holder.recommendation.setText(null);

        ImageButton arrow;
        LinearLayout hiddenView;
        CardView cardView;
        cardView = view.findViewById(R.id.base_cardview);
        arrow = view.findViewById(R.id.expand_button);
        hiddenView = view.findViewById(R.id.hidden_view);
        String[] members = model.getGroupMembers().split(", ");
        List<String> membersArray = new ArrayList<>(Arrays.asList(members));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        assert user != null;
        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];

        membersArray.add(username);

        holder.removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskManager.executeRemoveGroup(username, model, members, callback, pos);
            }
        });



        //Initiate DB
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("watchlist");
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    if (task.getResult().getValue() != null){
                        ArrayList<String> allMovies = new ArrayList<>();
                        ArrayList<String> recMovies = new ArrayList<>();
                        String result = task.getResult().getValue().toString().substring(1);
                        result = result.substring(0, result.length() - 1);
                        String[] userData = result.split("\\}\\}, ");
                        for(String ud : userData){
                            String[] udSplit = ud.split("\\{", 2);
                            String name = udSplit[0].replace("=","");
                            udSplit = Arrays.copyOfRange(udSplit, 1, udSplit.length);
                            if(membersArray.contains(name)){
                                for (String ids : udSplit){
                                    ids = (ids + "}").replace("}}}","}");
                                    String[] idsSplit = ids.split("\\}, ");
                                    for (String id : idsSplit){
                                        id = (id + "}").replace("}}","}");
                                        allMovies.add(id);
                                    }

                                }
                            }


                        }

                        if(allMovies.isEmpty()){
                            String noWL = "Watchlist empty";
                            holder.recommendation_status.setText(noWL);
                        }else{
                            for(String movie : allMovies){
                                if(Collections.frequency(allMovies, movie) > 1){
                                    recMovies.add(movie);
                                }
                            }
                            if(recMovies.isEmpty()){
                                String noCommon = "No movies in common. Picking a random movie from all watchlists:";
                                holder.recommendation_status.setText(noCommon);
                                List<String> allMoviesDistinct = new ArrayList<>(new HashSet<>(allMovies));
                                holder.recommendation.setText(allMoviesDistinct.get(new Random().nextInt(allMoviesDistinct.size())));
                            }else {
                                String common = "Movies in common: ";
                                holder.recommendation_status.setText(common);
                                List<String> recMoviesDistinct = new ArrayList<>(new HashSet<>(recMovies));
                                holder.recommendation.setText(recMoviesDistinct.toString());

                            }

                        }

                    }
                }
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
        TextView groupName, groupMembers, recommendation, recommendation_status;
        ImageButton removeGroup;

        public Viewholder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_title);
            groupMembers = itemView.findViewById(R.id.group_members);
            recommendation = itemView.findViewById(R.id.recommendation);
            recommendation_status = itemView.findViewById(R.id.recommendation_status);
            removeGroup = itemView.findViewById(R.id.leave_group);
        }
    }
    public GroupModel getItem(int position){
        return groupModelArrayList.get(position);
    }


    @Override
    public void PutDataIntoRecyclerView(List<MovieModelClass> movieList) {

    }

    @Override
    public void removeAt(int position) {
        groupModelArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, groupModelArrayList.size());
    }

    public String getId(String username, String title){
        return "";
    }

}
