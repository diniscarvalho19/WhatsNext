package com.dinis.whatsnext;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dinis.whatsnext.TaskManager.TaskManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class WatchlistFragment extends Fragment implements RecyclerViewInterface, TaskManager.Callback{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    DB db;
    FirebaseAuth auth;
    FirebaseUser user;

    View root;
    RecyclerView recyclerView;
    MovieAdapter adapter;

    TaskManager taskManager = new TaskManager();

    public WatchlistFragment() {
        // Required empty public constructor
    }

    public static WatchlistFragment newInstance(String param1, String param2) {
        WatchlistFragment fragment = new WatchlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =inflater.inflate(R.layout.fragment_watchlist, container, false);
        TextView textView = (TextView) root.findViewById(R.id.textView);
        db = DB.getInstance(getActivity());
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        List<MovieModelClass> movieList = new ArrayList<>();

       taskManager.executeGetWatchlist(this);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                MovieModelClass deletedMovie = movieList.get(viewHolder.getAdapterPosition());

                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                movieList.remove(viewHolder.getAdapterPosition());

                //delete
                assert user != null;
                String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabase = database.getReference("watchlist");
                mDatabase.child(username).child(deletedMovie.getId()).removeValue();




                // below line is to notify our item is removed from adapter.
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                // below line is to display our snackbar with action.
                Snackbar.make(recyclerView, "Removed " + deletedMovie.getName() + " from watchlist", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.
                        movieList.add(position, deletedMovie);


                        //undo
                        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("watchlist");
                        myRef.child(username).child(deletedMovie.getId()).child("name").setValue(deletedMovie.getName());
                        myRef.child(username).child(deletedMovie.getId()).child("img").setValue(deletedMovie.getImg());

                        // below line is to notify item is
                        // added to our adapter class.
                        adapter.notifyItemInserted(position);
                    }
                }).show();
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recyclerView);
        return root;
    }

    public void PutDataIntoRecyclerView(List<MovieModelClass> movieList){
        adapter = new MovieAdapter((MainActivity)getActivity(), movieList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager((MainActivity)getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

    }
}