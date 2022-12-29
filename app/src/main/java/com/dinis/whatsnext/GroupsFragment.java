package com.dinis.whatsnext;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GroupsFragment extends Fragment implements RecyclerViewInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    DB db;
    FirebaseAuth auth;
    FirebaseUser user;

    View root;
    Button addMovie;
    RecyclerView recyclerView;

    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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
        root =inflater.inflate(R.layout.fragment_groups, container, false);
        TextView textView = (TextView) root.findViewById(R.id.textView);
        recyclerView = (RecyclerView) root.findViewById(R.id.group_recycler);

        ArrayList<GroupModel> groupModelArrayList = new ArrayList<GroupModel>();
        groupModelArrayList.add(new GroupModel("Group 1", "joao, dinis, joao"));
        groupModelArrayList.add(new GroupModel("Group 2", "andre, tiago, filipe"));
        groupModelArrayList.add(new GroupModel("Group 3", "jose, francisco, rodrigo"));

        GroupAdapter groupAdapter = new GroupAdapter((MainActivity)getActivity(), groupModelArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((MainActivity)getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(groupAdapter);




        return root;
    }

    @Override
    public void onItemClick(int position) {

    }
}