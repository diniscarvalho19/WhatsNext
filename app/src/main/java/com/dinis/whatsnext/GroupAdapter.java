package com.dinis.whatsnext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.dinis.whatsnext.TaskManager.TaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Viewholder> implements TaskManager.Callback {
    private final Context context;
    private final ArrayList<GroupModel> groupModelArrayList;
    private final RecyclerViewInterface recyclerViewInterface;
    View view;
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


        ImageButton arrow;
        LinearLayout hiddenView;
        CardView cardView;
        cardView = view.findViewById(R.id.base_cardview);
        arrow = view.findViewById(R.id.expand_button);
        hiddenView = view.findViewById(R.id.hidden_view);
        String[] members = model.getGroupMembers().split(", ");
        List<String> membersArray = new ArrayList<>(Arrays.asList(members));

        membersArray = taskManager.executeUpdateMembers(membersArray);

        holder.removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskManager.executeRemoveGroup(model, members, callback, pos);
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



        taskManager.executeGenRecommendations(membersArray,holder,callback);




    }

    @Override
    public void recHelper(ArrayList<String> recMovies, ArrayList<String> allMovies, @NonNull Viewholder holder) {
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
            }else {
                String common = "Movies in common: ";
                holder.recommendation_status.setText(common);

            }

        }
        holder.chooseMovie.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMovieRecommedation popupClass = new PopupMovieRecommedation(context, allMovies);
                popupClass.showPopupWindow(view);

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView groupName, groupMembers, recommendation, recommendation_status;
        Button chooseMovie;
        ImageButton removeGroup;

        public Viewholder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_title);
            groupMembers = itemView.findViewById(R.id.group_members);
            recommendation_status = itemView.findViewById(R.id.recommendation_status);
            removeGroup = itemView.findViewById(R.id.leave_group);
            chooseMovie = itemView.findViewById(R.id.choose_movie);
        }
    }
    public GroupModel getItem(int position){
        return groupModelArrayList.get(position);
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
        groupModelArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, groupModelArrayList.size());
    }

    public String getId(String username, String title){
        return "";
    }

}