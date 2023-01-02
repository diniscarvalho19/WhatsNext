package com.dinis.whatsnext;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AllGroupsFragment extends Fragment implements RecyclerViewInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    FirebaseAuth auth;
    FirebaseUser user;


    View root;
    Button addGroup;
    RecyclerView recyclerView;
    RecyclerViewInterface rvi;


    public AllGroupsFragment() {
        // Required empty public constructor
    }

    public static AllGroupsFragment newInstance(String param1, String param2) {
        AllGroupsFragment fragment = new AllGroupsFragment();
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
        AllGroupsFragment allGroupsFragment = this;
        root = inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerView = root.findViewById(R.id.group_recycler);
        rvi = this;

        ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();

        //Initiate DB
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;
        String username = Objects.requireNonNull(user.getEmail()).split("@")[0];
        DatabaseReference mDatabase = database.getReference("groups").child(username);
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult().getValue() != null) {
                        String result = task.getResult().getValue().toString().substring(1);
                        result = result.substring(0, result.length() - 1);
                        Log.d("Groups1", result);
                        if (result.contains("], ")) {
                            String[] groups = result.split("], ");
                            for (String g : groups) {
                                Log.d("Groups1", g);
                                String tittle = g.split("=")[0];
                                String userList = g.split("=")[1].replace("[", "").replace("]", "");
                                groupModelArrayList.add(new GroupModel(tittle, userList));
                            }
                        } else {
                            Log.d("Groups1", result);
                            String tittle = result.split("=")[0];
                            String userList = result.split("=")[1].replace("[", "").replace("]", "");
                            groupModelArrayList.add(new GroupModel(tittle, userList));
                        }


                        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), groupModelArrayList, rvi);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(groupAdapter);


                    }
                }
            }
        });

        addGroup = root.findViewById(R.id.btn_create_group);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupCreateGroup popupClass = new PopupCreateGroup((MainActivity) getActivity());
                popupClass.showPopupWindow(view, allGroupsFragment);

            }
        });
        recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DatabaseReference mDatabase = database.getReference("groups").child(username);
                    mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                if (task.getResult().getValue() != null) {
                                    String result = task.getResult().getValue().toString().substring(1);
                                    result = result.substring(0, result.length() - 1);
                                    Log.d("Groups1", result);
                                    if (result.contains("], ")) {
                                        String[] groups = result.split("], ");
                                        for (String g : groups) {
                                            Log.d("Groups1", g);
                                            String tittle = g.split("=")[0];
                                            String userList = g.split("=")[1].replace("[", "").replace("]", "");
                                            groupModelArrayList.add(new GroupModel(tittle, userList));
                                        }
                                    } else {
                                        Log.d("Groups1", result);
                                        String tittle = result.split("=")[0];
                                        String userList = result.split("=")[1].replace("[", "").replace("]", "");
                                        groupModelArrayList.add(new GroupModel(tittle, userList));
                                    }


                                    GroupAdapter groupAdapter = new GroupAdapter(getActivity(), groupModelArrayList, rvi);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.setAdapter(groupAdapter);
                                }
                            }
                        }
                    });
                }
            }
        });

        return root;
    }

    @Override
    public void onItemClick(int position) {
    }

}